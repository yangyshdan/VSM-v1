package com.project.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SSHExecutors {
	public static void main(String[] args) {
		//创建一个最大线程数量为10的线程池
		ExecutorService service = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 1000; i++) {
			final int index = i;
			System.out.println("task:"+i);
			Runnable runnable = new Runnable() {
				public void run() {
					//这里写要执行SSH采集方法
					 System.out.println("thread start" + index); 
					 try {   
	                        Thread.sleep(10);   
	                    } catch (InterruptedException e) {   
	                        e.printStackTrace();   
	                    } 
	                 System.out.println("thread end" + index);   
				}
			};
			//把线程加入到线程池，自动排队
			service.execute(runnable);
		}
		try {
			//关闭线程池
			service.shutdown();
		} catch (Exception e) {
		}
		
	}
}
