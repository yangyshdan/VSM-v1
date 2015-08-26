package root.tasks.license;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.log4j.Logger;
import com.huiming.base.task.BaseTask;
import com.jeedsoft.license.License;
import com.jeedsoft.license.LicenseReader;

/**
 * 该类主要是用于检查license是否过期,如果过期则停止服务器(Tomcat)
 * @author Administrator
 *
 */
public class CheckLicenseExpireDateTask extends BaseTask {
	
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 执行任务
	 */
	public void execute() {
		String rootPath = this.getClass().getResource("/").getPath().replaceAll("%20", " ");
		String licenseFile = rootPath + "license.lic";
		License license = LicenseReader.read(licenseFile);
		String tomcatPath = rootPath.substring(1,rootPath.indexOf("webapps")) + "bin";
		String stopTomcatFile = tomcatPath + "/shutdown.bat";
		try {
			//比较当前时间和license到期时间的大小
			Date licExpireDate = license.getExpireDate();
			Date systemDate = new Date();
			//已经过期
			if (systemDate.after(licExpireDate)) {
				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec(stopTomcatFile);
				BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				while ((line = input.readLine()) != null) {
					break;
				}
				input.close();
			} 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			System.exit(0);
		}
	}
}
