package root.tasks.switchs;

import com.huiming.base.timerengine.Task;
import com.project.eventslog.SwitchEventslog;

public class SwitchEventTask implements Task{
	private SwitchEventslog st = new SwitchEventslog();

	public void execute() {
		st.getResult();
	}
	
	
}
