package root.virtualPlat;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.virtualPlat.VirtualPlatService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class VirtualPlatAction extends SecurityAction {
	
	VirtualPlatService virtualPlatService = new VirtualPlatService();
	VirtualmachineService vmService = new VirtualmachineService();
	BaseprfService baseService = new BaseprfService();
	
	/**
	 * 虚拟平台HYPERVISOR列表页面
	 * @return
	 */
	public ActionResult doVirtualPlatPage() {
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String limitDevIds = (String) getSession().getAttribute(WebConstants.HYPERVISOR_LIST);
		DBPage virtPlatPage = virtualPlatService.getVirtualPlatInfoPage(null, null, null, curPage, numPerPage, limitDevIds);
		setAttribute("virtPlatPage", virtPlatPage);
		return new ActionResult("/WEB-INF/views/virtualPlat/virtualPlatList.jsp");
	}
	
	/**
	 * 分页查询虚拟平台HYPERVISOR信息
	 * @return
	 */
	public ActionResult doAjaxVirtPlatPage() {
		String name = getStrParameter("name");
		String type = getStrParameter("type");
		String physicalName = getStrParameter("physicalName");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		String limitDevIds = (String) getSession().getAttribute(WebConstants.HYPERVISOR_LIST);
		DBPage virtPlatPage = virtualPlatService.getVirtualPlatInfoPage(name, type, physicalName, curPage, numPerPage, limitDevIds);
		setAttribute("name", name);
		setAttribute("type", type);
		setAttribute("physicalName", physicalName);
		setAttribute("virtPlatPage", virtPlatPage);
		return new ActionResult("/WEB-INF/views/virtualPlat/ajaxVirtPlat.jsp");
	}
	
	/**
	 * 物理机的详细信息及性能
	 * @return
	 */
	public ActionResult doVirtualPlatInfo(){
		String virtualPlatId = getStrParameter("virtualPlatId");
		Integer physicalId = getIntParameter("physicalId");
		int showVmTab = getIntParameter("showVmTab",0);
		//获取用户可见的虚拟机
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, physicalId);
		//获取指定的HYPERVISOR详细信息
		DataRow virtPlatInfo = virtualPlatService.getVirtualPlatInfo(virtualPlatId);
		//获取虚拟机列表
		List<DataRow> vmList = vmService.getVirtualListByPhysicalId(String.valueOf(physicalId),limitIds);
		getSession().setAttribute("vpVmList", vmList);
		//总览部分
		setDrawPerfChartData();
		//分页获取虚拟机列表
		DBPage virtualPage = vmService.getVirtualmachinePage(1,WebConstants.NumPerPage,physicalId,null,null,null,null,null,limitIds);
		setAttribute("showVmTab", showVmTab);
		setAttribute("virtPlatInfo", virtPlatInfo);
		setAttribute("virtualPage", virtualPage);
		setAttribute("vmList", JSONArray.fromObject(vmList));
		return new ActionResult("/WEB-INF/views/virtualPlat/virtualPlatInfo.jsp");
	}
	
	/**
	 * 处理绘制性能图数据
	 */
	public ActionResult doDrawPerfLine() {
		setDrawPerfChartData();
		return new ActionResult("/WEB-INF/views/virtualPlat/ajaxVirtPlatChart.jsp");
	}
	
	/**
	 * 处理绘制性能图数据
	 */
	@SuppressWarnings("unchecked")
	public void setDrawPerfChartData() {
		String physicalId = getStrParameter("physicalId");
		String timeRange = getStrParameter("timeRange");
		String vmIdsStr = getStrParameter("vmIds");
		//获取虚拟机列表
		List<DataRow> vmList = (List<DataRow>) getSession().getAttribute("vpVmList");
		List<DataRow> devList = new ArrayList<DataRow>();
		if (StringHelper.isNotBlank(vmIdsStr) && StringHelper.isNotEmpty(vmIdsStr) && vmIdsStr != "null") {
			String[] selVmIds = vmIdsStr.replaceFirst(",", "").split(",");
			for (int i = 0; i < selVmIds.length; i++) {
				for (int j = 0; j < vmList.size(); j++) {
					DataRow vm = vmList.get(j);
					if (selVmIds[i].equals(vm.getString("vm_id"))) {
						DataRow devRow = new DataRow();
						devRow.set("ele_id", vm.getString("vm_id"));
						devRow.set("ele_name", vm.getString("name"));
						devList.add(devRow);
					}
				}
			}
		} else {
			//超过3台,默认只取3台虚拟机数据
			for (int i = 0; i < vmList.size(); i++) {
				if (i >= 3) {
					break;
				}
				DataRow vm = vmList.get(i);
				DataRow devRow = new DataRow();
				devRow.set("ele_id", vm.getString("vm_id"));
				devRow.set("ele_name", vm.getString("name"));
				devList.add(devRow);
			}
		}
		
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
//			calendar.add(Calendar.MONTH, -1);
		}
		startTime = SrContant.getTimeFormat(calendar.getTime());
		
		//CPU User
		setAttribute("cpuPerfData", getDrawPerfLineData(devList, "HV1", startTime, endTime));
		//Memory Used
		setAttribute("memPerfData", getDrawPerfLineData(devList, "HV3", startTime, endTime));
		//Network Total Packets
		setAttribute("netPerfData", getDrawPerfLineData(devList, "HV10", startTime, endTime));
		//Disk Total Bytes
		setAttribute("diskPerfData", getDrawPerfLineData(devList, "HV11", startTime, endTime));
		//Virtual Machine CPU User TopN
		setAttribute("cpuTopNData", getDrawPerfTopNData(physicalId, "HV1", startTime, endTime));
		//Virtual Machine Memory Used TopN
		setAttribute("memTopNData", getDrawPerfTopNData(physicalId, "HV3", startTime, endTime));
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
		JSONArray array = baseService.getPrfDatas(1, devList, kpis, startTime, endTime);
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
	public JSONObject getDrawPerfTopNData(String devId, String kpi, String startTime, String endTime) {
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
	 * 加载性能图查询条件数据
	 * @return
	 */
	public ActionResult doHypervisorVmSettingPrf() {
		Integer level = getIntParameter("level", 1);
		Integer physicalId = getIntParameter("physicalId");
		Integer vmId = getIntParameter("vmId");
		List<DataRow> rows = vmService.getVirtualName(vmId, physicalId, null);
		List<DataRow> kpis = baseService.getView(SrContant.DEVTYPE_VAL_HOST, WebConstants.DEVTYPE_HYPERVISOR);
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, WebConstants.DEVTYPE_HYPERVISOR, SrContant.DEVTYPE_VAL_HOST, physicalId, vmId, getLoginUserId());
		if (dataRow2 == null && vmId != 0) {
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", vmId);
		}
		setAttribute("historyConfig", dataRow2);
		setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		//获取用户可见的虚拟机
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, physicalId);
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
		setAttribute("devList", storageList);
		setAttribute("physicalId", physicalId);
		setAttribute("kpisList", kpisList);
		return new ActionResult("/WEB-INF/views/virtualPlat/queryVirtualPlat.jsp");
	}
	
	/**
	 * 性能数据展示
	 * @return
	 */
	public ActionResult doVirtualPrfPage() {
		doHypervisorVmPerf();
		setAttribute("physicalId", getIntParameter("physicalId"));
		setAttribute("vmId", getIntParameter("vmId"));
		String tablePage = getStrParameter("tablePage");
		if (tablePage != null && tablePage.length() > 0) {
			return new ActionResult("/WEB-INF/views/virtualPlat/ajaxHypvPrfData.jsp");
		}
		return new ActionResult("/WEB-INF/views/virtualPlat/ajaxHypvPrfChart.jsp");
	}
	
	/**
	 * 加载性能图数据
	 * <%--
			level为1表示跳转到是存储的非详细页面
			level为2表示跳转到是非存储的非详细页面
			level为3表示跳转到是存储或非存储的详细页面
		--%>
	 */
	public void doHypervisorVmPerf(){
		JSONObject json = new JSONObject();
		Integer physicalId = getIntParameter("physicalId");
		Integer vmId = getIntParameter("vmId");
		Integer level = getIntParameter("level",1);
		if (vmId != null && vmId > 0) { level = 3; }
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);		
		DataRow thead = new DataRow();
		DBPage tbody = null;
		DataRow dataRow = baseService.getPrfFieldInfo(null, level,
				WebConstants.DEVTYPE_HYPERVISOR, SrContant.DEVTYPE_VAL_HOST,
				physicalId, vmId, getLoginUserId());
		//给默认性能信息
		int graphType = SrContant.GRAPH_TYPE_LINE;
		int topCount = 5;
		double threvalue = Double.MAX_VALUE;
		String startTime = "", endTime = "";
		if(dataRow == null || dataRow.size() == 0){
			dataRow = baseService.getDefaultRow("t_res_virtualmachine", 
					vmId, SrContant.DEVTYPE_VAL_HOST, 
					WebConstants.DEVTYPE_HYPERVISOR, "vm_id", "name");
			dataRow.set("fprfid", "'v1'");
			dataRow.set("fyaxisname", "%");
		}
		
		String eleIds = "-1";
		String kpi = "V1";
		if (dataRow != null && dataRow.size() > 0) {
			////////////////// HGC'code below
			graphType = dataRow.getInt("graphtype");
			json.put("graphType", graphType);
			topCount = dataRow.getInt("topnvalue");
			startTime = dataRow.getString("fstarttime");
			endTime = dataRow.getString("fendtime");
			threvalue = dataRow.getDouble("fthrevalue");
			if(threvalue <= 0){ threvalue = Double.MAX_VALUE; }
			
			eleIds = dataRow.getString("fdevice");
			topCount = eleIds == null? 5 : eleIds.split(",").length;
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), 
					"vm_id", "name", "t_res_virtualmachine");
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
					thead.set(r.getString("fid"), r.getString("ftitle").concat("(" + r.getString("funits") + ")"));
				}
				thead.set("prf_timestamp", "时间");
				thead.set("ele_name", "设备名");
				json.put("thead", thead);
				json.put("tbody", tbody);
			}
			
			if(tablePage == null || tablePage.length() == 0){
				if(graphType == SrContant.GRAPH_TYPE_LINE){
					json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"), 
							devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"), 
							dataRow.getString("time_type"))));
				}
				else {
					json.put("series", JSON.toJSONString(baseService.getTopnGraph(eleIds, kpi, 
							startTime, endTime, topCount, viewPostfix, 
							SrContant.SUBDEVTYPE_HYPERVISOR, threvalue)));
				}
			}
			json.put("legend", dataRow.getInt("flegend")==1?true:false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("kpiInfo", kpis);
		}
		String isFreshen = getStrParameter("isFreshen");
		if ("1".equals(isFreshen)) {
			writeDataToPage(json.toString());
		} else {
			this.setAttribute("prfData", json);
		}
	}
	
	/**
	 * 性能图设置
	 */
	public void doHypervisorVmPrf() {
		Integer physicalId = getIntParameter("physicalId");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		Integer graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		Integer topnValue = getIntParameter("topnValue", SrContant.TOPN_COUNT);
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de,"multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
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
		Integer level = getIntParameter("level", 2);
		DataRow row = new DataRow();
		row.set("fsubsystemid", physicalId);
		row.set("level", level);
		row.set("fname",WebConstants.DEVTYPE_HYPERVISOR);
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
			baseService.updatePrfField(row, WebConstants.DEVTYPE_HYPERVISOR, SrContant.DEVTYPE_VAL_HOST, devId, physicalId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	/**
	 * 导出性能图数据
	 * @see 将日期转换的方法，最好用"2015-12-12 12:12:12".replace("-", "/");
	 * @see 没有数据时最好返回空的CSV文件，否则浏览器将跳转
	 */
	public void doExportPrefData() {
		try {
			Integer level = getIntParameter("level");
			Integer physicalId = getIntParameter("physicalId");
			Integer vmId = getIntParameter("vmId");
			DataRow dataRow = baseService.getPrfFieldInfo(null,level,
					WebConstants.DEVTYPE_HYPERVISOR,SrContant.DEVTYPE_VAL_HOST,physicalId,vmId,getLoginUserId());
			//给默认性能信息
			if(dataRow == null || dataRow.size() == 0){
				dataRow = baseService.getDefaultRow("t_res_virtualmachine", vmId, SrContant.DEVTYPE_VAL_HOST, WebConstants.DEVTYPE_HYPERVISOR, "vm_id", "name");
			}
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), "vm_id", "name", "t_res_virtualmachine");
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
				CSVHelper.createCSVToPrintWriter(getResponse(), 
						devs.get(0).getString("ele_name").concat("_Perf_Data"), tbody, title, key);
			}
			else {
				CSVHelper.createCSVToPrintWriter(getResponse(), 
						devs.get(0).getString("ele_name").concat("_Perf_Data"), new ArrayList<DataRow>(), 
						new String[]{"暂无数据导出"}, new String[]{});
			}
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("", e);
			e.printStackTrace();
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

}
