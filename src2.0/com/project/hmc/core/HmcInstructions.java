package com.project.hmc.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.project.web.WebConstants;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class HmcInstructions {

	private String hostName;
	private int port;
	private String userName;
	private String password;
	private Connection connection;
	private static Logger logger = Logger.getLogger(HmcInstructions.class);

	public HmcInstructions() {

	}

	public HmcInstructions(String _hostName, int _port, String _userName,
			String _password) {
		this.hostName = _hostName;
		this.port = _port;
		this.userName = _userName;
		this.password = _password;

	}

	public void login() throws Exception {

		// 建立连接
		connection = new Connection(hostName, port);

		try {
			connection.connect();

			// 校验
			boolean isAuthenticated = connection.authenticateWithPassword(
					userName, password);

			logger.info("isAuthenticated = " + isAuthenticated);

			if (isAuthenticated == false) {
				throw new IOException("Authenticateion failed");
			}
		} catch (Exception e) {

			throw new Exception("UserOrPasswordError");
		}
	}

	public String execCommand(String command) {

		logger.info("start command");

		// 连接同道
		Session sess = null;

		StringBuilder sb = new StringBuilder(256);

		try {
			// 创建session
			sess = connection.openSession();

			// 执行命令
			sess.execCommand(command);

			InputStream stdout = new StreamGobbler(sess.getStdout());

			BufferedReader br = new BufferedReader(
					new InputStreamReader(stdout));

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

			logger.error("执行命令出错", e);
		} finally {

			sess.close();
		}

		return sb.toString();
	}

	public BufferedReader execCommandToBufferedReader(String command) {
		logger.info("start command");
		// 连接同道
		Session sess = null;

		BufferedReader br = null;

		try {
			// 创建session
			sess = connection.openSession();
			// 执行命令
			sess.execCommand(command);

			sess.waitForCondition(ChannelCondition.TIMEOUT, 10000);
			InputStream stdout = new StreamGobbler(sess.getStdout());

			br = new BufferedReader(new InputStreamReader(stdout));

			logger.info("ExitCode: " + sess.getExitStatus());
		} catch (Throwable e) {
			logger.error("执行命令出错", e);
		} finally {
			sess.close();
		}

		return br;
	}

	public void closeConnection() {

		if (connection != null) {

			connection.close();
		}
	}

	public BufferedReader getStrResultBuffere(String command) {
		HmcInstructions hi = new HmcInstructions(WebConstants.IP_ADDRESS,
				WebConstants.PORT, WebConstants.USER_NAME,
				WebConstants.PASSWORD);
		BufferedReader br = null;
		try {
			hi.login();
			logger.info("执行命令:" + command);
			br = hi.execCommandToBufferedReader(command);
		} catch (Exception e) {
			logger.error("执行命令出错:" + command);
			e.printStackTrace();
		} finally {
			hi.closeConnection();
		}
		return br;
	}

	/**
	 * 测试方法
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		HmcInstructions hi = new HmcInstructions("192.168.1.68", 22, "USERID",
				"Passw0rd(");

		String[] ec = new String[] { "smcli lssys -l" }; //

		try {
			hi.login();

			for (int i = 0; i < ec.length; i++) {

				String sb = hi.execCommand(ec[i]);

				System.out.println("" + ec[i] + ": ");
				System.out.println(sb);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			hi.closeConnection();
		}

	}

	public void test() {

		HmcInstructions hi = new HmcInstructions("9.125.40.158", 22, "USERID",
				"Passw0rd(");

		String[] ec = new String[] { "smcli lssys -l" }; //

		try {
			hi.login();

			for (int i = 0; i < ec.length; i++) {

				String sb = hi.execCommand(ec[i]);

				System.out.println("" + ec[i] + ": ");
				System.out.println(sb);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			hi.closeConnection();
		}
	}

}
