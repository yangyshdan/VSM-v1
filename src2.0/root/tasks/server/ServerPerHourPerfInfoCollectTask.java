package root.tasks.server;

import org.apache.log4j.Logger;
import com.huiming.base.timerengine.Task;
import com.huiming.service.x86monitor.DataCollectService;
import com.project.web.WebConstants;

/**
 * 采集服务器性能信息(小时)
 * @author Administrator
 *
 */
public class ServerPerHourPerfInfoCollectTask implements Task {

	private Logger logger = Logger.getLogger(ServerPerHourPerfInfoCollectTask.class);
//	private AgentService agentService = new AgentService();
//	private PrfTimestampService timestampService = new PrfTimestampService();
	private DataCollectService<Object> obj = new DataCollectService<Object>(WebConstants.DB_DEFAULT);
	
	/**
	 * 执行采集
	 */
	public void execute() {
		try {
			logger.info("<===================== BEGIN ServerPerHourPerfInfoCollectTask ======================>");
			//采集Linux服务器性能信息
			/**
			 * 这段代码重复了，建议屏蔽掉
			 */
//			List<DataRow> physicalList = agentService.getPhysicalConfigList();
//			onCollectPerHourPerfInfo(physicalList);
//			List<DataRow> virtualList = agentService.getVirtualConfigList();
//			onCollectPerHourPerfInfo(virtualList);
			
			//采集X86服务器性能信息   已修复既可以统计Linux和Window
			obj.generatePrfHourly();
			logger.info("<===================== END ServerPerHourPerfInfoCollectTask ======================>");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 采集性能数据(小时)
	 * @param computerList
	 */
//	public void onCollectPerHourPerfInfo(List<DataRow> computerList) {
//		try {
//			if (computerList.size() > 0) {
//				for (int i = 0; i < computerList.size(); i++) {
//					DataRow computer = computerList.get(i);
//					Integer computerId = computer.getInt("ref_computer_id");
//					String computerName = computer.getString("computer_name");
//					String deviceType = computer.getString("device_type");
//					Date date = new Date();
//					String endTime = SrContant.getTimeFormat(date);
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(date);
//					calendar.add(Calendar.HOUR, -1);
//					String startTime = SrContant.getTimeFormat(calendar.getTime());
//
//					//添加时间表信息
//					DataRow timeRow = new DataRow();
//					timeRow.set(SrTblColConstant.TT_SAMPLE_TIME, endTime);
//					timeRow.set(SrTblColConstant.TT_INTERVAL_LEN, 3600);
//					timeRow.set(SrTblColConstant.TT_SUMM_TYPE, SrContant.SUMM_TYPE_HOUR);
//					timeRow.set(SrTblColConstant.TT_SUBSYSTEM_ID, computerId);
//					timeRow.set(SrTblColConstant.TT_SUBSYSTEM_NAME, computerName);
//					timeRow.set(SrTblColConstant.TT_DEVICE_TYPE, deviceType);
//					DataRow resTimestamp = timestampService.addPerHourAndDayPrfTimestamp(timeRow);
//					Long timeId = resTimestamp.getLong(SrTblColConstant.TT_TIME_ID);
//					
//					// 获取时间列表信息
//					List<DataRow> timestampList = timestampService.getTimestampInfos(startTime,endTime,computerId,deviceType);
//					List<Long> timeIdList = new ArrayList<Long>();
//					if (timestampList.size() > 0) {
//						for (int j = 0; j < timestampList.size(); j++) {
//							timeIdList.add(timestampList.get(j).getLong("time_id"));
//						}
//					}
//					
//					//查找和处理符合时间段数据,并插入到数据库性能表
//					if (timeId > 0 && timeIdList.size() > 0) {
//						String timeIds = timeIdList.toString().replace("[", "").replace("]", "");
//						List<DataRow> findComPerfList = agentService.getPerHourAndDayComPerfInfos(timeIds);
//						for (int j = 0; j < findComPerfList.size(); j++) {
//							findComPerfList.get(j).set(SrTblColConstant.TT_TIME_ID,timeId);
//						}
//						agentService.addPerHourAndDayComPerfInfos(findComPerfList);
//						logger.info("It's success to collect per hour server performance info !");
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//	}

}
