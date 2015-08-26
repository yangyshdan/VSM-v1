package com.huiming.service.agent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.apps.AppsService;
import com.huiming.sr.constants.SrContant;
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
	@SuppressWarnings("unchecked")
	public List<DataRow> getHpNameAndID(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select hypervisor_id,host_computer_id,hmc_id,name from t_res_hypervisor");
	}
	
	/**
	 * 获取相应的物理机信息
	 * @param hypervisorId
	 * @return
	 */
	public DataRow getHypervisorInfo(String hypervisorId) {
		String sql = "select h.hypervisor_id,h.hmc_id,cs.computer_id,coalesce(cs.display_name,h.name) as computer_name,cs.ip_address,s.user,s.password,"
			+ "s.virt_plat_type from t_res_hypervisor h,t_res_computersystem cs,t_server s "
			+ "where h.hypervisor_id = ? and h.host_computer_id = cs.computer_id and h.hmc_id = s.id";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{hypervisorId});
	}
	
	
	/**
	 * 获取所有相应操作系统类型的物理机信息
	 * @param osType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPhysicalList(String osType) {
		String sql = "select h.hypervisor_id,h.host_computer_id,h.hmc_id,h.name from t_server hmc,t_res_hypervisor h where hmc.id = h.hmc_id and hmc.state = 1 and hmc.os_type = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{osType});
	}
	
	/**
	 * 获取所有具有虚拟化平台的物理机信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPhysicalListHaveHypv() {
		String sql = "select h.hypervisor_id,h.host_computer_id,h.hmc_id,h.name from t_server hmc,t_res_hypervisor h where hmc.id = h.hmc_id and hmc.state = 1 and hmc.virt_plat_type is not null and hmc.virt_plat_type <> ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{WebConstants.VIRT_PLAT_TYPE_NO});
	}
	
	/**
	 * 获取所有物理机信息
	 * @return
	 */
	public List<DataRow> getPhysicalList() {
		String sql = "select h.hypervisor_id,h.host_computer_id,h.hmc_id,h.name from t_server hmc,t_res_hypervisor h where hmc.id = h.hmc_id and hmc.state = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 获取所有虚拟机信息
	 * @return
	 */
	public List<DataRow> getVirtMachList() {
		String sql = "select vm.vm_id,vm.hypervisor_id,vm.computer_id,vm.hmc_id,vm.name from t_server hmc,t_res_virtualmachine vm where hmc.id = vm.hmc_id and hmc.state = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 插入一条物理机信息
	 * @Title: insertHypervisor
	 * @Description: TODO
	 * @param row
	 * void
	 */
	public String insertOrUpdateHypervisor(DataRow row){
		String hypervisorId = null;
		DataRow computer = new DataRow();
		computer.set("name", row.getString("name"));
//		computer.set("display_name", row.getString("name"));
		computer.set("ip_address", row.getString("ip_address"));
		if (row.getString("os_version").length() > 0) {
			computer.set("os_version", row.getString("os_version"));
		}
		if (row.getString("time_zone").length() > 0) {
			computer.set("time_zone", row.getString("time_zone"));
		}
		if (row.getString("detectable").length() > 0) {
			computer.set("detectable", row.getString("detectable"));
		}
		if (row.getString("uid").length() > 0) {
			computer.set("uid", row.getString("uid"));
		}
		if (row.getString("vendor").length() > 0) {
			computer.set("vendor", row.getString("vendor"));
		}
		if (row.getString("processor_count").length() > 0) {
			computer.set("processor_count", row.getString("processor_count"));
		}
		if (row.getString("processor_speed").length() > 0) {
			computer.set("processor_speed", row.getString("processor_speed"));
		}
		if (row.getString("ram_size").length() > 0) {
			computer.set("ram_size", row.getString("ram_size"));
		}
		if (row.getString("disk_space").length() > 0) {
			computer.set("disk_space", row.getString("disk_space"));
		}
		if (row.getString("disk_available_space").length() > 0) {
			computer.set("disk_available_space", row.getString("disk_available_space"));
		}
		if (row.getString("model").length() > 0) {
			computer.set("model", row.getString("model"));
		}
		if (row.getString("cpu_architecture").length() > 0) {
			computer.set("cpu_architecture", row.getString("cpu_architecture"));
		}
		computer.set("is_virtual", 0);
		if (row.getString("operational_status").length() > 0) {
			computer.set("operational_status", row.getString("operational_status"));
		}
		computer.set("update_timestamp", DateHelper.formatTime(new Date()));
		if (row.getString("type").length() > 0) {
			computer.set("type", row.getString("type"));
		}
		DataRow hypervisor = new DataRow();
		hypervisor.set("detectable", 1);
		hypervisor.set("name", row.getString("name"));
		hypervisor.set("hmc_id", row.getString("hmc_id"));
		if (row.getString("available_cpu").length() > 0) {
			hypervisor.set("available_cpu", row.getString("available_cpu"));
		}
		if (row.getString("available_mem").length() > 0) {
			hypervisor.set("available_mem", row.getString("available_mem"));
		}
		Session session = null;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			if(row.getInt("hypervisor_id") > 0){
				session.update("t_res_computersystem", computer, "computer_id", row.getInt("host_computer_id"));
				session.update("t_res_hypervisor", hypervisor, "hypervisor_id", row.getInt("hypervisor_id"));
				hypervisorId = String.valueOf(row.getInt("hypervisor_id"));
			}else{
				String id = session.insert("t_res_computersystem", computer);
				hypervisor.set("host_computer_id", id);
				hypervisorId = session.insert("t_res_hypervisor", hypervisor);
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
		return hypervisorId;
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
			 temp[0] = row.getString("processor_count");
			 temp[1] = row.getString("ram_size");
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
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select a.vm_id,a.computer_id,a.hypervisor_id,a.hmc_id,a.name,b.display_name,a.host_name,a.targeted_os from t_res_virtualmachine a inner join t_res_computersystem b on a.computer_id = b.computer_id where a.hypervisor_id = ? ",new Object[]{id});
	}
	
	/**
	 * 查询所有可探测虚拟机的名称和id
	 * @Title: getVirtualNameAndId
	 * @Description: TODO
	 * @return
	 * List<DataRow>
	 */
	public List<DataRow> getVirtualNameAndIdIsDetectable(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select a.vm_id,a.computer_id,a.hypervisor_id,a.hmc_id,a.name,b.display_name,a.host_name,a.targeted_os from t_res_virtualmachine a inner join t_res_computersystem b on a.computer_id = b.computer_id where b.detectable = 1 AND a.hypervisor_id = ? ",new Object[]{id});
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
		if (row.getString("targeted_os").length() > 0) {
			virtual.set("targeted_os", row.getString("targeted_os"));
		}
		if (row.getString("assigned_cpu_number").length() > 0) {
			virtual.set("assigned_cpu_number", row.getString("assigned_cpu_number"));
		}
		if (row.getString("assigned_cpu_processunit").length() > 0) {
			virtual.set("assigned_cpu_processunit", row.getString("assigned_cpu_processunit"));
		}
		if (row.getString("maximum_cpu_number").length() > 0) {
			virtual.set("maximum_cpu_number", row.getString("maximum_cpu_number"));
		}
		if (row.getString("maximum_cpu_processunit").length() > 0) {
			virtual.set("maximum_cpu_processunit", row.getString("maximum_cpu_processunit"));
		}
		if (row.getString("minimum_cpu_number").length() > 0) {
			virtual.set("minimum_cpu_number", row.getString("minimum_cpu_number"));
		}
		if (row.getString("minimum_cpu_processunit").length() > 0) {
			virtual.set("minimum_cpu_processunit", row.getString("minimum_cpu_processunit"));
		}
		if (row.getString("total_memory").length() > 0) {
			virtual.set("total_memory", row.getString("total_memory"));
		}
		if (row.getString("host_name").length() > 0) {
			virtual.set("host_name", row.getString("host_name"));
		}
		if (row.getString("processing_mode").length() > 0) {
			virtual.set("processing_mode", row.getString("processing_mode"));
		}
		virtual.set("update_timestamp", DateHelper.formatTime(new Date()));
		if (row.getString("type").length() > 0) {
			computer.set("type", row.getString("type"));
		}
		if (row.getString("detectable").length() > 0) {
			computer.set("detectable", row.getString("detectable"));
		}
		if (row.getString("name").length() > 0) {
			virtual.set("name", row.getString("name"));
			computer.set("name", row.getString("name"));
		}
//		computer.set("display_name", row.getString("name"));
		if (row.getString("targeted_os").length() > 0) {
			computer.set("os_version", row.getString("targeted_os"));
		}
		if (row.getString("os_version").length() > 0) {
			computer.set("os_version", row.getString("os_version"));
		}
		if (row.getString("processor_count").length() > 0) {
			computer.set("processor_count", row.getString("processor_count"));
		}
		if (row.getString("processor_speed").length() > 0) {
			computer.set("processor_speed", row.getString("processor_speed"));
		}
		if (row.getString("operational_status").length() > 0) {
			computer.set("operational_status", row.getString("operational_status"));
		}
		if (row.getString("ip_address").length() > 0) {
			computer.set("ip_address", row.getString("ip_address"));
		}
		if (row.getString("assigned_cpu_number").length() > 0) {
			computer.set("processor_count", row.getString("assigned_cpu_number"));
		}
		if (row.getString("total_memory").length() > 0) {
			computer.set("ram_size", row.getString("total_memory"));
		}
		if (row.getString("detectable").length() > 0) {
			computer.set("detectable", row.getString("detectable"));
		}
		computer.set("is_virtual", 1);
		if (row.getString("disk_space").length() > 0) {
			computer.set("disk_space", row.getString("disk_space"));
		}
		if (row.getString("disk_available_space").length() > 0) {
			computer.set("disk_available_space", row.getString("disk_available_space"));
		}
		if (row.getString("filesystem_available_space").length() > 0) {
			computer.set("filesystem_available_space", row.getString("filesystem_available_space"));
		}
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
	 * 批量插入物理机或虚拟机性能数据(t_prf_timestamp,t_prf_computerper)
	 * SUMM_TYPE:1为即时信息，2为小时统计，3为天统计
	 * @Title: batchInsertPrf
	 * @Description: TODO
	 * @param list
	 * void
	 */
	public void batchInsertServerPerf(List<DataRow> list){
		for (DataRow row : list) {
			DataRow timestamp = new DataRow();
			DataRow prf = new DataRow();
			timestamp.set("sample_time", row.getString("sample_time"));
			timestamp.set("interval_len", row.getString("interval_len"));
			timestamp.set("summ_type", row.getString("summ_type"));
			timestamp.set("subsystem_name", row.getString("computer_name"));
			timestamp.set("subsystem_id", row.getString("computer_id"));
			timestamp.set("device_type", row.getString("device_type"));
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
			if(StringHelper.isNotEmpty(row.getString("processor_units_utilized"))){
				prf.set("processor_units_utilized", row.getString("processor_units_utilized"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_proc_prct"))){
				prf.set("mem_proc_prct", row.getString("mem_proc_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_fscache_prct"))){
				prf.set("mem_fscache_prct", row.getString("mem_fscache_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_sys_prct"))){
				prf.set("mem_sys_prct", row.getString("mem_sys_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_free_prct"))){
				prf.set("mem_free_prct", row.getString("mem_free_prct"));
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
			String timeId = null;
			//检测数据库中是否有记录
			String timeSql = "select time_id from t_prf_timestamp where sample_time = ? and summ_type = ? and subsystem_id = ? and device_type = ?";
			DataRow timeRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(timeSql, new Object[]{row.getString("sample_time"),row.getString("summ_type"),row.getString("computer_id"),row.getString("device_type")});
			//存在记录
			if (timeRow != null && timeRow.size() > 0) {
				timeId = timeRow.getString("time_id");
			} else {
				//插入数据到数据库
				timeId = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_timestamp", timestamp);
			}
			if (timeId != null && timeId.length() > 0) {
				prf.set("time_id", timeId);
				//判断是否有记录存在,有则更新,否则就插入数据
				String comSql = "select time_id from t_prf_computerper where time_id = ? and computer_id = ?";
				DataRow comRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(comSql, new Object[]{timeId,row.getString("computer_id")});
				if (comRow != null && comRow.size() > 0) {
					getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_prf_computerper", prf, "time_id", timeId);
				} else {
					getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_computerper", prf);
				}
			}
		}
	} 
	
	/**
	 * 获取物理机或虚拟机性能信息(小时或天)
	 * @param timeIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPerHourAndDayComPerfInfos(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select computer_id,computer_name,avg(cpu_usr_prct) as cpu_usr_prct,avg(cpu_sys_prct) as cpu_sys_prct,");
		sb.append("avg(cpu_wait_prct) as cpu_wait_prct,avg(cpu_idle_prct) as cpu_idle_prct,");
		sb.append("avg(cpu_busy_prct) as cpu_busy_prct,avg(mem_proc_prct) as mem_proc_prct,");
		sb.append("avg(mem_fscache_prct) as mem_fscache_prct,avg(mem_sys_prct) as mem_sys_prct,");
		sb.append("avg(mem_free_prct) as mem_free_prct,avg(mem_used_prct) as mem_used_prct,");
		sb.append("avg(disk_readdatarate_kb) disk_readdatarate_kb,avg(disk_writedatarate_kb) disk_writedatarate_kb,");
		sb.append("avg(disk_read_iops) disk_read_iops,avg(disk_write_iops) disk_write_iops,avg(disk_overall_iops) as disk_overall_iops,");
		sb.append("sum(disk_read_iops*disk_read_await)/sum(disk_overall_iops) as disk_read_await,");
		sb.append("sum(disk_write_iops*disk_write_await)/sum(disk_overall_iops) as disk_write_await,");
		sb.append("avg(net_send_kb) as net_send_kb,avg(net_recv_kb) as net_recv_kb,");
		sb.append("avg(net_send_packet) as net_send_packet,avg(net_recv_packet) as net_recv_packet");
		sb.append(" from t_prf_computerper where time_id in (" + timeIds + ")");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加物理机或虚拟机性能信息(小时或天)
	 * @param comPerfiInfoList
	 */
	public void addPerHourAndDayComPerfInfos(List<DataRow> comPerfiInfoList) {
		for (int i = 0; i < comPerfiInfoList.size(); i++) {
			DataRow row = comPerfiInfoList.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_computerper", row);
		}
	}
	
	/**
	 * 获取HYPERVISOR虚拟机性能信息(小时或天)
	 * @param timeIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPerHourAndDayHypvVmPerf(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select computer_id,computer_name,avg(cpu_usr_prct) as cpu_usr_prct,avg(cpu_sys_prct) as cpu_sys_prct,");
		sb.append("avg(cpu_wait_prct) as cpu_wait_prct,avg(cpu_idle_prct) as cpu_idle_prct,");
		sb.append("avg(cpu_busy_prct) as cpu_busy_prct,avg(mem_proc_prct) as mem_proc_prct,");
		sb.append("avg(mem_fscache_prct) as mem_fscache_prct,avg(mem_sys_prct) as mem_sys_prct,");
		sb.append("avg(mem_free_prct) as mem_free_prct,avg(mem_used_prct) as mem_used_prct,");
		sb.append("avg(disk_readdatarate_kb) disk_readdatarate_kb,avg(disk_writedatarate_kb) disk_writedatarate_kb,");
		sb.append("avg(disk_read_iops) disk_read_iops,avg(disk_write_iops) disk_write_iops,avg(disk_overall_iops) as disk_overall_iops,");
		sb.append("sum(disk_read_iops*disk_read_await)/sum(disk_overall_iops) as disk_read_await,");
		sb.append("sum(disk_write_iops*disk_write_await)/sum(disk_overall_iops) as disk_write_await,");
		sb.append("avg(net_send_kb) as net_send_kb,avg(net_recv_kb) as net_recv_kb,");
		sb.append("avg(net_send_packet) as net_send_packet,avg(net_recv_packet) as net_recv_packet");
		sb.append(" from t_prf_hypervisor_vm where time_id in (" + timeIds + ")");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加HYPERVISOR虚拟机性能信息(小时或天)
	 * @param comPerfiInfoList
	 */
	public void addPerHourAndDayHypvVmPerf(List<DataRow> hypvVmPerfiInfoList) {
		for (int i = 0; i < hypvVmPerfiInfoList.size(); i++) {
			DataRow row = hypvVmPerfiInfoList.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_hypervisor_vm", row);
		}
	}
	
	/***
	 * 获得虚拟机配置信息列表
	 * @return
	 */
	public List<DataRow> getVirtualConfigList(){
		String sql="select s.id,vm.hypervisor_id,vm.vm_id as computer_id,COALESCE(NULL,cs.display_name,cs.name) as computer_name,vm.computer_id as ref_computer_id,s.ip_address,s.user,s.password,"
			+ "'" + SrContant.SUBDEVTYPE_VIRTUAL + "' as device_type,s.os_type "
			+ "from t_res_virtualmachine vm,t_res_computersystem cs,t_server s where vm.computer_id = cs.computer_id "
			+ "and vm.hmc_id = s.id and cs.detectable = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 获得物理机配置信息列表
	 * @return
	 */
	public List<DataRow> getPhysicalConfigList(){
		String sql = "select s.id,h.hypervisor_id as computer_id,COALESCE(NULL,cs.display_name,cs.name) as computer_name,h.host_computer_id as ref_computer_id,s.ip_address,s.user,s.password," 
			+ "'" + SrContant.SUBDEVTYPE_PHYSICAL + "' as device_type,s.os_type "
			+ "from t_res_hypervisor h,t_res_computersystem cs,t_server s where h.host_computer_id = cs.computer_id " 
			+ "and h.hmc_id = s.id and cs.detectable = 1";
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
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.add(Calendar.DAY_OF_MONTH, -interval_len_day);
		String sql="DELETE FROM t_prf_timestamp WHERE SAMPLE_TIME < '"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ca.getTime())+"'";
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
	}
	
	/**
	 * 获取相应操作系统类型的服务器配置信息
	 * @param schemaType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getServerLoginInfo(String osType, String serverType) {
		String sql = "select * from t_server where state = 1 and os_type = ? and toptype = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{osType,serverType.toLowerCase()});
	}
	
	/**
	 * 获取所有具有虚拟化平台的物理机配置信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPhysicalConfigInfo() {
		String sql = "select * from t_server where state = 1 and toptype = 'physical'";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 修改服务器配置信息
	 * @param row
	 */
	public void updateServerLoginInfo(DataRow row) {
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_server", row, "id", row.getString("id"));
	}
	
	/**
	 * 获取虚拟机详细配置信息
	 * @param vmId
	 * @return
	 */
	public DataRow getVirtMachLoginInfo(String vmId){
		String sql = "select vm.hmc_id,vm.vm_id,vm.hypervisor_id,vm.computer_id,vm.name,s.ip_address,s.user,s.password,s.os_type from t_server s,t_res_virtualmachine vm "
			+ "where s.id = vm.hmc_id and s.state = 1 and vm.vm_id = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{vmId});
	}
	
	/**
	 * 获取服务器详细配置信息
	 * @param id
	 * @return
	 */
	public DataRow getHMCLoginInfo(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_server where state = 1 and id = " + id);
	}
	
	//switch的性能
	public void batchInsertSwitchPrf(List<DataRow> list){
		Session session = null;
		for (DataRow row : list) {
			DataRow timestamp = new DataRow();
			DataRow prf = new DataRow();
			timestamp.set("sample_time", row.getString("sample_time"));
			timestamp.set("interval_len", row.getString("interval_len"));
			timestamp.set("summ_type", row.getString("summ_type"));
			timestamp.set("subsystem_name", row.getString("subsystem_name"));
			timestamp.set("subsystem_id", row.getString("subsystem_id"));
			timestamp.set("perf_marker", row.getString("perf_marker"));
			prf.set("switch_id", row.getString("switch_id"));
			if(StringHelper.isNotEmpty(row.getString("cup_used_prct"))){
				prf.set("cup_used_prct", row.getString("cup_used_prct"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_used_prct") )){
				prf.set("mem_used_prct", row.getString("mem_used_prct"));
			}
			try {
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
				int time_id = getTimeId(row.getString("perf_marker"),row.getInt("subsystem_id"));
				if(time_id > 0){
					session.update("t_prf_switch", prf, "time_id", time_id);
				}else{
					String id = session.insert("t_prf_timestamp", timestamp);
					prf.set("time_id", id);
					session.insert("t_prf_switch", prf);
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
	//switch的状态
	public void batchInsertSwitchRes(List<DataRow> list){
		Session session = null;
		for (DataRow row : list) {
			DataRow res = new DataRow();
			//res.set("id", row.getString("id"));
			res.set("engine_status", row.getString("engine_status"));
			res.set("power_status", row.getString("power_status"));
			res.set("port_status", row.getString("port_status"));
			res.set("fiber_status", row.getString("fiber_status"));
			res.set("fan_status", row.getString("fan_status"));
			
			try {
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
				DataRow SwitchInfo = getSwitchInfo(Integer.parseInt(row.getString("id")));
				if(SwitchInfo != null && SwitchInfo.getInt("id") > 0){
					session.update("t_res_switch", res, "id", SwitchInfo.getInt("id"));
				}else{
					res.set("switch_id", row.getString("id"));
					session.insert("t_res_switch", res);
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
	public DataRow getSwitchInfo(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_res_switch where switch_id = "+id);
	}
	
}
