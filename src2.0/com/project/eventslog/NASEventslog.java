package com.project.eventslog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import com.huiming.service.storage.StorageService;
import com.huiming.service.usercon.UserConService;
import com.project.hmc.core.HmcBase;

/**
 * 采集NAS事件日志信息
 * @author LiuCH
 *
 */
public class NASEventslog {
	private UserConService userConService = new UserConService();
	private static Logger log = Logger.getLogger(NASEventslog.class);
	DeviceAlertService service = new DeviceAlertService();
	StorageService storservice=new StorageService();
	
	public void getResult(){
		List<DataRow> nasList = storservice.getSubsystemNames("10",null);
		if(nasList!=null && nasList.size()>0){
			List<DataRow> nasUser = userConService.getDeviceList(null, "5");
			for (DataRow dataRow : nasList) {
				if(nasUser!=null && nasUser.size()>0){
					for (DataRow dataRow2 : nasUser) {
						if(dataRow.getString("id").equals(dataRow2.getString("dev_id"))){
							String ip = "";
							String[] ipAddress = dataRow.getString("ip_address").split(",");
							String name = dataRow.getString("name");
							if(ipAddress.length>1){
								ip = ipAddress[1];
							}else{
								ip = ipAddress[0];
							}
							HmcBase hmcBase = new HmcBase(ip, 22, dataRow2.getString("user"), new DES().decrypt(dataRow2.getString("pwd")));
							Connection conn = null;
							Session session = null;
							try {
								conn = hmcBase.getConn();
								session =conn.openSession();
								session.execCommand("event status show -node "+name+" -severity <=warning -fields indications,drops,severity,stat-starting-time,last-time-occurred");
								log.info("执行命令:event status show -node "+name+" -severity <=warning -fields indications,drops,severity,stat-starting-time,last-time-occurred");
								session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout= new StreamGobbler(session.getStdout());
								BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
								String line="";
								while((line=br.readLine())!=null){
									DataRow row = new DataRow();
									String[] str = line.split("[\\s]+");
									if(str.length>0 && str[0].trim().equals(name)){
										row.set("fname", str[0]);
										row.set("fdetail", str[1]);
										row.set("fcount", str[2]);
										row.set("fdescript", str[4]);
										if(str[4].trim().equalsIgnoreCase("info")){
											row.set("flevel", 0);
										}else if(str[4].trim().equalsIgnoreCase("warning")){
											row.set("flevel", 1);
										}else if(str[4].trim().equalsIgnoreCase("node_error") ||str[4].trim().equalsIgnoreCase("svc_error")){
											row.set("flevel", 2);
										}else if(str[4].trim().equalsIgnoreCase("svc_fault")||str[4].trim().equalsIgnoreCase("node_fault")){
											row.set("flevel", 3);
										}else{
											row.set("flevel", 0);
										}
										row.set("ffirsttime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(str[5]+" "+str[6])));
										row.set("flasttime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(str[7]+" "+str[8])));
										row.set("ftopid", dataRow.getString("id"));
										row.set("ftopname", dataRow.getString("name"));
										row.set("ftoptype", "Storage");
										row.set("fresourceid", dataRow.getString("id"));
										row.set("fresourcename", dataRow.getString("name"));
										row.set("fresourcetype", "storage");
										row.set("flogtype", 0);
										row.set("fstate", 0);
										//插入日志信息
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
	
	public static void main(String[] args) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("D:\\output.txt"))));
			String line = "";
			while((line=br.readLine())!=null){
				DataRow row = new DataRow();
				String[] str = line.split("[\\s]+");
				if(str.length>0 && str[0].trim().equals("node1")){
					row.set("fname", str[0]);
					row.set("fdescript", str[1]);
					row.set("fcount", str[2]);
					row.set("flevel", str[4]);
					row.set("ffirsttime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(str[5]+" "+str[6])));
					row.set("flasttime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(str[7]+" "+str[8])));
					System.out.println(row);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
