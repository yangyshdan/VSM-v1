package root.hypervisor;

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
import com.huiming.service.hypervisor.HypervisorService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class HypervisorAction extends SecurityAction {
	BaseprfService baseService = new BaseprfService();
	HypervisorService service=new HypervisorService();
	public ActionResult doHypervisorPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		page=service.getHypervisorPage(curPage, numPerPage, null, null, null, null, null, null, null, null);
		this.setAttribute("hypervisorPage", page);	
		return new ActionResult("/WEB-INF/views/hypervisor/hypervisorList.jsp");

	}
	//分页展示
	public ActionResult doAjaxHypervisorPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String displayName=getStrParameter("displayName").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String cpuArchitecture=getStrParameter("cpuArchitecture");
		Integer startRamSize = getIntParameter("startRamSize");
		Integer endRamSize = getIntParameter("endRamSize");
		Integer startDiskSpace = getIntParameter("startDiskSpace");
		Integer endDiskSpace = getIntParameter("endDiskSpace");
		page = service.getHypervisorPage(curPage, numPerPage, null, displayName, ipAddress,cpuArchitecture,startRamSize,endRamSize, startDiskSpace, endDiskSpace);
		this.setAttribute("hypervisorPage", page);
		this.setAttribute("displayName", displayName);
		this.setAttribute("ipAddress", ipAddress);
		this.setAttribute("cpuArchitecture", cpuArchitecture);
		this.setAttribute("startRamSize", startRamSize);
		this.setAttribute("endRamSize", endRamSize);
		this.setAttribute("startDiskSpace", startDiskSpace);
		this.setAttribute("endDiskSpace", endDiskSpace);
		return new ActionResult("/WEB-INF/views/hypervisor/ajaxHypervisor.jsp");
	}
	/**
	 * 导出主机配置信息
	 */
	public void doExportHypervisorConfigData(){
		String displayName = getStrParameter("displayName").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		Integer startDiskSpace = getIntParameter("startDiskSpace");
		Integer endDiskSpace = getIntParameter("endDiskSpace");
		List<DataRow> rows = service.getHypervisorList(null, null,displayName, ipAddress, startDiskSpace, endDiskSpace);
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","IP地址","处理器架构","处理器总数","内存(MB)","未分配CPU","未分配内存(MB)","磁盘总容量(MB)","磁盘剩余容量(MB)","更新时间"};
			String[] keys = new String[]{"display_name","ip_address","cpu_architecture","processor_count","ram_size","available_cpu","available_mem","disk_space","disk_available_space","update_timestamp"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "HYPERVISOR-DATA", rows, title, keys);
		}
	}
	//物理机的详细信息及性能
	public ActionResult doHypervisorInfo(){
		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");

		DataRow row=service.getHypervisorInfo(hypervisorId, computerId);
		this.setAttribute("hypervisorInfo", row);
		
		DBPage fibrePage = service. getHwresPage(1, WebConstants.NumPerPage, 1,hypervisorId);
		this.setAttribute("fibrePage", fibrePage);
		this.setAttribute("fibreCount", fibrePage.getTotalRows());
		
		VirtualmachineService virtualmachineService=new VirtualmachineService();
		DBPage virtualPage = virtualmachineService.getVirtualmachinePage(1, WebConstants.NumPerPage, hypervisorId,null,null,null,null,null);
		this.setAttribute("virtualPage", virtualPage);
		this.setAttribute("virtualCount", virtualPage.getTotalRows());
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1, hypervisorId.toString(), null,null,null, "Physical", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
	//	setAttribute("cputopJson", getTopChart(computerId,"v_prf_physical", "h1"));
//		setAttribute("memtopJson", getTopChart(computerId,"v_prf_physical", "h2"));
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("computerId", computerId);
		this.setAttribute("resourcetype", "Physical");
		//性能柱状图
		
		
		return new ActionResult("/WEB-INF/views/hypervisor/hypervisorInfo.jsp");
	}
	@SuppressWarnings("static-access")
	public ActionResult doHypervisorSettingPrf(){
		Integer level = getIntParameter("level",1);
		Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		List<DataRow> rows = service.getHypervisorName(hypervisorId,null);
		List<DataRow> kpis = baseService.getView("HOST", "Physical");
		DataRow dataRow2 = baseService.getPrfFieldInfo(null, level, "Physical", "HOST", hypervisorId, null);
		if(dataRow2==null&&hypervisorId!=0){
			dataRow2 = new DataRow();
			dataRow2.set("fdevice", hypervisorId);
		}
		this.setAttribute("historyConfig", dataRow2);
		this.setAttribute("level", level);
		JSONArray storageList = new JSONArray();
		
		JSONArray kpisList = new JSONArray().fromObject(kpis);
		for (DataRow row : rows) {
			row.set("name", row.getString("name"));
			row.set("id", row.getString("hypervisor_id"));
			storageList.add(row);
		}
		this.setAttribute("devList", storageList);
//		this.setAttribute("computerId", computerId);
		this.setAttribute("hypervisorId", hypervisorId);
		this.setAttribute("kpisList", kpisList);
		return new ActionResult("/WEB-INF/views/hypervisor/editHypervisor.jsp");
	}
	public void doHypervisorPrf(){
//		Integer computerId = getIntParameter("computerId");
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
		row.set("fname","Physical");
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
			baseService.updatePrfField(row, "Physical","HOST" , devId, hypervisorId, level);
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
	public void doHypervisorPrfField(){
		JSONObject json = new JSONObject();
	//	Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		Integer level = getIntParameter("level",1);
		if(hypervisorId!=null && hypervisorId>0){
			level = 3;
		}
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);		
		DataRow thead = new DataRow();
		DBPage tbody = null;
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Physical", "HOST",hypervisorId,null);
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("t_res_hypervisor", hypervisorId, "HOST", "Physical", "hypervisor_id", "name");
			dataRow.set("fprfid", "'H1','H2'");
			dataRow.set("fyaxisname", "%");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), "hypervisor_id", "name", "t_res_hypervisor");
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
	public ActionResult doHypervisorPrfPage() {
		doHypervisorPrfField();
		this.setAttribute("hypervisorId", getIntParameter("hypervisorId"));
		this.setAttribute("computerId", getIntParameter("computerId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/hypervisor/ajaxPrfHypervisor.jsp");
		}
		return new ActionResult("/WEB-INF/views/hypervisor/prefHypervisorPage.jsp");
	}
	public ActionResult doVirtualPrfPage(){
		//性能曲线
		doHypervisorPrfField();
		this.setAttribute("vmId", getIntParameter("vmId"));
		this.setAttribute("hypervisorId", getIntParameter("hypervisorId"));
	//	this.setAttribute("hypervisorId", getIntParameter("hypervisorId"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/virtual/ajaxPrfVirtual.jsp");
		}
		return new ActionResult("/WEB-INF/views/virtual/prefVirtualPage.jsp");
	}
	//导出性能信息
	@SuppressWarnings("unchecked")
	public void doExportPrefData() {
		Integer level = getIntParameter("level");
		//Integer computerId = getIntParameter("computerId");
		Integer hypervisorId = getIntParameter("hypervisorId");
		DataRow dataRow =baseService.getPrfFieldInfo(null,level,"Physical", "HOST",hypervisorId,null);
		List<DataRow> devs = baseService.getDeviceofHostInfo(dataRow.getString("fdevice"), "hypervisor_id", "name", "t_res_hypervisor");
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
	//topn图表
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
//		
//		  }
	
	//分页展示
	public ActionResult doAjaxFibrePage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		int type = getIntParameter("type",1);
		Integer hypervisorId = getIntParameter("hypervisorId");
		page = service.getHwresPage(curPage, numPerPage, type,hypervisorId);
		this.setAttribute("fibrePage", page);
		this.setAttribute("hypervisorId", hypervisorId);
		
		return new ActionResult("/WEB-INF/views/hypervisor/ajaxFibre.jsp");
	}
}
