package com.project.eventslog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.svcevenslog.MdiskEventsService;
import com.huiming.service.usercon.UserConService;
import com.project.hmc.core.HmcBase;

/**
 * DS 类型日志信息
 * @author Liuch
 *
 */
public class DSEventslog {
	private UserConService userConService = new UserConService();
	private static Logger log = Logger.getLogger(DSEventslog.class);
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
							
							//创建日志路径
							String path = this.getClass().getResource("/").getPath();
							path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"resource/subsyslog/output.txt";
							Connection conn = null;
							Session session = null;
							try {
								conn=hmcBase.getConn();
								session =conn.openSession();
								session.execCommand("save storageSubsystem criticalEvents file = '"+path+"'");
								log.info("日志路径:"+path);
								session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
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
							
							BufferedReader br = null;
							try {
								br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
								StringBuffer sb = new StringBuffer();
								String line=null;
								DataRow row = new DataRow();
								while((line=br.readLine())!=null){
									if(line.startsWith("Date/Time:")){
										row = new DataRow();
										String time = new SimpleDateFormat("20yy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(line.replace("Date/Time:","").trim()));
										row.set("ffirsttime",time);
										row.set("flasttime", time);
										String readToLine = "";
										boolean isflag = false;
										while((readToLine=br.readLine())!=null){
											String[] strAry = readToLine.split(":");
											if(strAry.length>0){
												row.set("flogtype", 4);
												row.set("fresourcetype", "Mdisk");
												row.set("fstate", 0);
												row.set("ftoptype", "Storage");
												row.set("fsourcetype", "SSH");
												if(strAry[0].trim().equals("Sequence number")){
													row.set("fno", strAry[1].trim());
												}
												if(strAry[0].trim().equals("Event type")){
													row.set("fruleid", strAry[1].trim());
												}
												if(strAry[0].trim().equals("Priority")){
													if(strAry[0].trim().equalsIgnoreCase("info")){
														row.set("flevel", 0);
													}else if(strAry[0].trim().equalsIgnoreCase("warning")){
														row.set("flevel", 1);
													}else if(strAry[0].trim().equalsIgnoreCase("error")){
														row.set("flevel", 2);
													}else if(strAry[0].trim().equalsIgnoreCase("critical")){
														row.set("flevel", 2);
													}
												}
												if(strAry[0].trim().equals("Description")){
													row.set("fdescript", strAry[1].trim());	
												}
												if(strAry[0].trim().equals("Raw data")){
													String line2 = "";
													while((line2=br.readLine())!=null){
														if(Pattern.compile("^[\\s]*$").matcher(line2).find()){
															isflag = true;
															break;
														}
														sb.append(line2+"<br />");
													}
													row.set("fdetail",sb.toString());
												}
												if(isflag){
													if(row!=null && row.size()>0){
														//插入日志信息
														service.insertLog(row);
														System.out.println(row);
														break;
													}
												}
											}
										}
									}
								}
							} catch (Exception e) {
								log.error(e);
								e.printStackTrace();
							}finally{
								try {
									if(br!=null){
										br.close();
									}
								} catch (Exception e2) {
									e2.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		String path = "D:/java/apache-tomcat/webapps/dongguan_sr/resource/subsyslog/output.txt";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
			StringBuffer sb = new StringBuffer();
			String line=null;
			DataRow row = new DataRow();
			while((line=br.readLine())!=null){
				if(line.startsWith("Date/Time:")){
					row = new DataRow();
					String time = new SimpleDateFormat("20yy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(line.replace("Date/Time:","").trim()));
					row.set("ffirsttime",time);
					row.set("flasttime", time);
					String readToLine = "";
					boolean isflag = false;
					while((readToLine=br.readLine())!=null){
						String[] strAry = readToLine.split(":");
						if(strAry.length>0){
							if(strAry[0].trim().equals("Sequence number")){
								row.set("fno", strAry[1].trim());
							}
							if(strAry[0].trim().equals("Event type")){
								row.set("fruleid", strAry[1].trim());
							}
							if(strAry[0].trim().equals("Priority")){
								row.set("flevel",strAry[1].trim());
							}
							if(strAry[0].trim().equals("Description")){
								row.set("fdescript", strAry[1].trim());	
							}
							if(strAry[0].trim().equals("Raw data")){
								String line2 = "";
								while((line2=br.readLine())!=null){
									if(Pattern.compile("^[\\s]*$").matcher(line2).find()){
										isflag = true;
										break;
									}
									sb.append(line2+"<br />");
								}
								row.set("fdetail",sb.toString());
							}
							if(isflag){
								if(row!=null && row.size()>0){
									//插入日志信息
//									service.insertLog(row);
									System.out.println(row);
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}finally{
			try {
				if(br!=null){
					br.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
