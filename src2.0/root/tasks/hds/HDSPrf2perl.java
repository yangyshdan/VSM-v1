package root.tasks.hds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;

import com.huiming.base.task.BaseTask;
import com.huiming.sr.constants.SrContant;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;

public class HDSPrf2perl extends BaseTask{

	private Logger logger = Logger.getLogger(this.getClass());
	
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
		String configScriptPath = path + "perl/hds_block_performance.pl";
		doScript(info, actPath, configScriptPath);
	}
	
	public void doScript(Info info,String actPath,String scriptPath) {
		String[] cmd = { actPath, scriptPath, info.getSubSystemID().toString(),
				info.getSystemName(), info.getUsername(), info.getPassword(), 
				info.getIpAddress(), info.getIp1Address(), info.getNativePath(), 
				info.getIsUpdateConfig().toString() };
		int exitValue = 0;
		Process proc = null;
		String lineToRead = "";
		try {
			proc = Runtime.getRuntime().exec(cmd);
			InputStream inputStream = proc.getErrorStream();
			BufferedReader bufferedRreader = new BufferedReader(new InputStreamReader(inputStream));
			while ((lineToRead = bufferedRreader.readLine()) != null) {
				logger.info(lineToRead);
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
			logger.info("Script runing success!");
		} else if (exitValue == 25) {
			logger.error("Script Abnormal exit:");
		}
	}  
}
