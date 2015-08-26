package com.huiming.service.extend;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ExtendService extends BaseService{
	public DBPage getExtendPage(int curPage,int numPerPage,String name,String deviceId,
			Integer startCap,Integer endCap,Integer startAvailableCap,Integer endAvailableCap,Integer subsystemId){
		String sql="select e.*,s.the_display_name as sub_name,p.the_display_name as pool_name " +
				"from v_res_storage_extent e,v_res_storage_subsystem s,v_res_storage_pool p " +
				"where e.subsystem_id = s.subsystem_id " +
				"and e.pool_id = p.pool_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and e.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(subsystemId!=null && subsystemId >0){
			sb.append("and e.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(deviceId!=null && deviceId.length()>0){
			sb.append("and e.device_id = ? ");
			args.add(deviceId);
		}
		if(startCap!=null && startCap>0){
			sb.append("and e.the_total_space >= ? ");
			args.add(startCap);
		}
		if(endCap!=null && endCap>0){
			sb.append("and e.the_total_space <= ? ");
			args.add(endCap);
		}
		if(startAvailableCap!=null && startAvailableCap > 0){
			sb.append("and e.the_available_space >= ? ");
			args.add(startAvailableCap);
		}
		if(endAvailableCap!=null && endAvailableCap > 0){
			sb.append("and e.the_available_space <= ? ");
			args.add(endAvailableCap);
		}
		sb.append("order by e.the_total_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getExtendList(String name,String deviceId,
			Integer startCap,Integer endCap,Integer startAvailableCap,Integer endAvailableCap,Integer subsystemId){
		String sql="select e.*,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_extent e,v_res_storage_subsystem s,v_res_storage_pool p " +
		"where e.subsystem_id = s.subsystem_id " +
		"and e.pool_id = p.pool_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and e.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(subsystemId!=null && subsystemId >0){
			sb.append("and e.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(deviceId!=null && deviceId.length()>0){
			sb.append("and e.device_id = ? ");
			args.add(deviceId);
		}
		if(startCap!=null && startCap>0){
			sb.append("and e.the_total_space >= ? ");
			args.add(startCap);
		}
		if(endCap!=null && endCap>0){
			sb.append("and e.the_total_space <= ? ");
			args.add(endCap);
		}
		if(startAvailableCap!=null && startAvailableCap > 0){
			sb.append("and e.the_available_space >= ? ");
			args.add(startAvailableCap);
		}
		if(endAvailableCap!=null && endAvailableCap > 0){
			sb.append("and e.the_available_space <= ? ");
			args.add(endAvailableCap);
		}
		sb.append("order by e.the_total_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getCapacityInfo(Integer subsystemId){
		String sql="select the_display_name,subsystem_id,storage_extent_id,the_total_space " +
				"from v_res_storage_extent where subsystem_id = "+subsystemId+" order by the_total_space desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,WebConstants.DEFAULT_CONFIG_TOP);
		
	}
	
	public DataRow getExtendInfo(Integer extendId){
		String sql="select e.*,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_extent e,v_res_storage_subsystem s,v_res_storage_pool p " +
		"where e.subsystem_id = s.subsystem_id " +
		"and e.pool_id = p.pool_id and storage_extent_id = "+extendId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
}
