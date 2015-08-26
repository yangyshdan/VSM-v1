package com.huiming.service.sr.hba;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class StoragehbaService extends BaseService{
	//添加
	public void addStoragehbas(List<DataRow> storagehbas, Integer subsystemID){
		for (DataRow dataRow : storagehbas) {
			dataRow.set("subsystem_id", subsystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagehba", dataRow);
		}
	}
	//更新
	public void updateStoragehba(List<DataRow> storagehbas, Integer subsystemID){
		for (DataRow dataRow : storagehbas) {
			dataRow.set("subsystem_id", subsystemID);
			String sql = "select hba_uid from t_res_storagehba where hba_uid = '"+dataRow.getString("hba_uid")+"' and subsystem_id = "+subsystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size() > 0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagehba", dataRow, "hba_id", dataRow.getString("hba_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagehba", dataRow);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getHBAbyUID(String uid,Integer subsystemId){
		String sql = "select * from t_res_storagehba where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(uid!=null && uid.length()>0){
			sb.append("and hba_uid = ? ");
			args.add(uid);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
}
