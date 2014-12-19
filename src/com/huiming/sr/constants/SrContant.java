package com.huiming.sr.constants;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SrContant {
	//数据库名
	public static String SR_DB = "srDB";
	//分页显示数据量
	public static int SR_NumPerPage = 20;
	//存储系统ID
	public static Long FKEY = 0L;
	//
	public static String staticMonth = "";
	public static String staticday = "";
	public static String staticcustom = "";
	//默认查询数据时间段
	public static int DEFAULT_REF_HOUR = 2088;
//	public static int DEFAULT_REF_HOUR = 24;
	public static int DEFAULT_DAY_FOR_ACTUAL_PRF= 2;
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
	//超过该时间段后查小时性能
	public static Long SEARCH_IN_PERHOURPERF = 2*24*60*60*1000L;
	//超过该时间段后查天性能
	public static Long SEARCH_IN_PERDAYPERF = 20*24*60*60*1000L;
	
	public static Integer isUpdateConfig = 0;
	
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

	public static Timestamp getTime(String timeStr) {
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",Locale.US);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = null;
		try {
			ts = Timestamp.valueOf(sdf.format(df.parse(timeStr)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ts;
	}

}
