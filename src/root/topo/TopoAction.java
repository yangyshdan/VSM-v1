package root.topo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.topo.TopoService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;

public class TopoAction extends SecurityAction {
	BaseprfService baseService = new BaseprfService();
	TopoService service=new TopoService();
	public ActionResult doTopoPage(){
		//交换机
		List<DataRow> Switches=service.getSwitchList();
		JSONArray switchArray = new JSONArray();
		for(int i=0;i<Switches.size();i++){
			JSONObject switchObject = new JSONObject();
			switchObject.put("switchid", Switches.get(i).getInt("switch_id"));
			switchObject.put("logicalname", Switches.get(i).getString("logical_name"));
			switchArray.add(switchObject);
		}
		this.setAttribute("switchArray", switchArray);
		
		//存储
		List<DataRow> storages=service.getStorageList();
		JSONArray storageArray = new JSONArray();
		for(int i=0;i<storages.size();i++){
			JSONObject storageObject = new JSONObject();
			storageObject.put("sid", storages.get(i).getInt("subsystem_id"));
			storageObject.put("sname", storages.get(i).getString("display_name"));
			storageArray.add(storageObject);
		}
		this.setAttribute("storage1Array", storageArray);
		//服务器
		List<DataRow> servers=service.getServerList();
		JSONArray serverArray = new JSONArray();
		for(int i=0;i<servers.size();i++){
			JSONObject serverObject = new JSONObject();
			serverObject.put("hid", servers.get(i).getInt("server_id"));
			serverObject.put("hname", servers.get(i).getString("server_name"));
			serverArray.add(serverObject);
		}
		this.setAttribute("hostArray", serverArray);
		//存储和交换机
		List<DataRow> Storage=service.getSwitchandStorageList();
		JSONArray Array = new JSONArray();
		for(DataRow storage:Storage){
			JSONArray storage1Array = new JSONArray();
			JSONArray switch1Array = new JSONArray();
			JSONObject storageObject= new JSONObject();
			for(int x=0;x<storages.size();x++){
				if(storages.get(x).getInt("subsystem_id")==storage.getInt("subsystem_id")){
					storage1Array.add(storage.getInt("subsystem_id"));
				}
			}
			for(int y=0;y<Switches.size();y++){
				if(Switches.get(y).getInt("switch_id")==storage.getInt("switch_id")){
					switch1Array.add(storage.getInt("switch_id"));
				}
			}
			storageObject.put("storid", storage1Array);
			storageObject.put("switid", switch1Array);
			Array.add(storageObject);
		}
		this.setAttribute("SwiandStorArray", Array);
		//交换机和交换机
		List<DataRow> SwitchandSwitchList=service.getSwitchandSwitchList();
		JSONArray Arrays = new JSONArray();
		for(DataRow switchandswitch:SwitchandSwitchList){
			JSONArray switch1Array = new JSONArray();
			JSONArray switch2Array = new JSONArray();
			JSONObject switch1Object = new JSONObject();
			JSONObject switch2Object= new JSONObject();
			for(int x=0;x<Switches.size();x++){
				if(Switches.get(x).getInt("switch_id")==switchandswitch.getInt("switch_id1")){
					switch1Array.add(switchandswitch.getInt("switch_id1"));
				}
			}
			for(int y=0;y<Switches.size();y++){
				if(Switches.get(y).getInt("switch_id")==switchandswitch.getInt("switch_id2")){
					switch2Array.add(switchandswitch.getInt("switch_id2"));

				}
			}
			switch1Object.put("switch1", switch1Array);
			switch1Object.put("switch2", switch2Array);
			Arrays.add(switch1Object);
		}
		this.setAttribute("SwitandSwitArray", Arrays);
		//交换机和服务器
		List<DataRow> SwitchandHostList=service.getSwitchandHostList();
		JSONArray Array1 = new JSONArray();
		for(DataRow SwitchandHost:SwitchandHostList){
			JSONArray switch1Array = new JSONArray();
			JSONArray host1Array = new JSONArray();
			JSONObject Object1= new JSONObject();
			for(int x=0;x<Switches.size();x++){
				if(Switches.get(x).getInt("switch_id")==SwitchandHost.getInt("switch_id")){
					switch1Array.add(SwitchandHost.getInt("switch_id"));
				}
			}
			for(int y=0;y<servers.size();y++){
				if(servers.get(y).getInt("server_id")==SwitchandHost.getInt("server_id")){
					host1Array.add(SwitchandHost.getInt("server_id"));

				}
			}
			Object1.put("switch1", switch1Array);
			Object1.put("host1", host1Array);
			Array1.add(Object1);
		}
		this.setAttribute("HostandSwitArray", Array1);
		return new ActionResult("/WEB-INF/views/topo/topo.jsp");
	}
} 
