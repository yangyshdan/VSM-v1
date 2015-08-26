package root.tasks.server;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import com.huiming.base.timerengine.Task;
import com.project.hmc.engn.ComputerSystem;
import com.project.hmc.engn.VirtualMac;
import com.project.x86monitor.DataCollectConfig;
import com.project.x86monitor.DeviceInfo;
import com.project.x86monitor.MyConfig;
import com.project.x86monitor.MyTask;
import csharpwmi.ICSharpWMIClass;

/**
 * 采集服务器配置信息
 * @author Administrator
 *
 */
public class ServerConfigInfoCollectTask implements Task {
	private Logger logger = Logger.getLogger(ServerConfigInfoCollectTask.class);
	private ComputerSystem phsicalService = new ComputerSystem();
	private VirtualMac virtualService = new VirtualMac();
	private MyConfig config = new MyConfig();

	/**
	 * 执行任务
	 */
	public void execute() {
		try {
			//创建一个线程数量为10的线程池
			ExecutorService service = Executors.newFixedThreadPool(10);
			logger.info("<===================== BEGIN ServerConfigInfoCollectTask ======================>");
			Runnable runnable = new Runnable() {
				public void run() {
					//采集LINUX服务器的配置信息
					phsicalService.getPhysicalAndVirtualConfigInfo(null);
					virtualService.getVirtualConfigInfo(null,null);
				}
			};
			service.execute(runnable);
			//采集X86服务器的配置信息
			Map<String, DeviceInfo> deviceInfos = config.getDeviceInfos();
			Map<String, ICSharpWMIClass> cswmis = config.getCswmis();
			//第一次或者当前任务与信息的数量不等
			if(deviceInfos != null && deviceInfos.size() > 0 && cswmis != null && cswmis.size() > 0) {
				for(String key : deviceInfos.keySet()){
					service.execute(new MyTask<DataCollectConfig>(
							new DataCollectConfig(cswmis.get(key), 
							deviceInfos.get(key)), "execute"));
				}
			}
			logger.info("<===================== END ServerConfigInfoCollectTask ======================>");
			try {
				//关闭线程池
				service.shutdown();
			} catch (Exception e) {
				logger.error("关闭线程池出错!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
