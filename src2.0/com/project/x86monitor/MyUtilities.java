package com.project.x86monitor;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.jni4net.Bridge;

import org.apache.log4j.Logger;

import system.DateTime;

public class MyUtilities {
	public static final Logger logger = Logger.getLogger(MyUtilities.class);
	public static final int Connect = 0;
	public static final int Call = 1;
	public static final int None = 2;
	public static final int Packet = 3;
	public static final int PacketIntegrity = 4;
	public static final int PacketPrivacy = 5;
	public static final int Unchanged = 6;

	public static final int Impersonation = 7;
	public static final int Identify = 8;
	public static final int MyDelegate = 9;
	public static final int Anonymous = 10;
	public static final String BRIDGE_INIT = "cswmi.dll";

	public static final int Default = 99;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private MyUtilities(){}

	public static String getClassDirectory(Class<?> clazz) {
		return clazz.getClassLoader().getResource("/").toString().replace("file:/", "");
	}
	
	/**
	 * @see 修复表单提交  Ajax提交带来的字符串问题
	 * @return
	 */
	public static String repairFormString(String str){
		return str == null? "" : str.replace("&amp;", "&").replace("&nbsp;", " ");
	}
	
	private static final String htmls[] = {"&amp;", "&lt;", "&gt;", "&nbsp;", "<br>", "&#039;",   "&#034;", "&#092;"},
	   texts[] = {"&",     "<",    ">", " ",      "\n",   "\'",   "\"",    "\\"};
	public static String htmlToText(String str){  
        if(str == null){ return null; }
        int len, index;
        StringBuilder sb = new StringBuilder(str); // &#092;
        
        for(int i = 0, l = htmls.length; i < l; ++i){
        	index = sb.indexOf(htmls[i]);
        	len = htmls[i].length();
        	while(index >= 0){
        		sb.replace(index, index + len, texts[i]);
        		index = sb.indexOf(htmls[i]);
        	}
        }
        return sb.toString();
    }
	
	/**
	 * @see 如果填0L，那么就返回当前时间，如果填1天*24*60*60*1000，那么就返回明天
	 * @param fromNowOn
	 * @return
	 */
	public static Date getDateFromNowOn(long fromNowOn){
		Date now = new Date();
		now.setTime(now.getTime() + fromNowOn);
		return now;
	}
	
	public static Date convertDateTime2Date(DateTime dt){
		if(dt != null && dt.getTicks() > 0){
			try {
				return sdf.parse(dt.ToString().replace("/", "-"));
			} catch (ParseException e) {
				logger.error("", e);
			}
		}
		return null;
	}
	
	public static DateTime convertDate2DateTime(Date dt){
		if(dt != null && dt.getTime() > 0){
			try {
				return DateTime.Parse(sdf.format(dt));
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return null;
	}
	
	public static DateTime convertString2DateTime(String dt){
		if(dt != null && dt.trim().length() > 0){
			try {
				return DateTime.Parse(dt);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return null;
	}
	
	public static void initBridge(Class<?> clazz){
		String clazzDir = MyUtilities.getClassDirectory(clazz);
		String dir = new File(clazzDir).getParent();
		File file = new File(dir + "/lib/CSharpWMI.j4n.dll");
		if(MySession.getAttribute(MyUtilities.BRIDGE_INIT) == null){ // 说明还没有初始化
			MyUtilities.initBridge(file);
		}
	}

	/**
	 * @see 初始化DLL
	 * @param file
	 */
	private static synchronized void initBridge(File file){
		if (!Bridge.isRegistered()) { // 如果还没有注册COM
			Bridge.setVerbose(true);
			logger.info("初始化CSharpWMI.dll ...");
			//Bridge.RegisterAssembly(Assembly.GetEntryAssembly());
			try {
				Bridge.init();
				Bridge.LoadAndRegisterAssemblyFrom(file);
			} catch (IOException e) {
				logger.error("注册DLL不成功", e);
				// 注册DLL不成功
				MySession.setAttribute(MyUtilities.BRIDGE_INIT, null);
			}
		}
		MySession.setAttribute(MyUtilities.BRIDGE_INIT, MyUtilities.BRIDGE_INIT);
	}
}
