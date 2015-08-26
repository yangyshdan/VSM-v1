package root.snmp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;

public class LoadDS8KMib {
	private Logger logger = LoggerFactory.getLogger(LoadDS8KMib.class);
	private Mib mib;
	public Mib getMib(){
		return mib;
	}
	
	public void setMib(Mib mib){
		this.mib = mib;
	}
	private LoadDS8KMib(){}
	
	private static LoadDS8KMib loadMib = null;
	
	public static LoadDS8KMib getLoadMib(){
		if(loadMib == null){
			loadMib = new LoadDS8KMib();
			loadMib.setMib(loadMib.getMibByName());
		}
		return loadMib;
	}
	
	private Mib getMibByName(){
		String path = this.getClass().getResource("/").getPath();
		path=path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"resource/mibs/2100Seas.mib";
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
