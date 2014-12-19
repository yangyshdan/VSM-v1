package com.huiming.service.volume;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class VolumeService extends BaseService {
	/**
	 * 卷分页信息
	 * @param curPage
	 * @param numPerPage
	 * @param name
	 * @param greatLogical_Capacity
	 * @param lessLogical_Capacity
	 * @param pool_id
	 * @param system_id
	 * @return
	 */
	public DBPage getVolumePage(int curPage, int numPerPage, String theDisplayName, String lessCapacity,String greatCapacity,
			String pool_id, Integer system_id) {
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
				"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
				"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (theDisplayName != null && theDisplayName.length() > 0) {
			sb.append(" and v.the_display_name like ? ");
			args.add("%" + theDisplayName + "%");
		}
		if (lessCapacity != null && lessCapacity.length()> 0) {
			sb.append(" and v.the_capacity >= ?");
			args.add(lessCapacity);
		}
		if (greatCapacity != null && greatCapacity.length()> 0) {
			sb.append(" and v.the_capacity <= ?");
			args.add(greatCapacity);
		}
		if (pool_id != null && pool_id.length() > 0) {
			sb.append(" and v.pool_id = ?");
			args.add(pool_id);
		}
		if (system_id != null && system_id > 0) {
			sb.append(" and v.subsystem_id = ?");
			args.add(system_id);
		}
		sb.append(" order by v.svid desc");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeCapacityInfo(Integer subsystemId){
		String sql="select the_used_space,the_display_name,svid,subsystem_id from v_res_storage_volume where subsystem_id = "+subsystemId+" order by the_used_space desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,WebConstants.DEFAULT_CONFIG_TOP);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeInfo(String theDisplayName, String lessCapacity,String greatCapacity,String pool_id, Integer system_id){
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
		"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (theDisplayName != null && theDisplayName.length() > 0) {
			sb.append(" and v.the_display_name like ? ");
			args.add("%" + theDisplayName + "%");
		}
		if (lessCapacity != null && lessCapacity.length() > 0) {
			sb.append(" and v.the_capacity >= ?");
			args.add(lessCapacity);
		}
		if (greatCapacity != null && greatCapacity.length()> 0) {
			sb.append(" and v.the_capacity <= ?");
			args.add(greatCapacity);
		}
		if (pool_id != null && pool_id.length() > 0) {
			sb.append(" and v.pool_id = ?");
			args.add(pool_id);
		}
		if (system_id != null && system_id > 0) {
			sb.append(" and v.subsystem_id = ?");
			args.add(system_id);
		}
		sb.append(" order by v.the_used_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 卷详细信息
	 * @param id
	 * @param system_id
	 * @return
	 */
	public DataRow getVolumeById(Integer id) {
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
		"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		sb.append("and v.svid = "+id);
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolume(Integer subsystemId){
		String sql="select svid,the_display_name,subsystem_id from v_res_storage_volume where subsystem_id = "+subsystemId;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}

//	public List<DataRow> getTop10IOPs(){
//		String sql = "SELECT * FROM (select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A3 as iops FROM PRF_TARGET_SVC_VDISK"
//			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A427 as iops FROM PRF_TARGET_BSP_VOLUME"
//			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A183 as iops FROM PRF_TARGET_DSVOL) a ORDER BY a.IOPS DESC  Fetch First 10 Rows Only";
//		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
//	}
	
	public List<DataRow> getTop10IOPs(){
		String sql = "select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A3 as iops FROM PRF_TARGET_SVC_VDISK ORDER BY A3 DESC";
			
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,10);
	}
	

	
	public List<DataRow> getTop10DataRate(){
		String sql = "SELECT * FROM (select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A9 as datarate FROM PRF_TARGET_SVC_VDISK"
			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A433 as datarate FROM PRF_TARGET_BSP_VOLUME"
			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A195 as datarate FROM PRF_TARGET_DSVOL) ORDER BY datarate DESC Fetch First 10 Rows Only";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
}
