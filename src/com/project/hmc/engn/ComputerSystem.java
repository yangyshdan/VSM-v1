package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
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

public class ComputerSystem {
	private Logger log = Logger.getLogger(ComputerSystem.class);
	HmcBase base = null;
	AgentService agent = new AgentService();
	PhysicalMAC phy = new PhysicalMAC();
	int i = 0;
	
	public void getResult(){
		final String com = "lssyscfg -r sys -m ";    //hmc
		final String com2 = "lshwres -r mem -m ";  //hmc
		final String com3 = "lshwres -r proc -m ";  //hmc
		List<DataRow> list = new ArrayList<DataRow>();  //结果集
		List<DataRow> list1= agent.getHpNameAndID();   //数据库原有数据
		try {
			List<DataRow> hmc = agent.getHMCLoginInfo();
			if(hmc!=null && hmc.size()>0){
				for (DataRow dRow : hmc) {
					base = new HmcBase(dRow.getString("ip_address"), 22, dRow.getString("user"), new DES().decrypt(dRow.getString("password")));
					List<DataRow> rows = phy.getResult(dRow);  //物理机集
					for (DataRow dataRow : rows) {
						Session sess1 = null;
						DataRow row = new DataRow();
						row.set("hmc_id", dRow.getString("id"));
						if(sess1==null){
							BufferedReader br = null;
							try {
								sess1 = base.openConn();
								log.info("执行执行:"+com+dataRow.getString("name"));
								sess1.execCommand(com+dataRow.getString("name"));
								sess1.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout = new StreamGobbler(sess1.getStdout());
								br = new BufferedReader(new InputStreamReader(stdout));
								String lineToRead = "";
								while((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")){
									row.set("type", "hypervisor");
									row.set("hmc_id", dRow.getString("id"));
									
									Matcher name = Pattern.compile("name=([^=,]*)").matcher(lineToRead);
									Matcher ip = Pattern.compile("ipaddr=([^=,]*)").matcher(lineToRead);
									Matcher model = Pattern.compile("type_model=([^=,]*)").matcher(lineToRead);
									Matcher status = Pattern.compile("primary_state=([^=,]*)").matcher(lineToRead);
									Matcher version = Pattern.compile("service_lpar_name=([^=,]*)").matcher(lineToRead);
									if(name.find()){
										row.set("name", name.group(1));
										row.set("serial_num", dataRow.getString("name"));
										row.set("display_name", name.group(1).trim());
									}
									if(ip.find()){
										row.set("ip_address", ip.group(1).trim());
									}
									if(model.find()){
										row.set("model", model.group(1).trim());
									}
									if(status.find()){
										row.set("operational_status", status.group(1).trim());
									}
									if(version.find()){
										row.set("os_version", version.group(1).trim());
									}
								}
								log.info("执行完成:"+com+dataRow.getString("name"));
							} catch (Exception e) {
								dRow.set("state", 0);
								log.info("非法主机!");
								e.printStackTrace();
							}finally{
								if(sess1!=null){
									sess1.close();
								}
								if(br!=null){
									br.close();
								}
							}
						}
						
						if(i==0){
							
							
							
							
							
						
							//配置性能采集
							Session sessStart= null;
							if(sessStart==null){
								try {
									sessStart = base.openConn();
									log.info("执行执行:chlparutil -r config -s 300000 ");
									sessStart.execCommand("chlparutil -r config -s 300000 ");
									log.info("执行完成:chlparutil -r config -s 300000 ");
								} catch (Exception e) {
									e.printStackTrace();
								}finally{
									if(sessStart!=null){
										sessStart.close();
									}
								}
							}
						}
						//可以忽视
						i++;
						if(i>999){
							i=1;
						}
						//mem
						Session sessMEM= null;
						if(sessMEM==null){
							BufferedReader br = null;
							try {
								sessMEM = base.openConn();
								log.info("执行执行:"+com2+dataRow.getString("name")+" --level sys");
								sessMEM.execCommand(com2+dataRow.getString("name")+" --level sys");
								sessMEM.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout = new StreamGobbler(sessMEM.getStdout());
								br = new BufferedReader(new InputStreamReader(stdout));
								String lineToRead = "";
								while((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")){
									//内存总数
									Matcher ramSize = Pattern.compile("installed_sys_mem=([^=,]*)").matcher(lineToRead);
									if(ramSize.find()){
										row.set("ram_size", ramSize.group(1).trim());
									}
									//可分配内存总数
									Matcher available_mem = Pattern.compile("curr_avail_sys_mem=([^=,]*)").matcher(lineToRead);
									if(available_mem.find()){
										row.set("available_mem", available_mem.group(1).trim());
									}
								}
								log.info("执行完成:"+com2+dataRow.getString("name")+" --level sys");
							} catch (Exception e) {
								dRow.set("state", 0);
								log.info("非法主机!");
								e.printStackTrace();
							}finally{
								if(sessMEM!=null){
									sessMEM.close();
								}
								if(br!=null){
									br.close();
								}
							}
						}
						
						//cpu
						Session sessCPU= null;
						if(sessCPU==null){
							BufferedReader br = null;
							try {
								sessCPU = base.openConn();
								log.info("执行执行:"+com3+dataRow.getString("name")+" --level sys");
								sessCPU.execCommand(com3+dataRow.getString("name")+" --level sys");
								sessCPU.waitForCondition(ChannelCondition.TIMEOUT, 10000);
								InputStream stdout = new StreamGobbler(sessCPU.getStdout());
								br = new BufferedReader(new InputStreamReader(stdout));
								String lineToRead = "";
								while((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")){
									//CPU总数
									Matcher cpuCount = Pattern.compile("configurable_sys_proc_units=([^=,]*)").matcher(lineToRead);
									if(cpuCount.find()){
										double cpu = Double.parseDouble(cpuCount.group(1).trim());
										row.set("processor_count", Integer.parseInt(new DecimalFormat("0").format(cpu)));
									}
									Matcher cpuAvai = Pattern.compile("curr_avail_sys_proc_units=([^=,]*)").matcher(lineToRead);
									if(cpuAvai.find()){
										double avai_cpu = Double.parseDouble(cpuAvai.group(1).trim());
										row.set("available_cpu", Integer.parseInt(new DecimalFormat("0").format(avai_cpu)));
									}
								}
								log.info("执行完成:"+com3+dataRow.getString("name")+" --level sys");
							} catch (Exception e) {
								dRow.set("state", 0);
								log.info("非法主机!");
								e.printStackTrace();
							}finally{
								if(sessMEM!=null){
									sessMEM.close();
								}
								if(br!=null){
									br.close();
								}
							}
						}
						//添加
						list.add(row);
					}	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(base!=null){
				base.closeConn();
			}
		}
		
		//处理数据
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
	

}
