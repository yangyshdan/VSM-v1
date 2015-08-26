package root.tasks.windows;

import com.huiming.base.timerengine.Task;
import com.huiming.service.x86monitor.DataCollectService;
import com.project.web.WebConstants;

/**
 * @category 收集device id为ms.server.2008.r2的性能数据
 * @author hgc
 * @see 收集device id为ms.server.2008.r2的性能数据
 */
public class MSServerDataCollectPerformanceDailyTask implements Task {

	private DataCollectService<Object> obj = new DataCollectService<Object>(WebConstants.DB_DEFAULT);

	public void execute() {
		obj.generatePrfDaily();
	}
	
}
