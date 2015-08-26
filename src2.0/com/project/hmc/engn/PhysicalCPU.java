package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.AES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;
import com.project.web.WebConstants;

public class PhysicalCPU {
	AgentService agent = new AgentService();
	private Logger log = Logger.getLogger(this.getClass());
//	HmcInstructions hs = new HmcInstructions();
	
	/**
	 * list内容：物理机ID,物理机名称,采集类型,时间,物理机CPU繁忙百分比,时间间隔(秒)
	 * list{host_computer_id,name,summ_type,time,cpu_busy_prct,interlva_len}
	 */
	public void getResult(String marker){
		List<DataRow> rows = agent.getHpNameAndID();
		List<DataRow> list = new ArrayList<DataRow>();
		if(rows!=null && rows.size()>0){
			for (DataRow dataRow : rows) {
				String com = "lslparutil -r procpool -m "+dataRow.getString("name").trim()+" -n 2";
				Session session = null;
				BufferedReader br = null;
				DataRow loginInfo = agent.getHMCLoginInfo(dataRow.getInt("hmc_id"));
				HmcBase base = new HmcBase(loginInfo.getString("ip_address"), 22, loginInfo.getString("user"), new AES(loginInfo.getString("id")).decrypt(loginInfo.getString("password"),"UTF-8"));
				try {
					session = base.openConn();   //打开链接
					if(session!=null){
						log.info("开始执行:"+com);
						session.execCommand(com);    //执行命令
						session.waitForCondition(ChannelCondition.TIMEOUT, 10000);   //设置最高等待时间
						InputStream stdout = new StreamGobbler(session.getStdout());   //得到输入流
						br = new BufferedReader(new InputStreamReader(stdout));
						String lineToRead = "";
						String regEx = "([\\w]+)=(([0-9]{1,2}/[0-9]{1,2}/[\\d]{4} [0-9]{1,2}:[0-9]{1,2}:[0-9]{2})|([\\d]+)|([\\w]+))";
						int count=0;
						double tempTotalPoolCycles= 0D;
						double tempUtilizedPoolCycles=0D;
						while((lineToRead=br.readLine())!=null && !lineToRead.trim().equals("No results were found.")){
							DataRow row = new DataRow();
							row.set("computer_id", dataRow.getString("host_computer_id"));
							row.set("computer_name", dataRow.getString("name"));
							row.set("interval_len", WebConstants.interval);
							row.set("perf_marker", marker);
							row.set("summ_type", 1);    //类型(1为即时信息，2为小时统计，3为天统计)
							Matcher m = Pattern.compile(regEx).matcher(lineToRead);  
							double total_pool_cycles = 0D;
							double utilized_pool_cycles = 0D;
							double cpuBusy = 0d;
							
							++count;
							while(m.find()){
								String key = m.group(1).trim();
								String value = m.group(2).trim();
								if(key.equals("time")){
									Date time = new SimpleDateFormat("MM/dd/yyyy HH:ss:mm").parse(value);
									row.set("sample_time", new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(time));
								}else if(key.equals("total_pool_cycles")){
									total_pool_cycles = Double.parseDouble(value.trim());
								}else if(key.equals("utilized_pool_cycles")){
									utilized_pool_cycles = Double.parseDouble(value.trim());
								}
							}
							if(count==1){
								tempTotalPoolCycles=total_pool_cycles;
								tempUtilizedPoolCycles=utilized_pool_cycles;
								continue;
							}
							if(total_pool_cycles!=0){
								double subTotalPoolCycles = total_pool_cycles - tempTotalPoolCycles;
								double subUtilizedPoolCycles = utilized_pool_cycles - tempUtilizedPoolCycles;
								
								cpuBusy = Double.parseDouble(new DecimalFormat("0.00").format(subTotalPoolCycles==0?0:subUtilizedPoolCycles/subTotalPoolCycles));
								row.set("cpu_busy_prct", cpuBusy*10000);
							}else{
								row.set("cpu_busy_prct", cpuBusy);
							}
							row.set("cpu_idle_prct", (1d-cpuBusy)*10000);
							list.add(row);
						}
						log.info("执行完成:"+com);
						agent.batchInsertPrf(list);
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
					base.closeConn();
				}
			}  
		}
	}
	
	public static void main(String[] args) {
//		String str="time=12/12/2013 16:23:25,event_type=sample,resource_type=lpar,sys_time=12/12/2013 16:23:11,time_cycles=10166426530034943,lpar_name=gdst-ls,lpar_id=6,curr_proc_mode=shared,curr_proc_units=1.0,curr_procs=2,curr_sharing_mode=uncap,curr_uncap_weight=128,curr_shared_proc_pool_name=DefaultPool,curr_shared_proc_pool_id=0,curr_5250_cpw_percent=0.0,mem_mode=ded,curr_mem=8192,entitled_cycles=7924841577630,capped_cycles=114212174310,uncapped_cycles=53160395358,shared_cycles_while_active=0,idle_cycles=65181421320,run_latch_instructions=532288684852,run_latch_cycles=883145578939";
//		String regEx = "([\\w]+)=(([0-9]{1,2}/[0-9]{1,2}/[\\d]{4} [0-9]{1,2}:[0-9]{1,2}:[0-9]{2})|([\\d]+)|([\\w]+))";
//		Matcher m = Pattern.compile(regEx).matcher(str);  
//		while(m.find()){
//			System.out.println("key:"+m.group(1)+"\t Value:"+m.group(2));
//		}
		
//		String time = "12/12/2013 17:04:22";
//		try {
//			Date date = new Date(new SimpleDateFormat("MM/dd/yyyy HH:ss:mm").parse(time).getTime());
//			String tis = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(date);
//			System.out.println(tis);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		PhysicalCPU c = new PhysicalCPU();
	}
}
