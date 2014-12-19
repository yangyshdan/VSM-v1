package com.huiming.service.baseprf;

import java.text.DecimalFormat;
import java.text.ParseException;
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

public class BaseprfService extends BaseService{
	@SuppressWarnings("unchecked")
	private List<DataRow> getPrfDataBase(DataRow row,DataRow kpis,String startTime,String endTime,String TimeType){
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select prf_timestamp,dev_id,ele_id,ele_name,");
		String tableName = kpis.getString("fprfview");
		if(kpis!=null && kpis.size()>0){
			sql.append(kpis.getString("fid"));
			if(startTime!=null && startTime.length()>0){
				Date start = null;
				Date end = null;
				Long overTimes=null;
				try {
					start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					if(endTime!=null && endTime.length()>0){
						end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
						overTimes = end.getTime();
					}else{
						overTimes = new Date().getTime();  
					}
					if(TimeType!=null && TimeType.equals("report")){
						Long lastTime = start.getTime();
						Long tem = 1000l;
						if((overTimes - lastTime) >= 30*24*60*60*tem){
							tableName+="_daily";
						}else if(overTimes - lastTime >= 7*24*60*60*tem){  
							tableName+="_hourly";
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(TimeType!=null&&TimeType.length()>0){
				if(TimeType.equals("hour")){
					tableName+="_hourly";
				}else if(TimeType.equals("day")){
					tableName+="_daily";
				}
			}
			sql.append(" from "+tableName+" ");
			sql.append("where ele_id = ? ");
			args.add(row.getString("ele_id"));
			if(startTime!=null && startTime.length()>0){
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if(endTime!=null && endTime.length()>0){
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if(startTime==null || startTime.length()==0){
				if (endTime == null || endTime.length() == 0) {
					sql.append("and prf_timestamp >= ? ");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DATE, -1);
					args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
				}
			}
			sql.append("order by prf_timestamp");
		}
		if(kpis.getString("fstoragetype").equals("EMC")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
		}else if(kpis.getString("fstoragetype").equals("APPLICATION")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
		}else if(kpis.getString("fstoragetype").equals("HOST")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
		}else{
			return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString(),args.toArray());
		}
		
	}
	public JSONArray getPrfDatas(Integer showDeviceName,List<DataRow> rows,List<DataRow> kpis,String startTime,String endTime,String TimeType){
		JSONArray array = new JSONArray();
		for (DataRow dataRow : kpis) {
			for (DataRow row : rows) {
				JSONObject obj = new JSONObject();
				JSONArray ary = new JSONArray();
				List<DataRow> data = getPrfDataBase(row, dataRow, startTime, endTime,TimeType);
				for (DataRow dataRow2 : data) {
					JSONObject objs = new JSONObject();
					Long time = 0l;
					try {
						time = Long.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataRow2.getString("prf_timestamp")).getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Double kpi = Double.parseDouble(new DecimalFormat("0.00").format(dataRow2.getDouble(dataRow.getString("fid").toLowerCase())));
					objs.put("unit", dataRow.getString("funits"));
					objs.put("x", time);
					objs.put("y", kpi);
					ary.add(objs);
				}
//				//默认值
//				if(data.size()==0){
//					JSONObject objs = new JSONObject();
//					Long time = 0l;
//					try {
//						 DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
//						time = Long.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date())).getTime());
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					objs.put("unit", dataRow.getString("funits"));
//					objs.put("x", time);
//					objs.put("y", 0.00);
//					ary.add(objs);
//				}
				String name = row.getString("ele_name");
				if(name.length()>23){
					name = name.substring(0, 23)+"..";
				}
				if(showDeviceName==1){
					obj.put("name", name+":"+dataRow.getString("ftitle"));
				}else{
					obj.put("name", dataRow.getString("ftitle"));
				}
				obj.put("data", ary);
				array.add(obj);
			}
		}
		return array;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfDatas(List<DataRow> rows,List<DataRow> kpis,String startTime,String endTime,String TimeType){
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select prf_timestamp,dev_id,ele_id,ele_name,");
		String tableName = kpis.get(0).getString("fprfview");
		if(kpis!=null && kpis.size()>0){
			for (int i=0;i<kpis.size();i++) {
				sql.append(kpis.get(i).getString("fid"));
				if(i<kpis.size()-1){
					sql.append(",");
				}
			}
			if(startTime!=null && startTime.length()>0){
				Date start = null;
				Date end = null;
				Long overTimes=null;
				try {
					start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					if(endTime!=null && endTime.length()>0){
						end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
						overTimes = end.getTime();
					}else{
						overTimes = new Date().getTime();  
					}
//					Long lastTime = start.getTime();
//					Long tem = 1000l;
//					if(overTimes - lastTime > 30*24*60*60*tem){
//						tableName+="_daily";
//					}else if(overTimes - lastTime > 7*24*60*60*tem){  
//						tableName+="_hourly";
//					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(TimeType!=null&&TimeType.length()>0){
				if(TimeType.equals("hour")){
					tableName+="_hourly";
				}else if(TimeType.equals("day")){
					tableName+="_daily";
				}
			}
			sql.append(" from "+tableName+" where 1=1 ");
			if(rows!=null && rows.size()>0){
				sql.append("and ele_id in (");
				for (int i=0;i<rows.size();i++) {
					sql.append(rows.get(i).getString("ele_id"));
					if(i<rows.size()-1){
						sql.append(",");
					}
				}
				sql.append(") ");
			}
			if(startTime!=null && startTime.length()>0){
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if(endTime!=null && endTime.length()>0){
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if(startTime==null || startTime.length()==0){
				if (endTime == null || endTime.length() == 0) {
					sql.append("and prf_timestamp >= ? ");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DATE, -1);
					args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
				}
			}
			sql.append("order by prf_timestamp");
		}
		if(kpis.get(0).getString("fstoragetype").equals("EMC")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
		}else if(kpis.get(0).getString("fstoragetype").equals("HOST")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
		}else{
			return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString(),args.toArray());
		}
	}
	
	public DBPage getPrfDatas(int curPage,int numPerPage,List<DataRow> rows,List<DataRow> kpis,String startTime,String endTime,String TimeType){
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select prf_timestamp,dev_id,ele_id,ele_name,");
		String tableName = kpis.get(0).getString("fprfview");
		if(kpis!=null && kpis.size()>0){
			for (int i=0;i<kpis.size();i++) {
				sql.append(kpis.get(i).getString("fid"));
				if(i<kpis.size()-1){
					sql.append(",");
				}
			}
			if(startTime!=null && startTime.length()>0){
				Date start = null;
				Date end = null;
				Long overTimes=null;
				try {
					start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					if(endTime!=null && endTime.length()>0){
						end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
						overTimes = end.getTime();
					}else{
						overTimes = new Date().getTime();  
					}
//					Long lastTime = start.getTime();
//					Long tem = 1000l;
//					if(overTimes - lastTime > 30*24*60*60*tem){
//						tableName+="_daily";
//					}else if(overTimes - lastTime > 7*24*60*60*tem){  
//						tableName+="_hourly";
//					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(TimeType!=null&&TimeType.length()>0){
				if(TimeType.equals("hour")){
					tableName+="_hourly";
				}else if(TimeType.equals("day")){
					tableName+="_daily";
				}
			}
			sql.append(" from "+tableName+" where 1=1 ");
			if(rows!=null && rows.size()>0){
				sql.append("and ele_id in (");
				for (int i=0;i<rows.size();i++) {
					sql.append(rows.get(i).getString("ele_id"));
					if(i<rows.size()-1){
						sql.append(",");
					}
				}
				sql.append(") ");
			}
			if(startTime!=null && startTime.length()>0){
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if(endTime!=null && endTime.length()>0){
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if(startTime==null || startTime.length()==0){
				if (endTime == null || endTime.length() == 0) {
					sql.append("and prf_timestamp >= ? ");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.MONTH, -1);
					args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
				}
			}
			sql.append("order by prf_timestamp");
		}
		if(kpis.get(0).getString("fstoragetype").equals("EMC")){
			//EMC 去mysql
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(),curPage,numPerPage);
		}else if(kpis.get(0).getString("fstoragetype").equals("HOST")||kpis.get(0).getString("fstoragetype").equals("APPLICATION")){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(),curPage,numPerPage);
		}else{
			return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql.toString(),args.toArray(),curPage,numPerPage);
		}
	}
	
	public List<DataRow> getDeviceofHostInfo(String devList,String keyName,String displayName,String tableName){
		StringBuffer sb = new StringBuffer("select "+keyName+" as ele_id,"+displayName+" as ele_name from "+tableName);
		List<Object> args = new ArrayList<Object>();
		if(devList!=null&&devList.length()>0){
		sb.append(" where "+keyName+" in ");
		sb.append("("+devList+")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceInfo(String devList,String keyName,String displayName,String tableName){
		StringBuffer sb = new StringBuffer("select "+keyName+" as ele_id,"+displayName+" as ele_name from "+tableName);
		List<Object> args = new ArrayList<Object>();
		sb.append(" where "+keyName+" in ");
		sb.append("("+devList+")");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceInfo2(String devList){
		String sql="select subsystem_id as ele_id,model as ele_name from t_res_storagesubsystem where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		sb.append("and subsystem_id in ("+devList+") ");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getKPIInfo(String kpiList){
		StringBuffer sb = new StringBuffer("select * from tnprffields where fid in");
		List<Object> args = new ArrayList<Object>();
		sb.append("("+kpiList+")");
		sb.append("order by ftitle");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getView(String storageType,String deviceType){
		StringBuffer sb = new StringBuffer("select * from tnprffields where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(storageType!=null && storageType.length()>0){
			sb.append("and fstoragetype = ? ");
			args.add(storageType);
		}
		if(deviceType!=null && deviceType.length()>0){
			sb.append("and fdevtype = ? ");
			args.add(deviceType);
		}
		sb.append("order by ftitle ");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	public void updatePrfField(DataRow row,String fname,String fdevicetype,Integer devId,Integer subsystemId,Integer level){
		
		String sql="select fid from tpdpfields where 1=1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if(level!=null && level>0){
			sb.append("and level = ? ");
			args.add(level);
		}
		if(fname!=null && fname.length()>0){
			sb.append("and fname = ? ");
			args.add(fname);
		}
		if(fdevicetype!=null && fdevicetype.length()>0){
			sb.append("and fdevicetype = ? ");
			args.add(fdevicetype);
		}
		if(subsystemId!=null && subsystemId>0){
			sb.append("and fsubsystemid = ? ");
			args.add(subsystemId);
		}
		if(devId!=null && devId>0){
			sb.append("and fdevice = ? ");
			args.add(devId);
		}
		DataRow dataRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
		if(dataRow!=null && dataRow.size()>0){
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("tpdpfields", row, "fid", dataRow.getInt("fid"));
		}else{
			PrfField(row);
		}
	}
	public void PrfField(DataRow row){
		String seq = "select max(fid) as maxId from tpdpfields";
		int maxId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(seq);
		row.set("FId", maxId+1);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tpdpfields", row);
	}

	public void PrfField1(DataRow row){
		String seq = "select max(fid) as maxId from tpcpfields";
		int maxId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(seq);
		row.set("FId", maxId+1);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tpcpfields", row);
	}
	
	public DataRow getPrfFieldInfo(Integer fid,Integer level,String fname,String fdevtype,Integer subsystemId,Integer devId){
		StringBuffer sb = new StringBuffer("select * from tpdpfields where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(fid!=null && fid>0){
			sb.append("and fid = ? ");
			args.add(fid);
		}
		if(level!=null && level>0){
			sb.append("and level = ? ");
			args.add(level);
		}
		if(fname!=null && fname.length()>0){
			sb.append("and fname = ? ");
			args.add(fname);
		}
		if(fdevtype!=null && fdevtype.length()>0){
			sb.append("and fdevicetype = ? ");
			args.add(fdevtype);
		}
		if(subsystemId!=null && subsystemId>0&&subsystemId.SIZE>0){
			sb.append("and fsubsystemid = ? ");
			args.add(subsystemId);
		}
		if(devId!=null && devId>0){
			sb.append("and fdevice = ? ");
			args.add(devId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
	public DataRow getStorageType(Integer subsystemId){
		String sql="select subsystem_id,the_display_name,os_type from v_res_storage_subsystem where subsystem_id = "+subsystemId;
		DataRow row = getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
		String type = "";
		if("25".equals(row.getString("os_type"))){
			type="DS";
		}else if("21".equals(row.getString("os_type")) || "38".equals(row.getString("os_type")) ){
			type="SVC";
		}else if("15".equals(row.getString("os_type")) || "37".equals(row.getString("os_type"))){
			type="BSP";
		}
		row.set("type", type);
		return row;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getdevInfo(Integer pid,String tableName,String theDisplayName,String id,String parentId) {
		
		String sql="select "+theDisplayName+" as ele_name,"+id+" as ele_id,"+parentId+" as pid from "+tableName+" where "+parentId+" = "+pid;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	} 
	
	/**
	 * 
	 * @param deviceTable   设备配置信息表名
	 * @param devId     	设备ID
	 * @param storageType 	类型
	 * @param deviceType  	设备类型
	 * @param tkey     		设备表主键列名
	 * @param tname    		设备表名称列名
	 * @return
	 */
	public DataRow getDefaultRow(String deviceTable,Integer devId,String storageType,String deviceType,String tkey,String tname){
		DataRow row = new DataRow();
		if(devId!=null && devId>0){
			row.set("fdevice", devId);
		}else{
			String sql="select "+tkey+" as ele_id,"+tname+" as ele_name from "+deviceTable;
			List<DataRow> rows=null;
			if(storageType.equals("HOST")){
				rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,2);
			}else{
				rows = getJdbcTemplate(WebConstants.DB_TPC).query(sql,2);
			}
			if(rows!=null && rows.size()>0){
				String devStr = "";
				for (int i = 0;i<rows.size();i++) {
					devStr+=rows.get(i).getString("ele_id");
					if(i<rows.size()-1){
						devStr+=",";
					}
				}
				row.set("fdevice", devStr);
			}
		}
		String sql="select fid from tnprffields where fstoragetype='"+storageType+"' and fdevtype='"+deviceType+"' order by ftitle";
		@SuppressWarnings("unchecked")
		List<DataRow> rowss = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,2);
		if(rowss!=null && rowss.size()>0){
			String filedStr="";
			for(int i=0;i<rowss.size();i++){
				filedStr += "'"+rowss.get(i).getString("fid")+"'";
				if(i<rowss.size()-1){
					filedStr+=",";
				}
			}
			row.set("fprfid", filedStr);
			row.set("fthrevalue", "");
			row.set("threshold", "");
			row.set("fyaxisname", "");
			row.set("flegend", 1);
			row.set("fisshow", 1);
			row.set("fstarttime", WebConstants.getDefaultStartTime());
			row.set("fendtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			return row;
		}else{
			return null;
		}
	}
	
	public List<DataRow> getUnitsById(String ids){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select funits from tnprffields where fid in ("+ids+")");
	}
}
