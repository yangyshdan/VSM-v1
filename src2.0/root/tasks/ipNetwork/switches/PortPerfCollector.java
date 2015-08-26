package root.tasks.ipNetwork.switches;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.deviceSnmp.DeviceSnmpService;
import com.project.ipnetwork.PortPrfExecutor;
import com.project.ipnetwork.SnmpUtil;
import com.project.ipnetwork.TCfgDeviceSnmp;
import com.project.x86monitor.MyTask;
import com.project.x86monitor.beans.IdBean;

/**
 * @category 搜集端口的性能数据的任务
 * @author 何高才
 *
 */
public class PortPerfCollector implements Task {
	private Logger logger = Logger.getLogger(PortPerfCollector.class);
	private DeviceSnmpService service = new DeviceSnmpService();
	private SnmpUtil util = new SnmpUtil();
	
	public void execute() {
		// 针对所有交换机
		List<TCfgDeviceSnmp> snmps = service.getDeviceSnmp(null, "Switch", null);
		// 把所有交换机的OID查出来
		if(snmps != null && snmps.size() > 0) {  // SNMP存在，那么devIds必定存在，entities不一定存在
			IdBean devIds = extractId(snmps);
			Map<Integer, List<DataRow>> entities = service.getEntityData(devIds.getIdStr(), devIds.getIdCount(), "t_nw_prf_port");
			ExecutorService serv = Executors.newFixedThreadPool(10);
			String method = "execute";
			for(TCfgDeviceSnmp snmp : snmps) {
				PortPrfExecutor exe = new PortPrfExecutor(entities.get(snmp.getDeviceId()), snmp, util, service);
				serv.execute(new MyTask<PortPrfExecutor>(exe, method));
			}
			try { // 关闭线程池
				serv.shutdown();
			} catch (Exception e) {
				logger.error("关闭线程池出错", e);
			}
		}
		
	}
	
	private IdBean extractId(List<TCfgDeviceSnmp> configs) {
		if(configs != null && configs.size() > 0) {
			StringBuilder sb = new StringBuilder(configs.size() * 4);
			Integer t;
			IdBean idBean = new IdBean();
			Set<Integer> set = new HashSet<Integer>();
			for(int i = 0, l = configs.size(); i < l; ++i) {
				t = configs.get(i).getDeviceId();
				if(t != null && t.intValue() > 0 && (!set.contains(t))) {
					set.add(t);
					sb.append(t.intValue());
					sb.append(',');
				}
			}
			if(sb.length() - 1 >= 0 && sb.charAt(sb.length() - 1) == ',') {
				sb.deleteCharAt(sb.length() - 1);
			}
			idBean.setIdStr(sb.toString());
			idBean.setIdCount(set.size());
			return idBean;
		}
		return null;
	}
}
