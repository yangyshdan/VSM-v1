package com.huiming.service.roles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class RolesService extends BaseService {
	
	// ID的生成规则是 pid + "_" + 123
	/////////////////////////// 方便修改
	private String physical_prefix = "phy_";
	private String virtual_prefix = "vir_";
	private String hypervisor_prefix = "hyp_";
	private String fabric_prefix = "fab_";
	private String switch_prefix = "sw_";
	private String zoneset_prefix = "zset_";
	private String zone_prefix = "zone_";
	private String storage_sr_prefix = "sto_sr_";
	private String storage_tpc_prefix = "sto_tpc_";
	private String app_prefix = "app_";
	///////////////////////////
	
	private String queryMenuId(JdbcTemplate srDB, String fdevtype, String roleIds){
		return queryMenuId(srDB, fdevtype, roleIds, 999);
	}
	@SuppressWarnings("unchecked")
	private String queryMenuId(JdbcTemplate srDB, String fdevtype, String roleIds, int which){
		String osType;
		switch(which){
		case 0:
			osType = "and os_type in('EMC','HDS','NETAPP')";
			break;
		case 1:
			osType = "and os_type not in('EMC','HDS','NETAPP')"; // 查找的是DB2
			break;
		default:
			osType = "";
		}
		String sql = String.format("SELECT distinct fmenuid as mid FROM tsrolemenu WHERE fdevtype='%s' AND froleid IN(%s) %s order by fmenuid",
				fdevtype, roleIds, osType);
		List<DataRow> drs = srDB.query(sql);
		if(drs != null && drs.size() > 0){
			StringBuilder sb = new StringBuilder(drs.size() * 5);
			int len = drs.size() - 1;
			for(int i = 0; i < len; ++i){
				sb.append(drs.get(i).getString("mid"));
				sb.append(",");
			}
			sb.append(drs.get(len).getString("mid"));
			return sb.toString();
		}
		return "-1";
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getUsersWithThisRole(Long roleId) {
		String sql = "SELECT * FROM tsuser WHERE fid IN(SELECT fuserid FROM tsuserrole WHERE froleId=" + roleId + ")";
		List<DataRow> drs = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		if(drs == null || drs.size() == 0){
			return null;
		}
		return drs;
	}
	
	public List<DataRow> getAllPhysical(String pid){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		return getAllPhysical(srDB, pid, "0", SrContant.STATE_ADD);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllPhysical(JdbcTemplate srDB, String pid, String roleIds, int action){
		// id,devid,devtype,pid,name,  url,icon,checked
		String sql = null;
		if(action == SrContant.STATE_ADD){
			sql = String.format(
					"SELECT concat('%s',HYPERVISOR_ID) AS id,HYPERVISOR_ID as devid,'%s' as devtype,name,'%s' as pid FROM t_res_hypervisor ORDER BY HYPERVISOR_ID",
					physical_prefix, SrContant.SUBDEVTYPE_PHYSICAL, pid);
		}
		else if(action == SrContant.STATE_EDIT) {
			sql = String.format("SELECT concat('%s',h.HYPERVISOR_ID) AS id,h.HYPERVISOR_ID AS devid,'%s' AS devtype,h.name,'%s' AS pid,CASE WHEN t.fmenuid IS NULL THEN 0 ELSE 1 END AS checked " +
				" FROM t_res_hypervisor h LEFT JOIN (SELECT fmenuid FROM tsrolemenu WHERE fdevtype='%s' AND froleid IN(%s)) t ON h.HYPERVISOR_ID=t.fmenuid ORDER BY HYPERVISOR_ID", 
				physical_prefix, SrContant.SUBDEVTYPE_PHYSICAL, pid, SrContant.SUBDEVTYPE_PHYSICAL, roleIds);
		}
		if(sql != null){
			List<DataRow> drs = srDB.query(sql);
			// 因为物理机展开之后
			if(drs != null){
				for(DataRow dr : drs){ 
					dr.set("isParent", true);
					dr.set("checked", dr.getInt("checked") != 0);
				}
				return drs;
			}
		}
		return new ArrayList<DataRow>(0);
	}
	
	public List<DataRow> getAllVirtualByPhyId(String phyIds, String pid){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		return getAllVirtualByPhyId(srDB, phyIds, pid, "0", SrContant.STATE_ADD);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllVirtualByPhyId(JdbcTemplate srDB, String phyIds, String pid, 
			String roleIds, int action){
		String sql = null;
		if(action == SrContant.STATE_ADD){
			// 这样写避免id重复，id重复导致树形不正确
			sql = String.format("SELECT concat('%s',vm_id) AS id,vm_id AS devid,'%s' AS pid,name,'%s' AS devtype FROM t_res_virtualmachine WHERE hypervisor_id IN(%s) order by vm_id", 
				virtual_prefix, pid, SrContant.SUBDEVTYPE_VIRTUAL, phyIds);
		}
		else if(action == SrContant.STATE_EDIT){
			sql = String.format("SELECT concat('%s',v.vm_id) AS id,v.vm_id AS devid,'%s' AS pid,v.name,'%s' AS devtype,CASE WHEN t.fmenuid IS NULL THEN 0 ELSE 1 END AS checked " +
					" FROM t_res_virtualmachine v LEFT JOIN (SELECT fmenuid FROM tsrolemenu WHERE fdevtype='%s' AND froleid IN(%s)) t ON v.vm_id=t.fmenuid WHERE v.hypervisor_id IN(%s) ORDER BY v.vm_id",
					virtual_prefix, pid, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.SUBDEVTYPE_VIRTUAL, roleIds, phyIds);
		}
		if(sql != null){
			List<DataRow> drs = srDB.query(sql);
			if(drs != null){
				for(DataRow dr : drs){
					dr.set("checked", dr.getInt("checked") != 0);
				}
				return drs;
			}
		}
		return new ArrayList<DataRow>(0);
	}
	
	public List<DataRow> getAllHypervByPhyId(String phyIds, String pid){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		return getAllHypervByPhyId(srDB, phyIds, pid, "0", SrContant.STATE_ADD);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllHypervByPhyId(JdbcTemplate srDB, String phyIds, String pid, String roleIds, int action){
		String sql = null;
		if(action == SrContant.STATE_ADD){
			sql = String.format("SELECT CONCAT('%s',vp.id) AS id,vp.id AS devid,'%s' AS pid,name,'%s' AS devtype FROM t_res_virtualplatform vp WHERE vp.hypervisor_id IN(%s) order by vp.id", 
					hypervisor_prefix, pid, SrContant.SUBDEVTYPE_HYPERVISOR, phyIds);
		}
		else if(action == SrContant.STATE_EDIT) {
			sql = String.format("SELECT concat('%s',vp.id) AS id,vp.id AS devid,'%s' AS pid,vp.name,'%s' AS devtype,CASE WHEN t.fmenuid IS NULL THEN 0 ELSE 1 END AS checked " +
					" FROM t_res_virtualplatform vp LEFT JOIN (SELECT fmenuid FROM tsrolemenu WHERE fdevtype='%s' AND froleid IN(%s)) t " +
					" ON vp.id=t.fmenuid WHERE vp.hypervisor_id IN(%s) ORDER BY vp.id",
					hypervisor_prefix, pid, SrContant.SUBDEVTYPE_HYPERVISOR, SrContant.SUBDEVTYPE_HYPERVISOR, roleIds, phyIds);
		}
		if(sql != null){
			List<DataRow> drs = srDB.query(sql);
			if(drs != null){
				for(DataRow dr : drs){
					dr.set("checked", dr.getInt("checked") != 0);
				}
				return drs;
			}
		}
		return new ArrayList<DataRow>(0);
	}
	
	public List<DataRow> getAllFabric(String pid){
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		return getAllFabric(tpc, null, null, pid, SrContant.STATE_ADD);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllFabric(JdbcTemplate tpc, JdbcTemplate srDB, String roleIds, String pid, int action){
		String sql = null;
		if(action == SrContant.STATE_ADD){
			sql = String.format("SELECT CONCAT('%s',f.fabric_id) AS id,f.fabric_id AS devid,'%s' AS pid,'%s' AS devtype,f.the_display_name as name FROM V_RES_FABRIC f order by f.fabric_id",
				fabric_prefix, pid, SrContant.DEVTYPE_VAL_FABRIC);
		}
		else if(action == SrContant.STATE_EDIT) {
			String ids = queryMenuId(srDB, SrContant.DEVTYPE_VAL_FABRIC, roleIds);
			sql = String.format("SELECT concat('%s',f.fabric_id) AS id,f.fabric_id AS devid,'%s' AS pid,'%s' AS devtype,f.the_display_name as name,case when t.id is null then 0 else 1 end as checked " +
					" FROM V_RES_FABRIC f left join (select fabric_id as id from V_RES_FABRIC where fabric_id in(%s)) t on t.id=f.fabric_id order by f.fabric_id",
					fabric_prefix, pid, SrContant.DEVTYPE_VAL_FABRIC, ids);
		}
		if(sql != null){
			List<DataRow> drs = tpc.query(sql);
			if(drs != null){
				for(DataRow dr : drs){ 
					dr.set("isParent", true);
					dr.set("checked", dr.getInt("checked") != 0);
				}
			}
			return drs;
		}
		return new ArrayList<DataRow>(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllApplication(JdbcTemplate srDB, String roleIds,
			String pid, int action){
		String sql = null;
		if(action == SrContant.STATE_ADD){
			sql = String.format("SELECT CONCAT('%s',f.id) AS id,f.id AS devid,'%s' AS pid,'%s' AS devtype,f.name AS NAME FROM t_app f ORDER BY f.id",
					app_prefix, pid, SrContant.SUBDEVTYPE_APP);
		}
		else if(action == SrContant.STATE_EDIT) {
			sql = String.format("SELECT CONCAT('%s',h.id) AS id,h.id AS devid,'%s' AS devtype,h.name,'%s' AS pid,CASE WHEN t.fmenuid IS NULL THEN 0 ELSE 1 END AS checked FROM t_app h " +
					" LEFT JOIN (SELECT fmenuid FROM tsrolemenu WHERE fdevtype='%s' AND froleid IN(%s)) t ON h.id=t.fmenuid ORDER BY h.id",
					app_prefix, SrContant.SUBDEVTYPE_APP, pid, SrContant.SUBDEVTYPE_APP, roleIds);
		}
		if(sql != null){
			List<DataRow> drs = srDB.query(sql);
			if(drs != null){
				for(DataRow dr : drs){
					dr.set("checked", dr.getInt("checked") != 0);
				}
			}
			return drs;
		}
		return new ArrayList<DataRow>(0);
	}
	
	public List<DataRow> getAllSwitchByFabricId(String fabricIds, String pid){
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		return getAllSwitchByFabricId(tpc, null, fabricIds, null, pid, SrContant.STATE_ADD);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllSwitchByFabricId(JdbcTemplate tpc, JdbcTemplate srDB, String fabricIds,
			String roleIds, String pid, int action){		
		String sql = null;
		if(action == SrContant.STATE_ADD){
			sql = String.format("SELECT CONCAT('%s',f.switch_id) AS id,f.switch_id AS devid,the_display_name as name,'%s' AS pid,'%s' AS devtype FROM v_res_switch f where f.the_fabric_id in(%s) order by f.switch_id",
					switch_prefix, pid, SrContant.SUBDEVTYPE_SWITCH, fabricIds);
		}
		else if(action == SrContant.STATE_EDIT) {
			String ids = queryMenuId(srDB, SrContant.SUBDEVTYPE_SWITCH, roleIds);
			sql = String.format("SELECT CONCAT('%s',f.switch_id) AS id,f.switch_id AS devid,the_display_name as name,'%s' AS pid,'%s' AS devtype,case when t.id is null then 0 else 1 end as checked " +
					" FROM v_res_switch f left join (select switch_id as id from v_res_switch where switch_id in(%s)) t on t.id=f.switch_id where f.the_fabric_id in(%s) order by f.switch_id ", 
					switch_prefix, pid, SrContant.SUBDEVTYPE_SWITCH, ids, fabricIds);
		}
		if(sql != null){
			List<DataRow> drs = tpc.query(sql);
			if(drs != null){
				for(DataRow dr : drs){
					dr.set("checked", dr.getInt("checked") != 0);
				}
			}
			return drs;
		}
		return new ArrayList<DataRow>(0);
	}
	
	public List<DataRow> getAllZonesetByFabricId(String fabricIds, String pid){
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		return getAllZonesetByFabricId(tpc, null, fabricIds, null, pid, SrContant.STATE_ADD);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllZonesetByFabricId(JdbcTemplate tpc, JdbcTemplate srDB, String fabricIds,
			String roleIds, String pid, int action){
		String sql = null;
		if(action == SrContant.STATE_ADD){
			sql = String.format("SELECT CONCAT('%s',f.zset_id) AS id,f.zset_id AS devid,the_display_name as name,'%s' AS pid,'%s' AS devtype FROM V_RES_ZSET f where f.the_fabric_id in(%s) order by f.zset_id",
					zoneset_prefix, pid, SrContant.DEVTYPE_VAL_ZONESET, fabricIds);
		}
		else if(action == SrContant.STATE_EDIT) {
			String ids = queryMenuId(srDB, SrContant.DEVTYPE_VAL_ZONESET, roleIds);
			sql = String.format("SELECT CONCAT('%s',f.zset_id) AS id,f.zset_id AS devid,the_display_name as name,'%s' AS pid,'%s' AS devtype,case when t.id is null then 0 else 1 end as checked " +
					" FROM V_RES_ZSET f left join (select zset_id as id from V_RES_ZSET where zset_id in(%s)) t on t.id=f.zset_id where f.the_fabric_id in(%s) order by f.zset_id ", 
					zoneset_prefix, pid, SrContant.DEVTYPE_VAL_ZONESET, ids, fabricIds);
		}
		if(sql != null){
			List<DataRow> drs = tpc.query(sql);
			if(drs != null){
				for(DataRow dr : drs){
					dr.set("checked", dr.getInt("checked") != 0);
				}
			}
			return drs;
		}
		return new ArrayList<DataRow>(0);
	}
	
	public List<DataRow> getAllZoneByZSetId(String zsetIds, String pid){
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		return getAllZoneByZSetId(tpc, null, zsetIds, null, pid, SrContant.STATE_ADD);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllZoneByZSetId(JdbcTemplate tpc, JdbcTemplate srDB, String zsetIds, 
			String roleIds, String pid, int action){
		String sql = null;
		if(action == SrContant.STATE_ADD){
			sql = String.format("SELECT CONCAT('%s',f.zone_id) AS id,f.the_display_name as name,f.zone_id AS devid,'%s' AS pid,'%s' AS devtype FROM V_RES_ZONE f join V_RES_ZSET2ZONE zs2z on f.zone_id=zs2z.zone_id and zs2z.zset_id in(%s) order by f.zone_id",
					zone_prefix, pid, SrContant.SUBDEVTYPE_ZONE, zsetIds);
		}
		else if(action == SrContant.STATE_EDIT) {
			String ids = queryMenuId(srDB, SrContant.DEVTYPE_VAL_ZONESET, roleIds);
			sql = String.format("SELECT CONCAT('%s',f.zone_id) AS id,f.the_display_name as name,f.zone_id AS devid,'%s' AS pid,'%s' AS devtype,case when t.id is null then 0 else 1 end as checked FROM V_RES_ZONE f join V_RES_ZSET2ZONE zs2z on f.zone_id=zs2z.zone_id and zs2z.zset_id in(%s) " +
					" left join (select zone_id as id from V_RES_ZONE where zone_id in(%s)) t on t.id=f.zone_id order by f.zone_id", 
					zone_prefix, pid, SrContant.SUBDEVTYPE_ZONE, ids, zsetIds);
		}
		if(sql != null){
			List<DataRow> drs = tpc.query(sql);
			if(drs != null){
				for(DataRow dr : drs){
					dr.set("checked", dr.getInt("checked") != 0);
				}
			}
			return drs;
		}
		return new ArrayList<DataRow>(0);
	}
	
	public List<DataRow> getAllStorage(String pid) {
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		return getAllStorage(srDB, tpc, null, pid, SrContant.STATE_ADD);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getAllStorage(JdbcTemplate srDB, JdbcTemplate tpc, String roleIds, String pid, int action){
		String srSQL = null;
		String tpcSQL = null;
		boolean isOk = true;
		if(action == SrContant.STATE_ADD){
			srSQL = String.format("SELECT CONCAT('%s',s.subsystem_id) AS id,s.subsystem_id AS devid,COALESCE(NAME, DISPLAY_NAME) AS NAME,'%s' AS pid,storage_type AS sto_type,'%s' AS devtype FROM t_res_storagesubsystem s ORDER BY s.subsystem_id",
					storage_sr_prefix, pid, SrContant.SUBDEVTYPE_STORAGE);
			tpcSQL = String.format("SELECT CONCAT('%s',f.subsystem_id) AS id,f.the_display_name as name,f.subsystem_id AS devid,'%s' AS pid,'%s' AS devtype,f.os_type as sto_type FROM v_res_storage_subsystem f order by f.subsystem_id",
					storage_tpc_prefix, pid, SrContant.SUBDEVTYPE_STORAGE);
			
		}
		else if(action == SrContant.STATE_EDIT) {
			srSQL = String.format("SELECT CONCAT('%s',s.subsystem_id) AS id,s.subsystem_id AS devid,COALESCE(NAME,DISPLAY_NAME) AS NAME,'%s' AS pid,storage_type AS sto_type," +
					"'%s' AS devtype,CASE WHEN t.fmenuid IS NULL THEN 0 ELSE 1 END AS checked FROM t_res_storagesubsystem s " +
					" LEFT JOIN (SELECT fmenuid FROM tsrolemenu WHERE fdevtype='%s' AND froleid IN(%s) and os_type in(%s)) t ON s.subsystem_id=t.fmenuid ORDER BY s.subsystem_id ",
					storage_sr_prefix, pid, SrContant.SUBDEVTYPE_STORAGE, SrContant.SUBDEVTYPE_STORAGE, roleIds, "'EMC','HDS','NETAPP'");
			
			String ids = queryMenuId(srDB, SrContant.SUBDEVTYPE_STORAGE, roleIds, 1);
			tpcSQL = String.format("SELECT CONCAT('%s',f.subsystem_id) AS id,f.the_display_name as name,f.subsystem_id AS devid,'%s' AS pid,'%s' AS devtype,f.os_type as sto_type,case when t.id is null then 0 else 1 end as checked FROM v_res_storage_subsystem f " +
					" left join (select subsystem_id as id from v_res_storage_subsystem where subsystem_id in(%s)) t on t.id=f.subsystem_id order by f.subsystem_id", 
					storage_tpc_prefix, pid, SrContant.SUBDEVTYPE_STORAGE, ids);
		}
		else {
			isOk = false;
		}
		
		if(isOk){
			List<DataRow> srData = srDB.query(srSQL);
			List<DataRow> tpcData = null;
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				tpcData = tpc.query(tpcSQL);
			}
			if(srData != null && srData.size() > 0){
				if(tpcData != null && tpcData.size() > 0) {
					srData.addAll(tpcData);
				}
			}
			else {
				srData = tpcData;
			}
			if(srData != null){
				for(DataRow dr : srData){
					dr.set("checked", dr.getInt("checked") != 0);
				}
			}
			return srData;
		}
		return new ArrayList<DataRow>(0);
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getMenuId(String menuName, String roleIds, String devtype){
		Set<String> menu_id = new HashSet<String>();
		String sql = String.format("SELECT menu_id as mid FROM tsrolemenu WHERE menu_name in(%s) %s AND fdevtype in(%s)", 
				menuName, (roleIds == null || roleIds.trim().length() == 0)? "" : "AND FRoleId IN("+roleIds+")", devtype);
		List<DataRow> data = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		if(data != null && data.size() > 0){
			for(DataRow dr : data){
				menu_id.add(dr.getString("mid"));
			}
		}
		return menu_id;
	}
	
	/**
	 * @see 异步菜单获取数据的方法, 只有新增和编辑时会用到这个方法
	 * @param devtype
	 * @param pid
	 * @param devIds
	 * @param userId
	 * @param action 当action的值为0时，也就是处于新增时，userId将被忽略
	 * @return
	 */
	public List<DataRow> getAllDevices(String devtype, String pid, String devIds, String roleIds, int action){
		List<DataRow> array = null;
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
		String phy = "m_phy", vir = "vir_01", hyp = "hyp_01", 
			fab = "m_fab", sw = "sw_01", zset = "zset_01", sto = "m_storage",
			m_application = "m_application";
		
//		boolean isChecked = action == SrContant.STATE_EDIT;
		if(phy.equalsIgnoreCase(devtype)){
			array = getAllPhysical(srDB, pid, roleIds, action);
		}
		else if(SrContant.SUBDEVTYPE_PHYSICAL.equalsIgnoreCase(devtype)){
			// id,devid,devtype,pid,name,  url,icon,checked  isParent
			Set<String> menu_id = getMenuId("'虚拟机','Hypervisor'", roleIds, "'" + vir + "','" + hyp + "'");
			array = new ArrayList<DataRow>(2);
			DataRow dr = new DataRow();
			dr.set("id", pid + "_vir");
			dr.set("devid", devIds); // 把物理机的ID记录到虚拟机的节点
			dr.set("checked", menu_id.contains(dr.getString("id")));
			dr.set("isParent", true);
			dr.set("devtype", vir);
			dr.set("pid", pid);
			dr.set("name", "虚拟机");
			array.add(dr);
			
			dr = new DataRow();
			dr.set("id", pid + "_hyp");
			dr.set("devid", devIds); // 把物理机的ID记录到虚拟机的节点
			dr.set("checked", menu_id.contains(dr.getString("id")));
			dr.set("isParent", true);
			dr.set("devtype", hyp);
			dr.set("pid", pid);
			dr.set("name", "Hypervisor");
			array.add(dr);
		}
		else if(vir.equalsIgnoreCase(devtype)){
			array = getAllVirtualByPhyId(srDB, devIds, pid, roleIds, action);
		}
		else if(hyp.equalsIgnoreCase(devtype)){
			array = getAllHypervByPhyId(srDB, devIds, pid, roleIds, action);
		}
		else if(fab.equalsIgnoreCase(devtype)){
			array = getAllFabric(tpc, srDB, roleIds, pid, action);
		}
		else if(SrContant.DEVTYPE_VAL_FABRIC.equalsIgnoreCase(devtype)){
			// id,devid,devtype,pid,name,  url,icon,checked
			Set<String> menu_id = getMenuId("'交换机','ZoneSet'", roleIds, "'" + sw + "','" + zset + "'");
			array = new ArrayList<DataRow>(2);
			DataRow dr = new DataRow();
			dr.set("id", pid + "_sw");
			dr.set("devid", devIds);
			dr.set("checked", menu_id.contains(dr.getString("id")));
			dr.set("isParent", true);
			dr.set("devtype", sw);
			dr.set("pid", pid);
			dr.set("name", "交换机");
			array.add(dr);
			
			dr = new DataRow();
			dr.set("id", pid + "_zset");
			dr.set("devid", devIds);
			dr.set("checked", menu_id.contains(dr.getString("id")));
			dr.set("isParent", true);
			dr.set("devtype", zset);
			dr.set("pid", pid);
			dr.set("name", "ZoneSet");
			array.add(dr);
		}
		else if(sw.equalsIgnoreCase(devtype)){
			array = getAllSwitchByFabricId(tpc, srDB, devIds, roleIds, pid, action);
		}
		else if(zset.equalsIgnoreCase(devtype)){
			array = getAllZonesetByFabricId(tpc, srDB, devIds, roleIds, pid, action);
		}
		else if(SrContant.DEVTYPE_VAL_ZONESET.equalsIgnoreCase(devtype)){
			array = getAllZoneByZSetId(tpc, srDB, devIds, roleIds, pid, action);
		}
		else if(sto.equalsIgnoreCase(devtype)){
			array = getAllStorage(srDB, tpc, roleIds, pid, action);
		}
		else if(m_application.equalsIgnoreCase(devtype)){
			// 读取其他人添加
			array = getAllApplication(srDB, roleIds, pid, action);
		}
		return array;
	}
	
	/**
	 * 
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DBPage getRolesPage(int curPage, int numPerPage){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		DBPage page = srDB.queryPage("SELECT * FROM tsrole order by fid", 
				curPage, numPerPage);
		if(page != null && page.getData() != null && page.getData().size() > 0){
			List<DataRow> data = page.getData();
			for(DataRow dr : data){
				dr.set("miCount", getMenuItemsByRoleId(srDB, dr.getLong("fid")));
			}
		}
		return page;
	}
	
	public void saveRoleMenus(String roleName, List<DataRow> data){
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			// 保存角色
			DataRow dr1 = new DataRow();
			dr1.set("fname", roleName);
			long roleId = Long.parseLong(session.insert("tsrole", dr1));
			// 保存角色和设备菜单的编号
			if(data != null && data.size() > 0){
				for(DataRow d : data){
					d.set("froleid", roleId);
					session.insert("tsrolemenu", d);
				}
			}
			session.commitTrans();
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getLocalizedMessage(), e);
			if (session != null) {
				session.rollbackTrans();
			}
		}
		finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}
	
	/**
	 * @see 备份代码，带软件稳定后，可以删除改代码
	 * @param roleName
	 * @param data
	 */
	public void saveRoleMenus_bak(String roleName, List<DataRow> data){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		// 保存角色
		DataRow dr1 = new DataRow();
		dr1.set("fname", roleName);
		long roleId = Long.parseLong(srDB.insert("tsrole", dr1));
		// 保存角色和设备菜单的编号
		if(data != null && data.size() > 0){
			for(DataRow d : data){
				d.set("froleid", roleId);
				srDB.insert("tsrolemenu", d);
			}
		}
	}
	
	public void updateRoleMenus(long roleId, String roleName, List<DataRow> data){
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			session.delete("tsrolemenu", "froleId", roleId);
			// 保存角色
			DataRow dr1 = new DataRow();
			dr1.set("fname", roleName);
			session.update("tsrole", dr1, "fid", roleId);
			// 保存角色和设备菜单的编号
			if(data != null && data.size() > 0){
				for(DataRow d : data){
					d.set("froleid", roleId);
					session.insert("tsrolemenu", d);
				}
			}
			session.commitTrans();
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getLocalizedMessage(), e);
			if (session != null) {
				session.rollbackTrans();
			}
		}
		finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}
	
	public void updateRoleMenus_bak(long roleId, String roleName, List<DataRow> data){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		srDB.delete("tsrolemenu", "FRoleId", roleId);
		DataRow dr = new DataRow();
		dr.set("fname", roleName);
		srDB.update("tsrole", dr, "fid", roleId);
		if(data != null && data.size() > 0){
			for(DataRow d : data){
				d.set("froleid", roleId);
				srDB.insert("tsrolemenu", d);
			}
		}
	}
	
	public void deleteRoleById(long roleId){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tsrole", "fid", roleId);
	}
	
	public DataRow getRoleNameById(long roleId){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select fname from tsrole where fid=" + roleId);
	}
	
	private int getMenuItemsByRoleId(JdbcTemplate srDB, Long roleId){
		return srDB.queryInt("SELECT COUNT(*) FROM tsrolemenu WHERE froleid=" + roleId);
	}
}
