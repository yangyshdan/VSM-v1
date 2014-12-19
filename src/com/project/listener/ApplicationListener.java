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
 * ����:ϵͳ����ʱ������
 */
public class ApplicationListener implements ServletContextListener
{
	
	private ServletContext context = null;
	
	private static Logger logger = Logger.getLogger(ApplicationListener.class);
	
	/**
	 * ��ϵͳ����ʱ����
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
	 * ��ϵͳֹͣʱ����
	 *
	 * @param event a ServletContextEvent instance
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
		//�ر����Դ������
		Configure.getInstance().destroyDataSource();
		
		if (logger.isInfoEnabled())
			logger.info("Stopping application......");
	}
	
	/**
	 * ϵͳ����ʱ��ʼ����Ӧ�����
	 */ 
	private void init()
	{
		//��ʼӦ�ó����Ŀ¼·��
		Application.setRootPath(context.getRealPath("/"));
		//������ݿ������ļ�
		Configure.getInstance();
		//初始化ssh登录配置
//		new SshConfig().initSshCon();
		//�������������
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