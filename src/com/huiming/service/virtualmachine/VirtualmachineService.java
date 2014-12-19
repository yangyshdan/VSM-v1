package com.huiming.service.virtualmachine;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class VirtualmachineService extends BaseService {
	//分页查询
	public DBPage getVirtualmachinePage(int curPage,int numPerPage,Integer hypervisorId,String virtualName,String endMemory,
			String startMemory,String startDiskSpace,String endDiskSpace){
		String sql="select v.vm_id,v.hypervisor_id,v.targeted_os,v.assigned_cpu_number,v.assigned_cpu_processunit,v.maximum_cpu_number,v.maximum_cpu_processunit," +
				"v.minimum_cpu_number,v.minimum_cpu_processunit,v.total_memory,v.update_timestamp,v.host_name," +
				"c.computer_id,COALESCE(c.display_name,v.name) as display_name,c.disk_space,c.disk_available_space,c.ip_address" +
				" from t_res_virtualmachine v,t_res_computersystem c where c.computer_id=v.computer_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(hypervisorId!=null && hypervisorId>0){
			sb.append(" and v.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if(virtualName!=null && virtualName.length()>0){
			sb.append(" and COALESCE(v.name,c.display_name) like ? ");
			args.add("%"+virtualName+"%");
		}
		if(startMemory!=null && startMemory.length()>0){
			sb.append(" and v.total_memory>= ? ");
			args.add(startMemory);
		}
		if(endMemory!=null && endMemory.length()>0){
			sb.append(" and v.total_memory <= ? ");
			args.add(endMemory);
		}
		if(startDiskSpace!=null && startDiskSpace.length()>0){
			sb.append(" and c.disk_space>= ? ");
			args.add(startDiskSpace);
		}
		if(endDiskSpace!=null && endDiskSpace.length()>0){
			sb.append(" and c.disk_space <= ? ");
			args.add(endDiskSpace);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	//获取虚拟机详细信息
	public DataRow getVirtualInfo(Integer vmId,Integer hypervisorId){
		String sql="select v.*,COALESCE(c.display_name,v.name) as display_name,c.disk_space,c.disk_available_space,c.ip_address,c.operational_status from t_res_virtualmachine v,t_res_computersystem c " +
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
	public List<DataRow> getVirtualList(Integer vmId,Integer hypervisorId,String virtualName,String endMemory,
			String startMemory,String startDiskSpace,String endDiskSpace){
		String sql="select v.vm_id,v.hypervisor_id,v.targeted_os,v.assigned_cpu_number,v.assigned_cpu_processunit,v.maximum_cpu_number,v.maximum_cpu_processunit," +
		"v.minimum_cpu_number,v.minimum_cpu_processunit,v.total_memory,v.update_timestamp,v.host_name," +
		"c.computer_id,COALESCE(c.display_name,v.name) as display_name,c.disk_space,c.disk_available_space,c.ip_address " +
		" from t_res_virtualmachine v,t_res_computersystem c where c.computer_id=v.computer_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(vmId!=null && vmId>0&&vmId!=0){
			sb.append("and v.vm_id = ? ");
			args.add(vmId);
		}
		if(hypervisorId!=null && hypervisorId>0&&hypervisorId!=0){
			sb.append("and v.hypervisor_id = ? ");
			args.add(hypervisorId);
		}
		if(virtualName!=null && virtualName.length()>0){
			sb.append(" and COALESCE(v.name,c.display_name) as display_name like ? ");
			args.add("%"+virtualName+"%");
		}
		if(startMemory!=null && startMemory.length()>0){
			sb.append(" and v.total_memory>= ? ");
			args.add(startMemory);
		}
		if(endMemory!=null && endMemory.length()>0){
			sb.append(" and v.total_memory <= ? ");
			args.add(endMemory);
		}
		if(startDiskSpace!=null && startDiskSpace.length()>0){
			sb.append(" and c.disk_space>= ? ");
			args.add(startDiskSpace);
		}
		if(endDiskSpace!=null && endDiskSpace.length()>0){
			sb.append(" and c.disk_space <= ? ");
			args.add(endDiskSpace);
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
	
	public DBPage getDiskPage(int curPage,int numPerPage,int vmId){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage("select * from t_res_vm2disk where vm_id=? ", new Object[]{vmId}, curPage, numPerPage);
	}
}
