package com.project.sax.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;


public class KillHDSPrfProcess {
	private Logger logger= Logger.getLogger(this.getClass());
	
	public static void init(){
		new KillHDSPrfProcess().killProcess("auperform.exe");
		File file = new File("E:/vsm/test.txt");
	}
	
	private void killProcess(String processName){
		if(findProcess(processName)){
			String command = "taskkill /F /IM "+processName;
			try {
				Runtime.getRuntime().exec(command);
				logger.info("kill the HDS performance process[auperform.exe] success !");
			} catch (Exception e) {
				logger.error("kill the HDS performance process[auperform.exe] failed:" + e.getMessage());
			}
		}
	} 
	
	 private boolean findProcess(String processName){   
        BufferedReader br=null;   
        try{   
            //下面这句是列出含有processName的进程图像名   
            Process proc=Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq "+processName+"\"");   
            br=new BufferedReader(new InputStreamReader(proc.getInputStream()));   
            String line=null;   
            while((line=br.readLine())!=null){   
                //判断指定的进程是否在运行   
                if(line.contains(processName)){   
                	logger.info("process ["+processName+"] is exist!");
                    return true;   
                }   
            }   
            logger.info("process ["+processName+"] is not exist!");
            return false;   
        }catch(Exception e){   
            e.printStackTrace(); 
            logger.error("find process ["+processName+"] error:"+e.getMessage());
            return false;   
        }finally{   
            if(br!=null){   
                try{   
                    br.close();   
                }catch(Exception ex){   
                }   
            }   
        }   
    }   
}
