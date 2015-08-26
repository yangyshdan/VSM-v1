package com.project.eventslog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.usercon.UserConService;
import com.project.hmc.core.HmcBase;

/**
 * San Switch 日志采集
 * 首先测试命令是否可行，得到的结果格式是否与预期结果一样，测试命令如下：
 * errdump
 * 若和预期结果一样，则不做修改
 * @author Administrator
 *
 */
public class SwitchEventslog {
	private UserConService userConService = new UserConService();
	private static Logger log = Logger.getLogger(SwitchEventslog.class);
	DeviceAlertService service = new DeviceAlertService();
	SwitchService switchService=new SwitchService();
	
	/**
	 * 目标：重点检查错误日志中是否存在ERROR或者WARNING信息
	 */
	public void getResult() {
		List<DataRow> sList = switchService.getSwitchList(null, null, null, null, null);
		List<DataRow> switchList = userConService.getDeviceList(null, "2");
		if(sList!=null && sList.size()>0){
			for (DataRow switchs : sList) {
				if(switchList!=null && switchList.size()>0){
					for (DataRow data : switchList) {
						if(switchs.getString("switch_id").equals(data.getString("dev_id"))){
							HmcBase hmcBase = new HmcBase(data.getString("ip_address"), 22, data.getString("users"), new DES().decrypt(data.getString("pwd")));
							Connection conn = null;
							Session session = null;
							try {
								conn=hmcBase.getConn();
								session =conn.openSession();
								session.execCommand("errdump");
								session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout= new StreamGobbler(session.getStdout());
								BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
								String line=null;
								while((line=br.readLine())!=null){
									String[] strAry = line.split(",");
									if(strAry.length>1){
										DataRow row = new DataRow();
										String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").parse(strAry[0].trim()));
										row.set("ffirsttime",time);
										row.set("flasttime", time);
										row.set("fruleid", strAry[1].replaceAll("[\\[\\]]", "").trim());
										row.set("fno", strAry[2].trim());
										if(strAry[4].trim().equalsIgnoreCase("info")){
											row.set("flevel", 0);
										}else if(strAry[4].trim().equalsIgnoreCase("warning")){
											row.set("flevel", 1);
										}else if(strAry[4].trim().equalsIgnoreCase("error")){
											row.set("flevel", 2);
										}else if(strAry[4].trim().equalsIgnoreCase("critical")){
											row.set("flevel", 2);
										}
										row.set("ftopid", switchs.getString("switch_id"));
										row.set("ftopname", switchs.getString("the_display_name"));
										row.set("ftoptype", "Switch");
										row.set("fresourceid", switchs.getString("switch_id"));
										row.set("fresourcename", switchs.getString("the_display_name"));
										row.set("fresourcetype", "Switch");
										row.set("flogtype", 0);
										row.set("fstate", 0);
										row.set("fisdelete", 0);
										row.set("fisforward", 0);
										row.set("fcount", 1);
										row.set("fsourcetype", "SSH");
										row.set("fdescript", strAry[6].trim());
										row.set("fdetail", strAry[6].trim());
										//插入日志信息
//										if(row.getInt("flevel") > 0)
											service.insertLog(row);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								log.error(e);
							}finally{
								if(session!=null){
									session.close();
								}
								if(conn!=null){
									conn.close();
								}
							}
						}
					}
				}
			}
		}
	}
}
