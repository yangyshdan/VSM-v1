package com.huiming.service.hypervisor;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class HypervisorService extends BaseService {
	/**
	 * 分页查询
	 * @param curPage
	 * @param numPerPage
	 * @param computerId
	 * @param displayName
	 * @param ipAddress
	 * @param cpuArchitecture
	 * @param startRamSize
	 * @param endRamSize
	 * @param startDiskSpace
	 * @param endDiskSpace
	 * @param limitIds
	 * @return
	 */
	public DBPage getHypervisorPage(int curPage,int numPerPage,Integer computerId,String displayName,String ipAddress,String cpuArchitecture,
			Integer startRamSize,Integer endRamSize,Integer startDiskSpace,Integer endDiskSpace,String limitIds){
		String sql = "select t.* from (select h.hypervisor_id,c.computer_id,COALESCE(c.display_name,h.name) AS display_name,c.ip_address,c.os_version,c.cpu_architecture,c.processor_count,"
			+ "c.ram_size,(1 - c.disk_available_space * 1.0 / c.disk_space) AS percent,c.disk_space,c.disk_available_space,c.update_timestamp,"
			+ "vp.id as vp_id,vp.name as vp_name,h.available_cpu,h.available_mem "
			+ "from t_res_hypervisor h "
			+ "left join t_res_computersystem c on c.computer_id=h.host_computer_id "
			+ "left join t_res_virtualplatform vp on vp.hypervisor_id = h.hypervisor_id) t where 1 = 1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sb.append("and t.hypervisor_id in (" + limitIds + ") ");
		}
		if (computerId != null && computerId > 0) {
			sb.append("and t.computer_id = ? ");
			args.add(computerId);
		}
		if (displayName != null && displayName.length() > 0) {
			sb.append("and t.display_name like ? ");
			args.add("%" + displayName + "%");
		}
		if (ipAddress != null && ipAddress.length() > 0) {
			sb.append("and t.ip_address = ? ");
			args.add(ipAddress);
		}
		if (cpuArchitecture != null && cpuArchitecture.length() > 0) {
			sb.append("and t.cpu_architecture like ? ");
			args.add("%" + cpuArchitecture + "%");
		}
		if (startRamSize != null && startRamSize > 0) {
			sb.append("and t.ram_size >= ? ");
			args.add(startRamSize*1024);
		}
		if (endRamSize != null && endRamSize > 0) {
			sb.append("and t.ram_size <= ? ");
			args.add(endRamSize*1024);
		}
		if (startDiskSpace != null && startDiskSpace > 0) {
			sb.append("and t.disk_space >= ? ");
			args.add(startDiskSpace*1024);
		}
		if (endDiskSpace != null && endDiskSpace > 0) {
			sb.append("and t.disk_space <= ? ");
			args.add(endDiskSpace*1024);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	//获取物理机详细信息
	public DataRow getHypervisorInfo(Long hypervisorId, Integer computerId){
//		String sql="select h.hypervisor_id,h.kernel_memory,h.available_cpu,h.available_mem,c.vendor,c.cpu_architecture,c.disk_space,c.ram_size," +
//				"c.os_version,c.ip_address,c.processor_count,c.processor_speed,c.disk_available_space,c.update_timestamp,c.operational_status,c.computer_id,COALESCE(c.display_name,h.name) as display_name " +
//		"from t_res_hypervisor h,t_res_computersystem c where c.computer_id=h.host_computer_id "; 
		String sql = "SELECT h.hypervisor_id,h.kernel_memory,h.available_cpu,h.available_mem,c.vendor,c.cpu_architecture,c.disk_space,c.ram_size,c.os_version," +
			"c.ip_address,c.processor_count,c.processor_speed,c.disk_available_space,c.update_timestamp,c.operational_status,c.computer_id," +
			"COALESCE(c.display_name,h.name) AS display_name,b.board_vendor,b.board_factory,b.board_serial_num,b.board_model,b.board_mfg_datetime," +
			"b.prod_factory,b.prod_name,b.prod_model,b.prod_version,b.prod_serial_num,b.system_guid,b.chassis_type,b.chassis_model,b.chassis_serial_num FROM " +
			String.format("(SELECT * FROM t_res_hypervisor WHERE 1=1 %s %s) h ", 
					hypervisorId != null && hypervisorId > 0? (" and hypervisor_id=" + hypervisorId) : "",
					computerId != null && computerId > 0? (" AND host_computer_id=" + computerId) : "") +
					"JOIN t_res_computersystem c ON c.computer_id=h.host_computer_id LEFT JOIN t_res_bmc b ON h.HYPERVISOR_ID=b.HYPERVISOR_ID ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);		
	}
	
	public DBPage getSensorStatusData(int curPage, int numPerPage, long hypervisorId){
		String sql = "SELECT * FROM t_status_sensors WHERE hypervisor_id=" + hypervisorId + " ORDER BY id";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql, curPage, numPerPage);
//		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 获取与物理机相关联的HYPERVISOR管理程序信息
	 * @param hypervisorId
	 * @return
	 */
	public DataRow getVirtualPlatByHypvId(Integer hypervisorId) {
		String sql = "SELECT * FROM t_res_virtualplatform WHERE hypervisor_id = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{hypervisorId});
	}
	
	/**
	 * 获取物理机列表数据
	 * @param hypervisorId
	 * @param computerId
	 * @param displayName
	 * @param ipAddress
	 * @param startDiskSpace
	 * @param endDiskSpace
	 * @param limitIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getHypervisorList(Integer hypervisorId,Integer computerId,String displayName,String ipAddress,Integer startRamSize,Integer endRamSize,Integer startDiskSpace,Integer endDiskSpace,String limitIds){
		String sql = "select t.* from (select h.hypervisor_id,c.computer_id,COALESCE(c.display_name,h.name) AS display_name,c.ip_address,c.os_version,c.cpu_architecture,c.processor_count,"
			+ "c.ram_size,(1 - c.disk_available_space * 1.0 / c.disk_space)*100 AS percent,c.disk_space,c.disk_available_space,DATE_FORMAT(c.update_timestamp,'%Y/%m/%d %H:%i:%s') AS update_timestamp,"
			+ "vp.id as vp_id,vp.name as vp_name,h.available_cpu,h.available_mem "
			+ "from t_res_hypervisor h "
			+ "left join t_res_computersystem c on c.computer_id=h.host_computer_id "
			+ "left join t_res_virtualplatform vp on vp.hypervisor_id = h.hypervisor_id) t where 1 = 1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sb.append("and t.hypervisor_id in (" + limitIds + ") ");
		}
		if (hypervisorId != null && hypervisorId > 0) {
			sb.append("and t.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if (computerId != null && computerId > 0) {
			sb.append("and t.computer_id = ? ");
			args.add(computerId);
		}
		if (displayName != null && displayName.length() > 0) {
			sb.append("and t.display_name like ? ");
			args.add("%" + displayName + "%");
		}
		if (ipAddress != null && ipAddress.length() > 0) {
			sb.append("and t.ip_address = ? ");
			args.add(ipAddress);
		}
		if (startRamSize != null && startRamSize > 0) {
			sb.append("and t.ram_size >= ? ");
			args.add(startRamSize*1024);
		}
		if (endRamSize != null && endRamSize > 0) {
			sb.append("and t.ram_size <= ? ");
			args.add(endRamSize*1024);
		}
		if (startDiskSpace != null && startDiskSpace > 0) {
			sb.append("and t.disk_space >= ? ");
			args.add(startDiskSpace*1024);
		}
		if (endDiskSpace != null && endDiskSpace > 0) {
			sb.append("and t.disk_space <= ? ");
			args.add(endDiskSpace*1024);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getHypervisorCapacityByHypId(String hypIds, String compIds){
		// 查询server name
		// 查询服务器数据 server data
		// 其他comp_id hyp_id
		String _hypIds = "", _compIds = "";
		if(hypIds != null && hypIds.trim().length() > 0){
			_hypIds = " and h.hypervisor_id in (&) ".replace("&", hypIds);
		}
		if(compIds != null && compIds.trim().length() > 0){
			_compIds = " and c.computer_id in (&) ".replace("&", compIds);
		}
		String sql = "SELECT c.computer_id AS comp_id,h.hypervisor_id AS hyp_id,c.name," +
			"FORMAT(c.disk_space / 1024.0-FORMAT(c.disk_available_space / 1024.0,2),2) AS used," +
			"FORMAT(c.disk_available_space / 1024.0,2) AS available FROM t_res_computersystem c " +
			" JOIN t_res_hypervisor h ON c.computer_id=h.HOST_COMPUTER_ID" + _hypIds + _compIds;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public List<DataRow> getHypervisorName(Integer hypervisorId,Integer computerId){
		String sql="select h.hypervisor_id,h.host_computer_id as computer_id,h.name from t_res_hypervisor h where 1=1";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(hypervisorId!=null && hypervisorId.SIZE >0&&hypervisorId!=0){
			sb.append(" and h.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if(computerId!=null && computerId.SIZE >0&&computerId!=0){
			sb.append(" and h.host_computer_id = ? ");
			args.add(computerId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPhysicalList(Integer pid){
		String sql="select c.computer_id,h.detectable,COALESCE(c.display_name,h.name) as the_display_name,c.ip_address,c.cpu_architecture,c.processor_count,c.ram_size,c.disk_space,c.disk_available_space,c.operational_status, " +
		"DATE_FORMAT(c.update_timestamp,'%Y-%c-%d %H:%m:%s') AS update_timestamp,h.hypervisor_id,h.host_computer_id,h.available_cpu,h.available_mem from t_res_hypervisor h,t_res_computersystem c where c.computer_id=h.host_computer_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(pid!=null && pid.SIZE >0&&pid!=0){
			sb.append(" and h.detectable = 1 and h.hypervisor_id = ? ");
			args.add(pid);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());		
	}
}
