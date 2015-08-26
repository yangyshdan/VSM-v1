package com.huiming.service.switchs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class SwitchService extends BaseService {
	/**
	 * 分页获取交换机列表
	 * @param curPage
	 * @param numPerPage
	 * @param name
	 * @param ipAddress
	 * @param status
	 * @param serialNumber
	 * @param limitIds
	 * @return
	 */
	public DBPage getSwitchPage(int curPage,int numPerPage,String name,String ipAddress,String status,String serialNumber,String limitIds){
		String sql="select s.*,m.model_name,v.vendor_name,f.the_display_name as fabric_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v ,v_res_fabric f " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id and s.the_fabric_id = f.fabric_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and s.switch_id in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sb.append("and s.the_display_name like ? ");
			args.add("%" + name + "%");
		}	
		if (ipAddress != null && ipAddress.length() > 0) {
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if (status != null && status.length() > 0) {
			sb.append("and s.the_propagated_status = ? ");
			args.add(status);
		}
		if (serialNumber != null && serialNumber.length() > 0) {
			sb.append("and s.serial_number = ? ");
			args.add(serialNumber);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList(String name,String ipAddress,String status,String serialNumber,String limitIds){
		String sql = "select s.switch_id, s.the_display_name,s.the_propagated_status,s.domain,s.ip_address," +
				"s.switch_wwn,s.the_backend_name,s.serial_number,s.description,TO_CHAR(s.update_timestamp,'YYYY/MM/DD HH24:MI:SS') AS update_timestamp," +
				"m.model_name,v.vendor_name " +
				"from v_res_switch s,v_res_model m,v_res_vendor v " +
				"where s.model_id = m.model_id " +
				"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and s.switch_id in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sb.append("and s.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (ipAddress != null && ipAddress.length() > 0) {
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if (status != null && status.length() > 0) {
			sb.append("and s.the_propagated_status = ? ");
			args.add(status);
		}
		if (serialNumber != null && serialNumber.length() > 0) {
			sb.append("and s.serial_number = ? ");
			args.add(serialNumber);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getSwitchInfo(Integer switchId){
		String sql="select s.*,m.model_name,v.vendor_name,f.the_display_name as fabric_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v ,v_res_fabric f " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id and s.the_fabric_id = f.fabric_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(switchId!=0 && switchId>0){
			sb.append("and s.switch_id = ? ");
			args.add(switchId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString(),args.toArray());
	}
	
	public DataRow getSwitchStatus(Integer switchId){
		String sql="SELECT MAX(id),engine_status,power_status,port_status,fiber_status," +
				" switch_id FROM t_res_switch WHERE switch_id=?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{switchId});
	}

	public List<DataRow> getSwitchInfoList(String limitIds) {
		String sql = "select s.switch_id as id,s.the_display_name as name from v_res_switch s,v_res_model m,v_res_vendor v ,v_res_fabric f " +
		"where s.model_id = m.model_id and s.vendor_id = v.vendor_id and s.the_fabric_id = f.fabric_id ";
		if (limitIds != null && limitIds.length() > 0) {
			sql = sql + "and s.switch_id in (" + limitIds + ")";
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getResSwitchList(String limitIds){
		String sql = "select * from t_res_switch where 1 = 1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and switch_id in (" + limitIds + ") ");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	public DataRow getResSwitchInfo(Integer switchId){
		String sql="SELECT * FROM t_res_switch where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(switchId!=0 && switchId>0){
			sb.append("and switch_id = ? ");
			args.add(switchId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
	
	//所有事件()
	@SuppressWarnings("unchecked")
	public List<DataRow> getIncident(String startTime, String endTime){
		String sql = "SELECT FTopId,COUNT(FCount) FROM v_devicelog where FFirstTime='"+startTime+"' and FlastTime='"+endTime+"' GROUP BY FTopId ORDER BY COUNT(FCount) DESC ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * @see 交换机的TOP事件
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getSwitchEventTOP5(String startTime, String endTime, String limitIds) {
		String devIdStr = "";
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			devIdStr = "and fresourceid in (" + limitIds + ")";
		}
		String sql = String.format("SELECT FDescript,fcount,fresourceid as eleId FROM(SELECT FDescript,fresourceid,COUNT(FCount) AS fcount FROM " +
			" (SELECT * FROM v_devicelog WHERE FFirstTime>=timestamp('%s') AND FlastTime<=timestamp('%s') " +
			" %s AND fresourcetype='%s' %s) t GROUP BY fresourceid,FDescript) t ORDER BY FCount DESC LIMIT 0,5",
			// AND fstate=0
			startTime, endTime, "AND fstate=0 and fisdelete=0", SrContant.SUBDEVTYPE_SWITCH, devIdStr);
		List<DataRow> data = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		JSONObject json = new JSONObject();
		json.put("smallTitle", startTime + " ~ " + endTime);
		json.put("funits", "次");
		json.put("ftitle", "交换机事件TOP5");
		json.put("charttype", "rcloumn");
		JSONArray names = new JSONArray();
		JSONArray array = new JSONArray();
		if(data != null && data.size() > 0){
			//获取TopN数据
			DataRow dataRow;
			for(int i = 0, size = data.size(); i < size; ++i){
				dataRow = data.get(i);
				names.add(dataRow.getString("fdescript"));
				JSONObject obj = new JSONObject();
				obj.put("y", dataRow.getInt("fcount"));
				obj.put("eleId", dataRow.getLong("eleid"));
				array.add(obj);
			}
			if(array.size() > 0){
				array.getJSONObject(0).put("dataLabels","{style: {fontWeight:'bold',color: 'red'}}");
			}
		}
		json.put("names", names);
		json.put("data", array);
		return json;
		 
			
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getSwitchEventDistr(String startTime, String endTime, String limitIds){
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		//指定的设备ids
		String devIdStr = "";
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			devIdStr = "and fresourceid in (" + limitIds + ")";
		}
		String fstate = " AND fstate=0 and fisdelete=0 "; // AND fstate=0
		long eventCount = srDB.queryLong(String.format(
				"SELECT count(1) FROM v_devicelog WHERE FFirstTime>=timestamp('%s') AND FlastTime<=timestamp('%s')" +
				" %s AND fresourcetype='%s' %s", startTime, endTime, fstate, SrContant.SUBDEVTYPE_SWITCH, devIdStr));
		
		String sql = String.format("SELECT ftopname,fcount as fc,fresourceid as rid FROM(SELECT ftopname,fresourceid,COUNT(FCount) AS fcount "+
				"FROM (SELECT * FROM v_devicelog WHERE FFirstTime>=timestamp('%s') AND FlastTime<=timestamp('%s')" +
				" %s AND fresourcetype='%s' %s) t GROUP BY fresourceid,ftopname) t ORDER BY FCount DESC",
				startTime, endTime, fstate, SrContant.SUBDEVTYPE_SWITCH, devIdStr);
		
		JSONObject json = new JSONObject();
		json.put("type", "pie");
		json.put("name", "事件分布占比(%s)");
		
		DecimalFormat decFmt = new DecimalFormat("#.##");
		List<DataRow> data = srDB.query(sql);
		JSONArray array = new JSONArray();
		if(data != null && data.size() > 0){
			long t;
			double c = eventCount * 1.0, p;
			for(DataRow dr : data){
				t = dr.getLong("fc");
				JSONObject obj = new JSONObject();
				obj.put("name", dr.getString("ftopname"));
				p = Double.parseDouble(decFmt.format(t / c));
				obj.put("y", p);
				obj.put("times", t);
				obj.put("p", p * 100);
				obj.put("rid", dr.getLong("rid"));
				array.add(obj);
				/*
				 * new Object[]{
					dr.getString("ftopname"),
					Double.parseDouble(decFmt.format(t / c)),
					t
				}
				 */
				
			}
		}
		JSONArray series = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("data", array);
		obj.put("name", "告警次数");
		obj.put("type", "pie");
		
		series.add(obj);
		json.put("series", series);
		return json;
	}
}
