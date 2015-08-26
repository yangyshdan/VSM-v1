package root.tasks.alert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import root.email.JavaMail;
import root.snmp.SmsClientSend;
import root.snmp.SnmpUtilSendTrap;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.AlertRuleService;
import com.huiming.service.alert.DeviceAlertService;

public class ForwardMsgTask implements Task{

	public static final String SUBJECT = "[邮件]TPC监控：[196.1.1.97,SR存储设备资源管理系统]告警信息摘要";
	
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
			String headStr = "";
			if(StringHelper.isNotEmpty(forward.getString("femailsmtp")) && log.getInt("flevel") >= forward.getInt("fforwordlevel")){
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
				StringBuffer msg = new StringBuffer();
				msg.append("资产名称##"+log.getString("ftopname"));
				msg.append("@@事件类别##服务器、存储、SAN等硬件@@紧急度##非紧急@@影响度##影响度一般的事件@@详细信息##");
				msg.append(log.getString("fdetail"));
				msg.append("@@服务级别##@@关闭理由##@@事件来源##SR存储设备资源报表系统@@事件根源##");
//				String msg = "Rule ID:"+log.getString("fruleid")+";<br/>Counter:"+log.getInt("fcount")+";<br/>Description:"+log.getString("fdetail")+";<br/>First Alert Time:"+log.getString("ffirsttime")+";<br/>Last Alert Time:"+log.getString("flasttime")+"";
				String femailto[]=forward.getString("femailto").split(";");
				if(femailto.length>0){
					se.initProps(forward.getString("femailsmtp"), forward.getString("femailport"));
					for(int i=0;i<femailto.length;i++){
						se.doSendHtmlEmail(forward.getString("femailsmtp"), forward.getString("femailuser"), forward.getString("femailpwd"), SUBJECT,msg.toString() , femailto[i]);
					}
				}else{
					se.doSendHtmlEmail(forward.getString("femailsmtp"), forward.getString("femailuser"), forward.getString("femailpwd"), SUBJECT,msg.toString() , forward.getString("femailuser"));
				}	
			}
			//短信
			if(StringHelper.isNotEmpty(forward.getString("fsmsto")) && log.getInt("flevel") >= forward.getInt("fforwordlevel")){
				try {
					String[] phones = forward.getString("fsmsto").split(";");
					if(phones.length > 0 ){
						for (int i = 0; i < phones.length; i++) {
							StringBuffer sms = new StringBuffer("SR event:"+headStr);
							sms.append(log.getString("ftoptype")+":"+log.getString("ftopname")+";"+log.getString("fdetail"));
							SmsClientSend ss = new SmsClientSend();
							
							if(sms.length() > 140){
								ss.SmsSend(phones[i], sms.substring(0, 135)+"...");
							}else{
								ss.SmsSend(phones[i], sms.toString());
							}
							
						}
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	}

}
