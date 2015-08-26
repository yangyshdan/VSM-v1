package root.alert;

import root.snmp.MultiThreadedTrapReceiver;

import com.huiming.base.timerengine.Task;
import com.huiming.base.util.security.AES;

public class AlertTask implements Task{

	public void execute() {
		MultiThreadedTrapReceiver mul = new MultiThreadedTrapReceiver();
		mul.run();
	}

	public static void main(String[] args) {
		System.out.println(new AES().encrypt("sysadmin","UTF-8"));
		System.out.println(new AES().encrypt("password","UTF-8"));
	}
	
}
