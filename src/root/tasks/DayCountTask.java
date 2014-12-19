package root.tasks;

import com.huiming.base.timerengine.Task;
import com.project.prf.sec.HypervisorCountSec;
import com.project.prf.sec.VirtualCountSec;


public class DayCountTask implements Task{

	public void execute() {

		vds.execDaily();
		hsc.execDaily();
		
	}
	
	private VirtualCountSec vds = new VirtualCountSec();
	private HypervisorCountSec hsc =new HypervisorCountSec();

}
