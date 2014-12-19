package root.switchs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.switchport.SwitchportService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.zone.ZoneService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class SwitchAction extends SecurityAction{
	SwitchService service = new SwitchService();
	BaseprfService baseService = new BaseprfService();
	public ActionResult doSwitchPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		page = service.getSwitchPage(curPage, numPerPage, null, null, null, null);
		this.setAttribute("switchPage", page);
		
		//性能曲线
		//doSwitchPrfField();
		return new ActionResult("/WEB-INF/views/switch/switchList.jsp");
	}
	
	public ActionResult doAjaxSwitchPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String status = getStrParameter("status");
		String serialNumber = getStrParameter("serialNumber");
		page = service.getSwitchPage(curPage, numPerPage, name, ipAddress, status, serialNumber);
		this.setAttribute("switchPage", page);
		this.setAttribute("name", name);
		this.setAttribute("ipAddress", ipAddress);
		this.setAttribute("status", status);
		this.setAttribute("serialNumber", serialNumber);
		return new ActionResult("/WEB-INF/views/switch/ajaxSwitch.jsp");
	}
	
	public ActionResult doSwitchInfo(){
		SwitchportService portServcie = new SwitchportService();
		ZoneService zoneService = new ZoneService();
		Integer switchId = getIntParameter("switchId");
		DataRow row = service.getSwitchInfo(switchId);
		DBPage portPage = portServcie.getPortPage(1, WebConstants.NumPerPage, null, null, null, null, null, switchId);
		this.setAttribute("portPage", portPage);
		this.setAttribute("portCount", portPage.getTotalRows());
		this.setAttribute("zonePage", zoneService.getZonePage(1, WebConstants.NumPerPage, null, null, null, null,null));
		this.setAttribute("switchInfo", row);
		this.setAttribute("switchId", switchId);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1, switchId.toString(), null,null,null, "Switch", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		//doSwitchPrfField();
		return new ActionResult("/WEB-INF/views/switch/switchInfo.jsp");
	}
	
	public ActionResult doSwitchPrfPage() {
		doSwitchPrfField();
		this.setAttribute("switchId", getIntParameter("switchId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/switch/ajaxPrfSwitch.jsp");
		}
		return new ActionResult("/WEB-INF/views/switch/prefSwitchPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doSwitchSettingPrf(){
		Integer switchId = getIntParameter("switchId");
		Integer level = getIntParameter("level");
		List<DataRow> kpis = baseService.getView("SWITCH", "Switch");
		List<DataRow> devs = service.getdevInfo();
		DataRow row = baseService.getPrfFieldInfo(null, level, "SWITCH", "Switch", switchId, null);
		if(row==null && switchId!=0){
			row = new DataRow();
			row.set("fdevice", switchId);
		}
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("level", level);
		this.setAttribute("switchId", switchId);
		this.setAttribute("historyConfig",row);
		return new ActionResult("/WEB-INF/views/switch/editSwitch.jsp");
	}
	
	public void doSwitchPrfField(){
		Integer switchId = getIntParameter("switchId");
		Integer level = getIntParameter("level",1);
		if(switchId!=null && switchId>0){
			level = 3;
		}
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DataRow thead = new DataRow();
		DBPage tbody = null;
		JSONObject json = new JSONObject();
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, "SWITCH", "Switch", switchId, null);
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_switch", switchId, "SWITCH", "Switch", "switch_id", "the_display_name");
			dataRow.set("fprfid", "'A515','A518'");
			dataRow.set("fyaxisname", "Packets Per Second,MB/Sec");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "switch_id", "the_display_name", "v_res_switch");
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			if(level!=null && level==3){
				thead.set("prf_timestamp", "时间");
				thead.set("ele_name", "设备名");
				for (DataRow r : kpis) {
					thead.set(r.getString("fid"), r.getString("ftitle"));
				}
				tbody = baseService.getPrfDatas(curPage,numPerPage,devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
			}
			if(tablePage==null || tablePage.length()==0){
				JSONArray array = baseService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
				json.put("series", array);
			}
			json.put("thead", thead);
			json.put("tbody", tbody);
			json.put("legend", dataRow.getInt("flegend")==1?true:false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("kpiInfo", kpis);
		}
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}else{
			this.setAttribute("prfData", json);
		}
	}
	
	public void doSwitchPrf(){
		Integer subsystemId = getIntParameter("switchId");
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
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname","Switch");
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
		try {
			baseService.updatePrfField(row, "Switch", "SWITCH", null, subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doExportSwitchConfigData(){
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String status = getStrParameter("status");
		String serialNumber = getStrParameter("serialNumber");
		List<DataRow> rows = service.getSwitchList(name, ipAddress, status, serialNumber);
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","状态","域ID","IP地址","WWN","序列号","描述","更新时间"};
			String[] keys = new String[]{"the_display_name","the_propagated_status","domain","ip_address","switch_wwn","serial_number","description","update_timestamp"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "SWITCH-CONFIG-DATA", rows, title, keys);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData() {
		Integer level = getIntParameter("level");
		Integer subsystemId = getIntParameter("switchId");
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, "Switch",null, subsystemId, null);
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "switch_id", "the_display_name","v_res_switch");
		List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
		DataRow thead = new DataRow();
		thead.set("prf_timestamp", "时间");
		thead.set("ele_name", "设备名");
		for (DataRow r : kpis) {
			thead.set(r.getString("fid"), r.getString("ftitle"));
		}
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis,dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
		if (tbody != null && tbody.size() > 0) {
			String[] title = (String[]) thead.values().toArray(new String[thead.size()]);
			String[] key = new String[thead.keySet().size()];
			Iterator<Object> it = thead.keySet().iterator();
			for (int i = 0; i < thead.keySet().size(); i++) {
				key[i] = it.next().toString().toLowerCase();
			}
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name"), tbody, title, key);
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
