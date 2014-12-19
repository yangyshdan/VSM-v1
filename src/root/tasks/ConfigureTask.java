package root.tasks;

import com.huiming.base.timerengine.Task;
import com.huiming.service.agent.AgentService;
import com.project.hmc.engn.ComputerSystem;
import com.project.hmc.engn.VirtualMac;
import com.project.hmc.engn.VirtualMacsd;
import com.project.web.WebConstants;

public class ConfigureTask implements Task {

	public void execute() {

		//更新或添加物理机信息
		computerSystem.getResult();
		//更新或添加虚拟机信息
//		virtualMac.getResult();
		virtualMacsd.getResult();
		//删除过时性能数据
//		agent.deleteOldPerfData(WebConstants.PERF_INTERVAL_LEN_DAY);
	}

	private ComputerSystem computerSystem = new ComputerSystem();
	private VirtualMac virtualMac = new VirtualMac();
	private AgentService agent = new AgentService();
	private VirtualMacsd virtualMacsd = new VirtualMacsd();

}
