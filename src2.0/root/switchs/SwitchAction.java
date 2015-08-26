package root.switchs;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.switchport.SwitchportService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.zone.ZoneService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;

public class SwitchAction extends SecurityAction {
	SwitchService service = new SwitchService();
	BaseprfService baseService = new BaseprfService();

	@SuppressWarnings("unchecked")
	public ActionResult doSwitchPage() {
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		//获取用户可见的交换机
		String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		DBPage switchPage = null;
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			switchPage = service.getSwitchPage(curPage, numPerPage, null, null, null, null, limitIds);
			List<DataRow> switchList = switchPage.getData();
			List<DataRow> reswitchList = service.getResSwitchList(limitIds);
			List<DataRow> list = new ArrayList<DataRow>();
			for (DataRow row : switchList) {
				for (DataRow row1 : reswitchList) {
					if (row.getInt("switch_id") == row1.getInt("switch_id")) {
						row.set("engine_status", row1.get("engine_status"));
						row.set("power_status", row1.get("power_status"));
						row.set("port_status", row1.get("port_status"));
						row.set("fiber_status", row1.get("fiber_status"));
						list.add(row);
					}
				}
			}
			switchList.removeAll(list);
			for (DataRow row2 : switchList) {
				row2.set("engine_status", "");
				row2.set("power_status", "");
				row2.set("port_status", "");
				row2.set("fiber_status", "");
				row2.set("fan_status", "");
				list.add(row2);
			}
			switchPage.setData(list);
		}
		setAttribute("switchPage", switchPage);
		return new ActionResult("/WEB-INF/views/switch/switchList.jsp");
	}

	/**
	 * 总览刷新
	 * */
	public ActionResult doDrawPerffile() {
		setTpir();
		return new ActionResult("/WEB-INF/views/switch/ajaxSwitchChart2.jsp");
	}

	/**
	 * 获取设备告警信息
	 * @param devId
	 */
	public void setDeviceLogInfo(String devId) {
		final String count = "count";
		// 获取告警信息
		DeviceAlertService deviceService = new DeviceAlertService();
		List<DataRow> resultList = deviceService.getCompDeviceLogList(devId,
				null, SrContant.SUBDEVTYPE_SWITCH, null, null, null, 0, 0,
				null, null);
		DataRow errorRow = new DataRow();
		DataRow warnRow = new DataRow();
		DataRow infoRow = new DataRow();
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				DataRow row = resultList.get(i);
				int fLevel = row.getInt("flevel");
				int fCount = row.getInt("fcount");
				// Level(Critical:2;Warning:1;Info:0)
				if (fLevel == 2) {
					errorRow.set(count, fCount);
				} else if (fLevel == 1) {
					warnRow.set(count, fCount);
				} else if (fLevel == 0) {
					infoRow.set(count, fCount);
				}
			}
		} else {
			errorRow.set(count, 0);
			warnRow.set(count, 0);
			infoRow.set(count, 0);
		}
		setAttribute("errorData", errorRow);
		setAttribute("warnData", warnRow);
		setAttribute("infoData", infoRow);
	}

	/**
	 * 处理 Total Port Packet Rate 处理 事件
	 * */
	@SuppressWarnings("unchecked")
	public void setTpir() {
		Date date = new Date();
		String timeRange = getStrParameter("timeRange");
		String startTime = null;
		String endTime = SrContant.getTimeFormat(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (StringHelper.isNotEmpty(timeRange) && StringHelper.isNotBlank(timeRange)) {
			// Hour
			if (timeRange.equals("hour")) {
				calendar.add(Calendar.HOUR, -1);
				// calendar.add(Calendar.YEAR, -2);
				// Day
			} else if (timeRange.equals("day")) {
				calendar.add(Calendar.DATE, -1);
				// calendar.add(Calendar.YEAR, -2);
				// Week
			} else if (timeRange.equals("week")) {
				calendar.add(Calendar.WEEK_OF_MONTH, -1);
				// calendar.add(Calendar.YEAR, -2);
				// Month
			} else if (timeRange.equals("month")) {
				calendar.add(Calendar.MONTH, -1);
				// calendar.add(Calendar.YEAR, -2);
			}
			// 默认查找前一小时(Hour)
		} else {
			 calendar.add(Calendar.HOUR, -1);
//			calendar.add(Calendar.YEAR, -2);
		}
		startTime = SrContant.getTimeFormat(calendar.getTime());
		// 绑定开始和结束时间
		setAttribute("startTime", startTime);
		setAttribute("endTime", endTime);

		// TPDR TRPR
		// 获取所有属于这台交换机的交换机端口
		// 由于速度太慢，建议通过AJAX请求获取数据
		// Logger.getLogger(getClass()).info("************************************************");
		// setAttribute("PordTopNData", getDrawPerfTopNData02(null, "A494",
		// startTime, endTime));
		// setAttribute("PorsTopNData", getDrawPerfTopNData02(null, "A491",
		// startTime, endTime));
		// Logger.getLogger(getClass()).info("************************************************");

		//事件开始
		//获取用户可见的交换机
		String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		List<DataRow> switchList = service.getSwitchList(null, null, null, null, limitIds);
		List<DataRow> reswitchList = service.getIncident(startTime, endTime);
		JSONObject json1 = new JSONObject();

		json1.put("type", "pie");
		json1.put("name", "事件占用容量");
		JSONArray array = new JSONArray();
		JSONArray ary = new JSONArray();
		JSONObject json2 = new JSONObject();
		List<Switch> resultList = new ArrayList<Switch>();
		// 放switch_id
		// List<Integer> li=new ArrayList<Integer>();
		ok: for (DataRow row : switchList) {
			Switch swt = null;
			JSONArray used = new JSONArray();
			for (DataRow row1 : reswitchList) {
				if (row.getInt("switch_id") == row1.getInt("ftopid")) {
					used.add(row.getString("the_display_name"));
					used.add(row1.get("count(fcount)"));
					ary.add(used);
					swt = new Switch(row.getString("the_display_name"),
							row1.getInt("count(fcount)"));
					resultList.add(swt);
					// li.add(row1.getInt("ftopid"));
					continue ok;
				}
			}
			used.add(row.getString("the_display_name"));
			used.add(0);
			ary.add(used);
			swt = new Switch(row.getString("the_display_name"), 0);
			resultList.add(swt);
			// li.add(row.getInt("switch_id"));
		}

		/*
		 Pies = {
		 	series: [{type: 'pie', name: '告警总数', data:[]}],
		 }
		 */
		json2.put("type", "pie");
		json2.put("name", "告警总数");
		json2.put("data", ary);
		array.add(json2);

		json1.put("series", array);
		setAttribute("Pies", json1);

		Collections.sort(resultList);
		setAttribute("inname1", resultList.get(0).getName());
		if (resultList.get(0).getCount() == 0) {
			setAttribute("value1", 0);
		} else {
			setAttribute("value1", resultList.get(0).getCount());
		}
		setAttribute("inname2", resultList.get(1).getName());
		if (resultList.get(1).getCount() == 0) {
			setAttribute("value2", 0);
		} else {
			setAttribute("value2", resultList.get(1).getCount());
		}
		setAttribute("inname3", resultList.get(2).getName());
		if (resultList.get(2).getCount() == 0) {
			setAttribute("value3", 0);
		} else {
			setAttribute("value3", resultList.get(2).getCount());
		}
		setAttribute("inname4", resultList.get(3).getName());
		if (resultList.get(3).getCount() == 0) {
			setAttribute("value4", 0);
		} else {
			setAttribute("value4", resultList.get(3).getCount());
		}
		setAttribute("inname5", resultList.get(4).getName());
		if (resultList.get(4).getCount() == 0) {
			setAttribute("value5", 0);
		} else {
			setAttribute("value5", resultList.get(4).getCount());
		}
		// 事件结束
	}

	public void doGetSwitchPortTOP5() {
		JsonData jsonData = new JsonData();
		try {
			//获取用户可见的交换机
			String devIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
			Map<String, Object> json = new HashMap<String, Object>(10);
			Calendar calendar = Calendar.getInstance();
			String timeRange = getStrParameter("timeRange");
			String startTime = null;
			String endTime = SrContant.getTimeFormat(calendar.getTime());
			int which = getIntParameter("which", 1);

			if (timeRange == null || timeRange.trim().length() == 0) {
				calendar.add(Calendar.HOUR, -1);
			} 
			else {
				timeRange = timeRange.toLowerCase();
				if (timeRange.contains("day")) {
					calendar.add(Calendar.DATE, -1);
				} else if (timeRange.contains("week")) {
					calendar.add(Calendar.WEEK_OF_MONTH, -1);
				} else if (timeRange.contains("month")) {
					calendar.add(Calendar.MONTH, -1);
				} else {
					calendar.add(Calendar.HOUR, -1);
				}
			}
			startTime = SrContant.getTimeFormat(calendar.getTime());
//			startTime = "2013-05-22 16:10:09";

			// 由于速度太慢，建议通过AJAX请求获取数据
			
			// 绑定开始和结束时间
			json.put("startTime", startTime);
			json.put("endTime", endTime);
			switch(which){
				case 2:
					json.put("PorsTopNData", getDrawPerfTopNData02(devIds, null, "A491", startTime, endTime));
					break;
				case 3:
					// 交换机事件TOP5
					json.put("eventTOP5", service.getSwitchEventTOP5(startTime, endTime, devIds));
					break;
				case 4:
					// 交换机事件分布
					json.put("eventDistr", service.getSwitchEventDistr(startTime, endTime, devIds));
					break;
				default: 
					json.put("PordTopNData", getDrawPerfTopNData02(devIds, null, "A494", startTime, endTime));
			}
			jsonData.setValue(json);
		} catch (Exception e) {
			jsonData.setMsg("获取数据失败...");
			jsonData.setSuccess(true);
			Logger.getLogger(getClass()).error("", e);
		} finally{
			printWithDate(jsonData);
		}
	}

	/**
	 * 获取绘制TopN图数据 HGC's code 画TOP5值排5名的交换机端口
	 * @param devId
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public JSONObject getDrawPerfTopNData02(String devIds, String eleIds, String kpi, String startTime, String endTime) {
		JSONObject json = new JSONObject();
		// 获取指定的KPI详细信息
		List<DataRow> kpiList = baseService.getKPIInfo("'" + kpi + "'");
		DataRow kpiRow = kpiList == null ? new DataRow() : kpiList.get(0);
		json.put("smallTitle", startTime + " ~ " + endTime);
		json.put("funits", kpiList.get(0).getString("funits"));
		// 选取前五台端口
		JSONObject obj = new JSONObject();
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			baseService.generateTopNChartData02(devIds, eleIds, kpiRow, startTime, endTime, 5, SrContant.SUBDEVTYPE_SWITCHPORT);
		}
		// baseService.generateTopNChartData(kpiRow, startTime, endTime, 5);

		json.putAll(obj);
		json.put("ftitle", kpiRow.getString("ftitle"));
		json.put("charttype", "rcloumn");
		return json;
	}

	/**
	 * 分页查询交换机
	 * @return
	 */
	public ActionResult doAjaxSwitchPage() {
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String status = getStrParameter("status");
		String serialNumber = getStrParameter("serialNumber");
		//获取用户可见的交换机
		String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		DBPage switchPage = null;
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			switchPage = service.getSwitchPage(curPage, numPerPage, name, ipAddress, status, serialNumber, limitIds);
			List<DataRow> switchList = switchPage.getData();
			List<DataRow> reswitchList = service.getResSwitchList(limitIds);
			List<DataRow> list = new ArrayList<DataRow>();
			for (DataRow row : switchList) {
				for (DataRow row1 : reswitchList) {
					if (row.getInt("switch_id") == row1.getInt("id")) {
						row.set("engine_status", row1.get("engine_status"));
						row.set("power_status", row1.get("power_status"));
						row.set("port_status", row1.get("port_status"));
						row.set("fiber_status", row1.get("fiber_status"));
						list.add(row);
					}
	
				}
			}
			switchList.removeAll(list);
			for (DataRow row2 : switchList) {
				row2.set("engine_status", "");
				row2.set("power_status", "");
				row2.set("port_status", "");
				row2.set("fiber_status", "");
				row2.set("fan_status", "");
				list.add(row2);
			}
			switchPage.setData(list);
		}
		this.setAttribute("switchPage", switchPage);
		this.setAttribute("name", name);
		this.setAttribute("ipAddress", ipAddress);
		this.setAttribute("status", status);
		this.setAttribute("serialNumber", serialNumber);
		return new ActionResult("/WEB-INF/views/switch/ajaxSwitch.jsp");
	}

	/**
	 * 交换机详细信息页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionResult doSwitchInfo() {
		SwitchportService portServcie = new SwitchportService();
		ZoneService zoneService = new ZoneService();
		Integer switchId = getIntParameter("switchId");
		DataRow row = service.getSwitchInfo(switchId);
		DataRow resrow = service.getResSwitchInfo(switchId);
		DBPage portPage = portServcie.getPortPage(1, WebConstants.NumPerPage,null, null, null, null, null, switchId);
		this.setAttribute("portPage", portPage);
		this.setAttribute("portCount", portPage.getTotalRows());
		//获取用户可见的Zone
		String limitIds = (String) getSession().getAttribute(WebConstants.ZONE_LIST);
		this.setAttribute("zonePage", zoneService.getZonePage(1,WebConstants.NumPerPage, null, null, null, null, null, limitIds));
		this.setAttribute("switchInfo", row);
		this.setAttribute("resswitchInfo", resrow);
		this.setAttribute("switchId", switchId);

		// 设备事件跳转
		int level = getIntParameter("level", -1);
		int state = getIntParameter("state", -1);
		int tabToShow = getIntParameter("tabToShow", SrContant.TAB_SUMMARY);
		setAttribute("tabToShow", tabToShow);
		String overViewTab = "", detailTab = "", prfTab = "", alertTab = "", dataTab = "";
		switch (tabToShow) {
			case 0: overViewTab = "active"; break;
			case 1: detailTab = "active"; break;
			case 2: prfTab = "active"; break;
			case 3: alertTab = "active"; break;
			case 4: dataTab = "active"; break;
		}
		setAttribute("overViewTab", overViewTab);
		setAttribute("detailTab", detailTab);
		setAttribute("prfTab", prfTab);
		setAttribute("alertTab", alertTab);
		setAttribute("dataTab", dataTab);
		setAttribute("attachment", String.format("&level=%s&state=%s&tabToShow=%s", level, state, tabToShow));
		setAttribute("level", level);
		setAttribute("state", state);

		// 告警
		DeviceAlertService deviceService = new DeviceAlertService();
		DBPage devicePage = deviceService.getLogPage(1,WebConstants.NumPerPage, -1, switchId.toString(), null, null,
				null, SrContant.SUBDEVTYPE_SWITCH, state, level, null, null);
		DBPage devicePage1 = deviceService.getLogPage(1, WebConstants.NumPerPage, -1, switchId.toString(), null,null,null, SrContant.SUBDEVTYPE_SWITCH, 0, level, null, null);
		setAttribute("deviceLogPage", devicePage);
		// doSwitchPrfField();
		// Total Port Data Rate
		setTpdr();
		DBPage devicePage2 = deviceService.getLogPage(1,WebConstants.NumPerPage, -1, switchId.toString(), null, null,
				null, SrContant.SUBDEVTYPE_SWITCH, -1, -1, null, null);
		List<DataRow> levels = devicePage2.getData();
		JSONObject jsonreport = new JSONObject();
		DataRow report = new DataRow();
		for (DataRow leve : levels) {
			if ("0".equals(leve.getString("flevel"))) {
				report.set("Info", leve.getInt("fcount"));
			} else if ("1".equals(leve.getString("flevel"))) {
				report.set("Warning", leve.getInt("fcount"));
			} else if ("2".equals(leve.getString("flevel"))) {
				report.set("Critical", leve.getInt("fcount"));
			}
		}
		jsonreport.put("smallTitle", row.getString("the_display_name")+ " Alert Info");
		JSONArray array2 = new JSONArray();
		array2 = getHighchartPieData(report,switchId);
		jsonreport.put("series", array2);
		jsonreport.put("charttype", "pie");
		setAttribute("report", jsonreport);
		return new ActionResult("/WEB-INF/views/switch/switchInfo.jsp");
	}

	/**
	 * 交换机告警饼图
	 * */
	public JSONArray getHighchartPieData(DataRow row, Integer switchId) {
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();

		JSONObject info = new JSONObject();
		JSONObject warning = new JSONObject();
		JSONObject critical = new JSONObject();
		String baseUrl = "/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=" + switchId +"&tabToShow=3";
		//Info
		info.put("name", "Info");
		info.put("y", row.getDouble("Info"));
		info.put("url", baseUrl + "&level=0");
		//Warning
		warning.put("name", "Warning");
		warning.put("y", row.getDouble("Warning"));
		warning.put("url", baseUrl + "&level=1");
		//Critical
		critical.put("name", "Critical");
		critical.put("y", row.getDouble("Critical"));
		critical.put("url", baseUrl + "&level=2");
			
		ary.add(info);
		ary.add(warning);
		ary.add(critical);

		json.put("type", "pie");
		json.put("name", "告警数量");
		if (row.size() < 1) {
			json.put("data", null);
		} else {
			json.put("data", ary);
		}
		array.add(json);
		return array;
	}

	/**
	 * 处理Total Port Date Rate
	 * */
	public void setTpdr() {
		String timeRange = getStrParameter("timeRange");
		Date date = new Date();
		String startTime = null;
		String endTime = SrContant.getTimeFormat(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (StringHelper.isNotEmpty(timeRange)
				&& StringHelper.isNotBlank(timeRange)) {
			// Hour
			if (timeRange.equals("hour")) {
				calendar.add(Calendar.HOUR, -1);
				// calendar.add(Calendar.YEAR, -2);
				// Day
			} else if (timeRange.equals("day")) {
				calendar.add(Calendar.DATE, -1);
				// calendar.add(Calendar.YEAR, -2);
				// Week
			} else if (timeRange.equals("week")) {
				calendar.add(Calendar.WEEK_OF_MONTH, -1);
				// calendar.add(Calendar.YEAR, -2);
				// Month
			} else if (timeRange.equals("month")) {
				calendar.add(Calendar.MONTH, -1);
				// calendar.add(Calendar.YEAR, -2);
			}
			// 默认查找前一小时(Hour)
		} else {
			calendar.add(Calendar.HOUR, -1);
			calendar.add(Calendar.YEAR, -2);
		}
		startTime = SrContant.getTimeFormat(calendar.getTime());
		/*
		 * 性能曲线
		 */
		List<DataRow> devList = new ArrayList<DataRow>();
		DataRow devRow = new DataRow();
		devRow.set("ele_id", getStrParameter("switchId"));
		devList.add(devRow);
		// 获取指定的KPI详细信息
		List<DataRow> kpis = baseService.getKPIInfo("'A518'");
		// 获取绘图数据
		JSONArray array = baseService.getPrfDatas(0, devList, kpis, startTime, endTime);
		JSONObject json = new JSONObject();
		json.put("series", array);
		json.put("legend", true);
		json.put("ytitle", "MB/Sec");
		json.put("threshold", 0);
		json.put("threvalue", "");
		setAttribute("PortRateData", json);
		/*
		 * Total Port Data Rate
		 */
		setAttribute("PortdataTopNData",getDrawPerfTopNData(getStrParameter("switchId"), "A494", startTime, endTime));
	}

	/**
	 * 获取绘制TopN图数据
	 * 
	 * @param devId
	 * @param kpi
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public JSONObject getDrawPerfTopNData(String devId, String kpi, String startTime, String endTime) {
		JSONObject json = new JSONObject();
		// 获取指定的KPI详细信息
		List<DataRow> kpiList = baseService.getKPIInfo("'" + kpi + "'");
		DataRow kpiRow = kpiList == null ? new DataRow() : kpiList.get(0);
		json.put("smallTitle", startTime + " ~ " + endTime);
		json.put("funits", kpiList.get(0).getString("funits"));
		JSONObject obj = baseService.generateTopNChartData(devId, kpiRow, startTime, endTime, 5);

		json.putAll(obj);
		json.put("ftitle", kpiRow.getString("ftitle"));
		json.put("charttype", "rcloumn");
		return json;
	}

	/**
	 * 处理绘制总览页面性能图数据
	 */
	public ActionResult doDrawPerfLine() {
		setTpdr();
		return new ActionResult("/WEB-INF/views/switch/ajaxSwitchChart.jsp");
	}

	public ActionResult doSwitchPrfPage() {
		doSwitchPrfField();
		this.setAttribute("switchId", getIntParameter("switchId"));
		String tablePage = getStrParameter("tablePage");
		if (tablePage != null && tablePage.length() > 0) {
			return new ActionResult("/WEB-INF/views/switch/ajaxPrfSwitch.jsp");
		}
		return new ActionResult("/WEB-INF/views/switch/prefSwitchPage.jsp");
	}

	@SuppressWarnings("static-access")
	public ActionResult doSwitchSettingPrf() {
		Integer switchId = getIntParameter("switchId");
		Integer level = getIntParameter("level");
		List<DataRow> kpis = baseService.getView(SrContant.DEVTYPE_VAL_SWITCH, SrContant.SUBDEVTYPE_SWITCH);
		//获取用户可见的交换机
		String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		List<DataRow> devs = service.getSwitchInfoList(limitIds);
		DataRow row = baseService.getPrfFieldInfo(null, level, SrContant.DEVTYPE_VAL_SWITCH, SrContant.SUBDEVTYPE_SWITCH, switchId, null, getLoginUserId());
		if (row == null && switchId != 0) {
			row = new DataRow();
			row.set("fdevice", switchId);
		}
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("level", level);
		this.setAttribute("switchId", switchId);
		this.setAttribute("historyConfig", row);
		return new ActionResult("/WEB-INF/views/switch/editSwitch.jsp");
	}

	@SuppressWarnings("static-access")
	public ActionResult doSwitchSettingPrf2() {
		Integer switchId = getIntParameter("switchId");
		Integer level = getIntParameter("level");
		List<DataRow> kpis = baseService.getView(SrContant.DEVTYPE_VAL_SWITCH, SrContant.SUBDEVTYPE_SWITCH);
		//获取用户可见的交换机
		String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		List<DataRow> devs = new ArrayList<DataRow>();
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			devs = service.getSwitchInfoList(limitIds);
		}
		DataRow row = baseService.getPrfFieldInfo(null, level, SrContant.DEVTYPE_VAL_SWITCH, SrContant.SUBDEVTYPE_SWITCH, switchId, null, getLoginUserId());
		if (row == null && switchId != 0) {
			row = new DataRow();
			row.set("fdevice", switchId);
		}
		this.setAttribute("kpisList", new JSONArray().fromObject(kpis));
		this.setAttribute("devList", new JSONArray().fromObject(devs));
		this.setAttribute("level", level);
		this.setAttribute("switchId", switchId);
		this.setAttribute("historyConfig", row);
		return new ActionResult("/WEB-INF/views/switch/querySwitch.jsp");
	}

	public void doSwitchPrf2() {
		Integer subsystemId = getIntParameter("switchId");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
		String timeType = getStrParameter("time_type");
		String[] de = getStrArrayParameter("device");
		String[] devices = checkStrArray(de, "multiselect-all");
		String[] kpis = getStrArrayParameter("prfField");

		Integer graphType = getIntParameter("graphType", SrContant.GRAPH_TYPE_LINE);
		Integer topnValue = getIntParameter("topnValue", SrContant.TOPN_COUNT);

		StringBuffer kpi = new StringBuffer(50);
		for (int i = 0, len = kpis.length - 1; i <= len; ++i) {
			kpi.append("'" + kpis[i] + "'");
			if (i < len) {
				kpi.append(",");
			}
		}
		String dev = "";
		if (devices != null && devices.length > 0) {
			StringBuffer device = new StringBuffer(50);
			for (int i = 0, len = devices.length - 1; i <= len; ++i) {
				device.append(devices[i]);
				if (i < len) {
					device.append(",");
				}
			}
			dev = device.toString();
		} else {
			dev = devId.toString();
		}
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		Integer legend = getIntParameter("legend");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname", "Switch");
		row.set("fdevicetype", storageType);
		row.set("fdevice", dev);
		row.set("fprfid", kpi.toString());
		row.set("fisshow", 1);
		row.set("graphtype", graphType);
		row.set("topnValue", topnValue);

		List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString());
		if (units != null && units.size() > 0) {
			Set<String> set = new HashSet<String>();
			for (DataRow unit : units) {
				if (StringHelper.isNotEmpty(unit.getString("funits")))
					set.add(unit.getString("funits"));
			}
			String tempStr = set.toString().replace("[", "").replace("]", "");
			row.set("fyaxisname", tempStr.length() > 40 ? tempStr.substring(0, 37) + "..." : tempStr);
		} else {
			row.set("fyaxisname", "");
		}
		row.set("flegend", legend);
		row.set("fstarttime", startTime);
		row.set("fendtime", endTime);
		row.set("time_type", timeType);
		row.set("fthreshold", threshold);
		row.set("fthreValue", threValue);
		row.set("fuserid", getLoginUserId());
		try {
			baseService.updatePrfField(row, SrContant.SUBDEVTYPE_SWITCH, SrContant.DEVTYPE_VAL_SWITCH, null, subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}

	public void doSwitchPrfField() {
		Integer switchId = getIntParameter("switchId");
		Integer level = getIntParameter("level", 1);
		if (switchId != null && switchId > 0) {
			level = 3;
		}
		String tablePage = getStrParameter("tablePage");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		boolean isFreshen = "1".equals(getStrParameter("isFreshen"));
		boolean isLine = true;

		DataRow thead = new DataRow();
		DBPage tbody = null;
		JSONObject json = new JSONObject();
		DataRow dataRow = baseService.getPrfFieldInfo(null, level,
				SrContant.SUBDEVTYPE_SWITCH, SrContant.DEVTYPE_VAL_SWITCH,
				switchId, null, getLoginUserId());
		// 给默认性能信息
		int graphType = SrContant.GRAPH_TYPE_LINE;
		int topCount = 5;
		double threvalue = Double.MAX_VALUE;
		String startTime = "", endTime = "";
		if (dataRow == null || dataRow.size() == 0) {
			dataRow = baseService.getDefaultRow("v_res_switch", switchId,
					SrContant.SUBDEVTYPE_SWITCH, SrContant.DEVTYPE_VAL_SWITCH,
					"switch_id", "the_display_name");
			dataRow.set("fprfid", "'A515','A518'");
			dataRow.set("fyaxisname", "Packets Per Second,MB/Sec");
		}
		String eleIds = "-1";
		String kpi = "V1";
		if (dataRow != null && dataRow.size() > 0) {
			//判断是否有TPC配置
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "switch_id","the_display_name", "v_res_switch");
				List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
				graphType = dataRow.getInt("graphtype");
				isLine = graphType == SrContant.GRAPH_TYPE_LINE;
				json.put("graphType", graphType);
				topCount = dataRow.getInt("topnvalue");
				startTime = dataRow.getString("fstarttime");
				endTime = dataRow.getString("fendtime");
				threvalue = dataRow.getDouble("fthrevalue");
				if (threvalue <= 0) {
					threvalue = Integer.MAX_VALUE;
				} // 阈值不可能是0和负数
	
				String time_type = dataRow.getString("time_type");
				String viewPostfix = "";
				if (time_type == null) {
					time_type = "minite";
				}
				if (time_type.contains("hour")) {
					viewPostfix = "_hourly";
				} else if (time_type.contains("day")) {
					viewPostfix = "_daily";
				}
	
				eleIds = dataRow.getString("fdevice");
				topCount = eleIds == null ? 5 : eleIds.split(",").length;
				kpi = dataRow.getString("fprfid").split(",")[0].replace("'", "");
	
				tbody = baseService.getPrfDatas(curPage, numPerPage, devs, kpis, dataRow.getString("fstarttime"),
					dataRow.getString("fendtime"), time_type);
				if (level == 1 || level == 2) {
					StringBuilder tableHeader = new StringBuilder(50);
					StringBuilder tableBody = new StringBuilder(5000);
					// ////////////////////////////////////////////////////
					tableHeader.append("<th style='text-align:center;'>");
					tableHeader.append("设备名");
					tableHeader.append("</th>");
					List<String> headers = new ArrayList<String>(kpis.size());
					for (DataRow r : kpis) {
						headers.add(r.getString("fid").toLowerCase());
						tableHeader.append("<th style='text-align:center;'>");
						tableHeader.append(r.getString("ftitle").concat("(" + r.getString("funits") + ")"));
						tableHeader.append("</th>");
					}
					tableHeader.append("<th style='text-align:center;'>");
					tableHeader.append("时间");
					tableHeader.append("</th>");
					if (tbody != null && tbody.getData() != null && tbody.getData().size() > 0) {
						List<DataRow> data = tbody.getData();
						Object obj;
						DecimalFormat df = new DecimalFormat("#.##");
						double t;
						float t2;
						int t3;
						String criticalLb = " style='color:red'";
						for (DataRow dr : data) {
							tableBody.append("<tr>"); // 一个dr代表table的一行
							tableBody.append("<td class='rc-td'>");
							tableBody.append(dr.getString("ele_name"));
							tableBody.append("</td>");
							for (String key : headers) {
								tableBody.append("<td class='rc-td'><span");
								obj = dr.getObject(key);
								if (obj instanceof Double) {
									t = (Double) obj;
									if (t > threvalue) {
										tableBody.append(criticalLb);
									}
									tableBody.append('>');
									tableBody.append(df.format(t));
								} else if (obj instanceof Float) {
									t2 = (Float) obj;
									if (t2 > threvalue) {
										tableBody.append(criticalLb);
									}
									tableBody.append('>');
									tableBody.append(df.format(t2));
								} else if (obj instanceof Integer) {
									t3 = (Integer) obj;
									if (t3 > threvalue) {
										tableBody.append(criticalLb);
									}
									tableBody.append('>');
									tableBody.append(t3);
								} else {
									tableBody.append(obj);
								}
								tableBody.append("</span></td>");
							}
							tableBody.append("<td class='rc-td'>");
							tableBody.append(dr.getString("prf_timestamp"));
							tableBody.append("</td>");
							tableBody.append("</tr>");
						}
						json.put("totalPages", tbody.getTotalPages());
						json.put("currentPage", tbody.getCurrentPage());
						json.put("numPerPage", tbody.getNumPerPage());
					} else {
						tableBody.append("<tr><td colspan='" + (kpis.size() + 2)
								+ "'>暂无数据</tr>");
						json.put("totalPages", 0);
						json.put("currentPage", 0);
						json.put("numPerPage", 0);
					}
					json.put("thead", tableHeader.toString());
					json.put("tbody", tableBody.toString());
				} else {
					for (DataRow r : kpis) {
						thead.set(
								r.getString("fid"),
								r.getString("ftitle").concat(
										"(" + r.getString("funits") + ")"));
					}
					thead.set("prf_timestamp", "时间");
					thead.set("ele_name", "设备名");
					json.put("thead", thead);
					json.put("tbody", tbody);
				}
	
				if (tablePage == null || tablePage.length() == 0) { // 是否需要画图，如果需要，那么不要在URL添加tablePage
					if (isLine) {
						json.put("series", JSON.toJSONString(baseService.getSeries(
								dataRow.getInt("fisshow"), devs, kpis,
								dataRow.getString("fstarttime"),
								dataRow.getString("fendtime"),
								dataRow.getString("time_type"))));
					} else {
						json.put("series", JSON.toJSONString(baseService
								.getTopnGraph(eleIds, kpi, startTime, endTime,
										topCount, viewPostfix,
										SrContant.SUBDEVTYPE_SWITCH, threvalue)));
					}
				}
				json.put("legend", dataRow.getInt("flegend") == 1);
				json.put("ytitle", dataRow.getString("fyaxisname"));
				json.put("threshold", dataRow.getInt("fthreshold"));
				json.put("threvalue", dataRow.getString("fthrevalue"));
				json.put("kpiInfo", kpis);
			}
		}

		if (isFreshen) {
			writeDataToPage(json.toString());
		} else {
			this.setAttribute("prfData", json);
		}
	}

	public void doSwitchPrf() {
		Integer subsystemId = getIntParameter("switchId");
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
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname", "Switch");
		row.set("fdevicetype", storageType);
		row.set("fdevice", dev);
		row.set("fprfid", kpi.toString());
		row.set("fisshow", 1);
		List<DataRow> units = new BaseprfService().getUnitsById(kpi.toString());
		if (units != null && units.size() > 0) {
			Set<String> set = new HashSet<String>();
			for (DataRow unit : units) {
				if (StringHelper.isNotEmpty(unit.getString("funits")))
					set.add(unit.getString("funits"));
			}
			String tempStr = set.toString().replace("[", "").replace("]", "");
			row.set("fyaxisname",tempStr.length() > 40 ? tempStr.substring(0, 37) + "..." : tempStr);
		} else {
			row.set("fyaxisname", "");
		}
		row.set("flegend", legend);
		row.set("fstarttime", startTime);
		row.set("fendtime", endTime);
		row.set("time_type", timeType);
		row.set("fthreshold", threshold);
		row.set("fthreValue", threValue);
		row.set("fuserid", getLoginUserId());
		try {
			baseService.updatePrfField(row, "Switch", "SWITCH", null, subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}

	/**
	 * 导出交换机配置信息列表
	 */
	public void doExportSwitchConfigData() {
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String ipAddress = getStrParameter("ipAddress");
		String status = getStrParameter("status");
		String serialNumber = getStrParameter("serialNumber");
		//获取用户可见的交换机
		String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			List<DataRow> switchList = service.getSwitchList(name, ipAddress, status, serialNumber, limitIds);
			List<DataRow> reswitchList = service.getResSwitchList(limitIds);
			List<DataRow> list = new ArrayList<DataRow>();
			for (DataRow row : switchList) {
				for (DataRow row1 : reswitchList) {
					if (row.getInt("switch_id") == row1.getInt("id")) {
						row.set("engine_status", row1.get("engine_status"));
						row.set("power_status", row1.get("power_status"));
						row.set("port_status", row1.get("port_status"));
						row.set("fiber_status", row1.get("fiber_status"));
						list.add(row);
					}
				}
			}
			switchList.removeAll(list);
			for (DataRow row2 : switchList) {
				row2.set("engine_status", "");
				row2.set("power_status", "");
				row2.set("port_status", "");
				row2.set("fiber_status", "");
				list.add(row2);
			}
			if (list != null && list.size() > 0) {
				String[] title = new String[] { "名称", "厂商", "型号", "状态", "域ID",
						"IP地址", "Fabric网络", "WWN", "序列号", "更新时间" };
				String[] keys = new String[] { "the_display_name", "vendor_name",
						"model_name", "the_propagated_status", "domain",
						"ip_address", "the_backend_name", "switch_wwn",
						"serial_number", "update_timestamp" };
				// String[] keys = new
				// String[]{"the_display_name","vendor_name","model_name","the_propagated_status","domain","ip_address","fabric_name","switch_wwn","serial_number","description","engine_status","power_status","port_status","fiber_status","update_timestamp"};
				getResponse().setCharacterEncoding("GBK");
				CSVHelper.createCSVToPrintWriter(getResponse(),"Switch_Config_Data", list, title, keys);
			}
		}
	}

	/**
	 * 导出交换机性能信息
	 * @see 建议转换日期的方法
	 */
	public void doExportPrefData() {
		Integer level = getIntParameter("level");
		Integer switchId = getIntParameter("switchId");
		DataRow dataRow = baseService.getPrfFieldInfo(null, level,SrContant.SUBDEVTYPE_SWITCH, SrContant.DEVTYPE_VAL_SWITCH,switchId, null, getLoginUserId());
		if (dataRow == null || dataRow.size() == 0) {
			dataRow = baseService.getDefaultRow("v_res_switch", switchId,SrContant.DEVTYPE_VAL_SWITCH, SrContant.SUBDEVTYPE_SWITCH,
					"switch_id", "the_display_name");
		}
		List<DataRow> devs = baseService.getDeviceInfo(dataRow.getString("fdevice"), "switch_id", "the_display_name","v_res_switch");
		List<DataRow> kpis = baseService.getKPIInfo(dataRow.getString("fprfid"));
		List<DataRow> tbody = baseService.getPrfDatas(devs, kpis,
				dataRow.getString("fstarttime"),
				dataRow.getString("fendtime"),
				dataRow.getString("time_type"));

		baseService.createAndSendCSVFile(getResponse(), kpis, tbody, devs);
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

	/**
	 * 该内部类用于封装Switch数据
	 * 
	 * @author huiming
	 * 
	 */
	class Switch implements Comparable {

		private String name;
		private int count;

		public Switch(String name, int count) {
			super();
			this.name = name;
			this.count = count;
		}

		public int compareTo(Object o) {
			Switch tmp = (Switch) o;
			int result = tmp.count > count ? 1 : (tmp.count == count ? 0 : -1);
			if (result == 0) {
				result = tmp.name.indexOf(0) > name.indexOf(0) ? 1 : -1;
			}
			return result;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

	}

}
