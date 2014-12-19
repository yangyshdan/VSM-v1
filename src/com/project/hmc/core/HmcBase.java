package com.project.hmc.core;

import java.io.PrintWriter;
import org.apache.log4j.Logger;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class HmcBase {
	private static Logger logger = Logger.getLogger(HmcBase.class);
	private Connection connection;
	private Session sess;
	
	private String hostName;
	private int port;
	private String userName;
	private String password;
	
	public HmcBase(){}
	
	public HmcBase(String hostName, int port, String userName, String password) {
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	public Session openConn() throws Exception{
		// 建立连接
		connection = new Connection(hostName, port);
		try {
			connection.connect();
			boolean isAuthenticated = connection.authenticateWithPassword(userName, password);
			// 校验
			logger.info("校验:" + isAuthenticated);
			if (isAuthenticated == false) {
				logger.info("HMC登录失败");
				return null;
			}
			sess = connection.openSession();
		} catch (Exception e) {
			logger.info("打开链接失败"+e);
			return null;
		}
		return sess;
	}
	
	
	public Connection getConn() throws Exception{
		// 建立连接
		connection = new Connection(hostName, port);
		try {
			connection.connect();
			boolean isAuthenticated = connection.authenticateWithPassword(userName, password);
			// 校验
			logger.info("isAuthenticated = " + isAuthenticated);
			if (isAuthenticated == false) {
				return null;
			}

		} catch (Exception e) {
			throw new Exception("UserOrPasswordError: username:"+userName+"  password:"+password+"\n"+e);
		}
		return connection;
	}
	
	
	public Session writeToHMC(Object[] obj){
		// 连接同道
		Session sess = null;
		PrintWriter output = null;
		try {
			// 创建session
			sess = connection.openSession();
			sess.requestDumbPTY();
			sess.startShell();
			output = new PrintWriter(sess.getStdin());
			if(obj.length>0){
				for (Object object : obj) {
					output.println(object);
					output.flush();
					Thread.sleep(1000);
				}
			}
		} catch (Throwable e) {
			logger.error("执行命令出错", e);
		}
		return sess;
	}
	public void exec(String command){

		logger.info("start command: "+command);

		// 连接同道
		Session sess = null;

		try {
			// 创建session
			sess = connection.openSession();
			// 执行命令
			sess.execCommand(command);
		} catch (Throwable e) {

			logger.error("执行命令出错", e);
			e.printStackTrace();
			
		} finally {
			if(sess!=null){
				sess.close();
			}
		}
	}
	
	public void closeConn() {
		if(sess!=null){
			sess.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
	
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
