package root.tasks.svc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.task.BaseTask;
import com.huiming.base.util.security.AES;
import com.huiming.service.diskgroup.DiskgroupService;
import com.huiming.service.node.NodeService;
import com.huiming.service.port.PortService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.jeedsoft.license.License;
import com.jeedsoft.license.LicenseReader;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;
import com.project.storage.xmlread.DoRead;
import com.project.v7000.performance.PrfDiskGroup;
import com.project.v7000.performance.PrfPortAndNode;
import com.project.v7000.performance.PrfStorageVolume;
import com.project.web.SecurityAction;

public class V7000PerfCollectTask extends BaseTask{
	private static final String BASE_CMD = "cd /home/huiming/dumps/iostats;ls";
	private static final String SOURCE_PATH = "/home/huiming/dumps/iostats/";
	private static final String FILE_TYPE_NM = "Nm";
	private static final String FILE_TYPE_NN = "Nn";
	private static final String FILE_TYPE_NV = "Nv";
	private static final String TYPE_DISK = "DISK";
	private static final String TYPE_PORT = "PORT";
	private static final String TYPE_NODE = "NODE";
	private static final String TYPE_VOLUME = "VOLUME";
	private Logger logger = Logger.getLogger(this.getClass());
	PrfTimestampService timestampService = new PrfTimestampService();
	DiskgroupService diskgroupService = new DiskgroupService();
	PortService portService = new PortService();
	VolumeService volumeService = new VolumeService();
	NodeService nodeService = new NodeService();
	Connection connection = null;

	public void execute() {
		StorageConfigService service = new StorageConfigService();
		List<Info> storageInfos = service.getStorageConfigList(SrContant.DEVTYPE_VAL_SVC);
		String fileName = SecurityAction.class.getClassLoader().getResource("").getPath().replaceAll("%20", " ")+"/license.lic";
		License license = LicenseReader.read(fileName);
		int i = 1;
		for (Info info : storageInfos) {
			//执行采集
			if (i <= license.getMaxDeviceCount()) {
				onExecuteCollectAndConfig(info);
			}
			i++;
		}
	}
	
	/**
	 * 通过SCP将性能数据从/dumps/iostats目录写下来,解析文件,并将所采集的信息插入相应的数据库表
	 * @param info
	 */
	public void onExecuteCollectAndConfig(Info info) {
		try {
			//建立连接
			initConnection(info);
			
			//获取/dumps/iostats目录下的文件列表
			List<String> fileList = executeCommand(BASE_CMD);
			
			//建立文件路径
			String filePath = this.getClass().getResource("/").getPath();
			filePath = filePath.substring(1, filePath.indexOf("WEB-INF"));
			String oldFilePath = filePath.concat("svc/old");
			String newFilePath = filePath.concat("svc/new");
			
			//下载文件到指定目录
			downloadFiles(oldFilePath,newFilePath,fileList);
			
			//读取并解析文件,并插入数据库表
			onExecuteAddConfig(oldFilePath,newFilePath);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
	}
	
	/**
	 * 读取并解析文件,并插入到数据库表
	 * @param oldFilePath
	 * @param newFilePath
	 */
	public void onExecuteAddConfig(String oldFilePath, String newFilePath) {
		try {
			//用new和old文件夹区分待解析文件和已解析文件,通过比较决定new文件夹中哪些文件需要解析
			List<String> needToParseList = new ArrayList<String>();
			File[] newFiles = new File(newFilePath).listFiles();
			File[] oldFiles = new File(oldFilePath).listFiles();
			for (int i = 0; i < newFiles.length; i++) {
				File newFile = newFiles[i];
				if (oldFiles.length > 0) {
					for (int j = 0; j < oldFiles.length; j++) {
						File oldFile = oldFiles[j];
						if (newFile.getName().equals(oldFile.getName())) {
							break;
						} else if (j == (oldFiles.length - 1)) {
							needToParseList.add(newFile.getAbsolutePath());
						}
					}
				} else {
					needToParseList.add(newFile.getAbsolutePath());
				}
			}
			
			//将解析的数据存到相应的HashMap中
			HashMap<String, DataRow> nmTimeMap = new HashMap<String, DataRow>();
			HashMap<String, List<DataRow>> diskGroupMap = new HashMap<String, List<DataRow>>();
			HashMap<String, DataRow> nnTimeMap = new HashMap<String, DataRow>();
			HashMap<String, List<DataRow>> portMap = new HashMap<String, List<DataRow>>();
			HashMap<String, List<DataRow>> nodeMap = new HashMap<String, List<DataRow>>();
			HashMap<String, DataRow> nvTimeMap = new HashMap<String, DataRow>();
			HashMap<String, List<DataRow>> volumeMap = new HashMap<String, List<DataRow>>();
			
			//解析文件
			for (int i = 0; i < needToParseList.size(); i++) {
				String filePath = needToParseList.get(i);
				String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
				if (fileName.indexOf(FILE_TYPE_NM) > -1) {
					//Nm开头的文件,数据插入表(t_prf_diskgroup)
					PrfDiskGroup diskGroupHandler = (PrfDiskGroup) onGetParseDataHandler(FILE_TYPE_NM, filePath);
					String timestamp = diskGroupHandler.getTimestamp().getString(SrTblColConstant.TT_SAMPLE_TIME);
					nmTimeMap.put(timestamp,diskGroupHandler.getTimestamp());
					diskGroupMap.put(timestamp, diskGroupHandler.getDiskGroupList());
				} else if (fileName.indexOf(FILE_TYPE_NN) > -1) {
					//Nn开头的文件,数据插入表(t_prf_port,t_prf_storagenode)
					PrfPortAndNode portAndNodeHandler = (PrfPortAndNode) onGetParseDataHandler(FILE_TYPE_NN, filePath);
					String timestamp = portAndNodeHandler.getTimestamp().getString(SrTblColConstant.TT_SAMPLE_TIME);
					nnTimeMap.put(timestamp, portAndNodeHandler.getTimestamp());
					//For Port
					portMap.put(timestamp, portAndNodeHandler.getPortList());
					//For Storage Node
					nodeMap.put(timestamp, portAndNodeHandler.getStorageNodeList());
				} else if (fileName.indexOf(FILE_TYPE_NV) > -1) {
					//Nv开头的文件,数据插入表(t_prf_storagevolume)
					PrfStorageVolume volumeHandler = (PrfStorageVolume) onGetParseDataHandler(FILE_TYPE_NV, filePath);
					String timestamp = volumeHandler.getTimestamp().getString(SrTblColConstant.TT_SAMPLE_TIME);
					nvTimeMap.put(timestamp, volumeHandler.getTimestamp());
					volumeMap.put(timestamp, volumeHandler.getStorageVolumeList());
				}
			}
			
			//调用方法,执行插入数据到数据库表
			List<DataRow> diskGroupList = onExecAddTimeAndCalcDiff(nmTimeMap, diskGroupMap, TYPE_DISK);
			diskgroupService.addPrfDiskGroups(diskGroupList);
			List<DataRow> portList = onExecAddTimeAndCalcDiff(nnTimeMap ,portMap, TYPE_PORT);
			portService.addPrfPorts(portList);
			List<DataRow> nodeList = onExecAddTimeAndCalcDiff(nnTimeMap ,nodeMap, TYPE_NODE);
			nodeService.addPrfNodes(nodeList);
			List<DataRow> volumeList = onExecAddTimeAndCalcDiff(nvTimeMap ,volumeMap, TYPE_VOLUME);
			volumeService.addPrfVolumes(volumeList);
			
			//删除old文件夹的文件
			onExecuteDeleteFiles(oldFiles);
			//复制new文件夹的文件到old文件夹
			for (int i = 0; i < newFiles.length; i++) {
				File source = newFiles[i];
				File target = new File(oldFilePath + "/" + source.getName());
				onExecuteCopyFiles(source, target);
			}
			//删除new文件夹的文件
			onExecuteDeleteFiles(newFiles);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 插入数据到时间表,并计算性能差值
	 * @param timestampMap
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List<DataRow> onExecAddTimeAndCalcDiff(HashMap<String, DataRow> timestampMap, HashMap<String, List<DataRow>> listMap, String type) throws Exception {
		List<DataRow> resultList = null;
		Iterator iterator = timestampMap.entrySet().iterator();
		List<String> keyList = new ArrayList<String>();
		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			String key = (String) entry.getKey();
			keyList.add(key);
		}
		//至少有两个同一类型不同时间的文件
		if (keyList.size() > 1) {
			resultList = new ArrayList<DataRow>();
			String[] timestamps = SrContant.orderTimestamp(keyList);
			for (int i = 1; i < timestamps.length; i++) {
				String current = timestamps[i];
				String pref = timestamps[i-1];
				//插入时间表
				DataRow realTimeRow = timestampMap.get(current);
				realTimeRow.set(SrTblColConstant.TT_INTERVAL_LEN, SrContant.getTimeInterval(current, pref));
				realTimeRow.set(SrTblColConstant.TT_SUMM_TYPE, SrContant.SUMM_TYPE_REAL);
				realTimeRow.set(SrTblColConstant.TT_DEVICE_TYPE, SrContant.DEVTYPE_VAL_SVC);
				DataRow timeRow = timestampService.addPrfTimestamps(realTimeRow);
				
				//比较时间(找前一个时间的数据),计算各性能指标差值
				List<DataRow> listOne = listMap.get(current);
				List<DataRow> listTwo = listMap.get(pref);
				for (int k = 0; k < listOne.size(); k++) {
					DataRow rowOne = listOne.get(k);
					rowOne.set(SrTblColConstant.TT_SUBSYSTEM_ID, timeRow.getString(SrTblColConstant.TT_SUBSYSTEM_ID));
					rowOne.set(SrTblColConstant.TT_TIME_ID, timeRow.getString(SrTblColConstant.TT_TIME_ID));
					DataRow realRow = (DataRow) rowOne.clone();
					for (int l = 0; l < listTwo.size(); l++) {
						DataRow rowTwo = listTwo.get(l);
						//For Disk Group
						if (type.equals(TYPE_DISK)) {
							if (rowOne.getString(SrTblColConstant.DG_DISKGROUP_NAME).equals(rowTwo.getString(SrTblColConstant.DG_DISKGROUP_NAME))) {
								realRow.set(SrTblColConstant.DG_BCK_READ_IO, (rowOne.getLong(SrTblColConstant.DG_BCK_READ_IO) - rowTwo.getLong(SrTblColConstant.DG_BCK_READ_IO)));
								realRow.set(SrTblColConstant.DG_BCK_WRITE_IO, (rowOne.getLong(SrTblColConstant.DG_BCK_WRITE_IO) - rowTwo.getLong(SrTblColConstant.DG_BCK_WRITE_IO)));
								realRow.set(SrTblColConstant.DG_BCK_READ_KB, (rowOne.getLong(SrTblColConstant.DG_BCK_READ_KB) - rowTwo.getLong(SrTblColConstant.DG_BCK_READ_KB)));
								realRow.set(SrTblColConstant.DG_BCK_WRITE_KB, (rowOne.getLong(SrTblColConstant.DG_BCK_WRITE_KB) - rowTwo.getLong(SrTblColConstant.DG_BCK_WRITE_KB)));
								realRow.set(SrTblColConstant.DG_BCK_READ_TIME, (rowOne.getLong(SrTblColConstant.DG_BCK_READ_TIME) - rowTwo.getLong(SrTblColConstant.DG_BCK_READ_TIME)));
								realRow.set(SrTblColConstant.DG_BCK_WRITE_TIME, (rowOne.getLong(SrTblColConstant.DG_BCK_WRITE_TIME) - rowTwo.getLong(SrTblColConstant.DG_BCK_WRITE_TIME)));
							}
						}
						//For Port
						else if (type.equals(TYPE_PORT)) {
							if (rowOne.getString(SrTblColConstant.P_PORT_NAME).equals(rowTwo.getString(SrTblColConstant.P_PORT_NAME))) {
								realRow.set(SrTblColConstant.P_SEND_IO, (rowOne.getLong(SrTblColConstant.P_SEND_IO) - rowTwo.getLong(SrTblColConstant.P_SEND_IO)));
								realRow.set(SrTblColConstant.P_RECV_IO, (rowOne.getLong(SrTblColConstant.P_RECV_IO) - rowTwo.getLong(SrTblColConstant.P_RECV_IO)));
								realRow.set(SrTblColConstant.P_SEND_KB, (rowOne.getLong(SrTblColConstant.P_SEND_KB) - rowTwo.getLong(SrTblColConstant.P_SEND_KB)));
								realRow.set(SrTblColConstant.P_RECV_KB, (rowOne.getLong(SrTblColConstant.P_RECV_KB) - rowTwo.getLong(SrTblColConstant.P_RECV_KB)));
							}
						}
						//For Storage Node
						else if (type.equals(TYPE_NODE)) {
							if (rowOne.getString(SrTblColConstant.SN_SP_NAME).equals(rowTwo.getString(SrTblColConstant.SN_SP_NAME))) {
								realRow.set(SrTblColConstant.SN_READ_IO, (rowOne.getLong(SrTblColConstant.SN_READ_IO) - rowTwo.getLong(SrTblColConstant.SN_READ_IO)));
								realRow.set(SrTblColConstant.SN_WRITE_IO, (rowOne.getLong(SrTblColConstant.SN_WRITE_IO) - rowTwo.getLong(SrTblColConstant.SN_WRITE_IO)));
								realRow.set(SrTblColConstant.SN_READ_KB, (rowOne.getLong(SrTblColConstant.SN_READ_KB) - rowTwo.getLong(SrTblColConstant.SN_READ_KB)));
								realRow.set(SrTblColConstant.SN_WRITE_KB, (rowOne.getLong(SrTblColConstant.SN_WRITE_KB) - rowTwo.getLong(SrTblColConstant.SN_WRITE_KB)));
								realRow.set(SrTblColConstant.SN_READ_IO_TIME, (rowOne.getLong(SrTblColConstant.SN_READ_IO_TIME) - rowTwo.getLong(SrTblColConstant.SN_READ_IO_TIME)));
								realRow.set(SrTblColConstant.SN_WIRTE_IO_TIME, (rowOne.getLong(SrTblColConstant.SN_WIRTE_IO_TIME) - rowTwo.getLong(SrTblColConstant.SN_WIRTE_IO_TIME)));
							}
						}
						//For Storage Volume
						else if (type.equals(TYPE_VOLUME)) {
							if (rowOne.getString(SrTblColConstant.SV_VOLUME_NAME).equals(rowTwo.getString(SrTblColConstant.SV_VOLUME_NAME))) {
								realRow.set(SrTblColConstant.SV_READ_IO, (rowOne.getLong(SrTblColConstant.SV_READ_IO) - rowTwo.getLong(SrTblColConstant.SV_READ_IO)));
								realRow.set(SrTblColConstant.SV_WRITE_IO, (rowOne.getLong(SrTblColConstant.SV_WRITE_IO) - rowTwo.getLong(SrTblColConstant.SV_WRITE_IO)));
								realRow.set(SrTblColConstant.SV_READ_HIT_IO, (rowOne.getLong(SrTblColConstant.SV_READ_HIT_IO) - rowTwo.getLong(SrTblColConstant.SV_READ_HIT_IO)));
								realRow.set(SrTblColConstant.SV_WRITE_HIT_IO, (rowOne.getLong(SrTblColConstant.SV_WRITE_HIT_IO) - rowTwo.getLong(SrTblColConstant.SV_WRITE_HIT_IO)));
								realRow.set(SrTblColConstant.SV_READ_KB, (rowOne.getLong(SrTblColConstant.SV_READ_KB) - rowTwo.getLong(SrTblColConstant.SV_READ_KB)));
								realRow.set(SrTblColConstant.SV_WRITE_KB, (rowOne.getLong(SrTblColConstant.SV_WRITE_KB) - rowTwo.getLong(SrTblColConstant.SV_WRITE_KB)));
								realRow.set(SrTblColConstant.SV_READ_IO_TIME, (rowOne.getLong(SrTblColConstant.SV_READ_IO_TIME) - rowTwo.getLong(SrTblColConstant.SV_READ_IO_TIME)));
								realRow.set(SrTblColConstant.SV_WRITE_IO_TIME, (rowOne.getLong(SrTblColConstant.SV_WRITE_IO_TIME) - rowTwo.getLong(SrTblColConstant.SV_WRITE_IO_TIME)));
							}
						}
					}
					resultList.add(realRow);
				}
			}
		}
		return resultList;
	}
	
	/**
	 * 删除文件
	 * @param files
	 */
	public void onExecuteDeleteFiles(File[] files) {
		try {
			if (files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.exists()) {
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 复制文件
	 * @throws IOException 
	 */
	public void onExecuteCopyFiles(File source, File target) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			in = fis.getChannel();
			out = fos.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			fis.close();
			in.close();
			fos.close();
			out.close();
		}
	}
	
	
	/**
	 * 根据文件类型,解析文件并将
	 * @param fileType
	 * @param filePath
	 * @return
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public DefaultHandler onGetParseDataHandler(String fileType, String filePath) throws ParserConfigurationException, SAXException, IOException {
		DefaultHandler handler = null;
		//for disk group
		if (fileType.equals(FILE_TYPE_NM)) {
			handler = new PrfDiskGroup();
		//for port and storage node
		} else if (fileType.equals(FILE_TYPE_NN)) {
			handler = new PrfPortAndNode();
		//for storage volume
		} else if (fileType.equals(FILE_TYPE_NV)) {
			handler = new PrfStorageVolume();
		}
		InputStream input = new FileInputStream(filePath);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(input, handler);
		return handler;
	}
	
	/**
	 * 根据命令,用SCP下载文件到指定目录
	 * @param cmd
	 */
	public void downloadFiles(String oldFilePath, String newFilePath, List<String> list) {
		try {
			//创建old和new的文件夹
			File oldDir = new File(oldFilePath);
			oldDir.mkdir();
			File newDir = new File(newFilePath);
			newDir.mkdir();
			if (list.size() > 0) {
				String[] remoteFiles = new String[list.size()];
				SCPClient scpClient = new SCPClient(connection);
				for (int i = 0; i < list.size(); i++) {
					remoteFiles[i] = SOURCE_PATH.concat(list.get(i));
				}
				//批量下载文件
				scpClient.get(remoteFiles, newFilePath);
				logger.info("Download files success !");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			session = getConnSession();
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
	 * 建立连接
	 * @param info
	 * @return
	 */
	public void initConnection(Info info) {
		try {
			connection = new Connection(info.getIpAddress());
			connection.connect();
			boolean isAuth = connection.authenticateWithPassword(info.getUsername(), info.getPassword());
			logger.info("isAuth is : " + isAuth);
			if (isAuth) {
				logger.info("Login success !");
			} else {
				logger.info("Login failed !");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	/**
	 * 获取Session
	 * @return
	 */
	public Session getConnSession() {
		Session session = null;
		try {
			if (connection != null) {
				session = connection.openSession();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (session != null) {
				session.close();
			}
		}
		return session;
	}
}
