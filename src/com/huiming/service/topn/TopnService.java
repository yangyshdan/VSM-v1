package com.huiming.service.topn;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class TopnService extends BaseService{
	public DBPage getTopnPage(int curPage,int numPerPage,String name,String startTime,String endTime){
		String sql="select t.* from tstopn t where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and t.name like ? ");
			args.add("%"+name+"%");
		}
		if(startTime!=null && startTime.length()>0){
			sb.append("and t.create_time >= ? ");
			args.add(startTime);
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and t.create_time <= ? ");
			args.add(endTime);
		}
		sb.append("order by t.create_time ");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	public DataRow getTopnInfo(Integer tid){
		String sql="select * from tstopn where tid = "+tid;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDevTypeName(String storageType){
		String sql="SELECT FStorageType,FDevType,FDevTypeName " +
				"FROM tnprffields " +
				"WHERE FStorageType='"+storageType+"' GROUP BY FDevType order by ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public void addTopn(DataRow row){
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tstopn", row);
	}
	
	public Integer getTid(){
		String seq = "select max(tid) as maxId from tstopn";
		int maxId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(seq);
		return maxId+1;
	}
	
	public void deleteTopn(Integer tid){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tstopn", "tid", tid);
	}
	
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
	public List<DataRow> getEMCStorage(){
		String sql="select subsystem_id as ele_id,model as ele_name from t_res_storagesubsystem where vendor_name = 'EMC' ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList() {
		String sql="select switch_id as ele_id,the_display_name as ele_name from v_res_switch where the_display_name !=''";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getFnameList(String storageType){
		String sql="SELECT FStorageType,FDevType AS ele_id,FDevTypeName AS ele_name,FPrfView FROM tnprffields ";
		if(storageType!=null && storageType.length()>0){
			sql+="where FStorageType = '"+storageType+"' ";
		}
		sql+="GROUP BY FDevType,FStorageType order by ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getFprffildList(String storageType){
		String sql="SELECT fid AS ele_id,ftitle AS ele_name,FStorageType,FDevType FROM tnprffields WHERE 1=1 ";
		if(storageType!=null && storageType.length()>0){
			sql+="and FStorageType = '"+storageType+"' ";
		}
		sql+="order by Ftitle ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public void updatePrfField(DataRow row,Integer tid){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("tstopn", row, "tid", tid);
	}
	
	public DataRow getTnptableInfo(String fid){
		String sql="select * from tnprffields where fid = '"+fid+"'";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}

	public JSONArray getPrffiled(String string, Integer tid) {
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONArray getPrfJSON(DataRow row){
		String kpis = row.getString("fprfid");
		String[] kpi = kpis.split(",");
		JSONArray ja = new JSONArray();
		for (String str : kpi) {
			JSONObject obj = new JSONObject();
			DataRow tnpRow = getTnptableInfo(str);
			String sql="select p.prf_timestamp,p."+str+",p.ele_name from ";
			StringBuffer sb = new StringBuffer(sql);
			String tableName = row.getString("fprfview");
			String timeSize = row.getString("time_size");
			Long startTime = null;
			Long endTime = null;
			String starttime = "";
			String endtime = "";
			if(row.getString("timescope_type").equals("0")){
				starttime = row.getString("starttime");
				endtime = row.getString("endtime");
				if(starttime==null || starttime.length()==0){
					Integer timeLength = row.getInt("time_length");
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					if(row.getString("time_type").equalsIgnoreCase("day")){
						c.add(Calendar.DAY_OF_MONTH, -(timeLength==0?1:timeLength));
					}else if(row.getString("time_type").equalsIgnoreCase("week")){
						c.add(Calendar.DAY_OF_MONTH, -(timeLength==0?7:timeLength*7));
					}else if(row.getString("time_type").equalsIgnoreCase("month")){
						c.add(Calendar.MONTH, -(timeLength==0?1:timeLength));
					}else if(row.getString("time_type").equalsIgnoreCase("year")){
						c.add(Calendar.YEAR, -(timeLength==0?1:timeLength));
					}
					starttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				}
				if(endtime==null || endtime.length()==0){
					endtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				}
				try {
					endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endtime).getTime();
					startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(starttime).getTime();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				String type = row.getString("time_type");
				Integer timeLength = row.getInt("time_length");
				if(type.equals("day")){
					c.add(Calendar.DATE, -timeLength);
				}else if(type.equals("minute")){
					c.add(Calendar.MINUTE, -timeLength);
				}else if(type.equals("hour")){
					c.add(Calendar.HOUR_OF_DAY, -timeLength);
				}else if(type.equals("month")){
					c.add(Calendar.MONTH, -timeLength);
				}else if(type.equals("year")){
					c.add(Calendar.YEAR, -timeLength);
				}
				endTime = new Date().getTime();
				startTime = c.getTime().getTime();
				starttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				endtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			Long tmp = 1000l;
//			if(endTime - startTime > 30*24*60*60*tmp){
//				tableName+="_daily";
//			}else if(endTime - startTime > 7*24*60*60*tmp){  
//				tableName+="_hourly";
//			}
			if(timeSize!=null&&timeSize.length()>0){
				if(timeSize.equals("hour")){
					tableName+="_hourly";
				}else if(timeSize.equals("day")){
					tableName+="_daily";
				}
			}
			sb.append(tableName);
			sb.append(" p where 1=1 ");
			String devs = row.getString("fdevice");
			String[] dev = devs.split(",");
			sb.append("and p.dev_id in (");
			for (int i=0;i<dev.length;i++) {
				sb.append("'"+dev[i]+"'");
				if(i<dev.length-1){
					sb.append(",");
				}
			}
			sb.append(") ");
			sb.append("and p.prf_timestamp >= '"+starttime+"' ");
			sb.append("and p.prf_timestamp <= '"+endtime+"' ");
			sb.append("order by p."+str+" desc");
			List<DataRow> rows = null;
			if(row.getString("fdevicetype").equals("EMC")){
				rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), row.getInt("top_count"));
			}else if(row.getString("fdevicetype").equals("HOST")){
				rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), row.getInt("top_count"));
			}else if(row.getString("fdevicetype").equals("APPLICATION")){
				rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), row.getInt("top_count"));
			}else{
				rows = getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(), row.getInt("top_count"));
			}
			
			List<Object> names = new ArrayList<Object>();
			JSONArray jsonAry = new JSONArray();
			for(int i=0;i<rows.size();i++){
				JSONObject objs = new JSONObject();
				String subName = rows.get(i).getString("sub_name");
				if (subName.length() > 23) {
					subName = subName.substring(0, 23) + "..";
				}
				names.add(rows.get(i).getString("ele_name"));
				if(i==0){
					objs.put("dataLabels", "{style: {fontWeight:'bold',color: 'red'}}");
				}
				objs.put("time", rows.get(i).getString("prf_timestamp").toString());
				objs.put("y", Double.parseDouble(new DecimalFormat("0.00").format(rows.get(i).getDouble(str.toLowerCase()))));
				jsonAry.add(objs);
			}
			obj.put("ftitle", tnpRow.getString("ftitle"));
			obj.put("names", names);
			
			obj.put("data", jsonAry);
			obj.put("id", str);
			obj.put("starttime", starttime);
			obj.put("endtime", endtime);
			obj.put("funits", tnpRow.getString("funits"));
			ja.add(obj);
		}
		return ja;
	}
	
	/**
	 * 如果选择了多台设备，则计算指定时间内每台设备性能平均值
	 * @param row
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getPrfJSON3(DataRow row){
		String kpis = row.getString("fprfid");
		String[] kpi = kpis.split(",");
		JSONArray ja = new JSONArray();
		for (String str : kpi) {
			JSONObject obj = new JSONObject();
			DataRow tnpRow = getTnptableInfo(str);
			String sql="select min(p.prf_timestamp) as prf_timestamp,AVG(p."+str+") as "+str+",p.ele_name from ";
			StringBuffer sb = new StringBuffer(sql);
			String tableName = row.getString("fprfview");
			String timeSize = row.getString("time_size");
			String starttime = "";
			String endtime = "";
			if(row.getString("timescope_type").equals("0")){
				starttime = row.getString("starttime");
				endtime = row.getString("endtime");
				if(starttime==null || starttime.length()==0){
					Integer timeLength = row.getInt("time_length");
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					if(row.getString("time_type").equalsIgnoreCase("day")){
						c.add(Calendar.DAY_OF_MONTH, -(timeLength==0?1:timeLength));
					}else if(row.getString("time_type").equalsIgnoreCase("week")){
						c.add(Calendar.DAY_OF_MONTH, -(timeLength==0?7:timeLength*7));
					}else if(row.getString("time_type").equalsIgnoreCase("month")){
						c.add(Calendar.MONTH, -(timeLength==0?1:timeLength));
					}else if(row.getString("time_type").equalsIgnoreCase("year")){
						c.add(Calendar.YEAR, -(timeLength==0?1:timeLength));
					}
					starttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				}
				if(endtime==null || endtime.length()==0){
					endtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				}
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				String type = row.getString("time_type");
				Integer timeLength = row.getInt("time_length");
				if(type.equals("day")){
					c.add(Calendar.DATE, -timeLength);
				}else if(type.equals("minute")){
					c.add(Calendar.MINUTE, -timeLength);
				}else if(type.equals("hour")){
					c.add(Calendar.HOUR_OF_DAY, -timeLength);
				}else if(type.equals("month")){
					c.add(Calendar.MONTH, -timeLength);
				}else if(type.equals("year")){
					c.add(Calendar.YEAR, -timeLength);
				}
				starttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				endtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			Long tmp = 1000l;
//			if(endTime - startTime > 30*24*60*60*tmp){
//				tableName+="_daily";
//			}else if(endTime - startTime > 7*24*60*60*tmp){  
//				tableName+="_hourly";
//			}
			if(timeSize!=null&&timeSize.length()>0){
				if(timeSize.equals("hour")){
					tableName+="_hourly";
				}else if(timeSize.equals("day")){
					tableName+="_daily";
				}
			}
			sb.append(tableName);
			sb.append(" p where 1=1 ");
			String devs = row.getString("fdevice");
			String[] dev = devs.split(",");
			sb.append("and p.dev_id in (");
			for (int i=0;i<dev.length;i++) {
				sb.append("'"+dev[i]+"'");
				if(i<dev.length-1){
					sb.append(",");
				}
			}
			sb.append(") ");
			sb.append("and p.prf_timestamp >= '"+starttime+"' ");
			sb.append("and p.prf_timestamp <= '"+endtime+"' ");
			sb.append("GROUP BY p.ele_id order by AVG(p."+str+") desc");
			List<DataRow> rows = null;
			if(row.getString("fdevicetype").equals("EMC")){
				rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), row.getInt("top_count"));
			}else if(row.getString("fdevicetype").equals("HOST")){
				rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), row.getInt("top_count"));
			}else if(row.getString("fdevicetype").equals("APPLICATION")){
					rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), row.getInt("top_count"));
			}else{
				int orderIndex = sb.lastIndexOf(" order ");
				String strBeforeOrder = sb.substring(0, orderIndex);
				String strAfterOrder = sb.substring(orderIndex);
				StringBuffer reSb =new StringBuffer(strBeforeOrder+",p.ele_name "+strAfterOrder);
				
				rows = getJdbcTemplate(WebConstants.DB_TPC).query(reSb.toString(), row.getInt("top_count"));
			}
			
			List<Object> names = new ArrayList<Object>();
			JSONArray jsonAry = new JSONArray();
			for(int i=0;i<rows.size();i++){
				JSONObject objs = new JSONObject();
				String subName = rows.get(i).getString("sub_name");
				if (subName.length() > 23) {
					subName = subName.substring(0, 23) + "..";
				}
				names.add(rows.get(i).getString("ele_name"));
				if(i==0){
					objs.put("dataLabels", "{style: {fontWeight:'bold',color: 'red'}}");
				}
				objs.put("time", rows.get(i).getString("prf_timestamp").toString());
				objs.put("y", Double.parseDouble(new DecimalFormat("0.00").format(rows.get(i).getDouble(str.toLowerCase()))));
				jsonAry.add(objs);
			}
			obj.put("ftitle", tnpRow.getString("ftitle"));
			obj.put("names", names);
			
			obj.put("data", jsonAry);
			obj.put("id", str);
			obj.put("starttime", starttime);
			obj.put("endtime", endtime);
			obj.put("funits", tnpRow.getString("funits"));
			ja.add(obj);
		}
		return ja;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getdeviceList(DataRow row) {
		String type = row.getString("fdevicetype");
		if(type.equalsIgnoreCase("switch")){
			StringBuffer sb= new StringBuffer("select the_display_name as ele_name,switch_id as ele_id from ");
			sb.append("v_res_switch where 1=1 ");
			String devs = row.getString("fdevice");
			String[] dev = devs.split(",");
			sb.append("and switch_id in (");
			for (int i=0;i<dev.length;i++) {
				sb.append("'"+dev[i]+"'");
				if(i<dev.length-1){
					sb.append(",");
				}
			}
			sb.append(") ");
			return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString());
		}else if(type.equalsIgnoreCase("emc")){
			StringBuffer sb= new StringBuffer("select model as ele_name,subsystem_id as ele_id from ");
			sb.append("t_res_storagesubsystem where 1=1 ");
			String devs = row.getString("fdevice");
			String[] dev = devs.split(",");
			sb.append("and subsystem_id in (");
			for (int i=0;i<dev.length;i++) {
				sb.append("'"+dev[i]+"'");
				if(i<dev.length-1){
					sb.append(",");
				}
			}
			sb.append(") ");
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
		}else if(type.equalsIgnoreCase("host")){
			StringBuffer sb= new StringBuffer("select name as ele_name,hypervisor_id ele_id FROM t_res_hypervisor ");
			sb.append(" where 1=1 ");
			String devs = row.getString("fdevice");
			String[] dev = devs.split(",");
			sb.append("and hypervisor_id in (");
			for (int i=0;i<dev.length;i++) {
				sb.append("'"+dev[i]+"'");
				if(i<dev.length-1){
					sb.append(",");
				}
			}
			sb.append(") ");
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
		}
		else if(type.equalsIgnoreCase("application")){
			StringBuffer sb= new StringBuffer("SELECT fid AS ele_id,fname AS ele_name FROM tnapps ");
			sb.append(" where 1=1 ");
			String devs = row.getString("fdevice");
			String[] dev = devs.split(",");
			sb.append("and fid in (");
			for (int i=0;i<dev.length;i++) {
				sb.append("'"+dev[i]+"'");
				if(i<dev.length-1){
					sb.append(",");
				}
			}
			sb.append(") ");
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
		}
		else{
			StringBuffer sb= new StringBuffer("select the_display_name as ele_name,subsystem_id ele_id from ");
			sb.append("v_res_storage_subsystem where 1=1 ");
			String devs = row.getString("fdevice");
			String[] dev = devs.split(",");
			sb.append("and subsystem_id in (");
			for (int i=0;i<dev.length;i++) {
				sb.append("'"+dev[i]+"'");
				if(i<dev.length-1){
					sb.append(",");
				}
			}
			sb.append(") ");
			return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostList(){
		String sql="SELECT h.HYPERVISOR_ID AS ele_id,h.HOST_COMPUTER_ID AS pid,COALESCE(c.display_name,h.name) AS ele_name FROM t_res_hypervisor h,t_res_computersystem c WHERE h.HOST_COMPUTER_ID = c.COMPUTER_ID AND h.detectable = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVirtualList(){
		String sql="SELECT v.vm_id AS ele_id,COALESCE(t.display_name,v.name) AS ele_name,v.computer_id AS pid FROM t_res_virtualmachine v,t_res_computersystem t WHERE t.computer_id = v.computer_id AND t.DETECTABLE = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public List<DataRow> getAppList(){
		String sql="SELECT fid AS ele_id,fname AS ele_name FROM tnapps"; 
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	

}
