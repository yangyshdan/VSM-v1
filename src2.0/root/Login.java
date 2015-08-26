package root;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.CookieHelper;
import com.huiming.base.util.RequestHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.DES;
import com.huiming.service.user.UserEngineService;
import com.huiming.service.user.UserService;
import com.huiming.service.widget.WidgetService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.huiming.web.base.BaseAction;
import com.project.web.WebConstants;
/**
 * @Name Login
 * @Author gugu
 * @Date 2013-8-8 05:44:18
 * @Description TODO
 */
public class Login extends BaseAction {
	private UserService service = new UserService();
	private UserEngineService userEngineService = new UserEngineService();
	public ActionResult doDefault() throws Exception {
		String loginid = RequestHelper.getString(getRequest(), "loginName");
		String password = RequestHelper.getString(getRequest(), "password");
		//POST提交表单
		if (isPostBack()) {
			if (StringHelper.isEmpty(loginid) || StringHelper.isEmpty(password)) {
				addActionError("用户名或密码不能为空！");
				return new ActionResult("/login.jsp", true);
			}
			DataRow client = findClientByLoginId(loginid);
			if (client == null || !client.getString("floginname").equals(loginid) || !client.getString("fpassword").equals(password)) {
				addActionError("用户名或密码错误！");
				return new ActionResult("/login.jsp", true);
			}
			
			//成功登录
			saveLoginData(client);
			setSsoCookie(getResponse(), client);
			getSession().setAttribute(WebConstants.SHOW_TAG, 0);
//			String retUrl = getStrParameter("retUrl");
//			if (StringHelper.isNotEmpty(retUrl))
//			{
//				retUrl = URLEncoder.encode(retUrl);
//				//返回登录URL
//				return new ActionResult(retUrl);
//			}
			return new ActionResult("/servlet/index/Index", false);
		//直接访问页面
		} else {
			// 删除存在的登录信息
			removeLoginData();
			String retMsg = RequestHelper.getString(getRequest(), "retMsg");
			if (StringHelper.isNotEmpty(retMsg)) {
				addActionError(retMsg);
				return new ActionResult("/login.jsp");
			}
			return new ActionResult("/login.jsp");
		}
	}
	
	/**
	 * @param loginId
	 * @return
	 */
	public DataRow findClientByLoginId(String loginId) {
		DataRow client = null;
		client = service.getUserByName(loginId);
		if (client != null) {
			DES des = new DES();
			client.set("fpassword",des.decrypt(client.getString("fpassword"), "utf-8"));
		}
		return client;
	}
	
	/**
	 * 描述：登陆成功，将用户数据保存到session
	 */
	@SuppressWarnings("rawtypes")
	protected void saveLoginData(DataRow client) throws Exception {
		//清空Session
		Enumeration e = getSession().getAttributeNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			getSession().removeAttribute(name);
		}
		
		getSession().setAttribute(WebConstants.SESSION_CLIENT_LOGIN_ID, client.getString("floginname"));
		getSession().setAttribute(WebConstants.SESSION_CLIENT_NAME, client.getString("fname"));
		//用户的userId
		getSession().setAttribute(WebConstants.SESSION_CLIENT_ID, client.getLong("fid"));
		//用户类型标记
		getSession().setAttribute(WebConstants.SESSION_CLIENT_TYPE, client.getString("froleid"));
		//这个权限是控制“设置”及其附属子功能是否在页面显示。
		getSession().setAttribute(WebConstants.SESSION_AUTHORITY_KEY,service.getMenuIdsByUserId(client.getInt("fid")));

		//保存所有设备类型到session
		Map<String, String> deviceType = new HashMap<String, String>();
		deviceType.put(SrContant.DEVTYPE_VAL_DS,SrContant.DEVTYPE_LBL_DS);
		deviceType.put(SrContant.DEVTYPE_VAL_BSP,SrContant.DEVTYPE_LBL_BSP);
		deviceType.put(SrContant.DEVTYPE_VAL_SVC,SrContant.DEVTYPE_LBL_SVC);
		deviceType.put(SrContant.DEVTYPE_VAL_EMC,SrContant.DEVTYPE_LBL_EMC);
		deviceType.put(SrContant.DEVTYPE_VAL_HDS,SrContant.DEVTYPE_LBL_HDS);
		deviceType.put(WebConstants.STORAGE_TYPE_VAL_NETAPP,WebConstants.STORAGE_TYPE_LBL_NETAPP);
		deviceType.put(SrContant.DEVTYPE_VAL_SWITCH,SrContant.DEVTYPE_LBL_SWITCH);
		deviceType.put(SrContant.DEVTYPE_VAL_HOST,WebConstants.DEVTYPE_LBL_HOST);
		getSession().setAttribute("vsm_devtype", deviceType);
		
		//保存用户可见的设备
		getSession().setAttribute(WebConstants.PHYSICAL_LIST, getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_PHYSICAL, null, null));
		getSession().setAttribute(WebConstants.VIRTUAL_LIST, getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_VIRTUAL, null, null));
		getSession().setAttribute(WebConstants.HYPERVISOR_LIST, getUserDefinedDeviceIds(WebConstants.DEVTYPE_HYPERVISOR, null, null));
		getSession().setAttribute(WebConstants.SWITCH_LIST, getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_SWITCH, null, null));
		getSession().setAttribute(WebConstants.FABRIC_LIST, getUserDefinedDeviceIds(SrContant.DEVTYPE_VAL_FABRIC, null, null));
		getSession().setAttribute(WebConstants.ZONEZET_LIST, getUserDefinedDeviceIds(SrContant.DEVTYPE_VAL_ZONESET, null, null));
		getSession().setAttribute(WebConstants.ZONE_LIST, getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_ZONE, null, null));
		getSession().setAttribute(WebConstants.TPC_STORAGE_LIST, getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_TPC, null));
		getSession().setAttribute(WebConstants.SR_STORAGE_LIST, getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, SrContant.DBTYPE_SR, null));
		
		//获取所有类型的性能指标信息,并保存到session中
		List<DataRow> prfList = new WidgetService().getFprffildList(null);
		getSession().setAttribute(WebConstants.ALL_KPI_LIST, prfList);
	}
	
	public ActionResult doLogOut() throws Exception {
		removeLoginData();
		return new ActionResult("/login.jsp", false);
	}
	
	/**
	 * 获取用户可见的设备
	 * @param deviceType
	 * @param storageType
	 * @param parentId
	 * @return
	 */
	private String getUserDefinedDeviceIds(String deviceType,String storageType,Integer parentId) {
		Long userId = (Long) getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
		String userType = (String) getSession().getAttribute(WebConstants.SESSION_CLIENT_TYPE);
		String definedIds = null;
		if (!userType.equals(SrContant.ROLE_SUPER)) {
			definedIds = userEngineService.getUserDefinedDevIds(userId, deviceType, storageType, parentId);
			if (definedIds == null) {
				definedIds = WebConstants.NO_DATA_SIGN;
			}
		}
		return definedIds;
	}
	
	/**
	 * 保存用户登录信息到Cookie
	 */
	protected void setSsoCookie(HttpServletResponse response, DataRow client) throws Exception {
		long time = System.currentTimeMillis();
		StringBuffer buffer = new StringBuffer();
		buffer.append("id").append(":").append(client.getString("id")).append("|");
		buffer.append("loginName").append(":").append(client.getString("floginname")).append("|");
		buffer.append("name").append(":").append(client.getString("fname")).append("|");
		buffer.append("role").append(":").append(client.getString("froleid")).append("|");
		buffer.append("time").append(":").append(time);
		String loginIdcookie = URLEncoder.encode(client.getString("loginname"), "utf-8");
		String securityStr = URLEncoder.encode(buffer.toString() + "|" + time) + "|" + loginIdcookie;
		CookieHelper.setCookie(response, "sso_cookie", securityStr, -1, "/", "");
	}
	
	/**
	 * 删除用户登录信息
	 */
	private void removeLoginData() {
		getSession().invalidate();
		CookieHelper.setCookie(getResponse(), "sso_cookie", "", 0, "/", "");
	}
	
}
