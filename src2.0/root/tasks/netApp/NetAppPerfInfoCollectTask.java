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
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.service.port.PortService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.service.storagesystem.StorageSystemService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.project.sax.performance.NetAppStoragePrfInfo;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;
import com.project.web.WebConstants;

public class NetAppPerfInfoCollectTask implements Task {
	
	private Logger logger = Logger.getLogger(this.getClass());
	public static final String NETAPP_7_MODE = "7-Mode";
	public static final String NETAPP_CLUSTER_MODE = "Cluster-Mode";
	PrfTimestampService timese = new PrfTimestampService();
	StorageSystemService systemService = new StorageSystemService();
	VolumeService volumeService = new VolumeService();
	PortService portService = new PortService();

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
	 * 执行脚本,搜集性能信息
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
			scriptPath = path + "perl/netapp_7mode_block_perf_collect.pl";
		} else if (type.equals(NETAPP_CLUSTER_MODE)) {
			scriptPath = path + "perl/netapp_clurmode_block_perf_collect.pl";
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
			doSavePerfInfo(info);
		}
	}
	
	/**
	 * 解析XML文件,获取性能信息
	 */
	public void doSavePerfInfo(Info info) {
		try {
			String path = this.getClass().getResource("/").getPath();
			path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
			InputStream input = new FileInputStream(path + "perl/output/netapp_perf_" + info.getIpAddress() + ".xml");
			NetAppStoragePrfInfo handler = new NetAppStoragePrfInfo();
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			if (handler.getTime() != null && handler.getTime().size() > 0) {
				DataRow realTimeRow = handler.getTime().get(0);
				realTimeRow.set(SrTblColConstant.TT_SUMM_TYPE, SrContant.SUMM_TYPE_REAL);
				realTimeRow.set(SrTblColConstant.TT_DEVICE_TYPE, WebConstants.STORAGE_TYPE_VAL_NETAPP);
				DataRow timeRow = timese.addPrfTimestamps(realTimeRow);
				String timeId = timeRow.getString(SrTblColConstant.TT_TIME_ID);
				String systemId = timeRow.getString(SrTblColConstant.TT_SUBSYSTEM_ID);
				List<DataRow> systemList = handler.getSystem();
				for (int i = 0; i < systemList.size(); i++) {
					systemList.get(i).set(SrTblColConstant.TT_SUBSYSTEM_ID, systemId);
					systemList.get(i).set(SrTblColConstant.TT_TIME_ID, timeId);
				}
				List<DataRow> volumeList = handler.getVolume();
				for (int i = 0; i < volumeList.size(); i++) {
					volumeList.get(i).set(SrTblColConstant.TT_SUBSYSTEM_ID, systemId);
					volumeList.get(i).set(SrTblColConstant.TT_TIME_ID, timeId);
				}
				List<DataRow> portList = handler.getPort();
				for (int i = 0; i < portList.size(); i++) {
					portList.get(i).set(SrTblColConstant.TT_SUBSYSTEM_ID, systemId);
					portList.get(i).set(SrTblColConstant.TT_TIME_ID, timeId);
				}
				//插入数据到数据库表
				systemService.addPrfSystems(systemList);
				volumeService.addPrfVolumes(volumeList);
				portService.addPrfPorts(portList);
				logger.info("It's success to collect the netApp storage performance information !");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
