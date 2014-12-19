package com.huiming.service.port;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class PortService extends BaseService {
	/**
	 * 端口分页信息
	 * 
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getPortPage(int curPage, int numPerPage, String portName,String portType,Integer startPort,Integer endPort,String status,Integer subSystemID) {
		String sql = "select p.*,s.the_display_name as sub_name " +
				"from v_res_port p,v_res_storage_subsystem s " +
				"where p.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (portName != null && portName.length() > 0) {
			sb.append(" and p.name like ?");
			args.add("%" + portName + "%");
		}
		if (portType != null && portType.length() > 0) {
			sb.append(" and p.the_type = ?");
			args.add(portType);
		}
		if(startPort!=null && startPort>0){
			sb.append(" and p.port_number >= ?");
			args.add(startPort);
		}
		if(endPort !=null && endPort > 0){
			sb.append(" and p.port_number <= ?");
			args.add(endPort);
		}
		if(status!=null && status.length()>0){
			sb.append(" and p.the_operational_status = ?");
			args.add(status);
		}
		if (subSystemID != null && subSystemID> 0) {
			sb.append(" and p.SUBSYSTEM_ID = ?");
			args.add(subSystemID);
		}
		sb.append(" order by p.port_id");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}

	/**
	 * 端口列表信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortList(String portName,String portType,Integer startPort,Integer endPort,String status,Integer subSystemID) {
		String sql = "select p.*,s.the_display_name as sub_name " +
				"from v_res_port p,v_res_storage_subsystem s " +
				"where p.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (portName != null && portName.length() > 0) {
			sb.append(" and p.name like ?");
			args.add("%" + portName + "%");
		}
		if (portType != null && portType.length() > 0) {
			sb.append(" and p.the_type = ?");
			args.add(portType);
		}
		if(startPort!=null && startPort>0){
			sb.append(" and p.port_number >= ?");
			args.add(startPort);
		}
		if(endPort !=null && endPort > 0){
			sb.append(" and p.port_number <= ?");
			args.add(endPort);
		}
		if(status!=null && status.length()>0){
			sb.append(" and p.the_operational_status = ?");
			args.add(status);
		}
		if (subSystemID != null && subSystemID> 0) {
			sb.append(" and p.SUBSYSTEM_ID = ?");
			args.add(subSystemID);
		}
		sb.append(" order by p.port_number");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getPortById(Integer portId){
		String sql = "select p.*,s.the_display_name as sub_name " +
		"from v_res_port p,v_res_storage_subsystem s " +
		"where p.subsystem_id = s.subsystem_id and p.port_id = "+portId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortSpeed(Integer subsystemId){
		String sql="select the_port_speed,the_display_name,port_id,subsystem_id from v_res_port where subsystem_id = "+subsystemId+" order by the_port_speed desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,WebConstants.DEFAULT_CONFIG_TOP);
	}
}
