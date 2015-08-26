package root.tasks.emc;

import com.huiming.base.timerengine.Task;
import com.huiming.sr.constants.SrContant;

/**
 * 定时更新配置信息
 * @author Administrator
 *
 */
public class UpdateConfigTask implements Task{
	public void execute() {
		SrContant.isUpdateConfig = 1;
	}
}
