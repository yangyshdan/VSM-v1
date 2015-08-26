package root.virtual;

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
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;

public class VirtualAction extends SecurityAction {
	BaseprfService baseService = new BaseprfService();
	VirtualmachineService service = new VirtualmachineService();
	
	/**
	 * 虚拟机列表页面
	 * @return
	 */
	public ActionResult doVirtualPage(){
		//列表信息
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.LIST_NUM_PER_PAGE);
		Integer hypervisorId = getIntParameter("hypervisorId");
		//获取用户可见虚拟机
		String limitDevIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		page = service.getVirtualmachinePage(curPage, numPerPage, hypervisorId, null, null, null, null, null, limitDevIds);
		setAttribute("virtualPage", page);
		setAttribute("hypervisorId", hypervisorId);
		setAttribute("isShowCap", 1);
		String vmIds = extractIds(page, "vm_id");
		if (StringHelper.isNotEmpty(vmIds) && StringHelper.isNotBlank(vmIds)) {
			setVMCapacity(vmIds, null, null);
		} else {
			setVMCapacity("0", null, null);
		}
		return new ActionResult("/WEB-INF/views/virtual/virtualList.jsp");
	}

	/**
	 * 分页展示虚拟机列表信息
	 * @return
	 */
	public ActionResult doAjaxVirtualPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.LIST_NUM_PER_PAGE);
		Integer hypervisorId = getIntParameter("hypervisorId");
		String virtualName = getStrParameter("virtualName").replaceAll("&amp;nbsp;", " ");
		Integer endMemory = getIntParameter("endMemory");
		Integer startMemory = getIntParameter("startMemory");
		Integer startDiskSpace = getIntParameter("startDiskSpace");
		Integer endDiskSpace = getIntParameter("endDiskSpace");
		//是否显示容量图
		int isShowCap = getIntParameter("isShowCap",0);
		//获取用户可见虚拟机
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, hypervisorId);
		page = service.getVirtualmachinePage(curPage, numPerPage, hypervisorId,virtualName,endMemory,startMemory,startDiskSpace,endDiskSpace,limitIds);
		setAttribute("virtualPage", page);
		setAttribute("hypervisorId", hypervisorId);
		setAttribute("virtualName", virtualName);
		setAttribute("endMemory", endMemory);
		setAttribute("startMemory", startMemory);
		setAttribute("startDiskSpace", startDiskSpace);
		setAttribute("endDiskSpace", endDiskSpace);
		setAttribute("isShowCap", isShowCap);
		
		//获取绘制容量图数据
		if (isShowCap == 1) {
			String vmIds = extractIds(page, "vm_id");
			if (StringHelper.isNotEmpty(vmIds) && StringHelper.isNotBlank(vmIds)) {
				setVMCapacity(vmIds, null, null);
			} else {
				setVMCapacity("0", null, null);
			}
		}
		return new ActionResult("/WEB-INF/views/virtual/ajaxVirtual.jsp");
	}
	
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
	private void setVMCapacity(String vmIds, String hypIds, String compIds){
		try {
			List<DataRow> drs = service.getVMCapacityByIds(vmIds, hypIds, compIds);
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
					d.set("vm_id", dr.getString("vm_id"));
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
//				Logger.getLogger(getClass()).info(JSON.toJSONStringWithDateFormat(json, "yyyy-MM-dd HH:mm:ss"));
//			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass()).error("", ex);
			setAttribute("serverCapacity", "获取虚拟机的硬盘容量数据失败");
		}
	}
	
	/**
	 * @see 一次性读取所有硬盘容量
	 */
	public void doGetVMCapacity(){
		JsonData jsonData = new JsonData();
		try{
			//获取用户可见虚拟机
			String limitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
			List<DataRow> drs = service.getVMCapacityByIds(limitIds, null, null);
			if(drs != null && drs.size() > 0){
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
					d.set("vm_id", dr.getString("vm_id"));
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
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取物理机的磁盘容量数据失败");
		}
		print(jsonData);
	}
	
	/**
	 * 虚拟机详细信息页面
	 * @return
	 */
	public ActionResult doVirtualInfo(){
		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId = getIntParameter("vmId");
		//虚拟机详细信息
		DataRow row = service.getVirtualInfo(vmId, hypervisorId);
		setAttribute("virtualInfo", row);
		//总览部分
		setDrawPerfChartData();
		
		// HGC的代码
		int level = this.getIntParameter("level", SrContant.INVALID_VALUE);
		int state = this.getIntParameter("state", SrContant.INVALID_VALUE);
		int tabToShow = this.getIntParameter("tabToShow", SrContant.TAB_SUMMARY);
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
		setAttribute("overViewTab", overViewTab);
		setAttribute("detailTab", detailTab);
		setAttribute("prfTab", prfTab);
		setAttribute("alertTab", alertTab);
		setAttribute("dataTab", dataTab);
		setAttribute("attachment", String.format("&level=%s&state=%s&tabToShow=%s", level, state, tabToShow));
		// HGC的代码
		//告警信息
		setDeviceLogInfo(hypervisorId.toString(),vmId.toString());
		//告警信息列表
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage = deviceService.getLogPage(1, WebConstants.NumPerPage, -1,
				hypervisorId.toString(),null,vmId.toString(),null,SrContant.SUBDEVTYPE_VIRTUAL,
				state, level, null, null);
		setAttribute("logPage",devicePage);
		setAttribute("hypervisorId", hypervisorId);
		setAttribute("computerId", computerId);
		setAttribute("vmId", vmId);
		setAttribute("level", level);
		setAttribute("state", state);
		
		return new ActionResult("/WEB-INF/views/virtual/virtualInfo.jsp");
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
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId = getIntParameter("vmId");
		String timeRange = getStrParameter("timeRange");
		//虚拟机详细信息
		DataRow row = service.getVirtualInfo(vmId, hypervisorId);
		//总览信息
		List<DataRow> devList = new ArrayList<DataRow>();
		DataRow devRow = new DataRow();
		devRow.set("ele_id", row.getString("vm_id"));
		devRow.set("ele_name", row.getString("display_name"));
		devList.add(devRow);
		
		//处理选择日期
		Date date = new Date();
		String startTime = null;
		String endTime = SrContant.getTimeFormat(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (StringHelper.isNotEmpty(timeRange) && StringHelper.isNotBlank(timeRange)) {
			//Hour
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
		setAttribute("cpuPerfData", getDrawPerfLineData(devList, "V2", startTime, endTime));
		//Memory Used
		setAttribute("memPerfData", getDrawPerfLineData(devList, "V3", startTime, endTime));
		//Network Total Packets
		setAttribute("netPerfData", getDrawPerfLineData(devList, "V15", startTime, endTime));
		//Disk Total Bytes
		setAttribute("diskPerfData", getDrawPerfLineData(devList, "V16", startTime, endTime));
		//Server Type
		setAttribute("serverType", SrContant.SUBDEVTYPE_VIRTUAL);
		//Virtual Machine CPU Busy TopN
		setAttribute("cpuTopNData", new JSONObject());
		//Virtual Machine Memory Used TopN	
		setAttribute("memTopNData", new JSONObject());
	}
	
	/**
	 * 获取绘制性能图的数据
	 * @param devList
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public JSONObject getDrawPerfLineData(List<DataRow> devList, String kpi, String startTime, String endTime) {
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
	 * 获取设备告警信息
	 * @param hypervisorId
	 * @param vmId
	 */
	public void setDeviceLogInfo(String hypervisorId,String vmId) {
		final String count = "count";
		final String timeLen = "timelen";
		final String lateTime = "lateTime";
		//获取告警信息
		DeviceAlertService deviceService = new DeviceAlertService();
		List<DataRow> resultList = deviceService.getCompDeviceLogList(hypervisorId, null, SrContant.SUBDEVTYPE_VIRTUAL, vmId, null, null, 0, 0, null, null);
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
	
	/**
	 * 导出虚拟机配置信息
	 */
	public void doExportVirtualConfigData(){
		Integer hypervisorId = getIntParameter("hypervisorId");
		String virtualName=getStrParameter("virtualName").replaceAll("&amp;nbsp;", " ");
		Integer endMemory = getIntParameter("endMemory");
		Integer startMemory = getIntParameter("startMemory");
		Integer startDiskSpace = getIntParameter("startDiskSpace");
		Integer endDiskSpace = getIntParameter("endDiskSpace");
		DecimalFormat df = new DecimalFormat("#.##");
		//获取用户可见虚拟机
		String limitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		List<DataRow> rows = service.getVirtualList(hypervisorId, virtualName, endMemory, startMemory, startDiskSpace, endDiskSpace,limitIds);
		getResponse().setCharacterEncoding("GBK");
		if (rows != null && rows.size() > 0) {
			String[] title = new String[]{"名称","所属物理机","IP地址","逻辑CPU个数","物理CPU个数","总内存(GB)","磁盘总容量(GB)","磁盘剩余容量(GB)","磁盘容量使用率(%)","更新时间"};
			String[] keys = new String[]{"display_name","host_name","ip_address","assigned_cpu_number","assigned_cpu_processunit","total_memory","disk_space","disk_available_space","percent","update_timestamp"};
			for (int i = 0; i < rows.size(); i++) {
				rows.get(i).set("total_memory", df.format(rows.get(i).getDouble("total_memory")/1024));
				rows.get(i).set("disk_space", df.format(rows.get(i).getDouble("disk_space")/1024));
				rows.get(i).set("disk_available_space", df.format(rows.get(i).getDouble("disk_available_space")/1024));
				rows.get(i).set("percent", df.format(rows.get(i).getDouble("percent")));
			}
			CSVHelper.createCSVToPrintWriter(getResponse(), "Virtual_Config_Data", rows, title, keys);
		} else {
			CSVHelper.createCSVToPrintWriter(getResponse(), "Virtual_Config_Data", 
					new ArrayList<DataRow>(0), new String[]{"暂无数据可导出！"}, new String[]{});
		}
	}
	
	@Deprecated
	public ActionResult doVirtualSettingPrf(){
		Integer level = getIntParameter("level",1);
//		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId = getIntParameter("vmId");
		List<DataRow> rows = service.getVirtualName(vmId, hypervisorId, null);
		List<DataRow> kpis = baseService.getView(SrContant.DEVTYPE_VAL_HOST, SrContant.SUBDEVTYPE_VIRTUAL);
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.DEVTYPE_VAL_HOST, hypervisorId, vmId, getLoginUserId());
		if(dataRow2==null&&vmId!=0){
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", vmId);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		for (DataRow row : rows) {
			row.set("name", row.getString("name"));
			row.set("id", row.getString("vm_id"));
			storageList.add(row);
		}
		this.setAttribute("devList", storageList);
		this.setAttribute("vmId", vmId);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("kpisList", kpisList);
		return new ActionResult("/WEB-INF/views/virtual/editVirtual.jsp");
	}
	
	
	public ActionResult doVirtualSettingPrf2(){
		Integer level = getIntParameter("level",1);
//		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId = getIntParameter("vmId");
		List<DataRow> rows = service.getVirtualName(vmId, hypervisorId, null);
		List<DataRow> kpis = baseService.getView(SrContant.DEVTYPE_VAL_HOST, SrContant.SUBDEVTYPE_VIRTUAL);
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.DEVTYPE_VAL_HOST, hypervisorId, vmId, getLoginUserId());
		if (dataRow2 == null && vmId != 0) {
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", vmId);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		//获取用户可见虚拟机
		String limitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		//过滤可见设备
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			String[] ids = limitIds.indexOf(",") > 0 ? limitIds.split(",") : new String[] { limitIds };
			for (DataRow row : rows) {
				for (int i = 0; i < ids.length; i++) {
					if (ids[i].equals(row.getString("vm_id"))) {
						row.set("name", row.getString("name"));
						row.set("id", row.getString("vm_id"));
						storageList.add(row);
					}
				}
			}
		} else {
			for (DataRow row : rows) {
				row.set("name", row.getString("name"));
				row.set("id", row.getString("vm_id"));
				storageList.add(row);
			}
		}
		this.setAttribute("devList", storageList);
		this.setAttribute("vmId", vmId);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("kpisList", kpisList);
		return new ActionResult("/WEB-INF/views/virtual/queryVirtual.jsp");
	}
	
	
	public void doVirtualPrfField(){
		JSONObject json = new JSONObject();
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId = getIntParameter("vmId");
		Integer level = getIntParameter("level", 2);
		if(vmId != null && vmId > 0){ level = 3; }
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		boolean isFreshen = "1".equals(getStrParameter("isFreshen"));
		boolean isLine = true;
		DataRow thead = new DataRow();
		DBPage tbody = null;
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.DEVTYPE_VAL_HOST, hypervisorId, vmId, getLoginUserId());
		//给默认性能信息
		int graphType = SrContant.GRAPH_TYPE_LINE;
		int topCount = 5;
		double threvalue = Double.MAX_VALUE;
		String startTime = "", endTime = "";
		if(dataRow == null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("t_res_virtualmachine", vmId, SrContant.DEVTYPE_VAL_HOST, SrContant.SUBDEVTYPE_VIRTUAL, "vm_id", "name");
			dataRow.set("fprfid", "'v1'");
			dataRow.set("fyaxisname", "%");
			dataRow.set("graphtype", SrContant.GRAPH_TYPE_LINE);
		}
		String eleIds = "-1";
		String kpi = "V1";
		if(dataRow != null && dataRow.size() > 0){
			graphType = dataRow.getInt("graphtype");
			isLine = graphType == SrContant.GRAPH_TYPE_LINE;
			json.put("graphType", graphType);
			topCount = dataRow.getInt("topnvalue");
			startTime = dataRow.getString("fstarttime");
			endTime = dataRow.getString("fendtime");
			threvalue = dataRow.getDouble("fthrevalue");
			if(threvalue <= 0){ threvalue = Integer.MAX_VALUE; } // 阈值不可能是0和负数
			
			eleIds = dataRow.getString("fdevice");
			topCount = eleIds == null? 5 : eleIds.split(",").length;
			List<DataRow> devs = baseService.getDeviceofHostInfo(eleIds, "vm_id", "name", "t_res_virtualmachine");
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
			
			kpi = dataRow.getString("fprfid").split(",")[0].replace("'", "");
			
			tbody = baseService.getPrfDatas(curPage, numPerPage, devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"), time_type);
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
					json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"), 
								devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"), 
								dataRow.getString("time_type"))));
				}
				else {
					json.put("series", JSON.toJSONString(
							baseService.getTopnGraph(eleIds, kpi, startTime, endTime, topCount,
									viewPostfix, SrContant.SUBDEVTYPE_VIRTUAL, threvalue)));
				}
			}
			json.put("legend", dataRow.getInt("flegend") == 1? true : false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("kpiInfo", kpis);
		}
		
		if(isFreshen){
			writeDataToPage(json.toString());
		}
		else{
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
	public ActionResult doVirtualPrfPage() {
		doVirtualPrfField();
		setAttribute("hypervisorId", getIntParameter("hypervisorId"));
		setAttribute("vmId", getIntParameter("vmId"));
		//this.setAttribute("computerId", getIntParameter("computerId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length() > 0){
			return new ActionResult("/WEB-INF/views/virtual/ajaxPrfVirtual.jsp");
		}
		
		return new ActionResult("/WEB-INF/views/virtual/prefVirtualPage.jsp");
	}
	public void doVirtualPrf(){
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de,"multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
		
		Integer graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		Integer topnValue = getIntParameter("topnValue", SrContant.TOPN_COUNT);
		
		StringBuffer kpi = new StringBuffer();
		for (int i = 0, len = kpis.length - 1; i <= len; i++) {
			kpi.append("'" + kpis[i] + "'");
			if (i < len) {
				kpi.append(",");
			}
		}
		String dev = "";
		if (devices != null && devices.length > 0) {
			StringBuffer device = new StringBuffer();
			for (int i = 0, len = devices.length - 1; i <= len; i++) {
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
		Integer showname = getIntParameter("isshow",getIntParameter("hideisshow"));
		Integer legend = getIntParameter("legend");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", hypervisorId);
		row.set("level", level);
		row.set("fname","Virtual");
        row.set("fdevicetype",storageType);
        row.set("fdevice",dev);
        row.set("fprfid",kpi.toString());
        row.set("fisshow",showname);
        List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString()); 
		if (units != null && units.size() > 0) {
        	Set<String> set = new HashSet<String>();
        	for (DataRow unit : units) {
				if (StringHelper.isNotEmpty(unit.getString("funits"))) {
					set.add(unit.getString("funits"));
				}
			}
        	String tempStr = set.toString().replace("[", "").replace("]", "");
        	row.set("fyaxisname", tempStr.length()>40?tempStr.substring(0, 37)+"...":tempStr);
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
			baseService.updatePrfField(row, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.DEVTYPE_VAL_HOST, devId, hypervisorId, level);
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
	 * 导出虚拟机性能信息
	 * @see 建议转换日期的方法
	 */
	public void doExportPrefData() {
		try {
			Integer level = getIntParameter("level");
			Integer vmId = getIntParameter("vmId");
			Integer hypervisorId = getIntParameter("hypervisorId");
			DataRow dataRow = baseService.getPrfFieldInfo(null, level,
					SrContant.SUBDEVTYPE_VIRTUAL,SrContant.DEVTYPE_VAL_HOST, hypervisorId, vmId, getLoginUserId());
			//给默认性能信息
			if(dataRow==null || dataRow.size()==0){
				dataRow = baseService.getDefaultRow("t_res_virtualmachine", vmId, 
						SrContant.DEVTYPE_VAL_HOST, SrContant.SUBDEVTYPE_VIRTUAL, "vm_id", "name");
			}
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), 
					"vm_id", "name", "t_res_virtualmachine");
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
				CSVHelper.createCSVToPrintWriter(getResponse(), 
						devs.get(0).getString("ele_name").concat("_Perf_Data"), tbody, title, key);
			}
			else {
				CSVHelper.createCSVToPrintWriter(getResponse(), 
						devs.get(0).getString("ele_name").concat("_Perf_Data"), new ArrayList<DataRow>(), 
						new String[]{"暂无数据导出"}, new String[]{});
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(getClass()).error("", e);
		}
	}
	
//	//topn图表
//	 public JSONObject getTopChart(Integer computerId,String Table,String tname) {
//		    JSONObject json = new JSONObject();
//		    List<DataRow> cpus = baseService.getTop10(computerId,Table,tname);
//		    JSONArray Name = new JSONArray();
//		    JSONArray Data = new JSONArray();
//		    JSONArray Time = new JSONArray();
//		    for (DataRow cpu : cpus) {
//		    	Name.add(cpu.getString("ele_name"));
//		    	Data.add(Double.valueOf(NumericHelper.round(cpu.getDouble("prf"), 2)));
//		    	Time.add(cpu.getString("prf_timestamp"));
//		    }
//		    json.put("Name", Name);
//		    json.put("Data", Data);
//		    json.put("time", Time);
//		    return json;
//		  }
}
