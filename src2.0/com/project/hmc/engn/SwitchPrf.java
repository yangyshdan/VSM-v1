package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.usercon.UserConService;
import com.project.hmc.core.HmcBase;

public class SwitchPrf {
	private UserConService userConService = new UserConService();
	private static Logger log = Logger.getLogger(SwitchPrf.class);
	DeviceAlertService service = new DeviceAlertService();
	SwitchService switchService=new SwitchService();
	AgentService agent = new AgentService();
	List<DataRow> list = new ArrayList<DataRow>();
	//CPU和memory
	public void getResult(String marker) {
		List<DataRow> sList = switchService.getSwitchList(null, null, null, null, null);
		List<DataRow> switchList = userConService.getDeviceList(null, "2");
		if(sList!=null && sList.size()>0){
			for (DataRow switchs : sList) {
				if(switchList!=null && switchList.size()>0){
					for (DataRow data : switchList) {
						if(switchs.getString("switch_id").equals(data.getString("dev_id"))){
							HmcBase hmcBase = new HmcBase(data.getString("ip_address"), 22, data.getString("user"), new DES().decrypt(data.getString("pwd")));
							Connection conn = null;
							Session session = null;
							Connection conn1 = null;
							Session session1 = null;
							try {
								conn=hmcBase.getConn();
								session =conn.openSession();
								session.execCommand("sysmonitor --show mem");
								session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout= new StreamGobbler(session.getStdout());
								BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
								String line=null;
								DataRow row = new DataRow();									
								row.set("sample_time", DateHelper.formatTime(new Date()));
								row.set("summ_type",1);
								row.set("switch_id", data.getString("dev_id"));
								row.set("subsystem_name", data.getString("device_name"));
								row.set("subsystem_id", data.getString("dev_id"));
								row.set("perf_marker", marker);
								while((line=br.readLine())!=null){
									String[] strAry = line.split(":");
									if(strAry.length>0){
										if(strAry[0].trim().equalsIgnoreCase("Polling Interval")){
											row.set("interval_len",strAry[1].trim());
										}
										if(strAry[0].trim().equalsIgnoreCase("Used Memory")){
											Matcher m = Pattern.compile(".*k (.*%)").matcher(strAry[1]);
											while (m.find()) {
												row.set("mem_used_prct", m.group(1));
											}
										}
									}
								}
								conn1=hmcBase.getConn();
								session1 =conn.openSession();
								session1.execCommand("sysmonitor --show cpu");
								session1.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout1= new StreamGobbler(session1.getStdout());
								BufferedReader br1 = new BufferedReader(new InputStreamReader(stdout1));
								String line1=null;
								
								while((line1=br1.readLine())!=null){	
									String[] strAry1 = line1.split(":");
									if(strAry1.length>0){
										if(strAry1[0].trim().equalsIgnoreCase("Cpu Usage")){
											row.set("cup_used_prct",strAry1[1].trim());
										}
										
									}
								}
								list.add(row);
								agent.batchInsertSwitchPrf(list);
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
