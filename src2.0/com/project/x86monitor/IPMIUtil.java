package com.project.x86monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DataRow;
import com.huiming.sr.constants.SrContant;
import com.project.x86monitor.beans.SysEventLogBean;


public class IPMIUtil {
	private String ipmiUtilFile; // 是指ipmiutil.exe所在的路径
	private SimpleDateFormat sdf = new SimpleDateFormat();
	private final String pattern1 = "MM/dd/yy HH:mm:ss"; // 05/21/15 15:06:00
	private final String pattern2 = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * @see IPMIUtil util = new IPMIUtil(this.getClass());
	 * @param clazz
	 */
	public IPMIUtil(Class<?> clazz) {
		String clazzDir = MyUtilities.getClassDirectory(clazz);
		String dir = new File(clazzDir).getParent();
		ipmiUtilFile = dir + "/lib/ipmiutil-2.9.6-win64/ipmiutil.exe ";
	}
	/*void print(Object obj, String mark){
		Logger logger = Logger.getLogger(getClass());
		logger.info("******************************************");
		logger.info(mark);
		logger.info(JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss"));
		logger.info("******************************************");
	}*/

	/**
	 * @param bean负责传值和保存结果，ipmi
	 */
	public void loadServerConfig(SysEventLogBean bean){
		IPMIInfo ipmi = bean.getIpmi();
		String cmd = String.format("%s fru -N %s -U %s -P %s -T %s -V %s -c -b ", ipmiUtilFile, 
				ipmi.getIpAddress(), ipmi.getUserName(), ipmi.getPassword(), ipmi.getAuthType(), ipmi.getLevel());
		JsonData result = new JsonData();
		result.setValue(bean);
		handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				Map<String, String> field2cols = new HashMap<String, String>();
				field2cols.put("Chassis Type", "chassis_type"); 
				field2cols.put("Chassis Part Number", "chassis_model");
				field2cols.put("Chassis Serial Num", "chassis_serial_num");
				field2cols.put("Board Manufacturer", "board_factory");
				//field2cols.put("Board Mfg DateTime", "board_mfg_datetime");
				field2cols.put("Board Product Name", "board_vendor");
				field2cols.put("Board Serial Number", "board_serial_num");
				field2cols.put("Board Part Number", "board_model");
				field2cols.put("Product Manufacturer", "prod_factory");
				field2cols.put("Product Name", "prod_name"); 
				field2cols.put("Product Part Number", "prod_model");
				field2cols.put("Product Version", "prod_version");
				field2cols.put("Product Serial Num", "prod_serial_num"); 
				field2cols.put("System GUID", "system_guid");
				String line;
				Set<String> keySet = field2cols.keySet();
				Date update_timestamp = new Date();
				DataRow dr = new DataRow();
				String parts[] = null;
				SysEventLogBean bean = (SysEventLogBean)result.getValue();
				long hypId = bean.getIpmi().getHypervisorId();
				StringBuilder sb_error = new StringBuilder(100);
				while((line = br.readLine()) != null){
					if(line.contains("error")){ // 如果有error，说明不成功，就将所有信息拿出来
						do{
							sb_error.append(line);
							sb_error.append("\r\n");
						} while((line = br.readLine()) != null);
						result.setSuccess(false); // 说明连接不成功
						break;
					}
					if(line.contains("Board Mfg DateTime")){
						parts = split(line, '|', 2);
						if(parts != null){
							dr.set("board_mfg_datetime", getFormattedDate(parts[1]));
						}
					}
					else {
						for(String key : keySet){
							if(line.contains(key)){
								parts = split(line, '|', 2);
								if(parts == null){ break; }
								dr.set(field2cols.get(key), parts[1]);
								break;
							}
						}
					}
				}
				if(sb_error.length() > 0){
					Logger.getLogger(getClass()).error(sb_error);
				}
				else {
					List<DataRow> drs = new ArrayList<DataRow>(1);
					dr.set("hypervisor_id", hypId);
					sdf.applyPattern(SrContant.TIME_PATTERN);
					dr.set("update_timestamp", sdf.format(update_timestamp));
					drs.add(dr);
					bean.setInsertData(drs);
				}
			}
		}, result);
	}
	
	/**
	 * @see 获得sensor搜集的状态
	 * @param bean是负责传值的和保存返回值，需要传的值包括ipmi, statusIds
	 */
	public void loadSensorStatus(SysEventLogBean bean){ // bmc_self_status / chassis power status /
		IPMIInfo ipmi = bean.getIpmi();
		String cmd = String.format("%s sensor -N %s -U %s -P %s -c -b -e -T %s -V %s", ipmiUtilFile,
				ipmi.getIpAddress(), ipmi.getUserName(), ipmi.getPassword(), ipmi.getAuthType(), ipmi.getLevel());
		JsonData result = new JsonData();
		result.setValue(bean);
		handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				String line;
				List<DataRow> updateSensors = new ArrayList<DataRow>(30); // 用于保存要更新的数据
				List<DataRow> insertSensors = new ArrayList<DataRow>(30); // 用于保存要插入的数据
				SysEventLogBean bean = (SysEventLogBean)result.getValue();
				Set<Long> statusIds = bean.getStatusIds();
				sdf.applyPattern(SrContant.TIME_PATTERN);
//				Logger.getLogger(getClass()).info(JSON.toJSON(statusIds));
				long hypervisor_id = bean.getIpmi().getHypervisorId();
				StringBuilder sb_error = new StringBuilder(100);
				DataRow status;
				while((line = br.readLine()) != null){
					if(line.contains("error")){ // 如果有error，说明不成功，就将所有信息拿出来
						do{
							sb_error.append(line);
							sb_error.append("\r\n");
						} while((line = br.readLine()) != null);
						Logger.getLogger(getClass()).error(sb_error.toString());
						result.setSuccess(false); // 说明连接不成功
						break;
					}
					if(line.contains("SDRType")){
						String parts[] = null;
						while((line = br.readLine()) != null && line.lastIndexOf("successfully") < 0){
							parts = split(line, '|', 7);
							if(parts == null){ continue; }
							status = getSensorStatusDataRow(parts, -1L, hypervisor_id);
							status.set("update_timestamp", sdf.format(new Date()));
							// 如果存在ID说明这个数据将用于更新当前数据
							if(statusIds.contains(status.getLong("bmc_index"))){
								updateSensors.add(status);
							}
							else { insertSensors.add(status); }
						}
						bean.setInsertData(insertSensors);
						bean.setUpdateData(updateSensors);
						// 处理完，新增bmc_self_status和chassis power status
						IPMIInfo ipmi = bean.getIpmi();
						handler(String.format("%s health -N %s -U %s -P %s -T %s -V %s -c", ipmiUtilFile, 
								ipmi.getIpAddress(), ipmi.getUserName(), ipmi.getPassword(), ipmi.getAuthType(), ipmi.getLevel()),
								new IHandler(){
							public void handle(BufferedReader br, JsonData result) throws IOException {
								SysEventLogBean bean = (SysEventLogBean)result.getValue();
								Set<Long> statusIds = bean.getStatusIds();
								List<DataRow> updateSensors = new ArrayList<DataRow>(10);
								List<DataRow> insertSensors = new ArrayList<DataRow>(10);
								String line;
								int drsCount1 = bean.getUpdateData().size() - 1;
								int drsCount2 = bean.getInsertData().size() - 1;
								long hypId = drsCount1 >= 0? bean.getUpdateData().get(drsCount1).getLong("bmc_index") : 0L;
								long currMaxId = drsCount2 >= 0? bean.getInsertData().get(drsCount2).getLong("bmc_index") : 0L;
								currMaxId = currMaxId > hypId? currMaxId : hypId;
								hypId = bean.getIpmi().getHypervisorId();
								StringBuilder sb_error = new StringBuilder(100);
								while((line = br.readLine()) != null){
									if(line.contains("error")){ // 如果有error，说明不成功，就将所有信息拿出来
										do{
											sb_error.append(line);
											sb_error.append("\r\n");
										} while((line = br.readLine()) != null);
										Logger.getLogger(getClass()).error(sb_error.toString());
										result.setSuccess(false); // 说明连接不成功
										break;
									}
									if(line.contains("Power State")){
										loadSensorStatusDataRow((statusIds.contains(++currMaxId)? updateSensors : insertSensors), 
												split(line, '|', 2), currMaxId, hypId, "Power State");
									}
									else if(line.contains("Selftest status")){
										loadSensorStatusDataRow((statusIds.contains(++currMaxId)? updateSensors : insertSensors), 
												split(line, '|', 2), currMaxId, hypId, "BMC Status");
									}
									else if(line.contains("chassis_power")){
										loadSensorStatusDataRow((statusIds.contains(++currMaxId)? updateSensors : insertSensors), 
												split(line, '|', 2), currMaxId, hypId, "chassis power");
									}
									else if(line.contains("pwr_restore_policy")){
										loadSensorStatusDataRow((statusIds.contains(++currMaxId)? updateSensors : insertSensors), 
												split(line, '|', 2), currMaxId, hypId, "power restore policy");
									}
									else if(line.contains("chassis_intrusion")){
										loadSensorStatusDataRow((statusIds.contains(++currMaxId)? updateSensors : insertSensors), 
												split(line, '|', 2), currMaxId, hypId, "chassis intrusion");
									}
									else if(line.contains("front_panel_lockout")){
										loadSensorStatusDataRow((statusIds.contains(++currMaxId)? updateSensors : insertSensors), 
												split(line, '|', 2), currMaxId, hypId, "front panel lockout");
									}
									else if(line.contains("cooling_fan_fault")){
										loadSensorStatusDataRow((statusIds.contains(++currMaxId)? updateSensors : insertSensors), 
												split(line, '|', 2), currMaxId, hypId, "cooling fan fault");
									}
								}
								bean.getInsertData().addAll(insertSensors);
								bean.getUpdateData().addAll(updateSensors);
							}
						}, result);
						break;
					}
				}
			}
		}, result);
	}
	private void loadSensorStatusDataRow(List<DataRow> drs, String parts[], long bmc_index, long hypId, String devName){
		if(parts != null){
			DataRow status = new DataRow();
			status.set("bmc_index", bmc_index);
			status.set("hypervisor_id", hypId);
			status.set("sdr_type", "Compact");
			status.set("device_type", "BMC");
			status.set("device_name", devName);
			status.set("status", parts[1]);
			status.set("update_timestamp", sdf.format(new Date()));
			drs.add(status);
		}
	}
	/**
	 * @see 获取System Event Logs
	 * @param ipmi
	 * @param myResult：hypId->Long, fromNowOn->Long, SysEventLog->List<DataRow>
	 */
	public void loadSystemEventLog(SysEventLogBean bean){
		// 0=INF, 1=MIN, 2=MAJ, 3=CRT “-s 1”表示只显示1到3的级别的事件
		IPMIInfo ipmi = bean.getIpmi();
		String cmd = String.format("%s sel -N %s -U %s -P %s -c -s 1 -T %s -V %s", ipmiUtilFile, ipmi.getIpAddress(), 
				ipmi.getUserName(), ipmi.getPassword(), ipmi.getAuthType(), ipmi.getLevel());
		JsonData result = new JsonData();
		result.setValue(bean);
		handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				SysEventLogBean bean = (SysEventLogBean)result.getValue();
				long muillisec = bean.getFromNowOn();
				long maxMuillisec = Long.MIN_VALUE;
				long hypId = bean.getIpmi().getHypervisorId();
				String hypName = bean.getIpmi().getHypervisorName();
				List<DataRow> sels = new ArrayList<DataRow>(100);
				String line;
				Date eventDate = null;
				double usedPercentage = 0.0;
				StringBuilder sb_error = new StringBuilder(100);
				while((line = br.readLine()) != null){
					if(line.contains("error")){ // 如果有error，说明不成功，就将所有信息拿出来
						do{
							sb_error.append(line);
							sb_error.append("\r\n");
						} while((line = br.readLine()) != null);
						Logger.getLogger(getClass()).error(sb_error.toString());
						result.setSuccess(false); // 说明连接不成功
						break;
					}
					if(line.contains("BMC version") && line.contains("IPMI version")){
						line = br.readLine();
						if(line.contains("Used=")){
							Pattern pat = Pattern.compile("[0-9]+");
							Matcher mat = pat.matcher(line);
							double nums[] = new double[3];
							int count = 0;
							while(mat.find() && count < 3){
								nums[count++] = Double.parseDouble(mat.group());
							}
							// 计算得到当前事件已使用空间百分比
							usedPercentage = nums[1] / nums[0];
						}
					}
					if(line.lastIndexOf("Evt_detail") >= 0){ // 说明有事件
						String parts[];
						while((line = br.readLine()) != null && line.lastIndexOf("successfully") < 0){
							parts = split(line, '|', 7);
							if(parts == null || parts[1].length() == 0){ continue; } // 没有时间
							//if(parts[2].equalsIgnoreCase("INF")){ continue; } // 不要INFO级别的事件
							// 过滤时间
							sdf.applyPattern(pattern1);
							eventDate = null;
							try {
								eventDate = sdf.parse(parts[1]);
							} catch (ParseException e) {
								eventDate = null;
								Logger.getLogger(getClass()).error(e.getLocalizedMessage(), e);
							}
							if(eventDate != null && eventDate.getTime() > muillisec){
								if(maxMuillisec < eventDate.getTime()){
									maxMuillisec = eventDate.getTime();
								}
								// 能正常解析时间日期和所得时间
								sels.add(getSysEventLogDataRow(parts, hypId, eventDate, hypName));
							}
						}
						// 将最大时间保存到
						bean.setFromNowOn((maxMuillisec > muillisec)? maxMuillisec : muillisec);
						break;
					}
				}
				if(usedPercentage > 0.80){  // 超过80%，那么就清除这些事件
					IPMIInfo ipmi = bean.getIpmi();
					handler(String.format("%s sel -N %s -U %s -P %s -d -T %s -V %s", ipmiUtilFile, 
							ipmi.getIpAddress(), ipmi.getUserName(), ipmi.getPassword(), ipmi.getAuthType(), ipmi.getLevel()), 
							new IHandler(){
									public void handle(BufferedReader br, JsonData result) throws IOException {
										String line;
										StringBuilder sb_msg = new StringBuilder(100);
										while((line = br.readLine()) != null){
											sb_msg.append(line);
											sb_msg.append("\r\n");
										}
										Logger.getLogger(getClass()).info(sb_msg);
									}
							}, null);
				}
				if(sb_error.length() > 0){
					String parts[] = {
							"", "", "CRT", "BMC", "Connected Error", "", 
							result.getMsg().replace("\r\n", ". ")
					};
					DataRow sel = getSysEventLogDataRow(parts, hypId, new Date(), hypName);
					sels.add(sel);
				}
				bean.setSysEventLogs(sels);
			}
		}, result);
	}
	
	/**
	 * @see 测试用户所填写的BMC
	 * @param ipmi
	 * @return true表示连接成功
	 */
	public boolean isBMCConnected(IPMIInfo ipmi){
		// -T 是auth_type
		String cmd = String.format("%s health -N %s -U %s -P %s -T %s -V %s", ipmiUtilFile, ipmi.getIpAddress(), 
				ipmi.getUserName(), ipmi.getPassword(), ipmi.getAuthType(), ipmi.getLevel());
		JsonData result = new JsonData();
		handler(cmd, new IHandler(){
			public void handle(BufferedReader br, JsonData result) throws IOException {
				String line;
				StringBuilder sb_error = new StringBuilder(100);
				while((line = br.readLine()) != null){
					if(line.contains("error")){  // 获取所有的错误信息
						do{
							sb_error.append(line);
							sb_error.append("\r\n");
						} while((line = br.readLine()) != null);
						break;
					}
				}
				if(sb_error.length() > 0){
					result.setSuccess(false); // 说明连接不成功
					Logger.getLogger(getClass()).error(sb_error);
				}
			}
		}, result);
		return result.isSuccess();
	}
	
	/**
	 * @see 
	 * @param dateStr = "Mon Aug 17 12:48:00 2009"
	 * @return
	 */
	public String getFormattedDate(String dateStr){
		String parts[] = split(dateStr, ' ', 5);
		if(parts == null){
			return dateStr;
		}
		String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sept", "Oct", "Nov", "Dec" };
		int m = 0;
		for(int i = 0, len = months.length; i < len; ++i){
			if(months[i].equalsIgnoreCase(parts[1])){
				m = i + 1;
				break;
			}
		}
		return String.format("%s-%s-%s %s", parts[4], (m < 10? "0" + m : m), parts[2], parts[3]);
	}
	
	private DataRow getSensorStatusDataRow(String parts[], long bmc_index, long hypervisor_id){
		DataRow status = new DataRow();
		status.set("bmc_index", bmc_index > 0L? bmc_index : Long.parseLong(parts[0], 16));
		status.set("hypervisor_id", hypervisor_id);
		status.set("sdr_type", parts[1]);
		status.set("device_type", parts[2]);
		status.set("snum", parts[3]);
		status.set("device_name", parts[4]);
		status.set("status", parts[5]);
		status.set("read_data", parts[6]);
		return status;
	}
	
	private DataRow getSysEventLogDataRow(String parts[], long hypId, Date eventDate, String hypName){
		DataRow sel = new DataRow();
		sel.set("fno", parts[0]);
		sel.set("FRuleId", parts[0]);
		sel.set("FLogType", SrContant.LOG_TYPE_HARDWARE);
		sel.set("FTopId", hypId);
		sel.set("FTopType", SrContant.SUBDEVTYPE_PHYSICAL);
		sel.set("FResourceId", hypId);
		sel.set("FTopName", hypName);
		sel.set("FResourceType", SrContant.SUBDEVTYPE_PHYSICAL);
		sel.set("FResourceName", hypName);
		sel.set("FIsdelete", 0);
		sel.set("FCount", 1);
		sdf.applyPattern(pattern2);
		String eventDateStr = sdf.format(eventDate);
		sel.set("FFirstTime", eventDateStr);
		sel.set("FLastTime", eventDateStr);
		sel.set("FLevel", 
				"CRT".equalsIgnoreCase(parts[2])? SrContant.EVENT_LEVEL_CRITICAL : SrContant.EVENT_LEVEL_WARNING);
		sel.set("FDescript", String.format("Source: %s, Event_Type: %s, Sensor: %s", parts[3], parts[4], parts[5]));
		sel.set("FDetail", parts[6]);
		sel.set("FState", 0);
		sel.set("FSourceType", parts[3]);
		return sel;
	}
	
	private String[] split(String str, char sep, int numOfParts){
		String parts[] = new String[numOfParts];
		Arrays.fill(parts, "");
		if(str == null){ return parts; }
		char chs[] = str.toCharArray();
		int pre = 0, i = 0, end;
		char whiteSpace = ' ';
		for(int curr = 0, len = chs.length; curr < len; ++curr){
			if(chs[curr] == sep){
				end = curr - 1;
				while(end > pre && chs[end] == whiteSpace){ --end; }
				while(end > pre && chs[pre] == whiteSpace){ ++pre; }
				end = end - pre + 1;
				if(end == 1 && chs[pre] == whiteSpace){ end = 0; }
				parts[i] =  new String(chs, pre, end);
				pre = curr + 1;
				++i;
			}
		}
		end = chs.length - 1;
		while(end > pre && chs[end] == whiteSpace){ --end; }
		while(end > pre && chs[pre] == whiteSpace){ ++pre; }
		end = end - pre + 1;
		if(end == 1 && chs[pre] == whiteSpace){ end = 0; }
		parts[i] = new String(chs, pre, end);
		// 不满足预期，一律抛弃
		return i < numOfParts - 1? null : parts;
	}
	
	private void handler(String cmd, IHandler hdr, JsonData result){
		int exitValue = 0;
		Process proc = null;
		BufferedReader br = null;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			InputStream inputStream = proc.getInputStream();
			br = new BufferedReader(new InputStreamReader(inputStream));
			hdr.handle(br, result);
			proc.waitFor(); // wait for reading STDOUT and STDERR over
			exitValue = proc.exitValue();
		}
		catch (Exception e) {
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
		if(exitValue != 0){
			Logger.getLogger(getClass()).error("Script Abnormal exit!");
		}
	}
	
	private interface IHandler {
		void handle(BufferedReader br, JsonData result) throws IOException;
	}
	
}
