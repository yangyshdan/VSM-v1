package root.tasks;

import java.util.List;
import java.util.Map;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.agent.AgentService;
import com.project.hmc.engn.PhysicalFibric;

public class HvFibreChannel implements Task {

	public void execute() {
		Map<String, List<DataRow>> map = new PhysicalFibric().getResult();
		new AgentService().insertHwres(map);
	}

}
