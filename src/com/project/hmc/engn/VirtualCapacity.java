package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;

public class VirtualCapacity {
	private static Logger log= Logger.getLogger(VirtualCapacity.class);
	HmcBase base = null;
	AgentService agent = new AgentService();
	Connection conn = null;
	public void getResult(){
		List<DataRow> hmc = agent.getHMCLoginInfo();
		if(hmc!=null && hmc.size()>0){
			for (DataRow dRow : hmc) {
				try {
					base = new HmcBase(dRow.getString("ip_address"), 22, dRow.getString("user"),  new DES().decrypt(dRow.getString("password")));
					conn = base.getConn();
					if(conn!=null){
						List<DataRow> hypList = agent.getHpNameAndID();
						if(hypList!=null && hypList.size()>0){
							for (DataRow dataRow : hypList) {
								DataRow hRow = new DataRow();
								hRow.set("computer_id", dataRow.getString("host_computer_id"));
								double htotalCap = 0l;
								double hfreeCap = 0l;
								List<DataRow> virList = agent.getVirtualNameAndId(dataRow.getInt("hypervisor_id"));
								if(virList!=null && virList.size()>0){
									for (DataRow dataRow2 : virList) {
										DataRow row = new DataRow();
										row.set("computer_id", dataRow2.getString("computer_id"));
										DataRow loginInfo = null;
										loginInfo = agent.getVIOSLoginInfo(dataRow2.getInt("vm_id"));
										Session session = null;
										BufferedReader br = null;
										try {
											if(dataRow2.getString("targeted_os").equalsIgnoreCase("vioserver") && loginInfo==null){
												//使用默认用户名密码
												log.info("向HMC输入内容："+"rmvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name")+",mkvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name")+",padmin,padmin,oem_setup_env,df -g,ifconfig -a,~.,y");
												session = base.writeToHMC(new Object[]{"rmvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name"),"mkvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name"),"padmin","padmin","oem_setup_env","df -g","ifconfig -a","~.","y"});
											}else if(loginInfo!=null && loginInfo.size()>0){
												log.info("向HMC输入内容："+"rmvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name")+",mkvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name")+","+dataRow2.getString("name")+","+new DES().decrypt(loginInfo.getString("password"))+",oem_setup_env,df -g,ifconfig -a,~.,y");
												session = base.writeToHMC(new Object[]{"rmvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name"),"mkvterm -m "+dataRow.getString("name")+" -p "+dataRow2.getString("name"),loginInfo.getString("user"),new DES().decrypt(loginInfo.getString("password")),"oem_setup_env","df -g","ifconfig -a","~.","y"});
												//使用配置用户名密码
											}
											if(session!=null){
												InputStream stdout = new StreamGobbler(session.getStdout());
												br = new BufferedReader(new InputStreamReader(stdout));
												double totalCap = 0l;
												double freeCap = 0l;
												String lineToRead = "";
												while((lineToRead=br.readLine())!=null){
													log.info("HMC Disk C:"+ lineToRead);
													if(lineToRead.equals("# df -g") ||lineToRead.contains("/#df -g")){
														String line2 = "";
														while((line2 = br.readLine())!=null){
															String str[] =line2.split("[\\s]+"); 
															if(str.length>3){
																if(Pattern.compile("[\\d]+").matcher(str[1]).find() || Pattern.compile("[\\d]+\\.[\\d]+").matcher(str[1]).find()){
																	totalCap += Double.parseDouble(str[1]);
																}
																if(Pattern.compile("[\\d]+").matcher(str[2]).find() || Pattern.compile("[\\d]+\\.[\\d]+").matcher(str[2]).find()){
																	freeCap += Double.parseDouble(str[2]);
																}
															}
															if(line2.equals("# ifconfig -a") || line2.contains("/#ifconfig -a")){
																lineToRead = line2;
																break;
															}
														}
														row.set("disk_space", totalCap);
														row.set("disk_available_space", freeCap);
														//物理及磁盘容量
														htotalCap+=totalCap;
														hfreeCap+=freeCap;
													}
													if(lineToRead.equals("# ifconfig -a")){
														String line2 = "";
														String ipStr="";
														int i = 0;
														while((line2=br.readLine())!=null){
															Matcher m = Pattern.compile("[\\s]+inet ([\\d]+.[\\d]+.[\\d]+.[\\d]+)[\\s]").matcher(line2);
															if(m.find() && !m.group(1).trim().equals("127.0.0.1")){
																if(i>0){
																	ipStr+=",";
																}
																ipStr+=m.group(1);
																i++;
															}
															if(line2.contains("#")){
																break;
															}
														}
														row.set("ip_address", ipStr);
													}
													if(lineToRead.contains("Terminate session? [y/n]")){
														break;
													}
													System.out.println(lineToRead);
												}
												//更新虚拟机IP和盘空间信息
												agent.updateComputer(row);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}finally{
											if(session!=null){
												session.close();
											}
										}
									}
								}
								if(htotalCap!=0 || hfreeCap!=0){
									hRow.set("disk_space", htotalCap);
									hRow.set("disk_available_space", hfreeCap);
									//更新物理机盘空间信息
									agent.updateComputer(hRow);
								}
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(conn!=null){
						conn.close();
					}
					if(base!=null){
						base.closeConn();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:\\output.txt"))));
			double a = 0l;
			double b = 0l;
			while((line=br.readLine())!=null){
//				String str[] =line.split("[\\s]+"); 
//				if(str.length>1){
//					System.out.println(str[1]+"\t"+str[2]);
//					if(Pattern.compile("[\\d]+").matcher(str[1]).find() || Pattern.compile("[\\d]+\\.[\\d]+").matcher(str[1]).find()){
//						a += Double.parseDouble(str[1]);
//					}
//					if(Pattern.compile("[\\d]+").matcher(str[2]).find() || Pattern.compile("[\\d]+\\.[\\d]+").matcher(str[2]).find()){
//						b += Double.parseDouble(str[2]);
//					}
//				}
				Matcher m = Pattern.compile("[\\s]+inet ([\\d]+.[\\d]+.[\\d]+.[\\d]+)[\\s]").matcher(line);
				if(m.find() && !m.group(1).trim().equals("127.0.0.1")){
					System.out.println(m.group(1));
				}
			}
			System.out.println("a:"+a+",b:"+b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
