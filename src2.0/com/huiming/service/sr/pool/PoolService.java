package com.huiming.service.sr.pool;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class PoolService extends BaseService {

	//分页查询
	public DBPage getPoolPage(int curPage,int numPerPage,String name,Long greatTotal_Capacity,Long lessTotal_Capacity,
			String system_id){
		StringBuffer sql = new StringBuffer("select * from t_res_storagepool where 1=1");
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sql.append(" and name like ? ");
			args.add("%"+name+"%");
		}
		if(greatTotal_Capacity!=null&&greatTotal_Capacity!=0){
			sql.append(" and total_usable_capacity <= ?");
			args.add(greatTotal_Capacity);
		}
		if(lessTotal_Capacity!=null&&lessTotal_Capacity!=0){
			sql.append(" and total_usable_capacity >= ?");
			args.add(lessTotal_Capacity);
		}
		if(system_id!=null&&system_id.length()>0){
			sql.append(" and subsystem_id = ?");
			args.add(system_id);
		}
		sql.append(" order by total_usable_capacity desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(), curPage, numPerPage);
	}
	//查询容量
	@SuppressWarnings("unchecked")
	public List<DataRow> getCapacity(String subSystemID){
		String sql="select p.* from t_res_storagepool p where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(subSystemID!=null && subSystemID.length()>0){
			 sb.append("and p.SUBSYSTEM_ID = ? ");
			 args.add(subSystemID);
		}
		sb.append("order by p.TOTAL_USABLE_CAPACITY desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray(),20);
	}
	//查询id详细信息
	public DataRow getPoolById(String id,String system_id,String pool_name){
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select * from t_res_storagepool where 1=1");
		if(id!=null&&id.length()>0){
			sql.append(" and pool_id = ?");
			args.add(id);
		}
		if(system_id!=null&&system_id.length()>0){
			sql.append(" and subsystem_id = ?");
			args.add(system_id);
		}
		if(pool_name!=null&&pool_name.length()>0){
			sql.append(" and name = ?");
			args.add(pool_name);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql.toString(),args.toArray());
	}
	//添加存储池信息
	public void addPool(List<DataRow> pools,Integer subSystemID){
		for (int i = 0; i <pools.size(); ++i) {
			DataRow pool = pools.get(i);
			pool.set("subsystem_id",subSystemID);	
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagepool", pool);
		}
	}
	//更新
	public void updatePoolInfo(List<DataRow> pools,Integer subSystemID){
		for (DataRow dataRow : pools) {
			dataRow.set("subsystem_id",subSystemID);	
			String sql = "select name from t_res_storagepool where name = '"+dataRow.getString("name") +
					"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size()>0){
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagepool", dataRow, "pool_id", row.getInt("pool_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagepool", dataRow);
			}
		}
	}
	
	//获得存储池报表数据
	@SuppressWarnings("unchecked")
	public List<DataRow> reportPool(Integer subSystemID){
		String sql="SELECT * FROM t_res_storagepool where subsystem_id = "+subSystemID;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getPoolInfoByName(String name,Integer subSystemID){
		String sql="select p.* from t_res_storagepool p where p.name='"+name+"' and p.SUBSYSTEM_ID = "+subSystemID+" order by p.update_timestamp desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> doExportConfigDatas(String name,Long greatTotal_Capacity,Long lessTotal_Capacity,
			String system_id){
		StringBuffer sql = new StringBuffer("select * from t_res_storagepool where 1=1");
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sql.append(" and name like ? ");
			args.add("%"+name+"%");
		}
		if(greatTotal_Capacity!=null&&greatTotal_Capacity!=0){
			sql.append(" and total_usable_capacity <= ?");
			args.add(greatTotal_Capacity);
		}
		if(lessTotal_Capacity!=null&&lessTotal_Capacity!=0){
			sql.append(" and total_usable_capacity >= ?");
			args.add(lessTotal_Capacity);
		}
		if(system_id!=null&&system_id.length()>0){
			sql.append(" and subsystem_id = ?");
			args.add(system_id);
		}
		sql.append(" order by total_usable_capacity desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args.toArray());
	}
}
