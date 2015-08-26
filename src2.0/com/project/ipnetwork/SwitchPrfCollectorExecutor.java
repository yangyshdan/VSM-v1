package com.project.ipnetwork;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.deviceSnmp.DeviceSnmpService;
import com.huiming.sr.constants.SrContant;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;

/**
 * @category 搜集交换机的性能数据的执行者
 * @author 何高才
 *
 */
public class SwitchPrfCollectorExecutor {
	private Logger logger = Logger.getLogger(SwitchPrfCollectorExecutor.class);
	private List<DataRow> entities;
	private TCfgDeviceSnmp snmpInfo;
	private SnmpUtil snmpUtil;
	private DeviceSnmpService service;
	
	public SwitchPrfCollectorExecutor(List<DataRow> entities, TCfgDeviceSnmp snmpInfo, SnmpUtil snmpUtil, 
			DeviceSnmpService service) {
		this.entities = entities;
		this.snmpInfo = snmpInfo;
		this.snmpUtil = snmpUtil;
		this.service = service;
	}

	public void execute() {
		if(entities != null && entities.size() > 0 && snmpInfo != null && snmpUtil != null) {
			String dir = new File(MyUtilities.getClassDirectory(getClass())).getParent() + "/lib/net-snmp/bin/";
			String oids[] = new String[entities.size()];
			DataRow dr;
			String oid;
			for(int i = 0, l = oids.length; i < l; ++i) {
				dr = entities.get(i);
				oid = dr.getString("device_oid");
				if(oid != null && oid.length() > 0){ oids[i] = oid; }
				else { oids[i] = dr.getString("device_oid_2"); }
			}
			boolean isV3 = SrContant.SNMP_V3.equalsIgnoreCase(snmpInfo.getSnmpVersion());
			String agent = dir + "snmpget";
			String cmd = isV3? snmpUtil.getV3Command(agent, snmpInfo.getIpAddressV4(),
						snmpInfo.getSnmpV3UserName(), snmpInfo.getSnmpV3AuthProtocal(), 
						snmpInfo.getSnmpV3AuthPasswd(), snmpInfo.getSnmpV3EncryptProtocal(), 
						snmpInfo.getSnmpV3EncryptPasswd(), oids) 
						: snmpUtil.getV1V2Command(agent, snmpInfo.getIpAddressV4(), 
								snmpInfo.getSnmpVersion(), snmpInfo.getSnmpCommunity(), oids);
			JsonData result = new JsonData();
			snmpUtil.loadSwitchPerfData(cmd, result);
			if(result.isSuccess()) {
				DataRow data = (DataRow)result.getValue();
				try {
					service.saveIpSwitchPerData(data, snmpInfo.getSnmpId());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			else {
				logger.error(result.getMsg());
			}
		}
	}
}
