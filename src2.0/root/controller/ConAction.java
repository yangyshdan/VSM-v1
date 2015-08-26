package root.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.controller.ConService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ConAction extends SecurityAction{
	ConService conService = new ConService();
	BaseprfService baseService = new BaseprfService();
	
	public ActionResult doConPrfPage() {
		doConPrfField();
		this.setAttribute("conId", getIntParameter("conId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/controller/ajaxPrfCon.jsp");
		}
		return new ActionResult("/WEB-INF/views/controller/prefConPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doConSettingPrf(){
		Integer devId = getIntParameter("conId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "Controller");
		List<DataRow> devs = conService.getConList(subsystemId,null);
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Controller", row.getString("type"),subsystemId,devId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Controller", row.getString("type"),subsystemId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/controller/ConAction?func=ConPrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	public ActionResult doConPrfCurvePage() {
		//conId,level,subSystemID,tablePage,isFreshen,,,,
		doConPrfField();  // 这个查询表格
		this.setAttribute("conId", getIntParameter("conId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		return new ActionResult("/WEB-INF/views/controller/conCurvePage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doConSettingPrf2(){
		Integer devId = getIntParameter("conId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "Controller");
		List<DataRow> devs = conService.getConList(subsystemId,null);
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Controller", row.getString("type"),subsystemId,devId,getLoginUserId());
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Controller", row.getString("type"),subsystemId,null,getLoginUserId()));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/controller/ConAction?func=ConPrf");
		return new ActionResult("/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp");
	}
	
	
	public void doConPrfField(){
		Integer devId = getIntParameter("conId",0);
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Controller", row.getString("type"),subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
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
		this.setAttribute("conPrfData", json);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
	}
	
	public void doConPrf() {
		Integer subsystemId = getIntParameter("subSystemID");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de, "multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");
		StringBuffer kpi = new StringBuffer();
		for (int i = 0; i < kpis.length; i++) {
			kpi.append("'" + kpis[i] + "'");
			if (i < kpis.length - 1) {
				kpi.append(",");
			}
		}
		String dev = "";
		if (devices != null && devices.length > 0) {
			StringBuffer device = new StringBuffer();
			for (int i = 0; i < devices.length; i++) {
				device.append(devices[i]);
				if (i < devices.length - 1) {
					device.append(",");
				}
			}
			dev = device.toString();
		} else {
			dev = devId.toString();
		}
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;"," ");
		Integer legend = getIntParameter("legend");
		Integer level = getIntParameter("level");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname", "Controller");
		row.set("fdevicetype", storageType);
		row.set("fdevice", dev);
		row.set("fprfid", kpi.toString());
		row.set("fisshow", 1);
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
		row.set("flegend", legend);
		row.set("fstarttime", startTime);
		row.set("fendtime", endTime);
		row.set("time_type",timeType);
		row.set("fthreshold", threshold);
		row.set("fthreValue", threValue);
		row.set("fuserid", getLoginUserId());
		try {
			baseService.updatePrfField(row, "Controller", storageType, devId,subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData(){
		Integer devId = getIntParameter("conId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Controller", row.getString("type"),subsystemId,devId,getLoginUserId());
		List<DataRow> devs = conService.getConList(subsystemId,dataRow.getString("fdevice"));
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

	private String[] checkStrArray(String[] str, String mach) {
		if (str == null || str.length == 0) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (String string : str) {
			if (!string.equals(mach)) {
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
