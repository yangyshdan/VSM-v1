package com.huiming.service.chart;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class ChartService extends BaseService{

	private JdbcTemplate getJdbcTemplate()
	{
		JdbcTemplate jdbcTemplate = getJdbcTemplate(WebConstants.DB_DEFAULT);
		return jdbcTemplate;
	}
	
	public List<DataRow> getChartList(){
		String sql = "select * from TsNsChart where FIsShow = 1 order by FRow,FIndex";
		return getJdbcTemplate().query(sql);
	}
	
	public List<DataRow> getSort(int modelId){
		String sql = "select frow,count(fid) as countChart from TsNsChart where fisshow = 1 and fmodelid = ? group by frow";
		return getJdbcTemplate().query(sql,new Object[]{modelId});
	}
	
	/**
	 * 新增或编辑模板
	 * @param chart
	 */
	public void addChart(DataRow chart){
		int fid = chart.getInt("fid");
		DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from TsNsChart where fid = ?", new Object[]{fid});
		if (row != null) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("TsNsChart", chart, "FId", fid);
		} else {
			getJdbcTemplate().insert("TsNsChart", chart);
		}
	}
	
	public int getmaxId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(fid) from TsNsChart ");
	}
	
	public int getmaxConsoleId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(fid) from tnchartmodel ");
	}
	public void updateChart(DataRow chart){
		getJdbcTemplate().update("TsNsChart", chart, "FId", chart.getInt("FId"));
	}
	
	public void deleteChart(String id){
		getJdbcTemplate().update("delete from TsNsChart where fid in ("+id+")");
	}
	
	public DBPage getChartPage(int modelId,int curPage,int numPerPage){
		String sql = "select a.* from TsNsChart a where a.fmodelid = ? ";
		return getJdbcTemplate().queryPage(sql,new Object[]{modelId}, curPage, numPerPage);
	}
	
	public List<DataRow> getLayout(int modelId){
		String sql = "select a.* from TsNsChart a where a.fisshow = 1 and a.fmodelid=? order by a.fid asc";
		return getJdbcTemplate().query(sql,new Object[]{modelId});
	}
	
	public DataRow getChart(int id){
		String sql = "select * from TsNsChart where FId = ?";
		return getJdbcTemplate().queryMap(sql, new Object[]{id});
	}
	
	public int showChart(String ids,int isShow){
		String sql = "update TsNsChart set FISSHOW = ? where fid in ("+ids+")";
		return getJdbcTemplate().update(sql, new Object[]{isShow});
	}
	
	public int saveLayout(int modelId , JSONArray targets){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
//			session.update("update tsnschart set fisshow='0' where fmodelid = ?", new Object[]{modelId});
			DataRow r = new DataRow();
			r.set("fisshow", 0);
			session.update("tsnschart", r, "fmodelid", modelId);
			session.commitTrans();
			for (int i = 0; i < targets.size(); i++) {
				JSONObject target = targets.getJSONObject(i);
				DataRow chart = new DataRow();
				chart.set("fid", target.get("id"));
				chart.set("frow", target.get("row"));
				chart.set("findex", target.get("index"));
				chart.set("fsize", target.get("size"));
				chart.set("fisshow", 1);
				session.update("tsnschart", chart, "fid", chart.getInt("fid"));
			}
			session.commitTrans();
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}finally{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		return flag;
	}
	
	public int addModel(DataRow model){
		int flag = -1;
		try {
			getJdbcTemplate().insert("tnchartmodel", model);
			flag = 0;
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
		}
		return flag;
	}
	
	public int updateModel(DataRow model){
		int flag = -1;
		try {
			getJdbcTemplate().update("tnchartmodel", model,"fid",model.getString("fid"));
			flag = 0;
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
		}
		return flag;
	}
	
	public int delModel(int id){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			session.delete("tnchartmodel", "fid", id);
			session.delete("tsnschart", "fmodelid", id);
			session.commitTrans();
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}finally{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		return flag;
	}
	
	public DBPage getAllModel(int curPage,int numPerPage){
		return getJdbcTemplate().queryPage("select * from tnchartmodel", curPage, numPerPage);
	}
	
	public DataRow getModel(int id){
		return getJdbcTemplate().queryMap("select * from tnchartmodel where fid = ? ", new Object[]{id});
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getModelList(Long userId) {
		return getJdbcTemplate().query("select * from tnchartmodel where fisshow = 1 and fuserid = ? ", new Object[] {userId});
	}
	
	public int getMaxRow(int modelId){
		return getJdbcTemplate().queryInt("select max(frow) as frow from tsnschart where fmodelid = ? ",new Object[]{modelId});
	}
}
