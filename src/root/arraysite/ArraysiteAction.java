package root.arraysite;

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
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ArraysiteAction extends SecurityAction{
	ArraysiteService service = new ArraysiteService();
	BaseprfService baseService = new BaseprfService();
	
	@SuppressWarnings("static-access")
	public ActionResult doArraysitePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		page = service.getArraysitePage(curPage, numPerPage, null, null,subsystemId);
		List<DataRow> rows = service.getDiskNum(subsystemId);
		Map<Object,Object> map = new HashMap<Object,Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add(dataRow.getString("the_display_name"));
			dataJson.put("devId", dataRow.getInt("disk_group_id"));
			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("num"))));
			jarray.add(dataJson);
		}
		map.put("name", "磁盘数");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);  
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("arraysitePage", page);
		doArraysitePrfField();
		return new ActionResult("/WEB-INF/views/arraysite/arraysiteList.jsp");
	}
	
	public ActionResult doAjaxArraysitePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String raidLevel = getStrParameter("raidLevel").replaceAll("&amp;nbsp;", " ");
		page = service.getArraysitePage(curPage, numPerPage, name, raidLevel, subsystemId);
		this.setAttribute("name", name);
		this.setAttribute("raidLevel", raidLevel);
		this.setAttribute("arraysitePage", page);
		return new ActionResult("/WEB-INF/views/arraysite/ajaxArraysite.jsp");
	}
	
	public ActionResult doArraysiteInfo(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		Integer arraysiteId = getIntParameter("arraysiteId");
		DataRow arraysiteInfo = service.getArraysiteInfo(arraysiteId);
		page = service.getDiskPage(curPage, numPerPage, arraysiteId);
		this.setAttribute("arraysiteInfo", arraysiteInfo);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("arraysiteId", arraysiteId);
		this.setAttribute("diskPage",page);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1,subsystemId.toString(),null ,arraysiteId.toString(), null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		//doArraysitePrfField();
		return new ActionResult("/WEB-INF/views/arraysite/arraysiteInfo.jsp");
	}
	
	public ActionResult doAjaxDiskPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer arraysiteId = getIntParameter("arraysiteId");
		System.out.println(arraysiteId);
		page = service.getDiskPage(curPage, numPerPage, arraysiteId);
		this.setAttribute("arraysiteId", arraysiteId);
		this.setAttribute("diskPage",page);
		return new ActionResult("/WEB-INF/views/arraysite/ajaxDisk.jsp");
	}
	
	public void doExportArraysiteConfigData(){
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String raidLevel = getStrParameter("raidLevel").replaceAll("&amp;nbsp;", " ");
		List<DataRow> rows = service.getArraysiteList(name, raidLevel, subsystemId);
		String subName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","池","Rank","冗余级别","说明"};
			String[] keys = new String[]{"the_display_name","sub_name","pool_name","rank_name","raid_level","description"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-ArraySite", rows, title, keys);
		}
	}
	
	public void doExportDiskConfigData(){
		Integer arraysiteId = getIntParameter("arraysiteId");
		List<DataRow> rows = service.getDiskList(arraysiteId);
		String subName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","阵列","厂商","型号","序列号","固件版本","运行状态","硬件状态","转速","容量(G)"};
			String[] keys = new String[]{"the_display_name","sub_name","diskgroup_name","vendor_name","model_name","serial_number","firmware_rev","the_operational_status","the_consolidated_status","speed","the_capacity"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-Disks", rows, title, keys);
		}
	}
	
	public ActionResult doArraysitePrfPage(){
		//性能曲线
		doArraysitePrfField();
		this.setAttribute("arraysiteId", getIntParameter("arraysiteId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/arraysite/ajaxPrfArraysite.jsp");
		}
		return new ActionResult("/WEB-INF/views/arraysite/prefArraysitePage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doArraysiteSettingPrf(){
		Integer devId = getIntParameter("arraysiteId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "ArraySite");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_arraysite", "the_display_name", "disk_group_id","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "ArraySite", row.getString("type"),subsystemId,devId);
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "ArraySite", row.getString("type"),subsystemId,null));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/arraysite/ArraysiteAction?func=ArraysitePrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	public void doArraysitePrfField(){
		Integer devId = getIntParameter("arraysiteId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"ArraySite", row.getString("type"),subsystemId,devId);
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_arraysite", devId, row.getString("type"), "ArraySite", "disk_group_id", "the_display_name");
			dataRow.set("fprfid", "'A360','A372'");
			dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "disk_group_id", "the_display_name", "v_res_arraysite");
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
	
	public void doArraysitePrf(){
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
		row.set("fname","ArraySite");
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
			baseService.updatePrfField(row,"ArraySite",storageType,devId,subsystemId,level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData(){
		Integer devId = getIntParameter("arraysiteId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"ArraySite", row.getString("type"),subsystemId,devId);
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "disk_group_id", "the_display_name", "v_res_arraysite");
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
