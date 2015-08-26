package root.tasks.windows;

import com.huiming.base.timerengine.Task;
import com.huiming.service.x86monitor.DataCollectService;
import com.project.web.WebConstants;

/**
 * @category 收集device id为ms.server.2008.r2的性能数据
 * @author hgc
 * @see 收集device id为ms.server.2008.r2的性能数据
 */
public class MSServerDataCollectPerformanceHourlyTask implements Task {

	private DataCollectService<Object> obj = 
		new DataCollectService<Object>(WebConstants.DB_DEFAULT);
	
	public void execute() {
		obj.generatePrfHourly();
	}

	public static void main(String args[]){
//		String sql = "SELECT t1.COMPUTER_ID AS COMPUTER_ID,t1.computer_name,AVG(t1.CPU_IDLE_PRCT) AS CPU_IDLE_PRCT,AVG(t1.CPU_BUSY_PRCT) AS CPU_BUSY_PRCT," +
//		"AVG(MEM_FREE_PRCT) AS MEM_FREE_PRCT,AVG(MEM_USED_PRCT) AS MEM_USED_PRCT,SUM(t1.DISK_READDATARATE_KB) AS DISK_READDATARATE_KB," +
//		"SUM(t1.DISK_WRITEDATARATE_KB) AS DISK_WRITEDATARATE_KB,SUM(t1.DISK_OVERALL_IOPS) AS DISK_OVERALL_IOPS,SUM(t1.DISK_READ_AWAIT) AS DISK_READ_AWAIT," +
//		"SUM(t1.DISK_WRITE_AWAIT) AS DISK_WRITE_AWAIT,SUM(t1.NET_SEND_KB) AS NET_SEND_KB,SUM(t1.NET_RECV_KB) AS NET_RECV_KB," +
//		"SUM(t1.NET_SEND_PACKET) AS NET_SEND_PACKET,SUM(t1.NET_RECV_PACKET) AS NET_RECV_PACKET " +
//		"FROM (SELECT t1.COMPUTER_ID,t1.computer_name,t1.CPU_IDLE_PRCT,t1.CPU_BUSY_PRCT,t1.MEM_FREE_PRCT,t1.MEM_USED_PRCT,t1.DISK_READDATARATE_KB,t1.DISK_WRITEDATARATE_KB," +
//		"t1.DISK_OVERALL_IOPS,t1.DISK_READ_AWAIT,t1.DISK_WRITE_AWAIT,t1.NET_SEND_KB,t1.NET_RECV_KB,t1.NET_SEND_PACKET,t1.NET_RECV_PACKET " +
//		"FROM t_prf_computerper t1 JOIN t_prf_timestamp t2 ON t1.time_id=t2.time_id AND t2.SUMM_TYPE=1 AND t2.device_type='X86' " +
//		"AND (t2.sample_time BETWEEN DATE_SUB(SYSDATE(), INTERVAL 1 DAY) AND SYSDATE())) t1 GROUP BY COMPUTER_ID";
		System.out.println(1^1);
		Long l = 100L;
		System.out.println(l.toString());
	}
}
