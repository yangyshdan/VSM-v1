package com.huiming.service.rank;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class RankService extends BaseService {
	public DBPage getRankPage(int curPage, int numPerPage, String name,String type, Integer subsystemId) {
		String sql = "select r.*,s.the_display_name as sub_name,p.the_display_name as pool_name "
				+ "from V_RES_STORAGE_RANK r,v_res_storage_subsystem s,v_res_storage_pool p "
				+ "where s.subsystem_id = r.subsystem_id and r.pool_id = p.pool_id ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if (name != null && name.length() > 0) {
			sb.append("and r.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (type != null && type.length() > 0) {
			sb.append("and r.the_type = ? ");
			args.add(type);
		}
		if (subsystemId != null && subsystemId > 0) {
			sb.append("and r.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("order by r.the_total_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getRankList(String name, String type,Integer subsystemId) {
		String sql = "select r.*,s.the_display_name as sub_name,p.the_display_name as pool_name "
				+ "from V_RES_STORAGE_RANK r,v_res_storage_subsystem s,v_res_storage_pool p "
				+ "where s.subsystem_id = r.subsystem_id and r.pool_id = p.pool_id ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if (name != null && name.length() > 0) {
			sb.append("and r.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (type != null && type.length() > 0) {
			sb.append("and r.the_type = ? ");
			args.add(type);
		}
		if (subsystemId != null && subsystemId > 0) {
			sb.append("and r.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("order by r.the_total_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getCapacityInfo(Integer subsystemId){
		String sql="select subsystem_id,the_total_space as number,the_display_name,storage_extent_id as devId " +
				"from V_RES_STORAGE_RANK "+
				"where subsystem_id = "+subsystemId+" order by the_total_space desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,10);
	}
	
	public DataRow getRankInfo(Integer rankId){
		String sql = "select r.*,s.the_display_name as sub_name,p.the_display_name as pool_name "
			+ "from V_RES_STORAGE_RANK r,v_res_storage_subsystem s,v_res_storage_pool p "
			+ "where s.subsystem_id = r.subsystem_id and r.pool_id = p.pool_id "
			+ "and r.storage_extent_id = "+rankId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
}
