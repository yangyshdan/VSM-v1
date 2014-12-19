package com.huiming.service.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class StorageService extends BaseService{
	
	private static final Map<String, String> viewMap = new HashMap<String, String>();
	
	static{
		viewMap.put("SVC", "PRF_TARGET_SVC_SYSTEM");
		viewMap.put("BSP", "PRF_TARGET_BSP_SYSTEM");
		viewMap.put("DS", "PRF_TARGET_DSSYSTEM");
		viewMap.put("SWITCH", "PRF_TARGET_SWITCH");
		viewMap.put("Physical", "v_prf_physical");
		viewMap.put("Virtual", "v_prf_virtual");
		viewMap.put("App", "v_prf_app");
	}
	public List<DataRow> getCapacity(){
		String sql = "select SUBSYSTEM_ID,THE_DISPLAY_NAME,THE_ALLOCATED_CAPACITY AS allocated,THE_AVAILABLE_CAPACITY as available from V_RES_STORAGE_SUBSYSTEM";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getAsset(){
		String sql = "select 'storage' as SystemType, count(SUBSYSTEM_ID) as counts from V_RES_STORAGE_SUBSYSTEM " +
							" union select 'switch' as SystemType, count(SWITCH_ID) as counts from V_RES_SWITCH " +
							" union select 'host' as SystemType, count(COMPUTER_ID) as counts from V_RES_HOST " + 
							 "union select 'fabric' as SystemType, count(FABRIC_ID) as counts from V_RES_FABRIC";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getIndexPrfFields(String osType){
		String sql = "select FID,concat(FTitle,'[',FDevType,']') as FTitle from TnPrfFields where fdevtype in ('Storage','Switch','Physical','Virtual') and FStorageType = ? order by FIndex ,FTitle asc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{osType});
	}
	
	public List<DataRow> getPrfFields(String osType){
		String sql = "select FID,concat(FTitle,'[',FDevType,']') as FTitle from TnPrfFields where fdevtype in ('Physical','Virtual','App') and fdevtype = ? order by FIndex ,FTitle asc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{osType});
	}
	
	public List<DataRow> getPrfField(String osType){
		StringBuffer sql = new StringBuffer("select FID,concat(FTitle,'[',FDevType,']') as FTitle from TnPrfFields where  FStorageType = ? ");
		if(osType.equalsIgnoreCase("PHYSICAL") || osType.equalsIgnoreCase("VIRTUAL")){
			sql.append(" and FDevType = '" + osType.toLowerCase()+"'");
			osType = "HOST";
		}else if(osType.equalsIgnoreCase("APP")){
			sql.append(" and FDevType = '" + osType.toLowerCase()+"'");
			osType = "APPLICATION";
		}
		sql.append(" order by FIndex ,FTitle asc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),new Object[]{osType});
	}
	
	public List<DataRow> getAllStorage(){
		String sql = "select SUBSYSTEM_ID,THE_DISPLAY_NAME,OS_TYPE from V_RES_STORAGE_SUBSYSTEM";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getAllSwitch(){
		String sql = "select SWITCH_ID,THE_DISPLAY_NAME from V_RES_SWITCH";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	public List<DataRow> getAllHost(){
		String sql = "SELECT COMPUTER_ID,DISPLAY_NAME FROM t_res_computersystem";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public List<DataRow> getPrfData(DataRow chart){
		StringBuffer sql = new StringBuffer("select a.dev_id,a.ele_id,a.prf_timestamp,b.the_display_name as dev_name, "+chart.getString("fprfid")+" from " );
		String where = " where 1=1 and prf_timestamp > ? and prf_timestamp < ? and dev_id in ( "+chart.getString("fdevice")+" ) order by a.prf_timestamp";
		String viewName = viewMap.get(chart.getString("fdevicetype"));
		List args = new ArrayList();
		int diff = 0;
		if(chart.getString("fdaterange").equals("day")){
			diff = 1;
		}else if(chart.getString("fdaterange").equals("week")){
			viewName += "_Hourly";
			diff = 7;
		}else if(chart.getString("fdaterange").equals("month")){
			viewName += "_Daily";
			diff = 30;
		}
		args.add(DateHelper.formatDate(DateHelper.getDataDiff(new Date(), diff), "yyyy-MM-dd HH:mm:ss"));
		args.add(DateHelper.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
		sql.append(viewName);
		if(chart.getString("fdevicetype").equals("SWITCH"))
			sql.append("  a inner join V_RES_SWITCH b on a.dev_id = b.switch_id ");
		else
			sql.append("  a inner join V_RES_STORAGE_SUBSYSTEM b on a.dev_id = b.subsystem_id ");
		sql.append(where);
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString(), args.toArray());
	}
	//主机（物理机和虚拟机）
	public List<DataRow> getPrfData1(DataRow chart){
		StringBuffer sql = new StringBuffer("select dev_id,ele_id,prf_timestamp,ele_name as dev_name, "+chart.getString("fprfid")+" from " );
		String where = " where 1=1 and prf_timestamp > ? and prf_timestamp < ? and ele_id in ( "+chart.getString("fdevice")+" ) order by prf_timestamp";
		String viewName = viewMap.get(chart.getString("fdevicetype"));
		List args = new ArrayList();
		int diff = 0;
		if(chart.getString("fdaterange").equals("day")){
			diff = 1;
		}else if(chart.getString("fdaterange").equals("week")){
		//	viewName += "_Hourly";
			diff = 7;
		}else if(chart.getString("fdaterange").equals("month")){
		//	viewName += "_Daily";
			diff = 30;
		}
		args.add(DateHelper.formatDate(DateHelper.getDataDiff(new Date(), diff), "yyyy-MM-dd HH:mm:ss"));
		args.add(DateHelper.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
		sql.append(viewName);
		sql.append(where);
		if(chart.getInt("fcharttype")==1){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(), args.toArray(),chart.getInt("ftopncount"));
		}else{
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(), args.toArray());
		}
	}
	//topn
	public List<DataRow> getTopNData(DataRow chart,String keyName){
		String[] devices=chart.getString("fdevice").split(",");
		StringBuffer sql;
		String where;
		if(devices.length>1){
			sql = new StringBuffer("select dev_id,ele_id,MIN(prf_timestamp) as prf_timestamp,ele_name as dev_name,AVG("+keyName+") as prf from " );
			where = " where 1=1 and prf_timestamp > ? and prf_timestamp < ? and ele_id in ( "+chart.getString("fdevice")+" ) group by ele_id order by prf desc";
		}else{
			sql = new StringBuffer("select dev_id,ele_id,prf_timestamp,ele_name as dev_name,"+keyName+" as prf from " );
			where = " where 1=1 and prf_timestamp > ? and prf_timestamp < ? and ele_id in ( "+chart.getString("fdevice")+" ) group by ele_id order by prf desc";
		}
		String viewName = viewMap.get(chart.getString("fdevicetype"));
		List args = new ArrayList();
		int diff = 0;
		if(chart.getString("fdaterange").equals("day")){
			diff = 1;
		}else if(chart.getString("fdaterange").equals("week")){
			//viewName += "_Hourly";
			diff = 7;
		}else if(chart.getString("fdaterange").equals("month")){
			//viewName += "_Daily";
			diff = 30;
		}
		if(chart.getString("ftimesize")!=null&&chart.getString("ftimesize").length()>0){
			if(chart.getString("ftimesize").equals("hour")){
				viewName += "_Hourly";
			}else if(chart.getString("ftimesize").equals("day")){
				viewName += "_Daily";
			}
			
		}
		args.add(DateHelper.formatDate(DateHelper.getDataDiff(new Date(), diff), "yyyy-MM-dd HH:mm:ss"));
		args.add(DateHelper.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
		sql.append(viewName);
		sql.append(where);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(), args.toArray(),chart.getInt("ftopncount"));
	}
	public String getFieldName(String id){
		String sql = "select FTITLE from TNPRFFIELDS where FID = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryString(sql,new Object[]{id});
	}
	
	public DataRow getTotalCapacity(){
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap("select sum(THE_ALLOCATED_CAPACITY) AS allocated,sum(THE_AVAILABLE_CAPACITY) as available from V_RES_STORAGE_SUBSYSTEM");
	}
	
	public int getTotalEmc(){
		try {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select count(*) from t_res_storagesubsystem");
		} catch (Exception e) {
			return 0;
		}
		
	}
}
