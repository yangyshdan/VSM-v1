package root.tasks;

import com.huiming.base.timerengine.Task;
import com.project.hmc.engn.VirtualCapacity;

/**
 * 更新虚拟机ip和磁盘容量信息
 * @author LiuCh
 *
 */
public class VirtualCapAndIPTask  implements Task{
	private VirtualCapacity ca = new VirtualCapacity();

	public void execute() {
		ca.getResult();
	}
	public static void main(String[] args) {
		VirtualCapacity ca = new VirtualCapacity();
		ca.getResult();
	}
}
