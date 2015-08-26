package root.widget;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import root.index.Index;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.StringHelper;
import com.huiming.service.chart.ChartService;
import com.huiming.service.report.ReportService;
import com.huiming.service.user.UserEngineService;
import com.huiming.service.widget.WidgetService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class WidgetAction extends SecurityAction {
	UserEngineService userEngineService = new UserEngineService();
	WidgetService widgetService = new WidgetService();
	//图形类型
	private static final String CHART_TYPE_LINE = "0";
	private static final String CHART_TYPE_TOPN = "1";
	private static final String CHART_TYPE_CLOUMN = "2";
	private static final String CHART_TYPE_CAPACITY = "3";
	private static final String CHART_TYPE_LIST = "4";
	private static final String CHART_TYPE_LOG = "5";

	public ActionResult doAddwidgets() {
		String fid = getStrParameter("fmodelid");
		this.setAttribute("fmodelid", fid);
		return new ActionResult("/WEB-INF/views/editpage/cho-type.jsp");
	}

	public ActionResult doAddConsoles() {
		return new ActionResult("/WEB-INF/views/editpage/editConsole.jsp");
	}

	/**
	 * 根据类型返回添加的模板
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doEditwidget() {
		String modelid = getStrParameter("fmodelid");
		String fid = getStrParameter("fid");
		String type = getStrParameter("fcharttype");
		//获取模板详细信息
		DataRow row = widgetService.getWidgetInfo(fid);
		JSONObject obj = new JSONObject();
		obj.put("fmodelid", modelid);
		obj.put("fid", fid);
		obj.put("fcharttype", type);
		setAttribute("item", obj);
		setAttribute("moduleInfo", new JSONObject().fromObject(row));
		if (type.equals(CHART_TYPE_LINE)) {
			// 性能曲线图
			return new ActionResult("/WEB-INF/views/editpage/editline.jsp");
		} else if (type.equals(CHART_TYPE_TOPN)) {
			// TopN信息图
			return new ActionResult("/WEB-INF/views/editpage/edittopn.jsp");
		} else if (type.equals(CHART_TYPE_CLOUMN)) {
			// r-cloumn
			return new ActionResult("/WEB-INF/views/editpage/editcloumn.jsp");
		} else if (type.equals(CHART_TYPE_CAPACITY)) {
			// 容量图
			return new ActionResult("/WEB-INF/views/editpage/editcap.jsp");
		} else if (type.equals(CHART_TYPE_LIST)) {
			// list
			return new ActionResult("/WEB-INF/views/editpage/editlog.jsp");
		} else if (type.equals(CHART_TYPE_LOG)) {
			// log
			return new ActionResult("/WEB-INF/views/editpage/editlist.jsp");
		}
		return null;
	}

	/**
	 * 组件类型的列表信息
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public void doChangeDevType() {
		JSONObject obj = new JSONObject();
		String type = getStrParameter("devType");
		JSONArray jsArray = null;
		JSONArray jsKpis = null;
		if (StringHelper.isNotEmpty(type) && StringHelper.isNotBlank(type)) {
			//获取用户配置的设备列表
			String limitDevIds = null;
			//For SWITCH
			if (type.equals(SrContant.DEVTYPE_VAL_SWITCH)) {
				limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_SWITCH, null, null);
				if(Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
					jsArray = new JSONArray().fromObject(widgetService.getSwitchList(limitDevIds));
				}
			//For HOST/PHYSICAL
			} else if (type.equals(SrContant.DEVTYPE_VAL_HOST) || type.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
				limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_PHYSICAL, null, null);
				jsArray = new JSONArray().fromObject(widgetService.getPhysicalList(limitDevIds));
			//For VIRTUAL
			} else if (type.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)) {
				limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, null);
				jsArray = new JSONArray().fromObject(widgetService.getVirtualList(limitDevIds));
			//For APPLICATION
			} else if (type.equals(SrContant.DEVTYPE_VAL_APPLICATION)) {
				jsArray = new JSONArray().fromObject(widgetService.getAppList());
			//For EMC
			} else if (type.equals(SrContant.DEVTYPE_VAL_EMC)) {
				limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DEVTYPE_VAL_EMC, null);
				jsArray = new JSONArray().fromObject(widgetService.getDeviceListByDevType(SrContant.DEVTYPE_VAL_EMC,limitDevIds));
			//For HDS
			} else if (type.equals(SrContant.DEVTYPE_VAL_HDS)) {
				limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DEVTYPE_VAL_HDS, null);
				jsArray = new JSONArray().fromObject(widgetService.getDeviceListByDevType(SrContant.DEVTYPE_VAL_HDS,limitDevIds));
			//For NETAPP
			} else if (type.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
				limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, WebConstants.STORAGE_TYPE_VAL_NETAPP, null);
				jsArray = new JSONArray().fromObject(widgetService.getDeviceListByDevType(WebConstants.STORAGE_TYPE_VAL_NETAPP,limitDevIds));
			//For SVC/BSP/DS
			} else {
				if(Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
					limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC, null);
					jsArray = new JSONArray().fromObject(widgetService.getStorageType(type,limitDevIds));
				}
			}
			//获取性能指标列表
			List<DataRow> allKpiList = (List<DataRow>) getSession().getAttribute(WebConstants.ALL_KPI_LIST);
			List<DataRow> needKpiList = new ArrayList<DataRow>();
			for (int i = 0; i < allKpiList.size(); i++) {
				DataRow row = allKpiList.get(i);
				if (row.getString("fstoragetype").equals(type)) {
					needKpiList.add(row);
				}
			}
			jsKpis = new JSONArray().fromObject(needKpiList);
		}
		obj.put("devOption", jsArray);
		obj.put("kpiOption", jsKpis);
		writetopage(obj);
	}

	/**
	 * 得到部件
	 */
	@SuppressWarnings("static-access")
	public void doChangeKpi() {
		String fid = getStrParameter("fid");
		Integer sysId = getIntParameter("device");
		DataRow row = widgetService.getKPIinfo(fid);
		if (row == null) {
			return;
		}
		String storageType = row.getString("fstoragetype");
		String devType = row.getString("fdevtype");

		JSONObject json = new JSONObject();
		ReportService ts = new ReportService();
		List<DataRow> rows = null;
		//交换机(SWITCH)
		if (storageType.equals(SrContant.DEVTYPE_VAL_SWITCH)) { 
			if(Configure.getInstance().getDataSource(WebConstants.DB_TPC)  != null){
				if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCH)) {
					rows = ts.getSubgroupDevice(sysId, "switch_id", "switch_id", "the_display_name", "v_res_switch");
				} else if (devType.equalsIgnoreCase("port")) {
					rows = ts.getSubgroupDevice(sysId, "switch_id", "port_id", "the_display_name", "v_res_switch_port");
				}
			}
		//服务器(HOST)
		} else if (storageType.equals(SrContant.DEVTYPE_VAL_HOST)) { 
			if (devType.equals(SrContant.SUBDEVTYPE_PHYSICAL)) {
				rows = ts.getSubgrouphost(sysId, "hypervisor_id", "hypervisor_id", "name", "t_res_hypervisor", null);
			} else if (devType.equals(SrContant.SUBDEVTYPE_VIRTUAL) || devType.equals(WebConstants.DEVTYPE_HYPERVISOR)) {
				//获取设置的虚拟机
				String limitDevIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, sysId);
				rows = ts.getSubgrouphost(sysId, "hypervisor_id", "vm_id", "name", "t_res_virtualmachine", limitDevIds);
			}
		//应用(APPLICATION)
		} else if (storageType.equals(SrContant.DEVTYPE_VAL_APPLICATION)) { 
			rows = ts.getSubgroupDevice2(sysId, "fid", "fid", "fname", "tnapps", WebConstants.DB_DEFAULT);
		//For EMC/HDS/NETAPP
		} else if (storageType.equals(SrContant.DEVTYPE_VAL_HDS) 
				|| storageType.equals(SrContant.DEVTYPE_VAL_EMC)
				|| storageType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			//For Storage
			if (devType.equals(SrContant.SUBDEVTYPE_STORAGE)) {
				rows = widgetService.getSubDeviceList(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_NAME, SrTblColConstant.TBL_RES_STORAGESUBSYSTEM);
			//For disk group
			} else if (devType.equals(SrContant.SUBDEVTYPE_DISKGROUP)) {
				rows = widgetService.getSubDeviceList(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME, SrTblColConstant.TBL_RES_DISKGROUP);
			//For host group
			} else if (devType.equals(SrContant.SUBDEVTYPE_HOSTGROUP)) {
				rows = widgetService.getSubDeviceList(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RHG_HOSTGROUP_ID, SrTblColConstant.RHG_HOSTGROUP_NAME, SrTblColConstant.TBL_RES_HOSTGROUP);
			//For Port
			} else if (devType.equals(SrContant.SUBDEVTYPE_PORT)) {
				rows = widgetService.getSubDeviceList(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME, SrTblColConstant.TBL_RES_PORT);
			//For Node
			} else if (devType.equals(SrContant.SUBDEVTYPE_NODE)) {
				rows = widgetService.getSubDeviceList(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSN_SP_ID, SrTblColConstant.RSN_SP_NAME, SrTblColConstant.TBL_RES_STORAGENODE);
			//rows Volume
			} else if (devType.equals(SrContant.SUBDEVTYPE_VOLUME)) {
				rows = widgetService.getSubDeviceList(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME, SrTblColConstant.TBL_RES_STORAGEVOLUME);
			}
		//For DS/BSP/SVC
		} else { 
			if (devType.equalsIgnoreCase("app")) {
				rows = ts.getSubgroupDevice2(sysId, "fid", "fid", "fname", "tnapps", WebConstants.DB_DEFAULT);
			}else if(Configure.getInstance().getDataSource(WebConstants.DB_TPC)  != null) {
				if (devType.equalsIgnoreCase("storage")) {
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "subsystem_id", "the_display_name", "v_res_storage_subsystem");
				} else if (devType.equalsIgnoreCase("Node")) {
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "redundancy_id", "the_display_name", "V_RES_REDUNDANCY");
				} else if (devType.equalsIgnoreCase("port")) {
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "port_id", "the_display_name", "v_res_port");
				} else if (devType.equalsIgnoreCase("mdiskgroup")) { // pool
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "pool_id", "the_display_name", "v_res_storage_pool");
				} else if (devType.equalsIgnoreCase("arraysite")) {
					rows = ts.getSubgroupDevice(sysId, "subsystem_id","disk_group_id", "the_display_name", "v_res_arraysite");
				} else if (devType.equalsIgnoreCase("mdisk")) { // extent
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id", "the_display_name", "v_res_storage_extent");
				} else if (devType.equalsIgnoreCase("rank")) {
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id", "the_display_name", "V_RES_STORAGE_RANK");
				} else if (devType.equalsIgnoreCase("iogroup")) {
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "io_group_id", "the_display_name", "V_RES_STORAGE_IOGROUP");
				} else if (devType.equalsIgnoreCase("controller")) {
					rows = ts.getSubgroupDevice(sysId, "dev_id", "ele_id", "ele_name", "PRF_TARGET_DSCONTROLLER");
				} else if (devType.equalsIgnoreCase("volume")) {
					rows = ts.getSubgroupDevice(sysId, "subsystem_id", "svid", "the_display_name", "v_res_storage_volume");
				}
			}
		}
		
		JSONArray array = new JSONArray().fromObject(rows);
		json.put("subDev", devType);
		json.put("dataList", array);
		writetopage(json);
	}

	/**
	 * 添加控制台
	 */
	public void doAddConsole() {
		ChartService cser = new ChartService();
		JSONObject json = new JSONObject();
		DataRow row = new DataRow();
		try {
			int sid = cser.getmaxConsoleId() + 1;
			row.set("fid", sid);
			row.set("fuserid", (Long)getSession().getAttribute(WebConstants.SESSION_CLIENT_ID));
			row.set("fisshow", 1);
			row.set("fname", new String(getStrParameter("cname").replaceAll("&amp;nbsp;", " ").getBytes(), "GBK"));
			int state = cser.addModel(row);
			json.put("state", state);
			json.putAll(row);
		} catch (Exception e) {
			e.printStackTrace();
		}
		writetopage(json);
	}
	
	/**
	 * 根据设备类型设置相应的数据库类型
	 * @param deviceType
	 * @return
	 */
	public String getDBType(String deviceType) {
		String dbtype = null;
		//For BSP/SVC/DS
		if (deviceType.equals(SrContant.DEVTYPE_VAL_BSP)
				|| deviceType.equals(SrContant.DEVTYPE_VAL_SVC)
				|| deviceType.equals(SrContant.DEVTYPE_VAL_DS)) {
			dbtype = SrContant.DBTYPE_TPC;
		//For EMC/HDS/NETAPP
		} else if (deviceType.equals(SrContant.DEVTYPE_VAL_EMC)
				|| deviceType.equals(SrContant.DEVTYPE_VAL_HDS)
				|| deviceType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			dbtype = SrContant.DBTYPE_SR;
		//For PHYSICAL AND VIRTUAL
		} else if (deviceType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL) 
				|| deviceType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)) {
			dbtype = SrContant.DBTYPE_SR;
		}
		return dbtype;
	}

	/**
	 * 添加或编辑模板
	 */
	public ActionResult doAddWidget() {
		ChartService cser = new ChartService();
		String charttype = getStrParameter("charttype");
		try {
			DataRow row = new DataRow();
			//公共属性
			row.set("fmodelid", getStrParameter("modelid"));
			int fid = getIntParameter("fid",0);
			row.set("fcharttype", charttype);
			row.set("fisshow", 1);
			row.set("fid", fid == 0 ? (cser.getmaxId() + 1) : fid);
			row.set("fname",new String(getStrParameter("fname").replaceAll("&amp;nbsp;", " ").getBytes(), "gbk"));
			row.set("frefresh", getStrParameter("refresh"));
			
			//For list
			if (charttype.equals(CHART_TYPE_LIST)) {
				
			//For log
			} else if (charttype.equals(CHART_TYPE_LOG)) {
				row.set("fsize", 12);
				cser.addChart(row);
				this.setAttribute("item", row);
				Index index = new Index();
				this.setAttribute("logCount", index.getLogCounts());
				return new ActionResult("/WEB-INF/views/widget/logdata.jsp");
			//For line 
			} else if (charttype.equals(CHART_TYPE_LINE)) { 
				row.set("fdevicetype", getStrParameter("devType"));
				row.set("fdevice", getStrParameter("device"));
				row.set("fsubdev", getStrParameter("subDev").replaceAll("&amp;acute;","\"").replaceAll("&amp;nbsp;", " "));
				row.set("fprfid", getStrParameter("prfField"));
				row.set("fprfview", getStrParameter("view"));
				row.set("fdaterange", getStrParameter("daterange"));
				row.set("ftimesize", getStrParameter("timesize"));
				row.set("fyaxisname", getStrParameter("yname").replaceAll("&amp;nbsp;", " "));
				row.set("flegend", getStrParameter("flegend") == "" ? 0 : getStrParameter("flegend"));
				row.set("fsize", getStrParameter("winSize"));
				row.set("ftitle", getStrParameter("title").replaceAll("&amp;nbsp;", " "));
				row.set("fdbtype", widgetService.getKPIinfo(getStrParameter("prfField")).getString("fdbtype"));
				this.setAttribute("item", row);
				cser.addChart(row);
				return new ActionResult("/WEB-INF/views/widget/drawimg.jsp");
			//For capacity
			} else if(charttype.equals(CHART_TYPE_CAPACITY)) {
				String deviceType = getStrParameter("devType").replaceAll("&amp;nbsp;", " ");
				row.set("fsize", getStrParameter("winSize"));
				row.set("fdevicetype", deviceType);
				row.set("fdevice", getStrParameter("device"));
				row.set("fdbtype", getDBType(deviceType));
				this.setAttribute("item", row);
				cser.addChart(row);
				return new ActionResult("/WEB-INF/views/widget/drawimg.jsp");
			//For topn
			} else if (charttype.endsWith(CHART_TYPE_TOPN)) {
				row.set("fdevicetype", getStrParameter("devType"));
				row.set("fdevice", getStrParameter("device"));
				row.set("fsubdev", getStrParameter("subDev").replaceAll("&amp;acute;","\"").replaceAll("&amp;nbsp;", " "));
				row.set("fprfid", getStrParameter("prfField"));
				row.set("fprfview", getStrParameter("view"));
				row.set("fdaterange", getStrParameter("daterange"));
				row.set("ftimesize", getStrParameter("timesize"));
				row.set("fyaxisname", getStrParameter("yname").replaceAll("&amp;nbsp;", " "));
				row.set("ftopncount", getStrParameter("topncount"));
				row.set("fsize", getStrParameter("winSize"));
				row.set("ftitle", getStrParameter("title").replaceAll("&amp;nbsp;", " "));
				row.set("fdbtype", widgetService.getKPIinfo(getStrParameter("prfField")).getString("fdbtype"));
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
	public void doWidgetContent() {
		String fid = getStrParameter("fid");
		DataRow row = widgetService.getWidgetInfo(fid);
		JSONObject json = new JSONObject();
		String chartType = row.getString("fcharttype");
		//For line
		if (chartType.equals(CHART_TYPE_LINE)) {
			// 模块图形相关属性
			json.put("title", row.getString("fname"));
			json.put("size", row.getString("fsize"));
			json.put("legend", row.getString("flegend").equals("1") ? true : false);
			json.put("yname", row.getString("fyaxisname"));
			json.put("refresh", row.getString("frefresh"));
			String daterange = row.getString("fdaterange");
			Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			if (daterange.equals("day")) {
				ca.add(Calendar.DAY_OF_MONTH, -1);
			} else if (daterange.equals("week")) {
				ca.add(Calendar.DAY_OF_MONTH, -7);
			} else {
				ca.add(Calendar.MONTH, -1);
			}
			String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
			json.put("smallTitle", startTime + " ~ " + endTime);
			JSONArray array = new JSONArray();
			array = widgetService.getHighchartLineData(row);
			json.put("series", array);
			json.put("charttype", "line");
		//For topn
		} else if (chartType.equals(CHART_TYPE_TOPN)) {
			String daterange = row.getString("fdaterange");
			Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			if (daterange.equals("day")) {
				ca.add(Calendar.DAY_OF_MONTH, -1);
			} else if (daterange.equals("week")) {
				ca.add(Calendar.DAY_OF_MONTH, -7);
			} else {
				ca.add(Calendar.MONTH, -1);
			}
			String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
			json.put("smallTitle", startTime + " ~ " + endTime);
			json.put("funits", row.getString("fyaxisname"));
			JSONObject obj = new JSONObject();
			obj = widgetService.getHighchartTopnData(row);
			json.putAll(obj);
			json.put("ftitle", row.getString("ftitle"));
			json.put("charttype", "rcloumn");
		//For column
		} else if (chartType.equals(CHART_TYPE_CLOUMN)) {
			
		//For capacity
		} else if (chartType.equals(CHART_TYPE_CAPACITY)) {
			String displayName = null;
			DataRow row2 = widgetService.getCapacityInfo(row);
			if (row2 != null) {
				displayName = row2.getString("display_name");
				if (StringHelper.isNotEmpty(displayName) && StringHelper.isNotBlank(displayName)) {
					if (displayName.length() > 32) {
						displayName = displayName.substring(0, 32) + "...";
					}
				}
			}
			json.put("smallTitle", displayName + " Capacity Info");
			JSONArray array = new JSONArray();
			array = widgetService.getHighchartPieData(row);
			json.put("series", array);
			json.put("charttype", "pie");
		//For list
		} else if (chartType.equals(CHART_TYPE_LIST)) {
			Index index = new Index();
			json.put("charttype", "log");
			json.put("logCount", index.getLogCounts());
		//For log
		} else if (chartType.equals(CHART_TYPE_LOG)) {
			
		}
		writetopage(json);
	}

	/**
	 * 删除模块
	 */
	public void doDelWidget() {
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
	public void doDelConsole() {
		ChartService cser = new ChartService();
		Integer fid = getIntParameter("fid");
		JSONObject json = new JSONObject();
		int state = 0;
		if (fid == 1) {
			state = 2;
		} else {
			state = cser.delModel(fid);
		}
		json.put("state", state);
		writetopage(json);
	}

	/**
	 * 向页面写入数据
	 * 
	 * @param obj
	 */
	private void writetopage(Object obj) {
		PrintWriter writer = null;
		try {
			getResponse().setCharacterEncoding("UTF-8");
			writer = getResponse().getWriter();
			writer.print(obj);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		}
	}
}
