package com.project.svc.eventslog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.svcevenslog.MdiskEventsService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.usercon.UserConService;
import com.project.hmc.core.HmcBase;

public class DsEventslog {
	private UserConService userConService = new UserConService();
	private static Logger log = Logger.getLogger(MdiskEventslog.class);
	DeviceAlertService service = new DeviceAlertService();
	MdiskEventsService ev = new MdiskEventsService();

	public void getResult() {
		//ds 阵列集合
		List<DataRow> dsList = ev.getMdiskList("ds");
		//登陆信息
		List<DataRow> loginInfo = userConService.getDeviceList(null, "3");
		if(dsList!=null && dsList.size()>0){
			for (DataRow ds : dsList) {
				if(loginInfo!=null && loginInfo.size()>0){
					for (DataRow logins : loginInfo) {
						if(ds.getString("dev_id").equals(logins.getString("dev_id"))){
							HmcBase hmcBase = new HmcBase(logins.getString("ip_address"), 22, logins.getString("user"), new DES().decrypt(logins.getString("pwd")));
							Connection conn = null;
							Session session = null;
							try {
								conn=hmcBase.getConn();
								session =conn.openSession();
								session.execCommand("save storageSubsystem (allEvents | criticalEvents)");
								session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout= new StreamGobbler(session.getStdout());
								BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
								StringBuffer sb = new StringBuffer();
								String line=null;
								DataRow row = new DataRow();
								while((line=br.readLine())!=null){
									if(line.startsWith("Date/Time:")){
										String time = new SimpleDateFormat("20yy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(line.replace("Date/Time:","").trim()));
										row.set("ffirsttime",time);
										row.set("flasttime", time);
									}
									String[] strAry = line.split(":");
									if(strAry.length>0){
										if(strAry[0].equals("Sequence number")){
											row.set("fno", strAry[1].trim());
										}
										if(strAry[0].equals("Event type")){
											row.set("fruleid", strAry[1].trim());
										}
										
										
										if(strAry[0].equals("Event category")){
											row.set("flevel",strAry[1].trim());
										}
										row.set("fcount", 1);
										row.set("flogtype", 0);
										row.set("ftopid", ds.getString("dev_id"));
										row.set("ftopname", ds.getString("dev_name"));
										row.set("ftoptype", "Storage");
										row.set("fresourceid", ds.getString("ele_id"));
										row.set("fresourcename", ds.getString("ele_name"));
										row.set("fresourcetype", "disk");
										row.set("fstate", 0);
										row.set("fsourcetype", "SSH");
										if(strAry[0].equals("Description")){
											row.set("fdescript", strAry[1].trim());	
										}
										Matcher m = Pattern.compile("(\\w\\w ){3,16}").matcher(strAry[0]);
										if(m.find()){
											sb.append(m.group(0)+"<br />");
										}
										row.set("fdetail",sb.toString());
										//插入日志信息
									}
								}
								service.insertLog(row);
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
	DeviceAlertService service = new DeviceAlertService();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("D://output.txt"))));
			String line=null;
			try {
				StringBuffer sb = new StringBuffer();
				DataRow row = new DataRow();
				while((line=br.readLine())!=null){
					//String[] strAry = line.split(":");
					//if(strAry.length>0){
						
						
						if(line.startsWith("Date/Time:")){
							String time;
							try {
								time = new SimpleDateFormat("20yy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(line.replace("Date/Time:","").trim()));
								row.set("ffirsttime",time);
								row.set("flasttime", time);
								System.out.println(time+"+++++++++===");
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						/*
						if(line.startsWith("Sequence number:")){
							row.set("fno", line.replace("Sequence number:","").trim());
						}
						if(line.startsWith("Event type:")){
							row.set("fruleid", line.replace("Event type:","").trim());
						}
						
						
						if(line.startsWith("Event category:")){
							row.set("flevel",line.replace("Event category:","").trim());
						}
						row.set("fcount", 1);
						row.set("flogtype", 0);
						row.set("fresourcetype", "disk");
						row.set("fstate", 0);
						row.set("fsourcetype", "SSH");
						if(line.startsWith("Description:")){
							row.set("fdescript", line.replace("Description:","").trim());	
						}
						Matcher m = Pattern.compile("(\\w\\w ){3,16}").matcher(line);
						//Matcher m = Pattern.compile("Raw data:(.*?)").matcher(line);
						if(m.find()){
							sb.append(m.group(0)+"<br />");
						}
						row.set("fdetail",sb.toString());
						*/
						
						String[] strAry = line.split(":");
						
						if(strAry.length>0){
							
							if(strAry[0].equals("Sequence number")){
								row.set("fno", strAry[1].trim());
							}
							if(strAry[0].equals("Event type")){
								row.set("fruleid", strAry[1].trim());
							}
							
							
							if(strAry[0].equals("Event category")){
								row.set("flevel",strAry[1].trim());
							}
							row.set("fcount", 1);
							row.set("flogtype", 0);
							
							row.set("ftoptype", "Storage");
							
							row.set("fresourcetype", "disk");
							row.set("fstate", 0);
							row.set("fsourcetype", "SSH");
							if(strAry[0].equals("Description")){
								row.set("fdescript", strAry[1].trim());	
							}
							Matcher m = Pattern.compile("(\\w\\w ){3,16}").matcher(strAry[0]);
							if(m.find()){
								sb.append(m.group(0)+"<br />");
							}
							row.set("fdetail",sb.toString());
							System.out.println(row);		
					}	
				}
				service.insertLog(row);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
