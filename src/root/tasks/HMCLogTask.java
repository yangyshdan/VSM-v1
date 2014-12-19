package root.tasks;

import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.agent.AgentService;
import com.huiming.service.alert.DeviceAlertService;
import com.project.hmc.engn.HMCLog;

public class HMCLogTask implements Task{

	public void execute() {
		List<DataRow> hmc = new AgentService().getHMCLoginInfo();
		if(hmc!=null && hmc.size()>0){
			DeviceAlertService service = new DeviceAlertService();
			for (DataRow dRow : hmc) {
				List<DataRow> logs = new HMCLog().getResult(dRow);
				if(logs != null && logs.size() > 0 ){
					service.insertLog(logs);
				} 
			}
		}
		
		
	}

}
