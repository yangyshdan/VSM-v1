package root.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.service.apps.AppsService;
import com.huiming.service.arraysite.ArraysiteService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.disk.DiskService;
import com.huiming.service.extend.ExtendService;
import com.huiming.service.hypervisor.HypervisorService;
import com.huiming.service.iogroup.IoGroupService;
import com.huiming.service.node.NodeService;
import com.huiming.service.pool.PoolService;
import com.huiming.service.port.PortService;
import com.huiming.service.rank.RankService;
import com.huiming.service.report.ReportService;
import com.huiming.service.sr.ddm.DdmService;
import com.huiming.service.sr.hostgroup.HostgroupService;
import com.huiming.service.sr.storagesystem.StorageSystemService;
import com.huiming.service.storage.StorageService;
import com.huiming.service.switchport.SwitchportService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.topn.TopnService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.service.volume.VolumeService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.huiming.web.HtmlGenerator;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ReportMaker extends SecurityAction {
	private HtmlGenerator htmlGenerator = new HtmlGenerator();
	private Map<String, Object> variables = new HashMap<String, Object>();
	public static final String TPC_STORAGE_LIMIT = "tpc_storage_limit";
	public static final String HDS_STORAGE_LIMIT = "hds_storage_limit";
	public static final String EMC_STORAGE_LIMIT = "emc_storage_limit";
	public static final String NETAPP_STORAGE_LIMIT = "netapp_storage_limit";
	public static final String SWITCH_LIMIT = "switch_limit";
	public static final String HOST_LIMIT = "host_limit";
	//内置性能指标
	private static final String SVC_TOPN = "[{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A9','name':'Total Data Rate'},{'id':'A3','name':'Total I/O Rate (overall)'},{'id':'A12','name':'Total Response Time'}]},{'topn_count':'10','id':'Storage','name':'存储系统TOP10','children':[{'id':'A44','name':'Total Data Rate'},{'id':'A38','name':'Total I/O Rate (overall)'},{'id':'A47','name':'Total Response Time'}]},{'topn_count':'10','id':'IOGroup','name':'IO组TOP10','children':[{'id':'A699','name':'Total Data Rate'},{'id':'A693','name':'Total I/O Rate (overall)'},{'id':'A702','name':'Total Response Time'}]},{'topn_count':'10','id':'Port','name':'端口TOP10','children':[{'id':'A142','name':'Total Port Data Rate'},{'id':'A139','name':'Total Port I/O Rate'}]},{'topn_count':'10','id':'Node','name':'存储节点TOP10','children':[{'id':'A545','name':'Total Data Rate'},{'id':'A539','name':'Total I/O Rate (overall)'},{'id':'A573','name':'Total Port I/O Rate'}]}]";
	private static final String BSP_TOPN = "[{'topn_count':'10','id':'Storage','name':'存储系统TOP10','children':[{'id':'A421','name':'Total Data Rate'},{'id':'A415','name':'Total I/O Rate (overall)'}]},{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A433','name':'Total Data Rate'},{'id':'A427','name':'Total I/O Rate (overall)'}]},{'topn_count':'10','id':'Port','name':'端口TOP10','children':[{'id':'A409','name':'Total Port Data Rate'},{'id':'A406','name':'Total Port I/O Rate'}]}]";
	private static final String DS_TOPN = "[{'topn_count':'10','id':'Controller','name':'控制器TOP10','children':[{'id':'A458','name':'Total Data Rate'},{'id':'A444','name':'Total I/O Rate (normal)'},{'id':'A461','name':'Total Response Time'}]},{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A183','name':'Total I/O Rate (overall)'},{'id':'A198','name':'Total Response Time'}]},{'topn_count':'10','id':'Rank','name':'RankTOP10','children':[{'id':'A799','name':'Total Backend Data Rate'}]}]";
	private static final String EMC_TOPN = "[{'topn_count':'10','id':'Storage','name':'存储系统TOP10','children':[{'id':'A112_09','name':'Total Data Rate'},{'id':'A112_03','name':'Total I/O Rate'}]},{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A117_09','name':'Total Data Rate'},{'id':'A117_03','name':'Total I/O Rate'}]},{'topn_count':'10','id':'Port','name':'端口TOP10','children':[{'id':'A115_09','name':'Total Data Rate'},{'id':'A115_03','name':'Total I/O Rate'}]}]";
	private static final String HDS_TOPN = "[{'topn_count':'10','id':'Storage','name':'存储系统TOP10','children':[{'id':'A106_09','name':'Total Data Rate'},{'id':'A106_03','name':'Total I/O Rate'}]},{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A111_09','name':'Total Data Rate'},{'id':'A111_03','name':'Total I/O Rate'}]},{'topn_count':'10','id':'Port','name':'端口TOP10','children':[{'id':'A109_09','name':'Total Data Rate'},{'id':'A109_03','name':'Total I/O Rate'}]}]";
	private static final String NETAPP_TOPN = "[{'topn_count':'10','id':'Storage','name':'存储系统TOP10','children':[{'id':'NA100_08','name':'Total I/O Rate'}]},{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'NA101_09','name':'Total Data Rate'},{'id':'NA101_03','name':'Total I/O Rate'}]},{'topn_count':'10','id':'Port','name':'端口TOP10','children':[{'id':'NA102_09','name':'Total Data Rate'},{'id':'NA102_03','name':'Total I/O Rate'}]}]";
	private final String SWITCH_TOPN = "[{'topn_count':'10','id':'Switch','name':'交换机TOP10','children':[{'id':'A529','name':'CRC Error Rate'},{'id':'A518','name':'Total Port Data Rate'}]}]";
	private final String APP_TOPN = "[{'topn_count':'10','id':'App','name':'应用程序TOP10','children':[{'id':'APP1','name':'cpu idle percentage'},{'id':'APP3','name':'memory used percentage'},{'id':'APP10','name':'READ IOPS'}]}]";
	private final String HOST_TOPN = "[{'topn_count':'10','id':'Physical','name':'物理机TOP10','children':[{'id':'H1','name':'cpu busy percentage'},{'id':'H3','name':'cpu idle percentage'},{'id':'H2','name':'memory used percentage'}]}]";

	/**
	 * 生成报表页面
	 * @param row
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public boolean doReportFtl(DataRow row,JSONObject msg) throws Exception{
		String path = this.getClass().getClassLoader().getResource("/").toString().replaceAll("%20"," ").replaceAll("/WEB-INF/classes", "").replaceAll("file:/", "");
		DataRow dataRow = doInitReport(row,null);
		//右侧菜单及配置信息及告警信息
		doGetzTree(dataRow);
		//性能信息
		doperfCon2(dataRow);
		//TopN信息
		doTopnCon(dataRow);
		//告警信息
		doAlertInfo(dataRow,row);
		
		String img = row.getString("report_logo_url")==""?null:row.getString("report_logo_url").substring(row.getString("report_logo_url").lastIndexOf("/"),row.getString("report_logo_url").length());
		variables.put("logo", img);
		variables.put("startTime", row.getString("starttime"));
		variables.put("endTime", row.getString("endtime"));
		variables.put("title", row.getString("the_display_name"));
		return htmlGenerator.buildHtml("report.ftl",variables, path+row.getString("real_name"));
	}
	
	/**
	 * 得到性能信息(以性能为节点)
	 * @param row
	 */
	public void doperfCon2(DataRow row){
		BaseprfService bs = new BaseprfService();
		JSONArray pnode = new JSONArray().fromObject(row.getString("perf_array"));
		JSONArray pary = new JSONArray();
		for (Object object : pnode) {
			//得到设备类型,如:{"id":"HOST","name":"服务器","children":[...]}
			JSONObject obj = new JSONObject().fromObject(object);
			JSONObject pobj = new JSONObject();
			//获取设备
			if(obj.has("children") && obj.getJSONArray("children").size()>0){
				JSONArray array = new JSONArray().fromObject(obj.getString("children"));
				JSONArray pary1 = new JSONArray();
				for (Object object2 : array) {
					//得到设备,如:{"id":"4","name":"TEST-PC","children":[...]}
					JSONObject obj2 = new JSONObject().fromObject(object2);
					JSONObject pobj2 = new JSONObject();
					//获取部件
					if(obj2.has("children") && obj2.getJSONArray("children").size()>0){
						JSONArray array2 = new JSONArray().fromObject(obj2.getString("children"));
						JSONArray pary2 = new JSONArray();
						for (Object object3 : array2) {
							//得到部件,如:{"id":"Physical","name":"物理机","children":[...]}
							JSONObject obj3 = new JSONObject().fromObject(object3);
							JSONObject pobj3 = new JSONObject();
							//获取性能指标
							if(obj3.has("children") && obj3.getJSONArray("children").size()>0){
								//得到性能指标,如:{"id":"H11","name":"net recv packet (Package/second)","children":[...]}
								JSONArray array3 = new JSONArray().fromObject(obj3.getString("children"));
								JSONArray pary3 = new JSONArray();
								for (Object object4 : array3) {
									JSONObject obj4 = new JSONObject().fromObject(object4);
									JSONObject pobj4 = new JSONObject();
									//获取选择的设备
									if(obj4.has("children") && obj4.getJSONArray("children").size()>0){
										//性能指标对应选择的设备,如:{"id":"4","name":"TEST-PC"}
										JSONArray array5 = new JSONArray().fromObject(obj4.getString("children"));
										JSONArray pary4 = new JSONArray();
										List<DataRow> devs = new ArrayList<DataRow>();
										for (Object object5 : array5) {
											JSONObject obj5 = new JSONObject().fromObject(object5);
											DataRow appDev = new DataRow();
											appDev.set("ele_id", obj5.getString("id"));
											appDev.set("ele_name", obj5.getString("name"));
											devs.add(appDev);
										}
										//获取KPI信息
										List<DataRow> kpis = bs.getKPIInfo("'" + obj4.getString("id") + "'");
										//获取绘图性能信息
										JSONArray perfData = bs.getPrfDatas(1, devs, kpis, row.getString("starttime"), row.getString("endtime"));
										//封装性能数据
										JSONObject json = new JSONObject();  
										if (perfData != null && perfData.size() > 0) {
											json.put("series", perfData);
										}
										json.put("legend", true);
										json.put("ytitle", "");
										//获取告警值
										String threshold = obj4.getString("threshold");
										//获取告警提示信息
										String alertInfo = "";
										if (threshold != null && threshold.length() > 0) {
											json.put("threvalue",obj4.getInt("threshold"));
											alertInfo = bs.computePerfAlertCount(devs, kpis, row.getString("starttime"), row.getString("endtime"), obj4.getInt("threshold"));
											pobj4.put("alertInfo", alertInfo);
										} else {
											json.put("threvalue","");
											pobj4.put("alertInfo", alertInfo);
										}
										json.put("kpiInfo", kpis);
										pobj4.put("id", obj4.getString("id"));
										pobj4.put("name", obj4.getString("name"));
										pobj4.put("configList", json);
										pary3.add(pobj4);
									}
								}
								pobj3.put("id", obj3.getString("id"));
								pobj3.put("name", obj3.getString("name"));
								pobj3.put("configList", pary3);
								pary2.add(pobj3);
								
							}
						}
						pobj2.put("id", obj2.getString("id"));
						pobj2.put("name", obj2.getString("name"));
						pobj2.put("configList", pary2);
						pary1.add(pobj2);
					}
				}
				pobj.put("id", obj.getString("id"));
				pobj.put("name", obj.getString("name"));
				pobj.put("configList", pary1);
				pary.add(pobj);
			}
		}
		variables.put("perfData", pary);
	}
	
	/**
	 * TopN性能信息
	 * @param row
	 */
	public void doTopnCon(DataRow row){
		BaseprfService bs = new BaseprfService();
		TopnService ts = new TopnService();
		JSONArray array = new JSONArray().fromObject(row.getString("topn_array"));
		JSONArray tary = new JSONArray();  //存
		for (Object object : array) {  //得到存储类型such as {id:DS,name:DS8K,children:[...]}
			JSONObject obj1 = new JSONObject().fromObject(object);
			JSONObject tobj1 = new JSONObject();
			JSONArray array2 = new JSONArray().fromObject(obj1.getString("children"));
			JSONArray tary2 = new JSONArray();
			for (Object object2 : array2) {//得到设备such as {id:1232,name:DS8000,children[...]}
				JSONObject obj2= new JSONObject().fromObject(object2);
				JSONObject tobj2 = new JSONObject();
				JSONArray array3 = new JSONArray().fromObject(obj2.getString("children"));
				JSONArray tary3 = new JSONArray();
				for (Object object3 : array3) { //得到组件such as {id:arrayset,name:阵列top12,topn_count:12,children[...]}
					JSONObject obj3= new JSONObject().fromObject(object3);
					JSONObject tobj3 = new JSONObject();
					JSONArray perfIdArray = new JSONArray().fromObject(obj3.getString("children"));
					String perfIds = "";
					for (int i=0;i<perfIdArray.size();i++) {
						JSONObject perfObj = new JSONObject().fromObject(perfIdArray.get(i));
						perfIds+=perfObj.getString("id");
						if(i<perfIdArray.size()-1){
							perfIds+=",";
						}
					}
					DataRow topnCon = new DataRow();
					topnCon.set("fprfid", perfIds);
					topnCon.set("fdevicetype", obj1.getString("id"));
					topnCon.set("fdevice", obj2.getString("id"));
					topnCon.set("fname", obj3.getString("id"));
					topnCon.set("fprfview",bs.getView(obj1.getString("id"), obj3.getString("id")).get(0).getString("fprfview"));
					topnCon.set("top_count", obj3.getInt("topn_count"));
					topnCon.set("timescope_type", 0);
					topnCon.set("starttime", row.getString("starttime"));
					topnCon.set("endtime", row.getString("endtime"));
					JSONArray perfArray  = null;
					if(topnCon.getString("fname").equalsIgnoreCase("storage")
							||topnCon.getString("fname").equalsIgnoreCase("switch")
							||topnCon.getString("fname").equalsIgnoreCase("app")){
						perfArray = new JSONArray().fromObject(ts.getPrfJSON(topnCon));
					}else{
						perfArray = new JSONArray().fromObject(ts.getPrfJSON3(topnCon));
					}
					tobj3.put("id", obj3.getString("id"));
					tobj3.put("name", obj3.getString("name"));
					tobj3.put("configList", perfArray);
					tary3.add(tobj3);
				}
				tobj2.put("id", obj2.getString("id"));
				tobj2.put("name", obj2.getString("name"));
				tobj2.put("configList", tary3);
				tary2.add(tobj2);
			}
			tobj1.put("id", obj1.getString("id"));
			tobj1.put("name", obj1.getString("name"));
			tobj1.put("configList", tary2);
			tary.add(tobj1);
		}
		variables.put("topnData", tary);
	}
	
	/**
	 * 得到性能阀值告警信息和硬件告警信息
	 * @param row
	 */
	public void doAlertCon(DataRow row){
//		AlertService as = new AlertService();
		DeviceAlertService da = new DeviceAlertService();
		JSONArray array = new JSONArray().fromObject(row.getString("alert_array"));
		JSONArray ary = new JSONArray();
		for (Object object : array) {
			JSONObject cobj = new JSONObject();
			JSONObject obj = new JSONObject().fromObject(object);  //such as {id:}
			if(obj.has("children") && obj.getJSONArray("children").size()>0){
				JSONArray array2 = new JSONArray().fromObject(obj.getString("children"));
				JSONArray carray2 = new JSONArray().fromObject(da.getLogList(array2));
				cobj.put("id", obj.getString("id"));
				cobj.put("configList", carray2);
				ary.add(cobj);
//				if(obj.getString("id").equals("alertLevel1")){     //阀值告警
//					JSONArray array2 = new JSONArray().fromObject(obj.getString("children"));
//					JSONArray carray2 = new JSONArray().fromObject(as.getConfigAlert(array2));
//					cobj.put("id", obj.getString("id"));
//					cobj.put("configList", carray2);
//					ary.add(cobj);
//				}else if(obj.getString("id").equals("alertLevel2")){    //硬件告警
//					JSONArray array2 = new JSONArray().fromObject(obj.getString("children"));
//					JSONArray carray2 = new JSONArray().fromObject(as.getPerfAlert(array2));
//					cobj.put("id", obj.getString("id"));
//					cobj.put("configList", carray2);
//					ary.add(cobj);
//				}
			}
		}
		variables.put("alertData", ary);
	}
	
	/**
	 * 得到设备告警信息(包括静态告警和动态告警)
	 * @param row1 (告警级别基本信息)
	 * @param row2 (告警信息查询参数)
	 */
	public void doAlertInfo(DataRow baseRow, DataRow paramRow) {
		DeviceAlertService service = new DeviceAlertService();
		String startTime = paramRow.getString("starttime");
		String endTime = paramRow.getString("endtime");
		// ***********
		// 静态告警信息
		// ***********
		JSONArray alertArray = new JSONArray().fromObject(baseRow.getString("alert_array"));
		JSONArray resultArray = new JSONArray();
		for (Object alertObject : alertArray) {
			JSONObject alertRoot = new JSONObject().fromObject(alertObject); //{"id":"alertLevel1","name":"告警信息级别","children":[{"id":"Info"......
			JSONObject result = new JSONObject();
			if (alertRoot.has("children") && alertRoot.getJSONArray("children").size() > 0) {
				//获取设置的告警级别
				JSONArray levelNodes = new JSONArray().fromObject(alertRoot.getString("children"));
				
				//设置查询告警信息的设备、时间参数
				List<String> deviceList = new ArrayList<String>();
				JSONArray deviceArray = new JSONArray().fromObject(paramRow.getString("device_array"));
				for (Object deviceObject : deviceArray) {
					JSONObject deviceRoot = new JSONObject().fromObject(deviceObject);
					if (deviceRoot.has("children") && deviceRoot.getJSONArray("children").size() > 0) {
						JSONArray deviceNodes = new JSONArray().fromObject(deviceRoot.getString("children"));
						for (Object node : deviceNodes) {
							JSONObject device = new JSONObject().fromObject(node);
							deviceList.add("'" + device.getString("id") + "'");
						}
					}
				}
				String deviceParams = deviceList.toString().replace("[", "").replace("]", "");
				
				//设置告警级别参数
				List<String> levelList = new ArrayList<String>();
				for (Object object3 : levelNodes) {
					JSONObject obj = new JSONObject().fromObject(object3);
					String type = obj.getString("id");
					if(type.equalsIgnoreCase("info")){
						levelList.add("'0'");
					}else if(type.equalsIgnoreCase("warning")){
						levelList.add("'1'");
					}else {
						levelList.add("'2'");
					}
				}
				String levelParams = levelList.toString().replace("[", "").replace("]", "");
				
				//获取设备告警信息
				List<DataRow> alertList = service.getDeviceLogList(deviceParams, startTime, endTime, levelParams);
				alertList = (alertList == null ? new ArrayList<DataRow>() : alertList); 
				JSONArray searchArray = new JSONArray().fromObject(alertList);
				result.put("id", alertRoot.getString("id"));
				result.put("data", alertList);
				result.put("configList", searchArray);
				resultArray.add(result);
			}
		}
		variables.put("alertData", resultArray);
	}
	
	/**
	 * 得到设备配置信息
	 * @param row
	 * @return
	 */
	public JSONArray doGetConfig(DataRow row){
		JSONArray configData = new JSONArray();
		JSONArray devZnode = new JSONArray();
		JSONArray cZnode = new JSONArray().fromObject(row.getObject("device_array"));
		for (Object object : cZnode) {
			JSONObject json = new JSONObject().fromObject(object);
			JSONObject devjson1 = new JSONObject();
			String stype = json.getString("id");
			JSONObject cjson = new JSONObject();
			cjson.put("id", json.getString("id"));
			JSONArray carray = new JSONArray();
			devjson1.put("divId", "D_"+stype);
			devjson1.put("name", json.getString("name"));
			if(json.has("children") && json.getString("children")!=null && json.getString("children").length()>0){
				JSONArray array = new JSONArray().fromObject(json.getString("children"));
				JSONArray devarr = new JSONArray();
				for (Object object2 : array) {
					JSONObject json2 = new JSONObject().fromObject(object2);
					Integer subsystemId = json2.getInt("id");
					JSONObject cjson2 = new JSONObject();
					cjson2.put("id", json2.getInt("id"));
					cjson2.put("name", json2.getString("name"));
					JSONArray carray2 = new JSONArray();
					if(json2.has("children") && json2.getString("children")!=null && json2.getString("children").length()>0){
						JSONObject devjson2 = new JSONObject();
						devjson2.put("divId", "D_"+stype+"_Storage"+subsystemId);
						devjson2.put("name",json2.getString("name"));
						JSONArray array2 = new JSONArray().fromObject(json2.getString("children"));
						JSONArray devarr2 = new JSONArray();
						for (Object object3 : array2) {
							JSONObject json3 = new JSONObject().fromObject(object3);
							String devId = json3.getString("id");
							JSONObject cjson3 = new JSONObject();
							cjson3.put("id", json3.getString("id"));
							List<DataRow> rows = new ArrayList<DataRow>();
							
							//For BSP/SVC/DS
							if (stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC) 
								|| stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_BSP)
								|| stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_DS)) {
								//判断是否有TPC配置
								if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
									//Storage
									if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
										StorageService ser = new StorageService();
										rows = ser.subStorageList(json.getString("id").toUpperCase());
									//Node
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
										NodeService ns = new NodeService();
										rows = ns.getNodeList(null, null, null, subsystemId);
									//Port
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
										PortService ps = new PortService();
										rows = ps.getPortList(null, null, null, null, null, subsystemId);
									//Volume
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
										VolumeService vs = new VolumeService();
										rows = vs.getVolumeInfo(null, null, null, null, subsystemId);
									//Pool
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_POOL)) {
										PoolService ps = new PoolService();
										rows = ps.getPoolsInfo(null, null, null, subsystemId);
									//Disk
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_DISK)) {
										DiskService ds = new DiskService();
										rows = ds.getDiskList(null, null, null, subsystemId);
									//ArraySet
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_ARRAYSET)) {
										ArraysiteService as = new ArraysiteService();
										rows = as.getArraysiteList(null, null, subsystemId);
									//Extent
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_EXTENT)) {
										ExtendService es = new ExtendService();
										rows = es.getExtendList(null, null, null, null, null, null, subsystemId);
									//Rank
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_EXTENT)) {
										RankService rs = new RankService();
										rows = rs.getRankList(null, null, subsystemId);
									//IoGroup
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_IOGROUP)) {
										IoGroupService is = new IoGroupService();
										rows = is.getIogroupList(null, subsystemId);
									}
								}
							//For EMC/HDS/NETAPP
							} else if (stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC) 
								|| stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)
								|| stype.equalsIgnoreCase(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
								//Storage
								if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
									StorageSystemService ser = new StorageSystemService();
									rows = ser.getStorageInfoById(subsystemId);
								//Node
								} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
									com.huiming.service.sr.node.NodeService ns = new com.huiming.service.sr.node.NodeService();
									rows = ns.getNodeBysubID(subsystemId);
								//Port
								} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
									com.huiming.service.sr.port.PortService ps = new com.huiming.service.sr.port.PortService();
									rows = ps.getPortInfos(Long.parseLong(String.valueOf(subsystemId)));
								//Volume
								} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
									com.huiming.service.sr.volume.VolumeService vs = new com.huiming.service.sr.volume.VolumeService();
									rows = vs.getVolumeInfo(Long.parseLong(subsystemId.toString()));
								//Pool
								} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_POOL)) {
									com.huiming.service.sr.pool.PoolService ps = new com.huiming.service.sr.pool.PoolService();
									rows = ps.reportPool(subsystemId);
								//DiskGroup
								} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_DISKGROUP)) {
									DdmService ds = new DdmService();
									rows = ds.getDiskList(subsystemId, null);
								//HostGroup
								} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_HOSTGROUP)) {
									HostgroupService hs = new HostgroupService();
									rows = hs.getHostgroupList(subsystemId);
								}
							//For SWITCH
							} else if (stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SWITCH)) {
								//判断是否有TPC配置
								if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
									//Switch
									if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCH)) {
										SwitchService ss = new SwitchService();
										rows = new ArrayList<DataRow>();
										DataRow data = ss.getSwitchInfo(subsystemId);
										DataRow switchStatus = ss.getSwitchStatus(data.getInt("switch_id"));
										data.set("engine_status", switchStatus.getString("engine_status"));
										data.set("power_status", switchStatus.getString("power_status"));
										data.set("port_status", switchStatus.getString("port_status"));
										data.set("fiber_status", switchStatus.getString("fiber_status"));
										rows.add(data);
									//SwitchPort
									} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCHPORT)) {
										SwitchportService ss = new SwitchportService();
										rows = ss.getExtentportList(null, null, null, null, null, subsystemId);
									}
								}
							//For HOST
							} else if (stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HOST)) {
								//PHYSICAL
								if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
									HypervisorService hs = new HypervisorService();
									rows = hs.getPhysicalList(subsystemId);
								//VIRTUAL AND HYPERVISOR
								} else if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)
										|| devId.equalsIgnoreCase(WebConstants.DEVTYPE_HYPERVISOR)) {
									VirtualmachineService vs = new VirtualmachineService();
									rows = vs.getVirtualList(subsystemId);
								}
							//For APPLICATION
							} else if (stype.equalsIgnoreCase(SrContant.DEVTYPE_VAL_APPLICATION)) {
								//App
								if (devId.equalsIgnoreCase(SrContant.SUBDEVTYPE_APP)) {
									AppsService app = new AppsService();
									rows = app.getMappingVirtual(subsystemId);
								}
							}
							
							if (rows != null && rows.size() > 0) {
								JSONObject devjson3 = new JSONObject();
								devjson3.put("divId", "D_"+stype+"_Storage"+subsystemId+"_"+devId);
								devjson3.put("name", json3.getString("name"));
								devarr2.add(devjson3);
							}
							cjson3.put("configList",rows);
							carray2.add(cjson3);
						}
						cjson2.put("configList", carray2);
						carray.add(cjson2);
						
						devjson2.put("children", devarr2);
						devarr.add(devjson2);
					}
				}
				devjson1.put("children", devarr);
				devZnode.add(devjson1);
			}
			cjson.put("configList", carray);
			configData.add(cjson);
		}
		variables.put("configData",configData);
		return devZnode;
	}
	
	
	/**
	 * 产生右侧菜单
	 * @param row
	 */
	public void doGetzTree(DataRow row){
		//得到设备配置信息
		JSONArray cZnode = doGetConfig(row);
		JSONArray pZnode = new JSONArray().fromObject(row.getObject("perf_array"));
		JSONArray tZnode = new JSONArray().fromObject(row.getObject("topn_array"));
		JSONArray aZnode = new JSONArray().fromObject(row.getObject("alert_array"));
		String devTree = "{id:'devTree',divId:'deviceDiv',name:'设备信息',open:true,children:"+cZnode+"},";
		String perfTree = "{id:'perfTree',divId:'perfDiv',name:'性能信息',open:true,children:"+editTreeNode("P", pZnode)+"},";
		String topnTree = "{id:'topnTree',divId:'topnDiv',name:'TopN信息',open:true,children:"+editTreeNode("T", tZnode)+"},";
		String alertTree = "{id:'alertTree',divId:'alertDiv',name:'告警信息',open:true,children:"+editTreeNode("A", aZnode)+"}";
		String BaseTree = "[{id:'all',name:'全部',open:true,children:["+devTree+perfTree+topnTree+alertTree+"]}]";
		variables.put("zNodes", BaseTree);
	}
	
	/**
	 * 处理树菜单
	 * @param alias 别名
	 * @param zNode 节点集合
	 * @return
	 */
	public String editTreeNode(String alias,JSONArray zNode){
		for(int k=0;k<zNode.size();k++){   //such as {id:DS,name:DS8K,children:[...]}
			JSONObject obj = new JSONObject().fromObject(zNode.get(k));
			obj.put("divId", alias+"_"+obj.getString("id"));
			if(obj.has("children")){
				//such as {id:1231,name:DS8000,children:[...]}
				JSONArray array = new JSONArray().fromObject(obj.getString("children"));
				for (int j=0;j<array.size();j++) {
					JSONObject obj2 = new JSONObject().fromObject(array.get(j));
					obj2.put("divId", alias+"_"+obj.getString("id")+"_Storage"+obj2.getString("id"));
					if(obj2.has("children") && obj2.getJSONArray("children").size()>0){
						//such as {id:arraysite,name阵列,threshold:1000,children:[...]}
						JSONArray ay = new JSONArray().fromObject(obj2.getString("children"));
						if(ay!=null && ay.size()>0){
							for (int i=0;i<ay.size();i++) {
								JSONObject obj3 = new JSONObject().fromObject(ay.get(i));
								obj3.put("divId", alias+"_"+obj.getString("id")+"_Storage"+obj2.getString("id")+"_"+obj3.getString("id"));
								if(obj3.has("children") && obj3.getJSONArray("children").size()>0){
									//such as {id:1233,name:iew-asdf,children:[...]}
									JSONArray a = new JSONArray().fromObject(obj3.getString("children"));
									for(int h=0;h<a.size();h++){
										JSONObject obj4 = new JSONObject().fromObject(a.get(h));
										obj4.put("divId", alias+"_"+obj.getString("id")+"_Storage"+obj2.getString("id")+"_"+obj3.getString("id")+"_"+obj4.getString("id"));
										a.set(h, obj4);
										if(obj4.has("children")){
											obj4.remove("children");
										}
									}
									obj3.put("children", a);
								}
								ay.set(i, obj3);
							}
							obj2.put("children", ay);
						}
						array.set(j, obj2);
					}
				}
				obj.put("children", array);
			}
			zNode.set(k, obj);
		}
		return zNode.toString();
	}
	
	/**
	 * 初始化报表
	 * @param row
	 * @return
	 */
	public DataRow doInitReport(DataRow row,DataRow paramRow){
		ReportService rs = new ReportService();
		if (row != null && row.size() > 0) {
			String reportType = row.getString("report_type");
			//任务报表
			if (reportType.equals("1")) { 
				String deviceArray = row.getString("device_array");
				String perfArray = row.getString("perf_array");
				String topnArray = row.getString("topn_array");
				String alertArray = row.getString("alert_array");
				//获取用户可见设备
				String tpcStoLimitIds = null;
				String hdsStoLimitIds = null;
				String emcStoLimitIds = null;
				String netAppStoLimitIds = null;
				String switchLimitIds = null;
				String hostLimitIds = null;
				if (paramRow != null) {
					tpcStoLimitIds = paramRow.getString(TPC_STORAGE_LIMIT);
					hdsStoLimitIds = paramRow.getString(HDS_STORAGE_LIMIT);
					emcStoLimitIds = paramRow.getString(EMC_STORAGE_LIMIT);
					netAppStoLimitIds = paramRow.getString(NETAPP_STORAGE_LIMIT);
					switchLimitIds = paramRow.getString(SWITCH_LIMIT);
					hostLimitIds = paramRow.getString(HOST_LIMIT);
				}
				//SQL语句
				String tpcStoSql = "select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where 1 = 1";
				String srStoSql = "select subsystem_id as ele_id,name as ele_name from t_res_storagesubsystem where 1 = 1";
				String switchSql = "select switch_id as ele_id,the_display_name as ele_name from v_res_switch where the_operational_status = 'OK'";
				String hostSql = "select h.hypervisor_id as ele_id,coalesce(c.display_name,c.name) as ele_name from t_res_computersystem c,t_res_hypervisor h where c.computer_id = h.host_computer_id";
				
				//拼接SQL语句
				StringBuffer bspSql = null;
				StringBuffer dsSql = null;
				StringBuffer svcSql = null;
				StringBuffer emcSql = null;
				StringBuffer hdsSql = null;
				StringBuffer netAppSql = null;
				StringBuffer swtSql = null;
				StringBuffer physSql = null;
				if (deviceArray.equals("-1") || perfArray.equals("-1") || topnArray.equals("-1")) {
					//For BSP
					bspSql = new StringBuffer(tpcStoSql); 
					bspSql.append(" and os_type in (" + WebConstants.STORAGE_OS_TYPE.getString(SrContant.DEVTYPE_VAL_BSP) + ")");
					//For DS
					dsSql = new StringBuffer(tpcStoSql); 
					dsSql.append(" and os_type in (" + WebConstants.STORAGE_OS_TYPE.getString(SrContant.DEVTYPE_VAL_DS) + ")");
					//For SVC
					svcSql = new StringBuffer(tpcStoSql); 
					svcSql.append(" and os_type in (" + WebConstants.STORAGE_OS_TYPE.getString(SrContant.DEVTYPE_VAL_SVC) + ")");
					if (StringHelper.isNotEmpty(tpcStoLimitIds) && StringHelper.isNotBlank(tpcStoLimitIds)) {
						bspSql.append(" and subsystem_id in (" + tpcStoLimitIds + ")");
						dsSql.append(" and subsystem_id in (" + tpcStoLimitIds + ")");
						svcSql.append(" and subsystem_id in (" + tpcStoLimitIds + ")");
					}
					//For EMC
					emcSql = new StringBuffer(srStoSql); 
					emcSql.append(" and storage_type = '" + SrContant.DEVTYPE_VAL_EMC + "'");
					if (StringHelper.isNotEmpty(emcStoLimitIds) && StringHelper.isNotBlank(emcStoLimitIds)) {
						emcSql.append(" and subsystem_id in (" + emcStoLimitIds + ")");
					}
					
					//For HDS
					hdsSql = new StringBuffer(srStoSql); 
					hdsSql.append(" and storage_type = '" + SrContant.DEVTYPE_VAL_HDS + "'");
					if (StringHelper.isNotEmpty(hdsStoLimitIds) && StringHelper.isNotBlank(hdsStoLimitIds)) {
						hdsSql.append(" and subsystem_id in (" + hdsStoLimitIds + ")");
					}
					
					//For NETAPP
					netAppSql = new StringBuffer(srStoSql); 
					netAppSql.append(" and storage_type = '" + WebConstants.STORAGE_TYPE_VAL_NETAPP + "'");
					if (StringHelper.isNotEmpty(netAppStoLimitIds) && StringHelper.isNotBlank(netAppStoLimitIds)) {
						netAppSql.append(" and subsystem_id in (" + netAppStoLimitIds + ")");
					}
					
					//For SWITCH
					swtSql = new StringBuffer(switchSql); 
					if (StringHelper.isNotEmpty(switchLimitIds) && StringHelper.isNotBlank(switchLimitIds)) {
						swtSql.append(" and switch_id in (" + switchLimitIds + ")");
					}
					//For HOST
					physSql = new StringBuffer(hostSql);
					if (StringHelper.isNotEmpty(hostLimitIds) && StringHelper.isNotBlank(hostLimitIds)) {
						physSql.append(" and h.hypervisor_id in (" + hostLimitIds + ")");
					}
				}
				//内置设备信息 BSP,DS,SVC,EMC,HDS,NETAPP,SWITCH,APPLICATION,HOST
				if (deviceArray.equals("-1")) {
					//查询数据
					List<DataRow> bspRow = null;
					List<DataRow> dsRow = null;
					List<DataRow> svcRow = null;
					List<DataRow> switchRow = null;
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						bspRow = rs.getInitConfig(bspSql.toString(), null, WebConstants.DB_TPC);
						dsRow = rs.getInitConfig(dsSql.toString(), null, WebConstants.DB_TPC);
						svcRow = rs.getInitConfig(svcSql.toString(), null, WebConstants.DB_TPC);
						switchRow = rs.getInitConfig(swtSql.toString(), null, WebConstants.DB_TPC);
					}
					List<DataRow> emcRow = rs.getInitConfig(emcSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> hdsRow = rs.getInitConfig(hdsSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> netAppRow = rs.getInitConfig(netAppSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> appRow = rs.getInitConfig("select fid as ele_id,fname as ele_name from tnapps", null, WebConstants.DB_DEFAULT);
					List<DataRow> hostRow = rs.getInitConfig(physSql.toString(), null, WebConstants.DB_DEFAULT);
					//内置设备信息
					String svcStr = dataFilter(svcRow, ReportAction.STORAGE_OPT);
					StringBuffer sb = new StringBuffer();
					String hostStr = dataFilter(hostRow, ReportAction.HOST_OPT);
					String bspStr = dataFilter(bspRow, ReportAction.STORAGE_OPT);
					String dsStr = dataFilter(dsRow, ReportAction.STORAGE_OPT);
					String emcStr = dataFilter(emcRow, ReportAction.EMC_OPT);
					String hdsStr = dataFilter(hdsRow, ReportAction.HDS_OPT);
					String netAppStr = dataFilter(netAppRow, ReportAction.NETAPP_OPT);
					String switchStr = dataFilter(switchRow, ReportAction.SWITCH_OPT);
					String appStr = dataFilter(appRow, ReportAction.APP_OPT);
					if(hostStr.length()>0){sb.append("{'id':'HOST','name':'服务器','children':["+hostStr+"]},");}
					if(svcStr.length()>0){sb.append("{'id':'SVC','name':'SVC','children':["+svcStr+"]},");}
					if(bspStr.length()>0){sb.append("{'id':'BSP','name':'BSP','children':["+bspStr+"]},");}
					if(dsStr.length()>0){sb.append("{'id':'DS','name':'DS','children':["+dsStr+"]},");}
					if(emcStr.length()>0){sb.append("{'id':'EMC','name':'EMC','children':["+emcStr+"]},");}
					if(hdsStr.length()>0){sb.append("{'id':'HDS','name':'HDS','children':["+hdsStr+"]},");}
					if(netAppStr.length()>0){sb.append("{'id':'NETAPP','name':'NETAPP','children':["+netAppStr+"]},");}
					if(switchStr.length()>0){sb.append("{'id':'SWITCH','name':'交换机','children':["+switchStr+"]},");}
					if(appStr.length()>0){sb.append("{'id':'APPLICATION','name':'应用','children':["+appStr+"]},");}
					JSONArray array = new JSONArray().fromObject("["+sb.toString().substring(0, sb.toString().length()-1).toString()+"]");
					row.set("device_array", array.toString());
				}
				
				//性能信息
				if (perfArray.equals("-1")) {
					//查询数据
					List<DataRow> bspRow = null;
					List<DataRow> dsRow = null;
					List<DataRow> svcRow = null;
					List<DataRow> switchRow = null;
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						bspRow = rs.getInitConfig(bspSql.toString(), null, WebConstants.DB_TPC);
						dsRow = rs.getInitConfig(dsSql.toString(), null, WebConstants.DB_TPC);
						svcRow = rs.getInitConfig(svcSql.toString(), null, WebConstants.DB_TPC);
						switchRow = rs.getInitConfig(swtSql.toString(), null, WebConstants.DB_TPC);
					}
					List<DataRow> emcRow = rs.getInitConfig(emcSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> hdsRow = rs.getInitConfig(hdsSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> netAppRow = rs.getInitConfig(netAppSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> appRow = rs.getInitConfig("select fid as ele_id,fname as ele_name from tnapps", null, WebConstants.DB_DEFAULT);
					List<DataRow> hostRow = rs.getInitConfig(physSql.toString(), null, WebConstants.DB_DEFAULT);
					//内置性能信息
					StringBuffer sb = new StringBuffer();
					String hostStr = initChildren(hostRow, SrContant.DEVTYPE_VAL_HOST);
					String svcStr = initChildren(svcRow, SrContant.DEVTYPE_VAL_SVC);
					String bspStr = initChildren(bspRow, SrContant.DEVTYPE_VAL_BSP);
					String dsStr = initChildren(dsRow, SrContant.DEVTYPE_VAL_DS);
					String emcStr = initChildren(emcRow, SrContant.DEVTYPE_VAL_EMC);
					String hdsStr = initChildren(hdsRow, SrContant.DEVTYPE_VAL_HDS);
					String netAppStr = initChildren(netAppRow, WebConstants.STORAGE_TYPE_VAL_NETAPP);
					String switchStr = initChildren(switchRow, SrContant.DEVTYPE_VAL_SWITCH);
					String appStr = initChildren(appRow, SrContant.DEVTYPE_VAL_APPLICATION);
					
					if(hostStr.length()>0){sb.append("{'id':'HOST','name':'服务器','children':["+hostStr+"]},");}
					if(svcStr.length()>0){sb.append("{'id':'SVC','name':'SVC','children':["+svcStr+"]},");}
					if(bspStr.length()>0){sb.append("{'id':'BSP','name':'BSP','children':["+bspStr+"]},");}
					if(dsStr.length()>0){sb.append("{'id':'DS','name':'DS','children':["+dsStr+"]},");}
					if(emcStr.length()>0){sb.append("{'id':'EMC','name':'EMC','children':["+emcStr+"]},");}
					if(hdsStr.length()>0){sb.append("{'id':'HDS','name':'HDS','children':["+hdsStr+"]},");}
					if(netAppStr.length()>0){sb.append("{'id':'NETAPP','name':'NETAPP','children':["+netAppStr+"]},");}
					if(switchStr.length()>0){sb.append("{'id':'SWITCH','name':'交换机','children':["+switchStr+"]},");}
					if(appStr.length()>0){sb.append("{'id':'APPLICATION','name':'应用','children':["+appStr+"]},");}
					JSONArray array = new JSONArray().fromObject("["+sb.toString().substring(0, sb.toString().length()-1).toString()+"]");
					row.set("perf_array", array.toString());
				}
				
				//TopN信息
				if (topnArray.equals("-1")) {
					//查询数据
					List<DataRow> bspRow = null;
					List<DataRow> dsRow = null;
					List<DataRow> svcRow = null;
					List<DataRow> switchRow = null;
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						bspRow = rs.getInitConfig(bspSql.toString(), null, WebConstants.DB_TPC);
						dsRow = rs.getInitConfig(dsSql.toString(), null, WebConstants.DB_TPC);
						svcRow = rs.getInitConfig(svcSql.toString(), null, WebConstants.DB_TPC);
						switchRow = rs.getInitConfig(swtSql.toString(), null, WebConstants.DB_TPC);
					}
					List<DataRow> emcRow = rs.getInitConfig(emcSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> hdsRow = rs.getInitConfig(hdsSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> netAppRow = rs.getInitConfig(netAppSql.toString(), null, WebConstants.DB_DEFAULT);
					List<DataRow> appRow = rs.getInitConfig("select fid as ele_id,fname as ele_name from tnapps", null, WebConstants.DB_DEFAULT);
					List<DataRow> hostRow = rs.getInitConfig(physSql.toString(), null, WebConstants.DB_DEFAULT);
					//内置TopN信息
					String hostStr = dataFilter(hostRow, HOST_TOPN);
					String svcStr = dataFilter(svcRow, SVC_TOPN);
					StringBuffer sb = new StringBuffer();
					String bspStr = dataFilter(bspRow, BSP_TOPN);
					String dsStr = dataFilter(dsRow, DS_TOPN);
					String emcStr = dataFilter(emcRow, EMC_TOPN);
					String hdsStr = dataFilter(hdsRow, HDS_TOPN);
					String netAppStr = dataFilter(netAppRow, NETAPP_TOPN);
					String switchStr = dataFilter(switchRow, SWITCH_TOPN);
					String appStr = dataFilter(appRow, APP_TOPN);
					
					if(hostStr.length()>0){sb.append("{'id':'HOST','name':'服务器','children':["+hostStr+"]},");}
					if(svcStr.length()>0){sb.append("{'id':'SVC','name':'SVC','children':["+svcStr+"]},");}
					if(bspStr.length()>0){sb.append("{'id':'BSP','name':'BSP','children':["+bspStr+"]},");}
					if(dsStr.length()>0){sb.append("{'id':'DS','name':'DS','children':["+dsStr+"]},");}
					if(emcStr.length()>0){sb.append("{'id':'EMC','name':'EMC','children':["+emcStr+"]},");}
					if(hdsStr.length()>0){sb.append("{'id':'HDS','name':'HDS','children':["+hdsStr+"]},");}
					if(netAppStr.length()>0){sb.append("{'id':'NETAPP','name':'NETAPP','children':["+netAppStr+"]},");}
					if(switchStr.length()>0){sb.append("{'id':'SWITCH','name':'交换机','children':["+switchStr+"]},");}
					if(appStr.length()>0){sb.append("{'id':'APPLICATION','name':'应用','children':["+appStr+"]},");}
					JSONArray array = new JSONArray().fromObject("["+sb.toString().substring(0, sb.toString().length()-1).toString()+"]");
					//内置TopN信息
					row.set("topn_array", array.toString());
				}
				
				//告警信息
				if (alertArray.equals("-1")) {
					//内置告警信息
					row.set("alert_array", "[{'id':'alertLevel1','name':'告警信息级别','children':[{'id':'Info','name':'Info'},{'id':'Warning','name':'Warning'},{'id':'Critical','name':'Critical'}]}]");
				}
			}
		}
		return row;
	}
	
	public String dataFilter(List<DataRow> rows,String children){
		String str = "";
		if (rows != null && rows.size() > 0) {
			int i = 0;
			for (DataRow dataRow : rows) {
				if(i>0){
					str+=",";
				}
				str+="{'id':'"+dataRow.getString("ele_id")+"','name':'"+dataRow.getString("ele_name")+"','children':"+children+"}";
				i++;
			}
		}
		return str;
	}
	
	public static void main(String[] args) {
		ReportMaker m = new ReportMaker();
		ReportService rs = new ReportService();
		List<DataRow> appRow = rs.getInitConfig("select fid as ele_id,fname as ele_name from tnapps", null, WebConstants.DB_DEFAULT);
		System.out.println(m.initChildren(appRow,"APPLICATION"));
	}
	
	/**
	 * 初始化子设备性能
	 * @return
	 */
	public String initChildren(List<DataRow> syss ,String type){
		String str = "";
		if(syss!=null && syss.size()>0){
			int i = 0;
			for (DataRow sys : syss) {
				if(i>0){
					str+=",";
				}
				str+="{'id':'"+sys.getString("ele_id")+"','name':'"+sys.getString("ele_name")+"','children':"+getSubDevKPIs(type,sys.getInt("ele_id")).toString()+"}";
				i++;
			}
		}
		return str;
	}
	
	/**
	 * 获取部件性能指标
	 * @param type
	 * @param sysId
	 * @return
	 */
	public JSONArray getSubDevKPIs(String type,Integer sysId){
		ReportService rs = new ReportService();
		JSONArray ary = new JSONArray();
		List<DataRow> rows = rs.getSubDevicePerf(type);
		if (rows != null && rows.size() > 0) {
			for (DataRow dataRow : rows) {
				JSONArray array = new JSONArray();
				List<DataRow> list = new ArrayList<DataRow>();
				List<DataRow> subs = new ArrayList<DataRow>();
				JSONObject json = new JSONObject();
				String storageType = dataRow.getString("fstoragetype");
				String devType = dataRow.getString("fdevtype");
				
				//For BSP/DS/SVC
				if (type.equals(SrContant.DEVTYPE_VAL_BSP) 
						|| type.equals(SrContant.DEVTYPE_VAL_DS)
						|| type.equals(SrContant.DEVTYPE_VAL_SVC)) {
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						//For Storage
						if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
							//Total I/O Rate (overall),Total Response Time,Total Data Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_STORAGE, "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "subsystem_id", "the_display_name","v_res_storage_subsystem", WebConstants.DB_TPC);
						//For Port
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
							//Total Port Data Rate,Total Port I/O Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_PORT, "'Total Port Data Rate','Total Port I/O Rate'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "port_id", "the_display_name", "v_res_port", WebConstants.DB_TPC);
						//For Volume
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
							//Total Data Rate,Total I/O Rate (overall),Total Response Time
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_VOLUME, "'Total Data Rate','Total I/O Rate (overall)','Total Response Time'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "svid", "the_display_name","v_res_storage_volume", WebConstants.DB_TPC);
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_ARRAYSITE)) {
							//Total I/O Rate (overall),Total Data Rate,Total Response Time
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_ARRAYSITE, "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "disk_group_id", "the_display_name","v_res_arraysite", WebConstants.DB_TPC);
						//For Controller
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_CONTROLLER)) {
							//Total I/O Rate (overall),Total Response Time,Total Data Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_CONTROLLER, "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
							subs = rs.getSubDevices(sysId, "dev_id", "ele_id", "ele_name", "PRF_TARGET_DSCONTROLLER", WebConstants.DB_TPC);
						//For IOGroup
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_IOGROUP)) {
							//Total I/O Rate (overall),Total Response Time,Total Data Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_IOGROUP, "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "io_group_id", "the_display_name","V_RES_STORAGE_IOGROUP", WebConstants.DB_TPC);
						//For Mdisk
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_MDISK)) {
							//Total Backend Data Rate,Total Backend I/O Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_MDISK, "'Total Backend Data Rate','Total Backend I/O Rate'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "storage_extent_id", "the_display_name", "v_res_storage_extent", WebConstants.DB_TPC);
						//For MdiskGroup
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_MDISKGROUP)) {
							//Total I/O Rate (overall),Total Response Time,Total Data Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_MDISKGROUP, "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "pool_id", "the_display_name","v_res_storage_pool", WebConstants.DB_TPC);
						//For Node
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
							//Total I/O Rate (overall),Total Response Time,Total Data Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_NODE, "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "redundancy_id", "the_display_name","V_RES_REDUNDANCY", WebConstants.DB_TPC);
						//For Rank
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_RANK)) {
							//Total Backend Data Rate,Total Backend I/O Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_RANK, "'Total Backend Data Rate','Total Backend I/O Rate'");
							subs = rs.getSubDevices(sysId, "subsystem_id", "storage_extent_id", "the_display_name","V_RES_STORAGE_RANK", WebConstants.DB_TPC);
						}
					}
				//For EMC/HDS/NETAPP
				} else if (type.equals(SrContant.DEVTYPE_VAL_EMC) 
						|| type.equals(SrContant.DEVTYPE_VAL_HDS)
						|| type.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
					//For Storage
					if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
						if (type.equals(SrContant.DEVTYPE_VAL_EMC) || type.equals(SrContant.DEVTYPE_VAL_HDS)) {
							//Total I/O Rate,Total Data Rate,Total Response Time
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_STORAGE, "'Total I/O Rate','Total Data Rate','Total Response Time'");
							subs = rs.getSubDevices(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_NAME, SrTblColConstant.TBL_RES_STORAGESUBSYSTEM, WebConstants.DB_DEFAULT);
						} else if (type.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
							//Read I/O Rate,Write I/O Rate,Total I/O Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_STORAGE, "'Read I/O Rate','Write I/O Rate','Total I/O Rate'");
							subs = rs.getSubDevices(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_NAME, SrTblColConstant.TBL_RES_STORAGESUBSYSTEM, WebConstants.DB_DEFAULT);
						}
					//For DiskGroup
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_DISKGROUP)) {
						//Total I/O Rate,Total Data Rate,Total Response Time
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_DISKGROUP, "'Total I/O Rate','Total Data Rate','Total Response Time'");
						subs = rs.getSubDevices(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME, SrTblColConstant.TBL_RES_DISKGROUP, WebConstants.DB_DEFAULT);
					//For HostGroup
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_HOSTGROUP)) {
						//Total I/O Rate,Total Data Rate,Total Response Time
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_HOSTGROUP, "'Total I/O Rate','Total Data Rate','Total Response Time'");
						subs = rs.getSubDevices(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RHG_HOSTGROUP_ID, SrTblColConstant.RHG_HOSTGROUP_NAME, SrTblColConstant.TBL_RES_HOSTGROUP, WebConstants.DB_DEFAULT);
					//For Port
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
						//Total I/O Rate,Total Data Rate,Total Response Time
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_PORT, "'Total I/O Rate','Total Data Rate','Total Response Time'");
						subs = rs.getSubDevices(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME, SrTblColConstant.TBL_RES_PORT, WebConstants.DB_DEFAULT);
					//For Node
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
						//Total I/O Rate,Total Data Rate,Total Response Time
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_NODE, "'Total I/O Rate','Total Data Rate','Total Response Time'");
						subs = rs.getSubDevices(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSN_SP_ID, SrTblColConstant.RSN_SP_NAME, SrTblColConstant.TBL_RES_STORAGENODE, WebConstants.DB_DEFAULT);
					//For Volume
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
						//Total I/O Rate,Total Data Rate,Total Response Time
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_VOLUME, "'Total I/O Rate','Total Data Rate','Total Response Time'");
						subs = rs.getSubDevices(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME, SrTblColConstant.TBL_RES_STORAGEVOLUME, WebConstants.DB_DEFAULT);
					}
				//For SWITCH
				} else if (type.equals(SrContant.DEVTYPE_VAL_SWITCH)) {
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						//For Switch
						if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCH)) {
							//CRC Error Rate,Total Port Data Rate
							list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_SWITCH, "'CRC Error Rate','Total Port Data Rate'");
							subs = rs.getSubDevices(sysId, "switch_id", "switch_id", "the_display_name", "v_res_switch", WebConstants.DB_TPC);
						//For SwitchPort
						} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCHPORT)) {
							
						}
					}
				//For HOST
				} else if (type.equals(SrContant.DEVTYPE_VAL_HOST)) {
					//For Physical
					if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
						//cpu busy percentage,cpu idle percentage,memory used percentage
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_PHYSICAL, "'cpu busy percentage','cpu idle percentage','memory used percentage'");
						subs = rs.getSubDevices(sysId, "hypervisor_id", "hypervisor_id", "name", "t_res_hypervisor", WebConstants.DB_DEFAULT);
					//For Virtual
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)) {
						//cpu busy percentage,cpu idle percentage,memory used percentage
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_VIRTUAL, "'cpu busy percentage','cpu idle percentage','memory used percentage'");
						subs = rs.getSubDevices(sysId, "hypervisor_id", "vm_id", "name", "t_res_virtualmachine", WebConstants.DB_DEFAULT);
					//For HYPERVISOR
					} else if (devType.equalsIgnoreCase(WebConstants.DEVTYPE_HYPERVISOR)) {
						//CPU User Percentage,Total Network Packets,Total Disk Data Rate
						list = rs.getKPIInfoByTitle(storageType, WebConstants.DEVTYPE_HYPERVISOR, "'CPU User Percentage','Total Network Packets','Total Disk Data Rate'");
						subs = rs.getSubDevices(sysId, "hypervisor_id", "vm_id", "name", "t_res_virtualmachine", WebConstants.DB_DEFAULT);
					}
				//For APPLICATION
				} else if (type.equals(SrContant.DEVTYPE_VAL_APPLICATION)) {
					//For App
					if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_APP)) {
						//memory used percentage,READ IOPS
						list = rs.getKPIInfoByTitle(storageType, SrContant.SUBDEVTYPE_APP, "'memory used percentage','READ IOPS'");
						subs = rs.getSubDevices(sysId, "fid", "fid", "fname", "tnapps", WebConstants.DB_DEFAULT);
					}
				}
				
				if (list != null && list.size() > 0) {
					for (DataRow dataRow2 : list) {
						JSONObject obj = new JSONObject();
						obj.put("id", dataRow2.getString("fid"));
						obj.put("name", dataRow2.getString("ftitle"));
						if(subs!=null && subs.size()>0){
							JSONArray arrys = new JSONArray();
							for (DataRow dataRow3 : subs) {
								JSONObject obj3 = new JSONObject();
								obj3.put("id", dataRow3.getString("id"));
								obj3.put("name", dataRow3.getString("value"));
								arrys.add(obj3);
							}
							obj.put("children", arrys);
							array.add(obj);
						}
					}
					if (subs != null && subs.size() > 0) {
						json.put("id", dataRow.getString("fdevtype"));
						json.put("name", dataRow.getString("fdevtypename"));
						json.put("children", array);
						json.put("threshold", "");
						ary.add(json);
					}
				}
			}
		}
		return ary;
	}
	
}
