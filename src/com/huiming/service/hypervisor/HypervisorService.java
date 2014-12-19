package com.huiming.service.hypervisor;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class HypervisorService extends BaseService {
	//分页查询
	public DBPage getHypervisorPage(int curPage,int numPerPage,Integer computerId,String displayName,String ipAddress,String cpuArchitecture,
			Integer startRamSize,Integer endRamSize,Integer startDiskSpace,Integer endDiskSpace){
		String sql="SELECT c.computer_id,COALESCE(c.display_name,h.name) as display_name,c.ip_address,c.cpu_architecture,c.processor_count,c.ram_size,c.disk_space,c.disk_available_space,c.update_timestamp," +
				"h.hypervisor_id,h.host_computer_id,h.available_cpu,h.available_mem,COUNT(v.HYPERVISOR_ID) vcount " +
				"FROM t_res_hypervisor h " +
				"LEFT JOIN t_res_virtualmachine v ON h.HYPERVISOR_ID = v.HYPERVISOR_ID " +
				"INNER JOIN t_res_computersystem c ON c.computer_id=h.host_computer_id " +
				"GROUP BY c.computer_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(computerId!=null && computerId>0){
			sb.append("and c.computer_id = ? ");
			args.add(computerId);
		}
		if(displayName!=null && displayName.length()>0){
			sb.append("and COALESCE(h.name,c.display_name) like ? ");
			args.add("%"+displayName+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and c.ip_address = ? ");
			args.add(ipAddress);
		}
		if(cpuArchitecture!=null && cpuArchitecture.length()>0){
			sb.append("and c.cpu_architecture like ? ");
			args.add("%"+cpuArchitecture+"%");
		}
		if(startRamSize!=null && startRamSize >0){
			sb.append("and c.ram_size >= ? ");
			args.add(startRamSize);
		}
		if(endRamSize!=null && endRamSize >0){
			sb.append("and c.ram_size <= ? ");
			args.add(endRamSize);
		}
		if(startDiskSpace!=null && startDiskSpace >0){
			sb.append("and c.disk_space >= ? ");
			args.add(startDiskSpace);
		}
		if(endDiskSpace!=null && endDiskSpace >0){
			sb.append("and c.disk_space <= ? ");
			args.add(endDiskSpace);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	//获取物理机详细信息
	public DataRow getHypervisorInfo(Integer hypervisorId,Integer computerId){
		String sql="select h.hypervisor_id,h.kernel_memory,h.available_cpu,h.available_mem,c.vendor,c.cpu_architecture,c.disk_space,c.ram_size," +
				"c.os_version,c.ip_address,c.processor_count,c.disk_available_space,c.update_timestamp,c.operational_status,c.computer_id,COALESCE(c.display_name,h.name) as display_name " +
		"from t_res_hypervisor h,t_res_computersystem c where c.computer_id=h.host_computer_id "; 
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(hypervisorId!=null && hypervisorId>0&&hypervisorId!=0){
			sb.append("and h.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if(computerId!=null && computerId>0&&computerId!=0){
			sb.append("and c.computer_id = ? ");
			args.add(computerId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());		
	}
	public List<DataRow> getHypervisorList(Integer hypervisorId,Integer computerId,String displayName,String ipAddress,Integer startDiskSpace,Integer endDiskSpace){
		String sql="select c.computer_id,COALESCE(c.display_name,h.name) as display_name,c.ip_address,c.cpu_architecture,c.processor_count,c.ram_size,c.disk_space,c.disk_available_space," +
				"c.update_timestamp,h.hypervisor_id,h.host_computer_id,h.available_cpu,h.available_mem from t_res_hypervisor h,t_res_computersystem c where c.computer_id=h.host_computer_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(hypervisorId!=null && hypervisorId>0&&hypervisorId!=0){
			sb.append("and h.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if(computerId!=null && computerId>0&&computerId!=0){
			sb.append("and c.computer_id = ? ");
			args.add(computerId);
		}
		if(displayName!=null && displayName.length()>0){
			sb.append("and COALESCE(h.name,c.display_name) like ? ");
			args.add("%"+displayName+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and c.ip_address = ? ");
			args.add(ipAddress);
		}
		if(startDiskSpace!=null && startDiskSpace >0){
			sb.append("and c.disk_space >= ? ");
			args.add(startDiskSpace);
		}
		if(endDiskSpace!=null && endDiskSpace >0){
			sb.append("and c.disk_space <= ? ");
			args.add(endDiskSpace);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
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
	
	/**
	 * 分页获取硬件信息
	 * @Title: getHypervisorPage
	 * @Description: TODO
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
	 * @return
	 * DBPage
	 */
	public DBPage getHwresPage(int curPage,int numPerPage,int type,Integer hypervisorId){
		String sql="SELECT * from t_res_hwres where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(hypervisorId!=null && hypervisorId>0){
			sb.append(" and hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if(type > 0){
			sb.append(" and hwres_type = ? ");
			args.add(hypervisorId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
}
