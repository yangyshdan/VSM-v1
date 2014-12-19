package com.huiming.service.agent;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.apps.AppsService;
import com.project.web.WebConstants;
/**
 * @Name AgentService
 * @Author gugu
 * @Date 2013-12-17上午11:36:28
 * @Description TODO
 */
public class AgentService extends BaseService{
	/**
	 * 查询所有的物理机名称和id
	 * @Title: getHpNameAndID
	 * @Description: TODO
	 * @return
	 * List<DataRow>
	 */
	public List<DataRow> getHpNameAndID(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select hypervisor_id,host_computer_id ,hmc_id,name from t_res_hypervisor");
	}
	
	public DataRow getHpById(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(" select c.processor_count,c.ram_size from t_res_hypervisor h INNER JOIN t_res_computersystem c ON c.computer_id=h.host_computer_id where h.hypervisor_id = ?", new Object[]{id});
	}
	
	public List<DataRow> getHpNameAndIDByHmcId(int hmcId){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select hypervisor_id,host_computer_id ,hmc_id,name from t_res_hypervisor where hmc_id = ? ",new Object[]{hmcId});
	}
	
	/**
	 * 插入一条物理机信息
	 * @Title: insertHypervisor
	 * @Description: TODO
	 * @param row
	 * void
	 */
	public void insertOrUpdateHypervisor(DataRow row){
		DataRow computer = new DataRow();
		computer.set("name", row.getString("name"));
//		computer.set("display_name", row.getString("name"));
		computer.set("ip_address", row.getString("ip_address"));
		computer.set("os_version", row.getString("os_version"));
		computer.set("time_zone", row.getString("time_zone"));
		computer.set("detectable", 1);
//		computer.set("uid", row.getString("uid"));
//		computer.set("vendor", row.getString("vendor"));
		computer.set("processor_count", row.getInt("processor_count"));
		computer.set("ram_size", row.getInt("ram_size"));
		computer.set("model", row.getString("model"));
		computer.set("cpu_architecture", row.getString("cpu_architecture"));
		computer.set("is_virtual", 0);
		computer.set("operational_status", row.getString("operational_status"));
		computer.set("update_timestamp", DateHelper.formatTime(new Date()));
		computer.set("type", row.getString("type"));
		DataRow hypervisor = new DataRow();
		hypervisor.set("detectable", 1);
		hypervisor.set("name", row.getString("name"));
		hypervisor.set("hmc_id", row.getInt("hmc_id"));
		hypervisor.set("serial_num", row.getString("serial_num"));
		hypervisor.set("available_cpu", row.getString("available_cpu"));
		hypervisor.set("available_mem", row.getString("available_mem"));
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			if(row.getInt("hypervisor_id") > 0){
				session.update("t_res_computersystem", computer, "computer_id",  row.getInt("host_computer_id"));
				session.update("t_res_hypervisor", hypervisor, "hypervisor_id", row.getInt("hypervisor_id"));
			}else{
				String id = session.insert("t_res_computersystem", computer);
				hypervisor.set("host_computer_id", id);
				session.insert("t_res_hypervisor", hypervisor);
			}
			session.commitTrans();
		} catch (Exception e) {
			e.printStackTrace();
			if (session != null)
			{
				session.rollbackTrans();
			}
		}finally{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
	}
	
	/**
	 * 批量插入物理机信息
	 * @Title: batchInsertHp
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchInsertHp(List<DataRow> list){
		for (DataRow row : list) {
			insertOrUpdateHypervisor(row);
		}
	}
	
	/**
	 * 批量修改物理机信息
	 * datarow必备信息：HYPERVISOR_ID、COMPUTER_ID
	 * @Title: updateHpById
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchUpdateHp(List<DataRow> list){
		for (DataRow row : list) {
			insertOrUpdateHypervisor(row);
		}
	}
	
	/**
	 * 批量修改物理机CPU和MEM信息
	 * datarow必备信息：HYPERVISOR_ID、COMPUTER_ID
	 * @Title: batchUpdateCpuAndMem
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchUpdateCpuAndMem(List<DataRow> list){
		String[][] args = new String[list.size()][];
		String sql = "update t_res_computersystem set processor_count = ?,ram_size = ? where computer_id = ?";
		for (int i = 0; i < list.size(); i++) {
			DataRow row = list.get(i);
			 String[] temp = new String[3];
			 temp[0] = row.getString("processor_count")==""?"0":row.getString("processor_count");
			 temp[1] = row.getString("ram_size")==""?"0":row.getString("ram_size");
			 temp[2] = row.getString("computer_id");
			 args[i] = temp;
		}
		getJdbcTemplate(WebConstants.DB_DEFAULT).batchUpdate(sql, args);
			
			 
	}
	
	/**
	 * 查询所有虚拟机的名称和id
	 * @Title: getVirtualNameAndId
	 * @Description: TODO
	 * @return
	 * List<DataRow>
	 */
	public List<DataRow> getVirtualNameAndId(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select a.vm_id ,a.computer_id,a.hypervisor_id,a.name,b.display_name,a.host_name,a.targeted_os,b.ip_address  from t_res_virtualmachine a inner join t_res_computersystem b on a.computer_id = b.computer_id where a.hypervisor_id = ? ",new Object[]{id});
	}
	
	/**
	 * 查询所有可探测虚拟机的名称和id
	 * @Title: getVirtualNameAndId
	 * @Description: TODO
	 * @return
	 * List<DataRow>
	 */
	public List<DataRow> getVirtualNameAndIdIsDetectable(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select a.vm_id ,a.computer_id,a.hypervisor_id,a.name,b.display_name,a.host_name,a.targeted_os  from t_res_virtualmachine a inner join t_res_computersystem b on a.computer_id = b.computer_id where b.detectable=1 AND a.hypervisor_id = ? ",new Object[]{id});
	}
	
	/**
	 * 查询虚拟机的computerId
	 * @Title
	 * @return
	 * List<DataRow>
	 */
	public List<DataRow> getVirtualComputerId(String name){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("SELECT computer_id FROM t_res_virtualmachine trv WHERE NAME IN("+name+")");
		
	}
	
	
	/**
	 * 插入或者更新一条虚拟机信息
	 * @Title: insertOrUpdateVirtual
	 * @Description: TODO
	 * @param row
	 * void
	 */
	public void insertOrUpdateVirtual(DataRow row){
		DataRow computer  = new DataRow();
		DataRow virtual  = new DataRow();
		virtual.set("hypervisor_id", row.getString("hypervisor_id"));
		virtual.set("name", row.getString("name"));
		virtual.set("targeted_os", row.getString("targeted_os"));
		virtual.set("assigned_cpu_number", row.getString("assigned_cpu_number")==""?0:row.getString("assigned_cpu_number"));
		virtual.set("assigned_cpu_processunit", row.getString("assigned_cpu_processunit")==""?0:row.getString("assigned_cpu_processunit"));
		virtual.set("maximum_cpu_number", row.getString("maximum_cpu_number")==""?0:row.getString("maximum_cpu_number"));
		virtual.set("maximum_cpu_processunit", row.getString("maximum_cpu_processunit")==""?0:row.getString("maximum_cpu_processunit"));
		virtual.set("minimum_cpu_number", row.getString("minimum_cpu_number")==""?0:row.getString("minimum_cpu_number"));
		virtual.set("minimum_cpu_processunit", row.getString("minimum_cpu_processunit")==""?0:row.getString("minimum_cpu_processunit"));
		virtual.set("total_memory", row.getInt("total_memory"));
		virtual.set("host_name", row.getString("host_name"));
		virtual.set("processing_mode", row.getString("processing_mode"));
		virtual.set("update_timestamp", DateHelper.formatTime(new Date()));
		computer.set("type", row.getString("type"));
		computer.set("name", row.getString("name"));
//		computer.set("display_name", row.getString("name"));
		computer.set("os_version", row.getString("targeted_os"));
		computer.set("operational_status", row.getString("operational_status"));
		if(!StringHelper.isEmpty(row.getString("ip_address"))){
			computer.set("ip_address", row.getString("ip_address"));
		}
		computer.set("processor_count", row.getInt("assigned_cpu_number"));
		computer.set("ram_size", row.getInt("total_memory"));
		computer.set("detectable", row.getInt("detectable"));
		computer.set("is_virtual", 1);
		computer.set("update_timestamp", DateHelper.formatTime(new Date()));
		Session session = null;
		int vmId = row.getInt("vm_id");
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			if(row.getInt("vm_id") > 0){
				session.update("t_res_computersystem", computer, "computer_id",  row.getInt("computer_id"));
				session.update("t_res_virtualmachine", virtual, "vm_id", row.getInt("vm_id"));
			}else{
				String id = session.insert("t_res_computersystem", computer);
				virtual.set("computer_id", id);
				vmId = Integer.parseInt(session.insert("t_res_virtualmachine", virtual));
			}
			session.commitTrans();
			new AppsService().aotuMapping(vmId,  row.getString("name"));
		} catch (Exception e) {
			e.printStackTrace();
			if (session != null)
			{
				session.rollbackTrans();
			}
		}finally{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
	}
	
	/**
	 * 更新虚拟机IP和磁盘空间信息
	 * @param row
	 */
	public void updateComputer(DataRow row){
		Session session = null;
		try {
			if(row != null && row.size() > 1){
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
				session.update("t_res_computersystem", row, "computer_id",  row.getInt("computer_id"));
				session.commitTrans();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (session != null)
			{
				session.rollbackTrans();
			}
		}finally{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
	}
	
	/**
	 * 批量插入虚机信息
	 * @Title: batchInsertVirtual
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchInsertVirtual(List<DataRow> list){
		for (DataRow row : list) {
			insertOrUpdateVirtual(row);
		}
	}
	
	/**
	 * 批量更新虚机信息
	 * datarow必备信息：VM_ID、COMPUTER_ID
	 * @Title: batchUpateVirtual
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchUpateVirtual(List<DataRow> list){
		for (DataRow row : list) {
			insertOrUpdateVirtual(row);
		}
	}
	
	/**
	 * 批量更新已删除的虚机状态
	 * @Title: batchUpateState
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchUpateVmState(List<DataRow> list){
		String sql = "update t_res_computersystem set detectable = 0 where computer_id =  ? ";
		for (DataRow row : list) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql,new Object[]{row.getInt("computer_id")});
		}
	}
	
	/***
	 * 根据虚拟机名称查找虚拟机processingModel
	 */
	public DataRow getVirtualProcessingModeByName(String virtualName){
		String sql="SELECT processing_mode FROM t_res_virtualmachine  WHERE name = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{virtualName});
	}
	
	
	/**
	 * 批量更新已删除的物理机状态
	 * @Title: batchUpateState
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchUpateHpState(List<DataRow> list){
		Session session = null;
		String sql1 = "update t_res_hypervisor set detectable = 0 where hypervisor_id =  ? ";
		String sql2 = "update t_res_computersystem set detectable = 0 where computer_id =  ?  ";
		for (DataRow row : list) {
			try {
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
				session.update(sql1,new Object[]{row.getInt("hypervisor_id")});
				session.update(sql2,new Object[]{row.getInt("computer_id")});
				session.commitTrans();
			} catch (Exception e) {
				e.printStackTrace();
				if (session != null)
				{
					session.rollbackTrans();
				}
			}finally{
				if (session != null)
				{
					session.close();
					session = null;
				}
			}
		}
	}
	
	/**
	 * 批量插入性能信息
	 * datarow必备信息：SAMPLE_TIME、interval_len、SUMM_TYPE、SUBSYSTEM_NAME、SUBSYSTEM_ID
	 * SUMM_TYPE:1为即时信息，2为小时统计，3为天统计
	 * @Title: batchInsertPrf
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchInsertPrf(List<DataRow> list){
		Session session = null;
		for (DataRow row : list) {
			DataRow timestamp = new DataRow();
			DataRow prf = new DataRow();
			timestamp.set("sample_time", row.getString("sample_time"));
			timestamp.set("interval_len", row.getString("interval_len"));
			timestamp.set("summ_type", row.getString("summ_type"));
			timestamp.set("subsystem_name", row.getString("computer_name"));
			timestamp.set("subsystem_id", row.getString("computer_id"));
			timestamp.set("perf_marker", row.getString("perf_marker"));
			prf.set("computer_id", row.getString("computer_id"));
			prf.set("computer_name", row.getString("computer_name"));
			if(StringHelper.isNotEmpty(row.getString("cpu_busy_prct"))){
				prf.set("cpu_busy_prct", row.getString("cpu_busy_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("cpu_idle_prct") )){
				prf.set("cpu_idle_prct", row.getString("cpu_idle_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_used_prct"))){
				prf.set("mem_used_prct", row.getString("mem_used_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("processor_units_utilized"))){
				prf.set("processor_units_utilized", row.getString("processor_units_utilized"));
			}
			try {
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
				int time_id = getTimeId(row.getString("perf_marker"),row.getInt("computer_id"));
				if(time_id > 0){
					session.update("t_prf_computerper", prf, "time_id", time_id);
				}else{
					String id = session.insert("t_prf_timestamp", timestamp);
					prf.set("time_id", id);
					session.insert("t_prf_computerper", prf);
				}
				session.commitTrans();
			} catch (Exception e) {
				e.printStackTrace();
				if (session != null)
				{
					session.rollbackTrans();
				}
			}finally{
				if (session != null)
				{
					session.close();
					session = null;
				}
			}
		}
	}
	
	public int getPerfMarker(String uuid,int computerId){
		int flag = 0;
		String sql = "select time_id from t_prf_timestamp where perf_marker = ? and subsystem_id = ? ";
		flag = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql,new Object[]{uuid,computerId});
		return flag;
	}
	
	public int getTimeId(String marker,int computerId){
		int flag = 0;
		String sql = "select time_id from t_prf_timestamp where perf_marker = ? and subsystem_id = ? ";
		flag = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql,new Object[]{marker,computerId});
		return flag;
	}
	
	/**
	 * 批量插入性能数据(第三阶段)
	 * datarow必备信息：SAMPLE_TIME、interval_len、SUMM_TYPE、SUBSYSTEM_NAME、SUBSYSTEM_ID
	 * SUMM_TYPE:1为即时信息，2为小时统计，3为天统计
	 * @Title: batchInsertPrf
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchInsertPrf3(List<DataRow> list){
		Session session = null;
		for (DataRow row : list) {
			DataRow timestamp = new DataRow();
			DataRow prf = new DataRow();
			timestamp.set("sample_time", row.getString("sample_time"));
			timestamp.set("interval_len", row.getString("interval_len"));
			timestamp.set("summ_type", row.getString("summ_type"));
			timestamp.set("subsystem_name", row.getString("computer_name"));
			timestamp.set("subsystem_id", row.getString("computer_id"));
			prf.set("computer_id", row.getString("computer_id"));
			prf.set("computer_name", row.getString("computer_name"));
			if(StringHelper.isNotEmpty(row.getString("cpu_usr_prct"))){
				prf.set("cpu_usr_prct", row.getString("cpu_usr_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("cpu_wait_prct"))){
				prf.set("cpu_sys_prct", row.getString("cpu_sys_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("cpu_wait_prct"))){
				prf.set("cpu_wait_prct", row.getString("cpu_wait_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("cpu_busy_prct"))){
				prf.set("cpu_busy_prct", row.getString("cpu_busy_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("cpu_idle_prct") )){
				prf.set("cpu_idle_prct", row.getString("cpu_idle_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_used_prct"))){
				prf.set("mem_used_prct", row.getString("mem_used_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_recv_kb"))){
				prf.set("net_recv_kb", row.getString("net_recv_kb"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_send_kb"))){
				prf.set("net_send_kb", row.getString("net_send_kb"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_recv_packet"))){
				prf.set("net_recv_packet", row.getString("net_recv_packet"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_send_packet"))){
				prf.set("net_send_packet", row.getString("net_send_packet"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_readdatarate_kb"))){
				prf.set("disk_readdatarate_kb", row.getString("disk_readdatarate_kb"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_writedatarate_kb"))){
				prf.set("disk_writedatarate_kb", row.getString("disk_writedatarate_kb"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_read_iops"))){
				prf.set("disk_read_iops", row.getString("disk_read_iops"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_write_iops"))){
				prf.set("disk_write_iops", row.getString("disk_write_iops"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_overall_iops"))){
				prf.set("disk_overall_iops", row.getString("disk_overall_iops"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_read_await"))){
				prf.set("disk_read_await", row.getString("disk_read_await"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_write_await"))){
				prf.set("disk_write_await", row.getString("disk_write_await"));
			}
			try {
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
				int time_id = getPerfMarker(row.getString("perf_marker"),row.getInt("computer_id"));
				if(time_id > 0){
					session.update("t_prf_computerper", prf, "time_id", time_id);
				}else{
					String id = session.insert("t_prf_timestamp", timestamp);
					prf.set("time_id", id);
					session.insert("t_prf_computerper", prf);
				}
				session.commitTrans();
			} catch (Exception e) {
				e.printStackTrace();
				if (session != null)
				{
					session.rollbackTrans();
				}
			}finally{
				if (session != null)
				{
					session.close();
					session = null;
				}
			}
		}
	} 
	
	/***
	 * 获得虚拟机ip
	 * @return
	 */
	public List<DataRow> getVirtualIpList(){
		String sql="SELECT v.vm_id,v.name as computer_name,v.computer_id,t.ip_address,v.targeted_os FROM t_res_virtualmachine v,t_res_computersystem t WHERE t.computer_id = v.computer_id AND t.DETECTABLE = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	/**
	 * 获得物理机ip
	 * @return
	 */
	public List<DataRow> getHostIpList(){
		String sql="SELECT h.HYPERVISOR_ID,h.HOST_COMPUTER_ID as computer_id,h.name as computer_name,t.ip_address FROM t_res_hypervisor h,t_res_computersystem t WHERE h.HOST_COMPUTER_ID = t.COMPUTER_ID AND h.detectable = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 插入日志
	 * @Title: insertLog
	 * @Description: TODO
	 * @param logs
	 * void
	 */
	public void insertLog(List<DataRow> logs){
		for (DataRow log : logs) {
			String sql = "select fid from TnDeviceLog where fno = ? and ftopid = ? ";
			int id = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql, new Object[]{log.get("fno"),log.get("ftopid")});
			if(id < 1 ){
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("TnDeviceLog", log);
			}
		}
	}
	
	/**
	 * 删除过时性能数据
	 * @param interval_len_day 保留的性能时间长度(天)
	 */
	public void deleteOldPerfData(Integer interval_len_day){
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			session.update("DELETE FROM t_prf_timestamp WHERE SAMPLE_TIME < DATE_SUB(CURDATE(),INTERVAL ? day)",new Object[]{interval_len_day});
			session.commitTrans();
		} catch (Exception e) {
			if(session!=null){
				session.rollbackTrans();
			}
			e.printStackTrace();
		} finally{
			if(session!=null){
				session.close();
				session = null;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getHMCLoginInfo(){
		String sql="select * from t_acct_hmc where state = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public DataRow getVIOSLoginInfo(int vm_id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_acct_vios where state =1 and vm_id = "+vm_id);
	}
	
	public DataRow getHMCLoginInfo(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_acct_hmc where state =1 and id = "+id);
	}
	
	public DataRow getHyperVInfo(String serial_num){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select hypervisor_id as id,name from t_res_hypervisor where serial_num = ? ", new Object[]{serial_num});
	}
	
	public void insertHwres(Map<String, List<DataRow>> map){
		if(map != null && map.size() > 0 ){
			for (String hv_sno : map.keySet()) {
				if(map.get(hv_sno) != null && map.get(hv_sno).size() > 0){
					DataRow hv = getHyperVInfo(hv_sno);
					if(hv != null){
						Session session = null;
						try {
							session = getSession(WebConstants.DB_DEFAULT);
							session.beginTrans();
							session.delete("t_res_hwres", "hypervisor_id", hv.getString("id"));
							for (DataRow fibre : map.get(hv_sno)) {
								fibre.set("hypervisor_id", hv.getString("id"));
								fibre.set("update_timestamp", DateHelper.formatTime(new Date()));
								session.insert("t_res_hwres", fibre);
							}
							session.commitTrans();
						} catch (Exception e) {
							e.printStackTrace();
							if (session != null)
							{
								session.rollbackTrans();
							}
						}finally{
							if (session != null)
							{
								session.close();
								session = null;
							}
						}
				}
				
					
				}
			}
		}
	}
	
	public void insertDisk(Map<String, List<DataRow>> map){
		if(map != null && map.size() > 0 ){
			for (String key : map.keySet()) {
				if(map.get(key) != null && map.get(key).size() > 0){
					Session session = null;
					try {
						session = getSession(WebConstants.DB_DEFAULT);
						session.beginTrans();
						session.delete("t_res_vm2disk", "vm_id", key);
						for (DataRow disk : map.get(key)) {
							disk.set("update_timestamp", DateHelper.formatTime(new Date()));
							disk.set("vm_id", key);
							session.insert("t_res_vm2disk", disk);
						}
						session.commitTrans();
					} catch (Exception e) {
						e.printStackTrace();
						if (session != null)
						{
							session.rollbackTrans();
						}
					}finally{
						if (session != null)
						{
							session.close();
							session = null;
						}
					}
				}
			}
		}
	}
}
