package root.sr.storageport;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.huiming.service.sr.port.PortService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

/**
 * 端口
 * @author Administrator
 * 
 */
public class StoragePortAction extends SecurityAction {
	BaseprfService baseprfService = new BaseprfService();
	PortService portService = new PortService();

	/**
	 * 端口
	 * @return
	 */
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	public ActionResult doLoadPorts() {
		Long subSystemID = getLongParameter("subSystemID");
		if (subSystemID == 0) {
			subSystemID = null;
		}
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", SrContant.SR_NumPerPage);
		DBPage page = portService.getPortList(curPage, numPerPage, null, null, subSystemID);
		this.setAttribute("portPage", page);
		List<DataRow> rows = portService.getPortInfos(subSystemID);

		List categories = new ArrayList();
		List speeds = new ArrayList();
		Map map = new HashMap();
		for (int i = 0; i < rows.size(); i++) {
			categories.add(rows.get(i).getString("name"));
			int speed;
			String speedStr = rows.get(i).getString("port_speed");
			// 非空判断
			if ("N/A".equalsIgnoreCase(speedStr) || "".equals(speedStr)) {
				speed = 0;
			} else {
				// 截取内容，去除单位
				speedStr = speedStr.substring(0, speedStr.indexOf("Gbps"));
				speed = Integer.parseInt(speedStr);
			}
			if (speed != -1) {
				speeds.add(speed);
			}
		}
		JSONArray categore = new JSONArray().fromObject(categories);
		JSONArray jsArray = new JSONArray().fromObject(speeds);
		map.put("name", "端口速率");
		map.put("data", jsArray);
		JSONArray array = new JSONArray().fromObject(map);
		this.setAttribute("categories", categore);
		this.setAttribute("array", array);
		this.setAttribute("subSystemID", subSystemID);
		return new ActionResult("/WEB-INF/views/sr/storagePort/portList.jsp");
	}

	/**
	 * 分页显示端口信息列表
	 * @return
	 */
	public ActionResult doAjaxStoragePage() {
		Long subSystemID = getLongParameter("subSystemID");
		String storageType = getStrParameter("storageType");
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", SrContant.SR_NumPerPage);
		String portName = getStrParameter("portName").replaceAll("&amp;nbsp;"," ");
		String networkAddress = getStrParameter("networkAddress").replaceAll("&amp;nbsp;", " ");
		DBPage page = portService.getPortList(curPage, numPerPage, portName, networkAddress, subSystemID);
		setAttribute("portPage", page);
		setAttribute("portName", portName);
		setAttribute("networkAddress", networkAddress);
		setAttribute("subSystemID", subSystemID);
		setAttribute("storageType", storageType);
		return new ActionResult("/WEB-INF/views/sr/storagePort/portPage.jsp");
	}

	/**
	 * 端口详细信息
	 * @return
	 */
	public ActionResult doLoadPortInfo() {
		Integer subsystemId = getIntParameter("subsystemId");
		Integer portId = getIntParameter("portId");
		String storageType = getStrParameter("storageType");
		DataRow portInfo = portService.getPortInfo(subsystemId,portId);
		setAttribute("portInfo", portInfo);
		setAttribute("subsystemId", subsystemId);
		setAttribute("portId", portId);
		setAttribute("storageType", storageType);
		return new ActionResult("/WEB-INF/views/sr/storagePort/portInfo.jsp");
	}
	
	/**
	 * 获取性能信息
	 * @return
	 */
	public ActionResult doLoadPerfInfo(){
		getPerfInfoData();
		String tablePage = getStrParameter("tablePage");
		if (StringHelper.isNotEmpty(tablePage) && StringHelper.isNotBlank(tablePage)) {
			return new ActionResult("/WEB-INF/views/sr/storagePort/portPerfPage.jsp");
		}
		return new ActionResult("/WEB-INF/views/sr/storagePort/portWriteLine.jsp");
	}
	
	/**
	 * 获取并处理性能信息数据
	 */
	public void getPerfInfoData(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("portId");
		String storageType = getStrParameter("storageType");
		String tablePage = getStrParameter("tablePage");
		Integer level = getIntParameter("level", devId == 0 ? 2 : 3);
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		JSONObject json = new JSONObject();
		DataRow dataRow = baseprfService.getPrfFieldInfo(null,level,SrContant.SUBDEVTYPE_PORT,storageType,subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		DBPage tbody = null;
		//给默认性能信息
		if (dataRow == null || dataRow.size() == 0) {
			dataRow = baseprfService.getDefaultRow(SrTblColConstant.TBL_RES_PORT, devId, storageType, SrContant.SUBDEVTYPE_PORT, SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME);
		}
		
		if(dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = baseprfService.getDeviceInfoList(dataRow.getString("fdevice"), SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME, SrTblColConstant.TBL_RES_PORT);
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
		setAttribute("portId", devId);
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
		Integer devId = getIntParameter("portId");
		//获取存储系统信息
		DataRow storageInfo = baseprfService.getStorageInfo(subsystemId);
		String storageType = storageInfo.getString("type");
		//获取性能指标
		List<DataRow> kpiList = baseprfService.getView(storageType, SrContant.SUBDEVTYPE_PORT);
		List<DataRow> devList = baseprfService.getDeviceList(subsystemId, SrTblColConstant.TBL_RES_PORT, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME);
		setAttribute("kpisList", new JSONArray().fromObject(kpiList));
		setAttribute("devList", new JSONArray().fromObject(devList));
		setAttribute("subSystemID", subsystemId);
		setAttribute("storageInfo", storageInfo);
		if (devId != null && devId > 0) {
			//获取历史配置性能信息
			DataRow config = baseprfService.getPrfFieldInfo(null, 3, SrContant.SUBDEVTYPE_PORT, storageType, subsystemId, devId, getLoginUserId());
			if (config == null) {
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				setAttribute("historyConfig", drow);
			} else {
				setAttribute("historyConfig", config);
			}
			setAttribute("level", 3);
		} else {
			setAttribute("historyConfig",baseprfService.getPrfFieldInfo(null, 2, SrContant.SUBDEVTYPE_PORT, storageType, subsystemId, null, getLoginUserId()));
			setAttribute("level", 2);
		}
		setAttribute("url", "servlet/sr/storageport/StoragePortAction?func=PerfSetting");
		return new ActionResult("/WEB-INF/views/alert/editPage.jsp");
	}
	
	/**
	 * 设置性能图信息，这是HGC编写的查询条件，不要删除
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doPerfChartSetting2(){
		Integer subsystemId = getIntParameter("subsystemId");
		Integer devId = getIntParameter("portId");
		//获取存储系统信息
		DataRow storageInfo = baseprfService.getStorageInfo(subsystemId);
		String storageType = storageInfo.getString("type");
		//获取性能指标
		List<DataRow> kpiList = baseprfService.getView(storageType, SrContant.SUBDEVTYPE_PORT);
		List<DataRow> devList = baseprfService.getDeviceList(subsystemId, SrTblColConstant.TBL_RES_PORT, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME);
		setAttribute("kpisList", new JSONArray().fromObject(kpiList));
		setAttribute("devList", new JSONArray().fromObject(devList));
		setAttribute("subSystemID", subsystemId);
		setAttribute("storageInfo", storageInfo);
		if (devId != null && devId > 0) {
			//获取历史配置性能信息
			DataRow config = baseprfService.getPrfFieldInfo(null, 3, SrContant.SUBDEVTYPE_PORT, storageType, subsystemId, devId, getLoginUserId());
			if (config == null) {
				DataRow drow = new DataRow();
				drow.set("fdevice", devId);
				setAttribute("historyConfig", drow);
			} else {
				setAttribute("historyConfig", config);
			}
			setAttribute("level", 3);
		} else {
			setAttribute("historyConfig",baseprfService.getPrfFieldInfo(null, 2, SrContant.SUBDEVTYPE_PORT, storageType, subsystemId, null, getLoginUserId()));
			setAttribute("level", 2);
		}
		setAttribute("url", "servlet/sr/storageport/StoragePortAction?func=PerfSetting");
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
		row.set("fname", SrContant.SUBDEVTYPE_PORT);
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
			baseprfService.updatePrfField(row, SrContant.SUBDEVTYPE_PORT, storageType, devId, subsystemId, level);
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
		Integer devId = getIntParameter("portId");
		String storageType = getStrParameter("storageType");
		Integer level = getIntParameter("level", devId == 0 ? 2 : 3);
		DataRow dataRow = baseprfService.getPrfFieldInfo(null,level,SrContant.SUBDEVTYPE_PORT,storageType,subsystemId,devId,getLoginUserId());
		DataRow thead = new DataRow();
		
		if(dataRow != null && dataRow.size() > 0){
			List<DataRow> devs = baseprfService.getDeviceInfoList(dataRow.getString("fdevice"), SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME, SrTblColConstant.TBL_RES_PORT);
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
	 * 导出端口配置信息
	 */
	public void doExportPortConfigData() {
		String portName = getStrParameter("portName").replaceAll("&amp;nbsp;"," ");
		String networkAddress = getStrParameter("networkAddress").replaceAll("&amp;nbsp;", " ");
		Long subSystemID = getLongParameter("subSystemID");

		List<DataRow> rows = portService.exportPortConfigData(portName,networkAddress, subSystemID);
		if (rows != null && rows.size() > 0) {
			String[] title = new String[] { "端口名称", "网络地址", "端口速度", "端口类型" };
			String[] keys = new String[] { "name", "network_address", "port_speed", "type" };
			getResponse().setCharacterEncoding("GBK");
			CSVHelper.createCSVToPrintWriter(getResponse(), "PoolConfigData", rows, title, keys);
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
