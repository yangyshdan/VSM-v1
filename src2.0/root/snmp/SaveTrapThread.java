package root.snmp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import com.huiming.base.jdbc.DataRow;
import com.huiming.service.alert.DeviceAlertService;

public class SaveTrapThread extends Thread{
	public void run(String ip,Map<String, String> traps){
		DataRow model = getTrapMSG(ip,traps);
		new DeviceAlertService().insertLogNoCheck(model);
	}
	
	public DataRow getTrapMSG(String ip, Map<String, String> traps){
		DataRow alert = null;
		DeviceAlertService service = new DeviceAlertService();
		if(traps.size() > 0){
			alert = new DataRow();
			DataRow source = service.findResourceType(ip);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			alert.set("ffirsttime", format.format(new Date()));
			alert.set("flasttime", format.format(new Date()));
			if(source.getString("type").equals("ds8k")){
				alert.set("flogtype", "0");
				alert.set("ftopid", source.getString("id"));
				alert.set("ftoptype", "Stroage");
				alert.set("ftopname", source.getString("name"));
				alert.set("fresourcename", source.getString("name"));
				alert.set("fresourceid",  source.getString("id"));
				alert.set("fresourcetype", "Storage");
				alert.set("fstate", "0");
				alert.set("fsourcetype", "SNMP");
				alert.set("fisforward", "0");
				for (String oid : traps.keySet()) {
					if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.1")){
						alert.set("fno", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.3")){
						alert.set("flevel", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.4")){
						alert.set("ffirsttime", format.format(traps.get(oid)));
					}else if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.5")){
						alert.set("fcount", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.6")){
						alert.set("flasttime", format.format(traps.get(oid)));
					}else if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.8")){
						alert.set("fruleid", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.10")){
						alert.set("fdescript", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.2.6.130.2.1.1.11")){
						alert.set("fdetail", traps.get(oid));
					}
				}
			}else if(source.getString("type").equals("ds5k")){
				alert.set("fno", "-1");
				alert.set("ftopid", source.getString("id"));
				alert.set("ftoptype", "Stroage");
				alert.set("ftopname", source.getString("name"));
				alert.set("fresourcename", source.getString("name"));
				alert.set("fresourceid",  source.getString("id"));
				alert.set("fcount", "1");
				alert.set("fstate", "0");
				alert.set("fsourcetype", "SNMP");
				alert.set("fisforward", "0");
				for (String oid : traps.keySet()) {
					if(oid.equals("1.3.6.1.6.3.1.1.4.1.0")){
						if(traps.get(oid).contains("1.3.6.1.4.1.789.1123.1.500.0.2")){
							alert.set("flevel", 2);
						}else{
							alert.set("flevel", 0);
						}
					}else if(oid.equals("1.3.6.1.4.1.789.1123.1.500.1.1.7")){
						alert.set("fdetail", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.789.1123.1.500.1.1.4")){
						alert.set("fresourcename", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.789.1123.1.500.1.1.5")){
						alert.set("fruleid", traps.get(oid));
						alert.set("fdescript", "DSSNMPTrap"+traps.get(oid));
					}
				}
			}else if(source.getString("type").equals("tape")){
				alert.set("fno", "-1");
				alert.set("flogtype", "0");
				alert.set("ftopid", "-1");
				alert.set("ftoptype", "TapeLibrary");
				alert.set("ftopname", "TS3500");
				alert.set("fresourcename", "TS3500");
				alert.set("fresourceid", "-1");
				alert.set("fresourcetype", "TrapLibrary");
				alert.set("fcount", "1");
				alert.set("fstate", "0");
				alert.set("fsourcetype", "SNMP");
				alert.set("fisforward", "0");
				for (String oid : traps.keySet()) {
					if(oid.equals("1.3.6.1.6.3.1.1.4.1.0")){
						alert.set("fruleid", traps.get(oid).replace("1.3.6.1.4.1.2.6.182.1.0.", ""));
						alert.set("fdescript", "ibm3584Trap"+traps.get(oid).replace("1.3.6.1.4.1.2.6.182.1.0.", ""));
					}else if(oid.equals("1.3.6.1.4.1.2.6.182.1.2.41.1")){
						alert.set("fno", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.2.6.182.1.2.71.1")){
						alert.set("fdetail", traps.get(oid));
					}else if(oid.equals("1.3.6.1.4.1.2.6.182.1.2.151.1")){
						switch (Integer.parseInt(traps.get(oid))) {
						case 1:
							alert.set("flevel", 0 );
							break;
						case 2:
							alert.set("flevel", 1 );
							break;
						case 3:
							alert.set("flevel", 2 );
							break;
						case 4:
							alert.set("flevel", 1 );
							break;
						case 5:
							alert.set("flevel", 0 );
							break;
						case 6:
							alert.set("flevel", 2 );
							break;
						case 7:
							alert.set("flevel", 0 );
							break;
						default:
							alert.set("flevel", 0 );
							break;
						}
					}
				}
			}
		}
		return alert;
	}
}
