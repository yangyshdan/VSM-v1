package com.project.nmon.core;

import java.io.IOException;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class NmonBase {
	private static Logger logger = Logger.getLogger(NmonBase.class);
	private Connection connection;
	private Session sess;
	
	private String hostName;
	private int port;
	private String userName;
	private String password;
	
	public NmonBase(){}
	
	public NmonBase(String hostName, int port, String userName, String password) {
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
				throw new IOException("Authenticateion failed");
			}
			
			sess = connection.openSession();

		} catch (Exception e) {
			throw new Exception("UserOrPasswordError: username:"+userName+"  password:"+password+"\n"+e);
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
				throw new IOException("Authenticateion failed");
			}
			

		} catch (Exception e) {
			throw new Exception("UserOrPasswordError: username:"+userName+"  password:"+password+"\n"+e);
		}
		return connection;
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
