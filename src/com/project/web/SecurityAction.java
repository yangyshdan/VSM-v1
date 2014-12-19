package com.project.web;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.project.web.WebConstants;
import com.project.exception.SSOException;
import com.huiming.base.config.Configuration;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.CookieHelper;
import com.huiming.base.util.SessionHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.web.base.ActionResult;
import com.huiming.web.base.BaseAction;


/**
 * ����: ����ע���û���½�Ĺ�����������������ʵ��SSO�ĵ�½�жϣ����Ƿ�cookie�Ѿ���д�롣
 */
public class SecurityAction extends BaseAction
{
	
	private static Logger logger = Logger.getLogger(SecurityAction.class);

	/**
	 *��������������ע���û��ĵ�½���
	 * @throws Exception
	 */
	public ActionResult execute(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//���в�ѯ�û���Ȩ�޼��
		if (!isClientLogin(request, response))
		{
			//��ת��ͬһ��½ҳ��
			toSsoLoginPage(request, response);
			return null;
		}
		return super.execute(request, response);
	}
	
	/**
	 * �ж��û��Ƿ��Ѿ���¼
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
	 *��������ת��ͳһ��½ҳ��
	 */
	public static void toSsoLoginPage(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			String retUrl = request.getRequestURL().toString();
			String queryString = request.getQueryString();
			if(StringHelper.isNotEmpty(queryString))
			retUrl = retUrl + "?" + queryString;
			logger.info("�����½��url=========="+retUrl);
			response.sendRedirect("/vsm/login.jsp?retUrl=" + URLEncoder.encode(retUrl,"UTF-8"));
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(),ex);
		}
	}
	
	/**
	 *��������ȡcookie�����ֵ
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
				throw new SSOException("Cookie������Ч��");
			}
		}
		return dataRow;
	}
	
	/**
	 *���������һ��������ַ����һ��DataRow �ַ���������ָ�ʽ key1:value1|key2:value2|key3:value3
	 */
	private DataRow setStringValue(DataRow dataRow, String tmp)
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
