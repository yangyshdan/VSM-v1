package com.project.nmon.engn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import com.huiming.base.jdbc.DataRow;
import com.huiming.service.agent.AgentService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class NmonAgent {
	
	private static Logger logger = Logger.getLogger(NmonAgent.class);
	private AgentService agent = new AgentService();
	
	/**
	 * 解析nmon文件,获取性能数据
	 * @param marker
	 * @param row
	 * @param filePath
	 */
	public void getResult(String marker, DataRow row, String filePath) {
		//nmon文件
		File nmonFile = new File(filePath);
		BufferedReader br = null;
		List<DataRow> list = new ArrayList<DataRow>();
		String readToLine = null;
		try {
			logger.info("--开始解析文件--");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(nmonFile)));
			//记录索引
			List<Integer> cpuUserIndexs = new ArrayList<Integer>();
			List<Integer> cpuSysIndexs = new ArrayList<Integer>();
			List<Integer> cpuWaitIndexs = new ArrayList<Integer>();
			List<Integer> cpuIdleIndexs = new ArrayList<Integer>();
			List<Integer> cpuBusyIndexs = new ArrayList<Integer>();
			List<Integer> memTotalIndexs = new ArrayList<Integer>();
			List<Integer> memFreeIndexs = new ArrayList<Integer>();
			List<Integer> memCachedIndexs = new ArrayList<Integer>();
			List<Integer> netReadIndexs = new ArrayList<Integer>();
			List<Integer> netWriteIndexs = new ArrayList<Integer>();
			List<Integer> netPacketReads = new ArrayList<Integer>();
			List<Integer> netPacketWrites = new ArrayList<Integer>();
			DataRow dataRow = new DataRow();
			while ((readToLine = br.readLine()) != null) {
				String[] argsLine = readToLine.trim().split(",");
				//CPU信息
				if (argsLine[0].equals("CPU_ALL")) {
					//第二次读到标题,设置值
					if (cpuUserIndexs.size() > 0 && cpuSysIndexs.size() > 0 && cpuWaitIndexs.size() > 0 
							&& cpuIdleIndexs.size() > 0 && cpuBusyIndexs.size() > 0) {
						dataRow.set("cpu_usr_prct", getTureData(argsLine, cpuUserIndexs));
						dataRow.set("cpu_sys_prct", getTureData(argsLine, cpuSysIndexs));
						dataRow.set("cpu_wait_prct", getTureData(argsLine, cpuWaitIndexs));
						dataRow.set("cpu_idle_prct", getTureData(argsLine, cpuIdleIndexs));
						dataRow.set("cpu_busy_prct", 100 - getTureData(argsLine, cpuIdleIndexs));
					//第一次读到记录,记录各个指标的索引
					} else {
						cpuUserIndexs = getIndex(argsLine, "User");
						cpuSysIndexs = getIndex(argsLine, "Sys");
						cpuWaitIndexs = getIndex(argsLine, "Wait");
						cpuIdleIndexs = getIndex(argsLine, "Idle");
						cpuBusyIndexs = getIndex(argsLine, "Busy");
					}
				//内存信息
				} else if (argsLine[0].equals("MEM")) {
					//第二次读到记录,设置值
					if (memTotalIndexs.size() > 0 && memFreeIndexs.size() > 0) {
						dataRow.set("mem_free_prct", (getTureData(argsLine, memFreeIndexs)/getTureData(argsLine, memTotalIndexs)*100));
						dataRow.set("mem_used_prct", (getTureData(argsLine, memTotalIndexs)-getTureData(argsLine, memFreeIndexs))/getTureData(argsLine, memTotalIndexs)*100);
						dataRow.set("mem_fscache_prct", (getTureData(argsLine, memCachedIndexs)/getTureData(argsLine, memTotalIndexs)*100));
					//第一次读到标题,记录索引
					} else {
						memTotalIndexs = getIndex(argsLine, "memtotal");
						memFreeIndexs = getIndex(argsLine, "memfree");
						memCachedIndexs = getIndex(argsLine, "cached");
					}
				//磁盘读写信息
				} else if (dataRow.size() > 0 && argsLine[0].equals("DISKREAD")) {
					// 第一次读到的标题中都是read的内容，so直接处理第二次读到的数据
					dataRow.set("disk_readdatarate_kb",getTureData(argsLine, null));
				} else if (dataRow.size() > 0 && argsLine[0].equals("DISKWRITE")) {
					// 第二次读到的标题中都是read的内容，so直接处理第二次读到的数据
					dataRow.set("disk_writedatarate_kb", getTureData(argsLine, null));
				} else if (dataRow.size() > 0 && argsLine[0].equals("DISKRIO")) {
					dataRow.set("disk_read_iops", getTureData(argsLine, null));
					System.out.println("disk_read_iops : " + getTureData(argsLine, null));
				} else if (dataRow.size() > 0 && argsLine[0].equals("DISKWIO")) {
					dataRow.set("disk_write_iops", getTureData(argsLine, null));
				} else if (dataRow.size() > 0 && argsLine[0].equals("DISKREADSERV")) {
					dataRow.set("disk_read_await", getTureData(argsLine, netPacketReads));
				} else if (dataRow.size() > 0 && argsLine[0].equals("DISKWRITESERV")) {
					dataRow.set("disk_write_await", getTureData(argsLine, null));
				} else if (dataRow.size() > 0 && argsLine[0].equals("DISKAVGWIO")) {
					dataRow.set("disk_overall_iops", dataRow.getDouble("disk_read_iops") + dataRow.getDouble("disk_write_iops"));
				//网络适配器的数据传输信息
				} else if (argsLine[0].equals("NET")) {
					if (dataRow.size() == 0) {
						//第一次读到NET为NET信息的标题,记录read和write的索引
						netReadIndexs = getIndex(argsLine, "read");
						netWriteIndexs = getIndex(argsLine, "write");
					} else {
						//第二次读到NET为NET数据,根据索引处理数据
						dataRow.set("net_recv_kb",getTureData(argsLine, netReadIndexs));
						dataRow.set("net_send_kb",getTureData(argsLine, netWriteIndexs));
					}
				//适配器网络读写包信息
				} else if (argsLine[0].equals("NETPACKET")) {
					if (dataRow.size() == 0) {
						//得到索引
						netPacketReads = getIndex(argsLine, "read");
						netPacketWrites = getIndex(argsLine, "write");
					} else {
						//处理数据
						dataRow.set("net_recv_packet",getTureData(argsLine, netPacketReads));
						dataRow.set("net_send_packet",getTureData(argsLine, netPacketWrites));
					}
				//基本信息
				} else if (argsLine[0].equals("ZZZZ")) {
					Date time = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy", Locale.US).parse(argsLine[2] + " " + argsLine[3]);
					dataRow.set("sample_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
					dataRow.set("interval_len", WebConstants.interval);
					//类型(1.实时数据;2.小时数据;3.天数据)
					dataRow.set("summ_type", SrContant.SUMM_TYPE_REAL); 
					dataRow.set("computer_id", row.getString("ref_computer_id"));
					dataRow.set("computer_name", row.getString("computer_name"));
					dataRow.set("device_type", row.getString("device_type"));
				}
			}
			logger.info("--解析文件结束--");
			//将数据添加到list
			if (dataRow.size() > 0) {
				list.add(dataRow);
			}
			
			//批量插入性能数据
			agent.batchInsertServerPerf(list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				//关闭流
				br.close();
				//删除nmon文件
				nmonFile.deleteOnExit();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Main()
	 * @param args
	 */
	public static void main(String[] args) {
		NmonAgent agent = new NmonAgent();
		DataRow row = new DataRow();
		row.set("ref_computer_id", 12);
		row.set("computer_name", "localhost");
		row.set("summ_type", 1); 
		row.set("sample_time", "2015-01-22 17:01:02");
		row.set("device_type", SrContant.DEVTYPE_VAL_HOST);
		agent.getResult(null, row, "E:/JavaDevelopment/MyEcWorkspace/dongguan_sr/WebRoot/nmon/output/localhost_150122_1659.nmon");
	}
	
	/**
	 * 返回nmon文件夹下最新生成的nmon文件
	 * @param fileList
	 * @return
	 */
	public File getTureFile(File[] fileList){
		Long times = 0l;
		int j = 0;
		if (fileList != null && fileList.length > 0) {
			Long[] time = new Long[fileList.length];
			for (int i = 0; i < fileList.length; i++) {
				String filename = fileList[i].getName();
				String[] fname = filename.split("_");
				time[i] = Long.parseLong(fname[1] + fname[2]);
				if (time[i] > times) {
					j = i;
					times = time[i];
				}
			}
		}
		return fileList[j];
	}
	
	/**
	 * 得到包含特殊内容的索引
	 * @param args   数组
	 * @param alias  包含的内容
	 * @return
	 */
	public List<Integer> getIndex(String[] args,String alias){
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].contains(alias)) {
				list.add(i);
			}
		}
		return list;
	}
	
	/**
	 * 得到处理后的数据(若index为空则从下标为2的记录开始计算求和)
	 * @param args 分割后的readLine 数组
	 * @param index  包含实际数据的索引
	 * @return
	 */
	public Double getTureData(String[] args,List<Integer> index){
		Double dd = 0d;
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null && args[i].length() > 0) {
				if (index != null && index.size() > 0) {
					for (Integer integer : index) {
						if (i == integer) {
							dd += Double.parseDouble(args[i]);
						}
					}
				} else {
					if (i > 1) {
						dd += Double.parseDouble(args[i]);
					}
				}
			}
		}
		return dd;
	}
}
 