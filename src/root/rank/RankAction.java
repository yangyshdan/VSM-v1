package root.rank;

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
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.rank.RankService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class RankAction extends SecurityAction{
	BaseprfService baseService = new BaseprfService();
	RankService service = new RankService();
	@SuppressWarnings("static-access")
	public ActionResult doRankPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		page = service.getRankPage(curPage, numPerPage, null, null, subsystemId);

		List<DataRow> rows = service.getCapacityInfo(subsystemId);
		Map<Object, Object> map = new HashMap<Object, Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add(dataRow.getString("the_display_name").trim());
			dataJson.put("devId", dataRow.getInt("devid"));
			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("number"))));
			jarray.add(dataJson);
		}
		map.put("name", "容量");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("rankPage", page);
		this.setAttribute("subSystemID", subsystemId);
		//doRankPrfField();
		return new ActionResult("/WEB-INF/views/rank/rankList.jsp");
	}
	
	public ActionResult doAjaxRankPage() {
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String type = getStrParameter("rankType").replaceAll("&amp;nbsp;", " ");
		page = service.getRankPage(curPage, numPerPage, name, type, subsystemId);
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("name", name);
		this.setAttribute("type", type);
		this.setAttribute("rankPage", page);
		return new ActionResult("/WEB-INF/views/rank/ajaxRank.jsp");
	}
	
	public ActionResult doRankInfo() {
		Integer devId = getIntParameter("rankId");
		Integer subsystemId = getIntParameter("subSystemID");
		this.setAttribute("rankInfo", service.getRankInfo(devId));
		this.setAttribute("rankId", devId);
		this.setAttribute("subSystemID", subsystemId);
		//告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage=deviceService.getLogPage(1, WebConstants.NumPerPage, -1,subsystemId.toString(),null, devId.toString(), null, "Storage", -1, -1, null, null);
		setAttribute("deviceLogPage",devicePage);
		//doRankPrfField();
		return new ActionResult("/WEB-INF/views/rank/rankInfo.jsp");
	}
	
	public ActionResult doRankPrfPage(){
		//性能曲线
		doRankPrfField();
		this.setAttribute("rankId", getIntParameter("rankId"));
		this.setAttribute("subSystemID", getIntParameter("subSystemID"));
		String tablePage = getStrParameter("tablePage");
		if(tablePage!=null && tablePage.length()>0){
			return new ActionResult("/WEB-INF/views/rank/ajaxPrfRank.jsp");
		}
		return new ActionResult("/WEB-INF/views/rank/prefRankPage.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doRankSettingPrf(){
		Integer devId = getIntParameter("rankId");
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		List<DataRow> kpis = baseService.getView(row.getString("type"), "Rank");
		List<DataRow> devs = baseService.getdevInfo(subsystemId, "v_res_storage_rank", "the_display_name", "storage_extent_id","subsystem_id");
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("subSystemID", subsystemId);
		this.setAttribute("storageInfo", row);
		if(devId!=null && devId>0){
			DataRow config = baseService.getPrfFieldInfo(null,3, "Rank", row.getString("type"),subsystemId,devId);
			if(config==null){
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				this.setAttribute("historyConfig",drow);
			}else{
				this.setAttribute("historyConfig",config);
			}
			this.setAttribute("level", 3);
		}else{
			this.setAttribute("historyConfig", baseService.getPrfFieldInfo(null,2, "Rank", row.getString("type"),subsystemId,null));
			this.setAttribute("level", 2);
		}
		this.setAttribute("url","servlet/rank/RankAction?func=RankPrf");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	public void doRankPrfField(){
		Integer devId = getIntParameter("rankId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Rank", row.getString("type"),subsystemId,devId);
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if(dataRow==null || dataRow.size()==0){
			dataRow = baseService.getDefaultRow("v_res_storage_rank", devId, row.getString("type"), "Rank", "storage_extent_id", "the_display_name");
			dataRow.set("fprfid", "'A796','A799'");
			dataRow.set("fyaxisname", "Ops/Sec,MB/Sec");
		}
		if(dataRow!=null && dataRow.size()>0){
			List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "storage_extent_id", "the_display_name", "v_res_storage_rank");
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
	
	public void doRankPrf() {
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
		row.set("fname", "Rank");
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
		try {
			baseService.updatePrfField(row, "Rank", storageType, devId,subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doExportRankConfigData(){
		Integer subsystemId = getIntParameter("subSystemID");
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String type = getStrParameter("rankType").replaceAll("&amp;nbsp;", " ");
		List<DataRow> rows = service.getRankList(name, type, subsystemId);
		if(rows!=null && rows.size()>0){
			String subName = rows.get(0).getString("sub_name");
			String[] title = new String[]{"名称","存储系统","扩展卷数","总容量(G)","可用容量(G)","存储池","操作状态"};
			String[] keys = new String[]{"the_display_name","sub_name","the_extent_volume","the_total_space","the_available_space","pool_name","the_operational_status"};
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), subName+"-Rank", rows, title, keys);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doExportPrefData(){
		Integer devId = getIntParameter("rankId");
		Integer level = getIntParameter("level",devId==0?2:3);
		Integer subsystemId = getIntParameter("subSystemID");
		DataRow row = baseService.getStorageType(subsystemId);
		DataRow dataRow = baseService.getPrfFieldInfo(null,level,"Rank", row.getString("type"),subsystemId,devId);
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "storage_extent_id", "the_display_name", "v_res_storage_rank");
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
