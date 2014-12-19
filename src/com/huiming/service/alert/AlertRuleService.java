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
	public DBPage getPage(int curPage,int numPerPage,String name,int level,int enabled){
		StringBuffer sql = new StringBuffer("select * from tnalertrule where 1=1 ");
		List<Object> args = new  ArrayList<Object>();
		if(StringHelper.isNotEmpty(name)){
			sql.append(" and fname like ? ");
			args.add("%"+name+"%");
		}
		if(level != -1){
			sql.append(" and flevel = ? ");
			args.add(level);
		}
		if(enabled != -1){
			sql.append(" and fenabled = ? ");
			args.add(level);
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
			String ruleId = session.insert("tnalertrule", rule);
			for (int i = 0; i < targets.size(); i++) {
				JSONObject target = targets.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("ffieldid", target.get("fieldId"));
				tar.set("fminvalue", target.get("minValue"));
				tar.set("fmaxvalue", target.get("maxValue"));
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
				tar.set("fminvalue", target.get("minValue"));
				tar.set("fmaxvalue", target.get("maxValue"));
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
		String sql = "select a.fruleid,a.ffieldid,fminvalue,fmaxvalue,b.fprfview,b.funits,b.ftitle,b.fstoragetype,b.fdevtype from tnruletargets a inner join tnprffields b on a.ffieldid=b.fid where a.fruleid = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{id});
	}
	
	public DataRow getForward(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnforward");
	}
	public List<DataRow> getForwardField(int id){
		String sql = "select * from tnforwardsnmps where fforwardid = ? order by fid";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{id});
	}
	public int addForward(DataRow data,JSONArray targets){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			String forwardId = session.insert("Tnforward", data);
			for (int i = 0; i < targets.size(); i++) {
				JSONObject target = targets.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("fsnmphost", target.get("snmphost"));
				tar.set("fsnmpport", target.get("snmpport"));
				tar.set("fsnmppublic", target.get("snmppublic"));
				tar.set("fforwardid", Integer.parseInt(forwardId));
				session.insert("TnForwardSnmps", tar);
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
	public int updateForward(DataRow data,JSONArray targets){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			int forwardId = data.getInt("fid");
			session.update("Tnforward", data, "fid",forwardId );
			String sql = "delete from TnForwardSnmps where fforwardid in ( "+forwardId+")";
			session.update(sql);
			for (int i = 0; i < targets.size(); i++) {
				JSONObject target = targets.getJSONObject(i);
				DataRow tar = new DataRow();
				tar.set("fsnmphost", target.get("snmphost"));
				tar.set("fsnmpport", target.get("snmpport"));
				tar.set("fsnmppublic", target.get("snmppublic"));
				tar.set("fforwardid", forwardId);
				session.insert("TnForwardSnmps", tar);
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
	public List<DataRow> getHyperVisorIdByVMId(String vmId){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select distinct(hypervisor_id) as id from t_res_virtualmachine where vm_id in ("+vmId+")");
	}
}
