package com.project.x86monitor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import system.DateTime;

public class MySession {
	private static final MySession session = new MySession();
	private static final Map<String, Object> stores = new HashMap<String, Object>();
	
//	private static final Map<String, DeviceInfo> deviceInfos = new HashMap<String, DeviceInfo>();
//	private static final Map<String, ICSharpWMIClass> cswmis = new HashMap<String, ICSharpWMIClass>();
	// 记住已获取Windows日志事件的时间
	private static final Map<String, DateTime> fromNowOns = new HashMap<String, DateTime>();
	
	private static final Map<String, Date> fromNowOns_ = new HashMap<String, Date>();
	
	private MySession(){}
	
	public static MySession getInstance(){
		return session;
	}
	
	public synchronized static void setAttribute(String key, Object value){
		stores.put(key, value);
	}
	
	public synchronized static void removeAttribute(String key){
		if(stores.containsKey(key)){
			stores.remove(key);
		}
	}
	
	public synchronized static Object getAttribute(String key){
		if(stores.containsKey(key)){
			return stores.get(key);
		}
		return null;
	}

	public synchronized static void putFromNowOn(String key, DateTime fromNowOn) {
		fromNowOns.put(key, fromNowOn);
	}
	
	public synchronized static DateTime getFromNowOn(String key, DateTime defaultDateTime) {
		if(fromNowOns.containsKey(key)){
			return fromNowOns.get(key);
		}
		return defaultDateTime;
	}
	
	public synchronized static DateTime getFromNowOn(String key) {
		if(fromNowOns.containsKey(key)){
			return fromNowOns.get(key);
		}
		DateTime dt = DateTime.getNow().AddDays(-7);
		fromNowOns.put(key, dt);
		return dt; // 如果没有就默认是7天前
	}
	
	public synchronized static void putFromNowOn_(String key, Date fromNowOn) {
		fromNowOns_.put(key, fromNowOn);
	}
	
	public synchronized static Date getFromNowOn_(String key) {
		if(fromNowOns_.containsKey(key)){
			return fromNowOns_.get(key);
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		fromNowOns_.put(key, cal.getTime());
		return cal.getTime(); // 如果没有就默认是7天前
	}
	
}
