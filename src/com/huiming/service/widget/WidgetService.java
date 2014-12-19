package com.huiming.service.widget;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class WidgetService extends BaseService{
	/**
	 * 查找模块设置信息
	 * @param fid
	 * @return
	 */
	public DataRow getWidgetInfo(String fid){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tsnschart where fid = ?",new Object[]{fid});
	}
	/**
	 * 根据类型查找设备
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageType(String type){
		List<DataRow> rows = new ArrayList<DataRow>();
		String sql="select subsystem_id as ele_id,the_display_name as ele_name,case os_type " +
				"when 25 then 'DS' " +
				"when 21 then 'SVC' " +
				"when 38 then 'SVC' " +
				"when 37 then 'BSP' " +
				"when 15 then 'BSP' " +
				"end " +
				"as type " +
				"from V_RES_STORAGE_SUBSYSTEM ";
		List<DataRow> row = getJdbcTemplate(WebConstants.DB_TPC).query(sql);
		if(type!=null && type.length()>0){
			for (DataRow dataRow : row) {
				if(dataRow.getString("type").equalsIgnoreCase(type)){
					rows.add(dataRow);
				}
			}
		}else{
			return row;
		}
		return rows;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostList(){
		String sql="SELECT h.hypervisor_id AS ele_id, COALESCE(c.display_name,c.name) AS ele_name FROM " +
				"t_res_hypervisor h,t_res_computersystem c " +
				"WHERE h.HOST_COMPUTER_ID = c.COMPUTER_ID";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getsubHostList(){
		String sql="SELECT h.hypervisor_id AS id, COALESCE(c.display_name,c.name) AS value FROM " +
		"t_res_hypervisor h,t_res_computersystem c " +
		"WHERE h.HOST_COMPUTER_ID = c.COMPUTER_ID";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到应用设备
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getAppList(){
		String sql="SELECT fid AS ele_id,fname AS ele_name FROM tnapps"; 
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getsubAppList(){
		String sql="SELECT fid AS id,fname AS value FROM tnapps"; 
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到交换机列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList() {
		String sql="select switch_id as ele_id,the_display_name as ele_name from v_res_switch";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getsubSwitchList() {
		String sql="select switch_id as id,the_display_name as value from v_res_switch";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	
	/**
	 * 获得设备列表
	 * @param storageType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getFnameList(String storageType){
		String sql="SELECT FStorageType,FDevType AS ele_id,FDevTypeName AS ele_name,FPrfView FROM tnprffields ";
		if(storageType!=null && storageType.length()>0){
			sql+="where FStorageType = '"+storageType+"' ";
		}
		sql+="GROUP BY FDevType,FStorageType order by ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到性能指标列表
	 * @param storageType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getFprffildList(String storageType){
		String sql="SELECT fid AS ele_id,ftitle AS ele_name,funits,fprfview,FStorageType,FDevType FROM tnprffields WHERE 1=1 ";
		if(storageType!=null && storageType.length()>0){
			sql+="and FStorageType = '"+storageType+"' ";
		}
		sql+="ORDER BY fdevtype,ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getTopnKPIList(String storageType,String devType){
		String sql="SELECT fid AS ele_id,ftitle AS ele_name,funits,fprfview,FStorageType,FDevType FROM tnprffields WHERE 1=1 ";
		if(storageType!=null && storageType.length()>0){
			sql+="and FStorageType = '"+storageType+"' ";
		}
		if(devType!=null && devType.length()>0){
			sql+="and FDevType = '"+devType+"' ";
		}
		sql+="ORDER BY fdevtype,ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubtype(String storageType){
		String sql="SELECT FDevType AS id,FDevTypeName AS VALUE FROM tnprffields where 1=1 ";
		if(storageType!=null && storageType.length()>0){
			sql+="and FStorageType = '"+storageType+"' ";
		}
		sql+="GROUP BY FDevType";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到性能指标的详细信息
	 * @param fid
	 * @return
	 */
	public DataRow getKPIinfo(String fid){
		String sql="SELECT fid AS ele_id,ftitle AS ele_name,funits,fprfview,FStorageType,FDevType FROM tnprffields WHERE fid = ? ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{fid});
	}
	
	/**
	 * 处理产生曲线图数据
	 * @param row
	 * @return series
	 */
	@SuppressWarnings("static-access")
	public JSONArray getHighchartLineData(DataRow row){
		JSONArray array = new JSONArray();
		JSONArray subdevs = new JSONArray().fromObject(row.getString("fsubdev"));
		for (Object object : subdevs) {
			JSONObject subdev = new JSONObject().fromObject(object);
			JSONObject json = new JSONObject();
			List<DataRow> rows = getData(row,subdev);
			JSONArray ary = new JSONArray();
			for (DataRow dataRow : rows) {
				try {
					JSONObject obj = new JSONObject();
					obj.put("x", Long.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataRow.getString("prf_timestamp")).getTime()));
					obj.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble(row.getString("fprfid").toLowerCase()))));
					obj.put("unit", row.getString("fyaxisname"));
					ary.add(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String lineName = "";
			if(subdev.getString("name").length()>20){
				lineName=subdev.getString("name").substring(0,20)+"...:"+row.getString("ftitle");
			}else{
				lineName=subdev.getString("name")+":"+row.getString("ftitle");
			}
			json.put("name", lineName);
			json.put("data", ary);
			array.add(json);
		}
		return array;
	}
	
	/**
	 * 处理产生饼图数据
	 * @param row
	 * @return
	 */
	public JSONArray getHighchartPieData(DataRow row){
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		DataRow dataRow = getCapacityInfo(row.getString("fdevice"));
		
		JSONArray used = new JSONArray();
		JSONArray unuse = new JSONArray();
		used.add("已用容量(T)");
		used.add(Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("used")/1024)));
		unuse.add("空余容量(T)");
		unuse.add(Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("unuse")/1024)));
		ary.add(used);
		ary.add(unuse);
		
		json.put("type", "pie");
		json.put("name", "容量");
		json.put("data", ary);
		array.add(json);
		return array;
	}
	
	/**
	 * 得到一个部件下的性能信息
	 * @param row 
	 * @param subDev
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getData(DataRow row,JSONObject subDev){
		//表名
		String viewName = row.getString("fprfview")+(row.getString("ftimesize").length()>0?"_"+row.getString("ftimesize"):"");
		//得到时间段
		String daterange = row.getString("fdaterange");
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		if(daterange.equals("day")){
			ca.add(Calendar.DAY_OF_MONTH, -1);
		}else if(daterange.equals("week")){
			ca.add(Calendar.DAY_OF_MONTH, -7);
		}else{
			ca.add(Calendar.MONTH, -1);
//			ca.add(Calendar.YEAR, -1);   //测试
		}
		String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
		//DB
		String sql="select prf_timestamp,dev_id,ele_id,ele_name,";
		StringBuffer sb = new StringBuffer(sql);
		sb.append(row.getString("fprfid"));
		sb.append(" from "+viewName);
		sb.append(" where ele_id = ?");
		sb.append(" and prf_timestamp >= ?");
		sb.append(" and prf_timestamp <= ?");
		sb.append(" order by prf_timestamp");
		String devType = row.getString("fdevicetype");
		if(devType.equalsIgnoreCase("host")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),new Object[]{subDev.getString("id"),startTime,endTime});
		}else if(devType.equalsIgnoreCase("application")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),new Object[]{subDev.getString("id"),startTime,endTime});
		}else{
			return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),new Object[]{subDev.getString("id"),startTime,endTime});
		}
	}
	
	/**
	 * 得到存储系统容量信息
	 * @param subsystemId
	 * @return
	 */
	public DataRow getCapacityInfo(String subsystemId){
		String sql="select the_allocated_capacity as used,the_available_capacity as unuse,the_display_name " +
		"from v_res_storage_subsystem where subsystem_id = ? ";
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql,new Object[]{subsystemId});
	}
	/**
	 * 处理产生TopN图数据
	 * @param row
	 * @return
	 */
	public JSONObject getHighchartTopnData(DataRow row) {
		JSONObject json = new JSONObject();
		JSONArray names = new JSONArray();
		JSONArray array = new JSONArray();
		List<DataRow> rows = getTopnData(row);
		int i = 0;
		for (DataRow dataRow : rows) {
			names.add(dataRow.getString("ele_name"));
			JSONObject obj = new JSONObject();
			if(i==0){  //为Top1添加样式
				obj.put("dataLabels", "{style: {fontWeight:'bold',color: 'red'}}");
			}
			obj.put("time", dataRow.getString("prf_timestamp"));
			obj.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("kpi"))));
			array.add(obj);
			i++;
		}
		json.put("names", names);
		json.put("data", array);
		return json;
	}
	
	
	/**
	 * 查找Topn
	 * @param row
	 * @return
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public List<DataRow> getTopnData(DataRow row){
		//表名
		String viewName = row.getString("fprfview")+(row.getString("ftimesize").length()>0?"_"+row.getString("ftimesize"):"");
		//得到时间段
		String daterange = row.getString("fdaterange");
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		if(daterange.equals("day")){
			ca.add(Calendar.DAY_OF_MONTH, -1);
		}else if(daterange.equals("week")){
			ca.add(Calendar.DAY_OF_MONTH, -7);
		}else{
			ca.add(Calendar.MONTH, -1);
//			ca.add(Calendar.YEAR, -2);  //测试
		}
		String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
		
		String devId = row.getString("fdevice");
		String[] devary = devId.split(",");
		StringBuffer sb = new StringBuffer();
		if(devary.length>1){
			sb.append("select '' as prf_timestamp,dev_id,ele_id,ele_name,");
			sb.append("avg("+row.getString("fprfid")+") as kpi");
			sb.append(" from "+viewName);
			sb.append(" where dev_id in("+devId+")");
			sb.append(" and prf_timestamp >= ?");
			sb.append(" and prf_timestamp <= ?");
			sb.append(" group by ele_id,ele_name,dev_id");
			sb.append(" order by avg("+row.getString("fprfid")+") desc");
		}else{
			sb.append("select prf_timestamp,dev_id,ele_id,ele_name,");
			sb.append(row.getString("fprfid")+" as kpi");
			sb.append(" from "+viewName);
			sb.append(" where ele_id = "+devId);
			sb.append(" and prf_timestamp >= ?");
			sb.append(" and prf_timestamp <= ?");
			sb.append(" order by "+row.getString("fprfid")+" desc");
		}
		String devType = row.getString("fdevicetype");
		if(devType.equalsIgnoreCase("host")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), new Object[]{startTime,endTime}, row.getInt("ftopncount"));
		}else if(devType.equalsIgnoreCase("application")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), new Object[]{startTime,endTime}, row.getInt("ftopncount"));
		}else{
			return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(), new Object[]{startTime,endTime}, row.getInt("ftopncount"));
		}
	}
}
