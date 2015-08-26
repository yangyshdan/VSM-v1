package com.huiming.service.sr.diskgroup;

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

public class DiskgroupService extends BaseService {
	/**
	 * 磁盘组分页信息
	 * 
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	@SuppressWarnings("static-access")
	public DBPage getDiskgroupList(int currentPage, int numPerPage,
			String name, String raidLevel, Long subSystemID,String pool_id) {
		String sql = "select d.*,p.name as pname,s.name as sname "
				+ "from t_res_diskgroup d, t_res_storagesubsystem s,t_res_storagepool p "
				+ "where d.subsystem_id = s.subsystem_id and p.pool_id=d.pool_id";
		
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (name != null && name.length() > 0) {
			sb.append(" and d.name like ?");
			args.add("%" + name + "%");
		}
		if (raidLevel != null && raidLevel.length() > 0) {
			sb.append(" and d.raid_level like ?");
			args.add("%" + raidLevel + "%");
		}
		if (subSystemID != null && subSystemID.SIZE > 0) {
			sb.append(" and d.SUBSYSTEM_ID = ?");
			args.add(subSystemID);
		}
		if(pool_id!=null && pool_id.length()>0){
			sb.append(" and d.pool_id = ?");
			args.add(pool_id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), currentPage, numPerPage);
	}
	
	
	/**
	 * 磁盘组配置信息数据
	 * 
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public List<DataRow> getDiskgroupExportList(String name, String raidLevel, Long subSystemID,String pool_id) {
		String sql = "select d.*,p.name as pname,p.pool_id,s.name as sname "
			+ "from t_res_diskgroup d, t_res_storagesubsystem s,t_res_storagepool p "
			+ "where d.subsystem_id = s.subsystem_id and p.pool_id=d.pool_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (name != null && name.length() > 0) {
			sb.append(" and d.name like ?");
			args.add("%" + name + "%");
		}
		if (raidLevel != null && raidLevel.length() > 0) {
			sb.append(" and d.raid_level like ?");
			args.add("%" + raidLevel + "%");
		}
		if (subSystemID != null && subSystemID.SIZE > 0) {
			sb.append(" and d.SUBSYSTEM_ID = ?");
			args.add(subSystemID);
		}
		if(pool_id!=null && pool_id.length()>0){
			sb.append(" and d.pool_id = ?");
			args.add(pool_id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),500);
	}

	/**
	 * 磁盘组列表信息
	 * 
	 * @return
	 */
	@SuppressWarnings( { "unchecked" })
	public List<DataRow> getDiskgroupInfos(Long subSystemID) {
		String sql = "select d.*,s.name as 'sname' from t_res_diskgroup d, t_res_storagesubsystem s where d.subsystem_id = s.subsystem_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (subSystemID != null && subSystemID!= 0) {
			sb.append(" and d.SUBSYSTEM_ID = ?");
			args.add(subSystemID);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 获取磁盘组详细信息
	 * @param subsystemId
	 * @param diskgroupId
	 * @return
	 */
	public DataRow getDiskgroupInfo(Integer subsystemId, Integer diskgroupId) {
		String sql = "select d.*,s.model,s.name as 'sname',s.storage_type"
			+ " from t_res_diskgroup d, t_res_storagesubsystem s"
			+ " where d.subsystem_id = s.subsystem_id"
			+ " and d.subsystem_id = " + subsystemId
			+ " and d.diskgroup_id = " + diskgroupId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	//查找容量前20磁盘组
	@SuppressWarnings({ "unchecked", "static-access" })
	public List<DataRow> getDiskgroupDDMCapInfos(Long subSystemID) {
		String sql = "select d.*,p.TOTAL_USABLE_CAPACITY,p.UNALLOCATED_CAPACITY,s.name as 'sname' "
				+ "from t_res_diskgroup d, t_res_storagesubsystem s,t_res_storagepool p "
				+ "where d.subsystem_id = s.subsystem_id and d.pool_id = p.pool_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (subSystemID != null && subSystemID.SIZE > 0) {
			sb.append("and d.SUBSYSTEM_ID = ? ");
			args.add(subSystemID);
		}
		sb.append("order by d.ddm_cap desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.DEFAULT_HISTOGRAM_COUNT);
	}
	
	//Raid Group IOps top10
	@SuppressWarnings("unchecked")
	public List<DataRow> getRaidIOpsTop10(Long subSystemID,String startTime,String overTime){
		String sql = "SELECT AVG(p.bck_read_io+p.bck_write_io)/AVG(t.INTERLVA_LEN) AS avgIOps,t.SUBSYSTEM_ID,p.diskgroup_id,p.diskgroup_name ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(startTime!=null && startTime.length()>0){
			Date start = null;
			Date end = null;
			Long overTimes=null;
			try {
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
				if(overTime!=null && overTime.length()>0){
					end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(overTime);
					overTimes = end.getTime();
				}else{
					overTimes = new Date().getTime();  
				}
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM t_prf_diskgroup_perday p,t_prf_timestamp3 t WHERE p.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_diskgroup_perhour p,t_prf_timestamp2 t WHERE p.time_id = t.time_id ");
				}else{
					sb.append("FROM t_prf_timestamp t,t_prf_diskgroup p WHERE p.time_id = t.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_timestamp t,t_prf_diskgroup p WHERE p.time_id = t.time_id ");
		}
		if(subSystemID!=null && subSystemID!=0){
			sb.append("AND t.subsystem_id = ? ");
			args.add(subSystemID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(overTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (overTime == null || overTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY p.DISKGROUP_ID ORDER BY avgIOps DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),10);
	}
	
	//磁盘组容量信息
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskgroupCapecityInfos(Long subSystemID,Integer diskgroupID) {
		String sql = "select d.*,p.TOTAL_USABLE_CAPACITY,p.UNALLOCATED_CAPACITY,s.name as 'sname' "
				+ "from t_res_diskgroup d, t_res_storagesubsystem s,t_res_storagepool p "
				+ "where d.subsystem_id = s.subsystem_id and d.pool_id = p.pool_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (subSystemID != null && subSystemID!= 0) {
			sb.append("and d.SUBSYSTEM_ID = ? ");
			args.add(subSystemID);
		}
		if(diskgroupID!=null && diskgroupID!=0){
			sb.append("and d.diskgroup_id=? ");
			args.add(diskgroupID);
		}
		sb.append("order by d.ddm_cap desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}

	/**
	 * 磁盘组性能信息
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public List<DataRow> getPrefDiskgroupInfo(Integer diskgroupID, String startTime,String overTime) {
		String sql = "select p.*,t.SAMPL_TIME,t.INTERLVA_LEN ";
		StringBuffer sb = new StringBuffer(sql);
		List args = new ArrayList();
		if(startTime!=null && startTime.length()>0){
			Date start = null;
			Date end = null;
			Long overTimes=null;
			try {
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
				if(overTime!=null && overTime.length()>0){
					end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(overTime);
					overTimes = end.getTime();
				}else{
					overTimes = new Date().getTime();  
				}
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM t_prf_diskgroup_perday p,t_prf_timestamp3 t WHERE p.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_diskgroup_perhour p,t_prf_timestamp2 t WHERE p.time_id = t.time_id ");
				}else{
					sb.append("FROM t_prf_timestamp t,t_prf_diskgroup p WHERE p.time_id = t.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_timestamp t,t_prf_diskgroup p WHERE p.time_id = t.time_id ");
		}
		if(diskgroupID!=null && diskgroupID!=0){
			sb.append("and diskgroup_id = ? ");
			args.add(diskgroupID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(overTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (overTime == null || overTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append(" order by p.DISKGROUP_NAME,t.SAMPL_TIME");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_LINE_COUNT);

	}

	/**
	 * 性能数据分页显示
	 * 
	 * @param name
	 * @param startTime
	 * @param overTime
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getPerfDiskgroupPage(Integer diskID, String startTime,String overTime, int curPage, int numPerPage) {
		String sql = "select p.*,t.SAMPL_TIME ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(startTime!=null && startTime.length()>0){
			Date start = null;
			Date end = null;
			Long overTimes=null;
			try {
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
				if(overTime!=null && overTime.length()>0){
					end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(overTime);
					overTimes = end.getTime();
				}else{
					overTimes = new Date().getTime();  
				}
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM t_prf_diskgroup_perday p,t_prf_timestamp3 t WHERE p.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_diskgroup_perhour p,t_prf_timestamp2 t WHERE p.time_id = t.time_id ");
				}else{
					sb.append("FROM t_prf_timestamp t,t_prf_diskgroup p WHERE p.time_id = t.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_timestamp t,t_prf_diskgroup p WHERE p.time_id = t.time_id ");
		}
		if(diskID!=null && diskID!=0){
			sb.append("and p.diskgroup_id = ? ");
			args.add(diskID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(overTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一个月的数据
			if (overTime == null || overTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append(" order by t.SAMPL_TIME");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}

	// 添加
	public void addDiskGroup(List<DataRow> diskgroups,Integer subSystemID) {
		for (int i = 0; i < diskgroups.size(); ++i) {
			DataRow diskgroup = diskgroups.get(i);
			diskgroup.set("subsystem_id", subSystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_RES_DISKGROUP",diskgroup);
			//添加完成后，在添加ddm与diskgroup关系信息，再根据关系添加磁盘组容量信息
		}
	}
	//更新
	public void updateDiskgroup(List<DataRow> diskgroups,Integer subSystemID){
		for (DataRow dataRow : diskgroups) {
			dataRow.set("subsystem_id", subSystemID);
			String sql = "select name from t_res_diskgroup where name = '"+dataRow.getString("name")+"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size()>0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_diskgroup", dataRow, "diskgroup_id", dataRow.getInt("diskgroup_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_diskgroup",dataRow);
			}
		}
	}

	// 添加磁盘组性能
	public void addprfDiskGroup(List<DataRow> prfdiskgroups,Integer subSystemID) {
		for (int i = 0; i < prfdiskgroups.size(); ++i) {
			DataRow prfdiskgroup = prfdiskgroups.get(i);
			prfdiskgroup.set("TIME_ID", SrContant.TIME_FKEY);
			String diskgroupname = (String) prfdiskgroup.get("diskgroup_name");
			DiskgroupService service = new DiskgroupService();
			List<DataRow> rows = service.getDiskByName(diskgroupname,subSystemID);
			String diskId = rows.get(0).getString("diskgroup_id");
			prfdiskgroup.set("diskgroup_id", diskId);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_PRF_DISKGROUP",prfdiskgroup);
		}
	}

	// 报表磁盘组数据
	@SuppressWarnings("unchecked")
	public List<DataRow> reportDiskGroup(Integer subSystemID) {
		String sql = "SELECT * FROM t_res_diskgroup where subsystem_id="+subSystemID;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}

	// 报表磁盘组性能数据
	@SuppressWarnings("unchecked")
	public List<DataRow> reportPreDiskGroup(Integer subSystemID,Integer diskID,String startTime,String endTime) {
		String sql ="SELECT rdg.name,rdg.diskgroup_id,AVG((dg.bck_read_io+dg.bck_write_io)/tt.interlva_len) AS avg_iops," +
				"MAX((dg.bck_read_io+dg.bck_write_io)/tt.interlva_len) AS max_iops," +
				"rdg.width,rdg.raid_level,dg.bck_read_time,dg.bck_write_time ";
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
					sb.append("FROM t_prf_diskgroup_perday dg,t_prf_timestamp3 tt,t_res_diskgroup rdg WHERE dg.time_id=tt.time_id AND dg.diskgroup_id=rdg.diskgroup_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_diskgroup_perhour dg,t_prf_timestamp2 tt,t_res_diskgroup rdg WHERE dg.time_id=tt.time_id AND dg.diskgroup_id=rdg.diskgroup_id ");
				}else{
					sb.append("FROM t_prf_diskgroup dg,t_prf_timestamp tt,t_res_diskgroup rdg WHERE dg.time_id=tt.time_id AND dg.diskgroup_id=rdg.diskgroup_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_diskgroup dg,t_prf_timestamp tt,t_res_diskgroup rdg WHERE dg.time_id=tt.time_id AND dg.diskgroup_id=rdg.diskgroup_id ");
		}
		if(subSystemID!=null && subSystemID!=0){
			sb.append("and rdg.subsystem_id = ? ");
			args.add(subSystemID);
		}
		if(diskID!=null && diskID!=0){
			sb.append("and dg.diskgroup_id = ? ");
			args.add(diskID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("AND tt.sampl_time >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("AND tt.sampl_time <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一个月的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("AND tt.sampl_time >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY dg.diskgroup_id ORDER BY max_iops DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(), SrContant.REPORT_PERF_DATA_COUNT);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> doPrefDiskgroupInfo(Integer diskgroupID, String startTime,String overTime){
		String sql = "SELECT bck_read_io/t.INTERLVA_LEN AS read_iops," +
				"bck_write_io/t.INTERLVA_LEN AS write_iops," +
				"(bck_read_io+bck_write_io)/t.INTERLVA_LEN AS total_iops," +
				"bck_read_kb/t.INTERLVA_LEN AS read_kbps," +
				"bck_write_kb/t.INTERLVA_LEN AS write_kbps," +
				"(bck_read_kb+bck_write_kb)/t.INTERLVA_LEN AS total_kbps,t.sampl_time ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if(startTime!=null && startTime.length()>0){
			Date start = null;
			Date end = null;
			Long overTimes=null;
			try {
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
				if(overTime!=null && overTime.length()>0){
					end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(overTime);
					overTimes = end.getTime();
				}else{
					overTimes = new Date().getTime();  
				}
				Long lastTime = start.getTime();
				if(overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF){//大于20天查天性能表
					sb.append("FROM t_prf_diskgroup_perday d,t_prf_timestamp3 t WHERE d.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_diskgroup_perhour d,t_prf_timestamp2 t WHERE d.time_id = t.time_id ");
				}else{
					sb.append("FROM t_prf_diskgroup d,t_prf_timestamp t WHERE t.time_id = d.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_diskgroup d,t_prf_timestamp t WHERE t.time_id = d.time_id ");
		}
		if(diskgroupID!=null && diskgroupID!=0){
			sb.append("AND diskgroup_id = ? ");
			args.add(diskgroupID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(overTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (overTime == null || overTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("ORDER BY t.sampl_time");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_LINE_COUNT);
	}
	
	/**
	 * 24小时内热磁盘组信息
	 * @param subsystemId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHotDiskgroup(Integer subsystemId,String startTime,String endTime){
		String sql = "SELECT d.diskgroup_id,t.subsystem_id,d.diskgroup_name,AVG((bck_read_io+bck_write_io)/t.interlva_len) AS avg_iops " +
				"FROM t_prf_diskgroup d,t_prf_timestamp t WHERE d.time_id = t.time_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(subsystemId!=null && subsystemId>0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY d.diskgroup_id ORDER BY avg_iops DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.HOT_DISKGROUP_NUM);
	}

	// 获取前10条timestamp 数据
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfTimeTop() {
		String sql = "SELECT time_id FROM t_prf_timestamp ORDER BY sampl_time DESC";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, 10);
	}

	public DataRow getDiskGroupIopAndSpeed(String inTime,String diskGroupName){
		String sql="SELECT MAX(the_bck_io/the_time) AS maxiops, AVG(the_bck_io/the_time) AS avgiops"
				+" FROM t_prf_diskgroup WHERE time_id= "+inTime+" "
				+" AND diskgroup_name='"+diskGroupName+"'";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	//根据subsystemId 查询磁盘数
	public int getCountDiskGroupBySubSystemId(String sId){
		String sql="SELECT COUNT(ddm_id) FROM t_res_diskgroup trd,t_map_diskgroup2storage_ddm tmds"
			+" WHERE trd.diskgroup_id=tmds.diskgroup_Id AND trd.subsystem_id='"+sId+"'";
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql);
	}
	
	
	//根据diskgroup_Id 查询磁盘数
	public int getCountDiskGroupByDiskGroupId(String dgId){
		
		String sql="SELECT COUNT(ddm_id) FROM t_res_diskgroup trd,t_map_diskgroup2storage_ddm tmds" 
			+" WHERE trd.diskgroup_id=tmds.diskgroup_id and trd.diskgroup_id='"+dgId+"'";
		
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql);
	}
	
	@SuppressWarnings("unchecked")
	public Long checkPrimaryKey(Long key){
		String sql="select T.* from t_res_diskgroup T where T.diskgroup_id = "+key;
		List<DataRow> lists = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		if(lists!=null && lists.size()>0){
			return null;
		}
		return key;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskByName(String diskName,Integer fkey){
		String sql = "select d.* from t_res_diskgroup d where d.name = '"+diskName+"' and d.SUBSYSTEM_ID ="+fkey;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	//计算磁盘组磁盘容量及速度
	@SuppressWarnings("unchecked")
	public void setDDMInfo(Long diskgroupId){
		String sql="select m.*,d.* from t_map_diskgroup2storage_ddm m,t_res_storage_ddm d where d.DDM_ID = m.DDM_ID and m.DISKGROUP_ID = "+diskgroupId;
		String sql2 = "select m.*,p.* from t_map_storagepool2diskgroup m,t_res_storagepool p where p.pool_id = m.pool_id and m.DISKGROUP_ID= "+diskgroupId;
		List<DataRow> pools = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql2);
		List<DataRow> ddms = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		
		Long pool_id = 0l;
		Long ddmCap = 0l;
		String ddmSpeed = null;
		ddmCap = pools.get(0).getLong("total_usable_capacity");
		if(ddms!=null && ddms.size()>0){
			ddmSpeed = ddms.get(0).getString("ddm_speed");
		}
		pool_id = pools.get(0).getLong("pool_id");
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("update t_res_diskgroup set ddm_cap="+ddmCap+",DDM_SPEED = '"+ddmSpeed+"', pool_id = "+pool_id+" where diskgroup_id = "+diskgroupId);
	}
	
	//添加磁盘容量及磁盘速度
	@SuppressWarnings("unchecked")
	public void addDDMInfo(Integer subsystemId){
		List<DataRow> rows = getJdbcTemplate(WebConstants.DB_DEFAULT).query("select d.* from t_res_diskgroup d where d.subsystem_id = "+subsystemId);
		Long diskgroupid= null;
		for (DataRow dataRow : rows) {
			diskgroupid = dataRow.getLong("diskgroup_id");
			setDDMInfo(diskgroupid);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskPrefInfoByTime(String startTime,String endTime,Integer subsystemId){
		String sql="SELECT diskgroup_id,diskgroup_name," +
				"AVG(bck_read_io) AS bck_read_io," +
				"AVG(bck_write_io) AS bck_write_io," +
				"AVG(bck_read_kb) AS bck_read_kb," +
				"AVG(bck_write_kb) AS bck_write_kb," +
				"AVG(bck_read_time) AS bck_read_time," +
				"AVG(bck_write_time) AS bck_write_time," +
				"AVG(disk_util_percentage) AS disk_util_percentage," +
				"io_type AS io_type," +
				"AVG(the_bck_io) AS the_bck_io," +
				"AVG(the_bck_kb) AS the_bck_kb," +
				"AVG(the_time) AS the_time " +
				"FROM t_prf_diskgroup d,t_prf_timestamp t " +
				"WHERE d.time_id = t.time_id ";
		StringBuffer sb= new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY d.diskgroup_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 得到小时性能信息
	 * @param startTime
	 * @param endTime
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskPrefInfoPerhour(String startTime,String endTime,Integer subsystemId){
		String sql="SELECT diskgroup_id,diskgroup_name," +
		"AVG(bck_read_io) AS bck_read_io," +
		"AVG(bck_write_io) AS bck_write_io," +
		"AVG(bck_read_kb) AS bck_read_kb," +
		"AVG(bck_write_kb) AS bck_write_kb," +
		"AVG(bck_read_time) AS bck_read_time," +
		"AVG(bck_write_time) AS bck_write_time," +
		"AVG(disk_util_percentage) AS disk_util_percentage," +
		"io_type AS io_type," +
		"AVG(the_bck_io) AS the_bck_io," +
		"AVG(the_bck_kb) AS the_bck_kb," +
		"AVG(the_time) AS the_time " +
		"FROM t_prf_diskgroup_perhour d,t_prf_timestamp2 t " +
		"WHERE d.time_id = t.time_id ";
		StringBuffer sb= new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY d.diskgroup_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	//添加性能
	public void addDiskPrefInfo(Long timeId,List<DataRow> disks,String tableName){
		if(disks!=null && disks.size()>0){
			for (DataRow dataRow : disks) {
				dataRow.set("time_id", timeId);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert(tableName, dataRow);
			}
		}
	}
}
