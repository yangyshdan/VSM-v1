package root.snmp;

import java.io.File;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;

public class LoadEmcMib {
	private Mib mib;
	public Mib getMib(){
		return mib;
	}
	
	public void setMib(Mib mib){
		this.mib = mib;
	}
	private LoadEmcMib(){}
	
	private static LoadEmcMib loadMib = null;
	
	public static LoadEmcMib getLoadMib(){
		if(loadMib == null){
			loadMib = new LoadEmcMib();
			loadMib.setMib(loadMib.getMibByName());
		}
		return loadMib;
	}
	
	private Mib getMibByName(){
		String path = this.getClass().getResource("/").getPath();
		path=path.substring(1, path.indexOf("WEB-INF/classes")).replaceAll("%20", "\" \"")+"resource/mibs/CLARIION-MIB.mib";
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
