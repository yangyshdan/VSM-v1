package root.tasks.windows;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.huiming.base.timerengine.Task;
import com.project.x86monitor.DataCollectPerformance;
import com.project.x86monitor.DeviceInfo;
import com.project.x86monitor.MyConfig;
import com.project.x86monitor.MyTask;

import csharpwmi.ICSharpWMIClass;

/**
 * @category 收集device id为ms.server.2008.r2的性能数据
 * @author hgc
 * @see 收集device id为ms.server.2008.r2的性能数据
 */
public class MSServerDataCollectPerformanceTask implements Task {
	private Logger logger = Logger.getLogger(MSServerDataCollectPerformanceTask.class);
	
	public void execute() {
		MyConfig config = new MyConfig();
		Map<String, DeviceInfo> deviceInfos = config.getDeviceInfos();
		Map<String, ICSharpWMIClass> cswmis = config.getCswmis();
		if(deviceInfos != null && cswmis != null){
			/**
			 * 一个一个地执行任务，效率太低了
			 * 在这里建立一个线程池，让JVM开辟至多10条线程去处理任务
			 */
			ExecutorService executorService = Executors.newFixedThreadPool(10); // 获得线程池的服务
			for(String key : deviceInfos.keySet()){
				executorService.execute(new MyTask<DataCollectPerformance>(
						new DataCollectPerformance(cswmis.get(key), deviceInfos.get(key)), "execute"));
			}
			try { // 关闭线程池
				executorService.shutdown();
			} catch (Exception e) {
				logger.error("关闭线程池出错", e);
			}
		}
		
	}

}
