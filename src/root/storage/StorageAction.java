package root.storage;

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

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.AlertService;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.arraysite.ArraysiteService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.controller.ConService;
import com.huiming.service.disk.DiskService;
import com.huiming.service.extend.ExtendService;
import com.huiming.service.iogroup.IoGroupService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.rank.RankService;
import com.huiming.service.storage.StorageService;
import com.huiming.service.topn.TopnService;
import com.huiming.service.volume.VolumeService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class StorageAction extends SecurityAction {
	StorageService service = new StorageService();
	BaseprfService baseService = new BaseprfService();
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public ActionResult doStoragePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getStoragePage(curPage, numPerPage, null, null, null, null, null, null, null, null);
		this.setAttribute("storagePage", page);
		
		// 存储容量柱形图
		List<DataRow> rows = service.getCapacityInfo();
		JSONArray arr = new JSONArray();
		JSONArray allocatedCapacity = new JSONArray(); // 已用容量
		JSONArray availableCapacity = new JSONArray(); // 可用空间
		Map<Object,Object> mapAllocatedCapacity = new HashMap<Object,Object>();
		Map<Object,Object> mapAvailableCapacity = new HashMap<Object,Object>();
		for (DataRow row : rows) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("type",row.getString("os_type"));
			jsonObj.put("id", row.getInt("subsystem_id"));
			jsonObj.put("name", row.getString("the_display_name"));
			arr.add(jsonObj);
			allocatedCapacity.add(Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_allocated_capacity")/1024)));
			availableCapacity.add(Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_available_capacity")/1024)));
		}
		mapAvailableCapacity.put("name", "空余容量");
		mapAvailableCapacity.put("data", availableCapacity);
		mapAvailableCapacity.put("color", "#6CCA16");
		mapAllocatedCapacity.put("name", "已用容量");
		mapAllocatedCapacity.put("data", allocatedCapacity);
		mapAllocatedCapacity.put("color", "#C8C1CF");
		JSONArray jsArray = new JSONArray().fromObject(mapAvailableCapacity);
		jsArray.add(mapAllocatedCapacity);
		this.setAttribute("categories", jsArray);
		this.setAttribute("arr", arr);
		//性能
		//doStoragePrfField();
		return new ActionResult("/WEB-INF/views/storage/storageList.jsp");
	}
	
	@SuppressWarnings("unchecked")
	public ActionResult doAjaxStoragePage(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DBPage page = null;
		String storageName = getStrParameter("storageName").replaceAll("&amp;nbsp;", " ");;
		String ipAddress = getStrParameter("ipAddress");
		String type = getStrParameter("type");
		String serialNumber = getStrParameter("serialNumber");
		Integer startPoolCap = getIntParameter("startPoolCap");
		Integer endPoolCap = getIntParameter("endPoolCap");
		Integer startPoolAvailableCap = getIntParameter("startPoolAvailableCap");
		Integer endPoolAvailableCap = getIntParameter("endPoolAvailableCap");
		page = service.getStoragePage(curPage, numPerPage, storageName, ipAddress, type, serialNumber, startPoolCap, endPoolCap, startPoolAvailableCap, endPoolAvailableCap);
		this.setAttribute("storagePage", page);
		this.setAttribute("storageName", storageName);
		this.setAttribute("ipAddress", ipAddress);
		this.setAttribute("type", type);
		this.setAttribute("serialNumber", serialNumber);
		this.setAttribute("startPoolCap", startPoolCap);
		this.setAttribute("endPoolCap", endPoolCap);
		this.setAttribute("startPoolAvailableCap", startPoolAvailableCap);
		this.setAttribute("startPoolAvailableCap", startPoolAvailableCap);
		return new ActionResult("/WEB-INF/views/storage/ajaxStorage.jsp");
	}
	
	public ActionResult doStorageInfo(){
		PoolService poolService = new PoolService();
		PortService portService = new PortService();
		VolumeService volumeService = new VolumeService();
		DiskService diskService = new DiskService();
		ExtendService extendService = new ExtendService();
		NodeService nodeService = new NodeService();
		IoGroupService iogService = new IoGroupService();
		ArraysiteService arrService = new ArraysiteService();
		RankService rankService = new RankService();
		ConService conService = new ConService();
		
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = service.getSubsystemInfo(subsystemId);
		//doCapacityInfo();  //容量图信息
		DBPage poolPage = poolService.getPoolPage(1, WebConstants.NumPerPage, null, null, null, subsystemId);
		DBPage volumePage = volumeService.getVolumePage(1, WebConstants.NumPerPage, null, null, null, null, subsystemId);
		DBPage portPage = portService.getPortPage(1, WebConstants.NumPerPage, null, null, null, null, null, subsystemId);
		DBPage diskPage = diskService.getDiskPage(1, WebConstants.NumPerPage, null, null, null, subsystemId);
		DBPage extendPage = extendService.getExtendPage(1, WebConstants.NumPerPage, null, null, null, null, null, null, subsystemId);
		DBPage arraysitePage = arrService.getArraysitePage(1, WebConstants.NumPerPage, null, null, subsystemId);
		DBPage rankPage = rankService.getRankPage(1, WebConstants.NumPerPage, null, null, subsystemId);
		this.setAttribute("poolPage", poolPage);
		this.setAttribute("poolCount", poolPage.getTotalRows());
		this.setAttribute("volumePage", volumePage);
		this.setAttribute("volumeCount", volumePage.getTotalRows());
		this.setAttribute("portPage", portPage);
		this.setAttribute("portCount", portPage.getTotalRows());
		this.setAttribute("diskPage", diskPage);
		this.setAttribute("diskCount", diskPage.getTotalRows());
		this.setAttribute("extendPage", extendPage);
		this.setAttribute("extendCount", extendPage.getTotalRows());
		this.setAttribute("arraysitePage", arraysitePage);
		this.setAttribute("arrayCount", arraysitePage.getTotalRows());
		this.setAttribute("rankPage", rankPage);
		this.setAttribute("rankCount", rankPage.getTotalRows());
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1, subsystemId.toString(), null,null,null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);

		this.setAttribute("storageInfo", row);
		this.setAttribute("subSystemID", subsystemId);

		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow rown = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,2,"Controller", rown.getString("type"),subsystemId,null);
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_storage_subsystem", subsystemId, rown.getString("type"), "Storage", "subsystem_id", "the_display_name");
			if(row.getString("type").equals("DS")){
				dataRow.set("fprfid", "'A221','A233'");
			}else if(row.getString("type").equals("BSP")){
				dataRow.set("fprfid", "'A415','A421'");
			}else{
				dataRow.set("fprfid", "'A38','A44'");
			}
			
			dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = conService.getConList(subsystemId,dataRow.getString("fdevice"));
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			thead.set("prf_timestamp", "时间");
			thead.set("ele_name", "设备名");
			for (DataRow r : kpis) {
				thead.set(r.getString("fid"), r.getString("ftitle"));
			}
			tbody = baseService.getPrfDatas(curPage,numPerPage,devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
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
		this.setAttribute("conPrfData", json);
		
		//doStoragePrfField();
		DBPage nodePage = nodeService.getNodePage(1, WebConstants.NumPerPage, null, null, null, subsystemId);
		DBPage iogroupPage = iogService.getIogroupPage(1, WebConstants.NumPerPage, null, subsystemId);
		this.setAttribute("nodePage", nodePage);
		this.setAttribute("nodeCount", nodePage.getTotalRows());
		this.setAttribute("iogroupPage", iogroupPage);
		this.setAttribute("iogroupCount", iogroupPage.getTotalRows());
		return new ActionResult("/WEB-INF/views/storage/storageInfos.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doStorageSettingPrf(){
		Integer level = getIntParameter("level",1);
		Integer subSystemID = getIntParameter("subSystemID");
		List<DataRow> rows = service.getSubsystemNames(null);
		List<DataRow> kpis = baseService.getView(null, "Storage");
		DataRow dataRow2 = baseService.getPrfFieldInfo(null,level, "Storage", null,subSystemID,null);
		if(dataRow2==null && subSystemID!=0){
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", subSystemID);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		for (DataRow row : rows) {
			String type = "";
			if("25".equals(row.getString("os_type"))){
				type="DS";
			}else if("21".equals(row.getString("os_type")) || "38".equals(row.getString("os_type"))){
				type="SVC";
			}else if("15".equals(row.getString("os_type")) || "37".equals(row.getString("os_type"))){
				type="BSP";
			}
			row.set("type", type);
			storageList.add(row);
		}
		if(subSystemID!=null && subSystemID>0){
			DataRow drow = service.getType(subSystemID);
			if(drow!=null && drow.size()>0){
				this.setAttribute("type", drow.getString("type"));
			}
		}
		this.setAttribute("devList", storageList);
		this.setAttribute("tryargs", kpis);
		this.setAttribute("kpisList", kpisList);
		this.setAttribute("subSystemID", subSystemID);
		return new ActionResult("/WEB-INF/views/storage/editStorage.jsp");
	}
	
	public ActionResult doStoragePrfPage() {
		doStoragePrfField();
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/storage/ajaxPrfStorage.jsp");
		}
		return new ActionResult("/WEB-INF/views/storage/prefStoragePage.jsp");
	}
	
	public void doStoragePrf(){
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
		row.set("fname","Storage");
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
			baseService.updatePrfField(row,"Storage",null,null,subsystemId,level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doStoragePrfField(){
		JSONObject json = new JSONObject();
		Integer subsystemId = getIntParameter("subSystemID");
		Integer level = getIntParameter("level",1);
		if(subsystemId!=null && subsystemId>0){
			level = 3;
		}
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Storage", null,subsystemId,null);
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_storage_subsystem", subsystemId, "SVC", "Storage", "subsystem_id", "the_display_name");
		}
		DataRow thead = new DataRow();
		DBPage tbody = null;
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = null;
			if(dataRow.getString("fdevicetype").equals("EMC")){
				devs = baseService.getDeviceInfo2(dataRow.getString("fdevice"));
			}else{
				devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "subsystem_id", "the_display_name", "v_res_storage_subsystem");
			}
			List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
			if(level!=null && level==3){
				thead.set("prf_timestamp", "时间");
				thead.set("ele_name", "设备名");
				for (DataRow r : kpis) {
					thead.set(r.getString("fid"), r.getString("ftitle"));
				}
				tbody = baseService.getPrfDatas(curPage,numPerPage,devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
			}
			if(tablePage==null || tablePage.length()==0){
				JSONArray array = baseService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
				json.put("series", array);
			}
			json.put("thead", thead);
			json.put("tbody", tbody);
			json.put("legend", dataRow.getInt("flegend")==1?true:false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("kpiInfo", kpis);
		}
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}else{
			this.setAttribute("prfData", json);
		}
	}
	
	public void doCapacityInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = service.getSubsystemInfo(subsystemId);
		Double usedSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_allocated_capacity")));
		Double availableSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_available_capacity")));
		Double theSpace = usedSpace+availableSpace;
		Double perUsedSpace = Double.parseDouble(new DecimalFormat("0.00").format(usedSpace/theSpace*100));
		Double perAvailableSpace = Double.parseDouble(new DecimalFormat("0.00").format(availableSpace/theSpace*100));
		
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
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData() {
		Integer level = getIntParameter("level");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow dataRow = baseService.getPrfFieldInfo(null, level, "Storage",null, subsystemId, null);
		List<DataRow> devs = null;
		if(dataRow.getString("fdevicetype").equals("EMC")){
			devs = baseService.getDeviceInfo2(dataRow.getString("fdevice"));
		}else{
			devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "subsystem_id", "the_display_name", "v_res_storage_subsystem");
		}
		List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
		DataRow thead = new DataRow();
		thead.set("prf_timestamp", "时间");
		thead.set("ele_name", "设备名");
		for (DataRow r : kpis) {
			thead.set(r.getString("fid"), r.getString("ftitle"));
		}
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis,dataRow.getString("fstarttime"), dataRow.getString("fendtime"),dataRow.getString("time_type"));
		if (tbody != null && tbody.size() > 0) {
			String[] title = (String[]) thead.values().toArray(new String[thead.size()]);
			String[] key = new String[thead.keySet().size()];
			Iterator<Object> it = thead.keySet().iterator();
			for (int i = 0; i < thead.keySet().size(); i++) {
				key[i] = it.next().toString().toLowerCase();
			}
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name"), tbody, title, key);
		}
	}
	
	/**
	 * 导出配置信息
	 */
	public void doExportStorageConfigData(){
		String storageName = getStrParameter("storageName").replaceAll("&amp;nbsp;", " ");;
		String ipAddress = getStrParameter("ipAddress");
		String type = getStrParameter("type");
		String serialNumber = getStrParameter("serialNumber");
		Integer startPoolCap = getIntParameter("startPoolCap");
		Integer endPoolCap = getIntParameter("endPoolCap");
		Integer startPoolAvailableCap = getIntParameter("startPoolAvailableCap");
		Integer endPoolAvailableCap = getIntParameter("endPoolAvailableCap");
		List<DataRow> rows = service.getStorageList(storageName, ipAddress, type, serialNumber, startPoolCap, endPoolCap, startPoolAvailableCap, endPoolAvailableCap);
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","IP地址","状态","物理磁盘容量(G)","池容量(G)","池可用容量(G)","卷总容量(G)","已分配卷容量(G)","未分配卷容量","最近探查时间","缓存"};
			String[] keys = new String[]{"the_display_name","ip_address","the_propagated_status","the_physical_disk_space","the_storage_pool_consumed_space","the_storage_pool_available_space","the_volume_space","the_assigned_volume_space","the_unassigned_volume_space","last_probe_time","cache"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "STORAGE-SUBSYSTEM-DATA", rows, title, keys);
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
