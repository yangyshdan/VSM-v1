package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.usercon.UserConService;
import com.project.hmc.core.HmcBase;

public class SwitchRes {
	private UserConService userConService = new UserConService();
	private static Logger log = Logger.getLogger(SwitchRes.class);
	DeviceAlertService service = new DeviceAlertService();
	SwitchService switchService=new SwitchService();
	AgentService agent = new AgentService();
	List<DataRow> list = new ArrayList<DataRow>();
	/**
	 * 交换机状态信息
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
							Connection conn1 = null;
							Session session1 = null;
							try {
								conn=hmcBase.getConn();
								session =conn.openSession();
								session.execCommand("hashow");
								session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout= new StreamGobbler(session.getStdout());
								BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
								String line=null;
								DataRow row = new DataRow();
								while((line=br.readLine())!=null){
									if(line.startsWith("Remote CP")){
										if(line.contains("Healthy")){
											row.set("engine_status", "Healthy");
										}
										if(line.contains("Failed")){
											row.set("engine_status", "Failed");
										}
										if(line.contains("Unknown")){
											row.set("engine_status", "Unknown");
										}
									}
								}
								conn1=hmcBase.getConn();
								session1 =conn1.openSession();
								//session1.execCommand("psshow");
								session1.execCommand("switchstatusshow");
								session1.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout1= new StreamGobbler(session1.getStdout());
								BufferedReader br1 = new BufferedReader(new InputStreamReader(stdout1));
								String line1=null;
								while((line1=br1.readLine())!=null){
									//电源
									//if(line1.startsWith("Power Supply #1 is ")){
									//	row.set("power_status",line1.replace("Power Supply #1 is ","").trim());
									//}
									if(line1.startsWith("Power supplies monitor")){
										row.set("power_status",line1.replace("Power supplies monitor","").trim());
									}else if(line1.startsWith("Fans monitor")){
										row.set("fan_status",line1.replace("Fans monitor","").trim());
									}
								}
								row.set("id", data.getString("dev_id"));
								list.add(row);
								agent.batchInsertSwitchRes(list);
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
	
	public static void main(String[] args) {
		System.out.println(new DES().decrypt("dK6t0XKuJTo="));
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("D://output.txt"))));
//			String line=null;
//			try {
//				
//				while((line=br.readLine())!=null){
//					while((line=br.readLine())!=null){
//						if(line.startsWith("Power supplies monitor ")){
//							System.out.println(line.replace("Power supplies monitor ","").trim());
//						}
//					}
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}

	}
}
