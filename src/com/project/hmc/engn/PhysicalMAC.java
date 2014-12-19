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

public class PhysicalMAC {
//	HmcBaseService bs = new HmcBaseService();
	private AgentService agent = new AgentService();
	private Logger log = Logger.getLogger(this.getClass());
	HmcBase base = null;
	
	//的得到所有物理机的信息命令
//	private final String com = "smcli lssys --groups \"Blade servers\"";    //fsm
	private final String com = "lssysconn -r all";     //hmc
	String lineToRead = "";
//	String regEx = "^Server-7895-[\\w\\W]+[\\s]*$";   
	String regEx = "type_model_serial_num=([^=,]*)";
	public List<DataRow> getResult(DataRow dRow){
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(new File("D:/output1.txt")));
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
		
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
					Matcher m = Pattern.compile(regEx).matcher(lineToRead);
					if(m.find()){
						DataRow row = new DataRow();
						row.set("name", m.group(1).trim());
						list.add(row);
						log.info("the result:"+lineToRead);
					}
				}
				log.info("physical-list:"+list);
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
	
	public static void main(String[] args) {
		String lineToRead="resource_type=sys,type_model_serial_num=7895-22X*102459B,sp=primary,sp_phys_loc=U78AE.001.WZS017Z-P1,ipaddr=192.168.70.161,alt_ipaddr=unavailable,state=Connected";
		String regEx = "type_model_serial_num=([^=,]*)";
		Matcher m = Pattern.compile(regEx).matcher(lineToRead);
		if(m.find()){
			System.out.println(m.group(1));
		}else{
			System.out.println("not found!");
		}
	}
	
}
