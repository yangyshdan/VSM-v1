package root.snmp;


import com.huiming.base.timerengine.Task;

public class SnmpTrapListener implements Task{

	public void execute() {
		MultiThreadedTrapReceiver multithreadedtrapreceiver = new MultiThreadedTrapReceiver();
		multithreadedtrapreceiver.run();
	}
}
