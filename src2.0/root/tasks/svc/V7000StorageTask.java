package root.tasks.svc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.task.BaseTask;
import com.huiming.base.util.StringHelper;
import com.huiming.service.ddm.DdmService;
import com.huiming.service.diskgroup.DiskgroupService;
import com.huiming.service.hostgroup.HostgroupService;
import com.huiming.service.initial.InitializeService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.service.relationmap.RelationMapService;
import com.huiming.service.storagesystem.StorageSystemService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.jeedsoft.license.License;
import com.jeedsoft.license.LicenseReader;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;
import com.project.web.SecurityAction;

public class V7000StorageTask extends BaseTask{
	private static final String CAPACITY_TB = "TB";
	private static final String CAPACITY_GB = "GB";
	private static final String CAPACITY_MB = "MB";
	private static final String CONST_IBM = "IBM";
	private static final String SEPARATOR = ",";
	private Logger logger= Logger.getLogger(this.getClass());
	InitializeService initial = new InitializeService();
	StorageSystemService subse = new StorageSystemService();
	VolumeService volse = new VolumeService();
	PoolService poolse = new PoolService();
	DiskgroupService disksse = new DiskgroupService();
	PortService portse = new PortService();
	DdmService ddmse = new DdmService();
	NodeService nodese = new NodeService();
	HostgroupService hostse = new HostgroupService();
	RelationMapService relationse = new RelationMapService();
	PrfTimestampService timese = new PrfTimestampService();
	Connection connection = null;

	public void execute() {
		StorageConfigService service = new StorageConfigService();
		List<Info> storageInfos = service.getStorageConfigList(SrContant.DEVTYPE_VAL_SVC);
		String fileName = SecurityAction.class.getClassLoader().getResource("").getPath().replaceAll("%20", " ")+"/license.lic";
		License license = LicenseReader.read(fileName);
		int i = 1;
		for (Info info : storageInfos) {
			//执行采集
			if (i <= license.getMaxDeviceCount()) {
				onExecuteCollectAndConfig(info);
			}
			i++;
		}
	}
	
	/**
	 * 用lssystem命令采集数据并插入数据库表:t_res_storagesubsystem
	 * @param info
	 * @return
	 */
	public List<DataRow> onExecAddStorageSubSystem(Info info) {
		List<DataRow> list = new ArrayList<DataRow>();
		//lssystem -delim ,
		List<String> sysList = executeCommand("lssystem -delim " + SEPARATOR);
		if (sysList.size() > 0) {
			List<DataRow> systemsRows = new ArrayList<DataRow>();
			DataRow sysRow = new DataRow();
			Integer subsystemId = null;
			Double totalUsed = null;
			Double totalfree = null;
			for (int i = 0; i < sysList.size(); i++) {
				String[] values = onSplit(sysList.get(i), 2, SEPARATOR);
				String key = values[0].trim();
				String value = values[1];
				if (key.equals("total_free_space")) {
					totalfree = getConvertCapacity(value);
					sysRow.set("available_capacity", totalfree);
					sysRow.set("unallocated_usable_capacity", totalfree);
				} else if (key.equals("name")) {
					sysRow.set("name", value);
					sysRow.set("display_name", value);
				} else if (key.equals("total_used_capacity")) {
					totalUsed = getConvertCapacity(value);
					sysRow.set("allocated_capacity", totalUsed);
				} else if (key.equals("code_level")) {
					sysRow.set("code_level", value);
				} else if (key.equals("statistics_status")) {
					sysRow.set("operational_status", value);
				} else if (key.equals("total_mdisk_capacity")) {
					sysRow.set("physical_disk_capacity", getConvertCapacity(value));
				} else if (key.equals("total_vdisk_capacity")) {
					sysRow.set("total_lun_capacity", getConvertCapacity(value));
				}
			}
			String ipAddress = StringHelper.isEmpty(info.getIpAddress()) ? null : info.getIpAddress();
			if (!StringHelper.isEmpty(ipAddress)) {
				if (!StringHelper.isEmpty(info.getIp1Address())) {
					ipAddress.concat(",").concat(info.getIp1Address());
				}
			}
			sysRow.set("ip_address", ipAddress);
			sysRow.set("model", SrContant.DEVTYPE_VAL_SVC);
			sysRow.set("update_timestamp", SrContant.getTimestamp());
			sysRow.set("vendor_name", CONST_IBM);
			sysRow.set("storage_type", SrContant.DEVTYPE_VAL_SVC);
			//计算"TOTAL_USABLE_CAPACITY"的值("total_used_capacity+total_free_space")
			if (totalfree != null || totalUsed != null) {
				Double free = totalfree == null ? 0 : totalfree;
				Double used = totalUsed == null ? 0 : totalUsed;
				sysRow.set("total_usable_capacity", (free + used));
			}
			//设置"NUM_DISK"(根据lsdrive命令统计)
			List<String> diskList = executeCommand("lsdrive -delim " + SEPARATOR);
			int numDisk = diskList == null ? 0 : (diskList.size() - 1);
			sysRow.set("num_disk", numDisk);
			//设置"NUM_LUN"(根据lsvdisk命令统计)
			List<String> lunList = executeCommand("lsvdisk -delim " + SEPARATOR);
			int numLun = lunList == null ? 0 : (lunList.size() - 1);
			sysRow.set("num_lun", numLun);
			systemsRows.add(sysRow);
			//插入数据
			String insertId = subse.insertOrUpdateStorage(systemsRows, 0);
			subsystemId = StringHelper.isEmpty(insertId) ? 0 : Integer.parseInt(insertId); 
			sysRow.set("subsystem_id", subsystemId);
			list.add(sysRow);
		}
		return list;
	}
	
	/**
	 * 用lsnodecanister命令采集数据并插入数据库表:t_res_storagenode
	 * @param subsystemId
	 * @return
	 */
	public List<DataRow> onExecAddStorageNode(Integer subsystemId) {
		List<DataRow> list = new ArrayList<DataRow>();
		//lsnodecanister(lsnode) --> t_res_storagenode
		List<String> nodeList = executeCommand("lsnodecanister -delim " + SEPARATOR);
		if (nodeList.size() > 1) {
			List<DataRow> nodeRows = new ArrayList<DataRow>();
			for (int i = 0; i < nodeList.size(); i++) {
				String[] title = nodeList.get(0).split(SEPARATOR);
				String[] values = onSplit(nodeList.get(i), title.length, SEPARATOR);
				if (!values[0].equals("id")) {
					DataRow nodeRow = new DataRow();
					nodeRow.set("subsystem_id", subsystemId);
					//name
					nodeRow.set("sp_name", values[1]);
					nodeRow.set("update_timestamp", SrContant.getTimestamp());
					nodeRows.add(nodeRow);
					
					list.add(nodeRow);
				}
			}
			nodese.updateStoragenodes(nodeRows, subsystemId);
		}
		return list;
	}
	
	/**
	 * 用lsmdisk命令采集数据并插入数据库表:t_res_diskgroup
	 * @param subsystemId
	 * @return
	 */
	public List<DataRow> onExecAddDiskgroup(Integer subsystemId) {
		List<DataRow> list = new ArrayList<DataRow>();
		//lsmdisk --> t_res_diskgroup
		List<String> mdiskList = executeCommand("lsmdisk -delim " + SEPARATOR);
		if (mdiskList.size() > 1) {
			List<DataRow> mdiskRows = new ArrayList<DataRow>();
			for (int i = 0; i < mdiskList.size(); i++) {
				String[] title = mdiskList.get(0).split(SEPARATOR);
				String[] values = onSplit(mdiskList.get(i), title.length, SEPARATOR);
				if (!values[0].equals("id")) {
					DataRow mdiskRow = new DataRow();
					DataRow mdiskRow2 = new DataRow();
					//name
					mdiskRow.set("name", values[1]);
					//name
					mdiskRow.set("display_name", values[1]);
					mdiskRow.set("subsystem_id", subsystemId);
					//capacity
					mdiskRow.set("ddm_cap", getConvertCapacity(values[6]));
					mdiskRow.set("update_timestamp", SrContant.getTimestamp());
					//status
					mdiskRow.set("operational_status", values[2]);
					//mdisk_grp_name
					mdiskRow.set("mdisk_grp_name", values[5]);
					mdiskRows.add(mdiskRow);
					
					//for mdiskRow2
					mdiskRow2.set("name", values[1]);
					mdiskRow2.set("display_name", values[1]);
					mdiskRow2.set("subsystem_id", subsystemId);
					mdiskRow2.set("ddm_cap", getConvertCapacity(values[6]));
					mdiskRow2.set("update_timestamp", SrContant.getTimestamp());
					mdiskRow2.set("operational_status", values[2]);
					mdiskRow2.set("mdisk_grp_name", values[5]);
					list.add(mdiskRow2);
				}
			}
			disksse.updateDiskgroup(mdiskRows, subsystemId);
		}
		return list;
	}
	
	/**
	 * 用lsfabric命令采集数据并插入数据库表:t_res_port
	 * @param subsystemId
	 * @param subsystemName
	 * @return
	 */
	public List<DataRow> onExecAddPort(Integer subsystemId, String subsystemName) {
		List<DataRow> list = new ArrayList<DataRow>();
		//lsfabric --> t_res_port
		List<String> portList = executeCommand("lsfabric -delim " + SEPARATOR);
		List<String> nodeList = executeCommand("lsnodecanister -delim " + SEPARATOR);
		List<String> finalPortList = new ArrayList<String>();
		if (portList.size() > 1) {
			//过滤数据(名称<remote_wwpn>相同的过滤掉,取第一条)
			for (int i = 0; i < portList.size(); i++) {
				String portStr = portList.get(i);
				String[] values1 = portStr.split(SEPARATOR);
				if (finalPortList.size() > 0) {
					for (int j = 0; j < finalPortList.size(); j++) {
						String[] values2 = finalPortList.get(j).split(SEPARATOR);
						if (values1[0].equals(values2[0])) {
							break;
						} else {
							if (j == (finalPortList.size() - 1)) {
								finalPortList.add(portStr);
							}
						}
					}
				} else {
					finalPortList.add(portStr);
				}
			}
			//过滤数据,当remote_wwpn和lsnodecanister中的WWNN相同时移除该数据
			for (int i = 0; i < finalPortList.size(); i++) {
				String[] values1 = finalPortList.get(i).split(SEPARATOR);
				for (int j = 0; j < nodeList.size(); j++) {
					String[] values2 = nodeList.get(j).split(SEPARATOR);
					if (values1[0].equals(values2[3])) {
						finalPortList.remove(i);
					}
				}
			}
			List<DataRow> portRows = new ArrayList<DataRow>();
			for (int i = 0; i < finalPortList.size(); i++) {
				String[] title = finalPortList.get(0).split(SEPARATOR);
				String[] values = onSplit(finalPortList.get(i), title.length, SEPARATOR);
				if (!values[0].equals("remote_wwpn")) {
					DataRow portRow = new DataRow();
					//remote_wwpn
					portRow.set("name", values[0]);
					//node_name
					portRow.set("node_name", values[3]);
					//state
					portRow.set("link_status", values[7]);
					portRow.set("subsystem_id", subsystemId);
					portRow.set("subsystem_name", subsystemName);
					portRow.set("update_timestamp", SrContant.getTimestamp());
					portRows.add(portRow);
					
					list.add(portRow);
				}
			}
			portse.updatePortInfo(portRows, subsystemId);
		}
		return list;
	}
	
	/**
	 * 用lsdrive命令采集数据并插入数据库表:t_res_storage_ddm
	 * @param subsystemId
	 * @param subsystemName
	 * @return
	 */
	public List<DataRow> onExecAddStorageDdm(Integer subsystemId, String subsystemName) {
		List<DataRow> list = new ArrayList<DataRow>();
		//lsdrive --> t_res_storage_ddm
		List<String> ddmList = executeCommand("lsdrive -delim " + SEPARATOR);
		if (ddmList.size() > 1) {
			List<DataRow> ddmRows = new ArrayList<DataRow>();
			for (int i = 0; i < ddmList.size(); i++) {
				String[] title = ddmList.get(0).split(SEPARATOR);
				String[] values = onSplit(ddmList.get(i), title.length, SEPARATOR);
				if (!values[0].equals("id")) {
					DataRow ddmRow = new DataRow();
					DataRow ddmRow2 = new DataRow();
					//'disk' + id
					ddmRow.set("name", new String("disk" + values[0]));
					ddmRow.set("subsystem_id", subsystemId);
					ddmRow.set("subsystem_name", subsystemName);
					//capacity
					ddmRow.set("ddm_cap", getConvertCapacity(values[5]));
					ddmRow.set("update_timestamp", SrContant.getTimestamp());
					//status
					ddmRow.set("operational_status", values[1]);
					//tech_type
					ddmRow.set("ddm_type", values[4]);
					ddmRows.add(ddmRow);
					
					//for ddmRow2
					ddmRow2.set("name", new String("disk" + values[0]));
					ddmRow2.set("subsystem_id", subsystemId);
					ddmRow2.set("subsystem_name", subsystemName);
					//capacity
					ddmRow2.set("ddm_cap", getConvertCapacity(values[5]));
					ddmRow2.set("update_timestamp", SrContant.getTimestamp());
					//status
					ddmRow2.set("operational_status", values[1]);
					//tech_type
					ddmRow2.set("ddm_type", values[4]);
					//mdisk_id
					ddmRow2.set("mdisk_id", values[6]);
					//mdisk_name
					ddmRow2.set("mdisk_name", values[7]);
					list.add(ddmRow2);
				}
			}
			ddmse.updateDDMInfo(ddmRows, subsystemId);
		}
		return list;
	}
	
	/**
	 * 用lsvdisk命令采集数据并插入数据库表:t_res_storagevolume
	 * @param subsystemId
	 * @param subsystemName
	 * @return
	 */
	public List<DataRow> onExecAddStorageVolume(Integer subsystemId, String subsystemName) {
		List<DataRow> list = new ArrayList<DataRow>();	
		//lsvdisk --> t_res_storagevolume
		List<String> volumeList = executeCommand("lsvdisk -delim " + SEPARATOR);
		if (volumeList.size() > 1) {
			List<DataRow> volumeRows = new ArrayList<DataRow>();
			for (int i = 0; i < volumeList.size(); i++) {
				String[] title = volumeList.get(0).split(SEPARATOR);
				String[] values = onSplit(volumeList.get(i), title.length, SEPARATOR);
				if (!values[0].equals("id")) {
					DataRow volumeRow = new DataRow();
					DataRow volumeRow2 = new DataRow();
					volumeRow.set("subsystem_id", subsystemId);
					//capacity
					volumeRow.set("logical_capacity", getConvertCapacity(values[7]));
					//name
					volumeRow.set("name", values[1]);
					volumeRow.set("update_timestamp", SrContant.getTimestamp());
					//status
					volumeRow.set("operational_status", values[4]);
					volumeRows.add(volumeRow);
					
					//for volumeRow2
					volumeRow2.set("subsystem_id", subsystemId);
					volumeRow2.set("logical_capacity", getConvertCapacity(values[7]));
					volumeRow2.set("name", values[1]);
					volumeRow2.set("update_timestamp", SrContant.getTimestamp());
					volumeRow2.set("operational_status", values[4]);
					//id
					volumeRow2.set("volume_id", values[0]);
					//mdisk_grp_id
					volumeRow2.set("mdisk_id", values[5]);
					//mdisk_grp_name
					volumeRow2.set("mdisk_grp_name", values[6]);
					list.add(volumeRow2);
				}
			}
			volse.updateVolumeInfo(volumeRows, subsystemId);
		}
		return list;
	}
	
	/**
	 * 用lshost命令采集数据并插入数据库表:t_res_hostgroup
	 * @param subsystemId
	 * @return
	 */
	public List<DataRow> onExecAddHostgroup(Integer subsystemId) {
		List<DataRow> list = new ArrayList<DataRow>();
		//lshost --> t_res_hostgroup
		List<String> hostList = executeCommand("lshost -delim " + SEPARATOR);
		if (hostList.size() > 1) {
			List<DataRow> hostRows = new ArrayList<DataRow>();
			for (int i = 0; i < hostList.size(); i++) {
				String[] title = hostList.get(0).split(SEPARATOR);
				String[] values = onSplit(hostList.get(i), title.length, SEPARATOR);
				if (!values[0].equals("id")) {
					DataRow hostRow = new DataRow();
					//name
					hostRow.set("hostgroup_name", values[1]);
					hostRow.set("subsystem_id", subsystemId);
					hostRow.set("update_timestamp", SrContant.getTimestamp());
					hostRows.add(hostRow);
					list.add(hostRow);
				}
			}
			hostse.updateHostgroup(hostRows, subsystemId);
		}
		return list;
	}
	
	/**
	 * 用lsmdiskgrp命令采集数据并插入数据库表:t_res_storagepool
	 * @param subsystemId
	 * @return
	 */
	public List<DataRow> onExecAddStoragePool(Integer subsystemId) {
		List<DataRow> list = new ArrayList<DataRow>();
		//lsmdiskgrp --> t_res_storagepool
		List<String> poolList = executeCommand("lsmdiskgrp -delim " + SEPARATOR);
		if (poolList.size() > 1) {
			List<DataRow> poolRows = new ArrayList<DataRow>();
			for (int i = 0; i < poolList.size(); i++) {
				String[] title = poolList.get(0).split(SEPARATOR);
				String[] values = onSplit(poolList.get(i), title.length, SEPARATOR);
				if (!values[0].equals("id")) {
					DataRow poolRow = new DataRow();
					poolRow.set("subsystem_id", subsystemId);
					//用name值作参数,统计数值
					poolRow.set("num_backend_disk", getNumBackendDisk(values[1]));
					//vdisk_count
					poolRow.set("num_lun", values[4]);
					//name
					poolRow.set("name", values[1]);
					//capacity
					poolRow.set("total_usable_capacity", getConvertCapacity(values[5]));
					poolRow.set("update_timestamp", SrContant.getTimestamp());
					//status
					poolRow.set("operational_status", values[2]);
					//free_capacity
					poolRow.set("unallocated_capacity", getConvertCapacity(values[7]));
					poolRows.add(poolRow);
					
					list.add(poolRow);
				}
			}
			poolse.updatePoolInfo(poolRows, subsystemId);
		}
		return list;
	}
	
	/**
	 * 插入数据到关联表:t_map_diskgroup2storage_ddm
	 * @param subsystemId
	 * @param diskgroupList
	 * @param ddmList
	 */
	public void onExecAddDiskgroupAndDdm(Integer subsystemId, List<DataRow> diskgroupList, List<DataRow> ddmList) {
		if (diskgroupList.size() > 0 && ddmList.size() > 0) {
			List<DataRow> diskgroupAndDdmList = new ArrayList<DataRow>();
			for (int i = 0; i < diskgroupList.size(); i++) {
				DataRow diskgroup = diskgroupList.get(i);
				for (int j = 0; j < ddmList.size(); j++) {
					DataRow ddm = ddmList.get(j);
					if (diskgroup.getString("name").equals(ddm.getString("mdisk_name"))) {
						DataRow diskgroupAndDdm = new DataRow();
						diskgroupAndDdm.set("diskgroup_name", diskgroup.getString("name"));
						diskgroupAndDdm.set("ddm_name", ddm.getString("name"));
						diskgroupAndDdmList.add(diskgroupAndDdm);
					}
				}
			}
			relationse.updateDiskgroupAndDDM(diskgroupAndDdmList, subsystemId);
		}
	}
	
	/**
	 * 插入数据到关联表:t_map_hostgroupandvolume
	 * @param subsystemId
	 * @param volumeList
	 */
	public void onExecAddHostgroupAndVolume(Integer subsystemId, List<DataRow> volumeList) {
		if (volumeList.size() > 0) {
			for (int i = 0; i < volumeList.size(); i++) {
				DataRow volume = volumeList.get(i);
				List<String> hostMapList = executeCommand("lsvdiskhostmap -delim " + SEPARATOR + " " + volume.getInt("volume_id"));
				if (hostMapList.size() > 1) {
					List<DataRow> hostMapRows = new ArrayList<DataRow>();
					for (int j = 0; j < hostMapList.size(); j++) {
						String[] title = hostMapList.get(0).split(SEPARATOR);
						String[] values = onSplit(hostMapList.get(j), title.length, SEPARATOR);
						if (!values[0].equals("id")) {
							DataRow hostMapRow = new DataRow();
							//name
							hostMapRow.set("volume_name", values[1]);
							//host_name
							hostMapRow.set("hostgroup_name", values[4]);
							hostMapRows.add(hostMapRow);
						}
					}
					relationse.updateHostgroupAndVolume(hostMapRows, subsystemId);
				}
			}
		}
	}
	
	/**
	 * 插入数据到关联表:t_map_storagepool2diskgroup
	 * @param subsystemId
	 * @param diskgroupList
	 * @param poolList
	 */
	public void onExecAddDiskgroupAndPool(Integer subsystemId, List<DataRow> diskgroupList, List<DataRow> poolList) {
		if (diskgroupList.size() > 0 && poolList.size() > 0) {
			List<DataRow> diskgroupAndPoolList = new ArrayList<DataRow>();
			for (int i = 0; i < diskgroupList.size(); i++) {
				DataRow diskgroup = diskgroupList.get(i);
				for (int j = 0; j < poolList.size(); j++) {
					DataRow pool = poolList.get(j);
					if (diskgroup.getString("mdisk_grp_name").equals(pool.getString("name"))) {
						DataRow diskgroupAndPool = new DataRow();
						diskgroupAndPool.set("diskgroup_name", diskgroup.getString("name"));
						diskgroupAndPool.set("pool_name", pool.getString("name"));
						diskgroupAndPoolList.add(diskgroupAndPool);
					}
				}
			}
			relationse.updateDiskgroupAndPool(diskgroupAndPoolList, subsystemId);
		}
	}
	
	/**
	 * 插入数据到关联表:t_map_storagepool2storagevolume
	 * @param subsystemId
	 * @param volumeList
	 * @param poolList
	 */
	public void onExecAddVolumeAndPool(Integer subsystemId, List<DataRow> volumeList, List<DataRow> poolList) {
		if (volumeList.size() > 0 && poolList.size() > 0) {
			List<DataRow> poolAndVolumeList = new ArrayList<DataRow>();
			for (int i = 0; i < volumeList.size(); i++) {
				DataRow volume = volumeList.get(i);
				for (int j = 0; j < poolList.size(); j++) {
					DataRow pool = poolList.get(j);
					if (volume.getString("mdisk_grp_name").equals(pool.getString("name"))) {
						DataRow poolAndVolume = new DataRow();
						poolAndVolume.set("volume_name", volume.getString("name"));
						poolAndVolume.set("pool_name", pool.getString("name"));
						poolAndVolumeList.add(poolAndVolume);
					}
				}
			}
			relationse.updateVolumeAndPool(poolAndVolumeList, subsystemId);
		}
	}
	
	/**
	 * 用于执行V7000的CLI命令采集相关信息,并将所采集的信息插入相应的数据库表
	 * @param info
	 */
	public void onExecuteCollectAndConfig(Info info) {
		try {
			//建立连接
			initConnection(info);
			//插入数据库表
			List<DataRow> subsystemList = onExecAddStorageSubSystem(info);
			Integer subsystemId = 0;
			String subsystemName = null;
			if (subsystemList.size() > 0) {
				subsystemId = subsystemList.get(0).getInt("subsystem_id");
				subsystemName = subsystemList.get(0).getString("name");
			} else {
				//存储系统信息插入失败时，终止操作
				if (subsystemId.intValue() == 0) {
					logger.error("存储系统信息插入失败,终止后续操作!");
					return;
				}
			}
			
			List<DataRow> nodeList = onExecAddStorageNode(subsystemId);
			List<DataRow> portList = onExecAddPort(subsystemId, subsystemName);
			List<DataRow> ddmList = onExecAddStorageDdm(subsystemId, subsystemName);
			List<DataRow> volumeList = onExecAddStorageVolume(subsystemId, subsystemName);
			List<DataRow> poolList = onExecAddStoragePool(subsystemId);
			List<DataRow> diskgroupList = onExecAddDiskgroup(subsystemId);
			List<DataRow> hostgroupList = onExecAddHostgroup(subsystemId);
			
			//插入关联表
			onExecAddDiskgroupAndDdm(subsystemId, diskgroupList, ddmList);
			onExecAddHostgroupAndVolume(subsystemId, volumeList);
			onExecAddDiskgroupAndPool(subsystemId, diskgroupList, poolList);
			onExecAddVolumeAndPool(subsystemId, volumeList, poolList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
	}
	
	/**
	 * 用指定的字符分割字符串,并返回字符串数值,类似String的split()方法
	 * @param value
	 * @param length
	 * @param regex
	 * @return
	 */
	public String[] onSplit(String value, int length, String regex) {
		String[] values = new String[length];
		//拼接一字符到字符串后,方便后续操作
		value = value.concat(regex);
		for (int i = 0; i < values.length; i++) {
			String str = value.substring(0, value.indexOf(regex));
			value = value.replaceFirst(str + regex, "");
			values[i] = str;
		}
		return values;
	}
	
	/**
	 * 首先通过lsmdisk命令查找mdisk_grp_name对应的所有mdisk_name,
	 * 然后用lsdrive命令查找所有与上一步中得到的mdisk_name对应的数据,统计记录数得到总数
	 * @param diskgroupName
	 * @return
	 */
	public int getNumBackendDisk(String diskgroupName) {
		int result = 0;
		try {
			//execute lsmdisk command
			List<String> mdiskList = executeCommand("lsmdisk -delim " + SEPARATOR);
			if (mdiskList.size() > 0) {
				List<String> mdiskNameList = new ArrayList<String>();
				for (int i = 0; i < mdiskList.size(); i++) {
					String[] diskTitle = mdiskList.get(0).split(SEPARATOR);
					String[] diskValues = onSplit(mdiskList.get(i), diskTitle.length, SEPARATOR);
					//查找mdisk_grp_name值与参数:diskgroupName相同的记录
					if (diskValues[5].equals(diskgroupName)) {
						//获取name列的数值
						mdiskNameList.add(diskValues[1]);
					}
				}
				//execute lsdrive command
				List<String> driveList = executeCommand("lsdrive -delim " + SEPARATOR);
				if (driveList.size() > 0) {
					for (int i = 0; i < driveList.size(); i++) {
						String[] driveTitle = driveList.get(0).split(SEPARATOR);
						String[] driveValues = onSplit(driveList.get(i), driveTitle.length, SEPARATOR);
						for (int j = 0; j < mdiskNameList.size(); j++) {
							String mdiskName = mdiskNameList.get(j).trim();
							//查找mdisk_name值与上一步查到的mdisk_name值比较
							if (driveValues[7].equals(mdiskName)) {
								result = result + 1;
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 用于將TB\GB單位的数值转换成MB单位的数值
	 * @param value
	 * @return
	 */
	public Double getConvertCapacity(String value) {
		Double result = null;
		try {
			if (value != null) {
				//如果是"TB",计算：1TB = 1024GB = 1024 * 1024MB
				if (value.indexOf(CAPACITY_TB) > 1) {
					double tb = Double.parseDouble(value.substring(0,value.indexOf(CAPACITY_TB)));
					result = tb*1024*1024;
				//如果是"GB",计算：1GB = 1024MB
				} else if (value.indexOf(CAPACITY_GB) > 1) {
					double gb = Double.parseDouble(value.substring(0,value.indexOf(CAPACITY_GB)));
					result = gb*1024;
				//如果是"MB"
				} else if (value.indexOf(CAPACITY_MB) > 1) {
					double mb = Double.parseDouble(value.substring(0,value.indexOf(CAPACITY_MB)));
					result = mb;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	
	/**
	 * 用于执行CLI命令,并返回结果
	 * @param command
	 * @return
	 */
	public List<String> executeCommand(String cmd) {
		List<String> list = new ArrayList<String>();
		Session session = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			session = getConnSession();
			if (session != null) {
				session.execCommand(cmd);
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				is = new StreamGobbler(session.getStdout());
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					list.add(line);
				}
			} else {
				logger.error("session is null !");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (session != null) {
				session.close();
			}
		}
		return list;
	}
	
	/**
	 * 建立连接
	 * @param info
	 * @return
	 */
	public void initConnection(Info info) {
		try {
			connection = new Connection(info.getIpAddress());
			connection.connect();
			boolean isAuth = connection.authenticateWithPassword(info.getUsername(), info.getPassword());
			logger.info("isAuth is : " + isAuth);
			if (isAuth) {
				logger.info("Login success !");
			} else {
				logger.info("Login failed !");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	/**
	 * 获取Session
	 * @return
	 */
	public Session getConnSession() {
		Session session = null;
		try {
			if (connection != null) {
				session = connection.openSession();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (session != null) {
				session.close();
			}
		}
		return session;
	}
}
