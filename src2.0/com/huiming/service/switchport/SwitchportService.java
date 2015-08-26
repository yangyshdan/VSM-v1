package com.huiming.service.switchport;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class SwitchportService extends BaseService{
	public DBPage getPortPage(int curPage, int numPerPage, String portName,String portType,Integer startPort,Integer endPort,String status,Integer switchId) {
		String sql = "select p.*,s.the_display_name as switch_name " +
				"from v_res_switch_port p,v_res_switch s " +
				"where p.switch_id = s.switch_id ";
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
		if (switchId != null && switchId> 0) {
			sb.append(" and p.switch_id = ?");
			args.add(switchId);
		}
		sb.append(" order by p.port_number");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getExtentportList(String portName,String portType,Integer startPort,Integer endPort,String status,Integer switchId){
		String sql = "select p.*,s.the_display_name as switch_name " +
		"from v_res_switch_port p,v_res_switch s " +
		"where p.switch_id = s.switch_id ";
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
		if (switchId != null && switchId> 0) {
			sb.append(" and p.switch_id = ?");
			args.add(switchId);
		}
		sb.append(" order by p.port_number");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getExtentportInfo(Integer portId){
		String sql = "select p.*,s.the_display_name as switch_name " +
		"from v_res_switch_port p,v_res_switch s " +
		"where p.switch_id = s.switch_id " +
		"and p.port_id = "+portId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortSpeed(Integer switchId){
		String sql="select the_port_speed,the_display_name,port_id,switch_id from v_res_switch_port where switch_id = "+switchId+" order by the_port_speed desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,WebConstants.DEFAULT_CONFIG_TOP);
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getdevInfo(Integer switchId) {
		String sql="select the_display_name,port_id,switch_id from v_res_switch_port where switch_id = "+switchId+" order by the_port_speed desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	} 

}
