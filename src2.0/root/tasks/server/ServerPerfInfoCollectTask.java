package root.tasks.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.agent.AgentService;
import com.project.hmc.engn.LibvirtEngine;
import com.project.nmon.engn.UpNmon;
import com.project.x86monitor.DataCollectPerformance;
import com.project.x86monitor.DeviceInfo;
import com.project.x86monitor.MyConfig;
import com.project.x86monitor.MyTask;
import csharpwmi.ICSharpWMIClass;

/**
 * 采集服务器性能信息
 * @author Administrator
 *
 */
public class ServerPerfInfoCollectTask implements Task {

	private static Logger logger = Logger.getLogger(ServerPerfInfoCollectTask.class);
	private AgentService agentService = new AgentService();
	private UpNmon nmonEngine = new UpNmon();
	private LibvirtEngine libvirtEngine = new LibvirtEngine();
	private MyConfig config = new MyConfig();
	
	/**
	 * 执行任务
	 */
	public void execute() {
		try {
			//创建一个线程数量为10的线程池
			ExecutorService service = Executors.newFixedThreadPool(10);
			logger.info("<===================== BEGIN ServerPerfInfoCollectTask ======================>");
			//采集Linux服务器性能信息
			Runnable runnable = new Runnable() {
				public void run() {
					//采集物理机性能信息
					List<DataRow> physicalList = agentService.getPhysicalConfigList();
					nmonEngine.doCollectPerfInfo(physicalList, null);
					
					//采集虚拟机性能信息
					List<DataRow> virtualList = agentService.getVirtualConfigList();
					libvirtEngine.doCollectVirtMachinePerf(virtualList);
					nmonEngine.doCollectPerfInfo(virtualList, null);
				}
			};
			service.execute(runnable);
			//采集X86服务器性能信息
			Map<String, DeviceInfo> deviceInfos = config.getDeviceInfos();
			Map<String, ICSharpWMIClass> cswmis = config.getCswmis();
			if (deviceInfos != null && deviceInfos.size() > 0 && cswmis != null && cswmis.size() > 0) {
				for(String key : deviceInfos.keySet()){
					service.execute(new MyTask<DataCollectPerformance>(new DataCollectPerformance(
							cswmis.get(key), deviceInfos.get(key)), "execute"));
				}
			}
			logger.info("<===================== END ServerPerfInfoCollectTask ======================>");
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
