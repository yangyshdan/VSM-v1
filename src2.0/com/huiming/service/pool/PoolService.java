package com.huiming.service.pool;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class PoolService extends BaseService {

	//分页查询
	public DBPage getPoolPage(int curPage, int numPerPage, String name, String greatTotal_Capacity,String lessTotal_Capacity,
			Integer system_id){
		String sql="select p.*,s.the_display_name as sub_name " +
				"from v_res_storage_pool p,v_res_storage_subsystem s " +
				"where s.subsystem_id = p.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append(" and p.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(greatTotal_Capacity!=null&&greatTotal_Capacity.length()>0){
			sb.append(" and p.the_space <= ?");
			args.add(greatTotal_Capacity);
		}
		if(lessTotal_Capacity!=null&&lessTotal_Capacity.length()>0){
			sb.append(" and p.the_space >= ?");
			args.add(lessTotal_Capacity);
		}
		if(system_id!=null&&system_id>0){
			sb.append(" and p.subsystem_id = ?");
			args.add(system_id);
		}
		sb.append(" order by p.pool_id desc");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	//所有存储池信息
	@SuppressWarnings("unchecked")
	public List<DataRow> getPoolsInfo(String name,String greatTotal_Capacity,String lessTotal_Capacity,
			Integer system_id){
		String sql="select p.*,s.the_display_name as sub_name " +
		"from v_res_storage_pool p,v_res_storage_subsystem s " +
		"where s.subsystem_id = p.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append(" and p.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(greatTotal_Capacity!=null&&greatTotal_Capacity.length()>0){
			sb.append(" and p.the_space <= ?");
			args.add(greatTotal_Capacity);
		}
		if(lessTotal_Capacity!=null&&lessTotal_Capacity.length()>0){
			sb.append(" and p.the_space >= ?");
			args.add(lessTotal_Capacity);
		}
		if(system_id!=null&&system_id>0){
			sb.append(" and p.subsystem_id = ?");
			args.add(system_id);
		}
		sb.append(" order by p.the_consumed_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
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
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getPoolInfoByName(String name,Integer subSystemID){
		String sql="select p.* from t_res_storagepool p where p.name='"+name+"' and p.SUBSYSTEM_ID = "+subSystemID+" order by p.update_timestamp desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	//查询容量
	@SuppressWarnings("unchecked")
	public List<DataRow> getCapacity(Integer subSystemID){
		List<Object> args = new ArrayList<Object>();
		String sql="select the_consumed_space,the_display_name,pool_id,subsystem_id from v_res_storage_pool where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if(subSystemID!=null && subSystemID>0){
			sb.append(" and subsystem_id = ? ");
			args.add(subSystemID);
		}
		sb.append("order by the_consumed_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray(),WebConstants.DEFAULT_CONFIG_TOP);
	}
	//查询id详细信息
	public DataRow getPoolById(Integer poolId){
		String sql="select p.*,s.the_display_name as sub_name " +
		"from v_res_storage_pool p,v_res_storage_subsystem s " +
		"where s.subsystem_id = p.subsystem_id ";
		List<Object> args = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(sql);
		if(poolId!=null && poolId>0){
			sb.append("and p.pool_id = ? ");
			args.add(poolId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString(),args.toArray());
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
	
	public DBPage getVolumePageByPoolId(int curPage,int numPerPage,Integer poolId){
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
		"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id and v.pool_id = "+poolId;
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sql, curPage, numPerPage);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeByPoolId(Integer poolId){
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
		"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id and v.pool_id = "+poolId;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
}
