package root.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.UUID;
import com.huiming.service.report.ReportService;
import com.huiming.service.report.TemplateService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ReportTemplate extends SecurityAction{
	TemplateService service = new TemplateService();
	public ActionResult doDefault(){
		DBPage page = service.getPage(null, 1, WebConstants.NumPerPage);
		this.setAttribute("dbPage", page);
		return new ActionResult("/WEB-INF/views/reporttemp/templateList.jsp");
	}
	
	/**
	 * 分页
	 * @return
	 */
	public ActionResult doAjaxPage(){
		String name = getStrParameter("name");
		int curPage = getIntParameter("curPage",1);
		DBPage page = service.getPage(name, curPage, WebConstants.NumPerPage);
		this.setAttribute("dbPage", page);
		return new ActionResult("/WEB-INF/views/reporttemp/ajaxPage.jsp");
	}
	
	/**
	 * 生成报表后添加模板
	 * @return
	 */
	public ActionResult doAddTemplate(){
		ReportService rs = new ReportService();
		DBPage page = null;
		int reportId = getIntParameter("reportId");
		DataRow row = rs.getReportInfo(reportId);
		if(row.size()>0){
			row.set("the_display_name", row.getString("the_display_name")+"模板");
			service.updateTemplate(-1,row);
		}
		int curPage = 1;
		int numPerPage = WebConstants.NumPerPage;
		page = rs.getReportPage(null, null, null, null, curPage, numPerPage);
		this.setAttribute("reportPage", page);
		return new ActionResult("/WEB-INF/views/report/reportList.jsp");
	}
	
	/**
	 * 添加和编辑模板
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doEditTemplate(){
		String id = getStrParameter("id");
		DataRow row = null;
		if(id!=null && id.length()>0){
			row = service.getTemplateInfo(id);
			JSONArray cZnode = new JSONArray().fromObject(row.getString("device_array"));
			JSONArray pZnode = new JSONArray().fromObject(row.getString("perf_array"));
			JSONArray tZnode = new JSONArray().fromObject(row.getString("topn_array"));
			JSONArray aZnode = new JSONArray().fromObject(row.getString("alert_array"));
			JSONObject json = new JSONObject();
			json.put("cZnode", cZnode);
			json.put("pZnode", pZnode);
			json.put("tZnode", tZnode);
			json.put("aZnode", aZnode);
			this.setAttribute("jsonStr", json);
		}
		JSONObject obj = new JSONObject().fromObject(row);
		if(obj==null){
			this.setAttribute("item", "null");
		}else{
			this.setAttribute("item", obj);
		}
		return new ActionResult("/WEB-INF/views/reporttemp/editReport.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doEditTaskTemplate(){
		String id = getStrParameter("id");
		DataRow row = null;
		if(id!=null && id.length()>0){
			row = service.getTemplateInfo(id);
		}
		JSONObject json = new JSONObject().fromObject(row);
		this.setAttribute("editTask", json);
		return new ActionResult("/WEB-INF/views/report/editReport.jsp");
	}
	
	public void doDelReport(){
		int id = getIntParameter("id");
		service.delTemplate(id);
		ResponseHelper.print(getResponse(), "true");
	}
	
	/**
	 * 添加模板
	 */
	public void doAddReportTemplate(){
		String jsonStr = getStrParameter("jsonStr").replaceAll("&amp;quot;", "\"").replaceAll("&amp;nbsp;", " ");
		JSONObject json = JSONObject.fromObject(jsonStr);
		JSONArray aFormArray = json.getJSONArray("cForm");
		JSONArray timeFormArray = json.getJSONArray("timeForm");
		String cZnode = json.getString("cZnode");
		String pZnode = json.getString("pZnode");
		String tZnode = json.getString("tZnode");
		String aZnode = json.getString("aZnode");
		String reportName = "";
		String timeLength = "";
		String timeType = "";
		for (Object obj : aFormArray) {
			JSONObject jsonVal = JSONObject.fromObject(obj);
			if (jsonVal.getString("name").equals("report_name")) {
				reportName = jsonVal.getString("value").replaceAll("&amp;nbsp;", " ")+"报表模板";
			}

		}
		for (Object obj : timeFormArray) {
			JSONObject jsonVal = JSONObject.fromObject(obj);
			if (jsonVal.getString("name").equals("time_type")) {
				timeType = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("time_length")) {
				timeLength = jsonVal.getString("value");
			}
		}
		DataRow row = new DataRow();
		row.set("the_display_name", reportName);
		row.set("report_logo_url", WebConstants.REPORT_LOGO_URL);
		row.set("report_type", 0);
		row.set("timescope_type", 0);
		row.set("time_length", timeLength);
		row.set("time_type", timeType);
		row.set("device_array", cZnode);
		row.set("perf_array", pZnode);
		row.set("topn_array", tZnode);
		row.set("alert_array", aZnode);
		row.set("create_time", new Date());
		JSONObject result = new JSONObject();
		try {
			if(json.getInt("id")>0){
				service.updateTemplate(json.getInt("id"),row);
			}else{
				service.updateTemplate(-1,row);
			}
			result.put("res", "true");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("res", "false");
		}
		ResponseHelper.print(getResponse(), result);
	}
	
	/**
	 * 生成报表
	 */
	public void doCreateReport(){
		ReportService ts = new ReportService();
		ReportMaker rm = new ReportMaker();
		JSONObject obj = new JSONObject();
		String id = getStrParameter("id");
		DataRow row = service.getTemplateInfo(id);
		String realName = UUID.randomUUID().toString();
		row.set("real_name", "report/custom/" + realName + ".htm");
		if(row.getString("report_type").equals("0")){
			row.set("the_display_name", row.getString("the_display_name").substring(0, row.getString("the_display_name").length()-2));
		}else{
			row.set("the_display_name", row.getString("the_display_name"));
		}
		int timeLength = row.getInt("time_length");
		String timeType = row.getString("time_type");
		//处理时间
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		if(timeLength >0 && timeType.length()>0){
			if(timeType.equalsIgnoreCase("day")){
				ca.add(Calendar.DAY_OF_MONTH, -timeLength);
			}else if(timeType.equalsIgnoreCase("week")){
				ca.add(Calendar.DAY_OF_WEEK, -timeLength);
			}else if(timeType.equalsIgnoreCase("month")){
				ca.add(Calendar.MONTH, -timeLength);
			}else if(timeType.equalsIgnoreCase("year")){
				ca.add(Calendar.YEAR, -timeLength);
			}
			String start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ca.getTime());
			String end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			row.set("starttime", start);
			row.set("endtime", end);
		}
		try {
			if(rm.doReportFtl(row, obj)){
				row.set("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				if(row.getString("report_type").equals("1")){
					service.updateTemplate(row.getInt("id"), row);
				}
				ts.addReport(row);
				obj.put("res", "true");
			}else{
				obj.put("res", "false");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseHelper.print(getResponse(), obj);
	}
}
