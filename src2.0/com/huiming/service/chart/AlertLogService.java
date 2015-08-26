package com.huiming.service.chart;

import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class AlertLogService extends BaseService{
	
	public List<DataRow> getAlertLog(){
		String sql = "select ALERT_ID,FIRST_ALERT_TIME,LAST_ALERT_TIME,RESOURCE_NAME,STATE,THE_SEVERITY,ALERT_COUNT,MSG,RESOURCE_TYPE from V_ALERT_LOG order by LAST_ALERT_TIME desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql, 10);
	}
}
