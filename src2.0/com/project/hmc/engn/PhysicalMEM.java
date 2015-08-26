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

public class PhysicalMEM {
	AgentService agent = new AgentService();
	private Logger log = Logger.getLogger(this.getClass());
//	HmcInstructions hs = new HmcInstructions();
	
	/**
	 * list内容：物理机ID,物理机名称,采集类型,时间,物理机程序所占内存的百分比,时间间隔(秒)
	 * list{host_computer_id,name,summ_type,time,mem_sys_prct,interlva_len}
	 */
	public void getResult(String marker){
		List<DataRow> rows = agent.getHpNameAndID();
		List<DataRow> list = new ArrayList<DataRow>();
		if(rows!=null && rows.size()>0){
			for (DataRow dataRow : rows) {
				String com = "lslparutil -r sys -m "+dataRow.getString("name").trim()+" -n 2";
				Session session  = null;
				BufferedReader br = null;
				DataRow loginInfo = agent.getHMCLoginInfo(dataRow.getInt("hmc_id"));
				HmcBase base = new HmcBase(loginInfo.getString("ip_address"), 22, loginInfo.getString("user"), new AES(loginInfo.getString("id")).decrypt(loginInfo.getString("password"),"UTF-8"));
				try {
					session = base.openConn();
					
					if(session!=null){
						log.info("开始执行:"+com);
						session.execCommand(com);
						session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
						InputStream stdout = new StreamGobbler(session.getStdout());
						br = new BufferedReader(new InputStreamReader(stdout));
						String lineToRead = "";
						String regEx = "([\\w]+)=(([0-9]{1,2}/[0-9]{1,2}/[\\d]{4} [0-9]{1,2}:[0-9]{1,2}:[0-9]{2})|([\\d]+)|([\\w]+))";
						int count =0;
						double tempConfigurableSysMem=0d;
						double tempCurrAvailSysMem=0d;
						while((lineToRead=br.readLine())!=null && !lineToRead.trim().equals("No results were found.")){
							DataRow row = new DataRow();
							row.set("computer_id", dataRow.getString("host_computer_id"));
							row.set("computer_name", dataRow.getString("name"));
							row.set("interlva_len", WebConstants.interval);
							row.set("perf_marker", marker);
							row.set("summ_type", 1);    //类型(1为即时信息，2为小时统计，3为天统计)
							Matcher m = Pattern.compile(regEx).matcher(lineToRead);  
							double configurable_sys_mem = 0d;
							double curr_avail_sys_mem = 0d;
							double memSys = 0d;
							++count;
							while(m.find()){
								String key = m.group(1).trim();
								String value = m.group(2).trim();
								if(key.equals("time")){
									Date time = new SimpleDateFormat("MM/dd/yyyy HH:ss:mm").parse(value);
									row.set("sample_time", new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(time));
								}else if(key.equals("configurable_sys_mem")){
									configurable_sys_mem = Double.parseDouble(value);
								}else if(key.equals("curr_avail_sys_mem")){
									curr_avail_sys_mem = Double.parseDouble(value);
								}
							}
							if(count==1){
								tempConfigurableSysMem=configurable_sys_mem;
								tempCurrAvailSysMem =curr_avail_sys_mem;
								continue;
							}
							
							if(configurable_sys_mem!=0){
								double subConfigurableSysMem=configurable_sys_mem-tempConfigurableSysMem;
								double subCurrAvailSysMem=curr_avail_sys_mem-tempCurrAvailSysMem;
								memSys = Double.parseDouble(new DecimalFormat("0.00").format(subConfigurableSysMem==0?0:subCurrAvailSysMem/subConfigurableSysMem*10000));
								row.set("mem_used_prct", memSys);
							}else{
								row.set("mem_used_prct", memSys);
							}
							list.add(row);
						}
						log.info("执行完成:"+com);
						//添加性能信息
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
		PhysicalMEM m = new PhysicalMEM();
//		m.getResult();
	}
}
