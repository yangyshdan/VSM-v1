package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;

public class VIOSMapping {
	private Logger log = Logger.getLogger(VIOSMapping.class);
	HmcBase base = null;
	AgentService agent = new AgentService();
	Connection conn = null;
	public void getResult(){
		
		List<DataRow> hmcs = agent.getHMCLoginInfo();
		if(hmcs != null && hmcs.size() > 0){
			for (DataRow hmc : hmcs) {
				base = new HmcBase(hmc.getString("ip_address"), 22, hmc.getString("user"),  new DES().decrypt(hmc.getString("password")));
				List<DataRow> rows = agent.getHpNameAndIDByHmcId(hmc.getInt("id"));  //物理机集
				for (DataRow hyperV : rows) {
					Map<String, List<DataRow>> viosMap = new HashMap<String, List<DataRow>>();
					Map<String, String[]> viocMap = new HashMap<String, String[]>();
					List<DataRow> virtuals = agent.getVirtualNameAndId(hyperV.getInt("hypervisor_id"));
					for (DataRow virtual : virtuals) {
						Session session = null;
						BufferedReader br = null;
						String lineToRead = "";
						try {
							conn = base.getConn();
							if(virtual.getString("targeted_os").equalsIgnoreCase("vioserver") ){
								//使用默认用户名密码读取VIOS map
								log.info("向HMC输入内容："+"rmvterm -m "+hyperV.getString("name")+" -p "+virtual.getString("name")+",mkvterm -m "+hyperV.getString("name")+" -p "+virtual.getString("name")+",padmin,padmin,lsmap -all,~.,y");
								session = base.writeToHMC(new Object[]{"rmvterm -m "+hyperV.getString("name")+" -p "+virtual.getString("name"),"mkvterm -m "+hyperV.getString("name")+" -p "+virtual.getString("name"),"padmin","padmin","lsmap -all","~.","y"});
								if(session!=null){
									InputStream stdout = new StreamGobbler(session.getStdout());
									br = new BufferedReader(new InputStreamReader(stdout));
									while((lineToRead=br.readLine())!=null){
										log.info("VIOS -----:"+ lineToRead);
										if(lineToRead.equals("$ lsmap -all")){
											String line2 = "";
											String physloc = "";
											List<DataRow> vios = null;
											DataRow temp = null;
											while((line2 = br.readLine())!=null){
												String str[] =line2.split("[\\s]+"); 
												if(str.length>2){
													if(!(str[0].startsWith("SVSA")|| str[0].startsWith("---------------") || str[0].startsWith("Backing"))){
														physloc = str[1].trim();
														if(vios != null && vios.size() > 0){
															viosMap.put(physloc, vios);
														}
														vios = new ArrayList<DataRow>();
													}else if(str[0].startsWith("Backing")){
														temp.set("disk_name", str[1]);
													}
												}else if(str.length == 2){
													if(str[0].trim().equals("VTD")){
														temp = new DataRow();
														temp.set("vid", str[1]);
													}else if(str[0].trim().equals("Status")){
														temp.set("status", str[1]);
													}else if(str[0].trim().equals("LUN")){
														temp.set("lun", str[1]);
													}else if(str[0].trim().equals("Physloc")){
														temp.set("physloc", str[1]);
													}else if(str[0].trim().equals("Mirrored")){
														temp.set("mirrored", str[1]);
														vios.add(temp);
													}
												}else if(line2.trim().startsWith("$")){
													viosMap.put(physloc, vios);
													break;
												}
											}
										}
										if(lineToRead.contains("Terminate session? [y/n]")){
											break;
										}
										System.out.println(lineToRead);
									}
								}
							}else{
								session = base.openConn();
								if(session!=null){
									log.info("执行执行: lshwres -m " +hyperV.getString("name") + " -r virtualio --rsubtype scsi --level lpar --filter \"lpar_names="+virtual.getString("name")+"\" -F remote_lpar_id,remote_slot_num ");
									session.execCommand("lshwres -m " +hyperV.getString("name") + " -r virtualio --rsubtype scsi --level lpar --filter \"lpar_names="+virtual.getString("name")+"\" -F remote_lpar_id,remote_slot_num ");
									session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
									InputStream stdout = new StreamGobbler(session.getStdout());
									br = new BufferedReader(new InputStreamReader(stdout));
									while ((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")) {
										Matcher matcher = Pattern.compile("^[\\d]+,[\\d]+$").matcher(lineToRead);
										if(matcher.find()){
											String[] fibricStr = lineToRead.trim().split(",");
											viocMap.put(virtual.getString("vm_id"), fibricStr);
										}
										log.info("VIOC Map:"+lineToRead);
									}
									
								}
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(br!=null){
								try {
									br.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							if(session!=null){
								session.close();
							}
							if(base!=null){
								base.closeConn();
							}
						}
					}
//					Map<String, List<DataRow>> viosMap = new HashMap<String, List<DataRow>>();
//					Map<String, String[]> viocMap = new HashMap<String, String[]>();
					Map<String , List<DataRow>> result = new HashMap<String, List<DataRow>>();
					if(viosMap.size() > 0 && viocMap.size() > 0){
						for (String viocKey : viocMap.keySet()) {
							String key = "-V"+viocMap.get(viocKey)[0]+"-C"+viocMap.get(viocKey)[1];
							for (String viosKey : viosMap.keySet()) {
								if(viosKey.endsWith(key)){
									result.put(viocKey, viosMap.get(viosKey));
								}
							}
						}
						agent.insertDisk(result);
					}
				}
			}
		}
	}
}
