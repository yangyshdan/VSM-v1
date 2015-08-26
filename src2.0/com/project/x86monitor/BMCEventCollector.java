package com.project.x86monitor;

import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.x86monitor.DataCollectService;
import com.project.web.WebConstants;
import com.project.x86monitor.beans.SysEventLogBean;

/**
 * @see 用于收集BMC的事件
 * @author 何高才
 *
 */
public class BMCEventCollector {
	private IPMIInfo ipmi;
	public BMCEventCollector(IPMIInfo ipmi) {
		this.ipmi = ipmi;
	}
	
	public void execute(){
		IPMIUtil util = new IPMIUtil(this.getClass());
		String BMC_FROM_NOW_ON_KEY = "BMC_" + ipmi.getIpAddress();
		Date date = MySession.getFromNowOn_(BMC_FROM_NOW_ON_KEY);
		SysEventLogBean bean = new SysEventLogBean();
		bean.setFromNowOn(date.getTime());
		bean.setIpmi(ipmi);
		util.loadSystemEventLog(bean);
		
		date = new Date(bean.getFromNowOn());
		MySession.putFromNowOn_(BMC_FROM_NOW_ON_KEY, date);
		
		List<DataRow> sels = bean.getSysEventLogs();
		DataCollectService<DataRow> collector = new DataCollectService<DataRow>(WebConstants.DB_DEFAULT);
		collector.insertDeviceLogs(sels);
	}
}
