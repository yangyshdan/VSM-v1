package com.huiming.service.fabric;

import java.util.ArrayList;
import java.util.List;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class FabricService extends BaseService {
	/**
	 * 分页查询Fabiric网络数据
	 * @param name
	 * @param status
	 * @param curPage
	 * @param numPerPage
	 * @param limitIds
	 * @return
	 */
	public DBPage getfabricPage(String name,String status,int curPage,int numPerPage,String limitIds){
		String sql="select F.*,Z.ZSET_ID,Z.ZSET_NAME,Z.THE_ZONE_COUNT from V_RES_FABRIC F,V_RES_ZSET Z where F.THE_ACTIVE_ZSET_ID = Z.ZSET_ID ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds != "") {
			sb.append("and F.FABRIC_ID in (" + limitIds + ") ");
		}
		if (name != null && name != "") {
			sb.append("and F.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (status != null && status != "") {
			sb.append("and F.the_propagated_status = ? ");
			args.add(status);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取Fabric网络数据
	 * @param name
	 * @param status
	 * @param limitIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getfabricList(String name,String status,String limitIds){
		String sql="select F.*,Z.ZSET_ID,Z.ZSET_NAME,Z.THE_ZONE_COUNT from V_RES_FABRIC F,V_RES_ZSET Z where F.THE_ACTIVE_ZSET_ID = Z.ZSET_ID ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (limitIds != null && limitIds != "") {
			sb.append("and F.FABRIC_ID in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sb.append("and F.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (status != null && status != "") {
			sb.append("and F.the_propagated_status = ? ");
			args.add(status);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 获取Fabric网络详细数据
	 * @param fabricId
	 * @return
	 */
	public DataRow getFabricInfo(String fabricId){
		String sql="select * from V_RES_FABRIC where FABRIC_ID = "+fabricId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList(String fabricId){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb= new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(fabricId!=null && fabricId.length()>0){
			sb.append("and s.THE_FABRIC_ID = ? ");
			args.add(fabricId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 获取Fabric网络下的交换机
	 * @param fabricId
	 * @param curPage
	 * @param numPerPage
	 * @param limitIds
	 * @return
	 */
	public DBPage getSwitchPage(String fabricId,int curPage,int numPerPage, String limitIds){
		String sql = "select s.*,m.model_name,v.vendor_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb= new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (fabricId != null && fabricId.length() > 0) {
			sb.append("and s.the_fabric_id = ? ");
			args.add(fabricId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
}
