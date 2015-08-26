package com.project.nmon.engn;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.security.AES;
import com.project.hmc.core.HmcBase;
import com.project.web.WebConstants;

public class UpNmon {

	private static Logger logger = Logger.getLogger(UpNmon.class);
	private NmonAgent nmonAgent = new NmonAgent();
	
	//linux nmon文件所在本地文件夹
	private static final String LOCAL_LINUX_DIR = "/linux"; 
	//aix nmon文件所在本地文件夹
	private static final String LOCAL_AIX_DIR = "/aix"; 
	//nmon名称
	private static final String NMON_NAME = "nmon";
	//nmon文件
	private static final String NMON_AIX = "nmon";
	private static final String NMON_X86_SLES11 = "nmon_x86_sles11";
	private static final String NMON_X86_64_SLES11 = "nmon_x86_64_sles11";
	//nmon被拷贝到服务器端的目录
	private static final String REMOTE_TARGET_DIR = "/usr/local";
	//nmon文件存放在服务器端的路径
	private static final String REMOTE_NMON_PATH = "/usr/local/nmon"; 
	//服务器nmon输出文件路径
	private static final String REMOTE_OUTPUT_PATH = "/usr/local/nmon/output";
	
	/**
	 * 根据服务器(物理机,虚拟机)操作系统版本,上传相应版本的nmon文件,收集性能数据
	 * @param marker
	 */
	public void doCollectPerfInfo(List<DataRow> computerList, final String marker) {
		try {
			//nmon文件所在目录
			final String nmonDir = this.getClass().getClassLoader().getResource("/").toString().replaceAll("%20", " ")
					.replaceAll("/WEB-INF/classes", "").replaceAll("file:/", "") + "nmon";
			//创建一个最大线程数量为10的线程池
			ExecutorService service = Executors.newScheduledThreadPool(10);

			//开始执行采集操作
			for (int i = 0, len = computerList.size(); i < len; i++) {
				final DataRow computer = computerList.get(i);
				if (computer != null && computer.size() > 0) {
					String[] ipaddressarray = computer.getString("ip_address").split(",");
					String osType = computer.getString("os_type");
					String ipaddress = null;
					if (ipaddressarray.length > 1) {
						ipaddress = ipaddressarray[1];
					} else {
						ipaddress = ipaddressarray[0];
					}
					
					//判断操作系统类型,如果不是LINUX或UNIX,则不进行采集操作
					if (!osType.equals(WebConstants.OSTYPE_LINUX)) {
						continue;
					}

					//建立与服务器的连接
					final HmcBase hmcBase = new HmcBase(ipaddress, 22, computer.getString("user"), new AES(computer.getString("id")).decrypt(computer.getString("password"),"UTF-8"));
					//获取连接
					final Connection connection = hmcBase.getConn();
					//判断服务器系统类型,位数,上传相应的nmon文件
					String result = hmcBase.executeCommand("uname -a").get(0);
					String tmpFileName = null;
					String tmpFilePath = null;
					//For Linux
					if (result.indexOf("Linux") > -1) {
						tmpFilePath = LOCAL_LINUX_DIR;
						//For 64 bit(x86_64)
						if (result.indexOf("x86_64") > -1) {
							tmpFileName = NMON_X86_64_SLES11;
						//For 32 bit(i686|i386)
						} else if (result.indexOf("i686") > -1 || result.indexOf("i386") > -1) {
							tmpFileName = NMON_X86_SLES11;
						}
					//For AIX
					} else {
						tmpFilePath = LOCAL_AIX_DIR;
						tmpFileName = NMON_AIX;
					}
					//nmon文件名
					final String fileName = tmpFileName;
					//本地nmon文件所在目录(:nmon/linux|nmon/aix)
					final String localTarget = nmonDir + tmpFilePath;
					//本地nmon输出文件路径
					final String localOutputPath = nmonDir + "/output";
					
					Runnable runnable = new Runnable() {
						public void run() {
							try {
								if (connection != null) {
									//建立一个SFTP客户端
									SFTPv3Client sftpClient = new SFTPv3Client(connection);
									SCPClient client = new SCPClient(connection);
									//获取远程目录下的所有目录
									Vector<SFTPv3DirectoryEntry> directoryVector = sftpClient.ls(REMOTE_TARGET_DIR);
									boolean haveNmonDir = false;
									//判断目录是否存在
									for (SFTPv3DirectoryEntry de : directoryVector) {
										if (de.filename.equals(NMON_NAME)) {
											haveNmonDir = true;
											break;
										}
									}
									//nmon目录不存在
									if (!haveNmonDir) {
										//远程新建目录,权限可读写 
										//目录:/usr/local/nmon
										sftpClient.mkdir(REMOTE_NMON_PATH, 0777);
										//目录:/usr/local/nmon/output
										sftpClient.mkdir(REMOTE_OUTPUT_PATH, 0777);
										//将本地nmon文件上传到服务器端的/usr/local/nmon目录下
										client.put(localTarget + "/" + fileName, REMOTE_NMON_PATH);
										//执行命令,修改nmon文件权限
										hmcBase.execCommand("cd " + REMOTE_NMON_PATH + " \n " + "chmod 777 " + fileName );
									}
									
									String count = hmcBase.execCommand("ps -ef | grep " + fileName + " | grep -v grep | grep -v /usr/bin/perl | wc -l");
									logger.info("nmon进程数:" + count);
									
									//进程不存在
									if (Integer.parseInt(count.trim()) <= 0) {
										//读取输出nmon文件目录
										Vector<SFTPv3DirectoryEntry> nmonLogVector = sftpClient.ls(REMOTE_OUTPUT_PATH);
										//下载文件到本地目录(../nmon/output)
										DownNmontoLocal(marker, nmonLogVector, client, sftpClient, computer, localOutputPath, REMOTE_OUTPUT_PATH);
										//执行命令,生成文件
										hmcBase.exeCOM(REMOTE_TARGET_DIR + "/" + NMON_NAME + "/"
												+ fileName + " -F " + computer.getString("computer_name") + "_"
												+ DateHelper.formatDate(new Date(), "yyMMdd_HHmm") + ".nmon"
												+ " -t -s "
												+ WebConstants.interval
												+ " -c 1 -m "
												+ REMOTE_OUTPUT_PATH);
									} else {
										logger.info("nmon进程存在!");
									}
								}
							} catch (IOException e) {
								e.printStackTrace(System.err);
								System.exit(2);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								hmcBase.closeConn();
							}
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
					//把线程加入到线程池，自动排队
					service.execute(runnable);
				}
			}
			//关闭线程池
			service.shutdown();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 下载nmon文件到本地目录
	 * @param marker
	 * @param nmonLogVector
	 * @param client
	 * @param sftpClient
	 * @param dataRow
	 * @param localOutputPath
	 * @param remoteOutputPath
	 * @throws IOException
	 */
	public synchronized void DownNmontoLocal(String marker, Vector<SFTPv3DirectoryEntry> nmonLogVector, SCPClient client,
			SFTPv3Client sftpClient, DataRow dataRow, String localOutputPath, String remoteOutputPath) throws IOException {
	for (SFTPv3DirectoryEntry logEntry : nmonLogVector) {
			String logName = logEntry.filename;
			if (logName.lastIndexOf(".nmon") > 0) {
				//将nmon文件下载到本地目录
				client.get(remoteOutputPath + "/" + logName, localOutputPath);
				logger.info("已下载文件：" + logName);
				//将服务器中该nmon文件删除
				sftpClient.rm(remoteOutputPath + "/" + logName);
				//解析nmon文件
				nmonAgent.getResult(marker, dataRow, localOutputPath + "/" + logName);
				break;
			}
		}
	}
	
}
