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
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

@SuppressWarnings("unchecked")
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
	
	/**
	 * 统计设备数量(TPC DB)
	 * @param storageLimitIds
	 * @param switchLimitIds
	 * @return
	 */
	public List<DataRow> getTpcAsset(String storageLimitIds,String switchLimitIds){
		StringBuffer sql = new StringBuffer("select 'storage' as SystemType, count(subsystem_id) as counts from v_res_storage_subsystem where 1 = 1");
		if (StringHelper.isNotEmpty(storageLimitIds) && StringHelper.isNotBlank(storageLimitIds)) {
			sql.append(" and subsystem_id in (" + storageLimitIds + ")");
		}
		sql.append(" union select 'switch' as SystemType, count(switch_id) as counts from v_res_switch where 1 = 1");
		if (StringHelper.isNotEmpty(switchLimitIds) && StringHelper.isNotBlank(switchLimitIds)) {
			sql.append(" and switch_id in (" + switchLimitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString());
	}
	
	/**
	 * 统计设备数量(SR DB)
	 * @param physLimitIds
	 * @param vmLimitIds
	 * @param storageLimitIds
	 * @return
	 */
	public List<DataRow> getSrAsset(String physLimitIds,String vmLimitIds) {
		//For Physical Machine
		StringBuffer sql = new StringBuffer("select 'hv' as SystemType, count(hypervisor_id) as counts from t_res_hypervisor where 1 = 1");
		if (StringHelper.isNotEmpty(physLimitIds) && StringHelper.isNotBlank(physLimitIds)) {
			sql.append(" and hypervisor_id in (" + physLimitIds + ")");
		}
		//For Virtual Machine
		sql.append(" union select 'vm' as SystemType, count(vm_id) as counts from t_res_virtualmachine where 1 = 1");
		if (StringHelper.isNotEmpty(vmLimitIds) && StringHelper.isNotBlank(vmLimitIds)) {
			sql.append(" and vm_id in (" + vmLimitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}
	
	/**
	 * 统计存储数量(SR DB)
	 * @param limitIds
	 * @return
	 */
	public int getSrStorageCount(String limitIds) {
		StringBuffer sql = new StringBuffer("select count(*) from t_res_storagesubsystem where 1 = 1");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and subsystem_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql.toString());
	}
	
	public List<DataRow> getIndexPrfFields(String osType){
		String sql = "select FID,concat(FTitle,'[',FDevType,']') as FTitle from TnPrfFields where fdevtype in ('Storage','Switch','Physical','Virtual') and FStorageType = ? order by FIndex ,FTitle asc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{osType});
	}
	
	public List<DataRow> getPrfFields(String osType){
		String sql = "select FID,concat(FTitle,'[',FDevType,']') as FTitle from TnPrfFields where fdevtype in ('Physical','Virtual','App') and fdevtype = ? order by FIndex ,FTitle asc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{osType});
	}
	
	public List<DataRow> getPrfField(String devType){
		StringBuffer sql = new StringBuffer("select FID,concat(FTitle,'[',FDevType,']') as FTitle from TnPrfFields where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (devType.equals(SrContant.SUBDEVTYPE_PHYSICAL) || devType.equals(SrContant.SUBDEVTYPE_VIRTUAL)) {
			sql.append(" and fstoragetype = ? and fdevtype = ?");
			args.add(SrContant.DEVTYPE_VAL_HOST);
			args.add(devType);
		} else {
			sql.append(" and fstoragetype = ? ");
			args.add(devType);
		}
		sql.append(" order by FIndex ,FTitle asc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
	}
	
	/**
	 * 获取DB2数据库中的存储
	 * @param limitIds
	 * @return
	 */
	public List<DataRow> getAllTpcStorage(String limitIds) {
		StringBuffer sql = new StringBuffer("select subsystem_id,the_display_name,os_type from v_res_storage_subsystem where 1 = 1");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and subsystem_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString());
	}
	
	/**
	 * 获取MySql数据库中的存储
	 * @param limitIds
	 * @return
	 */
	public List<DataRow> getAllSrStorage(String limitIds) {
		StringBuffer sql = new StringBuffer("select subsystem_id,coalesce(display_name,name) as display_name,storage_type from t_res_storagesubsystem where 1 = 1");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and subsystem_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}
	
	/**
	 * 获取交换机
	 * @param limitIds
	 * @return
	 */
	public List<DataRow> getAllSwitch(String limitIds) {
		StringBuffer sql = new StringBuffer("select switch_id,the_display_name from v_res_switch where 1 = 1");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and switch_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString());
	}
	public List<DataRow> getAllHost(){
		String sql = "SELECT COMPUTER_ID,DISPLAY_NAME FROM t_res_computersystem";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public List<DataRow> getPrfData(DataRow chart){
		StringBuffer sql = new StringBuffer("select a.dev_id,a.ele_id,a.prf_timestamp,b.the_display_name as dev_name, "+chart.getString("fprfid")+" from " );
		String where = " where 1=1 and prf_timestamp > ? and prf_timestamp < ? and dev_id in ( "+chart.getString("fdevice")+" ) order by a.prf_timestamp";
		String viewName = viewMap.get(chart.getString("fdevicetype"));
		List<Object> args = new ArrayList<Object>();
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
		List<Object> args = new ArrayList<Object>();
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
		List<Object> args = new ArrayList<Object>();
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

}
