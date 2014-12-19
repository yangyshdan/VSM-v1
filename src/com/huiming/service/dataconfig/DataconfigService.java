package com.huiming.service.dataconfig;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class DataconfigService extends BaseService{
	
	/**
	 * 加载配置
	 * @param dataType
	 * @return
	 */
	public DataRow getDataConfigInfo(String dataType){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tndataconfig where data_type = ? ",new Object[]{dataType});
	}
	
	/**
	 * 更新配置
	 * @param dataType
	 * @param row
	 */
	public void updateDataconfig(String dataType,DataRow row){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("tndataconfig", row, "data_type", dataType);
	}
}
