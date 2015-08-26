package com.huiming.service.relationmap;

import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.service.ddm.DdmService;
import com.huiming.service.diskgroup.DiskgroupService;
import com.huiming.service.hostgroup.HostgroupService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.storagehbaservice.StoragehbaService;
import com.huiming.service.volume.VolumeService;
import com.project.web.WebConstants;

public class RelationMapService extends BaseService {
	//添加磁盘组和DDM关系表
	public void addDiskgroupAndDdm(List<DataRow> dgandddms,Integer subSystemID){
		for (int i = 0; i <dgandddms.size(); ++i) {
			String ddmId = null;
			String disId = null;
			DataRow dgandddm = dgandddms.get(i);
			//获取DDM_ID
			String ddmname=(String) dgandddm.get("ddm_name");
			DdmService service=new DdmService();
			List<DataRow> rows = service.getDDMInfoByName(ddmname,subSystemID);
			if(rows!=null && rows.size()>0){
				ddmId = rows.get(0).getString("ddm_id");
				dgandddm.set("ddm_id", ddmId);
			}
        	//获取DiskGroup_ID
        	String diskgroupname=(String) dgandddm.get("diskgroup_name");
			DiskgroupService service1=new DiskgroupService();
			List<DataRow> dRows = service1.getDiskByName(diskgroupname,subSystemID);
			if(dRows!=null && dRows.size()>0){
				disId = dRows.get(0).getString("diskgroup_id");
				dgandddm.set("diskgroup_id", disId);
			}
			if(ddmId!=null && disId!=null){
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_MAP_DISKGROUP2STORAGE_DDM", dgandddm);
			}
		}		
	}
	public void updateDiskgroupAndDDM(List<DataRow> dgandddms,Integer subSystemID){
		for (DataRow dataRow : dgandddms) {
			String sql = "delete from t_map_diskgroup2storage_ddm where ddm_name = '"+dataRow.getString("ddm_name")+"' and diskgroup_name = '"+dataRow.getString("diskgroup_name")+"'";
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
			
			String ddmId = null;
			String disId = null;
			//获取DDM_ID
			String ddmname=(String) dataRow.get("ddm_name");
			DdmService service=new DdmService();
			List<DataRow> rows = service.getDDMInfoByName(ddmname,subSystemID);
			if(rows!=null && rows.size()>0){
				ddmId = rows.get(0).getString("ddm_id");
				dataRow.set("ddm_id", ddmId);
			}
        	//获取DiskGroup_ID
        	String diskgroupname=(String) dataRow.get("diskgroup_name");
			DiskgroupService service1=new DiskgroupService();
			List<DataRow> dRows = service1.getDiskByName(diskgroupname,subSystemID);
			if(dRows!=null && dRows.size()>0){
				disId = dRows.get(0).getString("diskgroup_id");
				dataRow.set("diskgroup_id", disId);
			}
			if(ddmId!=null && disId!=null){
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_MAP_DISKGROUP2STORAGE_DDM", dataRow);
			}
		}
	} 
	
	//添加磁盘组和存储池关系表
	public void addDiskgroupAndPool(List<DataRow> dgandpools,Integer subSystemID){
		for (int i = 0; i <dgandpools.size(); ++i) {
			String poolId = null;
			String diskId = null;
			DataRow dgandpool = dgandpools.get(i);
			//获取Pool_ID
			String poolname=(String) dgandpool.get("pool_name");
			PoolService service = new PoolService();
			List<DataRow> pRows = service.getPoolInfoByName(poolname,subSystemID);
			if(pRows!=null && pRows.size()>0){
				poolId = pRows.get(0).getString("pool_id");
				dgandpool.set("pool_id", poolId);
			}
        	//获取DiskGroup_ID
        	String diskgroupname=(String) dgandpool.get("diskgroup_name");
			DiskgroupService service1=new DiskgroupService();
			List<DataRow> dRows = service1.getDiskByName(diskgroupname,subSystemID);
			if(dRows!=null && dRows.size()>0){
				diskId = dRows.get(0).getString("diskgroup_id");
				dgandpool.set("diskgroup_id", diskId);
        	}
			if(poolId!=null && diskId!=null){
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_MAP_STORAGEPOOL2DISKGROUP", dgandpool);
			}
		}		
	}
	public void updateDiskgroupAndPool(List<DataRow> dgandpools,Integer subSystemID){
		for (DataRow dataRow : dgandpools) {
			String sql = "delete from t_map_storagepool2diskgroup where diskgroup_name = '"+dataRow.getString("diskgroup_name")+"' and pool_name = '"+dataRow.getString("pool_name")+"'";
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
			String poolId = null;
			String diskId = null;
			//获取Pool_ID
			String poolname=(String) dataRow.get("pool_name");
			PoolService service = new PoolService();
			List<DataRow> pRows = service.getPoolInfoByName(poolname,subSystemID);
			if(pRows!=null && pRows.size()>0){
				poolId = pRows.get(0).getString("pool_id");
				dataRow.set("pool_id", poolId);
			}
        	//获取DiskGroup_ID
        	String diskgroupname=(String) dataRow.get("diskgroup_name");
			DiskgroupService service1=new DiskgroupService();
			List<DataRow> dRows = service1.getDiskByName(diskgroupname,subSystemID);
			if(dRows!=null && dRows.size()>0){
				diskId = dRows.get(0).getString("diskgroup_id");
				dataRow.set("diskgroup_id", diskId);
        	}
			if(poolId!=null && diskId!=null){
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_MAP_STORAGEPOOL2DISKGROUP", dataRow);
			}
		}
	}
	//添加卷和存储池关系表
	public void addVolumeAndPool(List<DataRow> volumeandpools,Integer subSystemID) {
		for (int i = 0; i < volumeandpools.size(); ++i) {
			String volumeId = null;
			String PoolId = null;
			DataRow volumeandpool = volumeandpools.get(i);
			// 获取Pool_ID
			String poolname = (String) volumeandpool.get("pool_name");
			PoolService service = new PoolService();
			List<DataRow> pRows = service.getPoolInfoByName(poolname,subSystemID);
			if (pRows != null && pRows.size() > 0) {
				PoolId = pRows.get(0).getString("pool_id");
				volumeandpool.set("pool_id", PoolId);
			}
			// 获取volume_id
			String volumename = (String) volumeandpool.get("volume_name");
			VolumeService service1 = new VolumeService();
			List<DataRow> vRows = service1.getVolumeByName(volumename,subSystemID);
			if (vRows != null && vRows.size() > 0) {
				volumeId = vRows.get(0).getString("volume_id");
				volumeandpool.set("volume_id", volumeId);
			}
			if (volumeId != null && PoolId != null) {
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_MAP_STORAGEPOOL2STORAGEVOLUME", volumeandpool);
			}
		}
	}
	public void updateVolumeAndPool(List<DataRow> volumeandpools,Integer subSystemID){
		for (DataRow dataRow : volumeandpools) {
			String sql="delete from t_map_storagepool2storagevolume where pool_name = '"+dataRow.getString("pool_name")+"' and volume_name = '"+dataRow.getString("volume_name")+"'";
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
			
			String volumeId = null;
			String PoolId = null;
			// 获取Pool_ID
			String poolname = dataRow.getString("pool_name");
			PoolService service = new PoolService();
			List<DataRow> pRows = service.getPoolInfoByName(poolname,subSystemID);
			if (pRows != null && pRows.size() > 0) {
				PoolId = pRows.get(0).getString("pool_id");
				dataRow.set("pool_id", PoolId);
			}
			// 获取volume_id
			String volumename = dataRow.getString("volume_name");
			VolumeService service1 = new VolumeService();
			List<DataRow> vRows = service1.getVolumeByName(volumename,subSystemID);
			if (vRows != null && vRows.size() > 0) {
				volumeId = vRows.get(0).getString("volume_id");
				dataRow.set("volume_id", volumeId);
			}
			if (volumeId != null && PoolId != null) {
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("T_MAP_STORAGEPOOL2STORAGEVOLUME", dataRow);
			}
		}
	}
	//添加Hostgroup 与 卷关系表
	public void addHostgroupAddVolume(List<DataRow> hostgroupAndVolume,Integer subsystemID){
		VolumeService volumeService = new VolumeService();
		HostgroupService hostService = new HostgroupService();
		for (DataRow dataRow : hostgroupAndVolume) {
			DataRow hostRow = hostService.getHostgroupByName(dataRow.getString("hostgroup_name"), subsystemID).get(0);
			String hostgroupId = hostRow.getString("hostgroup_id");
			DataRow volumeRow = volumeService.getVolumeByName(dataRow.getString("volume_name"), subsystemID).get(0);
			String volumeId = volumeRow.getString("volume_id");
			dataRow.set("hostgroup_id", hostgroupId);
			dataRow.set("volume_id", volumeId);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_map_hostgroupandvolume", dataRow);
		}
	}
	public void updateHostgroupAndVolume(List<DataRow> hostgroupAndVolume,Integer subsystemID){
		VolumeService volumeService = new VolumeService();
		HostgroupService hostService = new HostgroupService();
		for (DataRow dataRow : hostgroupAndVolume) {
			String sql="delete from t_map_hostgroupandvolume where hostgroup_name = '"+dataRow.getString("hostgroup_name")+"' and volume_name = '"+dataRow.getString("volume_name")+"'";
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
			
			DataRow hostRow = hostService.getHostgroupByName(dataRow.getString("hostgroup_name"), subsystemID).get(0);
			String hostgroupId = hostRow.getString("hostgroup_id");
			DataRow volumeRow = volumeService.getVolumeByName(dataRow.getString("volume_name"), subsystemID).get(0);
			String volumeId = volumeRow.getString("volume_id");
			dataRow.set("hostgroup_id", hostgroupId);
			dataRow.set("volume_id", volumeId);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_map_hostgroupandvolume", dataRow);
		}
	}
	
	//添加Hostgrup 与 HBA 关系表
	public void addHostgroupAndHBA(List<DataRow> hostgroupAndHBAInfo,Integer subsystemID){
		HostgroupService hostService = new HostgroupService();
		StoragehbaService hbaService = new StoragehbaService();
		for (DataRow dataRow : hostgroupAndHBAInfo) {
			DataRow hostRow = hostService.getHostgroupByName(dataRow.getString("hostgroup_name"), subsystemID).get(0);
			DataRow hbaRow = hbaService.getHBAbyUID(dataRow.getString("hba_uid"), subsystemID).get(0);
			dataRow.set("hba_id", hbaRow.getString("hba_id"));
			dataRow.set("server_name", hbaRow.getString("server_name"));
			dataRow.set("hostgroup_id", hostRow.getString("hostgroup_id"));
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_map_hostgroupandhba", dataRow);
		}
	}
	public void updateHostgroupAndHBA(List<DataRow> hostgroupAndHBAInfo,Integer subsystemID){
		HostgroupService hostService = new HostgroupService();
		StoragehbaService hbaService = new StoragehbaService();
		for (DataRow dataRow : hostgroupAndHBAInfo) {
			String sql = "delete from t_map_hostgroupandhba where hostgroup_name = '"+dataRow.getString("hostgroup_name")+"' and hba_uid = '"+dataRow.getString("hba_uid")+"' or hba_uid is null";
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
			
			DataRow hostRow = hostService.getHostgroupByName(dataRow.getString("hostgroup_name"), subsystemID).get(0);
			DataRow hbaRow = hbaService.getHBAbyUID(dataRow.getString("hba_uid"), subsystemID).get(0);
			dataRow.set("hba_id", hbaRow.getString("hba_id"));
			dataRow.set("server_name", hbaRow.getString("server_name"));
			dataRow.set("hostgroup_id", hostRow.getString("hostgroup_id"));
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_map_hostgroupandhba", dataRow);
		}
	}
	//添加 HBA 与 port关系表
	public void addPortAndHBAinfo(List<DataRow> portAndHBAinfo,Integer subsystemId){
		StoragehbaService hbaService = new StoragehbaService();
		PortService portService = new PortService();
		for (DataRow dataRow : portAndHBAinfo) {
			DataRow hbaRow = hbaService.getHBAbyUID(dataRow.getString("hba_uid"), subsystemId).get(0);
			DataRow portRow = portService.getPortByName(dataRow.getString("port_name"), subsystemId).get(0);
			dataRow.set("hba_id", hbaRow.getString("hba_id"));
			dataRow.set("port_id", portRow.getString("port_id"));
			dataRow.remove("uid");
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_map_storageportandhba", dataRow);
		}
	}
	
	public void updatePortAndHBA(List<DataRow> portAndHBAinfo,Integer subsystemId){
		StoragehbaService hbaService = new StoragehbaService();
		PortService portService = new PortService();
		for (DataRow dataRow : portAndHBAinfo) {
			String sql="delete from t_map_storageportandhba where port_name = '"+dataRow.getString("port_name")+"' and hba_devicename = '"+dataRow.getString("hba_devicename")+"'";
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
			
			DataRow hbaRow = hbaService.getHBAbyUID(dataRow.getString("hba_uid"), subsystemId).get(0);
			DataRow portRow = portService.getPortByName(dataRow.getString("port_name"), subsystemId).get(0);
			dataRow.set("hba_id", hbaRow.getString("hba_id"));
			dataRow.set("port_id", portRow.getString("port_id"));
			dataRow.remove("uid");
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_map_storageportandhba", dataRow);
		}
	}
	
}
