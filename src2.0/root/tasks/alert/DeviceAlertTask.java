	package root.tasks.alert;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.NumericHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.AlertRuleService;
import com.huiming.service.alert.DeviceAlertService;
import com.project.web.WebConstants;

public class DeviceAlertTask implements Task{

	public void execute() {
		DeviceAlertService service = new DeviceAlertService();
		List<DataRow> rules = new AlertRuleService().getList();
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date endDate = new Date();
	    Date startDate = new Date();
	    try {
			endDate = format.parse(format.format(new Date()));
			long time = endDate.getTime();
			//time -= (86400000L * 5 / 1440);
			time -= (86400000L *100);
			startDate = new Date(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<DataRow> alertLogs = new ArrayList<DataRow>();
		
		for (DataRow rule : rules) {
			List<DataRow> targets = new AlertRuleService().getTargets(rule.getInt("fid"));
			StringBuffer sql = new StringBuffer();
			StringBuffer where = new StringBuffer(" where 1=1 and PRF_TIMESTAMP > '"+format.format(startDate) +"' and PRF_TIMESTAMP < '"+format.format(endDate) + "'");
			StringBuffer from = new StringBuffer(" from ");
			List<DataRow> elements = new ArrayList<DataRow>();
			String devType = "";
			String db = WebConstants.DB_TPC;
			if(rule.getInt("fisalone") > 0){
				for (int i = 0; i < targets.size(); i++) {
					sql =  new StringBuffer();
					DataRow target = targets.get(i);
					if(target.getString("ffieldid").equalsIgnoreCase("C1") || target.getString("ffieldid").equalsIgnoreCase("C2") || target.getString("ffieldid").equalsIgnoreCase("C3") || target.getString("ffieldid").equalsIgnoreCase("C4")){
						sql.append("select a.* from (select subsystem_id as dev_id,subsystem_id as ele_id,the_display_name as dev_name,the_display_name as ele_name ,cast(the_allocated_capacity*100.0/(the_allocated_capacity+the_available_capacity) as dec(5,2)) as perc from v_res_storage_subsystem) a where 1=1 and ");
						if(target.getDouble("fminvalue") > 0 && target.getDouble("fmaxvalue") > 0){
							if(target.getDouble("fminvalue") > target.getDouble("fmaxvalue")){
								sql.append(" ( a.perc >" + target.getDouble("fminvalue")+" or a.perc<" + target.getDouble("fmaxvalue") + ") ");
							}else{
								sql.append(" ( a.perc>" + target.getDouble("fminvalue")+" and a.perc<" + target.getDouble("fmaxvalue") + ") ");
							}
						}else if(target.getDouble("fminvalue") > 0 ){
							sql.append(" a.perc>" + target.getDouble("fminvalue"));
						}else if(target.getDouble("fmaxvalue") > 0 ){
							sql.append(" a.perc<" + target.getDouble("fmaxvalue"));
						}
						sql.append( " and a.DEV_ID in (" + rule.getString("fdeviceid")+")");
						db = WebConstants.DB_TPC;
					}else{
						where = new StringBuffer(" where 1=1 and PRF_TIMESTAMP > '"+format.format(startDate) +"' and PRF_TIMESTAMP < '"+format.format(endDate) + "'");
						from = new StringBuffer(" from ");
						
						sql.append("select a.DEV_ID, a.ELE_ID,a.ELE_NAME ,a.PRF_TIMESTAMP");
						from.append(target.getString("FPrfView"));
						sql.append(" ," + target.getString("ffieldid") );
						where.append(" and ");
						
						if(target.getDouble("fminvalue") > 0 && target.getDouble("fmaxvalue") > 0){
							if(target.getDouble("fminvalue") > target.getDouble("fmaxvalue")){
								where.append(" ( " + target.getString("ffieldid") + ">" + target.getDouble("fminvalue")+" or " + target.getString("ffieldid") + "<" + target.getDouble("fmaxvalue") + ") ");
							}else{
								where.append(" ( " + target.getString("ffieldid") + ">" + target.getDouble("fminvalue")+" and " + target.getString("ffieldid") + "<" + target.getDouble("fmaxvalue") + ") ");
							}
						}else if(target.getDouble("fminvalue") > 0 ){
							where.append(target.getString("fid") + ">" + target.getDouble("fminvalue"));
						}else if(target.getDouble("fmaxvalue") > 0 ){
							where.append(target.getString("fid") + "<" + target.getDouble("fmaxvalue"));
						}
						
						if(rule.getString("ftype").equalsIgnoreCase("SWITCH")){
							sql.append(",b.THE_DISPLAY_NAME as DEV_NAME ");
							db = WebConstants.DB_TPC;
							from.append(" a inner join V_RES_SWITCH b on a.DEV_ID = b.SWITCH_ID ");
						}else if(rule.getString("ftype").equalsIgnoreCase("Physical") ){
							db = WebConstants.DB_DEFAULT;
							sql.append(",b.NAME as DEV_NAME ");
							from.append(" a inner join t_res_hypervisor b on a.ele_id = b.hypervisor_id ");
							where.append( " and b.hypervisor_id in (" + rule.getString("fdeviceid") + ") ");
						}else if(rule.getString("ftype").equalsIgnoreCase("virtual")){
							db = WebConstants.DB_DEFAULT;
							sql.append(",b.NAME as DEV_NAME ");
							from.append(" a inner join t_res_virtualmachine b on a.ele_id = b.vm_id ");
							where.append( " and b.vm_id in (" + rule.getString("fdeviceid") + ") ");
						}else{
							sql.append(",b.THE_DISPLAY_NAME as DEV_NAME ");
							db = WebConstants.DB_TPC;
							from.append(" a inner join V_RES_STORAGE_SUBSYSTEM b on a.DEV_ID = b.SUBSYSTEM_ID ");
						} 
						where.append( " and a.DEV_ID in (" + rule.getString("fdeviceid")+")");
						sql.append(from.toString()+where.toString());
					}
					
					List<DataRow> items = service.checkRule(db,sql.toString());
					if (!items.isEmpty()) {
						elements.add(target);
						addAlertItems(rule,items,alertLogs,elements);
						elements.clear();
					}
				}
			}else{
				for (int i = 0; i < targets.size(); i++) {
					DataRow target = targets.get(i);
					if(! (target.getString("fdevtype").equals(devType))){
						if(i>0){
							if(rule.getString("ftype").equalsIgnoreCase("SWITCH")){
								db = WebConstants.DB_TPC;
								sql.append(",b.THE_DISPLAY_NAME as DEV_NAME ");
								from.append(" a inner join V_RES_SWITCH b on a.DEV_ID = b.SWITCH_ID ");
								where.append( " and DEV_ID in (" + rule.getString("fdeviceid") + ") ");
							}else if(rule.getString("ftype").equalsIgnoreCase("Physical") ){
								db = WebConstants.DB_DEFAULT;
								sql.append(",b.NAME as DEV_NAME ");
								from.append(" a inner join t_res_hypervisor b on a.ele_id = b.hypervisor_id ");
								where.append( " and b.hypervisor_id in (" + rule.getString("fdeviceid") + ") ");
							}else if(rule.getString("ftype").equalsIgnoreCase("virtual")){
								db = WebConstants.DB_DEFAULT;
								sql.append(",b.NAME as DEV_NAME ");
								from.append(" a inner join t_res_virtualmachine b on a.ele_id = b.vm_id ");
								where.append( " and b.vm_id in (" + rule.getString("fdeviceid") + ") ");
							}else{
								sql.append(",b.THE_DISPLAY_NAME as DEV_NAME ");
								db = WebConstants.DB_TPC;
								from.append(" a inner join V_RES_STORAGE_SUBSYSTEM b on a.DEV_ID = b.SUBSYSTEM_ID ");
								where.append( " and DEV_ID in (" + rule.getString("fdeviceid") + ") ");
							}
							sql.append(from.toString()+where.toString());
							List<DataRow> items = service.checkRule(db,sql.toString());
							if (!items.isEmpty()) {
								addAlertItems(rule,items,alertLogs,elements);
							}
							sql = new StringBuffer();
							where = new StringBuffer(" where 1=1 and PRF_TIMESTAMP > '"+format.format(startDate) +"' and PRF_TIMESTAMP < '"+format.format(endDate) + "'");
							from = new StringBuffer(" from ");
							elements.clear();
						}
						sql.append("select DEV_ID, ELE_ID,ELE_NAME ,PRF_TIMESTAMP");
						from.append(target.getString("fprfview"));
					}
					sql.append(" ," + target.getString("ffieldid") );
					where.append(" and ");
					if(target.getDouble("fminvalue") > 0 && target.getDouble("fmaxvalue") > 0){
						if(target.getDouble("fminvalue") > target.getDouble("fmaxvalue")){
							where.append(" ( " + target.getString("ffieldid") + ">" + target.getDouble("fminvalue")+" or " + target.getString("ffieldid") + "<" + target.getDouble("fmaxvalue") + ") ");
						}else{
							where.append(" ( " + target.getString("ffieldid") + ">" + target.getDouble("fminvalue")+" and " + target.getString("ffieldid") + "<" + target.getDouble("fmaxvalue") + ") ");
						}
					}else if(target.getDouble("fminvalue") > 0 ){
						where.append(target.getString("ffieldid") + ">" + target.getDouble("fminvalue"));
					}else if(target.getDouble("fmaxvalue") > 0 ){
						where.append(target.getString("ffieldid") + "<" + target.getDouble("fmaxvalue"));
					}
					
					devType = target.getString("fdevtype");
					elements.add(target);
					if(i==(targets.size()-1)){
						if(rule.getString("ftype").equalsIgnoreCase("SWITCH")){
							sql.append(",b.THE_DISPLAY_NAME as DEV_NAME ");
							db = WebConstants.DB_TPC;
							from.append(" a inner join V_RES_SWITCH b on a.DEV_ID = b.SWITCH_ID ");
							where.append( " and DEV_ID in (" + rule.getString("fdeviceid")+") ");
						}else if(rule.getString("ftype").equalsIgnoreCase("Physical") ){
							db = WebConstants.DB_DEFAULT;
							sql.append(",b.NAME as DEV_NAME ");
							from.append(" a inner join t_res_hypervisor b on a.ele_id = b.hypervisor_id ");
							where.append( " and b.hypervisor_id in (" + rule.getString("fdeviceid") + ") ");
						}else if(rule.getString("ftype").equalsIgnoreCase("virtual")){
							db = WebConstants.DB_DEFAULT;
							sql.append(",b.NAME as DEV_NAME ");
							from.append(" a inner join t_res_virtualmachine b on a.ele_id = b.vm_id ");
							where.append( " and b.vm_id in (" + rule.getString("fdeviceid") + ") ");
						}else{
							db = WebConstants.DB_TPC;
							sql.append(",b.THE_DISPLAY_NAME as DEV_NAME ");
							from.append(" a inner join V_RES_STORAGE_SUBSYSTEM b on a.DEV_ID = b.SUBSYSTEM_ID ");
							where.append( " and DEV_ID in (" + rule.getString("fdeviceid") +") ");
						}
						sql.append(from.toString()+where.toString());
						List<DataRow> items = service.checkRule(db,sql.toString());
						if (!items.isEmpty()) {
							addAlertItems(rule,items,alertLogs,elements);
						}
					}
				
				}
			}
		}
	}
	
	private void addAlertItems(DataRow rule, List<DataRow> items,List<DataRow> alertLogs,
			 List<DataRow> targets)
	{
		for (DataRow item: items) {
			DataRow deviceLog = new DataRow();
			StringBuffer message = new StringBuffer();
			StringBuffer descript = new StringBuffer("");
			if(rule.getString("ftype").equalsIgnoreCase("switch")){
				if(!targets.get(0).getString("fdevtype").equalsIgnoreCase("switch")){
					message.append("Switch :'"+item.getString("dev_name")+"'; <br/>");
					deviceLog.set("ftoptype", "Switch");
				}
			}else if(rule.getString("ftype").equalsIgnoreCase("virtual")){
				message.append("Virtual :'"+item.getString("dev_name")+"'; <br/> ");
				deviceLog.set("ftoptype", "Virtual");
			}else if(rule.getString("ftype").equalsIgnoreCase("app")){
				message.append("App :'"+item.getString("dev_name")+"'; <br/> ");
				deviceLog.set("ftoptype", "App");
			}else if(rule.getString("ftype").equalsIgnoreCase("physical")){
				message.append("Physical :'"+item.getString("dev_name")+"'; <br/> ");
				deviceLog.set("ftoptype", "Physical");
			}else{
				if(!targets.get(0).getString("fdevtype").equalsIgnoreCase("storage")){
					message.append("Storage :'"+item.getString("dev_name")+"'; <br/> ");
					deviceLog.set("ftoptype", "Storage");
				}
			}
			message.append(targets.get(0).getString("fdevtype")+" :'"+item.getString("ele_name")+"'; <br/> ");
			descript.append(deviceLog.getString("ftoptype") +" " +targets.get(0).getString("fdevtype")+" ");
			for (DataRow target : targets) {
				if(target != null){
					String des = "";
					descript.append(target.getString("ftitle")+", ");
					if(target.getDouble("fminvalue") > 0 && target.getDouble("fmaxvalue") > 0){
						if(target.getDouble("fminvalue") > target.getDouble("fmaxvalue")){
							des =  ">" + NumericHelper.round(target.getDouble("fminvalue"), 2)+" or <" + NumericHelper.round(target.getDouble("fmaxvalue"), 2) + ") ";
						}else{
							des =  ">" + NumericHelper.round(target.getDouble("fminvalue"), 2)+" and <" +  NumericHelper.round(target.getDouble("fmaxvalue"), 2)+ ") ";
						}
					}else if(target.getDouble("fminvalue") > 0 ){
						des =  ">" +  NumericHelper.round(target.getDouble("fminvalue"), 2);
					}else if(target.getDouble("fmaxvalue") > 0 ){
						des = "<" + NumericHelper.round(target.getDouble("fmaxvalue"), 2) ;
					}
					message.append("Threshold Field:"+target.getString("ftitle")+";<br/> Current Value:"+NumericHelper.round(item.getDouble(target.getString("ffieldid")), 2)+target.getString("funits")+"; <br/>Condition:"+des+";<br/>");
				}
			}
			descript.replace(descript.length()-2, descript.length()-1, "").append("Threshold Exceeded;");
			if(targets.get(0).getString("fdevtype").equalsIgnoreCase("port")&&targets.get(0).getString("fstoragetype").equalsIgnoreCase("switch")){
				deviceLog.set("fresourcetype", "SwitchPort");
			}else{
				deviceLog.set("fresourcetype", targets.get(0).getString("fdevtype"));
			}
			deviceLog.set("fname",rule.getString("fname"));
			deviceLog.set("fresourceid", item.getString("ele_id"));
			deviceLog.set("ftopid", item.getString("dev_id"));
			deviceLog.set("fresourcename", item.getString("ele_name"));
			deviceLog.set("fstate", 0);
			deviceLog.set("fdescript", descript.toString());
			deviceLog.set("flogtype", 2);
			deviceLog.set("fcount", 1);
			deviceLog.set("fisforward", 0);
			deviceLog.set("fruleid", rule.getInt("fid"));
			if(StringHelper.isNotEmpty(item.getString("prf_timestamp"))){
				deviceLog.set("ffirsttime", item.getString("prf_timestamp"));
				deviceLog.set("flasttime", item.getString("prf_timestamp"));
			}else{
				deviceLog.set("ffirsttime", DateHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				deviceLog.set("flasttime", DateHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			}
			
			deviceLog.set("flevel", rule.getInt("flevel"));
			deviceLog.set("fdetail", message.toString());
			alertLogs.add(deviceLog);
		}
		saveLog(alertLogs);
	}
	
	public void saveLog(List<DataRow> logs){
		DeviceAlertService service = new DeviceAlertService();
		for (DataRow log : logs) {
			//save deviceLog
			service.insertLog(log);
		}
	}
}
