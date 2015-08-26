package com.huiming.service.zset;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ZsetService extends BaseService {
	
	/**
	 * 分页查询ZoneZet
	 * @param name
	 * @param active
	 * @param fabricId
	 * @param curPage
	 * @param numPerPage
	 * @param limitIds
	 * @return
	 */
	public DBPage getzsetPage(String name,String active,String fabricId, int curPage, int numPerPage, String limitIds) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select z.*,f.the_display_name as f_name from V_RES_ZSET z,V_RES_FABRIC f where z.THE_FABRIC_ID = f.FABRIC_ID ");
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and z.zset_id in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sb.append("and z.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(active!=null && active.length()>0){
			sb.append("and z.active = ? ");
			args.add(active);
		}
		if(fabricId != null && fabricId.length() > 0){
			sb.append("and z.the_fabric_id = ? ");
			args.add(fabricId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getZsetList(String name,String active,String limitIds) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select z.*,f.the_display_name as f_name from V_RES_ZSET z,V_RES_FABRIC f where z.THE_FABRIC_ID = f.FABRIC_ID ");
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and z.zset_id in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sb.append("and z.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(active!=null && active.length()>0){
			sb.append("and z.active = ? ");
			args.add(active);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getZoneList(String zsetId,String limitIds){
		String sql = "select z.*,zone.zset_id,zs.zset_name,f.the_display_name as fabric_name from v_res_zone z "
			+ "left join v_res_zset2zone zone on z.zone_id = zone.zone_id "
			+ "left join v_res_zset zs on zs.zset_id = zone.zset_id "
			+ "left join v_res_fabric f on f.fabric_id = zs.the_fabric_id where 1 = 1 ";
		if (zsetId != null && zsetId.length() > 0) {
			sql += "and zone.zset_id = " + zsetId;
		}
		if (limitIds != null && limitIds.length() > 0) {
			sql = sql + " and z.zone_id in (" + limitIds + ")";
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	/**
	 * 分页获取Zone信息列表
	 * @param zsetId
	 * @param curPage
	 * @param numPerPage
	 * @param limitIds
	 * @return
	 */
	public DBPage getZonePage(String zsetId,int curPage,int numPerPage,String limitIds){
		String sql = "select z.*,zone.zset_id,zs.zset_name,zs.the_fabric_id,f.the_display_name as fabric_name from v_res_zone z "
			+ "left join v_res_zset2zone zone on z.zone_id = zone.zone_id "
			+ "left join v_res_zset zs on zs.zset_id = zone.zset_id "
			+ "left join v_res_fabric f on f.fabric_id = zs.the_fabric_id where 1 = 1 ";
		if (zsetId != null && zsetId.length() > 0) {
			sql += " and zone.zset_id = " + zsetId;
		}
		if (limitIds != null && limitIds.length() > 0) {
			sql = sql + " and z.zone_id in (" + limitIds + ")";
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql, curPage, numPerPage);
	}
	
	public DataRow getZsetInfo(String zsetId){
		String sql="select z.*,f.the_display_name as f_name from V_RES_ZSET z,V_RES_FABRIC f where z.THE_FABRIC_ID = f.FABRIC_ID and z.zset_id="+zsetId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
}
