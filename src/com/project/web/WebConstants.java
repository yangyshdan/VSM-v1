package com.project.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONObject;

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
	 *
	 */
	public static final String COOKIE_CLIENT_INFO_KEY = "user_login";
	
	/**
	 * 
	 */
	public static final String SESSION_CLIENT_ID = "@web_client_id";
	
	
	/**
	 * 
	 */
	public static final String SESSION_CLIENT_LOGIN_ID = "@web_client_loginid";


	/**
	 * 
	 */
	public static final String SESSION_CLIENT_NAME = "@web_client_name";


	/**
	 * 
	 */
	public static final String SESSION_CLIENT_TYPE = "@web_client_type";
	
	/**
	 * 默认配置信息中柱状图数
	 */
	public static final Integer DEFAULT_CONFIG_TOP = 10;

	/**
	 * 报表图片路径
	 */
	public static String REPORT_LOGO_URL="";
	
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
	
	/**
	 * nmon登录信息
	 */
	public static int NMON_PORT=0;
	public static String NMON_USER_NAME="";
	public static String NMON_PASSWORD="";
	
	/**
	 * 每次命令产生性能条数
	 */
	public static int PERFORMANCE_COUNT = 2;
	
	/**
	 * nmon 时间间隔
	 */
	public static int interval=0; 
	
	/**
	 * 数据库存实时性能数据长度(天)
	 */
	public static final Integer PERF_INTERVAL_LEN_DAY = 60;
	
	/**
	 * 存储系统类型
	 */
	public static final JSONObject STORAGE_OS_TYPE = new JSONObject().fromObject("{SVC:'21,38',BSP:'15,37',DS:'25'}");
	
}

