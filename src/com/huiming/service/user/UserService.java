package com.huiming.service.user;

import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

/**
 * 
 * @Name UserService
 * @Author gugu
 * @Date 2013-8-8����04:36:22
 * @Description TODO
 */
public class UserService extends BaseService{
	/**
	 * @Title: getUserByName
	 * @Description: TODO
	 * @param name
	 * @return
	 * DataRow
	 */
	public DataRow getUserByName(String name){
		String sql = "select fid,fname,floginname,fpassword, froleid from tsuser where floginname = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{name});
	}
	/***
	 * 
	 * @param fid
	 * @return
	 */
	public DataRow getUserById(String fid){
		String sql = "select fid,fname,floginname,fpassword,femail, froleid from tsuser where fid = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{fid});
	}
	/**
	 * @Title 
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getPage(int curPage,int numPerPage){
		String sql="select fid,fname,floginname,femail from tsuser";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql, curPage, numPerPage);
	}
	
	public DBPage getPage(int curPage,int numPerPage,String userName){
		String sql="SELECT fid,fname,floginname,femail FROM tsuser WHERE floginname LIKE ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql, new Object[]{"%"+userName+"%"}, curPage, numPerPage);
		
	}
	/**
	 * @Title 保存
	 * @param data
	 */
	public void save(DataRow data){
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tsuser", data);
	}
	
	public void update(DataRow data){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("tsuser", data, "fid", data.getString("fid"));
	}
	
	public void delete(String fid){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tsuser", "fid", fid);
	}
	/**
	 * @Title 获取最近添加用户id
	 * @return
	 */
	public int getLateId(){
		String sql="SELECT MAX(fid) FROM tsuser";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql);
	} 
}
