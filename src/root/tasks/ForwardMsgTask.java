package root.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import root.email.JavaMail;
import root.snmp.SnmpUtilSendTrap;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.AlertRuleService;
import com.huiming.service.alert.DeviceAlertService;

public class ForwardMsgTask implements Task{

	public void execute() {
		DeviceAlertService service = new DeviceAlertService();
		int maxId = service.getMaxForwardId();
		List<DataRow> logs = service.getForwardLog(maxId);
		if(logs != null && logs.size() > 0){
			forwardLog(logs);
		}
		service.updateForward(maxId);
	}
	
	public void forwardLog(List<DataRow> logs){
		DataRow forward = new AlertRuleService().getForward();
		if(forward != null){
		SnmpUtilSendTrap sust = new  SnmpUtilSendTrap();
		JavaMail se = new JavaMail();
		List<DataRow> snmps = new AlertRuleService().getForwardField(forward.getInt("fid"));
		
		for (DataRow log : logs) {
			//SNMP forward
			if(snmps != null && snmps.size() > 0){
				Map<String, String> msgs = new HashMap<String, String>();
				msgs.put("1.3.6.1.4.1.2.6.194.5.1.1", log.getString("ftopname"));
				msgs.put("1.3.6.1.4.1.2.6.194.5.1.2",  log.getString("ftoptype"));
				msgs.put("1.3.6.1.4.1.2.6.194.5.1.3", log.getString("fdetail").replace("<br/>", "    "));
				switch (log.getInt("flevel")) {
				case 0:
					msgs.put("1.3.6.1.4.1.2.6.194.5.1.4", "Info");
					break;
				case 1:
					msgs.put("1.3.6.1.4.1.2.6.194.5.1.4", "Warning");
					break;
				case 2:
					msgs.put("1.3.6.1.4.1.2.6.194.5.1.4", "Critical");
					break;

				default:
					break;
				}
				msgs.put("1.3.6.1.4.1.2.6.194.5.1.5", log.getString("ffirsttime"));
				msgs.put("1.3.6.1.4.1.2.6.194.5.1.6", log.getString("flasttime"));
				msgs.put("1.3.6.1.4.1.2.6.194.5.1.7", log.getString("fcount"));
				msgs.put("1.3.6.1.4.1.2.6.194.5.1.8", log.getString("fruleid"));
				switch (log.getInt("flevel")) {
				case 0:
					msgs.put("1.3.6.1.4.1.2.6.194.5.1.9", "系统告警");
					break;
				case 1:
					msgs.put("1.3.6.1.4.1.2.6.194.5.1.9", "TPC告警");
					break;
				case 2:
					msgs.put("1.3.6.1.4.1.2.6.194.5.1.9", "阀值告警");
					break;
				case 3:
					msgs.put("1.3.6.1.4.1.2.6.194.5.1.9", "HMC告警");
					break;

				default:
					break;
				}
				for (DataRow snmp : snmps) {
					if(StringHelper.isNotEmpty(snmp.getString("fsnmphost")) && log.getInt("flevel") >= forward.getInt("fforwordlevel")){
						try {
							sust.initComm(snmp.getString("fsnmphost")+"/"+snmp.getString("fsnmpport"));
							sust.sendPDU("1.3.6.1.4.1.2.6.194.5.1",snmp.getString("fsnmppublic"),msgs);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			//Email forward
			if(StringHelper.isNotEmpty(forward.getString("femailsmtp")) && log.getInt("flevel") >= forward.getInt("fforwordlevel")){
				String headStr = "";
				switch (log.getInt("flevel")) {
				case 0:
					headStr = "[Info]";
					break;
				case 1:
					headStr = "[Warning]";
					break;
				case 2:
					headStr = "[Critical]";
					break;

				default:
					break;
				}
				String msg = "Rule ID:"+log.getString("fruleid")+";<br/>Counter:"+log.getInt("fcount")+";<br/>Description:"+log.getString("fdetail")+";<br/>First Alert Time:"+log.getString("ffirsttime")+";<br/>Last Alert Time:"+log.getString("flasttime")+"";
				String femailto[]=forward.getString("femailto").split(";");
				if(femailto.length>0){
					se.initProps(forward.getString("femailsmtp"), forward.getString("femailport"));
					for(int i=0;i<femailto.length;i++){
						se.doSendHtmlEmail(forward.getString("femailsmtp"), forward.getString("femailuser"), forward.getString("femailpwd"), "VSM Virtual System Manager Event"+headStr,msg , femailto[i]);
					}
				}else{
					se.doSendHtmlEmail(forward.getString("femailsmtp"), forward.getString("femailuser"), forward.getString("femailpwd"), "VSM Virtual System Manager Event"+headStr,msg , forward.getString("femailuser"));
				}	
			}
		}
	}
	}

}
