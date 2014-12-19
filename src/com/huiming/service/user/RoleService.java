package com.huiming.service.user;

import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;


public class RoleService extends BaseService{

	public List<DataRow> getList(){
		String sql="select fid,fname from tsrole";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}

	public DataRow getById(String roleId){
		String sql="SELECT fid,fname FROM tsrole WHERE fid=?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{roleId});
	} 
	
	public void save(DataRow data){
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tsuserrole", data);
	}
	
	public void delete(String userId){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tsuserrole", "fuserid", userId);
	}
	
	public DataRow getByUserId(String userId){
		String sql="SELECT tr.* FROM tsuser tu,tsrole tr,tsuserrole tsr"+
		" WHERE tu.fid=? AND tu.fid=tsr.fuserid AND tsr.froleid=tr.fid";
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{userId});
	}
}
