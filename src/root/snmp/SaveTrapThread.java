package root.snmp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibValueSymbol;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.AlertService;

public class SaveTrapThread extends Thread{
	public void run(String ip,Map<String, String> traps){
		DataRow model = getTrapMSG(traps);
		AlertService service = new AlertService();
		service.insertAlert(model);
	}
	
	public DataRow getTrapMSG(Map<String, String> traps){
		DataRow alert = null;
		if(traps.size() > 0){
			alert = new DataRow();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			alert.put("ftimestamp", format.format(new Date()));
			Mib mib = null;
			LoadEmcMib loadMib = LoadEmcMib.getLoadMib();
			mib = loadMib.getMib();
			for (String oid : traps.keySet()) {
				MibValueSymbol sym = null;
				try {
					if(!oid.equalsIgnoreCase("trapType")){
						sym = mib.getSymbolByOid(oid);
					}else{
						if(traps.get("trapType").equals("2")){
							alert.put("fseverity", "notice");
						}else if(traps.get("trapType").equals("3")){
							alert.put("fseverity", "info");
						}else if(traps.get("trapType").equals("4")){
							alert.put("fseverity", "warning");
						}else if(traps.get("trapType").equals("5")){
							alert.put("fseverity", "error");
						}else if(traps.get("trapType").equals("6")){
							alert.put("fseverity", "critical");
						}
					}
				} catch (Exception e) {
					sym = null;
				}
				if(sym != null){
				    String mibName = sym.getName();
				    if(StringHelper.isNotEmpty(mibName)){
				    	if(mibName.equalsIgnoreCase("deviceType")){
				    		alert.put("fdevicetype", traps.get(oid));
				    	}else if(mibName.equalsIgnoreCase("hostName")){
				    		alert.put("fhostname", traps.get(oid));
				    	}else if(mibName.equalsIgnoreCase("deviceID")){
				    		alert.put("fdeviceid", traps.get(oid));
				    	}else if(mibName.equalsIgnoreCase("eventID")){
				    		alert.put("feventid", traps.get(oid));
				    	}else if(mibName.equalsIgnoreCase("storageSystem")){
				    		alert.put("fstoragename", traps.get(oid));
					    }else if(mibName.equalsIgnoreCase("eventtext")){
				    		alert.put("feventtext", traps.get(oid));
					    }
				    }
				}
			}
		}
		return alert;
	}
}
