package com.project.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import root.Login;

import com.project.exception.SSOException;
import com.huiming.base.config.Configuration;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ConvertHelper;
import com.huiming.base.util.CookieHelper;
import com.huiming.base.util.SessionHelper;
import com.huiming.base.util.StringHelper;

public class LoginSessionManage
{
	
	
	private static Logger logger = Logger.getLogger(LoginSessionManage.class);
	
	
	private static String SESSION_TIME = "J_SESSION_TIME";
	
	
	
	
	/**
	 *描述：获取cookie保存的值
	 * @return
	 * @throws SSOException 
	 */
	public static DataRow getSsoCookie(HttpServletRequest request) throws SSOException
	{
		DataRow dataRow = null;
		String cookieStr = CookieHelper.getCookieValue(request, Configuration.getString("sso.cookiename"));
		//cookieStr = URLDecoder.decode(cookieStr);
		if (cookieStr != null && cookieStr.indexOf("|") > 0)
		{
			String[] str = cookieStr.split("\\|");
			String tmp = str[0];
			tmp = URLDecoder.decode(tmp);
			dataRow = setStringValue(dataRow, tmp);
			String time = tmp.substring(tmp.lastIndexOf("|")+1);
			//判断时间戳
			if (time.length() > 0 && !time.equals(dataRow.getString("time")))
			{
				dataRow = null;
				throw new SSOException("Cookie无效！");
			}
		}
		return dataRow;
	}
	
	
	
	/**
	 * 描述：用来验证登陆是否超时
	 * @param request
	 * @param response
	 */
	private static boolean isCookieNotTimeout(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
	{
		int maxInactiveInterval = Configuration.getInt("session.maxInactiveInterval", 30);
		String domain = Configuration.getString("session.domain");
		
		if (maxInactiveInterval == -1)
		{
			return true;
		}
		
		long loginTime = 0;
		long lastAccessTime = 0;
		long currentTime = System.currentTimeMillis();
		
		String sessionTimeStr = CookieHelper.getCookieValue(httpRequest, SESSION_TIME);
		//如果不存在SESSION_TIME值则设为初始的0
		if (StringHelper.isEmpty(sessionTimeStr))
		{
			sessionTimeStr = "0";
		}
		
		lastAccessTime = ConvertHelper.strToLong(sessionTimeStr);
		
		
		//如果没有超时则更新SESSION_TIME为当前时间值值
		if (currentTime - lastAccessTime <= maxInactiveInterval * 60 * 1000)
		{
			sessionTimeStr = String.valueOf(currentTime);
		}
		else
		{
			//通过sso_cookie取登陆时间
			String cookieStr = CookieHelper.getCookieValue(httpRequest, Configuration.getString("sso.cookiename"));
			cookieStr = URLDecoder.decode(cookieStr);
			if (cookieStr != null && cookieStr.indexOf("|") > 0)
			{
				String[] str = cookieStr.split("\\|");
				if (str.length > 1 && str[1].length() > 0)
				{
					loginTime = ConvertHelper.strToLong(str[1]);
				}
			}
			
			
			//如果登陆时间在最后访问之间之后，则说明用户已经重新登陆过，更新SESSION_TIME
			if (loginTime == 0 || loginTime > lastAccessTime)
			{
				sessionTimeStr = String.valueOf(currentTime);
			}
			else
			//如果登录时间在最后访问时间之前，则说明已经超时，需要清楚ssocookie和当前系统的会话，并重新初始化
			{
				sessionTimeStr = "0";
				LoginSessionManage.removeLoginData(httpRequest, httpResponse);
			}
		}
		Cookie cookie = new Cookie(SESSION_TIME, sessionTimeStr);
		if (!StringHelper.isEmpty(domain))
		{
			cookie.setDomain(domain);
		}
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		httpResponse.addCookie(cookie);
		
		return !"0".equals(sessionTimeStr);
	}
	
	
	
	/**
	 * 描述：校验用户登录信息，
	 * @param request
	 * @param response
	 */
	private static void checkLoginData(HttpServletRequest request, HttpServletResponse response)
	{
		//判断cookie，看用户是否已经登陆，用于sso权限验证
		DataRow cookieInf = null;
		try
		{
			String loginIdCleartext = getLoginIdFromSsocookie(request);//明文
			if (StringHelper.isEmpty(loginIdCleartext))
			{
				return;
			}
			
			String loginIdInSession = SessionHelper.getString(WebConstants.SESSION_CLIENT_LOGIN_ID, request.getSession());
			//判断用户是否登陆（session是否存在），并检验和cookie是否一致
			if (!loginIdInSession.equals(loginIdCleartext))
			{
				cookieInf = getSsoCookie(request);
				if (cookieInf == null)
				{
					return;
				}
				String loginIdInCookie = cookieInf.getString("name");
				if (StringHelper.isNotEmpty(loginIdInCookie) && loginIdInCookie.equals(loginIdCleartext))
				{
					setLoginSession(request, response, cookieInf);
				}
				else
				{
					removeLoginData(request, response);
				}
				
			}
		}
		catch (SSOException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		
	}
	
	
	
	/**
	 * 描述：用来判断用户登陆和写入session
	 * @param request
	 */
	public static void saveLoginSessionData(HttpServletRequest request, HttpServletResponse response)
	{
		if (isCookieNotTimeout(request, response))
		{
			checkLoginData(request, response);
		}
	}
	
	
	
	/**
	 * 描述：根据cookie中的值写session
	 * @param request
	 * @param response
	 * @param cookieInf
	 */
	private static void setLoginSession(HttpServletRequest request, HttpServletResponse response, DataRow cookieInf)
	{
		String loginId = cookieInf.getString("name");
		DataRow client = new Login().findClientByLoginId(loginId);
		String id = client.getString("id"); //用户自动编号
		request.getSession().invalidate();
		
		//向session当中写入 客户登陆id（昵称） 自增长id 等级 真实姓名
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_ID, new Long(id));
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_LOGIN_ID, loginId);
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_NAME, client.getString("name"));
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_TYPE, client.getString("type"));

	}
	
	
	
	/**
	 * 描述：清除用户登录信息，包括论坛空间，当前系统会话，单点登陆cookie
	 * @param request
	 * @param response
	 */
	public static void removeLoginData(HttpServletRequest request, HttpServletResponse response)
	{
		request.getSession().invalidate();
		CookieHelper.setCookie(response, Configuration.getString("sso.cookiename"), "", 0, "/", Configuration.getString("sso.domain"));
		
	}
	
	
	
	/**
	 * 描述：获取cookie中明文loginid
	 * @param request
	 * @return
	 */
	private static String getLoginIdFromSsocookie(HttpServletRequest request)
	{
		String cookieStr = CookieHelper.getCookieValue(request, Configuration.getString("sso.cookiename"));
		//logger.info(cookieStr);
		if (cookieStr != null)
		{
			String[] cookieStrs = cookieStr.split("\\|");
			if (cookieStrs != null && cookieStrs.length == 2 && cookieStrs[1].length() > 0)
			{
				try
				{
					return java.net.URLDecoder.decode(cookieStrs[1], "utf-8");
				}
				catch (UnsupportedEncodingException e)
				{
					logger.error("cookie编码异常：" + e, e);
				}
			}
		}
		return null;
	}
	
	/**
	 *描述：根据一个规则的字符串填充一个DataRow 字符串必须是这种格式 key1:value1|key2:value2|key3:value3
	 * @param dataRow
	 * @param tmp
	 * @return
	 */
	private static DataRow setStringValue(DataRow dataRow, String tmp)
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
	
}
