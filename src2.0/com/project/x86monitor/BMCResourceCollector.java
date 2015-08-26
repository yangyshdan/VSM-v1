package com.project.x86monitor;

import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.x86monitor.DataCollectService;
import com.project.web.WebConstants;
import com.project.x86monitor.beans.SysEventLogBean;

public class BMCResourceCollector {
	private IPMIInfo ipmi;
	public BMCResourceCollector(IPMIInfo ipmi) {
		this.ipmi = ipmi;
	}
	
	public void execute(){
		IPMIUtil util = new IPMIUtil(this.getClass());
		DataCollectService<DataRow> collector = new DataCollectService<DataRow>(WebConstants.DB_DEFAULT);
		boolean isUpadate = collector.isBMCResourceExists(ipmi.getHypervisorId());
		SysEventLogBean bean = new SysEventLogBean();
		bean.setIpmi(ipmi);
		util.loadServerConfig(bean);
		List<DataRow> drs = bean.getInsertData();
		String table = "t_res_bmc";
		if(isUpadate){ // 一个hypervisor_id代表一个配置
			collector.update(drs, table, "hypervisor_id");
		}
		else {
			collector.insert(drs, table);
		}
	}
}
