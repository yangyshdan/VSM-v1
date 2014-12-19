package com.project.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.project.web.LoginSessionManage;

public class SsoLoginFilter implements Filter
{
	
	private static Logger logger = Logger.getLogger(SsoLoginFilter.class);
	
	public void destroy()
	{
		if (logger.isInfoEnabled())
		{
			logger.info("SosLoginFilter destroy ----------");
		}
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		LoginSessionManage.saveLoginSessionData(httpRequest,httpResponse);
		
		chain.doFilter(request, response);
	}
	
	public void init(FilterConfig arg0) throws ServletException
	{
		if (logger.isInfoEnabled())
		{
			logger.info("SessionFilter init ----------");
		}
	}
}
