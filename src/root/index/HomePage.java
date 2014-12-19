package root.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.NumericHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.chart.AlertLogService;
import com.huiming.service.chart.ChartService;
import com.huiming.service.chart.StorageService;
import com.huiming.service.topn.TopnService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;

public class HomePage extends SecurityAction{
	
	public ActionResult doDefault(){
		AlertLogService service = new AlertLogService();
		List<DataRow> logs = service.getAlertLog();
		this.setAttribute("logPage", logs);
		this.setAttribute("capacityJson", getCapacity());
		this.setAttribute("assetsJson", getAsset());
		JSONArray chartList = new JSONArray();
		for (DataRow cc :  new ChartService().getChartList()) {
			JSONObject json = new JSONObject();
			json.put("fid", cc.getInt("fid"));
			json.put("fname", cc.getString("fname"));
			json.put("frefresh", cc.getInt("frefresh"));
			json.put("frow", cc.getInt("frow"));
			json.put("findex", cc.getInt("findex"));
			chartList.add(json);
		}
		this.setAttribute("chartList",chartList);
		JSONArray chartSort = new JSONArray();
		for (DataRow cc :  new ChartService().getSort(1)) {
			JSONObject json = new JSONObject();
			json.put("frow", cc.getInt("frow"));
			json.put("countChart", cc.getInt("countchart"));
			chartSort.add(json);
		}
		this.setAttribute("chartSort", chartSort);
		return new ActionResult("/WEB-INF/views/sys/index.jsp");
	}
	
	public ActionResult doAjaxAlertLog(){
		List<DataRow> logs = new AlertLogService().getAlertLog();
		this.setAttribute("logPage", logs);
		return new ActionResult("/WEB-INF/views/sys/ajaxLog.jsp");
	}
	
	private JSONObject getCapacity(){
		StorageService service = new StorageService();
		List<DataRow> list = service.getCapacity();
		JSONObject json = new  JSONObject();
		JSONArray series = new JSONArray();
		JSONArray storages = new JSONArray();
		JSONArray allocated = new JSONArray();
		JSONArray available = new JSONArray();
		for (DataRow dataRow : list) {
			if(dataRow != null){
				storages.add(dataRow.getString("the_display_name"));
				allocated.add(NumericHelper.round(dataRow.getDouble("allocated")/1000,2));
				available.add(NumericHelper.round(dataRow.getDouble("available")/1000,2));
			}
		}
		JSONObject availableSerie = new JSONObject();
		availableSerie.put("name", "空余容量(T)");
		availableSerie.put("data", available);
		availableSerie.put("color", "#59EBE3");
		series.add(availableSerie);
		JSONObject allocatedSerie = new JSONObject();
		allocatedSerie.put("name", "已用容量(T)");
		allocatedSerie.put("data", allocated);
		allocatedSerie.put("color", "#058DC7");
		series.add(allocatedSerie);
		
		json.put("storageName", storages);
		json.put("series", series);
		return json;
	}
	
	public void doAjaxCapacity(){
		JSONObject json = new JSONObject();
		json.put("capacityJson", getCapacity());
		json.put("assetsJson", getAsset());
		writeDataToPage(json.toString());
	}
	
	private JSONObject getAsset(){
		StorageService service = new StorageService();
		JSONObject json = new JSONObject();
		JSONArray names = new JSONArray();
		JSONArray assets = new JSONArray();
		for (DataRow row : service.getAsset()) {
			names.add(row.getString("systemtype"));
			assets.add(row.getInt("counts"));
		}
		JSONObject serie = new JSONObject();
		serie.put("data", assets);
		serie.put("name", "资产");
		serie.put("color", "#EB6E00");
		JSONArray series = new JSONArray().fromObject(serie);
		json.put("names", names);
		json.put("series", series);
		
		return json;
	}
	
	public void doAjaxAssets(){
		JSONObject json = getAsset();
		writeDataToPage(json.toString());
	}
	
	public void doAjaxChart(){
		int id = this.getIntParameter("id",-1);
		String FPrfId = this.getStrParameter("FPrfId");
		if(id != -1){
			DataRow chart = new ChartService().getChart(id);
			if(chart!=null){
				if(chart.getInt("fcharttype")==0){
				List<DataRow> list = new StorageService().getPrfData1(chart);
				JSONObject json = getPrfJson(chart,list);
				writeDataToPage(json.toString());
				}else if(chart.getInt("fcharttype")==1){
					if(FPrfId!=null&&FPrfId.length()>0){
						JSONObject json =getTopChart(chart,FPrfId);
						writeDataToPage(json.toString());
					}else{
						String[] devs = chart.getString("fprfid").split(",");
						for(String dev:devs){
							JSONObject json =getTopChart(chart,dev);
							writeDataToPage(json.toString());
						}
					}
				}
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
		BaseprfService baseService = new BaseprfService();
		for (String dev : devs) {
			for (String field : prfFields) {
				data.put(dev+"-"+field, new JSONArray());
				xAxis.put(dev+"-"+field, new String());
			}
		}
		for (String field : prfFields) {
			String title = service.getFieldName(field);
			List<DataRow> kpis = baseService.getKPIInfo("'"+field+"'");
			for (DataRow row : prfData) {
				JSONArray args = new JSONArray();
				Long time = null;
				try {
					time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(row.getString("prf_timestamp")).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				args.add(time);
				//args.add(kpis.get(0).getString("funits"));
				args.add(NumericHelper.round(Double.parseDouble(StringHelper.isEmpty(row.getString(field.toLowerCase()))?"0":row.getString(field.toLowerCase())), 2));
				data.get(row.getString("ele_id")+"-"+field).add(args);
				if(prfFields.length>1){
					xAxis.put(row.getString("ele_id")+"-"+field,row.getString("dev_name")+"["+title+"]");
				}else{
					xAxis.put(row.getString("ele_id")+"-"+field,row.getString("dev_name"));
				}
			//	JSONArray objs = new JSONArray();
			//	objs.add(kpis.get(0).getString("funits"));
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
	
	//topn图表
	 public JSONObject getTopChart(DataRow chart,String keyName) {
		    JSONObject json = new JSONObject();
		    StorageService stService =new StorageService();
		    List<DataRow> tops = stService.getTopNData(chart, keyName);
		    BaseprfService prfService=new BaseprfService();
		    List<DataRow> tnPrfFields=prfService.getKPIInfo("'"+keyName+"'");
		    JSONArray Name = new JSONArray();
		    JSONArray eleId = new JSONArray();
		    JSONArray Data = new JSONArray();
		    JSONArray Time = new JSONArray();
//		    for (DataRow top : tops) {
		    for(int i=0;i<tops.size();i++){
		    	JSONObject objs = new JSONObject();
		    	eleId.add(tops.get(i).getString("ele_id"));
		    	Name.add(tops.get(i).getString("dev_name"));
		    	if(i==0){
		    		objs.put("dataLabels", "{style: {fontWeight:'bold',color: 'red'}}");
		    	}
		    	objs.put("y",Double.valueOf(NumericHelper.round(tops.get(i).getDouble("prf"), 2)));
		    	Data.add(objs);
		    	Time.add(tops.get(i).getString("prf_timestamp"));
		    }
		    json.put("eleId", eleId);
		    json.put("prfName", Name);
		    json.put("prfData", Data);
		    json.put("time", Time);
		    json.put("id", chart.getInt("fid"));
		    json.put("keyName",keyName);
		    json.put("deviceType", chart.getString("fdevicetype"));
		    json.put("toptitle",tnPrfFields.get(0).getString("ftitle"));
		    json.put("funit",tnPrfFields.get(0).getString("funits"));
		    return json;
//		
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
