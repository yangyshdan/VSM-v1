package com.huiming.service.virtualmachine;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class VirtualmachineService extends BaseService {
	/**
	 * 分页查询
	 * @param curPage
	 * @param numPerPage
	 * @param hypervisorId
	 * @param virtualName
	 * @param endMemory
	 * @param startMemory
	 * @param startDiskSpace
	 * @param endDiskSpace
	 * @param limitIds
	 * @return
	 */
	public DBPage getVirtualmachinePage(int curPage,int numPerPage,Integer hypervisorId,String virtualName,Integer endMemory,
			Integer startMemory,Integer startDiskSpace,Integer endDiskSpace,String limitIds){
		String sql="select v.vm_id,v.hypervisor_id,v.targeted_os,v.assigned_cpu_number,v.assigned_cpu_processunit,v.maximum_cpu_number,v.maximum_cpu_processunit," +
				"v.minimum_cpu_number,v.minimum_cpu_processunit,v.total_memory,v.update_timestamp,v.host_name," +
				"c.computer_id,COALESCE(c.display_name,v.name) as display_name,(1 - c.disk_available_space * 1.0 / c.disk_space) as percent,c.disk_space,c.disk_available_space,c.ip_address" +
				" from t_res_virtualmachine v,t_res_computersystem c where c.computer_id = v.computer_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sb.append(" and v.vm_id in (" + limitIds + ") ");
		}
		if (hypervisorId != null && hypervisorId > 0) {
			sb.append(" and v.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if (virtualName != null && virtualName.length() > 0) {
			sb.append(" and COALESCE(v.name,c.display_name) like ? ");
			args.add("%" + virtualName + "%");
		}
		if (startMemory != null && startMemory > 0) {
			sb.append(" and v.total_memory >= ? ");
			args.add(startMemory*1024);
		}
		if (endMemory != null && endMemory > 0) {
			sb.append(" and v.total_memory <= ? ");
			args.add(endMemory*1024);
		}
		if (startDiskSpace != null && startDiskSpace > 0) {
			sb.append(" and c.disk_space>= ? ");
			args.add(startDiskSpace*1024);
		}
		if (endDiskSpace != null && endDiskSpace > 0) {
			sb.append(" and c.disk_space <= ? ");
			args.add(endDiskSpace*1024);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVMCapacityByIds(String vmIds, String hypIds, String compIds){
		// 查询server name
		// 查询服务器数据 server data
		// 其他comp_id hyp_id
		String _vmIds = "", _hypIds = "", _compIds = "";
		if(vmIds != null && vmIds.trim().length() > 0){
			_vmIds = " and v.vm_id in (&) ".replace("&", vmIds);
		}
		if(hypIds != null && hypIds.trim().length() > 0){
			_hypIds = " and v.hypervisor_id in (&) ".replace("&", hypIds);
		}
		if(compIds != null && compIds.trim().length() > 0){
			_compIds = " and c.computer_id in (&) ".replace("&", compIds);
		}
		String sql = "SELECT c.computer_id AS comp_id,v.vm_id,v.hypervisor_id AS hyp_id,c.name," +
			"FORMAT(c.disk_space / 1024.0-FORMAT(c.disk_available_space / 1024.0,2),2) AS used," +
			"FORMAT(c.disk_available_space / 1024.0,2) AS available " +
			"FROM t_res_computersystem c " +
			"JOIN t_res_virtualmachine v ON c.COMPUTER_ID=v.COMPUTER_ID " + _vmIds + _hypIds + _compIds ;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	//获取虚拟机详细信息
	public DataRow getVirtualInfo(Integer vmId,Integer hypervisorId){
		String sql="select v.*,COALESCE(c.display_name,v.name) as display_name,c.disk_space,c.disk_available_space,c.os_version,c.ip_address,c.processor_count,c.processor_speed,c.operational_status from t_res_virtualmachine v,t_res_computersystem c " +
		"where c.computer_id=v.computer_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(vmId!=null && vmId>0){
			sb.append(" and v.vm_id=? ");
			args.add(vmId);
		}
		if(hypervisorId!=null && hypervisorId>0){
			sb.append(" and v.hypervisor_id=? ");
			args.add(hypervisorId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());		
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVirtualList(Integer hypervisorId,String virtualName,Integer endMemory,
			Integer startMemory,Integer startDiskSpace,Integer endDiskSpace,String limitIds){
		String sql="select v.vm_id,v.hypervisor_id,v.targeted_os,v.assigned_cpu_number,v.assigned_cpu_processunit,v.maximum_cpu_number,v.maximum_cpu_processunit," +
		"v.minimum_cpu_number,v.minimum_cpu_processunit,v.total_memory,v.host_name,(1 - c.disk_available_space * 1.0 / c.disk_space)*100 as percent," +
		"c.computer_id,COALESCE(c.display_name,v.name) as display_name,c.disk_space,c.disk_available_space,c.ip_address,date_format(c.update_timestamp,'%Y/%m/%d %H:%i:%s') AS update_timestamp " +
		" from t_res_virtualmachine v,t_res_computersystem c where c.computer_id = v.computer_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sb.append(" and v.vm_id in (" + limitIds + ") ");
		}
		if(hypervisorId!=null && hypervisorId>0&&hypervisorId!=0){
			sb.append(" and v.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if (virtualName != null && virtualName.length() > 0) {
			sb.append(" and COALESCE(v.name,c.display_name) like ? ");
			args.add("%" + virtualName + "%");
		}
		if (startMemory != null && startMemory > 0) {
			sb.append(" and v.total_memory >= ? ");
			args.add(startMemory*1024);
		}
		if (endMemory != null && endMemory > 0) {
			sb.append(" and v.total_memory <= ? ");
			args.add(endMemory*1024);
		}
		if (startDiskSpace != null && startDiskSpace > 0) {
			sb.append(" and c.disk_space>= ? ");
			args.add(startDiskSpace*1024);
		}
		if (endDiskSpace != null && endDiskSpace > 0) {
			sb.append(" and c.disk_space <= ? ");
			args.add(endDiskSpace*1024);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	@SuppressWarnings({ "unchecked", "static-access" })
	public List<DataRow> getVirtualName(Integer vmId,Integer hypervisorId,Integer computerId){
		String sql="SELECT v.vm_id,v.hypervisor_id,v.computer_id,v.name FROM t_res_virtualmachine v where 1=1";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(vmId!=null && vmId.SIZE >0&&vmId!=0){
			sb.append(" and v.vm_id = ? ");
			args.add(vmId);
		}
		if(hypervisorId!=null && hypervisorId.SIZE >0&&hypervisorId!=0){
			sb.append(" and v.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if(computerId!=null && computerId.SIZE >0&&computerId!=0){
			sb.append(" and v.computer_id = ? ");
			args.add(computerId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVirtualList(Integer pid){
		String sql="select v.vm_id,v.name,v.hypervisor_id,v.targeted_os,v.assigned_cpu_number,v.assigned_cpu_processunit,v.maximum_cpu_number,v.maximum_cpu_processunit," +
		"v.minimum_cpu_number,v.minimum_cpu_processunit,v.total_memory,DATE_FORMAT(c.update_timestamp,'%Y-%c-%d %H:%m:%s') AS update_timestamp,v.host_name," +
		"c.computer_id,COALESCE(c.display_name,v.name) as the_display_name,c.disk_space,c.disk_available_space,c.operational_status,c.ip_address" +
		" from t_res_virtualmachine v,t_res_computersystem c where c.computer_id=v.computer_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(pid!=null && pid.SIZE >0&&pid!=0){
			sb.append(" and v.hypervisor_id = ? ");
			args.add(pid);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 查找指定物理机下的虚拟机信息列表
	 * @param physicalId
	 * @return
	 */
	public List<DataRow> getVirtualListByPhysicalId(String physicalId,String limitIds) {
		String sql = "select vm.vm_id,vm.computer_id,vm.hypervisor_id,vm.hmc_id,coalesce(null,cs.display_name,cs.name) as name "
			+ "from t_res_computersystem cs,t_res_virtualmachine vm where vm.hypervisor_id = ? and vm.computer_id = cs.computer_id";
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + " and vm.vm_id in (" + limitIds + ")";
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{physicalId});
	}
	
	/**
	 * 通过hmcId获取虚拟机信息
	 * @param vmHmcId
	 * @return
	 */
	public DataRow getVirtualInfoByHmcId(String vmHmcId) {
		String sql = "select vm.vm_id,vm.computer_id,vm.hypervisor_id,vm.hmc_id,coalesce(null,cs.display_name,cs.name) as name "
			+ "from t_res_computersystem cs,t_res_virtualmachine vm where vm.hmc_id = ? "
			+ "and vm.computer_id = cs.computer_id";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{vmHmcId});
	}
	
}
