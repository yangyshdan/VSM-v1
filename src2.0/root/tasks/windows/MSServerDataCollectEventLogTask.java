package root.tasks.windows;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.huiming.base.timerengine.Task;
import com.huiming.service.x86server.X86ServerService;
import com.project.x86monitor.BMCResourceCollector;
import com.project.x86monitor.IPMIInfo;
import com.project.x86monitor.MyTask;

/**
 * @see 凡是在<type>x86.config</type>那么认为这是针对于搜集配置性能的任务
 */
public class MSServerDataCollectEventLogTask implements Task {
	private Logger logger = Logger.getLogger(MSServerDataCollectEventLogTask.class);

	public void execute() {
		X86ServerService service = new X86ServerService();
		Map<String, IPMIInfo> ipmis = service.getBMCs();
		ExecutorService executorService = Executors.newFixedThreadPool(10); // 获得线程池的服务
		for(String key : ipmis.keySet()){
			// 搜集事件
//			executorService.execute(new MyTask<BMCEventCollector>(new BMCEventCollector(ipmis.get(key)), "execute"));
			
			// 搜集状态
//			executorService.execute(new MyTask<BMCStatusCollector>(new BMCStatusCollector(ipmis.get(key)), "execute"));
			
			// 搜集配置
			executorService.execute(new MyTask<BMCResourceCollector>(new BMCResourceCollector(ipmis.get(key)), "execute"));
			
		}
		try { // 关闭线程池
			executorService.shutdown();
		} catch (Exception e) {
			logger.error("关闭线程池出错", e);
		}
//		Map<String, DeviceInfo> deviceInfos = config.getDeviceInfos();
//		Map<String, ICSharpWMIClass> cswmis = config.getCswmis();
//		// 第一次或者当前任务与信息的数量不等
//		if(deviceInfos != null && deviceInfos.size() > 0 && cswmis != null && cswmis.size() > 0){
//			logger.info(">>>>>>>>>>>>>>>>>>MSServerDataCollectEventLogTask>>>>>>>>>>>>>>>");
//			/**
//			 * 一个一个地执行任务，效率太低了
//			 * 在这里建立一个线程池，让JVM开辟至多10条线程去处理任务
//			 */
//			ExecutorService executorService = Executors.newFixedThreadPool(10); // 获得线程池的服务
//			for(String key : deviceInfos.keySet()){
//				executorService.execute(new MyTask<DataCollectEventlog>(
//						new DataCollectEventlog(cswmis.get(key), deviceInfos.get(key)), "execute"));
//			}
//			logger.info(">>>>>>>>>>>>>>>>>>MSServerDataCollectEventLogTask>>>>>>>>>>>>>>>");
//			try { // 关闭线程池
//				executorService.shutdown();
//			} catch (Exception e) {
//				logger.error("关闭线程池出错", e);
//			}
//			
//		}
	}
	
}
