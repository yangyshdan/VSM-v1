package com.huiming.service.zone;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ZoneService extends BaseService {
	
	/**
	 * 分页获取Zone数据
	 * @param curPage
	 * @param numPerPage
	 * @param name
	 * @param wwn
	 * @param active
	 * @param zoneType
	 * @param zsetId
	 * @param limitIds
	 * @return
	 */
	public DBPage getZonePage(int curPage,int numPerPage,String name,String wwn,String active,String zoneType,String zsetId,String limitIds){
		String sql = "select z.*,zone.zset_id,zs.zset_name,zs.the_fabric_id,f.the_display_name as fabric_name from v_res_zone z "
			+ "left join v_res_zset2zone zone on z.zone_id = zone.zone_id "
			+ "left join v_res_zset zs on zs.zset_id = zone.zset_id "
			+ "left join v_res_fabric f on f.fabric_id = zs.the_fabric_id where 1 = 1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and z.zone_id in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sb.append("and z.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (wwn != null && wwn.length() > 0) {
			sb.append("and z.fabric_wwn = ? ");
			args.add(wwn);
		}
		if (active != null && active.length() > 0) {
			sb.append("and z.active = ? ");
			args.add(active);
		}
		if (zoneType != null && zoneType.length() > 0) {
			sb.append("and z.zone_type = ? ");
			args.add(zoneType);
		}
		if (zsetId != null && zsetId.length() > 0) {
			sb.append("and zone.zset_id = ?");
			args.add(zsetId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取Zone列表数据
	 * @param name
	 * @param wwn
	 * @param active
	 * @param zoneType
	 * @param limitIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getZoneList(String name,String wwn,String active,String zoneType,String limitIds){
		String sql = "select z.*,zone.zset_id,zs.zset_name,f.the_display_name as fabric_name from v_res_zone z "
			+ "left join v_res_zset2zone zone on z.zone_id = zone.zone_id "
			+ "left join v_res_zset zs on zs.zset_id = zone.zset_id "
			+ "left join v_res_fabric f on f.fabric_id = zs.the_fabric_id where 1 = 1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if (limitIds != null && limitIds.length() > 0) {
			sb.append("and z.zone_id in (" + limitIds + ") ");
		}
		if (name != null && name.length() > 0) {
			sb.append("and z.the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (wwn != null && wwn.length() > 0) {
			sb.append("and z.fabric_wwn = ? ");
			args.add(wwn);
		}
		if (active != null && active.length() > 0) {
			sb.append("and z.active = ? ");
			args.add(active);
		}
		if (zoneType != null && zoneType.length() > 0) {
			sb.append("and z.zone_type = ? ");
			args.add(zoneType);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getZoneInfo(Integer zoneId){
		String sql="select * from v_res_zone where zone_id = "+zoneId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
}
