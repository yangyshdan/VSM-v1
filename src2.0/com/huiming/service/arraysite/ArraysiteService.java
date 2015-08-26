package com.huiming.service.arraysite;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ArraysiteService extends BaseService{
	
	public DBPage getArraysitePage(int curPage,int numPerPage,String name,String raidLevel,Integer subsystemId){
		String sql = "select a.*,s.the_display_name as sub_name,r.the_display_name as rank_name,p.the_display_name as pool_name " +
				"from v_res_arraysite a,v_res_storage_subsystem s,v_res_storage_rank r,v_res_storage_pool p " +
				"where a.subsystem_id = s.subsystem_id " +
				"and a.storage_extent_id = r.storage_extent_id " +
				"and a.the_pool_id = p.pool_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and a.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(raidLevel!=null && raidLevel.length()>0){
			sb.append("and a.raid_level = ? ");
			args.add(raidLevel);
		}
		if(subsystemId!=null && subsystemId>0){
			sb.append("and a.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getArraysiteList(String name,String raidLevel,Integer subsystemId){
		String sql = "select a.*,s.the_display_name as sub_name,r.the_display_name as rank_name,p.the_display_name as pool_name " +
		"from v_res_arraysite a,v_res_storage_subsystem s,v_res_storage_rank r,v_res_storage_pool p " +
		"where a.subsystem_id = s.subsystem_id " +
		"and a.storage_extent_id = r.storage_extent_id " +
		"and a.the_pool_id = p.pool_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and a.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(raidLevel!=null && raidLevel.length()>0){
			sb.append("and a.raid_level = ? ");
			args.add(raidLevel);
		}
		if(subsystemId!=null && subsystemId>0){
			sb.append("and a.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getArraysiteInfo(Integer arraysiteId){
		String sql = "select a.*,s.the_display_name as sub_name,r.the_display_name as rank_name,p.the_display_name as pool_name " +
		"from v_res_arraysite a,v_res_storage_subsystem s,v_res_storage_rank r,v_res_storage_pool p " +
		"where a.subsystem_id = s.subsystem_id " +
		"and a.storage_extent_id = r.storage_extent_id " +
		"and a.the_pool_id = p.pool_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(arraysiteId!=0 && arraysiteId>0){
			sb.append("and a.disk_group_id =? ");
			args.add(arraysiteId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString(),args.toArray());
	}
	
	public DBPage getDiskPage(int curPage,int numPerPage,Integer arraysiteId){
		String sql="select d.*,s.the_display_name as sub_name ,a.the_display_name as diskgroup_name,v.vendor_name,m.model_name " +
		"from v_res_physical_volume d " +
		"left join v_res_arraysite a on d.the_arraysite_id=a.disk_group_id " +
		"inner join v_res_storage_subsystem s on d.subsystem_id = s.subsystem_id " +
		"inner join v_res_vendor v on d.vendor_id = v.vendor_id " +
		"inner join v_res_model m on d.model_id = m.model_id " +
		"where 1=1 and d.the_arraysite_id = "+arraysiteId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql, curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskList(Integer arraysiteId){
		String sql="select d.*,s.the_display_name as sub_name ,a.the_display_name as diskgroup_name,v.vendor_name,m.model_name " +
		"from v_res_physical_volume d " +
		"left join v_res_arraysite a on d.the_arraysite_id=a.disk_group_id " +
		"inner join v_res_storage_subsystem s on d.subsystem_id = s.subsystem_id " +
		"inner join v_res_vendor v on d.vendor_id = v.vendor_id " +
		"inner join v_res_model m on d.model_id = m.model_id " +
		"where 1=1 and d.the_arraysite_id = "+arraysiteId;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskNum(Integer subsystemId){
		String sql="select count(*) as num,a.disk_group_id,a.the_display_name,a.subsystem_id from v_res_arraysite a,v_res_physical_volume d where d.the_arraysite_id = a.disk_group_id " +
				"and a.subsystem_id = " +subsystemId+
				" group by a.disk_group_id,a.the_display_name,a.subsystem_id";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,10);
	}
	public List<DataRow> getArrayInfo() {
		String sql="select disk_group_id as id,the_display_name as name from v_res_arraysite";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	public List<DataRow> getResArrayList() {
		String sql="select * from t_res_array";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	public DataRow getResArrayInfo(Integer arrayId){
		String sql="SELECT * FROM t_res_array where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(arrayId!=0 && arrayId>0){
			sb.append("and array_id = ? ");
			args.add(arrayId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
}
