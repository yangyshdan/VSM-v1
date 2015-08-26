package com.huiming.service.node;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrTblColConstant;
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
	
	//添加
	public void addStoragenodes(List<DataRow> nodes, Integer subsystemID){
		for (DataRow dataRow : nodes) {
			dataRow.set("subsystem_id", subsystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagenode", dataRow);
		}
	}
	
	//更新
	public void updateStoragenodes(List<DataRow> nodes, Integer subsystemID){
		for (DataRow dataRow : nodes) {
			dataRow.set("subsystem_id", subsystemID);
			String sql = "select sp_name from t_res_storagenode where sp_name = '"+dataRow.getString("sp_name")+"' and subsystem_id = "+subsystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size() > 0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagenode", dataRow, "sp_id", row.getInt("sp_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagenode", dataRow);
			}
		}
	}
	
	//查
	@SuppressWarnings("unchecked")
	public List<DataRow> getNodeByName(String name,Integer subsystemId,Integer nodeId){
		String sql="select * from t_res_storagenode where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and sp_name = ? ");
			args.add(name);
		}
		if(subsystemId!=null &&subsystemId!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		if(nodeId!=null && nodeId!=0){
			sb.append("and sp_id = ? ");
			args.add(nodeId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 添加节点性能信息
	 * @param prfNodes
	 */
	public void addPrfNodes(List<DataRow> prfNodes) {
		for (int i = 0; i < prfNodes.size(); i++) {
			DataRow prfNode = prfNodes.get(i);
			String nodeName = prfNode.getString(SrTblColConstant.SN_SP_NAME);
			List<DataRow> rows = getNodeByName(nodeName, Integer.parseInt(prfNode.getString(SrTblColConstant.TT_SUBSYSTEM_ID)), 0);
			if (rows.size() > 0) {
				String nodeId = rows.get(0).getString("sp_id");
				prfNode.set("sp_id", nodeId);
				prfNode.remove(SrTblColConstant.TT_SUBSYSTEM_ID);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_storagenode", prfNode);
			}
		}
	}
	
	/**
	 * 获取节点性能信息
	 * @param timeIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfNodes(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select sp_id,sp_name,SUM(read_io) as read_io,SUM(write_io) as write_io,");
		sb.append("SUM(read_kb) as read_kb,SUM(write_kb) as write_kb,");
		sb.append("SUM(read_io*read_io_time)/SUM(read_io_time) as read_io_time,");
		sb.append("SUM(write_io*wirte_io_time)/SUM(wirte_io_time) as wirte_io_time");
		sb.append(" from t_prf_storagenode where time_id in (" + timeIds + ")");
		sb.append(" group by sp_id,sp_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加节点性能信息(小时/天)
	 * @param prfDiskGroups
	 */
	public void addPerHourAndDayPrfNodes(List<DataRow> prfNodes) {
		for (int i = 0; i < prfNodes.size(); i++) {
			DataRow row = prfNodes.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_storagenode", row);
		}
	}
}
