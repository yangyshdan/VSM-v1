package root.tasks;

import java.util.List;


import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.alert.DeviceAlertService;

public class TpcLogSyncTask implements Task{

	public void execute() {
		DeviceAlertService service = new DeviceAlertService();
		int id = service.getMaxId();
		List<DataRow> list =  service.getTpcLog(id);
		if(list != null && list.size() > 0 )
			service.insertLog(list);
	}
}
