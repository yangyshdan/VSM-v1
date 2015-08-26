package root.snmp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;

public class LoadSWMib {
	private Logger logger = LoggerFactory.getLogger(LoadSWMib.class);
	private Mib mib;
	public Mib getMib(){
		return mib;
	}
	
	public void setMib(Mib mib){
		this.mib = mib;
	}
	private LoadSWMib(){}
	
	private static LoadSWMib loadMib = null;
	
	public static LoadSWMib getLoadMib(){
		if(loadMib == null){
			loadMib = new LoadSWMib();
			loadMib.setMib(loadMib.getMibByName());
		}
		return loadMib;
	}
	
	private Mib getMibByName(){
		String path = this.getClass().getResource("/").getPath();
		path=path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"resource/mibs/SW.mib";
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
