package com.huiming.service.sr.storagesystem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class StorageSystemService extends BaseService {
	private DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DBPage getStoragePage(int curPage, int numPerPage, String name,
			String ip, String type, String serialNumber, Integer startcapacity,
			Integer endcapacity, Integer startunallocatedcapacity,
			Integer endunallocatedcapacity,String limitIds) {
		StringBuffer sql = new StringBuffer("select * from t_res_storagesubsystem where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds.length() > 0) {
			sql.append(" and subsystem_id in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sql.append(" and name like ? ");
			args.add("%" + name + "%");
		}
		if (ip != null && ip.length() > 0) {
			sql.append(" and ip_address like ? ");
			args.add("%" + ip + "%");
		}
		if (type != null && type.length() > 0) {
			sql.append("and storage_type = ? ");
			args.add(type);
		}
		if (serialNumber != null && serialNumber.length() > 0) {
			sql.append("and serial_number = ? ");
			args.add(serialNumber);
		}
		if (startcapacity != null && startcapacity > 0) {
			sql.append("and total_usable_capacity >= ? ");
			args.add(startcapacity);
		}
		if (endcapacity != null && endcapacity > 0) {
			sql.append("and total_usable_capacity <= ? ");
			args.add(endcapacity);
		}
		if (startunallocatedcapacity != null && startunallocatedcapacity > 0) {
			sql.append("and unallocated_usable_capacity >= ? ");
			args.add(startunallocatedcapacity);
		}
		if (endunallocatedcapacity != null && endunallocatedcapacity > 0) {
			sql.append("and unallocated_usable_capacity <= ? ");
			args.add(endunallocatedcapacity);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
	}

	public DataRow getStorageById(String id) {
		List<Object> args = new ArrayList<Object>();
		String sql = "select * from t_res_storagesubsystem where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if (id != null && id.length() > 0) {
			sb.append("and subsystem_id = ? ");
			args.add(id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(), args.toArray());
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageInfoById(Integer id) {
		List<Object> args = new ArrayList<Object>();
		String sql = "select * from t_res_storagesubsystem where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if (id != null && id != 0) {
			sb.append("and subsystem_id = ? ");
			args.add(id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),
				args.toArray());
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageByName(String name) {
		List<Object> args = new ArrayList<Object>();
		String sql = "select * from t_res_storagesubsystem where name = ?";
		args.add(name);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,
				args.toArray());
	}

	/**
	 * 查询存储系统下的端口
	 * 
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getPortById(int curPage, int numPerPage, String id) {
		String sql = "select * from t_res_port where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,
				args.toArray(), curPage, numPerPage);
	}

	/**
	 * 查询存储系统下的磁盘组
	 * 
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getDiskGroupById(int curPage, int numPerPage, String id) {
		String sql = "select * from t_res_diskgroup where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,
				args.toArray(), curPage, numPerPage);
	}

	/**
	 * 查询所有的存储系统
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageCapacityInfo(String limitIds) {
		StringBuffer sql = new StringBuffer("select * from t_res_storagesubsystem where 1 = 1");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and subsystem_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}

	/**
	 * 查询存储系统下的池
	 * 
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getPoolById(int curPage, int numPerPage, String id) {
		String sql = "select * from t_res_storagepool where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,
				args.toArray(), curPage, numPerPage);
	}

	/**
	 * 查询存储系统下的卷
	 * 
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getVolumeById(int curPage, int numPerPage, String id) {
		String sql = "select * from t_res_storagevolume where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,
				args.toArray(), curPage, numPerPage);
	}

	// 添加
	public void addStorage(List<DataRow> subsystems, Integer subSystemID) {
		subsystems.get(0).set("subsystem_id", subSystemID);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert(
				"t_res_storagesubsystem", subsystems.get(0));
	}

	// 更新
	public void updateStorage(List<DataRow> subsystems, Integer subsystemId) {
		if (subsystems != null && subsystems.size() > 0) {
			for (DataRow dataRow : subsystems) {
				getJdbcTemplate(WebConstants.DB_DEFAULT).update(
						"t_res_storagesubsystem", dataRow, "subsystem_id",
						subsystemId);
			}
		}
	}

	//
	@SuppressWarnings("unchecked")
	public List<DataRow> reportStorage(Integer subSystemID) {
		String sql = "select * from t_res_storagesubsystem where subsystem_id = "
				+ subSystemID;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}

	@SuppressWarnings("unchecked")
	public Long checkPrimaryKey(Long key) {
		String sql = "select T.* from t_res_storagesubsystem T where T.subsystem_id = "
				+ key;
		List<DataRow> lists = getJdbcTemplate(WebConstants.DB_DEFAULT).query(
				sql);
		if (lists != null && lists.size() > 0) {
			return null;
		}
		return key;
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getSystemByName(String name) {
		String sql = "select s.* from t_res_storagesubsystem s where s.name='"
				+ name + "' order by s.UPDATE_TIMESTAMP desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}

	public void clearTable() {
		String sql = "TRUNCATE TABLE t_res_storagesubsystem";
		getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}

	// storage_subsystem_IOps
	// storage_subsystem_MBps
	// storage_subsystem_HIT
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubsystemPref(Integer subSystemID,
			String startTime, String endTime) {
		List<Object> args = new ArrayList<Object>();
		String sql = "SELECT SUM(V.READ_KB) AS SUM_READ_KB,SUM(V.WRITE_KB) AS SUM_WRITE_KB,SUM(V.READ_HIT_IO) AS SUM_READ_HIT,SUM(V.READ_IO) AS SUM_READ_IO,SUM(V.WRITE_IO) AS SUM_WRITE_IO,T.* ";
		StringBuffer sb = new StringBuffer(sql);
		if (startTime != null && startTime.length() > 0) {
			Date start = null;
			try {
				start = dateFmt.parse(startTime);
				Long currentTime = new Date().getTime();
				Long lastTime = start.getTime();
				if (currentTime - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {// 大于20天查天性能表
					sb.append("FROM t_prf_storagevolume_perday V,t_prf_timestamp3 T WHERE T.TIME_ID = V.time_id ");
				} else if (currentTime - lastTime > SrContant.SEARCH_IN_PERHOURPERF) { // 大于2天查小时性能表
					sb.append("FROM t_prf_storagevolume_perhour V,t_prf_timestamp2 T WHERE T.TIME_ID = V.time_id ");
				} else {
					sb.append("FROM t_prf_storagevolume V,t_prf_timestamp T WHERE T.TIME_ID = V.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			sb.append("FROM t_prf_storagevolume V,t_prf_timestamp T WHERE T.TIME_ID = V.time_id ");
		}
		if (subSystemID != null && subSystemID != 0) {
			sb.append("and T.SUBSYSTEM_ID= ? ");
			args.add(subSystemID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and T.sampl_time > ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and T.sampl_time < ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
				sb.append("and T.sampl_time > ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(dateFmt.format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY V.TIME_ID ORDER BY T.SAMPL_TIME");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),
				args.toArray());
	}

	// storage_subsystem_responseTime
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubsystemResponseTime(Integer subsystemID,
			String startTime, String endTime) {
		String sql = "SELECT SUM(V.SEND_IO) AS SUM_SEND_RES,SUM(V.RECV_IO) AS SUM_RECV_RES,SUM(V.SEND_TIME) AS SUM_SEND_IO_TIME,SUM(V.RECV_TIME) AS SUM_RECV_IO_TIME,T.* ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if (startTime != null && startTime.length() > 0) {
			Date start = null;
			try {
				start = dateFmt.parse(startTime);
				Long currentTime = new Date().getTime();
				Long lastTime = start.getTime();
				if (currentTime - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {// 大于20天查天性能表
					sb.append("FROM t_prf_port_perday V,t_prf_timestamp3 T WHERE T.TIME_ID = V.time_id ");
				} else if (currentTime - lastTime > SrContant.SEARCH_IN_PERHOURPERF) { // 大于2天查小时性能表
					sb.append("FROM t_prf_port_perhour V,t_prf_timestamp2 T WHERE T.TIME_ID = V.time_id ");
				} else {
					sb.append("FROM t_prf_port V,t_prf_timestamp T WHERE T.TIME_ID = V.time_id ");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			sb.append("FROM t_prf_port V,t_prf_timestamp T WHERE T.TIME_ID = V.time_id ");
		}
		if (subsystemID != null && subsystemID != 0) {
			sb.append("and T.SUBSYSTEM_ID=? ");
			args.add(subsystemID);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and T.sampl_time > ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and T.sampl_time < ? ");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
				sb.append("and T.sampl_time > ? ");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(dateFmt.format(calendar.getTime()));
			}
		}
		sb.append("GROUP BY V.TIME_ID ORDER BY T.SAMPL_TIME");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),
				args.toArray());
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getSubsystemNames(String type,String limitIds) {
		StringBuffer sql = new StringBuffer("select subsystem_id as id,name,storage_type as type from t_res_storagesubsystem where 1 = 1 ");
		if (limitIds != null && limitIds.length() > 0) {
			sql.append("and subsystem_id in (" + limitIds + ") ");
		}
		if (type != null && type.length() > 0) {
			sql.append("and storage_type = '" + type + "'");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}

	/**
	 * 获取存储导出数据
	 * 
	 * @param name
	 * @param ipAddress
	 * @param type
	 * @param serialNumber
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getExportStorageList(String name, String ipAddress, String type, String serialNumber,Integer startcapacity,
			Integer endcapacity, Integer startunallocatedcapacity, Integer endunallocatedcapacity, String limitIds) {
		StringBuffer sb = new StringBuffer("select t.*,t.update_timestamp as last_probe_time from " +
				"(select vendor_name,name as the_display_name,ip_address,operational_status as the_propagated_status,physical_disk_capacity as the_physical_disk_space," 
			+ "total_usable_capacity as the_storage_pool_consumed_space,unallocated_usable_capacity as the_storage_pool_available_space,total_lun_capacity as the_volume_space,"
			+ "(total_lun_capacity - unmapped_lun_capacity) as the_assigned_volume_space,unmapped_lun_capacity as the_unassigned_volume_space," +
					"DATE_FORMAT(update_timestamp,'%Y/%m/%d %H:%i:%s') as update_timestamp," 
			+ "cache_gb as cache from t_res_storagesubsystem where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sb.append(" and subsystem_id in (" + limitIds + ") ");
		}
		if (StringHelper.isNotEmpty(name) && StringHelper.isNotBlank(name)) {
			sb.append(" and name like ? ");
			args.add("%" + name + "%");
		}
		if (StringHelper.isNotEmpty(ipAddress) && StringHelper.isNotBlank(ipAddress)) {
			sb.append(" and ip_address = ? ");
			args.add(ipAddress);
		}
		if (StringHelper.isNotEmpty(type) && StringHelper.isNotBlank(type)) {
			sb.append(" and storage_type = ? ");
			args.add(type);
		}
		if (StringHelper.isNotEmpty(serialNumber) && StringHelper.isNotBlank(serialNumber)) {
			sb.append(" and serial_number = ? ");
			args.add(serialNumber);
		}
		if (startcapacity != null && startcapacity > 0) {
			sb.append(" and total_usable_capacity >= ? ");
			args.add(startcapacity);
		}
		if (endcapacity != null && endcapacity > 0) {
			sb.append(" and total_usable_capacity <= ? ");
			args.add(endcapacity);
		}
		if (startunallocatedcapacity != null && startunallocatedcapacity > 0) {
			sb.append(" and unallocated_usable_capacity >= ? ");
			args.add(startunallocatedcapacity);
		}
		if (endunallocatedcapacity != null && endunallocatedcapacity > 0) {
			sb.append(" and unallocated_usable_capacity <= ? ");
			args.add(endunallocatedcapacity);
		}
		sb.append(") t");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}

	/**
	 * 获取存储客户端命名
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageNames(String limitIds) {
		String sql = String.format("select display_name as the_backend_name,name as the_display_name,subsystem_id,"
						+ "'%s' as dbType from t_res_storagesubsystem where 1 = 1",SrContant.DBTYPE_SR);
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + " and subsystem_id in (" + limitIds + ")";
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}

	Session session = null;

	/**
	 * @see 如果插入成功就返回true
	 * @param list
	 * @return
	 */
	public boolean storageRename(List<DataRow> list) {
		if(list == null || list.size() == 0){ return true; }
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			long id, temp;
			for (DataRow row : list) {
				id = row.getLong("device_id");
				temp = session.queryLong(
						"select count(1) from t_res_storagesubsystem where subsystem_id = " + id);
//						.query("select * from t_res_storagesubsystem where subsystem_id = "
//								+ id);
				if (temp > 0L) {
					session.update("update t_res_storagesubsystem set name = ? where subsystem_id = ?",
							new Object[] { row.getString("custom_name"), id });
				}
			}
			session.commitTrans();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (session != null) {
				session.rollbackTrans();
			}
			return false;
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}

}
