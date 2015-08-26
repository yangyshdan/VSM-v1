package root.hypervisor;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.hypervisor.HypervisorService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;

public class HypervisorAction extends SecurityAction {
	BaseprfService baseService = new BaseprfService();
	HypervisorService service = new HypervisorService();
	
	/**
	 * 物理机列表页面
	 * @return
	 */
	public ActionResult doHypervisorPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.LIST_NUM_PER_PAGE);
		String limitDevIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		page = service.getHypervisorPage(curPage, numPerPage, null, null, null, null, null, null, null, null, limitDevIds);
		setAttribute("hypervisorPage", page);
		String hypIds = extractIds(page, "hypervisor_id");
		if (StringHelper.isNotEmpty(hypIds) && StringHelper.isNotBlank(hypIds)) {
			setHypCapacity(hypIds, null);
		} else {
			setHypCapacity("0", null);
		}
		//虚拟机TopN图不作展示
//		setVMTopn(page, "hypervisor_id");
		return new ActionResult("/WEB-INF/views/hypervisor/hypervisorList.jsp");

	}
	
	/**
	 * @see 将Page里的id值抽取出来
	 * @param page
	 * @param idName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String extractIds(DBPage page, String idName){
		if(page != null && page.getData() != null && page.getData().size() > 0){
			List<DataRow> drs = page.getData();
			StringBuilder ids = new StringBuilder(drs.size() * 5);
			ids.append(drs.get(0).getString(idName));
			for(int i = 1, size = drs.size(); i < size; ++i){
				ids.append(',');
				ids.append(drs.get(i).getString(idName));
			}
			return ids.toString();
		}
		return null;
	}
	/**
	 * @see 读取画图数据
	 * @param hypIds
	 * @param compIds
	 */
	private void setHypCapacity(String hypIds, String compIds){
		try {
			List<DataRow> drs = service.getHypervisorCapacityByHypId(hypIds, compIds);
//			if(drs != null && drs.size() > 0){
				// 查询server name
				// 查询服务器数据 server data
				// 其他comp_id hyp_id
				Map<String, Object> json = new HashMap<String, Object>(3);
				List<String> names = new ArrayList<String>(drs.size());
				List<Float> usedData = new ArrayList<Float>(drs.size());
				List<Float> availableData = new ArrayList<Float>(drs.size());
				List<Map<String, Object>> categories = new ArrayList<Map<String, Object>>(2);
				Map<String, Object> categories1 = new HashMap<String, Object>(3);
				Map<String, Object> categories2 = new HashMap<String, Object>(3);
				List<DataRow> ids = new ArrayList<DataRow>(drs.size());
				for(DataRow dr : drs){
					names.add(dr.getString("name"));
					usedData.add(dr.getFloat("used"));
					availableData.add(dr.getFloat("available"));
					DataRow d = new DataRow();
					d.set("hyp_id", dr.getString("hyp_id"));
					d.set("comp_id", dr.getString("comp_id"));
					ids.add(d);
				}
				categories1.put("name", "空余容量");
				categories1.put("data", availableData);
				categories1.put("color", "#6CCA16");
				
				categories2.put("name", "已用容量");
				categories2.put("data", usedData);
				categories2.put("color", "#C8C1CF");
				categories.add(categories1);
				categories.add(categories2);
				json.put("names", names);
				json.put("categories", categories);
				json.put("ids", ids);
				setAttribute("serverCapacity", JSON.toJSONStringWithDateFormat(json, "yyyy-MM-dd HH:mm:ss"));
//			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass()).error("", ex);
			setAttribute("serverCapacity", "获取物理机的磁盘容量数据失败");
		}
	}
	
	public void doGetHypervisorCapacity(){
		JsonData jsonData = new JsonData();
		try{
			String limitDevIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
			List<DataRow> drs = service.getHypervisorCapacityByHypId(limitDevIds, null);
			if (drs != null && drs.size() > 0) {
				// 查询server name
				// 查询服务器数据 server data
				// 其他comp_id hyp_id
				Map<String, Object> json = new HashMap<String, Object>(3);
				List<String> names = new ArrayList<String>(drs.size());
				List<Float> usedData = new ArrayList<Float>(drs.size());
				List<Float> availableData = new ArrayList<Float>(drs.size());
				List<Map<String, Object>> categories = new ArrayList<Map<String, Object>>(2);
				Map<String, Object> categories1 = new HashMap<String, Object>(3);
				Map<String, Object> categories2 = new HashMap<String, Object>(3);
				List<DataRow> ids = new ArrayList<DataRow>(drs.size());
				for(DataRow dr : drs){
					names.add(dr.getString("name"));
					usedData.add(dr.getFloat("used"));
					availableData.add(dr.getFloat("available"));
					DataRow d = new DataRow();
					d.set("hyp_id", dr.getString("hyp_id"));
					d.set("comp_id", dr.getString("comp_id"));
					ids.add(d);
				}
				categories1.put("name", "空余容量");
				categories1.put("data", availableData);
				categories1.put("color", "#6CCA16");
				
				categories2.put("name", "已用容量");
				categories2.put("data", usedData);
				categories2.put("color", "#C8C1CF");
				categories.add(categories1);
				categories.add(categories2);
				json.put("names", names);
				json.put("categories", categories);
				json.put("ids", ids);
				jsonData.setValue(json);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass()).error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取物理机的磁盘容量数据失败");
		}
		printWithDate(jsonData);
	}
	
	/**
	 * 物理机列表分页和筛选查询
	 * @return
	 */
	public ActionResult doAjaxHypervisorPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.LIST_NUM_PER_PAGE);
		String displayName=getStrParameter("displayName").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String cpuArchitecture=getStrParameter("cpuArchitecture");
		Integer startRamSize = getIntParameter("startRamSize");
		Integer endRamSize = getIntParameter("endRamSize");
		Integer startDiskSpace = getIntParameter("startDiskSpace");
		Integer endDiskSpace = getIntParameter("endDiskSpace");
		String limitDevIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		page = service.getHypervisorPage(curPage, numPerPage, null, displayName, ipAddress,cpuArchitecture,startRamSize,endRamSize, startDiskSpace, endDiskSpace, limitDevIds);
		setAttribute("hypervisorPage", page);
		setAttribute("displayName", displayName);
		setAttribute("ipAddress", ipAddress);
		setAttribute("cpuArchitecture", cpuArchitecture);
		setAttribute("startRamSize", startRamSize);
		setAttribute("endRamSize", endRamSize);
		setAttribute("startDiskSpace", startDiskSpace);
		setAttribute("endDiskSpace", endDiskSpace);
		
		String hypIds = extractIds(page, "hypervisor_id");
		if (StringHelper.isNotEmpty(hypIds) && StringHelper.isNotBlank(hypIds)) {
			setHypCapacity(hypIds, null);
		} else {
			setHypCapacity("0", null);
		}
//		setVMTopn(page, "hypervisor_id");
		return new ActionResult("/WEB-INF/views/hypervisor/ajaxHypervisor.jsp");
	}
	
	/**
	 * 物理机的详细信息及性能
	 * @return
	 */
	public ActionResult doHypervisorInfo(){
		Integer computerId = getIntParameter("computerId");
		Long hypervisorId = getLongParameter("hypervisorId");
		//物理机详细信息
		DataRow row = service.getHypervisorInfo(hypervisorId, computerId);
		setAttribute("hypervisorInfo", row);
		
		DBPage sdr = service.getSensorStatusData(1, WebConstants.NumPerPage, hypervisorId);
		setAttribute("sdrPage", sdr);
		setAttribute("sdrCount", sdr != null? sdr.getTotalRows() : 0);
		
//		//HYPERVISOR信息
//		DataRow vpRow = service.getVirtualPlatByHypvId(hypervisorId);
//		setAttribute("hypvInfo", vpRow);
		
		//总览部分
		setDrawPerfChartData();
		//告警信息
		setDeviceLogInfo(hypervisorId.toString());
		VirtualmachineService virtualmachineService = new VirtualmachineService();
		//该物理机下用户可见的虚拟机
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, hypervisorId.intValue());
		DBPage virtualPage = virtualmachineService.getVirtualmachinePage(1, WebConstants.NumPerPage, hypervisorId.intValue(), null, null, null, null, null, limitIds);
		setAttribute("virtualPage", virtualPage);
		setAttribute("virtualCount", virtualPage.getTotalRows());
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		
		// HGC的代码
		int level = this.getIntParameter("level", -1);
		int state = this.getIntParameter("state", -1);
		int tabToShow = this.getIntParameter("tabToShow", SrContant.TAB_SUMMARY);
		setAttribute("tabToShow", tabToShow);
		String overViewTab = "", detailTab = "", prfTab = "", alertTab = "", dataTab = "";
		switch(tabToShow){
			case 0: overViewTab = "active"; break;
			case 1: detailTab = "active"; break;
			case 2: prfTab = "active"; break;
			case 3: alertTab = "active"; break; 
			case 4: dataTab = "active"; break;
		}
		setAttribute("overViewTab", overViewTab);
		setAttribute("detailTab", detailTab);
		setAttribute("prfTab", prfTab);
		setAttribute("alertTab", alertTab);
		setAttribute("dataTab", dataTab);
		setAttribute("attachment", String.format("&level=%s&state=%s&tabToShow=%s", level, state, tabToShow));
		// HGC的代码
		DBPage devicePage = deviceService.getLogPage(1, WebConstants.NumPerPage, -1, 
				hypervisorId.toString(), null, hypervisorId.toString(), null, 
				SrContant.SUBDEVTYPE_PHYSICAL, state, level, null, null);
		
		setAttribute("deviceLogPage", devicePage);
		setAttribute("hypervisorId", hypervisorId);
		setAttribute("computerId", computerId);
		setAttribute("level", level);
		setAttribute("state", state);
		
		return new ActionResult("/WEB-INF/views/hypervisor/hypervisorInfo.jsp");
	}
	
	public ActionResult doSdrPage() {
		Integer curPage = getIntParameter("curPage");
		Integer numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		Long hypervisorId = getLongParameter("hypervisorId");
		DBPage sdr = service.getSensorStatusData(curPage, numPerPage, hypervisorId);
		setAttribute("sdrPage", sdr);
		setAttribute("hypervisorId", hypervisorId);
		return new ActionResult("/WEB-INF/views/hypervisor/ajaxHardwareStatus.jsp");
	}
	
	/**
	 * 处理绘制性能图数据
	 */
	public ActionResult doDrawPerfLine() {
		setDrawPerfChartData();
		return new ActionResult("/WEB-INF/views/chart/ajaxServerChart.jsp");
	}
	
	/**
	 * 处理绘制性能图数据
	 */
	public void setDrawPerfChartData() {
		Integer computerId = getIntParameter("computerId");
		Long hypervisorId = getLongParameter("hypervisorId");
		String timeRange = getStrParameter("timeRange");
		//物理机详细信息
		DataRow row = service.getHypervisorInfo(hypervisorId, computerId);
		//总览信息
		List<DataRow> devList = new ArrayList<DataRow>();
		DataRow devRow = new DataRow();
		devRow.set("ele_id", row.getString("hypervisor_id"));
		devRow.set("ele_name", row.getString("display_name"));
		devList.add(devRow);
		
		//处理选择日期
		Date date = new Date();
		String startTime = null;
		String endTime = SrContant.getTimeFormat(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (StringHelper.isNotEmpty(timeRange) && StringHelper.isNotBlank(timeRange)) {
			//HourString timeRange = getStrParameter("timeRange");
			if (timeRange.equals(WebConstants.TIME_RANGE_HOUR)) {
				calendar.add(Calendar.HOUR, -1);
			//Day
			} else if (timeRange.equals(WebConstants.TIME_RANGE_DAY)) {
				calendar.add(Calendar.DATE, -1);
			//Week
			} else if (timeRange.equals(WebConstants.TIME_RANGE_WEEK)) {
				calendar.add(Calendar.WEEK_OF_MONTH, -1);
			//Month
			} else if (timeRange.equals(WebConstants.TIME_RANGE_MONTH)) {
				calendar.add(Calendar.MONTH, -1);
			}
		//默认查找前一小时(Hour)
		} else {
			calendar.add(Calendar.HOUR, -1);
//			calendar.add(Calendar.YEAR, -1);
		}
		startTime = SrContant.getTimeFormat(calendar.getTime());
		
		//CPU Busy
		setAttribute("cpuPerfData", getDrawPerfLineData(devList, "H1", startTime, endTime));
		//Memory Used
		setAttribute("memPerfData", getDrawPerfLineData(devList, "H2", startTime, endTime));
		
		//Network Total Packets
		setAttribute("netPerfData", getDrawPerfLineData(devList, "H14", startTime, endTime));
		//Disk Total Bytes
		setAttribute("diskPerfData", getDrawPerfLineData(devList, "H15", startTime, endTime));
		//Server Type
		setAttribute("serverType", SrContant.SUBDEVTYPE_PHYSICAL);
		
//		//Virtual Machine CPU Busy TopN
//		setAttribute("cpuTopNData", getDrawPerfTopNData(hypervisorId.toString(), "V2", startTime, endTime));
//		//Virtual Machine Memory Used TopN
//		setAttribute("memTopNData", getDrawPerfTopNData(hypervisorId.toString(), "V3", startTime, endTime));
	}
	
	/**
	 * 获取绘制性能图的数据
	 * @param devList
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private JSONObject getDrawPerfLineData(List<DataRow> devList, String kpi, String startTime, String endTime) {
		JSONObject json = new JSONObject();
		//获取指定的KPI详细信息
		List<DataRow> kpis = baseService.getKPIInfo("'" + kpi + "'");
		//获取绘图数据
		JSONArray array = baseService.getPrfDatas(0, devList, kpis, startTime, endTime);
		json.put("series", array);
		json.put("legend", true);
		json.put("ytitle", "");
		json.put("threshold", 0);
		json.put("threvalue", "");
		return json;
	}
	
	/**
	 * 获取绘制TopN图数据
	 * @param devId
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private JSONObject getDrawPerfTopNData(String devId, String kpi, String startTime, 
			String endTime) {
		JSONObject json = new JSONObject();
		//获取指定的KPI详细信息
		List<DataRow> kpiList = baseService.getKPIInfo("'" + kpi + "'");
		DataRow kpiRow = kpiList == null ? new DataRow() : kpiList.get(0);
		json.put("smallTitle", startTime + " ~ " + endTime);
		json.put("funits", "%");
		JSONObject obj = baseService.generateTopNChartData(devId, kpiRow, startTime, endTime, 5);
		
		json.putAll(obj);
		json.put("ftitle", kpiRow.getString("ftitle"));
		json.put("charttype", "rcloumn");
		return json;
	}
	
	/**
	 * 获取设备告警信息
	 * @param devId
	 */
	private void setDeviceLogInfo(String devId) {
		final String count = "count";
		final String timeLen = "timelen";
		final String lateTime = "lateTime";
		//获取告警信息
		DeviceAlertService deviceService = new DeviceAlertService();
		List<DataRow> resultList = deviceService.getCompDeviceLogList(devId, null, SrContant.SUBDEVTYPE_PHYSICAL, null, null, null, 0, 0, null, null);
		DataRow errorRow = new DataRow();
		DataRow warnRow = new DataRow();
		DataRow infoRow = new DataRow();
		DecimalFormat df = new DecimalFormat("###");
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				DataRow row = resultList.get(i);
				int fLevel = row.getInt("flevel");
				int fCount = row.getInt("fcount");
				//String fFirstTime = row.getString("ffirsttime");
				String fLastTime = row.getString("flasttime");
				String fTimeLen = "0";
				Double timeLenTemp = row.getDouble("timelen");
				//是否大于24小时,转换成天
				if (timeLenTemp >= 24) {
					fTimeLen = df.format(timeLenTemp/24).concat("天");
				//默认为小时
				} else if (timeLenTemp >= 1) {
					fTimeLen = df.format(timeLenTemp).concat("小时");
				//是否小于小时,转换成分钟
				} else if (timeLenTemp > 0) {
					fTimeLen = df.format(timeLenTemp*60).concat("分钟");
				}
				//Level(Critical:2;Warning:1;Info:0)
				if (fLevel == 2) {
					errorRow.set(count, fCount);
					errorRow.set(timeLen, fTimeLen);
					errorRow.set(lateTime, fLastTime);
				} else if (fLevel == 1) {
					warnRow.set(count, fCount);
					warnRow.set(timeLen, fTimeLen);
					warnRow.set(lateTime, fLastTime);
				} else if (fLevel == 0) {
					infoRow.set(count, fCount);
					infoRow.set(timeLen, fTimeLen);
					infoRow.set(lateTime, fLastTime);
				}
			}
		} else {
			errorRow.set(count, 0);
			errorRow.set(timeLen, 0);
			warnRow.set(count, 0);
			warnRow.set(timeLen, 0);
			infoRow.set(count, 0);
			infoRow.set(timeLen, 0);
		}
		setAttribute("errorData", errorRow);
		setAttribute("warnData", warnRow);
		setAttribute("infoData", infoRow);
	}
	
	@Deprecated
	@SuppressWarnings("static-access")
	public ActionResult doHypervisorSettingPrf(){
		Integer level = getIntParameter("level",1);
//		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		int graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		List<DataRow> rows = service.getHypervisorName(hypervisorId,null);
		List<DataRow> kpis = baseService.getView(SrContant.DEVTYPE_VAL_HOST, "Physical");
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, "Physical", SrContant.DEVTYPE_VAL_HOST, hypervisorId, null, graphType, getLoginUserId());
		if(dataRow2==null&&hypervisorId!=0){
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", hypervisorId);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		for (DataRow row : rows) {
			row.set("name", row.getString("name"));
			row.set("id", row.getString("hypervisor_id"));
			storageList.add(row);
		}
		this.setAttribute("devList", storageList);
//		this.setAttribute("computerId", computerId);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("kpisList", kpisList);
		return new ActionResult("/WEB-INF/views/hypervisor/editHypervisor.jsp");
	}
	
	/**
	 * @see 获取查询条件的页面，包括读取历史记录
	 * 2015-04-16 13:19:12 modified by HGC.
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doHypervisorSettingPrf2(){
		Integer level = getIntParameter("level", 1);
//		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		// 默认画
		//int graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		List<DataRow> rows = service.getHypervisorName(hypervisorId, null);
		List<DataRow> kpis = baseService.getView(SrContant.DEVTYPE_VAL_HOST, SrContant.SUBDEVTYPE_PHYSICAL);
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.DEVTYPE_VAL_HOST, hypervisorId, null, null, getLoginUserId());
		if(dataRow2 == null && hypervisorId != 0){
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", hypervisorId);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		//获取用户可见的物理机
		String limitDevIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		if (StringHelper.isNotEmpty(limitDevIds) && StringHelper.isNotBlank(limitDevIds)) {
			String[] ids = limitDevIds.indexOf(",") > 0 ? limitDevIds.split(",") : new String[] { limitDevIds };
			for (DataRow row : rows) {
				for (int i = 0; i < ids.length; i++) {
					if (ids[i].equals(row.getString("hypervisor_id"))) {
						row.set("name", row.getString("name"));
						row.set("id", row.getString("hypervisor_id"));
						storageList.add(row);
					}
				}
			}
		} else {
			for (DataRow row : rows) {
				row.set("name", row.getString("name"));
				row.set("id", row.getString("hypervisor_id"));
				storageList.add(row);
			}
		}
		this.setAttribute("devList", storageList);
		//this.setAttribute("graphType", graphType);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("kpisList", kpisList);
		
		return new ActionResult("/WEB-INF/views/hypervisor/queryHypervisor.jsp");
	}
	
	/**
	 * @see 保存性能图的条件, 不需要考虑哪一种画图类型，只要把相应的数据保存到数据库表即可
	 */
	public void doHypervisorPrf(){
//		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer devId = getIntParameter("devId");
		Integer graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		Integer topnValue = getIntParameter("topnValue", SrContant.TOPN_COUNT);
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de,"multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		Integer showname = getIntParameter("isshow",getIntParameter("hideisshow"));
		Integer legend = getIntParameter("legend");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		String yname = getStrParameter("yname").replaceAll("&amp;nbsp;", " ");
		try {
			yname = new String(yname.getBytes("iso-8859-1"), "utf8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		StringBuilder kpi = new StringBuilder(200);
		for (int i = 0, len = kpis.length - 1;i <= len;i++) {
			kpi.append("'" + kpis[i] + "'");
			if(i < len){ kpi.append(","); }
		}
		String dev = "";
		if(devices != null && devices.length > 0){
			StringBuilder device = new StringBuilder(150);
			for (int i = 0, len = devices.length - 1; i <= len; i++) {
				device.append(devices[i]);
				if(i < len){ device.append(","); }
			}
			dev = device.toString();
		}
		else{
			dev = devId.toString();
		}
		
		DataRow row = new DataRow();
		row.set("fsubsystemid", hypervisorId);
		row.set("level", level);
		row.set("fname", "Physical");
        row.set("fdevicetype", storageType);
        row.set("fdevice", dev);
        row.set("fprfid", kpi.toString());
        row.set("fisshow", showname);
        List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString()); 
		if (units != null && units.size() > 0) {
        	Set<String> set = new HashSet<String>();
        	for (DataRow unit : units) {
        		if(StringHelper.isNotEmpty(unit.getString("funits"))) {
        			set.add(unit.getString("funits"));
        		}
			}
        	String tempStr = set.toString().replace("[", "").replace("]", "");
			row.set("fyaxisname",tempStr.length() > 40 ? tempStr.substring(0, 37) + "..." : tempStr);
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
			baseService.updatePrfField(row, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.DEVTYPE_VAL_HOST, devId, hypervisorId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	
	private String[] checkStrArray(String[] str,String mach){
		if(str==null || str.length == 0){
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
	
	/**
	 * @see 这是获取画图数据的方法
	 */
	@SuppressWarnings("unchecked")
	public void doHypervisorPrfField(){
		JSONObject json = new JSONObject();
	//	Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer level = getIntParameter("level", 1);
		if(hypervisorId != null && hypervisorId > 0){ level = 3; }
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
//		Integer graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		String isFreshen = getStrParameter("isFreshen");
		DataRow thead = new DataRow();
		DBPage tbody = null;
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.DEVTYPE_VAL_HOST, hypervisorId, null, null,getLoginUserId());
		//给默认性能信息
		int graphType = SrContant.GRAPH_TYPE_LINE;
		int topCount = 5;
		String startTime = "", endTime = "";
		if(dataRow == null || dataRow.size() == 0){
			dataRow = baseService.getDefaultRow("t_res_hypervisor", hypervisorId, SrContant.DEVTYPE_VAL_HOST, SrContant.SUBDEVTYPE_PHYSICAL, "hypervisor_id", "name");
			dataRow.set("fprfid", "'H1'");
			dataRow.set("fyaxisname", "%");
		}
		String devices = "-1";
		String kpi = "H1";
		double threvalue = Double.MAX_VALUE;
		if(dataRow != null && dataRow.size() > 0){
			graphType = dataRow.getInt("graphtype");
			json.put("graphType", graphType);
//			topCount = dataRow.getInt("topnvalue");
			startTime = dataRow.getString("fstarttime");
			endTime = dataRow.getString("fendtime");
			threvalue = dataRow.getDouble("fthrevalue");
			if(threvalue <= 0.00){ threvalue = Double.MAX_VALUE; }
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"),"hypervisor_id", "name", "t_res_hypervisor");
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
			
			devices = dataRow.getString("fdevice");
			topCount = devices == null? 5 : devices.split(",").length;
			kpi = dataRow.getString("fprfid").split(",")[0].replace("'", "");
			
			tbody = baseService.getPrfDatas(curPage, numPerPage, devs, kpis, dataRow.getString("fstarttime"), 
					dataRow.getString("fendtime"), time_type);
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
//					DateFormat dateFmt = new SimpleDateFormat();
					DecimalFormat df = new DecimalFormat("######.##");
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
					thead.set(r.getString("fid"), r.getString("ftitle").concat("(" + r.getString("funits") + ")"));
				}
				thead.set("prf_timestamp", "时间");
				thead.set("ele_name", "设备名");
				json.put("thead", thead);
				json.put("tbody", tbody);
			}
			//////////////////////////////////////////////////////
			if((tablePage == null || tablePage.trim().length() == 0)){
				if(graphType == SrContant.GRAPH_TYPE_LINE){
					json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"), 
							devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"), 
							dataRow.getString("time_type"))));
				}
				else {
					json.put("series", JSON.toJSONString(
							baseService.getTopnGraph(devices, kpi, startTime, 
							endTime, topCount, viewPostfix, SrContant.SUBDEVTYPE_PHYSICAL, threvalue)));
				}
				
			}
			
			json.put("legend", dataRow.getInt("flegend")==1? true : false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("kpiInfo", kpis);
			
		}
		
		if("1".equals(isFreshen)){ // 如果是刷新，那么就通过Ajax返回
			writeDataToPage(json.toString());
		}
		else{ // 反之，就直接写到JSP页面 topnData
			this.setAttribute("prfData", json);
		}
	}
	
	/**
	 * 适应多台设备
	 * <%--
			level为1表示跳转到是存储的非详细页面
			level为2表示跳转到是非存储的非详细页面
			level为3表示跳转到是存储或非存储的详细页面
		--%>
		这个方法是针对非存储的非详细页面
	 */
	public void doHypervisorPrfField02(){
		JSONObject json = new JSONObject();
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer level = getIntParameter("level", 1);
		if(hypervisorId != null && hypervisorId > 0){ level = 3; }
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);

		String isFreshen = getStrParameter("isFreshen");
		DataRow thead = new DataRow();
		DBPage tbody = null;
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_PHYSICAL,
				SrContant.DEVTYPE_VAL_HOST, hypervisorId, null, null, getLoginUserId());
		//给默认性能信息
		int graphType = SrContant.GRAPH_TYPE_LINE;
		int topCount = 5;
		String startTime = "", endTime = "";
		if(dataRow == null || dataRow.size() == 0){
			dataRow = baseService.getDefaultRow("t_res_hypervisor", hypervisorId, 
					SrContant.DEVTYPE_VAL_HOST, SrContant.SUBDEVTYPE_PHYSICAL, "hypervisor_id", "name");
			dataRow.set("fprfid", "'H1'");
			dataRow.set("fyaxisname", "%");
		}
		String devices;
		String kpi = "H1";
		if(dataRow != null && dataRow.size() > 0){
			graphType = dataRow.getInt("graphtype");
			json.put("graphType", graphType);
			topCount = dataRow.getInt("topnvalue");
			startTime = dataRow.getString("fstarttime");
			endTime = dataRow.getString("fendtime");
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"),
					"hypervisor_id", "name", "t_res_hypervisor");
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			
			devices = dataRow.getString("fdevice");
			kpi = dataRow.getString("fprfid").split(",")[0].replace("'", "");
			
			if(level != null && level == 3){
				thead.set("prf_timestamp", "时间");
				thead.set("ele_name", "设备名");
				for (DataRow r : kpis) {
					thead.set(r.getString("fid"), r.getString("ftitle").concat("(" + r.getString("funits") + ")"));
				}
				tbody = baseService.getPrfDatas(curPage, numPerPage, devs, kpis, dataRow.getString("fstarttime"), 
						dataRow.getString("fendtime"), dataRow.getString("time_type"));
			}
			if((tablePage == null || tablePage.trim().length() == 0)){
				if(graphType == SrContant.GRAPH_TYPE_LINE){
					json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"), 
							devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"), 
							dataRow.getString("time_type"))));
				}
				else {
					json.put("series", JSON.toJSONString(
							baseService.getTopnGraph(devices, kpi, startTime, 
							endTime, topCount, "time_type", "", 0.0)));
				}
				
			}
			json.put("thead", thead);
			json.put("tbody", tbody);
			json.put("legend", dataRow.getInt("flegend") == 1? true : false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("kpiInfo", kpis);
			
		}
		
		if("1".equals(isFreshen)){ // 如果是刷新，那么就通过Ajax返回
			writeDataToPage(json.toString());
		}
		else{ // 反之，就直接写到JSP页面 topnData
			this.setAttribute("prfData", json);
		}
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
	
	/**
	 * @see 
	 * @return
	 */
	public ActionResult doHypervisorPrfPage() {
		doHypervisorPrfField();
		this.setAttribute("hypervisorId", getIntParameter("hypervisorId"));
		this.setAttribute("computerId", getIntParameter("computerId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage != null && tablePage.length() > 0){
			return new ActionResult("/WEB-INF/views/hypervisor/ajaxPrfHypervisor.jsp");
		}
		return new ActionResult("/WEB-INF/views/hypervisor/prefHypervisorPage.jsp");
	}
	
	public ActionResult doVirtualPrfPage(){
		//性能曲线
		doHypervisorPrfField();
		this.setAttribute("vmId", getIntParameter("vmId"));
		this.setAttribute("hypervisorId", getIntParameter("hypervisorId"));
	//	this.setAttribute("hypervisorId", getIntParameter("hypervisorId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/virtual/ajaxPrfVirtual.jsp");
		}
		return new ActionResult("/WEB-INF/views/virtual/prefVirtualPage.jsp");
	}
	/**
	 * 导出物理机配置信息
	 */
	public void doExportHypervisorConfigData(){
		String displayName = getStrParameter("displayName").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		Integer startRamSize = getIntParameter("startRamSize");
		Integer endRamSize = getIntParameter("endRamSize");
		Integer startDiskSpace = getIntParameter("startDiskSpace");
		Integer endDiskSpace = getIntParameter("endDiskSpace");
		DecimalFormat df = new DecimalFormat("#.##");
		String limitDevIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		List<DataRow> rows = service.getHypervisorList(null, null,displayName, ipAddress, startRamSize, endRamSize, startDiskSpace, endDiskSpace, limitDevIds);
		if (rows != null && rows.size() > 0) {
			String[] title = new String[]{"名称","IP地址","处理器架构","处理器总数","内存(GB)","未分配CPU","未分配内存(GB)","磁盘总容量(GB)","磁盘剩余容量(GB)","磁盘容量使用率(%)","更新时间"};
			String[] keys = new String[]{"display_name","ip_address","cpu_architecture","processor_count","ram_size","available_cpu","available_mem","disk_space","disk_available_space","percent","update_timestamp"};
			for (int i = 0; i < rows.size(); i++) {
				rows.get(i).set("ram_size", df.format(rows.get(i).getDouble("ram_size")/1024));
				rows.get(i).set("available_mem", df.format(rows.get(i).getDouble("available_mem")/1024));
				rows.get(i).set("disk_space", df.format(rows.get(i).getDouble("disk_space")/1024));
				rows.get(i).set("disk_available_space", df.format(rows.get(i).getDouble("disk_available_space")/1024));
				rows.get(i).set("percent", df.format(rows.get(i).getDouble("percent")));
			}
			getResponse().setCharacterEncoding("GBK");
			CSVHelper.createCSVToPrintWriter(getResponse(), "Physical_Config_Data", rows, title, keys);
		}
	}
	/**
	 * 导出物理机性能信息
	 * 2015/03/31 11:02:55
	 * 2015-03-31 11:02:55
	 */
	public void doExportPrefData() {
		try {
			Integer level = getIntParameter("level");
			//Integer computerId = getIntParameter("computerId");
			Integer hypervisorId = getIntParameter("hypervisorId");
			DataRow dataRow = baseService.getPrfFieldInfo(null, level,SrContant.SUBDEVTYPE_PHYSICAL, SrContant.DEVTYPE_VAL_HOST, hypervisorId, null, getLoginUserId());
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), "hypervisor_id", "name", "t_res_hypervisor");
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
			List<DataRow> tbody = baseService.getPrfDatas(devs, kpis,dataRow.getString("fstarttime"), 
					dataRow.getString("fendtime"),	dataRow.getString("time_type"));
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
				getResponse().setCharacterEncoding("GBK");
				CSVHelper.createCSVToPrintWriter(getResponse(),devs.get(0).getString("ele_name").concat("_Perf_Data"), tbody, title, key);
			}
			else {
				CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name").concat("_Perf_Data"), new ArrayList<DataRow>(), 
						new String[]{"暂无数据导出"}, new String[]{});
			}
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("", e);
			e.printStackTrace();
		}
	}

}
