package com.huiming.service.deviceSnmp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.project.ipnetwork.TCfgDeviceSnmp;
import com.project.web.WebConstants;

@SuppressWarnings("unchecked")
public class DeviceSnmpService extends BaseService {
	
	public void savePortPerfData(List<DataRow> perfs, List<DataRow> timestamps) throws Exception {
		JdbcTemplate mySql = getJdbcTemplate(WebConstants.DB_DEFAULT);
		if(timestamps != null && timestamps.size() > 0) {
			String tableName = "t_prf_timestamp";
			for(DataRow timestamp : timestamps) {
				mySql.insert(tableName, timestamp);
			}
		}
		if(perfs != null && perfs.size() > 0) {
			String tableName = "t_nw_prf_port";
			for(DataRow perf : perfs) {
				mySql.insert(tableName, perf);
			}
		}
	}
	
	/**
	 * @see 保存端口配置数据
	 * @param portConfigs
	 * @throws Exception
	 */
	public void savePortConfigData(List<DataRow> portConfigs) throws Exception {
		if(portConfigs != null && portConfigs.size() > 0) {
			JdbcTemplate jdbc = getJdbcTemplate(WebConstants.DB_DEFAULT);
			DataRow temp;
			String sqlFmt = "select port_id from t_nw_res_port where switch_id=%s and port_number=%s";
			for(DataRow portCfg : portConfigs) {
				temp = jdbc.queryMap(String.format(sqlFmt, portCfg.getInt("switch_id"), portCfg.getInt("port_number")));
				if(temp != null && temp.getInt("port_id") > 0) {
					jdbc.update("t_nw_res_port", portCfg, "port_id", temp.getInt("port_id"));
				}
				else {
					jdbc.insert("t_nw_res_port", portCfg);
				}
			}
		}
	}
	/**
	 * @see 不需要更新，但是要插入t_prf_timestamp
	 * @param data
	 * @throws Exception
	 */
	public void saveIpSwitchPerData(DataRow data, int snmpId) throws Exception {
		JdbcTemplate mySql = getJdbcTemplate(WebConstants.DB_DEFAULT);
		DataRow dr0 = mySql.queryMap("select switch_id,switch_name from t_nw_res_switch where snmp_id=" + snmpId);
		int switchId = 0;
		if(dr0 != null) { switchId = dr0.getInt("switch_id"); }
		if(switchId > 0) {
			DataRow dr = new DataRow();
			Date date = new Date();
			dr.set("sample_time", date);
			dr.set("interval_len", 300);
			dr.set("summ_type", SrContant.SUMM_TYPE_REAL);
			dr.set("subsystem_name", dr0.getString("switch_name"));
			dr.set("subsystem_id", switchId);
			dr.set("device_type", SrContant.SUBDEVTYPE_IPNW_SWITCH);
			mySql.insert("t_prf_timestamp", dr);
			
			data.set("time_id", date.getTime());
			data.set("switch_id", switchId);
			mySql.insert("t_nw_prf_switch", data);
		}
	}
	
	/**
	 * @see 需要更新
	 * @param data
	 * @throws Exception
	 */
	public void saveIpSwitchConfigData(DataRow data) throws Exception {
		JdbcTemplate mySql = getJdbcTemplate(WebConstants.DB_DEFAULT);
		int switchId = mySql.queryInt("select switch_id from t_nw_res_switch where snmp_id=" + data.getInt("snmp_id"));
		if(switchId > 0) {
			mySql.update("t_nw_res_switch", data, "switch_id", switchId);
		}
		else {
			mySql.insert("t_nw_res_switch", data);
		}
	}
	
	public List<DataRow> getDeviceSnmp() {
		StringBuilder sql = new StringBuilder(200);
		sql.append("select a.*,b.group_name,b.polling_interval_minute,b.polling_interval_hour,b.polling_interval_day,c.device_name,c.device_model ");
		sql.append("from t_nw_res_device_snmp a join t_res_group b");
		sql.append(" on a.group_id=b.group_id ");
		JdbcTemplate mySql = getJdbcTemplate(WebConstants.DB_DEFAULT);
		return mySql.query(sql.toString());
	}
	
	public List<TCfgDeviceSnmp> getDeviceSnmp(Integer groupId, String deviceType, String deviceModel) {
		StringBuilder sql = new StringBuilder(200);
		sql.append("select a.*,b.group_name,b.polling_interval_minute,c.device_type,c.device_model ");
		sql.append("from t_nw_res_device_snmp a join t_nw_res_group b on a.group_id=b.group_id and a.enabled=1 ");
		if(groupId != null && groupId > 0) {
			sql.append(" and a.group_id=" + groupId);
		}
		sql.append(" join t_nw_res_device c on a.device_id=c.device_id ");
		if(deviceType != null && deviceType.trim().length() > 0) {
			sql.append(" and c.device_type='" + deviceType + "' ");
		}
		if(deviceModel != null && deviceModel.trim().length() > 0) {
			sql.append(" and c.device_model='" + deviceModel + "'");
		}
		sql.append(" order by a.snmp_id");
		JdbcTemplate mySql = getJdbcTemplate(WebConstants.DB_DEFAULT);
		List<DataRow> drs = mySql.query(sql.toString());
		if(drs != null && drs.size() > 0) {
			List<TCfgDeviceSnmp> snmps = new ArrayList<TCfgDeviceSnmp>(drs.size());
			for(DataRow dr : drs) {
				TCfgDeviceSnmp snmp = new TCfgDeviceSnmp();
				copy(snmp, dr);
				snmps.add(snmp);
			}
			return snmps;
		}
		return new ArrayList<TCfgDeviceSnmp>(0);
	}
	
	public Map<Integer, List<DataRow>> getEntityData(String devIds, int idCount, String deviceEntityClass) {
		JdbcTemplate mySql = getJdbcTemplate(WebConstants.DB_DEFAULT);
		StringBuilder sql = new StringBuilder(100);
		sql.append("select * from t_nw_res_entity where enabled=1 ");
		if(devIds != null && devIds.trim().length() > 0) {
			sql.append(" and device_id in(");
			sql.append(devIds);
			sql.append(") ");
		}
		if(deviceEntityClass != null && deviceEntityClass.trim().length() > 0) {
			sql.append(" and device_entity_class='");
			sql.append(deviceEntityClass);
			sql.append('\'');
		}
		sql.append(" order by entity_index");  // 这个很重要
		List<DataRow> drs = mySql.query(sql.toString());
		Map<Integer, List<DataRow>> map = new HashMap<Integer, List<DataRow>>(idCount);

		List<DataRow> datas = null;
		for(int i = 0, l = drs.size(), devId; i < l; ++i) {
			devId = drs.get(i).getInt("device_id");
			if(!map.containsKey(devId)) {
				datas = new ArrayList<DataRow>(30); // 实际上这个有26个字段
				map.put(devId, datas);
			}
			datas.add(drs.get(i));
		}
		return map;
	}
	
	private void copy(TCfgDeviceSnmp snmp, DataRow dr) {
		snmp.setSnmpId(dr.getInt("snmp_id"));
		snmp.setGroupId(dr.getInt("group_id"));
		snmp.setDeviceId(dr.getInt("device_id"));
		snmp.setIpAddressV4(dr.getString("ip_address_v4"));
		snmp.setIpAddressV6(dr.getString("ip_address_v6"));
		snmp.setSnmpVersion(dr.getString("snmp_version"));
		snmp.setSnmpPort(dr.getInt("snmp_port"));
		snmp.setSnmpCommunity(dr.getString("snmp_community"));
		snmp.setSnmpV3UserName(dr.getString("snmp_v3_user_name"));
		snmp.setSnmpV3OptionalName(dr.getString("snmp_v3_optional_name"));
		snmp.setSnmpV3AuthProtocal(dr.getString("snmp_v3_auth_protocal"));
		snmp.setSnmpV3AuthPasswd(dr.getString("snmp_v3_auth_passwd"));
		snmp.setSnmpV3EncryptProtocal(dr.getString("snmp_v3_encrypt_protocal"));
		snmp.setSnmpV3EncryptPasswd(dr.getString("snmp_v3_encrypt_passwd"));
		snmp.setSnmpTimeout(dr.getInt("snmp_timeout"));
		snmp.setSnmpRetry(dr.getInt("snmp_retry"));
		snmp.setDescription(dr.getString("description"));
		String str = dr.getString("device_type");
		if(str != null){ snmp.setDeviceType(str); }
		str = dr.getString("device_model");
		if(str != null){ snmp.setDeviceModel(str); }
	}
	
	public List<DataRow> getPortBySnmpId(int snmpId){
		// port_number
		String sql = "SELECT port_id,switch_id,interface_index FROM t_nw_res_port WHERE snmp_id=" + snmpId;
		return this.getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public DataRow getSwitchBySnmpId(int snmpId){
		String sql = "SELECT switch_id,port_count,switch_name FROM t_nw_res_switch WHERE snmp_id=" + snmpId;
		return this.getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	public DataRow getGroupById(int id) throws Exception {
		String sql = "SELECT * FROM t_nw_res_group WHERE group_id=" + id;
		return this.getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	public void saveSnmpGroup(DataRow data) throws Exception {
		this.getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_nw_res_group", data);
	}
	
	public void delSnmpGroup(int id) throws Exception {
		this.getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_nw_res_group", "group_id", id);
	}
	
	public void deleteSnmpDetail(int snmpId) throws Exception {
		this.getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_nw_res_device_snmp", "snmp_id", snmpId);
	}
	
	public DataRow getSnmpInfoById(int snmpId) throws Exception {
		StringBuilder sql = new StringBuilder(200);
		sql.append("select a.*,b.group_name,b.polling_interval_minute,b.polling_interval_hour,b.polling_interval_day,c.device_type,c.device_model ");
		sql.append("from t_nw_res_device_snmp a join t_nw_res_group b on a.group_id=b.group_id ");
		sql.append(" join t_nw_res_device c on a.device_id=c.device_id and a.snmp_id=");
		sql.append(snmpId);
		return this.getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql.toString());
	}
	
	public void updateSnmpGroup(DataRow data, int groupId) throws Exception {
		this.getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_nw_res_group", data, "group_id", groupId);
	}
	
	public void saveSnmpInfo(DataRow data) throws Exception {
		this.getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_nw_res_device_snmp", data);
	}
	
	public void updateSnmpInfo(DataRow data, int snmpId) throws Exception {
		this.getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_nw_res_device_snmp", data, "snmp_id", snmpId);
	}
	
	public boolean isIpAddressExist(String ipAddr) {
		String sql = "SELECT COUNT(1) FROM t_nw_res_device_snmp WHERE ip_address_v4='"+ipAddr+"'";
		return this.getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql) > 0;
	}
	
	public List<DataRow> getGroupsIdNames() throws Exception {
		JdbcTemplate jdbc = getJdbcTemplate(WebConstants.DB_DEFAULT);
		String sql = "SELECT group_id,group_name FROM t_nw_res_group order by group_id";
		return jdbc.query(sql);
	}
	
	public List<DataRow> getDeviceIdNames() throws Exception {
		JdbcTemplate jdbc = getJdbcTemplate(WebConstants.DB_DEFAULT);
		String sql = "SELECT * FROM t_nw_res_device order by device_id";
		return jdbc.query(sql);
	}
}



