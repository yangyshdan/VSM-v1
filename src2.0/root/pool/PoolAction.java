package root.pool;

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
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.pool.PoolService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class PoolAction extends SecurityAction {
	PoolService service = new PoolService();
	BaseprfService baseService = new BaseprfService();

	@SuppressWarnings({ "static-access"})
	public ActionResult doPoolPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer system_id = getIntParameter("subSystemID");
		page = service.getPoolPage(curPage, numPerPage,null,null,null,system_id);
		this.setAttribute("poolPage", page);
		
		List<DataRow> rows = service.getCapacity(system_id);
		Map<Object,Object> map = new HashMap<Object,Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add(dataRow.getString("the_display_name"));
			dataJson.put("poolId", dataRow.getInt("pool_id"));
			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("the_consumed_space"))));
			jarray.add(dataJson);
		}
		map.put("name", "已用容量");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);  
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("subSystemID", system_id);
		
		//性能信息
		//doPoolPrfField();
		return new ActionResult("/WEB-INF/views/pool/poolList.jsp");

	}
	/**
	 * 池分页
	 * @return
	 */
	public ActionResult doAjaxPoolPage(){
		DBPage page = null;
		Integer system_id = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");;
		String greatCapacity = getStrParameter("greatCapacity");
		String lessCapacity = getStrParameter("lessCapacity");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getPoolPage(curPage, numPerPage,name,greatCapacity,lessCapacity,system_id);
		this.setAttribute("name", name);
		this.setAttribute("greatCapacity", greatCapacity);
		this.setAttribute("lessCapacity", lessCapacity);
		this.setAttribute("poolPage", page);
		this.setAttribute("subSystemID", system_id);
		return new ActionResult("/WEB-INF/views/pool/ajaxPool.jsp");
	}
	//查询存储池的详细信息
	public ActionResult doPoolInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer poolId = getIntParameter("poolId");
		DataRow row = service.getPoolById(poolId);
		//容量图信息
		//doPoolCapacityInfo();
		//卷信息
		doVolumeInfo();
		//性能信息
		//doPoolPrfField();
		
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1,subsystemId.toString(),null, poolId.toString(), null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		
		this.setAttribute("poolInfo", row);
		this.setAttribute("poolId", poolId);
		this.setAttribute("subSystemID", subsystemId);
		return new ActionResult("/WEB-INF/views/pool/poolInfo.jsp");
	}
	
	/**
	 * 池容量信息
	 */
	public void doPoolCapacityInfo(){
		Integer poolId = getIntParameter("poolId");
		DataRow row = service.getPoolById(poolId);
		Double usedSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_consumed_space")));
		Double availableSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_available_space")));
		Double theSpace = Double.parseDouble(new DecimalFormat("0.00").format(row.getDouble("the_space")));
		Double perUsedSpace = Double.parseDouble(new DecimalFormat("0.00").format(theSpace==0?0:usedSpace/theSpace));
		Double perAvailableSpace = Double.parseDouble(new DecimalFormat("0.00").format(theSpace==0?0:availableSpace/theSpace));
		
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
	
	/**
	 * 池相关卷信息
	 * @return
	 */
	public ActionResult doVolumeInfo(){
		DBPage page = null;
		Integer poolId = getIntParameter("poolId");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page = service.getVolumePageByPoolId(curPage, numPerPage, poolId);
		this.setAttribute("poolId", poolId);
		this.setAttribute("volumePage", page);
		String isFreshen = getStrParameter("isFreshen");
		if(isFreshen==null || isFreshen.length()==0){
			return null;
		}
		return new ActionResult("/WEB-INF/views/pool/ajaxVolume.jsp");
	}
	
	public ActionResult doPoolPrfPage(){
		//性能曲线
		doPoolPrfField();
		this.setAttribute("poolId", getIntParameter("poolId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/pool/ajaxPrfPool.jsp");
		}
		return new ActionResult("/WEB-INF/views/pool/prefPoolPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doPoolSettingPrf(){
		Integer devId = getIntParameter("poolId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "MdiskGroup");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_storage_pool", "the_display_name", "pool_id","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "MdiskGroup", row.getString("type"),subsystemId,devId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "MdiskGroup", row.getString("type"),subsystemId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/pool/PoolAction?func=PoolPrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doPoolSettingPrf2(){
		Integer devId = getIntParameter("poolId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "MdiskGroup");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_storage_pool", "the_display_name", "pool_id","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "MdiskGroup", row.getString("type"),subsystemId,devId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "MdiskGroup", row.getString("type"),subsystemId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/pool/PoolAction?func=PoolPrf");
		return new ActionResult("/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp");
	}
	
	public void doPoolPrfField(){
		Integer devId = getIntParameter("poolId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"MdiskGroup", 
				row.getString("type"), subsystemId, devId,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_storage_pool", devId, row.getString("type"), "MdiskGroup", "pool_id", "the_display_name");
			if(dataRow != null && dataRow.size() > 0){
				dataRow.set("fprfid", "'A661','A664'");
				dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
			}
			
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "pool_id", "the_display_name", "v_res_storage_pool");
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
	
	public void doPoolPrf(){
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
		Integer level = getIntParameter("level");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname","MdiskGroup");
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
			baseService.updatePrfField(row,"MdiskGroup",storageType,devId,subsystemId,level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	/**
	 * 导出存储池配置信息
	 */
	public void doExportPoolConfigData(){
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");;
		String greatCapacity = getStrParameter("greatCapacity");
		String lessCapacity = getStrParameter("lessCapacity");
		Integer subSystemID = getIntParameter("subSystemID");
		List<DataRow> rows = service.getPoolsInfo(name, greatCapacity, lessCapacity, subSystemID);
		String subName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","容量(G)","已用容量(G)","可用容量(G)","已分配容量(G)","未分配容量(G)","本地状态","操作状态","硬件状态","沉余级别"};
			String[] keys = new String[]{"the_display_name","sub_name","the_space","the_consumed_space","the_available_space","the_assigned_space","the_unassigned_space","the_native_status","the_operational_status","the_consolidated_status","raid_level"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-Pools", rows, title, keys);
		}
	}	
	
	/**
	 * 导出池相关卷信息
	 */
	public void doExpertVolumeConfigData(){
		Integer poolId = getIntParameter("poolId");
		List<DataRow> rows = service.getVolumeByPoolId(poolId);
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
		Integer devId = getIntParameter("poolId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"MdiskGroup", row.getString("type"),subsystemId,devId,getLoginUserId());
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "pool_id", "the_display_name", "v_res_storage_pool");
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
