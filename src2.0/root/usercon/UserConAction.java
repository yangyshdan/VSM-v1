package root.usercon;

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
import com.huiming.web.base.ActionResult;
import com.project.hmc.core.HmcBase;
import com.project.hmc.engn.ComputerSystem;
import com.project.hmc.engn.LibvirtEngine;
import com.project.hmc.engn.VirtualMac;
import com.project.nmon.engn.Scp_Sftp;
import com.project.web.SecurityAction;
import com.project.x86monitor.DataCollectConfig;
import com.project.x86monitor.DeviceInfo;
import com.project.x86monitor.IPMIInfo;
import com.project.x86monitor.IPMIUtil;
import com.project.x86monitor.MyUtilities;
import csharpwmi.CSharpWMIClass;
import csharpwmi.ICSharpWMIClass;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

public class UserConAction extends SecurityAction
{
  private static final Logger logger = Logger.getLogger(UserConAction.class);
  UserConService service = new UserConService();
  VirtualmachineService virtualService = new VirtualmachineService();
  TopoService topoService = new TopoService();

  public ActionResult doDefault()
  {
    DBPage serverPage = null;
    DBPage storageCfgPage = null;
    DBPage storagePage = null;
    DBPage labraryPage = null;
    DBPage switchPage = null;
    DBPage arraysitePage = null;
    DBPage nasPage = null;
    int curPage = getIntParameter("curPage", 1);
    DBPage hostPage = null;

    int numPerPage = 25;

    serverPage = this.service.getServerPage(null, null, null, curPage, numPerPage);

    storageCfgPage = this.service.getStorageCfgPage(null, null, curPage, numPerPage);

    labraryPage = this.service.getDevicePage(curPage, numPerPage, null, Integer.valueOf(4));

    storagePage = this.service.getDevicePage(curPage, numPerPage, null, Integer.valueOf(1));

    switchPage = this.service.getDevicePage(curPage, numPerPage, null, Integer.valueOf(2));

    arraysitePage = this.service.getDevicePage(curPage, numPerPage, null, Integer.valueOf(3));

    nasPage = this.service.getDevicePage(curPage, numPerPage, null, Integer.valueOf(5));
    setAttribute("serverPage", serverPage);
    setAttribute("storageCfgPage", storageCfgPage);
    setAttribute("labraryPage", labraryPage);
    setAttribute("storagePage", storagePage);
    setAttribute("switchPage", switchPage);
    setAttribute("arraysitePage", arraysitePage);
    setAttribute("nasPage", nasPage);

    initSelectItem();
    setAttribute("hostPage", hostPage);
    return new ActionResult("/WEB-INF/views/usercon/userconList.jsp");
  }

  public ActionResult doAjaxDevicePage()
  {
    DBPage storagePage = null;
    DBPage labraryPage = null;
    DBPage switchPage = null;
    DBPage arraysitePage = null;
    DBPage nasPage = null;
    int curPage = getIntParameter("curPage", 1);
    int numPerPage = 25;
    String labraryname = getStrParameter("labraryname");
    String storagename = getStrParameter("storagename");
    String switchname = getStrParameter("switchname");
    String arrayname = getStrParameter("arrayname");
    String nasname = getStrParameter("nasname");
    Integer typeId = Integer.valueOf(getIntParameter("typeId"));

    if (typeId.intValue() == 4) {
      labraryPage = this.service.getDevicePage(curPage, numPerPage, labraryname, Integer.valueOf(4));
      setAttribute("labraryPage", labraryPage);
      return new ActionResult("/WEB-INF/views/usercon/labraryPage.jsp");
    }
    if (typeId.intValue() == 1) {
      storagePage = this.service.getDevicePage(curPage, numPerPage, storagename, Integer.valueOf(1));
      setAttribute("storagePage", storagePage);
      return new ActionResult("/WEB-INF/views/usercon/storagePage.jsp");
    }
    if (typeId.intValue() == 2) {
      switchPage = this.service.getDevicePage(curPage, numPerPage, switchname, Integer.valueOf(2));
      setAttribute("switchPage", switchPage);
      return new ActionResult("/WEB-INF/views/usercon/switchPage.jsp");
    }
    if (typeId.intValue() == 3) {
      arraysitePage = this.service.getDevicePage(curPage, numPerPage, arrayname, Integer.valueOf(3));
      setAttribute("arraysitePage", arraysitePage);
      return new ActionResult("/WEB-INF/views/usercon/arraysitePage.jsp");
    }
    if (typeId.intValue() == 5) {
      nasPage = this.service.getDevicePage(curPage, numPerPage, nasname, Integer.valueOf(5));
      setAttribute("nasPage", nasPage);
      return new ActionResult("/WEB-INF/views/usercon/nasPage.jsp");
    }
    return null;
  }

  public void initSelectItem()
  {
    Map vendors = new HashMap();
    vendors.put("IBM", "IBM");
    vendors.put("Lenovo", "Lenovo");
    vendors.put("DELL", "DELL");
    vendors.put("HUAWEI", "HUAWEI");
    vendors.put("HP", "HP");
    vendors.put("INSPUR", "INSPUR");
    vendors.put("Sugon", "Sugon");
    vendors.put("Other", "Other");

    Map schemaTypes = new HashMap();
    schemaTypes.put("X86", "X86");
    schemaTypes.put("Power", "Power");

    Map osTypes = new HashMap();
    osTypes.put("Linux", "Linux");
    osTypes.put("Windows", "Windows");
    osTypes.put("ESXi", "ESXi");

    Map virtPlatTypes = new HashMap();
    virtPlatTypes.put("KVM", "KVM");
    virtPlatTypes.put("VMware", "VMware");
    virtPlatTypes.put("XenServer", "XenServer");
    virtPlatTypes.put("Hyper-V", "Hyper-V");
    virtPlatTypes.put("无", "无");

    List switchList = null;
    String limitIds = (String)getSession().getAttribute("switch_list");

    if (Configure.getInstance().getDataSource("tpc") != null) {
      switchList = this.service.getAllSwitches(limitIds);
    }
    switchList = switchList == null ? new ArrayList() : switchList;

    getSession().setAttribute("vendors", vendors);
    getSession().setAttribute("schemaTypes", schemaTypes);
    getSession().setAttribute("osTypes", osTypes);
    getSession().setAttribute("virtPlatTypes", virtPlatTypes);
    getSession().setAttribute("switchList", switchList);
  }

  public void doDeviceDel()
  {
    String id = getStrParameter("id");
    int typeId = getIntParameter("typeId");
    if ((id != null) && (id.length() > 0))
    {
      if (typeId == 6) {
        this.service.delServerConfigInfo(id);
      }
      else if (typeId == 7)
        this.service.delStorageConfigInfo(id);
      else {
        this.service.deviceDel(id);
      }
      ResponseHelper.print(getResponse(), "true");
    } else {
      ResponseHelper.print(getResponse(), "false");
    }
  }

  public ActionResult doServerPage()
  {
    DBPage serverPage = null;
    String serverName = getStrParameter("serverName");
    String serverType = getStrParameter("serverType");
    String state = getStrParameter("state");
    int curPage = getIntParameter("curPage", 1);
    int numPerPage = 25;
    serverPage = this.service.getServerPage(serverName, serverType, state, curPage, numPerPage);
    setAttribute("serverPage", serverPage);
    return new ActionResult("/WEB-INF/views/usercon/serverPage.jsp");
  }

  public ActionResult doEditServerInfo()
  {
    String id = getStrParameter("id");
    DataRow serverRow = null;
    List mapSwitchList = null;
    List virtList = null;
    if ((id != null) && (id.length() > 0)) {
      serverRow = this.service.getServerConfigInfo(id);
      String serverType = serverRow.getString("toptype");
      serverRow.set("password", new AES(id).decrypt(serverRow.getString("password"), "UTF-8"));
      if (serverType.equalsIgnoreCase("Physical"))
      {
        mapSwitchList = this.service.getServerSwitchMap(id);
      } else if (serverType.equalsIgnoreCase("Virtual"))
      {
        DataRow virtRow = this.virtualService.getVirtualInfoByHmcId(id);
        if (virtRow != null) {
          serverRow.set("vm_id", virtRow.getString("vm_id"));
        }
        virtList = new ArrayList();
        virtList.add(serverRow);
      }
    }
    new JSONObject(); setAttribute("serverInfo", JSONObject.fromObject(serverRow));
    new JSONArray(); setAttribute("switchs", JSONArray.fromObject(getSession().getAttribute("switchList")));
    new JSONArray(); setAttribute("mapSwitchs", JSONArray.fromObject(mapSwitchList));
    new JSONArray(); setAttribute("virtList", JSONArray.fromObject(virtList));
    return new ActionResult("/WEB-INF/views/usercon/editServer.jsp");
  }

  public void doTestAndSavePhysiConfig()
  {
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

      if ((StringHelper.isEmpty(phyHmcId)) && 
        (this.service.hasServerConfigInfo(ipAddress, user, "Physical".toLowerCase()))) {
        resJsonObject.put("result", "has_user");
        return;
      }

      HmcBase hmcBase = null;
      Session session = null;

      if (osType.equals("Linux")) {
        try {
          hmcBase = new HmcBase(ipAddress, 22, user, password);
          session = hmcBase.openConn();

          if (session != null) {
            state = 1;
            isAuth = true;
          }
        } finally {
          hmcBase.closeConn();
        }

      }
      else if (osType.equals("Windows")) {
        Object cswmi = null;
        try {
          MyUtilities.initBridge(getClass());

          cswmi = new CSharpWMIClass(new String[] { ipAddress }, user, password, 3, 7);
          serverName = ((ICSharpWMIClass)cswmi).GetComputerName();

          if (!((ICSharpWMIClass)cswmi).IsDeviceConnectedByWMI()) {
          serverRow.set("impersonation", 7);
          serverRow.set("authentication", 3);
          state = 1;
          isAuth = true;
        }
        }catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
      else if (osType.equals("ESXi")) {
        LibvirtEngine libvirtEngine = new LibvirtEngine("VMware", ipAddress, user, password);
        if (libvirtEngine.getConnect() != null) {
          state = 1;
          isAuth = true;
        }
      }

      label414: serverRow.set("id", phyHmcId);
      serverRow.set("name", serverName == null ? null : serverName);
      serverRow.set("ip_address", ipAddress);
      serverRow.set("description", description);
      serverRow.set("user", user);
      serverRow.set("state", state);
      serverRow.set("vendor", vendor);
      serverRow.set("model", model);
      serverRow.set("toptype", "Physical".toLowerCase());
      serverRow.set("os_type", osType);
      serverRow.set("schema_type", schemaType);
      serverRow.set("virt_plat_type", virtPlatType);
      if (isAuth)
      {
        phyHmcId = this.service.saveServerConfigInfo(serverRow);

        this.service.updateServerConfigPassword(phyHmcId, new AES(phyHmcId).encrypt(password, "UTF-8"));

        if ((osType.equals("ESXi")) || (osType.equals("Linux"))) {
          ComputerSystem computerSystem = new ComputerSystem();
          computerSystem.getPhysicalAndVirtualConfigInfo(phyHmcId);
        }
        else if (osType.equals("Windows")) {
          MyUtilities.initBridge(getClass());
          Object cswmi = new CSharpWMIClass(new String[] { ipAddress }, user, password, 3, 7);
          DeviceInfo deviceInfo = new DeviceInfo();
          deviceInfo.setIpAddress(ipAddress);
          deviceInfo.setUsername(user);
          deviceInfo.setPassword(password);
          deviceInfo.setAuthentication(Integer.valueOf(3));
          deviceInfo.setImpersonate(Integer.valueOf(7));
          deviceInfo.setToptype("Physical");
          new DataCollectConfig((ICSharpWMIClass)cswmi, deviceInfo).execute();
        }

        String hypervisorId = this.service.getPhysicalInfoByConfigId(phyHmcId).getString("hypervisor_id");

        this.service.updatePhysicHmcId(phyHmcId, hypervisorId);

        List allSwitchList = (List)getSession().getAttribute("switchList");
        List addPhySwitchList = new ArrayList();
        DataRow phySwitchRow = null;
        for (int i = 0; i < switchIds.length; i++) {
          phySwitchRow = new DataRow();
          phySwitchRow.set("hypervisor_id", hypervisorId);
          phySwitchRow.set("switch_id", switchIds[i]);
          for (int j = 0; j < allSwitchList.size(); j++) {
            DataRow switchRow = (DataRow)allSwitchList.get(j);
            if (switchIds[i].equals(switchRow.getString("hypswid"))) {
              phySwitchRow.set("switch_name", switchRow.getString("hypswname"));
              phySwitchRow.set("switch_ip_address", switchRow.getString("hypswip"));
              break;
            }
          }
          addPhySwitchList.add(phySwitchRow);
        }
//        this.service.saveHyperSwitchMap(addPhySwitchList, hypervisorId);

        resJsonObject.put("physicalId", hypervisorId);

        DataRow bmcInfo = this.service.getBmcConfigInfo(hypervisorId);
        if (bmcInfo != null) {
          String id = bmcInfo.getString("id");
          String bmcPwd = bmcInfo.getString("session_pwd");
          if ((bmcPwd != null) && (bmcPwd.length() > 0)) {
            bmcInfo.set("session_pwd", new AES(id).decrypt(bmcPwd, "UTF-8"));
          }
        }
        resJsonObject.put("bmcInfo", bmcInfo);

        if (!virtPlatType.equals("无"))
        {
          List virtList = getVirtualMachineList(hypervisorId);
          resJsonObject.put("virtList", virtList);
        }
        resJsonObject.put("result", "true");
      }
      else {
        if ((StringHelper.isNotEmpty(phyHmcId)) && (StringHelper.isNotBlank(phyHmcId))) {
          serverRow.set("password", new AES(phyHmcId).encrypt(password, "UTF-8"));

          phyHmcId = this.service.saveServerConfigInfo(serverRow);

          String hypervisorId = this.service.getPhysicalInfoByConfigId(phyHmcId).getString("hypervisor_id");

          this.service.updatePhysicHmcId(phyHmcId, hypervisorId);

          List allSwitchList = (List)getSession().getAttribute("switchList");
          List addPhySwitchList = new ArrayList();
          DataRow phySwitchRow = null;
          for (int i = 0; i < switchIds.length; i++) {
            phySwitchRow = new DataRow();
            phySwitchRow.set("hypervisor_id", hypervisorId);
            phySwitchRow.set("switch_id", switchIds[i]);
            for (int j = 0; j < allSwitchList.size(); j++) {
              DataRow switchRow = (DataRow)allSwitchList.get(j);
              if (switchIds[i].equals(switchRow.getString("hypswid"))) {
                phySwitchRow.set("switch_name", switchRow.getString("hypswname"));
                phySwitchRow.set("switch_ip_address", switchRow.getString("hypswip"));
                break;
              }
            }
            addPhySwitchList.add(phySwitchRow);
          }
//          this.service.saveHyperSwitchMap(addPhySwitchList, hypervisorId);
        }
        resJsonObject.put("result", "false");
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    } finally {
      ResponseHelper.print(getResponse(), resJsonObject.toString());
    }
  }

  public void doTestAndSaveBmcConfig()
  {
    try
    {
      String result = "true";
      long physicalId = getLongParameter("physicalId");
      String bmcIp = getStrParameter("bmcIp").trim();
      String bmcUser = getStrParameter("bmcUser").replaceAll("&amp;nbsp;", " ");
      String bmcPassword = getStrParameter("bmcPassword").replaceAll("&amp;nbsp;", " ");
      int bmcPort = getIntParameter("bmcPort", 623);
      Integer bmcAuthLevel = Integer.valueOf(getIntParameter("bmcAuthLevel"));
      Integer bmcAuthType = Integer.valueOf(getIntParameter("bmcAuthType"));

      if ((bmcIp != null) && (bmcIp != "")) {
        IPMIInfo ipmiInfo = new IPMIInfo(Long.valueOf(physicalId), bmcUser, bmcPassword, bmcIp, String.valueOf(bmcPort), bmcAuthLevel, bmcAuthType);
        IPMIUtil ipmiUtil = new IPMIUtil(getClass());
        boolean isAuth = ipmiUtil.isBMCConnected(ipmiInfo);
        if (isAuth)
        {
          DataRow row = new DataRow();
          row.set("hypervisor_id", physicalId);
          row.set("user_name", bmcUser);
          row.set("session_pwd", bmcPassword);
          row.set("ip_address", bmcIp);
          row.set("port", bmcPort);
          row.set("level", bmcAuthLevel);
          row.set("auth_type", bmcAuthType);
          String bmcId = this.service.saveBmcConfigInfo(row);

          if ((bmcPassword != null) && (bmcPassword != "")) {
            bmcPassword = new AES(bmcId).encrypt(bmcPassword, "UTF-8");
            this.service.updateBmcConfigPassword(bmcId, bmcPassword);
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

  public List<DataRow> getVirtualMachineList(String physicalId)
  {
    List virtConfigList = new ArrayList();

    String limitIds = getUserDefinedDeviceIds("Virtual", null, Integer.valueOf(Integer.parseInt(physicalId)));
    List virtList = this.virtualService.getVirtualListByPhysicalId(physicalId, limitIds);
    List configList = new ArrayList();
    String serverIds = "";
    for (int i = 0; i < virtList.size(); i++) {
      DataRow row = (DataRow)virtList.get(i);
      int hmcId = row.getInt("hmc_id");
      if (hmcId > 0) {
        serverIds = serverIds.length() > 0 ? serverIds + ',' + hmcId : String.valueOf(hmcId);
      }
    }

    if (serverIds.length() > 0) {
      configList = this.service.getServerConfigList(serverIds);
    }

    for (int i = 0; i < virtList.size(); i++) {
      DataRow virtRow = (DataRow)virtList.get(i);
      DataRow virtConRow = new DataRow();
      virtConRow.set("vm_id", virtRow.getString("vm_id"));
      virtConRow.set("hmc_id", virtRow.getString("hmc_id"));
      virtConRow.set("name", virtRow.getString("name"));
      for (int j = 0; j < configList.size(); j++) {
        DataRow configRow = (DataRow)configList.get(j);
        if (virtRow.getString("hmc_id").equals(configRow.getString("id"))) {
          virtConRow.set("ip_address", configRow.getString("ip_address"));
          virtConRow.set("user", configRow.getString("user"));
          virtConRow.set("password", new AES(virtRow.getString("hmc_id")).decrypt(configRow.getString("password"), "UTF-8"));
          break;
        }
      }
      virtConfigList.add(virtConRow);
    }
    return virtConfigList;
  }

  public void doTestAndSaveVirtConfig()
  {
    try
    {
      String physicalId = getStrParameter("physicalId");
      String osType = getStrParameter("ostype");
      String hyperType = getStrParameter("hyperType");
      String data = URLDecoder.decode(getStrParameter("data"), "UTF-8");
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
        try
        {
          hmcBase = new HmcBase(ip, 22, user, password);
          session = hmcBase.openConn();
          if (session != null) {
            logger.info("Successful connect to linux...");
            isAuth = true;

            dataArray.getJSONObject(i).put("osType", "Linux");
            hmcBase.closeConn();
          }
        } catch (Exception e) {
          logger.error("Fail connect to linux...");
        }

        if (!isAuth) {
          try {
            MyUtilities.initBridge(getClass());

            cswmi = new CSharpWMIClass(new String[] { ip }, user, password, 3, 7);
            isAuth = cswmi.IsDeviceConnectedByWMI();

            if (isAuth)
            {
              dataArray.getJSONObject(i).put("osType", "Windows");
              logger.info("Successful connect to windows...");
            }
            else {
              String alertVal = vmName.length() == 0 ? ip : vmName;
              notPass = notPass.length() > 0 ? notPass + "," + alertVal : alertVal;
            }
          }
          catch (Exception e) {
            logger.error("Fail connect to windows...");
          }
        }
      }

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
          serverRow.set("toptype", "Virtual".toLowerCase());
          serverRow.set("os_type", vmOsType);

          if (vmOsType.equals("Windows")) {
            serverRow.set("impersonation", 7);
            serverRow.set("authentication", 3);
          }

          if (((osType.equals("Linux")) && (hyperType.equals("KVM"))) || (
            (osType.equals("ESXi")) && (hyperType.equals("VMware"))))
          {
            hmcId = this.service.saveServerConfigInfo(serverRow);

            this.service.updateVirtHmcId(hmcId, vmId, vmName);

            this.service.updateServerConfigPassword(hmcId, new AES(hmcId).encrypt(password, "UTF-8"));

            if (vmOsType.equals("Linux"))
            {
              virtualMac = new VirtualMac();
              virtualMac.getVirtualConfigInfo(physicalId, hmcId);
            }
            else if (vmOsType.equals("Windows"))
            {
              this.service.updateComputerSytem(vmId, vmName, ip);

              MyUtilities.initBridge(getClass());
              cswmi = new CSharpWMIClass(new String[] { ip }, user, password, 3, 7);
              DeviceInfo deviceInfo = new DeviceInfo();
              deviceInfo.setIpAddress(ip);
              deviceInfo.setUsername(user);
              deviceInfo.setPassword(password);
              deviceInfo.setAuthentication(Integer.valueOf(3));
              deviceInfo.setImpersonate(Integer.valueOf(7));
              deviceInfo.setToptype("Virtual");
              new DataCollectConfig(cswmi, deviceInfo).execute();
            }

          }
          else if (vmOsType.equals("Linux"))
          {
            virtualMac = new VirtualMac();
            virtualMac.getVirtualConfigInfo(physicalId, hmcId);
          }
          else if (vmOsType.equals("Windows")) {
            MyUtilities.initBridge(getClass());
            cswmi = new CSharpWMIClass(new String[] { ip }, user, password, 3, 7);
            if ((StringHelper.isEmpty(vmName)) || (StringHelper.isBlank(vmName))) {
              vmName = cswmi.GetComputerName();
              serverRow.set("name", vmName);
            }
            serverRow.set("os_type", "Windows");

            hmcId = this.service.saveServerConfigInfo(serverRow);

            this.service.updateServerConfigPassword(hmcId, new AES(hmcId).encrypt(password, "UTF-8"));

            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setIpAddress(ip);
            deviceInfo.setUsername(user);
            deviceInfo.setPassword(password);
            deviceInfo.setAuthentication(Integer.valueOf(3));
            deviceInfo.setImpersonate(Integer.valueOf(7));
            deviceInfo.setToptype("Virtual");
            new DataCollectConfig(cswmi, deviceInfo).execute();

            this.service.updateVirtHmcId(hmcId, vmId, vmName);
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

  public ActionResult doStorageCfgPage()
  {
    DBPage storageCfgPage = null;
    String storageName = getStrParameter("storageName");
    String storageType = getStrParameter("storageType");
    int curPage = getIntParameter("curPage", 1);
    int numPerPage = 25;
    storageCfgPage = this.service.getStorageCfgPage(storageName, storageType, curPage, numPerPage);
    setAttribute("storageCfgPage", storageCfgPage);
    return new ActionResult("/WEB-INF/views/usercon/storageCfgPage.jsp");
  }

  public ActionResult doEditStorageConfig()
  {
    String id = getStrParameter("id");
    DataRow storageCfgInfo = null;
    if ((StringHelper.isNotEmpty(id)) && (StringHelper.isNotBlank(id))) {
      storageCfgInfo = this.service.getStorageCfgInfo(id);
      if (storageCfgInfo != null) {
        String pwd = storageCfgInfo.getString("password");
        storageCfgInfo.set("password", new AES().decrypt(pwd, "UTF-8"));
      }
    }
    new JSONObject(); setAttribute("storageCfgInfo", JSONObject.fromObject(storageCfgInfo));
    return new ActionResult("/WEB-INF/views/usercon/editStorageCfg.jsp");
  }

  public void doTestAndSaveStorageCfgInfo()
  {
    try
    {
      long id = getLongParameter("id", 0L);
      String storageName = getStrParameter("storageName");
      String storageType = getStrParameter("storageType");
      String ctl01Ip = getStrParameter("ctl01Ip");
      String ctl02Ip = getStrParameter("ctl02Ip");
      String user = getStrParameter("user");
      String password = getStrParameter("password").replaceAll("&amp;nbsp;", " ");
      String nativeCliPath = getStrParameter("nativeCliPath");
      int state = 1;
      HmcBase hmcBase = null;
      Session session = null;
//      try {
//        hmcBase = new HmcBase(ctl01Ip, 22, user, password);
//        session = hmcBase.openConn();
//        if (session != null) {
//          state = 1;
//          hmcBase.closeConn();
//          logger.info("验证配置信息成功!");
//        }
//      } catch (Exception e) {
//        logger.error("验证配置信息失败!");
//      }

      if (state == 1) {
        DataRow dataRow = new DataRow();
        dataRow.set("name", storageName);
        dataRow.set("storage_type", storageType);
        dataRow.set("ctl01_ip", ctl01Ip);
        dataRow.set("ctl02_ip", ctl02Ip);
        dataRow.set("user", user);
        dataRow.set("password", new AES().encrypt(password, "UTF-8"));
        if (storageType.equals("EMC"))
        {
          if ((StringHelper.isEmpty(nativeCliPath)) || (StringHelper.isBlank(nativeCliPath))) {
            nativeCliPath = "C:\\\\NavisphereCLI";
          }
        }
        dataRow.set("native_cli_path", nativeCliPath);
        dataRow.set("state", state);
        this.service.saveStorageCfgInfo(dataRow, id);
        ResponseHelper.print(getResponse(), "true");
      } else {
        ResponseHelper.print(getResponse(), "false");
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  private String[] checkStrArray(String[] str, String mach) {
    if ((str == null) || (str.length == 0)) {
      return null;
    }
    List list = new ArrayList();
    for (String string : str) {
      if (!string.equals(mach)) {
        list.add(string);
      }
    }
    return (String[])list.toArray(new String[list.size()]);
  }

//  public ActionResult doEditDeviceInfo()
//  {
//    String id = getStrParameter("id");
//    Integer typeId = Integer.valueOf(getIntParameter("typeId"));
//    DataRow row = null;
//    List<DataRow> deviceList = null;
//    if ((id != null) && (id.length() > 0)) {
//      row = this.service.getDeviceInfo(id, typeId.toString());
//    }
//
//    if (typeId.intValue() == 1) {
//      StorageService storservice = new StorageService();
//      deviceList = storservice.getStoragesys();
//    }
//    else if (typeId.intValue() == 2) {
//      SwitchService switchService = new SwitchService();
//
//      String limitIds = (String)getSession().getAttribute("switch_list");
//      deviceList = switchService.getSwitchInfoList(limitIds);
//    }
//    else if (typeId.intValue() == 5) {
//      StorageService storservice = new StorageService();
//
//      String limitIds = (String)getSession().getAttribute("tpc_storage_list");
//      deviceList = storservice.getSubsystemNames("10", limitIds);
//    }
//
//    List removeList = new ArrayList();
//    List devicesList = this.service.getDeviceList(null, typeId.toString());
//    JSONArray devList = new JSONArray();
//    if ((deviceList != null) && (deviceList.size() > 0))
//    {
//      Iterator localIterator2;
//      for (Iterator localIterator1 = deviceList.iterator(); localIterator1.hasNext(); 
//        localIterator2.hasNext())
//      {
//        DataRow device = (DataRow)localIterator1.next();
//        localIterator2 = devicesList.iterator(); continue; 
//        DataRow dev = (DataRow)localIterator2.next();
//        if (device.getString("id").equals(dev.getString("ele_id"))) {
//          removeList.add(device);
//        }
//      }
//
//      deviceList.removeAll(removeList);
//      for (DataRow device : deviceList) {
//        device.set("name", device.getString("name"));
//        device.set("id", device.getString("id"));
//        devList.add(device);
//      }
//    }
//
//    setAttribute("deviceList", devList);
//    new JSONObject(); setAttribute("deviceInfo", JSONObject.fromObject(row));
//    setAttribute("Id", id);
//    setAttribute("typeId", typeId);
//    return new ActionResult("/WEB-INF/views/usercon/editDevice.jsp");
//  }

  public ActionResult doEditDeviceInfo1()
  {
    String id = getStrParameter("id");
    Integer typeId = Integer.valueOf(getIntParameter("typeId"));
    DataRow row = null;
    DataRow row1 = null;
    if ((id != null) && (id.length() > 0)) {
      row = this.service.getDeviceInfo(id, typeId.toString());
    }

    if (typeId.intValue() == 3) {
      ArraysiteService arrayService = new ArraysiteService();
      if ((row != null) && (row.size() > 0)) {
        row1 = arrayService.getArraysiteInfo(Integer.valueOf(row.getInt("ele_id")));
      }
    }
    StorageService storservice = new StorageService();

    String limitIds = (String)getSession().getAttribute("tpc_storage_list");
    List rows = storservice.getSubsystemNames(null, limitIds);
    setAttribute("device", rows);
    new JSONObject(); setAttribute("deviceInfo", JSONObject.fromObject(row));
    new JSONObject(); setAttribute("deviceInfo1", JSONObject.fromObject(row1));
    setAttribute("Id", id);
    setAttribute("typeId", typeId);
    return new ActionResult("/WEB-INF/views/usercon/editDevice1.jsp");
  }

//  public void doLoadEle()
//  {
//    String id = getStrParameter("deviceId");
//    Integer typeId = Integer.valueOf(getIntParameter("typeId"));
//    JSONArray array = new JSONArray();
//    List eleList = null;
//    ArraysiteService arrayService;
//    if (typeId.intValue() == 3) {
//      arrayService = new ArraysiteService();
//      eleList = arrayService.getArraysiteList(null, null, Integer.valueOf(Integer.parseInt(id)));
//      List elesList = this.service.getDeviceList(id, typeId.toString());
//      List removeList = new ArrayList();
//      if ((eleList != null) && (eleList.size() > 0))
//      {
//        Iterator localIterator2;
//        label205: for (Iterator localIterator1 = eleList.iterator(); localIterator1.hasNext(); 
//          localIterator2.hasNext())
//        {
//          DataRow dataRow = (DataRow)localIterator1.next();
//          if ((elesList == null) || (elesList.size() <= 0)) break label205;
//          localIterator2 = elesList.iterator(); continue; DataRow dataRow2 = (DataRow)localIterator2.next();
//          if (dataRow.getString("disk_group_id").equals(dataRow2.getString("ele_id")))
//          {
//            removeList.add(dataRow);
//          }
//
//        }
//
//        eleList.removeAll(removeList);
//        for (DataRow dataRow : eleList) {
//          JSONObject obj = new JSONObject();
//          obj.put("ele_id", dataRow.getString("disk_group_id"));
//          obj.put("ele_name", dataRow.getString("the_display_name"));
//          array.add(obj);
//        }
//      }
//    } else if (typeId.intValue() == 4) {
//      arrayService = new LibraryService();
//    }
//
//    ResponseHelper.print(getResponse(), array);
//  }

  public void doTestAcctDevice()
  {
    Integer typeId = Integer.valueOf(getIntParameter("typeId"));
    String Id = getStrParameter("Id");
    String[] device_id = getStrArrayParameter("device");
    String[] deviceId = checkStrArray(device_id, "multiselect-all");
    String user = getStrParameter("user");
    String password = getStrParameter("password").replaceAll("&amp;nbsp;", " ");
    JSONObject obj = new JSONObject();
    int j = 0;
    for (int i = 0; i < deviceId.length; i++) {
      DataRow row = null;
      if (typeId.intValue() == 1) {
        StorageService storservice = new StorageService();
        row = storservice.getSubsystemInfo(Integer.valueOf(Integer.parseInt(deviceId[i])));
      } else if (typeId.intValue() == 2) {
        SwitchService switchService = new SwitchService();
        row = switchService.getSwitchInfo(Integer.valueOf(Integer.parseInt(deviceId[i])));
      }
      else if (typeId.intValue() == 5) {
        StorageService storservice = new StorageService();

        row = storservice.getSubsystemInfo(Integer.valueOf(Integer.parseInt(deviceId[i])));
      }
      if ((row != null) && (row.size() > 0)) {
        String[] ip = row.getString("ip_address").replaceAll("&amp;nbsp;", " ").split(",");
        String ipaddress = "";
        if (ip.length > 1)
          ipaddress = ip[1];
        else {
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
          this.service.addDeviceInfo(dataRow);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (con != null) {
            con.close();
          }
        }
      }
    }
    obj.put("fcount", Integer.valueOf(j));
    ResponseHelper.print(getResponse(), obj);
  }

  public void doTestAcctDevice1()
  {
    String devId = getStrParameter("devices");
    Integer typeId = Integer.valueOf(getIntParameter("typeId"));
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
      if (typeId.intValue() == 3) {
        LibraryService libraryService = new LibraryService();
        row = libraryService.getLibraryInfo(Integer.valueOf(Integer.parseInt(eleId[i])));
        StorageService storservice = new StorageService();
        row1 = storservice.getSubsystemInfo(Integer.valueOf(Integer.parseInt(devId)));
      } else if (typeId.intValue() == 4)
      {
        StorageService storservice = new StorageService();
        row1 = storservice.getSubsystemInfo(Integer.valueOf(Integer.parseInt(devId)));
      }

      if ((row != null) && (row.size() > 0)) {
        String[] ip = row1.getString("ip_address").replaceAll("&amp;nbsp;", " ").split(",");
        String ipaddress = "";
        if (ip.length > 1)
          ipaddress = ip[1];
        else {
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
          this.service.addDeviceInfo(dataRow);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (con != null) {
            con.close();
          }
        }
      }
    }
    obj.put("fcount", Integer.valueOf(j));
    ResponseHelper.print(getResponse(), obj);
  }
}