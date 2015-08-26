package root.sr.diskgroup;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.office.CSVHelper;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.sr.ddm.DdmService;
import com.huiming.service.sr.diskgroup.DiskgroupService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class DiskgroupAction extends SecurityAction {
	BaseprfService baseprfService = new BaseprfService();
	DiskgroupService diskgroupService = new DiskgroupService();
	DdmService ddmService = new DdmService();

	/**
	 * 磁盘组
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public ActionResult doLoadDiskgroup() {
		Long subSystemID = getLongParameter("subSystemID");
		if (subSystemID == 0) {
			subSystemID = null;
		}
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", SrContant.SR_NumPerPage);
		String pool_id = getStrParameter("pool_id");
		String name = getStrParameter("diskgroupName");
		String raidLevel = getStrParameter("raidLevel");
		DBPage page = diskgroupService.getDiskgroupList(curPage, numPerPage, name, raidLevel, subSystemID, pool_id);
		this.setAttribute("diskPage", page);
		List<DataRow> tops10 = diskgroupService.getRaidIOpsTop10(subSystemID, null, null);

		List categories = new ArrayList(); // 名称列表
		Map map = new HashMap();
		JSONArray jsArray = new JSONArray(); // 把带宽数值存到JSONArray中
		for (int i = 0; i < tops10.size(); i++) {
			JSONObject diskjson = new JSONObject();
			categories.add("RAID Group " + tops10.get(i).getString("diskgroup_name"));
			diskjson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(tops10.get(i).getDouble("avgiops"))));
			diskjson.put("diskgroupId", tops10.get(i).getInt("diskgroup_id"));
			diskjson.put("subsystemId", tops10.get(i).getInt("subsystem_id"));
			jsArray.add(diskjson);
		}
		JSONArray categorie = new JSONArray().fromObject(categories);
		map.put("name", "IO/s");
		map.put("data", jsArray);
		JSONArray array = new JSONArray().fromObject(map);
		// array.add(map2); //若要查看多个选项，只需添加一个非空的map对象
		this.setAttribute("categories", categorie);
		this.setAttribute("array", array);
		this.setAttribute("diskgroupName", name);
		this.setAttribute("raidLevel", raidLevel);
		this.setAttribute("pool_id", pool_id);
		this.setAttribute("subSystemID", subSystemID);
		return new ActionResult("/WEB-INF/views/sr/diskgroup/diskgroupList.jsp");
	}

	/**
	 * 分页显示磁盘组信息列表
	 * @return
	 */
	public ActionResult doAjaxStoragePage() {
		Long subSystemID = getLongParameter("subSystemID");
		String storageType = getStrParameter("storageType");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", SrContant.SR_NumPerPage);
		String pool_id = getStrParameter("pool_id");
		String name = getStrParameter("diskgroupName").replaceAll("&amp;nbsp;"," ");
		name = name.replaceAll("RAID Group ", "");
		String raidLevel = getStrParameter("raidLevel").replaceAll("&amp;nbsp;", " ");
		DBPage page = diskgroupService.getDiskgroupList(curPage, numPerPage, name, raidLevel, subSystemID, pool_id);
		setAttribute("diskgroupName", name);
		setAttribute("raidLevel", raidLevel);
		setAttribute("diskPage", page);
		setAttribute("pool_id", pool_id);
		setAttribute("subSystemID", subSystemID);
		setAttribute("storageType", storageType);
		return new ActionResult("/WEB-INF/views/sr/diskgroup/diskgroupPage.jsp");
	}

	/**
	 * 磁盘组的详细信息
	 * @return
	 */
	public ActionResult doLoadDiskgroupInfo() {
		Integer subsystemId = getIntParameter("subsystemId");
		Integer diskgroupId = getIntParameter("diskgroupId");
		String storageType = getStrParameter("storageType");
		setAttribute("subDiskPage", ddmService.getDiskPage(1, SrContant.SR_NumPerPage, subsystemId, diskgroupId));
		DataRow diskgroupInfo = diskgroupService.getDiskgroupInfo(subsystemId, diskgroupId);
		
		setAttribute("diskgroupInfo", diskgroupInfo);
		setAttribute("subsystemId", subsystemId);
		setAttribute("diskgroupId", diskgroupId);
		setAttribute("storageType", storageType);

		return new ActionResult("/WEB-INF/views/sr/diskgroup/diskgroupInfo.jsp");
	}

	/**
	 * 获取性能信息
	 * @return
	 */
	public ActionResult doLoadPerfInfo(){
		getPerfInfoData();
		String tablePage = getStrParameter("tablePage");
		if (StringHelper.isNotEmpty(tablePage) && StringHelper.isNotBlank(tablePage)) {
			return new ActionResult("/WEB-INF/views/sr/diskgroup/diskgroupPerfPage.jsp");
		}
		return new ActionResult("/WEB-INF/views/sr/diskgroup/diskgroupWriteLine.jsp");
	}
	
	/**
	 * 获取并处理性能信息数据
	 */
	public void getPerfInfoData(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("diskgroupId");
		String storageType = getStrParameter("storageType");
		String tablePage = getStrParameter("tablePage");
		Integer level = getIntParameter("level", devId == 0 ? 2 : 3);
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow dataRow = baseprfService.getPrfFieldInfo(null,level,SrContant.SUBDEVTYPE_DISKGROUP,storageType,subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if (dataRow == null || dataRow.size() == 0) {
			dataRow = baseprfService.getDefaultRow(SrTblColConstant.TBL_RES_DISKGROUP, devId, storageType, SrContant.SUBDEVTYPE_DISKGROUP, SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME);
		}
		
		if(dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = baseprfService.getDeviceInfoList(dataRow.getString("fdevice"), SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME, SrTblColConstant.TBL_RES_DISKGROUP);
			List<DataRow> kpis = baseprfService.getKPIInfo(dataRow.getString("fprfid"));
			thead.set("prf_timestamp", "时间");
			thead.set("ele_name", "设备名");
			for (DataRow r : kpis) {
				thead.set(r.getString("fid"), r.getString("ftitle").concat("(" + r.getString("funits") + ")"));
			}
			tbody = baseprfService.getPrfDatas(curPage, numPerPage, devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"));

			if (StringHelper.isEmpty(tablePage) || StringHelper.isBlank(tablePage)) {
				JSONArray array = baseprfService.getPrfDatas(dataRow.getInt("fisshow"), devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"));
				json.put("series", array);
			}
			json.put("legend", dataRow.getInt("flegend") == 1 ? true : false);
			json.put("ytitle", dataRow.getString("fyaxisname"));
			json.put("threshold", dataRow.getInt("fthreshold"));
			json.put("threvalue", dataRow.getString("fthrevalue"));
			json.put("thead", thead);
			json.put("tbody", tbody);
			json.put("kpiInfo", kpis);
		} 
		setAttribute("prfData", json);
		setAttribute("subsystemId", subsystemId);
		setAttribute("diskgroupId", devId);
		setAttribute("storageType", storageType);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
	}
	
	/**
	 * 设置性能图信息
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doPerfChartSetting(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("diskgroupId");
		//获取存储系统信息
		DataRow storageInfo = baseprfService.getStorageInfo(subsystemId);
		String storageType = storageInfo.getString("type");
		//获取性能指标
		List<DataRow> kpiList = baseprfService.getView(storageType, SrContant.SUBDEVTYPE_DISKGROUP);
		List<DataRow> devList = baseprfService.getDeviceList(subsystemId, SrTblColConstant.TBL_RES_DISKGROUP, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME);
		setAttribute("kpisList", new JSONArray().fromObject(kpiList));
		setAttribute("devList", new JSONArray().fromObject(devList));
		setAttribute("subSystemID", subsystemId);
		setAttribute("storageInfo", storageInfo);
		if (devId != null && devId > 0) {
			//获取历史配置性能信息
			DataRow config = baseprfService.getPrfFieldInfo(null, 3, SrContant.SUBDEVTYPE_DISKGROUP, storageType, subsystemId, devId,getLoginUserId());
			if (config == null) {
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				setAttribute("historyConfig", drow);
			} else {
				setAttribute("historyConfig", config);
			}
			setAttribute("level", 3);
		} else {
			setAttribute("historyConfig",baseprfService.getPrfFieldInfo(null, 2, SrContant.SUBDEVTYPE_DISKGROUP, storageType, subsystemId, null,getLoginUserId()));
			setAttribute("level", 2);
		}
		setAttribute("url", "servlet/sr/diskgroup/DiskgroupAction?func=PerfSetting");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	/**
	 * 设置性能图信息，这是HGC编写的查询条件
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doPerfChartSetting2(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("diskgroupId");
		//获取存储系统信息
		DataRow storageInfo = baseprfService.getStorageInfo(subsystemId);
		String storageType = storageInfo.getString("type");
		//获取性能指标
		List<DataRow> kpiList = baseprfService.getView(storageType, SrContant.SUBDEVTYPE_DISKGROUP);
		List<DataRow> devList = baseprfService.getDeviceList(subsystemId, SrTblColConstant.TBL_RES_DISKGROUP, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME);
		setAttribute("kpisList", new JSONArray().fromObject(kpiList));
		setAttribute("devList", new JSONArray().fromObject(devList));
		setAttribute("subSystemID", subsystemId);
		setAttribute("storageInfo", storageInfo);
		if (devId != null && devId > 0) {
			//获取历史配置性能信息
			DataRow config = baseprfService.getPrfFieldInfo(null, 3, SrContant.SUBDEVTYPE_DISKGROUP, storageType, subsystemId, devId, getLoginUserId());
			if (config == null) {
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				setAttribute("historyConfig", drow);
			} else {
				setAttribute("historyConfig", config);
			}
			setAttribute("level", 3);
		} else {
			setAttribute("historyConfig",baseprfService.getPrfFieldInfo(null, 2, SrContant.SUBDEVTYPE_DISKGROUP, storageType, subsystemId, null, getLoginUserId()));
			setAttribute("level", 2);
		}
		setAttribute("url", "servlet/sr/diskgroup/DiskgroupAction?func=PerfSetting");
		return new ActionResult("/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp");
	}
	
	/**
	 * 性能图信息设置
	 */
	public void doPerfSetting(){
		Integer subsystemId = getIntParameter("subSystemID");
		Integer devId = getIntParameter("devId");
		String storageType = getStrParameter("storageType");
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
		Integer showname = getIntParameter("isshow",getIntParameter("hideisshow"));
		Integer legend = getIntParameter("legend");
		String yname = getStrParameter("yname").replaceAll("&amp;nbsp;", " ");
		Integer threshold = getIntParameter("threshold");
		String threValue = getStrParameter("threValue").replaceAll("&amp;nbsp;", " ");
		Integer level = getIntParameter("level");
		DataRow row = new DataRow();
		row.set("fsubsystemid", subsystemId);
		row.set("level", level);
		row.set("fname", SrContant.SUBDEVTYPE_DISKGROUP);
		row.set("fdevicetype", storageType);
		row.set("fdevice", dev);
		row.set("fprfid", kpi.toString());
//		row.set("fisshow", showname);
		row.set("fisshow", 1);
		row.set("fyaxisname", yname);
		row.set("flegend", legend);
		row.set("fstarttime", startTime);
		row.set("fendtime", endTime);
		row.set("fthreshold", threshold);
		row.set("fthreValue", threValue);
		row.set("fuserid", getLoginUserId());
		try {
			baseprfService.updatePrfField(row, SrContant.SUBDEVTYPE_DISKGROUP, storageType, devId, subsystemId, level);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.print(getResponse(), "false");
		}
	}

	/**
	 * 导出性能数据
	 */
	@SuppressWarnings("unchecked")
	public void doExportPerfData() {
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("diskgroupId");
		String storageType = getStrParameter("storageType");
		Integer level = getIntParameter("level", devId == 0 ? 2 : 3);
		DataRow dataRow = baseprfService.getPrfFieldInfo(null,level,SrContant.SUBDEVTYPE_DISKGROUP,storageType,subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		
		if(dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = baseprfService.getDeviceInfoList(dataRow.getString("fdevice"), SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME, SrTblColConstant.TBL_RES_DISKGROUP);
			List<DataRow> kpis = baseprfService.getKPIInfo(dataRow.getString("fprfid"));
			thead.set("prf_timestamp", "时间");
			thead.set("ele_name", "设备名");
			for (DataRow r : kpis) {
				thead.set(r.getString("fid"), r.getString("ftitle").concat("(" + r.getString("funits") + ")"));
			}
			List<DataRow> tbody = baseprfService.getPrfDatas(devs, kpis, dataRow.getString("fstarttime"), dataRow.getString("fendtime"));
			if (tbody != null && tbody.size() > 0) {
				String[] title = (String[]) thead.values().toArray(new String[thead.size()]);
				String[] key = new String[thead.keySet().size()];
				Iterator<Object> it = thead.keySet().iterator();
				for (int i = 0; i < thead.keySet().size(); i++) {
					key[i] = it.next().toString().toLowerCase();
				}
				getResponse().setCharacterEncoding("GBK");
				CSVHelper.createCSVToPrintWriter(getResponse(), devs.get(0).getString("ele_name"), tbody, title, key);
			}
		} 
	}

	/**
	 * 导出磁盘组配置信息数据
	 */
	public void doExportConfigData() {
		Long subSystemID = getLongParameter("subSystemID");
		String name = getStrParameter("diskgroupName").replaceAll("&amp;nbsp;"," ");
		String raidLevel = getStrParameter("raidLevel").replaceAll("&amp;nbsp;", " ");
		List<DataRow> rows = diskgroupService.getDiskgroupExportList(name, raidLevel,subSystemID, null);
		if (rows != null && rows.size() > 0) {
			String[] title = new String[] { "磁盘组名称", "阵列类型", "磁盘速度", "磁盘容量(MB)", "磁盘数", "池名称" };
			String[] keys = new String[] { "name", "raid_level", "ddm_speed", "ddm_cap", "width", "pname" };
			getResponse().setCharacterEncoding("GBK");
			CSVHelper.createCSVToPrintWriter(getResponse(), "diskgroupConfigData", rows, title, keys);
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
	
	/**
	 * 写入数据到页面
	 * @param data
	 */
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
