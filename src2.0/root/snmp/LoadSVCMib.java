package root.snmp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;

public class LoadSVCMib {
	private Logger logger = LoggerFactory.getLogger(LoadSVCMib.class);
	private Mib mib;
	public Mib getMib(){
		return mib;
	}
	
	public void setMib(Mib mib){
		this.mib = mib;
	}
	private LoadSVCMib(){}
	
	private static LoadSVCMib loadMib = null;
	
	public static LoadSVCMib getLoadMib(){
		if(loadMib == null){
			loadMib = new LoadSVCMib();
			loadMib.setMib(loadMib.getMibByName());
		}
		return loadMib;
	}
	
	private Mib getMibByName(){
		String path = this.getClass().getResource("/").getPath();
		path=path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"resource/mibs/SVC_MIB_6.3.0.MIB";
//		path=path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"MIB_FOS_v6.4/SW.mib";
		MibLoader loader = new MibLoader();
		Mib mib = null;
			File file = new File(path);
			try {
				loader.addDir(file.getParentFile());
				mib = loader.load(file);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return mib;
	}
}
