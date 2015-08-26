package com.project.nmon.engn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.DateHelper;

/**
 * 需要解析的性能数据在 bbbp 下 所以遇到bbbp开头的直接跳过循环
 * @author lic
 *
 */
public class AnalyzeNmonData {
	
	public void analyzeData(){
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("D:\\yingChuang\\fsm\\nmon\\localhost_140102_0915.nmon"));
			String line="";
			int i=0;
			while( (line=br.readLine())!=null){
				
				if(line.startsWith("BBBP")){
					i++;
					continue;
				}
				
				
				if(i>0){
					
					DataRow data = new DataRow();
					
					if(line.startsWith("ZZZZ")){
						
						String [] time  = line.split(",");
						
						data.put("", DateHelper.formatTime(DateHelper.parseString(time[3]+time[2],"dd-MMM-yyyy HH:mm:ss")));
						
					}else if(line.startsWith("CPU_ALL")){
						
						String [] cpuPref = line.split(",");
						
						data.put("cpu_usr_prct", cpuPref[2]);
						data.put("cpu_sys_prct", cpuPref[3]);
						data.put("cpu_wait_prct", cpuPref[4]);
						data.put("cpu_idle_prct", cpuPref[5]);
						data.put("cpu_busy_prct", 100-Double.parseDouble(cpuPref[5]));
						
					}else if(line.startsWith("MEM")){
						
						String [] mem_pref = line.split(",");
						
//						data.put("mem_proc_prct", );
//						data.put("mem_fscache_prct", );
//						data.put("mem_sys_prct", );
//						data.put("mem_free_prct", );
//						data.put("mem_used_prct", );
						
					}else if(line.startsWith("NET")){
						
						
					}else if(line.startsWith("NETPACKET")){
						
						
					}else if(line.startsWith("DISKREAD")){
						
						String[] diskRead =  line.split(",");
						double sumRead=0.0d;
						
						for (int j = 2; j < diskRead.length; j++) {
							  sumRead+=Double.valueOf(diskRead[j]);
						}
						data.put("DISK_READDATARATE_KB", sumRead);
						
					}else if(line.startsWith("DISKWRITE")){
					
						String[] diskWrite =  line.split(",");
						double sumWrite= 0.0d;
						for (int j = 2; j < diskWrite.length; j++) {
							sumWrite += Double.valueOf(diskWrite[j]);
						}
						data.put("DISK_WRITEDATARATE_KB", sumWrite);
					}
				}
				
				
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
