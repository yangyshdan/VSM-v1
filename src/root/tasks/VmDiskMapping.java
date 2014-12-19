package root.tasks;

import com.huiming.base.timerengine.Task;
import com.project.hmc.engn.VIOSMapping;

public class VmDiskMapping implements Task{

	public void execute() {
		new VIOSMapping().getResult();
	}

}
