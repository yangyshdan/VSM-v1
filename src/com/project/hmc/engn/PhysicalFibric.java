package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;

public class PhysicalFibric {
	private Logger log = Logger.getLogger(PhysicalFibric.class);
	HmcBase base = null;
	AgentService agent = new AgentService();
	PhysicalMAC phy = new PhysicalMAC();
	
	public Map<String, List<DataRow>> getResult(){
		final String comHead = "lshwres -r io --rsubtype slotchildren -m ";    //hmc
		final String comFoot = " -F phys_loc,wwpn,wwnn,description ";    //hmc
		
		Map<String, List<DataRow>> map = new HashMap<String, List<DataRow>>();
		List<DataRow> hmcs = agent.getHMCLoginInfo();
		if(hmcs != null && hmcs.size() > 0){
			for (DataRow hmc : hmcs) {
				base = new HmcBase(hmc.getString("ip_address"), 22, hmc.getString("user"),  new DES().decrypt(hmc.getString("password")));
				List<DataRow> rows = phy.getResult(hmc);  //物理机集
				for (DataRow dataRow : rows) {
					List<DataRow> list = new ArrayList<DataRow>();
					Session session = null;
					BufferedReader br = null;
					String lineToRead = "";
					try {
						session = base.openConn();
						if(session!=null){
							log.info("执行执行:"+ comHead +dataRow.getString("name") + comFoot);
							session.execCommand(comHead+dataRow.getString("name") + comFoot);
							session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
							InputStream stdout = new StreamGobbler(session.getStdout());
							br = new BufferedReader(new InputStreamReader(stdout));
							while ((lineToRead = br.readLine()) != null && !lineToRead.trim().equals("No results were found.")) {
								if(lineToRead.contains(",fibre-channel")){
									DataRow fibric = new DataRow();
									String[] fibricStr = lineToRead.trim().split(",");
									fibric.set("phys_loc", fibricStr[0]);
									fibric.set("hwres_type", 1);
									fibric.set("wwpn", fibricStr[1]);
									fibric.set("wwnn", fibricStr[2]);
									list.add(fibric);
								}
							}
							map.put(dataRow.getString("name"), list);
							log.info("fibre-list:"+list);
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
							log.info("执行完成:"+comHead +dataRow.getString("name") + comFoot);
							session.close();
						}
						if(base!=null){
							base.closeConn();
						}
					}
				}
			}
		}
		return map;
	}
	
	
	public static void main(String[] args) {

		HmcBase base = new HmcBase("192.168.1.68", 22, "test",  "test\\123");
		Session session = null;
		BufferedReader br = null;	
		try {
				
				String lineToRead = "";
				session = base.openConn();
				if(session!=null){
					session.execCommand("ifconfig");
					session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
					InputStream stdout = new StreamGobbler(session.getStdout());
					br = new BufferedReader(new InputStreamReader(stdout));
					while ((lineToRead = br.readLine()) != null ) {
							System.out.println(lineToRead);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				
				if(session!=null){
					session.close();
				}
				if(base!=null){
					base.closeConn();
				}
			}
	
	}
}
