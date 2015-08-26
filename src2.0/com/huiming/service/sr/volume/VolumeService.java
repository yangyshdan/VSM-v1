package com.huiming.service.sr.volume;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.service.sr.pool.PoolService;
import com.huiming.service.sr.node.NodeService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class VolumeService extends BaseService {
	// 分页查询
	public DBPage getVolumePage(int curPage, int numPerPage, String name, Long greatLogical_Capacity,
			Long lessLogical_Capacity,
			String pool_id, String system_id) {
		StringBuffer sql = new StringBuffer("select * from t_res_storagevolume where 1=1");
		List<Object> args = new ArrayList<Object>();
		if (name != null && name.length() > 0) {
			sql.append(" and name like ? ");
			args.add("%" + name + "%");
		}
		if (greatLogical_Capacity != null && greatLogical_Capacity!= 0) {
			sql.append(" and logical_capacity <= ?");
			args.add(greatLogical_Capacity);
		}
		if (lessLogical_Capacity != null && lessLogical_Capacity!= 0) {
			sql.append(" and logical_capacity >= ?");
			args.add(lessLogical_Capacity);
		}
		if (pool_id != null && pool_id.length() > 0) {
			sql.append(" and pool_id = ?");
			args.add(pool_id);
		}
		if (system_id != null && system_id.length() > 0) {
			sql.append(" and subsystem_id = ?");
			args.add(system_id);
		}
		 sql.append(" order by logical_capacity desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(), curPage, numPerPage);
	}

	// 查询
	@SuppressWarnings("unchecked")
	public List<DataRow> getCapacity(String subSystemId) {
		String sql = "select name,logical_capacity from t_res_storagevolume where subsystem_id="+subSystemId+" group by name order by logical_capacity desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,20);
	}

	// 查询id详细信息
	public DataRow getVolumeById(String id, String system_id) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select * from t_res_storagevolume where volume_id= ?");
		args.add(id);
		if (system_id != null && system_id.length() > 0) {
			sql.append(" and subsystem_id = ?");
			args.add(system_id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql.toString(),args.toArray());
	}
	
	/**
	 * 获取卷详细信息
	 * @param subsystemId
	 * @param volumeId
	 * @return
	 */
	public DataRow getVolumeInfo(Integer subsystemId, Integer volumeId) {
		String sql = "select v.*,s.model,s.name as sname,s.storage_type from t_res_storagevolume v,t_res_storagesubsystem s"
			+ " where v.subsystem_id = s.subsystem_id"
			+ " and v.subsystem_id = " + subsystemId
			+ " and v.volume_id = " + volumeId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}

	// 分页查询卷性能
	public DBPage getPrfVolumePage(int curPage, int numPerPage, String id,
			String startTime, String endTime) {
		StringBuffer sb = new StringBuffer("select v.*,t.sampl_time,t.interlva_len ");
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
					sb.append("FROM t_prf_storagevolume_perday v,t_prf_timestamp3 t WHERE v.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour v,t_prf_timestamp2 t WHERE v.time_id = t.time_id ");
				}else{
					sb.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
		}
		if (id != null && id.length() > 0) {
			sb.append("and volume_id= ? ");
			args.add(id);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and t.SAMPL_TIME <= ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {// 默认查询最近一个月的数据
			if (endTime == null || endTime.length() == 0) {
				sb.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR_OF_DAY, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append(" order by v.volume_id,t.SAMPL_TIME");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}

	// 添加卷
	public void addVolume(List<DataRow> volumes,Integer subSystemID) {
		PoolService pool = new PoolService();
		NodeService node = new NodeService();
		for (DataRow volumeRow:volumes) {
			volumeRow.set("subsystem_id", subSystemID);
			String pool_name = volumeRow.getString("pool_name");
			String currentOwner = volumeRow.getString("current_owner");
			if(currentOwner!=null && currentOwner.length()>0){
				DataRow spRow = node.getNodeByName(currentOwner, subSystemID, null).get(0);
				if(spRow!=null && spRow.size()>0){
					volumeRow.set("sp_id", spRow.getString("sp_id"));
				}
			}
			
			if (pool_name!=null && !pool_name.equals("N/A")) {
				DataRow id = pool.getPoolById(null, subSystemID.toString(), pool_name);
				if(id!=null && id.size()>0){
					volumeRow.set("pool_id", id.getLong("pool_id"));
				}
			}
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagevolume",volumeRow);
		}
	}
	//更新卷
	public void updateVolumeInfo(List<DataRow> volumes,Integer subSystemID){
		PoolService pool = new PoolService();
		NodeService node = new NodeService();
		for (DataRow volumeRow : volumes) {
			String pool_name = volumeRow.getString("pool_name");
			String currentOwner = volumeRow.getString("current_owner");
			volumeRow.set("subsystem_id", subSystemID);
			if(currentOwner!=null && currentOwner.length()>0){
				DataRow spRow = node.getNodeByName(currentOwner, subSystemID, null).get(0);
				if(spRow!=null && spRow.size()>0){
					volumeRow.set("sp_id", spRow.getString("sp_id"));
				}
			}
			if (pool_name!=null && !pool_name.equals("N/A")) {
				DataRow id = pool.getPoolById(null, subSystemID.toString(), pool_name);
				if(id!=null && id.size()>0){
					volumeRow.set("pool_id", id.getLong("pool_id"));
				}
			}
			String sql = "select name from t_res_storagevolume where name = '"+volumeRow.getString("name")+"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size() > 0){   
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagevolume", volumeRow, "volume_id", volumeRow.getString("volume_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagevolume",volumeRow);
			}
		}
	}

	// 添加卷性能
	public void addprfVolume(List<DataRow> prfvolumes,Integer subSystemID) {
		for (int i = 0; i < prfvolumes.size(); ++i) {
			DataRow prfvolume = prfvolumes.get(i);
			prfvolume.set("TIME_ID", SrContant.TIME_FKEY);
			String volumename = (String) prfvolume.get("volume_name");
			VolumeService service = new VolumeService();
			List<DataRow> volumes = service.getVolumeByName(volumename, subSystemID);
			if (volumes!=null && volumes.size() > 0) {
				for (DataRow dataRow : volumes) {
					String id = dataRow.getString("volume_id");
					prfvolume.set("volume_id", id);
					getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_PRF_STORAGEVOLUME", prfvolume);
				}
			}

		}
	}
	
	/**
	 * 24小时内热卷信息(TOP3)
	 * @param subsystemId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHostVolumeInfo(Integer subsystemId,String startTime,String endTime){
		String sql = "SELECT v.volume_id,v.volume_name,t.subsystem_id,AVG((read_io+write_io)/t.interlva_len) AS avg_iops " +
				"FROM t_prf_storagevolume v,t_prf_timestamp t WHERE v.time_id = t.time_id ";
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
				calendar.add(Calendar.HOUR_OF_DAY, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY v.volume_id ORDER BY avg_iops DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.HOT_LUN_NUM);
	}
	/**
	 * 24小时内热卷信息(TOP10)
	 * @param subsystemId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> gettop10VolumePerfInfo(Integer subsystemId,String startTime,String endTime){
		String sql = "SELECT v.volume_id,v.volume_name,t.subsystem_id,AVG((read_io+write_io)/t.interlva_len) AS avg_iops " +
		"FROM t_prf_storagevolume v,t_prf_timestamp t WHERE v.time_id = t.time_id ";
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
				calendar.add(Calendar.HOUR_OF_DAY, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY v.volume_id ORDER BY avg_iops DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),10);
	}

	/**
	 * 查询卷性能数据
	 * 
	 * @param id
	 * @param startTime
	 * @param overTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrefVolumeInfo(Integer volumeId,String startTime,String overTime) {
		StringBuffer sql = new StringBuffer("select v.*,t.sampl_time,t.interlva_len ");
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
					sql.append("FROM t_prf_storagevolume_perday v,t_prf_timestamp3 t WHERE v.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sql.append("FROM t_prf_storagevolume_perhour v,t_prf_timestamp2 t WHERE v.time_id = t.time_id ");
				}else{
					sql.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sql.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
		}
		if(volumeId!=null && volumeId!=0){
			sql.append("and v.volume_id = ? ");
			args.add(volumeId);
		}
		if (startTime != null && startTime.length() > 0) {
			sql.append("and t.SAMPL_TIME >= ? ");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sql.append("and t.SAMPL_TIME <= ? ");
			args.add(overTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (overTime == null || overTime.length() == 0) {
				sql.append("and t.SAMPL_TIME >= ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sql.append(" order by v.volume_id,t.SAMPL_TIME");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray(),SrContant.REPORT_PERF_LINE_COUNT);
	}
	
	//报表性能曲线数据
	@SuppressWarnings("unchecked")
	public List<DataRow> doPrefVolumeInfo(Integer volumeId,String startTime,String overTime){
		String sql = "SELECT v.read_io/t.INTERLVA_LEN AS read_iops," +
				"v.write_io/t.INTERLVA_LEN AS write_iops," +
				"(v.read_io+v.write_io)/t.INTERLVA_LEN AS total_iops," +
				"v.read_kb/t.INTERLVA_LEN AS read_kbps," +
				"v.write_kb/t.INTERLVA_LEN AS write_kbps," +
				"(v.read_kb+v.write_kb)/t.INTERLVA_LEN AS total_kbps," +
				"v.read_io_time/v.read_io AS read_restime," +
				"v.write_io_time/v.write_io AS write_restime," +
				"(v.read_io_time+v.write_io_time)/(v.read_io+v.write_io) AS avg_restime,t.sampl_time ";
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
					sb.append("FROM t_prf_storagevolume_perday v,t_prf_timestamp3 t WHERE v.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour v,t_prf_timestamp2 t WHERE v.time_id = t.time_id ");
				}else{
					sb.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
		}
		if(volumeId!=null && volumeId!=0){
			sb.append("and v.volume_id = ? ");
			args.add(volumeId);
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
	 * 查询卷列表信息
	 * 
	 * @param subSystemID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeInfo(Long subSystemID) {
		String sql = "select * from t_res_storagevolume where 1=1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if (subSystemID != null && subSystemID != 0) {
			sb.append("and subsystem_id = ?");
			args.add(subSystemID);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}

	// 卷报表数据
	@SuppressWarnings("unchecked")
	public List<DataRow> reportVolume(Integer subSystemID) {
		String sql = "SELECT volume_id,NAME,logical_capacity,physical_capacity,raid_level,default_owner,current_owner FROM t_res_storagevolume where subsystem_id = "+subSystemID;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	//卷性能报表数据top10
	@SuppressWarnings("unchecked")
	public List<DataRow> reportPrfVolume(Integer subsystemID,Integer volumeId,String startTime,String endTime){
		String sql = "SELECT v.volume_name,v.volume_id,AVG((v.read_io+v.write_io)/t.interlva_len) AS avg_iops," +
				"MAX((v.read_io+v.write_io)/t.interlva_len) AS max_iops," +
				"AVG((v.read_kb+v.write_kb)/t.interlva_len) AS avg_speed," +
				"MAX((v.read_kb+v.write_kb)/t.interlva_len) AS max_speed," +
				"AVG(v.read_io_time/v.read_io) AS avg_retime," +
				"AVG(v.write_io_time/v.write_io) AS avg_wrtime ";
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
					sb.append("FROM t_prf_storagevolume_perday v,t_prf_timestamp3 t WHERE v.time_id = t.time_id ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour v,t_prf_timestamp2 t WHERE v.time_id = t.time_id ");
				}else{
					sb.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("from t_prf_storagevolume v,t_prf_timestamp t where v.time_id = t.time_id ");
		}
		if(subsystemID!=null && subsystemID!=0){
			sb.append("AND t.subsystem_id = ? ");
			args.add(subsystemID);
		}
		if(volumeId!=null && volumeId!=0){
			sb.append("and v.volume_id = ? ");
			args.add(volumeId);
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
		sb.append("GROUP BY v.volume_id ORDER BY max_iops DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_DATA_COUNT);
	}
	
	
	public DataRow getVolumeIopAndSpeed(String inTime,String diskGroupName){
		String sql="SELECT MAX(the_io/the_io_time) AS maxiops, AVG(the_io/the_io_time) AS avgiops,"
			+"MAX(the_kb/the_io_time) AS maxsulv,AVG(the_kb/the_io_time) AS avgsulv FROM t_prf_storagevolume"
			+" WHERE time_id IN("+inTime+") AND volume_name='"+diskGroupName+"'";
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public Long checkPrimaryKey(Long key){
		String sql="select T.VOLUME_ID from t_res_storagevolume T where T.VOLUME_ID = "+key;
		List<DataRow> lists = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		if(lists!=null && lists.size()>0){
			return null;
		}
		return key;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeByName(String name,Integer subSystemID){
		String sql="select v.* from t_res_storagevolume v where v.name ='"+name+"' and v.SUBSYSTEM_ID = "+subSystemID+" order by v.update_timestamp desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> exportVolumeConfigData(String name,Long greatLogical_Capacity,
			Long lessLogical_Capacity,
			String pool_id, String system_id) {
		StringBuffer sql = new StringBuffer("select * from t_res_storagevolume where 1=1");
		List<Object> args = new ArrayList<Object>();
		if (name != null && name.length() > 0) {
			sql.append(" and name like ? ");
			args.add("%" + name + "%");
		}
		if (greatLogical_Capacity != null && greatLogical_Capacity!= 0) {
			sql.append(" and logical_capacity <= ?");
			args.add(greatLogical_Capacity);
		}
		if (lessLogical_Capacity != null && lessLogical_Capacity!= 0) {
			sql.append(" and logical_capacity >= ?");
			args.add(lessLogical_Capacity);
		}
		if (pool_id != null && pool_id.length() > 0) {
			sql.append(" and pool_id = ?");
			args.add(pool_id);
		}
		if (system_id != null && system_id.length() > 0) {
			sql.append(" and subsystem_id = ?");
			args.add(system_id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray(),500);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumePrefInfoByTime(String startTime,String endTime,Integer subsystemId){
		String sql="SELECT volume_id,volume_name," +
				"AVG(read_io) AS read_io," +
				"AVG(write_io) AS write_io," +
				"AVG(read_hit_io) AS read_hit_io," +
				"AVG(write_hit_io) AS write_hit_io," +
				"AVG(read_kb) AS read_kb," +
				"AVG(write_kb) AS write_kb," +
				"AVG(read_io_time) AS read_io_time," +
				"AVG(write_io_time) AS write_io_time," +
				"AVG(vol_util_percentage) AS vol_util_percentage," +
				"AVG(the_io) AS the_io," +
				"AVG(the_hit_io) AS the_hit_io," +
				"AVG(the_kb) AS the_kb," +
				"AVG(the_io_time) AS the_io_time " +
				"FROM t_prf_storagevolume v,t_prf_timestamp t " +
				"WHERE v.time_id = t.time_id ";
		StringBuffer sb = new StringBuffer(sql);
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
		sb.append("GROUP BY volume_id");
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
	public List<DataRow> getVolumePrefInfoPerhour(String startTime,String endTime,Integer subsystemId){
		String sql="SELECT volume_id,volume_name," +
		"AVG(read_io) AS read_io," +
		"AVG(write_io) AS write_io," +
		"AVG(read_hit_io) AS read_hit_io," +
		"AVG(write_hit_io) AS write_hit_io," +
		"AVG(read_kb) AS read_kb," +
		"AVG(write_kb) AS write_kb," +
		"AVG(read_io_time) AS read_io_time," +
		"AVG(write_io_time) AS write_io_time," +
		"AVG(vol_util_percentage) AS vol_util_percentage," +
		"AVG(the_io) AS the_io," +
		"AVG(the_hit_io) AS the_hit_io," +
		"AVG(the_kb) AS the_kb," +
		"AVG(the_io_time) AS the_io_time " +
		"FROM t_prf_storagevolume_perhour v,t_prf_timestamp2 t " +
		"WHERE v.time_id = t.time_id ";
		StringBuffer sb = new StringBuffer(sql);
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
		sb.append("GROUP BY volume_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	//添加
	public void addVolumePrefInfo(Long timeId,List<DataRow> volumes,String tableName){
		if(volumes!=null && volumes.size()>0){
			for (DataRow dataRow : volumes) {
				dataRow.set("time_id", timeId);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert(tableName, dataRow);
			}
		}
	}
}
