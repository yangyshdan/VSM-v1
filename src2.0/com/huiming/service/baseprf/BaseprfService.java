package com.huiming.service.baseprf;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class BaseprfService extends BaseService {
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * @see 获取TOPN的数据
	 * @param eleIds     因为一张表代表一种设备，那么ele_id就可以区分
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @param viewPostfix
	 * @param devType
	 * @param threvalue
	 * @return
	 */
	public JSONObject getTopnGraph(String eleIds, String kpi, String startTime, String endTime, 
			int topCount, String viewPostfix, String devType, double threvalue){

		//处理选择日期
		if(startTime == null || startTime.trim().isEmpty()){
			startTime = "1970-12-31 23:59:59";
		}
		if(endTime == null || endTime.trim().isEmpty()){
			endTime = SrContant.getTimeFormat(new Date());
		}
		return getDrawPerfTopNData(null, eleIds, null, kpi, startTime, endTime, topCount, viewPostfix, devType, threvalue);
	}
	
	/**
	 * 获取指定设备的部件的TopN数据
	 * @param devIds
	 * @param eleIds
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @param viewPostfix
	 * @param devType
	 * @param threvalue
	 * @return
	 */
	public JSONObject getTopnGraph(String devIds, String eleIds, String kpi, String startTime, String endTime, 
			int topCount, String viewPostfix, String devType, double threvalue){

		//处理选择日期
		if(startTime == null || startTime.trim().isEmpty()){
			startTime = "1970-12-31 23:59:59";
		}
		if(endTime == null || endTime.trim().isEmpty()){
			endTime = SrContant.getTimeFormat(new Date());
		}
		return getDrawPerfTopNData(devIds, eleIds, null, kpi, startTime, endTime, topCount, viewPostfix, devType, threvalue);
	}
	
	/**
	 * 
	 * @param devIds
	 * @param funits
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @param viewPostfix "" "_hourly" "_daily"
	 * @return
	 */
	public JSONObject getDrawPerfTopNData(String devIds, String eleIds, String funits, String kpi, 
			String startTime,  String endTime, int topCount, String viewPostfix, String devType, double threvalue) {
		JSONObject json = new JSONObject();
		//获取指定的KPI详细信息
		List<DataRow> kpiList = getKPIInfo("'" + kpi + "'");
		DataRow kpiRow = kpiList == null ? new DataRow() : kpiList.get(0);
		if(kpiRow.getString("fprfview") != null){
			kpiRow.set("fprfview", kpiRow.getString("fprfview") + viewPostfix);
		}
		String title = kpiRow.getString("ftitle");
		json.put("smallTitle", startTime + " ~ " + endTime);
		json.put("funits", title + "(" + kpiRow.getString("funits") + ")");
		JSONObject obj = generateTopNChartData02(devIds, eleIds, kpiRow, startTime, endTime, topCount, devType, threvalue);
		
		json.putAll(obj);
		json.put("ftitle", title);
		json.put("charttype", "rcloumn");
		return json;
	}
	
	/**
	 * 获取性能数据
	 * @param row
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @param TimeType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DataRow> getPrfDataBase(DataRow row, DataRow kpis, String startTime, String endTime, String TimeType) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select prf_timestamp,dev_id,ele_id,ele_name,");
		String tableName = kpis.getString("fprfview");
		String dbType = kpis.getString("fdbtype").trim();
		if (kpis != null && kpis.size() > 0) {
			sql.append(kpis.getString("fid"));
			if (startTime != null && startTime.length() > 0) {
				Date start = null;
				Date end = null;
				Long overTimes = null;
				try {
					start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					if (endTime != null && endTime.length() > 0) {
						end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
						overTimes = end.getTime();
					} else {
						overTimes = new Date().getTime();
					}
					if (TimeType != null && TimeType.equals("report")) {
						Long lastTime = start.getTime();
						Long tem = 1000l;
						if ((overTimes - lastTime) >= 30 * 24 * 60 * 60 * tem) {
							tableName += "_daily";
						} else if (overTimes - lastTime >= 7 * 24 * 60 * 60 * tem) {
							tableName += "_hourly";
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (TimeType != null && TimeType.length() > 0) {
				if (TimeType.equals("hour")) {
					tableName += "_hourly";
				} else if (TimeType.equals("day")) {
					tableName += "_daily";
				}
			}
			sql.append(" from " + tableName + " ");
			sql.append("where ele_id = ? ");
			args.add(row.getString("ele_id"));
			if (startTime != null && startTime.length() > 0) {
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if (endTime != null && endTime.length() > 0) {
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if (startTime == null || startTime.length() == 0) {
				if (endTime == null || endTime.length() == 0) {
					sql.append("and prf_timestamp >= ? ");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DATE, -1);
					args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(calendar.getTime()));
				}
			}
			sql.append("order by prf_timestamp");
		}
		//判断查询那个数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
		} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
			return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString(),args.toArray());
		}
		return null;
	}

	void print(Object obj){
		//Logger.getLogger(this.getClass()).info(JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss"));
		Logger.getLogger(this.getClass()).info(JSON.toJSONString(obj));
	}
	/**
	 * 处理性能绘图数据
	 * @deprecated1 效率低下且不正确
	 * @param showDeviceName 是否显示设备名称
	 * @param rows  
	 * @param kpis  性能指标
	 * @param startTime
	 * @param endTime
	 * @param TimeType  时间粒度
	 * @return
	 */
	public JSONArray getPrfDatas(Integer showDeviceName, List<DataRow> rows, 
			List<DataRow> kpis, String startTime, 
			String endTime, String TimeType) {
		JSONArray array = new JSONArray();
		List<DataRow> data = getPrfDatas(rows, kpis, startTime, endTime, TimeType);
//		print(data);
//		print(rows);
//		print(kpis);
		DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat decifmt = new DecimalFormat("0.00");
		String dateStr;
		for (DataRow dataRow : kpis) {
			for (DataRow row : rows) {
				JSONObject obj = new JSONObject();
				JSONArray ary = new JSONArray();
				for (DataRow dataRow2 : data) {
					JSONObject objs = new JSONObject();
					Long time = 0l;
					dateStr = dataRow2.getString("prf_timestamp");
					if(dateStr != null){
						try {
							time = Long.valueOf(datefmt.parse(dateStr.replace('/', '-')).getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					else {
						time = 0L;
					}
					
					double kpi = Double.parseDouble(decifmt.format(dataRow2.getDouble(dataRow.getString("fid").toLowerCase())));
					objs.put("unit", dataRow.getString("funits"));
					objs.put("x", time);
					objs.put("y", kpi);
					ary.add(objs);
				}
				String name = row.getString("ele_name");
				if (name.length() > 23) {
					name = name.substring(0, 23) + "...";
				}
				if (showDeviceName == 1) {
					obj.put("name", name + ":" + dataRow.getString("ftitle"));
				} else {
					obj.put("name", dataRow.getString("ftitle"));
				}
				obj.put("data", ary);
				array.add(obj);
			}
		}
		return array;
	}
	
	/**
	 * 处理性能绘图数据
	 * @param showDeviceName 是否显示设备名称
	 * @param rows  
	 * @param kpis  性能指标
	 * @param startTime
	 * @param endTime
	 * @param TimeType  时间粒度
	 * @return
	 */
	public List<Object> getSeries(
			Integer showDeviceName, 
			List<DataRow> rows, 
			List<DataRow> kpis, 
			String startTime, 
			String endTime, 
			String TimeType) {
		List<Object> series = new ArrayList<Object>(kpis.size() * rows.size());
		String name;
		for (DataRow kpi : kpis) {
			for (DataRow row : rows) {
				Map<String, Object> obj = new HashMap<String, Object>();
				List<DataRow> data = getPrfDatas2(row.getString("ele_id"), 
						kpi.getString("funits"), kpi, startTime, endTime, TimeType);
				name = row.getString("ele_name");
				if (name != null) {
					if(name.length() > 23){
						name = name.substring(0, 23) + "...";
					}
				}
				else if(data != null && data.size() > 0){
					name = data.get(0).getString("ele_name");
				}
//				if (showDeviceName == 1) {
					obj.put("name", name + ":" + kpi.getString("ftitle"));
//				} else {
//					obj.put("name", kpi.getString("ftitle"));
//				}
				obj.put("data", data);
				series.add(obj);
			}
		}
		//print(series);
		return series;
	}

	/**
	 * 获取指定部件的性能数据
	 * @param rows
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @param TimeType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfDatas(List<DataRow> rows, List<DataRow> kpis, 
			String startTime, String endTime, String TimeType) {
		List<Object> args = new ArrayList<Object>();
		String tableName = kpis.get(0).getString("fprfview");
		String dbType = kpis.get(0).getString("fdbtype").trim();
		String str = "";
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			str = " DATE_FORMAT(prf_timestamp,'%Y/%m/%d %H:%i:%s') AS prf_timestamp ";
		} 
		else if (dbType.equals(SrContant.DBTYPE_TPC)) {
			str = " TO_CHAR(prf_timestamp,'YYYY/MM/DD HH24:MI:SS') AS prf_timestamp ";
		}
		StringBuffer sql = new StringBuffer("select "+str+",dev_id,ele_id,ele_name,");
		DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (kpis != null && kpis.size() > 0) {
			StringBuilder sb_where = new StringBuilder(30);
			String temp;
			for (int i = 0, s = kpis.size() - 1, size = kpis.size(); i < size; i++) {
				temp = kpis.get(i).getString("fid");
				sb_where.append(temp + " is not null ");
				sql.append(temp);
				if (i < s) {
					sql.append(",");
					sb_where.append(" and ");
				}
			}
			if (startTime != null && startTime.length() > 0) {
				Date start = null;
				Date end = null;
				Long overTimes = null;
				try {
					start = datefmt.parse(startTime);
					if (endTime != null && endTime.length() > 0) {
						end = datefmt.parse(endTime);
						overTimes = end.getTime();
					} else {
						overTimes = new Date().getTime();
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if ("hour".equalsIgnoreCase(TimeType)) {
				tableName += "_hourly";
			} else if ("day".equalsIgnoreCase(TimeType)) {
				tableName += "_daily";
			}
			sql.append(" from " + tableName + " where 1=1 ");
			if (rows != null && rows.size() > 0) {
				sql.append("and ele_id in (");
				for (int i = 0, s = rows.size() - 1, size = rows.size(); i < size; i++) {
					sql.append(rows.get(i).getString("ele_id"));
					if (i < s) { sql.append(","); }
				}
				sql.append(") ");
			}
			if (startTime != null && startTime.length() > 0) {
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if (endTime != null && endTime.length() > 0) {
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if (startTime == null || startTime.length() == 0) {
				if (endTime == null || endTime.length() == 0) {
					sql.append("and prf_timestamp >= ? ");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DATE, -1);
					args.add(datefmt.format(calendar.getTime()));
				}
			}
			sql.append(" and ");
			sql.append(sb_where);
			sql.append(" order by prf_timestamp ");
		}
//		Logger.getLogger(this.getClass()).info(JSON.toJSONStringWithDateFormat(args, "yyyy-MM-dd HH:mm:ss"));
		
		//判断查询那个数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(), args.toArray());
		} 
		else if (dbType.equals(SrContant.DBTYPE_TPC)) {
			return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString(), args.toArray());
		}
		return null;
	}
	
	/**
	 * 获取指定部件的性能数据
	 * @param rows
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @param TimeType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfDatas2(String ele_id, String unit, DataRow kpis, 
			String startTime, String endTime, String TimeType) {
		StringBuffer sql = new StringBuffer("select prf_timestamp as x,"); // dev_id,ele_id,ele_name,
		String tableName = kpis.getString("fprfview");
		String dbType = kpis.getString("fdbtype").trim();
		if (kpis != null && kpis.size() > 0) {
			boolean isSR = dbType.equals(SrContant.DBTYPE_SR);
			if(isSR){
				sql.append(" round(" + kpis.getString("fid") + ",2)");
			}
			else {
				sql.append(" cast(" + kpis.getString("fid") + " as decimal(8,2))");
			}
			sql.append(" as y,'" + unit + "' as unit " );
			
			if ("hour".equalsIgnoreCase(TimeType)) {
				tableName += "_hourly";
			} else if ("day".equalsIgnoreCase(TimeType)) {
				tableName += "_daily";
			}
			sql.append(" from " + tableName + " where 1=1 ");
			if (startTime != null && startTime.length() > 0) {
				sql.append(" and prf_timestamp>=timestamp('");
				sql.append(startTime);
				sql.append("') ");
			}
			if (endTime != null && endTime.length() > 0) {
				sql.append(" and prf_timestamp<=timestamp('");
				sql.append(endTime);
				sql.append("') ");
			}
			
			if (ele_id != null && ele_id.trim().length() > 0) {
				sql.append(" and ele_id=");
				sql.append(ele_id);
			}
			sql.append(" order by prf_timestamp");
			if(isSR){
				return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
			}
			else {
				if(Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null){
					return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString());
				}
			}
		}
		return null;
	}

	/**
	 * 获取分页性能数据
	 * @param curPage
	 * @param numPerPage
	 * @param rows
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @param TimeType
	 * @return
	 */
	public DBPage getPrfDatas(int curPage, int numPerPage, List<DataRow> rows, List<DataRow> kpis, 
			String startTime, String endTime, String TimeType) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select prf_timestamp,dev_id,ele_id,ele_name,");
		String tableName = kpis.get(0).getString("fprfview");
		String dbType = kpis.get(0).getString("fdbtype").trim();
		if (kpis != null && kpis.size() > 0) {
			StringBuilder sb_where = new StringBuilder(30);
			String temp;
			for (int i = 0, len = kpis.size() - 1; i <= len; ++i) {
				temp = kpis.get(i).getString("fid");
				sql.append(temp);
				sb_where.append(temp + " is not null ");
				if (i < len) {
					sql.append(",");
					sb_where.append(" and ");
				}
			}
			if (startTime != null && startTime.length() > 0) {
				Date start = null;
				Date end = null;
				Long overTimes = null;
				try {
					start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					if (endTime != null && endTime.length() > 0) {
						end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
						overTimes = end.getTime();
					} else {
						overTimes = new Date().getTime();
					}
					// Long lastTime = start.getTime();
					// Long tem = 1000l;
					// if(overTimes - lastTime > 30*24*60*60*tem){
					// tableName+="_daily";
					// }else if(overTimes - lastTime > 7*24*60*60*tem){
					// tableName+="_hourly";
					// }
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (TimeType != null && TimeType.length() > 0) {
				if (TimeType.equals("hour")) {
					tableName += "_hourly";
				} else if (TimeType.equals("day")) {
					tableName += "_daily";
				}
			}
			sql.append(" from " + tableName + " where 1=1 ");
			if (rows != null && rows.size() > 0) {
				sql.append("and ele_id in (");
				for (int i = 0; i < rows.size(); i++) {
					sql.append(rows.get(i).getString("ele_id"));
					if (i < rows.size() - 1) {
						sql.append(",");
					}
				}
				sql.append(") ");
			}
			if (startTime != null && startTime.length() > 0) {
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if (endTime != null && endTime.length() > 0) {
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if (startTime == null || startTime.length() == 0) {
				if (endTime == null || endTime.length() == 0) {
					sql.append("and prf_timestamp >= ? ");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.MONTH, -1);
					args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
				}
			}
			sql.append(" and ");
			sql.append(sb_where);
			sql.append(" order by prf_timestamp");
		}
		//判断查询那个数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
		} 
		else if (dbType.equals(SrContant.DBTYPE_TPC)) {
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql.toString(),
						args.toArray(), curPage, numPerPage);
			}
			
		}
		return null;
	}
	
	/**
	 * 处理绘制性能图数据
	 * @param showDeviceName
	 * @param rows
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public JSONArray getPrfDatas(Integer showDeviceName,List<DataRow> rows,List<DataRow> kpis,String startTime,String endTime){
		JSONArray array = new JSONArray();
		List<DataRow> perfData = getPrfDatas(rows, kpis, startTime, endTime);
		for (DataRow kpiRow : kpis) {
			for (DataRow devRow : rows) {
				int devId1 = devRow.getInt("ele_id");
				JSONObject obj = new JSONObject();
				JSONArray ary = new JSONArray();
				for (DataRow dataRow2 : perfData) {
					int devId2 = dataRow2.getInt("ele_id");
					if (devId1 == devId2) {
						JSONObject objs = new JSONObject();
						Long time = 0l;
						try {
							time = Long.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataRow2.getString("prf_timestamp")).getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						Double kpi = Double.parseDouble(new DecimalFormat("0.00").format(dataRow2.getDouble(kpiRow.getString("fid").toLowerCase())));
						objs.put("unit", kpiRow.getString("funits"));
						objs.put("x", time);
						objs.put("y", kpi);
						ary.add(objs);
					}
				}
				String name = devRow.getString("ele_name");
				if (name.length() > 23) {
					name = name.substring(0, 23) + "...";
				}
				if (showDeviceName == 1) {
					obj.put("name", name + ":" + kpiRow.getString("ftitle"));
				} else {
					obj.put("name", kpiRow.getString("ftitle"));
				}
				obj.put("data", ary);
				array.add(obj);
			}
		}
		return array;
	}
	
	/**
	 * 获取性能数据
	 * @param rows
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfDatas(List<DataRow> rows,List<DataRow> kpis,String startTime,String endTime){
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select prf_timestamp,dev_id,ele_id,ele_name,");
		String viewName = kpis.get(0).getString("fprfview").trim();
		String dbType = kpis.get(0).getString("fdbtype").trim();
		if (kpis != null && kpis.size() > 0) {
			for (int i = 0; i < kpis.size(); i++) {
				sql.append(kpis.get(i).getString("fid"));
				if (i < kpis.size()-1) {
					sql.append(",");
				}
			}
			if (startTime != null && startTime.length() > 0) {
				Date start = null;
				Date end = null;
				Long overTimes = null;
				try {
					start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					if (endTime != null && endTime.length() > 0) {
						end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
						overTimes = end.getTime();
					} else {
						overTimes = new Date().getTime();
					}
					Long lastTime = start.getTime();
					Long tem = 1000l;
					if (overTimes - lastTime > 30 * 24 * 60 * 60 * tem) {
						viewName += "_daily";
					} else if (overTimes - lastTime > 7 * 24 * 60 * 60 * tem) {
						viewName += "_hourly";
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			sql.append(" from " + viewName + " where 1=1 ");
			if (rows != null && rows.size() > 0) {
				sql.append("and ele_id in (");
				for (int i = 0; i < rows.size(); i++) {
					sql.append(rows.get(i).getString("ele_id"));
					if (i < rows.size() - 1) {
						sql.append(",");
					}
				}
				sql.append(") ");
			}
			if (startTime != null && startTime.length() > 0) {
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if (endTime != null && endTime.length() > 0) {
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if (startTime == null || startTime.length() == 0) {
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
		//判断查询那个数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
		} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
			return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString(),args.toArray());
		}
		return null;
	}
	
	/**
	 * 获取性能数据(分页)
	 * @param curPage
	 * @param numPerPage
	 * @param rows
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public DBPage getPrfDatas(int curPage,int numPerPage,List<DataRow> rows,List<DataRow> kpis,String startTime,String endTime){
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select prf_timestamp,dev_id,ele_id,ele_name,");
		String viewName = kpis.get(0).getString("fprfview").trim();
		String dbType = kpis.get(0).getString("fdbtype").trim();
		if (kpis != null && kpis.size() > 0) {
			for (int i = 0; i < kpis.size(); i++) {
				sql.append(kpis.get(i).getString("fid"));
				if (i < kpis.size()-1) {
					sql.append(",");
				}
			}
			if (startTime != null && startTime.length() > 0) {
				Date start = null;
				Date end = null;
				Long overTimes=null;
				try {
					start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					if (endTime != null && endTime.length() > 0) {
						end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
						overTimes = end.getTime();
					} else {
						overTimes = new Date().getTime();
					}
					Long lastTime = start.getTime();
					Long tem = 1000l;
					if (overTimes - lastTime > 30 * 24 * 60 * 60 * tem) {
						viewName += "_daily";
					} else if (overTimes - lastTime > 7 * 24 * 60 * 60 * tem) {
						viewName += "_hourly";
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			sql.append(" from " + viewName + " where 1 = 1 ");
			if (rows != null && rows.size() > 0) {
				sql.append("and ele_id in (");
				for (int i = 0; i < rows.size(); i++) {
					sql.append(rows.get(i).getString("ele_id"));
					if (i < rows.size() - 1) {
						sql.append(",");
					}
				}
				sql.append(") ");
			}
			if (startTime != null && startTime.length() > 0) {
				sql.append("and prf_timestamp >= ? ");
				args.add(startTime);
			}
			if (endTime != null && endTime.length() > 0) {
				sql.append("and prf_timestamp <= ? ");
				args.add(endTime);
			}
			if (startTime == null || startTime.length() == 0) {
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
		//判断查询那个数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
		} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
			return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceofHostInfo(String devList, String keyName, String displayName, String tableName) {
		StringBuffer sb = new StringBuffer("select " + keyName + " as ele_id," + displayName + " as ele_name from " + tableName);
		List<Object> args = new ArrayList<Object>();
		if (devList != null && devList.length() > 0) {
			sb.append(" where " + keyName + " in ");
			sb.append("(" + devList + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(), args.toArray());
	}

	/**
	 * 获取设备信息(TPC DB)
	 * @param devList
	 * @param keyName
	 * @param displayName
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceInfo(String devList, String keyName, String displayName, String tableName){
		if(devList != null && devList.trim().length() > 0) {
			StringBuffer sb = new StringBuffer("select " + keyName + " as ele_id," + displayName + " as ele_name from " + tableName);
			sb.append(" where " + keyName + " in ");
			sb.append("(" + devList + ")");
			return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString());
		}
		return new ArrayList<DataRow>(0);
	}
	
	/**
	 * 获取设备信息
	 * @param devList
	 * @param keyName
	 * @param displayName
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceInfoList(String devList,String keyName,String displayName,String tableName){
		StringBuffer sb = new StringBuffer("select " + keyName + " as ele_id," + displayName + " as ele_name from " + tableName);
		List<Object> args = new ArrayList<Object>();
		sb.append(" where " + keyName + " in ");
		sb.append("(" + devList + ")");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 获取设备信息(SR DB)
	 * @param devList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getSRDeviceInfo(String devList){
		String sql="select subsystem_id as ele_id,name as ele_name from t_res_storagesubsystem where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		sb.append("and subsystem_id in (" + devList + ") ");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}

	/**
	 * 获取性能指标信息
	 * @param kpiList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getKPIInfo(String kpiList){
		StringBuffer sb = new StringBuffer("select * from tnprffields where fid in ");
		List<Object> args = new ArrayList<Object>();
		sb.append("(" + kpiList + ")");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getView(String storageType, String deviceType){
		StringBuffer sb = new StringBuffer("select * from tnprffields where 1=1 ");
		if(storageType != null && storageType.length()>0){
			sb.append(" and fstoragetype='" + storageType + "' ");
		}
		if(deviceType!=null && deviceType.length()>0){
			sb.append(" and fdevtype='" + deviceType + "' ");
		}
		//对重要程度排序
		sb.append(" order by fimp desc ");
		//end
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 更新性能图信息配置
	 * @param row
	 * @param fname
	 * @param fdevicetype
	 * @param devId
	 * @param subsystemId
	 * @param level
	 */
	public void updatePrfField(DataRow row, String fname, String fdevicetype, Integer devId, Integer subsystemId, Integer level){
		String sql = "select fid from tpdpfields where 1 = 1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		Long userId = row.getLong("fuserid");
		if (userId != null && userId > 0) {
			sb.append("and fuserid = ? ");
			args.add(userId);
		}
		if (level != null && level > 0) {
			sb.append("and level = ? ");
			args.add(level);
		}
		if (fname != null && fname.length() > 0) {
			sb.append("and fname = ? ");
			args.add(fname);
		}
		if (fdevicetype != null && fdevicetype.length() > 0) {
			sb.append("and fdevicetype = ? ");
			args.add(fdevicetype);
		}
		if (subsystemId != null && subsystemId > 0) {
			sb.append("and fsubsystemid = ? ");
			args.add(subsystemId);
		}
		if (devId != null && devId > 0) {
			sb.append("and fdevice = ? ");
			args.add(devId);
		}
		DataRow dataRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(), args.toArray());
		if (dataRow != null && dataRow.size() > 0) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("tpdpfields", row, "fid", dataRow.getInt("fid"));
		} else {
			PrfField(row);
		}
	}

	public void PrfField(DataRow row) {
		String seq = "select max(fid) as maxId from tpdpfields";
		int maxId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(seq);
		row.set("FId", maxId + 1);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tpdpfields", row);
	}

	public void PrfField1(DataRow row) {
		String seq = "select max(fid) as maxId from tpcpfields";
		int maxId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(seq);
		row.set("FId", maxId + 1);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tpcpfields", row);
	}
	
	/**
	 * @see
	 * 2015-04-16 13:13:25 modified by HGC. 增加画图类型的查询条件
	 * 获取定义的性能数据
	 * @param fid
	 * @param level
	 * @param fname
	 * @param fdevtype
	 * @param subsystemId
	 * @param devId
	 * @param graphType
	 * @param userId
	 * @return
	 */
	public DataRow getPrfFieldInfo(Integer fid, Integer level, String fname, String fdevtype,
			Integer subsystemId, Integer devId, Integer graphType, Long userId) {
		StringBuilder sb = new StringBuilder("select * from tpdpfields where 1 = 1 ");
//		List<Object> args = new ArrayList<Object>();
		if (fid != null && fid > 0) {
			sb.append("and fid=");
			sb.append(fid);
			sb.append(" ");
		}
		if (level != null && level > 0) {
			sb.append("and level=");
			sb.append(level);
			sb.append(" ");
		}
		if (fname != null && fname.length() > 0) {
			sb.append("and fname='");
			sb.append(fname);
			sb.append("' ");
		}
		if (fdevtype != null && fdevtype.length() > 0) {
			sb.append("and fdevicetype='");
			sb.append(fdevtype);
			sb.append("' ");
		}
		if (subsystemId != null && subsystemId > 0) {
			sb.append("and fsubsystemid=");
			sb.append(subsystemId);
			sb.append(" ");
		}
		if (devId != null && devId > 0) {
			sb.append("and fdevice=");
			sb.append(devId);
			sb.append(" ");
		}
		if(graphType != null){ // 增加
			sb.append(" and graphType=");
			sb.append(graphType);
			sb.append(" ");
		}
		if (userId != null && userId > 0) {
			sb.append(" and fuserid=");
			sb.append(userId);
			sb.append(" ");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString());
	}

	/**
	 * @see 获取定义的性能历史数据
	 * @param fid   
	 * @param level
	 * @param fname
	 * @param fdevtype 设备类型，例如HDS、EMC和DS、SVC、BSP
	 * @param subsystemId
	 * @param devId
	 * @return
	 */
	public DataRow getPrfFieldInfo(Integer fid, Integer level, String fname, String fdevtype, Integer subsystemId, Integer devId, Long userId) {
		return getPrfFieldInfo(fid, level, fname, fdevtype, subsystemId, devId, null, userId);
	}
	
	/**
	 * 获取存储系统信息
	 * @param subsystemId
	 * @return
	 */
	public DataRow getStorageType(Integer subsystemId){
		String sql = "select subsystem_id,the_display_name,os_type from v_res_storage_subsystem where subsystem_id = " + subsystemId;
		DataRow row = getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
		String type = "";
		String osType = row.getString("os_type");
		if ("25".equals(osType)) { 
			type = "DS";
		} 
		else if ("21".equals(osType) || "38".equals(osType)) {
			type = "SVC";
		} 
		else if ("15".equals(osType) || "37".equals(osType) || "10".equals(osType)) {
			type = "BSP";
		}
		row.set("type", type);
		return row;
	}
	public String getStorageType(String osType){
		String type = null;
		if ("25".equals(osType)) { 
			type = "DS";
		} 
		else if ("21".equals(osType) || "38".equals(osType)) {
			type = "SVC";
		} 
		else if ("15".equals(osType) || "37".equals(osType) || "10".equals(osType)) {
			type = "BSP";
		}
		return type == null? osType : type;
	}
	
	/**
	 * 获取存储系统信息
	 * @param subsystemId
	 * @return
	 */
	public DataRow getStorageInfo(Integer subsystemId){
		String sql = "select subsystem_id,name,storage_type as type from t_res_storagesubsystem where subsystem_id = " + subsystemId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}

	/**
	 * 获取设备信息
	 * @param pid
	 * @param tableName
	 * @param theDisplayName
	 * @param id
	 * @param parentId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getdevInfo(Integer pid,String tableName,String theDisplayName,String id,String parentId) {
		String sql = "select " + theDisplayName + " as ele_name," + id
				+ " as ele_id," + parentId + " as pid from " + tableName
				+ " where " + parentId + " = " + pid;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	} 
	
	/**
	 * 获取设备列表
	 * @param subsystemId
	 * @param tableName
	 * @param colSystemId
	 * @param colSubDevId
	 * @param colSubDevName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceList(Integer subsystemId,String tableName,String colSystemId,String colSubDevId,String colSubDevName) {
		String sql = "select " + colSubDevId + " as ele_id," + colSubDevName + " as ele_name," 
			+ colSystemId + " as pid from " + tableName + " where " + colSystemId + " = " + subsystemId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
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
	@SuppressWarnings("unchecked")
	public DataRow getDefaultRow(String deviceTable,
			Integer devId, String storageType, String deviceType,
			String tkey, String tname){
		DataRow row = new DataRow();
		if (devId != null && devId > 0) { row.set("fdevice", devId); }
		else {
			String sql = "select " + tkey + " as ele_id," + tname + " as elve_name from " + deviceTable;
			List<DataRow> rows = null;
			//判断设备类型,查询相应数据库
			rows = getJdbcTemplate(SrContant.getDBType(storageType)).query(sql,3);
			if (rows != null && rows.size() > 0) {
				String devStr = "";
				for (int i = 0, size = rows.size() - 1; i <= size; i++) {
					devStr += rows.get(i).getString("ele_id");
					if (i < size) {
						devStr += ",";
					}
				}
				row.set("fdevice", devStr);
			}
		}
		String sql = "select fid from tnprffields where fstoragetype='" + storageType + "' and fdevtype='" + deviceType + "'";
		List<DataRow> rowss = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);//, SrContant.PERF_COUNT);
		if (rowss != null && rowss.size() > 0) {
			String filedStr = "";
			for (int i = 0, len = rowss.size() - 1; i < rowss.size(); i++) {
				filedStr += "'" + rowss.get(i).getString("fid") + "'";
				if (i < len) {
					filedStr += ",";
				}
			}
			row.set("fprfid", filedStr);
		} 
		else {
			return null;
		}
		row.set("fthrevalue", "");
		row.set("threshold", "");
		row.set("fyaxisname", "");
		row.set("flegend", 0);
		row.set("fisshow", 1);
		row.set("fstarttime", WebConstants.getDefaultStartTime());
		row.set("fendtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		return row;
	}

	public List<DataRow> getUnitsById(String ids) {
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select funits from tnprffields where fid in (" + ids + ")");
	}
	
	/**
	 * 获取指定设备的TopN数据
	 * @param devIds(设备Ids)
	 * @param eleIds(部件Ids)
	 * @param kpiRow
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @param devType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getTopnData02(String devIds, String eleIds, DataRow kpiRow, String startTime,
			String endTime, int topCount, String devType){
		String sql;
		String viewName = kpiRow.getString("fprfview").trim();
		String dbType = kpiRow.getString("fdbtype").trim();
		String kpi = kpiRow.getString("fid").trim();
		String _devIds = "";
		String _eleIds = "";
		if (StringHelper.isNotEmpty(devIds) && StringHelper.isNotBlank(devIds)) {
			_devIds = String.format(" and dev_id in (%s) ", devIds);
		}
		if(eleIds != null && eleIds.trim().length() > 0){
			_eleIds = String.format(" and ele_id IN (%s) ", eleIds);
		}
		//判断查哪个视图
		if (SrContant.SUBDEVTYPE_PHYSICAL.equalsIgnoreCase(devType)) {
			sql = String.format("SELECT t1.*,t2.%s AS comp_id FROM (SELECT dev_id,ele_id,ele_name, '' as prf_timestamp,avg(%s) as kpi FROM %s WHERE 1 = 1 %s %s "+ 
					" and prf_timestamp >= ? and prf_timestamp <= ? GROUP BY ele_id,ele_name,dev_id) t1 JOIN %s t2 ON t2.%s=t1.ele_id order by t1.kpi desc", 
					"host_computer_id", kpi, viewName, _devIds, _eleIds, "t_res_hypervisor", "hypervisor_id");
			
		} else if (SrContant.SUBDEVTYPE_VIRTUAL.equalsIgnoreCase(devType)) {
			sql = String.format("SELECT t1.*,t2.%s AS comp_id FROM (SELECT dev_id,ele_id,ele_name, '' as prf_timestamp,avg(%s) as kpi FROM %s WHERE 1 = 1 %s %s "+ 
					" and prf_timestamp >= ? and prf_timestamp <= ? GROUP BY ele_id,ele_name,dev_id) t1 JOIN %s t2 ON t2.%s=t1.ele_id order by t1.kpi desc", 
					"computer_id", kpi, viewName, _devIds, _eleIds, "t_res_virtualmachine", "vm_id");
		} else {
//			sql = String.format(
//			"select * from (select '' as prf_timestamp,dev_id,ele_id,ele_name,avg(%s) as kpi from %s where ele_id in(%s) and prf_timestamp >= ? " +
//			" and prf_timestamp <= ? group by ele_id,ele_name,dev_id) order by kpi desc", kpi, viewName, eleIds, kpi);
//			
			sql = String.format("select '%s' as dbtype,'' as prf_timestamp,dev_id,ele_id,ele_name," +
		           "avg(%s) as kpi from %s where 1 = 1 %s %s and prf_timestamp >= ? " +
		           "and prf_timestamp <= ? group by ele_id,ele_name,dev_id order by avg(%s) desc", 
		           dbType, kpi, viewName, _devIds, _eleIds, kpi);
		}
		//判断到哪个数据库查询视图
		if (StringHelper.isNotEmpty(dbType) && StringHelper.isNotBlank(dbType)) {
			if (dbType.equals(SrContant.DBTYPE_SR)) {
				return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{startTime,endTime}, topCount);
			} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
				return getJdbcTemplate(WebConstants.DB_TPC).query(sql, new Object[]{startTime, endTime}, topCount);
			}
		}
		return null;
	}
	
	/**
	 * 获取指定设备的TopN数据
	 * @param devId
	 * @param kpiRow
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getTopnData(String devId, DataRow kpiRow, String startTime, String endTime,
			int topCount){
		StringBuilder sb = new StringBuilder(100);
		String viewName = kpiRow.getString("fprfview").trim();
		String dbType = kpiRow.getString("fdbtype").trim();
		String kpi = kpiRow.getString("fid").trim();
		//判断查哪个视图
		if (startTime != null && startTime.length() > 0) {
			Date start = null;
			Date end = null;
			Long overTimes = null;
			try {
				start = df.parse(startTime);
				if (endTime != null && endTime.length() > 0) {
					end = df.parse(endTime);
					overTimes = end.getTime();
				} else {
					overTimes = new Date().getTime();
				}
				Long lastTime = start.getTime();
				Long tem = 1000l;
				if (overTimes - lastTime > 30 * 24 * 60 * 60 * tem) {
					viewName += "_daily";
				} else if (overTimes - lastTime > 7 * 24 * 60 * 60 * tem) {
					viewName += "_hourly";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		sb.append("select '' as prf_timestamp,dev_id,ele_id,ele_name,");
		sb.append("avg(" + kpi + ") as kpi");
		sb.append(" from " + viewName);
		sb.append(" where dev_id in(" + devId + ")");
		sb.append(" and prf_timestamp >= ?");
		sb.append(" and prf_timestamp <= ?");
		sb.append(" group by ele_id,ele_name,dev_id");
		sb.append(" order by avg(" + kpi + ") desc");
		//判断到哪个数据库查询视图
		if (StringHelper.isNotEmpty(dbType) && StringHelper.isNotBlank(dbType)) {
			if (dbType.equals(SrContant.DBTYPE_SR)) {
				return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),new Object[]{startTime,endTime},topCount);
			} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
				return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),new Object[]{startTime,endTime},topCount);
			}
		}
		return null;
	}
	
	/**
	 * 生成TopN图形数据(指定设备)
	 * @param devId
	 * @param kpiRow
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @return
	 */
	public JSONObject generateTopNChartData(String devId,
			DataRow kpiRow, String startTime, String endTime, int topCount) {
		JSONObject json = new JSONObject();
		JSONArray names = new JSONArray();
		JSONArray array = new JSONArray();
		//获取TopN数据
		List<DataRow> rows = getTopnData(devId, kpiRow, startTime, endTime, topCount);
		DecimalFormat decFmt = new DecimalFormat("0.00");
		for (DataRow dataRow : rows) {
			names.add(dataRow.getString("ele_name"));
			JSONObject obj = new JSONObject();
			//添加样式
//			if (i == 0) {
//				obj.put("dataLabels","{style: {fontWeight:'bold',color: 'red'}}");
//			}
			obj.put("time", dataRow.getString("prf_timestamp"));
			obj.put("y", Double.parseDouble(decFmt.format(dataRow.getDouble("kpi"))));
			//topN跳转
			obj.put("eleid",dataRow.getString("ele_id"));
			obj.put("devid",dataRow.getString("dev_id"));
			obj.put("compId", dataRow.getString("comp_id"));
			obj.put("dbtype", dataRow.getString("dbtype"));
			obj.put("elename",dataRow.getString("ele_name"));
			//
			array.add(obj);
		}
		if(array.size() > 0){
			array.getJSONObject(0).put("dataLabels","{style: {fontWeight:'bold',color: 'red'}}");
		}
		json.put("names", names);
		json.put("data", array);
		return json;
	}
	
	/**
	 * 获取绘制TopN图数据
	 * @param devId
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public JSONObject getDrawPerfTopNData(String eleIds, String funits, String kpi, 
			String startTime, String endTime, int topCount) {
		JSONObject json = new JSONObject();
		//获取指定的KPI详细信息
		List<DataRow> kpiList = getKPIInfo("'" + kpi + "'");
		DataRow kpiRow = kpiList == null ? new DataRow() : kpiList.get(0);
		json.put("smallTitle", startTime + " ~ " + endTime);
		json.put("funits", funits);
		JSONObject obj = generateTopNChartData(eleIds, kpiRow, startTime, endTime, topCount);
		
		json.putAll(obj);
		json.put("ftitle", kpiRow.getString("ftitle"));
		json.put("charttype", "rcloumn");
		return json;
	}

	/**
	 * 生成TopN图形数据(指定设备列表) HGC's code
	 * @param devId
	 * @param kpiRow
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @return
	 */
	public JSONObject generateTopNChartData02(String devIds, String eleIds, DataRow kpiRow, String startTime, 
				String endTime, int topCount, String devType, double threvalue) {
		JSONObject json = new JSONObject();
		JSONArray names = new JSONArray();
		JSONArray array = new JSONArray();
		//获取TopN数据
		DecimalFormat decFmt = new DecimalFormat("0.00");
		List<DataRow> data = getTopnData02(devIds, eleIds, kpiRow, startTime, endTime, topCount, devType);
		DataRow dataRow;
		double t;
		for(int i = 0, size = data.size(); i < size; ++i){
			dataRow = data.get(i);
			names.add(dataRow.getString("ele_name"));
			JSONObject obj = new JSONObject();
			obj.put("time", dataRow.getString("prf_timestamp"));
			obj.put("devId", dataRow.getString("dev_id"));
			obj.put("eleId", dataRow.getString("ele_id"));
			obj.put("compId", dataRow.getString("comp_id"));
			obj.put("dbtype", dataRow.getString("dbtype"));
			t = Double.parseDouble(decFmt.format(dataRow.getDouble("kpi"))); // threvalue
			obj.put("y", t);
			if(t > threvalue){
				obj.put("dataLabels","{style: {fontWeight:'bold',color: 'red'}}");
			}
			array.add(obj);
		}
		json.put("names", names);
		json.put("data", array);
		return json;
	}
	
	public void createAndSendCSVFile(HttpServletResponse response, List<DataRow> kpis,
			List<DataRow> tbody, List<DataRow> devs){
		try {
			List<String> theadKey = new ArrayList<String>(kpis == null ? 2
					: kpis.size() + 2);
			List<String> theadTitle = new ArrayList<String>(theadKey.size());
			theadKey.add("ele_name");
			theadTitle.add("设备名");
			String temp;
			for (DataRow r : kpis) {
				temp = r.getString("fid");
				if (temp != null) {
					theadKey.add(temp.toLowerCase());
				}
				temp = r.getString("ftitle");
				if (temp != null) {
					theadTitle.add(temp.concat("(" + r.getString("funits") + ")"));
				}
			}
			theadKey.add("prf_timestamp");
			theadTitle.add("时间");
			
			response.setCharacterEncoding("GBK");
			if (tbody != null && tbody.size() > 0) {
				String[] title = new String[theadTitle.size()];
				theadTitle.toArray(title);
				String[] key = new String[theadKey.size()];
				theadKey.toArray(key);
				String time;
				for (int i = 0; i < tbody.size(); i++) {
					time = tbody.get(i).getString("prf_timestamp");
					tbody.get(i).set("prf_timestamp",
							time == null ? null : time.replace("-", "/"));
				}
				CSVHelper.createCSVToPrintWriter(response, devs.get(0)
						.getString("ele_name").concat("_Perf_Data"), tbody,
						title, key);
			} 
			else {
				CSVHelper.createCSVToPrintWriter(response, 
						((devs != null && devs.size() > 0)? 
								devs.get(0).getString("ele_name") : "Unknown").concat("_Perf_Data"),
						new ArrayList<DataRow>(), new String[] { "暂无数据导出" },
						new String[] {});
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(getClass()).error("", e);
		}
	}
	
	/**
	 * 生成TopN图形数据(指定设备列表) HGC's code
	 * @param eleIds
	 * @param kpiRow
	 * @param startTime
	 * @param endTime
	 * @param topCount   TOPN中的N，即是一台设备性能数据的前N条
	 * @param devType    这台设备属于什么类型
	 * @return
	 */
	public JSONObject generateTopNChartData02(String devIds, String eleIds, DataRow kpiRow, String startTime, 
				String endTime, int topCount, String devType) {
		JSONObject json = new JSONObject();
		JSONArray names = new JSONArray();
		JSONArray array = new JSONArray();
		//获取TopN数据
		DecimalFormat decFmt = new DecimalFormat("#.##");
		List<DataRow> data = getTopnData02(devIds, eleIds, kpiRow, startTime, endTime, topCount, devType);
		DataRow dataRow;
		for(int i = 0, size = data.size(); i < size; ++i){
			dataRow = data.get(i);
			names.add(dataRow.getString("ele_name"));
			JSONObject obj = new JSONObject();
			obj.put("time", dataRow.getString("prf_timestamp"));
			obj.put("devId", dataRow.getString("dev_id"));
			obj.put("eleId", dataRow.getString("ele_id"));
			obj.put("compId", dataRow.getString("comp_id"));
			obj.put("dbtype", dataRow.getString("dbtype"));
			obj.put("y", Double.parseDouble(decFmt.format(dataRow.getDouble("kpi"))));
			array.add(obj);
		}
		if(array.size() > 0){
			array.getJSONObject(0).put("dataLabels","{style: {fontWeight:'bold',color: 'red'}}");
		}
		json.put("names", names);
		json.put("data", array);
		return json;
	}
	
	/**
	 * 获取所有设备的TopN数据
	 * @param devId
	 * @param kpiRow
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getTopnData(DataRow kpiRow,String startTime,String endTime,int topCount){
		StringBuffer sb = new StringBuffer();
		String viewName = kpiRow.getString("fprfview").trim();
		String dbType = kpiRow.getString("fdbtype").trim();
		String kpi = kpiRow.getString("fid").trim();
		//判断查哪个视图
		if (startTime != null && startTime.length() > 0) {
			Date start = null;
			Date end = null;
			Long overTimes = null;
			try {
				start = df.parse(startTime);
				if (endTime != null && endTime.length() > 0) {
					end = df.parse(endTime);
					overTimes = end.getTime();
				} else {
					overTimes = new Date().getTime();
				}
				Long lastTime = start.getTime();
				Long tem = 1000l;
				if (overTimes - lastTime > 30 * 24 * 60 * 60 * tem) {
					viewName += "_daily";
				} else if (overTimes - lastTime > 7 * 24 * 60 * 60 * tem) {
					viewName += "_hourly";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		sb.append("select '' as prf_timestamp,dev_id,ele_id,ele_name,");
		sb.append("avg(" + kpi + ") as kpi");
		sb.append(" from " + viewName);
		sb.append(" where prf_timestamp >= ?");
		sb.append(" and prf_timestamp <= ?");
		sb.append(" group by ele_id,ele_name,dev_id");
		sb.append(" order by avg(" + kpi + ") desc");
		//判断到哪个数据库查询视图
		if (StringHelper.isNotEmpty(dbType) && StringHelper.isNotBlank(dbType)) {
			if (dbType.equals(SrContant.DBTYPE_SR)) {
				return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),new Object[]{startTime,endTime},topCount);
			} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
				return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),new Object[]{startTime,endTime},topCount);
			}
		}
		return null;
	}
	
	/**
	 * 生成TopN图形数据(指定设备)
	 * @param devId
	 * @param kpiRow
	 * @param startTime
	 * @param endTime
	 * @param topCount
	 * @return
	 */
	public JSONObject generateTopNChartData(DataRow kpiRow, String startTime,
			String endTime, int topCount) {
		JSONObject json = new JSONObject();
		JSONArray names = new JSONArray();
		JSONArray array = new JSONArray();
		//获取TopN数据
		List<DataRow> rows = getTopnData(kpiRow, startTime, endTime, topCount);

		DecimalFormat decFmt = new DecimalFormat("#.##");
		for (DataRow dataRow : rows) {
			names.add(dataRow.getString("ele_name"));
			JSONObject obj = new JSONObject();
			//添加样式
			obj.put("time", dataRow.getString("prf_timestamp"));
			obj.put("y", Double.parseDouble(decFmt.format(dataRow.getDouble("kpi"))));
			//topN跳转
			obj.put("devid", dataRow.getString("dev_id"));
			obj.put("eleid", dataRow.getString("ele_id"));
			obj.put("compId", dataRow.getString("comp_id"));
			obj.put("dbtype", dataRow.getString("dbtype"));
			//
			array.add(obj);
		}
		if(array.size() > 0){
			array.getJSONObject(0).put("dataLabels","{style: {fontWeight:'bold',color: 'red'}}");
		}
		json.put("names", names);
		json.put("data", array);
		return json;
	}
	
	public List<DataRow> getSVCAvePrfData(String startTime,String endTime){
		String sql = "select avg(a63) as a63,avg(a47) as a47,avg(a38) as a38,avg(a40) as a40,avg(a39) as a39 ,avg(a75) as a75,ele_name from prf_target_svc_system where prf_timestamp > ? and prf_timestamp < ? group by ele_name";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,new Object[]{startTime,endTime});
	}
	
	public List<DataRow> getDSAvePrfData(String startTime,String endTime){
		String sql = "select avg(a236) as a236,avg(a254) as a254,avg(a227) as a227,avg(a224) as a224,avg(a263) as a263 ,avg(a266) as a266,ele_name from prf_target_ds_system where prf_timestamp > ? and prf_timestamp < ? group by ele_name";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,new Object[]{startTime,endTime});
	}
	
	/**
	 * 计算某个性能指标在某个时间段超过设置的阀值的次数并返回相应信息
	 * @param rows
	 * @param kpis
	 * @param startTime
	 * @param endTime
	 * @param alertValue
	 * @return
	 */
	public String computePerfAlertCount(List<DataRow> rows,List<DataRow> kpis,String startTime,String endTime,Integer alertValue) {
		StringBuffer result = new StringBuffer("");
		//获取性能数据
		List<DataRow> perfData = getPrfDatas(rows, kpis, startTime, endTime);
		if (perfData != null && perfData.size() > 0) {
			DataRow kpiRow = kpis.get(0);
			String kpi = kpiRow.getString("fid").toLowerCase();
			String kpiTitle = kpiRow.getString("ftitle");
			//分别代表下面四个百分比层次
			String level1Dev = null,level2Dev = null,level3Dev = null,level4Dev = null;
			for (DataRow devRow : rows) {
				//参数设备列表中的设备ID
				int eleId_1 = devRow.getInt("ele_id");
				String eleName_1 = devRow.getString("ele_name");
				int totalCount = 0;
				int alertCount = 0;
				for (int i = 0; i < perfData.size(); i++) {
					DataRow row = perfData.get(i);
					//返回性能数据中的设备ID
					int eleId_2 = row.getInt("ele_id");
					if (eleId_1 == eleId_2) {
						totalCount = totalCount + 1;
						if (row.getDouble(kpi) > alertValue) {
							alertCount = alertCount + 1;
						}
					}
				}
				//计算百分比
				double percent = (new Double(alertCount)/new Double(totalCount));
				//保存相同告警层次的设备
				//“较为繁忙”级别
				if (percent > 0.80) {
					if (level1Dev != null && level1Dev.length() > 1) {
						level1Dev = level1Dev + "," + eleName_1;
					} else {
						level1Dev = eleName_1;
					}
				//“繁忙”级别
				} else if (percent > 0.40) {
					if (level2Dev != null && level2Dev.length() > 1) {
						level2Dev = level2Dev + "," + eleName_1;
					} else {
						level2Dev = eleName_1;
					}
				//“正常”级别
				} else if (percent > 0) {
					if (level3Dev != null && level3Dev.length() > 1) {
						level3Dev = level3Dev + "," + eleName_1;
					} else {
						level3Dev = eleName_1;
					}
				//“良好”级别
				} else {
					if (level4Dev != null && level4Dev.length() > 1) {
						level4Dev = level4Dev + "," + eleName_1;
					} else {
						level4Dev = eleName_1;
					}
				}
			}
			//拼接提示信息
			if (level1Dev != null && level1Dev.length() > 1) {
				result.append(level1Dev).append("在该时间段内,性能指标：").append(kpiTitle).append("数值超过性能阀值告警值次数过多,整体系统较为繁忙;");
			} else if (level2Dev != null && level2Dev.length() > 1) {
				if (result.toString().indexOf(";") > -1) {
					result.append("</br>");
				}
				result.append(level2Dev).append("在该时间段内,性能指标：").append(kpiTitle).append("数值超过性能阀值告警值次数偏多,整体系统繁忙;");
			} else if (level3Dev != null && level3Dev.length() > 1) {
				if (result.toString().indexOf(";") > -1) {
					result.append("</br>");
				}
				result.append(level3Dev).append("在该时间段内,性能指标：").append(kpiTitle).append("数值超过性能阀值告警值次数属正常范围,整体运行状态正常;");
			} else if (level4Dev != null && level4Dev.length() > 1) {
				if (result.toString().indexOf(";") > -1) {
					result.append("</br>");
				}
				result.append(level4Dev).append("在该时间段内,性能指标：").append(kpiTitle).append("数值均未达到性能阀值告警值,整体运行状态良好;");
			}
		}
		return result.toString();
	}
}
