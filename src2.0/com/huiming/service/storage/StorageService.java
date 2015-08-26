package com.huiming.service.storage;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class StorageService extends BaseService{
	
	/**
	 * @see 存储系统列表
	 * @param curPage
	 * @param numPerPage
	 * @param storageName
	 * @param ipAddress
	 * @param type
	 * @param serialNumber
	 * @param startPoolCap
	 * @param endPoolCap
	 * @param startPoolAvailableCap
	 * @param endPoolAvailableCap
	 * @param limitIds
	 * @return
	 */
	public DBPage getStoragePage(int curPage, int numPerPage,String storageName,String ipAddress,
			String type,String serialNumber,Integer startPoolCap,Integer endPoolCap,
			Integer startPoolAvailableCap,Integer endPoolAvailableCap,String limitIds){
		
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_storage_subsystem s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and s.subsystem_id in (" + limitIds + ") ");
		}
		if (storageName != null && storageName.length() > 0) {
			sb.append("and s.the_display_name like ? ");
			args.add("%" + storageName + "%");
		}
		if (ipAddress != null && ipAddress.length() > 0) {
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if (type != null && type.length() > 0) {
			sb.append("and s.the_type = ? ");
			args.add(type);
		}
		if (serialNumber != null && serialNumber.length() > 0) {
			sb.append("and serial_number = ? ");
			args.add(serialNumber);
		}
		if (startPoolCap != null && startPoolCap > 0) {
			sb.append("and the_storage_pool_consumed_space >= ? ");
			args.add(startPoolCap);
		}
		if (endPoolCap != null && endPoolCap > 0) {
			sb.append("and the_storage_pool_consumed_space <= ? ");
			args.add(endPoolCap);
		}
		if (startPoolAvailableCap != null && startPoolAvailableCap > 0) {
			sb.append("and the_storage_pool_available_space >= ? ");
			args.add(startPoolAvailableCap);
		}
		if (endPoolAvailableCap != null && endPoolAvailableCap > 0) {
			sb.append("and the_storage_pool_available_space <= ? ");
			args.add(endPoolAvailableCap);
		}
		sb.append("order by s.subsystem_id desc");
		
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageList(String storageName,String ipAddress,
			String type,String serialNumber,Integer startPoolCap,Integer endPoolCap,
			Integer startPoolAvailableCap,Integer endPoolAvailableCap,String limitIds){
		String sql="select s.the_display_name,s.ip_address,s.the_propagated_status,s.the_physical_disk_space," +
				"s.the_storage_pool_consumed_space,s.the_storage_pool_available_space,s.the_volume_space,s.the_assigned_volume_space," +
				"s.the_unassigned_volume_space,TO_CHAR(s.last_probe_time,'YYYY/MM/DD HH24:MI:SS') AS last_probe_time," +
				"s.cache,m.model_name,v.vendor_name " +
		"from v_res_storage_subsystem s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and s.subsystem_id in (" + limitIds + ") ");
		}
		if (storageName != null && storageName.length() > 0) {
			sb.append("and s.the_display_name like ? ");
			args.add("%" + storageName + "%");
		}
		if (ipAddress != null && ipAddress.length() > 0) {
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if (type != null && type.length() > 0) {
			sb.append("and s.the_type = ? ");
			args.add(type);
		}
		if (serialNumber != null && serialNumber.length() > 0) {
			sb.append("and serial_number = ? ");
			args.add(serialNumber);
		}
		if (startPoolCap != null && startPoolCap > 0) {
			sb.append("and the_storage_pool_consumed_space >= ? ");
			args.add(startPoolCap);
		}
		if (endPoolCap != null && endPoolCap > 0) {
			sb.append("and the_storage_pool_consumed_space <= ? ");
			args.add(endPoolCap);
		}
		if (startPoolAvailableCap != null && startPoolAvailableCap > 0) {
			sb.append("and the_storage_pool_available_space >= ? ");
			args.add(startPoolAvailableCap);
		}
		if (endPoolAvailableCap != null && endPoolAvailableCap > 0) {
			sb.append("and the_storage_pool_available_space <= ? ");
			args.add(endPoolAvailableCap);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubsystemNames(String os_type,String limitIds){
		StringBuffer sql = new StringBuffer("select ip_address,the_display_name as name,os_type,subsystem_id as id from v_res_storage_subsystem where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds.length() > 0) {
			sql.append("and subsystem_id in (" + limitIds + ") ");
		}
		if (os_type != null && os_type.length() > 0) {
			sql.append("and os_type = ? ");
			args.add(os_type);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> kpiInfo(String storageType,String devType){
		StringBuffer sb= new StringBuffer("select fid,ftitle,fstoragetype from tnprffields where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(storageType!=null && storageType.length()>0){
			sb.append("and fstoragetype=? ");
			args.add(storageType);
		}
		if(devType!=null && devType.length()>0){
			sb.append("and fdevtype=? ");
			args.add(devType);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> subStorageList(String type){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from (select v.*,v.last_probe_time ||'' as the_last_probe_time,case v.os_type when 25 then 'DS' when 21 then 'SVC' when 38 then 'SVC' when 15 then 'BSP' when 37 then 'BSP' when 10 then 'NAS' end as stype from V_RES_STORAGE_SUBSYSTEM v) s," +
		"v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id "+
		"and s.stype = '"+type+"'";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStoragesys(){
		String sql="select s.*,s.subsystem_id as id,s.the_display_name as name,m.model_name,v.vendor_name " +
		"from (select v.*,v.last_probe_time ||'' as the_last_probe_time,case v.os_type when 25 then 'DS' when 21 then 'SVC' when 38 then 'SVC' when 15 then 'BSP' when 37 then 'BSP' when 10 then 'NAS' end as stype from V_RES_STORAGE_SUBSYSTEM v) s," +
		"v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id "+
		"and s.stype != '10'";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	public DataRow getSubsystemInfo(Integer subsystemId){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_storage_subsystem s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(subsystemId!=0 && subsystemId>0&&subsystemId.SIZE>0){
			sb.append("and s.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString(),args.toArray());	
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getCapacityInfo(String limitIds){
		StringBuffer sql = new StringBuffer("select the_allocated_capacity,the_available_capacity,the_display_name,subsystem_id,os_type from v_res_storage_subsystem where 1 = 1 ");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and subsystem_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageType(String type){
		String sql="select subsystem_id as ele_id,the_display_name as ele_name, case os_type " +
				"when 25 then 'DS' " +
				"when 21 then 'SVC' " +
				"when 38 then 'SVC' " +
				"when 37 then 'BSP' " +
				"when 15 then 'BSP' " +
				"end " +
				"as type " +
				"from V_RES_STORAGE_SUBSYSTEM ";
		if(type!=null && type.length()>0){
			sql+="where type = '"+type+"'";
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public DataRow getType(Integer subsystemId){
		String sql="select subsystem_id as ele_id,the_display_name as ele_name, case os_type " +
		"when 25 then 'DS' " +
		"when 21 then 'SVC' " +
		"when 38 then 'SVC' " +
		"when 37 then 'BSP' " +
		"when 15 then 'BSP' " +
		
		"when 10 then 'BSP' " +
		
		"end " +
		"as type " +
		"from V_RES_STORAGE_SUBSYSTEM "+
		"where subsystem_id = "+subsystemId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	public List<DataRow> getNasList(Integer nasId){
		String sql="SELECT * FROM t_res_nas s where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(nasId!=null&&nasId.SIZE>0 && nasId>0){
			sb.append("and s.id = ? ");
			args.add(nasId);
		}
		sb.append("order by s.id desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	public DataRow getNasInfo(Integer nasId){
		String sql="select s.* from t_res_nas s where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(nasId!=0 && nasId>0&&nasId.SIZE>0){
			sb.append("and s.nas_id = ? ");
			args.add(nasId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());	
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageNames(String limitIds) {
		String sql = String.format("select case when c.CUSTOM_NAME is null then a.the_backend_name else c.CUSTOM_NAME end as the_backend_name," +
				"a.the_backend_name as the_display_name,a.subsystem_id ,'%s' as dbType " +
				"from v_res_storage_subsystem a left join t_custom_name c " +
				"on a.subsystem_id = c.device_id where 1 = 1", SrContant.DBTYPE_TPC);
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + " and a.subsystem_id in (" + limitIds + ")";
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public boolean storageRename(List<DataRow> list){
//		System.out.println(list);
		if(list == null || list.size() == 0){ return true; }
		Session session = null;
		try {
			session = getSession(WebConstants.DB_TPC);
			session.beginTrans();
			long id, temp;
			for (DataRow row : list) {
				id = row.getLong("device_id");
				temp = session.queryLong("select count(1) from v_res_storage_subsystem where subsystem_id = " + id);
				if (temp > 0L) {
					session.update("delete from t_custom_name where unit_code = 'Tpc.StorageSystem' and device_id = " + id);
				}
				session.insert("t_custom_name", row);
			}
			session.commitTrans();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (session != null)
			{
				session.rollbackTrans();
			}
			return false;
		}finally{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
	}
	
}
