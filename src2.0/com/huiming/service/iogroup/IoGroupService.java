package com.huiming.service.iogroup;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class IoGroupService extends BaseService{
	public DBPage getIogroupPage(int curPage,int numPerPage,String name,Integer subsystemId){
		String sql="select i.*,s.the_display_name as sub_name " +
				"from V_RES_STORAGE_IOGROUP i,V_RES_STORAGE_SUBSYSTEM s " +
				"where i.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and i.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(subsystemId!=null && subsystemId >0 ){
			sb.append("and i.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getIogroupList(String name,Integer subsystemId){
		String sql="select i.*,s.the_display_name as sub_name " +
			"from V_RES_STORAGE_IOGROUP i,V_RES_STORAGE_SUBSYSTEM s " +
			"where i.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and i.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(subsystemId!=null && subsystemId >0 ){
			sb.append("and i.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getIogroupInfo(Integer iogroupId){
		String sql="select i.*,s.the_display_name as sub_name " +
		"from V_RES_STORAGE_IOGROUP i,V_RES_STORAGE_SUBSYSTEM s " +
		"where i.subsystem_id = s.subsystem_id and i.io_group_id = "+iogroupId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getdevInfo(Integer subsystemId){
		String sql="select io_group_id,the_display_name,subsystem_id from V_RES_STORAGE_IOGROUP where subsystem_id = "+subsystemId;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
}	
