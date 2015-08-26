package com.huiming.service.volume;

import java.util.ArrayList;
import java.util.List;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.project.web.WebConstants;

public class VolumeService extends BaseService {
	/**
	 * 卷分页信息
	 * @param curPage
	 * @param numPerPage
	 * @param name
	 * @param greatLogical_Capacity
	 * @param lessLogical_Capacity
	 * @param pool_id
	 * @param system_id
	 * @return
	 */
	public DBPage getVolumePage(int curPage, int numPerPage, String theDisplayName, String lessCapacity,String greatCapacity,
			String pool_id, Integer system_id) {
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
				"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
				"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (theDisplayName != null && theDisplayName.length() > 0) {
			sb.append(" and v.the_display_name like ? ");
			args.add("%" + theDisplayName + "%");
		}
		if (lessCapacity != null && lessCapacity.length()> 0) {
			sb.append(" and v.the_capacity >= ?");
			args.add(lessCapacity);
		}
		if (greatCapacity != null && greatCapacity.length()> 0) {
			sb.append(" and v.the_capacity <= ?");
			args.add(greatCapacity);
		}
		if (pool_id != null && pool_id.length() > 0) {
			sb.append(" and v.pool_id = ?");
			args.add(pool_id);
		}
		if (system_id != null && system_id > 0) {
			sb.append(" and v.subsystem_id = ?");
			args.add(system_id);
		}
		sb.append(" order by v.svid desc");
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeCapacityInfo(Integer subsystemId){
		String sql="select the_used_space,the_display_name,svid,subsystem_id from v_res_storage_volume where subsystem_id = "+subsystemId+" order by the_used_space desc";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,WebConstants.DEFAULT_CONFIG_TOP);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeInfo(String theDisplayName, String lessCapacity,String greatCapacity,String pool_id, Integer system_id){
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
		"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (theDisplayName != null && theDisplayName.length() > 0) {
			sb.append(" and v.the_display_name like ? ");
			args.add("%" + theDisplayName + "%");
		}
		if (lessCapacity != null && lessCapacity.length() > 0) {
			sb.append(" and v.the_capacity >= ?");
			args.add(lessCapacity);
		}
		if (greatCapacity != null && greatCapacity.length()> 0) {
			sb.append(" and v.the_capacity <= ?");
			args.add(greatCapacity);
		}
		if (pool_id != null && pool_id.length() > 0) {
			sb.append(" and v.pool_id = ?");
			args.add(pool_id);
		}
		if (system_id != null && system_id > 0) {
			sb.append(" and v.subsystem_id = ?");
			args.add(system_id);
		}
		sb.append(" order by v.the_used_space desc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 卷详细信息
	 * @param id
	 * @param system_id
	 * @return
	 */
	public DataRow getVolumeById(Integer id) {
		String sql="select v.* ,s.the_display_name as sub_name,p.the_display_name as pool_name " +
		"from v_res_storage_volume v,v_res_storage_pool p,v_res_storage_subsystem s " +
		"where v.pool_id = p.pool_id and v.subsystem_id = s.subsystem_id ";
		StringBuffer sb = new StringBuffer(sql);
		sb.append("and v.svid = "+id);
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString());
	}
	
	// 添加卷
	public void addVolume(List<DataRow> volumes,Integer subSystemID) {
		PoolService pool = new PoolService();
		NodeService node = new NodeService();
		for (DataRow volumeRow:volumes) {
			volumeRow.set("subsystem_id", subSystemID);
			String pool_name = volumeRow.getString("pool_name");
			String currentOwner = volumeRow.getString("current_owner");
			if(currentOwner!=null && currentOwner.length()>0){
				DataRow spRow = node.getNodeByName(currentOwner, subSystemID, null).get(0);
				if(spRow!=null && spRow.size()>0){
					volumeRow.set("sp_id", spRow.getString("sp_id"));
				}
			}
			
			if (pool_name!=null && !pool_name.equals("N/A")) {
				DataRow id = pool.getPoolById(null, subSystemID.toString(), pool_name);
				if(id!=null && id.size()>0){
					volumeRow.set("pool_id", id.getLong("pool_id"));
				}
			}
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagevolume",volumeRow);
		}
	}
	
	//更新卷
	public void updateVolumeInfo(List<DataRow> volumes,Integer subSystemID){
		PoolService pool = new PoolService();
		NodeService node = new NodeService();
		for (DataRow volumeRow : volumes) {
			String pool_name = volumeRow.getString("pool_name");
			String currentOwner = volumeRow.getString("current_owner");
			volumeRow.set("subsystem_id", subSystemID);
			if(currentOwner!=null && currentOwner.length()>0){
				DataRow spRow = node.getNodeByName(currentOwner, subSystemID, null).get(0);
				if(spRow!=null && spRow.size()>0){
					volumeRow.set("sp_id", spRow.getString("sp_id"));
				}
			}
			if (pool_name!=null && !pool_name.equals("N/A")) {
				DataRow id = pool.getPoolById(null, subSystemID.toString(), pool_name);
				if(id!=null && id.size()>0){
					volumeRow.set("pool_id", id.getLong("pool_id"));
				}
			}
			String sql = "select volume_id from t_res_storagevolume where name = '"+volumeRow.getString("name")+"' and subsystem_id = "+subSystemID;
			DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
			if(row!=null && row.size() > 0){   
				//更新
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_res_storagevolume", volumeRow, "volume_id", row.getString("volume_id"));
			}else{
				//添加
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_res_storagevolume",volumeRow);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolume(Integer subsystemId){
		String sql="select svid,the_display_name,subsystem_id from v_res_storage_volume where subsystem_id = "+subsystemId;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}

//	public List<DataRow> getTop10IOPs(){
//		String sql = "SELECT * FROM (select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A3 as iops FROM PRF_TARGET_SVC_VDISK"
//			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A427 as iops FROM PRF_TARGET_BSP_VOLUME"
//			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A183 as iops FROM PRF_TARGET_DSVOL) a ORDER BY a.IOPS DESC  Fetch First 10 Rows Only";
//		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
//	}
	
	public List<DataRow> getTop10IOPs(){
		String sql = "select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A3 as iops FROM PRF_TARGET_SVC_VDISK ORDER BY A3 DESC";
			
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql,10);
	}
	
	public List<DataRow> getTop10DataRate(){
		String sql = "SELECT * FROM (select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A9 as datarate FROM PRF_TARGET_SVC_VDISK"
			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A433 as datarate FROM PRF_TARGET_BSP_VOLUME"
			+" union select DEV_ID,ELE_ID,ELE_NAME,PRF_TIMESTAMP,A195 as datarate FROM PRF_TARGET_DSVOL) ORDER BY datarate DESC Fetch First 10 Rows Only";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getVolumeByName(String name,Integer subSystemID){
		String sql="select v.* from t_res_storagevolume v where v.name ='"+name+"' and v.SUBSYSTEM_ID = "+subSystemID+" order by v.update_timestamp desc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	// 添加卷性能
	public void addprfVolume(List<DataRow> prfvolumes,Integer subSystemID) {
		for (int i = 0; i < prfvolumes.size(); ++i) {
			DataRow prfvolume = prfvolumes.get(i);
			prfvolume.set("TIME_ID", SrContant.TIME_FKEY);
			String volumename = (String) prfvolume.get("volume_name");
			VolumeService service = new VolumeService();
			List<DataRow> volumes = service.getVolumeByName(volumename, subSystemID);
			if (volumes!=null && volumes.size() > 0) {
				for (DataRow dataRow : volumes) {
					String id = dataRow.getString("volume_id");
					prfvolume.set("volume_id", id);
					getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_PRF_STORAGEVOLUME", prfvolume);
				}
			}

		}
	}
	
	/**
	 * 添加卷性能信息
	 * @param prfVolumes
	 */
	public void addPrfVolumes(List<DataRow> prfVolumes) {
		for (int i = 0; i < prfVolumes.size(); i++) {
			DataRow prfVolume = prfVolumes.get(i);
			String name = prfVolume.getString(SrTblColConstant.SV_VOLUME_NAME);
			List<DataRow> rows = getVolumeByName(name, Integer.parseInt(prfVolume.getString(SrTblColConstant.TT_SUBSYSTEM_ID)));
			if (rows.size() > 0) {
				String volumeId = rows.get(0).getString("volume_id");
				prfVolume.set("volume_id", volumeId);
				prfVolume.remove(SrTblColConstant.TT_SUBSYSTEM_ID);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_storagevolume", prfVolume);
			}
		}
	}
	
	/**
	 * 获取卷性能信息
	 * @param timeIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPrfVolumes(String timeIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select volume_id,volume_name,SUM(read_io) as read_io,SUM(write_io) as write_io,");
		sb.append("SUM(read_hit_io) as read_hit_io,SUM(write_hit_io) as write_hit_io,");
		sb.append("SUM(read_kb) as read_kb,SUM(write_kb) as write_kb,");
		sb.append("SUM(read_io*read_io_time)/SUM(read_io_time) as read_io_time,");
		sb.append("SUM(write_io*write_io_time)/SUM(write_io_time) as write_io_time");
		sb.append(" from t_prf_storagevolume where time_id in (" + timeIds + ")");
		sb.append(" group by volume_id,volume_name");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString());
	}
	
	/**
	 * 添加卷性能信息(小时/天)
	 * @param prfDiskGroups
	 */
	public void addPerHourAndDayPrfVolumes(List<DataRow> prfVolumes) {
		for (int i = 0; i < prfVolumes.size(); i++) {
			DataRow row = prfVolumes.get(i);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_storagevolume", row);
		}
	}
}
