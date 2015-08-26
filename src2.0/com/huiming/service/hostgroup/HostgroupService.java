package com.huiming.service.hostgroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.project.web.WebConstants;

public class HostgroupService extends BaseService{
	//添加
	public void addHostgroup(List<DataRow> hostgroups, Integer subsystemID){
		for (DataRow dataRow : hostgroups) {
			dataRow.set("subsystem_id", subsystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_hostgroup", dataRow);
		}
	}
	//更新
	public void updateHostgroup(List<DataRow> hostgroups, Integer subsystemID){
		for (DataRow dataRow : hostgroups) {
			dataRow.set("subsystem_id", subsystemID);
			String sql="select hostgroup_name from t_res_hostgroup where hostgroup_name = '"+dataRow.getString("hostgroup_name")+"' and subsystem_id = "+subsystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size() > 0){
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_hostgroup", dataRow, "hostgroup_id", row.getInt("hostgroup_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_hostgroup", dataRow);
			}
		}
	}
	
	//查
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostgroupList(Integer subsystemId){
		String sql = "SELECT h.*,b.server_ip_address,b.server_name,s.model " +
				"FROM t_map_hostgroupandhba m RIGHT JOIN t_res_hostgroup h ON h.hostgroup_id = m.hostgroup_id " +
				"LEFT JOIN t_res_storagehba b ON b.hba_id = m.hba_id " +
				"INNER JOIN t_res_storagesubsystem s ON s.subsystem_id = h.subsystem_id " +
				"where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and h.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY b.server_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	//根据hostgroupName查信息
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostgroupByName(String name,Integer subsystemId){
		String sql = "select * from t_res_hostgroup where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and hostgroup_name = ? ");
			args.add(name);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	//分页查
	public DBPage getHostgroupPage(String name,Integer subsystemId,Integer currentPage,Integer numPerPage){
		String sql = "select h.*,s.model from t_res_hostgroup h, t_res_storagesubsystem s where h.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null &&name.length()>0){
			sb.append("and h.hostgroup_name like ? ");
			args.add("%" + name + "%");
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and h.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), currentPage, numPerPage);
	}
	
	/**
	 * 导出配置信息
	 * @param name
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> exportConfigInfo(String name,Integer subsystemId){
		String sql = "select h.*,s.model from t_res_hostgroup h, t_res_storagesubsystem s where h.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null &&name.length()>0){
			sb.append("and h.hostgroup_name like ? ");
			args.add("%" + name + "%");
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and h.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 存储关系组详细信息 ，带主机信息
	 * (该方法实用有待考证)
	 * @param subsystemId
	 * @param hostgroupId
	 * @return
	 */
	public DataRow getHostgroupInfo(Integer subsystemId, Integer hostgroupId) {
		String sql = "SELECT h.*,b.server_ip_address,b.server_name,s.model,s.storage_type "
				+ "FROM t_map_hostgroupandhba m RIGHT JOIN t_res_hostgroup h ON h.hostgroup_id = m.hostgroup_id "
				+ "LEFT JOIN t_res_storagehba b ON b.hba_id = m.hba_id "
				+ "INNER JOIN t_res_storagesubsystem s ON s.subsystem_id = h.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (subsystemId != null && subsystemId > 0) {
			sb.append("AND h.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if (hostgroupId != null && hostgroupId > 0) {
			sb.append("AND h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		sb.append("GROUP BY b.server_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
	
	/**
	 * 获取存储关系组下卷列表信息(分页)
	 * @param hostgroupId
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getHostgroupVolumeInfo(Integer hostgroupId,Integer curPage,Integer numPerPage){
		String sql="SELECT v.* FROM t_map_hostgroupandvolume m,t_res_storagevolume v,t_res_hostgroup h " 
			+ "WHERE h.hostgroup_id = m.hostgroup_id AND v.volume_id = m.volume_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (hostgroupId != null && hostgroupId > 0) {
			sb.append("and h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取存储关系组下卷列表信息
	 * @param hostgroupId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostgroupVolumeInfo(Integer hostgroupId){
		String sql="SELECT v.* FROM t_map_hostgroupandvolume m,t_res_storagevolume v,t_res_hostgroup h " +
		"WHERE h.hostgroup_id = m.hostgroup_id AND v.volume_id = m.volume_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (hostgroupId != null && hostgroupId > 0) {
			sb.append("and h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 得到IOps Top10信息
	 * @param subsystemId
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getIOpsTop10PrefInfo(Integer subsystemID,String startTime,String endTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select dev_id as subsystem_id,ele_id as hostgroup_id,ele_name as hostgroup_name,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("AVG(A102_03) as avg_iops from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("AVG(A108_03) as avg_iops from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("AVG(A114_03) as avg_iops from ");
		}
		
		sb.append(viewName + " where 1 = 1");
		if (subsystemID != null && subsystemID != 0) {
			sb.append(" and dev_id = ?");
			args.add(subsystemID);
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
		sb.append(" group by hostgroup_id order by avg_iops desc");
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.TOP_LIMIT_COUNT);
		}
		return null;
	}
	
	/**
	 * 获取主机组性能信息
	 * @param subsystemID
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostgroupPerfInfo(Integer subsystemID,String startTime,String endTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select ele_id as hostgroup_id,ele_name as hostgroup_name,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("AVG(A102_03) as avg_iops,MAX(A102_03) as max_iops,");
			sb.append("MAX(A102_06) as max_restime,AVG(A102_06) as avg_restime,");
			sb.append("AVG(A102_09*1024) as avg_kbps,MAX(A102_09*1024) as max_kbps from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("AVG(A108_03) as avg_iops,MAX(A108_03) as max_iops,");
			sb.append("MAX(A108_06) as max_restime,AVG(A108_06) as avg_restime,");
			sb.append("AVG(A108_09*1024) as avg_kbps,MAX(A108_09*1024) as max_kbps from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("AVG(A114_03) as avg_iops,MAX(A114_03) as max_iops,");
			sb.append("MAX(A114_06) as max_restime,AVG(A114_06) as avg_restime,");
			sb.append("AVG(A114_09*1024) as avg_kbps,MAX(A114_09*1024) as max_kbps from ");
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
				// 大于20天查天性能数据
				if (overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_DAILY);
					// 大于2天查小时性能数据
				} else if (overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_HOURLY);
					// 否则查实时性能数据
				} else {
					sb.append(viewName);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// 查实时性能数据
		} else {
			sb.append(viewName);
		}
		sb.append(" where 1 = 1");
		if (subsystemID != null && subsystemID != 0) {
			sb.append(" and dev_id = ?");
			args.add(subsystemID);
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
		sb.append(" group by hostgroup_id order by max_iops desc");
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_DATA_COUNT);
		}
		return null;
	}
	
	/**
	 * 获取相应的主机组性能信息
	 * @param subsystemID
	 * @param hostgroupId
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPerHostgroupPerfInfo(Integer subsystemID,Integer hostgroupId,String startTime,String endTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select dev_id,ele_id,ele_name,prf_timestamp,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("A102_01 as read_iops,A102_02 as write_iops,A102_03 as total_iops,");
			sb.append("A102_04 as read_res_time,A102_05 as write_res_time,A102_06 as total_res_time,");
			sb.append("(A102_07*1024) as read_kbps,(A102_08*1024) as write_kbps,(A102_09*1024) as total_kbps from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("A108_01 as read_iops,A108_02 as write_iops,A108_03 as total_iops,");
			sb.append("A108_04 as read_res_time,A108_05 as write_res_time,A108_06 as total_res_time,");
			sb.append("(A108_07*1024) as read_kbps,(A108_08*1024) as write_kbps,(A108_09*1024) as total_kbps from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("A114_01 as read_iops,A114_02 as write_iops,A114_03 as total_iops,");
			sb.append("A114_04 as read_res_time,A114_05 as write_res_time,A114_06 as total_res_time,");
			sb.append("(A114_07*1024) as read_kbps,(A114_08*1024) as write_kbps,(A114_09*1024) as total_kbps from ");
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
		if (subsystemID != null && subsystemID != 0) {
			sb.append(" and dev_id = ?");
			args.add(subsystemID);
		}
		if (hostgroupId != null && hostgroupId != 0) {
			sb.append(" and ele_id = ?");
			args.add(hostgroupId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and prf_timestamp >= ?");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append(" and prf_timestamp <= ?");
			args.add(endTime);
		}
		// 默认查询最近一个月的数据
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
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
}
