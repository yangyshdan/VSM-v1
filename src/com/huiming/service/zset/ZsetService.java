package com.huiming.service.zset;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ZsetService extends BaseService {
	public DBPage getzsetPage(String name,String active,String fabricId, int curPage, int numPerPage) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select z.*,f.the_display_name as f_name from V_RES_ZSET z,V_RES_FABRIC f where z.THE_FABRIC_ID = f.FABRIC_ID ");
		if (name != null && name.length() > 0) {
			sb.append("and z.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(active!=null && active.length()>0){
			sb.append("and z.active = ? ");
			args.add(active);
		}
		if(fabricId!=null && fabricId.length()>0){
			sb.append("and z.the_fabric_id = ? ");
			args.add(fabricId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}

	@SuppressWarnings("unchecked")
	public List<DataRow> getzsetList(String name,String active) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select z.*,f.the_display_name as f_name from V_RES_ZSET z,V_RES_FABRIC f where z.THE_FABRIC_ID = f.FABRIC_ID ");
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
	public List<DataRow> getzoneList(String zsetId){
		String sql="select z.* from V_RES_ZONE z where 1=1 ";
		if(zsetId!=null && zsetId.length()>0){
			sql+="and z.zset_id = "+zsetId;
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public DBPage getzonePage(String zsetId,int curPage,int numPerPage){
		String sql="select z.*,zone.zset_id  from V_RES_ZONE z inner join v_res_zset2zone zone on z.zone_id = zone.zone_id where 1=1 ";
		if(zsetId!=null && zsetId.length()>0){
			sql+="and zone.zset_id = "+zsetId;
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql, curPage, numPerPage);
	}
	
	public DataRow getZsetInfo(String zsetId){
		String sql="select z.*,f.the_display_name as f_name from V_RES_ZSET z,V_RES_FABRIC f where z.THE_FABRIC_ID = f.FABRIC_ID and z.zset_id="+zsetId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
}
