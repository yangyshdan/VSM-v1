package com.project.ipnetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.deviceSnmp.DeviceSnmpService;
import com.huiming.sr.constants.SrContant;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;
import com.project.x86monitor.beans.SnmpBean;

public class PortConfigExecutor {
	private Logger logger = Logger.getLogger(PortConfigExecutor.class);
	private List<DataRow> entities;
	private TCfgDeviceSnmp snmpInfo;
	private SnmpUtil snmpUtil;
	private DeviceSnmpService service;
	
	public PortConfigExecutor(List<DataRow> entities, TCfgDeviceSnmp snmpInfo, SnmpUtil snmpUtil, 
			DeviceSnmpService service) {
		this.entities = entities;
		this.snmpInfo = snmpInfo;
		this.snmpUtil = snmpUtil;
		this.service = service;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
		if(entities != null && entities.size() > 0 && snmpInfo != null && snmpUtil != null) {
			DataRow sw = service.getSwitchBySnmpId(snmpInfo.getSnmpId());
			int switchId = 0;
			int portCount = 0;
			if(sw != null){
				switchId = sw.getInt("switch_id");
				portCount = sw.getInt("port_count");
			}
			if(switchId == 0 && portCount == 0){ return; }
			String dir = new File(MyUtilities.getClassDirectory(getClass())).getParent() + "/lib/net-snmp/bin/";
			boolean isV3 = SrContant.SNMP_V3.equalsIgnoreCase(snmpInfo.getSnmpVersion());
			///////////////// 获得接口与端口的映射
			JsonData interf2PortResult = new JsonData();
			String agent = dir + "snmpwalk";
			String interf2PortOids[] = { entities.get(0).getString("device_oid") };
			String cmd = isV3? snmpUtil.getV3Command(agent, snmpInfo.getIpAddressV4(),
					snmpInfo.getSnmpV3UserName(), snmpInfo.getSnmpV3AuthProtocal(), 
					snmpInfo.getSnmpV3AuthPasswd(), snmpInfo.getSnmpV3EncryptProtocal(), 
					snmpInfo.getSnmpV3EncryptPasswd(), interf2PortOids) 
					: snmpUtil.getV1V2Command(agent, snmpInfo.getIpAddressV4(), 
							snmpInfo.getSnmpVersion(), snmpInfo.getSnmpCommunity(), interf2PortOids);
			snmpUtil.loadInterface2Port(cmd, interf2PortResult);
			if(!interf2PortResult.isSuccess()){
				// 不成功就将报错信息打印出来
				logger.error(interf2PortResult.getMsg());
				return;
			}
			// 把端口与接口的映射拿出来
			Map<Integer, Integer> interf2Port = (Map<Integer, Integer>)interf2PortResult.getValue();
			if(portCount != interf2Port.size()) {
				// 交换机获得的端口数量与现在获得的端口数量不一致
				String errMsg = "交换机获得的端口数量与使用Interface2Port的命令获得的端口数量不一致, 校正交换机获得端口数量的OID";
				logger.error("", new IllegalArgumentException(errMsg));
				return;
			}
			// 保存结果
			List<DataRow> portConfig = new ArrayList<DataRow>(interf2Port.size());
			Date updateTimestamp = new Date();
			for(Integer key : interf2Port.keySet()) {
				DataRow dr = new DataRow();
				dr.set("interface_index", key);
				dr.set("port_number", interf2Port.get(key));
				dr.set("update_timestamp", updateTimestamp);
				dr.set("snmp_id", snmpInfo.getSnmpId());
				dr.set("switch_id", switchId);
				portConfig.add(dr);
			}
			// 开始合成OID
			DataRow dr;
			String oid;
			// "vlan_index", "vlan_name"   "port_number", "interface_index"
			/*String columns[] = {"port_name", "port_type",
					"availability", "admin_status", "port_mtu", "port_speed", 
					"last_change_timeticks", "link_up_down_trap_enable", "high_speed", "promiscuous_mode", 
					"connector_present", "port_alias", "port_description"};*/
			String empty[] = {" "};
			agent = dir + "snmpget"; // 使用get命令，精确定位所需的交换机端口配置数据
			cmd = isV3? snmpUtil.getV3Command(agent, snmpInfo.getIpAddressV4(),
					snmpInfo.getSnmpV3UserName(), snmpInfo.getSnmpV3AuthProtocal(), 
					snmpInfo.getSnmpV3AuthPasswd(), snmpInfo.getSnmpV3EncryptProtocal(), 
					snmpInfo.getSnmpV3EncryptPasswd(), empty) 
					: snmpUtil.getV1V2Command(agent, snmpInfo.getIpAddressV4(), 
							snmpInfo.getSnmpVersion(), snmpInfo.getSnmpCommunity(), empty);
			snmpUtil.print(cmd, "命令");
			// 因为第一个是port_number的OID用于获得端口号与接口索引的映射，从而避免ifindex
			// 尽可能减少OID，减少SNMP的查询
			List<SnmpBean> beans = null;
			String column;
			for(int i = 2, l = entities.size(), s = interf2Port.size(), j; i < l; ++i) {
				dr = entities.get(i);
				oid = dr.getString("device_oid");
				if(oid == null || oid.length() == 0){
					oid = dr.getString("device_oid_2");
				}
				StringBuilder sb = new StringBuilder(cmd.length() + oid.length() + s * 2);
				sb.append(cmd);
				for(Integer key : interf2Port.keySet()) {
					sb.append(oid);
					sb.append('.');
					sb.append(key);
					sb.append(' ');
				}
				JsonData result = new JsonData();
				result.setValue(s);
				snmpUtil.loadPortConfigData(sb.toString(), result);
				
				if(result.isSuccess()) {
					beans = (List<SnmpBean>) result.getValue();
					if(beans.size() != s) {
						logger.error("", new IllegalArgumentException("获取交换机端口的结果与预期结果不一致, OID是" + oid));
					}
					column = dr.getString("device_entity");
					for(j = 0; j < s; ++j) {
						portConfig.get(j).set(column, beans.get(j).getDataValue());
					}
				}
				else {
					logger.error(result.getMsg());
				}
				
			}
//			snmpUtil.print(portConfig, "插入数据库的");
			try {
				service.savePortConfigData(portConfig);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
