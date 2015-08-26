package root.report;

import java.awt.Insets;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
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
import org.zefer.pd4ml.PD4Constants;
import org.zefer.pd4ml.PD4ML;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.UUID;
import com.huiming.service.report.ReportService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class ReportAction extends SecurityAction {

	private static Logger logger = Logger.getLogger(ReportAction.class);
	SchedulerFactory sf = new StdSchedulerFactory();
	ReportService reportService = new ReportService();
	ReportMaker rm = new ReportMaker();

	public static final String STORAGE_OPT = "[{id:'storage',name:'存储系统'},{id:'node',name:'冗余节点'}," +
		"{id:'port',name:'端口'},{id:'volume',name:'卷'}," +
		"{id:'mdiskgroup',name:'存储池'},{id:'disk',name:'磁盘'}," +
		"{id:'arrayset',name:'阵列'},{id:'mdisk',name:'存储扩展'}," +
		"{id:'rank',name:'Rank'},{id:'iogroup',name:'IOGroup'}]";
	public static final String SWITCH_OPT = "[{id:'switch',name:'交换机'},{id:'switchPort',name:'交换机端口'}]";
	public static final String EMC_OPT = "[{id:'storage',name:'存储系统'},{id:'diskgroup',name:'磁盘组'}," +
		"{id:'hostgroup',name:'存储关系组'},{id:'port',name:'端口'}," +
		"{id:'node',name:'冗余节点'},{id:'volume',name:'卷'}," +
		"{id:'pool',name:'存储池'}]";
	public static final String HDS_OPT = "[{id:'storage',name:'存储系统'},{id:'diskgroup',name:'磁盘组'}," +
		"{id:'hostgroup',name:'存储关系组'},{id:'port',name:'端口'}," +
		"{id:'node',name:'冗余节点'},{id:'volume',name:'卷'}," +
		"{id:'pool',name:'存储池'}]";
	public static final String NETAPP_OPT = "[{id:'storage',name:'存储系统'}," +
	"{id:'port',name:'端口'},{id:'node',name:'冗余节点'}," +
	"{id:'volume',name:'卷'},{id:'pool',name:'存储池'}]";
	public static final String HOST_OPT = "[{id:'physical',name:'物理机'},{id:'virtual',name:'虚拟机'}]";
	public static final String APP_OPT = "[{id:'app',name:'应用程序'}]";
	
	/**
	 * 配置信息加载的设备列表
	 * @return
	 */
	public ActionResult doCustomReport() {
		//获取用户可见设备
		String hostLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String srStoLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		String tpcStoLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		//获取设备列表
		JSONObject deviceList = new JSONObject();
		deviceList.put(SrContant.DEVTYPE_VAL_HOST, JSONArray.fromObject(reportService.getHostList(hostLimitIds)));
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			deviceList.put(SrContant.DEVTYPE_VAL_SVC, JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_SVC,tpcStoLimitIds)));
			deviceList.put(SrContant.DEVTYPE_VAL_BSP, JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_BSP,tpcStoLimitIds)));
			deviceList.put(SrContant.DEVTYPE_VAL_DS, JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_DS,tpcStoLimitIds)));
			deviceList.put(SrContant.DEVTYPE_VAL_SWITCH, JSONArray.fromObject(reportService.getSwitchList(switchLimitIds)));
		}
		deviceList.put(SrContant.DEVTYPE_VAL_EMC, JSONArray.fromObject(reportService.getStorageByType(SrContant.DEVTYPE_VAL_EMC,srStoLimitIds)));
		deviceList.put(SrContant.DEVTYPE_VAL_HDS, JSONArray.fromObject(reportService.getStorageByType(SrContant.DEVTYPE_VAL_HDS,srStoLimitIds)));
		deviceList.put(WebConstants.STORAGE_TYPE_VAL_NETAPP, JSONArray.fromObject(reportService.getStorageByType(WebConstants.STORAGE_TYPE_VAL_NETAPP,srStoLimitIds)));
//		JSONArray.fromObject(arg0)
		//获取部件
		JSONObject fnameList = new JSONObject();
		fnameList.put(SrContant.DEVTYPE_VAL_HOST, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_HOST)));
		fnameList.put(SrContant.DEVTYPE_VAL_SVC, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_SVC)));
		fnameList.put(SrContant.DEVTYPE_VAL_BSP, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_BSP)));
		fnameList.put(SrContant.DEVTYPE_VAL_DS, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_DS)));
		fnameList.put(SrContant.DEVTYPE_VAL_EMC, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_EMC)));
		fnameList.put(SrContant.DEVTYPE_VAL_HDS, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_HDS)));
		fnameList.put(WebConstants.STORAGE_TYPE_VAL_NETAPP, JSONArray.fromObject(reportService.getFnameList(WebConstants.STORAGE_TYPE_VAL_NETAPP)));
		fnameList.put(SrContant.DEVTYPE_VAL_SWITCH, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_SWITCH)));
		
		//获取性能指标
		JSONObject fprfidList = new JSONObject();
		fprfidList.put(SrContant.DEVTYPE_VAL_HOST, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_HOST)));
		fprfidList.put(SrContant.DEVTYPE_VAL_SVC, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_SVC)));
		fprfidList.put(SrContant.DEVTYPE_VAL_BSP, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_BSP)));
		fprfidList.put(SrContant.DEVTYPE_VAL_DS, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_DS)));
		fprfidList.put(SrContant.DEVTYPE_VAL_EMC, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_EMC)));
		fprfidList.put(SrContant.DEVTYPE_VAL_HDS, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_HDS)));
		fprfidList.put(WebConstants.STORAGE_TYPE_VAL_NETAPP, JSONArray.fromObject(reportService.getFprffildList(WebConstants.STORAGE_TYPE_VAL_NETAPP)));
		fprfidList.put(SrContant.DEVTYPE_VAL_SWITCH, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_SWITCH)));

		this.setAttribute("fprfidList", fprfidList);
		this.setAttribute("fnameList", fnameList);
		this.setAttribute("deviceList", deviceList);
		this.setAttribute("editTask", "null");
		return new ActionResult("/WEB-INF/views/report/editReport.jsp");
	}

	/**
	 * 组件类型的列表信息
	 */
	public void doDeviceTypeChange() {
		//获取用户可见设备
		String hostLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String srStoLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		String tpcStoLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		JSONObject obj = new JSONObject();
		JSONArray jsArray = null;
		String type = getStrParameter("type");
		//HOST
		if (type.equals(SrContant.DEVTYPE_VAL_HOST)) {
			jsArray = JSONArray.fromObject(reportService.getHostList(hostLimitIds));
			obj.put("subOption", HOST_OPT);
		//SVC
		} else if (type.equals(SrContant.DEVTYPE_VAL_SVC)) {
			//判断是否有TPC配置
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				jsArray = JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_SVC,tpcStoLimitIds));
			}
			obj.put("subOption", STORAGE_OPT);
		//BSP
		} else if (type.equals(SrContant.DEVTYPE_VAL_BSP)) {
			//判断是否有TPC配置
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				jsArray = JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_BSP,tpcStoLimitIds));
			}
			obj.put("subOption", STORAGE_OPT);
		//DS
		} else if (type.equals(SrContant.DEVTYPE_VAL_DS)) {
			//判断是否有TPC配置
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				jsArray = JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_DS,tpcStoLimitIds));
			}
			obj.put("subOption", STORAGE_OPT);
		//EMC
		} else if (type.equals(SrContant.DEVTYPE_VAL_EMC)) {
			jsArray = JSONArray.fromObject(reportService.getStorageByType(SrContant.DEVTYPE_VAL_EMC,srStoLimitIds));
			obj.put("subOption", EMC_OPT);
		//HDS
		} else if (type.equals(SrContant.DEVTYPE_VAL_HDS)) {
			jsArray = JSONArray.fromObject(reportService.getStorageByType(SrContant.DEVTYPE_VAL_HDS,srStoLimitIds));
			obj.put("subOption", HDS_OPT);
		//NETAPP
		} else if (type.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			jsArray = JSONArray.fromObject(reportService.getStorageByType(WebConstants.STORAGE_TYPE_VAL_NETAPP, srStoLimitIds));
			obj.put("subOption", NETAPP_OPT);
		//SWITCH
		} else if (type.equals(SrContant.DEVTYPE_VAL_SWITCH)) {
			//判断是否有TPC配置
			if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
				jsArray = JSONArray.fromObject(reportService.getSwitchList(switchLimitIds));
			}
			obj.put("subOption", SWITCH_OPT);
		}
		obj.put("devOption", jsArray);
		writetopage(obj);
	}

	/**
	 * 组件列表信息
	 */
	public void doGetsubgroupDev() {
		//获取用户可见设备
		String physLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String vmLimitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String srStoLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		String tpcStoLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		//设备类型
		String stypeId = getStrParameter("stypeId"); 
		//部件类型
		String subtypeId = getStrParameter("subtypeId"); 
		String b = getStrParameter("subId");
		String[] storageIds = b.split(",");
		JSONArray ary = new JSONArray();
		if (storageIds[0].length() > 0) {
			for (int i = 0; i < storageIds.length; i++) {
				int sysId = Integer.parseInt(storageIds[i]);
				List<DataRow> rows = null;
				JSONObject obj = new JSONObject();
				//For EMC/HDS/NETAPP
				if (stypeId.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC) 
						|| stypeId.equals(SrContant.DEVTYPE_VAL_HDS)
						|| stypeId.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
					//For Storage
					if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
						rows = reportService.getSRSubgroupDevice(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_ID, SrTblColConstant.RSS_SUBSYSTEM_NAME, SrTblColConstant.TBL_RES_STORAGESUBSYSTEM, srStoLimitIds);
					//For DiskGroup
					} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_DISKGROUP)) {
						rows = reportService.getSRSubgroupDevice(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RDG_DISKGROUP_ID, SrTblColConstant.RDG_DISKGROUP_NAME, SrTblColConstant.TBL_RES_DISKGROUP);
					//For HostGroup
					} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_HOSTGROUP)) {
						rows = reportService.getSRSubgroupDevice(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RHG_HOSTGROUP_ID, SrTblColConstant.RHG_HOSTGROUP_NAME, SrTblColConstant.TBL_RES_HOSTGROUP);
					//For Port
					} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
						rows = reportService.getSRSubgroupDevice(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RP_PORT_ID, SrTblColConstant.RP_NAME, SrTblColConstant.TBL_RES_PORT);
					//For Node
					} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
						rows = reportService.getSRSubgroupDevice(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSN_SP_ID, SrTblColConstant.RSN_SP_NAME, SrTblColConstant.TBL_RES_STORAGENODE);
					//For Volume
					} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
						rows = reportService.getSRSubgroupDevice(sysId, SrTblColConstant.REF_SUBSYSTEM_ID, SrTblColConstant.RSV_VOLUME_ID, SrTblColConstant.RSV_NAME, SrTblColConstant.TBL_RES_STORAGEVOLUME);
					}
				//For SVC/BSP/DS
				} else if (stypeId.equalsIgnoreCase(SrContant.DEVTYPE_VAL_BSP) 
						|| stypeId.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SVC) 
						|| stypeId.equalsIgnoreCase(SrContant.DEVTYPE_VAL_DS)) {
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "subsystem_id", "the_display_name","v_res_storage_subsystem",tpcStoLimitIds);
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "redundancy_id", "the_display_name","V_RES_REDUNDANCY");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "port_id", "the_display_name", "v_res_port");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_MDISKGROUP)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "pool_id", "the_display_name","v_res_storage_pool");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_ARRAYSITE)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "disk_group_id", "the_display_name","v_res_arraysite");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_MDISK)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id", "the_display_name","v_res_storage_extent");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_RANK)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "storage_extent_id", "the_display_name","V_RES_STORAGE_RANK");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_IOGROUP)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "io_group_id", "the_display_name","V_RES_STORAGE_IOGROUP");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_CONTROLLER)) {
							rows = reportService.getSubgroupDevice(sysId, "dev_id", "ele_id", "ele_name", "PRF_TARGET_DSCONTROLLER");
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
							rows = reportService.getSubgroupDevice(sysId, "subsystem_id", "svid", "the_display_name","v_res_storage_volume");
						}
					}
				//For SWITCH
				} else if (stypeId.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SWITCH)) {
					//判断是否有TPC配置
					if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
						if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCH)) {
							rows = reportService.getSubgroupDevice(sysId, "switch_id", "switch_id", "the_display_name", "v_res_switch", switchLimitIds);
						} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCHPORT)) {
							rows = reportService.getSubgroupDevice(sysId, "switch_id", "port_id", "the_display_name", "v_res_switch_port");
						}
					}
				//For HOST
				} else if (stypeId.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HOST)) {
					if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
						rows = reportService.getSubgrouphost(sysId, "hypervisor_id", "hypervisor_id", "name", "t_res_hypervisor", physLimitIds);
					} else if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)
							|| subtypeId.equalsIgnoreCase(WebConstants.DEVTYPE_HYPERVISOR)) {
						rows = reportService.getSubgrouphost(sysId, "hypervisor_id", "vm_id", "name", "t_res_virtualmachine", vmLimitIds);
					}
				//For APPLICAITON
				} else if (stypeId.equalsIgnoreCase(SrContant.DEVTYPE_VAL_APPLICATION)) {
					if (subtypeId.equalsIgnoreCase(SrContant.SUBDEVTYPE_APP)) {
						rows = reportService.getSubgroupDevice2(sysId, "fid", "fid", "fname", "tnapps", WebConstants.DB_DEFAULT);
					}
				}
				if (rows != null && rows.size() > 0) {
					JSONArray array = JSONArray.fromObject(rows);
					obj.put("id", storageIds[i]);
					obj.put("configList", array);
					ary.add(obj.toString());
				}
			}
		}
		writetopage(ary);
	}

	/**
	 * 全选所有设备
	 */
	public void doCheckAllDevice() {
		//获取用户可见设备
		String hostLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String srStoLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		String tpcStoLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		JSONObject json = new JSONObject();
		//获取显示设备
		String devTypesStr = getRequest().getParameter("devTypes");
		String[] devTypes = devTypesStr.replaceFirst(",", "").split(",");
		for (int i = 0; i < devTypes.length; i++) {
			String devType = devTypes[i];
			//SVC,BSP,DS,SWITCH需要检查是否有TPC配置
			if (devType.equals(SrContant.DEVTYPE_VAL_SVC) 
					|| devType.equals(SrContant.DEVTYPE_VAL_BSP)
					|| devType.equals(SrContant.DEVTYPE_VAL_DS)
					|| devType.equals(SrContant.DEVTYPE_VAL_SWITCH)) {
				//判断是否有TPC配置
				if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
					if (devType.equals(SrContant.DEVTYPE_VAL_SWITCH)) {
						json.put(SrContant.DEVTYPE_VAL_SWITCH, JSONArray.fromObject(reportService.getSwitchList(switchLimitIds)));
					} else {
						json.put(devType, JSONArray.fromObject(reportService.getStorageType(devType,tpcStoLimitIds)));
					}
				}
			//For HOST
			} else if (devType.equals(SrContant.DEVTYPE_VAL_HOST)) {
				json.put(SrContant.DEVTYPE_VAL_HOST, JSONArray.fromObject(reportService.getHostList(hostLimitIds)));
			//For EMC,HDS,NETAPP
			} else if (devType.equals(SrContant.DEVTYPE_VAL_EMC)
					|| devType.equals(SrContant.DEVTYPE_VAL_HDS)
					|| devType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
				json.put(devType, JSONArray.fromObject(reportService.getStorageByType(devType,srStoLimitIds)));
			}
		}
		writetopage(json);
	}

	public void doGetPrfInfo() {
		//获取用户可见设备
		String hostLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		String srStoLimitIds = (String) getSession().getAttribute(WebConstants.SR_STORAGE_LIST);
		String tpcStoLimitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);

		//获取设备列表
		JSONObject deviceList = new JSONObject();
		deviceList.put(SrContant.DEVTYPE_VAL_HOST, JSONArray.fromObject(reportService.getHostList(hostLimitIds)));
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			deviceList.put(SrContant.DEVTYPE_VAL_SVC, JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_SVC,tpcStoLimitIds)));
			deviceList.put(SrContant.DEVTYPE_VAL_BSP, JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_BSP,tpcStoLimitIds)));
			deviceList.put(SrContant.DEVTYPE_VAL_DS, JSONArray.fromObject(reportService.getStorageType(SrContant.DEVTYPE_VAL_DS,tpcStoLimitIds)));
			deviceList.put(SrContant.DEVTYPE_VAL_SWITCH, JSONArray.fromObject(reportService.getSwitchList(switchLimitIds)));
		}
		deviceList.put(SrContant.DEVTYPE_VAL_EMC, JSONArray.fromObject(reportService.getStorageByType(SrContant.DEVTYPE_VAL_EMC,srStoLimitIds)));
		deviceList.put(SrContant.DEVTYPE_VAL_HDS, JSONArray.fromObject(reportService.getStorageByType(SrContant.DEVTYPE_VAL_HDS,srStoLimitIds)));
		deviceList.put(WebConstants.STORAGE_TYPE_VAL_NETAPP, JSONArray.fromObject(reportService.getStorageByType(WebConstants.STORAGE_TYPE_VAL_NETAPP,srStoLimitIds)));
		
		//获取部件
		JSONObject fnameList = new JSONObject();
		fnameList.put(SrContant.DEVTYPE_VAL_HOST, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_HOST)));
		fnameList.put(SrContant.DEVTYPE_VAL_SVC, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_SVC)));
		fnameList.put(SrContant.DEVTYPE_VAL_BSP, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_BSP)));
		fnameList.put(SrContant.DEVTYPE_VAL_DS, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_DS)));
		fnameList.put(SrContant.DEVTYPE_VAL_EMC, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_EMC)));
		fnameList.put(SrContant.DEVTYPE_VAL_HDS, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_HDS)));
		fnameList.put(WebConstants.STORAGE_TYPE_VAL_NETAPP, JSONArray.fromObject(reportService.getFnameList(WebConstants.STORAGE_TYPE_VAL_NETAPP)));
		fnameList.put(SrContant.DEVTYPE_VAL_SWITCH, JSONArray.fromObject(reportService.getFnameList(SrContant.DEVTYPE_VAL_SWITCH)));
		
		//获取性能指标
		JSONObject fprfidList = new JSONObject();
		fprfidList.put(SrContant.DEVTYPE_VAL_HOST, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_HOST)));
		fprfidList.put(SrContant.DEVTYPE_VAL_SVC, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_SVC)));
		fprfidList.put(SrContant.DEVTYPE_VAL_BSP, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_BSP)));
		fprfidList.put(SrContant.DEVTYPE_VAL_DS, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_DS)));
		fprfidList.put(SrContant.DEVTYPE_VAL_EMC, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_EMC)));
		fprfidList.put(SrContant.DEVTYPE_VAL_HDS, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_HDS)));
		fprfidList.put(WebConstants.STORAGE_TYPE_VAL_NETAPP, JSONArray.fromObject(reportService.getFprffildList(WebConstants.STORAGE_TYPE_VAL_NETAPP)));
		fprfidList.put(SrContant.DEVTYPE_VAL_SWITCH, JSONArray.fromObject(reportService.getFprffildList(SrContant.DEVTYPE_VAL_SWITCH)));

		JSONObject obj = new JSONObject();
		obj.put("fprfidList", fprfidList);
		obj.put("fnameList", fnameList);
		obj.put("deviceList", deviceList);
		writetopage(obj);
	}

	/**
	 * 报表内容预览
	 * @return
	 */
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
//			reportName += "报表";
			if (timescopeType.equals("0")) {
				ca.setTime(new Date());
				if (timeType.equals("day")) {
					ca.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(timeLength));
				} else if (timeType.equals("month")) {
					ca.add(Calendar.MONTH, -Integer.parseInt(timeLength));
				} else if (timeType.equals("year")) {
					ca.add(Calendar.MONTH, -Integer.parseInt(timeLength));
				}
				end = SrContant.DATE_FORMAT.format(new Date());
				start = SrContant.DATE_FORMAT.format(ca.getTime());
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
				end = SrContant.DATE_FORMAT.format(c.getTime());
				c.add(Calendar.DAY_OF_MONTH, -1);
				start = SrContant.DATE_FORMAT.format(c.getTime());
			} else if (exeType1.equals("month")) {
				reportName += "月报";
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(exeType3));
				end = SrContant.DATE_FORMAT.format(c.getTime());
				c.add(Calendar.MONTH, -1);
				start = SrContant.DATE_FORMAT.format(c.getTime());
			} else if (exeType1.equals("year")) {
				reportName += "年报";
				c.set(Calendar.MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType3));
				end = SrContant.DATE_FORMAT.format(c.getTime());
				c.add(Calendar.YEAR, -1);
				start = SrContant.DATE_FORMAT.format(c.getTime());
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
	public void doTaskReportConfig() {
		String exeType1 = getStrParameter("timeType");
		DataRow row = reportService.getTaskReportConfig(exeType1,getLoginUserId());
		//获取用户可见设备
		String tpcStoLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC, null);
		String hdsStoLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DEVTYPE_VAL_HDS, null);
		String emcStoLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DEVTYPE_VAL_EMC, null);
		String netAppStoLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, WebConstants.STORAGE_TYPE_VAL_NETAPP, null);
		String switchLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_SWITCH, null, null);
		String hostLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_PHYSICAL, null, null);
		DataRow paramRow = new DataRow();
		paramRow.set(ReportMaker.TPC_STORAGE_LIMIT, tpcStoLimitIds);
		paramRow.set(ReportMaker.HDS_STORAGE_LIMIT, hdsStoLimitIds);
		paramRow.set(ReportMaker.EMC_STORAGE_LIMIT, emcStoLimitIds);
		paramRow.set(ReportMaker.NETAPP_STORAGE_LIMIT, netAppStoLimitIds);
		paramRow.set(ReportMaker.SWITCH_LIMIT, switchLimitIds);
		paramRow.set(ReportMaker.HOST_LIMIT, hostLimitIds);
		//内置任务报表信息
		DataRow dataRow = rm.doInitReport(row,paramRow);
		JSONArray cZnode = JSONArray.fromObject(dataRow.getString("device_array"));
		JSONArray pZnode = JSONArray.fromObject(dataRow.getString("perf_array"));
		JSONArray tZnode = JSONArray.fromObject(dataRow.getString("topn_array"));
		JSONArray aZnode = JSONArray.fromObject(dataRow.getString("alert_array"));
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
		if (exeType1.length() == 0) {
			exeType1 = "day";
		}
		JSONObject json = new JSONObject();
		DataRow row = reportService.getTaskReportConfig(exeType1,getLoginUserId());
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
		//最近时间段 (0),固定时间段(1)
		String timescopeType = ""; 
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
				end = SrContant.DATE_FORMAT.format(new Date());
				start = SrContant.DATE_FORMAT.format(ca.getTime());
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
				end = SrContant.DATE_FORMAT.format(c.getTime());
				c.add(Calendar.DAY_OF_MONTH, -1);
				start = SrContant.DATE_FORMAT.format(c.getTime());
				// 每天 exeType2 点执行一次
				exprDay = "0 0 " + Integer.parseInt(exeType2) + " * * ?";
			} else if (exeType1.equals("month")) {
				timeType = "month";
				reportName += "月报";
				realName = "report/month/" + subReal + ".htm";
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(exeType3));
				end = SrContant.DATE_FORMAT.format(c.getTime());
				c.add(Calendar.MONTH, -1);
				start = SrContant.DATE_FORMAT.format(c.getTime());
				// 每月exeType2号exeType3点执行一次
				exprMonth = "0 0 " + Integer.parseInt(exeType3) + " " + Integer.parseInt(exeType2) + " * ?";
			} else if (exeType1.equals("year")) {
				timeType = "year";
				reportName += "年报";
				realName = "report/year/" + subReal + ".htm";
				c.set(Calendar.MONTH, Integer.parseInt(exeType2));
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeType3));
				end = SrContant.DATE_FORMAT.format(c.getTime());
				c.add(Calendar.YEAR, -1);
				start = SrContant.DATE_FORMAT.format(c.getTime());
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
		row.set("user_id", getLoginUserId());
		JSONObject res = new JSONObject();

		try {
			if (reportType.equals("0")) {
				if (rm.doReportFtl(row, res)) {
					int id = reportService.addReport(row);
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
				
				if (StringHelper.isNotEmpty(exprDay)) {
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
				if (StringHelper.isNotEmpty(exprMonth)) {
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
				if (StringHelper.isNotEmpty(exprYear)) {
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
				DataRow reportConData = reportService.getByTimeType(timeType,getLoginUserId());
				if (reportConData != null) {
					row.set("id", reportConData.getString("id"));
					reportService.updateReportTaskConfig(row);
				} else {
					reportService.addReportTaskConfig(row);
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

	/**
	 * 报表列表页面
	 * @return
	 */
	public ActionResult doReportPage() {
		DBPage page = null;
		int curPage = 1;
		int numPerPage = WebConstants.NumPerPage;
		Long userId = null;
		if (!getLoginUserType().equals(SrContant.ROLE_SUPER)) {
			userId = getLoginUserId();
		}
		page = reportService.getReportPage(null, null, null, null, curPage, numPerPage, userId);
		setAttribute("reportPage", page);
		return new ActionResult("/WEB-INF/views/report/reportList.jsp");
	}

	/**
	 * 删除报表
	 */
	public void doDelReport() {
		Integer id = getIntParameter("id");
		DataRow row = reportService.getReportInfo(id);
		if (id != null && id > 0) {
			String path = getRequest().getSession().getServletContext().getRealPath(row.getString("real_name"));
			File file = new File(path);
			boolean isright = false;
			if (file.exists()) {
				isright = delDir(file);
				reportService.delReport(id);
			} else {
				reportService.delReport(id);
				isright = true;
			}
			if (isright) {
				reportService.delReport(id);
				writetopage("true");
			} else {
				writetopage("false");
			}
		}
	}

	/**
	 * 分页查询报表数据
	 * @return
	 */
	public ActionResult doAjaxReportPage() {
		DBPage page = null;
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		String name = getStrParameter("name").replaceAll("&amp;nbsp;", " ");
		String reportType = getStrParameter("reportType");
		String startTime = getStrParameter("startTime").replaceAll("&amp;nbsp;", " ");
		String endTime = getStrParameter("endTime").replaceAll("&amp;nbsp;", " ");
		Long userId = null;
		if (!getLoginUserType().equals(SrContant.ROLE_SUPER)) {
			userId = getLoginUserId();
		}
		page = reportService.getReportPage(name, reportType, startTime, endTime, curPage, numPerPage, userId);
		this.setAttribute("reportPage", page);
		this.setAttribute("name", name);
		this.setAttribute("reportType", reportType);
		this.setAttribute("startTime", startTime);
		this.setAttribute("endTime", endTime);
		return new ActionResult("/WEB-INF/views/report/ajaxReport.jsp");
	}

	public ActionResult doReportInfo() {
		Integer id = getIntParameter("id");
		DataRow row = reportService.getReportInfo(id);
		return new ActionResult(row.getString("real_name"), false);
	}

	public ActionResult doDownload() throws Exception {
		Integer id = getIntParameter("id");
		DataRow row = reportService.getReportInfo(id);
		String displayName = row.getString("the_display_name");
		// displayName = new String(displayName.getBytes("ISO-8859-1"),"UTF-8");
		// 获得servletContext
		ServletContext sc = getRequest().getSession().getServletContext();

		String packDir = sc.getRealPath("/packDir/temp");

//		File dir = createDir(packDir);
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
			getResponse().addHeader("Content-Disposition","attachment;filename=" + new String(filename.getBytes("utf-8"), "ISO-8859-1"));
			getResponse().addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(getResponse().getOutputStream());
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
	private void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath) throws IOException {
		FileInputStream in = null;
		try {
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);
				ZipEntry entry = new ZipEntry(curPath+ fileOrDirectory.getName());
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
					zipFileOrDirectory(out, entries[i], curPath + fileOrDirectory.getName() + "/");
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
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
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
//				int length;
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
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
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
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
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
					String childPath = folder.getPath() + File.separator + childName;
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
	
	/**
	 * 处理用于生成PDF文件所需要的HTML文本
	 * @param htmlDoc
	 * @return
	 */
	private String appendHtml(String htmlDoc) {
		StringBuffer sb = new StringBuffer();
		htmlDoc = htmlDoc.replaceAll("<table style=\"width:100%;", "<table style=\"width:1274px;");
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<meta content=\"text/html;charset=UTF-8\" http-equiv=\"content-type\"/>\n");
		sb.append("<style type=\"text/css\">\n");
		sb.append("*{font-family:ARIAL_UTF8;}\n");
		//start set report css 
		sb.append("body {margin: 0px;font-family: \"Century Gothic\", Helvetica, Arial, sans-serif;font-size: 13px;line-height: 18px;color: #555555;background-color: #ffffff;text-shadow:0 -1px 1px rgba(0, 0, 0, 0.2)}");
		sb.append("\n#reportContainer{float: right;padding-left: 5px;margin-top: 20px;}");
		sb.append("\n#D_SVC,#D_BSP,#D_DS,#D_EMC,#D_HDS,#D_NETAPP,#D_SWITCH,#perfDiv{margin-top: 10px;}");
		sb.append("\n.style_9 {font-weight: bold;font-size: 9pt;color: rgb(68, 78, 104);padding: 3px;border-bottom: 1px solid rgb(0, 0, 0);}");
		sb.append("\n.style_11 {font-size: 9pt;padding: 3px;}");
		sb.append("\n.style_19 {margin-top: 15px;margin-right: 15px;border-top: 2px solid rgb(0, 0, 0);border-bottom: 2px solid rgb(0, 0, 0);}");
		//end set report css
		sb.append("</style>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<div style=\"width:1280px;margin-left:20px;position: relative;\">\n");
		sb.append("<div id=\"reportContainer\" style=\"width: 1280px;\">\n");
		sb.append(htmlDoc);
		sb.append("</div>\n");
		sb.append("</div>\n");
		sb.append("</body>\n");
		sb.append("</html>");
		return sb.toString();
	}
	
	/**
	 * 根据需求生成HTML版本的报表和PDF版本的报表
	 * @throws Exception
	 */
	public void doDownloadHTMLorPDF() {
		String realName = getRequest().getParameter("realName");
		String htmlDoc = getRequest().getParameter("htmlDoc");
		
		try {
			if (StringHelper.isNotEmpty(realName) && StringHelper.isNotBlank(realName)) {
				DataRow row = reportService.getReportInfo(realName);
				String displayName = row.getString("the_display_name");
				
				// For PDF
				if (StringHelper.isNotEmpty(htmlDoc) && StringHelper.isNotBlank(htmlDoc)) {
					//获取HTML文本
					String resultDoc = appendHtml(htmlDoc);
					PD4ML pd4ml = new PD4ML();
					pd4ml.useTTF("java:fonts", true);
					pd4ml.setHtmlWidth(1320);
					pd4ml.setPageSize(pd4ml.changePageOrientation(PD4Constants.A4));
					pd4ml.setPageInsetsMM(new Insets(0, 0, 0, 0));
					pd4ml.enableImgSplit(false);
//					pd4ml.enableDebugInfo();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					pd4ml.render(new StringReader(resultDoc), baos);
					baos.close();
					
					OutputStream os = new BufferedOutputStream(getResponse().getOutputStream());
					getResponse().setHeader("Content-disposition", "attachment; filename=" + new String((displayName + ".pdf").getBytes("utf-8"), "ISO-8859-1"));
					getResponse().setContentType("application/pdf");
					getResponse().setCharacterEncoding("UTF-8");
					os.write(baos.toByteArray());
					os.flush();
					os.close();
				// For HTML
				} else {
					// 获得servletContext
					ServletContext sc = getRequest().getSession().getServletContext();
					String packDir = sc.getRealPath("/packDir/temp");
//					File dir = createDir(packDir);
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
	
						// 以流的形式下载文件。
						InputStream fis = new BufferedInputStream(new FileInputStream(path));
						byte[] buffer = new byte[fis.available()];
						fis.read(buffer);
						fis.close();
						// 清空response
						getResponse().reset();
						// 设置response的Header
						getResponse().addHeader("Content-Disposition","attachment;filename=" + new String(filename.getBytes("utf-8"), "ISO-8859-1"));
						getResponse().addHeader("Content-Length", "" + file.length());
						OutputStream toClient = new BufferedOutputStream(getResponse().getOutputStream());
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
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
