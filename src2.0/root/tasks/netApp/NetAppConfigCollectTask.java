package root.tasks.netApp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import netapp.manage.NaElement;
import netapp.manage.NaServer;
import org.apache.log4j.Logger;
import com.huiming.base.timerengine.Task;
import com.huiming.service.ddm.DdmService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.relationmap.RelationMapService;
import com.huiming.service.storagesystem.StorageSystemService;
import com.huiming.service.volume.VolumeService;
import com.project.sax.storage.NetAppConfigSax;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;
import com.project.web.WebConstants;

public class NetAppConfigCollectTask implements Task {
	
	private Logger logger = Logger.getLogger(this.getClass());
	public static final String NETAPP_7_MODE = "7-Mode";
	public static final String NETAPP_CLUSTER_MODE = "Cluster-Mode";
	StorageSystemService systemService = new StorageSystemService();
	VolumeService volumeService = new VolumeService();
	PoolService poolService = new PoolService();
	PortService portService = new PortService();
	DdmService ddmService = new DdmService();
	NodeService nodeService = new NodeService();
	RelationMapService relationMapService = new RelationMapService();

	public void execute() {
		StorageConfigService storageConfigService = new StorageConfigService();
		List<Info> storageInfos = storageConfigService.getStorageConfigList(WebConstants.STORAGE_TYPE_VAL_NETAPP);
		NaElement xi;
		NaElement xo;
		NaServer server;
		for (Info info : storageInfos) {
			try {
				server = new NaServer(info.getSystemName(), 1, 0);
				server.setAdminUser(info.getUsername(), info.getPassword());
				xi = new NaElement("system-get-version");
				xo = server.invokeElem(xi);
				String isClustered = xo.getChildContent("is-clustered");
				//Cluster-Mode
				if (isClustered != null && isClustered.equals("true")) {
					logger.info("The Storage System : " + info.getSystemName() + " is Cluster-Mode !");
					doCollect(info, NETAPP_CLUSTER_MODE);
				//7-Mode
				} else {
					logger.info("The Storage System : " + info.getSystemName() + " is 7-Mode !");
					doCollect(info, NETAPP_7_MODE);
				}
				server.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				System.exit(1);
			}
		}
	}
	
	/**
	 * 执行脚本,搜集配置信息
	 * @param info
	 * @param type
	 */
	public void doCollect(Info info,String type) {
		info.setState(1);
		String path = this.getClass().getResource("/").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
		String actPath = path.replace("apache-tomcat/webapps/vsm/", "") + "Perl\\bin\\perl.exe";
		String scriptPath = null;
		//判断存储类型,调用相应的脚本
		if (type.equals(NETAPP_7_MODE)) {
			scriptPath = path + "perl/netapp_7mode_block_config_collect.pl";
		} else if (type.equals(NETAPP_CLUSTER_MODE)) {
			scriptPath = path + "perl/netapp_clurmode_block_config_collect.pl";
		}
		//执行脚本
		String[] cmd = {actPath, scriptPath, info.getIpAddress(), info.getUsername(), info.getPassword()};
		String lineToRead = "";
		String lineToRead1 = "";
		int exitValue = 0;
		Process proc = null;
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
			proc.waitFor(); 
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
		//调用方法,解析XML文件
		if (exitValue == 0) {
			doSaveConfigInfo(info);
		}
	}
	
	/**
	 * 解析XML文件,获取配置信息
	 */
	public void doSaveConfigInfo(Info info) {
		try {
			String path = this.getClass().getResource("/").getPath();
			path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
			InputStream input = new FileInputStream(path + "perl/output/netapp_config_" + info.getIpAddress() + ".xml");
			NetAppConfigSax handler = new NetAppConfigSax();
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			//保存配置信息
			Integer systemId = Integer.parseInt(systemService.insertOrUpdateStorage(handler.getSystemList(), 0, WebConstants.STORAGE_TYPE_VAL_NETAPP));
			nodeService.updateStoragenodes(handler.getNodeList(), systemId);
			portService.updatePortInfo(handler.getPortList(), systemId);
			poolService.updatePoolInfo(handler.getPoolList(), systemId);
			//关联Pool和Volume
			for (int i = 0; i < handler.getVolumeList().size(); i++) {
				for (int j = 0; j < handler.getPoolAndVolumeList().size(); j++) {
					if (handler.getVolumeList().get(i).getString("display_name").indexOf(handler.getPoolAndVolumeList().get(j).getString("volume_name")) > -1) {
						handler.getVolumeList().get(i).set("pool_name", handler.getPoolAndVolumeList().get(j).getString("pool_name"));
					}
				}
			}
			volumeService.updateVolumeInfo(handler.getVolumeList(), systemId);
			ddmService.updateDDMInfo(handler.getDdmList(), systemId);
			relationMapService.updateVolumeAndPool(handler.getPoolAndVolumeList(), systemId);
			logger.info("It's success to collect the netApp storage config information !");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
