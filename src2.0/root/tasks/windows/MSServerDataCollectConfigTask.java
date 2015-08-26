package root.tasks.windows;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.huiming.base.timerengine.Task;
import com.project.x86monitor.DataCollectConfig;
import com.project.x86monitor.DeviceInfo;
import com.project.x86monitor.MyConfig;
import com.project.x86monitor.MyTask;

import csharpwmi.ICSharpWMIClass;

/**
 * @see 凡是在<type>x86.config</type>那么认为这是针对于搜集配置性能的任务
 */
public class MSServerDataCollectConfigTask implements Task {
	private Logger logger = Logger.getLogger(MSServerDataCollectConfigTask.class);
	
	public void execute() {
		MyConfig config = new MyConfig();
		Map<String, DeviceInfo> deviceInfos = config.getDeviceInfos();
		Map<String, ICSharpWMIClass> cswmis = config.getCswmis();
		// 第一次或者当前任务与信息的数量不等
		if(deviceInfos != null && deviceInfos.size() > 0 && cswmis != null && cswmis.size() > 0){
			/**
			 * 一个一个地执行任务，效率太低了
			 * 在这里建立一个线程池，让JVM开辟至多10条线程去处理任务
			 */
			ExecutorService executorService = Executors.newFixedThreadPool(10); // 获得线程池的服务
			logger.info(">>>>>>>>>>>>>>>>>>MSServerDataCollectConfigTask>>>>>>>>>>>>>>>");
			for(String key : deviceInfos.keySet()){
				executorService.execute(
						new MyTask<DataCollectConfig>(
							new DataCollectConfig(cswmis.get(key), deviceInfos.get(key)),
							"execute"  // 方法名
						)
					);
			}
			logger.info(">>>>>>>>>>>>>>>>>>MSServerDataCollectConfigTask>>>>>>>>>>>>>>>");
			
			try { // 关闭线程池
				executorService.shutdown();
			} catch (Exception e) {
				logger.error("关闭线程池出错", e);
			}
		}
		
	}
	
}
