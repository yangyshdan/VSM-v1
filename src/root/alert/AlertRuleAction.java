package root.alert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.service.agent.AgentService;
import com.huiming.service.alert.AlertRuleService;
import com.huiming.service.apps.AppsService;
import com.huiming.service.chart.StorageService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class AlertRuleAction extends SecurityAction{
	public ActionResult doDefault(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		AlertRuleService service = new AlertRuleService();
		setAttribute("dbPage", service.getPage(curPage, numPerPage, null, -1,-1));
		return new ActionResult("/WEB-INF/views/alert/alertRule.jsp");
	}
	
	public ActionResult doAjaxPage(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name = getStrParameter("name");
		int level = getIntParameter("level",-1);
		int enabled = getIntParameter("",-1);
		AlertRuleService service = new AlertRuleService();
		setAttribute("dbPage", service.getPage(curPage, numPerPage, name, level,enabled));
		return new ActionResult("/WEB-INF/views/alert/ajaxRule.jsp");
	}
	
	public ActionResult doPreEdit(){
		int id = getIntParameter("id",-1);
		JSONArray hvArray = new JSONArray();
		hvArray.add(-1);
		if(id != -1){
			AlertRuleService service = new AlertRuleService();
			DataRow rule = service.getInfo(id);
			setAttribute("rule", rule);
			List<DataRow> fields = service.getField(id);
			JSONArray fieldArray = new JSONArray();
			if(fields != null && fields.size() > 0){
				for (DataRow field : fields) {
					JSONObject json = new JSONObject();
					json.put("fieldId", field.getString("ffieldid"));
					json.put("minvalue", field.getString("fminvalue"));
					json.put("maxvalue", field.getString("fmaxvalue"));
					fieldArray.add(json);
				}
			}
			if(rule.getString("ftype").equalsIgnoreCase("virtual")){
				for (DataRow  row: service.getHyperVisorIdByVMId(rule.getString("fdeviceid"))) {
					hvArray.add(row.get("id"));
				}
			}
			
			setAttribute("fields", fieldArray);
		}
		setAttribute("selectHV", hvArray);
		this.setAttribute("prfJson", getEditJson());
		return new ActionResult("/WEB-INF/views/alert/editRule.jsp");
	}
	
	public void doAjaxAdd(){
		DataRow rule = new DataRow();
		String aa = "";
		try {
			rule.set("fname", URLDecoder.decode(getStrParameter("name"),"utf-8"));
			aa = URLDecoder.decode(getStrParameter("targets"),"utf-8");
			aa = aa.replace("&amp;acute;", "'"); 
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int fid = getIntParameter("id",-1);
		rule.set("ftype", getStrParameter("type"));
		rule.set("fdeviceid", getStrParameter("device"));
		rule.set("flevel", getIntParameter("level"));
		rule.set("fenabled", getIntParameter("enabled"));
		rule.set("fisalone", getIntParameter("isalone"));
		JSONArray targets =JSONArray.fromString(aa);
		
		AlertRuleService service = new AlertRuleService();
		int flag = 0;
		if(fid != -1){
			rule.set("fid", fid);
			flag = service.updateRule(rule, targets);
		}else{
			flag = service.addRule(rule, targets);
		}
		
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doAjaxEnabled(){
		String ids = getStrParameter("ids");
		int enabled = getIntParameter("enabled");
		AlertRuleService service = new AlertRuleService();
		try {
			service.updateStatus(ids, enabled);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doAjaxDelete(){
		String ids = getStrParameter("ids");
		AlertRuleService service = new AlertRuleService();
		int flag = service.deleteRule(ids);
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	private JSONObject getEditJson(){
		JSONArray svcStorages = new JSONArray();
		JSONArray bspStorages = new JSONArray();
		JSONArray dsStorages = new JSONArray();
		JSONArray switchs = new JSONArray();
		JSONArray hps = new JSONArray();
		JSONArray apps = new JSONArray();
		StorageService service = new StorageService();
		AgentService agentService = new AgentService();
		AppsService appService=new AppsService();
		for (DataRow storage : service.getAllStorage()) {
			JSONObject item = new JSONObject();
			item.put("value",storage.getInt("subsystem_id"));
			item.put("text",storage.getString("the_display_name"));
			String os_type = storage.getString("os_type");
			if(os_type.isEmpty()){
				os_type = "25";
			}
			if(os_type.equals("15") || os_type.equals("37")){
				os_type = "BSP";
				bspStorages.add(item);
			}else if(os_type.equals("21") || os_type.equals("38")){
				os_type = "SVC";
				svcStorages.add(item);
			}else{
				os_type = "DS";
				dsStorages.add(item);
			}
		}
		for (DataRow storage : service.getAllSwitch()) {
			JSONObject item = new JSONObject();
			item.put("value",storage.getInt("switch_id"));
			item.put("text",storage.getString("the_display_name"));
			switchs.add(item);
		}
		
		for (DataRow hp : agentService.getHpNameAndID()) {
			JSONObject item = new JSONObject();
			item.put("value", hp.getInt("hypervisor_id"));
			item.put("text", hp.getString("name"));
			hps.add(item);
		}
		for (DataRow app : appService.getAppList()) {
			JSONObject item = new JSONObject();
			item.put("value", app.getInt("fid"));
			item.put("text", app.getString("fname"));
			apps.add(item);
		}
		
		JSONObject targets = new JSONObject();
		for (String osType : new String[]{"DS","SVC","BSP","SWITCH","Physical","Virtual","App"}) {
			JSONArray items = new JSONArray();
			for (DataRow field : service.getPrfField(osType)) {
				JSONObject item = new JSONObject();
				item.put("value", field.getString("fid"));
				item.put("text", field.getString("ftitle"));
				items.add(item);
			}
			targets.put(osType, items);
		}
		
		JSONObject json = new JSONObject();
		json.put("SVC", svcStorages);
		json.put("BSP", bspStorages);
		json.put("Physical", hps);
		json.put("App", apps);
		json.put("DS", dsStorages);
		json.put("SWITCH", switchs);
		json.put("targets", targets);
		
		return json;
	}
	
	public void doAjaxVirtual(){
		int hyperVisiorId = getIntParameter("hyperVisiorId",-1);
		if(hyperVisiorId > -1){
			JSONArray array = new JSONArray();
			for (DataRow vt : new AgentService().getVirtualNameAndId(hyperVisiorId)) {
				JSONObject temp = new JSONObject();
				temp.put("value", vt.getInt("vm_id"));
				temp.put("text", vt.getString("name"));
				array.add(temp);
			}
			ResponseHelper.print(getResponse(), array);
		}
	}
	
	public static void main(String[] args) {
		String aa = "[{aa:2,bb:'ss',cc:3}]";
		JSONArray bb = JSONArray.fromString(aa);
		System.out.println(bb);
	}
	
	public ActionResult doPreForward(){
		AlertRuleService alertruleService=new AlertRuleService();
		DataRow data = alertruleService.getForward();
		JSONObject json = new JSONObject();
		//json.put("snmp_public", "public");
		//json.put("snmp_port", 162);
		
		if(data != null){
			json.put("forward_level", data.getString("fforwardlevel"));
			json.put("snmp_public", data.getString("fsnmppublic"));
			json.put("snmp_host", data.getString("fsnmphost"));
			json.put("snmp_port", data.getString("fsnmpport"));
			json.put("email_smtp", data.getString("femailsmtp"));
			json.put("email_port", data.getString("femailport"));
			json.put("email_user", data.getString("femailuser"));
			json.put("email_pwd", data.getString("femailpwd"));
			json.put("email_to", data.getString("femailto"));
			json.put("old_data", data.getString("folddata"));
			json.put("forwareId", data.getInt("fid"));
			List<DataRow> fields = alertruleService.getForwardField(data.getInt("fid"));
			JSONArray fieldArray = new JSONArray();
			if(fields != null && fields.size() > 0){
				for (DataRow field : fields) {
					JSONObject json1 = new JSONObject();
					//json1.put("fieldId", field.getString("fid"));
					json1.put("snmp_public", field.getString("fsnmppublic")==""?"public":field.getString("fsnmppublic"));
					json1.put("snmp_host", field.getString("fsnmphost"));
					json1.put("snmp_port", field.getString("fsnmpport")==""?"162":field.getString("fsnmpport"));
					fieldArray.add(json1);
				}
			}
			setAttribute("fields", fieldArray);
		}else{
			json.put("forward_level", "");
			//json.put("snmp_host", "");
			json.put("email_smtp", "");
			json.put("email_user", "");
			json.put("email_pwd", "");
			json.put("email_to", "");
			json.put("email_port", 25);
			json.put("old_data", 60);
			json.put("forwareId", "");
			setAttribute("fields", "");
		}
		setAttribute("forward", json);
		return new ActionResult("/WEB-INF/views/alert/forward.jsp");
	}
	
	public void doAjaxAddForward(){
		AlertRuleService alertruleService= new AlertRuleService();
		DataRow data = new DataRow();
		data.set("fforwordlevel", getIntParameter("forward_level",0));
		String aa = "";
		try {
			//data.set("fsnmppublic", URLDecoder.decode(getStrParameter("snmp_public","public"),"utf-8"));
			//data.set("fsnmphost", URLDecoder.decode(getStrParameter("snmp_host"),"utf-8"));
			data.set("femailsmtp", URLDecoder.decode(getStrParameter("email_smtp"),"utf-8"));
			data.set("femailuser", URLDecoder.decode(getStrParameter("email_user"),"utf-8"));
			data.set("femailto", URLDecoder.decode(getStrParameter("email_to"),"utf-8"));
			aa = URLDecoder.decode(getStrParameter("targets"),"utf-8");
			aa = aa.replace("&amp;acute;", "'"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		int fid = getIntParameter("forwareId",-1);
		data.set("femailport", getIntParameter("email_port",25));
		data.set("femailpwd", getStrParameter("email_pwd"));
		data.set("folddata", getIntParameter("old_data"));
		JSONArray targets =JSONArray.fromString(aa);
		int flag = 0;
		if(fid != -1){
			data.set("fid", fid);
			flag = alertruleService.updateForward(data, targets);
		}else{
			flag = alertruleService.addForward(data,targets);
		}
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
}
