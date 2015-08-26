package root.tasks.alert;

import root.snmp.MultiThreadedTrapReceiver;

import com.huiming.base.timerengine.Task;

public class AlertTask implements Task{

	public void execute() {
		MultiThreadedTrapReceiver mul = new MultiThreadedTrapReceiver();
		mul.run();
	}

}
