package root.sr.volume;

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
import com.huiming.service.sr.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class VolumeAction extends SecurityAction {
	BaseprfService baseprfService = new BaseprfService();
	VolumeService volumeService = new VolumeService();

	/**
	 * 卷信息
	 * @return
	 */
	@SuppressWarnings({ "static-access" })
	public ActionResult doVolumePage() {
		DBPage page = null;
		int curPage = getIntParameter("curPage", 1);
		String system_id = getStrParameter("subSystemID");
		int numPerPage = getIntParameter("numPerPage", SrContant.SR_NumPerPage);
		page = volumeService.getVolumePage(curPage, numPerPage, null, null, null, null, system_id);
		this.setAttribute("volumePage", page);
		List<DataRow> rows = volumeService.gettop10VolumePerfInfo(Integer.parseInt(system_id), null, null);
		Map<Object, Object> map = new HashMap<Object, Object>();
		JSONArray jarray = new JSONArray();
		JSONArray names = new JSONArray();
		for (DataRow dataRow : rows) {
			JSONObject dataJson = new JSONObject();
			names.add("LUN " + dataRow.getString("volume_name"));
			dataJson.put("volumeId", dataRow.getInt("volume_id"));
			dataJson.put("subsystemId", dataRow.getInt("subsystem_id"));
			dataJson.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("avg_iops"))));
			jarray.add(dataJson);
		}
		map.put("name", "IO/s");
		map.put("data", jarray);
		JSONArray array = new JSONArray().fromObject(map);
		this.setAttribute("names", names);
		this.setAttribute("array", array);
		this.setAttribute("subSystemID", system_id);
		return new ActionResult("/WEB-INF/views/sr/volume/volumeList.jsp");
	}

	/**
	 * 分页查询卷信息列表
	 * @return
	 */
	public ActionResult doAjaxVolumePage() {
		DBPage page = null;
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		name = name.substring(name.indexOf(" ") + 1, name.length());
		Long greatCapacity = getLongParameter("greatLogical_Capacity");
		Long lessCapacity = getLongParameter("lessLogical_Capacity");
		String poolId = getStrParameter("poolId");
		String systemId = getStrParameter("subSystemID");
		String storageType = getStrParameter("storageType");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", SrContant.SR_NumPerPage);
		page = volumeService.getVolumePage(curPage, numPerPage, name, greatCapacity, lessCapacity, poolId, systemId);

		setAttribute("name", name);
		setAttribute("greatLogical_Capacity", greatCapacity);
		setAttribute("lessLogical_Capacity", lessCapacity);
		setAttribute("volumePage", page);
		setAttribute("poolId", poolId);
		setAttribute("subSystemID", systemId);
		setAttribute("storageType", storageType);
		return new ActionResult("/WEB-INF/views/sr/volume/ajaxVolume.jsp");
	}
	
	/**
	 * 卷详细信息
	 * @return
	 */
	public ActionResult doLoadVolumeInfo() {
		Integer subsystemId = getIntParameter("subsystemId");
		Integer volumeId = getIntParameter("volumeId");
		String storageType = getStrParameter("storageType");
		DataRow volumeInfo = volumeService.getVolumeInfo(subsystemId, volumeId);
		setAttribute("volumeInfo", volumeInfo);
		setAttribute("subsystemId", subsystemId);
		setAttribute("volumeId", volumeId);
		setAttribute("storageType", storageType);
		return new ActionResult("/WEB-INF/views/sr/volume/volumeInfo.jsp");
	}

	/**
	 * 获取性能信息
	 * @return
	 */
	public ActionResult doLoadPerfInfo(){
		getPerfInfoData();
		String tablePage = getStrParameter("tablePage");
		if (StringHelper.isNotEmpty(tablePage) && StringHelper.isNotBlank(tablePage)) {
			return new ActionResult("/WEB-INF/views/sr/volume/prefVolumePage.jsp");
		}
		return new ActionResult("/WEB-INF/views/sr/volume/ajaxPrfVolume.jsp");
	}
	
	/**
	 * 获取并处理性能信息数据
	 */
	public void getPerfInfoData(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("volumeId");
		String storageType = getStrParameter("storageType");
		String tablePage = getStrParameter("tablePage");
		Integer level = getIntParameter("level", devId == 0 ? 2 : 3);
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow dataRow = baseprfService.getPrfFieldInfo(null,level,SrContant.SUBDEVTYPE_VOLUME,storageType,subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if (dataRow == null || dataRow.size() == 0) {
			dataRow = baseprfService.getDefaultRow(SrTblColConstant.TBL_RES_STORAGEVOLUME, devId, storageType, SrContant.SUBDEVTYPE_VOLUME, SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME);
		}
		
		if(dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = baseprfService.getDeviceInfoList(dataRow.getString("fdevice"), SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME, SrTblColConstant.TBL_RES_STORAGEVOLUME);
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
		setAttribute("volumeId", devId);
		setAttribute("storageType", storageType);
		String isFreshen = getStrParameter("isFreshen");
		if("1".equals(isFreshen)){
			writeDataToPage(json.toString());
		}
	}
	
	/**
	 * 设置性能图信息
	 * @return/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp
	 */
	@SuppressWarnings("static-access")
	public ActionResult doPerfChartSetting(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("volumeId");
		//获取存储系统信息
		DataRow storageInfo = baseprfService.getStorageInfo(subsystemId);
		String storageType = storageInfo.getString("type");
		//获取性能指标
		List<DataRow> kpiList = baseprfService.getView(storageType, SrContant.SUBDEVTYPE_VOLUME);
		List<DataRow> devList = baseprfService.getDeviceList(subsystemId, SrTblColConstant.TBL_RES_STORAGEVOLUME, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME);
		setAttribute("kpisList", new JSONArray().fromObject(kpiList));
		setAttribute("devList", new JSONArray().fromObject(devList));
		setAttribute("subSystemID", subsystemId);
		setAttribute("storageInfo", storageInfo);
		if (devId != null && devId > 0) {
			//获取历史配置性能信息
			DataRow config = baseprfService.getPrfFieldInfo(null, 3, SrContant.SUBDEVTYPE_VOLUME, storageType, subsystemId, devId, getLoginUserId());
			if (config == null) {
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				setAttribute("historyConfig", drow);
			} else {
				setAttribute("historyConfig", config);
			}
			setAttribute("level", 3);
		} else {
			setAttribute("historyConfig",baseprfService.getPrfFieldInfo(null, 2, SrContant.SUBDEVTYPE_VOLUME, storageType, subsystemId, null, getLoginUserId()));
			setAttribute("level", 2);
		}
		setAttribute("url", "servlet/sr/volume/VolumeAction?func=PerfSetting");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	/**
	 * 设置性能图信息
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doPerfChartSetting2(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("volumeId");
		//获取存储系统信息
		DataRow storageInfo = baseprfService.getStorageInfo(subsystemId);
		String storageType = storageInfo.getString("type");
		//获取性能指标
		List<DataRow> kpiList = baseprfService.getView(storageType, SrContant.SUBDEVTYPE_VOLUME);
		List<DataRow> devList = baseprfService.getDeviceList(subsystemId, SrTblColConstant.TBL_RES_STORAGEVOLUME, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME);
		setAttribute("kpisList", new JSONArray().fromObject(kpiList));
		setAttribute("devList", new JSONArray().fromObject(devList));
		setAttribute("subSystemID", subsystemId);
		setAttribute("storageInfo", storageInfo);
		if (devId != null && devId > 0) {
			//获取历史配置性能信息
			DataRow config = baseprfService.getPrfFieldInfo(null, 3, SrContant.SUBDEVTYPE_VOLUME, storageType, subsystemId, devId, getLoginUserId());
			if (config == null) {
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				setAttribute("historyConfig", drow);
			} else {
				setAttribute("historyConfig", config);
			}
			setAttribute("level", 3);
		} else {
			setAttribute("historyConfig",baseprfService.getPrfFieldInfo(null, 2, SrContant.SUBDEVTYPE_VOLUME, storageType, subsystemId, null, getLoginUserId()));
			setAttribute("level", 2);
		}
		setAttribute("url", "servlet/sr/volume/VolumeAction?func=PerfSetting");
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
		row.set("fname", SrContant.SUBDEVTYPE_VOLUME);
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
			baseprfService.updatePrfField(row, SrContant.SUBDEVTYPE_VOLUME, storageType, devId, subsystemId, level);
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
		Integer devId = getIntParameter("volumeId");
		String storageType = getStrParameter("storageType");
		Integer level = getIntParameter("level", devId == 0 ? 2 : 3);
		DataRow dataRow = baseprfService.getPrfFieldInfo(null,level,SrContant.SUBDEVTYPE_VOLUME,storageType,subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		
		if(dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = baseprfService.getDeviceInfoList(dataRow.getString("fdevice"), SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME, SrTblColConstant.TBL_RES_STORAGEVOLUME);
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
	 * 导出卷配置信息
	 */
	public void doExpertVolumeConfigData() {
		String name = getStrParameter("name");
		String poolId = getStrParameter("poolId");
		Long greatCapacity = getLongParameter("greatLogical_Capacity");
		Long lessCapacity = getLongParameter("lessLogical_Capacity");
		String subSystemID = getStrParameter("subSystemID");

		List<DataRow> rows = volumeService.exportVolumeConfigData(name, greatCapacity, lessCapacity, poolId, subSystemID);
		if (rows != null && rows.size() > 0) {
			String[] title = new String[] { "卷名称", "逻辑容量(MB)", "实占空间(MB)", "阵列类型", "DEFAULT_OWNER", "CURRENT_OWNER" };
			String[] keys = new String[] { "name", "logical_capacity", "physical_capacity", "raid_level", "default_owner", "current_owner" };
			getResponse().setCharacterEncoding("gbk");
			CSVHelper.createCSVToPrintWriter(getResponse(), "VolumeConfigData", rows, title, keys);
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
