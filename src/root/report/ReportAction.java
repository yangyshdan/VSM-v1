package root.report;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.UUID;
import com.huiming.service.report.ReportService;
import com.huiming.service.topn.TopnService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ReportAction extends SecurityAction {

	private static Logger logger = Logger.getLogger(ReportAction.class);
	SchedulerFactory sf = new StdSchedulerFactory();
	TopnService service = new TopnService();
	ReportService ts = new ReportService();
	ReportMaker rm = new ReportMaker();

	public static final String STORAGE_OPT = "[{id:'storage',name:'存储系统'},{id:'node',name:'冗余节点'}," +
			"{id:'port',name:'端口'},{id:'volume',name:'卷'}," +
			"{id:'mdiskgroup',name:'存储池'},{id:'disk',name:'磁盘'}," +
			"{id:'arrayset',name:'阵列'},{id:'mdisk',name:'存储扩展'}," +
			"{id:'rank',name:'Rank'},{id:'iogroup',name:'IOGroup'}]";
	public static final String SWITCH_OPT = "[{id:'switch',name:'交换机'},{id:'switchPort',name:'交换机端口'}]";
	public static final String EMC_OPT = "[{id:'storage',name:'存储系统'},{id:'storagegroup',name:'存储关系组'}," +
			"{id:'port',name:'端口'},{id:'volume',name:'卷'}," +
			"{id:'pool',name:'存储池'},{id:'disk',name:'磁盘'}]";
	public static final String HOST_OPT = "[{id:'physical',name:'物理机'},{id:'virtual',name:'虚拟机'}]";
	public static final String APP_OPT = "[{id:'app',name:'应用程序'}]";

	/**
	 * 配置信息加载的设备列表
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doCustomReport() {

		JSONObject deviceList = new JSONObject();
		// deviceList.put("EMC", new JSONArray().fromObject(service.getEMCStorage()));
		deviceList.put("SVC", new JSONArray().fromObject(service.getStorageType("SVC")));
		deviceList.put("BSP", new JSONArray().fromObject(service.getStorageType("BSP")));
		deviceList.put("DS", new JSONArray().fromObject(service.getStorageType("DS")));
		deviceList.put("SWITCH", new JSONArray().fromObject(service.getSwitchList()));
		deviceList.put("APPLICATION", new JSONArray().fromObject(service.getAppList()));
		JSONObject fnameList = new JSONObject();
		fnameList.put("SVC", new JSONArray().fromObject(service.getFnameList("SVC")));
		fnameList.put("BSP", new JSONArray().fromObject(service.getFnameList("BSP")));
		fnameList.put("DS", new JSONArray().fromObject(service.getFnameList("DS")));
//		fnameList.put("EMC", new JSONArray().fromObject(service.getFnameList("EMC")));
		fnameList.put("SWITCH", new JSONArray().fromObject(service.getFnameList("SWITCH")));
		fnameList.put("APPLICATION", new JSONArray().fromObject(service.getFnameList("APPLICATION")));
		JSONObject fprfidList = new JSONObject();
		fprfidList.put("SVC", new JSONArray().fromObject(service.getFprffildList("SVC")));
		fprfidList.put("BSP", new JSONArray().fromObject(service.getFprffildList("BSP")));
		fprfidList.put("DS", new JSONArray().fromObject(service.getFprffildList("DS")));
		//fprfidList.put("EMC", new JSONArray().fromObject(service.getFprffildList("EMC")));
		fprfidList.put("SWITCH", new JSONArray().fromObject(service.getFprffildList("SWITCH")));
		fprfidList.put("APPLICATION", new JSONArray().fromObject(service.getFprffildList("APPLICATION")));

		this.setAttribute("fprfidList", fprfidList);
		this.setAttribute("fnameList", fnameList);
		this.setAttribute("deviceList", deviceList);
		this.setAttribute("editTask", "null");
		return new ActionResult("/WEB-INF/views/report/editReport.jsp");
	}

	/**
	 * 组件类型的列表信息
	 */
	@SuppressWarnings("static-access")
	public void doDeviceTypeChange() {

		JSONObject obj = new JSONObject();
		JSONArray jsArray = null;
		String type = getStrParameter("type");
		if (type.equals("SVC")) {
			jsArray = new JSONArray().fromObject(service.getStorageType("SVC"));
			obj.put("subOption", STORAGE_OPT);
		} else if (type.equals("BSP")) {
			jsArray = new JSONArray().fromObject(service.getStorageType("BSP"));
			obj.put("subOption", STORAGE_OPT);
		} else if (type.equals("DS")) {
			jsArray = new JSONArray().fromObject(service.getStorageType("DS"));
			obj.put("subOption", STORAGE_OPT);
		} else if (type.equals("SWITCH")) {
			jsArray = new JSONArray().fromObject(service.getSwitchList());
			obj.put("subOption", SWITCH_OPT);
		} else if (type.equals("EMC")) {
			jsArray = new JSONArray().fromObject(service.getEMCStorage());
			obj.put("subOption", EMC_OPT);
		} else if (type.equals("HOST")) {
			jsArray = new JSONArray().fromObject(service.getHostList());
			obj.put("subOption", HOST_OPT);
		} else if (type.equals("APPLICATION")) {
			jsArray = new JSONArray().fromObject(service.getAppList());
			obj.put("subOption", APP_OPT);
		}
		obj.put("devOption", jsArray);
		writetopage(obj);
	}

	/**
	 * 组件列表信息
	 */
	@SuppressWarnings("static-access")
	public void doGetsubgroupDev() {

		String stypeId = getStrParameter("stypeId"); // 存储系统类型
		String subtypeId = getStrParameter("subtypeId"); // 组件类型
		String b = getStrParameter("subId");
		String[] storageIds = b.split(",");
		JSONArray ary = new JSONArray();
		if (storageIds[0].length() > 0) {
			for (int i = 0; i < storageIds.length; i++) {
				int sysId = Integer.parseInt(storageIds[i]);
				List<DataRow> rows = null;
				JSONObject obj = new JSONObject();
				if (stypeId.equalsIgnoreCase("emc")) {
					rows = ts.getemcSys(sysId);
				} else if (stypeId.equalsIgnoreCase("host")) {
					if (subtypeId.equals("Physical")) {
						rows = ts.getSubgrouphost(sysId, "hypervisor_id", "hypervisor_id", "name", "t_res_hypervisor");
					} else if (subtypeId.equals("Virtual")) {
						rows = ts.getSubgrouphost(sysId, "hypervisor_id", "vm_id", "name", "t_res_virtualmachine");
					}
				} else {
					if (subtypeId.equalsIgnoreCase("storage")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "subsystem_id", "the_display_name","v_res_storage_subsystem");
					} else if (subtypeId.equalsIgnoreCase("node")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "redundancy_id", "the_display_name","V_RES_REDUNDANCY");
					} else if (subtypeId.equalsIgnoreCase("port")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "port_id", "the_display_name", "v_res_port");
					} else if (subtypeId.equalsIgnoreCase("mdiskgroup")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "pool_id", "the_display_name","v_res_storage_pool");
					} else if (subtypeId.equalsIgnoreCase("arraysite")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "disk_group_id", "the_display_name","v_res_arraysite");
					} else if (subtypeId.equalsIgnoreCase("mdisk")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id", "the_display_name","v_res_storage_extent");
					} else if (subtypeId.equalsIgnoreCase("rank")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id", "the_display_name","V_RES_STORAGE_RANK");
					} else if (subtypeId.equalsIgnoreCase("iogroup")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "io_group_id", "the_display_name","V_RES_STORAGE_IOGROUP");
					} else if (subtypeId.equalsIgnoreCase("controller")) {
						rows = ts.getSubgroupDevice(sysId, "dev_id", "ele_id", "ele_name", "PRF_TARGET_DSCONTROLLER");
					} else if (subtypeId.equalsIgnoreCase("switch")) {
						rows = ts.getSubgroupDevice(sysId, "switch_id", "switch_id", "the_display_name", "v_res_switch");
					} else if (subtypeId.equalsIgnoreCase("switchPort")) {
						rows = ts.getSubgroupDevice(sysId, "switch_id", "port_id", "the_display_name", "v_res_switch_port");
					} else if (subtypeId.equalsIgnoreCase("volume")) {
						rows = ts.getSubgroupDevice(sysId, "subsystem_id", "svid", "the_display_name","v_res_storage_volume");
					} else if (subtypeId.equalsIgnoreCase("app")) {
						rows = ts.getSubgroupDevice2(sysId, "fid", "fid", "fname", "tnapps", WebConstants.DB_DEFAULT);
					}
				}
				if (rows != null && rows.size() > 0) {
					JSONArray array = new JSONArray().fromObject(rows);
					obj.put("id", storageIds[i]);
					obj.put("configList", array);
					ary.add(obj.toString());
				}
			}
		}
		writetopage(ary);
	}

	@SuppressWarnings("static-access")
	public void doCheckAllDevice() {

		JSONObject json = new JSONObject();
		json.put("SVC", new JSONArray().fromObject(service.getStorageType("SVC")));
		json.put("BSP", new JSONArray().fromObject(service.getStorageType("BSP")));
		json.put("SWITCH", new JSONArray().fromObject(service.getSwitchList()));
		// json.put("EMC", new JSONArray().fromObject(service.getEMCStorage()));
		json.put("DS", new JSONArray().fromObject(service.getStorageType("DS")));
		json.put("HOST", new JSONArray().fromObject(service.getHostList()));
		json.put("APPLICATION", new JSONArray().fromObject(service.getAppList()));
		writetopage(json);
	}

	@SuppressWarnings("static-access")
	public void doGetPrfInfo() {

		// 设备
		JSONObject deviceList = new JSONObject();
		//deviceList.put("EMC", new JSONArray().fromObject(service.getEMCStorage()));
		deviceList.put("SVC", new JSONArray().fromObject(service.getStorageType("SVC")));
		deviceList.put("BSP", new JSONArray().fromObject(service.getStorageType("BSP")));
		deviceList.put("DS", new JSONArray().fromObject(service.getStorageType("DS")));
		deviceList.put("SWITCH", new JSONArray().fromObject(service.getSwitchList()));
		deviceList.put("HOST", new JSONArray().fromObject(service.getHostList()));
		deviceList.put("APPLICATION", new JSONArray().fromObject(service.getAppList()));
		// 组件
		JSONObject fnameList = new JSONObject();
		fnameList.put("SVC", new JSONArray().fromObject(service.getFnameList("SVC")));
		fnameList.put("BSP", new JSONArray().fromObject(service.getFnameList("BSP")));
		fnameList.put("DS", new JSONArray().fromObject(service.getFnameList("DS")));
		//fnameList.put("EMC", new JSONArray().fromObject(service.getFnameList("EMC")));
		fnameList.put("HOST", new JSONArray().fromObject(service.getFnameList("HOST")));
		fnameList.put("SWITCH", new JSONArray().fromObject(service.getFnameList("SWITCH")));
		fnameList.put("APPLICATION", new JSONArray().fromObject(service.getFnameList("APPLICATION")));
		// 性能指标
		JSONObject fprfidList = new JSONObject();
		fprfidList.put("SVC", new JSONArray().fromObject(service.getFprffildList("SVC")));
		fprfidList.put("BSP", new JSONArray().fromObject(service.getFprffildList("BSP")));
		fprfidList.put("DS", new JSONArray().fromObject(service.getFprffildList("DS")));
		//fprfidList.put("EMC", new JSONArray().fromObject(service.getFprffildList("EMC")));
		fprfidList.put("HOST", new JSONArray().fromObject(service.getFprffildList("HOST")));
		fprfidList.put("SWITCH", new JSONArray().fromObject(service.getFprffildList("SWITCH")));
		fprfidList.put("APPLICATION", new JSONArray().fromObject(service.getFprffildList("APPLICATION")));

		JSONObject obj = new JSONObject();
		obj.put("fprfidList", fprfidList);
		obj.put("fnameList", fnameList);
		obj.put("deviceList", deviceList);
		writetopage(obj);
	}

	public ActionResult doReviewContent() {

		String jsonStr = getStrParameter("jsonStr").replaceAll("&amp;quot;", "\"").replaceAll("&amp;nbsp;", " ");
		JSONObject json = JSONObject.fromObject(jsonStr);
		JSONArray aFormArray = json.getJSONArray("cForm");
		JSONArray timeFormArray = json.getJSONArray("timeForm");
		JSONArray cZnode = json.getJSONArray("cZnode");
		JSONArray pZnode = json.getJSONArray("pZnode");
		JSONArray tZnode = json.getJSONArray("tZnode");
		JSONArray aZnode = json.getJSONArray("aZnode");
		String start = "";
		String end = "";
		String reportType = "";
		String reportName = "";
		String timescopeType = ""; // 0最近时间段 1固定时间段
		String timeLength = "";
		String timeType = "";
		String startTime = "";
		String endTime = "";
		String exeType1 = "";
		String exeType2 = "";
		String exeType3 = "";
		for (Object obj : aFormArray) {
			JSONObject jsonVal = JSONObject.fromObject(obj);
			if (jsonVal.getString("name").equals("report_type")) {
				reportType = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("report_name")) {
				reportName = jsonVal.getString("value").replaceAll("&amp;nbsp;", " ");
			}

		}
		for (Object obj : timeFormArray) {
			JSONObject jsonVal = JSONObject.fromObject(obj);
			if (jsonVal.getString("name").equals("exeType1")) {
				exeType1 = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("exeType2")) {
				exeType2 = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("exeType3")) {
				exeType3 = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("timescope_type")) {
				timescopeType = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("time_type")) {
				timeType = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("time_length")) {
				timeLength = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("startTime")) {
				startTime = jsonVal.getString("value").replaceAll("&map;nbsp", " ");
			} else if (jsonVal.getString("name").equals("endTime")) {
				endTime = jsonVal.getString("value").replaceAll("&map;nbsp", " ");
			}
		}
		Calendar ca = Calendar.getInstance();
		if (reportType.equals("0")) {
			reportName += "报表";
			if (timescopeType.equals("0")) {
				ca.setTime(new Date());
				if (timeType.equals("day")) {
					ca.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(timeLength));
				} else if (timeType.equals("month")) {
					ca.add(Calendar.MONTH, -Integer.parseInt(timeLength));
				} else if (timeType.equals("year")) {
					ca.add(Calendar.MONTH, -Integer.parseInt(timeLength));
				}
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ca.getTime());
			} else {
				start = startTime;
				end = endTime;
			}
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			if (exeType1.equals("day")) {
				reportName += "日报";
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(exeType2));
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				c.add(Calendar.DAY_OF_MONTH, -1);
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
			} else if (exeType1.equals("month")) {
				reportName += "月报";
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(exeType3));
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				c.add(Calendar.MONTH, -1);
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
			} else if (exeType1.equals("year")) {
				reportName += "年报";
				c.set(Calendar.MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType3));
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				c.add(Calendar.YEAR, -1);
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
			}
		}
		this.setAttribute("reportName", reportName);
		this.setAttribute("logoImg", WebConstants.REPORT_LOGO_URL);
		this.setAttribute("startTime", start);
		this.setAttribute("endTime", end);
		this.setAttribute("cZnode", cZnode);
		this.setAttribute("tZnode", tZnode);
		this.setAttribute("pZnode", pZnode);
		this.setAttribute("aZnode", aZnode);
		return new ActionResult("/WEB-INF/views/report/review.jsp");
	}
	
	/**
	 * 得到任务报表配置信息
	 */
	@SuppressWarnings("static-access")
	public void doTaskReportConfig(){
		ReportMaker mk = new ReportMaker();
		String exeType1 = getStrParameter("timeType");
		DataRow row = ts.getTaskReportConfig(exeType1);
		//内置任务报表信息
		DataRow dataRow = mk.doInitReport(row);
		JSONArray cZnode = new JSONArray().fromObject(dataRow.getString("device_array"));
		JSONArray pZnode = new JSONArray().fromObject(dataRow.getString("perf_array"));
		JSONArray tZnode = new JSONArray().fromObject(dataRow.getString("topn_array"));
		JSONArray aZnode = new JSONArray().fromObject(dataRow.getString("alert_array"));
		JSONObject json = new JSONObject();
		json.put("cZnode", cZnode);
		json.put("pZnode", pZnode);
		json.put("tZnode", tZnode);
		json.put("aZnode", aZnode);
		json.put("exe_type1", dataRow.getString("exe_type1"));
		json.put("exe_type2", dataRow.getString("exe_type2"));
		json.put("exe_type3", dataRow.getString("exe_type3"));
		writetopage(json);
	}
	
	public void doTaskReportChange(){
		String exeType1 = getStrParameter("timeType");
		if(exeType1.length()==0){
			exeType1 = "day";
		}
		JSONObject json = new JSONObject();
		DataRow row = ts.getTaskReportConfig(exeType1);
		json.put("exe_type1", row.getString("exe_type1"));
		json.put("exe_type2", row.getString("exe_type2"));
		json.put("exe_type3", row.getString("exe_type3"));
		writetopage(json);
	}

	public void doAddReport() {
		String jsonStr = getStrParameter("jsonStr").replaceAll("&amp;quot;", "\"").replaceAll("&amp;nbsp;", " ");
		JSONObject json = JSONObject.fromObject(jsonStr);
		JSONArray aFormArray = json.getJSONArray("cForm");
		JSONArray timeFormArray = json.getJSONArray("timeForm");
		String cZnode = json.getString("cZnode");
		String pZnode = json.getString("pZnode");
		String tZnode = json.getString("tZnode");
		String aZnode = json.getString("aZnode");
		// JSONArray cZnode = json.getJSONArray("cZnode");
		// JSONArray pZnode = json.getJSONArray("pZnode");
		// JSONArray tZnode = json.getJSONArray("tZnode");
		// JSONArray aZnode = json.getJSONArray("aZnode");
		String start = "";
		String end = "";
		String realName = "";
		String subReal = UUID.randomUUID().toString();
		String reportType = "";
		String reportName = "";
		String timescopeType = ""; // 0最近时间段 1固定时间段
		String timeLength = "";
		String timeType = "";
		String startTime = "";
		String endTime = "";
		String exeType1 = "";
		String exeType2 = "";
		String exeType3 = "";
		for (Object obj : aFormArray) {
			JSONObject jsonVal = JSONObject.fromObject(obj);
			if (jsonVal.getString("name").equals("report_type")) {
				reportType = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("report_name")) {
				reportName = jsonVal.getString("value").replaceAll("&amp;nbsp;", " ");
			}

		}
		for (Object obj : timeFormArray) {
			JSONObject jsonVal = JSONObject.fromObject(obj);
			if (jsonVal.getString("name").equals("exeType1")) {
				exeType1 = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("exeType2")) {
				exeType2 = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("exeType3")) {
				exeType3 = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("timescope_type")) {
				timescopeType = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("time_type")) {
				timeType = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("time_length")) {
				timeLength = jsonVal.getString("value");
			} else if (jsonVal.getString("name").equals("startTime")) {
				startTime = jsonVal.getString("value").replaceAll("&map;nbsp", " ");
			} else if (jsonVal.getString("name").equals("endTime")) {
				endTime = jsonVal.getString("value").replaceAll("&map;nbsp", " ");
			}
		}
		Calendar ca = Calendar.getInstance();
		String exprDay = "";
		String exprMonth = "";
		String exprYear = "";
		if (reportType.equals("0")) {
			reportName += "报表";
			realName = "report/custom/" + subReal + ".htm";
			if (timescopeType.equals("0")) {
				ca.setTime(new Date());
				if (timeType.equals("day")) {
					ca.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(timeLength));
				} else if (timeType.equals("month")) {
					ca.add(Calendar.MONTH, -Integer.parseInt(timeLength));
				} else if (timeType.equals("year")) {
					ca.add(Calendar.YEAR, -Integer.parseInt(timeLength));
				}
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ca.getTime());
			} else {
				start = startTime;
				end = endTime;
			}
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());

			if (exeType1.equals("day")) {
				timeType = "day";
				reportName += "日报";
				realName = "report/day/" + subReal + ".htm";
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(exeType2));
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				c.add(Calendar.DAY_OF_MONTH, -1);
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				// 每天 exeType2 点执行一次
				exprDay = "0 0 " + Integer.parseInt(exeType2) + " * * ?";
			} else if (exeType1.equals("month")) {
				timeType = "month";
				reportName += "月报";
				realName = "report/month/" + subReal + ".htm";
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(exeType3));
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				c.add(Calendar.MONTH, -1);
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				// 每月exeType2号exeType3点执行一次
				exprMonth = "0 0 " + Integer.parseInt(exeType3) + " " + Integer.parseInt(exeType2) + " * ?";
			} else if (exeType1.equals("year")) {
				timeType = "year";
				reportName += "年报";
				realName = "report/year/" + subReal + ".htm";
				c.set(Calendar.MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType3));
				end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				c.add(Calendar.YEAR, -1);
				start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
				// 每年exeType3日exeType2月 凌晨1 点 执行一次
				exprYear = "0 0 1 " + Integer.parseInt(exeType3) + " " + Integer.parseInt(exeType2) + " ?";
			}
		}
		DataRow row = new DataRow();
		row.set("real_name", realName);
		row.set("the_display_name", reportName);
		row.set("report_logo_url", WebConstants.REPORT_LOGO_URL);
		row.set("report_type", reportType);
		row.set("exe_type1", exeType1);
		row.set("exe_type2", exeType2);
		row.set("exe_type3", exeType3);
		row.set("timescope_type", timescopeType);
		row.set("time_length", timeLength);
		row.set("time_type", timeType);
		row.set("starttime", start);
		row.set("endtime", end);
		row.set("device_array", cZnode);
		row.set("perf_array", pZnode);
		row.set("topn_array", tZnode);
		row.set("alert_array", aZnode);
		row.set("create_time", new Date());
		JSONObject res = new JSONObject();

		try {
			if (reportType.equals("0")) {

				if (rm.doReportFtl(row, res)) {
					int id = ts.addReport(row);
					res.put("reportId", id);
					res.put("res", "true");
				} else {
					res.put("res", "false");
				}

			} else {
				Scheduler hander = sf.getScheduler();
				JobDetail jd = null;
				CronTrigger crontrigger = null;
				Date date = null;
				
				if(StringHelper.isNotEmpty(exprDay)){
					if (hander.getJobDetail("reportJobDay", "groupDay") != null) {
						jd = hander.getJobDetail("reportJobDay", "groupDay");
						crontrigger = (CronTrigger) hander.getTrigger("reportTriggerDay", "groupDay");
						removeJob(jd.getName(), jd.getGroup(), crontrigger.getName(), crontrigger.getGroup());

					}
					jd = new JobDetail("reportJobDay", "groupDay", ReportJob.class);
					jd.getJobDataMap().put("row", row);
					jd.getJobDataMap().put("msg", res);
					crontrigger = new CronTrigger("reportTriggerDay", "groupDay", exprDay);
					date = hander.scheduleJob(jd, crontrigger);
					hander.start();
				}
				if(StringHelper.isNotEmpty(exprMonth)){
					if (hander.getJobDetail("reportJobMonth", "groupMonth") != null) {
						jd = hander.getJobDetail("reportJobMonth", "groupMonth");
						crontrigger = (CronTrigger) hander.getTrigger("reportTriggerMonth", "groupMonth");
						removeJob(jd.getName(), jd.getGroup(), crontrigger.getName(), crontrigger.getGroup());

					}
					jd = new JobDetail("reportJobMonth", "groupMonth", ReportJob.class);
					jd.getJobDataMap().put("row", row);
					jd.getJobDataMap().put("msg", res);
					crontrigger = new CronTrigger("reportTriggerMonth", "groupMonth", exprMonth);
					date = hander.scheduleJob(jd, crontrigger);
					hander.start();
				}
				if(StringHelper.isNotEmpty(exprYear)){
					if (hander.getJobDetail("reportJobYear", "groupYear") != null) {
						jd = hander.getJobDetail("reportJobYear", "groupYear");
						crontrigger = (CronTrigger) hander.getTrigger("reportTriggerYear", "groupYear");
						removeJob(jd.getName(), jd.getGroup(), crontrigger.getName(), crontrigger.getGroup());

					}
					jd = new JobDetail("reportJobYear", "groupYear", ReportJob.class);
					jd.getJobDataMap().put("row", row);
					jd.getJobDataMap().put("msg", res);
					crontrigger = new CronTrigger("reportTriggerYear", "groupYear", exprYear);
					date = hander.scheduleJob(jd, crontrigger);
					hander.start();
				}
				DataRow reportConData= ts.getByTimeType(timeType);
				if(reportConData!=null){
					row.set("id", reportConData.getString("id"));
					ts.updateReportTaskConfig(row);
				}else{
					ts.addReportTaskConfig(row);
				}
				res.put("res", "true");
				logger.info(jd.getFullName() + "has been scheduled to run at:"
						+ DateHelper.formatTime(date) + " and excute based on expression: "
						+ crontrigger.getCronExpression());

			}

		} catch (Exception e) {
			e.printStackTrace();
			res.put("msg", e.getStackTrace());
		}
		writetopage(res);
	}

	public ActionResult doReportPage() {

		DBPage page = null;
		int curPage = 1;
		int numPerPage = WebConstants.NumPerPage;
		page = ts.getReportPage(null, null, null, null, curPage, numPerPage);
		this.setAttribute("reportPage", page);
		return new ActionResult("/WEB-INF/views/report/reportList.jsp");
	}

	public void doDelReport() {

		Integer id = getIntParameter("id");
		DataRow row = ts.getReportInfo(id);
		if (id != null && id > 0) {
			String path = getRequest().getSession().getServletContext().getRealPath(row.getString("real_name"));
			File file = new File(path);
			boolean isright = false;
			if (file.exists()) {
				isright = delDir(file);
				ts.delReport(id);
			} else {
				ts.delReport(id);
				isright = true;
			}
			if (isright) {
				ts.delReport(id);
				writetopage("true");
			} else {
				writetopage("false");
			}
		}
	}

	public ActionResult doAjaxReportPage() {

		DBPage page = null;
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String reportType = getStrParameter("reportType");
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		page = ts.getReportPage(name, reportType, startTime, endTime, curPage, numPerPage);
		this.setAttribute("reportPage", page);
		this.setAttribute("name", name);
		this.setAttribute("reportType", reportType);
		this.setAttribute("startTime", startTime);
		this.setAttribute("endTime", endTime);
		return new ActionResult("/WEB-INF/views/report/ajaxReport.jsp");
	}

	public ActionResult doReportInfo() {

		Integer id = getIntParameter("id");
		DataRow row = ts.getReportInfo(id);
		return new ActionResult(row.getString("real_name"), false);
	}

	public ActionResult doDownload() throws Exception {

		Integer id = getIntParameter("id");
		DataRow row = ts.getReportInfo(id);
		String displayName = row.getString("the_display_name");
		// displayName = new String(displayName.getBytes("ISO-8859-1"),"UTF-8");
		// 获得servletContext
		ServletContext sc = getRequest().getSession().getServletContext();

		String packDir = sc.getRealPath("/packDir/temp");

		File dir = createDir(packDir);
		// 创建压缩文件的路径
		StringBuffer sf = new StringBuffer(sc.getRealPath(row.getString("real_name")));
		String filePath = sf.toString().substring(0, sf.toString().lastIndexOf("\\"));

		try {
			// 将静态页面复制到压缩目录里
			copyFile(sf.toString(), packDir + "/" + displayName + ".htm");
			copyFolder(filePath + "/js", packDir + "/js");
			copyFolder(filePath + "/css", packDir + "/css");

			String path = sc.getRealPath("/packDir/" + displayName + ".zip");
			zip(packDir, path);

			File file = new File(path);
			// 取得文件名。
			String filename = displayName + ".zip";
			// 取得文件的后缀名。
			// String ext = filename.substring(filename.lastIndexOf(".") +
			// 1).toUpperCase();

			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			getResponse().reset();
			// 设置response的Header
			getResponse().addHeader(
					"Content-Disposition",
					"attachment;filename="
							+ new String(filename.getBytes("utf-8"), "ISO-8859-1"));
			getResponse().addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(getResponse()
					.getOutputStream());
			getResponse().setContentType("application/octet-stream");// x-download
			getResponse().setCharacterEncoding("UTF-8");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
			// 删除压缩包目录
			delDir(new File(sc.getRealPath("/packDir")));
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// 创建文件夹
	private File createDir(String path) {

		File dirFile = null;
		try {
			dirFile = new File(path);
			if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
				dirFile.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dirFile;
	}

	/**
	 * 压缩。
	 * 
	 * @param src
	 *            源文件或者目录
	 * @param dest
	 *            压缩文件路径
	 * @throws IOException
	 */
	public void zip(String src, String dest) throws IOException {

		ZipOutputStream out = null;
		try {
			File outFile = new File(dest);
			out = new ZipOutputStream(outFile);
			File fileOrDirectory = new File(src);

			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory, "");
			} else {
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], "");
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	/**
	 * 递归压缩文件或目录
	 * 
	 * @param out
	 *            压缩输出流对象
	 * @param fileOrDirectory
	 *            要压缩的文件或目录对象
	 * @param curPath
	 *            当前压缩条目的路径，用于指定条目名称的前缀
	 * @throws IOException
	 */
	private void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory,
			String curPath) throws IOException {

		FileInputStream in = null;
		try {
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);

				ZipEntry entry = new ZipEntry(curPath
						+ fileOrDirectory.getName());
				out.putNextEntry(entry);

				while ((bytes_read = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}
				out.closeEntry();
			} else {
				// 压缩目录
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], curPath
							+ fileOrDirectory.getName() + "/");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {

		int bytesum = 0;
		int byteread = 0;
		File oldfile = new File(oldPath);
		if (oldfile.exists()) { // 文件存在时
			try {
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				abort("没有这样的源文件: " + oldfile.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();
		}

	}

	// 删除文件及文件夹
	private boolean delDir(File folder) {

		boolean result = false;
		try {
			String childs[] = folder.list();
			if (childs == null || childs.length <= 0) {
				if (folder.delete()) {
					result = true;
				}
			} else {
				for (int i = 0; i < childs.length; i++) {
					String childName = childs[i];
					String childPath = folder.getPath() + File.separator
							+ childName;
					File filePath = new File(childPath);
					if (filePath.exists() && filePath.isFile()) {
						if (filePath.delete()) {
							result = true;
						} else {
							result = false;
							break;
						}
					} else if (filePath.exists() && filePath.isDirectory()) {
						if (delDir(filePath)) {
							result = true;
						} else {
							result = false;
							break;
						}
					}
				}
			}
			folder.delete();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	private static void abort(String msg) throws IOException {

		throw new IOException("文件复制： " + msg);
	}

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

	/**
	 * 移除一个任务
	 * 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @throws SchedulerException
	 */
	public void removeJob(String jobName, String jobGroupName,
			String triggerName, String triggerGroupName)
			throws SchedulerException {

		Scheduler sched = sf.getScheduler();
		sched.pauseTrigger(triggerName, triggerGroupName);// 停止触发器
		sched.unscheduleJob(triggerName, triggerGroupName);// 移除触发器
		sched.deleteJob(jobName, jobGroupName);// 删除任务
		logger.info("pauseTrigger: " + triggerName + " unscheduleJob: "
				+ triggerName + " deleteJob: " + jobName);
	}

}
