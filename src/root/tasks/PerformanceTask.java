package root.tasks;

import com.huiming.base.timerengine.Task;
import com.jeedsoft.common.basic.util.UuidUtil;
import com.project.hmc.engn.ComputerSystem;
import com.project.hmc.engn.PhysicalCPU;
import com.project.hmc.engn.PhysicalMEM;
import com.project.hmc.engn.VirtualMacPref;
import com.project.hmc.engn.VirtualMacsd;
import com.project.nmon.engn.UpNmon;

public class PerformanceTask implements Task{
	public void test() {
	}

	public void execute() {

		try {
			if(i==0){  //第一次执行，添加配置信息和上传nmon文件
				//添加物理机配置信息
				computerSystem.getResult();
				//添加虚拟机配置信息
				virtualMacsd.getResult();
				//上传nmon
				upNmon.doUpToVritual(null);
			}else{
				String PrfMarker = UuidUtil.randomUuid().toString();
				//HMC采集物理机性能
				physicalCPU.getResult(PrfMarker);
				physicalMem.getResult(PrfMarker);
				//hmc 采集虚拟机性能
				virtualMacPref.getResult(PrfMarker);
				//nmon方式采集虚拟机性能
//				upNmon.doUpToVritual(PrfMarker);
			}
			
			i++;
			if(i>9999){
				i=1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {

		PerformanceTask tc = new PerformanceTask();
		
//		System.out.println("a_"+DateHelper.formatDate(new Date(), "yyMMdd_HHmm")+".nmon");
	}

	private PhysicalCPU physicalCPU = new PhysicalCPU();
	private PhysicalMEM physicalMem = new PhysicalMEM();
	private VirtualMacPref virtualMacPref = new VirtualMacPref();
	private ComputerSystem computerSystem = new ComputerSystem();
//	private VirtualMac virtualMac = new VirtualMac();
	private VirtualMacsd virtualMacsd = new VirtualMacsd();
	private UpNmon upNmon = new UpNmon(); 
	private int i = 0;
//	private HmcInstructions hi =new HmcInstructions();
	

}
