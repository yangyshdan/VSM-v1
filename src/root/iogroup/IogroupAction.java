package root.iogroup;

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
import com.huiming.service.iogroup.IoGroupService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class IogroupAction extends SecurityAction{
	IoGroupService service = new IoGroupService();
	BaseprfService baseService = new BaseprfService();
	
	public ActionResult doIogroupPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		page = service.getIogroupPage(curPage, numPerPage, null, subsystemId);
		this.setAttribute("iogroupPage", page);
		this.setAttribute("subSystemID", subsystemId);
		
		//性能曲线
		doIogroupPrfField();
		return new ActionResult("/WEB-INF/views/iogroup/iogroupList.jsp");
	}
	
	public ActionResult doAjaxIogroupPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		page = service.getIogroupPage(curPage, numPerPage, name, subsystemId);
		this.setAttribute("iogroupPage", page);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("name", name);
		return new ActionResult("/WEB-INF/views/iogroup/ajaxIogroup.jsp");
	}
	
	public ActionResult doIogroupInfo(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer iogroupId = getIntParameter("iogroupId");
		DataRow row = service.getIogroupInfo(iogroupId);
		this.setAttribute("iogroupInfo", row);
		this.setAttribute("subSystemID", subsystemId);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1,  WebConstants.NumPerPage, -1, subsystemId.toString(),null,iogroupId.toString(), null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		//doIogroupPrfField();
		return new ActionResult("/WEB-INF/views/iogroup/iogroupInfo.jsp");
	}
	
	public ActionResult doIogroupPrfPage(){
		//性能曲线
		doIogroupPrfField();
		this.setAttribute("iogroupId", getIntParameter("iogroupId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/iogroup/ajaxPrfIogroup.jsp");
		}
		return new ActionResult("/WEB-INF/views/iogroup/prefIogroupPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doIogroupSettingPrf(){
		Integer devId = getIntParameter("iogroupId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "IOGroup");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_storage_iogroup", "the_display_name", "io_group_id","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "IOGroup", row.getString("type"),subsystemId,devId);
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "IOGroup", row.getString("type"),subsystemId,null));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/iogroup/IogroupAction?func=IogroupPrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	public void doIogroupPrfField(){
		Integer devId = getIntParameter("iogroupId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"IOGroup", row.getString("type"),subsystemId,devId);
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_storage_iogroup", devId, row.getString("type"), "IOGroup", "io_group_id", "the_display_name");
			dataRow.set("fprfid", "'A693','A699'");
			dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "io_group_id", "the_display_name", "v_res_storage_iogroup");
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
	
	public void doIogroupPrf(){
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
		row.set("fname","IOGroup");
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
			baseService.updatePrfField(row,"IOGroup",storageType,devId,subsystemId,level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doExportIogroupConfigData(){
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		List<DataRow> rows = service.getIogroupList(name, subsystemId);
		String subName = rows.get(0).getString("sub_name");
		if(rows!=null && rows.size()>0){
			String[] title = new String[]{"名称","存储系统","镜像内存(M)","镜像空闲内存(M)","快照内存(M)","快照空闲内存(M)","阵列内存(M)","阵列空闲内存(M)","维护状态"};
			String[] keys = new String[]{"the_display_name","sub_name","mirroring_total_memory","mirroring_free_memory","flash_copy_total_memory","flash_copy_free_memory","raid_total_memory","raid_free_memory","maintenance"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-REDUNDANCY", rows, title, keys);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData(){
		Integer devId = getIntParameter("iogroupId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"IOGroup", row.getString("type"),subsystemId,devId);
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "io_group_id", "the_display_name", "v_res_storage_iogroup");
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
