package com.huiming.service.alert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class DeviceAlertService extends BaseService{
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getList(String level,String msg,String resourName,String resourType,String startDate,String endDate){
		StringBuffer sql = new StringBuffer("select * from tndevicelog where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(StringHelper.isNotEmpty(level)){
			sql.append(" and flevel = ? ");
			args.add(level);
		}
		if(StringHelper.isNotEmpty(msg)){
			sql.append(" and fmessage like ? ");
			args.add("%"+msg+"%");
		}
		if(StringHelper.isNotEmpty(resourName)){
			sql.append(" and fresourceName like ? ");
			args.add("%"+resourName+"%");
		}
		if(StringHelper.isNotEmpty(resourType)){
			sql.append(" and fresourcetype = ? ");
			args.add(resourType);
		}
		if(StringHelper.isNotEmpty(startDate)){
			try {
				args.add(format.parse(startDate));
				sql.append(" and ffirsttime > ? ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(StringHelper.isNotEmpty(endDate)){
			try {
				args.add(format.parse(endDate));
				sql.append(" and flasttime < ? ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sql.append(" order by ftime desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
	}
	
	public DataRow getAlertById(int id){
		String sql = "select * from TnDeviceAlertLog where fid = ? ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{id});
	}
	
	public void delAlert(String ids){
		String sql = "delete from TnDeviceAlertLog where fid in (" + ids +" )";
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
	}
	
	public List<DataRow> getLevelShare(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select count(*) as shareY , flevel from TnDeviceAlertLog group by flevel");
	}
	
	public List<DataRow> getAllShare(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select count(*) as shareY ,fresourcetype, flevel from TnDeviceAlertLog group by fresourcetype, flevel");
	}
	
	public List<DataRow> checkRule(String db,String sql){
		return getJdbcTemplate(db).query(sql);
	}
	
	public void insertLog(DataRow log){
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tndevicelog", log);
	}
		
	public List<DataRow> getNewCount(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select count(*) as logcount, fresourcetype,flevel from TnDeviceAlertLog where  to_days(ftime)=to_days(now()) group by fresourcetype,flevel");
	}
	
	public List<DataRow> getNewLevel(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select count(fcount) as logcount ,ftoptype,flevel,ftopname from tndevicelog where fstate = 0 and ftoptype in ('App','Physical','Virtual','Switch','Storage')  group by ftoptype,flevel,ftopname order by ftoptype,flevel desc");
	}
	
	/**
	 * @Title: getPage
	 * @Description: 告警
	 * @param curPage
	 * @param numPerPage
	 * @param computerId
	 * @param state
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @return
	 * DBPage
	 */
	public DBPage getLogPage(int curPage,int numPerPage,int logtype,String topId,String topname,String resourceId,String resourcename,String resourceType,int state , int level,String startDate, String endDate){
		StringBuffer sql = new StringBuffer("select a.fid,count(fcount) as fcount ,a.ftopid,a.fresourceid,a.flevel,min(a.fstate) as fstate ,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from (select * from tndevicelog order by flasttime desc ) a  where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(StringHelper.isNotEmpty(topId)){
			sql.append(" and a.ftopid = ? ");
			args.add(topId);
		}
		if(StringHelper.isNotEmpty(topname)){
			sql.append(" and a.ftopname like ? ");
			args.add("%"+topname+"%");
		}
		if(StringHelper.isNotEmpty(resourceId)){
			sql.append(" and a.fresourceid = ? ");
			args.add(resourceId);
		}
		if(StringHelper.isNotEmpty(resourcename)&&!resourcename.equalsIgnoreCase("undefined")){
			sql.append(" and a.fresourcename like ? ");
			args.add("%"+resourcename+"%");
		}
		if(level > -1){
			sql.append(" and a.flevel = ? ");
			args.add(level);
		}
		if(logtype > -1){
			sql.append(" and a.flogtype = ? ");
			args.add(logtype);
		}
		if(state > -1){
			sql.append(" and a.fstate = ? ");
			args.add(state);
		}
		if(StringHelper.isNotEmpty(resourceType)&& !resourceType.equalsIgnoreCase("undefined")){
			sql.append(" and a.ftoptype = ? ");
			args.add(resourceType);
		}
		if(StringHelper.isNotEmpty(startDate)&&startDate.equals("undefined")==false){
			try {
				sql.append(" and a.flasttime > ? ");
				args.add(startDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(StringHelper.isNotEmpty(endDate)&&endDate.equals("undefined")==false){
			try {
				sql.append(" and a.flasttime < ? ");
				args.add(endDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sql.append(" group by a.fruleid,a.ftopid order by a.flasttime desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString() ,args.toArray() , curPage, numPerPage);
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public List<DataRow> getLogList(JSONArray array){
		StringBuffer sql = new StringBuffer("select count(fcount) as fcount ,a.fresourceid,a.flevel,a.fstate,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.fdescript,min(DATE_FORMAT(a.ffirsttime,'%Y-%c-%d %H:%m:%s')) as ffirsttime,max(DATE_FORMAT(a.flasttime,'%Y-%c-%d %H:%m:%s')) as flasttime,a.fdetail from tndevicelog a  where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		for (Object object : array) {
			JSONObject obj = new JSONObject().fromObject(object);
			String type = obj.getString("id");
			if(type.equalsIgnoreCase("info")){
				args.add("'0'");
			}else if(type.equalsIgnoreCase("warning")){
				args.add("'1'");
			}else {
				args.add("'2'");
			}
		}
		sql.append(" and a.flevel in ("+args.toString().replace("[", "").replace("]", "")+" )");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}
	
//	public DBPage getPage(int curPage,int numPerPage,Integer resourceId,String level,String resourName,String resourType,String startDate,String endDate){
//		StringBuffer sql = new StringBuffer("select * from tndevicelog where 1=1 ");
//		List<Object> args = new ArrayList<Object>();
//		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		if(resourceId.SIZE>0&&resourceId!=null&&resourceId>0){
//			sql.append(" and FResourceId = ? ");
//			args.add(level);
//		}
//		if(StringHelper.isNotEmpty(level)){
//			sql.append(" and flevel = ? ");
//			args.add(level);
//		}
//		if(StringHelper.isNotEmpty(resourName)){
//			sql.append(" and fresourceName like ? ");
//			args.add("%"+resourName+"%");
//		}
//		if(StringHelper.isNotEmpty(resourType)){
//			sql.append(" and fresourcetype = ? ");
//			args.add(resourType);
//		}
//		if(StringHelper.isNotEmpty(startDate)){
//			try {
//				args.add(format.parse(startDate));
//				sql.append(" and ffirsttime > ? ");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		if(StringHelper.isNotEmpty(endDate)){
//			try {
//				args.add(format.parse(endDate));
//				sql.append(" and flasttime < ? ");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		sql.append(" order by flasttime desc");
//		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(), curPage, numPerPage);
//	}
	
	public List<DataRow> getTpcLog(int id){
		String sql = "select alert_log_id as fno,alert_id as fruleid,resource_id as fresourceid,resource_type as fresourcetype,"
						+ " first_alert_time as ffirsttime,last_alert_time as flasttime,alert_count as fcount,resource_name as fresourcename,"
						+ " sev as flevel, 1 as flogtype , 0 as fisforward, toplevel_id as ftopid,toplevel_name as ftopname,'TPC' as fsourcetype ,"
						+ " toplevel_type as ftoptype,state as fstate,description as fdescript,msg as fdetail from v_alert_log where alert_log_id > ?";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,new Object[]{id});
	}
	
	public void insertLog(List<DataRow> list){
		for (DataRow row : list) {
			int id = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select fid from tndevicelog where fno = ?", new Object[]{row.getInt("fno")});
			if(id <= 0){
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tndevicelog", row);
			}
		}
	}
	
	public int getMaxId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select COALESCE(max(fno) ,0) as id from tndevicelog where fsourcetype = 'TPC' ");
	}
	
	public boolean updateAlert(String ruleId){
		String[] str = ruleId.split(",");
		try {
			for (int i = 0; i < str.length; i++) {
				List<Object> args = new ArrayList<Object>();
				String[] arg = str[i].split("_");
				StringBuffer sql1 = new StringBuffer("update tndevicelog set fstate = 1 where 1=1  ");
				StringBuffer sql2 = new StringBuffer("update TPC.t_alert_log set state = 1 where 1=1  ");
				if(StringHelper.isNotEmpty(arg[0])){
					sql1.append(" and fruleid = ? ");
					sql2.append(" and alert_id = ? ");
					args.add(arg[0]);
				}
				if(StringHelper.isNotEmpty(arg[1])){
					sql1.append(" and ftopid = ? ");
					sql2.append(" and TOPLEVEL_ID = ? ");
					args.add(arg[1]);
				}
				getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql1.toString(), args.toArray());
				if(Integer.parseInt(arg[2]) == 1){
					getJdbcTemplate(WebConstants.DB_TPC).update(sql2.toString(), args.toArray());
				}
			}
			return true;
		}  catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteAlert(String ruleId){
		String[] str = ruleId.split(",");
		try {
			for (int i = 0; i < str.length; i++) {
				List<Object> args = new ArrayList<Object>();
				String[] arg = str[i].split("_");
				StringBuffer sql1 = new StringBuffer("delete from tndevicelog where 1=1  ");
				StringBuffer sql2 = new StringBuffer("delete from TPC.t_alert_log  where 1=1  ");
				if(StringHelper.isNotEmpty(arg[0])){
					sql1.append(" and fruleid = ? ");
					sql2.append(" and alert_id = ? ");
					args.add(arg[0]);
				}
				if(StringHelper.isNotEmpty(arg[1])){
					sql1.append(" and ftopid = ? ");
					sql2.append(" and TOPLEVEL_ID = ? ");
					args.add(arg[1]);
				}
				getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql1.toString(), args.toArray());
				if(Integer.parseInt(arg[2]) == 1){
					getJdbcTemplate(WebConstants.DB_TPC).update(sql2.toString(), args.toArray());
				}
			}
			return true;
		}  catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public DataRow getalertbyId(String ruleId,String topId){
		String sql="select count(fcount) as fcount ,a.fresourceid,a.flevel,min(a.fstate) as fstate,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from (select * from tndevicelog order by flasttime desc) a  where a.fruleid=? and a.ftopid=?  group by a.fruleid,a.ftopid order by a.flasttime desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{ruleId,topId});
	}
	
	public void deleteOldLog(int day){
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			session.update("delete from tndevicelog where flasttime < DATE_SUB(CURDATE(),INTERVAL ? day)",new Object[]{day});
			session.commitTrans();
		} catch (Exception e) {
			if(session!=null){
				session.rollbackTrans();
			}
			e.printStackTrace();
		} finally{
			if(session!=null){
				session.close();
				session = null;
			}
		}
//		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql, new Object[]{day});
	}
	
	public int getMaxForwardId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(fid) as maxid from tndevicelog where fisforward = 0 ");
	}
	
	public List<DataRow> getForwardLog(int maxId){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select a.fid,count(fcount) as fcount ,a.ftopid,a.fresourceid,a.flevel,a.fstate,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from tndevicelog a  where fisforward = 0 and fid <= ?  group by a.fruleid,a.ftopid order by a.flasttime desc", new Object[]{maxId});
	}
	
	public void updateForward(int maxId){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("update tndevicelog set fisforward = 1 where fid <= ? ",new Object[]{maxId});
	}
}
