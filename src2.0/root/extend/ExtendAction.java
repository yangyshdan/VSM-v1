package root.extend;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.extend.ExtendService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ExtendAction extends SecurityAction{
	ExtendService service = new ExtendService();
	BaseprfService baseService = new BaseprfService();
	@SuppressWarnings("static-access")
	public ActionResult doExtendPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		page = service.getExtendPage(curPage, numPerPage, null, null, null, null, null, null, subsystemId);
		List<DataRow> rows = service.getCapacityInfo(subsystemId);
		Map<Object,Object> map = new HashMap<Object,Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add(dataRow.getString("the_display_name"));
			dataJson.put("extendId", dataRow.getInt("storage_extent_id"));
			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("the_total_space"))));
			jarray.add(dataJson);
		}
		map.put("name", "容量");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);  
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("extendPage", page);
		this.setAttribute("subSystemID", subsystemId);
		return new ActionResult("/WEB-INF/views/extend/extendList.jsp");
	}
	
	public ActionResult doAjaxExtendPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String deviceId = getStrParameter("deviceId");
		Integer startCap = getIntParameter("startCap");
		Integer endCap = getIntParameter("endCap");
		Integer startAvailableCap = getIntParameter("startAvailableCap");
		Integer endAvailableCap = getIntParameter("endAvailableCap");
		page = service.getExtendPage(curPage, numPerPage,name, deviceId, startCap, endCap, startAvailableCap, endAvailableCap,subsystemId);
		this.setAttribute("extendPage", page);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("name", name);
		this.setAttribute("deviceId", deviceId);
		this.setAttribute("startCap", startCap);
		this.setAttribute("endCap", endCap);
		this.setAttribute("startAvailableCap", startAvailableCap);
		this.setAttribute("endAvailableCap", endAvailableCap);
		return new ActionResult("/WEB-INF/views/extend/ajaxExtend.jsp");
	}
	
	public ActionResult doExtendInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer extendId = getIntParameter("extendId");
		DataRow row = service.getExtendInfo(extendId);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1,subsystemId.toString(),null, extendId.toString(), null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		
		this.setAttribute("extendInfo", row);
		this.setAttribute("subSystemID", subsystemId);
		
		//容量信息
		doExtendCapInfo();
		return new ActionResult("/WEB-INF/views/extend/extendInfo.jsp");
	}
	
	public ActionResult doExtendPrfPage(){
		doExtendPrfField();
		this.setAttribute("extendId", getIntParameter("extendId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/extend/ajaxPrfExtend.jsp");
		}
		return new ActionResult("/WEB-INF/views/extend/prefExtendPage.jsp");
	}
	
	public void doExtendCapInfo() {
		Integer extendId = getIntParameter("extendId");
		DataRow row = service.getExtendInfo(extendId);
		Double availableSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_available_space")));
		Double theSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_total_space")));
		Double usedSpace = Double.parseDouble(new DecimalFormat("0.00").format(theSpace-availableSpace));
		Double perUsedSpace = Double.parseDouble(new DecimalFormat("0.00").format(usedSpace/theSpace));
		Double perAvailableSpace = Double.parseDouble(new DecimalFormat("0.00").format(availableSpace/theSpace));
		
		JSONObject json = new JSONObject();
		json.put("usedSpace", usedSpace);
		json.put("availableSpace", availableSpace);
		json.put("perUsedSpace", perUsedSpace);
		json.put("perAvailableSpace", perAvailableSpace);
		json.put("the_display_name", row.getString("the_display_name"));
		this.setAttribute("jsonVal", json);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
		
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doExtendSettingPrf(){
		Integer devId = getIntParameter("extendId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "Mdisk");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_storage_extent", "the_display_name", "storage_extent_id","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Mdisk", row.getString("type"),subsystemId,devId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Mdisk", row.getString("type"),subsystemId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/extends/ExtendAction?func=ExtendPrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	public void doExtendPrfField(){
		Integer devId = getIntParameter("extendId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String tablePage = getStrParameter("tablePage");
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Mdisk", row.getString("type"),subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_storage_extent", devId, row.getString("type"), "Mdisk", "storage_extent_id", "the_display_name");
			dataRow.set("fprfid", "'A641','A644'");
			dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "storage_extent_id", "the_display_name", "v_res_storage_extent");
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
//				JSONArray array = baseService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
//				json.put("series", array);
				json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"))));
				
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
	
	public void doExtendPrf(){
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
		row.set("fname","Mdisk");
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
        row.set("fuserid", getLoginUserId());
		try {
			baseService.updatePrfField(row,"Mdisk",storageType,devId,subsystemId,level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}

	public void doExportExtendConfigData(){
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String deviceId = getStrParameter("deviceId");
		Integer startCap = getIntParameter("startCap");
		Integer endCap = getIntParameter("endCap");
		Integer startAvailableCap = getIntParameter("startAvailableCap");
		Integer endAvailableCap = getIntParameter("endAvailableCap");
		List<DataRow> rows = service.getExtendList(name, deviceId, startCap, endCap, startAvailableCap, endAvailableCap, subsystemId);
		String subName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","扩展卷数","扩展容量(G)","总容量(G)","可用容量(G)","操作状态","本地状态","存储池","设备ID"};
			String[] keys = new String[]{"the_display_name","sub_name","the_extent_volume","the_extend_space","the_total_space","the_available_space","the_operational_status","the_native_status","pool_name","device_id"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-Extends", rows, title, keys);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData(){
		Integer devId = getIntParameter("extendId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Mdisk", row.getString("type"),subsystemId,devId,getLoginUserId());
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "storage_extent_id", "the_display_name", "v_res_storage_extent");
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
