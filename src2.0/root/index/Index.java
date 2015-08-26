package root.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.NumericHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.chart.AlertLogService;
import com.huiming.service.chart.ChartService;
import com.huiming.service.chart.StorageService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.jeedsoft.license.License;
import com.jeedsoft.license.LicenseReader;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class Index extends SecurityAction {
	
	/**
	 * 首页
	 */
	public ActionResult doDefault(){
		Long userId = (Long) getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
		ChartService service = new ChartService();
		setAttribute("assetsJson", getAsset());
//		setAttribute("chartJson", getChartJson(userId));
		setAttribute("logCount", getLogCounts());
		setAttribute("dashList", service.getModelList(userId));
		doCheckLicExpire();
		return new ActionResult("/WEB-INF/views/sys/index.jsp");
	}
	
	/**
	 * 检测license是否到期,提前15天通知用户
	 */
	public void doCheckLicExpire() {
		String showMsg = null;
		int showTag = (Integer)getSession().getAttribute(WebConstants.SHOW_TAG);
		//登录后首次显示
		if (showTag == 0) {
			String rootPath = this.getClass().getResource("/").getPath().replaceAll("%20", " ");
			String licenseFile = rootPath + "license.lic";
			License license = LicenseReader.read(licenseFile);
			//比较当前时间和license到期时间的大小
			Date licExpireDate = license.getExpireDate();
			Date systemDate = new Date();
			if (systemDate.before(licExpireDate)) {
				Long time1 = licExpireDate.getTime();
				Long time2 = systemDate.getTime();
				Long timeLength = (time1 - time2)/1000/60/60/24;
				//小于或等于15天,则提示
				if (timeLength <= 15) {
					showMsg = new StringBuffer("本系统服务还有").append(timeLength).append("天将到期!").toString();
					getSession().setAttribute(WebConstants.SHOW_TAG, 1);
				}
			}
		}
		setAttribute("systemMsg", showMsg);
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
		return new ActionResult("/WEB-INF/views/editpage/teCharts.jsp");
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
	
	/**
	 * 获取设备,统计设备数量
	 * @return
	 */
	private JSONArray getAsset(){
		//获取用户可见设备
		String phyLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String vmLimitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String srStoLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		String tpcStoLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		StorageService service = new StorageService();
		List<DataRow> list = new ArrayList<DataRow>(); 
		//如果集成TPC,则查询DB2的存储,交换机列表
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			list = service.getTpcAsset(tpcStoLimitIds, switchLimitIds);
		} else {
			DataRow tpcSto = new DataRow();
			tpcSto.set("systemtype","storage");
			tpcSto.set("counts", 0);
			DataRow tpcSwitch = new DataRow();
			tpcSwitch.set("systemtype","switch");
			tpcSto.set("counts", 0);
			list.add(tpcSto);
			list.add(tpcSwitch);
		}
		list.addAll(service.getSrAsset(phyLimitIds,vmLimitIds));
		int srStorageCount = service.getSrStorageCount(srStoLimitIds);
		JSONArray assets = new JSONArray();
		for (DataRow row : list) {
			JSONObject json = new JSONObject();
			json.put("type", row.getString("systemtype"));
			int count = row.getInt("counts");
			if(row.getString("systemtype").equals("storage")){
				count = count + srStorageCount;
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
		DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (String field : prfFields) {
			String title = service.getFieldName(field);
			for (DataRow row : prfData) {
				JSONArray args = new JSONArray();
				Long time = null;
				try {
					time = dateFmt.parse(row.getString("prf_timestamp")).getTime();
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
		json.put("legend", chart.getInt("flegend") != 0);
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
	
	public JSONObject getChartJson(Long userId) {
		JSONObject json = new JSONObject();
		JSONArray models = new JSONArray();
		
		ChartService service = new ChartService();
		List<DataRow> charts = service.getChartList();
		for (DataRow model : service.getModelList(userId)) {
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
	
	/**
	 * 统计设备告警数量
	 * @return
	 */
	public JSONObject getLogCounts(){
		//获取用户可见设备
		String phyLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String vmLimitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String storageLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, null, null);
		DeviceAlertService service = new DeviceAlertService();
		JSONObject json = new JSONObject();
		JSONObject topLevel = new JSONObject();
		JSONObject detail = new JSONObject();
		Set<String> levels = new HashSet<String>();
		//获取设备告警数据
		List<DataRow> logs = service.getDeviceAlertSummary(phyLimitIds,vmLimitIds,storageLimitIds,switchLimitIds);
		for (String level : new String[]{SrContant.SUBDEVTYPE_PHYSICAL,SrContant.SUBDEVTYPE_VIRTUAL,SrContant.SUBDEVTYPE_STORAGE,SrContant.SUBDEVTYPE_SWITCH}) {
			topLevel.put(level, -1);
			for (DataRow log : logs) {
				levels.add(log.getString("ftoptype"));
				if (log.getString("ftoptype").equalsIgnoreCase(level)) {
					if (Integer.parseInt(topLevel.get(level).toString()) < log.getInt("flevel")) {
						topLevel.put(level, log.getInt("flevel"));
					}
					if (!detail.containsKey(level)) {
						JSONObject count = new JSONObject();
						count.put("infoc", 0);
						count.put("warningc", 0);
						count.put("errorc", 0);
						count.put("info", new JSONArray());
						count.put("warning", new JSONArray());
						count.put("error", new JSONArray());
						detail.put(level, count);
					}
					JSONObject device = new JSONObject();
					device.put("topId", log.getString("ftopid"));
					device.put("resId", log.getString("fresourceid"));
					device.put("resName", log.getString("ftopname"));
					switch (log.getInt("flevel")) {
					case 0:
						detail.getJSONObject(level).put("infoc", detail.getJSONObject(level).getInt("infoc")+log.getInt("logcount"));
						detail.getJSONObject(level).getJSONArray("info").add(device);
						break;
					case 1:
						detail.getJSONObject(level).put("warningc", detail.getJSONObject(level).getInt("warningc")+log.getInt("logcount"));
						detail.getJSONObject(level).getJSONArray("warning").add(device);
						break;
					case 2:
						detail.getJSONObject(level).put("errorc", detail.getJSONObject(level).getInt("errorc")+log.getInt("logcount"));
						detail.getJSONObject(level).getJSONArray("error").add(device);
						break;
					}
				}
			}
		}
		json.put("topLevel", topLevel);
		json.put("detail", detail);
		return json;
	}
}
