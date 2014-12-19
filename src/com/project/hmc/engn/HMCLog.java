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
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;

public class HMCLog {
//	HmcBaseService bs = new HmcBaseService();
	private Logger log = Logger.getLogger(this.getClass());
	HmcBase base = null;
	
	//得到一个小时的硬件日志命令
	private final String com = "lssvcevents -t hardware -i 60 ";     //hmc
	String lineToRead = "";
	String regEx = "([^=,]+)=([^=,]+)";
	public List<DataRow> getResult(DataRow dRow){
		BufferedReader br = null;
		List<DataRow> list = new ArrayList<DataRow>();
		base = new HmcBase(dRow.getString("ip_address"), 22, dRow.getString("user"),  new DES().decrypt(dRow.getString("password")));
		Session session = null;
		try {
			session = base.openConn();
			if(session!=null){
				log.info("开始执行:"+com);
				session.execCommand(com);
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				InputStream stdout = new StreamGobbler(session.getStdout());
				br = new BufferedReader(new InputStreamReader(stdout));
				while ((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")) {
					DataRow log = new DataRow();
					Matcher m = Pattern.compile(regEx).matcher(lineToRead);
					while (m.find()) {
						if(m.group(1).trim().equals("problem_num")){
							log.set("fno", m.group(2).trim());
						}else if(m.group(1).trim().equals("refcode")){
							log.set("fruleid", m.group(2).trim());
						}else if(m.group(1).trim().equals("first_time")){
							log.set("ffirsttime", m.group(2).trim());
						}else if(m.group(1).trim().equals("last_time")){
							log.set("flasttime", m.group(2).trim());
						}else if(m.group(1).trim().equals("text")){
							log.set("fdetail", m.group(2).trim());
						}else if(m.group(1).trim().equals("failing_mtms")){
							log.set("fresourcename", m.group(2).trim().replace("/", "*"));
						}
						
						log.set("flogtype", 3);
						log.set("fresourcetype", "Physical");
						log.set("ftoptype", "Physical");
						log.set("fcount", 1);
						log.set("flevel", 1);
						log.set("fstate", 0);
						log.set("fisforward", 0);
						log.set("fsourcetype", "HMC");
						DataRow temp = new AgentService().getHyperVInfo(log.getString("serial_num"));
						if(temp != null){
							log.set("ftopid", temp.getString("id"));
							log.set("fresourceid", temp.getString("id"));
							log.set("ftopname", temp.getString("name"));
							log.set("fresourcename", temp.getString("name"));
							list.add(log);
						}
					}
				}
				log.info("physical log:"+list);
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
				log.info("执行完成:"+com);
				session.close();
			}
			if(base!=null){
				base.closeConn();
			}
		}
		return list;
	}
	
}
