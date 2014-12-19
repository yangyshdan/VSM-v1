package root.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
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
import com.huiming.service.storage.StorageService;
import com.huiming.service.switchport.SwitchportService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.topn.TopnService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.service.volume.VolumeService;
import com.huiming.web.HtmlGenerator;
import com.project.web.WebConstants;

@SuppressWarnings("static-access")
public class ReportMaker {
	private HtmlGenerator htmlGenerator = new HtmlGenerator();
	private Map<String, Object> variables = new HashMap<String, Object>();
	public boolean doReportFtl(DataRow row,JSONObject msg) throws Exception{
		String path = this.getClass().getClassLoader().getResource("/").toString().replaceAll("%20"," ").replaceAll("/WEB-INF/classes", "").replaceAll("file:/", "");
		DataRow dataRow = doInitReport(row);
		//右侧菜单及配置信息及告警信息
		doGetzTree(dataRow);
		//告警信息
		doAlertCon(dataRow);
		//TopN
		doTopnCon(dataRow);
		//性能信息
		doperfCon2(dataRow);
		String img = row.getString("report_logo_url")==""?null:row.getString("report_logo_url").substring(row.getString("report_logo_url").lastIndexOf("/"),row.getString("report_logo_url").length());
		variables.put("logo", img);
		variables.put("startTime", row.getString("starttime"));
		variables.put("endTime", row.getString("endtime"));
		variables.put("title", row.getString("the_display_name"));
		return htmlGenerator.buildHtml("report.ftl",variables, path+row.getString("real_name"));
	}
	
	/**
	 * 得到性能信息(以设备为节点)
	 * 
	 * @param row
	 */
	public void doperfCon(DataRow row){
		BaseprfService bs = new BaseprfService();
		JSONArray pnode = new JSONArray().fromObject(row.getString("perf_array"));
		JSONArray pary = new JSONArray();
		for (Object object : pnode) {
			//得到设备类型such as {id:SVC,name:SVC,children:[...]}
			JSONObject obj = new JSONObject().fromObject(object);
			JSONObject pobj = new JSONObject();
			if(obj.has("children") && obj.getJSONArray("children").size()>0){
				JSONArray array = new JSONArray().fromObject(obj.getString("children"));
				JSONArray pary1 = new JSONArray();
				for (Object object2 : array) { 
					//得到设备信息such as {id:34208,name:SVC-2145-BOD,children:[...]}
					JSONObject obj2 = new JSONObject().fromObject(object2);
					JSONObject pobj2 = new JSONObject();
					if(obj2.has("children") && obj2.getJSONArray("children").size()>0){
						JSONArray array2 = new JSONArray().fromObject(obj2.getString("children"));
						JSONArray pary2 = new JSONArray();
						for (Object object3 : array2) {
							//得到组件信息such as {id:IOGroup,name:IO组,threshold:1000,children:[...]}
							JSONObject obj3 = new JSONObject().fromObject(object3);
							JSONObject pobj3 = new JSONObject();
							if(obj3.has("children") && obj3.getJSONArray("children").size()>0){
								JSONArray array3 = new JSONArray().fromObject(obj3.getString("children"));
								JSONArray pary3 = new JSONArray();
								for (Object object4 : array3) {
									//得到组件信息such as {id:36065,name:2107.75AMD31-A13,children:[...]}
									JSONObject obj4 = new JSONObject().fromObject(object4);
									JSONObject pobj4 = new JSONObject();
									if(obj4.has("children") && obj4.getJSONArray("children").size()>0){
										JSONArray array4 = new JSONArray().fromObject(obj4.getString("children"));
										String prfid = "";
										for (int h=0;h<array4.size();h++) {
											//这里统计fprfid
											//得到KPI信息such as {id:A213,name:Total read io}
											JSONObject obj5 = new JSONObject().fromObject(array4.get(h));
											prfid+="'"+obj5.getString("id")+"'";
											if(h<array4.size()-1){
												prfid+=",";
											}
										}
										//性能列
//										DataRow prow = new DataRow();
//										prow.set("fsubsystemid", obj2.getInt("id"));
//										prow.set("fname", obj3.getString("id"));
//										prow.set("fdevicetype", obj.getString("id"));
//										prow.set("fdevice", obj4.getInt("id"));
//										prow.set("fprfid", prfid);
//										prow.set("fisshow", 1);
//										prow.set("fyaxisname", "");
//										prow.set("flegend", 1);
//										prow.set("fThreValue", obj4.getInt("threshold"));
//										prow.set("fstarttime", row.getString("starttime"));
//										prow.set("fendtime", row.getString("endtime"));
										List<DataRow> dev = new ArrayList<DataRow>();
										DataRow  rw = new DataRow();
										rw.set("ele_id", obj4.getInt("id"));
										rw.set("ele_name", obj4.getString("name"));
										dev.add(rw);
										List<DataRow> kpis = bs.getKPIInfo(prfid);
										JSONArray perfData = bs.getPrfDatas(1, dev, kpis, row.getString("starttime"), row.getString("endtime"),null);
										//封装性能数据
										JSONObject json = new JSONObject();  
										if(perfData!=null && perfData.size()>0){
											json.put("series", perfData);
										}
										json.put("legend", true);
										json.put("ytitle", "");
										String threshold = obj3.getString("threshold");
										if(threshold!=null && threshold.length()>0){
											json.put("threvalue",obj3.getInt("threshold"));
										}else{
											json.put("threvalue","");
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
	 * 得到性能信息(以性能为节点)
	 * 
	 * @param row
	 */
	public void doperfCon2(DataRow row){
		BaseprfService bs = new BaseprfService();
		JSONArray pnode = new JSONArray().fromObject(row.getString("perf_array"));
		JSONArray pary = new JSONArray();
		for (Object object : pnode) {
			//得到设备类型such as {id:SVC,name:SVC,children:[...]}
			JSONObject obj = new JSONObject().fromObject(object);
			JSONObject pobj = new JSONObject();
			if(obj.has("children") && obj.getJSONArray("children").size()>0){
				JSONArray array = new JSONArray().fromObject(obj.getString("children"));
				JSONArray pary1 = new JSONArray();
				for (Object object2 : array) {
					//得到APP的KPI列表 for example: {id:'app',name:'应用程序',threshold:'',children:[...]}
					JSONObject obj2 = new JSONObject().fromObject(object2);
					JSONObject pobj2 = new JSONObject();
					if(obj2.has("children") && obj2.getJSONArray("children").size()>0){
						JSONArray array2 = new JSONArray().fromObject(obj2.getString("children"));
						JSONArray pary2 = new JSONArray();
						for (Object object3 : array2) {
							//的到APP性能列表信息 for example:{id:'app1',name:'Write IO',children:[]}
							JSONObject obj3 = new JSONObject().fromObject(object3);
							JSONObject pobj3 = new JSONObject();
							if(obj3.has("children") && obj3.getJSONArray("children").size()>0){
								//得到APP列表信息 for example:{id:'1',name:'APP1'}
								JSONArray array3 = new JSONArray().fromObject(obj3.getString("children"));
								JSONArray pary3 = new JSONArray();
								for (Object object4 : array3) {
									JSONObject obj4 = new JSONObject().fromObject(object4);
									JSONObject pobj4 = new JSONObject();
									if(obj4.has("children") && obj4.getJSONArray("children").size()>0){
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
										
										List<DataRow> kpis = bs.getKPIInfo("'"+obj4.getString("id")+"'");
										JSONArray perfData = bs.getPrfDatas(1, devs, kpis, row.getString("starttime"), row.getString("endtime"),"report");
										//封装性能数据
										JSONObject json = new JSONObject();  
										if(perfData!=null && perfData.size()>0){
											json.put("series", perfData);
										}
										json.put("legend", true);
										json.put("ytitle", "");
										String threshold = obj3.getString("threshold");
										if(threshold!=null && threshold.length()>0){
											json.put("threvalue",obj3.getInt("threshold"));
										}else{
											json.put("threvalue","");
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
							List<DataRow> rows =null;
							if(devId.equals("storage")){
								if(!stype.equalsIgnoreCase("EMC")){
									StorageService ser = new StorageService();
									rows = ser.subStorageList(json.getString("id").toUpperCase());
								}else{
//									StorageSystemService ser = new StorageSystemService();
//									rows = ser.getStorageInfoById(subsystemId);
								}
							}else if(devId.equals("node")){
								NodeService ns = new NodeService();
								rows = ns.getNodeList(null, null, null, subsystemId);
							}else if(devId.equals("port")){
								if(!stype.equalsIgnoreCase("EMC")){
									PortService ps = new PortService();
									rows = ps.getPortList(null, null, null, null, null, subsystemId);
								}else{
//									com.huiming.service.sr.port.PortService ps = new com.huiming.service.sr.port.PortService();
//									rows = ps.getPortInfos(Long.parseLong(subsystemId.toString()));
								}
							}else if(devId.equals("volume")){
								if(!stype.equalsIgnoreCase("EMC")){
									VolumeService vs = new VolumeService();
									rows = vs.getVolumeInfo(null, null, null, null, subsystemId);
								}else{
//									com.huiming.service.sr.volume.VolumeService vs = new com.huiming.service.sr.volume.VolumeService();
//									rows = vs.getVolumeInfo(Long.parseLong(subsystemId.toString()));
								}
							}else if(devId.equals("pool")){
								if(!stype.equalsIgnoreCase("EMC")){
									PoolService ps = new PoolService();
									rows = ps.getPoolsInfo(null, null, null, subsystemId);
								}else{
//									com.huiming.service.sr.pool.PoolService ps = new com.huiming.service.sr.pool.PoolService();
//									rows = ps.reportPool(subsystemId);
								}
							}else if(devId.equals("disk")){
								if(!stype.equalsIgnoreCase("EMC")){
									DiskService ds =new DiskService();
									rows = ds.getDiskList(null, null, null, subsystemId);
								}else{
//									DdmService ds = new DdmService();
//									rows = ds.getDiskList(subsystemId, null);
								}
							}else if(devId.equals("arrayset")){
								ArraysiteService as = new ArraysiteService();
								rows = as.getArraysiteList(null, null, subsystemId);
							}else if(devId.equals("extent")){
								ExtendService es = new ExtendService();
								rows = es.getExtendList(null, null, null, null, null, null, subsystemId);
							}else if(devId.equals("rank")){
								RankService rs = new RankService();
								rows = rs.getRankList(null, null, subsystemId);
							}else if(devId.equals("iogroup")){
								IoGroupService is = new IoGroupService();
								rows = is.getIogroupList(null, subsystemId);
							}else if(devId.equals("switch")){
								SwitchService ss = new SwitchService();
								rows = new ArrayList<DataRow>();
								rows.add(ss.getSwitchInfo(subsystemId));
							}else if(devId.equals("switchPort")){
								SwitchportService ss = new SwitchportService();
								rows = ss.getExtentportList(null, null, null, null, null, subsystemId);
							}else if(devId.equals("storagegroup")){
//								HostgroupService sh = new HostgroupService();
//								rows = sh.getHostgroupList(subsystemId);
							}else if(devId.equals("physical")){
								HypervisorService hs = new HypervisorService();
								rows = hs.getPhysicalList(subsystemId);
							}else if(devId.equals("virtual")){
								VirtualmachineService vs = new VirtualmachineService();
								rows = vs.getVirtualList(subsystemId);
							}else if(devId.equals("app")){
								AppsService app = new AppsService();
								rows = app.getMappingVirtual(subsystemId);
							}
							if(rows!=null && rows.size()>0){
								JSONObject devjson3 = new JSONObject();
								devjson3.put("divId", "D_"+stype+"_Storage"+subsystemId+"_"+devId);
								devjson3.put("name", json3.getString("name"));
								devarr2.add(devjson3);
							}
							//cjson3.put("configList", new JSONArray().fromObject(rows));
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
		
		JSONArray cZnode = doGetConfig(row);   //得到设备配置信息
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
	public DataRow doInitReport(DataRow row){
		ReportService rs = new ReportService();
		if(row!=null && row.size()>0){
			String reportType = row.getString("report_type");
			if(reportType.equals("1")){   //任务报表
				String deviceArray = row.getString("device_array");
				String perfArray = row.getString("perf_array");
				String topnArray = row.getString("topn_array");
				String alertArray = row.getString("alert_array");
				//内置设备信息      BSP,DS,SVC,SWITCH,APPLICATION,HOST (EMC的没做)
				if(deviceArray.equals("-1")){   
					List<DataRow> bspRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("BSP")+")", null, WebConstants.DB_TPC);
					List<DataRow> dsRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("DS")+")", null, WebConstants.DB_TPC);
					List<DataRow> svcRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("SVC")+")", null, WebConstants.DB_TPC);
					List<DataRow> switchRow = rs.getInitConfig("select switch_id as ele_id,the_display_name as ele_name from v_res_switch where the_display_name !='' ", null, WebConstants.DB_TPC);
					List<DataRow> appRow = rs.getInitConfig("select fid as ele_id,fname as ele_name from tnapps", null, WebConstants.DB_DEFAULT);
					List<DataRow> hostRow = rs.getInitConfig("SELECT h.hypervisor_id AS ele_id,COALESCE(c.display_name,c.name) AS ele_name FROM t_res_computersystem c,t_res_hypervisor h", null, WebConstants.DB_DEFAULT);
					String svcStr = dataFilter(svcRow, ReportAction.STORAGE_OPT);
					StringBuffer sb = new StringBuffer();
					String bspStr = dataFilter(bspRow, ReportAction.STORAGE_OPT);
					String dsStr = dataFilter(dsRow, ReportAction.STORAGE_OPT);
					String switchStr = dataFilter(switchRow, ReportAction.SWITCH_OPT);
					String appStr = dataFilter(appRow, ReportAction.APP_OPT);
					String hostStr = dataFilter(hostRow, ReportAction.HOST_OPT);
					if(svcStr.length()>0){sb.append("{'id':'SVC','name':'SVC','children':["+svcStr+"]},");};
					if(bspStr.length()>0){sb.append("{'id':'BSP','name':'BSP','children':["+bspStr+"]},");};
					if(dsStr.length()>0){sb.append("{'id':'DS','name':'DS','children':["+dsStr+"]},");};
					if(switchStr.length()>0){sb.append("{'id':'SWITCH','name':'交换机','children':["+switchStr+"]},");};
					if(appStr.length()>0){sb.append("{'id':'APPLICATION','name':'应用','children':["+appStr+"]},");};
					if(hostStr.length()>0){sb.append("{'id':'HOST','name':'主机','children':["+hostStr+"]},");};
					JSONArray array = new JSONArray().fromObject("["+sb.toString().substring(0, sb.toString().length()-1).toString()+"]");
//				System.out.println("Configration"+array.toString());
					//内置设备信息
					row.set("device_array", array.toString());
				}
				if(perfArray.equals("-1")){ 
					List<DataRow> bspRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("BSP")+")", null, WebConstants.DB_TPC);
					List<DataRow> dsRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("DS")+")", null, WebConstants.DB_TPC);
					List<DataRow> svcRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("SVC")+")", null, WebConstants.DB_TPC);
					List<DataRow> switchRow = rs.getInitConfig("select switch_id as ele_id,the_display_name as ele_name from v_res_switch where the_display_name !='' ", null, WebConstants.DB_TPC);
					List<DataRow> appRow = rs.getInitConfig("select fid as ele_id,fname as ele_name from tnapps", null, WebConstants.DB_DEFAULT);
					List<DataRow> hostRow = rs.getInitConfig("SELECT h.hypervisor_id AS ele_id,COALESCE(c.display_name,c.name) AS ele_name FROM t_res_computersystem c,t_res_hypervisor h", null, WebConstants.DB_DEFAULT);
					//内置性能信息
					StringBuffer sb = new StringBuffer();
					String svcStr = initChildren(svcRow, "SVC");
					String bspStr = initChildren(bspRow, "BSP");
					String dsStr = initChildren(dsRow, "DS");
					String switchStr = initChildren(switchRow, "SWITCH");
					String appStr = initChildren(appRow, "APPLICATION");
					String hostStr = initChildren(hostRow, "HOST");
					if(svcStr.length()>0){sb.append("{'id':'SVC','name':'SVC','children':["+svcStr+"]},");};
					if(bspStr.length()>0){sb.append("{'id':'BSP','name':'BSP','children':["+bspStr+"]},");};
					if(dsStr.length()>0){sb.append("{'id':'DS','name':'DS','children':["+dsStr+"]},");};
					if(switchStr.length()>0){sb.append("{'id':'SWITCH','name':'交换机','children':["+switchStr+"]},");};
					if(appStr.length()>0){sb.append("{'id':'APPLICATION','name':'应用','children':["+appStr+"]},");};
					if(hostStr.length()>0){sb.append("{'id':'HOST','name':'主机','children':["+hostStr+"]},");};
					JSONArray array = new JSONArray().fromObject("["+sb.toString().substring(0, sb.toString().length()-1).toString()+"]");
					
					row.set("perf_array", array.toString());
				}
				if(topnArray.equals("-1")){     
					List<DataRow> bspRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("BSP")+")", null, WebConstants.DB_TPC);
					List<DataRow> dsRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("DS")+")", null, WebConstants.DB_TPC);
					List<DataRow> svcRow = rs.getInitConfig("select subsystem_id as ele_id,the_display_name as ele_name from v_res_storage_subsystem where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString("SVC")+")", null, WebConstants.DB_TPC);
					List<DataRow> switchRow = rs.getInitConfig("select switch_id as ele_id,the_display_name as ele_name from v_res_switch where the_display_name !='' ", null, WebConstants.DB_TPC);
					List<DataRow> appRow = rs.getInitConfig("select fid as ele_id,fname as ele_name from tnapps", null, WebConstants.DB_DEFAULT);
					List<DataRow> hostRow = rs.getInitConfig("SELECT h.hypervisor_id AS ele_id,COALESCE(c.display_name,c.name) AS ele_name FROM t_res_computersystem c,t_res_hypervisor h", null, WebConstants.DB_DEFAULT);
					//内置TopN信息
					String svcStr = dataFilter(svcRow, SVC_TOPN);
					StringBuffer sb = new StringBuffer();
					String bspStr = dataFilter(bspRow, BSP_TOPN);
					String dsStr = dataFilter(dsRow, DS_TOPN);
					String switchStr = dataFilter(switchRow, SWITCH_TOPN);
					String appStr = dataFilter(appRow, APP_TOPN);
					String hostStr = dataFilter(hostRow, HOST_TOPN);
					if(svcStr.length()>0){sb.append("{'id':'SVC','name':'SVC','children':["+svcStr+"]},");};
					if(bspStr.length()>0){sb.append("{'id':'BSP','name':'BSP','children':["+bspStr+"]},");};
					if(dsStr.length()>0){sb.append("{'id':'DS','name':'DS','children':["+dsStr+"]},");};
					if(switchStr.length()>0){sb.append("{'id':'SWITCH','name':'交换机','children':["+switchStr+"]},");};
					if(appStr.length()>0){sb.append("{'id':'APPLICATION','name':'应用','children':["+appStr+"]},");};
					if(hostStr.length()>0){sb.append("{'id':'HOST','name':'主机','children':["+hostStr+"]},");};
					JSONArray array = new JSONArray().fromObject("["+sb.toString().substring(0, sb.toString().length()-1).toString()+"]");
					System.out.println("TopN:"+array.toString());
					//内置TopN信息
					row.set("topn_array", array.toString());
					
				}
				if(alertArray.equals("-1")){
					//内置告警信息
					row.set("alert_array", "[{'id':'alertLevel1','name':'告警信息级别','children':[{'id':'Info','name':'Info'},{'id':'Warning','name':'Warning'},{'id':'Critical','name':'Critical'}]}]");
				}
			}
		}
		return row;
	}
	
	public String dataFilter(List<DataRow> rows,String children){
		String str = "";
		if(rows!=null && rows.size()>0){
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
	
	
	public JSONArray getSubDevKPIs(String type,Integer sysId){
		ReportService rs = new ReportService();
		JSONArray ary = new JSONArray();
		List<DataRow> rows = rs.getSubDevicePerf(type);
		if(rows!=null && rows.size()>0){
			for (DataRow dataRow : rows) {
				JSONArray array = new JSONArray();
				List<DataRow> list = null;
				List<DataRow> subs = null;
				JSONObject json = new JSONObject();
				if(dataRow.getString("fdevtype").equalsIgnoreCase("port")){
					//Total Port Data Rate,Total Port I/O Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Port", "'Total Port Data Rate','Total Port I/O Rate'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "port_id", "the_display_name", "v_res_port", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("volume")){
					//Total Data Rate,Total I/O Rate (overall),Total Response Time
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Volume", "'Total Data Rate','Total I/O Rate (overall)','Total Response Time'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "svid", "the_display_name","v_res_storage_volume", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("app")){
					//memory used percentage,READ IOPS
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "App", "'memory used percentage','READ IOPS'");
					subs = rs.getSubDevices(sysId, "fid", "fid", "fname", "tnapps", WebConstants.DB_DEFAULT);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("arraysite")){
					//Total I/O Rate (overall),Total Data Rate,Total Response Time
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "ArraySite", "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "disk_group_id", "the_display_name","v_res_arraysite", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("controller")){
					//Total I/O Rate (overall),Total Response Time,Total Data Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Controller", "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
					subs = rs.getSubDevices(sysId, "dev_id", "ele_id", "ele_name", "PRF_TARGET_DSCONTROLLER", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("iogroup")){
					//Total I/O Rate (overall),Total Response Time,Total Data Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "IOGroup", "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "io_group_id", "the_display_name","V_RES_STORAGE_IOGROUP", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("mdisk")){
					//Total Backend Data Rate,Total Backend I/O Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Mdisk", "'Total Backend Data Rate','Total Backend I/O Rate'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "storage_extent_id", "the_display_name", "v_res_storage_extent", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("mdiskgroup")){
					//Total I/O Rate (overall),Total Response Time,Total Data Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "MdiskGroup", "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "pool_id", "the_display_name","v_res_storage_pool", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("node")){
					//Total I/O Rate (overall),Total Response Time,Total Data Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Node", "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "redundancy_id", "the_display_name","V_RES_REDUNDANCY", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("physical")){
					//cpu busy percentage,cpu idle percentage,memory used percentage
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Physical", "'cpu busy percentage','cpu idle percentage','memory used percentage'");
					subs = rs.getSubDevices(sysId, "hypervisor_id", "hypervisor_id", "name", "t_res_hypervisor", WebConstants.DB_DEFAULT);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("virtual")){
					//cpu busy percentage,cpu idle percentage,memory used percentage
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Virtual", "'cpu busy percentage','cpu idle percentage','memory used percentage'");
					subs = rs.getSubDevices(sysId, "hypervisor_id", "vm_id", "name", "t_res_virtualmachine", WebConstants.DB_DEFAULT);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("rank")){
					//Total Backend Data Rate,Total Backend I/O Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Rank", "'Total Backend Data Rate','Total Backend I/O Rate'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "storage_extent_id", "the_display_name","V_RES_STORAGE_RANK", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("storage")){
					//Total I/O Rate (overall),Total Response Time,Total Data Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Storage", "'Total I/O Rate (overall)','Total Data Rate','Total Response Time'");
					subs = rs.getSubDevices(sysId, "subsystem_id", "subsystem_id", "the_display_name","v_res_storage_subsystem", WebConstants.DB_TPC);
				}else if(dataRow.getString("fdevtype").equalsIgnoreCase("switch")){
					//CRC Error Rate,Total Port Data Rate
					list = rs.getKPIInfoByTitle(dataRow.getString("fstoragetype"), "Switch", "'CRC Error Rate','Total Port Data Rate'");
					subs = rs.getSubDevices(sysId, "switch_id", "switch_id", "the_display_name", "v_res_switch", WebConstants.DB_TPC);
				}
				if(list!=null && list.size()>0){
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
					if(subs!=null && subs.size()>0){
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
	
	//内置性能指标
	private final String SVC_TOPN = "[{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A9','name':'Total Data Rate'},{'id':'A3','name':'Total I/O Rate (overall)'},{'id':'A12','name':'Total Response Time'}]},{'topn_count':'10','id':'Storage','name':'存储系统TOP10','children':[{'id':'A44','name':'Total Data Rate'},{'id':'A38','name':'Total I/O Rate (overall)'},{'id':'A47','name':'Total Response Time'}]},{'topn_count':'10','id':'IOGroup','name':'IO组TOP10','children':[{'id':'A699','name':'Total Data Rate'},{'id':'A693','name':'Total I/O Rate (overall)'},{'id':'A702','name':'Total Response Time'}]},{'topn_count':'10','id':'Port','name':'端口TOP10','children':[{'id':'A142','name':'Total Port Data Rate'},{'id':'A139','name':'Total Port I/O Rate'}]},{'topn_count':'10','id':'Node','name':'存储节点TOP10','children':[{'id':'A545','name':'Total Data Rate'},{'id':'A539','name':'Total I/O Rate (overall)'},{'id':'A573','name':'Total Port I/O Rate'}]}]";
	private final String BSP_TOPN = "[{'topn_count':'10','id':'Storage','name':'存储系统TOP10','children':[{'id':'A421','name':'Total Data Rate'},{'id':'A415','name':'Total I/O Rate (overall)'}]},{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A433','name':'Total Data Rate'},{'id':'A427','name':'Total I/O Rate (overall)'}]},{'topn_count':'10','id':'Port','name':'端口TOP10','children':[{'id':'A409','name':'Total Port Data Rate'},{'id':'A406','name':'Total Port I/O Rate'}]}]";
	private final String DS_TOPN = "[{'topn_count':'10','id':'Controller','name':'控制器TOP10','children':[{'id':'A458','name':'Total Data Rate'},{'id':'A444','name':'Total I/O Rate (normal)'},{'id':'A461','name':'Total Response Time'}]},{'topn_count':'10','id':'Volume','name':'卷TOP10','children':[{'id':'A183','name':'Total I/O Rate (overall)'},{'id':'A198','name':'Total Response Time'}]},{'topn_count':'10','id':'Rank','name':'RankTOP10','children':[{'id':'A799','name':'Total Backend Data Rate'}]}]";
	private final String SWITCH_TOPN = "[{'topn_count':'10','id':'Switch','name':'交换机TOP10','children':[{'id':'A529','name':'CRC Error Rate'},{'id':'A518','name':'Total Port Data Rate'}]}]";
	private final String APP_TOPN = "[{'topn_count':'10','id':'App','name':'应用程序TOP10','children':[{'id':'APP1','name':'cpu idle percentage'},{'id':'APP3','name':'memory used percentage'},{'id':'APP10','name':'READ IOPS'}]}]";
	private final String HOST_TOPN = "[{'topn_count':'10','id':'Physical','name':'物理机TOP10','children':[{'id':'H1','name':'cpu busy percentage'},{'id':'H3','name':'cpu idle percentage'},{'id':'H2','name':'memory used percentage'}]}]";
}
