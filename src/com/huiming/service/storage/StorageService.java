package com.huiming.service.storage;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class StorageService extends BaseService{
	public DBPage getStoragePage(int curPage,int numPerPage,String storageName,String ipAddress,
			String type,String serialNumber,Integer startPoolCap,Integer endPoolCap,
			Integer startPoolAvailableCap,Integer endPoolAvailableCap){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_storage_subsystem s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(storageName!=null && storageName.length()>0){
			sb.append("and s.the_display_name like ? ");
			args.add("%"+storageName+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if(type!=null && type.length()>0){
			sb.append("and s.the_type = ? ");
			args.add(type);
		}
		if(serialNumber!=null && serialNumber.length()>0){
			sb.append("and serial_number = ? ");
			args.add(serialNumber);
		}
		if(startPoolCap!=null && startPoolCap>0){
			sb.append("and the_storage_pool_consumed_space >= ? ");
			args.add(startPoolCap);
		}
		if(endPoolCap!=null && endPoolCap>0){
			sb.append("and the_storage_pool_consumed_space <= ? ");
			args.add(endPoolCap);
		}
		if(startPoolAvailableCap!=null && startPoolAvailableCap>0){
			sb.append("and the_storage_pool_available_space >= ? ");
			args.add(startPoolAvailableCap);
		}
		if(endPoolAvailableCap!=null && endPoolAvailableCap>0){
			sb.append("and the_storage_pool_available_space <= ? ");
			args.add(endPoolAvailableCap);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageList(String storageName,String ipAddress,
			String type,String serialNumber,Integer startPoolCap,Integer endPoolCap,
			Integer startPoolAvailableCap,Integer endPoolAvailableCap){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_storage_subsystem s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(storageName!=null && storageName.length()>0){
			sb.append("and s.the_display_name like ? ");
			args.add("%"+storageName+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if(type!=null && type.length()>0){
			sb.append("and s.the_type = ? ");
			args.add(type);
		}
		if(serialNumber!=null && serialNumber.length()>0){
			sb.append("and serial_number = ? ");
			args.add(serialNumber);
		}
		if(startPoolCap!=null && startPoolCap>0){
			sb.append("and the_storage_pool_consumed_space >= ? ");
			args.add(startPoolCap);
		}
		if(endPoolCap!=null && endPoolCap>0){
			sb.append("and the_storage_pool_consumed_space <= ? ");
			args.add(endPoolCap);
		}
		if(startPoolAvailableCap!=null && startPoolAvailableCap>0){
			sb.append("and the_storage_pool_available_space >= ? ");
			args.add(startPoolAvailableCap);
		}
		if(endPoolAvailableCap!=null && endPoolAvailableCap>0){
			sb.append("and the_storage_pool_available_space <= ? ");
			args.add(endPoolAvailableCap);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubsystemNames(String os_type){
		StringBuffer sql= new StringBuffer("select the_display_name as name,os_type,subsystem_id as id from v_res_storage_subsystem where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(os_type!=null && os_type.length()>0){
			sql.append("and os_type=? ");
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
		"from (select v.*,v.last_probe_time ||'' as the_last_probe_time,case v.os_type when 25 then 'DS' when 21 then 'SVC' when 38 then 'SVC' else 'BSP' end as stype from V_RES_STORAGE_SUBSYSTEM v) s," +
		"v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id "+
		"and s.stype = '"+type+"'";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public DataRow getSubsystemInfo(Integer subsystemId){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_storage_subsystem s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id "+
		"and s.subsystem_id = "+subsystemId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
		
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getCapacityInfo(){
		String sql="select the_allocated_capacity,the_available_capacity,the_display_name,subsystem_id,os_type " +
				"from v_res_storage_subsystem where 1=1 ";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
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
		"end " +
		"as type " +
		"from V_RES_STORAGE_SUBSYSTEM "+
		"where subsystem_id = "+subsystemId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	
}
