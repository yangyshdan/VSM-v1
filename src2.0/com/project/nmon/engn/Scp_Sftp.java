package com.project.nmon.engn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import com.huiming.base.jdbc.DataRow;
import com.huiming.service.agent.AgentService;
import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Scp_Sftp {

	private static Logger logger = Logger.getLogger(Scp_Sftp.class);
	private Connection connection;
	//IP地址
	private String hostName;
	//端口
	private int port;
	//用户名
	private String userName;
	//密码
	private String password;
	
	/**
	 * Constructor
	 * @param _hostName
	 * @param _port
	 * @param _userName
	 * @param _password
	 */
	public Scp_Sftp(String _hostName, int _port, String _userName, String _password) {
		this.hostName = _hostName;
		this.port = _port;
		this.userName = _userName;
		this.password = _password;
	}

	/**
	 * 创建连接,登录验证
	 * @return
	 * @throws Exception
	 */
	public Connection login() throws Exception {
		//建立连接
		connection = new Connection(hostName, port);
		try {
			connection.connect();
			//校验
			boolean isAuthenticated = connection.authenticateWithPassword(userName, password);
			if (!isAuthenticated) {
				System.err.println("登录失败");
				return null;
			}
		} catch (Exception e) {
			System.err.println("打开连接出错");
			return null;
		}
		return connection;
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
			logger.error("ExitCode: " + sess.getExitStatus());
		} catch (Throwable e) {
			logger.error("执行命令出错 : " + e.getMessage());
		} finally {
			sess.close();
		}
		return sb.toString();
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

	public void closeConnection() {

		if (connection != null) {

			connection.close();
		}
	}

	public static void main(String[] args) {

//		Scp_Sftp ss =new Scp_Sftp();
//		
//		ss.executor();
	}

	public void executor() {

		final String localTarget = "D:/yingChuang/fsm/nmon"; // nmon所在本地路径

		final String fileName = "nmon_x86_rhel5";// nmon文件名

		final String remoteTargetDirectory = "/usr/local";// nmon被拷贝到的目录

		final String remoteNmonDir = "nmonTemp"; // nmon存放目录

		final String nmonOutPath = "/tmp/nmonOut"; // 输出nmone日志文件路径

		List<DataRow> virtualList = agentService.getVirtualConfigList();
		
		// 创建一个最大线程数量为10的线程池
		ExecutorService service = Executors.newFixedThreadPool(10);
		
//		for (int i = 0, len =virtualList.size(); i < len; i++) {
//			
//		}
		final String [] s =new String[]{"192.168.1.68","192.168.1.70"};
		
		for (int i = 0; i < s.length; i++) {

			final String temp= s[i];
			
			Scp_Sftp scp = new Scp_Sftp(temp,22,"root","1234567A");
			Runnable runnable = new Runnable() {

				public void run() {

					try {
						login();

						// 建立一个SFTP客户端
//						SFTPv3Client sftpClient = new SFTPv3Client(connection);
//
//						SCPClient client = new SCPClient(connection);
//						
//						// 获取远程目录下的所有目录
//						Vector<SFTPv3DirectoryEntry> directoryVector = sftpClient
//								.ls(remoteTargetDirectory);
//
//						boolean haveNmonDir = false;
//
//						// 判断目录是否存在
//						for (SFTPv3DirectoryEntry de : directoryVector) {
//							if (de.filename.equals(remoteNmonDir)) {
//								haveNmonDir = true;
//								break;
//							}
//						}
//
//						if (!haveNmonDir) {// nmon 目录不存在
//
//							// 远程新建目录 权限可读写 (默认文件夹的权限为0777，默认文件的权限为0666)
//							sftpClient.mkdir(remoteTargetDirectory + "/" + remoteNmonDir,
//									0777);
//							sftpClient.mkdir(nmonOutPath, 0777);
//
//							// 将本地nmon文件上传到服务器端的目录下
//							client.put(localTarget + "/" + fileName, remoteTargetDirectory
//									+ "/" + remoteNmonDir);
//							
//						    execCommand("cd " + remoteTargetDirectory + "/"
//									+ remoteNmonDir + " \n " + "chmod 777 " + fileName );
//							
//						} 
//
//						String count = execCommand("ps -ef | grep nmon_x86_rhel5 | grep -v "+"grep"+" | wc -l");
//						System.out.println("nmon进程数:"+count);
//						//进程不存在
//						if(Integer.parseInt(count.trim()) <= 0){
//							
//							//读取输出nmon日志文件目录
//							Vector<SFTPv3DirectoryEntry> nmonLogVector = sftpClient
//							.ls(nmonOutPath);
//							
//							Map<Integer,String > logMap = new HashMap<Integer,String>();
//							
//							//读取nmon日志文件
//							for (SFTPv3DirectoryEntry logEntry : nmonLogVector) {
//								
//								String logName = logEntry.filename;
//								int nmonSign = logName.lastIndexOf(".nmon");
//								
//								if( nmonSign> 0){
//									
//									String name = logName.substring(0,nmonSign);
//									String[] nameArr =  name.split("_");
//									logMap.put(Integer.parseInt(nameArr[1]+nameArr[2]),logName);
//								}
//							}
//							
//							List<Integer> logList =new ArrayList<Integer>(logMap.keySet()); 
//							
//							//如果有多个nmon日志文件
//							if(logList.size()>1){
//								
//								Collections.sort(logList);
//								System.out.println(logList);
//								Integer fileLength = logList.size();
//								for (int i = 0; i < fileLength-1; i++) {
//									
//									Integer file =logList.get(i);
//									sftpClient.rm(nmonOutPath+"/"+logMap.get(file));
//								}
//								
//								Integer file =logList.get(fileLength-1);
//								//将nmon文件下载到本地
//								client.get(nmonOutPath+"/"+logMap.get(file),"D:/yingChuang/fsm/nmon");
//								//将此文件删除
//								sftpClient.rm(nmonOutPath+"/"+logMap.get(file));
//								
//							}else if(logList.size()==1){
//								
//								Integer file =logList.get(0);
//								client.get(nmonOutPath+"/"+logMap.get(file),"D:/yingChuang/fsm/nmon");
//								sftpClient.rm(nmonOutPath+"/"+logMap.get(file));
//							}
//							
//							//执行nmon命令
//							execCommand(remoteTargetDirectory + "/" + remoteNmonDir + "/"
//									+ fileName+" -fT -s 30 -c 1 -m "+nmonOutPath);
//							
//						}else{
//							
//							System.out.println("nmon进程存在");
//							
//						}
							
					} catch (IOException e) {

						e.printStackTrace(System.err);
						System.exit(2);

					} catch (Exception e) {

						e.printStackTrace();
					}finally{
						
						closeConnection();
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			// 把线程加入到线程池，自动排队
			service.execute(runnable);
		}

		try {
			// 关闭线程池
			service.shutdown();
		} catch (Exception e) {
		}

	}
	
	private AgentService agentService = new AgentService();
}
