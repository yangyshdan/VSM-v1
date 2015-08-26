package com.huiming.service.report;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class TemplateService extends BaseService{
	
	public DBPage getPage(String name,String reportType,String startTime,String endTime,int curPage,int numPerPage,Long userId){
		StringBuffer sb = new StringBuffer("select * from tnreport_task_config where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (name != null && name.length() > 0) {
			sb.append("and the_display_name like ? ");
			args.add("%" + name + "%");
		}
		if (reportType != null && reportType.length() > 0) {
			sb.append("and report_type = ? ");
			args.add(reportType);
		}
		if (startTime != null && startTime.length() > 0) {
			sb.append("and create_time >= ? ");
			args.add(startTime);
		}
		if (endTime != null && endTime.length() > 0) {
			sb.append("and create_time <= ? ");
			args.add(endTime);
		}
		if (userId != null && userId > 0) {
			sb.append("and user_id = ? ");
			args.add(userId);
		}
		sb.append("order by create_time desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(), args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void delTemplate(int id){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tnreport_task_config", "id", id);
	}
	
	/**
	 * 添加或更新
	 * @param id
	 * @param row
	 */
	public void updateTemplate(int id,DataRow row){
		if(id>0){
			row.remove("id");
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("tnreport_task_config", row, "id", id);
		}else{
			int maxId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(id) from tnreport_task_config");
			row.set("id", maxId+1);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tnreport_task_config", row);
		}
	}
	
	public DataRow getTemplateInfo(String id) {
		String sql = "select * from tnreport_task_config where id = " + id;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
}