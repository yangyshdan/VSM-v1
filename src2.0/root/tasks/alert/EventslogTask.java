package root.tasks.alert;

import com.huiming.base.timerengine.Task;
import com.project.eventslog.DSEventslog;
import com.project.eventslog.NASEventslog;
import com.project.eventslog.SVCEventslog;
import com.project.eventslog.SwitchEventslog;

public class EventslogTask implements Task{
	private DSEventslog ds = new DSEventslog();
	private SVCEventslog svc = new SVCEventslog();
	private SwitchEventslog st = new SwitchEventslog();
	private NASEventslog nas = new NASEventslog();
	
	public void execute() {
		//DS事件日志
		ds.getResult();
		//SVC事件日志
		svc.getResult();
		//交换机事件日志
		st.getResult();
		//nas日志
		nas.getResult();
	}

}
