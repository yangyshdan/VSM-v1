package com.project.ipnetwork;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.deviceSnmp.DeviceSnmpService;
import com.huiming.sr.constants.SrContant;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;

public class SwitchConfigCollectorExecutor {
	private Logger logger = Logger.getLogger(SwitchConfigCollectorExecutor.class);
	private List<DataRow> entities;
	private TCfgDeviceSnmp snmpInfo;
	private SnmpUtil snmpUtil;
	private DeviceSnmpService service;
	
	public SwitchConfigCollectorExecutor(List<DataRow> entities, TCfgDeviceSnmp snmpInfo, SnmpUtil snmpUtil, 
			DeviceSnmpService service) {
		this.entities = entities;
		this.snmpInfo = snmpInfo;
		this.snmpUtil = snmpUtil;
		this.service = service;
	}
	void print(Object content, String mark){
		logger.info("***************************************************");
		logger.info(mark);
		logger.info(content);
		logger.info("***************************************************");
	}
	public void execute() {
		if(entities != null && entities.size() > 0 && snmpInfo != null && snmpUtil != null) {
			String dir = new File(MyUtilities.getClassDirectory(getClass())).getParent() + "/lib/net-snmp/bin/";
//			print(dir, "net-snmp目录");
			String oids[] = new String[entities.size()];
			DataRow dr;
			String oid;
			for(int i = 0, l = oids.length; i < l; ++i) {
				dr = entities.get(i);
				oid = dr.getString("device_oid");
				if(oid != null && oid.length() > 0){
					oids[i] = oid;
				}
				else {
					oids[i] = dr.getString("device_oid_2");
				}
			}
			boolean isV3 = SrContant.SNMP_V3.equalsIgnoreCase(snmpInfo.getSnmpVersion());
			String agent = dir + "snmpget";
			String cmd = isV3? snmpUtil.getV3Command(agent, snmpInfo.getIpAddressV4(),
						snmpInfo.getSnmpV3UserName(), snmpInfo.getSnmpV3AuthProtocal(), 
						snmpInfo.getSnmpV3AuthPasswd(), snmpInfo.getSnmpV3EncryptProtocal(), 
						snmpInfo.getSnmpV3EncryptPasswd(), oids) 
						: snmpUtil.getV1V2Command(agent, snmpInfo.getIpAddressV4(), 
								snmpInfo.getSnmpVersion(), snmpInfo.getSnmpCommunity(), oids);
//			print(cmd, "命令");
			JsonData result = new JsonData();
			snmpUtil.loadSwitchConfigData(cmd, result);
			if(result.isSuccess()) {
				DataRow data = (DataRow)result.getValue();
				try {
					data.set("snmp_id", snmpInfo.getSnmpId());
					data.set("update_timestamp", new Date());
					this.print(data, "data row");
					service.saveIpSwitchConfigData(data);
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
