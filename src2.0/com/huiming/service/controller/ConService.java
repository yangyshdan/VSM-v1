package com.huiming.service.controller;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ConService extends BaseService{
	@SuppressWarnings("unchecked")
	public List<DataRow> getConList(Integer subsystemId,String ele_id){
		String sql="select dev_id,ele_id,ele_name from PRF_TARGET_DSCONTROLLER where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(subsystemId!=null&& subsystemId>0){
			sb.append("and dev_id = ? ");
			args.add(subsystemId);
		}
		if(ele_id!=null && ele_id.length()>0){
			sb.append("and ele_id in ("+ele_id+") ");
		}
		sb.append("group by dev_id,ele_id,ele_name");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	
}
