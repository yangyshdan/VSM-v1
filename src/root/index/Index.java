package root.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.NumericHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.apps.AppsService;
import com.huiming.service.chart.AlertLogService;
import com.huiming.service.chart.ChartService;
import com.huiming.service.chart.StorageService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;

public class Index extends SecurityAction{
	public ActionResult doDefault(){
		ChartService service = new ChartService();
		this.setAttribute("assetsJson", getAsset());
//		this.setAttribute("chartJson", getChartJson());
		this.setAttribute("logCount", getLogCounts());
		this.setAttribute("dashList", service.getModelList());
		return new ActionResult("/WEB-INF/views/sys/index.jsp");
	}
	
	public ActionResult doGetWidget(){
		ChartService service = new ChartService();
		Integer fid = getIntParameter("fid");
		List<DataRow> charts = service.getLayout(fid);
		if(charts!=null && charts.size()>0){
			DataRow row = null;
			for (DataRow dataRow : charts) {
				if(dataRow.getString("fcharttype").equals("4")){
					row = dataRow;
					this.setAttribute("logItem", dataRow);
//					this.setAttribute("logCount", getLogCounts());
					break;
				}
			}
			if(row!=null){
				charts.remove(row);
			}
		}
		this.setAttribute("charts", charts);
		this.setAttribute("fid", fid);
		return new ActionResult("/WEB-INF/views/alert/teCharts.jsp");
	}
	
	public ActionResult doAjaxAlertLog(){
		List<DataRow> logs = new AlertLogService().getAlertLog();
		this.setAttribute("logPage", logs);
		return new ActionResult("/WEB-INF/views/sys/ajaxLog.jsp");
	}
	
	private JSONObject getCapacity(){
		StorageService service = new StorageService();
		DataRow row = service.getTotalCapacity();
		JSONObject json = new  JSONObject();
		JSONObject allocated = new  JSONObject();
		JSONObject available = new  JSONObject();
		JSONArray series = new JSONArray();
		allocated.put("name", "空余容量(T)");
		allocated.put("y", NumericHelper.round(row.getDouble("allocated")/1000, 2));
		allocated.put("color", "#6CCA16");
		series.add(allocated);
		available.put("name", "已用容量(T)");
		available.put("y", NumericHelper.round(row.getDouble("available")/1000, 2));
		available.put("color", "#C8C1CF");
		series.add(available);
		
		json.put("series", series);
		return json;
	}
	

	
	public void doAjaxCapacity(){
		JSONObject json = new JSONObject();
		json.put("capacityJson", getCapacity());
		json.put("assetsJson", getAsset());
		writeDataToPage(json.toString());
	}
	
	private JSONArray getAsset(){
		StorageService service = new StorageService();
		List<DataRow> list = service.getAsset();
		list.addAll(new AppsService().getAsset());
		JSONArray assets = new JSONArray();
		int emcCount = service.getTotalEmc();
		for (DataRow row : list) {
			JSONObject json = new JSONObject();
			json.put("type", row.getString("systemtype"));
			int count = row.getInt("counts");
			if(row.getString("systemtype").equals("storage")){
				count +=  emcCount;
			}
			json.put("counts", count);
			assets.add(json);
		}
		return assets;
	}
	
	public void doAjaxAssets(){
		JSONObject json = new JSONObject();
		json.put("assets", getAsset());
		writeDataToPage(json.toString());
	}
	
	public void doAjaxChart(){
		int id = this.getIntParameter("id",-1);
		if(id != -1){
			DataRow chart = new ChartService().getChart(id);
			if(chart!=null){
				List<DataRow> list = new StorageService().getPrfData(chart);
				JSONObject json = getPrfJson(chart,list);
				writeDataToPage(json.toString());
			}
		}
	}
	
	private JSONObject getPrfJson(DataRow chart,List<DataRow> prfData){
		String[] prfFields = chart.getString("fprfid").split(",");
		String[] devs = chart.getString("fdevice").split(",");
		Map<String, JSONArray> data = new HashMap<String, JSONArray>();
		Map<String, String> xAxis = new HashMap<String, String>();
		StorageService service = new StorageService();
		JSONArray series = new JSONArray();
		for (String dev : devs) {
			for (String field : prfFields) {
				data.put(dev+"-"+field, new JSONArray());
				xAxis.put(dev+"-"+field, new String());
			}
		}
		for (String field : prfFields) {
			String title = service.getFieldName(field);
			for (DataRow row : prfData) {
				JSONArray args = new JSONArray();
				Long time = null;
				try {
					time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(row.getString("prf_timestamp")).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				args.add(time);
				args.add(NumericHelper.round(Double.parseDouble(row.getString(field.toLowerCase())), 2));
				data.get(row.getString("dev_id")+"-"+field).add(args);
				if(prfFields.length>1){
					xAxis.put(row.getString("dev_id")+"-"+field,row.getString("dev_name")+"["+title+"]");
				}else{
					xAxis.put(row.getString("dev_id")+"-"+field,row.getString("dev_name"));
				}
			}
		}
		for (String id : xAxis.keySet()) {
			JSONObject sery = new JSONObject();
			sery.put("name", xAxis.get(id));
			sery.put("data", data.get(id));
			series.add(sery);
		}
		JSONObject json = new JSONObject();
		json.put("yaxisname", chart.getString("fyaxisname"));
		json.put("legend", chart.getInt("flegend")==0?false:true);
		json.put("series", series);
		json.put("id", chart.getInt("fid"));
		
		return json;
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
	
	public ActionResult doLayout(){
		return new ActionResult("/WEB-INF/views/chart/chartLayout.jsp");
	}
	
	public ActionResult doPrepareEdit(){
		return new ActionResult("/WEB-INF/views/index/editModel.jsp");
	}
	
	
	public JSONObject getChartJson(){
		JSONObject json = new JSONObject();
		JSONArray models = new JSONArray();
		
		ChartService service = new ChartService();
		List<DataRow> charts = service.getChartList();
		for (DataRow model : service.getModelList()) {
			JSONObject m = new JSONObject();
			JSONArray chartArray = new JSONArray();
			JSONArray sortArray = new JSONArray();
			m.put("id", model.getInt("fid"));
			m.put("name", model.getString("fname"));
			models.add(m);
			
			for (DataRow chart : charts) {
				if(chart.getInt("fmodelid") == model.getInt("fid")){
					JSONObject c = new JSONObject();
					c.put("fid", chart.getInt("fid"));
					c.put("fname", chart.getString("fname"));
					c.put("frefresh", chart.getInt("frefresh"));
					c.put("frow", chart.getInt("frow"));
					c.put("findex", chart.getInt("findex"));
					c.put("fcharttype", chart.getInt("fcharttype"));
					c.put("fdevice", chart.getString("fdevice"));
					c.put("fprfid", chart.getString("fprfid"));
					chartArray.add(c);
				}
			}
			
			for (DataRow sort : service.getSort(model.getInt("fid"))) {
				JSONObject sortJson = new JSONObject();
				sortJson.put("frow", sort.getInt("frow"));
				sortJson.put("countChart", sort.getInt("countchart"));
				sortArray.add(sortJson);
			}
			json.put("model_"+model.getInt("fid"), chartArray);
			json.put("sort_"+model.getInt("fid"), sortArray);
		}
		json.put("models", models);
		return json;
	}
	
	public JSONObject getLogCounts(){
		DeviceAlertService service = new DeviceAlertService();
		JSONObject json = new JSONObject();
		JSONObject topLevel = new JSONObject();
		JSONObject detail = new JSONObject();
		Set<String> levels = new HashSet<String>();
		List<DataRow> logs = service.getNewLevel();
		for (String level : new String[]{"App","Physical","Virtual","Switch","Storage"}) {
			topLevel.put(level, -1);
			for (DataRow log : logs) {
				levels.add(log.getString("ftoptype"));
				if(log.getString("ftoptype").equalsIgnoreCase(level)){
					if(Integer.parseInt(topLevel.get(level).toString()) < log.getInt("flevel")){
						topLevel.put(level,  log.getInt("flevel"));
					}
					if(!detail.containsKey(level)){
						JSONObject count = new JSONObject();
						count.put("infoc", 0);
						count.put("warningc", 0);
						count.put("errorc", 0);
						count.put("info", new JSONArray());
						count.put("warning", new JSONArray());
						count.put("error", new JSONArray());
						detail.put(level, count);
					}
					switch ( log.getInt("flevel")) {
					case 0:
						detail.getJSONObject(level).put("infoc", detail.getJSONObject(level).getInt("infoc")+log.getInt("logcount"));
						detail.getJSONObject(level).getJSONArray("info").add(log.getString("ftopname"));
						break;
					case 1:
						detail.getJSONObject(level).put("warningc", detail.getJSONObject(level).getInt("warningc")+log.getInt("logcount"));
						detail.getJSONObject(level).getJSONArray("warning").add(log.getString("ftopname"));
						break;
					case 2:
						detail.getJSONObject(level).put("errorc", detail.getJSONObject(level).getInt("errorc")+log.getInt("logcount"));
						detail.getJSONObject(level).getJSONArray("error").add(log.getString("ftopname"));
						break;
					}
				}
			}
		}
		json.put("topLevel", topLevel);
		json.put("detail", detail);
//		for (DataRow row : logs) {
//			String sev = row.getString("sev");
//			int count = row.getInt("logcount");
//			switch (row.getInt("toplevel_type")) {
//			case 1:
//				if(sev.equals("I")){
//					hostLog.put("info", count);
//				}else if(sev.equals("W")){
//					hostLog.put("warning", count);
//				}else if(sev.equals("E")){
//					hostLog.put("error",count);
//				}
//				break;
//			case 78:
//				if(sev.equals("I")){
//					storageLog.put("info", count);
//				}else if(sev.equals("W")){
//					storageLog.put("warning", count);
//				}else if(sev.equals("E")){
//					storageLog.put("error",count);
//				}
//				break;
//			case 121:
//				if(sev.equals("I")){
//					switchLog.put("info", count);
//				}else if(sev.equals("W")){
//					switchLog.put("warning", count);
//				}else if(sev.equals("E")){
//					switchLog.put("error",count);
//				}
//				break;
//			case 114:
//				if(sev.equals("I")){
//					fabricLog.put("info", count);
//				}else if(sev.equals("W")){
//					fabricLog.put("warning", count);
//				}else if(sev.equals("E")){
//					fabricLog.put("error",count);
//				}
//				break;
//			}
//		}
//		json.put("hostLog", hostLog);
//		json.put("switchLog", switchLog);
//		json.put("storageLog", storageLog);
//		json.put("fabricLog", fabricLog);
		return json;
	}
	
}
