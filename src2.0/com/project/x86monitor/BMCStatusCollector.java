package com.project.x86monitor;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.x86monitor.DataCollectService;
import com.project.web.WebConstants;
import com.project.x86monitor.beans.SysEventLogBean;

public class BMCStatusCollector {
	private IPMIInfo ipmi;
	public BMCStatusCollector(IPMIInfo ipmi) {
		this.ipmi = ipmi;
	}
	
	public void execute(){
		IPMIUtil util = new IPMIUtil(this.getClass());
		DataCollectService<DataRow> collector = new DataCollectService<DataRow>(WebConstants.DB_DEFAULT);
		SysEventLogBean bean = new SysEventLogBean();
		bean.setStatusIds(collector.getAllStatusId(ipmi.getHypervisorId()));
		bean.setIpmi(ipmi);
		util.loadSensorStatus(bean);
		String tableName = "t_status_sensors";
		collector.update(bean.getUpdateData(), tableName, "bmc_index");
		collector.insert(bean.getInsertData(), tableName);
	}
}
