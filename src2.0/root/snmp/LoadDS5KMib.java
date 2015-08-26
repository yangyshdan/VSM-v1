package root.snmp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;

public class LoadDS5KMib {
	private Logger logger = LoggerFactory.getLogger(LoadDS5KMib.class);
	private Mib mib;
	public Mib getMib(){
		return mib;
	}
	
	public void setMib(Mib mib){
		this.mib = mib;
	}
	private LoadDS5KMib(){}
	
	private static LoadDS5KMib loadMib = null;
	
	public static LoadDS5KMib getLoadMib(){
		if(loadMib == null){
			loadMib = new LoadDS5KMib();
			loadMib.setMib(loadMib.getMibByName());
		}
		return loadMib;
	}
	
	private Mib getMibByName(){
		logger.debug("into the getMibByName methord");
		String path = this.getClass().getResource("/").getPath();
		path=path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"resource/mibs/SM10_R2.MIB";
//		path=path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"MIB_STORAGE/SM10_R2.MIB";
		logger.debug("the path is :----"+ path);
		MibLoader loader = new MibLoader();
		Mib mib = null;
			File file = new File(path);
			try {
				loader.addDir(file.getParentFile());
				mib = loader.load(file);
			} catch (Exception e) {
				logger.debug("error:"+e.getMessage()); 
				e.printStackTrace();
			} 
			logger.debug("write the mib :" + mib.getName());
			return mib;
	}
}
