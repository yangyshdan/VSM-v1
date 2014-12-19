package root.node;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.AlertService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.node.NodeService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class NodeAction extends SecurityAction{
	BaseprfService baseService = new BaseprfService();
	NodeService service = new NodeService();
	public ActionResult doNodePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		page = service.getNodePage(curPage, numPerPage, null, null, null, subsystemId);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("nodePage", page);
		//性能曲线
		//doNodePrfField();
		return new ActionResult("/WEB-INF/views/node/nodeList.jsp");
	}
	
	public ActionResult doAjaxNodePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String componentId = getStrParameter("componentId");
		page = service.getNodePage(curPage, numPerPage, name, ipAddress, componentId, subsystemId);
		this.setAttribute("name", name);
		this.setAttribute("ipAddress", ipAddress);
		this.setAttribute("componentId", componentId);
		this.setAttribute("nodePage", page);
		this.setAttribute("subSystemID", subsystemId);
		return new ActionResult("/WEB-INF/views/node/ajaxNode.jsp");
	}
	
	public ActionResult doNodeInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer nodeId = getIntParameter("nodeId");
		DataRow row = service.getNodeInfo(nodeId);
		this.setAttribute("nodeInfo", row);
		this.setAttribute("subSystemID", subsystemId);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1,subsystemId.toString(),null, nodeId.toString(), null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		//doNodePrfField();
		return new ActionResult("/WEB-INF/views/node/nodeInfo.jsp");
	}
	
	public ActionResult doNodePrfPage(){
		//性能曲线
		doNodePrfField();
		this.setAttribute("nodeId", getIntParameter("nodeId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/node/ajaxPrfNode.jsp");
		}
		return new ActionResult("/WEB-INF/views/node/prefNodePage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doNodeSettingPrf(){
		Integer devId = getIntParameter("nodeId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "Node");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_redundancy", "the_display_name", "redundancy_id","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Node", row.getString("type"),subsystemId,devId);
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Node", row.getString("type"),subsystemId,null));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/node/NodeAction?func=NodePrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	public void doNodePrfField(){
		Integer devId = getIntParameter("nodeId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String tablePage = getStrParameter("tablePage");
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Node", row.getString("type"),subsystemId,devId);
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_redundancy", devId, row.getString("type"), "Node", "redundancy_id", "the_display_name");
			if(row.getString("type").equals("SVC")){
				dataRow.set("fprfid", "'A539','A545'");
				dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
			}
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "redundancy_id", "the_display_name", "v_res_redundancy");
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			thead.set("prf_timestamp", "时间");
			thead.set("ele_name", "设备名");
			if(devs!=null && devs.size()==1){
				for (DataRow r : kpis) {
					thead.set(r.getString("fid"), r.getString("ftitle"));
				}
				tbody = baseService.getPrfDatas(curPage,numPerPage,devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
			}
			if(tablePage ==null || tablePage.length()==0){
				JSONArray array = baseService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
				json.put("series", array);
			}
			json.put("legend", dataRow.getInt("flegend")==1?true:false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("thead", thead);
			json.put("tbody", tbody);
			json.put("kpiInfo", kpis);
		}
		this.setAttribute("prfData", json);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
	}
	
	public void doNodePrf(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de,"multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
		StringBuffer kpi = new StringBuffer();
		for (int i = 0;i<kpis.length;i++) {
			kpi.append("'"+kpis[i]+"'");
			if(i<kpis.length-1){
				kpi.append(",");
			}
		}
		String dev = "";
		if(devices!=null && devices.length>0){
			StringBuffer device = new StringBuffer();
			for (int i = 0;i<devices.length;i++) {
				device.append(devices[i]);
				if(i<devices.length-1){
					device.append(",");
				}
			}
			dev = device.toString();
		}else{
			dev = devId.toString();
		}
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		Integer legend = getIntParameter("legend");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname","Node");
        row.set("fdevicetype",storageType);
        row.set("fdevice",dev);
        row.set("fprfid",kpi.toString());
        row.set("fisshow",1);
        List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString()); 
        if(units !=  null && units.size()>0){
        	Set<String> set = new HashSet<String>();
        	for (DataRow unit : units) {
        		if(StringHelper.isNotEmpty(unit.getString("funits")))
        			set.add(unit.getString("funits"));
			}
        	String tempStr = set.toString().replace("[", "").replace("]", "");
        	row.set("fyaxisname", tempStr.length()>40?tempStr.substring(0, 37)+"...":tempStr);
        }else{
        	row.set("fyaxisname", "");
        }
        row.set("flegend",legend);
        row.set("fstarttime",startTime);
        row.set("fendtime",endTime);
        row.set("time_type",timeType);
        row.set("fthreshold",threshold);
        row.set("fthreValue",threValue);
		try {
			baseService.updatePrfField(row,"Node",storageType,devId,subsystemId,level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doExportNodeConfigData(){
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String componentId = getStrParameter("componentId");
		List<DataRow> rows = service.getNodeList(name, ipAddress, componentId, subsystemId);
		String subName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","组件ID","IP","IO GROUP","后端名称","操作状态","WWN"};
			String[] keys = new String[]{"the_display_name","sub_name","component_id","ip_address","iogroup_name","the_backend_name","the_operational_status","wwn"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-REDUNDANCY", rows, title, keys);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData(){
		Integer devId = getIntParameter("nodeId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Node", row.getString("type"),subsystemId,devId);
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "redundancy_id", "the_display_name", "v_res_redundancy");
		List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
		DataRow thead = new DataRow();
		thead.set("prf_timestamp", "时间");
		thead.set("ele_name", "设备名");
		for (DataRow r : kpis) {
			thead.set(r.getString("fid"), r.getString("ftitle"));
		}
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
		if(tbody!=null && tbody.size()>0){
			String[] title = (String[]) thead.values().toArray(new String[thead.size()]);
			String[] key = new String[thead.keySet().size()];
			Iterator<Object> it = thead.keySet().iterator();
			for (int i=0;i<thead.keySet().size();i++) {
				key[i] = it.next().toString().toLowerCase();
			}
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name"), tbody, title, key);
		}
	}
	
	private String[] checkStrArray(String[] str,String mach){
		if(str==null || str.length == 0){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (String string : str) {
			if(!string.equals(mach)){
				list.add(string);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	private void writeDataToPage(String data) {
		PrintWriter writer = null;
		try {
			getResponse().setCharacterEncoding("UTF-8");
			writer = getResponse().getWriter();
			writer.print(data);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		}
	}
}
