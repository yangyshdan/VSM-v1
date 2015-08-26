package com.project.hmc.engn;

import java.io.BufferedReader;
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
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.AES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;
import com.project.web.WebConstants;

/**
 * 获取虚拟机信息
 * 
 * @author lic
 * 
 */
public class VirtualMac {

	private static Logger logger = Logger.getLogger(VirtualMac.class);
	private AgentService agentService = new AgentService();
	
	/**
	 * 针对Power架构类型,虚拟机配置信息
	 */
	public void getResult() {
		// 获取所有物理机
		List<DataRow> hypervisorList = agentService.getPhysicalList(WebConstants.OSTYPE_LINUX);
		// 匹配虚拟机名称
		Pattern pattern = Pattern.compile("^[\\w\\W]*:\\s*$");
		String regEx2 = "^([\\s]*[\\w\\W]+): ([\\w\\W]+)$";
		BufferedReader br=null;
		for (int i = 0, hlLeng = hypervisorList.size(); i < hlLeng; i++) {
			DataRow hypervisor = hypervisorList.get(i);
			List<DataRow> virtualMacList = agentService.getVirtualNameAndIdIsDetectable(hypervisor.getInt("hypervisor_id"));
			String line;
			List<DataRow> cmdVirtualMacList = new ArrayList<DataRow>();
			Session session = null;
			DataRow loginInfo = agentService.getHMCLoginInfo(hypervisor.getInt("hmc_id"));
			HmcBase base = new HmcBase(loginInfo.getString("ip_address"), 22, loginInfo.getString("user"), new AES(loginInfo.getString("id")).decrypt(loginInfo.getString("password"),"UTF-8"));
			try {
				session = base.openConn();
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
								virtualData.set("minimum_cpu_processunit", Double.valueOf(matcher.group(2).trim())*100);
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
								virtualData.set("maximum_cpu_processunit", Double.valueOf(matcher.group(2).trim())*100);
							}else{
								virtualData.set("maximum_cpu_processunit", 0);
							}
						}
						else if (matcher.group(1).trim().equals("Assigned Processors")) {// 处理器数量
							virtualData.set("assigned_cpu_number", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("Assigned Processing Units")) {
							if(!matcher.group(2).trim().equals("Not Applicable")){
								virtualData.set("assigned_cpu_processunit", Double.valueOf(matcher.group(2).trim())*100);
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
		//匹配配置信息
		String regEx2 = "^([\\s]*[\\w\\W]+):([\\w\\W]+)$";
		//匹配ip地址
		String regIp = "([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})"; 
		BufferedReader br = null;
		String lineToRead;
		HmcBase base = new HmcBase(row.getString("ip_address"), 22, row.getString("user"), new AES(row.getString("id")).decrypt(row.getString("password"),"UTF-8"));
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
					while (matcher.find()) {
						if (matcher.group(1).trim().equals("OperatingState")) {
							String operatingState = matcher.group(2).trim();
							virtualMac.set("operational_status", operatingState);
							if (!operatingState.equals("8")) {
								virtualMac.set("detectable", 0);
							} else {
								virtualMac.set("detectable", 1);
							}
						} else if (matcher.group(1).trim().equals("IPv4Address")) {
							Matcher matcherIp = Pattern.compile(regIp).matcher(matcher.group(2).trim());
							StringBuffer str = new StringBuffer();
							int j = 0;
							while (matcherIp.find()) {
								if (j > 0) {
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
	
	/**
	 * 获取虚拟机的基本信息
	 * @param physicalId
	 * @param vmConfigid
	 */
	public void getVirtualConfigInfo(String physicalId, String vmConfigid) {
		try {
			HmcBase hmcBase = null;
			Session session = null;
			Matcher matcher = null;
			//指定要找的虚拟机配置
			if (StringHelper.isNotEmpty(physicalId) || StringHelper.isNotEmpty(vmConfigid)) {
				//数据库中存在的虚拟机数据
				List<DataRow> vmExistsList = agentService.getVirtMachList();
				DataRow vmConfigRow = agentService.getHMCLoginInfo(Integer.parseInt(vmConfigid));
				String hmcId = vmConfigid;
				String ipAddress = vmConfigRow.getString("ip_address");
				String userName = vmConfigRow.getString("user");
				String password = new AES(hmcId).decrypt(vmConfigRow.getString("password"),"UTF-8");
				String osType = vmConfigRow.getString("os_type");
				String vmName = null;
				
				//判断是否是LINUX操作系统,如果不是,则终止采集该物理机配置信息
				if (!osType.equals(WebConstants.OSTYPE_LINUX)) {
					logger.error("The program does not support not Linux os system , this virtual machine ip is : " + ipAddress);
					return;
				}
				
				//保存信息
				DataRow virtMachRow = new DataRow();
				virtMachRow.set("hypervisor_id", physicalId);
				virtMachRow.set("ip_address", ipAddress);
				//初始化连接参数
				hmcBase = new HmcBase(ipAddress, 22, userName, password);
				//建立连接
				session = hmcBase.openConn();
				//连接不成功
				if (session != null) {
					virtMachRow.set("detectable", 1);
				} else {
					virtMachRow.set("detectable", 0);
				}
				
				//获取计算机名称
				vmName = hmcBase.executeCommand("hostname").get(0);
				
				//获取系统信息
				String osInfo = hmcBase.executeCommand("uname").get(0);
				
				//获取操作系统发行版本信息
				List<String> osVersionInfo = hmcBase.executeCommand("head -n 1 /etc/issue");
				if (osVersionInfo != null && osVersionInfo.size() > 0) {
					virtMachRow.set("os_version", osVersionInfo.get(0));
				}
				
				//获取CPU信息
				//物理CPU个数
				List<String> physicalCpu = hmcBase.executeCommand("cat /proc/cpuinfo | grep \"physical id\" | sort | uniq | wc -l");
				virtMachRow.set("assigned_cpu_number", physicalCpu.get(0));
				//逻辑CPU个数
				List<String> logicCpu = hmcBase.executeCommand("cat /proc/cpuinfo | grep \"processor\" | wc -l");
				virtMachRow.set("assigned_cpu_processunit", logicCpu.get(0));
				//CPU频率
				List<String> cpuInfo = hmcBase.executeCommand("cat /proc/cpuinfo |grep MHz|uniq");
				String[] cpuVals = cpuInfo.get(0).split(":");
				virtMachRow.set("processor_speed", Math.round(Double.parseDouble(cpuVals[1].trim())));
				
				//获取内存信息
				List<String> memInfo = hmcBase.executeCommand("cat /proc/meminfo");
				//MemTotal: 1231231 KB
				String totalMemStr = memInfo.get(0).split(":")[1].trim();
				//MemFree: 343434 KB
				String freeMemStr = memInfo.get(1).split(":")[1].trim();
				int totalMem = Integer.parseInt(totalMemStr.substring(0, totalMemStr.indexOf("kB")).trim())/1024;
				int freeMem = Integer.parseInt(freeMemStr.substring(0, freeMemStr.indexOf("kB")).trim())/1024;
				virtMachRow.set("ram_size", totalMem);
				virtMachRow.set("total_memory", totalMem);
				
				//获取磁盘信息
				List<String> diskInfo = hmcBase.executeCommand("df");
				String diskRegExp = "^([\\w\\W]+[\\s]+)([\\d]+[\\s]+)([\\d]+[\\s]+)([\\d]+[\\s]+)([\\d]+[%]{1}[\\s]+)([\\w\\W]+)$";
				Integer totalDisk = 0;
				Integer freeDisk = 0;
				for (int j = 1; j < diskInfo.size(); j++) {
					matcher = Pattern.compile(diskRegExp).matcher(diskInfo.get(j));
					if (matcher.find()) {
						totalDisk = totalDisk + Integer.parseInt(matcher.group(2).trim());
						freeDisk = freeDisk + Integer.parseInt(matcher.group(4).trim());
					}
				}
				totalDisk = totalDisk == null ? 0 : (totalDisk/1024);
				freeDisk = freeDisk == null ? 0 : (freeDisk/1024);
				virtMachRow.set("disk_space", totalDisk);
				virtMachRow.set("disk_available_space", freeDisk);
				
				//判断记录是否存在,存在则只需要更新,否则添加
				if (vmExistsList.size() > 0) {
					for (int j = 0; j < vmExistsList.size(); j++) {
						if (vmExistsList.get(j).getString("hmc_id").equals(vmConfigid)){
							virtMachRow.set("computer_id", vmExistsList.get(j).getString("computer_id"));
							virtMachRow.set("hypervisor_id", vmExistsList.get(j).getString("hypervisor_id"));
							virtMachRow.set("vm_id", vmExistsList.get(j).getString("vm_id"));
							virtMachRow.set("name", vmExistsList.get(j).getString("name"));
							vmName = vmExistsList.get(j).getString("name");
							break;
						}
					}
				}
				
				//添加或更新数据到数据库表(t_res_computersystem,t_res_virtualmachine)
				agentService.insertOrUpdateVirtual(virtMachRow);
				//更新服务器配置信息表
				DataRow serverRow = new DataRow();
				serverRow.set("id", hmcId);
				serverRow.set("name", vmName);
				serverRow.set("os_type", osInfo);
				agentService.updateServerLoginInfo(serverRow);
				logger.info("It's success to update the virtual machine info !");
			//否则找配置信息里面的虚拟机
			} else {
				//获取所有物理机
				List<DataRow> hypervisorList = agentService.getPhysicalListHaveHypv();
				for (int i = 0; i < hypervisorList.size(); i++) {
					DataRow physicRow = hypervisorList.get(i);
					//获取物理机下的虚拟机
					List<DataRow> virtualMacList = agentService.getVirtualNameAndIdIsDetectable(physicRow.getInt("hypervisor_id"));
					if (virtualMacList.size() > 0) {
						for (int j = 0; j < virtualMacList.size(); j++) {
							DataRow vmRow = virtualMacList.get(j);
							//获取虚拟机配置信息
							DataRow vmConfigRow = agentService.getVirtMachLoginInfo(vmRow.getString("vm_id"));
							String hmcId = vmConfigRow.getString("hmc_id");
							String ipAddress = vmConfigRow.getString("ip_address");
							String userName = vmConfigRow.getString("user");
							String password = new AES(hmcId).decrypt(vmConfigRow.getString("password"),"UTF-8");
							String osType = vmConfigRow.getString("os_type");
							String vmName = vmConfigRow.getString("name");
							
							//判断是否是LINUX操作系统,如果不是,则终止采集该物理机配置信息
							if (!osType.equals(WebConstants.OSTYPE_LINUX)) {
								logger.error("The program does not support not Linux system , this virtual machine ip is : " + ipAddress);
								continue;
							}
							
							//保存信息
							DataRow virtMachRow = new DataRow();
							virtMachRow.set("computer_id", vmConfigRow.getString("computer_id"));
							virtMachRow.set("hypervisor_id", vmConfigRow.getString("hypervisor_id"));
							virtMachRow.set("vm_id", vmConfigRow.getString("vm_id"));
							virtMachRow.set("ip_address", ipAddress);
							
							//初始化连接参数
							hmcBase = new HmcBase(ipAddress, 22, userName, password);
							//建立连接
							session = hmcBase.openConn();
							//连接不成功
							if (session != null) {
								virtMachRow.set("detectable", 1);
							} else {
								virtMachRow.set("detectable", 0);
							}
							
							//获取系统信息
							String osInfo = hmcBase.executeCommand("uname").get(0);
							
							//获取操作系统发行版本信息
							List<String> osVersionInfo = hmcBase.executeCommand("head -n 1 /etc/issue");
							if (osVersionInfo != null && osVersionInfo.size() > 0) {
								virtMachRow.set("os_version", osVersionInfo.get(0));
							}
							
							//获取CPU信息
							List<String> cpuInfo = hmcBase.executeCommand("cat /proc/cpuinfo |grep MHz|uniq");
							String[] cpuVals = cpuInfo.get(0).split(":");
							virtMachRow.set("processor_speed", Math.round(Double.parseDouble(cpuVals[1].trim())));
							
							//获取内存信息
							List<String> memInfo = hmcBase.executeCommand("cat /proc/meminfo");
							//MemTotal: 1231231 KB
							String totalMemStr = memInfo.get(0).split(":")[1].trim();
							//MemFree: 343434 KB
							String freeMemStr = memInfo.get(1).split(":")[1].trim();
							int totalMem = Integer.parseInt(totalMemStr.substring(0, totalMemStr.indexOf("kB")).trim())/1024;
							int freeMem = Integer.parseInt(freeMemStr.substring(0, freeMemStr.indexOf("kB")).trim())/1024;
							virtMachRow.set("ram_size", totalMem);
							virtMachRow.set("total_memory", totalMem);
							
							//获取磁盘信息
							List<String> diskInfo = hmcBase.executeCommand("df");
							String diskRegExp = "^([\\w\\W]+[\\s]+)([\\d]+[\\s]+)([\\d]+[\\s]+)([\\d]+[\\s]+)([\\d]+[%]{1}[\\s]+)([\\w\\W]+)$";
							Integer totalDisk = 0;
							Integer freeDisk = 0;
							for (int k = 1; k < diskInfo.size(); k++) {
								matcher = Pattern.compile(diskRegExp).matcher(diskInfo.get(j));
								if (matcher.find()) {
									totalDisk = totalDisk + Integer.parseInt(matcher.group(2).trim());
									freeDisk = freeDisk + Integer.parseInt(matcher.group(4).trim());
								}
							}
							totalDisk = totalDisk == null ? 0 : (totalDisk/1024);
							freeDisk = freeDisk == null ? 0 : (freeDisk/1024);
							virtMachRow.set("disk_space", totalDisk);
							virtMachRow.set("disk_available_space", freeDisk);
							
							//更新数据到数据库表
							agentService.insertOrUpdateVirtual(virtMachRow);
							//更新服务器配置信息表
							DataRow serverRow = new DataRow();
							serverRow.set("id", hmcId);
							serverRow.set("os_type", osInfo);
							serverRow.set("name", vmName);
							agentService.updateServerLoginInfo(serverRow);
							logger.info("It's success to update the virtual machine info !");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
