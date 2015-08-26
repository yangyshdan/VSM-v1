package com.huiming.service.diskgroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.project.web.WebConstants;

public class DiskgroupService extends BaseService {
	/**
	 * 磁盘组分页信息
	 * 
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getDiskgroupList(int currentPage, int numPerPage,String name, String raidLevel, Integer subSystemID,String pool_id) {
		String sql = "select d.*,s.name as sname from t_res_diskgroup d, t_res_storagesubsystem s where d.subsystem_id = s.subsystem_id";
		
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
		if (subSystemID != null && subSystemID > 0) {
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
	public List<DataRow> getDiskgroupInfos(Integer subSystemID) {
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
	Logger logger = Logger.getLogger(getClass());
	/**
	 * Raid Group IOps top10
	 * @param subSystemID
	 * @param startTime
	 * @param overTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getRaidIOpsTop10(Integer subSystemID,String startTime,String overTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		logger.info("**************************************************");
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select dev_id as subsystem_id,ele_id as diskgroup_id,ele_name as diskgroup_name,prf_timestamp,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("AVG(A101_03) as avg_iops from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("AVG(A107_03) as avg_iops from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("AVG(A113_03) as avg_iops from ");
		//For NETAPP
		} else if (storageType.equalsIgnoreCase(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			sb.append("AVG(NA100_08) as avg_iops from ");
		}
		logger.info("1、" + sb);
		if (startTime != null && startTime.length() > 0) {
			Date start = null;
			Date end = null;
			Long overTimes = null;
			try {
				start = df.parse(startTime);
				if (overTime != null && overTime.length() > 0) {
					end = df.parse(overTime);
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
				//否则查询实时性能数据
				} else {
					sb.append(viewName);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		//默认查实时数据
		} else {
			sb.append(viewName);
		}
		sb.append(" where 1 = 1");
		if (subSystemID != null && subSystemID != 0) {
			sb.append(" and dev_id = ?");
			args.add(subSystemID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and prf_timestamp >= ?");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sb.append(" and prf_timestamp <= ?");
			args.add(overTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (overTime == null || overTime.length() == 0) {
				sb.append(" and prf_timestamp >= ?");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(df.format(calendar.getTime()));
			}
		}
		sb.append(" group by diskgroup_id order by avg_iops desc");// avgiops
//		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),
					SrContant.TOP_LIMIT_COUNT);
		}
		return null;
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
			String sql = "select diskgroup_id from t_res_diskgroup where name = '"+dataRow.getString("name")+"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			
			if(row!=null && row.size()>0){
				//移除该对象,只是作一个查询条件
				dataRow.remove("mdisk_grp_name");
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_diskgroup", dataRow, "diskgroup_id", row.getInt("diskgroup_id"));
			}else{
				//获取POOL_ID
				String sql2 = "select * from t_res_storagepool where name = '" + dataRow.getString("mdisk_grp_name") + "' and subsystem_id = " + subSystemID;
				DataRow row2 = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql2);
				if (row2 != null && row2.size() > 0) {
					dataRow.set("pool_id", row2.getInt("pool_id"));
					//移除该对象,只是作一个查询条件
					dataRow.remove("mdisk_grp_name");
					//添加
					getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_diskgroup",dataRow);
				}
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

	/**
	 * 获取磁盘组性能信息
	 * @param subSystemID
	 * @param diskID
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDiskgroupPerfInfo(Integer subSystemID,Integer diskID,String startTime,String endTime,DataRow paramRow) {
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select rdg.name,rdg.diskgroup_id,rdg.width,rdg.raid_level,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("AVG(v.A101_03) as avg_iops,MAX(v.A101_03) as max_iops,");
			sb.append("AVG(v.A101_09*1024) as avg_kbps,MAX(v.A101_09*1024) as max_kbps from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("AVG(v.A107_03) as avg_iops,MAX(v.A107_03) as max_iops,");
			sb.append("AVG(v.A107_09*1024) as avg_kbps,MAX(v.A107_09*1024) as max_kbps from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("AVG(v.A113_03) as avg_iops,MAX(v.A113_03) as max_iops,");
			sb.append("AVG(v.A113_09*1024) as avg_kbps,MAX(v.A113_09*1024) as max_kbps from ");
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
				//大于20天查天性能数据
				if (overTimes - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_DAILY);
				//大于2天查小时性能数据
				} else if (overTimes - lastTime > SrContant.SEARCH_IN_PERHOURPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_HOURLY);
				//否则查询实时性能数据
				} else {
					sb.append(viewName);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		//默认查询实时性能数据
		} else {
			sb.append(viewName);
		}
		sb.append(" v,t_res_diskgroup rdg where v.ele_id = rdg.diskgroup_id");
		if (subSystemID != null && subSystemID != 0) {
			sb.append(" and v.dev_id = ?");
			args.add(subSystemID);
		}
		if (diskID != null && diskID != 0) {
			sb.append(" and v.ele_id = ?");
			args.add(diskID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and v.prf_timestamp >= ?");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append(" and v.prf_timestamp <= ?");
			args.add(endTime);
		}
		//默认查询最近一个月的数据
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
				sb.append(" and v.prf_timestamp >= ?");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append(" group by v.ele_id order by max_iops desc");
		
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_DATA_COUNT);
		}
		return null;
	}
	
	/**
	 * 获取相应的磁盘组性能信息
	 * @param subSystemID
	 * @param diskgroupID
	 * @param startTime
	 * @param overTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPerDiskgroupPerfInfo(Integer subSystemID,Integer diskgroupID,String startTime,String overTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select dev_id,ele_id,ele_name,prf_timestamp,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("A101_01 as read_iops,A101_02 as write_iops,A101_03 as total_iops,(A101_07*1024) as read_kbps,(A101_08*1024) as write_kbps,(A101_09*1024) as total_kbps from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("A107_01 as read_iops,A107_02 as write_iops,A107_03 as total_iops,(A107_07*1024) as read_kbps,(A107_08*1024) as write_kbps,(A107_09*1024) as total_kbps from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("A113_01 as read_iops,A113_02 as write_iops,A113_03 as total_iops,(A113_07*1024) as read_kbps,(A113_08*1024) as write_kbps,(A113_09*1024) as total_kbps from ");
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
		if (diskgroupID != null && diskgroupID != 0) {
			sb.append(" and ele_id = ?");
			args.add(diskgroupID);
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
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.REPORT_PERF_DATA_COUNT);
		}
		return null;
	}
	
	/**
	 * 24小时内热磁盘组信息
	 * @param subsystemId
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHotDiskgroup(Integer subSystemID,String startTime,String overTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select dev_id as subsystem_id,ele_id as diskgroup_id,ele_name as diskgroup_name,prf_timestamp,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("AVG(A101_03) as avg_iops from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("AVG(A107_03) as avg_iops from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("AVG(A113_03) as avg_iops from ");
		}
		
		sb.append(viewName + " where 1 = 1");
		if (subSystemID != null && subSystemID != 0) {
			sb.append(" and dev_id = ?");
			args.add(subSystemID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and prf_timestamp >= ?");
			args.add(startTime);
		}
		if (overTime != null && overTime.length() > 0) {
			sb.append(" and prf_timestamp <= ?");
			args.add(overTime);
		}
		//查询数据
		if (startTime == null || startTime.length() == 0) {
			if (overTime == null || overTime.length() == 0) {
				sb.append(" and prf_timestamp >= ?");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append(" group by diskgroup_id order by avg_iops desc");
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),SrContant.HOT_DISKGROUP_NUM);
		}
		return null;
	}

	// 获取前10条timestamp 数据
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfTimeTop() {
		String sql = "select time_id from t_prf_timestamp order by sample_time desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, SrContant.TOP_LIMIT_COUNT);
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
		ddmSpeed = ddms.get(0).getString("ddm_speed");
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
	
	/**
	 * 添加磁盘组性能信息
	 * @param prfDiskGroups
	 */
	public void addPrfDiskGroups(List<DataRow> prfDiskGroups) {
		for (int i = 0; i < prfDiskGroups.size(); i++) {
			DataRow prfDiskGroup = prfDiskGroups.get(i);
			String name = prfDiskGroup.getString(SrTblColConstant.DG_DISKGROUP_NAME);
			List<DataRow> rows = getDiskByName(name,Integer.parseInt(prfDiskGroup.getString(SrTblColConstant.TT_SUBSYSTEM_ID)));
			if (rows.size() > 0) {
				String diskId = rows.get(0).getString("diskgroup_id");
				prfDiskGroup.set("diskgroup_id", diskId);
				prfDiskGroup.remove(SrTblColConstant.TT_SUBSYSTEM_ID);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_diskgroup",prfDiskGroup);
			}
		}
	}
	
	/**
	 * 获取磁盘性能指标信息
	 * @param timeIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfDiskGroups(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select diskgroup_id,diskgroup_name,SUM(bck_read_io) as bck_read_io,SUM(bck_write_io) as bck_write_io,");
		sb.append("SUM(bck_read_kb) as bck_read_kb,SUM(bck_write_kb) as bck_write_kb,");
		sb.append("SUM(bck_read_io*bck_read_time)/SUM(bck_read_time) as bck_read_time,");
		sb.append("SUM(bck_write_io*bck_write_time)/SUM(bck_write_time) as bck_write_time");
		sb.append(" from t_prf_diskgroup where time_id in (" + timeIds + ")");
		sb.append(" group by diskgroup_id,diskgroup_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加磁盘组性能信息(小时/天)
	 * @param prfDiskGroups
	 */
	public void addPerHourAndDayPrfDiskGroups(List<DataRow> prfDiskGroups) {
		for (int i = 0; i < prfDiskGroups.size(); i++) {
			DataRow row = prfDiskGroups.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_diskgroup", row);
		}
	}
}
