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
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;

/**
 * 获取虚拟机信息
 * 
 * @author lic
 * 
 */
public class VirtualMacsd {

	private static Logger logger= Logger.getLogger(VirtualMacsd.class);
	
	/**
	 * 获得虚拟机基本信息
	 */
	public void getResult() {
		// 获取所有物理机
		List<DataRow> hypervisorList = agentService.getHpNameAndID();
		// 匹配虚拟机名称
//		Pattern pattern = Pattern.compile("^[\\w\\W]*:\\s*$");
		String regEx2 = "([^=,]+)=([^=,]+)";
//		String regEx2 = "([\\w]+)=(([\\d]+)|([\\w]+))";
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
				// 获取物理机下的虚拟机
				session.execCommand("lssyscfg -r prof -m "+ hypervisor.getString("name"));
				logger.info("开始执行命令：lssyscfg -r prof -m "+ hypervisor.getString("name"));
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				InputStream stdout = new StreamGobbler(session.getStdout());
				br = new BufferedReader(new InputStreamReader(stdout));
				while ((line = br.readLine()) != null && !line.trim().equals("No results were found.")) {
					if (line.isEmpty()) {
						continue;
					}
					DataRow virtualData = new DataRow();
					Matcher matcher = Pattern.compile(regEx2).matcher(line);
					while (matcher.find()) {
						if(matcher.group(1).trim().equals("lpar_name")){
							virtualData.set("name", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("desired_mem")) {// 分配的内存大小
							virtualData.set("total_memory", matcher.group(2).trim());
						}  
						else if (matcher.group(1).trim().equals("min_procs")) {// 最小处理器数量
							virtualData.set("minimum_cpu_number", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("min_proc_units")) {
							if(!StringHelper.isEmpty(matcher.group(2))){
								virtualData.set("minimum_cpu_processunit", Double.valueOf(matcher.group(2).trim()));
							}else{
								virtualData.set("minimum_cpu_processunit", 0);
							}
						} 
						else if(matcher.group(1).trim().equals("proc_mode")){
							virtualData.set("processing_mode", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("max_procs")) {// 最大处理器数量
							virtualData.set("maximum_cpu_number", matcher.group(2).trim());
						} 
						else if (matcher.group(1).trim().equals("max_proc_units")) {
							if(!StringHelper.isEmpty(matcher.group(2))){
								virtualData.set("maximum_cpu_processunit", Double.valueOf(matcher.group(2).trim()));
							}else{
								virtualData.set("maximum_cpu_processunit", 0);
							}
						}
						else if (matcher.group(1).trim().equals("desired_procs")) {// 处理器数量
							virtualData.set("assigned_cpu_number", matcher.group(2).trim());
						}
						else if (matcher.group(1).trim().equals("desired_proc_units")) {
							if(!StringHelper.isEmpty(matcher.group(2))){
								virtualData.set("assigned_cpu_processunit", Double.valueOf(matcher.group(2).trim()));
							}else{
								virtualData.set("assigned_cpu_processunit", 0);
							}
						}  
						else if (matcher.group(1).trim().equals("lpar_env")) {// 支持环境
							if(matcher.group(2).equals("vioserver")){
								virtualData.set("type", "VIOS");
							}else{
								virtualData.set("type", "VOS");
							}
							virtualData.set("targeted_os", matcher.group(2).trim());
						} 
					}
					
					virtualData.set("hypervisor_id", hypervisor.getInt("hypervisor_id"));
					virtualData.set("host_name", hypervisor.getString("name"));
					cmdVirtualMacList.add(virtualData);
				}
				if(cmdVirtualMacList != null && cmdVirtualMacList.size() == 1){
					DataRow temp = agentService.getHpById(hypervisor.getInt("hypervisor_id"));
					if(temp != null){
						cmdVirtualMacList.get(0).set("assigned_cpu_number", temp.getString("processor_count"));
						cmdVirtualMacList.get(0).set("assigned_cpu_processunit", temp.getString("processor_count"));
						cmdVirtualMacList.get(0).set("total_memory", temp.getString("ram_size"));
					}
				}
				session.close();
				logger.info("结束命令：lssyscfg -r prof -m "+ hypervisor.getString("name"));
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

	/**********/
	
	public List<DataRow> getConfigVirtualMacByName(List<DataRow> virtualBasicList,DataRow row) {

//		Pattern pattern = Pattern.compile("^[\\w\\W]*:\\s*$");
//		String regEx2 = "^([\\s]*[\\w\\W]+):([\\w\\W]+)$";//匹配配置信息
//		String regIp = "([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})"; //匹配ip地址
		BufferedReader br = null;
		String lineToRead;
		HmcBase base = new HmcBase(row.getString("ip_address"), 22, row.getString("user"), new DES().decrypt(row.getString("password")));
		for (int n = 0, vbLeng = virtualBasicList.size(); n < vbLeng; n++) {

			DataRow virtualMac = virtualBasicList.get(n);
			try {
				Session session = base.openConn();
				session.execCommand("lssyscfg -r lpar -m "+virtualMac.getString("host_name")+" --filter \"\"lpar_names="+virtualMac.getString("name")+" -F name:state:rmc_ipaddr");
				
				logger.info("开始执行命令：lssyscfg -r lpar -m "+virtualMac.getString("host_name")+" --filter \"\"lpar_names="+virtualMac.getString("name")+"  -F name,state,rmc_ipaddr");
				
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				InputStream stdout = new StreamGobbler(session.getStdout());
				br = new BufferedReader(new InputStreamReader(stdout));
				
				while ((lineToRead = br.readLine()) != null ) {
//					Matcher ip = Pattern.compile("ip:([^=,]*)").matcher(lineToRead);
//					Matcher state = Pattern.compile("state:([^=,]*)").matcher(lineToRead);
//					
//					if(ip.find()){
//						virtualMac.set("ip_address", ip.toString().trim());
//					}
//					if(state.find()){
//						virtualMac.set("operational_status", state.toString().trim());
//						if(state.toString().equals("Running")){
//							virtualMac.set("detectable", 1);
//						}else{
//							virtualMac.set("detectable", 0);
//						}
//					}
					String[] lineArray = lineToRead.split(":");
					if(lineArray.length>0){
						if(lineArray.length == 3){
							virtualMac.set("ip_address", lineArray[2]);
						}
						if(lineArray.length>1){
							if(lineArray[1].equals("Running")){
								virtualMac.set("detectable", 1);
							}else{
								virtualMac.set("detectable", 0);
							}
							virtualMac.set("operational_status", lineArray[1]);
						}
					}
				}
				session.close();
				logger.info("结束命令：lssyscfg -r lpar -m "+virtualMac.getString("host_name")+" --filter \"\"lpar_names="+virtualMac.getString("name")+"  -F name,state,rmc_ipaddr");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
					base.closeConn();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return virtualBasicList;
	}

	
	
	public static void main(String[] args){
//		VirtualMacsd vm = new VirtualMacsd();
//		vm.getResult();
//		String regEx2 = "([^=,]+)=([^=,]+)";
//		String regEx =  "([\\w]+)=(([\\d]+)|([\\w]+))";
//		String s="name=hostname181-asdfasdf,lpar_name=hostnam-e181,lpar_id=10,lpar_env=aix-linux,all_resources=0,min_mem=256,desired_mem=8192,max_mem=9216,min_num_huge_pages=0,desired_num_huge_pages=0,max_num_huge_pages=0,mem_mode=de-d,hpt_ratio=1:64,proc_mode=shared,min_proc_units=0.1,desired_proc_units=0.1,max_proc_units=11.0,min_procs=1,desired_procs=1,max_procs=11,sharing_mode=uncap,uncap_weight=128,shared_proc_pool_id=0,shared_proc_pool_name=DefaultPool,affinity_group_id=none,io_slots=none,lpar_io_pool_ids=none,max_virtual_slots=20,\"virtual_serial_adapters=0//server//1/any//any/1,1/server/1/any//any/1\",virtual_scsi_adapters=2/client/1/VIOS/10/0,\"virtual_eth_adapters=3/0/1//0/0/ETHERNET0/06552b000005/all/none,4/0/1//0/0/ETHERNET0/06552b000006/all/none\",virtual_eth_vsi_profiles=none,vtpm_adapters=none,virtual_fc_adapters=none,hca_adapters=none,boot_mode=norm,conn_monitoring=0,auto_start=0,power_ctrl_lpar_ids=none,work_group_id=none,redundant_err_path_reporting=0,bsr_arrays=0,lpar_proc_compat_mode=POWER7,electronic_err_reporting=null";
//		Matcher matcher = Pattern.compile(regEx2).matcher(s);
//		
//		while(matcher.find()){
//			System.out.println(matcher.group(1));
//			System.out.println(matcher.group(2));
//		}
		String a = "::";
		String[] b = a.split(":");
		System.out.println(b.length);
	} 
	private AgentService agentService = new AgentService();
}
