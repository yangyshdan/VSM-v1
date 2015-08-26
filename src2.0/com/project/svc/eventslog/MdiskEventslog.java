package com.project.svc.eventslog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.svcevenslog.MdiskEventsService;
import com.project.hmc.core.HmcBase;

/**
 * SVC Mdisk 日志信息采集
 * 首先测试命令是否可行，得到的结果格式是否与预期结果一样，测试命令如下：
 * 1.lseventlog
 * 2.lseventlog -filtervalue object_type=mdisk
 * 若两条得到的结果和预期的都一样，则选第2条使用（修改本页面60、61行）
 * @author Lch
 *
 */
public class MdiskEventslog {
	MdiskEventsService ev = new MdiskEventsService();
	DeviceAlertService service = new DeviceAlertService();
	private static Logger log = Logger.getLogger(MdiskEventslog.class);
	/**
	 * 得到所有SVC Mdisk日志信息
	 * 目标得到SVC Mdisk：控制器工作状态;电池工作状态;电源工作状态;磁盘工作状态;接口卡工作状态;盘柜状态
	 */
	public void getResult(){
		//svc 阵列集合
		List<DataRow> mdiskList = ev.getMdiskList("svc");
		//设备登录信息集合
		List<DataRow> loginInfo = ev.getLoginInfo();
		
		if(mdiskList!=null && mdiskList.size()>0){
			for (DataRow mdisks : mdiskList) {
				if(loginInfo!=null && loginInfo.size()>0){
					for (DataRow logins : loginInfo) {
						if(mdisks.getString("dev_id").equals(logins.getString("dev_id"))){
							HmcBase base = new HmcBase(logins.getString("ip_address"),22,logins.getString("users"),new DES().decrypt(logins.getString("pwd")));
							Session session = null;
							BufferedReader br = null;
							try {
								String readToLine = null;
								session = base.openConn();
								if(session!=null){
//									session.execCommand("lseventlog -filtervalue object_type=mdisk");   //若到实际环境测试该命令是否可用
									session.execCommand("lseventlog");
									session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
									InputStream stdout = new StreamGobbler(session.getStdout());
									br = new BufferedReader(new InputStreamReader(stdout));
									while((readToLine = br.readLine())!=null){
										String[] strAry = readToLine.split(":");
										if(strAry[2].equalsIgnoreCase("mdisk")){
											BufferedReader br2 = null;
											Session se2 = null;
											try {
												String readLine = null;
												se2 = base.openConn();
												se2.execCommand("lseventlog "+strAry[0]);
												se2.waitForCondition(ChannelCondition.TIMEOUT, 10000);
												InputStream stdout2 = new StreamGobbler(se2.getStdout());
												br2 = new BufferedReader(new InputStreamReader(stdout2));
												StringBuffer sb = new StringBuffer();
												while((readLine = br2.readLine())!=null){
													Matcher m = Pattern.compile("^([\\S]+) ([\\w\\W]*)$").matcher(readLine);
													if(m.find()){
														String eleName = m.group(1).trim();
														String eleValue = m.group(2).trim();
														DataRow row = new DataRow();
														if(eleName.equals("first_timestamp")){ //首次发生时间
															String firstTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyMMddHHmmss").parse(eleValue));
															row.set("ffirsttime", firstTime); 
														}
														if(eleName.equals("last_timestamp")){  //最后发生时间
															String lastTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyMMddHHmmss").parse(eleValue));
															row.set("flasttime", lastTime);
														}
														if(eleName.equals("sequence_number")){ //日志ID
															row.set("fno", eleValue);
														}
														if(eleName.equals("event_count")){    //发生次数
															row.set("fcount", eleValue);
														}
														if(eleName.equals("notification_type")){// 事件等级 0:informational,1.warning,2.error,3:none
															if(eleValue.equalsIgnoreCase("informational")){
																row.set("flevel", 0);
															}else if(eleValue.equalsIgnoreCase("warning")){
																row.set("flevel", 1);
															}else if(eleValue.equalsIgnoreCase("error")){
																row.set("flevel", 2);
															}else if(eleValue.equalsIgnoreCase("none")){
																row.set("flevel", 3);
															}
														}
														if(eleName.equals("event_id_text")){  //说明
															row.set("fdescript", eleValue);
														}
														if(eleName.equals("error_code")){ //事件类型ID
															row.set("fruleid", eleValue);
														}
														if(eleName.equals("error_code_text")){  //事件详细
															sb.append("Error Text:"+eleValue+"<br />");
														}
														if(eleName.matches("^sense[0-9]+$")){
															sb.append(eleValue+"<br />");
														}
														row.set("fdetail", sb.toString());
														row.set("flogtype", 0);
														row.set("ftopid", mdisks.getString("dev_id"));
														row.set("ftoptype", "Storage");
														row.set("ftopname", mdisks.getString("dev_name"));
														row.set("fresourceid", mdisks.getString("ele_id"));
														row.set("fresourcename", mdisks.getString("ele_name"));
														row.set("fresourcetype", "Mdisk");
														row.set("fstate", 0);
														row.set("fsourcetype", "SSH");
														//插入日志信息
														service.insertLog(row);
													}
												}
											} catch (Exception e) {
												e.printStackTrace();
											}finally{
												if(br2!=null){
													br2.close();
												}
												if(se2!=null){
													se2.close();
												}
											}
											
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								log.error(e);
							}finally{
								if(br!=null){
									try {
										br.close();
									} catch (IOException e) {
										e.printStackTrace();
										log.error("BufferedReader closeed fail");
									}
								}
								if(session!=null){
									session.close();
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String readLine = null;
		BufferedReader br2 = null;
		try {
			br2 = new BufferedReader(new InputStreamReader(new FileInputStream("E://output.txt")));
			while((readLine = br2.readLine())!=null){
				Matcher m = Pattern.compile("^([\\S]+) ([\\w\\W]*)$").matcher(readLine);
				if(m.find()){
					System.out.println(m.group(1)+"//"+m.group(2));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
