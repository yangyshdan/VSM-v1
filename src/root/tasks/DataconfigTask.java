package root.tasks;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.agent.AgentService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.dataconfig.DataconfigService;

/**
 * 删除过时性能数据和事件日志数据
 * @author LiuCh
 *
 */
public class DataconfigTask implements Task{
	private DeviceAlertService dser = new DeviceAlertService();
	private AgentService aser = new AgentService();
	private DataconfigService service = new DataconfigService();

	public void execute() {
		DataRow erow = service.getDataConfigInfo("eventlog");
		DataRow prow = service.getDataConfigInfo("perfromance");
		if(erow!=null && erow.size()>0){
			//删除过时日志信息
			dser.deleteOldLog(erow.getInt("time_length"));
		}
		if(prow!=null && prow.size()>0){
			//删除过时性能数据
			aser.deleteOldPerfData(prow.getInt("time_length"));
		}
	}
}
