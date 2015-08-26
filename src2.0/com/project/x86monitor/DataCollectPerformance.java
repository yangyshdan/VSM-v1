package com.project.x86monitor;

import java.math.BigInteger;
import java.util.Date;

import org.apache.log4j.Logger;

import com.huiming.service.x86monitor.DataCollectService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

import csharpwmi.CSharpWMIClass;
import csharpwmi.ICSharpWMIClass;
import csharpwmi.TPrfComputerper;

/**
 * @category 收集性能
 * @author hgc
 * @see 收集性能
 */
public class DataCollectPerformance {
	Logger logger = Logger.getLogger(DataCollectPerformance.class);
	private DataCollectService<TPrfComputerper> tPrfComputerper = 
		new DataCollectService<TPrfComputerper>(WebConstants.DB_DEFAULT);
	
	private DataCollectService<TPrfTimestamp> tPrfTimestamp = 
		new DataCollectService<TPrfTimestamp>(WebConstants.DB_DEFAULT);
	
	private ICSharpWMIClass cswmi = null;
	private DeviceInfo deviceInfo = null;
	
	public DataCollectPerformance(ICSharpWMIClass cswmi, DeviceInfo deviceInfo){
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
		Long computerId = tPrfComputerper.getComputerId(deviceInfo.getCurrentIP());
		if(computerId == null){ return; }
		if(computerId > 0 && cswmi != null && cswmi.IsConnect() && deviceInfo != null){
			try{
		        TPrfTimestamp time = new TPrfTimestamp();
		        boolean isPhysical = SrContant.SUBDEVTYPE_PHYSICAL.equalsIgnoreCase(deviceInfo.getToptype());
		        time.setSubsystemId(computerId);
		        time.setSubsystemName(cswmi.GetComputerName());
		        time.setIntervalLen(600);
		        time.setSummType((short)1);
		        time.setSampleTime(new Date());
		        time.setDeviceType(isPhysical? SrContant.SUBDEVTYPE_PHYSICAL : SrContant.SUBDEVTYPE_VIRTUAL);
		        BigInteger timeId = new BigInteger(tPrfTimestamp.insert(time, null, false));
		        
		        TPrfComputerper c = cswmi.GetTPrfComputerper();
		        c.setComputerId(computerId);
		        c.setTimeId(timeId.longValue());
		        tPrfComputerper.insert(c, new String[]{ "clrHandle" }, false);
		        time = null;
			}catch(Exception ex){
				logger.error("", ex);
			}
		}
	}
}
