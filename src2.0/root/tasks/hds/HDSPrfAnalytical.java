package root.tasks.hds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.task.BaseTask;
import com.huiming.service.diskgroup.DiskgroupService;
import com.huiming.service.port.PortService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.project.sax.performance.StoragePrfInfo;
import com.project.service.StorageConfigService;
import com.project.storage.entity.Info;

public class HDSPrfAnalytical extends BaseTask{
	private Logger logger = Logger.getLogger(this.getClass());
	PrfTimestampService timese = new PrfTimestampService();
	DiskgroupService disksse = new DiskgroupService(); 
	PortService portse = new PortService();
	VolumeService volse = new VolumeService();   

	
	public void execute() {
		StorageConfigService service = new StorageConfigService();
		List<Info> storageInfos = service.getStorageConfigList(SrContant.DEVTYPE_VAL_HDS);
		for (Info info : storageInfos) {
			sax(info);
		}
	}
	
	public void sax(Info info) {
		info.setState(1);
		String path = this.getClass().getResource("/").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
		String actPath = path.substring(0, path.indexOf("apache-tomcat/"))+"Perl/bin/perl.exe";
		String configScriptPath = path+"perl/hds_performance_analytical.pl";
		doScript(info,actPath,configScriptPath);
	}
	
	public void doScript(Info info,String actPath,String scriptPath){
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
			try {
				String file = getData(info.getSubSystemID());
				if(!file.equals("pfm20000.xml")){
					addStoragePrf(file, info.getSubSystemID());
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else if (exitValue == 25) {
			info.setState(25);
			logger.error("Script Abnormal exit:");
		}
	}  
	
	private String getData(int id) {  
		String path = this.getClass().getResource("/").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
		String configScriptPath= path+"perl/output/"+id;
        File f=new File(configScriptPath);  
        java.text.DecimalFormat df = new java.text.DecimalFormat("00000");
        int min = 20000;
        if (f.isDirectory()) {  
            File[] fs=f.listFiles();  
            for (int i=0;i<fs.length;i++) {  
            	String name = fs[i].getName();
            	if(name.endsWith(".xml")){
            		int temp = Integer.parseInt(name.replace("pfm", "").replace(".xml", ""));
            		if(temp < min){
            			min = temp;
            		}
            	}
            }  
        } 
        return "pfm"+df.format(min)+".xml";  
    }

	/**
	 * 添加性能信息
	 * @param subsystemID
	 */
	public void addStoragePrf(String filename,Integer id){
		String path = this.getClass().getResource("/").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"");
		String configScriptPath= path+"perl/output/"+id+"/"+filename;
		StoragePrfInfo handler = new StoragePrfInfo();
		try {
			InputStream input = new FileInputStream(configScriptPath);
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			timese.addPrfTimestamp(handler.getTime(), id, SrContant.DEVTYPE_VAL_HDS);
			disksse.addprfDiskGroup(handler.getDiskgroup(), id);
			portse.addprfPort(handler.getPort(), id);
			volse.addprfVolume(handler.getVolume(), id);
			new File(configScriptPath).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		String configScriptPath= "E:/openstack/output/1/pfm00001.xml";
		StoragePrfInfo handler = new StoragePrfInfo();
		try {
			InputStream input = new FileInputStream(configScriptPath);
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, handler);
			for (DataRow disk : handler.getVolume()) {
				System.out.println(disk);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
