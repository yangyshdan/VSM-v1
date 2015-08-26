package com.huiming.service.storagesystem;

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

public class StorageSystemService extends BaseService{
	
	public DBPage getStoragePage(int curPage,int numPerPage,String name,String ip){
		StringBuffer sql = new StringBuffer("select * from t_res_storagesubsystem where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sql.append(" and name like ? ");
			args.add("%"+name+"%");
		}
		if(ip!=null && ip.length()>0){
			sql.append(" and ip_address like ? ");
			args.add("%"+ip+"%");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(), curPage, numPerPage);
	}
	
	public DataRow getStorageById(String id){
		List<Object> args = new ArrayList<Object>();
		String sql = "select * from t_res_storagesubsystem where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if(id!=null && id.length()>0){
			sb.append("and subsystem_id = ? ");
			args.add(id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageInfoById(Integer id){
		List<Object> args = new ArrayList<Object>();
		String sql = "select * from t_res_storagesubsystem where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if(id!=null && id!=0){
			sb.append("and subsystem_id = ? ");
			args.add(id);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageByName(String name){
		List<Object> args = new ArrayList<Object>();
		String sql = "select * from t_res_storagesubsystem where name = ?";
		args.add(name);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,args.toArray());
	}
	
	/**
	 * 查询存储系统下的端口
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getPortById(int curPage,int numPerPage,String id){
		String sql = "select * from t_res_port where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,args.toArray(),curPage, numPerPage);
	}
	
	/**
	 * 查询存储系统下的磁盘组
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getDiskGroupById(int curPage,int numPerPage,String id){
		String sql = "select * from t_res_diskgroup where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,args.toArray(),curPage, numPerPage);
	}
	
	/**
	 * 查询所有的存储系统
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageCapacityInfo(){
		String sql = "select * from t_res_storagesubsystem";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 查询存储系统下的池
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getPoolById(int curPage,int numPerPage,String id){
		String sql = "select * from t_res_storagepool where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,args.toArray(),curPage, numPerPage);
	}
	
	/**
	 * 查询存储系统下的卷
	 * @param curPage
	 * @param numPerPage
	 * @param id
	 * @return
	 */
	public DBPage getVolumeById(int curPage,int numPerPage,String id){
		String sql = "select * from t_res_storagevolume where subsystem_id = ?";
		List<Object> args = new ArrayList<Object>();
		args.add(id);
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,args.toArray(),curPage, numPerPage);
	}
	
	//添加
	public void addStorage(List<DataRow> subsystems,Integer subSystemID,String storageType){
		subsystems.get(0).set("subsystem_id", subSystemID);
		subsystems.get(0).set("storage_type", storageType);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagesubsystem", subsystems.get(0));
	}
	//更新
	public void updateStorage(List<DataRow> subsystems,Integer subsystemId,String storageType){
		if(subsystems!=null && subsystems.size()>0){
			for (DataRow dataRow : subsystems) {
				dataRow.set("storage_type", storageType);
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagesubsystem", dataRow, "subsystem_id", subsystemId);
			}
		}
	}
	
	//
	@SuppressWarnings("unchecked")
	public List<DataRow> reportStorage(Integer subSystemID){
		String sql = "select * from t_res_storagesubsystem where subsystem_id = "+subSystemID;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	} 
	
	@SuppressWarnings("unchecked")
	public Long checkPrimaryKey(Long key){
		String sql="select T.* from t_res_storagesubsystem T where T.subsystem_id = "+key;
		List<DataRow> lists = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
		if(lists!=null && lists.size()>0){
			return null;
		}
		return key;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSystemByName(String name){
		String sql="select s.* from t_res_storagesubsystem s where s.name='"+name+"' order by s.UPDATE_TIMESTAMP desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public void clearTable(){
		String sql = "TRUNCATE TABLE t_res_storagesubsystem";
		getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 获取存储系统性能信息
	 * @param subSystemID
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubsystemPerfInfo(Integer subSystemID,String startTime,String endTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		StringBuffer sb = new StringBuffer("select dev_id,ele_id,ele_name,prf_timestamp,");
		List<Object> args = new ArrayList<Object>();
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("A100_01 as read_iops,A100_02 as write_iops,A100_03 as total_iops,A100_07 as read_mbps,A100_08 as write_mbps,A100_09 as total_mbps from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("A106_01 as read_iops,A106_02 as write_iops,A106_03 as total_iops,A106_07 as read_mbps,A106_08 as write_mbps,A106_09 as total_mbps from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("A112_01 as read_iops,A112_02 as write_iops,A112_03 as total_iops,A112_07 as read_mbps,A112_08 as write_mbps,A112_09 as total_mbps from ");
		//For NETAPP
		} else if (storageType.equalsIgnoreCase(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			sb.append("NA100_06 as read_iops,NA100_07 as write_iops,NA100_08 as total_iops,NA100_14 as read_mbps,NA100_15 as write_mbps,(NA100_14+NA100_15) as total_mbps from ");
		}
	
		if (startTime != null && startTime.length() > 0) {
			Date start = null;
			try {
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
				Long currentTime = new Date().getTime();
				Long lastTime = start.getTime();
				//大于20天查天性能数据
				if (currentTime - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_DAILY);
				//大于2天查小时性能数据
				} else if (currentTime - lastTime > SrContant.SEARCH_IN_PERHOURPERF) {  
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
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and prf_timestamp > ?");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append(" and prf_timestamp < ?");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
				sb.append(" and prf_timestamp > ?");
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
	 * 从端口性能视图获取存储系统响应时间性能信息
	 * @param subsystemID
	 * @param startTime
	 * @param endTime
	 * @param paramRow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubsystemResponseTime(Integer subsystemID,String startTime,String endTime,DataRow paramRow){
		String storageType = paramRow.getString(SrTblColConstant.PF_STORAGE_TYPE).trim();
		String dbType = paramRow.getString(SrTblColConstant.PF_DBTYPE).trim();
		String viewName = paramRow.getString(SrTblColConstant.PF_VIEW).trim();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select prf_timestamp,");
		//For SVC
		if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC)) {
			sb.append("SUM(A103_01) as sum_send_io,SUM(A103_02) as sum_recv_io,");
			sb.append("SUM(A103_04) as sum_send_io_time,SUM(A103_05) as sum_recv_io_time from ");
		//For HDS
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)) {
			sb.append("SUM(A109_01) as sum_send_io,SUM(A109_02) as sum_recv_io,");
			sb.append("SUM(A109_04) as sum_send_io_time,SUM(A109_05) as sum_recv_io_time from ");
		//For EMC
		} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC)) {
			sb.append("SUM(A115_01) as sum_send_io,SUM(A115_02) as sum_recv_io,");
			sb.append("SUM(A115_04) as sum_send_io_time,SUM(A115_05) as sum_recv_io_time from ");
		//For NETAPP
		} else if (storageType.equalsIgnoreCase(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			sb.append("SUM(NA102_01) as sum_send_io,SUM(NA102_02) as sum_recv_io,");
			sb.append("SUM(NA102_04) as sum_send_io_time,SUM(NA102_05) as sum_recv_io_time from ");
		}
		
		if(startTime != null && startTime.length() > 0){
			Date start = null;
			try {
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
				Long currentTime = new Date().getTime();
				Long lastTime = start.getTime();
				//大于20天查天性能数据
				if (currentTime - lastTime > SrContant.SEARCH_IN_PERDAYPERF) {
					sb.append(viewName + SrTblColConstant.VIEW_SUFFIX_DAILY);
				//大于2天查小时性能数据
				} else if (currentTime - lastTime > SrContant.SEARCH_IN_PERHOURPERF) {
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
		if (startTime != null && startTime.length() > 0) {
			sb.append(" and prf_timestamp > ?");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append(" and prf_timestamp < ?");
			args.add(endTime);
		}
		if (startTime == null || startTime.length() == 0) {
			if (endTime == null || endTime.length() == 0) {
				sb.append(" and prf_timestamp > ?");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.HOUR, -SrContant.DEFAULT_REF_HOUR);
				args.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		sb.append(" group by prf_timestamp order by prf_timestamp");
		//判断查询的数据库
		if (dbType.equals(SrContant.DBTYPE_SR)) {
			return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
		}
		return null;
	}
	
	/**
	 * 添加存储系统信息
	 * @param subsystems
	 * @param subsystemId
	 * @return
	 */
	public String insertOrUpdateStorage(List<DataRow> subsystems,Integer subsystemId){
		String subSysId = null; 
		if (subsystems != null && subsystems.size() > 0) {
			DataRow subsystem = subsystems.get(0);
			subsystem.set("subsystem_id", subsystemId);
			DataRow temp = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_res_storagesubsystem where subsystem_id = ? ",new Object[]{subsystemId});
			if(temp != null && temp.getInt("subsystem_id") > 0){
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagesubsystem", subsystem, "subsystem_id", subsystemId);
				subSysId = String.valueOf(subsystemId);
			} else {
				DataRow temp2 = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_res_storagesubsystem where name= ? ",new Object[]{subsystem.getString("name")});
				if (temp2 == null) {
					subSysId = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagesubsystem", subsystem);
				} else {
					subSysId = temp2.getString("subsystem_id");
				}
			}
		}
		return subSysId;
	}
	
	/**
	 * 添加存储系统信息
	 * @param subsystems
	 * @param subsystemId
	 * @param storageType
	 * @return
	 */
	public String insertOrUpdateStorage(List<DataRow> subsystems,Integer subsystemId,String storageType){
		String subSysId = null; 
		if (subsystems != null && subsystems.size() > 0) {
			DataRow subsystem = subsystems.get(0);
			subsystem.set("subsystem_id", subsystemId);
			subsystem.set("storage_type", storageType);
			DataRow temp = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_res_storagesubsystem where subsystem_id = ? ",new Object[]{subsystemId});
			if(temp != null && temp.getInt("subsystem_id") > 0){
				subSysId = String.valueOf(subsystemId);
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagesubsystem", subsystem, "subsystem_id", subsystemId);
			}else{
				DataRow temp2 = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_res_storagesubsystem where name= ? ",new Object[]{subsystem.getString("name")});
				if (temp2 == null) {
					subSysId = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagesubsystem", subsystem);
				} else {
					subSysId = temp2.getString("subsystem_id");
				}
			}
		}
		return subSysId;
	}
	
	/**
	 * 获取存储系统性能信息(针对NETAPP类型存储)
	 * @param timeIds
	 * @return
	 */
	public List<DataRow> getPrfSystems(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select subsystem_id,subsystem_name,");
		sb.append("AVG(nfs_ops) as nfs_ops,AVG(cifs_ops) as cifs_ops,");
		sb.append("AVG(http_ops) as http_ops,AVG(fcp_ops) as fcp_ops,");
		sb.append("AVG(iscsi_ops) as iscsi_ops,AVG(read_ops) as read_ops,");
		sb.append("AVG(write_ops) as write_ops,AVG(total_ops) as total_ops,");
		sb.append("AVG(sys_avg_latency) as sys_avg_latency,AVG(net_data_recv) as net_data_recv,");
		sb.append("AVG(net_data_sent) as net_data_sent,AVG(fcp_data_recv) as fcp_data_recv,");
		sb.append("AVG(fcp_data_sent) as fcp_data_sent,AVG(disk_data_read) as disk_data_read,");
		sb.append("AVG(disk_data_written) as disk_data_written,AVG(hdd_data_read) as hdd_data_read,");
		sb.append("AVG(hdd_data_written) as hdd_data_written,AVG(ssd_data_read) as ssd_data_read,");
		sb.append("AVG(ssd_data_written) as ssd_data_written,AVG(cpu_busy) as cpu_busy,");
		sb.append("AVG(avg_processor_busy) as avg_processor_busy,AVG(total_processor_busy) as total_processor_busy");
		sb.append(" from t_prf_nas where time_id in (" + timeIds + ")");
		sb.append(" group by subsystem_id,subsystem_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加存储系统性能信息
	 * @param prfPorts
	 */
	public void addPrfSystems(List<DataRow> prfSystems) {
		for (int i = 0; i < prfSystems.size(); i++) {
			DataRow prfSystem = prfSystems.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_nas", prfSystem);
		}
	}
	
	/**
	 * 添加节点性能信息(小时/天)
	 * @param prfSystems
	 */
	public void addPerHourAndDayPrfSystems(List<DataRow> prfSystems) {
		for (int i = 0; i < prfSystems.size(); i++) {
			DataRow row = prfSystems.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_nas", row);
		}
	}
}
