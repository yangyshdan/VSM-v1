package root.volume;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class VolumeAction extends SecurityAction {
	VolumeService service = new VolumeService();
	BaseprfService baseService = new BaseprfService();
	
	@SuppressWarnings("static-access")
	public ActionResult doVolumePage(){
		//列表信息
		DBPage page = null;
		Integer subsystemId = getIntParameter("subSystemID");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getVolumePage(curPage, numPerPage, null, null, null, null, subsystemId);
		this.setAttribute("volumePage", page);
		
		//已用容量柱状图信息
		List<DataRow> rows = service.getVolumeCapacityInfo(subsystemId);
		Map<Object,Object> map = new HashMap<Object,Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add(dataRow.getString("the_display_name"));
			dataJson.put("svid", dataRow.getInt("svid"));
			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("the_used_space"))));
			jarray.add(dataJson);
		}
		map.put("name", "已用容量");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);  
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("subSystemID", subsystemId);
		//性能曲线
		//doVolumePrfField();
		return new ActionResult("/WEB-INF/views/volume/volumeList.jsp");
	}
	
	/**
	 * 分页
	 * @return
	 */
	public ActionResult doAjaxVolumePage(){
		//列表信息
		DBPage page = null;
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String greatCapacity = getStrParameter("greatLogical_Capacity");
		String lessCapacity = getStrParameter("lessLogical_Capacity");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getVolumePage(curPage, numPerPage, name, lessCapacity, greatCapacity, null, subsystemId);
		this.setAttribute("volumePage", page);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("name", name);
		this.setAttribute("lessLogical_Capacity", lessCapacity);
		this.setAttribute("greatLogical_Capacity", greatCapacity);
		return new ActionResult("/WEB-INF/views/volume/ajaxVolume.jsp");
	}
	
	public ActionResult doPerVolumeInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer svid = getIntParameter("svid");
		DataRow volumeInfo = service.getVolumeById(svid);
		this.setAttribute("volumeInfo", volumeInfo);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("svid", svid);
		//doVolumePrfField();
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage = deviceService.getLogPage(1, WebConstants.NumPerPage, -1, subsystemId.toString(),null,svid.toString(), null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		return new ActionResult("/WEB-INF/views/volume/volumeInfo.jsp");
	}
	
	public ActionResult doVolumePrfPage(){
		//性能曲线
		doVolumePrfField();
		this.setAttribute("svid", getIntParameter("svid"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage != null && tablePage.length() > 0){
			return new ActionResult("/WEB-INF/views/volume/ajaxPrfVolume.jsp");
		}
		return new ActionResult("/WEB-INF/views/volume/prefVolumePage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doVolumeSettingPrf(){
		Integer volumeId = getIntParameter("svid");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "Volume");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_storage_volume", "the_display_name", "svid","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(volumeId!=null && volumeId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Volume", row.getString("type"),subsystemId,volumeId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", volumeId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Volume", row.getString("type"),subsystemId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/volume/VolumeAction?func=VolumePrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doVolumeSettingPrf2(){
		Integer volumeId = getIntParameter("svid");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "Volume");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_storage_volume", "the_display_name", "svid","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(volumeId!=null && volumeId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Volume", row.getString("type"),subsystemId,volumeId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", volumeId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Volume", row.getString("type"),subsystemId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/volume/VolumeAction?func=VolumePrf");
		return new ActionResult("/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp");
	}
	
	public void doVolumePrfField(){
		Integer volumeId = getIntParameter("svid");
		Integer level = getIntParameter("level", volumeId == 0? 2 : 3);
		Integer subsystemId = getIntParameter("subSystemID");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		String tablePage = getStrParameter("tablePage");
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, SrContant.SUBDEVTYPE_VOLUME, 
				row.getString("type"), subsystemId, volumeId, getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow == null || dataRow.size() == 0){
			dataRow = baseService.getDefaultRow("v_res_storage_volume", volumeId, 
					row.getString("type"), "Volume", "svid", "the_display_name");
			if(row.getString("type").equals("DS")){
				dataRow.set("fprfid", "'A183','A195'");
			}
			else if(row.getString("type").equals("BSP")){
				dataRow.set("fprfid", "'A427','A433'");
			}else{
				dataRow.set("fprfid", "'A3','A9'");
			}
			
			dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
		}
		if(dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), 
					"svid", "the_display_name", "v_res_storage_volume");
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			thead.set("prf_timestamp", "时间");
			thead.set("ele_name", "设备名");
			if(devs!=null && devs.size()==1){
				for (DataRow r : kpis) {
					thead.set(r.getString("fid"), r.getString("ftitle")+"("+r.getString("funits")+")");
				}
				tbody = baseService.getPrfDatas(curPage,numPerPage,devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
			}
			if(tablePage == null || tablePage.length() == 0){
				json.put("series", JSON.toJSONString(baseService.getSeries(dataRow.getInt("fisshow"),
						devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),
						dataRow.getString("time_type"))));
//				JSONArray array = baseService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
//				json.put("series", array);
			}
			json.put("legend", dataRow.getInt("flegend") == 1? true : false);
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("ytitle", dataRow.getString("fyaxisname"));
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
	
	public void doVolumePrf(){
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
			for (int i = 0, len = devices.length - 1;i <= len;i++) {
				device.append(devices[i]);
				if(i < len){
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
		Integer level = getIntParameter("level");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname","Volume");
		row.set("fdevicetype", storageType);
		row.set("fdevice", dev);
		row.set("fprfid", kpi.toString());
		row.set("fisshow", 1);
		List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString());
		if (units != null && units.size() > 0) {
			Set<String> set = new HashSet<String>();
			for (DataRow unit : units) {
				if (StringHelper.isNotEmpty(unit.getString("funits")))
					set.add(unit.getString("funits"));
			}
        	String tempStr = set.toString().replace("[", "").replace("]", "");
        	row.set("fyaxisname", tempStr.length()>40?tempStr.substring(0, 37)+"...":tempStr);
		} else {
			row.set("fyaxisname", "");
		}
		row.set("flegend", legend);
		row.set("fstarttime", startTime);
		row.set("fendtime", endTime);
		row.set("time_type", timeType);
		row.set("fthreshold", threshold);
		row.set("fthreValue", threValue);
		row.set("fuserid", getLoginUserId());
		try {
			baseService.updatePrfField(row, "Volume", storageType, devId, subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	/**
	 * 导出卷信息
	 */
	public void doExpertVolumeConfigData(){
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");;
		String pool_id = getStrParameter("pool_id");
		String greatCapacity = getStrParameter("greatLogical_Capacity");
		String lessCapacity = getStrParameter("lessLogical_Capacity");
		Integer subSystemID = getIntParameter("subSystemID");
		List<DataRow> rows = service.getVolumeInfo(name, lessCapacity, greatCapacity, pool_id, subSystemID);
		String subsystemName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","状态","容量(G)","已用容量(G)","沉余级别","存储池","唯一编号"};
			String[] keys = new String[]{"the_display_name","sub_name","the_consolidated_status","the_capacity","the_used_space","the_redundancy","pool_name","unique_id"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subsystemName+"-VOLUMES", rows, title, keys);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData(){
		Integer volumeId = getIntParameter("svid");
		Integer level = getIntParameter("level",volumeId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Volume", row.getString("type"),subsystemId,volumeId,getLoginUserId());
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "svid", "the_display_name", "v_res_storage_volume");
		List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis,
				dataRow.getString("fstarttime"), 
				dataRow.getString("fendtime"),
				dataRow.getString("time_type"));
		baseService.createAndSendCSVFile(getResponse(), kpis, tbody, devs);
		/*DataRow thead = new DataRow();
		thead.set("prf_timestamp", "时间");
		thead.set("ele_name", "设备名");
		for (DataRow r : kpis) {
			thead.set(r.getString("fid"), r.getString("ftitle"));
		}
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
		DecimalFormat df = new DecimalFormat("#.##");
		if(tbody!=null && tbody.size()>0){
			String[] title = (String[]) thead.values().toArray(new String[thead.size()]);
			String[] key = new String[thead.keySet().size()];
			Iterator<Object> it = thead.keySet().iterator();
			for (int i=0;i<thead.keySet().size();i++) {
				key[i] = it.next().toString().toLowerCase();
			}
			for (int i = 0; i < tbody.size(); i++) {
				tbody.get(i).set("a175", df.format(tbody.get(i).getDouble("a175")));
			}
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name"), tbody, title, key);
		}*/
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
