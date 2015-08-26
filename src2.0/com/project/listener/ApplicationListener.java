package com.project.listener;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.timerengine.TaskManager;
import com.huiming.base.timerengine.config.TaskConfig;
import com.huiming.web.system.Application;
import com.project.web.WebConstants;

/**
 * 描述:系统启动时监听器
 */
public class ApplicationListener implements ServletContextListener
{
	
	private ServletContext context = null;
	
	private static Logger logger = Logger.getLogger(ApplicationListener.class);
	
	/**
	 * 在系统启动时调用
	 *
	 * @param event a ServletContextEvent instance
	 */
	public void contextInitialized(ServletContextEvent event)
	{
		if (logger.isInfoEnabled())
			logger.info("Starting application......");
		
		context = event.getServletContext();
		init();
	}
	
	/**
	 * 在系统停止时调用
	 *
	 * @param event a ServletContextEvent instance
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
		//关闭数据源的连接
		Configure.getInstance().destroyDataSource();
		
		if (logger.isInfoEnabled())
			logger.info("Stopping application......");
	}
	
	/**
	 * 系统启动时初始化相应的数据
	 */
	private void init()
	{
		//初始应用程序根目录路径
		Application.setRootPath(context.getRealPath("/"));
		//读入数据库配置文件
		Configure.getInstance();
		//初始化ssh登录配置
//		new SshConfig().initSshCon();
		//启动任务管理器
		TaskManager.start();
		
		Application.setRootPath(context.getRealPath("/"));
		Configure.getInstance();
		//配置采集性能的时间间隔
		TaskConfig config = new TaskConfig();
		List<Object> taskList = config.getTaskList();
		if(taskList!=null && taskList.size()>0){
			for (Object obj : taskList) {
				JSONObject json = new JSONObject().fromObject(obj);
				if(json.getString("id").equals("collectionInfo")){
					System.out.println(json.getInt("task-interval"));
					WebConstants.interval = json.getInt("task-interval");
					break;
				}
			}
		}
	}
	
	
	
}