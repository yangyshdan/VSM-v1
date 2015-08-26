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
	 *��������ȡcookie�����ֵ
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
			//�ж�ʱ���
			if (time.length() > 0 && !time.equals(dataRow.getString("time")))
			{
				dataRow = null;
				throw new SSOException("Cookie��Ч��");
			}
		}
		return dataRow;
	}
	
	
	
	/**
	 * ������������֤��½�Ƿ�ʱ
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
		//������SESSION_TIMEֵ����Ϊ��ʼ��0
		if (StringHelper.isEmpty(sessionTimeStr))
		{
			sessionTimeStr = "0";
		}
		
		lastAccessTime = ConvertHelper.strToLong(sessionTimeStr);
		
		
		//���û�г�ʱ�����SESSION_TIMEΪ��ǰʱ��ֵֵ
		if (currentTime - lastAccessTime <= maxInactiveInterval * 60 * 1000)
		{
			sessionTimeStr = String.valueOf(currentTime);
		}
		else
		{
			//ͨ��sso_cookieȡ��½ʱ��
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
			
			
			//����½ʱ����������֮��֮����˵���û��Ѿ����µ�½�����SESSION_TIME
			if (loginTime == 0 || loginTime > lastAccessTime)
			{
				sessionTimeStr = String.valueOf(currentTime);
			}
			else
			//����¼ʱ����������ʱ��֮ǰ����˵���Ѿ���ʱ����Ҫ���ssocookie�͵�ǰϵͳ�ĻỰ�������³�ʼ��
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
	 * ������У���û���¼��Ϣ��
	 * @param request
	 * @param response
	 */
	private static void checkLoginData(HttpServletRequest request, HttpServletResponse response)
	{
		//�ж�cookie�����û��Ƿ��Ѿ���½������ssoȨ����֤
		DataRow cookieInf = null;
		try
		{
			String loginIdCleartext = getLoginIdFromSsocookie(request);//����
			if (StringHelper.isEmpty(loginIdCleartext))
			{
				return;
			}
			
			String loginIdInSession = SessionHelper.getString(WebConstants.SESSION_CLIENT_LOGIN_ID, request.getSession());
			//�ж��û��Ƿ��½��session�Ƿ���ڣ����������cookie�Ƿ�һ��
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
	 * �����������ж��û���½��д��session
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
	 * ���������cookie�е�ֵдsession
	 * @param request
	 * @param response
	 * @param cookieInf
	 */
	private static void setLoginSession(HttpServletRequest request, HttpServletResponse response, DataRow cookieInf)
	{
		String loginId = cookieInf.getString("name");
		DataRow client = new Login().findClientByLoginId(loginId);
		String id = client.getString("id"); //�û��Զ����
		request.getSession().invalidate();
		
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_ID, new Long(id));
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_LOGIN_ID, loginId);
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_NAME, client.getString("name"));
		request.getSession().setAttribute(WebConstants.SESSION_CLIENT_TYPE, client.getString("type"));

	}
	
	
	
	/**
	 * ����������û���¼��Ϣ��������̳�ռ䣬��ǰϵͳ�Ự�������½cookie
	 * @param request
	 * @param response
	 */
	public static void removeLoginData(HttpServletRequest request, HttpServletResponse response)
	{
		request.getSession().invalidate();
		CookieHelper.setCookie(response, Configuration.getString("sso.cookiename"), "", 0, "/", Configuration.getString("sso.domain"));
		
	}
	
	
	
	/**
	 * ��������ȡcookie������loginid
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
					logger.error("cookie�����쳣��" + e, e);
				}
			}
		}
		return null;
	}
	
	/**
	 *���������һ��������ַ����һ��DataRow �ַ���������ָ�ʽ key1:value1|key2:value2|key3:value3
	 * @param dataRow
	 * @param tmp
	 * @return
	 */
	private static DataRow setStringValue(DataRow dataRow, String tmp)
	{
		if (tmp != null && tmp.indexOf(":") > 0)
		{//������һ����:ֵ
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
