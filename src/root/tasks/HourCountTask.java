package root.tasks;

import com.huiming.base.timerengine.Task;
import com.project.prf.sec.HypervisorCountSec;
import com.project.prf.sec.VirtualCountSec;


public class HourCountTask implements Task{

	public void execute() {

		vds.execHourly();
		hcs.execHourly();
	}
	
	private VirtualCountSec vds = new VirtualCountSec();
	private HypervisorCountSec hcs =new HypervisorCountSec();
}
