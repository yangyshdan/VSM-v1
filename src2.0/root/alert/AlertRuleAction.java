package root.alert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.ResponseHelper;
import com.huiming.service.agent.AgentService;
import com.huiming.service.alert.AlertRuleService;
import com.huiming.service.chart.StorageService;
import com.huiming.service.widget.WidgetService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class AlertRuleAction extends SecurityAction {
	/**
	 * 列表页面
	 */
	public ActionResult doDefault(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		AlertRuleService service = new AlertRuleService();
		Long userId = null;
		if (!getLoginUserType().equals(SrContant.ROLE_SUPER)) {
			userId = getLoginUserId();
		}
		DBPage dbPage = service.getPage(curPage, numPerPage, null, -1, userId);
		setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/alert/alertRule.jsp");
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public ActionResult doAjaxPage(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String name = getStrParameter("name");
		int enabled = getIntParameter("enabled",-1);
		AlertRuleService service = new AlertRuleService();
		Long userId = null;
		if (!getLoginUserType().equals(SrContant.ROLE_SUPER)) {
			userId = getLoginUserId();
		}
		DBPage dbPage = service.getPage(curPage, numPerPage, name, enabled, userId);
		setAttribute("dbPage", dbPage);
		setAttribute("name", name);
		setAttribute("enabled", enabled);
		return new ActionResult("/WEB-INF/views/alert/ajaxRule.jsp");
	}
	
	/**
	 * 增加或编辑阀值告警设置
	 * @return
	 */
	public ActionResult doPreEdit(){
		int id = getIntParameter("id",-1);
		JSONArray hvArray = new JSONArray();
		hvArray.add(-1);
		DataRow rule = new DataRow();
		if(id != -1){
			AlertRuleService service = new AlertRuleService();
			rule = service.getInfo(id);
			List<DataRow> fields = service.getField(id);
			JSONArray fieldArray = new JSONArray();
			if(fields != null && fields.size() > 0){
				for (DataRow field : fields) {
					JSONObject json = new JSONObject();
					json.put("fieldId", field.getString("ffieldid"));
					json.put("warnValue", field.getString("fwarnminvalue"));
					json.put("errorValue", field.getString("ferrorminvalue"));
					json.put("avemaxvalue", field.getString("favemaxvalue"));
					json.put("aveminvalue", field.getString("faveminvalue"));
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
		setAttribute("rule", rule);
		setAttribute("selectHV", hvArray);
		setAttribute("prfJson", getEditJson());
		return new ActionResult("/WEB-INF/views/alert/editRule.jsp");
	}
	
	/**
	 * 保存阀值规则设置
	 */
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
		rule.set("fenabled", getIntParameter("enabled"));
		rule.set("fisalone", getIntParameter("isalone"));
		JSONArray targets =JSONArray.fromString(aa);
		
		AlertRuleService service = new AlertRuleService();
		int flag = 0;
		if (fid != -1) {
			rule.set("fid", fid);
			flag = service.updateRule(rule, targets);
		} else {
			rule.set("fuserid", getLoginUserId());
			flag = service.addRule(rule, targets);
		}
		
		if (flag == 0) {
			ResponseHelper.print(getResponse(), "true");
		} else {
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
	
	/**获取可以设置阀值告警规则的设备
	 * @return
	 */
	private JSONObject getEditJson(){
		JSONArray svcStorages = new JSONArray();
		JSONArray bspStorages = new JSONArray();
		JSONArray dsStorages = new JSONArray();
		JSONArray nasStorages = new JSONArray();
		JSONArray emcStorages = new JSONArray();
		JSONArray hdsStorages = new JSONArray();
		JSONArray netAppStorages = new JSONArray();
		JSONArray physicals = new JSONArray();
		JSONArray virtuals = new JSONArray();
		JSONArray switchs = new JSONArray();
		//用户可见设备
		String phyLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String vmLimitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String srStoLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		String tpcStoLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		StorageService service = new StorageService();
		//BSP,SVC,DS,NAS
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			for (DataRow storage : service.getAllTpcStorage(tpcStoLimitIds)) {
				JSONObject item = new JSONObject();
				item.put("value",storage.getInt("subsystem_id"));
				item.put("text",storage.getString("the_display_name"));
				String os_type = storage.getString("os_type");
				if (os_type.isEmpty()) {
					os_type = "25";
				}
				if (os_type.equals("15") || os_type.equals("37")) {
					os_type = SrContant.DEVTYPE_VAL_BSP;
					bspStorages.add(item);
				} else if (os_type.equals("21") || os_type.equals("38")) {
					os_type = SrContant.DEVTYPE_VAL_SVC;
					svcStorages.add(item);
				} else if (os_type.equals("25")) {
					os_type = SrContant.DEVTYPE_VAL_DS;
					dsStorages.add(item);
				} else if (os_type.equals("10")) {
					os_type = "NAS";
					nasStorages.add(item);
				}
			}
		}
		//EMC,HDS,NETAPP
		for (DataRow storage : service.getAllSrStorage(srStoLimitIds)) {
			JSONObject item = new JSONObject();
			item.put("value",storage.getInt("subsystem_id"));
			item.put("text",storage.getString("display_name"));
			String storageType = storage.getString("storage_type");
			if (storageType.equals(SrContant.DEVTYPE_VAL_EMC)) {
				emcStorages.add(item);
			} else if (storageType.equals(SrContant.DEVTYPE_VAL_HDS)) {
				hdsStorages.add(item);
			} else if (storageType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
				netAppStorages.add(item);
			}
		}
		//PHYSICAL
		WidgetService widgetService = new WidgetService();
		for (DataRow physical : widgetService.getPhysicalList(phyLimitIds)) {
			JSONObject item = new JSONObject();
			item.put("value",physical.getInt("ele_id"));
			item.put("text",physical.getString("ele_name"));
			physicals.add(item);
		}
		//VIRTUAL
		for (DataRow physical : widgetService.getVirtualList(vmLimitIds)) {
			JSONObject item = new JSONObject();
			item.put("value",physical.getInt("ele_id"));
			item.put("text",physical.getString("ele_name"));
			virtuals.add(item);
		}
		//SWITCH
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			for (DataRow swit : service.getAllSwitch(switchLimitIds)) {
				JSONObject item = new JSONObject();
				item.put("value",swit.getInt("switch_id"));
				item.put("text",swit.getString("the_display_name"));
				switchs.add(item);
			}
		}
		
		JSONObject targets = new JSONObject();
		String[] devTypes = new String[] { SrContant.DEVTYPE_VAL_DS,
				SrContant.DEVTYPE_VAL_SVC, SrContant.DEVTYPE_VAL_BSP,
				"NAS", SrContant.DEVTYPE_VAL_SWITCH,
				SrContant.DEVTYPE_VAL_EMC, SrContant.DEVTYPE_VAL_HDS,
				WebConstants.STORAGE_TYPE_VAL_NETAPP,
				SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_VIRTUAL};
		for (String devType : devTypes) {
			JSONArray items = new JSONArray();
			for (DataRow field : service.getPrfField(devType)) {
				JSONObject item = new JSONObject();
				item.put("value", field.getString("fid"));
				item.put("text", field.getString("ftitle"));
				items.add(item);
			}
			targets.put(devType.toUpperCase(), items);
		}
		
		JSONObject json = new JSONObject();
		json.put(SrContant.DEVTYPE_VAL_SVC, svcStorages);
		json.put(SrContant.DEVTYPE_VAL_BSP, bspStorages);
		json.put(SrContant.DEVTYPE_VAL_DS, dsStorages);
		json.put("NAS", nasStorages);
		json.put(SrContant.DEVTYPE_VAL_EMC, emcStorages);
		json.put(SrContant.DEVTYPE_VAL_HDS, hdsStorages);
		json.put(WebConstants.STORAGE_TYPE_VAL_NETAPP, netAppStorages);
		json.put(SrContant.SUBDEVTYPE_PHYSICAL.toUpperCase(), physicals);
		json.put(SrContant.SUBDEVTYPE_VIRTUAL.toUpperCase(), virtuals);
		json.put(SrContant.DEVTYPE_VAL_SWITCH, switchs);
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
	
	/**
	 * 增加或编辑告警转发设置
	 * @return
	 */
	public ActionResult doPreForward(){
		AlertRuleService alertruleService = new AlertRuleService();
		DataRow data = alertruleService.getForward();
		JSONObject json = new JSONObject();
		if (data != null) {
			json.put("forward_level", data.getString("fforwordlevel"));
			json.put("snmp_public", data.getString("fsnmppublic"));
			json.put("snmp_host", data.getString("fsnmphost"));
			json.put("snmp_port", data.getString("fsnmpport"));
			json.put("email_smtp", data.getString("femailsmtp"));
			json.put("email_port", data.getString("femailport"));
			json.put("email_user", data.getString("femailuser"));
			json.put("email_pwd", data.getString("femailpwd"));
			json.put("email_to", data.getString("femailto"));
			json.put("sms_to", data.getString("fsmsto"));
			json.put("old_data", data.getString("folddata"));
			json.put("forwareId", data.getInt("fid"));
			List<DataRow> snmps = alertruleService.getForwardField(data.getInt("fid"));
			List<DataRow> smss = alertruleService.getForwardSmsField(data.getInt("fid"));
			JSONArray snmpsArray = new JSONArray();
			JSONArray smssArray = new JSONArray();
			if (snmps != null && snmps.size() > 0) {
				for (DataRow field : snmps) {
					JSONObject json1 = new JSONObject();
					json1.put("snmp_public",field.getString("fsnmppublic") == "" ? "public" : field.getString("fsnmppublic"));
					json1.put("snmp_host", field.getString("fsnmphost"));
					json1.put("snmp_port", field.getString("fsnmpport") == "" ? "162" : field.getString("fsnmpport"));
					snmpsArray.add(json1);
				}
			}
			setAttribute("snmps", snmpsArray);
			if (smss != null && smss.size() > 0) {
				for (DataRow field : smss) {
					JSONObject json1 = new JSONObject();
					json1.put("smsuser",field.getString("fsmsuser"));
					json1.put("smsphone", field.getString("fsmsphone"));
					smssArray.add(json1);
				}
			}
			setAttribute("smss", smssArray);
		} else {
			json.put("forward_level", "");
			json.put("snmp_host", "");
			json.put("email_smtp", "");
			json.put("email_user", "");
			json.put("email_pwd", "");
			json.put("email_to", "");
			json.put("sms_to", "");
			json.put("email_port", 25);
			json.put("old_data", 60);
			json.put("forwareId", "");
			setAttribute("snmps", "");
			setAttribute("smss", "");
		}
		setAttribute("forward", json);
		return new ActionResult("/WEB-INF/views/alert/forward.jsp");
	}
	
	/**
	 * 告警转发设置
	 */
	public void doAjaxAddForward(){
		AlertRuleService alertruleService = new AlertRuleService();
		DataRow data = new DataRow();
		data.set("fforwordlevel", getIntParameter("forward_level", 0));
		String snmpStr = "";
		String smsStr = "";
		try {
//			data.set("fsnmppublic", URLDecoder.decode(getStrParameter("snmp_public","public"),"utf-8"));
//			data.set("fsnmphost", URLDecoder.decode(getStrParameter("snmp_host"),"utf-8"));
			data.set("femailsmtp", URLDecoder.decode(getStrParameter("email_smtp"),"utf-8"));
			data.set("femailuser", URLDecoder.decode(getStrParameter("email_user"),"utf-8"));
			data.set("femailto", URLDecoder.decode(getStrParameter("email_to"),"utf-8"));
//			data.set("fsmsto", URLDecoder.decode(getStrParameter("sms_to"),"utf-8"));
			snmpStr = URLDecoder.decode(getStrParameter("snmps"),"utf-8");
			snmpStr = snmpStr.replace("&amp;acute;", "'"); 
			smsStr = URLDecoder.decode(getStrParameter("smss"),"utf-8");
			smsStr = smsStr.replace("&amp;acute;", "'"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		int fid = getIntParameter("forwareId", -1);
		data.set("femailport", getIntParameter("email_port", 25));
		data.set("femailpwd", getStrParameter("email_pwd"));
		data.set("folddata", getIntParameter("old_data"));
		JSONArray snmps = JSONArray.fromString(snmpStr);
		JSONArray smss = JSONArray.fromString(smsStr);
		int flag = 0;
		if (fid != -1) {
			data.set("fid", fid);
			flag = alertruleService.updateForward(data, snmps, smss);
		} else {
			flag = alertruleService.addForward(data, snmps, smss);
		}
		if (flag == 0) {
			ResponseHelper.print(getResponse(), "true");
		} else {
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
}
