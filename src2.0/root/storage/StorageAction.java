package root.storage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.arraysite.ArraysiteService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.controller.ConService;
import com.huiming.service.disk.DiskService;
import com.huiming.service.extend.ExtendService;
import com.huiming.service.iogroup.IoGroupService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.rank.RankService;
import com.huiming.service.sr.storagesystem.StorageSystemService;
import com.huiming.service.storage.StorageService;
import com.huiming.service.volume.VolumeService;
import com.huiming.service.widget.WidgetService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.MyUtilities;

public class StorageAction extends SecurityAction {
	StorageService tpcService = new StorageService();
	StorageSystemService srService = new StorageSystemService();
	BaseprfService baseService = new BaseprfService();
	WidgetService widgetService = new WidgetService();
	
	/**
	 * 存储系统信息列表
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public ActionResult doStoragePage(){
		DBPage page = null;
		DBPage srPage = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		//获取用户可见存储
		String tpcLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = tpcService.getStoragePage(curPage, numPerPage, null, null, null, null, null, null,null, null, tpcLimitIds);
		}
		srPage = srService.getStoragePage(curPage, numPerPage, null, null, null, null, null, null, null, null, srLimitIds);
		DBPage page1 = mergePage(page, srPage);
		setAttribute("storagePage", page1);
		//绘制容量柱形图
		List<DataRow> tpcList = null;
		List<DataRow> srList = null;
		tpcList = (page == null) ? null : page.getData();
		srList = (srPage == null) ? null : srPage.getData();
		doDrawCapacity(tpcList, srList);
		return new ActionResult("/WEB-INF/views/storage/storageList.jsp");
	}
	
	/**
	 * 绘制容量柱形图
	 */
	public void doDrawCapacity(List<DataRow> tpcStorageList,List<DataRow> srStorageList) {
		//存储容量柱形图
		JSONArray arr = new JSONArray();
		JSONArray allocatedCapacity = new JSONArray(); // 已用容量
		JSONArray availableCapacity = new JSONArray(); // 可用空间
		Map<Object,Object> mapAllocatedCapacity = new HashMap<Object,Object>();
		Map<Object,Object> mapAvailableCapacity = new HashMap<Object,Object>();
//		List<DataRow> rows = tpcService.getCapacityInfo(tpcLimitIds);
		List<DataRow> rows = tpcStorageList;
		DecimalFormat decFmt = new DecimalFormat("0.00");
		if (rows != null && rows.size() > 0) {
			for (DataRow row : rows) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("type",row.getString("os_type"));
				jsonObj.put("id", row.getInt("subsystem_id"));
				jsonObj.put("name", row.getString("the_display_name"));
				arr.add(jsonObj);
				allocatedCapacity.add(Double.parseDouble(decFmt.format(row.getDouble("the_allocated_capacity")/1024)));
				availableCapacity.add(Double.parseDouble(decFmt.format(row.getDouble("the_available_capacity")/1024)));
			}
		}
		
//		List<DataRow> capRows = srService.getStorageCapacityInfo(srLimitIds);
		List<DataRow> capRows = srStorageList;
		if (capRows != null && capRows.size() > 0) {
			for (DataRow dataRow : capRows) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("type", dataRow.getString("storage_type"));
				jsonObj.put("id", dataRow.getInt("subsystem_id"));
				jsonObj.put("name", dataRow.getString("name"));
				arr.add(jsonObj);
				double usedcapacity = dataRow.getDouble("total_usable_capacity")-dataRow.getDouble("unallocated_usable_capacity");
				allocatedCapacity.add(Double.parseDouble(new DecimalFormat("0.00").format(usedcapacity/1024/1024)));
				availableCapacity.add(Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("unallocated_usable_capacity")/1024/1024)));
			}
		}
		mapAvailableCapacity.put("name", "空余容量");
		mapAvailableCapacity.put("data", availableCapacity);
		mapAvailableCapacity.put("color", "#6CCA16");
		mapAllocatedCapacity.put("name", "已用容量");
		mapAllocatedCapacity.put("data", allocatedCapacity);
		mapAllocatedCapacity.put("color", "#C8C1CF");
		JSONArray jsArray = new JSONArray().fromObject(mapAvailableCapacity);
		jsArray.add(mapAllocatedCapacity);
		this.setAttribute("categories", jsArray);
		this.setAttribute("arr", arr);
	}
	
	/**
	 * 存储系统信息列表(分页)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionResult doAjaxStoragePage(){
		DBPage srPage = null;
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		DBPage page = null;
		String storageName = getStrParameter("storageName").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String type = getStrParameter("type");
		String serialNumber = getStrParameter("serialNumber");
		Integer startPoolCap = getIntParameter("startPoolCap");
		Integer endPoolCap = getIntParameter("endPoolCap");
		Integer startPoolAvailableCap = getIntParameter("startPoolAvailableCap");
		Integer endPoolAvailableCap = getIntParameter("endPoolAvailableCap");
		//获取用户可见存储
		String tpcLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			page = tpcService.getStoragePage(curPage, numPerPage, storageName, ipAddress, type, serialNumber, startPoolCap, endPoolCap, startPoolAvailableCap, endPoolAvailableCap, tpcLimitIds);
		}
		srPage = srService.getStoragePage(curPage, numPerPage, storageName,ipAddress, type, serialNumber, startPoolCap, endPoolCap, startPoolAvailableCap, endPoolAvailableCap, srLimitIds);
		DBPage page1 = mergePage(page, srPage);
		this.setAttribute("storagePage", page1);
		this.setAttribute("storageName", storageName);
		this.setAttribute("ipAddress", ipAddress);
		this.setAttribute("type", type);
		this.setAttribute("serialNumber", serialNumber);
		this.setAttribute("startPoolCap", startPoolCap);
		this.setAttribute("endPoolCap", endPoolCap);
		this.setAttribute("startPoolAvailableCap", startPoolAvailableCap);
		this.setAttribute("startPoolAvailableCap", startPoolAvailableCap);
		//绘制容量柱形图
		List<DataRow> tpcList = null;
		List<DataRow> srList = null;
		tpcList = (page == null) ? null : page.getData();
		srList = (srPage == null) ? null : srPage.getData();
		doDrawCapacity(tpcList, srList);
		return new ActionResult("/WEB-INF/views/storage/ajaxStorage.jsp");
	}
	
	/**
	 * 存储系统详细信息,性能,部件信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionResult doStorageInfo(){
		PoolService poolService = new PoolService();
		PortService portService = new PortService();
		VolumeService volumeService = new VolumeService();
		DiskService diskService = new DiskService();
		ExtendService extendService = new ExtendService();
		NodeService nodeService = new NodeService();
		IoGroupService iogService = new IoGroupService();
		ArraysiteService arrService = new ArraysiteService();
		RankService rankService = new RankService();
		ConService conService = new ConService();
		DeviceAlertService deviceService = new DeviceAlertService();
		
		Integer subsystemId = getIntParameter("subSystemID");
		//获取用户可见存储
		String tpcLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		DataRow storrow = tpcService.getSubsystemInfo(subsystemId);
		DataRow nasrow = tpcService.getNasInfo(subsystemId);//nas信息
		//doCapacityInfo();  //容量图信息
		DBPage poolPage = poolService.getPoolPage(1, WebConstants.NumPerPage, null, null, null, subsystemId);
		DBPage volumePage = volumeService.getVolumePage(1, WebConstants.NumPerPage, null, null, null, null, subsystemId);
		DBPage portPage = portService.getPortPage(1, WebConstants.NumPerPage, null, null, null, null, null, subsystemId);
		DBPage diskPage = diskService.getDiskPage(1, WebConstants.NumPerPage, null, null, null, subsystemId);
		DBPage extendPage = extendService.getExtendPage(1, WebConstants.NumPerPage, null, null, null, null, null, null, subsystemId);
		//磁盘阵列
		DBPage arraysitePage = arrService.getArraysitePage(1, WebConstants.NumPerPage, null, null, subsystemId);
		List<DataRow> arraysiteList = arraysitePage.getData();
		List<DataRow> resarraysiteList = arrService.getResArrayList();
		List<DataRow> list = new ArrayList<DataRow>(arraysiteList.size() * resarraysiteList.size());
		for (DataRow row : arraysiteList) {
			for (DataRow row1 : resarraysiteList) {
				if (row.getInt("disk_group_id") == row1.getInt("array_id")) {
					row.set("controller_status", row1.get("controller_status"));
					row.set("battery_status", row1.get("battery_status"));
					row.set("power_status", row1.get("power_status"));
					row.set("disk_status", row1.get("disk_status"));
					row.set("hea_status", row1.get("hea_status"));
					row.set("disk_enclosure_status", row1.get("disk_enclosure_status"));
					row.set("fiber_status", row1.get("fiber_status"));
					list.add(row);	
				}	
			}
		}
		arraysiteList.removeAll(list);
		for (DataRow row2 : arraysiteList) {
			row2.set("controller_status","");
			row2.set("battery_status", "");
			row2.set("power_status","");
			row2.set("disk_status","");
			row2.set("hea_status", "");
			row2.set("disk_enclosure_status","");
			row2.set("fiber_status", "");
			list.add(row2);
		}
		arraysitePage.setData(list);
		
		DBPage rankPage = rankService.getRankPage(1, WebConstants.NumPerPage, null, null, subsystemId);
		this.setAttribute("poolPage", poolPage);
		this.setAttribute("poolCount", poolPage.getTotalRows());
		this.setAttribute("volumePage", volumePage);
		this.setAttribute("volumeCount", volumePage.getTotalRows());
		this.setAttribute("portPage", portPage);
		this.setAttribute("portCount", portPage.getTotalRows());
		this.setAttribute("diskPage", diskPage);
		this.setAttribute("diskCount", diskPage.getTotalRows());
		this.setAttribute("extendPage", extendPage);
		this.setAttribute("extendCount", extendPage.getTotalRows());
		this.setAttribute("arraysitePage", arraysitePage);
		this.setAttribute("arrayCount", arraysitePage.getTotalRows());
		this.setAttribute("rankPage", rankPage);
		this.setAttribute("rankCount", rankPage.getTotalRows());
	
		//设备事件跳转
		int level = getIntParameter("level", -1);
		int state = getIntParameter("state", -1);
		int tabToShow = getIntParameter("tabToShow", SrContant.TAB_SUMMARY);
		setAttribute("tabToShow", tabToShow);
		String overViewTab = "", detailTab = "", prfTab = "", alertTab = "", dataTab = "";
		switch(tabToShow){
			case 0: overViewTab = "active"; break;
			case 1: detailTab = "active"; break;
			case 2: prfTab = "active"; break;
			case 3: alertTab = "active";
			break; case 4: dataTab = "active";
			break;
		}
		setAttribute("subTabToShow", getStrParameter("subTabToShow", ""));
		setAttribute("overViewTab", overViewTab);
		setAttribute("detailTab", detailTab);
		setAttribute("prfTab", prfTab);
		setAttribute("alertTab", alertTab);
		setAttribute("dataTab", dataTab);
//		setAttribute("attachment", String.format("&level=%s&state=%s&tabToShow=%s", level, state, tabToShow));
	
		//告警
		DBPage devicePage = deviceService.getLogPage(1, WebConstants.NumPerPage, -1, subsystemId.toString(), null,subsystemId.toString(),null, SrContant.SUBDEVTYPE_STORAGE, state, level, null, null);
		setAttribute("deviceLogPage",devicePage);
		setAttribute("storageInfo", storrow);
		setAttribute("nasInfo", nasrow);//nas
		setAttribute("subSystemID", subsystemId);	
		setAttribute("level", level);
		setAttribute("state", state);

		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow rown = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,2,"Controller", 
				rown.getString("type"),subsystemId,null,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if (dataRow == null || dataRow.size() == 0) {
			dataRow = baseService.getDefaultRow("v_res_storage_subsystem", subsystemId, rown.getString("type"), "Storage", "subsystem_id", "the_display_name");
			if (storrow != null) {
				if (storrow.getString("os_type").equals("25")) {
					dataRow.set("fprfid", "'A221','A233'");
				} else if (storrow.getString("os_type").equals("15")
						|| storrow.getString("os_type").equals("37")) {
					dataRow.set("fprfid", "'A415','A421'");
				} else if (storrow.getString("type").equals("21")) {
					dataRow.set("fprfid", "'A38','A44'");
				}
			}
			if (dataRow != null) {
				dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
			}
		}
		if (dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = conService.getConList(subsystemId,dataRow.getString("fdevice"));
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			thead.set("prf_timestamp", "时间");
			thead.set("ele_name", "设备名");
			for (DataRow r : kpis) {
				thead.set(r.getString("fid"), r.getString("ftitle"));
			}
			tbody = baseService.getPrfDatas(curPage,numPerPage,devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
			if (tablePage == null || tablePage.length() == 0) {
//				JSONArray array = baseService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
//				json.put("series", array);
				json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"), devs, kpis,
						dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"))));
				
			}
			json.put("legend", dataRow.getInt("flegend") == 1 ? true : false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("thead", thead);
			json.put("tbody", tbody);
			json.put("kpiInfo", kpis);
		}
		this.setAttribute("conPrfData", json);
		
		//doStoragePrfField();
		DBPage nodePage = nodeService.getNodePage(1, WebConstants.NumPerPage, null, null, null, subsystemId);
		DBPage iogroupPage = iogService.getIogroupPage(1, WebConstants.NumPerPage, null, subsystemId);
		this.setAttribute("nodePage", nodePage);
		this.setAttribute("nodeCount", nodePage.getTotalRows());
		this.setAttribute("iogroupPage", iogroupPage);
		this.setAttribute("iogroupCount", iogroupPage.getTotalRows());
		
		//存储容量柱形图
		JSONArray allocatedCapacity = new JSONArray(); // 已用容量
		JSONArray availableCapacity = new JSONArray(); // 可用空间
		DecimalFormat decFmt = new DecimalFormat("0.00");
		String displayname = "";
		List<DataRow> rows = tpcService.getCapacityInfo(tpcLimitIds);
		for (DataRow row : rows) {
			if (subsystemId == row.getInt("subsystem_id")) {
				displayname = row.getString("the_display_name");
				allocatedCapacity.add(Double.parseDouble(decFmt.format(row.getDouble("the_allocated_capacity")/1024)));
				availableCapacity.add(Double.parseDouble(decFmt.format(row.getDouble("the_available_capacity")/1024)));
			}
		}
		
		List<DataRow> capRows = srService.getStorageCapacityInfo(srLimitIds);
		for (DataRow dR : capRows) {
			if (subsystemId == dR.getInt("subsystem_id")) {
				double usedcapacity = dR.getDouble("total_usable_capacity")-dR.getDouble("unallocated_usable_capacity");
				allocatedCapacity.add(Double.parseDouble(decFmt.format(usedcapacity/1024/1024)));
				availableCapacity.add(Double.parseDouble(decFmt.format(dR.getDouble("unallocated_usable_capacity")/1024/1024)));
			}
		}
		JSONObject jsonvloume = new JSONObject();
		DataRow vloume = new DataRow();
		vloume.set("availableCapacity", availableCapacity.get(0));
		vloume.set("allocatedCapacity", allocatedCapacity.get(0));
		jsonvloume.put("smallTitle", displayname+ " Capacity Info");
		JSONArray array = new JSONArray();
		array = getHighchartPieData(vloume);
		jsonvloume.put("series", array);
		jsonvloume.put("charttype", "pie");
		setAttribute("vloume", jsonvloume);
//		 this.setAttribute("availableCapacity", availableCapacity.get(0));
//		 this.setAttribute("allocatedCapacity", allocatedCapacity.get(0));
		 
		//告警
		DBPage device2Page = deviceService.getLogPage(1, WebConstants.NumPerPage, -1, subsystemId.toString(), null,subsystemId.toString(),null, SrContant.SUBDEVTYPE_STORAGE, -1, -1, null, null);
		List<DataRow> levels = device2Page.getData();
		JSONObject jsonreport = new JSONObject();
		DataRow report = new DataRow();
		for (DataRow leve : levels) {
			switch(leve.getInt("flevel")) {
			case SrContant.EVENT_LEVEL_INFO:
				report.set("Info", leve.getInt("fcount"));
				break;
			case SrContant.EVENT_LEVEL_WARNING:
				report.set("Warning", leve.getInt("fcount"));
				break;
			case SrContant.EVENT_LEVEL_CRITICAL:
				report.set("Critical", leve.getInt("fcount"));
				break;
			}
		}
		jsonreport.put("smallTitle", displayname + " Alert Info");
		JSONArray array2 = new JSONArray();
		array2 = getHighchartPieData2(report,subsystemId);
		jsonreport.put("series", array2);
		jsonreport.put("charttype", "pie");
		setAttribute("report", jsonreport);
		//Total Port I/O Rate
		setTpir();
		return new ActionResult("/WEB-INF/views/storage/storageInfos.jsp");
	}
	
	/**
	 * 存储告警饼图
	 * */
	public JSONArray getHighchartPieData2(DataRow row, Integer subsystemId){
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		
		JSONObject info = new JSONObject();
		JSONObject warning = new JSONObject();
		JSONObject critical = new JSONObject();
		
		String baseUrl = "/servlet/storage/StorageAction?func=StorageInfo&subSystemID=" + subsystemId +"&tabToShow=3";
		//Info
		info.put("name", "Info");
		info.put("y", row.getDouble("Info"));
		info.put("url", baseUrl + "&level=0");
		//Warning
		warning.put("name", "Warning");
		warning.put("y", row.getDouble("Warning"));
		warning.put("url", baseUrl + "&level=1");
		//Critical
		critical.put("name", "Critical");
		critical.put("y", row.getDouble("Critical"));
		critical.put("url", baseUrl + "&level=2");
			
		ary.add(info);
		ary.add(warning);
		ary.add(critical);

		json.put("type", "pie");
		json.put("name", "告警数量");
		if (row.size() < 1) {
			json.put("data", null);
		} else {
			json.put("data", ary);
		}
		array.add(json);
		return array;
	}
	
	/**
	 * 存储容量饼图
	 * */
	public JSONArray getHighchartPieData(DataRow row){
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		
		JSONArray used = new JSONArray();
		JSONArray unuse = new JSONArray();
		used.add("已用容量(G)");
		used.add(row.getDouble("availableCapacity"));
		unuse.add("空余容量(G)");
		unuse.add(row.getDouble("allocatedCapacity"));
		ary.add(used);
		ary.add(unuse);
		
		json.put("type", "pie");
		json.put("name", "容量");
		json.put("data", ary);
		array.add(json);
		return array;
	}
	
	/**
	 * 处理Total I/O Rate (overall)
	 * 处理Total Port I/O Rate
	 * */
	public void setTpir(){
//		String str = "";
		Date date = new Date();
		String timeRange = getStrParameter("timeRange");
		String startTime = null;
		String endTime = SrContant.getTimeFormat(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
//		if (StringHelper.isNotEmpty(timeRange) && StringHelper.isNotBlank(timeRange)) {
			//Hour
			if (timeRange.equals("day")) {
				calendar.add(Calendar.DATE, -1);
			//Week
			} else if (timeRange.equals("week")) {
				calendar.add(Calendar.WEEK_OF_MONTH, -1);
			//Month
			} else if (timeRange.equals("month")) {
				calendar.add(Calendar.MONTH, -1);
			}
			else {  // if (timeRange.equals("hour")) 
				calendar.add(Calendar.HOUR, -1);
				//Day
			}  
		//默认查找前一小时(Hour)
//		} else {
//			calendar.add(Calendar.HOUR, -1);
//			calendar.add(Calendar.YEAR, -2);
//		}
		startTime = SrContant.getTimeFormat(calendar.getTime());
		String subsystemId = getStrParameter("subSystemID");
		Integer subsystemId2 = getIntParameter("subSystemID");
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			DataRow dr = tpcService.getType(subsystemId2);
			if (SrContant.DEVTYPE_VAL_SVC.equals(dr.getString("type"))) {
				setAttribute("HeatTopNData",getDrawPerfTopNData(subsystemId, "A3", startTime, endTime));
				setAttribute("PortTopNData",getDrawPerfTopNData(subsystemId, "A139", startTime, endTime));
			} else if (SrContant.DEVTYPE_VAL_DS.equals(dr.getString("type"))) {
				setAttribute("HeatTopNData",getDrawPerfTopNData(subsystemId, "A183", startTime, endTime));
				setAttribute("PortTopNData",getDrawPerfTopNData(subsystemId, "A300", startTime, endTime));
			} else if (SrContant.DEVTYPE_VAL_BSP.equals(dr.getString("type"))) {
				setAttribute("HeatTopNData",getDrawPerfTopNData(subsystemId, "A427", startTime, endTime));
				setAttribute("PortTopNData",getDrawPerfTopNData(subsystemId, "A406", startTime, endTime));
			} 
		} else {
			setAttribute("HeatTopNData", 0);
			setAttribute("PortTopNData", 0);
		}
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
	}
	/**
	 * 获取绘制TopN图数据
	 * @param devId
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public JSONObject getDrawPerfTopNData(String devId, String kpi, String startTime, String endTime) {
		JSONObject json = new JSONObject();
		//获取指定的KPI详细信息
		List<DataRow> kpiList = baseService.getKPIInfo("'" + kpi + "'");
		DataRow kpiRow = kpiList == null ? new DataRow() : kpiList.get(0);
		json.put("smallTitle", startTime + " ~ " + endTime);
		json.put("funits", kpiList.get(0).getString("funits"));
		JSONObject obj = baseService.generateTopNChartData(devId, kpiRow, startTime, endTime, 5);
		
		json.putAll(obj);
		json.put("ftitle", kpiRow.getString("ftitle"));
		json.put("charttype", "rcloumn");
		return json;
	}
	public ActionResult doDrawPerfLine() {
		setTpir();
		return new ActionResult("/WEB-INF/views/storage/ajaxStorageChart.jsp");
	}
	
	/**
	 * 性能图设置
	 * @return
	 */
	@Deprecated
	@SuppressWarnings("static-access")
	public ActionResult doStorageSettingPrf(){
		Integer level = getIntParameter("level",1);
		Integer subSystemID = getIntParameter("subSystemID");
		//获取用户可见存储
		String tpcLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		List<DataRow> rows = tpcService.getSubsystemNames(null, tpcLimitIds);
		List<DataRow> rows2 = srService.getSubsystemNames(null, srLimitIds);
		//获取所有存储设备性能指标
		List<DataRow> kpis = baseService.getView(null, SrContant.SUBDEVTYPE_STORAGE);
		DataRow dataRow2 = baseService.getPrfFieldInfo(null,level, SrContant.SUBDEVTYPE_STORAGE, null,subSystemID,null,getLoginUserId());
		if (dataRow2 == null && subSystemID != 0) {
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", subSystemID);
		}
		setAttribute("historyConfig", dataRow2);
		setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		for (DataRow row : rows) {
			String type = "";
			if("25".equals(row.getString("os_type"))){
				type="DS";
			}else if("21".equals(row.getString("os_type")) || "38".equals(row.getString("os_type"))){
				type="SVC";
			}else if("15".equals(row.getString("os_type")) || "37".equals(row.getString("os_type"))){
				type="BSP";
			}
			row.set("type", type);
			storageList.add(row);
		}
		for (DataRow row : rows2) {
			storageList.add(row);
		}
		if (subSystemID != null && subSystemID > 0) {
			DataRow drow = tpcService.getType(subSystemID);
			if (drow != null && drow.size() > 0) {
				setAttribute("type", drow.getString("type"));
			} else {
				setAttribute("type",srService.getStorageById(subSystemID.toString()).getString("storage_type"));
			}
		}
		this.setAttribute("devList", storageList);
		this.setAttribute("tryargs", kpis);
		this.setAttribute("kpisList", kpisList);
		this.setAttribute("subSystemID", subSystemID);
		return new ActionResult("/WEB-INF/views/storage/editStorage.jsp");
	}
	
	
	/**
	 * 性能图设置  这是HGC另外编写的
	 * @return
	 */
	public ActionResult doStorageSettingPrf2(){
		Integer level = getIntParameter("level", 1);
		Integer subSystemID = getIntParameter("subSystemID");
		boolean isSubSystemIDValid = subSystemID != null && subSystemID > 0;
		//获取用户可见存储
		String tpcLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		List<DataRow> rows = null;
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			rows = tpcService.getSubsystemNames(null, tpcLimitIds);
		}
		List<DataRow> rows2 = srService.getSubsystemNames(null, srLimitIds);
		//获取所有存储设备性能指标
		
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, 
				SrContant.SUBDEVTYPE_STORAGE, null, subSystemID, null, getLoginUserId());
		if (dataRow2 == null && isSubSystemIDValid) {
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", subSystemID);
		}
		setAttribute("historyConfig", dataRow2);
		setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		if (rows != null) {
			for (DataRow row : rows) {
				row.set("type", this.baseService.getStorageType(row.getString("os_type")));
				storageList.add(row);
			}
		}
		for (DataRow row : rows2) {
			storageList.add(row);
		}
		List<DataRow> kpis = null;
		if(isSubSystemIDValid) {
			DataRow drow = null;
			//判断是否有TPC配置
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				drow = tpcService.getType(subSystemID);
			}
			String type;
			if (drow != null && drow.size() > 0) {
				type = drow.getString("type");
			} 
			else { // 是否属于SR
				type = srService.getStorageById(subSystemID.toString()).getString("storage_type");
			}
			setAttribute("type", type);
			kpis = baseService.getView(type, SrContant.SUBDEVTYPE_STORAGE);
		}
		else {
			kpis = baseService.getView(null, SrContant.SUBDEVTYPE_STORAGE);
		}
		
		JSONArray kpisList = JSONArray.fromObject(kpis);
		this.setAttribute("devList", storageList);
		this.setAttribute("tryargs", kpis);
		this.setAttribute("kpisList", kpisList);
		this.setAttribute("subSystemID", subSystemID);
		return new ActionResult("/WEB-INF/views/storage/queryStorage.jsp");
	}
	void print(Object obj, String mark) {
		Logger.getLogger(getClass()).info("*****************************************************");
		Logger.getLogger(getClass()).info("mark: " + mark);
		Logger.getLogger(getClass()).info(JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss"));
		Logger.getLogger(getClass()).info("*****************************************************");
	}
	/**
	 * 这是最新，要修改，在这里修改
	 * 处理性能图设置信息
	 */
	public void doStoragePrf2(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de, "multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
		
		Integer graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		Integer topnValue = getIntParameter("topnValue", SrContant.TOPN_COUNT);
		
		StringBuffer kpi = new StringBuffer(50);
		for (int i = 0, len = kpis.length - 1; i <= len; i++) {
			kpi.append("'" + kpis[i] + "'");
			if (i < len) { kpi.append(","); }
		}
		String dev = "";
		if (devices != null && devices.length > 0) {
			StringBuffer device = new StringBuffer(50);
			for (int i = 0, len = devices.length - 1; i <= len; ++i) {
				device.append(devices[i]);
				if (i < len) { device.append(","); }
			}
			dev = device.toString();
		} 
		else {
			dev = devId.toString();
		}
		
		String startTime = MyUtilities.htmlToText(getStrParameter("startTime"));
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		Integer legend = getIntParameter("legend");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname", SrContant.SUBDEVTYPE_STORAGE);
		row.set("fdevicetype", storageType);
		row.set("fdevice", dev);
		row.set("fprfid", kpi.toString());
		row.set("fisshow", 1);
		List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString());
		if (units != null && units.size() > 0) {
			Set<String> set = new HashSet<String>();
			for (DataRow unit : units) {
				if (StringHelper.isNotEmpty(unit.getString("funits")))
					set.add(unit.getString("funits"));
			}
			String tempStr = set.toString().replace("[", "").replace("]", "");
			row.set("fyaxisname", tempStr.length() > 40 ? tempStr.substring(0, 37) + "..." : tempStr);
		} else {
			row.set("fyaxisname", "");
		}
		row.set("flegend", legend);
		row.set("fstarttime", startTime);
		row.set("fendtime", endTime);
		row.set("time_type", timeType); 
		row.set("fthreshold", threshold);
		row.set("fthreValue", threValue);
		
		row.set("graphtype", graphType);
        row.set("topnValue", topnValue);
        row.set("fuserid", getLoginUserId());
		
		try {
			baseService.updatePrfField(row, SrContant.SUBDEVTYPE_STORAGE, null, null, subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	/**
	 * 加载性能数据
	 * @return
	 */
	public ActionResult doStoragePrfPage() {
		doStoragePrfField();
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		
		String tablePage = getStrParameter("tablePage");
		if(tablePage != null && tablePage.length() > 0){
			return new ActionResult("/WEB-INF/views/storage/ajaxPrfStorage.jsp");
		}
		return new ActionResult("/WEB-INF/views/storage/prefStoragePage.jsp");
	}
	
	/**
	 * 处理性能图设置信息
	 */
	public void doStoragePrf(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de,"multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
		StringBuffer kpi = new StringBuffer();
		for (int i = 0, len = kpis.length - 1; i < kpis.length; i++) {
			kpi.append("'" + kpis[i] + "'");
			if (i < len) {
				kpi.append(",");
			}
		}
		String dev = "";
		if (devices != null && devices.length > 0) {
			StringBuffer device = new StringBuffer();
			for (int i = 0, len = devices.length - 1; i < devices.length; i++) {
				device.append(devices[i]);
				if (i < len) {
					device.append(",");
				}
			}
			dev = device.toString();
		} else {
			dev = devId.toString();
		}
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		Integer legend = getIntParameter("legend");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname",SrContant.SUBDEVTYPE_STORAGE);
		row.set("fdevicetype", storageType);
		row.set("fdevice", dev);
		row.set("fprfid", kpi.toString());
		row.set("fisshow", 1);
		List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString());
		if (units != null && units.size() > 0) {
			Set<String> set = new HashSet<String>();
			for (DataRow unit : units) {
				if (StringHelper.isNotEmpty(unit.getString("funits")))
					set.add(unit.getString("funits"));
			}
			String tempStr = set.toString().replace("[", "").replace("]", "");
			row.set("fyaxisname", tempStr.length() > 40 ? tempStr.substring(0, 37) + "..." : tempStr);
		} else {
			row.set("fyaxisname", "");
		}
		row.set("flegend", legend);
		row.set("fstarttime", startTime);
		row.set("fendtime", endTime);
		row.set("time_type", timeType);
		row.set("fthreshold", threshold);
		row.set("fthreValue", threValue);
		row.set("fuserid", getLoginUserId());
		try {
			baseService.updatePrfField(row, SrContant.SUBDEVTYPE_STORAGE, null, null, subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	/**
	 * 获取性能信息
	 */
	public void doStoragePrfField(){
		JSONObject json = new JSONObject();
		Integer subsystemId = getIntParameter("subSystemID");
		Integer level = getIntParameter("level", 1);
		if (subsystemId != null && subsystemId > 0) { level = 3; }
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		String storageType = getStrParameter("stotype", "");
		
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_STORAGE, 
				null, subsystemId, null, getLoginUserId());
		
		int graphType = SrContant.GRAPH_TYPE_LINE;
		int topCount = 5;
		double threvalue = Double.MAX_VALUE;
		boolean isLine = true;
		String startTime = "", endTime = "";
		String eleIds = "-1";
		String kpi = "a100";
		if (dataRow == null || dataRow.size() == 0) {//stotype
			if (SrContant.DEVTYPE_VAL_EMC.equalsIgnoreCase(storageType)) {
				dataRow = baseService.getDefaultRow("t_res_storagesubsystem", 
						subsystemId, SrContant.DEVTYPE_VAL_EMC, SrContant.SUBDEVTYPE_STORAGE, 
						"subsystem_id", "display_name");
				dataRow.set("graphtype", SrContant.GRAPH_TYPE_LINE);
				dataRow.set("fprfid", "'A112_01'");
				kpi = "A112_01";
			} else if(SrContant.DEVTYPE_VAL_HDS.equalsIgnoreCase(storageType)) {
				dataRow = baseService.getDefaultRow("t_res_storagesubsystem", 
						subsystemId, SrContant.DEVTYPE_VAL_HDS, SrContant.SUBDEVTYPE_STORAGE, 
						"subsystem_id", "display_name");
				dataRow.set("graphtype", SrContant.GRAPH_TYPE_LINE);
				dataRow.set("fprfid", "'A106_01'");
				kpi = "A106_01";
			} else if(WebConstants.STORAGE_TYPE_VAL_NETAPP.equalsIgnoreCase(storageType)) {
				dataRow = baseService.getDefaultRow("t_res_storagesubsystem", 
						subsystemId, WebConstants.STORAGE_TYPE_VAL_NETAPP, SrContant.SUBDEVTYPE_STORAGE, 
						"subsystem_id", "display_name");
				dataRow.set("graphtype", SrContant.GRAPH_TYPE_LINE);
				dataRow.set("fprfid", "'NA100_01'");
				kpi = "NA100_01";
			} else {
				//判断是否有TPC配置
				if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
					dataRow = baseService.getDefaultRow("v_res_storage_subsystem", 
							subsystemId, SrContant.DEVTYPE_VAL_SVC, SrContant.SUBDEVTYPE_STORAGE, 
							"subsystem_id", "the_display_name");
					dataRow.set("graphtype", SrContant.GRAPH_TYPE_LINE);
					dataRow.set("fprfid", "'a100'");
				}
			}
		}
		DataRow thead = new DataRow();
		DBPage tbody = null;
		if (dataRow != null && dataRow.size() > 0) {
			graphType = dataRow.getInt("graphtype");
			isLine = graphType == SrContant.GRAPH_TYPE_LINE;
			json.put("graphType", graphType);
			topCount = dataRow.getInt("topnvalue");
			startTime = dataRow.getString("fstarttime");
			endTime = dataRow.getString("fendtime");
			threvalue = dataRow.getDouble("fthrevalue");
			if(threvalue <= 0){ threvalue = Integer.MAX_VALUE; } // 阈值不可能是0和负数
			
			List<DataRow> devs = null;
			String devType = dataRow.getString("fdevicetype");
			String devices = dataRow.getString("fdevice");
			topCount = devices == null? 5 : devices.split(",").length;
			//获取设备信息列表
			//For EMC/HDS/NETAPP
			if (devType.equals(SrContant.DEVTYPE_VAL_EMC) 
					|| devType.equals(SrContant.DEVTYPE_VAL_HDS)
					|| devType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)){
				devs = baseService.getSRDeviceInfo(devices);
			//For DS/BSP/SVC
			} else {
				//判断是否有TPC配置
				if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
					devs = baseService.getDeviceInfo(devices, "subsystem_id", "the_display_name", "v_res_storage_subsystem");
				}
			}
			
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			
			String time_type = dataRow.getString("time_type");
			String viewPostfix = "";
			if(time_type == null){
				time_type = "minite";
			}
			if(time_type.contains("hour")){
				viewPostfix = "_hourly";
			}
			else if(time_type.contains("day")){
				viewPostfix = "_daily";
			}
			
			eleIds = dataRow.getString("fdevice");
			kpi = dataRow.getString("fprfid").split(",")[0].replace("'", "");
			
			//判断是否有TPC配置
			tbody = baseService.getPrfDatas(curPage, numPerPage, devs, kpis,
					dataRow.getString("fstarttime"), dataRow.getString("fendtime"), time_type);
			if(level == 1 || level == 2){
				StringBuilder tableHeader = new StringBuilder(50);
				StringBuilder tableBody = new StringBuilder(5000);
				//////////////////////////////////////////////////////
				tableHeader.append("<th style='text-align:center;'>");
				tableHeader.append("设备名");
				tableHeader.append("</th>");
				List<String> headers = new ArrayList<String>(kpis.size());
				for (DataRow r : kpis) {
					headers.add(r.getString("fid").toLowerCase());
					tableHeader.append("<th style='text-align:center;'>");
					tableHeader.append(r.getString("ftitle").concat("(" + r.getString("funits") + ")"));
					tableHeader.append("</th>");
				}
				tableHeader.append("<th style='text-align:center;'>");
				tableHeader.append("时间");
				tableHeader.append("</th>");
				if(tbody != null && tbody.getData() != null && tbody.getData().size() > 0){
					List<DataRow> data = tbody.getData();
					Object obj;
					DecimalFormat df = new DecimalFormat("#.##");
					double t;
					float t2;
					int t3;
					String criticalLb = " style='color:red'";
					for(DataRow dr : data){
						tableBody.append("<tr>"); // 一个dr代表table的一行
						tableBody.append("<td class='rc-td'>");
						tableBody.append(dr.getString("ele_name"));
						tableBody.append("</td>");
						for(String key : headers){
							tableBody.append("<td class='rc-td'><span");
							obj = dr.getObject(key);
							if(obj instanceof Double){
								t = (Double)obj;
								if(t > threvalue){
									tableBody.append(criticalLb);
								}
								tableBody.append('>');
								tableBody.append(df.format(t));
							}
							else if(obj instanceof Float){
								t2 = (Float)obj;
								if(t2 > threvalue){
									tableBody.append(criticalLb);
								}
								tableBody.append('>');
								tableBody.append(df.format(t2));
							}
							else if(obj instanceof Integer) {
								t3 = (Integer)obj;
								if(t3 > threvalue){
									tableBody.append(criticalLb);
								}
								tableBody.append('>');
								tableBody.append(t3);
							}
							else {
								tableBody.append(obj);
							}
							tableBody.append("</span></td>");
						}
						tableBody.append("<td class='rc-td'>");
						tableBody.append(dr.getString("prf_timestamp"));
						tableBody.append("</td>");
						tableBody.append("</tr>");
					}
					json.put("totalPages", tbody.getTotalPages());
					json.put("currentPage", tbody.getCurrentPage());
					json.put("numPerPage", tbody.getNumPerPage());
				}
				else {
					tableBody.append("<tr><td colspan='" + (kpis.size() + 2) + "'>暂无数据</tr>");
					json.put("totalPages", 0);
					json.put("currentPage", 0);
					json.put("numPerPage", 0);
				}
				json.put("thead", tableHeader.toString());
				json.put("tbody", tableBody.toString());
			}
			else {
				for (DataRow r : kpis) {
					thead.set(r.getString("fid"), r.getString("ftitle").concat("(" +
							r.getString("funits") + ")"));
				}
				thead.set("prf_timestamp", "时间");
				thead.set("ele_name", "设备名");
				json.put("thead", thead);
				json.put("tbody", tbody);
			}
			
			if(tablePage == null || tablePage.length() == 0){ // 是否需要画图，如果需要，那么不要在URL添加tablePage
				if(isLine){
					//获取绘制性能图数据
					json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"),
							devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),
							dataRow.getString("time_type"))));
				}
				else {
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						json.put("series", JSON.toJSONString(
								baseService.getTopnGraph(eleIds, kpi, startTime, endTime, topCount,
										viewPostfix, SrContant.SUBDEVTYPE_STORAGE, threvalue)));
					}
				}
			}
			json.put("legend", dataRow.getInt("flegend") == 1);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("kpiInfo", kpis);
		}
		
		if("1".equals(getStrParameter("isFreshen"))){
			writeDataToPage(json.toString());
		}
		else{
			this.setAttribute("prfData", json);
		}
	}
	
	public void doCapacityInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = tpcService.getSubsystemInfo(subsystemId);
		DecimalFormat decFmt = new DecimalFormat("0.00");
		Double usedSpace = Double.parseDouble(decFmt.format(row.getDouble("the_allocated_capacity")));
		Double availableSpace = Double.parseDouble(decFmt.format(row.getDouble("the_available_capacity")));
		Double theSpace = usedSpace+availableSpace;
		Double perUsedSpace = Double.parseDouble(decFmt.format(usedSpace/theSpace*100));
		Double perAvailableSpace = Double.parseDouble(decFmt.format(availableSpace/theSpace*100));
		
		JSONObject json = new JSONObject();
		json.put("usedSpace", usedSpace);
		json.put("availableSpace", availableSpace);
		json.put("perUsedSpace", perUsedSpace);
		json.put("perAvailableSpace", perAvailableSpace);
		json.put("the_display_name", row.getString("the_display_name"));
		this.setAttribute("jsonVal", json);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
	}
	
	public void doExportPerfData(){
		try {
			Integer level = getIntParameter("level");
			Integer subsystemId = getIntParameter("subSystemID");
			
			DataRow dataRow = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_STORAGE, null, subsystemId, null, getLoginUserId());
			List<DataRow> devs = null;
			String devType = dataRow.getString("fdevicetype");
			//获取设备信息列表
			//For EMC/HDS/NETAPP
			if (devType.equals(SrContant.DEVTYPE_VAL_EMC) 
					|| devType.equals(SrContant.DEVTYPE_VAL_HDS)
					|| devType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
				devs = baseService.getSRDeviceInfo(dataRow.getString("fdevice"));
			//For DS/BSP/SVC
			} else {
				devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "subsystem_id", "the_display_name", "v_res_storage_subsystem");
			}
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			
			List<String> theadKey = new ArrayList<String>(kpis == null? 2 : kpis.size() + 2);
			List<String> theadTitle = new ArrayList<String>(theadKey.size());
			theadKey.add("ele_name");
			theadTitle.add("设备名");
			String temp;
			for (DataRow r : kpis) {
				temp = r.getString("fid");
				if(temp != null){ theadKey.add(temp.toLowerCase()); }
				temp = r.getString("ftitle");
				if(temp != null){ theadTitle.add(temp.concat("(" + r.getString("funits") + ")")); }
			}
			theadKey.add("prf_timestamp");
			theadTitle.add("时间");
			List<DataRow> tbody = baseService.getPrfDatas(devs, kpis,dataRow.getString("fstarttime"), dataRow.getString("fendtime"),	dataRow.getString("time_type"));
			getResponse().setCharacterEncoding("GBK");
			if (tbody != null && tbody.size() > 0) {
				String[] title = new String[theadTitle.size()];
				theadTitle.toArray(title);
				String[] key = new String[theadKey.size()];
				theadKey.toArray(key);
				String time;
				for (int i = 0; i < tbody.size(); i++) {
					time = tbody.get(i).getString("prf_timestamp");
					tbody.get(i).set("prf_timestamp", time == null? null : time.replace("-", "/"));
				}
				CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name").concat("_Perf_Data"), tbody, title, key);
			}
			else {
				CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name").concat("_Perf_Data"), new ArrayList<DataRow>(), 
						new String[]{"暂无数据导出"}, new String[]{});
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(getClass()).error("", e);
		}
	}
	
	/**
	 * 导出性能数据
	 */
//	@SuppressWarnings("unchecked")
	public void doExportPrefData() {
		doExportPerfData();
		/*Integer level = getIntParameter("level");
		Integer subsystemId = getIntParameter("subSystemID");
		
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, 
				SrContant.SUBDEVTYPE_STORAGE, null, subsystemId, null);
		List<DataRow> devs = null;
		String devType = dataRow.getString("fdevicetype");
		//获取设备信息列表
		//For EMC/HDS
		if (devType.equals(SrContant.DEVTYPE_VAL_EMC) 
				|| devType.equals(SrContant.DEVTYPE_VAL_HDS)) {
			devs = baseService.getSRDeviceInfo(dataRow.getString("fdevice"));
		} 
		else { //For DS/BSP/SVC
			devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), 
					"subsystem_id", "the_display_name", "v_res_storage_subsystem");
		}
		List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
		DataRow thead = new DataRow();
		thead.set("prf_timestamp", "时间");
		thead.set("ele_name", "设备名");
		for (DataRow r : kpis) {
			thead.set(r.getString("fid"), r.getString("ftitle"));
		}
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis, dataRow.getString("fstarttime"), 
				dataRow.getString("fendtime"),dataRow.getString("time_type"));
		DecimalFormat df = new DecimalFormat("#.##");
		if (tbody != null && tbody.size() > 0) {
			String[] title = (String[]) thead.values().toArray(new String[thead.size()]);
			String[] key = new String[thead.keySet().size()];
			Iterator<Object> it = thead.keySet().iterator();
			for (int i = 0; i < thead.keySet().size(); i++) {
				key[i] = it.next().toString().toLowerCase();
			}
			for (int i = 0; i < tbody.size(); i++) {
				tbody.get(i).set("a229", df.format(tbody.get(i).getDouble("a229")));
			}
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name"), 
					tbody, title, key);
		}*/
	}
	
	/**
	 * 导出配置信息
	 */
	public void doExportStorageConfigData(){
		String storageName = getStrParameter("storageName").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String type = getStrParameter("type");
		String serialNumber = getStrParameter("serialNumber");
		Integer startPoolCap = getIntParameter("startPoolCap");
		Integer endPoolCap = getIntParameter("endPoolCap");
		Integer startPoolAvailableCap = getIntParameter("startPoolAvailableCap");
		Integer endPoolAvailableCap = getIntParameter("endPoolAvailableCap");
		
		//获取用户可见存储
		String tpcLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		List<DataRow> rows = new ArrayList<DataRow>();
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			rows = tpcService.getStorageList(storageName, ipAddress, type, serialNumber,
					startPoolCap, endPoolCap, startPoolAvailableCap, endPoolAvailableCap, tpcLimitIds);
		}
		List<DataRow> rows2 = srService.getExportStorageList(storageName, ipAddress, type, serialNumber,
				startPoolCap,endPoolCap, startPoolAvailableCap, endPoolAvailableCap, srLimitIds);

		int start = Integer.MAX_VALUE;
		if (rows != null && rows2 != null) {
			start = rows.size();
			rows.addAll(rows2);
		}
		
		String[] title = new String[]{"名称", "厂商", "IP地址", "状态", "物理磁盘容量(G)", "池容量(G)",
				"池可用容量(G)","卷总容量(G)","已分配卷容量(G)","未分配卷容量","最近探查时间"}; //,"缓存"};
		String[] keys = new String[]{"the_display_name", "vendor_name", "ip_address","the_propagated_status",
				
				"the_physical_disk_space","the_storage_pool_consumed_space",
				"the_storage_pool_available_space","the_volume_space",
				"the_assigned_volume_space","the_unassigned_volume_space",
				"last_probe_time"};//,"cache"};
		
		getResponse().setCharacterEncoding("GBK");
		if (rows != null && rows.size() > 0) {
			String temp;
			DataRow dr;
			DecimalFormat df = new DecimalFormat("#.##");
			double t;
			for (int i = 0, size = start; i < size; ++i) {// 属于TPC的
				dr = rows.get(i);
				t = dr.getDouble("the_physical_disk_space");
				dr.set("the_physical_disk_space", df.format(t));
				t = dr.getDouble("the_storage_pool_consumed_space");
				dr.set("the_storage_pool_consumed_space", df.format(t));
				t = dr.getDouble("the_storage_pool_available_space");
				dr.set("the_storage_pool_available_space", df.format(t));
				t = dr.getDouble("the_volume_space");
				dr.set("the_volume_space", df.format(t));
				t = dr.getDouble("the_assigned_volume_space");
				dr.set("the_assigned_volume_space", df.format(t));
				t = dr.getDouble("the_unassigned_volume_space");
				dr.set("the_unassigned_volume_space", df.format(t));
				//dr.set("cache", df.format(dr.getDouble("cache")));
			}
			for (int i = start, size = rows.size(); i < size; ++i) {// 属于SR的
				dr = rows.get(i);
				temp = dr.getString("the_propagated_status");
				if(temp == null || temp.trim().isEmpty()){ dr.set("the_propagated_status", "Normal"); }
				t = dr.getDouble("the_physical_disk_space") / 1024.0;
				dr.set("the_physical_disk_space", df.format(t));
				t = dr.getDouble("the_storage_pool_consumed_space") / 1024.0;
				dr.set("the_storage_pool_consumed_space", df.format(t));
				t = dr.getDouble("the_storage_pool_available_space") / 1024.0;
				dr.set("the_storage_pool_available_space", df.format(t));
				t = dr.getDouble("the_volume_space") / 1024.0;
				dr.set("the_volume_space", df.format(t));
				t = dr.getDouble("the_assigned_volume_space") / 1024.0;
				dr.set("the_assigned_volume_space", df.format(t));
				t = dr.getDouble("the_unassigned_volume_space") / 1024.0;
				dr.set("the_unassigned_volume_space", df.format(t));
				//dr.set("cache", df.format(dr.getDouble("cache")));
			}
			CSVHelper.createCSVToPrintWriter(getResponse(), "Storage_Config_Data", rows, title, keys);
		} else {
			CSVHelper.createCSVToPrintWriter(getResponse(), "Storage_Config_Data", new ArrayList<DataRow>(0), 
					new String[]{"暂无数据可导出！"}, new String[]{});
		}
	}
	
	private String[] checkStrArray(String[] str, String mach){
		if(str == null || str.length == 0){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (String string : str) {
			if(!string.equals(mach)){
				list.add(string);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	private void writeDataToPage(String data) {
		PrintWriter writer = null;
		try {
			getResponse().setCharacterEncoding("UTF-8");
			writer = getResponse().getWriter();
			writer.print(data);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		}
	}
	
	public ActionResult doPreRename(){
		//获取用户可见存储
		String tpcLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		String srLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		List<DataRow> storageList = tpcService.getStorageNames(tpcLimitIds);
		List<DataRow> srStorageNames = srService.getStorageNames(srLimitIds);
		if(srStorageNames != null && srStorageNames.size() > 0){
			storageList.addAll(srStorageNames);
		}
		setAttribute("storageList", storageList);
		return new ActionResult("/WEB-INF/views/storage/renameNew.jsp");
	}
	
	@Deprecated
	public void doStorageRename1(){
		String tar = "";
		try {
			tar = URLDecoder.decode(getStrParameter("targets"),"utf-8");
			tar = tar.replace("&amp;acute;", "'"); 
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if(StringHelper.isNotEmpty(tar)){
			JSONArray targets =JSONArray.fromString(tar);
			List<DataRow> list = new ArrayList<DataRow>();
			List<DataRow> dataList = new ArrayList<DataRow>();
			for (int i = 0; i < targets.size(); i++) {
				DataRow row = new DataRow();
				JSONObject json = targets.getJSONObject(i);
				row.set("custom_name", json.get("name"));
				row.set("device_id", json.get("id")); 
				row.set("unit_code", "Tpc.StorageSystem");
				if(StringHelper.isNotEmpty(row.getString("custom_name"))){
					list.add(row);
					row.set("unit_code", "Sr.StorageSystem");
					dataList.add(row);
				}
			}
			boolean result = (tpcService.storageRename(list) && srService.storageRename(dataList));
			writeDataToPage(String.valueOf(result));
		}
	}
	
	
	private DBPage mergePage(DBPage page1, DBPage page2){
		if(page1 != null && page1.getData() != null && page1.getData().size() > 0){
			if(page2 != null && page2.getData() != null && page2.getData().size() > 0){
				for (Object obj : page2.getData()) {
					page1.getData().add(obj);
				}
			}
			return page1;
		}else if(page2 != null && page2.getData() != null && page2.getData().size() > 0){
			return page2;
		}
		return null;
	}
	
	private List<DataRow> mergeList(List<DataRow> list1 , List<DataRow> list2){
		if(list1 != null && list1.size() > 0){
			if(list2 != null && list2.size() > 0 ){
				for (int i = 0; i < list2.size(); i++) {
					list1.add(list2.get(i));
				}
			}
			return list1;
		}else if(list2 != null && list2.size() > 0){
			return list2;
		}
		return null;
	}
	
	/**
	 * 存储重命名
	 */
	public void doStorageRename(){
		int srSize = getIntParameter("srSize");
		int tpcSize = getIntParameter("tpcSize");
		List<DataRow> srData = new ArrayList<DataRow>(srSize);
		List<DataRow> tpcData = new ArrayList<DataRow>(tpcSize);
		long id;
		for (int i = 0, total = tpcSize + srSize; i < total; ++i) {
			id = getLongParameter("id" + i, -1);
			if(id <= 0L){ continue; }
			DataRow row = new DataRow();
			row.set("custom_name", getStrParameter("name" + i));
			row.set("device_id", id);
			if (SrContant.DBTYPE_SR.equalsIgnoreCase(getStrParameter("dbt" + i))) {
				row.set("unit_code", "Sr.StorageSystem");
				srData.add(row);
			} else {
				row.set("unit_code", "Tpc.StorageSystem");
				tpcData.add(row);
			}
		}
		boolean b = srService.storageRename(srData) && tpcService.storageRename(tpcData);
		writeDataToPage(String.valueOf(b));
	}
}
