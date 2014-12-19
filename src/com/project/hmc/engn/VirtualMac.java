package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;

/**
 * 获取虚拟机信息
 * 
 * @author lic
 * 
 */
public class VirtualMac {

	private static Logger logger= Logger.getLogger(VirtualMac.class);
	
	/**
	 * 获得虚拟机基本信息
	 */
	public void getResult() {
		// 获取所有物理机
		List<DataRow> hypervisorList = agentService.getHpNameAndID();
		// 匹配虚拟机名称
		Pattern pattern = Pattern.compile("^[\\w\\W]*:\\s*$");
		String regEx2 = "^([\\s]*[\\w\\W]+): ([\\w\\W]+)$";
		BufferedReader br=null;
		for (int i = 0, hlLeng = hypervisorList.size(); i < hlLeng; i++) {
			DataRow hypervisor = hypervisorList.get(i);
			List<DataRow> virtualMacList = agentService.getVirtualNameAndId(hypervisor.getInt("hypervisor_id"));
			String line;
			List<DataRow> cmdVirtualMacList = new ArrayList<DataRow>();
			Session session = null;
			DataRow loginInfo = agentService.getHMCLoginInfo(hypervisor.getInt("hmc_id"));
			HmcBase base = new HmcBase(loginInfo.getString("ip_address"), 22, loginInfo.getString("user"), new DES().decrypt(loginInfo.getString("password")));
			try {
				session = base.openConn();
				String cmd = "";
				// 获取物理机下的虚拟机
				session.execCommand("smcli lsvrtsys -c immediate -n "+ hypervisor.getString("name") + " -l");
				logger.info("开始执行命令：smcli lsvrtsys -c immediate -n "+ hypervisor.getString("name") + " -l");
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				InputStream stdout = new StreamGobbler(session.getStdout());
				br = new BufferedReader(new InputStreamReader(stdout));
				DataRow virtualData = null;
				while ((line = br.readLine()) != null && !line.trim().equals("No results were found.")) {
					if (line.isEmpty()) {
						continue;
					}
					// String[] virtualMap = line.split(":");
					Matcher matcher = Pattern.compile(regEx2).matcher(line);
					if (pattern.matcher(line).find()) {
						virtualData = new DataRow();
						virtualData.set("name", line.substring(0, line.indexOf(":")));
					} 
					while (matcher.find()) {
						 if (matcher.group(1).trim().equals("Assigned Memory Size (MB)")) {// 分配的内存大小
							virtualData.set("total_memory", matcher.group(2).trim());
						} else if (matcher.group(1).trim().equals("Minimum Processors")) {// 最小处理器数量
							virtualData.set("minimum_cpu_number", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("Minimum Processing Units")) {
							if(!matcher.group(2).trim().equals("Not Applicable")){
								virtualData.set("minimum_cpu_processunit", Double.valueOf(matcher.group(2).trim()));
							}else{
								virtualData.set("minimum_cpu_processunit", 0);
							}
						} 
						else if(matcher.group(1).trim().equals("Processing Mode")){
							virtualData.set("processing_mode", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("Maximum Processors")) {// 最大处理器数量
							virtualData.set("maximum_cpu_number", matcher.group(2).trim());
						} 
						else if (matcher.group(1).trim().equals("Maximum Processing Units")) {
							if(!matcher.group(2).trim().equals("Not Applicable")){
								virtualData.set("maximum_cpu_processunit", Double.valueOf(matcher.group(2).trim()));
							}else{
								virtualData.set("maximum_cpu_processunit", 0);
							}
						}
						else if (matcher.group(1).trim().equals("Assigned Processors")) {// 处理器数量
							virtualData.set("assigned_cpu_number", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("Assigned Processing Units")) {
							if(!matcher.group(2).trim().equals("Not Applicable")){
								virtualData.set("assigned_cpu_processunit", Double.valueOf(matcher.group(2).trim()));
							}else{
								virtualData.set("assigned_cpu_processunit", 0);
							}
						}  
						else if (matcher.group(1).trim().equals("Environment")) {// 支持环境
							if(matcher.group(2).equals("VIOS")){
								virtualData.set("type", matcher.group(2));
							}else{
								virtualData.set("type", "VOS");
							}
							virtualData.set("targeted_os", matcher.group(2).trim());
						} else if (matcher.group(1).trim().equals("Prevent Relocation")) {
							virtualData.set("hypervisor_id", hypervisor.getInt("hypervisor_id"));
							virtualData.set("host_name", hypervisor.getString("name"));
							cmdVirtualMacList.add(virtualData);
						}
					}
				}
				session.close();
				logger.info("结束命令：smcli lsvrtsys -c immediate -n "+ hypervisor.getString("name") + " -l");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				base.closeConn();
			}
			cmdVirtualMacList = getConfigVirtualMacByName(cmdVirtualMacList,loginInfo);
			int vlLen = virtualMacList.size();
			List<DataRow> insertVirtualMaclist = new ArrayList<DataRow>();
			List<DataRow> updateVirtualMaclist = new ArrayList<DataRow>();
			if (vlLen > 0) {
				// 根据 发送命令得到的虚拟机list 更新 数据库中虚拟机list
				for (int n = 0; n < cmdVirtualMacList.size(); n++) {
					DataRow cmdVirtualRow = cmdVirtualMacList.get(n);
					if (virtualMacList.size() == 0) {
						
						insertVirtualMaclist.add(cmdVirtualRow);
					}
					// 与数据库原有的虚拟机列表做比较
					for (int j = 0; j < virtualMacList.size(); j++) {
						DataRow virtualRow = virtualMacList.get(j);
						if (cmdVirtualRow.getString("name").equals(virtualRow.getString("name"))) {
							virtualMacList.remove(j); // 动态删除 虚拟机列表
							// 出现相同的虚拟机则更新
							cmdVirtualRow.set("vm_id", virtualRow.getInt("vm_id"));
							cmdVirtualRow.set("computer_id", virtualRow.getInt("computer_id"));
							updateVirtualMaclist.add(cmdVirtualRow);
							break;
						}
						if (j == virtualMacList.size() - 1) {
							// hmcBaseService.insertDataRow("t_res_virtualmachine",
							// cmdVirtualRow);
							insertVirtualMaclist.add(cmdVirtualRow);
						}
					}

				}

			} else {
				// 数据库类没有此类型的物理机数据时，则直接添加此物理机下所有虚拟机
				insertVirtualMaclist = cmdVirtualMacList;
			}

			// 要更改状态的 虚拟机list
//			if (!virtualMacList.isEmpty()) {
//
//				for (DataRow data : virtualMacList) {
//					System.out.println(data.getString("name"));
//				}
//
//			}
			agentService.batchInsertVirtual(insertVirtualMaclist);
			agentService.batchUpateVirtual(updateVirtualMaclist);
			agentService.batchUpateVmState(virtualMacList);
			
		}

	}

	
	public List<DataRow> getConfigVirtualMacByName(List<DataRow> virtualBasicList,DataRow row) {

//		Pattern pattern = Pattern.compile("^[\\w\\W]*:\\s*$");
		String regEx2 = "^([\\s]*[\\w\\W]+):([\\w\\W]+)$";//匹配配置信息
		String regIp = "([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})"; //匹配ip地址
		BufferedReader br = null;
		String lineToRead;
		HmcBase base = new HmcBase(row.getString("ip_address"), 22, row.getString("user"), new DES().decrypt(row.getString("password")));
		for (int n = 0, vbLeng = virtualBasicList.size(); n < vbLeng; n++) {

			DataRow virtualMac = virtualBasicList.get(n);
			try {
				Session session = base.openConn();
				session.execCommand("smcli lssys -l "+virtualMac.getString("name"));
				
				logger.info("开始执行命令：smcli lssys -l "+virtualMac.getString("name"));
				
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				InputStream stdout = new StreamGobbler(session.getStdout());
				br = new BufferedReader(new InputStreamReader(stdout));
				
				while ((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")) {

					Matcher matcher =Pattern.compile(regEx2).matcher(lineToRead);
					
					while(matcher.find()){
						
						if(matcher.group(1).trim().equals("OperatingState")){
							String operatingState = matcher.group(2).trim();
							virtualMac.set("operational_status", operatingState);
							
							if(!operatingState.equals("8")){
								
								virtualMac.set("detectable", 0);
							}else{
								virtualMac.set("detectable", 1);
							}
						}
						else if(matcher.group(1).trim().equals("IPv4Address")){
							
							Matcher matcherIp = Pattern.compile(regIp).matcher(matcher.group(2).trim());
							StringBuffer str = new StringBuffer();
							
							int j= 0;
							while(matcherIp.find()){
								if(j>0){
									str.append(",");
								}
								
								str.append(matcherIp.group(1));
								j++;
							}
							virtualMac.set("ip_address", str.toString());
						}
						
					}
					
				}

				session.close();
				logger.info("结束命令：smcli lssys -l "+virtualMac.getString("name"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					br.close();
					base.closeConn();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return virtualBasicList;
	}

	
	private AgentService agentService = new AgentService();
}
