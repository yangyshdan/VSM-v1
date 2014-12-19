package root.widget;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import root.index.Index;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.chart.ChartService;
import com.huiming.service.report.ReportService;
import com.huiming.service.widget.WidgetService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class WidgetAction extends SecurityAction{
	WidgetService service = new WidgetService();
	
	public ActionResult doAddwidgets(){
		String fid = getStrParameter("fmodelid");
		this.setAttribute("fmodelid", fid);
		return new ActionResult("/WEB-INF/views/editpage/cho-type.jsp");
	}
	
	public ActionResult doAddConsoles(){
		return new ActionResult("/WEB-INF/views/editpage/editConsole.jsp");
	}
	
	/**
	 * 根据类型返回添加的模板
	 * @return
	 */
	public ActionResult doEditwidget(){
		String modelid = getStrParameter("fmodelid");
		String type = getStrParameter("fcharttype");
		JSONObject obj = new JSONObject();
		obj.put("fmodelid", modelid);
		obj.put("fcharttype", type);
		this.setAttribute("item", obj);
		if(type.equals("0")){  //line
			return new ActionResult("/WEB-INF/views/editpage/editline.jsp");
		}else if(type.equals("1")){//c-cloumn
			return new ActionResult("/WEB-INF/views/editpage/edittopn.jsp");
		}else if(type.equals("2")){//r-cloumn
			return new ActionResult("/WEB-INF/views/editpage/editcloumn.jsp");
		}else if(type.equals("3")){//pie
			return new ActionResult("/WEB-INF/views/editpage/editcap.jsp");
		}else if(type.equals("4")){//list
			return new ActionResult("/WEB-INF/views/editpage/editlog.jsp");
		}else if(type.equals("5")){//log
			return new ActionResult("/WEB-INF/views/editpage/editlist.jsp");
		}
		return null;
	}
	
	/**
	 * 组件类型的列表信息
	 */
	@SuppressWarnings("static-access")
	public void doChangeDevType(){
		JSONObject obj = new JSONObject();
		String type=getStrParameter("devType");
		JSONArray jsArray = null;
		JSONArray jsKpis = null;
		if(type.length()>0){
			if(type.equals("SWITCH")){
				jsArray = new JSONArray().fromObject(service.getSwitchList());
			}else if(type.equals("HOST")){
				jsArray = new JSONArray().fromObject(service.getHostList());
			}else if(type.equals("APPLICATION")){
				jsArray = new JSONArray().fromObject(service.getAppList());
			}else if(type.equals("EMC")){
				
			}
			else{
				jsArray = new JSONArray().fromObject(service.getStorageType(type));
			}
			jsKpis = new JSONArray().fromObject(service.getFprffildList(type));
		}
		obj.put("devOption", jsArray);
		obj.put("kpiOption", jsKpis);
		writetopage(obj);
	}
	
	public void doTopnChangeDevType(){
		JSONObject obj = new JSONObject();
		String type=getStrParameter("devType");
		JSONArray jsArray = null;
		JSONArray jsKpis = null;
		if(type.length()>0){
			if(type.equals("SWITCH")){
				jsArray = new JSONArray().fromObject(service.getSwitchList());
			}else if(type.equals("HOST")){
				jsArray = new JSONArray().fromObject(service.getHostList());
			}else if(type.equals("APPLICATION")){
				jsArray = new JSONArray().fromObject(service.getAppList());
			}else if(type.equals("EMC")){
				
			}
			else{
				jsArray = new JSONArray().fromObject(service.getStorageType(type));
			}
			jsKpis = new JSONArray().fromObject(service.getSubtype(type));
		}
		obj.put("devOption", jsArray);
		obj.put("subTypeOption", jsKpis);
		writetopage(obj);
	}
	
	public void doTopnKpi(){
		String devType = getStrParameter("devType");
		String subType = getStrParameter("subType");
		List<DataRow> rows = service.getTopnKPIList(devType,subType);
		writetopage(new JSONArray().fromObject(rows));
	}
	
	/**
	 * 得到部件
	 */
	public void doChangeKpi(){
		String fid = getStrParameter("fid");
		Integer sysId = getIntParameter("device");
		DataRow row = service.getKPIinfo(fid);
		String devType = row.getString("fstoragetype");
		String subtypeId = row.getString("fdevtype");
		
		JSONObject json = new JSONObject();
		ReportService ts = new ReportService();
		List<DataRow> rows = null;
		if(devType.equals("SWITCH")){  //交换机
			if(subtypeId.equalsIgnoreCase("switch")){
				rows = service.getsubSwitchList();
//				rows = ts.getSubgroupDevice(null, null, "switch_id","the_display_name", "v_res_switch");
			}else if(subtypeId.equalsIgnoreCase("port")){
				rows = ts.getSubgroupDevice(sysId, "switch_id", "port_id","the_display_name", "v_res_switch_port");
			}  
		}else if(devType.equals("HOST")){   //主机
			if(subtypeId.equals("Physical")){
				rows = service.getsubHostList();
//				rows = ts.getSubgrouphost(sysId,"hypervisor_id", "hypervisor_id", "name","t_res_hypervisor");
			}else if(subtypeId.equals("Virtual")){
				rows = ts.getSubgrouphost(sysId,"hypervisor_id", "vm_id", "name","t_res_virtualmachine");
			}
		}else if(devType.equals("APPLICATION")){   //应用
			rows = service.getsubAppList();
//			rows = ts.getSubgroupDevice2(sysId, "fid", "fid", "fname", "tnapps",WebConstants.DB_DEFAULT);
		}else if(devType.equals("EMC")){   //EMC存储
			rows = ts.getemcSys(sysId);
		}else{   //各种其他存储
			if(subtypeId.equalsIgnoreCase("storage")){
				rows = ts.getStoragebyOStype(row.getString("fstoragetype"));
			}else if(subtypeId.equalsIgnoreCase("Node")){
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "redundancy_id","the_display_name", "V_RES_REDUNDANCY");
			}else if(subtypeId.equalsIgnoreCase("port")){
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "port_id","the_display_name", "v_res_port");
			}else if(subtypeId.equalsIgnoreCase("mdiskgroup")){ //pool
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "pool_id","the_display_name", "v_res_storage_pool");
			}else if(subtypeId.equalsIgnoreCase("arraysite")){
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "disk_group_id","the_display_name", "v_res_arraysite");
			}else if(subtypeId.equalsIgnoreCase("mdisk")){ //extent
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id","the_display_name", "v_res_storage_extent");
			}else if(subtypeId.equalsIgnoreCase("rank")){
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id","the_display_name", "V_RES_STORAGE_RANK");
			}else if(subtypeId.equalsIgnoreCase("iogroup")){
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "io_group_id","the_display_name", "V_RES_STORAGE_IOGROUP");
			}else if(subtypeId.equalsIgnoreCase("controller")){
				rows = ts.getSubgroupDevice(sysId, "dev_id", "ele_id","ele_name", "PRF_TARGET_DSCONTROLLER");
			}else if(subtypeId.equalsIgnoreCase("volume")){
				rows = ts.getSubgroupDevice(sysId, "subsystem_id", "svid", "the_display_name", "v_res_storage_volume");
			}else if(subtypeId.equalsIgnoreCase("app")){
				rows = ts.getSubgroupDevice2(sysId, "fid", "fid", "fname", "tnapps",WebConstants.DB_DEFAULT);
			}
		}
		JSONArray array = new JSONArray().fromObject(rows);
		json.put("subDev", subtypeId);
		json.put("dataList", array);
		writetopage(json);
	}
	
	/**
	 * 添加控制台
	 */
	public void doAddConsole(){
		ChartService cser = new ChartService();
		JSONObject json = new JSONObject();
		DataRow row = new DataRow();
		try {
			int sid = cser.getmaxConsoleId()+1; 
			row.set("fid", sid);
			row.set("fisshow", 1);
			row.set("fname", new String(getStrParameter("cname").replaceAll("&amp;nbsp;", " ").getBytes(),"gbk"));
			int state = cser.addModel(row);
			json.put("state", state);
			json.putAll(row);
		} catch (Exception e) {
			e.printStackTrace();
		}
		writetopage(json);
	}
	/**
	 * 添加模板
	 */
	public ActionResult doAddWidget(){
		ChartService cser = new ChartService();
		String charttype = getStrParameter("charttype");
		try {
			DataRow row = new DataRow();
			//公共属性
			row.set("fmodelid", getStrParameter("modelid"));
			row.set("fcharttype", charttype);
			row.set("fisshow", 1);
			row.set("fid", cser.getmaxId()+1);
			row.set("fname", getStrParameter("fname").replaceAll("&amp;nbsp;", " "));
//			row.set("fname", new String(getStrParameter("fname").replaceAll("&amp;nbsp;", " ").getBytes(),"UTF8"));
			row.set("frefresh", getStrParameter("refresh"));
			
			if(charttype.equals("5")){
				//list  属性
			}else if(charttype.equals("4")){
				//log 属性
				row.set("fsize", 12);
				cser.addChart(row);
				this.setAttribute("item", row);
				Index index = new Index();
				this.setAttribute("logCount", index.getLogCounts());
				return new ActionResult("/WEB-INF/views/widget/logdata.jsp");
			}else if(charttype.equals("0")){ 
				//highchart-line属性
				row.set("fdevicetype", getStrParameter("devType"));
				row.set("fdevice", getStrParameter("device"));
				row.set("fsubdev", getStrParameter("subDev").replaceAll("&amp;acute;", "\"").replaceAll("&amp;nbsp;", " "));
				row.set("fprfid", getStrParameter("prfField"));
				row.set("fprfview", getStrParameter("view"));
				row.set("fdaterange", getStrParameter("daterange"));
				row.set("ftimesize", getStrParameter("timesize"));
				row.set("fyaxisname", getStrParameter("yname"));
				row.set("flegend", getStrParameter("flegend")==""?0:getStrParameter("flegend"));
				row.set("fsize", getStrParameter("winSize"));
				row.set("ftitle", getStrParameter("title").replaceAll("&amp;nbsp;", " "));
				this.setAttribute("item", row);
				cser.addChart(row);
				return new ActionResult("/WEB-INF/views/widget/drawimg.jsp");
			}else if(charttype.equals("3")){
				//Highchart-pie
				row.set("fsize", getStrParameter("winSize"));
				row.set("fdevicetype", getStrParameter("devType").replaceAll("&amp;nbsp;", " "));
				row.set("fdevice", getStrParameter("device"));
				this.setAttribute("item", row);
				cser.addChart(row);
				return new ActionResult("/WEB-INF/views/widget/drawimg.jsp");
			}else if(charttype.endsWith("1")){
				//Highchart-rcount
				row.set("fdevicetype", getStrParameter("devType"));
				row.set("fdevice", getStrParameter("device"));
				row.set("fsubdev", getStrParameter("subDev").replaceAll("&amp;acute;", "\"").replaceAll("&amp;nbsp;", " "));
				row.set("fprfid", getStrParameter("prfField"));
				row.set("fprfview", getStrParameter("view"));
				row.set("fdaterange", getStrParameter("daterange"));
				row.set("ftimesize", getStrParameter("timesize"));
				row.set("fyaxisname", getStrParameter("yname"));
				row.set("ftopncount", getStrParameter("topncount"));
				row.set("fsize", getStrParameter("winSize"));
				row.set("ftitle", getStrParameter("title").replaceAll("&amp;nbsp;", " "));
				this.setAttribute("item", row);
				cser.addChart(row);
				return new ActionResult("/WEB-INF/views/widget/drawimg.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 处理模块内容
	 * @param row
	 * @return
	 */
	public void doWidgetContent(){
		String fid = getStrParameter("fid");
		DataRow row = service.getWidgetInfo(fid);
		JSONObject json = new JSONObject();
		String chartType = row.getString("fcharttype");
		if(chartType.equals("0")){   	 //line
			//模块图形相关属性
			json.put("title", row.getString("fname"));
			json.put("size", row.getString("fsize"));
			json.put("legend", row.getString("flegend").equals("1")?true:false);
			json.put("yname", row.getString("fyaxisname"));
			json.put("refresh", row.getString("frefresh"));
			String daterange = row.getString("fdaterange");
			Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			if(daterange.equals("day")){
				ca.add(Calendar.DAY_OF_MONTH, -1);
			}else if(daterange.equals("week")){
				ca.add(Calendar.DAY_OF_MONTH, -7);
			}else{
				ca.add(Calendar.MONTH, -1);
			}
			String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
			json.put("smallTitle", startTime+" ~ "+endTime);
			JSONArray array = new JSONArray();
			array = service.getHighchartLineData(row);
			json.put("series", array);
			json.put("charttype", "line");
		}else if(chartType.equals("1")){ //r-cloumn
			String daterange = row.getString("fdaterange");
			Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			if(daterange.equals("day")){
				ca.add(Calendar.DAY_OF_MONTH, -1);
			}else if(daterange.equals("week")){
				ca.add(Calendar.DAY_OF_MONTH, -7);
			}else{
				ca.add(Calendar.MONTH, -1);
			}
			String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
			json.put("smallTitle", startTime+" ~ "+endTime);
			json.put("funits", row.getString("fyaxisname"));
			JSONObject obj = new JSONObject();
			obj = service.getHighchartTopnData(row);
			json.putAll(obj);
			json.put("ftitle", row.getString("ftitle"));
			json.put("charttype", "rcloumn");
		}else if(chartType.equals("2")){ //c-cloumn
			
		}else if(chartType.equals("3")){ //pie
			String displayName = service.getCapacityInfo(row.getString("fdevice")).getString("the_display_name");
			if(displayName.length()>32){
				displayName = displayName.substring(0,32)+"..";
			}
			json.put("smallTitle", displayName+" CapacityInfo");
			JSONArray array = new JSONArray();
			array = service.getHighchartPieData(row);
			json.put("series", array);
			json.put("charttype", "pie");
		}else if(chartType.equals("4")){ //log
			Index index = new Index();
			json.put("charttype", "log");
			json.put("logCount", index.getLogCounts());
		}else if(chartType.equals("5")){ //list
			
		}
		writetopage(json);
	}
	
	/**
	 * 删除模块
	 */
	public void doDelWidget(){
		ChartService cser = new ChartService();
		String fid = getStrParameter("fid");
		DataRow row = cser.getChart(Integer.parseInt(fid));
		JSONObject json = new JSONObject();
		int state = 0;
		try {
			cser.deleteChart(fid);
		} catch (Exception e) {
			e.printStackTrace();
			state = -1;
		}
		json.put("state", state);
		json.put("modelId", row.getString("fmodelid"));
		json.put("fid", row.getString("fid"));
		writetopage(json);
	}
	
	/**
	 * 删除控制台
	 */
	public void doDelConsole(){
		ChartService cser = new ChartService();
		Integer fid = getIntParameter("fid");
		JSONObject json = new JSONObject();
		int state = 0;
		if(fid==1){
			state=2;
		}else{
			state = cser.delModel(fid);
		}
		json.put("state", state);
		writetopage(json);
	}
	
	/**
	 * 向页面写入数据
	 * @param obj
	 */
	private void writetopage(Object obj){
		PrintWriter writer = null;
		try {
			getResponse().setCharacterEncoding("UTF-8");
			writer = getResponse().getWriter();
			writer.print(obj);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				writer.close();
				writer = null;
			}
		}
	}
}
