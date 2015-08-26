package com.huiming.service.alert;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class DeviceAlertService extends BaseService{
	
	public DataRow getAlertById(int id){
		String sql = "select * from v_devicelog where fid = ? ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{id});
	}
	
	public void delAlert(String ids){
		String sql = "delete from tndevicelog where fid in (" + ids +" )";
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
	}
	
	public List<DataRow> getLevelShare(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select count(*) as shareY , flevel from v_devicelog group by flevel");
	}
	
	public DataRow getValue(String sql){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	public List<DataRow> getAllShare(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select count(*) as shareY ,fresourcetype, flevel from v_devicelog group by fresourcetype, flevel");
	}
	
	public List<DataRow> checkRule(String db,String sql){
		return getJdbcTemplate(db).query(sql);
	}
	
	public void insertLog(DataRow log){
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			int i = session.queryInt("select fid from tndevicelog where fruleid=? and fno=? ",new Object[]{log.getString("fruleid"),log.getString("fno")});
			if(i <= 0){
				int id = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select fid from tsnoforward where fruleid = ? and ftopid = ? ",new Object[]{StringHelper.isEmpty(log.getString("fruleid"))?null:log.getString("fruleid"),StringHelper.isEmpty(log.getString("ftopid"))?null:log.getString("ftopid")});
				if(id > 0 || log.getString("flevel").equals("0")){
					log.set("fstate", 1);
					log.set("fisforward", 1);
				}
				session.insert("tndevicelog", log);
			}
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
	}
	//插入NAS日志
	public void insertNASLog(DataRow log){
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			int i = session.queryInt("select fid from tndevicelog where fruleid=? and ftopid=? and flasttime >= ? ",new Object[]{log.getString("fruleid"),log.getString("ftopid"),log.getString("flasttime")});
			if(i <= 0){
				int id = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select fid from tsnoforward where fruleid = ? and ftopid = ? ",new Object[]{StringHelper.isEmpty(log.getString("fruleid"))?null:log.getString("fruleid"),StringHelper.isEmpty(log.getString("ftopid"))?null:log.getString("ftopid")});
				if(id > 0 || log.getString("flevel").equals("0")){
					log.set("fstate", 1);
					log.set("fisforward", 1);
				}
				session.insert("tndevicelog", log);
			}
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
	}
		
	public List<DataRow> getNewCount(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select count(*) as logcount, fresourcetype,flevel from v_devicelog where  to_days(ftime)=to_days(now()) group by fresourcetype,flevel");
	}
	
	/**
	 * 统计未确认的设备告警信息
	 * 包括：物理机,虚拟机,存储系统,交换机
	 * @param physLimitIds
	 * @param vmLimitIds
	 * @param storageLimitIds
	 * @param switchLimitIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceAlertSummary(String physLimitIds,String vmLimitIds,String storageLimitIds,String switchLimitIds) {
		StringBuffer sb = new StringBuffer("select count(fcount) as logcount,ftoptype,flevel,ftopid,ftopname,fresourceid from v_devicelog where fstate = 0");
		//For Physical
		sb.append(" and ((ftoptype = '" + SrContant.SUBDEVTYPE_PHYSICAL + "'");
		if (StringHelper.isNotEmpty(physLimitIds) && StringHelper.isNotBlank(physLimitIds)) {
			sb.append(" and ftopid in (" + physLimitIds + ")");
		}
		sb.append(")");
		//For Virtual
		sb.append(" or");
		sb.append(" (ftoptype = '" + SrContant.SUBDEVTYPE_VIRTUAL + "'");
		if (StringHelper.isNotEmpty(vmLimitIds) && StringHelper.isNotBlank(vmLimitIds)) {
			sb.append(" and fresourceid in (" + vmLimitIds + ")");
		}
		sb.append(")");
		//For Storage
		sb.append(" or");
		sb.append(" (ftoptype = '" + SrContant.SUBDEVTYPE_STORAGE + "'");
		if (StringHelper.isNotEmpty(storageLimitIds) && StringHelper.isNotBlank(storageLimitIds)) {
			sb.append(" and ftopid in (" + storageLimitIds + ")");
		}
		sb.append(")");
		//For Switch
		sb.append(" or");
		sb.append(" (ftoptype = '" + SrContant.SUBDEVTYPE_SWITCH + "'");
		if (StringHelper.isNotEmpty(switchLimitIds) && StringHelper.isNotBlank(switchLimitIds)) {
			sb.append(" and ftopid in (" + switchLimitIds + ")");
		}
		sb.append(")");
		sb.append(") group by ftoptype,flevel,ftopid,fresourceid order by ftoptype,flevel desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 分页获取设备告警信息列表
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
	public DBPage getLogPage(int curPage,int numPerPage,int logtype,String topId,String topname,
			String resourceId,String resourcename,String resourceType,int state,int level,
			String startDate,String endDate) {
		StringBuffer sql = new StringBuffer("select a.fid,a.fremark,a.fconfirmtime,count(a.fcount) as fcount,a.ftopid,a.fresourceid,a.flevel,min(a.fstate) as fstate,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid,a.noid,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from (select * from v_devicelog order by flasttime desc) a where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(topId) && StringHelper.isNotBlank(topId)) {
			sql.append(" and a.ftopid in (" + topId + ") ");
		}
		if (StringHelper.isNotEmpty(topname)) {
			sql.append(" and a.ftopname like ? ");
			args.add("%" + topname + "%");
		}
		if (StringHelper.isNotEmpty(resourceId)) {
			sql.append(" and a.fresourceid = ? ");
			args.add(resourceId);
		}
		if (StringHelper.isNotEmpty(resourcename) && !resourcename.equalsIgnoreCase("undefined")) {
			sql.append(" and a.fresourcename like ? ");
			args.add("%" + resourcename + "%");
		}
		if (level > -1) {
			sql.append(" and a.flevel = ? ");
			args.add(level);
		}
		if (logtype > -1) {
			sql.append(" and a.flogtype = ? ");
			args.add(logtype);
		}
		if (state > -1) {
			sql.append(" and a.fstate = ? ");
			args.add(state);
		}
		if (StringHelper.isNotEmpty(resourceType) && !resourceType.equalsIgnoreCase("undefined")) {
			sql.append(" and a.ftoptype = ? ");
			args.add(resourceType);
		}
		if (StringHelper.isNotEmpty(startDate) && startDate.equals("undefined") == false) {
			try {
				sql.append(" and a.ffirsttime > ? ");
				args.add(startDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringHelper.isNotEmpty(endDate) && endDate.equals("undefined") == false) {
			try {
				sql.append(" and a.flasttime < ? ");
				args.add(endDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sql.append(" group by a.fruleid,a.ftopid,a.fresourceid order by a.flasttime desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 分页获取设备告警信息列表
	 * @param curPage
	 * @param numPerPage
	 * @param logtype
	 * @param topId
	 * @param topname
	 * @param resourceId
	 * @param resourcename
	 * @param resourceType
	 * @param state
	 * @param level
	 * @param startDate
	 * @param endDate
	 * @param physLimitIds
	 * @param vmLimitIds
	 * @param storageLimitIds
	 * @param switchLimitIds
	 * @return
	 */
	public DBPage getLogPage(int curPage,int numPerPage,int logtype,String topId,String topname,String resourceId,
			String resourcename,String resourceType,int state,int level,String startDate,String endDate,
			String physLimitIds,String vmLimitIds,String storageLimitIds,String switchLimitIds) {
		StringBuffer sql = new StringBuffer("select a.fid,a.fremark,a.fconfirmtime,count(a.fcount) as fcount,a.ftopid,a.fresourceid,a.flevel,min(a.fstate) as fstate,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid,a.noid,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from (select * from v_devicelog order by flasttime desc) a where 1 = 1");
		List<Object> args = new ArrayList<Object>();
		//For Physical
		sql.append(" and ((ftoptype = '" + SrContant.SUBDEVTYPE_PHYSICAL + "'");
		if (StringHelper.isNotEmpty(physLimitIds) && StringHelper.isNotBlank(physLimitIds)) {
			sql.append(" and ftopid in (" + physLimitIds + ")");
		}
		sql.append(")");
		//For Virtual
		sql.append(" or");
		sql.append(" (ftoptype = '" + SrContant.SUBDEVTYPE_VIRTUAL + "'");
		if (StringHelper.isNotEmpty(vmLimitIds) && StringHelper.isNotBlank(vmLimitIds)) {
			sql.append(" and fresourceid in (" + vmLimitIds + ")");
		}
		sql.append(")");
		//For Storage
		sql.append(" or");
		sql.append(" (ftoptype = '" + SrContant.SUBDEVTYPE_STORAGE + "'");
		if (StringHelper.isNotEmpty(storageLimitIds) && StringHelper.isNotBlank(storageLimitIds)) {
			sql.append(" and ftopid in (" + storageLimitIds + ")");
		}
		sql.append(")");
		//For Switch
		sql.append(" or");
		sql.append(" (ftoptype = '" + SrContant.SUBDEVTYPE_SWITCH + "'");
		if (StringHelper.isNotEmpty(switchLimitIds) && StringHelper.isNotBlank(switchLimitIds)) {
			sql.append(" and ftopid in (" + switchLimitIds + ")");
		}
		sql.append("))");
		//其他筛选条件
		if (StringHelper.isNotEmpty(topId) && StringHelper.isNotBlank(topId)) {
			sql.append(" and a.ftopid in (" + topId + ") ");
		}
		if (StringHelper.isNotEmpty(topname)) {
			sql.append(" and a.ftopname like ? ");
			args.add("%" + topname + "%");
		}
		if (StringHelper.isNotEmpty(resourceId)) {
			sql.append(" and a.fresourceid = ? ");
			args.add(resourceId);
		}
		if (StringHelper.isNotEmpty(resourcename) && !resourcename.equalsIgnoreCase("undefined")) {
			sql.append(" and a.fresourcename like ? ");
			args.add("%" + resourcename + "%");
		}
		if (level > -1) {
			sql.append(" and a.flevel = ? ");
			args.add(level);
		}
		if (logtype > -1) {
			sql.append(" and a.flogtype = ? ");
			args.add(logtype);
		}
		if (state > -1) {
			sql.append(" and a.fstate = ? ");
			args.add(state);
		}
		if (StringHelper.isNotEmpty(resourceType) && !resourceType.equalsIgnoreCase("undefined")) {
			sql.append(" and a.ftoptype = ? ");
			args.add(resourceType);
		}
		if (StringHelper.isNotEmpty(startDate) && startDate.equals("undefined") == false) {
			try {
				sql.append(" and a.ffirsttime > ? ");
				args.add(startDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringHelper.isNotEmpty(endDate) && endDate.equals("undefined") == false) {
			try {
				sql.append(" and a.flasttime < ? ");
				args.add(endDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sql.append(" group by a.fruleid,a.ftopid,a.fresourceid order by a.flasttime desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取相应设备告警信息
	 * @param topId
	 * @param topName
	 * @param resourceId
	 * @param resourceName
	 * @param state
	 * @param isDelete
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<DataRow> getCompDeviceLogList(String topId,String topName,String topType,String resourceId,String resourceName,String resourceType,int state,int isDelete,String startDate,String endDate) {
		StringBuffer sb = new StringBuffer("select v.flevel,sum(v.fcount) as fcount,min(v.ffirsttime) as ffirsttime,max(v.flasttime) as flasttime,(unix_timestamp(max(v.flasttime))-unix_timestamp(min(v.ffirsttime)))/3600 as timelen from v_devicelog v where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(topId) && StringHelper.isNotBlank(topId)) {
			sb.append(" and v.ftopid = ? ");
			args.add(topId);
		}
		if (StringHelper.isNotEmpty(topName) && StringHelper.isNotBlank(topName)) {
			sb.append(" and v.ftopname like ? ");
			args.add("%" + topName + "%");
		}
		if (StringHelper.isNotEmpty(topType) && StringHelper.isNotBlank(topType)) {
			sb.append(" and v.ftoptype = ? ");
			args.add(topType);
		}
		if (StringHelper.isNotEmpty(resourceId) && StringHelper.isNotBlank(resourceId)) {
			sb.append(" and v.fresourceid = ? ");
			args.add(resourceId);
		}
		if (StringHelper.isNotEmpty(resourceName) && StringHelper.isNotBlank(resourceName)) {
			sb.append(" and v.fresourcename like ? ");
			args.add("%" + resourceName + "%");
		}
		if (StringHelper.isNotEmpty(resourceType) && StringHelper.isNotBlank(resourceType)) {
			sb.append(" and v.fresourcetype = ? ");
			args.add(resourceType);
		}
		if (state > -1) {
			sb.append(" and v.fstate = ? ");
			args.add(state);
		}
		if (isDelete > -1) {
			sb.append(" and v.fisdelete = ? ");
			args.add(isDelete);
		}
		if (StringHelper.isNotEmpty(startDate) && StringHelper.isNotBlank(startDate)) {
			sb.append(" and v.ffirsttime >= ? ");
			args.add(startDate);
		}
		if (StringHelper.isNotEmpty(endDate) && StringHelper.isNotBlank(endDate)) {
			sb.append(" and v.flasttime <= ? ");
			args.add(endDate);
		}
		sb.append("group by v.flevel order by v.flevel desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), args.toArray());
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public List<DataRow> getLogList(JSONArray array){
		StringBuffer sql = new StringBuffer("select count(fcount) as fcount ,a.fremark,a.fconfirmtime,a.fresourceid,a.flevel,a.fstate,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.noid,a.fdescript,min(DATE_FORMAT(a.ffirsttime,'%Y-%c-%d %H:%m:%s')) as ffirsttime,max(DATE_FORMAT(a.flasttime,'%Y-%c-%d %H:%m:%s')) as flasttime,a.fdetail from v_devicelog a  where 1=1 ");
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
		sql.append(" and a.flevel in ("+args.toString().replace("[", "").replace("]", "")+" ) group by a.fruleid,a.ftopid order by a.flasttime desc ");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}
	
	/**
	 * 获取设备告警信息
	 * @param baseArray
	 * @param paramArray
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceLogList(String deviceParams, String startTime, String endTime, String levelParams) {
		StringBuffer sb = new StringBuffer("SELECT FTopName,fremark,DATE_FORMAT(fconfirmtime,'%Y-%m-%d %H:%i:%s') AS fconfirmtime,FResourceName,FLogType,COUNT(FCount) AS fcount,DATE_FORMAT(FFirstTime,'%Y-%m-%d %H:%i:%s') AS FFirstTime,DATE_FORMAT(FLastTime,'%Y-%m-%d %H:%i:%s') AS FLastTime,FLevel,FDescript,FDetail,FState FROM v_devicelog");
		//设置查询告警信息的设备、时间参数
		sb.append(" WHERE FTopId IN (" + deviceParams + ")");
		sb.append(" AND FFirstTime >= '" + startTime + "'");
		sb.append(" AND FLastTime <= '" + endTime + "'");
		//设置告警级别参数
		sb.append(" AND FLevel IN (" + levelParams + ")");
		sb.append(" GROUP BY ftopid,fresourceid,flevel");
		sb.append(" ORDER BY FTopName,FResourceName,FFirstTime,Flevel");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
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
				int fid = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select fid from tsnoforward where fruleid = ? and ftopid = ? ",new Object[]{StringHelper.isEmpty(row.getString("fruleid"))?null:row.getString("fruleid"),StringHelper.isEmpty(row.getString("ftopid"))?null:row.getString("ftopid")});
				if(fid > 0  || row.getString("flevel").equals("0")){
					row.set("fstate", 1);
					row.set("fisforward", 1);
				}
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tndevicelog", row);
			}
		}
	}
	
	public int getMaxId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select COALESCE(max(fno) ,0) as id from tndevicelog where fsourcetype = 'TPC' ");
	}
	
	public boolean updateAlert(String ruleId,String confirmUser,String remark){
		String[] str = ruleId.split(",");
		try {
			for (int i = 0; i < str.length; i++) {
				List<Object> args1 = new ArrayList<Object>();
				List<Object> args2 = new ArrayList<Object>();
				String[] arg = str[i].split("_");
				StringBuffer sql1 = new StringBuffer("update tndevicelog set fstate = 1,fconfirmuser = ?,fremark = ?,fconfirmtime = now() where fstate <> 1 ");
				StringBuffer sql2 = new StringBuffer("update t_alert_log set state = 1 where state <> 1 ");
				args1.add(confirmUser);
				args1.add(remark);
				if(StringHelper.isNotEmpty(arg[0])){
					sql1.append(" and fruleid = ? ");
					sql2.append(" and alert_id = ? ");
					args1.add(arg[0]);
					args2.add(arg[0]);
				}
				if(StringHelper.isNotEmpty(arg[1])){
					sql1.append(" and ftopid = ? ");
					sql2.append(" and toplevel_id = ? ");
					args1.add(arg[1]);
					args2.add(arg[1]);
				}
				getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql1.toString(), args1.toArray());
				if(Integer.parseInt(arg[2]) == 1){
					getJdbcTemplate(WebConstants.DB_TPC).update(sql2.toString(), args2.toArray());
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
				StringBuffer sql1 = new StringBuffer("update tndevicelog set fisdelete=1 where 1=1  ");
				StringBuffer sql2 = new StringBuffer("delete from t_alert_log  where 1=1  ");
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
	
	public boolean noForward(String ruleId){
		String[] str = ruleId.split(",");
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			for (int i = 0; i < str.length; i++) {
				List<Object> args = new ArrayList<Object>();
				String[] arg = str[i].split("_");
				int id = session.queryInt("select fid from tsnoforward where fruleid = ? and ftopid = ? ",new Object[]{StringHelper.isEmpty(arg[0])?null:arg[0],StringHelper.isEmpty(arg[1])?null:arg[1]});
				if(id > 0 ){
					session.update("delete from tsnoforward where fid = ? ", new Object[]{id});
				}else{
					DataRow row = new DataRow();
					row.set("fruleid", StringHelper.isEmpty(arg[0])?null:arg[0]);
					row.set("ftopid", StringHelper.isEmpty(arg[1])?null:arg[1]);
					session.insert("tsnoforward", row);
					session.update("update tndevicelog set fstate = 1 , fisforward=1 where 1=1  and fruleid = ? and ftopid = ? ", new Object[]{StringHelper.isEmpty(arg[0])?null:arg[0],StringHelper.isEmpty(arg[1])?null:arg[1]});
				}
				
			}
			session.commitTrans();
			return true;
		}  catch (Exception e) {
			if(session!=null){
				session.rollbackTrans();
			}
			e.printStackTrace();
			return false;
		} finally{
			if(session!=null){
				session.close();
				session = null;
			}
		}
	}
	
	public DataRow getalertbyId(String ruleId,String topId){
		String sql="select count(fcount) as fcount,a.fconfirmuser,a.fremark,a.fconfirmtime,a.fresourceid,a.noid,a.flevel,min(a.fstate) as fstate,a.flogtype,a.ftoptype,a.ftopid,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from (select * from v_devicelog order by flasttime desc) a  where a.fruleid=? and a.ftopid=?  group by a.fruleid,a.ftopid order by a.flasttime desc";
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
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select a.fid,count(fcount) as fcount ,a.ftopid,a.fresourceid,a.flevel,a.fstate,a.flogtype,a.ftoptype,a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from tndevicelog a  where fisforward = 0 and fstate = 0 and fisdelete = 0 and fid <= ?  group by a.fruleid,a.ftopid order by a.flasttime desc", new Object[]{maxId});
	}
	
	//一天发一次
	public List<DataRow> getForwardLog1(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select a.fid,count(fcount) as fcount ,max(fisforward) as fisforward,a.ftopid,a.fresourceid,a.flevel,a.fstate,a.flogtype,a.ftoptype, a.ftopname,a.fresourcename,a.fresourcetype,a.fruleid ,a.fdescript,min(a.ffirsttime) as ffirsttime,max(a.flasttime) as flasttime,a.fdetail from tndevicelog a  where to_days(flasttime)=to_days(now())  and fisdelete = 0 group by a.fruleid,a.ftopid order by a.flasttime desc ");
	}
	
	public void updateForward(int maxId){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("update tndevicelog set fisforward = 1 where fid <= ? ",new Object[]{maxId});
	}
	
	public void updateForward(String ruleId,String topId){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("update tndevicelog set fisforward = 1 where fruleid = ? and ftopid = ? ",new Object[]{ruleId,topId});
	}
	
	public DataRow findResourceType(String ip){
		DataRow result = new DataRow();
		JdbcTemplate template = getJdbcTemplate(WebConstants.DB_TPC);
		if(ip.equals("196.1.6.78")){
			result.set("name", "196.1.6.78");
			result.set("type", "tape");
			result.set("id", "-1");
		}else if(ip!=null && ip.length() > 0){
			String sqlSwitch = "select the_display_name as name,switch_id as id from v_res_switch where ip_address like ?";
			DataRow row = template.queryMap(sqlSwitch,new Object[]{ip});
			String type = "";
			if(row == null || row.getInt("id") <= 0 ){
				String sqlStorage = "select the_display_name as name ,subsystem_id as id,os_type from t_res_storage_subsystem where ip_address like ? ";
				DataRow storage = template.queryMap(sqlStorage);
				if(storage != null && storage.getInt("id") > 0 ){
					type = storage.getString("os_type");
				}
				if(type == null || type.equals("")){
					type = "15";
				}
				result.set("name", storage.getString("name"));
				result.set("id", storage.getString("id"));
				if(type.equals("15") || type.equals("37")){
					result.set("type", "ds5k");
				}else if(type.equals("21") || type.equals("38")){
					result.set("type", "svc");
				}else if(type.equals("25")){
					result.set("type", "ds8k");
				}else if(type.equals("10")){
					result.set("type", "nas");
				}
			}else{
				result.set("type", "switch");
				result.set("name", row.getString("name"));
				result.set("id", row.getString("id"));
			}
		}
		return result;
	}
	
	public void insertLogNoCheck(DataRow log){
		if(StringHelper.isNotEmpty(log.getString("fruleid")) && StringHelper.isNotEmpty(log.getString("fdetail"))){
			int id = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select fid from tsnoforward where fruleid = ? and ftopid = ? ",new Object[]{StringHelper.isEmpty(log.getString("fruleid"))?null:log.getString("fruleid"),StringHelper.isEmpty(log.getString("ftopid"))?null:log.getString("ftopid")});
			if(id > 0  || log.getString("flevel").equals("0")){
				log.set("fstate", 1);
				log.set("fisforward", 1);
			}
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tndevicelog", log);
		}
		
	}

}
