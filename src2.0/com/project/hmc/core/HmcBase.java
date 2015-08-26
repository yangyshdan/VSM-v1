package com.project.hmc.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class HmcBase {
	private static Logger logger = Logger.getLogger(HmcBase.class);
	private Connection connection;
	private Session sess;
	private String hostName;
	private int port;
	private String userName;
	private String password;
	
	public HmcBase() { }
	
	/**
	 * Constructor
	 * @param hostName
	 * @param port
	 * @param userName
	 * @param password
	 */
	public HmcBase(String hostName, int port, String userName, String password) {
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * 建立连接,获取Session
	 * @return
	 * @throws Exception
	 */
	public Session openConn() throws Exception{
		//建立连接
		connection = new Connection(hostName, port);
		try {
			connection.connect();
			boolean isAuthenticated = connection.authenticateWithPassword(userName, password);
			//校验
			logger.info("isAuthenticated : " + isAuthenticated);
			if (isAuthenticated) {
				logger.info("Login success !");
			} else {
				logger.info("Login failed !");
			}
			sess = connection.openSession();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (connection != null) {
				connection.close();
			}
			if (sess != null) {
				sess.close();
			}
			return null;
		}
		return sess;
	}
	
	/**
	 * 建立连接
	 * @return
	 * @throws Exception
	 */
	public Connection getConn() throws Exception{
		//建立连接
		connection = new Connection(hostName, port);
		try {
			connection.connect();
			boolean isAuthenticated = connection.authenticateWithPassword(userName, password);
			//校验
			logger.info("isAuthenticated : " + isAuthenticated);
			if (isAuthenticated) {
				logger.info("Login success !");
			} else {
				logger.info("Login failed !");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			if (connection != null) {
				connection.close();
			}
			throw new Exception("UserOrPasswordError: username:"+userName+"  password:"+password+"\n"+e);
		}
		return connection;
	}
	
	public Session writeToHMC(Object[] obj){
		//连接同道
		Session sess = null;
		PrintWriter output = null;
		try {
			//创建session
			sess = connection.openSession();
			sess.requestDumbPTY();
			sess.startShell();
			output = new PrintWriter(sess.getStdin());
			if (obj.length > 0) {
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
	
	/**
	 * 执行命令
	 * @param command
	 */
	public void exeCOM(String command){
		logger.info("Start execute command : "+command);
		//连接同道
		Session sess = null;
		try {
			//创建session
			sess = connection.openSession();
			//执行命令
			sess.execCommand(command);
			sess.waitForCondition(ChannelCondition.TIMEOUT, 3000);
			logger.info("ExitCode: " + sess.getExitStatus());
		} catch (Throwable e) {
			logger.error("执行命令出错", e);
		} finally {
			sess.close();
		}
		
	}
	
	/**
	 * 用于执行命令,并返回结果
	 * @param command
	 * @return
	 */
	public List<String> executeCommand(String cmd) {
		List<String> list = new ArrayList<String>();
		Session session = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			session = connection.openSession();
			if (session != null) {
				session.execCommand(cmd);
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				is = new StreamGobbler(session.getStdout());
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					list.add(line);
				}
			} else {
				logger.error("session is null !");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (session != null) {
				session.close();
			}
		}
		return list;
	}
	
	/**
	 * 执行命令
	 * @param command
	 * @return
	 */
	public String execCommand(String command) {
		logger.info("Start execute command : " + command);
		//连接同道
		Session sess = null;
		StringBuilder sb = new StringBuilder(256);
		try {
			//创建session
			sess = connection.openSession();
			//执行命令
			sess.execCommand(command);
			sess.waitForCondition(ChannelCondition.TIMEOUT, 3000);
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout, "utf-8"));
			char[] arr = new char[512];
			int read;
			while (true) {
				read = br.read(arr, 0, arr.length);
				if (read < 0) {
					break;
				}
				sb.append(new String(arr, 0, read));
			}
			logger.info("ExitCode: " + sess.getExitStatus());
		} catch (Throwable e) {
			logger.error("执行命令出错 : " + e.getMessage());
		} finally {
			sess.close();
		}
		return sb.toString();
	}
	
	/**
	 * 释放资源
	 */
	public void closeConn() {
		if (sess != null) {
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
