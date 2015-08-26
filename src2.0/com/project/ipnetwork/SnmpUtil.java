package com.project.ipnetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DataRow;
import com.project.ipnetwork.interf.IHandler;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.beans.SnmpBean;

public class SnmpUtil {
	
	public void loadIpAndMac(String cmd, JsonData result) {
		this.handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				
			}
		}, result);
	}
	
	/**
	 * @see 获取端口性能数据
	 * @param cmd
	 * @param result
	 */
	public void loadPortPerfData(String cmd, JsonData result) {
		this.handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				int portConfigSize = ((Integer) result.getValue()).intValue();
				List<SnmpBean> beans = new ArrayList<SnmpBean>(portConfigSize);
				String line;
				SnmpBean bean;
				while(true) {
					line = br.readLine();
					if(line == null) { break; }
//					Logger.getLogger(getClass()).info(line);
					bean = splitH3CLine(line);
					if(bean != null) { beans.add(bean); }
				}
				result.setValue(beans);
			}
		}, result);
	}
	
	public void loadPortConfigData(String cmd, JsonData result) {
		this.handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				int portConfigSize = ((Integer) result.getValue()).intValue();
				List<SnmpBean> beans = new ArrayList<SnmpBean>(portConfigSize);
				String line;
				SnmpBean bean;
				while(true) {
					line = br.readLine();
					if(line == null) { break; }
					bean = splitH3CLine(line);
					if(bean != null) {
						beans.add(bean);
					}
				}
				result.setValue(beans);
			}
		}, result);
	}
	
	public void loadInterface2Port(String cmd, JsonData result) {
		this.handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				Map<Integer, Integer> interf2Port = new TreeMap<Integer, Integer>();
				String line;
				String str;
				while(true) {
					line = br.readLine();
					if(line == null) { break; }
					SnmpBean snmp = splitH3CLine(line);
					if(snmp != null) {
						str = snmp.getOid();
						interf2Port.put(Integer.valueOf(snmp.getDataValue()),
								Integer.valueOf(str.substring(str.lastIndexOf('.') + 1)));
					}
				}
				result.setValue(interf2Port);
			}
		}, result);
	}
	
	/**
	 * @see 加载交换机性能数据
	 * @param cmd
	 * @param result
	 */
	public void loadSwitchPerfData(String cmd, JsonData result) {
		this.handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				DataRow dr = new DataRow();
				int idx = 0;
				String columns[] = {"cpu_busy_percent", "mem_busy_percent", "switch_temperature", "switch_voltage"};
				
				Object obj = splitData(br.readLine(), columns[idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				result.setValue(dr);
			}
		}, result);
	}
	/**
	 * @see 在数据库中设置entity_index一定要按照代码的顺序填写，否则将出错
	 * @see 用于收集交换机配置数据
	 **/
	public void loadSwitchConfigData(String cmd, JsonData result) {
		this.handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				DataRow dr = new DataRow();
				Long l;
				Integer i;
				float size = 1024.0f * 1024;
				int idx = 0;
				
				String columns[] = { "device_runing_day", "switch_vendor", "switch_name", "vendor_location",
						"service_count", "port_count", "serial_number", "ip_address", "netmask",
						"description", "software_version", "hardware_version", "memory_total_size_mb",
						"mac_address", "model", "operational_status", "disk_total_size_mb",
						"disk_available_size_mb", "disk_vendor", "disk_type", "interf_count"};
				Object obj = splitData(br.readLine(), columns[idx]);
				if(obj != null){
					l = (Long)obj;
					dr.set(columns[idx], new Double(l / 100.0D / 60 / 60 / 24).intValue());
				}
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){
					i = (Integer)obj;
					dr.set(columns[idx], new Float(i / size));
				}
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){
					i = (Integer)obj;
					dr.set(columns[idx], getSwitchOperationalStatus(i));
				}
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){
					i = (Integer)obj;
					dr.set(columns[idx], new Float(i / size)); // 5
				}
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){
					i = (Integer)obj;
					dr.set(columns[idx], new Float(i / size));
				}
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				
				obj = splitData(br.readLine(), columns[++idx]);
				if(obj != null){ dr.set(columns[idx], obj); }
				// 还差两个字段
				result.setValue(dr);
			}
		}, result);
	}
	
	/**
	 * @see 针对H3C分解字符串
	 * @param data
	 */
	public SnmpBean splitH3CLine(String line) {
		if(line == null || line.isEmpty()){ return null; }
		char ch[] = line.toCharArray();
		SnmpBean bean = new SnmpBean();
		StringBuilder dataType = new StringBuilder(15);
		StringBuilder dataValue = new StringBuilder(30);
		StringBuilder oid = new StringBuilder(50);
		boolean isString = true, isHex = false;
		for(int i = 0, l = ch.length, j; i < l; ++i) {
			if(ch[i] == '=') {
				boolean isFirst = false;
				for(j = i + 1; ch[j] != ':'; ++j) { // 获得数据类型
					if(ch[j] != ' '){ isFirst = true; }
					if(isFirst) { dataType.append(Character.toLowerCase(ch[j])); }
				}
				++j; // 跳过冒号
				String dt = dataType.toString();
				isHex = dt.equals("hex-string");
				// dt.equals("timeticks") 把timeticks当作毫秒数，这样在将来的计算中，计算得到天数
				isString = dt.equals("string") || dt.equals("ipaddress") || isHex;
				isFirst = false;
				for(; j < l; ++j) {
					if(ch[j] != ' ' && ch[j] != '"'){ isFirst = true; }
					if(isFirst){
						if(isString) {
							dataValue.append(ch[j]);
						}
						else { // 如果是数字型，则要判断该字符是否是数字
							if(Character.isDigit(ch[j])){
								dataValue.append(ch[j]);
							}
							else if(dataValue.length() > 0){ break; }
						}
					}
				}
				char c;
				for(j = dataValue.length() - 1; j >= 0; --j) {
					c = dataValue.charAt(j);
					if(c == ' ' || c == '"'){
						dataValue.deleteCharAt(j);
					}
					else { break; }
				}
				break;
			}
			else {
				oid.append(ch[i]);
			}
		}
		for(int i = oid.length() - 1; i >= 0; --i) {
			if(oid.charAt(i) == ' '){
				oid.deleteCharAt(i);
			}
		}
		bean.setDataValue(dataValue.toString());
		bean.setOid(oid.toString());
		
		String type = dataType.toString();
		if(isString){
			bean.setDataType("string");
		}
		else if(type.equals("integer") || type.equals("gauge32") || type.equals("counter32")) {
			bean.setDataType("integer");
		}
		else { bean.setDataType("long"); }
		return bean;
	}
	
	/**
	 * @see 针对H3C分解字符串
	 * @param data
	 */
	public Object splitData(String data, String column) {
		if(data == null || data.isEmpty()){ return null; }
		char ch[] = data.toCharArray();
		StringBuilder dataType = new StringBuilder(15);
		StringBuilder dataValue = new StringBuilder(30);
		boolean isString = true, isHex = false;
		for(int i = 0, l = ch.length, j; i < l; ++i) {
			if(ch[i] == '=') {
				boolean isFirst = false;
				for(j = i + 1; ch[j] != ':'; ++j) { // 获得数据类型
					if(ch[j] != ' '){ isFirst = true; }
					if(isFirst) { dataType.append(Character.toLowerCase(ch[j])); }
				}
				++j; // 跳过冒号
				String dt = dataType.toString();
				isHex = dt.equals("hex-string");
				// dt.equals("timeticks") 把timeticks当作毫秒数，这样在将来的计算中，计算得到天数
				isString = dt.equals("string") || dt.equals("ipaddress") || isHex;
				isFirst = false;
				for(; j < l; ++j) {
					if(ch[j] != ' ' && ch[j] != '"'){
						isFirst = true;
					}
					if(isFirst){
						if(isString) {
							dataValue.append(ch[j]);
						}
						else { // 如果是数字型，则要判断该字符是否是数字
							if(Character.isDigit(ch[j])){
								dataValue.append(ch[j]);
							}
							else if(dataValue.length() > 0){ break; }
						}
					}
				}
				char c;
				for(j = dataValue.length() - 1; j >= 0; --j) {
					c = dataValue.charAt(j);
					if(c == ' ' || c == '"'){
						dataValue.deleteCharAt(j);
					}
					else { break; }
				}
//				Logger.getLogger(getClass()).info(column + " & " + dataType+ ": " + dataValue);
				break;
			}
		}
		String val = dataValue.toString();
		String type = dataType.toString();
//		return null;
		if(isString){
			if(isHex){ return val.replace(' ', ':'); }
			else { return val; }
		}
		else if(type.equals("integer") || type.equals("gauge32") || type.equals("counter32")) {
			return Integer.parseInt(val);
		}
		else {
			return Long.parseLong(val);
		}
	}

	public void testSnmp(String cmd, JsonData result){
		try {
			handlerWithException(cmd, new IHandler(){
				public void handle(BufferedReader br, JsonData result) throws IOException {
//					Logger.getLogger(getClass()).error("step 2...");
					String line;
					result.setSuccess(true);
					while(true) {
						line = br.readLine();
						if(line == null) { break; }
						if(line.startsWith("Timeout:") || line.contains("error")) {
							result.setSuccess(false);
							StringBuilder err = new StringBuilder(100);
							while(line != null) {
								err.append(line);
								err.append("\r\n");
								line = br.readLine();
							}
							result.setMsg(err.toString());
							break;
						}
					}
				}
			}, result);
		} catch (IOException e) {
			result.setSuccess(false);
			result.setMsg(e.getMessage());
		} catch (InterruptedException e) {
			result.setSuccess(false);
			result.setMsg(e.getMessage());
		}
	}
	
	/**
		Parameter	Command Line Flag	snmp.conf token
		securityName	-u NAME	defSecurityName NAME
		authProtocol	-a (MD5|SHA)	defAuthType (MD5|SHA)
		privProtocol	-x (AES|DES)	defPrivType DES
		authKey	-A PASSPHRASE	defAuthPassphrase PASSPHRASE
		privKey	-X PASSPHRASE	defPrivPassphrase PASSPHRASE
		securityLevel	-l (noAuthNoPriv|authNoPriv|authPriv) defSecurityLevel (noAuthNoPriv|authNoPriv|authPriv)
		context	-n CONTEXTNAME	defContext CONTEXTNAME
	 * @param agent
	 * @param ip
	 * @param version
	 * @param community
	 * @param args
	 * @return
	 */
	public String getV1V2Command(String agent, String ip, String version, 
			String community, String oids[]) {
//		snmpwalk -c public -v 2c 192.168.1.21 system
//		snmpwalk -mALL -v1 -cpublic 192.168.1.21 system
		version = version.toLowerCase().trim();
		StringBuilder sb = new StringBuilder(100);
		sb.append(agent);
		sb.append(" -v ");
		sb.append(version);  // sb.append();
		sb.append(" -c \"");
		sb.append(community);
		sb.append('\"');
		sb.append(' ');
		sb.append(ip);
		if(oids == null || oids.length == 0) { throw new IllegalArgumentException("没有OID"); }
		else {
			for(String oid : oids) { sb.append(' '); sb.append(oid); }
		}
		return sb.toString();
	}
	
	public String getV3Command(String agent, String ip, String userName, String auth,
			String authP, String encrypt, String encryptP, String oids[]) {
/**
 * 		snmpgetnext -v 3 -n "" -u MD5DESUser -a MD5 -A "The Net-SNMP Demo Password" 
 * 		-x DES -X "The Net-SNMP Demo Password" -l authPriv 
 */
		StringBuilder sb = new StringBuilder(100);
		sb.append(agent);
		sb.append(" -v 3 "); // sb.append();
		sb.append(" -n \"\" ");  // CONTEXT NAME
		sb.append(" -u ");
		sb.append(userName);
		sb.append(' ');
		if(auth != null && auth.trim().length() > 0 && (!auth.equalsIgnoreCase("none"))) {
			sb.append(" -a ");
			sb.append(auth);
			sb.append(" -A \"");
			sb.append(authP);
			sb.append("\" ");
			if(encrypt != null && encrypt.trim().length() > 0 && (!encrypt.equalsIgnoreCase("none"))) {
				sb.append(" -x ");
				sb.append(encrypt);
				sb.append(" -X \"");
				sb.append(encryptP);
				sb.append("\" -l authPriv ");
			}
			else { sb.append(" -l authNoPriv "); }
		}
		else { sb.append(" -l noAuthNoPriv "); }
		sb.append(' ');
		sb.append(ip);
		
		if(oids == null || oids.length == 0) { throw new IllegalArgumentException("没有OID"); }
		else {
			for(String oid : oids) { sb.append(' '); sb.append(oid); }
		}
		return sb.toString();
	}
	
	private void handler(String cmd, IHandler hdr, JsonData result){
		int exitValue = 0;
		Process proc = null;
		BufferedReader br = null;
		result.setSuccess(true);
		try {
			proc = Runtime.getRuntime().exec(cmd);
			InputStream inputStream = proc.getInputStream();
			br = new BufferedReader(new InputStreamReader(inputStream));
			hdr.handle(br, result);
			proc.waitFor();
			exitValue = proc.exitValue();
			if(exitValue != 0){
				result.setSuccess(false);
				result.setMsg("Script Abnormal exit!");
				Logger.getLogger(getClass()).error(result.getMsg());
			}
		}
		catch (Exception e) {
			result.setSuccess(false);
			Logger.getLogger(getClass()).error(e.getMessage(), e);
		}
		finally{
			try {
				if(br != null){ br.close(); }
				if(proc.getInputStream() != null){ proc.getInputStream().close(); }
				proc.getErrorStream().close();
				proc.getOutputStream().close();
				proc.destroy();
			}catch (IOException e) {
				Logger.getLogger(getClass()).error(e.getMessage(), e);
			}
		}
	}
	
	private void handlerWithException(String cmd, IHandler hdr, JsonData result) 
										throws IOException, InterruptedException {
		
		int exitValue = 0;
		Process proc = null;
		BufferedReader br = null;
		proc = Runtime.getRuntime().exec(cmd);
		InputStream inputStream = proc.getInputStream();
		br = new BufferedReader(new InputStreamReader(inputStream));
		hdr.handle(br, result);
		proc.waitFor();
		exitValue = proc.exitValue();
		if(exitValue != 0){
			result.setSuccess(false);
			result.setMsg("Script Abnormal exit!");
			Logger.getLogger(getClass()).error(result.getMsg());
		}
		//////////// 下面的代码不能说明
		try {
			if(br != null){ br.close(); }
			if(proc.getInputStream() != null){ 
				proc.getInputStream().close();
			}
			if(proc.getErrorStream() != null){ 
				proc.getErrorStream().close();
			}
			if(proc.getOutputStream() != null){ 
				proc.getOutputStream().close();
			}
			proc.destroy();
		}catch (IOException e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
		}
	}
	
	public long parseLong(String s) {
		if(s == null || s.trim().length() == 0){ return 0L; }
		return Long.parseLong(s);
	}
	
	public String getSwitchOperationalStatus(int status) {
		if(status == 3) {
			return "up";
		}
		else { return "down"; }
	}
	
	/**
	 * @see 接口当前的操作状态。已定义的状态包括up(1)、down(2)和testing(3)
	 * @param status
	 * @return
	 */
	public String getPortOperationalStatus(int status) {
		switch(status) {
			case 1: return "up";
			case 2: return "down";
			case 3: return "testing";
			default: return "unknown";
		}
	}
	
	public void print(Object obj, String mark){
		Logger.getLogger(getClass()).info("***********************************************");
		Logger.getLogger(getClass()).info(mark);
		Logger.getLogger(getClass()).info(JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss"));
		Logger.getLogger(getClass()).info("***********************************************");
	}
	
}
