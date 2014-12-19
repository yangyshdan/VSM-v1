package root.usercon;

import java.util.List;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.security.DES;
import com.huiming.service.user.RoleService;
import com.huiming.service.user.UserService;
import com.huiming.web.base.ActionResult;
import com.huiming.web.base.BaseAction;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;


public class AccountMgrAction extends SecurityAction{

	private static Logger logger =Logger.getLogger(AccountMgrAction.class);
	
	public ActionResult doDefault() throws Exception {
		
		String clientType = (String) getSession().getAttribute(WebConstants.SESSION_CLIENT_TYPE);
		int curPage= getIntParameter("curPage",1);
		DBPage page = userService.getPage(curPage, WebConstants.NumPerPage);
		
		if(clientType.equals("1")){
			getRequest().setAttribute("dbPage", page);
			return new ActionResult("/WEB-INF/views/usercon/userList.jsp");
		}else{
			return new ActionResult("");
		}
		
	}
	
	public ActionResult doAjaxPage() throws Exception{
	
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DBPage dbPage = userService.getPage(curPage, numPerPage);
		getRequest().setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/usercon/ajaxAccount.jsp");
	}
	
	public ActionResult doAdd() throws Exception{
		
		List<DataRow> roleList = roleService.getList();
		
		getRequest().setAttribute("roleList", roleList);
		return new ActionResult("/WEB-INF/views/usercon/addUser.jsp");
	}
	
	public void doSave() throws Exception{
		
		String userName = getStrParameter("userName");
		String pwd = getStrParameter("pwd");
		String email = getStrParameter("email");
		String roleIds = getStrParameter("roleIds");
		DataRow roleData =roleService.getById(roleIds);
		try{
			DataRow data = new DataRow();
			DES des = new DES();
			data.set("floginname", userName);
			data.set("fpassword", des.encrypt(pwd));
			data.set("femail", email);
			data.set("fname", roleData.getString("fname"));
			userService.save(data);//保存用户
			int lateUserId = userService.getLateId();//获取最新id
			
			DataRow role= new DataRow();
			role.set("froleid", roleIds);
			role.set("fuserid", lateUserId);
			roleService.save(role);
			
			ResponseHelper.print(getResponse(), true);
			
		}catch(Exception e){
			logger.info(e);
			e.printStackTrace();
			ResponseHelper.print(getResponse(), false);
		}
	}
	
	public ActionResult doEdit() throws Exception{
		
		String fid = getStrParameter("fid");
		DataRow user = userService.getUserById(fid);
		List<DataRow> roleList = roleService.getList();
		DataRow userRole = roleService.getByUserId(user.getString("fid"));
		
		getRequest().setAttribute("roleList", roleList);
		getRequest().setAttribute("userRoleStr", userRole.getString("fid"));
		getRequest().setAttribute("user", user);
		return new ActionResult("/WEB-INF/views/usercon/editUser.jsp");
	}
	
	public ActionResult doEditPwd() throws Exception{
		String fid = getStrParameter("fid");
		DataRow user = userService.getUserById(fid);
		getRequest().setAttribute("user", user);
		getRequest().setAttribute("oldPwd", new DES().decrypt(user.getString("fpassword")));
		return new ActionResult("/WEB-INF/views/usercon/editPwd.jsp");
	}
	
	public void doUpdate() throws Exception{
		String fid = getStrParameter("fid");
		String userName = getStrParameter("userName");
		String pwd = getStrParameter("pwd");
		String email = getStrParameter("email");
		String roleIds = getStrParameter("roleIds");
		DataRow roleData =roleService.getById(roleIds);
		
		DataRow data = new DataRow();
		DES des = new DES();
		data.set("fid", fid);
		data.set("floginname", userName);
		data.set("fpassword", des.encrypt(pwd));
		data.set("femail", email);
		data.set("fname", roleData.getString("fname"));
		userService.update(data);//更改用户
		roleService.delete(fid);
		DataRow role= new DataRow();
		role.set("froleid", roleIds);
		role.set("fuserid", fid);
		roleService.save(role);
		ResponseHelper.print(getResponse(), true);
			
	}
	
	public void doUpdatePwd() throws Exception{
		String fid= getStrParameter("fid");
		String pwd = getStrParameter("pwd");
		String newPwd = getStrParameter("newPwd");
		boolean flag=false;
		DataRow userInfo=userService.getUserById(fid);
		String oldPwd = new DES().decrypt(userInfo.getString("fpassword"));
		if(!pwd.equals(oldPwd)){
			getRequest().setAttribute("error", "原密码错误");
		}else{
			flag=true;
			DataRow data = new DataRow();
			data.set("fid", fid);
			data.set("fpassword", new DES().encrypt(newPwd));
			userService.update(data);	
		}
		
		
		ResponseHelper.print(getResponse(), flag);
	}
	
	public void doDelete() throws Exception{
		String fid = getStrParameter("fid");
		userService.delete(fid);
		roleService.delete(fid);
		ResponseHelper.print(getResponse(), true);
	}
	
	public ActionResult doAjaxFilter() throws Exception{
		String userName =getStrParameter("userName");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		DBPage dbPage = userService.getPage(curPage, numPerPage,userName);
		getRequest().setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/usercon/ajaxAccount.jsp");
	}
	
	

	
	private RoleService roleService = new RoleService();
	private UserService userService= new UserService();
}
