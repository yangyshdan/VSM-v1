package root.tasks.windows;

import com.huiming.base.timerengine.Task;
import com.huiming.service.x86monitor.DataCollectService;
import com.project.web.WebConstants;

/**
 * @category 收集device id为ms.server.2008.r2的性能数据
 * @author hgc
 * @see 收集device id为ms.server.2008.r2的性能数据
 */
public class MSServerDataDeleteTask implements Task {

	DataCollectService<Object> service = new DataCollectService<Object>(WebConstants.DB_DEFAULT);
	
	public void execute() {
		// 因为设定删除级联，所以自动删除t_res_hypervisor
		service.deleteOldLog("t_res_computersystem", "update_timestamp", 60, "day");
		// 因为设定删除级联，所以自动删除t_prf_computerper
		service.deleteOldLog("t_prf_timestamp", "SAMPLE_TIME", 60, "day");
		
		//service.deleteOldLog("t_prf_timestamp", "FLastTime", 60, "day");
		//service.deleteOldLog("t_prf_computerper", "update_timestamp", 60, "day");
	}

}
