package com.project.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

import com.huiming.service.user.UserService;

/**
 *
 */
public class WebConstants{


	/**
	 *
	 */
	public static final String SITE_MAIN = "main";

	/**
	 * 
	 */
	public static final String DB_DEFAULT = "srDB";
	
	/**
	 * 
	 */
	public static final String DB_TPC = "tpc";
	
	/**
	 *
	 */
	public static final int NumPerPage = 25;
	
	/**
	 * 列表页面默认记录数
	 */
	public static final int LIST_NUM_PER_PAGE = 15;
	
	/**
	 *
	 */
	public static final String COOKIE_CLIENT_INFO_KEY = "user_login";
	
	/**
	 * 
	 */
	public static final String SESSION_CLIENT_ID = "clientId";
	
	
	/**
	 * 
	 */
	public static final String SESSION_CLIENT_LOGIN_ID = "@web_client_loginid";


	/**
	 * 
	 */
	public static final String SESSION_CLIENT_NAME = "clientName";


	/**
	 * 
	 */
	public static final String SESSION_CLIENT_TYPE = "clientType";
	
	/**
	 * 默认配置信息中柱状图数
	 */
	public static final Integer DEFAULT_CONFIG_TOP = 10;

	/**
	 * 报表图片路径
	 */
	public static String REPORT_LOGO_URL = "";
	
	/**
	 * 
	 */
	public static final String SESSION_AUTHORITY_KEY = "auth52";
	
	/**
	 * @see 当编辑用户，或者编辑角色时，都应该更新一下权限
	 * @param s
	 */
	@SuppressWarnings("unchecked")
	public static void resetAuthority(HttpSession s, long newUserId){
		Object obj = s.getAttribute(WebConstants.SESSION_AUTHORITY_KEY);
		if(obj != null){
			Long userId = (Long)((Map<String, Object>)obj).get("userId");
			if(userId != null && newUserId == userId){ // 更新自己的信息
				Logger logger = Logger.getLogger(WebConstants.class);
				logger.info("**********************************************");
				logger.info("Step here ...");
				logger.info("**********************************************");
				s.setAttribute(WebConstants.SESSION_AUTHORITY_KEY, new UserService().getMenuIdsByUserId(userId));
			}
		}
	}
	
	public static String getDefaultStartTime(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
	}
	
	/**
	 * HMC登录信息
	 */
	public static String IP_ADDRESS="";
	public static int PORT=0;
	public static String USER_NAME="";
	public static String PASSWORD="";
	
	// 访问Window服务器的默认值
	public static Integer X86_DEFAULT = 99;
	
	/**
	 * nmon登录信息
	 */
	public static int NMON_PORT = 0;
	public static String NMON_USER_NAME="";
	public static String NMON_PASSWORD="";
	
	/**
	 * 每次命令产生性能条数
	 */
	public static int PERFORMANCE_COUNT = 2;
	
	/**
	 * nmon 时间间隔
	 */
	public static int interval = 30; 
	
	/**
	 * 数据库存实时性能数据长度(天)
	 */
	public static final Integer PERF_INTERVAL_LEN_DAY = 60;
	
	/**
	 * 存储系统类型
	 */
	public static final JSONObject STORAGE_OS_TYPE = new JSONObject().fromObject("{SVC:'21,38',BSP:'15,37',DS:'25'}");
	public static final String STORAGE_TYPE_LBL_NETAPP = "存储系统(NETAPP)";
	public static final String STORAGE_TYPE_VAL_NETAPP = "NETAPP";
	
	//厂商
	public static final String VENDOR_IBM = "IBM"; //IBM
	public static final String VENDOR_LENOVO = "Lenovo"; //联想
	public static final String VENDOR_DELL = "DELL"; //戴尔
	public static final String VENDOR_HUAWEI = "HUAWEI"; //华为
	public static final String VENDOR_HP = "HP"; //惠普
	public static final String VENDOR_INSPUR = "INSPUR"; //浪潮
	public static final String VENDOR_SUGON = "Sugon"; //曙光
	public static final String VENDOR_OTHER = "Other"; //其他
	//架构类型
	public static final String SCHEMA_TYPE_X86 = "X86";
	public static final String SCHEMA_TYPE_POWER = "Power";
	//操作系统
	public static final String OSTYPE_LINUX = "Linux";
	public static final String OSTYPE_UNIX = "Unix";
	public static final String OSTYPE_WINDOWS = "Windows";
	public static final String OSTYPE_ESXI = "ESXi";
	//虚拟化平台类型
	public static final String VIRT_PLAT_TYPE_KVM = "KVM";
	public static final String VIRT_PLAT_TYPE_VMWARE = "VMware";
	public static final String VIRT_PLAT_TYPE_XENSERVER = "XenServer";
	public static final String VIRT_PLAT_TYPE_HYPER_V = "Hyper-V";
	public static final String VIRT_PLAT_TYPE_NO = "无";
	//时间范围
	public static final String TIME_RANGE_SECOND = "second";
	public static final String TIME_RANGE_MINUTE = "minute";
	public static final String TIME_RANGE_HOUR = "hour";
	public static final String TIME_RANGE_DAY = "day";
	public static final String TIME_RANGE_WEEK = "week";
	public static final String TIME_RANGE_MONTH = "month";
	public static final String TIME_RANGE_YEAR = "year";
	public static final String SHOW_TAG = "showTag";
	//HYPERVISOR
	public static final String DEVTYPE_HYPERVISOR = "Hypervisor";
	//服务器
	public static final String DEVTYPE_LBL_HOST = "服务器";
	//日期格式
	public static final String TIME_PATTERN_A = "yyyy/MM/dd HH:mm:ss";
	//List
	public static final String NO_DATA_SIGN = "0";
	public static final String ALL_KPI_LIST = "kpi_list";
	public static final String PHYSICAL_LIST = "physical_list";
	public static final String VIRTUAL_LIST = "virtual_list";
	public static final String HYPERVISOR_LIST = "hypervisor_list";
	public static final String SWITCH_LIST = "switch_list";
	public static final String FABRIC_LIST = "fabric_list";
	public static final String ZONEZET_LIST = "zonezet_list";
	public static final String ZONE_LIST = "zone_list";
	public static final String TPC_STORAGE_LIST = "tpc_storage_list";
	public static final String SR_STORAGE_LIST = "sr_storage_list";
	
	//物理机表示字符串
	public static final String HYPERVISOR = "physical";
	//虚拟机表示字符串
	public static final String VIRTUAL = "virtual";
	
	public static String getStorageType(String os_type){
		String type = null;
		if ("25".equals(os_type)) {
			type = "DS";
		} else if ("21".equals(os_type) || "38".equals(os_type)) {
			type = "SVC";
		} else if ("10".equals(os_type) || "15".equals(os_type) || "37".equals(os_type)) {
			type = "BSP";
		}
		return type == null? os_type : type;
	}
}

