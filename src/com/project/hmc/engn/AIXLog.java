package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.nmon.engn.Scp_Sftp;

public class AIXLog {
	private Logger logger = Logger.getLogger(ComputerSystem.class);
	private AgentService agentService = new AgentService();

	public void getResult() throws Exception {

		List<DataRow> hypervisorList = agentService.getHpNameAndID();

		ExecutorService executorService = Executors.newFixedThreadPool(30);

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		// 所有物理机hypervisorList.size();
		for (int i = 0, hlLeng = hypervisorList.size(); i < hlLeng; i++) {

			final DataRow hypervisor = hypervisorList.get(i);

			List<DataRow> virtualMacList = agentService.getVirtualNameAndId(hypervisor.getInt("hypervisor_id"));

			// 物理机下所有的虚拟机
			for (int j = 0; j < virtualMacList.size(); j++) {
				final DataRow virtualMac = virtualMacList.get(j);
				// 虚拟机日志
				final List<DataRow> virtualMacPrefList = new ArrayList<DataRow>();

				DataRow loginInfo = agentService.getVIOSLoginInfo(virtualMac.getInt("vm_id"));
				if (loginInfo != null && loginInfo.size() > 0) {
					String[] ipaddressarray = virtualMac.getString("ip_address").split(",");
					String ipaddress = "";
					if (ipaddressarray.length > 1) {
						ipaddress = ipaddressarray[1];
					} else {
						ipaddress = ipaddressarray[0];
					}
					final Scp_Sftp scp = new Scp_Sftp(ipaddress, 22, loginInfo.getString("user"), new DES().decrypt(loginInfo.getString("password")));

					Runnable runnable = new Runnable() {

						public void run() {

							BufferedReader br = null;
							try {
								// 正式环境下删除
								// br = new BufferedReader(new FileReader(
								// "E:/SR/guangfa/putty.log"));
								Connection conn = scp.login();
								logger.info("virtualLog connection:"+conn);
								if (conn != null) {
									String date = DateHelper.formatDate(DateHelper.getDataDiff(new Date(),1), "MMddHHmmyy");
									if (virtualMac.getString("targeted_os").equalsIgnoreCase("vios")) {
										scp.execCommand("oem_setup_env");
									}
									String output = scp.execCommand(" errpt -a -s " + date);

									logger.info("开始执行命令：errpt -aD -s  " + date
											+ ",设备:"
											+ hypervisor.getString("name")
											+ "/"
											+ virtualMac.getString("name"));
									// 正式环境
									br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(output.getBytes())));

									String line;
									StringBuffer detail = new StringBuffer();
									int flag = 0;
									DataRow log = new DataRow();
									List<DataRow> logs = new ArrayList<DataRow>();
									int number = 0;
									while ((line = br.readLine()) != null&& !line.trim().equals("No results were found.")) {
										if (flag == 1) {
											log.set("fdescript", line);
											flag = 0;
										} else if (flag == 3) {
											log.set("fcount", line.trim());
											flag = 0;
										} else if (flag == 4) {
											log.set("ffirsttime",DateHelper.formatTime(simpleDateFormat.parse(line.trim())));
											flag = 0;
										}
										if (line.startsWith("IDENTIFIER:")) {
											log.set("fruleid", line.replace("IDENTIFIER:", "").trim());
											log.set("fcount", 1);
											log.set("fsourcetype", "HMC");
										} else if (line.startsWith("Date/Time:")) {
											log.set("flasttime",DateHelper.formatTime(simpleDateFormat.parse(line.replace("Date/Time:","").trim())));
											log.set("ffirsttime", log.getString("flasttime"));
										} else if (line.startsWith("Sequence Number:")) {
											log.set("fno", line.replace("Sequence Number:", "").trim());
										} else if (line.startsWith("Resource Name:")) {
											log.set("fresourceid", line.replace("Resource Name:","").trim());
											log.set("fresourcename", line.replace("Resource Name:","").trim());
										} else if (line.startsWith("Resource Class:")) {
											log.set("fresourcetype", line.replace("Resource Class:","").trim());
										} else if (line.startsWith("Type:")) {
											String temp = line.replace("Type:","").trim();
											if (temp.equals("PERM")) {
												log.set("flevel", 2);
											} else if (temp.equals("INFO")) {
												log.set("flevel", 0);
											} else {
												log.set("flevel", 1);
											}
										} else if (line.trim().equals("Description")) {
											flag = 1;
										} else if (line.equals("Detail Data")) {
											flag = 2;
										} else if (line.equals("Duplicates")) {
											flag = 0;
										} else if (line.equals("Number of duplicates")) {
											flag = 3;
										} else if (line.equals("Time of first duplicate")) {
											flag = 4;
										} else if (line.startsWith("---------------------------------------------------------------------")) {
											number++;
											flag = 5;
										}

										if (flag == 2) {
											if (!line.equals("Detail Data")&& !line.startsWith("---------------------------------------------------------------------"))
												detail.append(line + "<br/>");
										} else if (flag == 5 && number > 1) {
											log.put("fdetail",detail.toString().length() > 1501 ? detail.toString().substring(0,1500): detail.toString());
											log.put("flogtype", 0);
											log.put("fstate", 0);
											log.put("fisforward", 0);
											log.put("ftoptype", "Virtual");
											log.put("ftopid", virtualMac.get("vm_id"));
											log.put("ftopname", StringHelper.isEmpty(virtualMac.getString("display_name"))?virtualMac.get("name"):virtualMac.getString("display_name"));
											logger.info(log.toString());
											logs.add(log);
											log = new DataRow();
											detail = new StringBuffer("");
											flag = 0;
										}
									}

									agentService.insertLog(logs);
									logger.info("结束命令：errpt -a -s " + date
											+ ", 设备:"
											+ hypervisor.getString("name")
											+ " /"
											+ virtualMac.getString("name"));
								}
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								try {
									if (br != null) {
										br.close();
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
								scp.closeConnection();
							}
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
					executorService.execute(runnable);
				}
			}
		}

		try {
			// 关闭线程池
			executorService.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("虚拟机日志采集结束");
	}
	
	public static void main(String[] args) {
		String a ="";
		String ip = "192.168.1.1";
		String[] strar = ip.split(",");
		if(strar.length>1){
			a = strar[1];
		}else{
			a = strar[0];
		}
		System.out.println(a);
	}
}
