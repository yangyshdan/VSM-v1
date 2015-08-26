package root.usercon;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.AES;
import com.huiming.base.util.security.DES;
import com.huiming.service.arraysite.ArraysiteService;
import com.huiming.service.library.LibraryService;
import com.huiming.service.storage.StorageService;
import com.huiming.service.switchs.SwitchService;
import com.huiming.service.topo.TopoService;
import com.huiming.service.usercon.UserConService;
import com.huiming.service.virtualmachine.VirtualmachineService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.hmc.core.HmcBase;
import com.project.hmc.engn.ComputerSystem;
import com.project.hmc.engn.LibvirtEngine;
import com.project.hmc.engn.VirtualMac;
import com.project.nmon.engn.Scp_Sftp;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.DataCollectConfig;
import com.project.x86monitor.DeviceInfo;
import com.project.x86monitor.IPMIInfo;
import com.project.x86monitor.IPMIUtil;
import com.project.x86monitor.MyUtilities;

import csharpwmi.CSharpWMIClass;
import csharpwmi.ICSharpWMIClass;
public class CopyOfUserConAction extends SecurityAction{

	private static final Logger logger = Logger.getLogger(CopyOfUserConAction.class);
	UserConService service = new UserConService();
	VirtualmachineService virtualService = new VirtualmachineService();
	TopoService topoService = new TopoService();
	
	/**
	 * 系统配置页面
	 */
	public ActionResult doDefault(){
		DBPage serverPage = null;
		DBPage storageCfgPage = null;
		DBPage storagePage = null;
		DBPage labraryPage = null;
		DBPage switchPage = null;
		DBPage arraysitePage = null;
		DBPage nasPage = null;
		
		DBPage snmpPage = null;
		
		int curPage = getIntParameter("curPage", 1);
		DBPage hostPage = null;
		
		int numPerPage = WebConstants.NumPerPage;
		//服务器
		serverPage = service.getServerPage(null,null,null,curPage,numPerPage);
		//存储系统
		storageCfgPage = service.getStorageCfgPage(null, null, curPage, numPerPage);
		//磁带库
		labraryPage = service.getDevicePage(curPage, numPerPage, null, 4);
		//存储系统
		storagePage = service.getDevicePage(curPage, numPerPage, null, 1);
		//交换机
		switchPage = service.getDevicePage(curPage, numPerPage, null, 2);
		//磁盘阵列
		arraysitePage = service.getDevicePage(curPage, numPerPage, null, 3);
		//NAS
		nasPage = service.getDevicePage(curPage, numPerPage, null, 5);
		//SNMP int curPage, int numPerPage, String ipAddress, String groupName, String snmpVersion, boolean enabled);
		snmpPage = service.getDeviceSnmpPage(curPage, numPerPage, null, null, null, null);
		
		setAttribute("serverPage", serverPage);
		setAttribute("storageCfgPage", storageCfgPage);
		setAttribute("labraryPage", labraryPage);
		setAttribute("storagePage", storagePage);
		setAttribute("switchPage", switchPage);
		setAttribute("arraysitePage", arraysitePage);
		setAttribute("nasPage", nasPage);
		setAttribute("snmpPage", snmpPage);
		
		//初始化下拉列表
		initSelectItem();
		this.setAttribute("hostPage", hostPage);
		return new ActionResult("/WEB-INF/views/usercon/userconList.jsp");
	}
	
	
	public ActionResult doAjaxDeviceSnmpPage(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		String ipAddress = getStrParameter("ipAddress");
		String groupName = getStrParameter("groupName");
		String snmpVersion = getStrParameter("snmpVersion");
		String enabled = getStrParameter("enabled", "-1");
		//SNMP 
		DBPage snmpPage = service.getDeviceSnmpPage(curPage, numPerPage, ipAddress, groupName,
				snmpVersion, enabled.equals("-1")? null : enabled.equals("1"));
		setAttribute("snmpPage", snmpPage);
		return new ActionResult("/WEB-INF/views/usercon/ajaxDeviceSnmp.jsp");
	}
	
	/**
	 * 加载配置信息列表
	 * @return
	 */
	public ActionResult doAjaxDevicePage(){
		DBPage storagePage = null;
		DBPage labraryPage = null;
		DBPage switchPage = null;
		DBPage arraysitePage = null;
		DBPage nasPage = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		String labraryname = getStrParameter("labraryname");
		String storagename = getStrParameter("storagename");
		String switchname = getStrParameter("switchname");
		String arrayname = getStrParameter("arrayname");
		String nasname = getStrParameter("nasname");
		Integer typeId = getIntParameter("typeId");
		//磁带库
		if (typeId == 4) {
			labraryPage = service.getDevicePage(curPage, numPerPage, labraryname, 4);
			this.setAttribute("labraryPage", labraryPage);
			return new ActionResult("/WEB-INF/views/usercon/labraryPage.jsp");
		//存储系统
		} else if (typeId == 1) {
			storagePage = service.getDevicePage(curPage, numPerPage, storagename, 1);
			this.setAttribute("storagePage", storagePage);
			return new ActionResult("/WEB-INF/views/usercon/storagePage.jsp");
		//交换机
		} else if (typeId == 2) {
			switchPage = service.getDevicePage(curPage, numPerPage, switchname, 2);
			this.setAttribute("switchPage", switchPage);
			return new ActionResult("/WEB-INF/views/usercon/switchPage.jsp");
		//磁盘阵列
		} else if (typeId == 3) {
			arraysitePage = service.getDevicePage(curPage, numPerPage, arrayname, 3);
			this.setAttribute("arraysitePage", arraysitePage);
			return new ActionResult("/WEB-INF/views/usercon/arraysitePage.jsp");
		//NAS
		} else if (typeId == 5) {
			nasPage = service.getDevicePage(curPage, numPerPage, nasname, 5);
			this.setAttribute("nasPage", nasPage);
			return new ActionResult("/WEB-INF/views/usercon/nasPage.jsp");
		} else {
			return null;
		}
	}
	
	/**
	 * 初始化厂商,架构类型和虚拟化平台类型列表
	 */
	public void initSelectItem() {
		//厂商
		Map<String, String> vendors = new HashMap<String, String>();
		vendors.put(WebConstants.VENDOR_IBM, WebConstants.VENDOR_IBM);
		vendors.put(WebConstants.VENDOR_LENOVO, WebConstants.VENDOR_LENOVO);
		vendors.put(WebConstants.VENDOR_DELL, WebConstants.VENDOR_DELL);
		vendors.put(WebConstants.VENDOR_HUAWEI, WebConstants.VENDOR_HUAWEI);
		vendors.put(WebConstants.VENDOR_HP, WebConstants.VENDOR_HP);
		vendors.put(WebConstants.VENDOR_INSPUR, WebConstants.VENDOR_INSPUR);
		vendors.put(WebConstants.VENDOR_SUGON, WebConstants.VENDOR_SUGON);
		vendors.put(WebConstants.VENDOR_OTHER, WebConstants.VENDOR_OTHER);
		//架构类型
		Map<String, String> schemaTypes = new HashMap<String, String>();
		schemaTypes.put(WebConstants.SCHEMA_TYPE_X86, WebConstants.SCHEMA_TYPE_X86);
		schemaTypes.put(WebConstants.SCHEMA_TYPE_POWER, WebConstants.SCHEMA_TYPE_POWER);
		//操作系统
		Map<String, String> osTypes = new HashMap<String, String>();
		osTypes.put(WebConstants.OSTYPE_LINUX, WebConstants.OSTYPE_LINUX);
		osTypes.put(WebConstants.OSTYPE_WINDOWS, WebConstants.OSTYPE_WINDOWS);
		osTypes.put(WebConstants.OSTYPE_ESXI, WebConstants.OSTYPE_ESXI);
//		osTypes.put(WebConstants.OSTYPE_UNIX, WebConstants.OSTYPE_UNIX);
		//虚拟化平台类型
		Map<String, String> virtPlatTypes = new HashMap<String, String>();
		virtPlatTypes.put(WebConstants.VIRT_PLAT_TYPE_KVM, WebConstants.VIRT_PLAT_TYPE_KVM);
		virtPlatTypes.put(WebConstants.VIRT_PLAT_TYPE_VMWARE, WebConstants.VIRT_PLAT_TYPE_VMWARE);
		virtPlatTypes.put(WebConstants.VIRT_PLAT_TYPE_XENSERVER, WebConstants.VIRT_PLAT_TYPE_XENSERVER);
		virtPlatTypes.put(WebConstants.VIRT_PLAT_TYPE_HYPER_V, WebConstants.VIRT_PLAT_TYPE_HYPER_V);
		virtPlatTypes.put(WebConstants.VIRT_PLAT_TYPE_NO, WebConstants.VIRT_PLAT_TYPE_NO);
		//获取用户可见的交换机
		List<DataRow> switchList = null;
		String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		//判断是否有TPC配置
		if (Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null) {
			switchList = service.getAllSwitches(limitIds);
		}
		switchList = switchList == null ? new ArrayList<DataRow>() : switchList;
		
		//设置到session中
		getSession().setAttribute("vendors", vendors);
		getSession().setAttribute("schemaTypes", schemaTypes);
		getSession().setAttribute("osTypes", osTypes);
		getSession().setAttribute("virtPlatTypes", virtPlatTypes);
		getSession().setAttribute("switchList", switchList);
	}
	
	/**
	 * 删除配置信息
	 */
	public void doDeviceDel(){
		String id = getStrParameter("id");
		int typeId = getIntParameter("typeId");
		if (id != null && id.length() > 0) {
			//删除服务器配置信息
			if (typeId == 6) {
				service.delServerConfigInfo(id);
			//存储系统配置信息
			} else if (typeId == 7) {
				service.delStorageConfigInfo(id);
			} else {
				service.deviceDel(id);
			}
			ResponseHelper.print(getResponse(), "true");
		} else {
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	/**
	 * 加载服务器配置列表
	 * @return
	 */
	public ActionResult doServerPage(){
		DBPage serverPage = null;
		String serverName = getStrParameter("serverName");
		String serverType = getStrParameter("serverType");
		String state = getStrParameter("state");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		serverPage = service.getServerPage(serverName, serverType, state, curPage, numPerPage);
		this.setAttribute("serverPage", serverPage);
		return new ActionResult("/WEB-INF/views/usercon/serverPage.jsp");
	}
	
	/**
	 * 服务器配置页面
	 * @return
	 */
	@SuppressWarnings({"static-access"})
	public ActionResult doEditServerInfo() {
		String id = getStrParameter("id");
		DataRow serverRow = null;
		List<DataRow> mapSwitchList = null;
		List<DataRow> virtList = null;
		if (id != null && id.length() > 0) {
			serverRow = service.getServerConfigInfo(id);
			String serverType = serverRow.getString("toptype");
			serverRow.set("password", new AES(id).decrypt(serverRow.getString("password"),"UTF-8"));
			if (serverType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
				//获取连接的交换机
				mapSwitchList = service.getServerSwitchMap(id);
			} else if (serverType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)) {
				//获取虚拟机配置信息
				DataRow virtRow = virtualService.getVirtualInfoByHmcId(id);
				if (virtRow != null) {
					serverRow.set("vm_id", virtRow.getString("vm_id"));
				}
				virtList = new ArrayList<DataRow>();
				virtList.add(serverRow);
			}
		}
		setAttribute("serverInfo", new JSONObject().fromObject(serverRow));
		setAttribute("switchs", new JSONArray().fromObject(getSession().getAttribute("switchList")));
		setAttribute("mapSwitchs", new JSONArray().fromObject(mapSwitchList));
		setAttribute("virtList", new JSONArray().fromObject(virtList));
		return new ActionResult("/WEB-INF/views/usercon/editServer.jsp");
	}
	
	/**
	 * 验证并保存物理机配置信息
	 */
	@SuppressWarnings("unchecked")
	public void doTestAndSavePhysiConfig() {
		JSONObject resJsonObject = new JSONObject();
		try {
			String phyHmcId = getStrParameter("physicalId");
			String vendor = getStrParameter("vendor");
			String model = getStrParameter("model");
			String schemaType = getStrParameter("schemaType");
			String osType = getStrParameter("osType");
			String[] switchIds = getStrArrayParameter("switch");
			String virtPlatType = getStrParameter("virtPlatType");
			String ipAddress = getStrParameter("ipAddress");
			String user = getStrParameter("user").replaceAll("&amp;nbsp;", " ");
			String password = getStrParameter("password").replaceAll("&amp;nbsp;", " ");
			String description = getStrParameter("desc");
			String serverName = null;
			int state = 0;
			boolean isAuth = false;
			DataRow serverRow = new DataRow();
			
			//验证该配置是否存在
			if (StringHelper.isEmpty(phyHmcId)) {
				if (service.hasServerConfigInfo(ipAddress, user, SrContant.SUBDEVTYPE_PHYSICAL.toLowerCase())) {
					resJsonObject.put("result", "has_user");
					return;
				}
			}
			//根据操作系统类型,对用户录入的物理机配置进行检测
			HmcBase hmcBase = null;
			Session session = null;
			//For LINUX
			if (osType.equals(WebConstants.OSTYPE_LINUX)) {
				try {
					hmcBase = new HmcBase(ipAddress, 22, user, password);
					session = hmcBase.openConn();
					//验证通过
					if (session != null) {
						state = 1;
						isAuth = true;
					}
				} finally {
					hmcBase.closeConn();
				}
				
			//For Windows
			} else if (osType.equals(WebConstants.OSTYPE_WINDOWS)) {
				ICSharpWMIClass cswmi = null;
				try {
					MyUtilities.initBridge(this.getClass());
					//默认设置authentication = 3,impersonation = 7,页面暂时不接收用户录入
					cswmi = new CSharpWMIClass(new String[]{ipAddress}, user, password, 3, 7);
					serverName = cswmi.GetComputerName();
					//连接成功
					if (cswmi.IsDeviceConnectedByWMI()) {
						serverRow.set("impersonation", 7);
						serverRow.set("authentication", 3);
						state = 1;
						isAuth = true;
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			//For ESXi
			} else if (osType.equals(WebConstants.OSTYPE_ESXI)) {
				LibvirtEngine libvirtEngine = new LibvirtEngine(WebConstants.VIRT_PLAT_TYPE_VMWARE, ipAddress, user, password);
				if (libvirtEngine.getConnect() != null) {
					state = 1;
					isAuth = true;
				}
			}
			//验证通过,保存配置信息到数据库
			serverRow.set("id", phyHmcId);
			serverRow.set("name", serverName == null ? null : serverName);
			serverRow.set("ip_address", ipAddress);
			serverRow.set("description", description);
			serverRow.set("user", user);
			serverRow.set("state", state);
			serverRow.set("vendor", vendor);
			serverRow.set("model", model);
			serverRow.set("toptype", SrContant.SUBDEVTYPE_PHYSICAL.toLowerCase());
			serverRow.set("os_type", osType);
			serverRow.set("schema_type", schemaType);
			serverRow.set("virt_plat_type", virtPlatType);
			if (isAuth) {
				//插入物理机配置信息
				phyHmcId = service.saveServerConfigInfo(serverRow);
				//修改配置信息密码
				service.updateServerConfigPassword(phyHmcId, new AES(phyHmcId).encrypt(password,"UTF-8"));
				
				//采集配置信息
				//For LINUX and ESXi
				if (osType.equals(WebConstants.OSTYPE_ESXI) || osType.equals(WebConstants.OSTYPE_LINUX)) {
					ComputerSystem computerSystem = new ComputerSystem();
					computerSystem.getPhysicalAndVirtualConfigInfo(phyHmcId);
				//For Windows
				} else if (osType.equals(WebConstants.OSTYPE_WINDOWS)) {
					MyUtilities.initBridge(this.getClass());
					ICSharpWMIClass cswmi = new CSharpWMIClass(new String[]{ipAddress}, user, password, 3, 7);
					DeviceInfo deviceInfo = new DeviceInfo();
					deviceInfo.setIpAddress(ipAddress);
					deviceInfo.setUsername(user);
					deviceInfo.setPassword(password);
					deviceInfo.setAuthentication(3);
					deviceInfo.setImpersonate(7);
					deviceInfo.setToptype(SrContant.SUBDEVTYPE_PHYSICAL);
					new DataCollectConfig(cswmi, deviceInfo).execute();
				}
				
//				//Power架构
//				if (schemaType.equals(WebConstants.SCHEMA_TYPE_POWER)) {
//					//立即去采集配置信息
//					ComputerSystem computerSystem = new ComputerSystem();
//					VirtualMac virtualMac = new VirtualMac();
//					computerSystem.getResult();
//					virtualMac.getResult();
//				}
				//获取配置的物理机ID
				String hypervisorId = service.getPhysicalInfoByConfigId(phyHmcId).getString("hypervisor_id");
				//修改物理机HMC_ID
				service.updatePhysicHmcId(phyHmcId, hypervisorId);
				
				//保存物理机和交换机的关联关系到数据库
				List<DataRow> allSwitchList = (List<DataRow>) getSession().getAttribute("switchList");
				List<DataRow> addPhySwitchList = new ArrayList<DataRow>();
				DataRow phySwitchRow = null;
				for (int i = 0; i < switchIds.length; i++) {
					phySwitchRow = new DataRow();
					phySwitchRow.set("hypervisor_id", hypervisorId); 
					phySwitchRow.set("switch_id", switchIds[i]);
					for (int j = 0; j < allSwitchList.size(); j++) {
						DataRow switchRow = allSwitchList.get(j);
						if (switchIds[i].equals(switchRow.getString("hypswid"))) {
							phySwitchRow.set("switch_name", switchRow.getString("hypswname"));
							phySwitchRow.set("switch_ip_address", switchRow.getString("hypswip"));
							break;
						}
					}
					addPhySwitchList.add(phySwitchRow);
				}
				service.saveHyperSwitchMap(addPhySwitchList);
				
				//设置物理机的ID
				resJsonObject.put("physicalId", hypervisorId);
				//获取BMC配置信息
				DataRow bmcInfo = service.getBmcConfigInfo(hypervisorId);
				if (bmcInfo != null) {
					String id = bmcInfo.getString("id");
					String bmcPwd = bmcInfo.getString("session_pwd");
					if (bmcPwd != null && bmcPwd.length() > 0) {
						bmcInfo.set("session_pwd", new AES(id).decrypt(bmcPwd, "UTF-8"));
					}
				}
				resJsonObject.put("bmcInfo", bmcInfo);
				//选择了虚拟化平台,获取虚拟机
				if (!virtPlatType.equals(WebConstants.VIRT_PLAT_TYPE_NO)) {
					//获取该物理机下的虚拟机
					List<DataRow> virtList = getVirtualMachineList(hypervisorId);
					resJsonObject.put("virtList", virtList);
				}
				resJsonObject.put("result", "true");
			//更改原有的配置信息
			} else {
				if (StringHelper.isNotEmpty(phyHmcId) && StringHelper.isNotBlank(phyHmcId)) {
					serverRow.set("password", new AES(phyHmcId).encrypt(password,"UTF-8"));
					//修改物理机配置信息
					phyHmcId = service.saveServerConfigInfo(serverRow);
					//获取配置的物理机ID
					String hypervisorId = service.getPhysicalInfoByConfigId(phyHmcId).getString("hypervisor_id");
					//修改物理机HMC_ID
					service.updatePhysicHmcId(phyHmcId, hypervisorId);
					//保存物理机和交换机的关联关系到数据库
					List<DataRow> allSwitchList = (List<DataRow>) getSession().getAttribute("switchList");
					List<DataRow> addPhySwitchList = new ArrayList<DataRow>();
					DataRow phySwitchRow = null;
					for (int i = 0; i < switchIds.length; i++) {
						phySwitchRow = new DataRow();
						phySwitchRow.set("hypervisor_id", hypervisorId); 
						phySwitchRow.set("switch_id", switchIds[i]);
						for (int j = 0; j < allSwitchList.size(); j++) {
							DataRow switchRow = allSwitchList.get(j);
							if (switchIds[i].equals(switchRow.getString("hypswid"))) {
								phySwitchRow.set("switch_name", switchRow.getString("hypswname"));
								phySwitchRow.set("switch_ip_address", switchRow.getString("hypswip"));
								break;
							}
						}
						addPhySwitchList.add(phySwitchRow);
					}
					service.saveHyperSwitchMap(addPhySwitchList);
				}
				resJsonObject.put("result", "false");
			}			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			ResponseHelper.print(getResponse(), resJsonObject.toString());
		}
	}
	
	/**
	 * 验证并保存BMC配置
	 */
	public void doTestAndSaveBmcConfig() {
		try {
			String result = "true";
			long physicalId = getLongParameter("physicalId");
			String bmcIp = getStrParameter("bmcIp").trim();
			String bmcUser = getStrParameter("bmcUser").replaceAll("&amp;nbsp;", " ");
			String bmcPassword = getStrParameter("bmcPassword").replaceAll("&amp;nbsp;", " ");
			int bmcPort = getIntParameter("bmcPort",623);
			Integer bmcAuthLevel = getIntParameter("bmcAuthLevel");
			Integer bmcAuthType = getIntParameter("bmcAuthType");
			//判断是否需要验证
			if (bmcIp != null && bmcIp != "") {
				IPMIInfo ipmiInfo = new IPMIInfo(physicalId, bmcUser, bmcPassword, bmcIp, String.valueOf(bmcPort), bmcAuthLevel, bmcAuthType);
				IPMIUtil ipmiUtil = new IPMIUtil(this.getClass());
				boolean isAuth = ipmiUtil.isBMCConnected(ipmiInfo);
				if (isAuth) {
					//保存配置信息
					DataRow row = new DataRow();
					row.set("hypervisor_id", physicalId);
					row.set("user_name", bmcUser);
					row.set("session_pwd", bmcPassword);
					row.set("ip_address", bmcIp);
					row.set("port", bmcPort);
					row.set("level", bmcAuthLevel);
					row.set("auth_type", bmcAuthType);
					String bmcId = service.saveBmcConfigInfo(row);
					//更新密码
					if (bmcPassword != null && bmcPassword != "") {
						bmcPassword = new AES(bmcId).encrypt(bmcPassword, "UTF-8");
						service.updateBmcConfigPassword(bmcId, bmcPassword);
					}
				} else {
					result = "false";
				}
			}
			ResponseHelper.print(getResponse(), result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 通过物理机的ID查找该物理机下的虚拟机信息
	 * @param physicalId
	 * @return
	 */
	public List<DataRow> getVirtualMachineList(String physicalId) {
		List<DataRow> virtConfigList = new ArrayList<DataRow>();
		//获取用户可见的虚拟机
		String limitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, Integer.parseInt(physicalId));
		List<DataRow> virtList = virtualService.getVirtualListByPhysicalId(physicalId,limitIds);
		List<DataRow> configList = new ArrayList<DataRow>();
		String serverIds = "";
		for (int i = 0; i < virtList.size(); i++) {
			DataRow row = virtList.get(i);
			int hmcId = row.getInt("hmc_id");
			if (hmcId > 0) {
				serverIds = serverIds.length() > 0 ? (serverIds + ',' + hmcId) : (String.valueOf(hmcId));
			}
		}
		//获取虚拟机配置信息列表
		if (serverIds.length() > 0) {
			configList = service.getServerConfigList(serverIds);
		}
		//将虚拟机配置信息加到list中
		for (int i = 0; i < virtList.size(); i++) {
			DataRow virtRow = virtList.get(i);
			DataRow virtConRow = new DataRow();
			virtConRow.set("vm_id", virtRow.getString("vm_id"));
			virtConRow.set("hmc_id", virtRow.getString("hmc_id"));
			virtConRow.set("name", virtRow.getString("name"));
			for (int j = 0; j < configList.size(); j++) {
				DataRow configRow = configList.get(j);
				if (virtRow.getString("hmc_id").equals(configRow.getString("id"))) {
					virtConRow.set("ip_address", configRow.getString("ip_address"));
					virtConRow.set("user", configRow.getString("user"));
					virtConRow.set("password", new AES(virtRow.getString("hmc_id")).decrypt(configRow.getString("password"),"UTF-8"));
					break;
				}
			}
			virtConfigList.add(virtConRow);
		}
		return virtConfigList;
	}
	
	/**
	 * 验证并保存虚拟机配置信息
	 */
	public void doTestAndSaveVirtConfig() {
		try {
			String physicalId = getStrParameter("physicalId");
			String osType = getStrParameter("ostype");
			String hyperType = getStrParameter("hyperType");
			String data = URLDecoder.decode(getStrParameter("data"),"UTF-8");
			data = data.replaceAll("&amp;acute;", "'");
			JSONArray dataArray = JSONArray.fromString(data);
			String notPass = "";
			HmcBase hmcBase = null;
			Session session = null;
			ICSharpWMIClass cswmi = null;
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject obj = dataArray.getJSONObject(i);
				String vmName = obj.getString("vmName");
				String ip = obj.getString("ip");
				String user = obj.getString("user");
				String password = obj.getString("password");
				boolean isAuth = false;
				
				//验证虚拟机配置信息
				try {
					//For LINUX OS
					hmcBase = new HmcBase(ip, 22, user, password);
					session = hmcBase.openConn();
					if (session != null) {
						logger.info("Successful connect to linux...");
						isAuth = true;
						//设置操作系统类型
						dataArray.getJSONObject(i).put("osType", WebConstants.OSTYPE_LINUX);
						hmcBase.closeConn();
					} 
				} catch (Exception e) {
					logger.error("Fail connect to linux...");
				}
				//For Windows OS
				if (!isAuth) {
					try {
						MyUtilities.initBridge(this.getClass());
						//默认设置authentication = 3,impersonation = 7,页面暂时不接收用户录入
						cswmi = new CSharpWMIClass(new String[]{ip}, user, password, 3, 7);
						isAuth = cswmi.IsDeviceConnectedByWMI();
						//连接成功
						if (isAuth) {
							//设置操作系统类型
							dataArray.getJSONObject(i).put("osType", WebConstants.OSTYPE_WINDOWS);
							logger.info("Successful connect to windows...");
						//验证失败
						} else {
							String alertVal = vmName.length() == 0 ? ip : vmName;
							notPass = notPass.length() > 0 ? (notPass + "," + alertVal) : alertVal; 
							continue;
						}
					} catch (Exception e) {
						logger.error("Fail connect to windows...");
					}
				}
			}
			//验证成功,保存配置信息到数据库,否则返回错误信息
			if (notPass.length() == 0) {
				VirtualMac virtualMac = null;
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject obj = dataArray.getJSONObject(i);
					String hmcId = obj.getString("hmcId");
					String vmId = obj.getString("vmId");
					String vmName = obj.getString("vmName");
					String ip = obj.getString("ip");
					String user = obj.getString("user");
					String password = obj.getString("password");
					String vmOsType = obj.getString("osType");
					DataRow serverRow = new DataRow();
					serverRow.set("id", hmcId);
					serverRow.set("name", vmName);
					serverRow.set("ip_address", ip);
					serverRow.set("user", user);
					serverRow.set("state", 1);
					serverRow.set("toptype", SrContant.SUBDEVTYPE_VIRTUAL.toLowerCase());
					serverRow.set("os_type", vmOsType);
					//Windows默认设置authentication = 3,impersonation = 7
					if (vmOsType.equals(WebConstants.OSTYPE_WINDOWS)) {
						serverRow.set("impersonation", 7);
						serverRow.set("authentication", 3);
					}
					
					//如果选择的条件是：(LINUX+KVM,ESXi+VMware)
					if ((osType.equals(WebConstants.OSTYPE_LINUX) && hyperType.equals(WebConstants.VIRT_PLAT_TYPE_KVM))
							|| (osType.equals(WebConstants.OSTYPE_ESXI) && hyperType.equals(WebConstants.VIRT_PLAT_TYPE_VMWARE))) {
						//插入配置信息
						hmcId = service.saveServerConfigInfo(serverRow);
						//更新虚拟机HMC_ID
						service.updateVirtHmcId(hmcId, vmId, vmName);
						//更新虚拟机配置密码
						service.updateServerConfigPassword(hmcId, new AES(hmcId).encrypt(password,"UTF-8"));
						//For LINUX OS TYPE VM
						if (vmOsType.equals(WebConstants.OSTYPE_LINUX)) {
							//采集虚拟机信息
							virtualMac = new VirtualMac();
							virtualMac.getVirtualConfigInfo(physicalId,hmcId);
						//For Windows OS TYPE VM
						} else if (vmOsType.equals(WebConstants.OSTYPE_WINDOWS)) {
							//更新t_res_computersystem数据(IP地址)
							service.updateComputerSytem(vmId, vmName, ip);
							//采集虚拟机信息
							MyUtilities.initBridge(this.getClass());
							cswmi = new CSharpWMIClass(new String[]{ip}, user, password, 3, 7);
							DeviceInfo deviceInfo = new DeviceInfo();
							deviceInfo.setIpAddress(ip);
							deviceInfo.setUsername(user);
							deviceInfo.setPassword(password);
							deviceInfo.setAuthentication(3);
							deviceInfo.setImpersonate(7);
							deviceInfo.setToptype(SrContant.SUBDEVTYPE_VIRTUAL);
							new DataCollectConfig(cswmi, deviceInfo).execute();
						}
					//选择的是其他条件
					} else {
						//For LINUX OS TYPE VM
						if (vmOsType.equals(WebConstants.OSTYPE_LINUX)) {
							//采集虚拟机信息
							virtualMac = new VirtualMac();
							virtualMac.getVirtualConfigInfo(physicalId,hmcId);
						//For Windows OS TYPE VM
						} else if (vmOsType.equals(WebConstants.OSTYPE_WINDOWS)) {
							MyUtilities.initBridge(this.getClass());
							cswmi = new CSharpWMIClass(new String[]{ip}, user, password, 3, 7);
							if (StringHelper.isEmpty(vmName) || StringHelper.isBlank(vmName)) {
								vmName = cswmi.GetComputerName();
								serverRow.set("name", vmName);
							}
							serverRow.set("os_type", WebConstants.OSTYPE_WINDOWS);
							//插入配置信息
							hmcId = service.saveServerConfigInfo(serverRow);
							//更新虚拟机配置密码
							service.updateServerConfigPassword(hmcId, new AES(hmcId).encrypt(password,"UTF-8"));
							//采集虚拟机信息
							DeviceInfo deviceInfo = new DeviceInfo();
							deviceInfo.setIpAddress(ip);
							deviceInfo.setUsername(user);
							deviceInfo.setPassword(password);
							deviceInfo.setAuthentication(3);
							deviceInfo.setImpersonate(7);
							deviceInfo.setToptype(SrContant.SUBDEVTYPE_VIRTUAL);
							new DataCollectConfig(cswmi, deviceInfo).execute();
							//更新虚拟机HMC_ID
							service.updateVirtHmcId(hmcId, vmId, vmName);
						}
					}
				}
				ResponseHelper.print(getResponse(), "true");
			} else {
				ResponseHelper.print(getResponse(), notPass);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 加载存储系统配置列表
	 * @return
	 */
	public ActionResult doStorageCfgPage(){
		DBPage storageCfgPage = null;
		String storageName = getStrParameter("storageName");
		String storageType = getStrParameter("storageType");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = WebConstants.NumPerPage;
		storageCfgPage = service.getStorageCfgPage(storageName, storageType, curPage, numPerPage);
		setAttribute("storageCfgPage", storageCfgPage);
		return new ActionResult("/WEB-INF/views/usercon/storageCfgPage.jsp");
	}
	
	/**
	 * 添加或编辑存储系统配置信息
	 * @return
	 */
	public ActionResult doEditStorageConfig() {
		String id = getStrParameter("id");
		DataRow storageCfgInfo = null;
		if (StringHelper.isNotEmpty(id) && StringHelper.isNotBlank(id)) {
			storageCfgInfo = service.getStorageCfgInfo(id);
			if (storageCfgInfo != null) {
				String pwd = storageCfgInfo.getString("password");
				storageCfgInfo.set("password", new AES().decrypt(pwd, "UTF-8"));
			}
		}
		setAttribute("storageCfgInfo", new JSONObject().fromObject(storageCfgInfo));
		return new ActionResult("/WEB-INF/views/usercon/editStorageCfg.jsp");
	}
	
	/**
	 * 验证并保存存储系统配置信息
	 */
	public void doTestAndSaveStorageCfgInfo() {
		try {
			long id = getLongParameter("id",0);
			String storageName = getStrParameter("storageName");
			String storageType = getStrParameter("storageType");
			String ctl01Ip = getStrParameter("ctl01Ip");
			String ctl02Ip = getStrParameter("ctl02Ip");
			String user = getStrParameter("user");
			String password = getStrParameter("password").replaceAll("&amp;nbsp;", " ");
			String nativeCliPath = getStrParameter("nativeCliPath");
			int state = 0;
			HmcBase hmcBase = null;
			Session session = null;
			try {
				hmcBase = new HmcBase(ctl01Ip, 22, user, password);
				session = hmcBase.openConn();
				if (session != null) {
					state = 1;
					hmcBase.closeConn();
					logger.info("验证配置信息成功!");
				}
			} catch (Exception e) {
				logger.error("验证配置信息失败!");
			}
			//如果验证成功,则保存配置信息到数据库
			if (state == 1) {
				DataRow dataRow = new DataRow();
				dataRow.set("name", storageName);
				dataRow.set("storage_type", storageType);
				dataRow.set("ctl01_ip", ctl01Ip);
				dataRow.set("ctl02_ip", ctl02Ip);
				dataRow.set("user", user);
				dataRow.set("password", new AES().encrypt(password, "UTF-8"));
				if (storageType.equals(SrContant.DEVTYPE_VAL_EMC)) {
					//设置默认代理目录
					if (StringHelper.isEmpty(nativeCliPath) || StringHelper.isBlank(nativeCliPath)) {
						nativeCliPath = "C:\\\\NavisphereCLI";
					}
				}
				dataRow.set("native_cli_path", nativeCliPath);
				dataRow.set("state", state);
				service.saveStorageCfgInfo(dataRow, id);
				ResponseHelper.print(getResponse(), "true");
			} else {
				ResponseHelper.print(getResponse(), "false");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private String[] checkStrArray(String[] str,String mach){
		if(str==null || str.length == 0){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (String string : str) {
			if(!string.equals(mach)){
				list.add(string);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * 添加或编辑设置
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ActionResult doEditDeviceInfo(){
		String id = getStrParameter("id");
		Integer typeId = getIntParameter("typeId");
		DataRow row = null;
		List<DataRow> deviceList = null;
		if (id != null && id.length() > 0) {
			row = service.getDeviceInfo(id, typeId.toString());
		}
		//存储系统
		if (typeId == 1) {
			StorageService storservice = new StorageService();
			deviceList = storservice.getStoragesys();
		//交换机
		} else if (typeId == 2) {
			SwitchService switchService = new SwitchService();
			//获取用户可见的交换机
			String limitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
			deviceList = switchService.getSwitchInfoList(limitIds);
		//存储系统
		} else if (typeId == 5) {
			StorageService storservice = new StorageService();
			//获取用户可见的存储
			String limitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
			deviceList = storservice.getSubsystemNames("10",limitIds);
		}

		List<DataRow> removeList = new ArrayList<DataRow>();
		List<DataRow> devicesList = service.getDeviceList(null,typeId.toString());
		JSONArray devList = new JSONArray();
		if (deviceList != null && deviceList.size() > 0) {
			for (DataRow device : deviceList) {
				for (DataRow dev : devicesList) {
					if (device.getString("id").equals(dev.getString("ele_id"))) {
						removeList.add(device);
					}
				}
			}
			deviceList.removeAll(removeList);
			for (DataRow device : deviceList) {
				device.set("name", device.getString("name"));
				device.set("id", device.getString("id"));
				devList.add(device);
			}
		}

		this.setAttribute("deviceList", devList);
		this.setAttribute("deviceInfo", new JSONObject().fromObject(row));
		this.setAttribute("Id", id);
		this.setAttribute("typeId", typeId);
		return new ActionResult("/WEB-INF/views/usercon/editDevice.jsp");
	}
	
	@SuppressWarnings("static-access")
	public ActionResult doEditDeviceInfo1(){
		String id = getStrParameter("id");
		Integer typeId = getIntParameter("typeId");
		DataRow row =null;
		DataRow row1 =null;
		if (id != null && id.length() > 0) {
			row = service.getDeviceInfo(id, typeId.toString());
		}
		//磁盘阵列
		if (typeId == 3) {
			ArraysiteService arrayService = new ArraysiteService();
			if (row != null && row.size() > 0) {
				row1 = arrayService.getArraysiteInfo(row.getInt("ele_id"));
			}
		}
		StorageService storservice = new StorageService();
		//获取用户可见的存储
		String limitIds = (String) getSession().getAttribute(WebConstants.TPC_STORAGE_LIST);
		List<DataRow> rows = storservice.getSubsystemNames(null,limitIds);
		this.setAttribute("device", rows);
		this.setAttribute("deviceInfo", new JSONObject().fromObject(row));
		this.setAttribute("deviceInfo1", new JSONObject().fromObject(row1));
		this.setAttribute("Id", id);
		this.setAttribute("typeId", typeId);
		return new ActionResult("/WEB-INF/views/usercon/editDevice1.jsp");
	}
	
	@SuppressWarnings("unused")
	public void doLoadEle(){
		String id = getStrParameter("deviceId");
		Integer typeId = getIntParameter("typeId");
		JSONArray array = new JSONArray(); 
		List<DataRow> eleList=null;
		if(typeId==3){
			ArraysiteService arrayService=new ArraysiteService();//磁盘阵列
			eleList=arrayService.getArraysiteList(null, null, Integer.parseInt(id));
			List<DataRow> elesList = service.getDeviceList(id, typeId.toString());
			List<DataRow> removeList = new ArrayList<DataRow>();
			if(eleList!=null && eleList.size()>0){
				for (DataRow dataRow : eleList) {
					if(elesList!=null && elesList.size()>0){
						for (DataRow dataRow2 : elesList) {
							if(dataRow.getString("disk_group_id").equals(dataRow2.getString("ele_id"))){
								//标记已经存在的
								removeList.add(dataRow);
							}
						}
					}
				}
				eleList.removeAll(removeList);  //删除已经存在的元素
				for (DataRow dataRow : eleList) {
					JSONObject obj = new JSONObject();
					obj.put("ele_id", dataRow.getString("disk_group_id"));
					obj.put("ele_name", dataRow.getString("the_display_name"));
					array.add(obj);
				}
			}
		}else if(typeId==4){
			LibraryService labService=new LibraryService();//磁带库
			
		}
		
		ResponseHelper.print(getResponse(), array);
	}
	/**
	 * 校验设备登录信息
	 */
	@SuppressWarnings("deprecation")
	public void doTestAcctDevice(){

		Integer typeId = getIntParameter("typeId");
		String Id = getStrParameter("Id");
		String[] device_id = getStrArrayParameter("device");
		String[] deviceId = checkStrArray(device_id,"multiselect-all");
		String user = getStrParameter("user");
		String password = getStrParameter("password").replaceAll("&amp;nbsp;", " ");
		JSONObject obj = new JSONObject();
		int j = 0;
		for (int i = 0; i < deviceId.length; i++) {
			DataRow row=null;
			if(typeId==1){
				StorageService storservice=new StorageService();
				row=storservice.getSubsystemInfo(Integer.parseInt(deviceId[i]));
			}else if(typeId==2){
				SwitchService switchService=new SwitchService();
				row=switchService.getSwitchInfo(Integer.parseInt(deviceId[i]));
			}
			else if(typeId==5){
				StorageService storservice=new StorageService();
				//row=storservice.getNasInfo(Integer.parseInt(deviceId[i]));
				row=storservice.getSubsystemInfo(Integer.parseInt(deviceId[i]));
			}
			if(row!=null && row.size()>0){
				String ip[] = row.getString("ip_address").replaceAll("&amp;nbsp;", " ").split(",");
				String ipaddress = "";
				if(ip.length>1){
					ipaddress = ip[1];
				}else{
					ipaddress = ip[0];
				}
				DataRow dataRow = new DataRow();
				dataRow.set("id", Id);
				dataRow.set("type_id", typeId);
				dataRow.set("ele_id", deviceId[i]);
				dataRow.set("dev_id", deviceId[i]);
				dataRow.set("device_name", row.getString("the_display_name"));
				dataRow.set("users", user);
				dataRow.set("ip_address", ipaddress);
				dataRow.set("pwd", new DES().encrypt(password));
				Scp_Sftp scp = new Scp_Sftp(ipaddress, 22,user,password);
				//Scp_Sftp scp = new Scp_Sftp("192.168.1.69", 22,"administrator","1234567a");
				Connection con = null;
				try {
					con = scp.login();
					if(con!=null){
						dataRow.set("state", 1);
						obj.put("state", "true");
					}else{
						dataRow.set("state", 0);
						obj.put("state", "false");
						j++;
					}
					service.addDeviceInfo(dataRow);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(con!=null){
						con.close();
					}
				}
			}
		}
		obj.put("fcount", j);
		ResponseHelper.print(getResponse(), obj);
	}

	@SuppressWarnings("deprecation")
	public void doTestAcctDevice1(){
		String devId = getStrParameter("devices");
		Integer typeId = getIntParameter("typeId");
		String Id = getStrParameter("Id");
		String[] ele_id = getStrArrayParameter("ele");
		String[] eleId = checkStrArray(ele_id, "multiselect-all");
		String user = getStrParameter("user");
		String password = getStrParameter("password").replaceAll("&amp;nbsp;",
				" ");
		JSONObject obj = new JSONObject();
		int j = 0;
		for (int i = 0; i < eleId.length; i++) {
			DataRow row = null;
			DataRow row1 = null;
			if (typeId == 3) {
				LibraryService libraryService = new LibraryService();// IP找不到
				row = libraryService.getLibraryInfo(Integer.parseInt(eleId[i]));
				StorageService storservice = new StorageService();// 存储IP
				row1 = storservice.getSubsystemInfo(Integer.parseInt(devId));
			} else if (typeId == 4) {
				// ArraysiteService arrayService=new ArraysiteService();//IP找不到
				// row=arrayService.getArraysiteInfo(Integer.parseInt(eleId[i]));
				StorageService storservice = new StorageService();// 存储IP
				row1 = storservice.getSubsystemInfo(Integer.parseInt(devId));
			}

			if (row != null && row.size() > 0) {
				String ip[] = row1.getString("ip_address").replaceAll("&amp;nbsp;", " ").split(",");
				String ipaddress = "";
				if (ip.length > 1) {
					ipaddress = ip[1];
				} else {
					ipaddress = ip[0];
				}

				DataRow dataRow = new DataRow();
				dataRow.set("id", Id);
				dataRow.set("type_id", typeId);
				dataRow.set("ele_id", eleId[i]);
				dataRow.set("device_name", row.getString("the_display_name"));
				dataRow.set("dev_id", devId);
				dataRow.set("users", user);
				dataRow.set("pwd", new DES().encrypt(password));
				Scp_Sftp scp = new Scp_Sftp(ipaddress, 22, user, password);
				Connection con = null;
				try {
					con = scp.login();
					if (con != null) {
						dataRow.set("state", 1);
						obj.put("state", "true");
					} else {
						dataRow.set("state", 0);
						obj.put("state", "false");
						j++;
					}
					service.addDeviceInfo(dataRow);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (con != null) {
						con.close();
					}
				}
			}
		}
		obj.put("fcount", j);
		ResponseHelper.print(getResponse(), obj);
	}
}
