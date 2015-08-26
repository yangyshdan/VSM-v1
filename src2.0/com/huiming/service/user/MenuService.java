package com.huiming.service.user;

import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;
/**
 * @Name MenuService
 * @Author gugu
 * @Date 2013-8-8обнГ05:43:51
 * @Description TODO
 */
public class MenuService extends BaseService {
	
	public List<DataRow> getMenu(String ruleId){
		List<DataRow> list = null;
		String sql = "select a.* from  TsMenu a inner join TsRoleMenu b on a.FId = b.FMenuId where b.FRoleId = ? ";
		list = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{ruleId});
		return list;
	}
}
