package root.virtual;

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
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class VirtualAction extends SecurityAction {
	BaseprfService baseService = new BaseprfService();
	VirtualmachineService service=new VirtualmachineService();
	public ActionResult doVirtualPage(){
		//列表信息
		DBPage page = null;
//		Integer computerId = getIntParameter("computerId");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer hypervisorId = getIntParameter("hypervisorId");
		page = service.getVirtualmachinePage(curPage, numPerPage, hypervisorId, null, null, null, null, null);
		this.setAttribute("virtualPage", page);
		this.setAttribute("hypervisorId", hypervisorId);
//		//已用容量柱状图信息
//		List<DataRow> rows = service.getVolumeCapacityInfo(subsystemId);
//		Map<Object,Object> map = new HashMap<Object,Object>();
//		JSONArray jarray = new JSONArray();
//		JSONArray names = new JSONArray();
//		for (DataRow dataRow : rows) {
//			JSONObject dataJson = new JSONObject();
//			names.add(dataRow.getString("the_display_name"));
//			dataJson.put("svid", dataRow.getInt("svid"));
//			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
//			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("the_used_space"))));
//			jarray.add(dataJson);
//		}
//		map.put("name", "已用容量");
//		map.put("data", jarray);
//		JSONArray array = new JSONArray().fromObject(map);  
//		this.setAttribute("names", names);
//		this.setAttribute("array", array);
//		this.setAttribute("subSystemID", subsystemId);
		//性能曲线
		//doVolumePrfField();
		return new ActionResult("/WEB-INF/views/virtual/virtualList.jsp");
	}
	//分页展示
	public ActionResult doAjaxVirtualPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer hypervisorId = getIntParameter("hypervisorId");
		String virtualName=getStrParameter("virtualName").replaceAll("&amp;nbsp;", " ");
		String endMemory=getStrParameter("endMemory");
		String startMemory=getStrParameter("startMemory");
		String startDiskSpace=getStrParameter("startDiskSpace");
		String endDiskSpace=getStrParameter("endDiskSpace");
		page = service.getVirtualmachinePage(curPage, numPerPage, hypervisorId,virtualName,endMemory,startMemory,startDiskSpace,endDiskSpace);
		this.setAttribute("virtualPage", page);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("virtualName", virtualName);
		this.setAttribute("endMemory", endMemory);
		this.setAttribute("startMemory", startMemory);
		this.setAttribute("startDiskSpace", startDiskSpace);
		this.setAttribute("endDiskSpace", endDiskSpace);
		return new ActionResult("/WEB-INF/views/virtual/ajaxVirtual.jsp");
	}
	public ActionResult doVirtualInfo(){
		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId=getIntParameter("vmId");
		DataRow row=service.getVirtualInfo(vmId,hypervisorId);
		this.setAttribute("virtualInfo", row);
		this.setAttribute("diskPage", service.getDiskPage(1, WebConstants.NumPerPage, vmId));
		//阀值告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1,vmId.toString(), null,null, null,"Virtual",-1, -1, null, null);
		setAttribute("logPage",devicePage);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("computerId", computerId);
		this.setAttribute("vmId", vmId);
		return new ActionResult("/WEB-INF/views/virtual/virtualInfo.jsp");
	}
	
	//分页展示
	public ActionResult doAjaxDiskPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		int vmId = getIntParameter("vmId");
		page = service.getDiskPage(curPage, numPerPage, vmId);
		this.setAttribute("diskPage", page);
		this.setAttribute("vmId", vmId);
		
		return new ActionResult("/WEB-INF/views/virtual/ajaxDisk.jsp");
	}
	/**
	 * 导出虚拟机配置信息
	 */
	public void doExportVirtualConfigData(){
		Integer hypervisorId = getIntParameter("hypervisorId");
		String virtualName=getStrParameter("virtualName").replaceAll("&amp;nbsp;", " ");
		String endMemory=getStrParameter("endMemory");
		String startMemory=getStrParameter("startMemory");
		String startDiskSpace=getStrParameter("startDiskSpace");
		String endDiskSpace=getStrParameter("endDiskSpace");
		List<DataRow> rows = service.getVirtualList(null, hypervisorId, virtualName, endMemory, startMemory, startDiskSpace, endDiskSpace);
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","所属物理机","IP地址","CPU逻辑个数","总内存(MB)","磁盘总容量(MB)","磁盘剩余容量(MB)","更新时间"};
			String[] keys = new String[]{"display_name","host_name","ip_address","assigned_cpu_number","total_memory","disk_space","disk_available_space","update_timestamp"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "VIRTUAL-DATA", rows, title, keys);
		}
	}
	public ActionResult doVirtualSettingPrf(){
		Integer level = getIntParameter("level",1);
//		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId = getIntParameter("vmId");
		List<DataRow> rows = service.getVirtualName(vmId, hypervisorId, null);
		List<DataRow> kpis = baseService.getView("HOST", "Virtual");
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, "Virtual", "HOST", hypervisorId, vmId);
		if(dataRow2==null&&vmId!=0){
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", vmId);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		for (DataRow row : rows) {
			row.set("name", row.getString("name"));
			row.set("id", row.getString("vm_id"));
			storageList.add(row);
		}
		this.setAttribute("devList", storageList);
		this.setAttribute("vmId", vmId);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("kpisList", kpisList);
		return new ActionResult("/WEB-INF/views/virtual/editVirtual.jsp");
	}
	public void doVirtualPrfField(){
		JSONObject json = new JSONObject();
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer vmId = getIntParameter("vmId");
		Integer level = getIntParameter("level",1);
		if(vmId!=null && vmId>0){
			level = 3;
		}
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);		
		DataRow thead = new DataRow();
		DBPage tbody = null;
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Virtual", "HOST",hypervisorId,vmId);
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("t_res_virtualmachine", vmId, "HOST", "Virtual", "vm_id", "name");
			dataRow.set("fprfid", "'V2','V3'");
			dataRow.set("fyaxisname", "%");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), "vm_id", "name", "t_res_virtualmachine");
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
	public ActionResult doVirtualPrfPage() {
		doVirtualPrfField();
		this.setAttribute("hypervisorId", getIntParameter("hypervisorId"));
		this.setAttribute("vmId", getIntParameter("vmId"));
		//this.setAttribute("computerId", getIntParameter("computerId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/virtual/ajaxPrfVirtual.jsp");
		}
		return new ActionResult("/WEB-INF/views/virtual/prefVirtualPage.jsp");
	}
	public void doVirtualPrf(){
		Integer hypervisorId = getIntParameter("hypervisorId");
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
		row.set("fsubsystemid", hypervisorId);
		row.set("level", level);
		row.set("fname","Virtual");
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
			baseService.updatePrfField(row, "Virtual","HOST" , devId, hypervisorId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
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
	//导出性能信息
	@SuppressWarnings("unchecked")
	public void doExportPrefData() {
		Integer level = getIntParameter("level");
		Integer vmId = getIntParameter("vmId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Virtual", "HOST",hypervisorId,vmId);
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("t_res_virtualmachine", vmId, "HOST", "Virtual", "vm_id", "name");
		}
		List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), "vm_id", "name", "t_res_virtualmachine");
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
//	//topn图表
//	 public JSONObject getTopChart(Integer computerId,String Table,String tname) {
//		    JSONObject json = new JSONObject();
//		    List<DataRow> cpus = baseService.getTop10(computerId,Table,tname);
//		    JSONArray Name = new JSONArray();
//		    JSONArray Data = new JSONArray();
//		    JSONArray Time = new JSONArray();
//		    for (DataRow cpu : cpus) {
//		    	Name.add(cpu.getString("ele_name"));
//		    	Data.add(Double.valueOf(NumericHelper.round(cpu.getDouble("prf"), 2)));
//		    	Time.add(cpu.getString("prf_timestamp"));
//		    }
//		    json.put("Name", Name);
//		    json.put("Data", Data);
//		    json.put("time", Time);
//		    return json;
//		  }
}
