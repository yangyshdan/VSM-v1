package root.tasks.server;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.x86monitor.DataCollectService;
import com.huiming.service.x86server.X86ServerService;
import com.project.web.WebConstants;
import com.project.x86monitor.IPMIInfo;
import com.project.x86monitor.IPMIUtil;
import com.project.x86monitor.MySession;
import com.project.x86monitor.MyTask;
import com.project.x86monitor.beans.SysEventLogBean;


public class ServerHardwareCollectTask implements Task {
	private Logger logger = Logger.getLogger(ServerHardwareCollectTask.class);

	public void execute() {
		Map<String, IPMIInfo> ipmis = new X86ServerService().getBMCs();;
		if(ipmis != null && ipmis.size() > 0){
			ExecutorService executorService = Executors.newFixedThreadPool(10); // 获得线程池的服务
			for(String key : ipmis.keySet()){
				// 搜集事件
//				executorService.execute(new MyTask<BMCEventCollector>(new BMCEventCollector(ipmis.get(key)), "execute"));
//				
//				// 搜集状态
//				executorService.execute(new MyTask<BMCStatusCollector>(new BMCStatusCollector(ipmis.get(key)), "execute"));
//				
//				// 搜集配置
//				executorService.execute(new MyTask<BMCResourceCollector>(new BMCResourceCollector(ipmis.get(key)), "execute"));
				
				executorService.execute(new MyTask<Executor>(new Executor(ipmis.get(key)), "execute"));
			}
			try { // 关闭线程池
				executorService.shutdown();
			} catch (Exception e) {
				logger.error("关闭线程池出错", e);
			}
		}
	}
	
	public class Executor
	{
		IPMIInfo ipmi;
		public Executor(IPMIInfo ipmi){
			this.ipmi = ipmi;
		}
		public void execute() {
			IPMIUtil util = new IPMIUtil(this.getClass());
			DataCollectService<DataRow> collector = new DataCollectService<DataRow>(WebConstants.DB_DEFAULT);
			SysEventLogBean bean = new SysEventLogBean();
			bean.setIpmi(ipmi);
			
			// 搜集事件
			String BMC_FROM_NOW_ON_KEY = "BMC_" + ipmi.getIpAddress();
			
			//  加锁
			Date date = MySession.getFromNowOn_(BMC_FROM_NOW_ON_KEY);
			// 执行完毕，解锁
			
			bean.setFromNowOn(date.getTime());
			util.loadSystemEventLog(bean);
			date.setTime(bean.getFromNowOn());  // date是引用对象，这样便可以修改
			List<DataRow> sels = bean.getSysEventLogs();
			collector.insertDeviceLogs(sels);
			
			// 搜集状态
			bean.setStatusIds(collector.getAllStatusId(ipmi.getHypervisorId()));
			util.loadSensorStatus(bean);
			String tableName = "t_status_sensors";
			collector.update(bean.getUpdateData(), tableName, "bmc_index");
			collector.insert(bean.getInsertData(), tableName);
			
			// 搜集配置
			boolean isUpadate = collector.isBMCResourceExists(ipmi.getHypervisorId());
			util.loadServerConfig(bean);
			List<DataRow> drs = bean.getInsertData();
			String table = "t_res_bmc";
			if(isUpadate){ // 一个hypervisor_id代表一个配置
				collector.update(drs, table, "hypervisor_id");
			}
			else {
				collector.insert(drs, table);
			}
		}
	}
}
