package root.apps;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.agent.AgentService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.apps.AppsService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class AppsAction extends SecurityAction{
	BaseprfService baseService = new BaseprfService();
	private AppsService service = new AppsService();
	public ActionResult doDefault(){
		DBPage dbPage = service.getPage(1, WebConstants.NumPerPage,null);
		setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/apps/appList.jsp");
	}
	
	public ActionResult doAjaxPage(){
		String name = getStrParameter("name");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DBPage dbPage = service.getPage(curPage, numPerPage,name);
		setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/apps/ajaxApp.jsp");
	}
	
	public ActionResult doPrepareEdit(){
		int appId = getIntParameter("appId",0);
		setAttribute("appInfo", service.getAppInfo(appId));
		setAttribute("editJson", getVMJson(appId));
		return new ActionResult("/WEB-INF/views/apps/appEdit.jsp");
	}
	
	public JSONObject getVMJson(int id){
		JSONObject json = new JSONObject();
		JSONArray hps = new JSONArray();
		JSONArray selectHps = new JSONArray();
		JSONArray selectVM = new JSONArray();
		
		for (DataRow hp : new AgentService().getHpNameAndID()) {
			JSONObject item = new JSONObject();
			item.put("value", hp.getInt("hypervisor_id"));
			item.put("text", hp.getString("name"));
			hps.add(item);
		}
		json.put("hypervisors", hps);
		for (DataRow selectHv : service.getMappingHV(id)) {
			selectHps.add(selectHv.getInt("hv_id"));
		}
		selectHps.add(-1);
		json.put("selectHV", selectHps);
		for (DataRow vm : service.getMappingVM(id)) {
			selectVM.add(vm.get("fvirtualid"));
		}
		selectVM.add(-1);
		json.put("selectVM", selectVM);
		return json;
	}
	
	public void doAjaxAdd(){
		int id = getIntParameter("id",0);
		DataRow app = new DataRow();
		app.set("fname", getStrParameter("name"));
		String vm = getStrParameter("device");
		int flag = service.updateMapping(id, app, vm);
		
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doAjaxDelete(){
		int id = getIntParameter("id",0);
		int flag = -1;
		if(id > 0){
			flag = service.deleteApp(id);
		}
		
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public ActionResult doAppsInfo(){
		Integer fappid = getIntParameter("fappid");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DBPage dbPage = service.getAppsVirtual(curPage, numPerPage, fappid);
		this.setAttribute("dbPage", dbPage);
		DataRow row=service.getAppInfo(fappid);
		this.setAttribute("appInfo", row);
		//硬件告警
	//	AlertService alertService = new AlertService();
	//	DBPage logPage = alertService.getPage(1, WebConstants.NumPerPage,fappid, null, null, null, null);
	//	setAttribute("logPage", logPage);
		//阀值告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1, fappid.toString(), null,null,null, "App", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		this.setAttribute("fappId", fappid);
		return new ActionResult("/WEB-INF/views/apps/appInfo.jsp");
	}
	public ActionResult doAjaxVirtual(){
		Integer fappid = getIntParameter("fappId");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DBPage dbPage = service.getAppsVirtual(curPage, numPerPage, fappid);
		this.setAttribute("dbPage", dbPage);
		this.setAttribute("fappId", fappid);
		return new ActionResult("/WEB-INF/views/apps/ajaxVirtual.jsp");
	}
	public ActionResult doAppSettingPrf(){
		Integer level = getIntParameter("level",1);
		Integer fappid = getIntParameter("fappid");
		//List<DataRow> rows = service.getMappingHV(id)
		
		List<DataRow> kpis = baseService.getView("APPLICATION", "App");
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, "App", "APPLICATION", fappid, fappid);
		if(dataRow2==null&&fappid!=0){
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", fappid);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		//for (DataRow row : rows) {
			DataRow row=service.getAppInfo(fappid);
			row.set("name", row.getString("fname"));
			row.set("id", row.getString("fid"));
			storageList.add(row);
		//}
		this.setAttribute("devList", storageList);
		this.setAttribute("fappId", fappid);
		this.setAttribute("kpisList", kpisList);
		return new ActionResult("/WEB-INF/views/apps/editApps.jsp");
	}
	public void doAppsPrf(){
		Integer fappId = getIntParameter("fappId");
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
		row.set("fsubsystemid", fappId);
		row.set("level", level);
		row.set("fname","App");
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
			baseService.updatePrfField(row, "App","APPLICATION" , devId, fappId, level);
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
	public void doAppPrfField(){
		JSONObject json = new JSONObject();
		Integer fappId = getIntParameter("fappId");
		Integer level = getIntParameter("level",1);
		if(fappId!=null && fappId>0){
			level = 3;
		}
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);		
		DataRow thead = new DataRow();
		DBPage tbody = null;
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"App", "APPLICATION",fappId,null);
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("tnapps", fappId, "APPLICATION", "App", "fid", "fname");
			dataRow.set("fprfid", "'APP2','APP3'");
			dataRow.set("fyaxisname", "%");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), "fid", "fname", "tnapps");
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
	public ActionResult doAppsPrfPage() {
		doAppPrfField();
		this.setAttribute("fappId", getIntParameter("fappId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/apps/ajaxPrfApps.jsp");
		}
		return new ActionResult("/WEB-INF/views/apps/prefAppsPage.jsp");
	}
	
}
