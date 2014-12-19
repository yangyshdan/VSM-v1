package com.huiming.service.node;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class NodeService extends BaseService{
	public DBPage getNodePage(int curPage,int numPerPage,String name,String ipAddress,String componentId,Integer subsystemId){
		String sql="select n.*,s.the_display_name as sub_name,i.the_display_name as iogroup_name " +
				"from V_RES_REDUNDANCY n " +
				"left join V_RES_STORAGE_IOGROUP i on n.io_group_id = i.io_group_id " +
				"inner join V_RES_STORAGE_SUBSYSTEM s on n.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and n.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and n.ip_address = ? ");
			args.add(ipAddress);
		}
		if(componentId!=null && componentId.length()>0){
			sb.append("and component_id = ? ");
			args.add(componentId);
		}
		if(subsystemId!=null && subsystemId>0){
			sb.append("and n.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getNodeList(String name,String ipAddress,String componentId,Integer subsystemId){
		String sql="select n.*,s.the_display_name as sub_name,i.the_display_name as iogroup_name " +
		"from V_RES_REDUNDANCY n " +
		"left join V_RES_STORAGE_IOGROUP i on n.io_group_id = i.io_group_id " +
		"inner join V_RES_STORAGE_SUBSYSTEM s on n.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and n.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and n.ip_address = ? ");
			args.add(ipAddress);
		}
		if(componentId!=null && componentId.length()>0){
			sb.append("and component_id = ? ");
			args.add(componentId);
		}
		if(subsystemId!=null && subsystemId>0){
			sb.append("and n.subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getNodeInfo(Integer nodeId){
		String sql="select n.*,s.the_display_name as sub_name,i.the_display_name as iogroup_name " +
		"from V_RES_REDUNDANCY n " +
		"left join V_RES_STORAGE_IOGROUP i on n.io_group_id = i.io_group_id " +
		"inner join V_RES_STORAGE_SUBSYSTEM s on n.subsystem_id = s.subsystem_id "+
		"and n.redundancy_id="+nodeId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getdevInfo(Integer subsystemId) {
		String sql="select the_display_name,redundancy_id,subsystem_id from V_RES_REDUNDANCY where subsystem_id = "+subsystemId;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
}
