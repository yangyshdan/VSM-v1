package root.deviceSnmp;

import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huiming.base.jdbc.DataRow;
import com.huiming.service.deviceSnmp.DeviceSnmpService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.ipnetwork.SnmpUtil;
import com.project.web.SecurityAction;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;

/**
 * @category 配置SNMP
 * @author 何高才
 *
 */
public class DeviceSnmpAction extends SecurityAction {
	private Logger logger = Logger.getLogger(DeviceSnmpAction.class);
	private DeviceSnmpService service = new DeviceSnmpService();
	
//	public ActionResult doDefault() {
//		List<DataRow> drs = null;
//		return new ActionResult("/WEB-INF/views/deviceSnmp/deviceSnmpList.jsp");
//	}
//	
//	public ActionResult doAjaxPage() {
//		
//		return new ActionResult("/WEB-INF/views/deviceSnmp/ajaxDeviceSnmp.jsp");
//	}
	
	public ActionResult doDevSnmpCfgDlg() {
		int snmpId = getIntParameter("snmpId", 0);
		if(snmpId > 0) {
			try {
				setAttribute("editSnmpInfo", JSON.toJSONString(service.getSnmpInfoById(snmpId)));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return new ActionResult("/WEB-INF/views/deviceSnmp/devSnmpCfgDlg.jsp");
	}
	
	public ActionResult doSnmpGroupDlg() {
		String actionType = getStrParameter("actionType");
		if("other".equalsIgnoreCase(actionType)) {
//			groups
		}
		else if("edit".equalsIgnoreCase(actionType)) {
			
		}
		return new ActionResult("/WEB-INF/views/deviceSnmp/groupDlg.jsp");
	}
	
	public void doGetGroup() {
		JsonData jsonData = new JsonData();
		int id = getIntParameter("groupId");
		try {
			DataRow dr = service.getGroupById(id);
			jsonData.setValue(dr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		print(jsonData);
	}
	
	/**
	 * @see 保存SNMP设备的组
	 */
	public void doSaveSnmpGroup() {
		JsonData jsonData = new JsonData();
		DataRow data = new DataRow();
		int groupId = getIntParameter("groupId", 0);
		data.set("group_name", MyUtilities.htmlToText(getStrParameter("groupName")));
		data.set("description", MyUtilities.htmlToText(getStrParameter("groupDesc")));
		data.set("update_timestamp", new Date());
		try {
			if(groupId <= 0) { service.saveSnmpGroup(data); }
			else { service.updateSnmpGroup(data, groupId); }
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		print(jsonData);
	}
	
	public void doDeleteSnmpDetail() {
		JsonData jsonData = new JsonData();
		int snmpId = getIntParameter("snmpId", 0);
		if(snmpId <= 0) {
			jsonData.setSuccess(false);
			jsonData.setMsg("不存在这个SNMP设备配置！");
		}
		else {
			try {
				service.deleteSnmpDetail(snmpId);
			} 
			catch (Exception e) {
				logger.error(e.getMessage(), e);
				jsonData.setSuccess(false);
				jsonData.setMsg(e.getMessage());
			}
		}
		print(jsonData);
	}
	
	public void doDeleteGroupById() {
		JsonData jsonData = new JsonData();
		int groupId = getIntParameter("groupId", 0);
		if(groupId <= 0) {
			jsonData.setSuccess(false);
			jsonData.setMsg("不存在这个组！");
		}
		else {
			try {
				service.delSnmpGroup(groupId);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jsonData.setSuccess(false);
				jsonData.setMsg(e.getMessage());
			}
		}
		print(jsonData);
	}
	
	public void doSaveSnmpInfo() {
		JsonData jsonData = new JsonData();
		int snmpId = getIntParameter("snmpId", 0);
		String ipAddress = getStrParameter("ipAddress");
		
		if(snmpId <= 0) {  // 添加
			if(service.isIpAddressExist(ipAddress)) {
				jsonData.setMsg("该IP地址对应的SNMP配置信息已存在");
				jsonData.setSuccess(false);
				print(jsonData);
				return;
			}
		}
		SnmpUtil util = new SnmpUtil();
		int snmpRetry = getIntParameter("snmpRetry", 3);
		String snmpVersion = getStrParameter("snmpVersion");
		boolean isV3 = SrContant.SNMP_V3.equals(snmpVersion);
		try {
			String cmd;
			String oids[] = new String[]{""};
			if(isV3) {
				String snmpV3UserName = MyUtilities.htmlToText(getStrParameter("snmpV3UserName"));
				String snmpV3AuthProtocal = getStrParameter("snmpV3AuthProtocal");
				String snmpV3AuthPasswd = MyUtilities.htmlToText(getStrParameter("snmpV3AuthPasswd"));
				String snmpV3EncryptProtocal = getStrParameter("snmpV3EncryptProtocal");
				String snmpV3EncryptPasswd = MyUtilities.htmlToText(getStrParameter("snmpV3EncryptPasswd"));
				cmd = util.getV3Command("snmpstatus", ipAddress, snmpV3UserName, 
						snmpV3AuthProtocal, snmpV3AuthPasswd, 
						snmpV3EncryptProtocal, snmpV3EncryptPasswd, oids);
			}
			else {
				String snmpCommunity = MyUtilities.htmlToText(getStrParameter("snmpCommunity"));
				cmd = util.getV1V2Command("snmpstatus", ipAddress, snmpVersion, snmpCommunity, oids);
			}
			logger.info("*************************************************");
			logger.info(cmd);
			for(int i = 0, len = snmpRetry; i < len; ++i) {
				util.testSnmp(cmd, jsonData);
				if(jsonData.isSuccess()) {
					jsonData.setMsg("成功连上SNMP终端设备！");
					break;
				} // 如果成功了则跳出循环
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		if(jsonData.isSuccess()) { // 如果成功了
			DataRow dr = new DataRow();
			dr.set("group_id", getIntParameter("groupId"));
			dr.set("snmp_retry", snmpRetry);
			dr.set("device_id", getIntParameter("deviceId"));
			dr.set("enabled", getIntParameter("snmpEnabled"));
			dr.set("description", MyUtilities.htmlToText(getStrParameter("snmpDesc")));
			dr.set("snmp_version", getStrParameter("snmpVersion"));
			dr.set("ip_address_v4", ipAddress);
			if(isV3) {
				String snmpV3UserName = MyUtilities.htmlToText(getStrParameter("snmpV3UserName"));
				String snmpV3AuthProtocal = getStrParameter("snmpV3AuthProtocal");
				String snmpV3AuthPasswd = MyUtilities.htmlToText(getStrParameter("snmpV3AuthPasswd"));
				String snmpV3EncryptProtocal = getStrParameter("snmpV3EncryptProtocal");
				String snmpV3EncryptPasswd = MyUtilities.htmlToText(getStrParameter("snmpV3EncryptPasswd"));
				dr.set("snmp_v3_user_name", snmpV3UserName);
				if(!snmpV3AuthProtocal.equalsIgnoreCase("none")) {
					dr.set("snmp_v3_auth_protocal", snmpV3AuthProtocal);
					dr.set("snmp_v3_auth_passwd", snmpV3AuthPasswd);
					if(!snmpV3EncryptProtocal.equalsIgnoreCase("none")) {
						dr.set("snmp_v3_encrypt_protocal", snmpV3EncryptProtocal);
						dr.set("snmp_v3_encrypt_passwd", snmpV3EncryptPasswd);
					}
				}
			}
			else {
				dr.set("snmp_community", MyUtilities.htmlToText(getStrParameter("snmpCommunity")));
			}
			
			try {
				if(snmpId <= 0) {
					service.saveSnmpInfo(dr);
				}
				else {
					service.updateSnmpInfo(dr, snmpId);
				}
			} catch (Exception e) {
				jsonData.setSuccess(false);
				jsonData.setMsg(e.getMessage());
				logger.error(e.getMessage(), e);
			}
		}
		print(jsonData);
	}
	
	public void doTestSnmp() {
		SnmpUtil util = new SnmpUtil();
		JsonData jsonData = new JsonData();
		String ipAddress = getStrParameter("ipAddress");
		int snmpRetry = getIntParameter("snmpRetry", 3);
		String snmpVersion = getStrParameter("snmpVersion");
		
		// 使用该命令来测试 snmpstatus -v 2c -c public 192.168.1.21
		try {
			String cmd;
			String oids[] = new String[]{""};
			if(SrContant.SNMP_V3.equals(snmpVersion)) {
				String snmpV3UserName = getStrParameter("snmpV3UserName");
				String snmpV3AuthProtocal = getStrParameter("snmpV3AuthProtocal");
				String snmpV3AuthPasswd = getStrParameter("snmpV3AuthPasswd");
				String snmpV3EncryptProtocal = getStrParameter("snmpV3EncryptProtocal");
				String snmpV3EncryptPasswd = getStrParameter("snmpV3EncryptPasswd");
				cmd = util.getV3Command("snmpstatus", ipAddress, snmpV3UserName, 
						snmpV3AuthProtocal, snmpV3AuthPasswd, 
						snmpV3EncryptProtocal, snmpV3EncryptPasswd, oids);
			}
			else {
				String snmpCommunity = getStrParameter("snmpCommunity");
				cmd = util.getV1V2Command("snmpstatus", ipAddress, snmpVersion, snmpCommunity, oids);
			}
			logger.info("*************************************************");
			logger.info(cmd);
			for(int i = 0, len = snmpRetry; i < len; ++i) {
				util.testSnmp(cmd, jsonData);
				if(jsonData.isSuccess()) {
					jsonData.setMsg("成功连上SNMP终端设备！");
					break;
				} // 如果成功了则跳出循环
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		print(jsonData);
	}
	
	public void doGetGroupDevice() {
		JsonData jsonData = new JsonData();
		JSONObject obj = new JSONObject();
		try {
			obj.put("group", service.getGroupsIdNames());
			obj.put("device", service.getDeviceIdNames());
			jsonData.setValue(obj);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		print(jsonData);
	}
}
