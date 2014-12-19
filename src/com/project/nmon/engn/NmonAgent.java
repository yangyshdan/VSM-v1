package com.project.nmon.engn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.agent.AgentService;
import com.huiming.service.agent.SecService;
import com.project.web.WebConstants;

public class NmonAgent {
	AgentService agent = new AgentService();
	private SecService secService = new SecService();
	private static Logger logger = Logger.getLogger(NmonAgent.class);
	
	
	public void getResult(String marker,DataRow row,String path){
		
		File nmonFile = new File(path);
		BufferedReader br = null;
		List<DataRow> list = new ArrayList<DataRow>();
		String readToLine = "";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(nmonFile)));
			logger.info("--开始解析文件--");
			//记录索引
			List<Integer> netReadIndexs = new ArrayList<Integer>();
			List<Integer> netWriteIndexs = new ArrayList<Integer>();
			List<Integer> netPacketReads = new ArrayList<Integer>();
			List<Integer> netPacketWrites = new ArrayList<Integer>();
			
			DataRow dataRow = new DataRow();
			
			while((readToLine = br.readLine())!=null){

				String[] argsLine = readToLine.trim().split(",");
				if(argsLine[0].equalsIgnoreCase("zzzz")){
					//基本信息
					Date time = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy",Locale.US).parse(argsLine[2]+" "+argsLine[3]);
					dataRow.set("sample_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
					dataRow.set("interval_len", WebConstants.interval);
					dataRow.set("summ_type", 1);    //类型(1为即时信息，2为小时统计，3为天统计)
					dataRow.set("computer_id", row.getString("computer_id"));
					dataRow.set("computer_name", row.getString("computer_name"));
					dataRow.set("perf_marker",marker);
				}else if(argsLine[0].equals("NET")){
					//网络性能
					if(dataRow.size()==0){
						//第一次读到NET为NET信息的标题,记录read和write的索引
						netReadIndexs = getIndex(argsLine, "read");
						netWriteIndexs = getIndex(argsLine, "write");
					}else{  
						//第二次读到NET为NET数据,根据索引处理数据
						dataRow.set("net_recv_kb", getTureData(argsLine, netReadIndexs));
						dataRow.set("net_send_kb", getTureData(argsLine, netWriteIndexs));
					}
				}else if(argsLine[0].equals("NETPACKET")){
					if(dataRow.size()==0){
						//得到索引
						netPacketReads = getIndex(argsLine, "read");
						netPacketWrites = getIndex(argsLine, "write");
					}else{  
						//处理数据
						dataRow.set("net_recv_packet", getTureData(argsLine, netPacketReads));
						dataRow.set("net_send_packet", getTureData(argsLine, netPacketWrites));
					}
				}else if(dataRow.size()>0 && argsLine[0].equals("DISKREAD")){
					//第一次读到的标题中都是read的内容，so直接处理第二次读到的数据
					dataRow.set("disk_readdatarate_kb", getTureData(argsLine, null));
				}else if(dataRow.size()>0 && argsLine[0].equals("DISKWRITE")){
					//第二次读到的标题中都是read的内容，so直接处理第二次读到的数据
					dataRow.set("disk_writedatarate_kb", getTureData(argsLine, null));
				}else if(dataRow.size()>0 && argsLine[0].equals("DISKRIO")){
					dataRow.set("disk_read_iops", getTureData(argsLine, null));
				}else if(dataRow.size()>0 && argsLine[0].equals("DISKWIO")){
					dataRow.set("disk_write_iops", getTureData(argsLine, null));
				}else if(dataRow.size()>0 && argsLine[0].equals("DISKREADSERV")){
					dataRow.set("disk_read_await", getTureData(argsLine, netPacketReads));
				}else if(dataRow.size()>0 && argsLine[0].equals("DISKWRITESERV")){
					dataRow.set("disk_write_await", getTureData(argsLine, null));
				}
				
				//结尾
				if(dataRow.size()>0 && argsLine[0].equals("DISKAVGWIO")){
					dataRow.set("disk_overall_iops", dataRow.getDouble("disk_read_iops")+dataRow.getDouble("disk_write_iops"));
					list.add(dataRow);
				}
			}
			//批量插入性能数据
			agent.batchInsertPrf3(list);
			
			List<DataRow> updateList = secService.getUpdateHypervisorPrf(marker);
			secService.batchUpdateHypervisorPrf(updateList);
		} catch (Exception e) {
			logger.error("解析文件异常结束！");
			logger.error(e);
			e.printStackTrace();
		}finally{
			try {
				//关闭流
				br.close();   
				//删除nmon文件
				nmonFile.delete();   
				logger.info("--解析文件结束--");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
//		String str = "09:15:30 02-JAN-2014";
//		try {
//			Date time = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy",Locale.US).parse(str);
//			System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		NmonAgent agent = new NmonAgent();
		DataRow row = new DataRow();
		row.set("computer_id", 1);
		row.set("computer_name", "bbb");
//		agent.getResult(row, "D://localhost_140102_0915.nmon");
		
		
	}
	
	/**
	 * 返回nmon文件夹下最新生成的nmon文件
	 * @param fileList
	 * @return
	 */
	public File getTureFile(File[] fileList){
		Long times = 0l;
		int j = 0;
		if(fileList!=null && fileList.length>0){
			Long[] time = new Long[fileList.length];
			for (int i = 0; i < fileList.length; i++) {
				String filename = fileList[i].getName();
				String[] fname = filename.split("_");
				time[i] = Long.parseLong(fname[1]+fname[2]);
				if(time[i]>times){
					j = i;
					times = time[i];
				}
			}
		}
		return fileList[j];
	}
	
	/**
	 * 的到包含特殊内容的索引
	 * @param args   数组
	 * @param alias  包含的内容
	 * @return
	 */
	public List<Integer> getIndex(String[] args,String alias){
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < args.length; i++) {
			if(args[i].contains(alias)){
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
			if(index!=null && index.size()>0){
				for (Integer integer : index) {
					if(i==integer){
						dd+=Double.parseDouble(args[i]);
					}
				}
			}else{ 
				if(i>1){
					dd+=Double.parseDouble(args[i]);
				}
			}
		}
		return dd;
	}
}
 