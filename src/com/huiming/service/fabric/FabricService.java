package com.huiming.service.fabric;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class FabricService extends BaseService{
	public DBPage getfabricPage(String name,int curPage,int numPerPage){
		String sql="select f.* from V_RES_FABRIC f where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and f.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getfabricList(String name){
		String sql="select f.* from V_RES_FABRIC f where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and f.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getFabricInfo(String fabricId){
		String sql="select * from V_RES_FABRIC where FABRIC_ID = "+fabricId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList(String fabricId){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb= new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(fabricId!=null && fabricId.length()>0){
			sb.append("and s.THE_FABRIC_ID = ? ");
			args.add(fabricId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DBPage getSwitchPage(String fabricId,int curPage,int numPerPage){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb= new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(fabricId!=null && fabricId.length()>0){
			sb.append("and s.THE_FABRIC_ID = ? ");
			args.add(fabricId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
}
