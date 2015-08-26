package com.huiming.service.ddm;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class DdmService extends BaseService {
	/**
	 * DDM分页信息
	 * 
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getDdmPage(int currentPage, int numPerPage,String name) {
		String sql = "select * from  t_res_storage_ddm where 1=1";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append(" and name like ?");
			args.add("%"+name+"%");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), currentPage,numPerPage);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getDdmList(String name){
		StringBuffer sql = new StringBuffer("select * from t_res_storagevolume where 1=1");
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sql.append(" and name = ? ");
			args.add(name);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), args.toArray());
	}
	//添加
	public void addDdm(List<DataRow> ddms,Integer subSystemID){
		for (int i = 0; i <ddms.size(); ++i) {
			DataRow ddm = ddms.get(i);
			ddm.set("subsystem_id",subSystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_RES_STORAGE_DDM", ddm);
		}		
	}
	//更新
	public void updateDDMInfo(List<DataRow> ddms,Integer subSystemID){
		for (DataRow dataRow : ddms) {
			dataRow.set("subsystem_id",subSystemID);
			String sql = "select ddm_id from t_res_storage_ddm where name = '"+dataRow.getString("name")+"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size() > 0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storage_ddm", dataRow, "ddm_id", dataRow.getString("ddm_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_RES_STORAGE_DDM", dataRow);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDDMInfoByName(String name,Integer subSystemId){
		String sql="select d.* from t_res_storage_ddm d where d.name = '"+name+"' and d.SUBSYSTEM_ID = "+subSystemId+" order by d.update_timestamp desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public Long checkPrimaryKey(Long key){
		String sql="select T.* from t_res_storage_ddm T where T.DDM_ID = "+key;
		List<DataRow> lists = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		if(lists!=null && lists.size()>0){
			return null;
		}
		return key;
	}
	
	public DBPage getDiskPage(int curPage, int numPerPage, Integer subsystemId, Integer diskgroupId) {
		String sql = "SELECT d.* FROM t_res_storage_ddm d,t_map_diskgroup2storage_ddm m WHERE d.DDM_ID = m.DDM_ID ";
		StringBuffer sb = new StringBuffer(sql);
		List args = new ArrayList();
		if ((subsystemId != null) && (subsystemId.intValue() > 0)) {
			sb.append("and d.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if ((diskgroupId != null) && (diskgroupId.intValue() > 0)) {
			sb.append("and m.diskgroup_id = ? ");
			args.add(diskgroupId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(), args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取磁盘信息列表
	 * @param subsystemId
	 * @param diskgroupId
	 * @return
	 */
	public List<DataRow> getDiskInfoList(Integer subsystemId, Integer diskgroupId) {
		String sql = "select d.* from t_res_storage_ddm d,t_map_diskgroup2storage_ddm m where d.ddm_id = m.ddm_id and d.subsystem_id = ? and m.diskgroup_id = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{subsystemId,diskgroupId});
	}
}
