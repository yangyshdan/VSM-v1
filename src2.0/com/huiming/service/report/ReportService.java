package com.huiming.service.report;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class ReportService extends BaseService{
	public int getmaxId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(id) from tnreport");
	}
	public int getReportTaskConId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(id) from tnreport_task_config");
	}
	public int addReport(DataRow row){
		int id = getmaxId()+1;
		row.set("id", id);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tnreport", row);
		return id;
	}
	
	public DataRow getByTimeType(String timeType,Long userId) {
		String sql = "select id,time_type from tnreport_task_config where time_type = ? and user_id = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[] {timeType,userId});
	}
	
	public void addReportTaskConfig(DataRow row){
		row.set("id", getReportTaskConId()+1);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tnreport_task_config", row);
	}
	
	public void updateReportTaskConfig(DataRow row){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("tnreport_task_config", row, "id", row.getString("id"));
	}
	
	/**
	 * 分页查询报表列表信息
	 * @param name
	 * @param reportType
	 * @param startTime
	 * @param endTime
	 * @param curPage
	 * @param numPerPage
	 * @param userId
	 * @return
	 */
	public DBPage getReportPage(String name,String reportType,String startTime,String endTime,int curPage,int numPerPage,Long userId){
		String sql = "select r.* from tnreport r where 1 = 1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (name != null && name.length() > 0) {
			sb.append("and r.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (reportType != null && reportType.length() > 0) {
			sb.append("and r.report_type = ? ");
			args.add(reportType);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and r.create_time >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and r.create_time <= ? ");
			args.add(endTime);
		}
		if (userId != null && userId > 0) {
			sb.append("and r.user_id = ? ");
			args.add(userId);
		}
		sb.append("order by r.create_time desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	public DataRow getReportInfo(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnreport where id = "+id);
	}
	
	public DataRow getReportInfo(String realName) {
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnreport where real_name = '" + realName + "'");
	}
	
	public void delReport(int id){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tnreport", "id", id);
	}
	
	/**
	 * 获取设备的部件(TPC DB)
	 * @param parentId
	 * @param ParentkeyName
	 * @param keyName
	 * @param displayName
	 * @param talbeName
	 * @return
	 */
	public List<DataRow> getSubgroupDevice(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName){
		String sql = "select t." + keyName + " as id,t." + displayName + " as value from " + talbeName + " t where 1 = 1 ";
		if (ParentkeyName != null && ParentkeyName.length() > 0) {
			sql += "and t." + ParentkeyName + " = " + parentId + " ";
		}
		sql += "group by t." + keyName + ",t." + displayName;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	/**
	 * 获取指定的设备(TPC DB)
	 * @param parentId
	 * @param ParentkeyName
	 * @param keyName
	 * @param displayName
	 * @param talbeName
	 * @param limitIds
	 * @return
	 */
	public List<DataRow> getSubgroupDevice(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName,String limitIds){
		String sql = "select t." + keyName + " as id,t." + displayName + " as value from " + talbeName + " t where 1 = 1 ";
		if (ParentkeyName != null && ParentkeyName.length() > 0) {
			sql += "and t." + ParentkeyName + " = " + parentId + " ";
		}
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + "and t." + ParentkeyName + " in (" + limitIds + ") ";
		}
		sql += "group by t." + keyName + ",t." + displayName;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	/**
	 * 获取设备的部件(SR DB)
	 * @param parentId
	 * @param ParentkeyName
	 * @param keyName
	 * @param displayName
	 * @param talbeName
	 * @return
	 */
	public List<DataRow> getSRSubgroupDevice(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName){
		String sql = "select t." + keyName + " as id,t." + displayName + " as value from " + talbeName + " t where 1 = 1 ";
		if (ParentkeyName != null && ParentkeyName.length() > 0) {
			sql += "and t." + ParentkeyName + " = " + parentId + " ";
		}
		sql += "group by t." + keyName + ",t." + displayName;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 获取指定的设备(SR DB)
	 * @param parentId
	 * @param ParentkeyName
	 * @param keyName
	 * @param displayName
	 * @param talbeName
	 * @param limitIds
	 * @return
	 */
	public List<DataRow> getSRSubgroupDevice(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName,String limitIds){
		String sql = "select t." + keyName + " as id,t." + displayName + " as value from " + talbeName + " t where 1 = 1 ";
		if (ParentkeyName != null && ParentkeyName.length() > 0) {
			sql += "and t." + ParentkeyName + " = " + parentId + " ";
		}
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + "and t." + ParentkeyName + " in (" + limitIds + ") ";
		}
		sql += "group by t." + keyName + ",t." + displayName;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubDevices(Integer pId,String pName,String kName,String sName,String tname,String resource){
		String sql="select t."+kName+" as id,t."+sName+" as value from "+tname+" t where 1=1 ";
		if(pName!=null && pName.length()>0){
			sql+="and t."+pName+" = "+pId+" ";
		}
		sql+="group by t."+kName+",t."+sName;
		return getJdbcTemplate(resource).query(sql,2);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStoragebyOStype(String osType){
		String sql="select subsystem_id as id,the_display_name as value " +
				"from v_res_storage_subsystem " +
				"where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString(osType.toUpperCase())+")";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getSubgroupDevice2(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName,String dbType){
		String sql="select t."+keyName+" as id,t."+displayName+" as value from "+talbeName+" t where 1=1 ";
		if(ParentkeyName!=null && ParentkeyName.length()>0){
			sql+="and t."+ParentkeyName+" = "+parentId+" ";
		}
		sql+="group by t."+keyName+",t."+displayName;
		return getJdbcTemplate(dbType).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubgrouphost(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName,String limitIds){
		String sql = "select t." + keyName + " as id,t." + displayName + " as value from " + talbeName + " t where t." + ParentkeyName + " = " + parentId;
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + " and t." + keyName + " in (" + limitIds + ")";
		}
		sql = sql + " group by t." + keyName + ",t." + displayName;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getemcSys(int sysId){
		String sql="select subsystem_id as id,model as value from t_res_storagesubsystem where subsystem_id ="+sysId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到任务报表配置信息
	 * @return
	 */
	public DataRow getTaskReportConfig(String exeType1,Long userId) {
		String sql = "select * from tnreport_task_config where exe_type1 = ? and report_type = 1 and user_id = ?";
		DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[] { exeType1, userId });
		if (row != null && row.size() > 0) {
			return row;
		} else {
			String sql2 = "select report_type,exe_type1,exe_type2,exe_type3,time_type from tnreport_task_config where exe_type1 = ? and report_type = 1 limit 1";
			DataRow row2 = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql2,new Object[]{exeType1});
			row2.set("user_id", userId);
			row2.set("timescope_type", "0");
			row2.set("time_length", "1");
			row2.set("device_array", "-1");
			row2.set("perf_arraysystem.sqlsystem.sqlsystem.sql", "-1");
			row2.set("topn_array", "-1");
			row2.set("alert_array", "-1");
			String fid = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tnreport_task_config", row2);
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnreport_task_config where id = " + fid);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubDevicePerf(String type){
		String sql="SELECT * FROM tnprffields where FStorageType = ? GROUP BY FStorageType,FdevType ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{type});
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getKPIInfoByTitle(String fstorageType,String subType,String title){
		String sql="SELECT * FROM tnprffields where FStorageType = ? and FdevType = ? and ftitle in ("+title+") ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{fstorageType,subType});
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getInitConfig(String sql,Object[] args,String dbType){
		if (args != null && args.length > 0) {
			return getJdbcTemplate(dbType).query(sql, args);
		} else {
			return getJdbcTemplate(dbType).query(sql);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostList(String limitIds) {
		StringBuffer sql = new StringBuffer("select h.hypervisor_id as ele_id,h.host_computer_id as pid,coalesce(c.display_name,h.name) as ele_name from t_res_hypervisor h,t_res_computersystem c where h.host_computer_id = c.computer_id and h.detectable = 1");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and h.hypervisor_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageType(String type,String limitIds) {
		List<DataRow> rows = new ArrayList<DataRow>();
		String sql = "select subsystem_id as ele_id,the_display_name as ele_name,case os_type " +
				"when 10 then 'BSP' " +
				"when 15 then 'BSP' " +
				"when 21 then 'SVC' " +
				"when 25 then 'DS' " +
				"when 37 then 'BSP' " +
				"when 38 then 'SVC' " +
				"end " +
				"as type " +
				"from v_res_storage_subsystem where 1 = 1 ";
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + " and subsystem_id in (" + limitIds + ")";
		}
		List<DataRow> row = getJdbcTemplate(WebConstants.DB_TPC).query(sql);
		if (type != null && type.length() > 0) {
			for (DataRow dataRow : row) {
				if (dataRow.getString("type").equalsIgnoreCase(type)) {
					rows.add(dataRow);
				}
			}
		} else {
			return row;
		}
		return rows;
	}
	
	/**
	 * 查询相应存储类型的存储设备(SR DB)
	 * @param storageType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageByType(String storageType,String limitIds) {
		StringBuffer sql = new StringBuffer("select subsystem_id as ele_id,name as ele_name from t_res_storagesubsystem where storage_type = ?");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and subsystem_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),new Object[] { storageType });
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList(String limitIds) {
		StringBuffer sql = new StringBuffer("select switch_id as ele_id,the_display_name as ele_name from v_res_switch where 1 = 1");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and switch_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getFnameList(String storageType) {
		String sql = "SELECT FStorageType,FDevType AS ele_id,FDevTypeName AS ele_name,FPrfView FROM tnprffields ";
		if (storageType != null && storageType.length() > 0) {
			sql += "where FStorageType = '" + storageType + "'";
		}
		sql += "GROUP BY FDevType,FStorageType order by ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getFprffildList(String storageType) {
		String sql = "SELECT fid AS ele_id,ftitle AS ele_name,FStorageType,FDevType,FImp,FUnits FROM tnprffields WHERE 1=1 ";
		if (storageType != null && storageType.length() > 0) {
			sql += "and FStorageType = '" + storageType + "' and FImp > 0 ";
		}
		sql += "order by Ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
}
