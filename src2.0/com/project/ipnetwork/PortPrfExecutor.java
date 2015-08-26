package com.project.ipnetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DataRow;
import com.huiming.service.deviceSnmp.DeviceSnmpService;
import com.huiming.sr.constants.SrContant;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;
import com.project.x86monitor.beans.SnmpBean;

/**
 * @category 搜集端口的性能数据的执行者
 * @author 何高才
 *
 */
public class PortPrfExecutor {
	private Logger logger = Logger.getLogger(PortPrfExecutor.class);
	private List<DataRow> entities;
	private TCfgDeviceSnmp snmpInfo;
	private SnmpUtil snmpUtil;
	private DeviceSnmpService service;
	
	public PortPrfExecutor(List<DataRow> entities, TCfgDeviceSnmp snmpInfo, SnmpUtil snmpUtil, 
			DeviceSnmpService service) {
		this.entities = entities;
		this.snmpInfo = snmpInfo;
		this.snmpUtil = snmpUtil;
		this.service = service;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
		if(entities != null && entities.size() > 0 && snmpInfo != null && snmpUtil != null) {
			DataRow switch_ = service.getSwitchBySnmpId(snmpInfo.getSnmpId());
			String switchName = "";
			int switchId = 0;
			if(switch_ != null) {
				switchName = switch_.getString("switch_name");
				switchId = switch_.getInt("switch_id");
			}
			if(switchId == 0) { return; }
			List<DataRow> ports = service.getPortBySnmpId(snmpInfo.getSnmpId());
			if(ports == null || ports.size() == 0){ return; }
			String dir = new File(MyUtilities.getClassDirectory(getClass())).getParent() + "/lib/net-snmp/bin/";
			boolean isV3 = SrContant.SNMP_V3.equalsIgnoreCase(snmpInfo.getSnmpVersion());
			
			// 开始合成OID
			DataRow dr;
			String oid;
			String empty[] = {" "};
			String agent = dir + "snmpget"; // 使用get命令，精确定位所需的交换机端口配置数据
			String cmd = isV3? snmpUtil.getV3Command(agent, snmpInfo.getIpAddressV4(),
					snmpInfo.getSnmpV3UserName(), snmpInfo.getSnmpV3AuthProtocal(), 
					snmpInfo.getSnmpV3AuthPasswd(), snmpInfo.getSnmpV3EncryptProtocal(), 
					snmpInfo.getSnmpV3EncryptPasswd(), empty) 
					: snmpUtil.getV1V2Command(agent, snmpInfo.getIpAddressV4(), 
							snmpInfo.getSnmpVersion(), snmpInfo.getSnmpCommunity(), empty);
			snmpUtil.print(cmd, "命令");
			// 因为第一个是port_number的OID用于获得端口号与接口索引的映射，从而避免ifindex
			// 尽可能减少OID，减少SNMP的查询
			List<SnmpBean> beans = null;
			String oids[] = new String[entities.size()];
			String columns[] = new String[oids.length];
			for(int i = 0, l = oids.length; i < l; ++i) {
				dr = entities.get(i);
				oid = dr.getString("device_oid");
				if(oid == null || oid.length() == 0){ oid = dr.getString("device_oid_2"); }
				oids[i] = oid;
				columns[i] = dr.getString("device_entity");
			}
			int capacity = cmd.length() + entities.size() * 20;
			
			long values[][] = new long[ports.size()][oids.length];
			String cmds[] = new String[ports.size()];
			for(int p = 0, pL = ports.size(); p < pL; ++p) { // 每一个端口拿一次性能
				DataRow port = ports.get(p);
				StringBuilder sb = new StringBuilder(capacity);
				sb.append(cmd);
				for(int i = 0, l = oids.length, ifindex = port.getInt("interface_index"); i < l; ++i) {
					sb.append(oids[i]);
					sb.append('.');
					sb.append(ifindex);
					sb.append(' ');
				}
				cmds[p] = sb.toString();
				JsonData result = new JsonData();
				result.setValue(oids.length);
				snmpUtil.loadPortPerfData(cmds[p], result);
				beans = (List<SnmpBean>)result.getValue();
				if(beans != null && beans.size() > 0) {
					for(int k = 0; k < oids.length; ++k) {
						values[p][k] = snmpUtil.parseLong(beans.get(k).getDataValue());
					}
				}
			}
			// 过了5分钟再去取
			try {
				Thread.sleep(30000L);  // 杨总的话，建议Time0跟Time1之间的时间差至少是5分钟
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
				return;  // 时间中断异常  抛弃这些数据
			}
			/////////////
			float seconds = 5 * 60.0f;
			List<DataRow> timestamps = new ArrayList<DataRow>(ports.size());
			for(int p = 0, pL = ports.size(); p < pL; ++p) { // 每一个端口拿一次性能
				Date date = new Date();
				ports.get(p).set("time_id", date.getTime());
				DataRow dr2 = new DataRow();
				dr2.set("sample_time", date);
				dr2.set("interval_len", 300);  // 5分钟，不建议5分钟
				dr2.set("summ_type", SrContant.SUMM_TYPE_REAL);
				dr2.set("subsystem_name", switchName);
				dr2.set("subsystem_id", switchId);
				dr2.set("device_type", SrContant.SUBDEVTYPE_IPNW_SWITCH_PORT);
				timestamps.add(dr2);
				
				JsonData result = new JsonData();
				result.setValue(oids.length);
				snmpUtil.loadPortPerfData(cmds[p], result);
				beans = (List<SnmpBean>)result.getValue();
				if(beans != null && beans.size() > 0) {
					for(int k = 0; k < oids.length; ++k) {
						ports.get(p).set(columns[k], 
							Math.abs(values[p][k] - snmpUtil.parseLong(beans.get(k).getDataValue())) / seconds);
					}
				}
				
			}
			logger.info(JSON.toJSONString(ports));
//			snmpUtil.print(portConfig, "插入数据库的");
			try {
				service.savePortPerfData(ports, timestamps);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}

