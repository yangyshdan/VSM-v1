package com.huiming.service.zone;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ZoneService extends BaseService{
	public DBPage getZonePage(int curPage,int numPerPage,String name,String wwn,String active,String zoneType,String zsetId){
		String sql="select z.*,zone.zset_id  from V_RES_ZONE z inner join v_res_zset2zone zone on z.zone_id = zone.zone_id where 1=1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if(name!=null && name.length()>0){
			sb.append("and z.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(wwn!=null && wwn.length()>0){
			sb.append("and z.fabric_wwn = ? ");
			args.add(wwn);
		}
		if(active!=null && active.length()>0){
			sb.append("and z.active = ? ");
			args.add(active);
		}
		if(zoneType!=null && zoneType.length()>0){
			sb.append("and z.zone_type = ? ");
			args.add(zoneType);
		}
		if(zsetId!=null && zsetId.length()>0){
			sb.append("and zone.zset_id = ?");
			args.add(zsetId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getZoneList(String name,String wwn,String active,String zoneType){
		String sql="select z.* from v_res_zone z where 1=1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if(name!=null && name.length()>0){
			sb.append("and z.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(wwn!=null && wwn.length()>0){
			sb.append("and z.fabric_wwn = ? ");
			args.add(wwn);
		}
		if(active!=null && active.length()>0){
			sb.append("and active = ? ");
			args.add(active);
		}
		if(zoneType!=null && zoneType.length()>0){
			sb.append("and zone_type = ? ");
			args.add(zoneType);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getZoneInfo(Integer zoneId){
		String sql="select * from v_res_zone where zone_id = "+zoneId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql);
	}
}
