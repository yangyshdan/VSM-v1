package com.huiming.service.alert;

import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;


public class NasAlertService extends BaseService{

	public DBPage getLogPage(int curPage,int numPerPage){
		StringBuffer sql = new StringBuffer("");
		
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(), curPage, numPerPage);
	}
	
	public void batchInsert(List<DataRow> nasList){
		for (DataRow dataRow : nasList) {
			Session session = getSession();
			try{
				session.beginTrans();
				session.insert("t_res_nas", dataRow);
				session.commitTrans();
			}catch(Exception e){
				e.printStackTrace();
				if(session!=null){
					session.rollbackTrans();
				}
			}finally{
				if(session!=null){
					session.close();
				}
			}
			
		}
	}
}
