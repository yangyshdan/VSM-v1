package com.huiming.service.disk;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class DiskService extends BaseService{
	
	/**
	 * 分页
	 * @param curPage
	 * @param numPerPage
	 * @param name
	 * @param startCap
	 * @param endCap
	 * @return
	 */
	public DBPage getDiskPage(int curPage,int numPerPage,String name,Integer startCap,Integer endCap,Integer subsystemId){
		String sql="select d.*,s.the_display_name as sub_name ,a.the_display_name as diskgroup_name,v.vendor_name,m.model_name " +
				"from v_res_physical_volume d " +
				"left join v_res_arraysite a on d.the_arraysite_id=a.disk_group_id " +
				"inner join v_res_storage_subsystem s on d.subsystem_id = s.subsystem_id " +
				"inner join v_res_vendor v on d.vendor_id = v.vendor_id " +
				"inner join v_res_model m on d.model_id = m.model_id " +
				"where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and d.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(startCap!=null && startCap >0){
			sb.append("and d.the_capacity >= ? ");
			args.add(startCap);
		}
		if(endCap!=null && endCap>0){
			sb.append("and d.the_capacity <= ? ");
			args.add(endCap);
		}
		if(subsystemId!=null && subsystemId > 0){
			sb.append("and d.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("order by physical_volume_id");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskList(String name,Integer startCap,Integer endCap,Integer subsystemId){
		String sql="select d.*,s.the_display_name as sub_name ,a.the_display_name as diskgroup_name,v.vendor_name,m.model_name " +
		"from v_res_physical_volume d " +
		"left join v_res_arraysite a on d.the_arraysite_id=a.disk_group_id " +
		"inner join v_res_storage_subsystem s on d.subsystem_id = s.subsystem_id " +
		"inner join v_res_vendor v on d.vendor_id = v.vendor_id " +
		"inner join v_res_model m on d.model_id = m.model_id " +
		"where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and d.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(startCap!=null && startCap >0){
			sb.append("and d.the_capacity >= ? ");
			args.add(startCap);
		}
		if(endCap!=null && endCap>0){
			sb.append("and d.the_capacity <= ? ");
			args.add(endCap);
		}
		if(subsystemId!=null && subsystemId > 0){
			sb.append("and d.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("order by d.the_capacity desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getDiskInfo(Integer diskId){
		String sql="select d.*,s.the_display_name as sub_name ,a.the_display_name as diskgroup_name,v.vendor_name,m.model_name " +
		"from v_res_physical_volume d " +
		"left join v_res_arraysite a on d.the_arraysite_id=a.disk_group_id " +
		"inner join v_res_storage_subsystem s on d.subsystem_id = s.subsystem_id " +
		"inner join v_res_vendor v on d.vendor_id = v.vendor_id " +
		"inner join v_res_model m on d.model_id = m.model_id " +
		"where d.physical_volume_id="+diskId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskCap(Integer subsystemId){
		String sql="select the_display_name,the_capacity,physical_volume_id,subsystem_id from v_res_physical_volume where subsystem_id = "+subsystemId +" order by the_capacity desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,WebConstants.DEFAULT_CONFIG_TOP);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getdevInfo(Integer subsystemId){
		String sql="select the_display_name,physical_volume_id,subsystem_id from v_res_physical_volume where subsystem_id = "+subsystemId +" order by the_capacity desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
}
