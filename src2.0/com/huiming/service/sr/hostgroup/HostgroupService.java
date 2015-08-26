package com.huiming.service.sr.hostgroup;

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
		String sql = "select h.*,s.model "
			+ "from t_res_hostgroup h, t_res_storagesubsystem s "
			+ "where h.subsystem_id = s.subsystem_id ";
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
		String sql = "select h.*,s.model "
			+ "from t_res_hostgroup h, t_res_storagesubsystem s "
			+ "where h.subsystem_id = s.subsystem_id ";
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
	 * 存储关系组性能信息
	 * @param subsystemId
	 * @param hostgroupId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostgroupPrfInfo(Integer subsystemId,Integer hostgroupId,String startTime,String endTime){
		String sql="SELECT SUM(read_io) AS read_io,SUM(write_io) AS write_io,SUM(read_kb) AS read_kb,SUM(write_kb) AS write_kb,SUM(read_hit_io) AS read_hit_io,SUM(read_io_time) AS read_io_time,SUM(write_io_time) AS write_io_time,t.interlva_len,t.sampl_time ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
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
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM t_prf_storagevolume_perday p,t_prf_timestamp3 t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour p,t_prf_timestamp2 t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
				}else{
					sb.append("FROM t_prf_storagevolume p,t_prf_timestamp t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_storagevolume p,t_prf_timestamp t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
		}
		sb.append("WHERE p.time_id = t.time_id AND h.hostgroup_id = m.hostgroup_id AND p.volume_id = m.volume_id ");
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(hostgroupId!=null && hostgroupId!=0){
			sb.append("and h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一天的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY p.time_id ORDER BY t.sampl_time");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),200);
	}
	
	/**
	 * 存储关系组分页性能信息
	 * @param subsystemId
	 * @param hostgroupId
	 * @param startTime
	 * @param endTime
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getHostgroupPrfPage(Integer subsystemId,Integer hostgroupId,String startTime,String endTime,Integer curPage,Integer numPerPage){
		String sql="SELECT SUM(read_io) AS read_io,SUM(write_io) AS write_io,SUM(read_kb) AS read_kb,SUM(write_kb) AS write_kb,SUM(read_hit_io) AS read_hit_io,SUM(read_io_time) AS read_io_time,SUM(write_io_time) AS write_io_time,t.interlva_len,t.sampl_time ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
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
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM t_prf_storagevolume_perday p,t_prf_timestamp3 t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour p,t_prf_timestamp2 t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
				}else{
					sb.append("FROM t_prf_storagevolume p,t_prf_timestamp t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_storagevolume p,t_prf_timestamp t,t_res_hostgroup h,t_map_hostgroupandvolume m ");
		}
		sb.append("WHERE p.time_id = t.time_id AND h.hostgroup_id = m.hostgroup_id AND p.volume_id = m.volume_id ");
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(hostgroupId!=null && hostgroupId!=0){
			sb.append("and h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一天的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY p.time_id ORDER BY t.sampl_time");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 存储关系组下控制器的性能信息
	 * @param subsystemId
	 * @param hostgroupId
	 * @param spId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostgroupSPprfInfo(Integer hostgroupId,Integer spId,String startTime,String endTime){
		String sql="SELECT SUM(read_io) AS read_io,SUM(write_io) AS write_io,SUM(read_kb) AS read_kb,SUM(write_kb) AS write_kb,SUM(read_hit_io) AS read_hit_io,SUM(read_io_time) AS read_io_time,SUM(write_io_time) AS write_io_time,t.interlva_len,t.sampl_time ";
		StringBuffer sb = new StringBuffer(sql);
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
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM t_prf_storagevolume_perday p,t_prf_timestamp3 t,t_res_hostgroup h,t_map_hostgroupandvolume m,t_res_storagevolume v ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour p,t_prf_timestamp2 t,t_res_hostgroup h,t_map_hostgroupandvolume m,t_res_storagevolume v ");
				}else{
					sb.append("FROM t_prf_storagevolume p,t_prf_timestamp t,t_res_hostgroup h,t_map_hostgroupandvolume m,t_res_storagevolume v ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_storagevolume p,t_prf_timestamp t,t_res_hostgroup h,t_map_hostgroupandvolume m,t_res_storagevolume v ");
		}
		sb.append("WHERE p.time_id = t.time_id AND h.hostgroup_id = m.hostgroup_id AND p.volume_id = m.volume_id AND v.volume_id = p.volume_id ");
		List<Object> args = new ArrayList<Object>();
		if(hostgroupId!=null && hostgroupId>0){
			sb.append("and h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		if(spId!=null && spId>0){
			sb.append("and v.sp_id = ? ");
			args.add(spId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一天的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY p.time_id ORDER BY t.sampl_time");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),200);
	}
	
	/**
	 * 分页显示存储关系组下卷列表信息
	 * @param hostgroupId
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getHostgroupVolumeInfo(Integer hostgroupId,Integer curPage,Integer numPerPage){
		String sql="SELECT v.* FROM t_map_hostgroupandvolume m,t_res_storagevolume v,t_res_hostgroup h " +
				"WHERE h.hostgroup_id = m.hostgroup_id AND v.volume_id = m.volume_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(hostgroupId!=null && hostgroupId>0){
			sb.append("and h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostgroupVolumeInfo(Integer hostgroupId){
		String sql="SELECT v.* FROM t_map_hostgroupandvolume m,t_res_storagevolume v,t_res_hostgroup h " +
		"WHERE h.hostgroup_id = m.hostgroup_id AND v.volume_id = m.volume_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(hostgroupId!=null && hostgroupId>0){
			sb.append("and h.hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 得到IOps Top10信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getIOpsTop10PrefInfo(Integer subsystemId,String startTime,String endTime){
		String sql="SELECT hostgroup_id,hostgroup_name,subsystem_id,AVG((read_io+write_io)/interlva_len) AS avg_iops " +
				"FROM v_prf_storagegroup where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(subsystemId!=null && subsystemId>0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一天的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("and SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("group by hostgroup_id order by avg_iops desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),10);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> reportPrfData(Integer subsystemId,String startTime,String endTime){
		String sql="SELECT hostgroup_id,hostgroup_name ,MAX((read_io+write_io)/interlva_len) AS max_iops," +
				"AVG((read_io+write_io)/interlva_len) AS avg_iops," +
				"MAX((read_kb+write_kb)/interlva_len) AS max_kbps," +
				"AVG((write_kb+read_kb)/interlva_len) AS avg_kbps," +
				"MAX((read_io_time+write_io_time)/(read_io+write_io)) AS max_restime," +
				"AVG((read_io_time+write_io_time)/(read_io+write_io)) AS avg_restime ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
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
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM v_prf_storagegroup_perday v ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM v_prf_storagegroup_perhour v ");
				}else{
					sb.append("FROM v_prf_storagegroup v ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM v_prf_storagegroup v ");
		}
		sb.append("where 1=1 ");
		if(subsystemId!=null && subsystemId>0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一天的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("and SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY hostgroup_id ORDER BY max_iops DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_DATA_COUNT);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> reportPrfInfo(Integer subsystemId,Integer hostgroupId,String startTime,String endTime){
		StringBuffer sb = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
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
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("SELECT * FROM v_prf_storagegroup_perday where 1=1 ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("SELECT * FROM v_prf_storagegroup_perhour where 1=1 ");
				}else{
					sb.append("SELECT * FROM v_prf_storagegroup where 1=1 ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("SELECT * FROM v_prf_storagegroup where 1=1 ");
		}
		if(subsystemId!=null && subsystemId>0){
			sb.append("AND subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(hostgroupId!=null && hostgroupId>0){
			sb.append("AND hostgroup_id = ? ");
			args.add(hostgroupId);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一天的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("and SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("ORDER BY sampl_time");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),300);
	}
}
