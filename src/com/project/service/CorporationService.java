package com.project.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.project.web.WebConstants;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.service.BaseService;

/**
 * ����: ��˾��Ϣ
 * ��˾: ӯ����Ϣ 
 * ����: �ž� 
 * �汾: 1.0 
 * ��������: 2012-11-16 
 * ����ʱ��: ����05:41:35
 */
public class CorporationService extends BaseService
{
	
	private static Logger logger = Logger.getLogger(CorporationService.class);
	
	/**
	 * ������ݿ�����ӿ�
	 * @return
	 */
	private JdbcTemplate getJdbcTemplate()
	{
		JdbcTemplate jdbcTemplate = getJdbcTemplate(WebConstants.DB_DEFAULT);
		return jdbcTemplate;
	}
	
	public List findAllCorporaction()
	{
		try
		{
			String sql = "select * from jxmc_enterprise_detail";
			List dataList = getJdbcTemplate().query(sql);
			return dataList;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}
	
	public DBPage findAllCorporaction(int curPage,int numPerPage)
	{
		try
		{
			String sql = "select * from jxmc_enterprise_detail";
			DBPage queryPage = getJdbcTemplate().queryPage(sql, curPage, numPerPage);
			return queryPage;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}
	
	public DataRow findCorporactionById(int enterpriseid)
	{
		try
		{
			String sql = "select * from jxmc_enterprise_detail where enterprise_id = ? ";
			DataRow datarow = getJdbcTemplate().queryMap(sql,new Object[]{enterpriseid});
			return datarow;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}
}
