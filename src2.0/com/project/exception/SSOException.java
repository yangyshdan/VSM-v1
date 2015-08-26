package com.project.exception;

/**
 * ����:  SSO��½�쳣
 */
public class SSOException extends Exception
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2684694244068141031L;
	
	
	
	public SSOException()
	{
		super();
	}
	
	
	public SSOException(String message)
	{
		super(message);
	}
	
	
	public SSOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	public SSOException(Throwable cause)
	{
		super(cause);
	}
}
