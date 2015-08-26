package com.huiming.service.agent;

import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;


public class SecService extends BaseService{

	/**
	 * 取虚拟机的小时和天的数据 
	 * @param startTime
	 * @param endTime
	 * @param summType
	 * @return
	 */
	public List<DataRow> getVirtualList(String startTime,String endTime,String summType){
		
		StringBuffer sql = new StringBuffer("SELECT t.subsystem_id,t.subsystem_name,t.perf_marker,"
			+" AVG(c.cpu_idle_prct) cpu_idle,AVG(c.cpu_busy_prct) cpu_busy,AVG(c.mem_used_prct) mem_used,"
			+" AVG(c.DISK_READDATARATE_KB) disk_read_rate,AVG(c.DISK_WRITEDATARATE_KB) disk_write_rate,"
			+" AVG(c.DISK_READ_IOPS) disk_read_iops,AVG(c.DISK_WRITE_IOPS) disk_write_iops,"
			+" AVG(c.NET_SEND_KB) net_send,AVG(c.NET_RECV_KB) net_recv,"
			+" AVG(c.NET_SEND_PACKET) net_send_packet,AVG(c.NET_RECV_PACKET) net_recv_packet,"
			+" SUM(c.`DISK_READ_IOPS`*c.`DISK_READ_AWAIT`)/SUM(c.disk_overall_iops) read_await,"
			+" SUM(c.`DISK_WRITE_IOPS`*c.`DISK_WRITE_AWAIT`)/SUM(c.disk_overall_iops) write_await"
			+" FROM t_prf_timestamp t,t_res_virtualmachine vm,t_prf_computerper c,t_res_computersystem cs"
			+" WHERE t.sample_time >= ? AND t.sample_time <= ? AND cs.detectable = 1 AND t.summ_type= ?" 
			+" AND t.time_id = c.time_id AND cs.computer_id=c.computer_id AND vm.computer_id= cs.computer_id"
			+" GROUP BY vm.vm_id");
		
		String type="";
		if(summType.equals("2")){
			type="1";
		}else if(summType.equals("3")){
			type="2";
		}
		
		Object[] args = new Object[]{startTime,endTime,type};
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args);
		
	}
	
	
	/**
	 * 更新物理机的性能的数据
	 * @param startTime
	 * @param endTime
	 * @param summType
	 * @return
	 */
	public List<DataRow> getUpdateHypervisorPrf(String marker){
		
		StringBuffer sql = new StringBuffer("SELECT tpc.time_id,ttrh.name,trhcount.* FROM t_res_hypervisor ttrh,t_prf_timestamp  tpc,("
				+" SELECT vm.hypervisor_id,t.perf_marker,AVG(c.cpu_idle_prct) cpu_idle,"
				+" AVG(c.cpu_busy_prct) cpu_busy,AVG(c.mem_used_prct) mem_used,"
				+" AVG(c.DISK_READDATARATE_KB) disk_read_rate,AVG(c.DISK_WRITEDATARATE_KB) disk_write_rate,"
				+" AVG(c.DISK_READ_IOPS) disk_read_iops,AVG(c.DISK_WRITE_IOPS) disk_write_iops,"
				+" AVG(c.NET_SEND_KB) net_send,AVG(c.NET_RECV_KB) net_recv,"
				+" AVG(c.NET_SEND_PACKET) net_send_packet,AVG(c.NET_RECV_PACKET) net_recv_packet,"
				+" SUM(c.`DISK_READ_IOPS`*c.`DISK_READ_AWAIT`)/SUM(c.disk_overall_iops) read_await,"
				+" SUM(c.`DISK_WRITE_IOPS`*c.`DISK_WRITE_AWAIT`)/SUM(c.disk_overall_iops) write_await"
				+" FROM t_prf_timestamp t,t_res_virtualmachine vm,t_prf_computerper c,t_res_computersystem cs"
				+" WHERE cs.detectable = 1 AND t.summ_type=1 AND cs.type='vios' and t.perf_marker= ?"
				+" AND t.time_id = c.time_id AND cs.computer_id=c.computer_id AND vm.computer_id= cs.computer_id"
				+" GROUP BY vm.vm_id) AS trhcount"
				+" WHERE ttrh.host_computer_id=tpc.subsystem_id  AND ttrh.hypervisor_id= trhcount.hypervisor_id" 
				+" AND tpc.perf_marker=trhcount.perf_marker GROUP BY ttrh.hypervisor_id");
		
		Object[] args =new Object[]{marker};
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args);
		
	}
	
	/**
	 * 取物理机的小时,天的性能的数据
	 * @param startTime
	 * @param endTime
	 * @param summType
	 * @return
	 */
	public List<DataRow> getHypervisorList(String startTime,String endTime,String summType){
		
		StringBuffer sql = new StringBuffer("SELECT tpt.subsystem_id,tpt.subsystem_name,tpt.perf_marker,AVG(tpc.cpu_idle_prct) cpu_idle,"
				+" AVG(tpc.cpu_busy_prct) cpu_busy,AVG(tpc.mem_used_prct) mem_used,"
				+" AVG(tpc.DISK_READDATARATE_KB) disk_read_rate,AVG(tpc.DISK_WRITEDATARATE_KB) disk_write_rate,"
				+" AVG(tpc.DISK_READ_IOPS) disk_read_iops,AVG(tpc.DISK_WRITE_IOPS) disk_write_iops,"
				+" AVG(tpc.NET_SEND_KB) net_send,AVG(tpc.NET_RECV_KB) net_recv,"
				+" AVG(tpc.NET_SEND_PACKET) net_send_packet,AVG(tpc.NET_RECV_PACKET) net_recv_packet,"
				+" SUM(tpc.`DISK_READ_IOPS`*tpc.`DISK_READ_AWAIT`)/SUM(tpc.disk_overall_iops) read_await,"
				+" SUM(tpc.`DISK_WRITE_IOPS`*tpc.`DISK_WRITE_AWAIT`)/SUM(tpc.disk_overall_iops) write_await"
				+" FROM t_prf_computerper tpc,t_prf_timestamp tpt,t_res_hypervisor trh"
				+" WHERE tpt.sample_time >= ? AND tpt.sample_time <= ? AND trh.`DETECTABLE`=1 AND tpt.summ_type=?" 
				+" AND tpt.time_id=tpc.time_id AND tpc.computer_id = trh.`HOST_COMPUTER_ID` GROUP BY trh.`HYPERVISOR_ID`");

		String type="";
		if(summType.equals("2")){
			type="1";
		}else if(summType.equals("3")){
			type="2";
		}
		Object[] args = new Object[]{startTime,endTime,type};
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(),args);
		
	}
	
	
	
	public void batchInsertPrf(List<DataRow> list,java.sql.Timestamp dateTime,String summ_type){
		Session session = null;
		for (DataRow row : list) {
			DataRow timestamp = new DataRow();
			DataRow prf = new DataRow();
			timestamp.set("sample_time", dateTime);
			timestamp.set("interval_len", 1);
			timestamp.set("summ_type", summ_type);
			timestamp.set("subsystem_name", row.getString("subsystem_name"));
			timestamp.set("subsystem_id", row.getString("subsystem_id"));
			timestamp.set("perf_marker", row.getString("perf_marker"));
			
			prf.set("computer_id", row.getString("subsystem_id"));
			prf.set("computer_name", row.getString("subsystem_name"));
			
			if(StringHelper.isNotEmpty(row.getString("cpu_busy"))){
				prf.set("cpu_busy_prct", row.getString("cpu_busy"));
			}
			if(StringHelper.isNotEmpty(row.getString("cpu_idle"))){
				prf.set("cpu_idle_prct", row.getString("cpu_idle"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_used"))){
				prf.set("mem_used_prct", row.getString("mem_used"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_read_rate"))){
				prf.set("disk_readdatarate_kb", row.getString("disk_read_rate"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_write_rate"))){
				prf.set("disk_writedatarate_kb", row.getString("disk_write_rate"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_read_iops"))){
				prf.set("disk_read_iops", row.getString("disk_read_iops"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_write_iops"))){
				prf.set("disk_write_iops", row.getString("disk_write_iops"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_send"))){
				prf.set("net_recv_kb", row.getString("net_send"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_recv"))){
				prf.set("net_send_kb", row.getString("net_recv"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_recv_packet"))){
				prf.set("net_recv_packet", row.getString("net_recv_packet"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_send_packet"))){
				prf.set("net_send_packet", row.getString("net_send_packet"));
			}
			if(StringHelper.isNotEmpty(row.getString("read_await"))){
				prf.set("disk_read_await", row.getString("read_await"));
			}
			if(StringHelper.isNotEmpty(row.getString("write_await"))){
				prf.set("disk_write_await", row.getString("write_await"));
			}
			
			try {
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
				String id = session.insert("t_prf_timestamp", timestamp);
				prf.set("time_id", id);
				session.insert("t_prf_computerper", prf);
				session.commitTrans();
			} catch (Exception e) {
				e.printStackTrace();
				if (session != null)
				{
					session.rollbackTrans();
				}
			}finally{
				if (session != null)
				{
					session.close();
					session = null;
				}
			}
		}
	}
	
	/**
	 * 根据perf_marker字段将虚拟机的数据更新到对应的物理机
	 * @param list
	 * @param dateTime
	 * @param summ_type
	 */
	public void batchUpdateHypervisorPrf(List<DataRow> list){
		Session session = null;
		for (DataRow row : list) {
			DataRow prf = new DataRow();
			
			prf.set("time_id", row.getString("time_id"));
			if(StringHelper.isNotEmpty(row.getString("cpu_busy"))){
				prf.set("cpu_busy_prct", row.getString("cpu_busy"));
			}
			if(StringHelper.isNotEmpty(row.getString("cpu_idle"))){
				prf.set("cpu_idle_prct", row.getString("cpu_idle"));
			}
			if(StringHelper.isNotEmpty(row.getString("mem_used"))){
				prf.set("mem_used_prct", row.getString("mem_used"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_read_rate"))){
				prf.set("disk_readdatarate_kb", row.getString("disk_read_rate"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_write_rate"))){
				prf.set("disk_writedatarate_kb", row.getString("disk_write_rate"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_read_iops"))){
				prf.set("disk_read_iops", row.getString("disk_read_iops"));
			}
			if(StringHelper.isNotEmpty(row.getString("disk_write_iops"))){
				prf.set("disk_write_iops", row.getString("disk_write_iops"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_send"))){
				prf.set("net_recv_kb", row.getString("net_send"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_recv"))){
				prf.set("net_send_kb", row.getString("net_recv"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_recv_packet"))){
				prf.set("net_recv_packet", row.getString("net_recv_packet"));
			}
			if(StringHelper.isNotEmpty(row.getString("net_send_packet"))){
				prf.set("net_send_packet", row.getString("net_send_packet"));
			}
			if(StringHelper.isNotEmpty(row.getString("read_await"))){
				prf.set("disk_read_await", row.getString("read_await"));
			}
			if(StringHelper.isNotEmpty(row.getString("write_await"))){
				prf.set("disk_write_await", row.getString("write_await"));
			}
			
			try {
				session = getSession(WebConstants.DB_DEFAULT);
				session.beginTrans();
//				session.insert("t_prf_computerper", prf);
				session.update("t_prf_computerper",prf,"time_id",prf.getInt("time_id"));
				session.commitTrans();
			} catch (Exception e) {
				e.printStackTrace();
				if (session != null)
				{
					session.rollbackTrans();
				}
			}finally{
				if (session != null)
				{
					session.close();
					session = null;
				}
			}
		}
	}
	
	public DataRow getVirtualUpDate(){
		
		String sql="SELECT MAX(time_id),MAX(SAMPLE_TIME) AS SAMPLE_TIME FROM t_prf_timestamp tpt,t_res_virtualmachine trv"
			+" WHERE tpt.`SUBSYSTEM_ID`=trv.`COMPUTER_ID`  AND tpt.summ_type=1";
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	public DataRow getHypervisorUpDate(){
		
		String sql="SELECT MAX(time_id),MAX(SAMPLE_TIME) AS SAMPLE_TIME FROM t_prf_timestamp tpt,t_res_hypervisor trh"
			+" WHERE tpt.`SUBSYSTEM_ID`=trh.`HOST_COMPUTER_ID`  AND tpt.summ_type=1";
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	
}
