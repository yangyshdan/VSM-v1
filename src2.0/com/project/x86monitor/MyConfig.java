package com.project.x86monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import system.DateTime;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.AES;
import com.huiming.service.x86monitor.DataCollectService;
import com.huiming.service.x86server.X86ServerService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

import csharpwmi.CSharpWMIClass;
import csharpwmi.ICSharpWMIClass;
import csharpwmi.Tndevicelog;

public class MyConfig {
	public static final Logger logger = Logger.getLogger(MyConfig.class);
	
	private Map<String, DeviceInfo> deviceInfos;
	private Map<String, ICSharpWMIClass> cswmis;

	public MyConfig() {
		X86ServerService service = new X86ServerService();
		List<DataRow> drs = service.getServers(null, true, WebConstants.OSTYPE_WINDOWS);
		
		if(drs != null && drs.size() > 0){
			deviceInfos = new HashMap<String, DeviceInfo>(drs.size());
			cswmis = new HashMap<String, ICSharpWMIClass>(drs.size());
			String key;
			DeviceInfo deviceInfo;
			DataCollectService<Tndevicelog> tndevicelog = null;
			MyUtilities.initBridge(this.getClass());

			AES aes = new AES();
			for(DataRow dr : drs){
				deviceInfo = new DeviceInfo();
				key = dr.getString("id");
				deviceInfo.setComputerId(dr.getLong("id"));
				deviceInfo.setHostName(dr.getString("name"));
				deviceInfo.setIpAddress(dr.getString("ip_address"));
				deviceInfo.setModel(dr.getString("model"));
				deviceInfo.setUsername(dr.getString("user"));
				aes.setKey(key);
				deviceInfo.setPassword(aes.decrypt(dr.getString("password"), "utf-8"));
				deviceInfo.setVendor(dr.getString("vendor"));
				deviceInfo.setAuthentication(dr.getInt("authentication"));
				deviceInfo.setImpersonate(dr.getInt("impersonation"));
				deviceInfo.setState(dr.getBoolean("state"));
				deviceInfo.setToptype(dr.getString("toptype"));
				//logger.info(deviceInfo);
				deviceInfos.put(key, deviceInfo);
				
				ICSharpWMIClass cswmi = null;
				try{
					cswmi = new CSharpWMIClass(
							new String[]{ deviceInfo.getIpAddress() },
							deviceInfo.getUsername(),
							deviceInfo.getPassword(),
							deviceInfo.getAuthentication(),
							deviceInfo.getImpersonate());
					cswmi.Connection();
					deviceInfo.setCurrentIP(cswmi.GetCurrentIP());
					cswmis.put(key, cswmi);
				}catch(Exception ex){
					try{ // 这种异常应该放到日志事件记录tndevicelog，并且关停任务
						if(tndevicelog == null){
							tndevicelog = new DataCollectService<Tndevicelog>(WebConstants.DB_DEFAULT);
						}
						Tndevicelog dev = new Tndevicelog();
						Long computerId = tndevicelog.getComputerId(cswmi.GetCurrentIP());
						dev.setFcount(1);
						dev.setFdescript(String.format("服务器名称: %s, IP地址: %s", 
								deviceInfo != null? deviceInfo.getHostName() : "unknown",
								deviceInfo != null? deviceInfo.getIpAddress() : "unknown"));
						dev.setFdetail("请检查IP地址为" + 
								(deviceInfo != null? deviceInfo.getIpAddress() : "unknown") + 
							"的主机: 1、可能该服务器的访问权限被更改，密码或用户名更改; 2、服务器宕机,服务器处于休眠、睡眠或者关机状态");
						dev.setFlogtype(4);
						dev.setFtopid(computerId.toString());
						dev.setFtoptype(deviceInfo.getToptype());
						dev.setFtopname(deviceInfo.getHostName());
						dev.setFresourceid(dev.getFtopid());
						dev.setFresourcename(dev.getFtopname());
						dev.setFresourcetype(dev.getFtopid());
						dev.setFfirsttime(DateTime.getNow());
						dev.setFlasttime(dev.getFfirsttime());
						dev.setFlevel(String.valueOf(SrContant.EVENT_LEVEL_CRITICAL));
						logger.error(dev.getFdetail(), ex);
						tndevicelog.insert(dev, new String[]{"clrHandle"}, false);
						tndevicelog.disableTask(computerId, null);
					}catch(Exception e){
						logger.error("保存日志出错", e);
					}
				}
				logger.info(deviceInfo);
			}
		}
		drs = null;		
	}
	
	public Map<String, DeviceInfo> getDeviceInfos() {
		return deviceInfos;
	}

	public Map<String, ICSharpWMIClass> getCswmis() {
		return cswmis;
	}
}
