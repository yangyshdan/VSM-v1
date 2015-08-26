package com.huiming.service.x86server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.security.AES;
import com.project.web.WebConstants;
import com.project.x86monitor.IPMIInfo;

@SuppressWarnings("unchecked")
public class X86ServerService extends BaseService {
	
	
	/**
	 * @see 获得交换机和交换机
	 * @return
	 */
	public List<DataRow> getServers(Integer id, Boolean state, String osType){
		String sql = "SELECT id,NAME,ip_address,USER,PASSWORD,state,vendor,model,impersonation,authentication,toptype FROM t_server where 1=1 ";
		if(id != null){
			sql += " and id=" + id;
		}
		if(state != null){
			sql += " and state=" + (state? 1:0);
		}
		if(osType != null && osType.trim().length() > 0){
			sql += " and os_type='" + osType + "'";
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public List<DataRow> getSwitches(Integer serverId){
		String sql = "SELECT id,server_id,switch_id,switch_name FROM t_server_switch where server_id=" + serverId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public Integer countAllServer(){
		String sql = "select count(id) as id from t_server";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql);
	}
	
	/*public Long getHypIdByIP(String ip){
		String sql = "SELECT b.* FROM t_cfg_bmc b JOIN t_res_hypervisor h ON b.hypervisor_id=h.hypervisor_id AND h.host_computer_id IN(SELECT computer_id FROM t_res_computersystem WHERE ip_address LIKE '%" + ip + "%')";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryLong(sql);
	}*/
	
	
	public IPMIInfo convert(DataRow dr) {
		if(dr == null){ return null; }
		IPMIInfo ipmi = new IPMIInfo();
//		Logger.getLogger(getClass()).info(JSON.toJSON(dr));
		ipmi.setHypervisorId(dr.getLong("hypervisor_id"));
		ipmi.setUserName(dr.getString("user_name"));
		ipmi.setPassword(dr.getString("session_pwd"));
		ipmi.setIpAddress(dr.getString("ip_address"));
		ipmi.setPort(dr.getString("port"));
		ipmi.setLevel(dr.getInt("level"));
		ipmi.setAuthType(dr.getInt("auth_type"));
		ipmi.setHypervisorName(dr.getString("name"));
		return ipmi;
	}
	/**
	 * @see
	 * @return
	 */
	public Map<String, IPMIInfo> getBMCs(){
		// 不要“No access”类型的，因为不可能访问
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		String sql = " SELECT b.*,h.name FROM t_cfg_bmc b JOIN t_res_hypervisor h ON b.hypervisor_id=h.hypervisor_id AND b.level<>9";
		List<DataRow> drs = srDB.query(sql);
		if(drs != null && drs.size() > 0){
			Map<String, IPMIInfo> ipmis = new HashMap<String, IPMIInfo>(drs.size());
			AES aes = new AES();
			for(DataRow dr : drs){
				aes.setKey(dr.getString("id"));
				dr.set("session_pwd", aes.decrypt(dr.getString("session_pwd"), "utf-8"));
				ipmis.put(dr.getString("hypervisor_id"), convert(dr));
			}
			return ipmis;
		}
		return new HashMap<String, IPMIInfo>(0);
	}
}


