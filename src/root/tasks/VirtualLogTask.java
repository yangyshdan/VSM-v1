package root.tasks;

import com.huiming.base.timerengine.Task;
import com.project.hmc.engn.AIXLog;

public class VirtualLogTask implements Task{

	public void execute() {
		AIXLog aixLog = new AIXLog();
		try {
			aixLog.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
