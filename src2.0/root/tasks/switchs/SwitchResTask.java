package root.tasks.switchs;

import com.huiming.base.timerengine.Task;
import com.project.hmc.engn.SwitchRes;

public class SwitchResTask implements Task{

	public void execute() {
		SwitchRes res = new SwitchRes();
		res.getResult();
	}

}
