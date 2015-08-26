package com.huiming.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;


public class UserService extends BaseService {
	
	public DBPage getUserPage(int curPage, int numPerPage, String userName, String loginName,
			String account, String idCard, String gender, 
			String startHireDate, String endHireDate, long userId, String role){
		StringBuilder sb = new StringBuilder(100);
		sb.append("select u.* from tsuser u where 1=1 ");
		List<Object> args = new ArrayList<Object>(8);
		setSQL(args, sb, "fname", userName);
		setSQL(args, sb, "user_account", account);
		setSQL(args, sb, "id_card", idCard);
		setSQL(args, sb, "gender", gender);
		
		if(!isBlank(startHireDate)){
			args.add(startHireDate);
			sb.append(" and u.hire_date>= ? ");
		}
		if(!isBlank(endHireDate)){
			args.add(endHireDate);
			sb.append(" and u.hire_date<= ? ");
		}
		if(SrContant.ROLE_USER.equalsIgnoreCase(role)){
			// 如果是普通用户，则不要显示超级管理员
			args.add(SrContant.ROLE_SUPER);
			sb.append(" and u.froleid<> ? ");
		}
		JdbcTemplate jdbc = getJdbcTemplate(WebConstants.DB_DEFAULT);
		DBPage dbPage = null;
		if(jdbc != null){
			dbPage = jdbc.queryPage(sb.toString(), args.toArray(), curPage, numPerPage);
		}
		return dbPage;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getRolesByUserId(Long userId){
		String sql = "SELECT r.*,ur.froleid IS NOT NULL AS checked FROM tsrole r LEFT JOIN " +
				"(SELECT DISTINCT froleid FROM tsuserrole &) ur ON r.fid=ur.froleid ORDER BY r.fid";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.replace("&",
				userId != null && userId > 0? " where fuserid=" + userId : ""));
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getRolesById(Long roleId){
		String sql = "SELECT * FROM tsrole & order by fid";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.replace("&",
				roleId != null && roleId > 0? "where fid=" + roleId : ""));
	}
	
	public DataRow getUserById(long fid){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(
				"select * from tsuser where fid=?", new Object[]{fid});
	}
	
	public void deleteUserById(long fid){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tsuser", "fid", fid);
	}
	
	public DataRow getUserByName(String name){
		String sql = "select fid,fname,floginname,fpassword, froleid from tsuser where floginname = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{name});
	}
	
	public void updateUser(DataRow dr, long userId){
		if(dr != null){
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("tsuser", dr, "fid", userId);
		}
	}
	
	public void updateUserRoles(List<DataRow> drs, long userId){
		if(drs != null && drs.size() > 0){
			JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
			for(DataRow dr : drs){
				srDB.insert("tsuserrole", dr);
			}
		}
	}
	
	public void deleteUserRole(long userId) {
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		srDB.delete("tsuserrole", "fuserid", userId);
	}
	
	public Long saveUser(DataRow dr){
		if(dr != null){
			return Long.parseLong(getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tsuser", dr));
		}
		return -1L;
	}
	
	public void saveUserRoles(List<DataRow> drs){
		if(drs != null && drs.size() > 0){
			JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
			for(DataRow dr : drs){
				srDB.insert("tsuserrole", dr);
			}
		}
	}
	
	/**
	 * @see 
	 * @param roleIds
	 * @param menuType
	 * @param isAllUnchecked
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getMenuItemsByRoleId(String roleIds, int action){
		if(isBlank(roleIds)){ roleIds = "-1"; }
		// id, name, children, checked, url, icon, devtype, devid, isparent
		// id, devid, devtype, pid, name, url, icon, checked, devtype, isparent
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		String sql;
		List<DataRow> drs;
		if(action == SrContant.STATE_CHECK){
			// 就是将tsrolemenu的数据查出来重新合成一棵树
			sql = String.format("SELECT distinct rm.menu_id AS id,rm.fmenuid AS devid,rm.fdevtype AS devtype,rm.parentid AS pid," +
					"rm.menu_name AS NAME,1 AS checked FROM tsrolemenu rm where rm.froleId IN (%s) ORDER BY fid",
					roleIds);
			drs = srDB.query(sql);
		}
		else if(action == SrContant.STATE_ADD){
			String prefix = "m_";
			sql = String.format("SELECT distinct concat('%s',m.fid) as id,m.fid as devid,concat('%s',m.fparentid) AS pid," +
			"m.fname AS name,fpageurl AS url,ficon AS icon,0 AS checked,devtype,isparent FROM tsmenu m " +
			" WHERE m.FEnabled=1 ORDER BY m.fid,m.FIndex", prefix, prefix);
			drs = srDB.query(sql);
		}
		else {
			String prefix = "m_";
			sql = String.format("SELECT CONCAT('%s',m.fid) AS id,m.fid AS devid,CONCAT('%s',m.fparentid) AS pid,m.fname AS NAME,fpageurl AS url,"+
					"ficon AS icon,CASE WHEN rm.FMenuId IS NULL THEN 0 ELSE 1 END AS checked,m.devtype,m.isparent FROM tsmenu m  " +
					"LEFT JOIN (SELECT DISTINCT FMenuId FROM tsrolemenu WHERE FRoleId IN(%s)) rm ON rm.FMenuId=m.fid " +
					" WHERE m.FEnabled=1 ORDER BY m.fid,m.FIndex", prefix, prefix, roleIds);
			drs = srDB.query(sql);
		}
		return drs;
	}
	
	/**
	 * @see 
	 * @param roleIds
	 * @param menuType
	 * @param isAllUnchecked
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getMenuItemsByRoleIdAndMenuType_bak_01(String roleIds, boolean isAllUnchecked){
		String _roleIds = "";
		if(!isBlank(roleIds)){ _roleIds = " where rm.FRoleId IN (" + roleIds + ") "; }
		
		String prefix = "m_";
//		String type = "MENU";
		String sql;
		// id,dev_id,dev_type,pid,name,url,icon,checked
		if(isAllUnchecked){ // 表示全部选出，并不用考虑
			sql = "SELECT CONCAT('"+prefix+"', m.fid) as id,m.fid as devid,CONCAT('"+prefix+"',m.FParentId) AS pid,m.fname AS NAME," +
					"fpageurl AS url,ficon AS icon,0 as checked,devtype,isparent FROM tsmenu m " +
			"WHERE m.fid IN (SELECT DISTINCT FMenuId FROM tsrolemenu rm " + _roleIds
			+") AND m.FEnabled=1 ORDER BY m.fid,m.FIndex";
		}
		else {
			sql= "SELECT CONCAT('"+prefix+"', m.fid) as id,m.fid as devid,CONCAT('"+prefix+"',m.FParentId) AS pid,m.fname AS NAME,fpageurl AS url," +
				"ficon AS icon,rm.fid IS NOT NULL AS checked,devtype,isparent FROM tsmenu m " +
				" LEFT JOIN (SELECT DISTINCT FMenuId AS fid FROM tsrolemenu rm " + _roleIds + ") rm"+
						" ON m.fid=rm.fid AND m.FEnabled=1 ORDER BY m.fid,m.FIndex";
		}
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		List<DataRow> drs = srDB.query(sql);
		if(drs == null || drs.size() == 0){//CONCAT('menu_', m.fid) as fid,
			drs = srDB.query("SELECT CONCAT('"+prefix+"', m.fid) as id,m.fid as devid,CONCAT('"+prefix+"',m.FParentId) AS pid," +
					"m.fname AS NAME,fpageurl AS url,ficon AS icon,0 AS checked,devtype,isparent FROM tsmenu m " +
					" WHERE m.FEnabled=1 ORDER BY m.fid,m.FIndex");
		}
		// id,name,children,checked,url,icon,devtype,devid,isparent
		return drs;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getMenuItemsByRoleIdAndMenuType_bak(String roleIds, boolean isAllUnchecked){
		String _roleIds = "";
		if(!isBlank(roleIds)){ _roleIds = " where rm.FRoleId IN (" + roleIds + ") "; }
		
		String sql;
		if(isAllUnchecked){ // 表示全部选出，并不用考虑
			sql = "SELECT m.fid,m.FParentId AS pid,m.fname AS NAME,fpageurl AS url,ficon AS icon,0 as checked FROM tsmenu m " +
			"WHERE m.fid IN (SELECT DISTINCT FMenuId FROM tsrolemenu rm " + _roleIds
			+") AND m.FEnabled=1 ORDER BY fid,FIndex";
		}
		else {
			sql= "SELECT m.fid,m.FParentId AS pid,m.fname AS NAME,fpageurl AS url," +
				"ficon AS icon,rm.fid IS NOT NULL AS checked FROM tsmenu m " +
				" LEFT JOIN (SELECT DISTINCT FMenuId AS fid FROM tsrolemenu rm " + _roleIds + ") rm"+
						" ON m.fid=rm.fid AND m.FEnabled=1 ORDER BY m.fid,m.FIndex";
		}
		// id,name,children,checked,url,icon
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMenuIdsByUserId(long userId){
		String sql = String.format("SELECT CONCAT(fdevtype,'_',fmid) AS fmid FROM (SELECT rm.fdevtype,rm.fmenuid AS fmid FROM tsrolemenu rm " +
			" WHERE rm.FRoleId IN (SELECT DISTINCT FRoleId FROM tsuserrole WHERE FUserId=%s) and rm.fdevtype in(%s) ORDER BY fmid) t",
			userId, "'m_settings','m_sys_setting','m_user_mgnt','m_role_mgnt'");
		
		List<DataRow> drs = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[0]);
		Map<String, Object> set;
		if(drs != null && drs.size() > 0){
			set = new HashMap<String, Object>(drs.size() + 1);
			for(DataRow dr : drs){
				set.put(dr.getString("fmid"), 1);
			}
		}
		else {
			set = new HashMap<String, Object>(1);
		}
		set.put("userId", userId);
		return set;
	}
	
	private void setSQL(List<Object> args, StringBuilder sb, String column, String arg){
		if(!isBlank(arg)){
			args.add("%" + arg + "%");
			sb.append(" and u.");
			sb.append(column);
			sb.append(" like ? ");
		}
	}
	private boolean isBlank(String str){
		return str == null || str.trim().length() == 0;
	}
}
