package com.huiming.service.topo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.sr.constants.SrContant;

public class SemiautoTopoService {
	public static final int SERVER_LAYER = 2;
	public static final int SAN_LAYER = 3;
	public static final int STORAGE_LAYER = 4;
	private boolean isRepaint;
//	private boolean hasDB2 = Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null;
//	private List<DataRow> emptyList = new ArrayList<DataRow>(0);
	
	/**
	 * 
	 * @param srDB
	 * @param userId
	 * @param devType
	 * @param osType
	 * @param isOsTypeIn true表示osType in('EMC')
	 * @return
	 */
	public String getMenuIdsByUserId(JdbcTemplate srDB, long userId, String devType, String osType, boolean isOsTypeIn){
		String dt = "";
		if(!(devType == null || devType.trim().isEmpty())){
			dt = "AND FDevType='" + devType + "'";
		}
		String ot = "";
		if(!(osType == null || osType.trim().isEmpty())){
			if(isOsTypeIn){
				ot = "AND os_type in(" + osType + ")";
			}
			else {
				ot = "AND os_type not in(" + osType + ")";
			}
		}
		String sql = String.format("SELECT distinct fmenuId as fmi FROM tsrolemenu WHERE FRoleId IN(SELECT FRoleId FROM tsuserrole WHERE FUserId=%s) %s %s ORDER BY FMenuId", userId, dt, ot);
		StringBuilder ids = new StringBuilder(200);
		@SuppressWarnings("unchecked")
		List<DataRow> drs = srDB.query(sql);
		if(drs != null && drs.size() > 0){
			for(int i = 0, size = drs.size() - 1; i < size; ++i){
				ids.append(drs.get(i).getString("fmi"));
				ids.append(',');
			}
			ids.append(drs.get(drs.size() - 1).getString("fmi"));
		}
		else {
			return "-1";
		}
		return ids.toString();
	}
	
	/**
	 * 
	 * @param srDB
	 * @param userId
	 * @param devType
	 * @param osType
	 * @param isOsTypeIn true表示osType in('EMC')
	 * @return
	 */
	public Set<Long> getMenuIdsByUserId02(JdbcTemplate srDB, long userId, String devType, String osType, boolean isOsTypeIn){
		String dt = "";
		if(!(devType == null || devType.trim().isEmpty())){
			dt = "AND FDevType='" + devType + "'";
		}
		String ot = "";
		if(!(osType == null || osType.trim().isEmpty())){
			if(isOsTypeIn){
				ot = "AND os_type in(" + osType + ")";
			}
			else {
				ot = "AND os_type not in(" + osType + ")";
			}
		}
		String sql = String.format("SELECT distinct fmenuId as fmi FROM tsrolemenu WHERE FRoleId IN(SELECT FRoleId FROM tsuserrole WHERE FUserId=%s) %s %s ORDER BY FMenuId", userId, dt, ot);
		Set<Long> ids = new HashSet<Long>(200);
		@SuppressWarnings("unchecked")
		List<DataRow> drs = srDB.query(sql);
		if(drs != null && drs.size() > 0){
			for(int i = 0, size = drs.size() - 1; i < size; ++i){
				ids.add(drs.get(i).getLong("fmi"));
			}
			ids.add(drs.get(drs.size() - 1).getLong("fmi"));
		}
		return ids;
	}
	
	public interface IDeviceIdHandler{
		DataRow handler(JdbcTemplate srDB, JdbcTemplate tpc, String devType, long devId, String dbType);
	}
	public Map<String, Map<String, DataRow>> getDeviceIds2(JdbcTemplate srDB, JdbcTemplate tpc, 
			List<DataRow> data, IDeviceIdHandler h){
		if(data == null || data.size() == 0){ return null; }
		Map<String, Map<String, DataRow>> ids = new HashMap<String, Map<String, DataRow>>(12);
		String pStr, str;
		String pid, id;
		String dbType;
		Map<String, DataRow> sb;
		DataRow dr = null;
		for(DataRow dat : data){
			pStr = dat.getString("ptype");
			str = dat.getString("type");
			pid = dat.getString("pid");
			id = dat.getString("id");
			dbType = dat.getString("dbt");
			if(ids.containsKey(pStr)){
				sb = ids.get(pStr);
				if(!sb.containsKey(pid)){
					dr = h.handler(srDB, tpc, pStr, dat.getLong("pid"), dbType);
					if(dr != null){ sb.put(pid, dr); }
				}
			}
			else {
				sb = new HashMap<String, DataRow>(30);
				dr = h.handler(srDB, tpc, pStr, dat.getLong("pid"), dbType);
				if(dr != null){ sb.put(pid, dr); ids.put(pStr, sb); }
			}
			if(ids.containsKey(str)){
				sb = ids.get(str);
				if(!sb.containsKey(id)){
					dr = h.handler(srDB, tpc, str, dat.getLong("id"), dbType);
					if(dr != null){ sb.put(id, dr); }
				}
			}
			else {
				sb = new HashMap<String, DataRow>(30);
				dr = h.handler(srDB, tpc, str, dat.getLong("id"), dbType);
				if(dr != null){ sb.put(id, dr); ids.put(str, sb); }
			}
		}
		return ids;
	}
	
	/**
	 * @see 获取所有设备类型的ID
	 * @param data
	 * @return
	 */
	public Map<String, StringBuilder> getDeviceIds(List<DataRow> data){
		if(data == null || data.size() == 0){ return null; }
		Map<String, StringBuilder> ids = new HashMap<String, StringBuilder>(20);
		String pStr, str;
		String pid, id;
		StringBuilder sb;
		for(DataRow dat : data){
			pStr = dat.getString("ptype");
			str = dat.getString("type");
			pid = dat.getString("pid");
			id = dat.getString("id");
			if(ids.containsKey(pStr)){
				sb = ids.get(pStr);
				if(sb.indexOf(pid) < 0){
					sb.append(pid);
					sb.append(',');
				}
			}
			else {
				sb = new StringBuilder(100);
				sb.append(pid);
				sb.append(',');
				ids.put(pStr, sb);
			}
			if(ids.containsKey(str)){
				sb = ids.get(str);
				if(sb.indexOf(id) < 0){
					sb.append(id);
					sb.append(',');
				}
			}
			else {
				sb = new StringBuilder(100);
				sb.append(id);
				sb.append(',');
				ids.put(str, sb);
			}
		}
		int last;
		for(String key : ids.keySet()){
			sb = ids.get(key);
			last = sb.length() - 1;
			if(last >= 0 && sb.charAt(last) == ','){ sb.deleteCharAt(last); }
		}
		return ids;
	}
	
	public Map<String, String> getDeviceIds02(List<DataRow> data){
		if(data == null || data.size() == 0){ return null; }
		Map<String, StringBuilder> ids = new HashMap<String, StringBuilder>(20);
		String pStr, str;
		String pid, id;
		StringBuilder sb;
		for(DataRow dat : data){
			pStr = dat.getString("ptype");
			str = dat.getString("type");
			pid = dat.getString("pid");
			id = dat.getString("id");
			if(ids.containsKey(pStr)){
				sb = ids.get(pStr);
				if(sb.indexOf(pid) < 0){
					sb.append(pid);
					sb.append(',');
				}
			}
			else {
				sb = new StringBuilder(100);
				sb.append(pid);
				sb.append(',');
				ids.put(pStr, sb);
			}
			if(ids.containsKey(str)){
				sb = ids.get(str);
				if(sb.indexOf(id) < 0){
					sb.append(id);
					sb.append(',');
				}
			}
			else {
				sb = new StringBuilder(100);
				sb.append(id);
				sb.append(',');
				ids.put(str, sb);
			}
		}
		Map<String, String> _ids = new HashMap<String, String>(20);
		int last;
		for(String key : ids.keySet()){
			sb = ids.get(key);
			last = sb.length() - 1;
			if(last >= 0 && sb.charAt(last) == ','){ sb.deleteCharAt(last); }
			_ids.put(key, sb.toString());
		}
		return _ids;
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceMap(JdbcTemplate srDB, long appId, String dbType, int whichLayerUnfold){
		List<String> types = new ArrayList<String>(8);
		types.add(SrContant.SUBDEVTYPE_APP);
		types.add(SrContant.SUBDEVTYPE_PHYSICAL);
		types.add(SrContant.SUBDEVTYPE_SWITCH);
		types.add(SrContant.SUBDEVTYPE_STORAGE);
		
		types.add(SrContant.SUBDEVTYPE_VIRTUAL);
		types.add(SrContant.SUBDEVTYPE_POOL);
		types.add(SrContant.SUBDEVTYPE_VOLUME);
		/*
		switch(whichLayerUnfold){
		case SERVER_LAYER:
			types.add(SrContant.SUBDEVTYPE_VIRTUAL);
			break;
		case SAN_LAYER:
			types.add(SrContant.SUBDEVTYPE_PHYSICALPORT);
			types.add(SrContant.SUBDEVTYPE_SWITCHPORT);
			types.add(SrContant.SUBDEVTYPE_PORT);
			break;
		case STORAGE_LAYER:
			types.add(SrContant.SUBDEVTYPE_POOL);
			types.add(SrContant.SUBDEVTYPE_VOLUME);
			break;
		}*/
		StringBuilder sbTypes = new StringBuilder(50);
		int size = types.size() - 1;
		for(int i = 0; i < size; ++i){
			sbTypes.append('\'');
			sbTypes.append(types.get(i));
			sbTypes.append('\'');
			sbTypes.append(',');
		}
		sbTypes.append('\'');
		sbTypes.append(types.get(size));
		sbTypes.append('\'');
		return srDB.query(String.format("SELECT parent_device_type AS ptype,parent_device_id AS pid,device_type AS TYPE," +
				"device_id AS id FROM t_map_devices WHERE db_type='%s' and app_id=%s " +
				"and parent_device_type in(%s) and device_type in(%s)", dbType, appId, sbTypes, sbTypes));
	}
	
	public DataRow getAppFromMySQL(JdbcTemplate srDB, Long appId){
		return srDB.queryMap(String.format(
				"SELECT a.id AS app_id,a.name AS appname,a.description AS appdesc,"+
				"node_count as nodecount,'%s' as devtype FROM t_app a WHERE a.id=%s", 
				SrContant.SUBDEVTYPE_APP, appId));
	}
	
	/**
	 * @see 检验data是否有效
	 * @param data
	 * @return true表示data不为空且DataRow个数超过0
	 */
	public boolean isDataListValid(List<DataRow> data){
		return data != null && data.size() > 0;
	}
	/**
	 * @see 检验data是否有效
	 * @param data
	 * @return true表示data不为空且DataRow个数超过0
	 */
	public boolean isDataMapValid(Map<String,DataRow> data){
		return data != null && data.size() > 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVMPhyMapDataFromMySQL(JdbcTemplate srDB, Long appId){
		return srDB.query(String.format("SELECT m.parent_device_id AS pid,m.device_id AS id," +
				"h.hypervisor_id AS hyp_id,h.name AS hyp_name,c.ip_address AS hyp_ip," +
				"c.DISK_SPACE/1024.0 AS total,(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used," +
				"c.DISK_AVAILABLE_SPACE/1024.0 AS available,c.computer_id AS comp_id," +
				"m.device_type as devtype FROM t_map_devices m JOIN t_res_hypervisor h " +
				"ON m.device_id=h.hypervisor_id AND m.app_id=%s AND m.parent_device_type='%s' " +
				"and m.device_type='%s' JOIN t_res_computersystem c ON h.host_computer_id=c.computer_id " +
				"ORDER BY m.device_id",
				appId, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.SUBDEVTYPE_PHYSICAL));
	}
	
	public void loadPhysicalLogs(JdbcTemplate srDB, Map<String, Object> json, String hypIds, String logKey){
		if(hypIds != null){
			Map<String, List<DataRow>> logs = queryLogs(srDB, hypIds, SrContant.SUBDEVTYPE_PHYSICAL);
			if(logs != null && logs.size() > 0){ json.put(logKey, logs); }
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAppVMMapDataFromMySQL(JdbcTemplate srDB, Long appId, Long devId){
		return srDB.query(String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,v.name as vm_name,v.vm_id,"+
				"c.DISK_SPACE/1024.0 AS total,(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used," +
				"c.DISK_AVAILABLE_SPACE/1024.0 AS available,v.hypervisor_id AS hyp_id," +
				"v.computer_id AS comp_id,c.ip_address AS vm_ip,m.device_type as devtype " +
				"FROM t_map_devices m JOIN t_res_virtualmachine v ON m.device_id=v.vm_id " +
				"AND m.app_id=%s AND m.parent_device_type='%s' AND m.device_type='%s' " +
				" %s JOIN t_res_computersystem c ON c.computer_id=v.computer_id",
					appId, SrContant.SUBDEVTYPE_APP, SrContant.SUBDEVTYPE_VIRTUAL,
					(devId != null && devId > 0L? " AND v.hypervisor_id=" + devId : "")
				));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getVMDataFromMySQL(JdbcTemplate srDB, String vmIds){
		if(vmIds != null && vmIds.trim().length() > 0){
			List<DataRow> data = srDB.query(String.format("SELECT v.name AS vm_name,v.vm_id,c.DISK_SPACE/1024.0 AS total," +
					"(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used,c.DISK_AVAILABLE_SPACE/1024.0 AS available," +
					"v.hypervisor_id AS hyp_id,v.computer_id AS comp_id,c.ip_address AS vm_ip,'%s' AS db_type " +
					"FROM t_res_virtualmachine v JOIN t_res_computersystem c ON c.computer_id=v.computer_id AND v.vm_id IN(%s)",
					SrContant.DBTYPE_SR, vmIds));
			Map<String, DataRow> myData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ myData.put(dr.getString("vm_id"), dr); }
			return myData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getPhysicalDataFromMySQL(JdbcTemplate srDB, String phyIds){
		if(phyIds != null && phyIds.trim().length() > 0){
			List<DataRow> data = srDB.query(String.format(
					"SELECT h.hypervisor_id AS hyp_id,h.name AS hyp_name,c.ip_address AS hyp_ip," +
					"c.DISK_SPACE/1024.0 AS total,(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used," +
					"c.DISK_AVAILABLE_SPACE/1024.0 AS available,c.computer_id AS comp_id FROM t_res_hypervisor h " +
					"JOIN t_res_computersystem c ON h.host_computer_id=c.computer_id " +
					"AND h.hypervisor_id IN(%s) ORDER BY h.hypervisor_id", phyIds));
			Map<String, DataRow> myData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ myData.put(dr.getString("hyp_id"), dr); }
			return myData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getPhyportDataFromMySQL(JdbcTemplate srDB, String phypIds){
		if(phypIds != null && phypIds.trim().length() > 0){
			List<DataRow> data = srDB.query(String.format(
					"SELECT p.hypervisor_id AS hyp_id,p.port_id,p.port_name,p.port_number,p.port_type "+
					" FROM t_res_physical_port p WHERE p.port_id IN(%s) ORDER BY p.port_id", phypIds));
			Map<String, DataRow> myData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ myData.put(dr.getString("port_id"), dr); }
			data = null;
			return myData;
		}
		return null;
	}
	
	/**
	 * @see 查询应用到物理机
	 * @param srDB
	 * @param appId
	 * @param phyIds 不需要查询哪些物理机
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getAppPhyMapDataFromMySQL(JdbcTemplate srDB, Long appId, String phyIds){
		String _phyIds = "";
		if(phyIds != null && phyIds.trim().length() > 0){
			_phyIds = "and h.hypervisor_id not in(&)".replace("&", phyIds);
		}
		return srDB.query(String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,h.hypervisor_id AS hyp_id," +
				"h.name AS hyp_name,c.ip_address AS hyp_ip,c.DISK_SPACE/1024.0 AS total," +
				"(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used,c.DISK_AVAILABLE_SPACE/1024.0 AS available," +
				"c.computer_id AS comp_id,m.device_type AS devtype FROM t_map_devices m " +
				"JOIN t_res_hypervisor h ON m.device_id=h.hypervisor_id %s AND m.app_id=%s " +
				"AND m.parent_device_type='%s' AND m.device_type='%s' JOIN t_res_computersystem c " + 
				"ON h.host_computer_id=c.computer_id ORDER BY m.device_id",
				_phyIds, appId, SrContant.SUBDEVTYPE_APP, SrContant.SUBDEVTYPE_PHYSICAL)
				);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPhyPortMapDataFromMySQL(JdbcTemplate srDB, Long appId){
		return srDB.query(String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype," +
				"p.hypervisor_id AS hyp_id,p.port_id,p.port_name,p.port_number,p.port_type " +
				"FROM t_map_devices m JOIN t_res_physical_port p ON m.device_id=p.port_id " +
				"AND m.app_id=%s AND m.parent_device_type='%s' AND m.device_type='%s' " +
				"ORDER BY m.device_id",
					appId, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_PHYSICALPORT)
				);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getMapFromMySQL(JdbcTemplate srDB, Long appId, String pDevType, String devType){
		return srDB.query(String.format(
			"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype,m.db_type " +
			"FROM t_map_devices m WHERE m.app_id=%s and m.parent_device_type='%s' AND " +
			"m.device_type='%s' ORDER BY m.device_id", appId, pDevType, devType));
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getMapFromMySQL2(JdbcTemplate srDB, Long appId, String pDevType, 
			String devType, String ids){
		String _ids = "";
		if(ids != null && ids.trim().length() > 0){
			_ids = " and m.device_id not in(&) ".replace("&", ids);
		}
		return srDB.query(String.format(
			"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype,m.db_type " +
			"FROM t_map_devices m WHERE m.app_id=%s and m.parent_device_type='%s' AND " +
			"m.device_type='%s' %s ORDER BY m.device_id", appId, pDevType, devType, _ids));
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getMapFromMySQL(JdbcTemplate srDB, Long appId, String pDevType,
			String devType, String dbType){
		return srDB.query(String.format(
			"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype,m.db_type " +
			"FROM t_map_devices m WHERE m.app_id=%s and m.parent_device_type='%s' AND " +
			"m.device_type='%s' and m.db_type='%s' ORDER BY m.device_id", appId, pDevType, devType, dbType));
	}
	
	public void loadVirtualLogs(JdbcTemplate srDB, Map<String, Object> json, String vmIds, String logKey){
		if(vmIds != null){
			Map<String, List<DataRow>> logs = queryLogs(srDB, vmIds, SrContant.SUBDEVTYPE_VIRTUAL);
			if(logs != null && logs.size() > 0){ json.put(logKey, logs); }
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPhySwMapFromMySQL(JdbcTemplate srDB, Long appId){
		return srDB.query(String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type as devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s AND m.parent_device_type='%s'" +
				" AND m.device_type='%s' ORDER BY m.device_id",
				appId, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_SWITCH));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getSwitchDataFromDB2(JdbcTemplate tpc, String swIds){
		if(swIds != null){
			List<DataRow> data = tpc.query(String.format(
					"select switch_id as sw_id,the_display_name as sw_name,ip_address as sw_ip," +
					"the_operational_status as oper_status,the_propagated_status as prop_status," +
					"the_consolidated_status as cons_status,switch_wwn as sw_wwn from v_res_switch where switch_id in (%s) order by switch_id", swIds));
			Map<String, DataRow> myData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ myData.put(dr.getString("sw_id"), dr); }
			return myData;
		}
		return null;
	}
	
	/**
	 * @see 将查询到的交换机事件装载进
	 * @param srDB
	 * @param json
	 * @param swIds
	 */
	public void loadSwitchLogs(JdbcTemplate srDB, Map<String, Object> json, String swIds, String key){
		if(swIds != null){
			Map<String, List<DataRow>> logs = queryLogs(srDB, swIds, swIds, SrContant.SUBDEVTYPE_SWITCH);
			if(logs != null && logs.size() > 0){ json.put(key, logs); }
		}
	}
	
	public void loadSwitchPortLogs(JdbcTemplate srDB, Map<String, Object> json, String portIds, 
			String key){
		if(portIds != null){
			Map<String, List<DataRow>> logs = queryLogs(srDB, portIds, SrContant.SUBDEVTYPE_SWITCHPORT);
			if(logs != null && logs.size() > 0){ json.put(key, logs); }
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getSwitchportDataFromDB2(JdbcTemplate tpc, String portIds){
		if(portIds != null && portIds.trim().length() > 0){
			List<DataRow> data =  tpc.query(String.format(
					"select switch_id as sw_id,port_id,the_display_name as port_name,the_port_speed as port_speed, " +
					"the_operational_status as oper_status,the_consolidated_status as consolidated," +
					"the_type as port_type,port_number " +
					" from v_res_switch_port where port_id in(%s) order by port_id", portIds));
			Map<String, DataRow> swPortData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ swPortData.put(dr.getString("port_id"), dr); }
			return swPortData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwSwMapFromMySQL(JdbcTemplate srDB, Long appId){
		return srDB.query(String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type as devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s AND m.parent_device_type='%s'" +
				" AND m.device_type='%s' ORDER BY m.device_id",
				appId, SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_SWITCH));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getSwportStoDataFromDB2(JdbcTemplate tpc, String portIds){
		if(portIds != null && portIds.trim().length() > 0){
			List<DataRow> data =  tpc.query(String.format(
					"select port_id,the_display_name as port_name,the_port_speed as port_speed, " +
					"the_operational_status as oper_status,the_consolidated_status as consolidated," +
					"the_type as port_type,port_number " +
					" from v_res_switch_port where port_id in(%s) order by port_id", portIds));
			Map<String, DataRow> backSwPortData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ backSwPortData.put(dr.getString("port_id"), dr); }
			return backSwPortData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwStoMapFromMySQL(JdbcTemplate srDB, Long appId){
		return srDB.query(String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type as devtype,m.db_type " +
				"FROM t_map_devices m WHERE m.app_id=%s AND m.parent_device_type='%s' AND m.device_type='%s'" +
				" AND m.db_type='%s' ORDER BY m.device_id",
				appId, SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getSwStoDataFromDB2(JdbcTemplate tpc, String stoIds){
		if(stoIds != null && stoIds.trim().length() > 0){
			List<DataRow> data =  tpc.query(String.format(
					"select subsystem_id as sto_id,the_display_name as sto_name,ip_address as sto_ip," +
					"os_type,type as sto_type,the_operational_status as oper_status " +
					" from v_res_storage_subsystem where subsystem_id in(%s) order by subsystem_id", stoIds));
			Map<String, DataRow> swStoData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ swStoData.put(dr.getString("sto_id"), dr); }
			return swStoData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStorageDataFromMySQL(JdbcTemplate srDB, String stoIds){
		if(stoIds != null && stoIds.trim().length() > 0){
			List<DataRow> data =  srDB.query(String.format(
					"SELECT subsystem_id AS sto_id,COALESCE(DISPLAY_NAME,NAME) AS sto_name,ip_address AS sto_ip," +
					"'%s' AS db_type,OPERATIONAL_STATUS AS oper_status,TOTAL_USABLE_CAPACITY/1024.0 AS total," +
					"(TOTAL_USABLE_CAPACITY - UNALLOCATED_USABLE_CAPACITY)/1024.0 AS used," +
					"UNALLOCATED_USABLE_CAPACITY/1024.0 AS available,storage_type AS os_type FROM t_res_storagesubsystem " +
					"WHERE subsystem_id IN(%s) ORDER BY subsystem_id",
					SrContant.DBTYPE_SR, stoIds));
			Map<String, DataRow> swStoData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ swStoData.put(dr.getString("sto_id"), dr); }
			return swStoData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStorageDataFromDB2(JdbcTemplate tpc, String stoIds){
		if(stoIds != null && stoIds.trim().length() > 0){
			List<DataRow> data =  tpc.query(String.format(
					"select subsystem_id as sto_id,the_display_name as sto_name,ip_address as sto_ip," +
					"'%s' as db_type,the_operational_status as oper_status,the_storage_pool_space as total," +
					"the_storage_pool_consumed_space as used,the_storage_pool_available_space as available,os_type " +
					" from v_res_storage_subsystem where subsystem_id in(%s) order by subsystem_id",
					SrContant.DBTYPE_TPC, stoIds));
			Map<String, DataRow> swStoData = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ swStoData.put(dr.getString("sto_id"), dr); }
			return swStoData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStorageportDataFromDB2(JdbcTemplate tpc, String stopIds){
		if(stopIds != null && stopIds.trim().length() > 0){
			List<DataRow> data =  tpc.query(String.format(
					"select port_id as stop_id,subsystem_id as sto_id,the_display_name as stop_name,the_port_speed as port_speed," +
					"THE_OPERATIONAL_STATUS as oper_status,'%s' as db_type from v_res_port " +
					"where port_id in(%s) order by port_id",
					SrContant.DBTYPE_TPC, stopIds));
			Map<String, DataRow> map = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ map.put(dr.getString("stop_id"), dr); }
			return map;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStorageportDataFromMySQL(JdbcTemplate srDB, String stopIds){
		if(stopIds != null && stopIds.trim().length() > 0){
			List<DataRow> data = srDB.query(String.format(
					"SELECT port_id AS stop_id,subsystem_id as sto_id,NAME AS stop_name,PORT_SPEED," +
					"OPERATIONAL_STATUS AS oper_status'%s' as db_type FROM t_res_port " +
					"WHERE port_id IN(%s) ORDER BY port_id",
					SrContant.DBTYPE_SR, stopIds));
			Map<String, DataRow> map = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ map.put(dr.getString("stop_id"), dr); }
			return map;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStoragevolumeDataFromMySQL(JdbcTemplate srDB, String volIds){
		if(volIds != null && volIds.trim().length() > 0){
			List<DataRow> data = srDB.query(String.format(
					"SELECT s.subsystem_id as sto_id,s.STORAGE_TYPE AS os_type,v.volume_id AS vol_id," +
					"COALESCE(v.display_name,v.name) AS vol_name,v.logical_CAPACITY/1024.0 AS total," +
					"v.OPERATIONAL_STATUS AS oper_status,v.raid_level AS redundancy,'%s' as db_type " +
					"FROM t_res_storagevolume v JOIN t_res_storagesubsystem s ON v.subsystem_id=s.subsystem_id and v.volume_id IN(%s) ORDER BY v.volume_id",
					SrContant.DBTYPE_SR, volIds));
			Map<String, DataRow> map = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ map.put(dr.getString("vol_id"), dr); }
			return map;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStoragevolumeDataFromDB2(JdbcTemplate tpc, String volIds){
		if(volIds != null && volIds.trim().length() > 0){
			List<DataRow> data = tpc.query(String.format("SELECT s.subsystem_id as sto_id,s.os_type,v.svid AS vol_id,v.the_display_name AS vol_name,v.the_capacity AS total," +
					"v.the_used_space AS used,v.the_operational_status AS oper_status,v.the_redundancy AS " +
					"redundancy,'%s' AS db_type FROM v_res_storage_volume v JOIN v_res_storage_subsystem s " +
					"on s.subsystem_id=v.subsystem_id AND v.svid IN (%s) ORDER BY v.svid",
					SrContant.DBTYPE_TPC, volIds));
			Map<String, DataRow> map = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ map.put(dr.getString("vol_id"), dr); }
			return map;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStoragepoolDataFromMySQL(JdbcTemplate srDB, String poolIds){
		if(poolIds != null && poolIds.trim().length() > 0){
			List<DataRow> data = srDB.query(String.format(
					"SELECT p.subsystem_id as sto_id,s.STORAGE_TYPE AS os_type,p.pool_id,COALESCE(p.display_name,p.name) AS pool_name," +
					"p.TOTAL_USABLE_CAPACITY/1024.0 AS total,(p.TOTAL_USABLE_CAPACITY-p.UNALLOCATED_CAPACITY)/1024.0 AS used," +
					"p.UNALLOCATED_CAPACITY/1024.0 AS available,p.OPERATIONAL_STATUS AS oper_status,v.num_lun,'%s' as db_type FROM t_res_storagepool p " +
					" JOIN (SELECT COUNT(volume_id) AS num_lun,pool_id FROM t_res_storagevolume GROUP BY pool_id) v " +
					" ON v.pool_id=p.POOL_ID AND p.POOL_ID IN(%s) JOIN t_res_storagesubsystem s ON p.subsystem_id=s.subsystem_id ORDER BY p.pool_id",
					SrContant.DBTYPE_SR, poolIds));
			Map<String, DataRow> map = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ map.put(dr.getString("pool_id"), dr); }
			return map;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> getStoragepoolDataFromDB2(JdbcTemplate tpc, String poolIds){
		if(poolIds != null && poolIds.trim().length() > 0){
			List<DataRow> data = tpc.query(String.format(
					"select p.subsystem_id as sto_id,p.pool_id,p.the_display_name as pool_name," +
					"p.the_space as total,p.the_consumed_space as used,p.the_available_space as available,'%s' as db_type," +
					"p.the_operational_status as oper_status,v.num_lun,s.os_type from v_res_storage_pool p " +
					" join v_res_storage_subsystem s on s.subsystem_id=p.subsystem_id " +
					"join (select count(v.svid) as num_lun,v.pool_id from v_res_storage_volume v " +
					"group by v.pool_id) v on p.pool_id=v.pool_id and p.pool_id in(%s) " +
					"order by p.pool_id", SrContant.DBTYPE_TPC, poolIds));
			Map<String, DataRow> map = new HashMap<String, DataRow>(data.size());
			for(DataRow dr : data){ map.put(dr.getString("pool_id"), dr); }
			return map;
		}
		return null;
	}
	
	public void loadStorageLogs(JdbcTemplate srDB, Map<String, Object> json, String stoIds, String logKey){
		if(stoIds != null){
			Map<String, List<DataRow>> logs = queryLogs(srDB, stoIds, stoIds, SrContant.SUBDEVTYPE_STORAGE);
			if(logs != null && logs.size() > 0){ json.put(logKey, logs); }
		}
	}
	
	public void loadStorageportLogs(JdbcTemplate srDB, Map<String, Object> json, String stopIds, String logKey){
		if(stopIds != null){
			Map<String, List<DataRow>> logs = queryLogs(srDB, stopIds, SrContant.SUBDEVTYPE_PORT);
			if(logs != null && logs.size() > 0){ json.put(logKey, logs); }
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStoMapDataFromMySQL(JdbcTemplate srDB, long appId, String pDevType){
		return srDB.query(String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.db_type,m.device_type AS devtype," +
				" COALESCE(s.display_name,s.name) AS sto_name,s.ip_address AS sto_ip,s.subsystem_id AS sto_id, " +
				" s.storage_type as sto_type,s.OPERATIONAL_STATUS AS oper_status" +
				" FROM t_map_devices m JOIN t_res_storagesubsystem s ON m.device_id=s.subsystem_id AND " +
				" m.app_id=%s AND m.parent_device_type='%s' AND m.device_type='%s' AND m.db_type='%s' " +
				" ORDER BY m.device_id", appId, pDevType, SrContant.SUBDEVTYPE_STORAGE,
				SrContant.DBTYPE_SR));
	}
	
	/**
	 * @see 查询事件日志
	 * @param jdbc
	 * @param ftopid
	 * @param fresourceid
	 * @param ftoptype
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<DataRow>> queryLogs(JdbcTemplate jdbc, String ftopid, String fresourceid,
			String ftoptype){
		String fresid = "";
		if(fresourceid != null && fresourceid.trim().length() != 0){
			fresid = " AND d.fresourceid IN(" + fresourceid + ") ";
		}
		if(ftopid != null && ftoptype != null && ftopid.trim().length() != 0 && ftoptype.trim().length() != 0){
			String sql = String.format("SELECT ftopid,fresourceId AS fresid,flevel,flevelcount,FDescript FROM (" +
				"SELECT d.ftopid,d.flevel,COUNT(d.flevel) AS flevelcount,d.FDescript,d.fstate,d.fresourceid,d.ftoptype " +
				" FROM tndevicelog d GROUP BY d.ftopid,d.flevel,d.FDescript HAVING d.ftopid IN(%s) " + fresid +
				" AND d.ftoptype='%s' AND d.fstate=0 " + " ORDER BY d.ftopid ASC) t1", ftopid, ftoptype);
			List<DataRow> logs = jdbc.query(sql);
			if(logs != null){
				Map<String, List<DataRow>> idLogs = new HashMap<String, List<DataRow>>(logs.size());
				String id;
				for(DataRow log : logs){
					id = log.getString("fresid");
					if(id != null){
						if(idLogs.containsKey(id)){
							idLogs.get(id).add(log);
						}
						else {
							List<DataRow> ls = new ArrayList<DataRow>();
							ls.add(log);
							idLogs.put(id, ls);
						}
					}
				}
				return idLogs;
			}
		}
		return null;
	}
	
//	private DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@SuppressWarnings("unchecked")
	public Map<String, DataRow> queryLogs02(JdbcTemplate srDB, String fresourceid, String ftoptype){
		List<DataRow> drs = srDB.query(String.format("SELECT d.flevel,COUNT(d.flevel) AS flevelcount," +
				"MIN(d.FFirstTime) AS early,MAX(d.FLastTime) AS latest FROM " +
				"(SELECT * FROM tndevicelog WHERE fresourceId=%s AND ftoptype='%s' AND fstate=0) d" +
				" GROUP BY d.flevel", fresourceid, ftoptype));
		if(drs != null && drs.size() > 0){
			Map<String, DataRow> data = new HashMap<String, DataRow>(drs.size());
			for(DataRow dr : drs){
				data.put(dr.getString("flevel"), dr);
			}
			return data;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, List<DataRow>> queryLogs(JdbcTemplate jdbc, String fresourceid, String ftoptype){
		if(ftoptype != null && ftoptype.trim().length() != 0){
			String sql = String.format(
					"SELECT ftopid,fresourceId AS fresid,flevel,COUNT(flevel) AS flevelcount,FDescript FROM("+
					"SELECT d.ftopid,d.flevel,d.FDescript,d.fstate,d.fresourceid,d.ftoptype " +
					"FROM tndevicelog d WHERE d.fresourceid IN(%s) AND d.ftoptype='%s' AND d.fstate=0 " +
					" ORDER BY d.ftopid ASC) d GROUP BY d.ftopid,d.flevel,d.FDescript",
					fresourceid, ftoptype);
					
//					"SELECT ftopid,fresourceId AS fresid,flevel,flevelcount,FDescript FROM (" +
//				"SELECT d.ftopid,d.flevel,COUNT(d.flevel) AS flevelcount,d.FDescript,d.fstate," +
//				"d.fresourceid,d.ftoptype " +
//				" FROM tndevicelog d GROUP BY d.ftopid,d.flevel,d.FDescript HAVING " + fresid +
//				" AND d.ftoptype='%s' AND d.fstate=0 " + " ORDER BY d.ftopid ASC) t1", ftoptype);
			List<DataRow> logs = jdbc.query(sql);
			if(logs != null){
				Map<String, List<DataRow>> idLogs = new HashMap<String, List<DataRow>>(logs.size());
				String id;
				for(DataRow log : logs){
					id = log.getString("fresid");
					if(id != null){
						if(idLogs.containsKey(id)){
							idLogs.get(id).add(log);
						}
						else {
							List<DataRow> ls = new ArrayList<DataRow>();
							ls.add(log);
							idLogs.put(id, ls);
						}
					}
				}
				return idLogs;
			}
		}
		return null;
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
	
	/**
	 * @see 交换机端口需要过滤一下
	 * @param srDB
	 * @param tpc
	 * @param swIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchAndSwitchportFromDB2(JdbcTemplate srDB, JdbcTemplate tpc, String swIds){
		String sql = "SELECT device_id AS dev FROM t_map_devices WHERE DEVICE_TYPE='&' ORDER BY dev"
			.replace("&", SrContant.SUBDEVTYPE_SWITCHPORT);
		List<DataRow> drs = srDB.query(sql);
		String swpIds = "";
		if(drs != null && drs.size() > 0){
			swpIds = " AND po.port_id NOT IN(&) AND kk.swp_id2 NOT IN(&)"
				.replace("&", extractIds(drs, "dev"));
		}
		sql = "select sw.switch_id as sw_id1,kk.sw_id2,po.port_id as swp_id1,kk.swp_id2," +
				"sw.the_display_name as sw_name1,kk.sw_name2,sw.ip_address as sw_ip1," +
				"kk.sw_ip2 from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id " +
				" join v_res_port2port p2p on po.port_id=p2p.port_id1 " +
				" join (select s.the_display_name as sw_name2,s.switch_id as sw_id2,p.port_id as swp_id2," +
				"s.ip_address as sw_ip2 from v_res_switch s " +
				"join v_res_switch2port p on s.switch_id=p.switch_id) kk on kk.swp_id2=p2p.port_id2 " + swpIds;
		if(swIds != null && swIds.trim().length() > 0){
			sql += " where sw.switch_id in (&) or kk.sw_id2 in (&) order by sw.switch_id".replace("&", swIds);
		}
		return tpc.query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchFromDB2(JdbcTemplate tpc, String swIds){
		String sql = "select sw.switch_id as sw_id1,kk.sw_id2,po.port_id as swp_id1,kk.swp_id2," +
				"sw.the_display_name as sw_name1,kk.sw_name2,sw.ip_address as sw_ip1," +
				"kk.sw_ip2 from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id " +
				" join v_res_port2port p2p on po.port_id=p2p.port_id1 " +
				" join (select s.the_display_name as sw_name2,s.switch_id as sw_id2,p.port_id as swp_id2," +
				"s.ip_address as sw_ip2 from v_res_switch s " +
				"join v_res_switch2port p on s.switch_id=p.switch_id) kk on kk.swp_id2=p2p.port_id2 ";
		if(swIds != null && swIds.trim().length() > 0){
			sql += " where sw.switch_id in (&) or kk.sw_id2 in (&) order by sw.switch_id".replace("&", swIds);
		}
		return tpc.query(sql);
	}
	

	public DataRow getVMDeviceInfoFromMySQL(JdbcTemplate srDB, long devId){
		return srDB.queryMap("SELECT v.name as vm_name,v.vm_id,v.HYPERVISOR_ID AS hyp_id,h.host_COMPUTER_ID AS comp_id,h.name AS hyp_name," +
				"c.RAM_SIZE AS mem,c.DISK_SPACE/1024.0 AS total,c.DISK_AVAILABLE_SPACE/1024.0 AS available," +
				"c.UPDATE_TIMESTAMP FROM t_res_virtualmachine v JOIN t_res_hypervisor h ON v.HYPERVISOR_ID=h.HYPERVISOR_ID " +
				"JOIN t_res_computersystem c ON v.COMPUTER_ID=c.COMPUTER_ID AND v.VM_ID=" + devId);
	}
	
	public DataRow getPhyDeviceInfoFromMySQL(JdbcTemplate srDB, long devId){
		return srDB.queryMap("SELECT h.HYPERVISOR_ID AS hyp_id,h.host_COMPUTER_ID AS comp_id,h.name AS hyp_name," +
				"c.processor_count AS cpu_count,c.VENDOR,c.OS_VERSION,c.processor_speed/1000 AS cpu_speed," +
				"c.DISK_SPACE/1024.0 AS total,c.DISK_AVAILABLE_SPACE/1024.0 AS available,c.RAM_SIZE AS mem," +
				"c.UPDATE_TIMESTAMP,v.vm_count FROM t_res_hypervisor h JOIN t_res_computersystem c " +
				"ON h.host_COMPUTER_ID=c.COMPUTER_ID JOIN (SELECT HYPERVISOR_ID,COUNT(vm_id) AS vm_count " +
				"FROM t_res_virtualmachine v GROUP BY HYPERVISOR_ID) v ON v.HYPERVISOR_ID=h.HYPERVISOR_ID AND h.HYPERVISOR_ID=" + devId);
	}
	
	public DataRow getSwitchDeviceInfoFromDB2(JdbcTemplate tpc, long devId){
		return tpc.queryMap("select s.the_display_name as sw_name,s.switch_id as sw_id,v.vendor_name,s.version,m.model_name,s.mgmt_url_addr,s.serial_number," +
				"p.port_count,s.domain as zone_id,s.update_timestamp from v_res_switch s join v_res_model m " +
				"on s.model_id=m.model_id join v_res_vendor v on s.vendor_id=v.vendor_id " +
				"join (select switch_id,count(port_id) as port_count from v_res_switch_port group by switch_id) p" +
				" on p.switch_id=s.switch_id and s.switch_id=" + devId);
	}
	
	public DataRow getStorageDeviceInfoFromDB2(JdbcTemplate tpc, long devId){
		return tpc.queryMap(("select s.subsystem_id as sto_id,s.the_display_name as sto_name,v.vendor_name,s.type as os_type,m.model_name,s.serial_number," +
				"s.code_level as micro_code,s.cache,s.the_physical_disk_space as phy_disk," +
				"s.the_storage_pool_space as pool_space,s.the_volume_space as vol_space," +
				"s.the_assigned_volume_space as ass_vol_space,s.the_unassigned_volume_space as unass_vol_space," +
				"the_consolidated_status as con_status,t1.port_count,t2.disk_count,t3.pool_count," +
				"t4.volume_count,t5.extent_count,t6.arrsize_count,t7.rank_count,t8.node_count,t9.iog_count " +
				" from v_res_storage_subsystem s left join v_res_vendor v on v.vendor_id=s.vendor_id " +
				" join v_res_model m on s.model_id=m.model_id left join (select & as sys_id," +
				" count(port_id) as port_count from v_res_port where subsystem_id=&) t1  " +
				" on t1.sys_id=s.subsystem_id left join (select & as sys_id,count(physical_volume_id) " +
				" as disk_count from v_res_physical_volume where subsystem_id=&) t2 on t2.sys_id=s.subsystem_id  " +
				" left join (select & as sys_id,count(pool_id) as pool_count " +
				" from v_res_storage_pool where subsystem_id=&) t3 on t3.sys_id=s.subsystem_id " +
				" left join (select & as sys_id,count(svid) as volume_count from v_res_storage_volume where subsystem_id=&) t4 " +
				" on t4.sys_id=s.subsystem_id left join (select & as sys_id,count(storage_extent_id) " +
				" as extent_count from v_res_storage_extent where subsystem_id=&) t5 " +
				" on t5.sys_id=s.subsystem_id left join (select & as sys_id,count(storage_extent_id) " +
				" as arrsize_count from v_res_arraysite where subsystem_id=&) t6 on t6.sys_id=s.subsystem_id " +
				" left join (select & as sys_id,count(storage_extent_id) as rank_count " +
				" from V_RES_STORAGE_RANK where subsystem_id=&) t7 on t7.sys_id=s.subsystem_id " +
				" left join (select & as sys_id,count(redundancy_id) as node_count " +
				" from V_RES_REDUNDANCY where subsystem_id=&) t8 on t8.sys_id=s.subsystem_id " +
				" left join (select & as sys_id,count(io_group_id) as iog_count " +
				"from V_RES_STORAGE_IOGROUP where subsystem_id=&) t9 on t9.sys_id=s.subsystem_id " +
				"where s.subsystem_id=&").replace("&", String.valueOf(devId)));
	}
	
	public DataRow getStorageDeviceInfoFromMySQL(JdbcTemplate srDB, long devId){
		return srDB.queryMap("SELECT s.subsystem_id AS sto_id,COALESCE(s.name,s.display_name) AS sto_name,s.storage_type as os_type," +
				" s.ip_address AS sto_ip,s.cache_gb/1024.0 AS cache_gb,s.SERIAL_NUMBER,s.nvs_gb/1024.0 AS write_cache_gb," +
				" s.vendor_name AS vendor,s.PHYSICAL_DISK_CAPACITY/1048576.0 AS phy_disk_tb,s.model," +
				" s.total_usable_capacity/1048576.0 AS total,s.code_level AS microcode," +
				" (s.total_usable_capacity-s.unallocated_usable_capacity)/1024.0/1024.0 AS used " +
				" FROM t_res_storagesubsystem s WHERE s.subsystem_id=" + devId);
	}
	
	public DataRow getPoolDeviceInfoFromDB2(JdbcTemplate tpc, long devId){
		return tpc.queryMap(("select p.the_display_name as pool_name,p.pool_id,s.subsystem_id as sto_id,s.the_display_name as sto_name,p.the_space as pool_space," +
			" p.the_consumed_space as pool_con_space,p.the_available_space as pool_available_space," +
			" p.the_assigned_space as pool_assigned_space,p.the_unassigned_space as pool_unassigned_space," +
			" p.the_native_status as native_status,p.the_consolidated_status as con_status," +
			" p.the_operational_status as oper_status,p.raid_level,v.volume_count " +
			" from v_res_storage_pool p join v_res_storage_subsystem s on p.subsystem_id=s.subsystem_id and p.pool_id=&" +
			" left join (select & as pool_id,count(svid) as volume_count from v_res_storage_volume where pool_id=&) v " +
			"on v.pool_id=p.pool_id").replace("&", String.valueOf(devId)));
	}
	
	public DataRow getPoolDeviceInfoFromMySQL(JdbcTemplate srDB, long devId){
		return srDB.queryMap(("SELECT s.subsystem_id AS sto_id,COALESCE(s.name,s.display_name) AS sto_name,s.storage_type as os_type,CONCAT('POOL ',COALESCE(p.name,p.display_name)) AS pool_name," +
				" p.NUM_BACKEND_DISK,p.TOTAL_USABLE_CAPACITY/1024.0 AS total,p.NUM_LUN AS volume_count,p.pool_id," +
				" (p.TOTAL_USABLE_CAPACITY-p.UNALLOCATED_CAPACITY)/1024.0 AS used,p.raid_level AS arraysize_type,p.update_timestamp " +
				" FROM t_res_storagepool p JOIN t_res_storagesubsystem s ON p.SUBSYSTEM_ID=s.SUBSYSTEM_ID AND p.pool_id=" + devId));
	}
	
	public DataRow getVolumeDeviceInfoFromDB2(JdbcTemplate tpc, long devId){
		return tpc.queryMap("select v.svid as vol_id,v.the_display_name as vol_name,p.the_display_name as pool_name,p.pool_id,s.subsystem_id as sto_id,s.the_display_name as sto_name,v.the_redundancy as raid_level," +
			" v.the_capacity as vol_space,v.the_used_space as vol_used_space,v.unique_id,p.pool_id," +
			" p.the_display_name as pool_name,v.update_timestamp from v_res_storage_volume v " +
			" join v_res_storage_subsystem s on v.subsystem_id=s.subsystem_id " +
			" join v_res_storage_pool p on v.pool_id=p.pool_id and v.svid=" + devId);
	}
	
	public DataRow getVolumeDeviceInfoFromMySQL(JdbcTemplate srDB, long devId){
		return srDB.queryMap("SELECT s.subsystem_id AS sto_id,COALESCE(s.name,s.display_name) AS sto_name,s.storage_type as os_type,p.pool_id,COALESCE(v.name,v.display_name) AS vol_name," +
				" CONCAT('POOL ',COALESCE(p.name,p.display_name)) AS pool_name,v.VOLUME_ID AS vol_id,v.LOGICAL_CAPACITY/1024.0 AS total,v.RAID_LEVEL AS arraysize_type," +
				" COALESCE(v.PHYSICAL_CAPACITY,0.00)/1024.0 AS phy_capacity,v.default_owner,v.current_owner  FROM t_res_storagevolume v JOIN t_res_storagepool p ON v.POOL_ID=p.POOL_ID " +
				" JOIN t_res_storagesubsystem s ON v.SUBSYSTEM_ID=s.SUBSYSTEM_ID AND v.VOLUME_ID=" + devId);
	}
		
	public interface ICallBack { boolean judge(DataRow dr); }
	public StringBuilder[] extractIds(List<DataRow> data, ICallBack icb, String ...idKeys){
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
				if(!idSet.get(idKeys[j]).contains(temp) && icb.judge(data.get(i))){
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
	
	public String extractIds(List<DataRow> data, String idKey, ICallBack icb){
		if(data == null || data.size() <= 0){ return "-1"; }
		StringBuilder ids = new StringBuilder(data.size() * 5);
		Set<Long> idSet = new HashSet<Long>(data.size());
		Long temp;
		for(int i = 0, len = data.size(); i < len; ++i){
			temp = data.get(i).getLong(idKey);
			if(!idSet.contains(temp) && icb.judge(data.get(i))){
				ids.append(temp);
				ids.append(',');
				idSet.add(temp);
			}
		}
		if(ids.length() - 1 >= 0 && ids.charAt(ids.length() - 1) == ','){
			ids.deleteCharAt(ids.length() - 1);
		}
		if(ids.length() == 0){ return "-1"; }
		return ids.toString();
	}

	public boolean isRepaint() {
		return isRepaint;
	}

	public void setRepaint(boolean isRepaint) {
		this.isRepaint = isRepaint;
	}
}
