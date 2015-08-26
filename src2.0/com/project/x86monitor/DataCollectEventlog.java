package com.project.x86monitor;

import org.apache.log4j.Logger;

import system.DateTime;

import com.huiming.service.x86monitor.DataCollectService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

import csharpwmi.CSharpWMIClass;
import csharpwmi.ICSharpWMIClass;
import csharpwmi.Tndevicelog;

/**
 * @category 收集主机事件数据
 * @author huiming02
 *
 */
public class DataCollectEventlog {
	static final Logger logger = Logger.getLogger(DataCollectEventlog.class);
	private DataCollectService<Tndevicelog> tndevicelog = new DataCollectService<Tndevicelog>(WebConstants.DB_DEFAULT);
		
	private static final String exclusions[] = new String[]{"clrHandle"};

	private DeviceInfo deviceInfo = null;
	private ICSharpWMIClass cswmi = null;
	
	public DataCollectEventlog(ICSharpWMIClass cswmi, DeviceInfo deviceInfo){
		try{
			if(cswmi == null){
				if(deviceInfo != null){
					MyUtilities.initBridge(this.getClass());
					cswmi = new CSharpWMIClass(
							new String[]{ deviceInfo.getIpAddress() },
							deviceInfo.getUsername(),
							deviceInfo.getPassword(),
							deviceInfo.getAuthentication(),
							deviceInfo.getImpersonate());
					cswmi.Connection();
					deviceInfo.setCurrentIP(cswmi.GetCurrentIP());
				}
			}
			else if(!cswmi.IsConnect()){
				cswmi.Connection();
			}
			this.cswmi = cswmi;
			this.deviceInfo = deviceInfo;
		}catch(Exception ex){
			logger.error("连接IP地址为" + deviceInfo.getIpAddress() + "出错", ex);
		}
	}
	
	public void execute(){
		Long computerId = tndevicelog.getComputerIdByIP(cswmi.GetCurrentIP());
		if(computerId == null){ return; }
		if(computerId > 0 && deviceInfo != null && cswmi != null && cswmi.IsConnect()){ // 说明配置信息收集完毕然后再收集事件
			// 默认前7天
			String key = deviceInfo.getComputerId().toString();  // 因为把t_server的id当作key
			DateTime fromNowOn = MySession.getFromNowOn(key); // 如果没有就默认是7天前的数据
			cswmi.InitEventLogRemoteReader(); // 初始化事件日志的远程Reader
			boolean isPhysical = WebConstants.HYPERVISOR.equalsIgnoreCase(deviceInfo.getToptype());
			Tndevicelog []devs = null;
			if(isPhysical){
				Long hypId = tndevicelog.getHypervisorIdByComputerId(computerId);
				if(hypId > 0L){
					// 这是收集操作系统的系统事件，监控操作系统
					devs = cswmi.GetTndevicelog(hypId.toString(), SrContant.SUBDEVTYPE_PHYSICAL, 
							deviceInfo.getToptype(), fromNowOn);
				}
			}
			else {
				Long hypVmIds[] = tndevicelog.getHypIdAndVMIdByComputerId(computerId);
				devs = cswmi.GetTndevicelog(hypVmIds[0].toString(), hypVmIds[1].toString(), 
						SrContant.SUBDEVTYPE_VIRTUAL, deviceInfo.getToptype(), fromNowOn);
				
			}
			
			if(devs != null && devs.length > 0){
				try{
					tndevicelog.insert(devs, exclusions, false);
				}catch(Exception ex){
					logger.error("插入设备事件日志出错", ex);
				}
				for(int i = devs.length - 1; i >= 0; --i){
					if(devs[i] != null){
						fromNowOn = devs[i].getFfirsttime();
						break;
					}
				}
				MySession.putFromNowOn(key, fromNowOn);
			}
		}
	}
}
