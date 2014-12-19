package root;

import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;
import com.project.web.WebConstants;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.CookieHelper;
import com.huiming.base.util.RequestHelper;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.base.util.security.DES;
import com.huiming.service.user.UserService;
import com.huiming.web.base.ActionResult;
import com.huiming.web.base.BaseAction;
/**
 * @Name Login
 * @Author gugu
 * @Date 2013-8-8 05:44:18
 * @Description TODO
 */
public class Login extends BaseAction
{
	
	public ActionResult doDefault() throws Exception
	{
		String loginid = RequestHelper.getString(getRequest(), "loginName");
		String password = RequestHelper.getString(getRequest(), "password");

		//POST提交表单
		if (isPostBack())
		{
			if (StringHelper.isEmpty(loginid) || StringHelper.isEmpty(password))
			{
				addActionError("用户名或密码不能为空！");
				return new ActionResult("/login.jsp", true);
			}
			DataRow client = findClientByLoginId(loginid);
			if (client == null || !client.getString("floginname").equals(loginid) || !client.getString("fpassword").equals(password))
			{
				addActionError("用户名或密码错误！");
				return new ActionResult("/login.jsp", true);
			}
			
			saveLoginData(client);
			setSsoCookie(getResponse(), client);
//			String retUrl = getStrParameter("retUrl");
//			if (StringHelper.isNotEmpty(retUrl))
//			{
//				retUrl = URLEncoder.encode(retUrl);
//				//返回登录URL
//				return new ActionResult(retUrl);
//			}
				return new ActionResult("/servlet/index/Index",false);
		}
		//直接访问页面
		else
		{
			//删除存在的登录信息
			removeLoginData();
			String retMsg = RequestHelper.getString(getRequest(), "retMsg");
			if (StringHelper.isNotEmpty(retMsg))
			{
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
	public DataRow findClientByLoginId(String loginId)
	{
		DataRow client = null;
		UserService service = new UserService();
		client = service.getUserByName(loginId);
		if(client != null){
			DES des = new DES();
			client.set("fpassword", des.decrypt(client.getString("fpassword")));
		}
		return client;
	}
	
	/**
	 * 描述：登陆成功，将用户数据保存到session
	 */
	protected void saveLoginData(DataRow client) throws Exception
	{
		getSession().setAttribute(WebConstants.SESSION_CLIENT_ID, new Long(client.getInt("fid")));
		getSession().setAttribute(WebConstants.SESSION_CLIENT_LOGIN_ID, client.getString("floginname"));
		getSession().setAttribute(WebConstants.SESSION_CLIENT_NAME, client.getString("fname"));
		getSession().setAttribute(WebConstants.SESSION_CLIENT_TYPE, client.getString("froleid"));
		
	}
	
	public ActionResult doLogOut() throws Exception {
		removeLoginData();
		return new ActionResult("/login.jsp", false);
	}
	
	/**
	 * ��������½�ɹ������û���ݱ��浽cookie 
	 */
	protected void setSsoCookie(HttpServletResponse response, DataRow client) throws Exception
	{
		long time = System.currentTimeMillis();
		//cookie��ʽΪ key0:value0|key1:value1
		StringBuffer buffer = new StringBuffer();
		//�û�id
		buffer.append("id").append(":").append(client.getString("id")).append("|");
		buffer.append("loginName").append(":").append(client.getString("floginname")).append("|");
		//�û�����
		buffer.append("name").append(":").append(client.getString("fname")).append("|");
		//�û���ɫ
		buffer.append("role").append(":").append(client.getString("froleid")).append("|");
		//ʱ��
		buffer.append("time").append(":").append(time);
		String loginIdcookie = URLEncoder.encode(client.getString("loginname"), "utf-8");
		String securityStr = URLEncoder.encode(buffer.toString() + "|" + time) + "|" + loginIdcookie;
		//дcookie 
		CookieHelper.setCookie(response, "sso_cookie", securityStr, -1, "/", "");
	}
	
	/**
	 * ������ɾ����ǰ��½ʱ��������
	 */
	private void removeLoginData()
	{
		// ��session���� �ͻ���½��Ϣ
		getSession().invalidate();
		
		// ɾ��cookie �û���Ϣ��
		CookieHelper.setCookie(getResponse(), "sso_cookie", "", 0, "/", "");
	}
	
}
