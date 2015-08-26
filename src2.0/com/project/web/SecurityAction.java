package com.project.web;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.config.Configuration;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.CookieHelper;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.SessionHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.user.UserEngineService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.huiming.web.base.BaseAction;
import com.project.exception.SSOException;


/**
 * 描述: 增加注册用户登陆的过滤条件，并在这里实现SSO的登陆判断，看是否cookie已经被写入。
 */
public class SecurityAction extends BaseAction {
	private static Logger logger = Logger.getLogger(SecurityAction.class);
	private UserEngineService userEngineService = new UserEngineService();

	/**
	 *描述：用来过滤注册用户的登陆情况
	 * @throws Exception
	 */
	public ActionResult execute(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//进行查询用户的权限检查
		if (!isClientLogin(request, response))
		{
			//跳转到同一登陆页面
			toSsoLoginPage(request, response);
			return null;
		}
		return super.execute(request, response);
	}
	
	/**
	 * 判断用户是否已经登录
	 *
	 * @return
	 */
	public static boolean isClientLogin(HttpServletRequest request, HttpServletResponse response)
	{
		long clientId = SessionHelper.getLong(WebConstants.SESSION_CLIENT_ID, request.getSession());
	
		if (clientId != 0)
			return true;
		return false;
	}
	
	/**
	 *描述：跳转到统一登陆页面
	 */
	public static void toSsoLoginPage(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			String retUrl = request.getRequestURL().toString();
			String queryString = request.getQueryString();
			if(StringHelper.isNotEmpty(queryString))
			retUrl = retUrl + "?" + queryString;
			logger.info("非正常登陆，url=========="+retUrl);
			String requestUrl = request.getRequestURI();
			String root =requestUrl.substring(1,requestUrl.indexOf("/", 1));
			response.sendRedirect("/"+root+"/login.jsp?retUrl=" + URLEncoder.encode(retUrl,"UTF-8"));
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(),ex);
		}
	}
	
	/**
	 *描述：获取cookie保存的值
	 * @return
	 * @throws SSOException 
	 */
	protected DataRow getSsoCookie() throws SSOException
	{
		DataRow dataRow = null;
		String cookieStr = CookieHelper.getCookieValue(getRequest(), Configuration.getString("sso.cookiename"));
		cookieStr = URLDecoder.decode(cookieStr);
		if (cookieStr != null && cookieStr.indexOf("|") > 0)
		{
			String[] str = cookieStr.split("\\|");
			String tmp = str[0];
			dataRow = setStringValue(dataRow, tmp);
			//�ж�ʱ���
			if (str[1].length() > 0 && !str[1].equals(dataRow.getString("time")))
			{
				dataRow = null;
				throw new SSOException("Cookie密文无效！");
			}
		}
		return dataRow;
	}
	
	/**
	 *描述：根据一个规则的字符串填充一个DataRow 字符串必须是这种格式key1:value1|key2:value2|key3:value3
	 */
	private DataRow setStringValue(DataRow dataRow, String tmp)
	{
		if (tmp != null && tmp.indexOf(":") > 0)
		{//至少有一个键:值
			dataRow = new DataRow();
			String[] items = tmp.split("\\|");
			for (int i = 0; i < items.length; i++)
			{
				if (items[i].indexOf(":") > 0)
				{
					String[] item = items[i].split(":");
					if (item.length == 1)
					{
						dataRow.set(item[0], "");
					}
					else
					{
						dataRow.set(item[0], item[1]);
					}
				}
			}
		}
		return dataRow;
	}
	
	/**
	 * 获取用户可见的设备
	 * @param deviceType
	 * @param storageType
	 * @param parentId
	 * @return
	 */
	protected String getUserDefinedDeviceIds(String deviceType,String storageType, Integer parentId) {
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
	 * 获取登录用户的ID
	 * @return
	 */
	protected Long getLoginUserId() {
		Long userId = (Long) getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
		return userId;
	}
	
	/**
	 * 获取登录用户类型
	 * @return
	 */
	protected String getLoginUserType() {
		String userType = (String) getSession().getAttribute(WebConstants.SESSION_CLIENT_TYPE);
		return userType;
	}
	
	public void print(Object obj) {
		ResponseHelper.print(getResponse(), JSON.toJSONString(obj));
	}
	
	public void printWithDate(Object obj){
		ResponseHelper.print(getResponse(), JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss"));
	}
}
