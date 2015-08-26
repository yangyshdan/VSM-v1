package com.huiming.service.svcevenslog;

import java.util.List;

import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

/**
 * 采集SVC磁盘阵列状态信息
 * os_type in (21,38)
 * @author Lch
 *
 */
public class MdiskEventsService extends BaseService{
	//存储系统类型
	private String OS_STR="{svc:'21,38',bsp:'15,37',ds:'25'}";
	/**
	 * 采集Mdisk列表
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public List<DataRow> getMdiskList(String storageType){
		String sql="select e.subsystem_id as dev_id, " +
				"s.the_display_name as dev_name, "+
				"e.storage_extent_id as ele_id, " +
				"e.name as ele_name, " +
				"e.the_display_name as display_name, " +
				"e.the_backend_name as backend_name " +
				"from V_RES_STORAGE_EXTENT e,V_RES_STORAGE_SUBSYSTEM s  " +
				"where e.subsystem_id = s.subsystem_id ";
		if(storageType!=null && storageType.length()>0){
			sql+="and s.os_type in ("+new JSONObject().fromObject(OS_STR).getString(storageType.toLowerCase())+")";
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getLoginInfo(){
		String sql="select * from t_acct_device where state = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	
}
