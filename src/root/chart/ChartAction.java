package root.chart;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.chart.ChartService;
import com.huiming.service.chart.StorageService;
import com.huiming.service.hypervisor.HypervisorService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ChartAction extends SecurityAction {
	public ActionResult doPage(){
		DBPage page = null;
		int modelId = getIntParameter("modelId",1);
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		ChartService service = new ChartService();
		page = service.getChartPage(modelId,curPage, numPerPage);
		this.setAttribute("dbPage", page);
		this.setAttribute("layoutChart", service.getLayout(modelId));
		this.setAttribute("modelId", modelId);
		return new ActionResult("/WEB-INF/views/chart/chartList.jsp");
	}
	
	public ActionResult doPrepareEdit(){
		int id = getIntParameter("id",-1);
		int modelId = getIntParameter("modelId",1);
		if(id != -1){
			ChartService cs = new ChartService();
			this.setAttribute("chartData", cs.getChart(id));
		}
		this.setAttribute("modelId", modelId);
		this.setAttribute("chartJson", getEditJson());
		return new ActionResult("/WEB-INF/views/chart/editChart.jsp");
	}
	
	private JSONObject getEditJson(){
//		JSONArray svcStorages = new JSONArray();
//		JSONArray bspStorages = new JSONArray();
//		JSONArray dsStorages = new JSONArray();
//		JSONArray switchs = new JSONArray();
		JSONArray physicals = new JSONArray();
		JSONArray virtuals = new JSONArray();
		JSONArray apps = new JSONArray();
		StorageService service = new StorageService();
//		for (DataRow storage : service.getAllStorage()) {
//			JSONObject item = new JSONObject();
//			item.put("value",storage.getInt("subsystem_id"));
//			item.put("text",storage.getString("the_display_name"));
//			String os_type = storage.getString("os_type");
//			if(os_type.isEmpty()){
//				os_type = "25";
//			}
//			if(os_type.equals("15") || os_type.equals("37")){
//				os_type = "BSP";
//				bspStorages.add(item);
//			}else if(os_type.equals("21")){
//				os_type = "SVC";
//				svcStorages.add(item);
//			}else{
//				os_type = "DS";
//				dsStorages.add(item);
//			}
//		}
//		for (DataRow storage : service.getAllSwitch()) {
//			JSONObject item = new JSONObject();
//			item.put("value",storage.getInt("switch_id"));
//			item.put("text",storage.getString("the_display_name"));
//			switchs.add(item);
//		}
		//物理机
		HypervisorService hypService=new HypervisorService();
		for (DataRow hypervisor : hypService.getHypervisorName(null, null)) {
			JSONObject item = new JSONObject();
			item.put("value",hypervisor.getInt("hypervisor_id"));
			item.put("text",hypervisor.getString("name"));
			physicals.add(item);
		}
		//虚拟机
		VirtualmachineService virService=new VirtualmachineService();
		for (DataRow virtual : virService.getVirtualName(null, null, null)) {
			JSONObject item = new JSONObject();
			item.put("value",virtual.getInt("vm_id"));
			item.put("text",virtual.getString("name"));
			virtuals.add(item);
		}
		//应用
		BaseprfService bprfService=new BaseprfService();
		for (DataRow app : bprfService.getDeviceofHostInfo(null, "fid", "fname", "tnapps")) {
			JSONObject item = new JSONObject();
			item.put("value",app.getInt("ele_id"));
			item.put("text",app.getString("ele_name"));
			apps.add(item);
		}
		JSONObject targets = new JSONObject();
		for (String osType : new String[]{"Physical","Virtual","App"}) {
			JSONArray items = new JSONArray();
			for (DataRow field : service.getPrfFields(osType)) {
				JSONObject item = new JSONObject();
				item.put("value", field.getString("fid"));
				item.put("text", field.getString("ftitle"));
				items.add(item);
			}
			targets.put(osType, items);
		}
		
		JSONObject json = new JSONObject();
		json.put("Physical", physicals);
		json.put("Virtual", virtuals);
		json.put("App", apps);
		json.put("targets", targets);
		
		return json;
	}
	
	public ActionResult doAjaxPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int modelId = getIntParameter("modelId",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		ChartService service = new ChartService();
		page = service.getChartPage(modelId,curPage, numPerPage);
		this.setAttribute("dbPage", page);
		return new ActionResult("/WEB-INF/views/chart/ajaxChartList.jsp");
	}
	
	public ActionResult doAjaxLayout(){
		int modelId = getIntParameter("modelId",1);
		ChartService service = new ChartService();
		this.setAttribute("layoutChart", service.getLayout(modelId));
		return new ActionResult("/WEB-INF/views/chart/ajaxLayout.jsp");
	}
	
	public ActionResult doAddChart(){
		DataRow chart = new DataRow();
		chart.set("FId", this.getIntParameter("id",0));
		try {
			chart.set("FName", URLDecoder.decode(getStrParameter("name"), "utf-8"));
			chart.set("FYaxisName", URLDecoder.decode(getStrParameter("yaxisname"), "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		chart.set("FDeviceType", this.getStrParameter("type"));
		chart.set("FIsShow", this.getIntParameter("show",0));
		chart.set("FLegend", this.getIntParameter("legend",0));
		chart.set("FRefresh", this.getIntParameter("refresh",5));
		chart.set("FDateRange", this.getStrParameter("dataRange"));
		chart.set("FTimeSize", this.getStrParameter("timeSize"));
		chart.set("FDevice", this.getStrParameter("device"));
		chart.set("FPrfId", this.getStrParameter("prfField"));
		chart.set("FModelId", this.getIntParameter("modelId"));
		chart.set("FChartType", this.getIntParameter("charttype"));
		chart.set("FTopNCount", this.getIntParameter("topcount"));
		ChartService service = new ChartService();
		if(chart.getInt("FId") == 0){
			try {
				chart.set("FIndex", 1);
				chart.set("FSize", 12);
				chart.set("FRow", service.getMaxRow(this.getIntParameter("modelId"))+1);
				service.addChart(chart);
				ResponseHelper.print(getResponse(), "true");
			} catch (Exception e) {
				ResponseHelper.print(getResponse(), "false");
			}
		}else{
			try {
				service.updateChart(chart);
				ResponseHelper.print(getResponse(), "true");
			} catch (Exception e) {
				e.printStackTrace();
				ResponseHelper.print(getResponse(), "false");
			}
		}
		return null;
	}
	
	public void doDelChart(){
		String id = this.getStrParameter("ids");
		ChartService service = new ChartService();
		try {
			service.deleteChart(id);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doShowChart(){
		String id = this.getStrParameter("ids");
		int isShow = this.getIntParameter("isShow",0);
		DataRow chart = new DataRow();
		ChartService service = new ChartService();
		try {
			service.showChart(id, isShow);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
		
	}
	
	public void doSaveLayout(){
		String layouts = "";
		try {
			layouts = URLDecoder.decode(getStrParameter("layoutBox"),"utf-8");
			layouts = layouts.replace("&amp;acute;", "");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int modelId = getIntParameter("modelId");
		JSONArray targets =JSONArray.fromString(layouts);
		ChartService service = new ChartService();
		int flag = service.saveLayout(modelId, targets);
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doEditModel(){
		DataRow row = new DataRow();
		int fid = getIntParameter("fid",0);
		try {
			String name = URLDecoder.decode(getStrParameter("name"),"utf-8");
			row.set("fname", name);
			System.out.println(row.getString("fname"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		row.set("fisshow", getIntParameter("isshow",1));
		ChartService service = new ChartService();
		int flag = -1;
		if(fid == 0){
			flag = service.addModel(row);
		}else{
			row.set("fid", fid);
			flag = service.updateModel(row);
		}
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public void doDelModel(){
		int flag = new ChartService().delModel(getIntParameter("id"));
		if(flag == 0){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	public ActionResult doModelPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		ChartService service = new ChartService();
		page = service.getAllModel(curPage, numPerPage);
		this.setAttribute("dbPage", page);
		return new ActionResult("/WEB-INF/views/chart/modelList.jsp");
	}
	
	public ActionResult doAjaxModelPage(){
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		ChartService service = new ChartService();
		page = service.getAllModel(curPage, numPerPage);
		this.setAttribute("dbPage", page);
		return new ActionResult("/WEB-INF/views/chart/ajaxModelList.jsp");
	}
	
	public ActionResult doPreEditModel(){
		int fid = getIntParameter("fid",0);
		if(fid != 0){
			DataRow row = new ChartService().getModel(fid);
		    setAttribute("model", row);
		}
		return new ActionResult("/WEB-INF/views/chart/editModel.jsp");
	}
}