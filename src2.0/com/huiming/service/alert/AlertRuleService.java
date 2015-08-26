package com.huiming.service.alert;

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

public class AlertRuleService extends BaseService{
	public DBPage getPage(int curPage,int numPerPage,String name,int enabled,Long userId) {
		StringBuffer sql = new StringBuffer("select * from tnalertrule where 1 = 1 ");
		List<Object> args = new  ArrayList<Object>();
		if(StringHelper.isNotEmpty(name)){
			sql.append(" and fname like ? ");
			args.add("%"+name+"%");
		}
//		if(level != -1){
//			sql.append(" and flevel = ? ");
//			args.add(level);
//		}
		if(enabled != -1){
			sql.append(" and fenabled = ? ");
			args.add(enabled);
		}
		if (userId != null && userId > 0) {
			sql.append(" and fuserid = ? ");
			args.add(userId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(), curPage, numPerPage);
	}
	
	public DataRow getInfo(int id){
		String sql = "select * from tnalertrule where fid = ? ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{id});
	}
	
	public List<DataRow> getList(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select * from tnalertrule");
	}
	
	public int addRule(DataRow rule,JSONArray targets){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			// column 'flevel' not set in the table 'tnalertrule' instead of table 'TnRuleTargets'
			String ruleId = session.insert("tnalertrule", rule);
			for (int i = 0; i < targets.size(); i++) {
				JSONObject target = targets.getJSONObject(i);
				DataRow tar = new DataRow();
				// column 'fminvalue' replaced by column 'FWarnMinValue' 
				// column 'fmaxvalue' replaced by column 'FErrorMinValue'
				// such as :  0  			 <   num  <  FWarnMinValue      ===>    Info
				// such as :  FWarnMinValue  <=  num  <  FWarnMinValue      ===>    Warning
				// such as :  FWarnMinValue  <=  num  					    ===>    Critical
				tar.set("ffieldid", target.get("fieldId"));
				tar.set("FWarnMinValue", target.get("warnValue"));
				tar.set("FErrorMinValue", target.get("errorValue"));
				tar.set("faveminvalue", target.get("aveminValue"));
				tar.set("favemaxvalue", target.get("avemaxValue"));
				tar.set("fruleid", ruleId);
				session.insert("TnRuleTargets", tar);
			}
			session.commitTrans();
		}  catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}
		finally
		{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		
		return flag;
	}
	
	public int updateRule(DataRow rule,JSONArray targets){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			int ruleId = rule.getInt("fid");
			session.update("tnalertrule", rule, "fid",ruleId );
			String sql = "delete from TnRuleTargets where fruleid in ( "+ruleId+")";
			session.update(sql);
			for (int i = 0; i < targets.size(); i++) {
				JSONObject target = targets.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("ffieldid", target.get("fieldId"));
				tar.set("FWarnMinValue", target.get("warnValue"));
				tar.set("FErrorMinValue", target.get("errorValue"));
				tar.set("faveminvalue", target.get("aveminValue"));
				tar.set("favemaxvalue", target.get("avemaxValue"));
				tar.set("fruleid", ruleId);
				session.insert("TnRuleTargets", tar);
			}
			session.commitTrans();
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}
		finally
		{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		return flag;
		
	}
	
	public int deleteRule(String ids){
		Session session = null;
		int flag = 0;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			session.update("delete from tnalertrule where fid in (" + ids +")");
			session.update("delete from TnRuleTargets where fruleid in ( "+ids+")");
			session.commitTrans();
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}
		finally
		{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		return flag;
	}
	
	public void updateStatus(String ids,int status){
		String sql = "update tnalertrule set fenabled = ? where fid in (" + ids + ")";
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql,new Object[]{status});
	}
	
	public List<DataRow> getField(int id){
		String sql = "select * from TnRuleTargets  where fruleid = ? order by fid";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{id});
	}
	
	public List<DataRow> getTargets(int id){
		String sql = "select a.fruleid,a.ffieldid,FWarnMinValue,FErrorMinValue,b.fprfview,b.funits,b.ftitle,b.fstoragetype,b.fdevtype from tnruletargets a inner join tnprffields b on a.ffieldid=b.fid where a.fruleid = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{id});
	}
	
	public DataRow getForward(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnforward");
	}
	
	public List<DataRow> getForwardField(int id){
		String sql = "select * from tnforwardsnmps where fforwardid = ? order by fid";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{id});
	}
	
	public List<DataRow> getForwardSmsField(int id){
		String sql = "select * from tnforwardsms where fforwardid = ? order by fid";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{id});
	}
	
	/**
	 * 增加告警转发配置
	 * @param data
	 * @param snmps
	 * @param smss
	 * @return
	 */
	public int addForward(DataRow data,JSONArray snmps,JSONArray smss){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			String forwardId = session.insert("Tnforward", data);
			//snmp
			for (int i = 0; i < snmps.size(); i++) {
				JSONObject target = snmps.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("fforwardid", Integer.parseInt(forwardId));
				tar.set("fsnmphost", target.get("snmphost"));
				tar.set("fsnmpport", target.get("snmpport"));
				tar.set("fsnmppublic", target.get("snmppublic"));
				session.insert("TnForwardSnmps", tar);
			}
			//sms
			for (int i = 0; i < smss.size(); i++) {
				JSONObject target = smss.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("fforwardid", forwardId);
				tar.set("fsmsuser", target.get("smsuser"));
				tar.set("fsmsphone", target.get("smsphone"));
				session.insert("TnForwardSms", tar);
			}
			session.commitTrans();
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null) {
				session.rollbackTrans();
			}
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
		return flag;
	}
	
	/**
	 * 修改告警转发配置
	 * @param data
	 * @param snmps
	 * @return
	 */
	public int updateForward(DataRow data,JSONArray snmps,JSONArray smss){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			int forwardId = data.getInt("fid");
			session.update("Tnforward", data, "fid",forwardId );
			session.update("delete from TnForwardSnmps where fforwardid in ( "+forwardId+")");
			session.update("delete from TnForwardSms where fforwardid in ( "+forwardId+")");
			//snmp
			for (int i = 0; i < snmps.size(); i++) {
				JSONObject target = snmps.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("fforwardid", forwardId);
				tar.set("fsnmphost", target.get("snmphost"));
				tar.set("fsnmpport", target.get("snmpport"));
				tar.set("fsnmppublic", target.get("snmppublic"));
				session.insert("TnForwardSnmps", tar);
			}
			//sms
			for (int i = 0; i < smss.size(); i++) {
				JSONObject target = smss.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("fforwardid", forwardId);
				tar.set("fsmsuser", target.get("smsuser"));
				tar.set("fsmsphone", target.get("smsphone"));
				session.insert("TnForwardSms", tar);
			}
			session.commitTrans();
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null) {
				session.rollbackTrans();
			}
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
		return flag;
	}
	
	public List<DataRow> getHyperVisorIdByVMId(String vmId){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select distinct(hypervisor_id) as id from t_res_virtualmachine where vm_id in ("+vmId+")");
	}
}
