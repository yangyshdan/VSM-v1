package com.project.x86monitor;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

public class MyTask<T> implements Runnable {
	
	private T task;
	private String methodName;
	private Logger logger = Logger.getLogger(MyTask.class);
	
	public MyTask(T task, String methodName) {
		this.task = task;
		this.methodName = methodName;
	}

	public void run() {
		if(task != null){
			try {
				task.getClass().getMethod(methodName).invoke(task, new Object[0]);
			} catch (NoSuchMethodException e) {
				logger.error("", e);
			} catch (SecurityException e) {
				logger.error("", e);
			} catch (IllegalAccessException e) {
				logger.error("", e);
			} catch (IllegalArgumentException e) {
				logger.error("", e);
			} catch (InvocationTargetException e) {
				logger.error("", e);
			}
		}
		else{
			logger.error("######## 任务为空", new IllegalArgumentException(task.getClass().getCanonicalName()));
		}
	}

}
