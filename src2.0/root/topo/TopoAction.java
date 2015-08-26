package root.topo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.service.topo.TopoService;
import com.huiming.service.topo.tree.MultiTree;
import com.huiming.service.topo.tree.TreeUtils;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;

public class TopoAction extends SecurityAction {
	private static final Logger logger = Logger.getLogger(TopoAction.class);
	TopoService service = new TopoService();

	/**
	 * @see 根据时间“1周”获取总览页面的曲线
	 */
	public void doShowSummaryCurve(){
		JsonData jsonData = new JsonData();
		StringBuilder msg = new StringBuilder();
		
		int errorCount = 0;
		String devicetype = this.getStrParameter("devtype");
		String stotype = this.getStrParameter("stotype");
		Long deviceid = this.getLongParameter("devid");
		String startdate = this.getStrParameter("startdate");
		
		if(devicetype == null || devicetype.trim().isEmpty()){
			++errorCount;
			msg.append(errorCount + "、设备类型为空<br>");
		}
		if(deviceid == null || deviceid <= 0){
			++errorCount;
			msg.append(errorCount + "、该设备不存在<br>");
		}
		if(errorCount == 0){ // 说明参数验证正确
			if(devicetype.equalsIgnoreCase("Switch")){
				jsonData.setValue(service.getSwitchTotalPortDataRate(deviceid, startdate));
			}
			else {
				jsonData.setValue(service.getStorageTotalIORate(startdate, stotype, deviceid));
			}
		}
		jsonData.setMsg(msg.toString());
		print(jsonData);
	}
	
	public void doSaveTopoIconsPosition(){
		int nodeCount = this.getIntParameter("nodeCount");
		long appid = this.getIntParameter("appid");
//		long userId = this.getUserIdFromSession();
		String params[];
		JsonData jsonData = new JsonData();
		List<DataRow> nodes = new ArrayList<DataRow>(nodeCount);
		for(int i = 0; i < nodeCount; ++i){
			params = getStrParameter("node" + i).split(",");
			if(params != null && params.length >= 4){
				DataRow dr = new DataRow();
				// 物理机%|%288%|%637.5%|%-66.5
				dr.set("appid", appid);
				dr.set("devtype", params[0]);
				dr.set("devid", params[1]);
				dr.set("dev_x", params[2]);
				dr.set("dev_y", params[3]);
//				dr.set("user_id", userId);
				nodes.add(dr);
			}
		}
		jsonData.setValue(nodes);
		try{
			service.updateNodeInfo(nodes, appid, this.getUserIdFromSession(), 
					this.getUserTypeFromSession());
			jsonData.setMsg("成功保存节点位置");
		}catch(Exception ex){
			jsonData.setMsg("保存节点位置失败");
			logger.info(jsonData.getMsg(), ex);
		}
		print(jsonData);
	}
	
	public void doGetSwitchPortData(){
		Long swid = getLongParameter("swid");
		JsonData jsonData = new JsonData();
		if(swid == null || swid <= 0L){
			jsonData.setSuccess(false);
			jsonData.setMsg("交换机编号不存在");
			print(jsonData);
			return;
		}
		try{
			jsonData.setValue(service.getSwitchPortBySwitchid(swid));
		}catch(Exception ex){
			jsonData.setSuccess(false);
			jsonData.setMsg("获取交换机(编号"+swid+")的数据失败");
			logger.error("", ex);
		}
		print(jsonData);
	}
	
	public void doGetAppDataFromMySQL(){
		JsonData jsonData = new JsonData();
		try{
			jsonData.setValue(service.getALLAppDataFromMySQL());
			jsonData.setMsg("成功获取所有的Windows X86 Server");
		}catch(Exception ex){
			jsonData.setSuccess(false);
			jsonData.setMsg("获取Windows X86 Server失败");
			logger.error("", ex);
		}
		print(jsonData);
	}
	
	/**
	 * @see 任何情况下，应用数据来自于MySQL
	 */
	public void doGetAllAppData(){
		print(service.getAppDataByIds(null));
	}
	
	//////////////////////////// 2015-03-03版拓扑
	/**
	 * @see 跳转到拓扑页面
	 * @return
	 */
	public ActionResult doHuimingTopo(){
		long appId = this.getLongParameter("appId", -1L);
		int action = this.getIntParameter("action", SrContant.STATE_ADD);
		if(appId > 0){
			this.setAttribute("appId", appId);
		}
		this.setAttribute("action", action);
		return new ActionResult("/WEB-INF/views/topo/topo.jsp");
	}
	
	/**
	 * @see 增加拓扑
	 * @return
	 */
	public ActionResult doTopoAddApp(){
		if(Configure.getInstance().getDataSource(WebConstants.DB_TPC) == null){
			setAttribute("title", "警告");
			setAttribute("msg", "不支持非SAN网络环境下实现拓扑图！");
			return new ActionResult("/WEB-INF/views/topo/MsgDialog.jsp");
		}
		long userId = this.getUserIdFromSession();
		String role = this.getUserTypeFromSession();
		//setAttribute("hypData", service.getAllHypervisor(userId, role));
		setAttribute("stoData", service.getAllStorage(userId, role));
		setAttribute("swData", service.getAllSwitch(userId, role));
		
		setAttribute("action", SrContant.STATE_ADD);
		
		return new ActionResult("/WEB-INF/views/topo/addApplications.jsp");
	}
	
	public void doGetAllPhysical(){
		JsonData jsonData = new JsonData();
		try{
			long userId = this.getUserIdFromSession();
			String role = this.getUserTypeFromSession();
			jsonData.setValue(service.getAllHypervisor(userId, role));
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取物理机的数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetAllSwitch(){
		JsonData jsonData = new JsonData();
		try{
			long userId = this.getUserIdFromSession();
			String role = this.getUserTypeFromSession();
			jsonData.setValue(service.getAllSwitch(userId, role));
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取交换机存储系统的数据失败");
		}
		printWithDate(jsonData);
	}
	
	/**
	 * 通过起点交换机的编号计算出终点交换机，和存储系统
	 */
	public void doGetAllSwSto(){
		JsonData jsonData = new JsonData();
		try{
			Map<String, Object> json = new HashMap<String, Object>(2);
			long userId = this.getUserIdFromSession();
			String role = this.getUserTypeFromSession();
			
			json.put("stoData", service.getAllStorage(userId, role));
			
			String swIds = this.getStrParameter("swIds");
			if(swIds != null && swIds.trim().length() > 0){
				json.put("swData", service.getAllSwitchByIds(swIds.split(","), userId, role));
			}
			jsonData.setValue(json);
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取交换机存储系统的数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetAllSwpStopBySwIdsStoIds(){
		String stoTPCIds = this.getStrParameter("stoTPCIds");
		String stoSRIds = this.getStrParameter("stoSRIds");
		String swIds = this.getStrParameter("swIds");
		JsonData jsonData = new JsonData();
		try{
			Map<String, Object> json = new HashMap<String, Object>(2);
			Map<String, List<DataRow>> stops = service.getSwpStopByIds(stoTPCIds, stoSRIds);
			json.put("stoData", stops);
			json.put("swpData", service.getSwitchPortsBySwIds(swIds));
			jsonData.setValue(json);
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取交换机端口与存储端口的数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetAllSwitchByHypIds(){
		String hypIds = getStrParameter("hypIds");
		JsonData jsonData = new JsonData();
		try{
			List<DataRow> sws = service.getAllSwitchByHypIds(hypIds);
			if(sws == null || sws.size() == 0){
				jsonData.setSuccess(false);
				jsonData.setMsg("没有交换机数据");
			}
			else {
				jsonData.setValue(sws);
			}
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取交换机数据失败");
		}
		printWithDate(jsonData);
	}
	
	/**
	 * @see 根据物理机获取对应的虚拟机
	 */
	public void doGetVMsByHypIds(){
		String hypIds = getStrParameter("hypIds");
		JsonData jsonData = new JsonData();
		try{
			long userId = this.getUserIdFromSession();
			String role = this.getUserTypeFromSession();
			List<DataRow> vms = service.getVMByHypIds(hypIds, userId, role);
			if(vms == null || vms.size() == 0){
				jsonData.setSuccess(false);
				jsonData.setMsg("没有虚拟机数据");
			}
			else {
				jsonData.setValue(vms);
			}
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取虚拟机数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetSwitchPortsBySwIds(){
		String swIds = getStrParameter("swIds");
		JsonData jsonData = new JsonData();
		try{
			List<DataRow> swPorts = service.getSwitchPortsBySwIds(swIds);
			if(swPorts == null || swPorts.size() == 0){
				jsonData.setSuccess(false);
				jsonData.setMsg("没有交换机端口数据");
			}
			else {
				jsonData.setValue(swPorts);
			}
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取交换机端口数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetPhyportSwportsByIds(){
		String phyportIds = getStrParameter("phyportIds");
		String isSwitchStr = getStrParameter("isSwitch");
		JsonData jsonData = new JsonData();
		boolean isSwitch = isSwitchStr == null || "true".equalsIgnoreCase(isSwitchStr)
			|| (!"0".equals(isSwitchStr));
		try{
			Map<String, Object> pools = service.getPhyportSwportsByIds(phyportIds, isSwitch);
			jsonData.setValue(pools);
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取物理机端口和交换机端口数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doAppNameExists(){
		String appName = MyUtilities.htmlToText(getStrParameter("appName"));
		JsonData jsonData = new JsonData();
		try{
			jsonData.setValue(service.isAppNameExists(appName));
		}catch(Exception e){
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			jsonData.setSuccess(false);
			jsonData.setMsg("检测业务系统名称是否重复失败！");
		}
		printWithDate(jsonData);
	}
	
	public void doGetPoolsBystoIds(){
		String tpcStoIds = getStrParameter("db2StoIds");
		String srDBStoIds = getStrParameter("srDBStoIds");
		JsonData jsonData = new JsonData();
		try{
			Map<String, Object> pools = service.getPoolsByStoIds(tpcStoIds, srDBStoIds);
			jsonData.setValue(pools);
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取存储池数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetVolumesBystoIdsPoolIds(){
		String tpcStoIds = getStrParameter("db2StoIds");
		String srDBStoIds = getStrParameter("srDBStoIds");
		String tpcPoolIds = getStrParameter("db2PoolIds");
		String srDBPoolIds = getStrParameter("srDBPoolIds");
		JsonData jsonData = new JsonData();
		try{
			Map<String, Object> pools = service.getVolumesByPoolIds(tpcStoIds, srDBStoIds,
					tpcPoolIds, srDBPoolIds);
			jsonData.setValue(pools);
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取存储卷数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetAllPhySwStoSw(){
		// phySwIds,stoSwIds
		String phySwIds = getStrParameter("phySwIds");
		String stoSwIds = getStrParameter("stoSwIds");
		JsonData jsonData = new JsonData();
		try{
			Map<String, Object> phyports = service.getPhySwStoSw(phySwIds, stoSwIds);
			if(phyports != null && phyports.size() > 0){
				jsonData.setValue(phyports);
			}
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取交换机数据失败");
		}
		printWithDate(jsonData);
	}
	
	public void doGetPhyportsByPhyids(){
		String phyids = getStrParameter("phyids");
		JsonData jsonData = new JsonData();
		try{
			Map<String, Object> phyports = service.getPhyportsByPhyids(phyids);
			if(phyports != null && phyports.size() > 0){
				jsonData.setValue(phyports);
			}
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("获取物理机端口数据失败");
		}
		printWithDate(jsonData);
	}
//	void info(Object obj, String mark){
//		Logger.getLogger(getClass()).info("***********************************************************************");
//		Logger.getLogger(getClass()).info(mark);
//		Logger.getLogger(getClass()).info(JSON.toJSONString(obj));
//		Logger.getLogger(getClass()).info("***********************************************************************");
//	}
	
	@SuppressWarnings("unchecked")
	public void doSaveDeviceMap(){
		List<List<DataRow>> params = new ArrayList<List<DataRow>>(50);
		long userId;
		String role = getUserTypeFromSession();
		Long appId = this.getLongParameter("appId", -1L);
		boolean isEdit = appId > 0L;
		if(SrContant.ROLE_SUPER.equalsIgnoreCase(role)){
			if(isEdit){  // 处于编辑状态
				userId = service.getUserIdByAppId(appId);
			}
			else { // 新增
				userId = getUserIdFromSession();
			}
		}
		else {
			userId = getUserIdFromSession();
		}
		// 应用的数据
		DataRow appData = new DataRow();
		appData.put("name", MyUtilities.htmlToText(this.getStrParameter("appName")));
		appData.put("description", MyUtilities.htmlToText(this.getStrParameter("appDesc")));
		appData.put("user_id", userId);
		
		if(isEdit){
			service.updateAppData(appData, appId);
			service.deleteDevMapById(appId); 
		}
		else {
			// 
			appId = service.saveAppData(appData);
		}
		////////////////////////////////
		/*
			交换机(物理机连接的交换机)------交换机(存储系统连接的交换机)
			交换机端口(物理机连接的交换机端口)--交换机端口(存储系统连接的交换机端口)
			physwStoswSize,phySwId,stoSwId
		 */
		int physwStoswSize = this.getIntParameter("physwStoswSize");
		MultiTree tree;
		TreeUtils treeUtils = new TreeUtils();
		List<DataRow> swMapSw = service.getSwitchMap(null);
		long [][] data = treeUtils.convert(swMapSw);
		List<DataRow> sw2SwData;
		List<DataRow> ssw2EswData = new ArrayList<DataRow>(physwStoswSize);
		long start, end; // phySwIdStart stoSwIdEnd
		StringBuilder errMsg = new StringBuilder(100);
		for(int i = 0; i < physwStoswSize; ++i){
			start = this.getLongParameter("phySwIdStart" + i, -1L);
			end = this.getLongParameter("stoSwIdEnd" + i, -1L);
			if(start > 0L && end > 0L){
				DataRow dr = new DataRow();
				dr.put("app_id", appId);
				dr.put("parent_device_type", SrContant.SUBDEVTYPE_STARTSWITCH);
				dr.put("device_type", SrContant.SUBDEVTYPE_ENDSWITCH);
				dr.put("parent_device_id", start);
				dr.put("device_id", end);
				dr.put("has_components", true);
				dr.put("db_type", SrContant.DBTYPE_TPC);
				ssw2EswData.add(dr);
				if(start == end){
					continue;
//					Long swPortId = this.getLongParameter("phySwPortId" + j, -1L),
//					portSwId = this.getLongParameter("portPhySwId" + j, -1L);
//					tree = treeUtils.getMultiTree(appId, start, end, swPortId, portSwId);
//					++j;
				}
				else {
					tree = treeUtils.getMultiTree(data, appId, start, end);
				}
				
				if(tree.getSwitchLinkSwitchs() != null && tree.getSwitchLinkSwitchs().size() > 0){
					sw2SwData = tree.getSwitchLinkSwitchs().get(0);
					if(sw2SwData != null && sw2SwData.size() > 0){
						params.add(sw2SwData);
						sw2SwData = tree.getPortLinkPorts().get(0);
						if(sw2SwData != null && sw2SwData.size() > 0){ params.add(sw2SwData); }
						sw2SwData = tree.getSwitchLinkPorts().get(0);
						if(sw2SwData != null && sw2SwData.size() > 0){ params.add(sw2SwData); }
						sw2SwData = tree.getPortLinkSwitchs().get(0);
						if(sw2SwData != null && sw2SwData.size() > 0){ params.add(sw2SwData); }
					}
					else{
						errMsg.append(String.format("起点交换机(%s)不能连上终点交换机(%s)<br>", start, end));
					}
				}
				else{
					errMsg.append(String.format("起点交换机(%s)不能连上终点交换机(%s)<br>", start, end));
				}
			}
		}
		JsonData jsonData = new JsonData();
		if(errMsg.length() > 0) {
			jsonData.setSuccess(false);
			jsonData.setMsg(errMsg.toString());
			service.deleteAppById(appId);
			printWithDate(jsonData);
			return;
		}
		params.add(ssw2EswData); // 保存起点交换机和终点交换机到数据库，方便编辑拓扑
		////////////////////////////////
		//  应用--------虚拟机
		int phyVMSize = this.getIntParameter("phyVMSize");
		List<DataRow> appVMData = new ArrayList<DataRow>(phyVMSize);
		for(int i = 0; i < phyVMSize; ++i){
			DataRow dr = new DataRow();
			dr.put("app_id", appId);
			dr.put("parent_device_type", SrContant.SUBDEVTYPE_APP);
			dr.put("device_type", SrContant.SUBDEVTYPE_VIRTUAL);
			dr.put("parent_device_id", appId);
			dr.put("device_id", this.getLongParameter("VMPhyId" + i));
			dr.put("has_components", false);
			dr.put("db_type", SrContant.DBTYPE_SR);
			appVMData.add(dr);
		}
		params.add(appVMData);
		
		// 应用--------物理机
		int appPhySize = this.getIntParameter("appPhySize");
		List<DataRow> appPhyData = new ArrayList<DataRow>(appPhySize);
		for(int i = 0; i < appPhySize; ++i){
			DataRow dr = new DataRow(); // 别忘了app_id
			dr.put("app_id", appId);
			dr.put("parent_device_type", SrContant.SUBDEVTYPE_APP);
			dr.put("device_type", SrContant.SUBDEVTYPE_PHYSICAL);
			dr.put("parent_device_id", appId);
			dr.put("device_id", this.getLongParameter("phyAppId" + i));
			dr.put("has_components", true);
			dr.put("db_type", SrContant.DBTYPE_SR);
			appPhyData.add(dr);
		}
		params.add(appPhyData);
		
		// 虚拟机------物理机
		List<DataRow> phyVMData = new ArrayList<DataRow>(phyVMSize);
		for(int i = 0; i < phyVMSize; ++i){
			DataRow dr = new DataRow();
			dr.put("app_id", appId);
			dr.put("parent_device_type", SrContant.SUBDEVTYPE_VIRTUAL);
			dr.put("device_type", SrContant.SUBDEVTYPE_PHYSICAL);
			dr.put("parent_device_id", this.getLongParameter("VMPhyId" + i));
			dr.put("device_id", this.getLongParameter("phyVMId" + i));
			dr.put("has_components", true);
			dr.put("db_type", SrContant.DBTYPE_SR);
			phyVMData.add(dr);
		}
		params.add(phyVMData);
		
		/**
		 * 将交换机端口抽取出来查询交换机
		 */
		// 物理机端口--交换机端口               物理机------交换机            物理机------物理机端口 
		// 交换机端口--交换机
		long temp;
		int phypSwpSize = this.getIntParameter("phypSwpSize");
		if(phypSwpSize > 0){
			List<DataRow> phypSwpData = new ArrayList<DataRow>(phypSwpSize);
			int phySwSize = this.getIntParameter("phySwSize");
			List<DataRow> phySwData = new ArrayList<DataRow>(phySwSize);
			int phyPhypSize = this.getIntParameter("phyPhypSize");
			List<DataRow> phyPhypSwpData = new ArrayList<DataRow>(phyPhypSize);
			int swpSwSize = this.getIntParameter("swpSwSize");
			List<DataRow> swpSwData = new ArrayList<DataRow>(swpSwSize);
			
			for(int i = 0; i < phypSwpSize; ++i){
				DataRow dr = new DataRow();
				dr.put("app_id", appId);
				dr.put("parent_device_type", SrContant.SUBDEVTYPE_PHYSICALPORT);
				dr.put("device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
				dr.put("parent_device_id", this.getLongParameter("phypSwpId" + i));
				dr.put("device_id", this.getLongParameter("swpPhypId" + i));
				dr.put("has_components", false);
				dr.put("db_type", SrContant.DBTYPE_TPC);
				phypSwpData.add(dr);
				
				temp = this.getLongParameter("phySwId" + i, -1L);
				if(temp > 0L){
					dr = new DataRow(); // 别忘了app_id
					dr.put("app_id", appId);
					dr.put("parent_device_type", SrContant.SUBDEVTYPE_PHYSICAL);
					dr.put("device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.put("parent_device_id", temp);
					dr.put("device_id", this.getLongParameter("swPhyId" + i));
					dr.put("has_components", true);
					dr.put("db_type", SrContant.DBTYPE_TPC);
					phySwData.add(dr);
				}
				
				temp = this.getLongParameter("phyPhypId" + i, -1L);
				if(temp > 0L){
					dr = new DataRow();
					dr.put("app_id", appId);
					dr.put("parent_device_type", SrContant.SUBDEVTYPE_PHYSICAL);
					dr.put("device_type", SrContant.SUBDEVTYPE_PHYSICALPORT);
					dr.put("parent_device_id", temp);
					dr.put("device_id", this.getLongParameter("phypPhyId" + i));
					dr.put("has_components", false);
					dr.put("db_type", SrContant.DBTYPE_SR);
					phyPhypSwpData.add(dr);
				}
				
				temp = this.getLongParameter("swpSwId" + i, -1L);
				if(temp > 0L){
					dr = new DataRow(); // 别忘了app_id
					dr.put("app_id", appId);
					dr.put("parent_device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
					dr.put("device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.put("parent_device_id", temp);
					dr.put("device_id", this.getLongParameter("swSwpId" + i));
					dr.put("has_components", true);
					dr.put("db_type", SrContant.DBTYPE_TPC);
					swpSwData.add(dr);
				}
			}
			params.add(phypSwpData);
			
			params.add(phySwData);
			
			params.add(phyPhypSwpData);
			
			params.add(swpSwData);
		}
		else {
			// phySwSize,phySwId,swPhyId,
			int phySwSize = this.getIntParameter("phySwSize");
			List<DataRow> phySwData = new ArrayList<DataRow>(phySwSize);
			for(int i = 0; i < phySwSize; ++i){
				temp = this.getLongParameter("phySwId" + i, -1L);
				if(temp > 0L){
					DataRow dr = new DataRow(); // 别忘了app_id
					dr.put("app_id", appId);
					dr.put("parent_device_type", SrContant.SUBDEVTYPE_PHYSICAL);
					dr.put("device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.put("parent_device_id", temp);
					dr.put("device_id", this.getLongParameter("swPhyId" + i));
					dr.put("has_components", true);
					dr.put("db_type", SrContant.DBTYPE_TPC);
					phySwData.add(dr);
				}
			}
			params.add(phySwData);
		}
		
		// 交换机----存储系统
		int swStoSize = this.getIntParameter("swStoSize");
		List<DataRow> swStoData = new ArrayList<DataRow>(swStoSize);
		for(int i = 0; i < swStoSize; ++i){
			DataRow dr = new DataRow();
			dr.put("app_id", appId);
			dr.put("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
			dr.put("device_type", SrContant.SUBDEVTYPE_STORAGE);
			dr.put("parent_device_id", this.getLongParameter("swStoId" + i));
			dr.put("device_id", this.getLongParameter("stoSwId" + i));
			dr.put("has_components", true);
			dr.put("db_type", this.getStrParameter("swStoDbtype" + i));
			swStoData.add(dr);
		}
		params.add(swStoData);
		
		/*
		交换机端口与存储端口的映射
		交换机------交换机端口
		交换机------存储端口
		存储端口----存储系统  
		swSwpSize,stopSwSize,stopStoSize
		 */
		int swpStopSize = this.getIntParameter("swpStopSize");
		List<DataRow> swpStopData = new ArrayList<DataRow>(swpStopSize);
		
		int stoSwSwpSize = this.getIntParameter("stoSwSwpSize");
		List<DataRow> stoSwSwpData = new ArrayList<DataRow>(stoSwSwpSize);
		
		int swStopSize = this.getIntParameter("swStopSize");
		List<DataRow> swStopData = new ArrayList<DataRow>(swStopSize);
		
		int stopStoSize = this.getIntParameter("stopStoSize");
		List<DataRow> stopStoData = new ArrayList<DataRow>(stopStoSize);
		
		for(int i = 0; i < swpStopSize; ++i){
			// 交换机端口-->存储端口
			DataRow dr = new DataRow();
			dr.put("app_id", appId);
			dr.put("parent_device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
			dr.put("device_type", SrContant.SUBDEVTYPE_PORT);
			dr.put("parent_device_id", this.getLongParameter("swpStopId" + i));
			dr.put("device_id", this.getLongParameter("stopSwpId" + i));
			dr.put("has_components", false);
			dr.put("db_type", this.getStrParameter("swpStopDbtype" + i));//swpStopDbtype
			swpStopData.add(dr);
			// 交换机 -- 交换机端口
			temp = this.getLongParameter("stoSwSwpId" + i, -1L);
			if(temp > 0L){
				dr = new DataRow();
				dr.put("app_id", appId);
				dr.put("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
				dr.put("device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
				dr.put("parent_device_id", temp);
				dr.put("device_id", this.getLongParameter("stoSwpSwId" + i));
				dr.put("has_components", false);
				dr.put("db_type", SrContant.DBTYPE_TPC);
				stoSwSwpData.add(dr);
			}
			// Switch -- Port
			temp = this.getLongParameter("swStopId" + i, -1L);
			if(temp > 0L){
				dr = new DataRow();
				dr.put("app_id", appId);
				dr.put("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
				dr.put("device_type", SrContant.SUBDEVTYPE_PORT);
				dr.put("parent_device_id", temp);
				dr.put("device_id", this.getLongParameter("stopSwId" + i));
				dr.put("has_components", false);
				dr.put("db_type", this.getStrParameter("swStopDbType" + i));
				swStopData.add(dr);
			}
			// Port --> Storage
			temp = this.getLongParameter("stopStoId" + i, -1L);
			if(temp > 0L){
				dr = new DataRow();
				dr.put("app_id", appId);
				dr.put("parent_device_type", SrContant.SUBDEVTYPE_PORT);
				dr.put("device_type", SrContant.SUBDEVTYPE_STORAGE);
				dr.put("parent_device_id", temp);
				dr.put("device_id", this.getLongParameter("stoStopId" + i));
				dr.put("has_components", true);
				dr.put("db_type", this.getStrParameter("stopStoDbType" + i));
				stopStoData.add(dr);
			}
		}
		params.add(swpStopData);
		
		params.add(stoSwSwpData);
		
		params.add(swStopData);
		
		params.add(stopStoData);
		
		// 存储系统----存储池
		int stoPoolSize = this.getIntParameter("stoPoolSize");
		List<DataRow> stoPoolData = new ArrayList<DataRow>(stoPoolSize);
		for(int i = 0; i < stoPoolSize; ++i){
			DataRow dr = new DataRow();
			dr.put("app_id", appId);
			dr.put("parent_device_type", SrContant.SUBDEVTYPE_STORAGE);
			dr.put("device_type", SrContant.SUBDEVTYPE_POOL);
			dr.put("parent_device_id", this.getLongParameter("stoPoolId" + i));
			dr.put("device_id", this.getLongParameter("poolStoId" + i));
			dr.put("has_components", false);
			dr.put("db_type", this.getStrParameter("stoPoolDbtype" + i));
			stoPoolData.add(dr);
		}
		params.add(stoPoolData);
		
		// 存储池------存储卷
		int poolVolSize = this.getIntParameter("poolVolSize");
		List<DataRow> poolVolData = new ArrayList<DataRow>(poolVolSize);
		for(int i = 0; i < poolVolSize; ++i){
			DataRow dr = new DataRow();
			dr.put("app_id", appId);
			dr.put("parent_device_type", SrContant.SUBDEVTYPE_POOL);
			dr.put("device_type", SrContant.SUBDEVTYPE_VOLUME);
			dr.put("parent_device_id", this.getLongParameter("poolVolId" + i));
			dr.put("device_id", this.getLongParameter("volPoolId" + i));
			dr.put("has_components", false);
			dr.put("db_type", this.getStrParameter("poolVolDbtype" + i));
			poolVolData.add(dr);
		}
		params.add(poolVolData);
		
		try{
			service.saveDeviceMap(params);
			jsonData.setMsg("成功保存设备映射关系");
		}catch(Exception ex){
			logger.error("", ex);
			jsonData.setSuccess(false);
			jsonData.setMsg("保存设备映射关系失败");
			service.deleteAppById(appId);
		}
		printWithDate(jsonData);
	}
	
	@Deprecated // 由于拓扑图不显示物理机端口，而且也没有任务去获取物理机端口，所以这个方法可以不用
	@SuppressWarnings("unchecked")
	public void doSavePhyPort(){
		Long hypId = getLongParameter("hypId");
		String portName = getStrParameter("portName");
		String portNum = getStrParameter("portNum");
		String portType = getStrParameter("portType");
		JsonData jsonData = new JsonData();
		DataRow dr = new DataRow();
		dr.put("hypervisor_id", hypId);
		dr.put("port_name", MyUtilities.htmlToText(portName));
		dr.put("port_number", MyUtilities.htmlToText(portNum));
		dr.put("port_type", MyUtilities.htmlToText(portType));
		long id = -1L;
		try{
			id = service.savePhyPort(dr);
			jsonData.setValue(id);
		}
		catch(Exception ex){
			jsonData.setMsg("保存物理机端口数据失败");
			jsonData.setSuccess(false);
			logger.error("", ex);
		}
		printWithDate(jsonData);
	}
	
	/**
	 * @see 从Session里获取用户类型
	 * @return
	 */
	private String getUserTypeFromSession(){
		Object obj = getSession().getAttribute(WebConstants.SESSION_CLIENT_TYPE);
		return obj == null? SrContant.ROLE_USER : (String)obj;
	}
	/**
	 * @see 从Session里获取用户的编号
	 * @return
	 */
	private long getUserIdFromSession(){
		Object obj = getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
		if(obj == null){ return -1L; } // 表示没有userId
		return (Long)obj;
	}
	
	public void doGetAllAppData1(){
		printWithDate(service.getAppDataByIds1(null, getUserIdFromSession(), getUserTypeFromSession()));
	}
	
	/**
	 * 当客户第一次打开拓扑图时，默认是显示第一个应用，当appId给getDeviceData
	 * 当客户不是第一次打开拓扑图时，默认显示的是上一个显示的应用
	 * 当客户在浏览器选择应用时，把该应用标记为上一个应用
	 */
	public void doGetTopoData(){
		long appId = getLongParameter("appid", -1L); // 优先
		int devType = getIntParameter("devtype", -1);
		// 是否需要重绘拓扑图，这样是为了减少读取数据库
		String noRepaint = getStrParameter("noRepaint");
		boolean isRepaint = noRepaint == null || noRepaint.trim().isEmpty();
		JsonData jsonData = new JsonData();
		try{
			jsonData.setValue(service.getDeviceData(appId, devType, this.getUserIdFromSession(),
					this.getUserTypeFromSession(), isRepaint));
			jsonData.setMsg("成功获取业务系统拓扑的数据");
		}catch(Exception ex){
			jsonData.setSuccess(false);
			jsonData.setMsg(ex.getMessage());
			logger.error("", ex);
		}
		printWithDate(jsonData);
	}
	
	/**
	 * @see 备份
	 */
	public void doGetTopoData_bak(){
		Integer appId = getIntParameter("appid");
		int devType = getIntParameter("devtype", -1);
		JsonData jsonData = new JsonData();
		if(appId != null && appId > 0){
			try{
				jsonData.setValue(service.getDeviceData(appId, devType, 0L, "", true));
				jsonData.setMsg("成功获取业务系统拓扑的数据");
			}catch(Exception ex){
				jsonData.setSuccess(false);
				jsonData.setMsg("获取业务系统拓扑的数据失败");
				logger.error("", ex);
			}
		}
		else {
			jsonData.setSuccess(false);
			jsonData.setMsg("选择的应用不正确, 请检查应用的数据");
		}
		printWithDate(jsonData);
	}
	
	public void doLoadDeviceInfo(){
		long devId = getLongParameter("devId");
		int devType = getIntParameter("devType", -1);
		int second = getIntParameter("second", 24 * 60 * 60);
		String osType = getStrParameter("osType");
		String dbType = getStrParameter("dbType");
		boolean isPerfOnly = getIntParameter("isPerfOnly", 1) != 0;
		JsonData jsonData = new JsonData();
		try{
			jsonData.setMsg("成功获取设备的数据");
			jsonData.setValue(service.getDeviceInfo(devId, devType, second, osType, dbType, isPerfOnly));
		}catch(Exception ex){
			jsonData.setSuccess(false);
			jsonData.setMsg("获取设备的数据失败");
			logger.error("", ex);
		}
		print(jsonData);
	}
	
	/**
	 * @see 这是编辑应用拓扑
	 * @return
	 */
	public ActionResult doTopoEditApp(){
		if(Configure.getInstance().getDataSource(WebConstants.DB_TPC) == null){
			setAttribute("title", "警告");
			setAttribute("msg", "不支持非SAN网络环境下编辑拓扑图！");
			return new ActionResult("/WEB-INF/views/topo/MsgDialog.jsp");
		}
		long appId = this.getLongParameter("appId", -1L);
		if(appId <= 0L){
			setAttribute("title", "警告");
			setAttribute("msg", "您所选的业务应用拓扑无效，不能编辑！");
			return new ActionResult("/WEB-INF/views/topo/MsgDialog.jsp");
		}
		
		long userId = this.getUserIdFromSession();
		String role = this.getUserTypeFromSession();
		setAttribute("hypData", service.getAllHypervisor(userId, role));
		setAttribute("stoData", service.getAllStorage(userId, role));
		setAttribute("swData", service.getAllSwitch(userId, role));
		setAttribute("action", SrContant.STATE_EDIT);
		Map<String, Object> obj = service.getDeviceIds(appId);
		setAttribute("history", JSON.toJSONStringWithDateFormat(obj, SrContant.TIME_PATTERN));
		return new ActionResult("/WEB-INF/views/topo/addApplications.jsp");
	}
} 
