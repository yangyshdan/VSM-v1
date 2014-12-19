package com.project.nmon.engn;

import java.io.IOException;
import java.util.ArrayList;
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
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.web.WebConstants;

public class UpNmon {

	private static Logger logger = Logger.getLogger(UpNmon.class);
	
	public void doUpToVritual(final String marker) {
		 final String nmonDir =
			 this.getClass().getClassLoader().getResource("/").toString()
			 .replaceAll("%20"," ").replaceAll("/WEB-INF/classes",
			 "").replaceAll("file:/", "")+"nmon";       

		final String localTarget = nmonDir; // nmon文件所在路径  
		final String fileName = "nmon";// nmon文件名
		final String remoteTargetDirectory = "/usr";// nmon被拷贝到的目录    
		final String remoteNmonDir = "nmonScript"; // nmon存放目录
		final String nmonOutPath = "/tmp/nmonLog"; // 输出nmone日志文件路径   
		//虚拟机列表  
		List<DataRow> virtualList = agentService.getVirtualIpList();   
		// 创建一个最大线程数量为10的线程池
		ExecutorService service = Executors.newScheduledThreadPool(10);

		for (final DataRow dataRow : virtualList) {
			//截取IP
			String[] ipaddressarray = dataRow.getString("ip_address").split(",");
			String ipaddress="";
			if(ipaddressarray.length>1){
				ipaddress = ipaddressarray[1];
			}else{
				ipaddress = ipaddressarray[0];
			}
			
			//判断用户类型是否给默认用户信息
			DataRow row = null;
			row = agentService.getVIOSLoginInfo(dataRow.getInt("vm_id"));
			String user = "";
			String pwd = "";
			boolean isflag = false;
			if(dataRow.getString("targeted_os").equalsIgnoreCase("vioserver") && row==null){
				user = "padmin";
				pwd="padmin";
				isflag = true;
			}else if(row!=null && row.size()>0){
				user = row.getString("user");
				pwd=new DES().decrypt(row.getString("password"));
				isflag = true;
			}
			if(isflag){
				final Scp_Sftp scp = new Scp_Sftp(ipaddress, 22, user,pwd);
				//多线程
				Runnable runnable = new Runnable() {
					public void run() {
						try {
							Connection connection = scp.login();
							if(connection!=null){
								// 建立一个SFTP客户端
								SFTPv3Client sftpClient = new SFTPv3Client(connection);
								SCPClient client = new SCPClient(connection);
								// 获取远程目录下的所有目录
								Vector<SFTPv3DirectoryEntry> directoryVector = sftpClient.ls(remoteTargetDirectory);
								boolean haveNmonDir = false;
								// 判断目录是否存在
								for (SFTPv3DirectoryEntry de : directoryVector) {
									if (de.filename.equals(remoteNmonDir)) {
										haveNmonDir = true;
										break;
									}
								}
								if (!haveNmonDir) {// nmon 目录不存在
									// 远程新建目录 权限可读写 (默认文件夹的权限为0777，默认文件的权限为0666)
									sftpClient.mkdir(remoteTargetDirectory + "/" + remoteNmonDir,0777);
									sftpClient.mkdir(nmonOutPath, 0777);
									// 将本地nmon文件上传到服务器端的目录下
									client.put(localTarget + "/" + fileName, remoteTargetDirectory+ "/" + remoteNmonDir);
									
									scp.execCommand("cd " + remoteTargetDirectory + "/"+ remoteNmonDir + " \n " + "chmod 777 " + fileName);
								}
								String count = scp.execCommand("ps -ef | grep "+fileName+" | grep -v grep | grep -v /usr/bin/perl | wc -l");
								System.out.println("nmon进程数:" + count);
								// 进程不存在
								if (Integer.parseInt(count.trim()) <= 0) {
									// 读取输出nmon日志文件目录
									Vector<SFTPv3DirectoryEntry> nmonLogVector = sftpClient.ls(nmonOutPath);
//								Map<Integer, String> logMap = new HashMap<Integer, String>();
									DownNmontoLocal(marker,nmonLogVector, client, sftpClient,dataRow,nmonDir,nmonOutPath);
									scp.exeCOM(remoteTargetDirectory + "/" + remoteNmonDir + "/"
											+ fileName + " -F " + dataRow.getString("computer_name") + "_"
											+ DateHelper.formatDate(new Date(), "yyMMdd_HHmm") + ".nmon" + " -T -s "+(WebConstants.interval/1000-30)+" -c 1 -m "
											+ nmonOutPath);
								} else {
									System.out.println("nmon进程存在");
								}
							}
						} catch (IOException e) {
							e.printStackTrace(System.err);
							System.exit(2);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							scp.closeConnection();
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
		}
		try {
			// 关闭线程池
			service.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void DownNmontoLocal(String marker,Vector<SFTPv3DirectoryEntry> nmonLogVector,SCPClient client,SFTPv3Client sftpClient, DataRow data,String nmonDir,String nmonOutPath) throws IOException{
		
		for (SFTPv3DirectoryEntry logEntry : nmonLogVector) {

			String logName = logEntry.filename;
			
			String reg= "^([\\w]+)_[\\d]+_[\\d]+\\.nmon$";
			Matcher m = Pattern.compile(reg).matcher(logName);
			
			if (m.find() && m.group(1).equals(data.getString("computer_name"))) {
				
				// 将nmon文件下载到本地
				client.get(nmonOutPath + "/" + logName, nmonDir);
				
				logger.info("下载的文件：" + logName);
				
				// 将此文件删除
				sftpClient.rm(nmonOutPath + "/" + logName);
				
				nmonAgent.getResult(marker,data, nmonDir+"\\"+logName);
				break;
				
			}
		}

	}
	

	private NmonAgent nmonAgent= new NmonAgent();
	private AgentService agentService = new AgentService();
}
