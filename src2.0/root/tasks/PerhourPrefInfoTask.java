package root.tasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.diskgroup.DiskgroupService;
import com.huiming.service.node.NodeService;
import com.huiming.service.port.PortService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.service.storagesystem.StorageSystemService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.project.web.WebConstants;

/**
 * 采集每小时性能数据
 * @author Administrator
 * 
 */
public class PerhourPrefInfoTask implements Task {

	private Logger logger = Logger.getLogger(this.getClass());
	StorageSystemService systemService = new StorageSystemService();
	PrfTimestampService timestampService = new PrfTimestampService();
	DiskgroupService diskgroupService = new DiskgroupService();
	PortService portService = new PortService();
	VolumeService volumeService = new VolumeService();
	NodeService nodeService = new NodeService();

	/**
	 * 执行target方法
	 */
	public void execute() {
		perhourPrefInfo();
	}

	/**
	 * 处理小时性能数据
	 */
	public void perhourPrefInfo() {
		// 得到存储列表
		List<DataRow> sys = systemService.getStorageInfoById(null);
		for (DataRow dataRow : sys) {
			Integer systemId = dataRow.getInt(SrTblColConstant.RSS_SUBSYSTEM_ID);
			String systemName = dataRow.getString(SrTblColConstant.RSS_SUBSYSTEM_NAME);
			String storageType = dataRow.getString(SrTblColConstant.RSS_STORAGE_TYPE);
			Date date = new Date();
			String endTime = SrContant.getTimeFormat(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.HOUR, -1);
			String startTime = SrContant.getTimeFormat(calendar.getTime());

			// 添加时间表信息
			DataRow timeRow = new DataRow();
			timeRow.set(SrTblColConstant.TT_SAMPLE_TIME, endTime);
			timeRow.set(SrTblColConstant.TT_INTERVAL_LEN, 3600);
			timeRow.set(SrTblColConstant.TT_SUMM_TYPE, SrContant.SUMM_TYPE_HOUR);
			timeRow.set(SrTblColConstant.TT_SUBSYSTEM_ID, systemId);
			timeRow.set(SrTblColConstant.TT_SUBSYSTEM_NAME, systemName);
			timeRow.set(SrTblColConstant.TT_DEVICE_TYPE, storageType);
			DataRow resTimestamp = timestampService.addPerHourAndDayPrfTimestamp(timeRow);
			Long timeId = resTimestamp.getLong(SrTblColConstant.TT_TIME_ID);
			
			// 获取时间列表信息
			List<DataRow> timestampList = timestampService.getTimestampInfos(startTime,endTime,systemId,storageType);
			List<Long> timeIdList = new ArrayList<Long>();
			if (timestampList.size() > 0) {
				for (int j = 0; j < timestampList.size(); j++) {
					timeIdList.add(timestampList.get(j).getLong("time_id"));
				}
			}
			
			// 计算性能指标,处理后插入数据
			if (timeId > 0 && timeIdList.size() > 0) {
				String timeIds = timeIdList.toString().replace("[", "").replace("]", "");

				// For Disk Group
				List<DataRow> diskGroupList = diskgroupService.getPrfDiskGroups(timeIds);
				for (int j = 0; j < diskGroupList.size(); j++) {
					diskGroupList.get(j).set(SrTblColConstant.TT_TIME_ID,timeId);
				}
				diskgroupService.addPerHourAndDayPrfDiskGroups(diskGroupList);

				// For Port
				List<DataRow> portList = portService.getPrfPorts(timeIds);
				for (int j = 0; j < portList.size(); j++) {
					portList.get(j).set(SrTblColConstant.TT_TIME_ID, timeId);
				}
				portService.addPerHourAndDayPrfPorts(portList);

				// For Storage Volume
				List<DataRow> volumeList = volumeService.getPrfVolumes(timeIds);
				for (int j = 0; j < volumeList.size(); j++) {
					volumeList.get(j).set(SrTblColConstant.TT_TIME_ID,timeId);
				}
				volumeService.addPerHourAndDayPrfVolumes(volumeList);
				
				//For SVC
				if (storageType.equals(SrContant.DEVTYPE_VAL_SVC)) {
					//For Storage Node
					List<DataRow> nodeList = nodeService.getPrfNodes(timeIds);
					for (int j = 0; j < nodeList.size(); j++) {
						nodeList.get(j).set(SrTblColConstant.TT_TIME_ID, timeId);
					}
					nodeService.addPerHourAndDayPrfNodes(nodeList);
				//For NETAPP
				} else if (storageType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
					//For Storage System
					List<DataRow> systemList = systemService.getPrfSystems(timeIds);
					for (int j = 0; j < systemList.size(); j++) {
						systemList.get(j).set(SrTblColConstant.TT_TIME_ID, timeId);
					}
					systemService.addPerHourAndDayPrfSystems(systemList);
				}
				logger.info("It's success to add per hour performance info !");
			}
		}
	}
}
