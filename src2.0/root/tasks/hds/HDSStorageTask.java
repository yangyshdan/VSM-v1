package root.tasks.hds;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import com.huiming.base.task.BaseTask;
import com.huiming.service.ddm.DdmService;
import com.huiming.service.diskgroup.DiskgroupService;
import com.huiming.service.hostgroup.HostgroupService;
import com.huiming.service.initial.InitializeService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.service.relationmap.RelationMapService;
import com.huiming.service.storagesystem.StorageSystemService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.project.sax.storage.StorageConfigInfo;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;

public class HDSStorageTask extends BaseTask{
	private Logger logger = Logger.getLogger(this.getClass());
	InitializeService initial = new InitializeService();
	StorageSystemService subse = new StorageSystemService();
	VolumeService volse = new VolumeService();
	PoolService poolse = new PoolService();
	DiskgroupService disksse = new DiskgroupService();
	PortService portse = new PortService();
	DdmService ddmse = new DdmService();
	NodeService nodese = new NodeService();
	HostgroupService hostse = new HostgroupService();
	RelationMapService relationse = new RelationMapService();
	PrfTimestampService timese = new PrfTimestampService();

	public void execute() {
		StorageConfigService service = new StorageConfigService();
		List<Info> storageInfos = service.getStorageConfigList(SrContant.DEVTYPE_VAL_HDS);
		for (Info info : storageInfos) {
			//执行采集
			sax(info);
		}
	}
	
	public void sax(Info info) {
		info.setState(1);
		String path = this.getClass().getResource("/").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
		String actPath = path.substring(0, path.indexOf("apache-tomcat/")) + "Perl/bin/perl.exe";
		String configScriptPath = path + "perl/hds_block_config.pl";
		doScript(info, actPath, configScriptPath);
	}
	
	public void doScript(Info info,String actPath,String scriptPath) {
		String[] cmd = { actPath, scriptPath, info.getSubSystemID().toString(),
				info.getSystemName(), info.getUsername(), info.getPassword(), 
				info.getIpAddress(), info.getIp1Address(), info.getNativePath(), 
				info.getIsUpdateConfig().toString() };
		String lineToRead = "";
		String lineToRead1 = "";
		int exitValue = 0;
		Process proc=null;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			InputStream inputStream = proc.getErrorStream();
			BufferedReader bufferedRreader = new BufferedReader(new InputStreamReader(inputStream));
			while ((lineToRead = bufferedRreader.readLine()) != null) {
				logger.info(lineToRead);
			}
			InputStream inputStream1 = proc.getInputStream();
			BufferedReader bufferedRreader1 = new BufferedReader(new InputStreamReader(inputStream1));
			while ((lineToRead1 = bufferedRreader1.readLine()) != null) {
				logger.info(lineToRead1);
			}
			proc.waitFor(); // wait for reading STDOUT and STDERR over
			exitValue = proc.exitValue();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				proc.getInputStream().close();
				proc.getErrorStream().close();
				proc.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (exitValue == 0) {
			info.setState(0);
			addStorageConfig(info.getSubSystemID());
		} else if (exitValue == 25) {
			info.setState(25);
			logger.error("Script Abnormal exit:");
		}
	}  
	
	/**
	 * 更新配置信息
	 * @param subSystemID
	 */
	public void addStorageConfig(Integer subSystemId){
		StorageConfigInfo handler = new StorageConfigInfo();
		try {
			String path = this.getClass().getResource("/").getPath();
			path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
			InputStream input = new FileInputStream(path+"perl/output/output_"+subSystemId+".xml");
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			//添加信息
			subse.insertOrUpdateStorage(handler.getSubsystem(),subSystemId,SrContant.DEVTYPE_VAL_HDS);
			poolse.updatePoolInfo(handler.getPool(), subSystemId);
			nodese.updateStoragenodes(handler.getStorageNodes(), subSystemId);
			disksse.updateDiskgroup(handler.getDiskgroups(), subSystemId);
			volse.updateVolumeInfo(handler.getVolumes(), subSystemId);
			portse.updatePortInfo(handler.getPorts(), subSystemId);
			hostse.updateHostgroup(handler.getHostgroups(), subSystemId);
			ddmse.updateDDMInfo(handler.getDdms(), subSystemId);
			relationse.updateDiskgroupAndDDM(handler.getDiskgroupAndDDM(), subSystemId);
			relationse.updateHostgroupAndVolume(handler.getHostgroupAndVolume(), subSystemId);
			relationse.updatePortAndHBA(handler.getPortAndHba(), subSystemId);
			relationse.updateHostgroupAndHBA(handler.getHostgroupAndHba(), subSystemId);
			relationse.updateDiskgroupAndPool(handler.getPoolAndDiskgroup(), subSystemId);
			relationse.updateVolumeAndPool(handler.getPoolAndVolume(), subSystemId);
			//添加磁盘组容量、速度、池ID
//			disksse.addDDMInfo(subSystemId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
