package com.huiming.service.topo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.topo.tree.TreeUtils;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

@SuppressWarnings("unchecked")
public class TopoService extends BaseService {
	
	private boolean hasDB2 = Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null;
	private List<DataRow> emptyList = new ArrayList<DataRow>(0);
	/**
	 * @see 获取所有的供用户选择, 由于应用既可以连接物理机  也可以连接虚拟机
	 * @see 已经安装应用的服务器不再显示
	 * @return
	 */
	public List<DataRow> getALLAppDataFromMySQL(){
		String sql = "SELECT c.computer_id AS dev_id,c.name AS dev_name,c.ip_address,s.id AS seid FROM t_res_computersystem c " +
		" JOIN t_server s ON c.IP_ADDRESS=s.IP_ADDRESS " +
		" WHERE c.computer_id NOT IN(SELECT DISTINCT computer_id FROM t_app_server) ORDER BY computer_id ASC";
	
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * @see 获得节点的位置数据
	 * @return
	 */
	public Map<String, Map<String, Float>> getNodeDataByAppId(JdbcTemplate jdbc, Long appId){
		List<DataRow> nodeData = jdbc.query("SELECT * FROM t_nodes_info WHERE appid="+appId+" ORDER BY id");
		if(nodeData != null && nodeData.size() > 0){
			Map<String, Map<String, Float>> nodePos = new HashMap<String, Map<String, Float>>();
			String key;
			for(DataRow dr : nodeData){
				key = dr.getString("devtype") + dr.getString("devid");
				Map<String, Float> pos = new HashMap<String, Float>(2);
				pos.put("x", dr.getFloat("dev_x"));
				pos.put("y", dr.getFloat("dev_y"));
				nodePos.put(key, pos);
			}
			return nodePos;
		}
		return null;
	}
	
	public List<DataRow> getSwitchPortBySwitchid(Long switchId){
		if(!hasDB2){
			return emptyList;
		}
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		String sql = "select port_id,the_display_name as name,the_type,the_operational_status as operation,the_consolidated_status as consolidate,the_enabled_state as enabled,the_port_speed as speed, " +
			"cast(port_number as integer) port_number from v_res_switch_port where switch_id="+switchId+" order by port_number asc";
		List<DataRow> swports = tpc.query(sql);
		if(swports != null && swports.size() > 0){
			String ftopid = switchId.toString();
			JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
			for(int i = 0, size = swports.size(); i < size; ++i){
				swports.get(i).put("logs", sa.queryLogs(srDB, ftopid, swports.get(i).getString("port_id"), SrContant.SUBDEVTYPE_SWITCHPORT));
			}
		}
		return swports;
	}
	
	/**
	 * @see 获取所有computersystem的供用户选择
	 * 
	 	<%--应用必须有app_id --%>
		<%--虚拟机必须有vm_id(t_res_virtualmachine), vm_name, ip_address, hyp_id, comp_id --%>
		<%--虚拟机连接物理机必须有vm_id, hyp_id(t_res_hypervisor), hyp_name, ip_address, comp_id --%>
		<%--物理机必须有hyp_id(t_res_hypervisor), hyp_name, ip_address, comp_id --%>
		<%--物理机连交换机必须有sw_id(v_res_switch), hyp_id(t_res_hypervisor), sw_name, hyp_name, sw_ip--%>
		<%--交换机必须有sw_id1(v_res_switch), sw_id2(v_res_switch), sw_name1, sw_name2, sw_ip1, sw_ip2--%>
		<%--存储系统必须有sto_id(v_res_storagesubsystem), sw_id(v_res_switch), sto_name, sto_ip, os_type, comp_id --%>
		<%--存储系统必须有sto_id(v_res_storagesubsystem), sw_id(v_res_switch), sto_name, sw_name, sto_ip, sw_ip--%>
	 * @return
	 */
	/**
	 * @see 通过ID获取应用
	 * @param appId
	 * @return
	 */
	public DataRow getAppDataByIdFromMySQL(Integer appId){
		String sql = "SELECT id,name,description FROM t_app id=" + appId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	public List<DataRow> getAppDataByIds(String appIds){
		String _appIds = "";
		if(appIds != null && appIds.trim().length() > 0){
			_appIds = " where id in(&) ".replace("&", appIds);
		}
		String sql = "SELECT id as appid,NAME as appname,node_count as nodecount FROM t_app & ORDER BY id ASC";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.replace("&", _appIds));
	}
	
	/**
	 * @see 获取应用
	 * @param appIds
	 * @param userId
	 * @param role
	 * @return
	 */
	public List<DataRow> getAppDataByIds1(String appIds, long userId, String role){
		if(!hasDB2){ return emptyList; }
		String sql = "SELECT id as appid,NAME as appname,node_count as nodecount FROM t_app where 1=1 ";
		if(appIds != null && appIds.trim().length() > 0){
			sql += " and id in(&) ".replace("&", appIds);
		}
		boolean isUser = !SrContant.ROLE_SUPER.equalsIgnoreCase(role);
		if(isUser){
			sql += " and user_id=" + userId;
		}
		// is_new<>0
		sql += " ORDER BY id ASC";
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		List<DataRow> drs = srDB.query(sql);
		if(drs != null && drs.size() > 0){
			// 先根据user_id（如果是super就不用考虑）和is_..._selected
			sql = "select app_id from t_app_user where user_id=" + userId;
			long selectedAppId = srDB.queryLong(sql);
			boolean isSelectedAppIdValid = selectedAppId > 0L;
			sql = "select count(id) from t_app where id=" + selectedAppId;
			if(srDB.queryInt(sql) == 0){
				selectedAppId = 0L;
			}
			if(selectedAppId <= 0L){// 说明查出来的selectedAppId无效
				// 既然查出来的appId无效，那么默认是表t_app字段user_id的第一个
				selectedAppId = srDB.queryLong("select id from t_app " +
						(isUser? "where user_id=" + userId : "") + 
						" order by id limit 0,1");
				DataRow dr = new DataRow();
				dr.set("app_id", selectedAppId);
				
				if(isSelectedAppIdValid){
					srDB.update("t_app_user", dr, "user_id", userId);
				}
				else {
					dr.set("user_id", userId);
					srDB.insert("t_app_user", dr);
				}
			}
			for(DataRow dr : drs){
				dr.set("is_new", dr.getLong("appid") == selectedAppId);
			}
		}
		return drs;
	}
	
	public static String htmlToText(String str){  
        if(str == null){ return null; }
        int len, index;
        StringBuffer sb = new StringBuffer(str);
        String htmls[] = {"&amp;", "&lt;", "&gt;", "&nbsp;", "<br>", "&#39;",   "&#34;", "&#92;"},
        	   texts[] = {"&",     "<",    ">", " ",      "\n",   "\'",   "\"",    "\\"};
        for(int i = 0, l = htmls.length; i < l; ++i){
        	index = sb.indexOf(htmls[i]);
        	len = htmls[i].length();
        	while(index >= 0){
        		sb.replace(index, index + len, texts[i]);
        		index = sb.indexOf(htmls[i]);
        	}
        }
        return sb.toString();
    }
	
	///////////////////////////////////////////////////
	public List<DataRow> getDeviceAlert(Integer ftopid, String ftoptype){
		// ('Fabric','Computer','Switch','Storage')    fresourceid, AND fresourceid=?
		String sql = "SELECT COUNT(fcount) AS logcount,flevel,ftopname FROM tndevicelog WHERE fstate = 0 "+
		" AND ftoptype=? AND ftopid=? GROUP BY flevel,ftopname ORDER BY ftoptype,flevel DESC";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{ ftoptype, ftopid });
	}
	
	
	/**
	 * @see 获得交换机和交换机
	 * @return
	 */
	public List<DataRow> getSwitchesAndSwitches(String switchIds){
		if(!hasDB2){ return emptyList; }
		/*
		 * swid1 swid2  switchIds要么在swid1出现要么在swid2或者是两者同时出现
		 */
//		String sql = 
//			"select sw.the_display_name as sname1,sw.switch_id as sid1,sw.ip_address as ip1," +
//			"po.port_id as pid1,kk.pid2,kk.sid2,kk.sname2,kk.ip2 " +
//			"from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id %s" +
//			"join v_res_port2port p2p on po.port_id=p2p.port_id1 join " +
//				"(select sw.the_display_name as sname2,sw.switch_id as sid2,po.port_id as pid2," +
//				"sw.ip_address as ip2 from v_res_switch sw join v_res_switch2port po on " +
//				"sw.switch_id=po.switch_id) " +
//			"kk on kk.pid2=p2p.port_id2";
		String ids = (switchIds == null || switchIds.length() <= 0)? "-1" : switchIds;
//		String sql = "select distinct sw.switch_id as sw_id1,kk.sid2,sw.the_display_name as sname1,sw.ip_address as ip1,"+
//			//"po.port_id as pid1,kk.pid2,"+
//			"kk.sname2,kk.ip2 " +
//			"from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id " +
//			"join v_res_port2port p2p on po.port_id=p2p.port_id1 join " +
//				"(select sw.the_display_name as sw_name2,sw.switch_id as sw_id2,po.port_id as sw_pid2, " +
//				"sw.ip_address as ip2 from v_res_switch sw join v_res_switch2port po on  " +
//				"sw.switch_id=po.switch_id)  " +
//			"kk on kk.pid2=p2p.port_id2 " +
//			"where sw.switch_id in (%s) or kk.sid2 in (%s) order by sw.switch_id".replace("%s", ids);	
		String sql = "SELECT DISTINCT sw.switch_id AS sw_id1,kk.sw_id2,sw.the_display_name AS sw_name1,sw.ip_address AS sw_ip1,kk.sw_name2,kk.sw_ip2 " +
			" FROM v_res_switch sw JOIN v_res_switch_port po ON sw.switch_id=po.switch_id  " +
			" JOIN v_res_port2port p2p ON po.port_id=p2p.port_id1  " +
			" JOIN (SELECT sw.the_display_name AS sw_name2,sw.switch_id AS sw_id2,po.port_id AS sw_pid2, sw.ip_address AS sw_ip2  " +
			" FROM v_res_switch sw JOIN v_res_switch2port po ON  sw.switch_id=po.switch_id)  kk ON kk.sw_pid2=p2p.port_id2  " +
			" WHERE sw.switch_id IN (%s) OR kk.sw_id2 IN (%s) ORDER BY sw.switch_id".replace("%s", ids);
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql, new Object[0]);
	}
	
	
	/**
	 * @see 更新节点的位置信息, 当应用存在时, 把属于应用的节点统统删除, 如果不存在
	 */
	public void updateNodeInfo(List<DataRow> nodes, long appid, long userId, String role){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		// 更新t_app表里的node_count
		srDB.update("UPDATE t_app SET node_count=? WHERE id=?", new Object[]{ nodes == null? 0 : nodes.size(), appid});
		if(SrContant.ROLE_SUPER.equalsIgnoreCase(role)){ 
			// 说明超级管理员看到所有用户创建的应用，而且超级管理员试图修改这些图标的位置，那么保持原有的userId不变
			userId = srDB.queryLong("select user_id from t_app where id=" + appid);
		}
		// 把属于应用的节点统统删除
		srDB.delete("t_nodes_info", "appid", appid);
		// 插入新的数据
		if(nodes != null && nodes.size() > 0){
			for(DataRow dr : nodes){
				dr.set("user_id", userId);
				srDB.insert("t_nodes_info", dr);
			}
		}
	}
	
	private DateFormat dateFmt = new SimpleDateFormat(SrContant.TIME_PATTERN);
	
	public List<DataRow> getSwitchTotalPortDataRate(Long devid, String startDate){
		if(!hasDB2){ return emptyList; }
//		startDate = "2000-01-01 01:01:01";
		Calendar cal = Calendar.getInstance();
		cal.set(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), 
				cal.get(Calendar.DATE), 23, 59, 59);
		String endDate = dateFmt.format(cal.getTime());
		return getJdbcTemplate(WebConstants.DB_TPC).query(
				String.format("select A518 as data from PRF_TARGET_SWITCH where dev_id=%s and prf_timestamp between timestamp('%s') AND timestamp('%s')",
								devid, startDate, endDate));
	}
	
	BaseprfService baseService = new BaseprfService();
	/**
	 * 获取绘制性能图的数据
	 * @param devList
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Map<String, Object> getDrawPerfLineData(String eleId, String eleName, String kpi, 
			String startTime, String endTime) {
		Map<String, Object> json = new HashMap<String, Object>(6);
		//获取指定的KPI详细信息
		List<DataRow> kpis = baseService.getKPIInfo("'" + kpi + "'");
		List<DataRow> devList = new ArrayList<DataRow>(1);
		DataRow dr = new DataRow();
		dr.set("ele_id", eleId);
		dr.set("ele_name", eleName);
		devList.add(dr);
		//获取绘图数据
		json.put("series", baseService.getSeries(0, devList, kpis, startTime, endTime, "minite"));
		json.put("legend", true);
		json.put("ytitle", (kpis == null || kpis.size() == 0)? "" : kpis.get(0).getString("funits"));
		json.put("threshold", 0);
		json.put("threvalue", "");
		return json;
	}
	
	/**
	 * @see 加载服务器性能
	 * @param srDB
	 * @param json
	 * @param devid
	 * @param ftopType
	 */
	public void loadServerPerf(JdbcTemplate srDB, Map<String, Object> json, 
			Long devId, String ftopType, int second){
		Calendar cal = Calendar.getInstance();
		String endDate = dateFmt.format(cal.getTime());
		
		cal.add(Calendar.SECOND, -second);
		String startDate = dateFmt.format(cal.getTime());
		
		boolean isPhysical = SrContant.SUBDEVTYPE_PHYSICAL.equalsIgnoreCase(ftopType);
		String sql = 
				"SELECT c.CPU_BUSY_PRCT,c.MEM_USED_PRCT FROM t_prf_computerper c " +
				"JOIN t_prf_timestamp t ON c.time_id=t.time_id AND " +
				"t.sample_time BETWEEN timestamp('%s') AND timestamp('%s') join "+
				( isPhysical? ("t_res_hypervisor h on h.host_computer_id=c.computer_id and h.hypervisor_id=") 
						: ("t_res_virtualmachine v on v.computer_id=c.computer_id and v.vm_id=") ) + devId +
				" order by t.time_id desc limit 0,1";
		
//		startDate = "2000-01-01 01:01:01";
		json.put("cpuMem", srDB.queryMap(String.format(sql, startDate, endDate)));
		
		DataRow dr = srDB.queryMap(String.format("select name from %s where %s=%s",
				isPhysical? "t_res_hypervisor" : "t_res_virtualmachine", 
				isPhysical? "hypervisor_id" : "vm_id", devId));
		json.put("totalDiskDataRate", getDrawPerfLineData(devId.toString(), 
				dr == null? "Unknown" : dr.getString("name"),
				isPhysical? "H15" : "V16", startDate, endDate));
	}
	
	public void loadSwitchPerf(JdbcTemplate tpc, Map<String, Object> json, Long devId, int second){
//		if(!hasDB2){ return; }
		Calendar cal = Calendar.getInstance();
		String endTime = dateFmt.format(cal.getTime());
		
		cal.add(Calendar.SECOND, -second);
		String startTime = dateFmt.format(cal.getTime());
//		startTime = "2013-05-23 12:01:01";

		DataRow dr = tpc.queryMap("select the_display_name as name from v_res_switch where switch_id=" + devId);
		String name = dr == null? "" : dr.getString("name");
		json.put("totalPortPacketRate", getDrawPerfLineData(devId.toString(), name, "A515", startTime, endTime));
		json.put("totalPortDataRate", getDrawPerfLineData(devId.toString(), name, "A518", startTime, endTime));
	}
	
	public void loadStoragePerf(JdbcTemplate tpc, JdbcTemplate srDB, Map<String, Object> json, Long devId, 
			int second, String osType){
		Calendar cal = Calendar.getInstance();
		String endDate = dateFmt.format(cal.getTime());
		
		cal.add(Calendar.SECOND, -second);
		String startDate = dateFmt.format(cal.getTime());
//		startDate = "2013-05-23 12:01:01";

		String devtype = WebConstants.getStorageType(osType);
		String name = "";
		DataRow dr = null;
		String tpcSql = "select the_display_name as name from v_res_storage_subsystem where subsystem_id=" + devId;
		String srDBSql = "SELECT COALESCE(NAME, DISPLAY_NAME) as name FROM t_res_storagesubsystem where subsystem_id=" + devId;
		if(SrContant.DEVTYPE_VAL_DS.equalsIgnoreCase(devtype)){
			if(!hasDB2){ return; }
			dr = tpc.queryMap(tpcSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A221", startDate, endDate));
			json.put("totalDataRate", getDrawPerfLineData(devId.toString(), name, "A233", startDate, endDate));
			json.put("totalRespTime", getDrawPerfLineData(devId.toString(), name, "A236", startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_SVC.equalsIgnoreCase(devtype)){
			if(!hasDB2){ return; }
			dr = tpc.queryMap(tpcSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A38", startDate, endDate));
			json.put("totalDataRate", getDrawPerfLineData(devId.toString(), name, "A44", startDate, endDate));
			json.put("totalRespTime", getDrawPerfLineData(devId.toString(), name, "A47", startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_BSP.equalsIgnoreCase(devtype)){
			if(!hasDB2){ return; }
			dr = tpc.queryMap(tpcSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A415", startDate, endDate));
			json.put("totalDataRate", getDrawPerfLineData(devId.toString(),name, "A421", startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_HDS.equalsIgnoreCase(devtype)){
			dr = srDB.queryMap(srDBSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalDataRate", getDrawPerfLineData(devId.toString(), name, "A106_09", startDate, endDate));
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A106_03", startDate, endDate));
			json.put("totalRespTime", getDrawPerfLineData(devId.toString(), name, "A106_06", startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_EMC.equalsIgnoreCase(devtype)){
			dr = srDB.queryMap(srDBSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalDataRate", getDrawPerfLineData(devId.toString(), name, "A112_09", startDate, endDate));
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A112_03", startDate, endDate));
			json.put("totalRespTime", getDrawPerfLineData(devId.toString(), name, "A112_06", startDate, endDate));
		}
	}
	
	public void loadPoolPerf(JdbcTemplate tpc, JdbcTemplate srDB, Map<String, Object> json, Long devId, 
			int second, String osType){
		// 存储池没有性能
	}
	
	public void loadVolumePerf(JdbcTemplate tpc, JdbcTemplate srDB, Map<String, Object> json, Long devId, 
			int second, String osType){
		Calendar cal = Calendar.getInstance();
		String endDate = dateFmt.format(cal.getTime());
		cal.add(Calendar.SECOND, -second);
		String startDate = dateFmt.format(cal.getTime());
		
//		startDate = "2013-05-10 15:25:42";
		
		String devtype = WebConstants.getStorageType(osType);
		String name = "";
		DataRow dr = null;
		String tpcSql = "select the_display_name as name from v_res_storage_volume where svid=" + devId;
		String srDBSql = "SELECT COALESCE(NAME, DISPLAY_NAME) AS NAME FROM t_res_storagevolume WHERE volume_id=" + devId;
		
		if(SrContant.DEVTYPE_VAL_DS.equalsIgnoreCase(devtype)){
			if(!hasDB2){ return; }
			dr = tpc.queryMap(tpcSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalIORate",  getDrawPerfLineData(devId.toString(), name, "A183", startDate, endDate));
			json.put("totalRespTime",  getDrawPerfLineData(devId.toString(), name, "A198", startDate, endDate));
			json.put("totalTransSize",  getDrawPerfLineData(devId.toString(), name, "A201", startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_SVC.equalsIgnoreCase(devtype)){ }
		else if(SrContant.DEVTYPE_VAL_BSP.equalsIgnoreCase(devtype)){
			if(!hasDB2){ return; }
			dr = tpc.queryMap(tpcSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A427", startDate, endDate));
			json.put("totalTransSize", getDrawPerfLineData(devId.toString(), name, "A436", startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_HDS.equalsIgnoreCase(devtype)){
			dr = srDB.queryMap(srDBSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A111_03", startDate, endDate));
			json.put("totalRespTime", getDrawPerfLineData(devId.toString(), name, "A111_06", startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_EMC.equalsIgnoreCase(devtype)){
			dr = srDB.queryMap(srDBSql);
			name = dr == null? "" : dr.getString("name");
			json.put("totalIORate", getDrawPerfLineData(devId.toString(), name, "A117_03", startDate, endDate));
			json.put("totalRespTime", getDrawPerfLineData(devId.toString(), name, "A117_06", startDate, endDate));
		}
	}
	
	public Map<String, Object> getStorageDeviceDetail(Long devid, String devicetype){
		Map<String, Object> data = new HashMap<String, Object>();
		
		String sql = "select the_available_capacity as avai,the_allocated_capacity as allocated from v_res_storage_subsystem where subsystem_id=" + devid;
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		DataRow capacity = tpc.queryMap(sql);
		data.put("capacity", capacity);
		
		Calendar cal = Calendar.getInstance();
		cal.set(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), 
				cal.get(Calendar.DATE), 0, 0, 0);
		String startDate = dateFmt.format(cal.getTime());
		cal.set(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), 
				cal.get(Calendar.DATE), 23, 59, 59);
		String endDate = dateFmt.format(cal.getTime());   
		
//		startDate = "2000-01-01 01:01:01";
		sql = "select %s from %s as data where dev_id=%s and prf_timestamp between timestamp('%s') and timestamp('%s')";
		List<DataRow> TotalIORate = null;
		String devtype = WebConstants.getStorageType(devicetype);
		boolean isIBM = false;
		if(SrContant.DEVTYPE_VAL_DS.equalsIgnoreCase(devtype)){
			TotalIORate = tpc.query(
			String.format(sql, "A221", "PRF_TARGET_DSSYSTEM", devid, startDate, endDate));
			isIBM = true;
		}
		else if(SrContant.DEVTYPE_VAL_SVC.equalsIgnoreCase(devtype)){
			TotalIORate = tpc.query(
			String.format(sql, "A38", "PRF_TARGET_SVC_SYSTEM", devid, startDate, endDate));
			isIBM = true;
		}
		else if(SrContant.DEVTYPE_VAL_BSP.equalsIgnoreCase(devtype)){
			TotalIORate = tpc.query(
					String.format(sql, "A415", "PRF_TARGET_BSP_SYSTEM", devid, startDate, endDate));
			isIBM = true;
		}
		else if(SrContant.DEVTYPE_VAL_HDS.equalsIgnoreCase(devtype)){
			TotalIORate = srDB.query(
					String.format(sql, "A106_03", "PRF_TARGET_HDS_STORAGE", devid, startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_EMC.equalsIgnoreCase(devtype)){
			TotalIORate = tpc.query(
					String.format(sql, "A112_03", "PRF_TARGET_EMC_STORAGE", devid, startDate, endDate));
		}
		data.put("TotalIORate", TotalIORate);
		
		
		Long eventCount = getJdbcTemplate(WebConstants.DB_DEFAULT).queryLong(
				String.format(
				"SELECT COUNT(1) FROM tndevicelog WHERE ftopid=%s AND FLastTime BETWEEN timestamp('%s') AND timestamp('%s')",
						devid, startDate, endDate));
		data.put("eventCount", eventCount);
		if(isIBM){
			DataRow dr = tpc.queryMap(("select a.pc,b.dc,c.vc,d.plc from " +
			"(select count(port_id) as pc from v_res_port where subsystem_id=%s) a," +
			"(select count(STORAGE_EXTENT_ID) as dc from V_RES_STORAGE_EXTENT where subsystem_id=%s) b," +
			"(select count(svid) as vc from V_RES_STORAGE_VOLUME where subsystem_id=%s) c," +
			"(select count(pool_id) as plc from V_RES_STORAGE_POOL where subsystem_id=%s) d")
				.replace("%s", String.valueOf(devid)));

			data.put("stoPortCount", dr.getLong("pc"));
			data.put("stoDiskCount", dr.getLong("dc"));
			data.put("stoVolumeCount", dr.getLong("vc"));
			data.put("stoPoolCount", dr.getLong("plc"));
		}
		
		
		return data;
	}
	
	public List<DataRow> getStorageTotalIORate(String startDate, String devicetype, Long devid){
//		startDate = "2000-01-01 01:01:01"; // 正式项目时删除掉
		Calendar cal = Calendar.getInstance();
		cal.set(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), 
				cal.get(Calendar.DATE), 23, 59, 59);
		String endDate = dateFmt.format(cal.getTime());
		
		String sql = "select %s from %s as data where dev_id=%s and prf_timestamp between timestamp('%s') and timestamp('%s')";
		List<DataRow> TotalIORate = null;
		String devtype = WebConstants.getStorageType(devicetype);
		if(SrContant.DEVTYPE_VAL_DS.equalsIgnoreCase(devtype)){
			TotalIORate = getJdbcTemplate(WebConstants.DB_TPC).query(
			String.format(sql, "A221", "PRF_TARGET_DSSYSTEM", devid, startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_SVC.equalsIgnoreCase(devtype)){
			TotalIORate = getJdbcTemplate(WebConstants.DB_TPC).query(
			String.format(sql, "A38", "PRF_TARGET_SVC_SYSTEM", devid, startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_BSP.equalsIgnoreCase(devtype)){
			TotalIORate = getJdbcTemplate(WebConstants.DB_TPC).query(
					String.format(sql, "A415", "PRF_TARGET_BSP_SYSTEM", devid, startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_HDS.equalsIgnoreCase(devtype)){
			TotalIORate = getJdbcTemplate(WebConstants.DB_DEFAULT).query(
					String.format(sql, "A106_03", "PRF_TARGET_HDS_STORAGE", devid, startDate, endDate));
		}
		else if(SrContant.DEVTYPE_VAL_EMC.equalsIgnoreCase(devtype)){
			TotalIORate = getJdbcTemplate(WebConstants.DB_DEFAULT).query(
					String.format(sql, "A112_03", "PRF_TARGET_EMC_STORAGE", devid, startDate, endDate));
		}
		return TotalIORate;
	}

	///////  2015-03-03版拓扑
	/**
	 * @see 获取所有物理机
	 */
	public List<DataRow> getAllHypervisor(long userId, String role){
		String sql;
		if(SrContant.ROLE_SUPER.equalsIgnoreCase(role)){
			sql = "SELECT h.hypervisor_id AS hyp_id,CONCAT(h.name,'(',c.ip_address,')') AS hyp_name FROM t_res_hypervisor h JOIN t_res_computersystem c ON c.COMPUTER_ID=h.HOST_COMPUTER_ID ORDER BY h.hypervisor_id;";
		}
		else {
			sql = "SELECT h.hypervisor_id AS hyp_id,CONCAT(h.name,'(',c.ip_address,')') AS hyp_name FROM t_res_hypervisor h JOIN t_res_computersystem c ON c.COMPUTER_ID=h.HOST_COMPUTER_ID and hypervisor_id IN " +
			"(SELECT FMenuId FROM tsrolemenu WHERE FRoleId IN(SELECT FRoleId FROM tsuserrole WHERE " +
			String.format(" FUserId=%s) AND fdevtype='%s') ORDER BY hypervisor_id", userId, SrContant.SUBDEVTYPE_PHYSICAL);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * @see 通过物理机编号查询到虚拟机
	 * @param hypIds
	 * @return
	 */
	public List<DataRow> getVMByHypIds(String hypIds, long userId, String role){
		String _hypIds = "";
		if(hypIds != null && hypIds.trim().length() > 0){
			_hypIds = " and h.hypervisor_id in(&) ".replace("&", hypIds);
		}
		String sql;
		if(SrContant.ROLE_SUPER.equalsIgnoreCase(role)){
			sql = String.format("SELECT h.hypervisor_id AS hyp_id,v.vm_id,concat(v.NAME,'(',c.ip_address,')') AS vm_name,h.name AS hyp_name " +
			" FROM t_res_virtualmachine v JOIN t_res_hypervisor h ON v.HYPERVISOR_ID=h.HYPERVISOR_ID %s JOIN t_res_computersystem c ON c.COMPUTER_ID=v.COMPUTER_ID ORDER BY h.hypervisor_id,v.vm_id",
			_hypIds);
		}
		else {
			sql = String.format("SELECT h.hypervisor_id AS hyp_id,v.vm_id,concat(v.NAME,'(',c.ip_address,')') AS vm_name,h.name AS hyp_name FROM t_res_virtualmachine v JOIN t_res_hypervisor h ON v.HYPERVISOR_ID=h.HYPERVISOR_ID " + 
					" %s JOIN t_res_computersystem c ON c.COMPUTER_ID=v.COMPUTER_ID and v.vm_id IN(SELECT FMenuId FROM tsrolemenu WHERE FRoleId IN(SELECT FRoleId FROM tsuserrole WHERE FUserId=%s) AND fdevtype='%s') ORDER BY h.hypervisor_id,v.vm_id",
					_hypIds, userId, SrContant.SUBDEVTYPE_VIRTUAL);
		}
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * @see 通过存储系统的编号查询到端口号
	 * @param stoTPCIds
	 * @param stoSRIds
	 * @return
	 */
	public Map<String, List<DataRow>> getSwpStopByIds(String stoTPCIds, String stoSRIds){
		String _stoTPCIds = "", _stoSRIds = "";
		Map<String, List<DataRow>> drs = new HashMap<String, List<DataRow>>(2);
		if(stoTPCIds != null && stoTPCIds.trim().length() > 0){
			_stoTPCIds = " and p.subsystem_id in (&) ".replace("&", stoTPCIds);
			JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
			
			String sql = String.format("select p.port_id as stop_id,p.the_display_name as stop_name,s.subsystem_id as sto_id," +
					"s.the_display_name as sto_name,s.the_type as sto_type,'%s' as dbtype " +
					"from v_res_port p join v_res_storage_subsystem s on" +
					" p.subsystem_id=s.subsystem_id %s ORDER BY p.port_id",
					SrContant.DBTYPE_TPC, _stoTPCIds);
			List<DataRow> data = tpc.query(sql);
			if(data != null && data.size() > 0){
				drs.put(SrContant.DBTYPE_TPC, data);
			}
		}
		if(stoSRIds != null && stoSRIds.trim().length() > 0){
			_stoSRIds = " and p.subsystem_id in (&) ".replace("&", stoSRIds);
			String sql = String.format("SELECT p.port_id AS stop_id,p.NAME AS stop_name,COALESCE(s.DISPLAY_NAME,s.name) AS sto_name," +
					"s.subsystem_id AS sto_id,s.STORAGE_TYPE AS sto_type,'%s' as dbtype FROM t_res_port p JOIN t_res_storagesubsystem s " +
					"ON s.subsystem_id=p.subsystem_id %s ORDER BY p.port_id",
					SrContant.DBTYPE_SR, _stoSRIds);
			JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
			List<DataRow> data = srDB.query(sql);
			if(data != null && data.size() > 0){
				drs.put(SrContant.DBTYPE_SR, data);
			}
		}
		return drs;
	}
	
	/**
	 * @see 通过交换机编号查询到交换机端口
	 * @param swIds
	 * @return
	 */
	public List<DataRow> getSwitchPortsBySwIds(String swIds){
		String _swIds = "";
		if(swIds != null && swIds.trim().length() > 0){
			_swIds = " and p.switch_id in(&) ".replace("&", swIds);
		}
		String sql = "SELECT device_id AS dev FROM t_map_devices WHERE DEVICE_TYPE='&' ORDER BY dev"
			.replace("&", SrContant.SUBDEVTYPE_SWITCHPORT);
		List<DataRow> drs = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		String swpIds = "";
		if(drs != null && drs.size() > 0){
			swpIds = " and p.port_id not in (&)"
				.replace("&", extractIds(drs, "dev"));
		}
		// where the_enabled_state<>'disabled'
		sql = String.format("select p.switch_id as sw_id,s.the_display_name as sw_name,p.port_id as swp_id," +
				"p.the_display_name as swp_name from v_res_switch_port p join v_res_switch s " +
				"on p.switch_id=s.switch_id and the_enabled_state<>'disabled' %s %s " +
				" order by p.switch_id,p.port_id", swpIds, _swIds);
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.replace("&", _swIds));
	}
	
	/**
	 * @see 通过物理机编号查询到交换机
	 * @param hypIds
	 * @return
	 */
	public List<DataRow> getAllSwitchByHypIds(String hypIds){
		String _hypIds = "";
		if(hypIds != null && (!StringHelper.isBlank(hypIds))){
			_hypIds = " and h.HYPERVISOR_ID in(&) ".replace("&", hypIds);
		}
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		List<DataRow> switchByHypMap = srDB.query(("SELECT s.HYPERVISOR_ID AS hyp_id,h.name AS hyp_name," +
				"s.switch_id AS sw_id,s.switch_name AS sw_name FROM t_hypervisor_switch s " +
				"JOIN t_res_hypervisor h ON s.hypervisor_id=h.hypervisor_id & ORDER BY s.HYPERVISOR_ID").replace("&", _hypIds));
		if(switchByHypMap != null && switchByHypMap.size() > 0){
			return switchByHypMap;
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(("select switch_id as sw_id," +
				"the_display_name as sw_name from v_res_switch order by switch_id"));
	}
	
	public List<DataRow> getAllSwitch(long userId, String role){
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		String sql;
		if(SrContant.ROLE_SUPER.equalsIgnoreCase(role)){
//			sql = "select switch_id as sw_id,the_display_name as sw_name from v_res_switch order by switch_id";
			sql = "SELECT switch_id AS sw_id,concat(concat(concat(the_display_name,'('),ip_address),')') AS sw_name FROM v_res_switch ORDER BY switch_id";
		}
		else {
			JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
			String ids = sa.getMenuIdsByUserId(srDB, userId, SrContant.SUBDEVTYPE_SWITCH, null, true);
			sql = String.format("select switch_id as sw_id,concat(concat(concat(the_display_name,'('),ip_address),')') as sw_name from v_res_switch where switch_id in(%s) order by switch_id", ids);
		}
		return tpc.query(sql);
	}
	
	/**
	 * @see 根据起点交换机推导出所有连接的交换机
	 * @param swIds
	 * @return
	 */
	public JSONObject getAllSwitchByIds(String swIds[], long userId, String role){
		if(swIds == null || swIds.length == 0){ return null; }
		boolean isUser = !SrContant.ROLE_SUPER.equalsIgnoreCase(role);
		TreeUtils treeUtils = new TreeUtils();
		JSONObject sws = new JSONObject();
		long data[][] = null; //treeUtils.convert(this.getSwitchMap(null));
		Set<Long> inSwIds = null;
		if(isUser){
			JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
			inSwIds = sa.getMenuIdsByUserId02(srDB, userId, SrContant.SUBDEVTYPE_SWITCH, null, true);
		}
		data = treeUtils.convert(this.getSwitchMap(null), inSwIds);
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		String fmt = "select switch_id as sw_id,concat(concat(concat(the_display_name,'('),ip_address),')') as sw_name " +
				"from v_res_switch where switch_id in(&)";
		if(isUser){  // 普通用户
			for(int i = 0, len = swIds.length; i < len; ++i){
				JSONObject obj = new JSONObject();
				obj.put("selfSws", tpc.queryMap(fmt.replace("&", swIds[i]))); // 必定被过滤了
				obj.put("otherSws", tpc.query((fmt.replace("&", treeUtils.getSwitchIds(data, Long.parseLong(swIds[i]), inSwIds)))));
				sws.put(swIds[i], obj);
			}
		}
		else { // 超级管理员
			for(int i = 0, len = swIds.length; i < len; ++i){
				JSONObject obj = new JSONObject();
				obj.put("selfSws", tpc.queryMap(fmt.replace("&", swIds[i]))); // 必定被过滤了
				obj.put("otherSws", tpc.query((
							fmt.replace("&", treeUtils.getSwitchIds(data, Long.parseLong(swIds[i]))
						)
					)));
				sws.put(swIds[i], obj);
			}
		}
		return sws;
	}
	
	public Map<String, Object> getAllStorage(long userId, String role){ // 分两部分
		String db2;
		String mysql;
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		boolean isUser = !SrContant.ROLE_SUPER.equalsIgnoreCase(role);
		if(isUser){
			mysql = String.format("SELECT subsystem_id AS sto_id,CONCAT(COALESCE(display_name,NAME),'(',ip_address,')') AS sto_name,storage_type AS sto_type,'%s' AS dbtype " +
					" FROM t_res_storagesubsystem WHERE subsystem_id IN(SELECT FMenuId FROM tsrolemenu WHERE FRoleId IN(SELECT FRoleId FROM tsuserrole WHERE FUserId=%s) " +
					" AND fdevtype='%s' AND os_type IN(%s)) ORDER BY subsystem_id", 
					SrContant.DBTYPE_SR, userId, SrContant.SUBDEVTYPE_STORAGE, "'EMC','HDS'");
			
			String ids = sa.getMenuIdsByUserId(srDB, userId, SrContant.SUBDEVTYPE_STORAGE, "'EMC','HDS'", false);
			db2 = String.format("select subsystem_id as sto_id,concat(concat(concat(the_display_name,'('),ip_address),')') as sto_name,type as sto_type,'%s' as dbtype from v_res_storage_subsystem where subsystem_id in(%s) order by subsystem_id",
				SrContant.DBTYPE_TPC, ids);
		}
		else {
			db2 = "select subsystem_id as sto_id,concat(concat(concat(the_display_name,'('),ip_address),')') as sto_name,type as sto_type,'&' as dbtype from v_res_storage_subsystem order by subsystem_id"
				.replace("&", SrContant.DBTYPE_TPC);
			
			mysql = "SELECT subsystem_id AS sto_id,CONCAT(COALESCE(display_name,NAME),'(',ip_address,')') AS sto_name,storage_type as sto_type,'&' as dbtype FROM t_res_storagesubsystem order by subsystem_id"
			.replace("&", SrContant.DBTYPE_SR);
		}
		
		List<DataRow> tpcDrs = tpc.query(db2);
		List<DataRow> srDrs = srDB.query(mysql);
		Map<String, Object> json = new HashMap<String, Object>(2);
		if(srDrs != null && srDrs.size() > 0){
			json.put(SrContant.DBTYPE_SR, srDrs);
		}
		if(tpcDrs != null && tpcDrs.size() > 0){
			json.put(SrContant.DBTYPE_TPC, tpcDrs);
		}
		return json;
	}
	
	/**
	 * @see 存储池不受角色影响
	 * @param tpcStoIds
	 * @param srDBStoIds
	 * @return
	 */
	public Map<String, Object> getPoolsByStoIds(String tpcStoIds, String srDBStoIds){
		Map<String, Object> json = new HashMap<String, Object>(2);
		String _tpcStoIds = "";
		if(tpcStoIds != null && tpcStoIds.trim().length() > 0){
			_tpcStoIds = " and p.subsystem_id in(&) ".replace("&", tpcStoIds);
			String db2 = String.format(
					"select p.subsystem_id as sto_id,p.POOL_ID,p.the_display_name as pool_name,'%s' as dbtype,"+
					"s.the_display_name as sto_name from V_RES_STORAGE_POOL p join v_res_storage_subsystem s on "+
					"p.subsystem_id=s.subsystem_id %s order by p.subsystem_id,p.POOL_ID",
					SrContant.DBTYPE_TPC, _tpcStoIds);
			List<DataRow> tpc = getJdbcTemplate(WebConstants.DB_TPC).query(db2);
			json.put(SrContant.DBTYPE_TPC, tpc);
		}
		String _srDBStoIds = "";
		if(srDBStoIds != null && srDBStoIds.trim().length() > 0){
			_srDBStoIds = " and p.subsystem_id in(&) ".replace("&", srDBStoIds);
			String mysql = String.format("SELECT p.subsystem_id AS sto_id,p.pool_id,COALESCE(p.display_name,p.NAME) AS pool_name,"+
					"'%s' AS dbtype,COALESCE(s.display_name,s.NAME) AS sto_name FROM t_res_storagepool p JOIN t_res_storagesubsystem s ON "+
					"p.SUBSYSTEM_ID=s.SUBSYSTEM_ID %s ORDER BY p.subsystem_id,p.pool_id",
				SrContant.DBTYPE_SR, _srDBStoIds);
			List<DataRow> srDB = getJdbcTemplate(WebConstants.DB_DEFAULT).query(mysql);
			json.put(SrContant.DBTYPE_SR, srDB);
		}
		return json;
	}
	
	/**
	 * @see 通过物理机端口号查询到，不必查询已经在t_map_devices使用的端口，因为端口一旦被占用，就不能重复使用第二次
	 * @param phyportIds
	 * @param isSwitch
	 * @return
	 */
	public Map<String, Object> getPhyportSwportsByIds(String phyportIds, boolean isSwitch){
		Map<String, Object> json = new HashMap<String, Object>(2);
		String ppIds = "";
		if(phyportIds != null && phyportIds.trim().length() > 0){
			ppIds = " and p.port_id IN(&) ".replace("&", phyportIds);
		}
		String ppSQL = String.format("SELECT p.port_id,p.port_name,h.HYPERVISOR_ID AS hyp_id," +
				"h.name AS hyp_name FROM t_res_physical_port p JOIN t_res_hypervisor h ON " +
				"p.HYPERVISOR_ID=h.HYPERVISOR_ID %s " +
				" AND p.port_id NOT IN(SELECT m.device_id AS devid FROM t_map_devices m WHERE m.device_type='%s')" +
				"ORDER BY hyp_id", ppIds, SrContant.SUBDEVTYPE_PHYSICALPORT);
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		List<DataRow> PHYSICALPORT = srDB.query(ppSQL);
		json.put(SrContant.SUBDEVTYPE_PHYSICALPORT, PHYSICALPORT);
		String hypIds = this.extractIds(PHYSICALPORT, "hyp_id");
		if(hypIds != null && isSwitch){
			List<DataRow> swIdsDR = srDB.query(String.format("SELECT switch_id AS sw_id FROM t_hypervisor_switch " +
					"WHERE hypervisor_id IN(%s)", hypIds));
			String swIds = this.extractIds(swIdsDR, "sw_id");
			if(swIds != null){
				json.put(SrContant.SUBDEVTYPE_SWITCHPORT, getSwitchPortsBySwIds(swIds));
			}
		}
		return json;
	}
	
	public Map<String, Object> getVolumesByPoolIds(String tpcStoIds, String srDBStoIds,
			String tpcPoolIds, String srDBPoolIds){
		Map<String, Object> json = new HashMap<String, Object>(2);
		String _tpcStoIds = "", _srDBStoIds = "", _tpcPoolIds = "", _srDBPoolIds = "";
		if(tpcStoIds != null && tpcStoIds.trim().length() > 0){
			_tpcStoIds = " and v.subsystem_id in(&) ".replace("&", tpcStoIds);
		}
		if(tpcPoolIds != null && tpcPoolIds.trim().length() > 0){
			_tpcPoolIds = " and p.pool_id in(&) ".replace("&", tpcPoolIds);
			String db2 = String.format(
					"select v.svid as vol_id,p.POOL_ID,v.the_display_name as vol_name,'%s' as dbtype," +
					"p.the_display_name as pool_name from v_res_storage_volume v join V_RES_STORAGE_POOL p " +
					"on v.pool_id=p.pool_id %s %s order by v.POOL_ID,v.svid",
					SrContant.DBTYPE_TPC, _tpcStoIds, _tpcPoolIds);
			List<DataRow> tpc = getJdbcTemplate(WebConstants.DB_TPC).query(db2);
			json.put(SrContant.DBTYPE_TPC, tpc);
		}
		
		if(srDBStoIds != null && srDBStoIds.trim().length() > 0){
			_srDBStoIds = " and v.subsystem_id in(&) ".replace("&", srDBStoIds);
		}
		if(srDBPoolIds != null && srDBPoolIds.trim().length() > 0){
			_srDBPoolIds = " and p.pool_id in(&) ".replace("&", srDBPoolIds);
			String mysql = String.format(
					"SELECT v.volume_id AS vol_id,p.POOL_ID,COALESCE(v.display_name,v.name) AS vol_name," +
					"'%s' AS dbtype,COALESCE(p.display_name,p.name) AS pool_name FROM t_res_storagevolume v " +
					"JOIN t_RES_STORAGEPOOL p ON v.pool_id=p.pool_id %s %s ORDER BY p.POOL_ID,v.volume_id",
				SrContant.DBTYPE_SR, _srDBStoIds, _srDBPoolIds);
			List<DataRow> srDB = getJdbcTemplate(WebConstants.DB_DEFAULT).query(mysql);
			json.put(SrContant.DBTYPE_SR, srDB);
		}
		return json;
	}
	
	public Map<String, Object> getPhySwStoSw(String phySwIds, String stoSwIds){
		Map<String, Object> json = new HashMap<String, Object>(2);
		JdbcTemplate tpc = this.getJdbcTemplate(WebConstants.DB_TPC);
		String sql = "select sw.the_display_name as sw_name,sw.switch_id as sw_id " +
				"from v_res_switch sw where sw.switch_id in(&) order by sw.switch_id";
		if(phySwIds != null && phySwIds.trim().length() > 0){
			json.put("phySw", tpc.query(sql.replace("&", phySwIds)));
		}
		
		if(stoSwIds != null && stoSwIds.trim().length() > 0){
			json.put("stoSw", tpc.query(sql.replace("&", stoSwIds)));
		}
		return json;
	}
	
	/**
	 * @see 通过物理机编号查询到物理机端口
	 * @param phyids
	 * @return
	 */
	public Map<String, Object> getPhyportsByPhyids(String phyids){
		Map<String, Object> json = new HashMap<String, Object>(2);
		String _ids = "", _ids1 = "";
		if(phyids != null && !StringHelper.isBlank(phyids)){
			_ids = "AND p.HYPERVISOR_ID IN (&)".replace("&", phyids);
			_ids1 = " where p.HYPERVISOR_ID IN (&)".replace("&", phyids);
		}
		JdbcTemplate srDB = this.getJdbcTemplate(WebConstants.DB_DEFAULT);
		List<DataRow> phyPorts = srDB.query(
				String.format("SELECT p.port_id,p.port_name,p.port_number," +
				"p.port_type,p.HYPERVISOR_ID AS hyp_id,h.name AS hyp_name FROM t_res_physical_port p " +
				"JOIN t_res_hypervisor h ON p.HYPERVISOR_ID=h.HYPERVISOR_ID %s " +
				"AND p.port_id NOT IN(SELECT device_id AS devid FROM t_map_devices WHERE device_type='%s')" +
				" ORDER BY p.HYPERVISOR_ID", _ids, SrContant.SUBDEVTYPE_PHYSICALPORT));
		if(phyPorts != null && phyPorts.size() > 0){
			json.put(SrContant.SUBDEVTYPE_PHYSICALPORT, phyPorts);
		}
		List<DataRow> swIdsDataRow = srDB.query(String.format("SELECT p.switch_id AS sw_id FROM " +
				"t_hypervisor_switch p %s", _ids1));
		String swIds = this.extractIds(swIdsDataRow, "sw_id");
		if(swIds != null){
			List<DataRow> swports = this.getSwitchPortsBySwIds(swIds);
			if(swports != null && swports.size() > 0){
				json.put("FrontSwitchPort", swports);
			}
		}
		return json;
	}
	
	public Long savePhyPort(DataRow phyport){
		return Long.parseLong(this.getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_physical_port", 
				phyport));
	}
	
	public void updateAppData(DataRow appData, long appId){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_app", appData, "id", appId);
	}
	
	public Long saveAppData(DataRow appData){
		return Long.parseLong(getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_app", appData));
	}
	
	public void deleteAppById(Long appId){
		if(appId != null){
			getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_app", "id", appId);
		}
	}
	
	public void deleteDevMapById(long appId){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_map_devices", "app_id", appId);
	}
	
	public long getUserIdByAppId(long appId){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryLong("select user_id from t_app where id=" + appId);
	}
	
	public List<DataRow> getSwitchBySwpIds(String swpIds){
		String sql = "select switch_id as sw_id from v_res_switch_port where port_id in(&) order by switch_id"
			.replace("&", swpIds);
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getPhysicalByPhypIds(String phypIds){
		String sql = "SELECT DISTINCT HYPERVISOR_ID AS phy_id FROM t_res_physical_port WHERE port_id IN(&) order by HYPERVISOR_ID"
			.replace("&", phypIds);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * @see 保存上传的设备映射
	 * @param params
	 */
	public void saveDeviceMap(List<List<DataRow>> params){
		if(params == null || params.size() == 0){ return; }
		JdbcTemplate srDB = this.getJdbcTemplate(WebConstants.DB_DEFAULT);
		String devMapTabName = "t_map_devices";
		List<DataRow> drs;
		for(int i = 0, size = params.size(); i < size; ++i){
			drs = params.get(i);
			if(drs != null && drs.size() > 0){
				for(int j = 0, jsize = drs.size(); j < jsize; ++j){
//					Logger.getLogger(getClass()).info(JSON.toJSONStringWithDateFormat(
//							drs.get(j), "yyyy-MM-dd HH:mm:ss"));
					srDB.insert(devMapTabName, drs.get(j));
				}
//				Logger.getLogger(getClass()).info("****************************************");
			}
			
		}
	}
	
	public List<DataRow> getSwitchAndSwitchport1(String swIds){
		return sa.getSwitchAndSwitchportFromDB2(
				this.getJdbcTemplate(WebConstants.DB_DEFAULT),
				this.getJdbcTemplate(WebConstants.DB_TPC), swIds);
	}
	
	/**
	 * @see 不过滤端口号，只针对交换机
	 * @param swIds
	 * @return
	 */
	public List<DataRow> getSwitchMap(String swIds){
		return sa.getSwitchFromDB2(this.getJdbcTemplate(WebConstants.DB_TPC), swIds);
	}
	
	/**
	 * @see 不过滤端口号，只针对交换机
	 * @param swIds
	 * @return
	 */
	public List<DataRow> getSwitchMap(JdbcTemplate tpc, String swIds){
		return sa.getSwitchFromDB2(tpc, swIds);
	}
	
	void info(Object obj, String mark){
		Logger.getLogger(getClass()).info("***********************************************************************");
		Logger.getLogger(getClass()).info(mark);
		Logger.getLogger(getClass()).info(JSON.toJSONString(obj));
		Logger.getLogger(getClass()).info("***********************************************************************");
	}
	
	/**
	 * @see 这是获取设备编号，将应用于拓扑图的编辑
	 * @param appId
	 * @return
	 */
	public Map<String, Object> getDeviceIds(long appId){
		Map<String, Object> obj = new HashMap<String, Object>(2);
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		List<DataRow> srData = sa.getDeviceMap(srDB, appId, SrContant.DBTYPE_SR, -1);
		List<DataRow> tpcData = sa.getDeviceMap(srDB, appId, SrContant.DBTYPE_TPC, -1);
		Map<String, String> srIds = sa.getDeviceIds02(srData);
		obj.put(SrContant.DBTYPE_SR, srIds);
		if(srIds.containsKey(SrContant.SUBDEVTYPE_PHYSICAL)){
			obj.put("phyDataSR", sa.getPhysicalDataFromMySQL(srDB, srIds.get(SrContant.SUBDEVTYPE_PHYSICAL)));
		}
		Map<String, String> tpcIds = sa.getDeviceIds02(tpcData);
		obj.put(SrContant.DBTYPE_TPC, tpcIds);
		DataRow appData = sa.getAppFromMySQL(srDB, appId);
		obj.put("appData", appData);
		List<DataRow> phySwMap = sa.getMapFromMySQL(srDB, appId,
				SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_SWITCH);
		if(sa.isDataListValid(phySwMap)){
			obj.put("phySwMap", phySwMap);
		}
		
		if(tpcIds.containsKey(SrContant.SUBDEVTYPE_SWITCH)){
			obj.put("swDataTPC", sa.getSwitchDataFromDB2(tpc, tpcIds.get(SrContant.SUBDEVTYPE_SWITCH)));
		}
		// 查询起点交换机与终点交换机的映射
		List<DataRow> sswEswTPCMap = sa.getMapFromMySQL(srDB, appId, SrContant.SUBDEVTYPE_STARTSWITCH, 
				SrContant.SUBDEVTYPE_ENDSWITCH);
		if(sa.isDataListValid(sswEswTPCMap)){ 
			obj.put("sswEswTPCMap", sswEswTPCMap); 
		}
		// 查询 SR和TPC的存储系统
		List<DataRow> swStoTPCMap = sa.getMapFromMySQL(srDB, appId, SrContant.SUBDEVTYPE_SWITCH, 
				SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC);
		if(sa.isDataListValid(swStoTPCMap)){ 
			obj.put("swStoTPCMap", swStoTPCMap); 
		}
		List<DataRow> swStoSRMap = sa.getMapFromMySQL(srDB, appId, SrContant.SUBDEVTYPE_SWITCH, 
				SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_SR);
		if(sa.isDataListValid(swStoSRMap)){ 
			obj.put("swStoSRMap", swStoSRMap); 
		}
		if(srIds.containsKey(SrContant.SUBDEVTYPE_STORAGE)){
			obj.put("stoDataSR", sa.getStorageDataFromMySQL(srDB, srIds.get(SrContant.SUBDEVTYPE_STORAGE)));
		}
		if(tpcIds.containsKey(SrContant.SUBDEVTYPE_STORAGE)){
			obj.put("stoDataTPC", sa.getStorageDataFromDB2(tpc, tpcIds.get(SrContant.SUBDEVTYPE_STORAGE)));
		}
		
		return obj;
	}
	
	
	SemiautoTopoService sa = new SemiautoTopoService();
	/**
	 * @see 获取拓扑图的数据
	 * @param appId
	 * @param whichLayerUnfold
	 * @param userId
	 * @param role
	 * @param isRepaint
	 * @return
	 */
	public Map<String, Object> getDeviceData(long appId, int whichLayerUnfold, long userId, String role, boolean isRepaint){
		sa.setRepaint(isRepaint); // 因为有些功能是在这个类实现的，通过传入isRepaint，使那些方法自动判断获取结果
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		/////////////////////////////////////////
		// 选择有效的appId
		boolean isUser = !SrContant.ROLE_SUPER.equalsIgnoreCase(role);
		// 先根据user_id（如果是super就不用考虑）和is_..._selected
		String sql = "select app_id from t_app_user where user_id=" + userId;
		long selectedAppId = srDB.queryLong(sql);
		if(appId > 0L){ // 说明appId有效
			if(selectedAppId <= 0L){// 说明查出来的selectedAppId无效
				DataRow dr = new DataRow();
				dr.set("app_id", appId);
				dr.set("user_id", userId);
				srDB.insert("t_app_user", dr);
			}
			else {
				if(selectedAppId != appId){ // 说明查出来的selectedAppId与前台发过来的appId不一样，就把t_app_user的app_id更新
					DataRow dr = new DataRow();
					dr.set("app_id", appId);
					srDB.update("t_app_user", dr, "user_id", userId);
				}
			}
		}
		else { // 说明appId无效
			if(selectedAppId <= 0L){// 说明查出来的selectedAppId无效
				// 既然查出来的appId无效，那么默认是表t_app字段user_id的第一个
				appId = srDB.queryLong("select id from t_app " +
						(isUser? "where user_id=" + userId : "") + 
						" order by id limit 0,1");
				DataRow dr = new DataRow();
				dr.set("app_id", appId);
				dr.set("user_id", userId);
				srDB.insert("t_app_user", dr);
			}
			else {
				appId = selectedAppId;
			}
		}
		/////////////////////////////////////////
		Map<String, Object> json = new HashMap<String, Object>(30);
		
		DataRow appData = sa.getAppFromMySQL(srDB, appId);
		// 获得应用层
		if(appData != null){
			json.put("appData", appData);
			if(isRepaint){
				int nodeCount = appData.getInt("nodecount");
				if(nodeCount > 0){ // 应该使用该节点的图标位置
					Map<String, Map<String, Float>> nodePos = getNodeDataByAppId(srDB, appId);
					if(nodePos != null){ json.put("nodePos", nodePos); }
				}
			}
		}
		else { 
			json.put("isTopoDataValid", false);
			return json;
		}
		whichLayerUnfold = SemiautoTopoService.SERVER_LAYER;
		List<DataRow> srData = sa.getDeviceMap(srDB, appId, SrContant.DBTYPE_SR, whichLayerUnfold);
		List<DataRow> tpcData = sa.getDeviceMap(srDB, appId, SrContant.DBTYPE_TPC, whichLayerUnfold);
		StringBuilder id;
		Map<String, StringBuilder> srIds = sa.getDeviceIds(srData);
		String devKey = SrContant.SUBDEVTYPE_VIRTUAL;
		if(srIds.containsKey(devKey)){
			id = srIds.get(SrContant.SUBDEVTYPE_VIRTUAL);
			json.put("vmDataSR", sa.getVMDataFromMySQL(srDB, id.toString()));
			sa.loadVirtualLogs(srDB, json, id.toString(), "vmLogs");
		}
		devKey = SrContant.SUBDEVTYPE_PHYSICAL;
		if(srIds.containsKey(devKey)){
			id = srIds.get(devKey);
			json.put("phyDataSR", sa.getPhysicalDataFromMySQL(srDB, id.toString()));
			sa.loadPhysicalLogs(srDB, json, id.toString(), "phyLogs");
		}
		devKey = SrContant.SUBDEVTYPE_PHYSICALPORT;
		if(srIds.containsKey(devKey)){
			id = srIds.get(devKey);
			json.put("phypDataSR", sa.getPhyportDataFromMySQL(srDB, id.toString()));
		}
		devKey = SrContant.SUBDEVTYPE_PORT;
		if(srIds.containsKey(devKey)){
			id = srIds.get(devKey);
			json.put("stopDataSR", sa.getStorageportDataFromMySQL(srDB, id.toString()));
			sa.loadStorageportLogs(srDB, json, id.toString(), "stopSRLogs");
		}
		devKey = SrContant.SUBDEVTYPE_STORAGE;
		if(srIds.containsKey(devKey)){
			id = srIds.get(devKey);
			json.put("stoDataSR", sa.getStorageDataFromMySQL(srDB, id.toString()));
			sa.loadStorageLogs(srDB, json, id.toString(), "stoSRLogs");
		}
		devKey = SrContant.SUBDEVTYPE_POOL;
		if(srIds.containsKey(devKey)){
			id = srIds.get(devKey);
			json.put("stoPoolDataSR", sa.getStoragepoolDataFromMySQL(srDB, id.toString()));
			sa.loadStorageLogs(srDB, json, id.toString(), "stoPoolSRLogs");
		}
		devKey = SrContant.SUBDEVTYPE_VOLUME;
		if(srIds.containsKey(devKey)){
			id = srIds.get(devKey);
			json.put("stoVolDataSR", sa.getStoragevolumeDataFromMySQL(srDB, id.toString()));
			sa.loadStorageLogs(srDB, json, id.toString(), "stoVolSRLogs");
		}
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		Map<String, StringBuilder> tpcIds = sa.getDeviceIds(tpcData);
		devKey = SrContant.SUBDEVTYPE_SWITCHPORT;
		if(tpcIds.containsKey(devKey)){
			id = tpcIds.get(devKey);
			json.put("swpDataTPC", sa.getSwitchportDataFromDB2(tpc, id.toString()));
			sa.loadSwitchPortLogs(srDB, json, id.toString(), "swpLogs");
		}
		devKey = SrContant.SUBDEVTYPE_SWITCH;
		if(tpcIds.containsKey(devKey)){
			id = tpcIds.get(devKey);
			json.put("swDataTPC", sa.getSwitchDataFromDB2(tpc, id.toString()));
			sa.loadSwitchLogs(srDB, json, id.toString(), "swLogs");
		}
		devKey = SrContant.SUBDEVTYPE_PORT;
		if(tpcIds.containsKey(devKey)){
			id = tpcIds.get(devKey);
			json.put("stopDataTPC", sa.getStorageportDataFromDB2(tpc, id.toString()));
			sa.loadStorageportLogs(srDB, json, id.toString(), "stopTPCLogs");
		}
		devKey = SrContant.SUBDEVTYPE_STORAGE;
		if(tpcIds.containsKey(devKey)){
			id = tpcIds.get(devKey);
			json.put("stoDataTPC", sa.getStorageDataFromDB2(tpc, id.toString()));
			sa.loadStorageLogs(srDB, json, id.toString(), "stoTPCLogs");
		}
		devKey = SrContant.SUBDEVTYPE_POOL;
		if(tpcIds.containsKey(devKey)){
			id = tpcIds.get(devKey);
			json.put("stoPoolDataTPC", sa.getStoragepoolDataFromDB2(tpc, id.toString()));
			sa.loadStorageLogs(srDB, json, id.toString(), "stoPoolTPCLogs");
		}
		devKey = SrContant.SUBDEVTYPE_VOLUME;
		if(tpcIds.containsKey(devKey)){
			id = tpcIds.get(devKey);
			json.put("stoVolDataTPC", sa.getStoragevolumeDataFromDB2(tpc, id.toString()));
			sa.loadStorageLogs(srDB, json, id.toString(), "stoVolTPCLogs");
		}
		// 服务器
		// 依据whichLayerUnfold判断[应用-->虚拟机-->物理机]和[应用-->物理机]
		if(whichLayerUnfold == SemiautoTopoService.SERVER_LAYER){
			// 应用-->虚拟机
			if(isRepaint){
				List<DataRow> appVMMap = sa.getMapFromMySQL(srDB, appId, SrContant.SUBDEVTYPE_APP, SrContant.SUBDEVTYPE_VIRTUAL);
				if(sa.isDataListValid(appVMMap)){ json.put("appVMMap", appVMMap); }
				// 虚拟机 --> 物理机
				List<DataRow> vmPhyMap = sa.getMapFromMySQL(srDB, appId, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.SUBDEVTYPE_PHYSICAL);
				String phyIds = null;
				if(sa.isDataListValid(vmPhyMap)){
					json.put("vmPhyMap", vmPhyMap);
					phyIds = this.extractIds(vmPhyMap, "id");
				}
				// 应用-->物理机
				List<DataRow> appPhyMap = sa.getMapFromMySQL2(srDB, appId, SrContant.SUBDEVTYPE_APP, SrContant.SUBDEVTYPE_PHYSICAL, phyIds);
				if(sa.isDataListValid(appPhyMap)){
					json.put("appPhyMap", appPhyMap);
				}
			}
			
		}
		else {
			// 应用-->物理机
			if(isRepaint){
				List<DataRow> appPhyMap = sa.getMapFromMySQL2(srDB, appId, SrContant.SUBDEVTYPE_APP, SrContant.SUBDEVTYPE_PHYSICAL, null);
				if(sa.isDataListValid(appPhyMap)){ json.put("appPhyMap", appPhyMap); }
			}
		}
		
		// SAN网络
		// 依据whichLayerUnfold判断, 获取
		/* 
		   [物理机-->物理机端口-->交换机端口-->交换机-->交换机端口-->交换机端口-->交换机-->交换机端口-->存储端口-->存储系统]
		 */
		// [物理机-->交换机-->交换机]
		if(whichLayerUnfold == SemiautoTopoService.SAN_LAYER){
			// 物理机-->物理机端口
			if(isRepaint){
				List<DataRow> phyPhypMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_PHYSICALPORT);
				if(sa.isDataListValid(phyPhypMap)){ json.put("phyPhypMap", phyPhypMap); }
				
				// 物理机端口-->交换机端口
				List<DataRow> phypSwpMap = sa.getMapFromMySQL(srDB, appId, 
						SrContant.SUBDEVTYPE_PHYSICALPORT, SrContant.SUBDEVTYPE_SWITCHPORT);
				if(sa.isDataListValid(phypSwpMap)){ json.put("phypSwpMap", phypSwpMap); }
				// 交换机端口-->交换机 == 物理机端口-->交换机端口 + 交换机端口-->交换机
				List<DataRow> swpSwMap = sa.getMapFromMySQL(srDB, appId, 
						SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_SWITCH);
				if(sa.isDataListValid(swpSwMap)){ json.put("swpSwMap", swpSwMap); }
				// 交换机-->交换机端口
				List<DataRow> swSwpMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_SWITCHPORT);
				if(sa.isDataListValid(swSwpMap)){ json.put("swSwpMap", swSwpMap); }
				// 交换机端口-->交换机端口
				List<DataRow> swpSwpMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_SWITCHPORT);
				if(sa.isDataListValid(swpSwpMap)){ json.put("swpSwpMap", swpSwpMap); }
				// 交换机端口-->存储端口(有一部分要从MySQL拿，有一部分要从DB2拿)
				List<DataRow> swpStopSRMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_PORT, SrContant.DBTYPE_SR);
				if(sa.isDataListValid(swpStopSRMap)){ json.put("swpStopSRMap", swpStopSRMap); }
				
				List<DataRow> swpStopTPCMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_PORT, SrContant.DBTYPE_TPC);
				if(sa.isDataListValid(swpStopTPCMap)){ json.put("swpStopTPCMap", swpStopTPCMap); }
				// 存储端口-->存储系统
				List<DataRow> stopStoTPCMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_PORT, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC);
				if(sa.isDataListValid(stopStoTPCMap)){
					json.put("stopStoTPCMap", stopStoTPCMap);
				}
				List<DataRow> stopStoSRMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_PORT, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_SR);
				if(sa.isDataListValid(stopStoSRMap)){ json.put("stopStoSRMap", stopStoSRMap); }
			}
		}
		else {
			if(isRepaint){
				// 物理机-->交换机
				List<DataRow> phySwMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_SWITCH);
				if(sa.isDataListValid(phySwMap)){
					json.put("phySwMap", phySwMap);
				}
				// 交换机-->交换机
				List<DataRow> swSwMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_SWITCH);
				if(sa.isDataListValid(swSwMap)){ json.put("swSwMap", swSwMap); }
				// 交换机-->存储系统
				List<DataRow> swStoTPCMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC);
				if(sa.isDataListValid(swStoTPCMap)){ json.put("swStoTPCMap", swStoTPCMap); }
				
				List<DataRow> swStoSRMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_SR);
				if(sa.isDataListValid(swStoSRMap)){ json.put("swStoSRMap", swStoSRMap); }
			}
		}
		whichLayerUnfold = SemiautoTopoService.STORAGE_LAYER; // 设定
		// 存储系统
		if(whichLayerUnfold == SemiautoTopoService.STORAGE_LAYER){
			if(isRepaint){
				// 存储系统-->存储池
				List<DataRow> stoPoolTPCMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_STORAGE, SrContant.SUBDEVTYPE_POOL, SrContant.DBTYPE_TPC);
				if(sa.isDataListValid(stoPoolTPCMap)){
					json.put("stoPoolTPCMap", stoPoolTPCMap);
				}
				
				List<DataRow> stoPoolSRMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_STORAGE, SrContant.SUBDEVTYPE_POOL, SrContant.DBTYPE_SR);
				if(sa.isDataListValid(stoPoolSRMap)){
					json.put("stoPoolSRMap", stoPoolSRMap);
				}
				
				// 存储池-->存储卷
				List<DataRow> stoVolTPCMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_POOL, SrContant.SUBDEVTYPE_VOLUME, SrContant.DBTYPE_TPC);
				if(sa.isDataListValid(stoVolTPCMap)){
					json.put("stoVolTPCMap", stoVolTPCMap);
				}
				List<DataRow> stoVolSRMap = sa.getMapFromMySQL(srDB, appId,
						SrContant.SUBDEVTYPE_POOL, SrContant.SUBDEVTYPE_VOLUME, SrContant.DBTYPE_SR);
				if(sa.isDataListValid(stoVolSRMap)){
					json.put("stoVolSRMap", stoVolSRMap);
				}
			}
		}
		return json;
	}
	
	/**
	 * @see 
	 * @param devId
	 * @param devType
	 * @param second
	 * @param osType
	 * @param dbType
	 * @param isPerfOnly
	 * @return
	 */
	public Map<String, Object> getDeviceInfo(final long devId, int devType, int second, String osType,
			String dbType, boolean isPerfOnly){
		Map<String, Object> json = new HashMap<String, Object>(4);
		JdbcTemplate srDB = super.getJdbcTemplate(WebConstants.DB_DEFAULT);
		JdbcTemplate tpc = super.getJdbcTemplate(WebConstants.DB_TPC);
		boolean isTPC = SrContant.DBTYPE_TPC.equalsIgnoreCase(dbType);
		boolean isCfgLogs = !isPerfOnly;
		switch(devType){
		case 2:
			if(isCfgLogs){
				json.put("vmSRCfg", sa.getVMDeviceInfoFromMySQL(srDB, devId));
				json.put("vmLogs", sa.queryLogs02(srDB, String.valueOf(devId), SrContant.SUBDEVTYPE_VIRTUAL));
			}
			loadServerPerf(srDB, json, devId, SrContant.SUBDEVTYPE_VIRTUAL, second);
			break;
		case 3:
			if(isCfgLogs){
				json.put("phySRCfg", sa.getPhyDeviceInfoFromMySQL(srDB, devId));
				json.put("phyLogs", sa.queryLogs02(srDB, String.valueOf(devId), 
						SrContant.SUBDEVTYPE_PHYSICAL));
			}
			loadServerPerf(srDB, json, devId, SrContant.SUBDEVTYPE_PHYSICAL, second);
			break;
		case 4: 
			if(isCfgLogs){
				json.put("swTPCCfg", sa.getSwitchDeviceInfoFromDB2(tpc, devId));
				json.put("swLogs", sa.queryLogs02(srDB, String.valueOf(devId), SrContant.SUBDEVTYPE_SWITCH));
			}
			loadSwitchPerf(tpc, json, devId, second);
			break;
		case 5:
			if(isCfgLogs){
				if(isTPC){
					json.put("stoTPCCfg", sa.getStorageDeviceInfoFromDB2(tpc, devId));
				}
				else {
					json.put("stoSRCfg", sa.getStorageDeviceInfoFromMySQL(srDB, devId));
				}
				json.put("stoLogs", sa.queryLogs02(srDB, String.valueOf(devId), SrContant.SUBDEVTYPE_STORAGE));
			}
			loadStoragePerf(tpc, srDB, json, devId, second, osType);
			break;
		case 6:
			if(isTPC){
				json.put("poolTPCCfg", sa.getPoolDeviceInfoFromDB2(tpc, devId));
			}
			else {
				json.put("poolSRCfg", sa.getPoolDeviceInfoFromMySQL(srDB, devId));
			}
			json.put("poolLogs", sa.queryLogs02(srDB, String.valueOf(devId), SrContant.SUBDEVTYPE_POOL));
			break;
		case 7:
			if(isCfgLogs){
				if(isTPC){
					json.put("volTPCCfg", sa.getVolumeDeviceInfoFromDB2(tpc, devId));
				}
				else {
					json.put("volSRCfg", sa.getVolumeDeviceInfoFromMySQL(srDB, devId));
				}
				json.put("volLogs", sa.queryLogs02(srDB, String.valueOf(devId), SrContant.SUBDEVTYPE_VOLUME));
			}
			loadVolumePerf(tpc, srDB, json, devId, second, osType);
			break;
		}
		return json;
	}
	
	/**@see 
	 	<%--应用必须有app_id --%>
		<%--虚拟机必须有vm_id(t_res_virtualmachine), vm_name, ip_address, hyp_id, comp_id --%>
		<%--虚拟机连接物理机必须有vm_id, hyp_id(t_res_hypervisor), hyp_name, ip_address, comp_id --%>
		<%--物理机必须有hyp_id(t_res_hypervisor), hyp_name, ip_address, comp_id --%>
		<%--物理机连交换机必须有sw_id(v_res_switch), hyp_id(t_res_hypervisor), sw_name, hyp_name, sw_ip--%>
		<%--交换机必须有sw_id1(v_res_switch), sw_id2(v_res_switch), sw_name1, sw_name2, sw_ip1, sw_ip2--%>
		<%--存储系统必须有sto_id(v_res_storagesubsystem), sw_id(v_res_switch), sto_name, sto_ip, os_type, comp_id --%>
		<%--存储系统必须有sto_id(v_res_storagesubsystem), sw_id(v_res_switch), sto_name, sw_name, sto_ip, sw_ip--%>
	 	有四层：应用层、服务器层、SAN网络层和存储层
	 * @param appId
	 * @param devType
	 * @param devId
	 * @return
	 */
	@Deprecated
	public Map<String, Object> getTopoData(long appId, String devtype){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		Map<String, Object> json = new HashMap<String, Object>(20);
//		boolean isTopoDataValid = true;
		DataRow appData = sa.getAppFromMySQL(srDB, appId);
		// 获得应用层
		if(appData != null){
			json.put("appData", appData);
//			boolean isGetAllData = appData.getInt("nodecount") == 0; // 说明没有记录图标节点节点的X和Y
		}
		else { 
			json.put("isTopoDataValid", false);
			return json;
		}
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		int whichLayerUnfold = 0; // 默认不显示任意一层的部件
		if(SrContant.SUBDEVTYPE_PHYSICAL.equalsIgnoreCase(devtype)){ whichLayerUnfold = 1; }
		else if(SrContant.SUBDEVTYPE_SWITCH.equalsIgnoreCase(devtype)){ whichLayerUnfold = 2; }
		else if(SrContant.SUBDEVTYPE_STORAGE.equalsIgnoreCase(devtype)){ whichLayerUnfold = 3; }

		// 服务器
		// 依据whichLayerUnfold判断[应用-->虚拟机-->物理机]和[应用-->物理机]
		if(whichLayerUnfold == 1){
			// 应用-->虚拟机
			List<DataRow> appVMMapData = sa.getAppVMMapDataFromMySQL(srDB, appId, null);
			if(sa.isDataListValid(appVMMapData)){
				json.put("appVMMapData", appVMMapData);
				// 虚拟机有事件
				sa.loadVirtualLogs(srDB, json, this.extractIds(appVMMapData, "vm_id"), "vmLogs");
			}
			// 虚拟机 --> 物理机
			List<DataRow> VMPhyMapData = sa.getVMPhyMapDataFromMySQL(srDB, appId);
			String phyIds = null;
			if(sa.isDataListValid(VMPhyMapData)){
				json.put("VMPhyMapData", VMPhyMapData);
				phyIds = this.extractIds(VMPhyMapData, "hyp_id");
				sa.loadPhysicalLogs(srDB, json, phyIds, "vmPhyLogs");
			}
			// 应用-->物理机
			List<DataRow> appPhyMapData = sa.getAppPhyMapDataFromMySQL(srDB, appId, phyIds);
			if(sa.isDataListValid(appPhyMapData)){
				json.put("appPhyMapData", appPhyMapData);
				sa.loadPhysicalLogs(srDB, json, this.extractIds(appPhyMapData, "hyp_id"), "appPhyLogs");
			}
		}
		else {
			// 应用-->物理机
			List<DataRow> appPhyMapData = sa.getAppPhyMapDataFromMySQL(srDB, appId, null);
			if(sa.isDataListValid(appPhyMapData)){
				json.put("appPhyMapData", appPhyMapData);
				sa.loadPhysicalLogs(srDB, json, this.extractIds(appPhyMapData, "hyp_id"), "appPhyLogs");
			}
		}
		
		// SAN网络
		// 依据whichLayerUnfold判断, 获取
		/* 
		   [物理机-->物理机端口-->交换机端口-->交换机-->交换机端口-->交换机端口-->交换机-->交换机端口-->存储端口-->存储系统]
		 */
		// [物理机-->交换机-->交换机]
		if(whichLayerUnfold == 2){
			// 物理机-->物理机端口
			List<DataRow> phyPhypMapData = sa.getPhyPortMapDataFromMySQL(srDB, appId);
			if(sa.isDataListValid(phyPhypMapData)){ json.put("phyPhypMapData", phyPhypMapData); } // 物理机端口没有事件
			// 物理机端口-->交换机端口
			List<DataRow> phypSwpMap = sa.getMapFromMySQL(srDB, appId, 
					SrContant.SUBDEVTYPE_PHYSICALPORT, SrContant.SUBDEVTYPE_SWITCHPORT);
			if(sa.isDataListValid(phypSwpMap)){
				json.put("phypSwpMap", phypSwpMap);
//				因为在[交换机端口-->交换机]重复了
				String swpIds = extractIds(phypSwpMap, "id");
				json.put("phypSwpData", sa.getSwitchportDataFromDB2(tpc, swpIds));
				// 因为在[交换机端口-->交换机]重复了
				sa.loadSwitchPortLogs(srDB, json, swpIds, "phypSwpLogs");
			}
			// 交换机端口-->交换机 == 物理机端口-->交换机端口 + 交换机端口-->交换机
			List<DataRow> swpSwMap = sa.getMapFromMySQL(srDB, appId, 
					SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_SWITCH);
			if(sa.isDataListValid(swpSwMap)){
				json.put("swpSwMap", swpSwMap);
//				StringBuilder ids[] = extractIds(swpSwMap, "id", "pid");
//				if(ids[0] != null) {
//					Map<String, DataRow> swpSwData = sa.getSwitchDataFromDB2(tpc, ids[0].toString());
//					if(sa.isDataMapValid(swpSwData)){ json.put("swpSwData", swpSwData); }
//					sa.loadSwitchLogs(srDB, json, ids[0].toString(), "swpSwLogs");
//				}
//				if(ids[1] != null) {
//					json.put("pSwpSwData", sa.getSwitchportDataFromDB2(tpc, ids[1].toString()));
//					sa.loadSwitchPortLogs(srDB, json, ids[1].toString(), "pSwpSwLogs");
//				}
			}
			// 交换机-->交换机端口
			List<DataRow> swSwpMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_SWITCHPORT);
			if(sa.isDataListValid(swSwpMap)){
				json.put("swSwpMap", swSwpMap);
				String swpIds = extractIds(swSwpMap, "id");
				sa.loadSwitchPortLogs(srDB, json, swpIds, "swSwpLogs");
				Map<String, DataRow> swSwpData = sa.getSwitchportDataFromDB2(tpc, swpIds);
				if(sa.isDataMapValid(swSwpData)){ json.put("swSwpData", swSwpData); }
			}
			// 交换机端口-->交换机端口
			List<DataRow> swpSwpMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_SWITCHPORT);
			if(sa.isDataListValid(swpSwpMap)){
				json.put("swpSwpMap", swpSwpMap);
				// 没有事件因为swSwp和swpSw分别包含交换机端口
				// 也没有数据因为swSwp和swpSw分别包含交换机端口数据
			}
			// 交换机端口-->存储端口(有一部分要从MySQL拿，有一部分要从DB2拿)
			List<DataRow> swpStopSRMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_PORT, SrContant.DBTYPE_SR);
			if(sa.isDataListValid(swpStopSRMap)){
				json.put("swpStopSRMap", swpStopSRMap);
				String stopIds = extractIds(swpStopSRMap, "id");
				Map<String, DataRow> swpSwpSRData = sa.getStorageportDataFromMySQL(srDB, stopIds);
				if(sa.isDataMapValid(swpSwpSRData)){ json.put("swpSwpSRData", swpSwpSRData); }
				sa.loadStorageportLogs(srDB, json, stopIds, "swpStopSRLogs");
			}
			
			List<DataRow> swpStopTPCMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_SWITCHPORT, SrContant.SUBDEVTYPE_PORT, SrContant.DBTYPE_TPC);
			if(sa.isDataListValid(swpStopTPCMap)){
				json.put("swpStopTPCMap", swpStopTPCMap);
				String stopIds = extractIds(swpStopTPCMap, "id");
				Map<String, DataRow> swpSwpTPCData = sa.getStorageportDataFromDB2(tpc, stopIds);
				if(sa.isDataMapValid(swpSwpTPCData)){ json.put("swpSwpTPCData", swpSwpTPCData); }
				sa.loadStorageportLogs(srDB, json, stopIds, "swpStopTPCLogs");
			}
			// 存储端口-->存储系统
			List<DataRow> stopStoTPCMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_PORT, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC);
			if(sa.isDataListValid(stopStoTPCMap)){
				json.put("stopStoTPCMap", stopStoTPCMap);
				String stoTPCIds = extractIds(stopStoTPCMap, "id");
				Map<String, DataRow> stopStoTPCData = sa.getStorageDataFromDB2(tpc, stoTPCIds);
				if(sa.isDataMapValid(stopStoTPCData)){
					json.put("stopStoTPCData", stopStoTPCData);
				}
				sa.loadStorageLogs(srDB, json, stoTPCIds, "stopStoTPCLogs");
			}
			List<DataRow> stopStoSRMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_PORT, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_SR);
			if(sa.isDataListValid(stopStoSRMap)){
				json.put("stopStoSRMap", stopStoSRMap);
				String stoSRIds = extractIds(stopStoSRMap, "id");
				Map<String, DataRow> stopStoSRData = sa.getStorageDataFromMySQL(srDB, stoSRIds);
				if(sa.isDataMapValid(stopStoSRData)){
					json.put("stopStoSRData", stopStoSRData);
				}
				sa.loadStorageLogs(srDB, json, stoSRIds, "stopStoSRLogs");
			}
		}
		else {
			// 物理机-->交换机
			List<DataRow> phySwMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_SWITCH);
			if(sa.isDataListValid(phySwMap)){
				json.put("phySwMap", phySwMap);
				String swIds = extractIds(phySwMap, "id");
				sa.loadSwitchLogs(srDB, json, swIds, "phySwLogs");
				Map<String,DataRow> phySwData = sa.getSwitchDataFromDB2(tpc, swIds);
				if(sa.isDataMapValid(phySwData)){ json.put("phySwData", phySwData); }
			}
			// 交换机-->交换机
			List<DataRow> swSwMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_SWITCH);
			if(sa.isDataListValid(swSwMap)){
				json.put("swSwMap", swSwMap);
				String swIds = extractIds(swSwMap, "id");
				sa.loadSwitchLogs(srDB, json, swIds, "swSwLogs");
				Map<String,DataRow> swSwData = sa.getSwitchDataFromDB2(tpc, swIds);
				if(sa.isDataMapValid(swSwData)){ json.put("swSwData", swSwData); }
			}
			// 交换机-->存储系统
			List<DataRow> swStoTPCMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC);
			if(sa.isDataListValid(swStoTPCMap)){
				json.put("swStoTPCMap", swStoTPCMap);
				String stoTPCIds = extractIds(swStoTPCMap, "id");
				Map<String, DataRow> swStoTPCData = sa.getStorageDataFromDB2(tpc, stoTPCIds);
				if(sa.isDataMapValid(swStoTPCData)){ json.put("swStoTPCData", swStoTPCData); }
				sa.loadStorageLogs(srDB, json, stoTPCIds, "swStoTPCLogs");
			}
			
			List<DataRow> swStoSRMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_SR);
			if(sa.isDataListValid(swStoSRMap)){
				json.put("swStoSRMap", swStoSRMap);
				String stoSRIds = extractIds(swStoSRMap, "id");
				Map<String, DataRow> swStoSRData = sa.getStorageDataFromMySQL(srDB, stoSRIds);
				if(sa.isDataMapValid(swStoSRData)){ json.put("swStoSRData", swStoSRData); }
				sa.loadStorageLogs(srDB, json, stoSRIds, "swStoSRLogs");
			}
		}
		// 存储系统
		if(whichLayerUnfold == 2){
			// 存储系统-->存储池
			List<DataRow> stoPoolTPCMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_STORAGE, SrContant.SUBDEVTYPE_POOL, SrContant.DBTYPE_TPC);
			if(sa.isDataListValid(stoPoolTPCMap)){
				json.put("stoPoolTPCMap", stoPoolTPCMap);
				String poolTPCIds = extractIds(stoPoolTPCMap, "id");
				Map<String, DataRow> stoPoolTPCData = sa.getStoragepoolDataFromDB2(tpc, poolTPCIds);
				if(sa.isDataMapValid(stoPoolTPCData)){ json.put("stoPoolTPCData", stoPoolTPCData); }
				sa.loadStorageLogs(srDB, json, poolTPCIds, "stoPoolTPCLogs");
			}
			
			List<DataRow> stoPoolSRMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_STORAGE, SrContant.SUBDEVTYPE_POOL, SrContant.DBTYPE_SR);
			if(sa.isDataListValid(stoPoolSRMap)){
				json.put("stoPoolSRMap", stoPoolSRMap);
				String poolSRIds = extractIds(stoPoolSRMap, "id");
				Map<String, DataRow> swStoSRData = sa.getStoragepoolDataFromMySQL(srDB, poolSRIds);
				if(sa.isDataMapValid(swStoSRData)){ json.put("swStoSRData", swStoSRData); }
				sa.loadStorageLogs(srDB, json, poolSRIds, "swStoSRLogs");
			}
			
			// 存储池-->存储卷
			List<DataRow> stoVolTPCMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_POOL, SrContant.SUBDEVTYPE_VOLUME, SrContant.DBTYPE_TPC);
			if(sa.isDataListValid(stoVolTPCMap)){
				json.put("stoVolTPCMap", stoVolTPCMap);
				String volTPCIds = extractIds(stoVolTPCMap, "id");
				Map<String, DataRow> stoVolTPCData = sa.getStoragevolumeDataFromMySQL(srDB, volTPCIds);
				if(sa.isDataMapValid(stoVolTPCData)){ json.put("stoVolTPCData", stoVolTPCData); }
				sa.loadStorageLogs(srDB, json, volTPCIds, "stoVolTPCLogs");
			}
			List<DataRow> stoVolSRMap = sa.getMapFromMySQL(srDB, appId,
					SrContant.SUBDEVTYPE_POOL, SrContant.SUBDEVTYPE_VOLUME, SrContant.DBTYPE_SR);
			if(sa.isDataListValid(stoVolSRMap)){
				json.put("stoVolSRMap", stoVolSRMap);
				String volSRIds = extractIds(stoVolSRMap, "id");
				Map<String, DataRow> stoVolSRData = sa.getStoragevolumeDataFromMySQL(srDB, volSRIds);
				if(sa.isDataMapValid(stoVolSRData)){ json.put("stoVolSRData", stoVolSRData); }
				sa.loadStorageLogs(srDB, json, volSRIds, "stoVolSRLogs");
			}
		}
		return json;
	}
	
	public Boolean isAppNameExists(String appName){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		return srDB.queryInt("SELECT COUNT(id) AS c FROM t_app WHERE NAME='"+appName+"'") > 0;
	}
	
	public String extractIds(List<DataRow> data, String idKey){
		if(data == null || data.size() <= 0){ return null; }
		StringBuilder ids = new StringBuilder(data.size() * 5);
		Set<Long> idSet = new HashSet<Long>(data.size());
		Long temp;
		for(DataRow dr : data){
			temp = dr.getLong(idKey);
			if(!idSet.contains(temp)){
				ids.append(temp);
				ids.append(',');
				idSet.add(temp);
			}
		}
		int last = ids.length() - 1;
		if(last >= 0 && ids.charAt(last) == ','){ ids.deleteCharAt(last); }
		if(ids.length() == 0){ return null; }
		return ids.toString();
	}
	
	public StringBuilder[] extractIds(List<DataRow> data, String ...idKeys){
		if(data == null || data.size() <= 0){ return null; }
		if(idKeys == null || idKeys.length == 0){ return null; }
		StringBuilder ids[] = new StringBuilder[idKeys.length];
		Map<String, Set<Long>> idSet = new HashMap<String, Set<Long>>(idKeys.length);
		for(int i = 0; i < idKeys.length; ++i){
			ids[i] = new StringBuilder(data.size() * 5);
			idSet.put(idKeys[i], new HashSet<Long>(data.size()));
		}
		Long temp;
		for(int i = 0, len = data.size(), j, idKeysLen = idKeys.length; i < len; ++i){
			for(j = 0; j < idKeysLen; ++j){
				temp = data.get(i).getLong(idKeys[j]);
				if(!idSet.get(idKeys[j]).contains(temp)){
					ids[j].append(temp);
					ids[j].append(',');
					idSet.get(idKeys[j]).add(temp);
				}
			}
			
		}
		for(int i = 0; i < idKeys.length; ++i){
			if(ids[i].length() - 1 >= 0 && ids[i].charAt(ids[i].length() - 1) == ','){
				ids[i].deleteCharAt(ids[i].length() - 1);
			}
			if(ids[i].length() == 0){ ids[i].append("-1"); }
		}
		
		return ids;
	}
}


