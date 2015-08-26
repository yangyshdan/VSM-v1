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
import com.huiming.service.virtualPlat.VirtualPlatService;
import com.huiming.sr.constants.SrContant;
import com.project.hmc.core.HmcBase;
import com.project.web.WebConstants;
import com.project.x86monitor.DataCollectConfig;
import com.project.x86monitor.DeviceInfo;
import com.project.x86monitor.MyUtilities;

import csharpwmi.CSharpWMIClass;
import csharpwmi.ICSharpWMIClass;

public class ComputerSystem {
	private Logger log = Logger.getLogger(ComputerSystem.class);
	HmcBase base = null;
	HmcBase base2 = null;
	AgentService agent = new AgentService();
	PhysicalMAC phy = new PhysicalMAC();
	
	/**
	 * 针对Power架构类型,获取物理机和虚拟机配置信息
	 */
	public void getResult(){
		//得到物理机详细信息
		Session sess1 = null;
		final String com = "smcli lssys -l";    
		//得到CPU和内存信息
		Session sess2 = null;
		final String com2 = "smcli lsvrtsys -l";
		BufferedReader br = null;
		BufferedReader br2 = null;
		List<DataRow> list = new ArrayList<DataRow>();
		//数据库原有数据
		List<DataRow> list1 = agent.getPhysicalList(WebConstants.OSTYPE_LINUX);   
		try {
			List<DataRow> hmc = agent.getServerLoginInfo(WebConstants.OSTYPE_LINUX, SrContant.SUBDEVTYPE_PHYSICAL);
			if (hmc != null && hmc.size() > 0) {
				for (DataRow dRow : hmc) {
					base = new HmcBase(dRow.getString("ip_address"), 22, dRow.getString("user"), new AES(dRow.getString("id")).decrypt(dRow.getString("password"),"UTF-8"));
					sess1 = base.openConn();
					if (sess1 != null) {
						log.info("执行执行:" + com);
						try {
							sess1.execCommand(com);
							sess1.waitForCondition(ChannelCondition.TIMEOUT, 10000);
							InputStream stdout = new StreamGobbler(sess1.getStdout());
							br = new BufferedReader(new InputStreamReader(stdout));
							String lineToRead = "";
							List<DataRow> rows = phy.getResult(dRow);    //物理机集
							while ((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")) {
								if (rows != null && rows.size() > 0) {
									for (DataRow dataRow : rows) {
										String regEx = "^"+dataRow.getString("name").trim()+":[\\s]*$";
										if(Pattern.compile(regEx).matcher(lineToRead).find()){
											//得到物理机名称
											String[] name = lineToRead.split(":");   
											DataRow row = new DataRow();
											row.set("name", name[0].trim());
											row.set("type", "hypervisor");
											row.set("hmc_id", dRow.getString("id"));
											String line = "";
											//匹配物理机属性
											String regEx2 = "^([\\s]+[\\w]+):([\\s]*[\\w\\W]+)$";
											while((line = br.readLine())!=null && !line.trim().equals("No results were found.")){
												Matcher m = Pattern.compile(regEx2).matcher(line);
												while(m.find()){
													if(m.group(1).trim().equals("DisplayName")){
														row.set("display_name", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("SystemBoardUUID")){
														row.set("uid", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("CurrentTimeZone")){
														row.set("time_zone", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("IPv4Address")){
														String ipReg = "([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})";
														Matcher mip = Pattern.compile(ipReg).matcher(m.group(2).trim());
														String ipStr = "";
														int i = 0;
														while(mip.find()){
															if(i>0){
																ipStr+=",";
															}
															ipStr+=mip.group(1);
															i++;
														}
														row.set("ip_address", ipStr);
													}
													else if(m.group(1).trim().equals("OperatingState")){
														row.set("operational_status", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("Manufacturer")){
														row.set("vendor", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("Model")){
														row.set("model", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("MachineType")){
														row.set("type", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("Architecture")){
														row.set("cpu_architecture", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("Virtual")){
														row.set("is_virtual", m.group(2).trim());
													}
													else if(m.group(1).trim().equals("InstalledOSDisplayName")){
														
														row.set("os_version", m.group(2).trim());
													}
												}
												//当前物理机结束
												if(Pattern.compile("^[\\s]+ChassisTypeDescription:[\\w\\W]+$").matcher(line).find()){
													list.add(row);
													break;
												}
											}
										}
									}
								}
							}
							log.info("执行完成:"+com);
						} catch (IOException e) {
							dRow.set("state", 0);
							log.info("非法主机!");
							e.printStackTrace();
						}
					}
					
					base2 = new HmcBase(dRow.getString("ip_address"), 22, dRow.getString("user"), new AES(dRow.getString("id")).decrypt(dRow.getString("password"),"UTF-8"));
					sess2 = base2.openConn();
					if(sess2!=null){
						log.info("开始执行:"+com2);
						try {
							sess2.execCommand(com2);
							sess2.waitForCondition(ChannelCondition.TIMEOUT, 10000);
							InputStream stdout2 = new StreamGobbler(sess2.getStdout());
							br2 = new BufferedReader(new InputStreamReader(stdout2));
							String line2 = "";
							while((line2=br2.readLine())!=null){
								for(int i=0;i<list.size();i++){
									String regEx = "^"+list.get(i).getString("name").trim()+":[\\s]*$";
									if(Pattern.compile(regEx).matcher(line2).find()){
										String regEx2 = "^[\\w\\W]+:[\\w\\W]+$";
										String line3="";
										while((line3 = br2.readLine())!=null && !line3.trim().equals("No results were found.")){
											if(Pattern.compile(regEx2).matcher(line3).find()){
												String[] args = line3.split(":");
												if(args.length==2){
													if(args[0].trim().equals("Physical CPU Count")){
														list.get(i).set("processor_count", args[1].trim());
													}else if(args[0].trim().equals("Available System Physical Processors")){
														list.get(i).set("available_cpu", args[1].trim());
													}else if(args[0].trim().equals("Installed Memory Size (MB)")){
														list.get(i).set("ram_size", args[1].trim());
													}else if(args[0].trim().equals("Available Memory (MB)")){
														list.get(i).set("available_mem", args[1].trim());
													}
												}
												if(Pattern.compile("^[\\s]+Suspend Resume Capable :[\\w\\W]+$").matcher(line3).find()){
													break;
												}
											}
										}
									}
								}
							}
							log.info("执行完成:"+com2);
						} catch (IOException e) {
							dRow.set("state", 0);
							e.printStackTrace();
							log.info("非法主机!");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(br2!=null){
				try {
					br2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(sess1!=null){
				sess1.close();
			}
			if(sess2!=null){
				sess2.close();
			}
			if(base!=null){
				base.closeConn();
			}
			if(base2!=null){
				base2.closeConn();
			}
		}
		
		List<DataRow> addlist = new ArrayList<DataRow>();
		List<DataRow> updatelist = new ArrayList<DataRow>();
		if(list!=null && list.size()>0){
			for (DataRow dataRow : list) {
				if(list1!=null && list1.size()>0){
					for (int i=0;i<list1.size();i++) {
						if(list1.get(i).getString("name").equals(dataRow.getString("name"))){
							dataRow.set("hypervisor_id", list1.get(i).getString("hypervisor_id"));
							dataRow.set("host_computer_id", list1.get(i).getString("host_computer_id"));
							updatelist.add(dataRow);     //需要更新物理机集合
							list1.remove(i);             //去除存在的物理机
							break;
						}
						if(i==list1.size()-1){   
							addlist.add(dataRow);        //需要添加的物理机集合
						}
					}
				}else{
					addlist.add(dataRow);        //需要添加的物理机集合
				}
			}
			agent.batchUpdateHp(updatelist);   //更新物理机
			agent.batchInsertHp(addlist);         //添加物理机
			if(list1!=null && list1.size()>0){
				agent.batchUpateHpState(list1);   //更新删除的物理机
			}
		}
	}
	
	/**
	 * 获取物理机和物理机下的虚拟机的配置信息
	 */
	public void getPhysicalAndVirtualConfigInfo(String physicalHmcId) {
		HmcBase hmcBase = null;
		try {
			//数据库中存在的物理机数据
			List<DataRow> physicExistsList = agent.getPhysicalList();
			//物理机配置信息
			List<DataRow> serverConfigList = new ArrayList<DataRow>();
			if (StringHelper.isNotBlank(physicalHmcId) && StringHelper.isNotEmpty(physicalHmcId)) {
				serverConfigList.add(agent.getHMCLoginInfo(Integer.parseInt(physicalHmcId)));
			} else {
				serverConfigList = agent.getPhysicalConfigInfo();
			}
			Matcher matcher = null;
			Session session = null;
			if (serverConfigList.size() > 0) {
				for (int i = 0; i < serverConfigList.size(); i++) {
					DataRow configRow = serverConfigList.get(i);
					String configId = configRow.getString("id");
					String ipAddress = configRow.getString("ip_address");
					String userName = configRow.getString("user");
					String password = new AES(configId).decrypt(configRow.getString("password"),"UTF-8");
					String osType = configRow.getString("os_type");
					String virtPlatType = configRow.getString("virt_plat_type").trim();
					
					//创建物理机记录Row
					DataRow physicalRow = null;
					
					//For LINUX OS
					if (osType.equals(WebConstants.OSTYPE_LINUX)) {
						physicalRow = new DataRow();
						//初始化连接参数
						hmcBase = new HmcBase(ipAddress, 22, userName, password);
						//建立连接
						session = hmcBase.openConn();
						//连接不成功
						if (session != null) {
							physicalRow.set("detectable", 1);
						} else {
							physicalRow.set("detectable", 0);
						}
						
						//获取系统信息
						String osInfo = hmcBase.executeCommand("uname -a").get(0);
						String[] osList = osInfo.split(" ");
						physicalRow.set("name", osList[1]);
						
						//获取操作系统发行版本信息
						List<String> osVersionInfo = hmcBase.executeCommand("head -n 1 /etc/issue");
						if (osVersionInfo != null && osVersionInfo.size() > 0) {
							physicalRow.set("os_version", osVersionInfo.get(0));
						}
						
						//获取CPU信息
						List<String> cpuInfo = hmcBase.executeCommand("cat /proc/cpuinfo | grep name | cut -f2 -d: | uniq -c");
						String cpuRegExp = "^([\\s]+[\\w]+)([\\s]+[\\w\\W]+)([\\s]+[\\w\\W]+)([\\s]+[@]{1})([\\s]+[\\w\\W]+)$";
						matcher = Pattern.compile(cpuRegExp).matcher(cpuInfo.get(0));
						if (matcher.find()) {
							physicalRow.set("processor_count", matcher.group(1).trim());
							physicalRow.set("available_cpu", matcher.group(1).trim());
							String cpuSpeedStr = matcher.group(5).trim();
							String cpuSpeedVal = cpuSpeedStr.substring(0, cpuSpeedStr.indexOf("G"));
							Integer cpuSpeed = (int)(Double.parseDouble(cpuSpeedVal)*1000);
							physicalRow.set("processor_speed", cpuSpeed);
						}
						
						//获取内存信息
						List<String> memInfo = hmcBase.executeCommand("cat /proc/meminfo");
						//MemTotal: 1231231 KB
						String totalMemStr = memInfo.get(0).split(":")[1].trim();
						//MemFree: 343434 KB
						String freeMemStr = memInfo.get(1).split(":")[1].trim();
						int totalMem = Integer.parseInt(totalMemStr.substring(0, totalMemStr.indexOf("kB")).trim())/1024;
						int freeMem = Integer.parseInt(freeMemStr.substring(0, freeMemStr.indexOf("kB")).trim())/1024;
						physicalRow.set("ram_size", totalMem);
						physicalRow.set("available_mem", freeMem);
						
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
						physicalRow.set("disk_space", totalDisk);
						physicalRow.set("disk_available_space", freeDisk);
					//For ESXi Server
					} else if (osType.equals(WebConstants.OSTYPE_ESXI)) {
						LibvirtEngine engine = new LibvirtEngine(WebConstants.VIRT_PLAT_TYPE_VMWARE, ipAddress, userName, password);
						physicalRow = engine.getHostInfo();
					}
					physicalRow.set("hmc_id", configId);
					physicalRow.set("ip_address", ipAddress);
					physicalRow.set("type", SrContant.SUBDEVTYPE_PHYSICAL);
					physicalRow.set("vendor", configRow.getString("vendor"));
					physicalRow.set("model", configRow.getString("model"));
					physicalRow.set("operational_status", "NORMAL");
					
					//判断记录是否存在,存在则只需要更新,否则添加
					if (physicExistsList.size() > 0) {
						for (int j = 0; j < physicExistsList.size(); j++) {
							if (physicExistsList.get(j).getString("hmc_id").equals(physicalRow.getString("hmc_id"))){
								physicalRow.set("hypervisor_id", physicExistsList.get(j).getString("hypervisor_id"));
								physicalRow.set("host_computer_id", physicExistsList.get(j).getString("host_computer_id"));
								break;
							}
						}
					}
					
					//将数据插入或更新到数据库表(t_res_computersystem,t_res_hypervisor)
					Integer hypervisorId = Integer.parseInt(agent.insertOrUpdateHypervisor(physicalRow));
					//更新服务器配置信息中对应的物理机配置信息(t_server)
					DataRow serverRow = new DataRow();
					serverRow.set("id", configId);
					serverRow.set("name", physicalRow.getString("name"));
					agent.updateServerLoginInfo(serverRow);
					
					//使用LIBVIRT获取该物理机下的所有虚拟机
					if ((osType.equals(WebConstants.OSTYPE_LINUX) && virtPlatType.equals(WebConstants.VIRT_PLAT_TYPE_KVM))
							|| (osType.equals(WebConstants.OSTYPE_ESXI) && virtPlatType.equals(WebConstants.VIRT_PLAT_TYPE_VMWARE))) {
						//数据库原有虚拟机数据
						List<DataRow> existsVmList = agent.getVirtualNameAndId(hypervisorId);
						//建立连接
						LibvirtEngine libvirtEngine = new LibvirtEngine(virtPlatType, ipAddress, userName, password);
						//获取虚拟化平台信息(HYPERVISOR)
						DataRow hyperRow = libvirtEngine.getHypervisorInfo();
						hyperRow.set("hypervisor_id", hypervisorId);
						//保存虚拟化平台HYPERVISOR信息
						VirtualPlatService virtualPlatService = new VirtualPlatService();
						virtualPlatService.saveVirtualPlatInfo(hyperRow);
						
						//获取虚拟机数据
						List<DataRow> newVmList = libvirtEngine.getDomainInfoList();
						//将数据插入或更新到数据库表(t_res_computersystem,t_res_virtualmachine)
						for (int j = 0; j < newVmList.size(); j++) {
							newVmList.get(j).set("hypervisor_id", hypervisorId);
							if (existsVmList.size() > 0) {
								for (int k = 0; k < existsVmList.size(); k++) {
									DataRow virtualRow = existsVmList.get(k);
									if (newVmList.get(j).getString("name").equals(virtualRow.getString("name"))
											&& newVmList.get(j).getString("hypervisor_id").equals(virtualRow.getString("hypervisor_id"))) {
										newVmList.get(j).set("vm_id", virtualRow.getString("vm_id"));
										newVmList.get(j).set("computer_id", virtualRow.getString("computer_id"));
										break;
									}
								}
							}
						}
						agent.batchInsertVirtual(newVmList);
						log.info("It's success to insert or update the physical machine and virtual machine config data !");
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Main()
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ComputerSystem cs = new ComputerSystem();
			cs.getPhysicalAndVirtualConfigInfo(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
