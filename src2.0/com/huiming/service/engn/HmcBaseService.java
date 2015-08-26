package com.huiming.service.engn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class HmcBaseService extends BaseService{
	
	/**
	 * 产生主键
	 * @param tableName
	 * @param keyName
	 * @return
	 */
	public Integer getKeyNum(String tableName,String keyName){
		String sql="select max("+keyName+") from "+tableName+" where 1=1 ";
		Integer maxNum = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql)+1;
		return maxNum;
	}
	
	/**
	 * 添加或更新
	 * @param row
	 * @param tableName
	 * @param identifykey
	 * @param identifyValue
	 */
	public void updateDataRow(DataRow row,String tableName,String identifykey,String identifyValue){
		String sql="select count(*) from "+tableName+" where "+identifykey+"= '"+identifyValue+"' ";
		int i = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql);
		if(i>0){
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(tableName, row, identifykey, identifyValue);
		}else{
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert(tableName, row);
		}
	}
	
	
	public void updateData(String tableName,DataRow data,String identify,String identifyValue){
		
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(tableName, data, identify, identifyValue);
	}
	
	/**
	 * 添加
	 * @param tableName
	 * @param data
	 */
	public void insertDataRow(String tableName,DataRow data){
		
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert(tableName, data);
	}
	
	/**
	 * 查找
	 * @param row  条件
	 * @param tableName  表名称
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DataRow> getRowlist(DataRow row,String tableName){
		String sql="select t.* from "+tableName+" t where 1=1 ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if(row!=null && row.size()>0){
			Iterator it = row.keySet().iterator();
			while(it.hasNext()){
				String key = it.next().toString();
				sb.append("and "+key+"= ? ");
				args.add(row.get(key));
			}
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
}
