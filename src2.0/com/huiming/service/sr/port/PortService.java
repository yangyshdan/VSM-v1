package com.huiming.service.sr.port;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.service.sr.node.NodeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.project.web.WebConstants;

/**
 * 端口
 * 
 * @author Administrator
 * 
 */
public class PortService extends BaseService {
	/**
	 * 端口分页信息
	 * 
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getPortList(int curPage, int numPerPage, String portName, String networkAddress, Long subSystemID) {
		String sql = "select d.*,s.name as 'sname' from t_res_port d, t_res_storagesubsystem s where d.subsystem_id = s.subsystem_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (portName != null && portName.length() > 0) {
			sb.append(" and d.name like ?");
			args.add("%" + portName + "%");
		}
		if (networkAddress != null && networkAddress.length() > 0) {
			sb.append(" and d.NETWORK_ADDRESS like ?");
			args.add("%" + networkAddress + "%");
		}
		if (subSystemID != null && subSystemID!= 0) {
			sb.append(" and d.SUBSYSTEM_ID = ?");
			args.add(subSystemID);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取端口详细信息
	 * @param portId
	 * @return
	 */
	public DataRow getPortInfo(Integer subsystemId, Integer portId) {
		String sql = "select d.*,s.model,s.name as 'sname',s.storage_type from t_res_port d, t_res_storagesubsystem s "
			+ "where d.subsystem_id = s.subsystem_id and d.subsystem_id = " + subsystemId
			+ " and d.port_id = " + portId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}

	/**
	 * 端口列表信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortInfos(Long subSystemID) {
		String sql = "select d.*,s.name as 'sname' from t_res_port d, t_res_storagesubsystem s "
				+ "where d.subsystem_id = s.subsystem_id and d.subsystem_id = " + subSystemID + " order by d.name";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,SrContant.DEFAULT_HISTOGRAM_COUNT);
	}
	
	//根据名字查端口信息
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortInfoByName(String name,Integer subsystemID){
		String sql = "select * from t_res_port where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and name = ? ");
			args.add(name);
		}
		if(subsystemID!=null && subsystemID!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemID);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 获取相应端口的性能信息
	 * @param subSystemID
	 * @param portID
	 * @param startTime
	 * @param overTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPerPortPerfInfo(Integer subSystemID,Integer portID,String startTime,String overTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		StringBuffer sb = new StringBuffer("select dev_id,ele_id,ele_name,prf_timestamp,");
		List<Object> args = new ArrayList<Object>();
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("A103_01 as send_iops,A103_02 as recv_iops,A103_03 as total_iops,A103_04 as send_res_time,A103_05 as recv_res_time,A103_06 as total_res_time from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("A109_01 as send_iops,A109_02 as recv_iops,A109_03 as total_iops,A109_04 as send_res_time,A109_05 as recv_res_time,A109_06 as total_res_time from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("A115_01 as send_iops,A115_02 as recv_iops,A115_03 as total_iops,A115_04 as send_res_time,A115_05 as recv_res_time,A115_06 as total_res_time from ");
		//For NETAPP
		} else if (storageType.equalsIgnoreCase(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			sb.append("NA102_01 as send_iops,NA102_02 as recv_iops,NA102_03 as total_iops,NA102_04 as send_res_time,NA102_05 as recv_res_time,NA102_06 as total_res_time from ");
		}
		
		if (startTime != null && startTime.length() > 0) {
			Date start = null;
			Date end = null;
			Long overTimes = null;
			try {
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
				if (overTime != null && overTime.length() > 0) {
					end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(overTime);
					overTimes = end.getTime();
				} else {
					overTimes = new Date().getTime();
				}
				Long lastTime = start.getTime();
				//大于20天查天性能数据
				if (overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_DAILY);
				//大于2天查小时性能数据
				} else if (overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_HOURLY);
				//否则查实时性能数据
				} else {
					sb.append(viewName);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		//默认查实时性能数据
		} else {
			sb.append(viewName);
		}
		sb.append(" where 1 = 1");
		if (subSystemID != null && subSystemID != 0) {
			sb.append(" and dev_id = ?");
			args.add(subSystemID);
		}
		if (portID != null && portID != 0) {
			sb.append(" and ele_id = ?");
			args.add(portID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and prf_timestamp >= ?");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sb.append(" and prf_timestamp <= ?");
			args.add(overTime);
		}
		// 默认查询最近一个月的数据
		if (startTime == null || startTime.length() == 0) {
			if (overTime == null || overTime.length() == 0) {
				sb.append(" and prf_timestamp >= ?");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_LINE_COUNT);
		}
		return null;
	}

	// 添加端口信息
	public void addPort(List<DataRow> ports,Integer subSystemID) {
		NodeService service = new NodeService();
		for (DataRow portRow:ports) {
			DataRow spRow = service.getNodeByName(portRow.getString("name").substring(0,4), subSystemID, null).get(0);
			portRow.set("sp_id", spRow.getString("sp_id"));
			portRow.set("subsystem_id",subSystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_port", portRow);
		}
	}
	//更新
	public void updatePortInfo(List<DataRow> ports,Integer subSystemID){
		NodeService service = new NodeService();
		for (DataRow dataRow : ports) {
//			DataRow spRow = service.getNodeByName(dataRow.getString("name").substring(0,4), subSystemID, null).get(0);
			List<DataRow> list = service.getNodeByName(dataRow.getString("node_name"), subSystemID, null);
			if (list.size() > 0) {
				DataRow spRow = list.get(0);
				dataRow.set("sp_id", spRow.getString("sp_id"));
			}
			dataRow.set("subsystem_id",subSystemID);
			dataRow.remove("node_name");
			String sql = "select port_id from t_res_port where name = '"+dataRow.getString("name")+"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size()>0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_port", dataRow, "port_id", row.getString("port_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_port", dataRow);
			}
		}
	}

	// 添加端口性能
	public void addprfPort(List<DataRow> prfports,Integer subSystemID) {
		for (int i = 0; i < prfports.size(); ++i) {
			DataRow prfport = prfports.get(i);
			prfport.set("TIME_ID", SrContant.TIME_FKEY);
			String portname = (String) prfport.get("port_name");
			List<DataRow> rows = getPortByName(portname,subSystemID);
			String portId = rows.get(0).getString("port_id");
			prfport.set("port_id", portId);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_PRF_PORT", prfport);
		}
	}

	// 查询端口配置报表数据
	@SuppressWarnings("unchecked")
	public List<DataRow> reportPort(Integer subSystemID) {
		String sql = "SELECT name,network_address,port_speed,link_status FROM t_res_port where subsystem_id = "+subSystemID;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}

	/**
	 * 获取端口性能信息
	 * @param subSystemID
	 * @param portId
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortPerfInfo(Integer subSystemID,Integer portId,String startTime,String endTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		StringBuffer sb = new StringBuffer("select ele_id as port_id,ele_name as port_name,");
		List<Object> args = new ArrayList<Object>();
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("AVG(A103_03) as avg_iops,MAX(A103_03) as max_iops,AVG(A103_09*1024) as avg_kbps,MAX(A103_09*1024) as max_kbps from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("AVG(A109_03) as avg_iops,MAX(A109_03) as max_iops,AVG(A109_09*1024) as avg_kbps,MAX(A109_09*1024) as max_kbps from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("AVG(A115_03) as avg_iops,MAX(A115_03) as max_iops,AVG(A115_09*1024) as avg_kbps,MAX(A115_09*1024) as max_kbps from ");
		//For NETAPP
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("AVG(NA102_03) as avg_iops,MAX(NA102_03) as max_iops,AVG(NA102_09*1024) as avg_kbps,MAX(NA102_09*1024) as max_kbps from ");
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
				//大于20天查天性能数据
				if (overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_DAILY);
				//大于2天查小时性能数据
				} else if (overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_HOURLY);
				//否则查实时性能数据
				} else {
					sb.append(viewName);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		//默认查实时性能数据
		} else {
			sb.append(viewName);
		}
		sb.append(" where 1 = 1");
		if (subSystemID != null && subSystemID != 0) {
			sb.append(" and dev_id = ?");
			args.add(subSystemID);
		}
		if (portId != null && portId != 0) {
			sb.append(" and ele_id = ?");
			args.add(portId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and prf_timestamp >= ?");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append(" and prf_timestamp <= ?");
			args.add(endTime);
		}
		// 默认查询最近一天的数据
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
				sb.append(" and prf_timestamp >= ?");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append(" group by port_id order by max_iops desc");
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_DATA_COUNT);
		}
		return null;
	}
	
	public DataRow getPortIopsAndSpeed(String inTime,String portName){
		String sql="SELECT MAX(the_kb/the_time) AS maxspeed,AVG(the_kb/the_time) AS avgspeed,MAX(send_time) AS maxst,AVG(send_time)"
			+" FROM t_prf_port WHERE time_id IN("+inTime+") AND port_name='"+portName+"'";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortByName(String name,Integer subSystemID){
		String sql="select p.* from t_res_port p where p.name='"+name+"' and p.SUBSYSTEM_ID = "+subSystemID+" order by p.UPDATE_TIMESTAMP desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> exportPortConfigData(String portName, String networkAddress, Long subSystemID) {
		String sql = "select d.*,s.name as 'sname' from t_res_port d, t_res_storagesubsystem s where d.subsystem_id = s.subsystem_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (portName != null && portName.length() > 0) {
			sb.append(" and d.name like ?");
			args.add("%" + portName + "%");
		}
		if (networkAddress != null && networkAddress.length() > 0) {
			sb.append(" and d.NETWORK_ADDRESS like ?");
			args.add("%" + networkAddress + "%");
		}
		if (subSystemID != null && subSystemID!= 0) {
			sb.append(" and d.SUBSYSTEM_ID = ?");
			args.add(subSystemID);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),500);
	}
	
	/**
	 * 实时性能信息到指定性能信息表中
	 * @param startTime
	 * @param endTime
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortPrefInfoByTime(String startTime,String endTime,Integer subsystemId){
		String sql="SELECT port_id,port_name," +
				"AVG(send_io) AS send_io," +
				"AVG(recv_io) AS recv_io," +
				"AVG(send_kb) AS send_kb," +
				"AVG(recv_kb) AS recv_kb," +
				"AVG(send_time) AS send_time," +
				"AVG(recv_time) AS recv_time," +
				"AVG(bndw_send_util) AS bndw_send_util," +
				"AVG(bndw_recv_util) AS bndw_recv_util," +
				"AVG(the_io) AS the_io," +
				"AVG(the_kb) AS the_kb," +
				"AVG(the_time) AS the_time," +
				"AVG(bndw_the_util) AS bndw_the_util " +
				"FROM t_prf_port p,t_prf_timestamp t " +
				"WHERE p.time_id = t.time_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPLE_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPLE_TIME <= ? ");
			args.add(endTime);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY port_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	/**
	 * 每天性能信息
	 * @param startTime
	 * @param endTime
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortPrefInfoPerhour(String startTime,String endTime,Integer subsystemId){
		String sql="SELECT port_id,port_name," +
				"AVG(send_io) AS send_io," +
				"AVG(recv_io) AS recv_io," +
				"AVG(send_kb) AS send_kb," +
				"AVG(recv_kb) AS recv_kb," +
				"AVG(send_time) AS send_time," +
				"AVG(recv_time) AS recv_time," +
				"AVG(bndw_send_util) AS bndw_send_util," +
				"AVG(bndw_recv_util) AS bndw_recv_util," +
				"AVG(the_io) AS the_io," +
				"AVG(the_kb) AS the_kb," +
				"AVG(the_time) AS the_time," +
				"AVG(bndw_the_util) AS bndw_the_util " +
				"FROM t_prf_port_perhour p,t_prf_timestamp2 t " +
				"WHERE p.time_id = t.time_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPLE_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPLE_TIME <= ? ");
			args.add(endTime);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY port_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	//添加
	public void addPortPrefInfo(Long timeId,List<DataRow> ports,String tableName){
		if(ports!=null && ports.size()>0){
			for (DataRow dataRow : ports) {
				dataRow.set("time_id", timeId);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert(tableName, dataRow);
			}
		}
	}
	
	/**
	 * 添加端口性能信息
	 * @param prfPorts
	 */
	public void addPrfPorts(List<DataRow> prfPorts) {
		for (int i = 0; i < prfPorts.size(); i++) {
			DataRow prfPort = prfPorts.get(i);
			String portName = prfPort.getString(SrTblColConstant.P_PORT_NAME);
			List<DataRow> rows = getPortByName(portName,Integer.parseInt(prfPort.getString(SrTblColConstant.TT_SUBSYSTEM_ID)));
			if (rows.size() > 0) {
				String portId = rows.get(0).getString("port_id");
				prfPort.set("port_id", portId);
				prfPort.remove(SrTblColConstant.TT_SUBSYSTEM_ID);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_port", prfPort);
			}
		}
	}
	
	/**
	 * 获取端口性能信息
	 * @param timeIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfPorts(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select port_id,port_name,SUM(send_io) as send_io,SUM(recv_io) as recv_io,");
		sb.append("SUM(send_kb) as send_kb,SUM(recv_kb) as recv_kb");
		sb.append(" from t_prf_port where time_id in (" + timeIds + ")");
		sb.append(" group by port_id,port_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加端口性能信息(小时/天)
	 * @param prfDiskGroups
	 */
	public void addPerHourAndDayPrfPorts(List<DataRow> prfPorts) {
		for (int i = 0; i < prfPorts.size(); i++) {
			DataRow row = prfPorts.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_port", row);
		}
	}
}
