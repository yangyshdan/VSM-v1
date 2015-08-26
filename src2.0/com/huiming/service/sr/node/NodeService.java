package com.huiming.service.sr.node;

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

public class NodeService extends BaseService{
	//添加
	public void addStoragenodes(List<DataRow> nodes, Integer subsystemID){
		for (DataRow dataRow : nodes) {
			dataRow.set("subsystem_id", subsystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagenode", dataRow);
		}
	}
	//更新
	public void updateStoragenodes(List<DataRow> nodes, Integer subsystemID){
		for (DataRow dataRow : nodes) {
			dataRow.set("subsystem_id", subsystemID);
			String sql = "select sp_name from t_res_storagenode where sp_name = '"+dataRow.getString("sp_name")+"' and subsystem_id = "+subsystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size() > 0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagenode", dataRow, "sp_id", row.getInt("sp_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagenode", dataRow);
			}
		}
	}
	
	//查
	@SuppressWarnings("unchecked")
	public List<DataRow> getNodeByName(String name,Integer subsystemId,Integer nodeId){
		String sql="select * from t_res_storagenode where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and sp_name = ? ");
			args.add(name);
		}
		if(subsystemId!=null &&subsystemId!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(nodeId!=null && nodeId!=0){
			sb.append("and sp_id = ? ");
			args.add(nodeId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getNodeBysubID(Integer subsystemID){
		String sql="select * from t_res_storagenode where subsystem_id = "+subsystemID;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 控制器列表信息
	 * @param sp_name
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getNodesInfo(String sp_name,Integer subsystemId){
		String sql = "select n.*,s.model,count(*) as lun_num "
			+ "from t_res_storagenode n, t_res_storagesubsystem s,t_res_storagevolume v "
			+ "where n.subsystem_id = s.subsystem_id and v.sp_id = n.sp_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(sp_name!=null &&sp_name.length()>0){
			sb.append("and n.sp_name like ? ");
			args.add("%" + sp_name + "%");
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and n.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY n.sp_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	//分页查
	public DBPage getNodePage(String sp_name,Integer subsystemId,Integer currentPage,Integer numPerPage){
		String sql = "SELECT n.*,s.model,(SELECT COUNT(*) FROM t_res_storagevolume v WHERE v.sp_id = n.sp_id) AS lun_num "
			+ "FROM t_res_storagenode n, t_res_storagesubsystem s WHERE n.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(sp_name!=null &&sp_name.length()>0){
			sb.append("and n.sp_name like ? ");
			args.add("%" + sp_name + "%");
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and n.subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY n.sp_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), currentPage, numPerPage);
	}
	
	/**
	 * 控制器详细信息
	 * @param spId
	 * @return
	 */
	public DataRow getStoragenodeIfo(Integer spId){
		String sql = "select n.*,s.model,count(*) as lun_num "
			+ "from t_res_storagenode n, t_res_storagesubsystem s,t_res_storagevolume v "
			+ "where n.subsystem_id = s.subsystem_id and v.sp_id = n.sp_id " 
			+ "and n.sp_id = "+spId+" GROUP BY n.sp_id";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	/**
	 * 控制器详细信息
	 * @param subsystemId
	 * @param spId
	 * @return
	 */
	public DataRow getStoragenodeIfo(Integer subsystemId, Integer spId){
		String sql = "select n.*,s.model,count(*) as lun_num,s.storage_type "
			+ "from t_res_storagenode n, t_res_storagesubsystem s "
			+ "where n.subsystem_id = s.subsystem_id " 
			+ "and n.subsystem_id = " + subsystemId
			+ " and n.sp_id = "+spId+" GROUP BY n.sp_id";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	/**
	 * 得到控制器下卷列表信息
	 * @param spId
	 * @return
	 */
	public DBPage getspVolumeInfo(Integer spId,Integer curPage,Integer numPerPage){
		String sql="select v.* from t_res_storagevolume v where sp_id = "+spId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql, curPage, numPerPage);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getspVolumeInfo(Integer spId){
		String sql="select v.* from t_res_storagevolume v where sp_id = "+spId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	/**
	 * 控制器性能信息
	 * @param subsystemId
	 * @param spID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getNodePref(Integer subsystemId,Integer spID,String startTime,String endTime){
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
					sb.append("FROM t_prf_storagevolume_perday p,t_prf_timestamp3 t,t_res_storagevolume r ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour p,t_prf_timestamp2 t,t_res_storagevolume r ");
				}else{
					sb.append("FROM t_prf_storagevolume p,t_res_storagevolume r,t_prf_timestamp t ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_storagevolume p,t_res_storagevolume r,t_prf_timestamp t ");
		}
		sb.append("WHERE p.time_id = t.time_id AND p.volume_id = r.volume_id ");
		List<Object> args = new ArrayList<Object>();
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(spID!=null && spID!=0){
			sb.append("and r.sp_id = ? ");
			args.add(spID);
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
	 * 控制器性能分页信息
	 * @param subsystemId
	 * @param spID
	 * @param startTime
	 * @param endTime
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getSPPrfPage(Integer subsystemId,Integer spID,String startTime,String endTime,Integer curPage,Integer numPerPage){
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
					sb.append("FROM t_prf_storagevolume_perday p,t_prf_timestamp3 t,t_res_storagevolume r ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表 
					sb.append("FROM t_prf_storagevolume_perhour p,t_prf_timestamp2 t,t_res_storagevolume r ");
				}else{
					sb.append("FROM t_prf_storagevolume p,t_res_storagevolume r,t_prf_timestamp t ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM t_prf_storagevolume p,t_res_storagevolume r,t_prf_timestamp t ");
		}
		sb.append("WHERE p.time_id = t.time_id AND p.volume_id = r.volume_id ");
		List<Object> args = new ArrayList<Object>();
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and t.subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(spID!=null && spID!=0){
			sb.append("and r.sp_id = ? ");
			args.add(spID);
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
	
	@SuppressWarnings("unchecked")
	public List<DataRow> reportData(Integer subsystemID,String startTime,String endTime){
		String sql = "SELECT sp_id,sp_name ,MAX((read_io+write_io)/interlva_len) AS max_iops," +
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
					sb.append("FROM v_prf_storagenode_perday v ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("FROM v_prf_storagenode_perhour v ");
				}else{
					sb.append("FROM v_prf_storagenode v ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("FROM v_prf_storagenode v ");
		}
		sb.append("WHERE 1=1 ");
		if(subsystemID!=null && subsystemID>0){
			sb.append("AND subsystem_id = ? ");
			args.add(subsystemID);
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
		sb.append("GROUP BY sp_id ORDER BY max_iops DESC");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_DATA_COUNT);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> reportPrfInfo(Integer spId,Integer subsystemID,String startTime,String endTime){
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
					sb.append("SELECT * FROM v_prf_storagenode_perday where 1=1 ");
				}else if(overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF){  //大于2天查小时性能表
					sb.append("SELECT * FROM v_prf_storagenode_perhour where 1=1 ");
				}else{
					sb.append("SELECT * FROM v_prf_storagenode where 1=1 ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			sb.append("SELECT * FROM v_prf_storagenode where 1=1 ");
		}
		if(spId!=null && spId>0){
			sb.append("AND sp_id = ? ");
			args.add(spId);
		}
		if(subsystemID!=null && subsystemID>0){
			sb.append("AND subsystem_id = ? ");
			args.add(subsystemID);
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
