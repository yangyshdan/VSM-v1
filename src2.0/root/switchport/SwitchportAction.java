package root.switchport;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.switchport.SwitchportService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class SwitchportAction extends SecurityAction{
	SwitchportService service = new SwitchportService();
	BaseprfService baseService = new BaseprfService();
	@SuppressWarnings("static-access")
	public ActionResult doPortPage(){
		DBPage page = null ;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		Integer switchId = getIntParameter("switchId");
		page = service.getPortPage(curPage, numPerPage, null, null, null, null, null, switchId);
		this.setAttribute("portPage", page);
		
		List<DataRow> rows = service.getPortSpeed(switchId);
		Map<Object,Object> map = new HashMap<Object,Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add(dataRow.getString("the_display_name"));
			dataJson.put("portId", dataRow.getInt("port_id"));
			dataJson.put("switchId", dataRow.getInt("switch_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("the_port_speed"))));
			jarray.add(dataJson);
		}
		map.put("name", "端口速率");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);  
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("switchId", switchId);
		//性能曲线
		//doPortPrfField();
		return new ActionResult("/WEB-INF/views/switchport/portList.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doPortSettingPrf2(){
		Integer devId = getIntParameter("portId");
		Integer switchId = getIntParameter("switchId");
		DataRow row = new DataRow();
		row.set("type", "SWITCH");
		List<DataRow> kpis = baseService.getView("SWITCH", "Port");
		List<DataRow> devs = baseService.getdevInfo(switchId, "v_res_switch_port", "the_display_name", "port_id","switch_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("switchId", switchId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Port", "SWITCH",switchId,devId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Port", row.getString("type"),switchId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/switchport/SwitchportAction?func=PortPrf");
		return new ActionResult("/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp");
	}
	
	public ActionResult doAjaxPortPage(){
		DBPage page = null ;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		Integer switchId = getIntParameter("switchId");
		String portName = getStrParameter("portName").replaceAll("&amp;nbsp;", " ");;
		String portType = getStrParameter("portType");
		Integer startPort = getIntParameter("startPort");
		Integer endPort = getIntParameter("endPort");
		String status = getStrParameter("status");
		page = service.getPortPage(curPage, numPerPage, portName, portType, startPort, endPort, status, switchId);
		this.setAttribute("portPage", page);
		this.setAttribute("switchId", switchId);
		this.setAttribute("portName",portName);
		this.setAttribute("portType",portType);
		this.setAttribute("startPort",portType);
		this.setAttribute("endPort",endPort);
		this.setAttribute("status",status);
		return new ActionResult("/WEB-INF/views/switchport/ajaxPort.jsp");
	}
	
	public ActionResult doPortInfo(){
		Integer devId = getIntParameter("portId");
		Integer switchId = getIntParameter("switchId");
		DataRow row = service.getExtentportInfo(devId);
		this.setAttribute("switchId", switchId);
		this.setAttribute("portInfo", row);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1, switchId.toString(), null, devId.toString(),null,"Switch", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		//doPortPrfField();
		return new ActionResult("/WEB-INF/views/switchport/portInfo.jsp");
	}
	
	public ActionResult doPortPrfPage(){
		//性能曲线
		doPortPrfField();
		this.setAttribute("portId", getIntParameter("portId"));
		this.setAttribute("switchId", getIntParameter("switchId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/switchport/ajaxPrfPort.jsp");
		}
		return new ActionResult("/WEB-INF/views/switchport/prefPortPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doPortSettingPrf(){
		Integer devId = getIntParameter("portId");
		Integer switchId = getIntParameter("switchId");
		DataRow row = new DataRow();
		row.set("type", "SWITCH");
		List<DataRow> kpis = baseService.getView("SWITCH", "Port");
		List<DataRow> devs = baseService.getdevInfo(switchId, "v_res_switch_port", "the_display_name", "port_id","switch_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("switchId", switchId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Port", "SWITCH",switchId,devId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Port", row.getString("type"),switchId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/switchport/SwitchportAction?func=PortPrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	public void doPortPrfField(){
		Integer devId = getIntParameter("portId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer switchId = getIntParameter("switchId");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String tablePage = getStrParameter("tablePage");
		JSONObject json = new JSONObject();
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Port", "SWITCH",switchId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("V_RES_SWITCH_PORT", devId, "SWITCH", "Port", "PORT_ID", "the_display_name");
			dataRow.set("fprfid", "'A491','A494'");
			dataRow.set("fyaxisname", "Packets Per Second,MB/Sec");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "port_id", "the_display_name", "v_res_switch_port");
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			thead.set("prf_timestamp", "时间");
			thead.set("ele_name", "设备名");
			if(devs!=null && devs.size()==1){
				for (DataRow r : kpis) {
					thead.set(r.getString("fid"), r.getString("ftitle")+"("+r.getString("funits")+")");
				}
				tbody = baseService.getPrfDatas(curPage,numPerPage,devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
			}
			if(tablePage ==null || tablePage.length()==0){
//				JSONArray array = baseService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
//				json.put("series", array);
				json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"))));
				
			}
			json.put("legend", dataRow.getInt("flegend")==1?true:false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("thead", thead);
			json.put("tbody", tbody);
			json.put("kpiInfo", kpis);
		}
		this.setAttribute("prfData", json);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
	}
	
	public void doPortPrf(){
		Integer switchId = getIntParameter("switchId");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de,"multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
		StringBuffer kpi = new StringBuffer();
		for (int i = 0;i<kpis.length;i++) {
			kpi.append("'"+kpis[i]+"'");
			if(i<kpis.length-1){
				kpi.append(",");
			}
		}
		String dev = "";
		if(devices!=null && devices.length>0){
			StringBuffer device = new StringBuffer();
			for (int i = 0;i<devices.length;i++) {
				device.append(devices[i]);
				if(i<devices.length-1){
					device.append(",");
				}
			}
			dev = device.toString();
		}else{
			dev = devId.toString();
		}
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		Integer legend = getIntParameter("legend");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", switchId);
		row.set("level", level);
		row.set("fname","Port");
        row.set("fdevicetype",storageType);
        row.set("fdevice",dev);
        row.set("fprfid",kpi.toString());
        row.set("fisshow",1);
        List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString()); 
        if(units !=  null && units.size()>0){
        	Set<String> set = new HashSet<String>();
        	for (DataRow unit : units) {
        		if(StringHelper.isNotEmpty(unit.getString("funits")))
        			set.add(unit.getString("funits"));
			}
        	String tempStr = set.toString().replace("[", "").replace("]", "");
        	row.set("fyaxisname", tempStr.length()>40?tempStr.substring(0, 37)+"...":tempStr);
        }else{
        	row.set("fyaxisname", "");
        }
        row.set("flegend",legend);
        row.set("fstarttime",startTime);
        row.set("fendtime",endTime);
        row.set("time_type",timeType);
        row.set("fthreshold",threshold);
        row.set("fthreValue",threValue);
        row.set("fuserid", getLoginUserId());
		try {
			baseService.updatePrfField(row,"Port",storageType,devId,switchId,level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doExportPortConfigData(){
		Integer switchId = getIntParameter("switchId");
		String portName = getStrParameter("portName").replaceAll("&amp;nbsp;", " ");;
		String portType = getStrParameter("portType");
		Integer startPort = getIntParameter("startPort");
		Integer endPort = getIntParameter("endPort");
		String status = getStrParameter("status");
		List<DataRow> rows = service.getExtentportList(portName, portType, startPort, endPort, status, switchId);
		String subName = rows.get(0).getString("switch_name");
		
		getResponse().setCharacterEncoding("gbk");
		if(rows != null && rows.size() > 0){
			String[] title = new String[]{"名称","端口号","端口类型","操作类型","硬件状态","端口速率(M)", "交换机"};
			String[] keys = new String[]{"the_display_name","port_number","the_type","the_operational_status","the_consolidated_status","the_port_speed","switch_name"};
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-Ports", rows, title, keys);
		}
		else {
			CSVHelper.createCSVToPrintWriter(this.getResponse(), 
					(portName != null? portName : "Unknown").concat("_Perf_Data"),
					new ArrayList<DataRow>(), new String[] { "暂无数据导出" }, new String[] {});
		}
	}
	
	public void doExportPrefData(){
		Integer devId = getIntParameter("portId");
		Integer level = getIntParameter("level", devId == 0? 2 : 3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Port", "SWITCH",subsystemId,devId,getLoginUserId());
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "port_id", "the_display_name", "v_res_switch_port");
		List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
		DataRow thead = new DataRow();
		thead.set("prf_timestamp", "时间");
		thead.set("ele_name", "设备名");
		for (DataRow r : kpis) {
			thead.set(r.getString("fid"), r.getString("ftitle"));
		}
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
//		DecimalFormat df = new DecimalFormat("#.##");
		baseService.createAndSendCSVFile(getResponse(), kpis, tbody, devs);
		/*if(tbody!=null && tbody.size()>0){
			String[] title = (String[]) thead.values().toArray(new String[thead.size()]);
			String[] key = new String[thead.keySet().size()];
			Iterator<Object> it = thead.keySet().iterator();
			for (int i=0;i<thead.keySet().size();i++) {
				key[i] = it.next().toString().toLowerCase();
			}
			for (int i = 0; i < tbody.size(); i++) {
				tbody.get(i).set("a490", df.format(tbody.get(i).getDouble("a490")));
			}
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name"), tbody, title, key);
		}*/
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
