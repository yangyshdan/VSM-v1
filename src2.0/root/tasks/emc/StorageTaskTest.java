package root.tasks.emc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import com.huiming.base.timerengine.Task;
import com.huiming.service.relationmap.RelationMapService;
import com.huiming.service.ddm.DdmService;
import com.huiming.service.diskgroup.DiskgroupService;
import com.huiming.service.hostgroup.HostgroupService;
import com.huiming.service.initial.InitializeService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.service.storagehbaservice.StoragehbaService;
import com.huiming.service.storagesystem.StorageSystemService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.project.sax.storage.StorageConfigInfo;
import com.project.sax.performance.StoragePrfInfo;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;

/**
 * @author Administrator
 *
 */
public class StorageTaskTest implements Task {
	private Logger logger = Logger.getLogger(this.getClass());
	InitializeService initial = new InitializeService();
	StorageSystemService subse = new StorageSystemService();
	VolumeService volse = new VolumeService();
	PoolService poolse = new PoolService();
	DiskgroupService disksse = new DiskgroupService();
	PortService portse = new PortService();
	DdmService ddmse = new DdmService();
	NodeService nodese = new NodeService();
	StoragehbaService hbase = new StoragehbaService();
	HostgroupService hostse = new HostgroupService();
	RelationMapService relationse = new RelationMapService();
	PrfTimestampService timese = new PrfTimestampService();
	
	public void execute() {
		StorageConfigService service = new StorageConfigService();
		List<Info> storageInfos = service.getStorageConfigList(SrContant.DEVTYPE_VAL_EMC);
		int i = 1;
		for (Info info : storageInfos) {
			if (!initial.isExsistSystem(info.getSubSystemID(), info.getSystemName())) {
				SrContant.TASK_COUNT = 1;
			}
			sax(info);
			Integer subSystemID = info.getSubSystemID();
//			updateStorageConfig(subSystemID);
			if (SrContant.TASK_COUNT == 1) {
				addStorageConfig(subSystemID);
			}
			if (SrContant.TASK_COUNT == 2 && info.getState() != 25) {
				if(info.getIsUpdateConfig() == 1){
					//更新配置信息
					updateStorageConfig(subSystemID);
					info.setIsUpdateConfig(0);
					if (i == storageInfos.size()) {
						SrContant.isUpdateConfig = 0;
					}
				}
				addStoragePrf(subSystemID);
			}
			i++;
		}
		SrContant.TASK_COUNT = 2;
	}

	public void sax(Info info) {
		info.setState(1);
		String path = this.getClass().getResource("/").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
		String actPath = path.replace("apache-tomcat/webapps/vsm/", "") + "Perl/bin/perl.exe";
		String configScriptPath = path + "perl/vnx_block_configuration_collect.pl";
		String prefScriptPath = path + "perl/vnx_block_performance_collect.pl";
		logger.info("path:"+path);
		logger.info("actpath:"+actPath);
		logger.info("configscriptpath:"+configScriptPath);
		logger.info("prefscriptpath:"+prefScriptPath);
		SrContant.CONFIGURATION = new File(path + "perl/" + info.getIpAddress() + "_configuration.xml");
		SrContant.PERFORMANCE = new File(path + "perl/" + info.getIpAddress() + "_performance.xml");
		if (SrContant.TASK_COUNT == 1) {
			doScript(info, actPath, configScriptPath, SrContant.CONFIGURATION);
		} else {
			doScript(info, actPath, prefScriptPath, SrContant.PERFORMANCE);
			info.setIsUpdateConfig(SrContant.isUpdateConfig);
			if (info.getIsUpdateConfig() == 1) {
				doScript(info, actPath, configScriptPath, SrContant.CONFIGURATION);
			}
		}
		//doScript(info,actPath,configScriptPath,SrContant.CONFIGURATION);
	}
	
	/**
	 * 
	 * @param info  存储系统信息
	 * @param actPath perl环境路径
	 * @param scriptPath  脚本路径
	 * @param file  接收数据文件
	 */
	public void doScript(Info info,String actPath,String scriptPath,File file){
		String[] cmd = {actPath,scriptPath, info.getUsername(),info.getPassword(), info.getIpAddress(), info.getNativePath(),info.getSystemName(),info.getIsUpdateConfig().toString()};
		logger.info(info.getUsername()+","+info.getPassword()+","+info.getIpAddress()+","+info.getNativePath()+","+info.getSystemName()+","+info.getIsUpdateConfig().toString());
		StringBuffer resultStringBuffer = new StringBuffer();
		String lineToRead = "";
		int exitValue = 0;
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			InputStream inputStream = proc.getInputStream();
			BufferedReader bufferedRreader = new BufferedReader(new InputStreamReader(inputStream));
//			if ((lineToRead = bufferedRreader.readLine()) != null) {
//				// resultStringBuffer.append(lineToRead);
//			}
			while ((lineToRead = bufferedRreader.readLine()) != null) {
				resultStringBuffer.append(lineToRead);
				resultStringBuffer.append("\r\n");
				logger.info("res::"+lineToRead);
			}
			proc.waitFor(); // wait for reading STDOUT and STDERR over
			exitValue = proc.exitValue();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				proc.getInputStream().close();
				proc.getErrorStream().close();
				proc.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (exitValue == 0) {
			RandomAccessFile mm = null;
			try {
				if(file.exists()){
					file.delete();
				}
				file.createNewFile();
				mm = new RandomAccessFile(file, "rw");
				mm.write(resultStringBuffer.toString().getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (mm != null) {
					try {
						mm.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
			}
		} else if (exitValue == 25) {
			info.setState(25);
//			StringBuffer errorStringBuffer = new StringBuffer();
//			String errorLine = "";
//			Process errorProc=null;
//			try {
//				errorProc = Runtime.getRuntime().exec(cmd);
//				InputStream input = errorProc.getErrorStream();
//				BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
//				while((errorLine=bfr.readLine())!=null){
//					errorStringBuffer.append(errorLine);
//				}
//				errorProc.waitFor();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}finally{
//				try {
//					errorProc.getErrorStream().close();
//					errorProc.getInputStream().close();
//					errorProc.getOutputStream().close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
			logger.error("Script Abnormal exit:");
			// 这里添加日志
		}
	}
	
	/**
	 * 添加配置信息
	 * @param subSystemID
	 */
	public void addStorageConfig(Integer subSystemID){
		StorageConfigInfo handler = new StorageConfigInfo();
		try {
			InputStream input = new FileInputStream(SrContant.CONFIGURATION);
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			//添加信息
			subse.addStorage(handler.getSubsystem(),subSystemID,SrContant.DEVTYPE_VAL_EMC);
			poolse.addPool(handler.getPool(), subSystemID);
			nodese.addStoragenodes(handler.getStorageNodes(), subSystemID);
			disksse.addDiskGroup(handler.getDiskgroups(), subSystemID);
			volse.addVolume(handler.getVolumes(), subSystemID);
			hbase.addStoragehbas(handler.getHbas(), subSystemID);
			portse.addPort(handler.getPorts(), subSystemID);
			hostse.addHostgroup(handler.getHostgroups(), subSystemID);
			ddmse.addDdm(handler.getDdms(), subSystemID);
			relationse.addDiskgroupAndDdm(handler.getDiskgroupAndDDM(), subSystemID);
			relationse.addHostgroupAddVolume(handler.getHostgroupAndVolume(), subSystemID);
			relationse.addPortAndHBAinfo(handler.getPortAndHba(), subSystemID);
			relationse.addHostgroupAndHBA(handler.getHostgroupAndHba(), subSystemID);
			relationse.addDiskgroupAndPool(handler.getPoolAndDiskgroup(), subSystemID);
			relationse.addVolumeAndPool(handler.getPoolAndVolume(), subSystemID);
			//添加磁盘组容量、速度、池ID
			disksse.addDDMInfo(subSystemID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新配置信息
	 * @param subsystemId
	 */
	public void updateStorageConfig(Integer subsystemId){
		StorageConfigInfo handler = new StorageConfigInfo();
		try {
			InputStream input = new FileInputStream(SrContant.CONFIGURATION);
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			subse.updateStorage(handler.getSubsystem(), subsystemId, SrContant.DEVTYPE_VAL_EMC);
			poolse.updatePoolInfo(handler.getPool(), subsystemId);
			nodese.updateStoragenodes(handler.getStorageNodes(), subsystemId);
			disksse.updateDiskgroup(handler.getDiskgroups(), subsystemId);
			volse.updateVolumeInfo(handler.getVolumes(), subsystemId);
			hbase.updateStoragehba(handler.getHbas(), subsystemId);
			portse.updatePortInfo(handler.getPorts(), subsystemId);
			hostse.updateHostgroup(handler.getHostgroups(), subsystemId);
			ddmse.updateDDMInfo(handler.getDdms(), subsystemId);
			relationse.updateDiskgroupAndDDM(handler.getDiskgroupAndDDM(), subsystemId);
			relationse.updateHostgroupAndVolume(handler.getHostgroupAndVolume(), subsystemId);
			relationse.updatePortAndHBA(handler.getPortAndHba(), subsystemId);
			relationse.updateHostgroupAndHBA(handler.getHostgroupAndHba(), subsystemId);
			relationse.updateDiskgroupAndPool(handler.getPoolAndDiskgroup(), subsystemId);
			relationse.updateVolumeAndPool(handler.getPoolAndVolume(), subsystemId);
			//添加磁盘组容量、速度、池ID
			disksse.addDDMInfo(subsystemId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加性能信息
	 * @param subsystemID
	 */
	public void addStoragePrf(Integer subsystemID){
		StoragePrfInfo handler = new StoragePrfInfo();
		try {
			InputStream input = new FileInputStream(SrContant.PERFORMANCE);
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			timese.addPrfTimestamp(handler.getTime(), subsystemID, SrContant.DEVTYPE_VAL_EMC);
			disksse.addprfDiskGroup(handler.getDiskgroup(), subsystemID);
			portse.addprfPort(handler.getPort(), subsystemID);
			volse.addprfVolume(handler.getVolume(), subsystemID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
