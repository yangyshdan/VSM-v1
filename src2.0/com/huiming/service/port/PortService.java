package com.huiming.service.port;

import java.util.ArrayList;
import java.util.List;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.service.node.NodeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
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
				"where p.subsystem_id = s.subsystem_id and p.port_number is not null";
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
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPortByName(String name,Integer subSystemID){
		String sql="select p.* from t_res_port p where p.name='"+name+"' and p.SUBSYSTEM_ID = "+subSystemID+" order by p.UPDATE_TIMESTAMP desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	// 添加端口信息
	public void addPort(List<DataRow> ports,Integer subSystemID) {
		NodeService service = new NodeService();
		for (DataRow portRow:ports) {
			DataRow spRow = service.getNodeByName(portRow.getString("name").substring(0,4), subSystemID, null).get(0);
			portRow.set("sp_id", spRow.getString("sp_id"));
			portRow.set("subsystem_id",subSystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_port", portRow);
		}
	}
	
	//更新
	public void updatePortInfo(List<DataRow> ports,Integer subSystemID){
		NodeService service = new NodeService();
		for (DataRow dataRow : ports) {
//			DataRow spRow = service.getNodeByName(dataRow.getString("name").substring(0,4), subSystemID, null).get(0);
			List<DataRow> list = service.getNodeByName(dataRow.getString("node_name"), subSystemID, null);
			if (list.size() > 0) {
				DataRow spRow = list.get(0);
				dataRow.set("sp_id", spRow.getString("sp_id"));
			}
			dataRow.set("subsystem_id",subSystemID);
			dataRow.remove("node_name");
			String sql = "select port_id from t_res_port where name = '"+dataRow.getString("name")+"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size()>0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_port", dataRow, "port_id", row.getString("port_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_port", dataRow);
			}
		}
	}
	
	// 添加端口性能
	public void addprfPort(List<DataRow> prfports,Integer subSystemID) {
		for (int i = 0; i < prfports.size(); ++i) {
			DataRow prfport = prfports.get(i);
			prfport.set("TIME_ID", SrContant.TIME_FKEY);
			String portname = (String) prfport.get("port_name");
			List<DataRow> rows = getPortByName(portname,subSystemID);
			String portId = rows.get(0).getString("port_id");
			prfport.set("port_id", portId);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_PRF_PORT", prfport);
		}
	}
	
	/**
	 * 添加端口性能信息
	 * @param prfPorts
	 */
	public void addPrfPorts(List<DataRow> prfPorts) {
		for (int i = 0; i < prfPorts.size(); i++) {
			DataRow prfPort = prfPorts.get(i);
			String portName = prfPort.getString(SrTblColConstant.P_PORT_NAME);
			List<DataRow> rows = getPortByName(portName,Integer.parseInt(prfPort.getString(SrTblColConstant.TT_SUBSYSTEM_ID)));
			if (rows.size() > 0) {
				String portId = rows.get(0).getString("port_id");
				prfPort.set("port_id", portId);
				prfPort.remove(SrTblColConstant.TT_SUBSYSTEM_ID);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_port", prfPort);
			}
		}
	}
	
	/**
	 * 获取端口性能信息
	 * @param timeIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfPorts(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select port_id,port_name,SUM(send_io) as send_io,SUM(recv_io) as recv_io,");
		sb.append("SUM(send_kb) as send_kb,SUM(recv_kb) as recv_kb");
		sb.append(" from t_prf_port where time_id in (" + timeIds + ")");
		sb.append(" group by port_id,port_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加端口性能信息(小时/天)
	 * @param prfDiskGroups
	 */
	public void addPerHourAndDayPrfPorts(List<DataRow> prfPorts) {
		for (int i = 0; i < prfPorts.size(); i++) {
			DataRow row = prfPorts.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_port", row);
		}
	}
}
