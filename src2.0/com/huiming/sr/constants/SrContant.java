package com.huiming.sr.constants;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.project.web.WebConstants;

public class SrContant {
	//分页显示数据量
	public static int SR_NumPerPage = 20;
	//存储系统ID
	public static Long FKEY = 0L;
	//
	public static String staticMonth = "";
	public static String staticday = "";
	public static String staticcustom = "";
	//默认查询数据时间段
	public static int DEFAULT_REF_HOUR = 1088;
//	public static int DEFAULT_REF_HOUR = 24;
	public static int DEFAULT_DAY_FOR_ACTUAL_PRF = 2;
	public static int DEFAULT_DAY_FOR_ACTUAL_PRFHOUER = 20;
	public static int DEFAULT_DAY_FOR_ACTUAL_PRFDAY = 60;
	//configuration.xml和preformence.xml文件
	public static File CONFIGURATION = null;
	public static File PERFORMANCE = null;
	//自动任务执行状态(0：初始化数据库	1：采集存储系统配置信息 	2：采集存储系统性能信息)
	public static int TASK_COUNT = 0;
	//perl环境
	public static String ACT_PERL_PATH = "";
	//时间ID
	public static Long TIME_FKEY = 0L;             
	//性能数据采集时间间隔
	//柱状图默认显示数据量
	public static int DEFAULT_HISTOGRAM_COUNT = 20;    
	//报表性能曲线默认显示
	public static int REPORT_PERF_LINE_COUNT = 200;
	//报表性能统计性能数据显示
	public static int REPORT_PERF_DATA_COUNT = 10;
	//热磁盘组数
	public static int HOT_DISKGROUP_NUM = 3;
	//热卷数
	public static int HOT_LUN_NUM = 3;
	//前10条数据
	public static int TOP_LIMIT_COUNT = 10;
	//超过该时间段后查小时性能
	public static Long SEARCH_IN_PERHOURPERF = 2*24*60*60*1000L;
	//超过该时间段后查天性能
	public static Long SEARCH_IN_PERDAYPERF = 20*24*60*60*1000L;
	public static Integer isUpdateConfig = 0;
	
	public static final String LABEL = "label";
	public static final String VALUE = "value";
	//数据库类型
	public static final String DBTYPE_SR = "SR";
	public static final String DBTYPE_TPC = "TPC";
	
	//设备类型
	public static final String DEVTYPE_LBL_DS = "存储系统(IBM-DS8k)";
	public static final String DEVTYPE_LBL_BSP = "存储系统(IBM-DS4k/5k)";
	public static final String DEVTYPE_LBL_SVC = "存储系统(IBM-SVC)";
	public static final String DEVTYPE_LBL_HDS = "存储系统(HDS-AMS)";
	public static final String DEVTYPE_LBL_EMC = "存储系统(EMC-CX/VNX)";
	
	public static final String DEVTYPE_LBL_APP = "业务系统";
	public static final String DEVTYPE_LBL_PHYSICAL = "物理机";
	public static final String DEVTYPE_LBL_VM = "虚拟机";
	public static final String DEVTYPE_LBL_SWITCH = "交换机";
	public static final String DEVTYPE_LBL_STORAGE = "存储系统";
	public static final String DEVTYPE_LBL_POOL = "存储池";
	public static final String DEVTYPE_LBL_VOLUME = "存储卷";
	
	public static final String DEVTYPE_VAL_DS = "DS";
	public static final String DEVTYPE_VAL_BSP = "BSP";
	public static final String DEVTYPE_VAL_SVC = "SVC";
	public static final String DEVTYPE_VAL_HDS = "HDS";
	public static final String DEVTYPE_VAL_EMC = "EMC";
	public static final String DEVTYPE_VAL_SWITCH = "SWITCH";
	public static final String DEVTYPE_VAL_HOST = "HOST";
	public static final String DEVTYPE_VAL_APPLICATION = "APPLICATION";
	
	// Fabric网络也是看作监控设备
	public static final String DEVTYPE_VAL_FABRIC = "FABRIC";
	public static final String DEVTYPE_VAL_ZONESET = "ZONESET";
	
	public static final String SUBDEVTYPE_STORAGE = "Storage";
	public static final String SUBDEVTYPE_DISKGROUP = "DiskGroup";
	public static final String SUBDEVTYPE_HOSTGROUP = "HostGroup";
	public static final String SUBDEVTYPE_PORT = "Port";
	public static final String SUBDEVTYPE_NODE = "Node";
	public static final String SUBDEVTYPE_VOLUME = "Volume";
	public static final String SUBDEVTYPE_CONTROLLER = "Controller";
	public static final String SUBDEVTYPE_POOL = "Pool";
	public static final String SUBDEVTYPE_DISK = "Disk";
	public static final String SUBDEVTYPE_ARRAYSET = "ArraySet";
	public static final String SUBDEVTYPE_EXTENT = "Extent";
	public static final String SUBDEVTYPE_RANK = "Rank";
	public static final String SUBDEVTYPE_IOGROUP = "IOGroup";
	public static final String SUBDEVTYPE_ARRAYSITE = "ArraySite";
	public static final String SUBDEVTYPE_MDISK = "Mdisk";
	public static final String SUBDEVTYPE_MDISKGROUP = "MdiskGroup";
	public static final String SUBDEVTYPE_SWITCHPORT = "SwitchPort";
	public static final String SUBDEVTYPE_PHYSICALPORT = "PhysicalPort";
	public static final String SUBDEVTYPE_PHYSICAL = "Physical";
	public static final String SUBDEVTYPE_VIRTUAL = "Virtual";
	public static final String SUBDEVTYPE_APP = "App";
	public static final String SUBDEVTYPE_ZONE = "Zone";
	// 这两个用于记住拓扑图的起点交换机和终点交换机
	public static final String SUBDEVTYPE_STARTSWITCH = "StartSwitch";
	public static final String SUBDEVTYPE_ENDSWITCH = "EndSwitch";
	
	public static final String SUBDEVTYPE_SWITCH = "Switch";
	public static final String SUBDEVTYPE_HYPERVISOR = "Hypervisor";
	public static final String SUBDEVTYPE_VM = "VirtualMachine";
	
	public static final String SUBDEVTYPE_IPNW_SWITCH = "IPNWSwitch";
	
	public static final String SUBDEVTYPE_IPNW_SWITCH_PORT = "IPNWSwPort";
	
	public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TIME_PATTERN);
	
	//表示时间类型(1:实时;2:小时;3:天)
	public static final int SUMM_TYPE_REAL = 1;
	public static final int SUMM_TYPE_HOUR = 2;
	public static final int SUMM_TYPE_DAY = 3;
	
	// 事件级别
	public static final int EVENT_LEVEL_INFO = 0;
	public static final int EVENT_LEVEL_WARNING = 1;
	public static final int EVENT_LEVEL_CRITICAL = 2;
	
	// 显示哪一个Tab，当跳转到详细信息页面时，确定哪一个页面是默认显示的
	public static final int TAB_SUMMARY = 0; // 显示总览
	public static final int TAB_CONFIG = 1; // 显示配置
	public static final int TAB_PERF = 2; // 显示性能
	public static final int TAB_EVENT = 3; // 显示事件
	public static final int TAB_COMPONENT = 4; // 显示部件（虚拟机，交换机端口等等）
	
	// 画图类型
	public static final int GRAPH_TYPE_LINE = 0;
	public static final int GRAPH_TYPE_TOPN = 1;
	
	// 默认TOPN的数量
	public static final int TOPN_COUNT = 5;
	
	// 性能改为单选，所以该选项为1
	public static final int PERF_COUNT = 1; // 默认显示多少条性能
	
	// 处于哪一种状态
	public static final int STATE_ADD = 0;		// 处于新增状态
	public static final int STATE_CHECK = 1;	// 处于查看状态
	public static final int STATE_EDIT = 2;		// 处于编辑状态
	
	// 权限
	public static final String ROLE_SUPER = "super";
	public static final String ROLE_USER = "user";
	
	// 事件日志类型
	public static final int LOG_TYPE_SYSTEM = 0;		// 0 系统告警
	public static final int LOG_TYPE_TPC = 1;		// 1 TPC告警
	public static final int LOG_TYPE_THRESHOLD = 2;		// 2 阀值告警
	public static final int LOG_TYPE_HARDWARE = 3;		// 3 硬件告警
	
	// 无效值
	public static final int INVALID_VALUE = Integer.MIN_VALUE;
	
	// 数据库表tpdpfields的level
	/*
	 	level为1表示存储的非详细页面
		level为2表示非存储的非详细页面
		level为3表示存储或非存储的详细页面
	 */
	public static final int STORAGE_WITHOUT_DETAILS = 1;
	public static final int NOT_STORAGE_WITHOUT_DETAILS = 2;
	public static final int ALL_DEVICE_WITH_DETAILS = 3;
	
	// SNMP的版本
	// 详情参考: http://wenku.baidu.com/view/ec7800d87f1922791688e860.html
	public static final String SNMP_V1 = "1";
	public static final String SNMP_V2 = "2c";
	public static final String SNMP_V3 = "3";
	
	public static synchronized Long getKey() {
		StringBuffer sb = new StringBuffer();
		Calendar c = Calendar.getInstance();
		sb.append(c.get(Calendar.YEAR));
		sb.append(c.get(Calendar.MONTH) + 1);
		sb.append(c.get(Calendar.DAY_OF_MONTH));
		sb.append(c.get(Calendar.HOUR_OF_DAY));
		sb.append(c.get(Calendar.MINUTE));
		sb.append(c.get(Calendar.SECOND));
		sb.append(c.get(Calendar.MILLISECOND));
		return Long.parseLong(sb.toString());
	}
	
	/**
	 * 获取指定格式日期的Timsstamp值
	 * @param timeStr
	 * @return
	 */
	public static Timestamp getTime(String timeStr) {
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",Locale.US);
		DateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
		Timestamp ts = null;
		try {
			ts = Timestamp.valueOf(sdf.format(df.parse(timeStr)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ts;
	}
	
	/**
	 * 获取当前时间的Timestamp值
	 * @return
	 */
	public static Timestamp getTimestamp() {
		DateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
		Timestamp ts = null;
		try {
			ts = Timestamp.valueOf(sdf.format(new Date()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}
	
	/**
	 * 获取指定格式的日期字符串
	 * @param date
	 * @return
	 */
	public static String getTimeFormat(Date date) {
		String timeStr = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
			timeStr = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStr;
	}
	
	/**
	 * 将日期格式转换为Timstamp值
	 * @param timeStr
	 * @return
	 */
	public static Timestamp getTimestamp(String timeStr) {
		Timestamp ts = null;
		try {
			ts = Timestamp.valueOf(timeStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}
	
	/**
	 * 比较两个时间相差的毫秒数
	 * @param timeOne
	 * @param timeTwo
	 * @return
	 */
	public static long getTimeInterval(String timeOne, String timeTwo) {
		long result = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
			Date dateOne = sdf.parse(timeOne);
			Date dateTwo = sdf.parse(timeTwo);
			result = dateOne.getTime() - dateTwo.getTime();
			result = (result > 0 ? (result/1000) : 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 将时间列表排序
	 * @param timeList
	 * @return
	 * @throws Exception
	 */
	public static String[] orderTimestamp(List<String> timeList) throws Exception {
		List<Date> list = new ArrayList<Date>();
		String[] resultList = new String[timeList.size()];
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
		for (int i = 0; i < timeList.size(); i++) {
			list.add(sdf.parse(timeList.get(i)));
		}
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			resultList[i] = sdf.format(list.get(i));
		}
		return resultList;
	}
	
	public static String getDBType(String deviceType){
		if (deviceType.equals(DEVTYPE_VAL_APPLICATION) 
				|| deviceType.equals(DEVTYPE_LBL_EMC) 
				|| deviceType.equals(DEVTYPE_VAL_HDS) 
				|| deviceType.equals(WebConstants.STORAGE_TYPE_VAL_NETAPP)
				|| deviceType.equals(DEVTYPE_VAL_HOST)) {
			return WebConstants.DB_DEFAULT;
		}	
		return WebConstants.DB_TPC;
	}
}
