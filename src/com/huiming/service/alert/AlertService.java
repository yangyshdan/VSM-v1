package com.huiming.service.alert;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class AlertService extends BaseService{

	public void insertAlert(DataRow alert){
		getJdbcTemplate("srDB").insert("tnalert", alert);
	}
	
	public void deleteAlert(int id){
		getJdbcTemplate("srDB").delete("tnalert", "fid", id);
	}
	
	public List<DataRow> getAlertLog(){
		String sql = "select ALERT_ID,FIRST_ALERT_TIME,LAST_ALERT_TIME,RESOURCE_NAME,STATE,THE_SEVERITY,ALERT_COUNT,MSG,RESOURCE_TYPE from V_ALERT_LOG order by LAST_ALERT_TIME desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql, 10);
	}
	
	public List<DataRow> getAllShare(){
		String sql = "select count(*) as share ,sev from V_ALERT_LOG group by SEV";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getLevelShare(){
		String sql = "select count(*) as share ,category,sev from V_ALERT_LOG where category>1 and category < 11 group by CATEGORY,SEV";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public DBPage getPage(int curPage,int numPerPage,Integer computerId,String state , String level,String startDate, String endDate){
		StringBuffer sql = new StringBuffer("select a.* from V_ALERT_LOG a where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(computerId!=null&&computerId.SIZE>0&&computerId>0){
			sql.append(" and COMPUTER_ID = ? ");
			args.add(computerId);
		}
		if(StringHelper.isNotEmpty(level)){
			sql.append(" and SEV = ? ");
			args.add(level);
		}
		if(StringHelper.isNotEmpty(state)){
			sql.append(" and STATE = ? ");
			args.add(state);
		}
		if(StringHelper.isNotEmpty(startDate)){
			try {
				args.add(startDate);
				sql.append(" and LAST_ALERT_TIME > ? ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(StringHelper.isNotEmpty(endDate)){
			try {
				args.add(endDate);
				sql.append(" and LAST_ALERT_TIME < ? ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sql.append(" order by LAST_ALERT_TIME desc");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql.toString() ,args.toArray() , curPage, numPerPage);
	}
	
	public List<DataRow> getNewCount(){
		return getJdbcTemplate(WebConstants.DB_TPC).query("select count(*) as logcount ,toplevel_type,sev from v_alert_log where toplevel_type in (1,114,121,78)  and JULIAN_DAY(last_alert_time)=JULIAN_DAY(CURRENT DATE) group by toplevel_type,sev");
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public List<DataRow> getConfigAlert(JSONArray array){
		String sql = "select a.*,a.first_alert_time || ' ' as the_first_alert_time,a.last_alert_time || ' ' as the_last_alert_time" +
				" from V_ALERT_LOG a where 1=1 ";
		List<Object> args = new ArrayList<Object>();
		for (Object object : array) {
			JSONObject obj = new JSONObject().fromObject(object);
			String type = obj.getString("id");
			if(type.equalsIgnoreCase("info")){
				args.add("'I'");
			}else if(type.equalsIgnoreCase("warning")){
				args.add("'W'");
			}else {
				args.add("'E'");
			}
		}
		sql+="and a.SEV in("+args.toString().replace("[", "").replace("]", "")+")";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public List<DataRow> getPerfAlert(JSONArray array){
		String sql="select a.* from tnalert a where 1=1 ";
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
		sql+="and a.fseverity in ("+args.toString().replace("[", "").replace("]", "")+" )";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	
}
