package root.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.DES;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.service.user.UserService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;

public class UserAction extends SecurityAction{
	BaseprfService baseService = new BaseprfService();
	
	private long getUserIdFromSession(){
		Object obj = getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
		return obj == null? -1L : (Long)obj;
	}
	
	private String getUserTypeFromSession(){
		Object obj = getSession().getAttribute(WebConstants.SESSION_CLIENT_TYPE);
		return obj == null? SrContant.ROLE_USER : (String)obj;
	}
	
	private UserService service = new UserService();
	
	public ActionResult doDefault(){
		DBPage dbPage = service.getUserPage(1, WebConstants.NumPerPage, null, null,
				null, null, null, null, null, getUserIdFromSession(), 
				getUserTypeFromSession());
		setAttribute("dbPage", dbPage);
		setAttribute("curPage", 1);
		setAttribute("numPerPage", WebConstants.NumPerPage);
		return new ActionResult("/WEB-INF/views/user/userList.jsp");
	}
	
	public ActionResult doAjaxPage(){
		String userName = getStrParameter("userName");
		String loginName = getStrParameter("loginName");
		String account = getStrParameter("account");
		String idCard = getStrParameter("idCard");
		String gender = getStrParameter("gender");
		String startHireDate = getStrParameter("startHireDate");
		String endHireDate = getStrParameter("endHireDate");
		
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		DBPage dbPage = service.getUserPage(curPage, numPerPage, userName, loginName, account,
				idCard, gender, startHireDate, endHireDate, getUserIdFromSession(), 
				getUserTypeFromSession());
		setAttribute("curPage", curPage);
		setAttribute("numPerPage", numPerPage);
		setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/user/ajaxUser.jsp");
	}
	
	public ActionResult doShowUserInfoDlg(){
		long userId = getLongParameter("userId", -1L);
		setAttribute("level", getStrParameter("level", "1"));
		setAttribute("rolesData", service.getRolesByUserId(userId));
		setAttribute("userId", userId);
		setAttribute("action", getIntParameter("action"));
		setAttribute("userInfo", userId > 0L? 
				service.getUserById(userId) : new HashMap<String, Object>(0));
		return new ActionResult("/WEB-INF/views/user/userInfoDlg.jsp");
	}
	
	public void doGetMenuItems(){
		/*
		  1、新增   所有角色都不能选，异步获取节点菜单，roleIds为null
		  2、查看   把相应角色和权限都加载出来，并把输入框disabled，树形菜单不能勾选
		  3、编辑   把相应角色和权限都加载出来
		 */
		String roleIds = getStrParameter("roleIds");
		int action = getIntParameter("action");
		JsonData jsonData = new JsonData();
		List<DataRow> drs = null;
		try{
			drs = service.getMenuItemsByRoleId(roleIds, action);
			JSONArray array = createZTreeJSON(drs, 
					"id", "pid", "name", "checked", "url", "icon",
					new String[]{"devid", "devtype"});
			jsonData.setValue(array);
		}catch (Exception e) {
			Logger.getLogger(getClass()).error("", e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		print(jsonData);
	}
	
	/**
	 * @see 将查询数据合成ZTree的JSON数据格式
	 * @param drs
	 * @param idKey
	 * @param pidKey
	 * @param nameKey
	 * @param checkedKey
	 * @param urlKey
	 * @param iconKey
	 * @return
	 */
	private JSONArray createZTreeJSON(List<DataRow> drs, String idKey, String pidKey,
			String nameKey, String checkedKey, String urlKey, String iconKey, 
			String otherKeys[]){
		JSONArray array = new JSONArray(); // parent为null的扔进这个array
		if(drs == null || drs.size() == 0){ return array; }
		String pid, id, t, name;
		Map<String, JSONObject> records = new HashMap<String, JSONObject>(drs.size());
		JSONObject temp;
		JSONArray tmp;
		boolean isNotEmpty = otherKeys != null && otherKeys.length > 0;
		for(DataRow dr : drs){
			pid = dr.getString(pidKey);
			id = dr.getString(idKey);
			name = dr.getString(nameKey);
			JSONObject obj = new JSONObject();
			obj.put("id", id);
			obj.put("name", name);
			obj.put("pid", pid);
//			Logger.getLogger(getClass()).info("Test: " + dr.getBoolean("isparent"));
			obj.put("isParent", dr.getBoolean("isparent")); // 0表示false
			
			if(isNotEmpty){
				for(String key : otherKeys){
					obj.put(key, dr.getString(key));
				}
			}
			
			// id,name,children,checked,url,icon, devicetype
			obj.put("checked", dr.getInt(checkedKey) == 1);
			
			t = dr.getString(urlKey);
			if(t != null && t.trim().length() > 0){
				obj.put("url", t);
			}
			t = dr.getString(iconKey);
			if(t != null && t.trim().length() > 0){
				obj.put("icon", t);
			}
			
			records.put(id, obj);
			if(pid == null || pid.trim().isEmpty()){
				array.add(obj);
			}
			else {
				if(records.containsKey(pid)){
					temp = records.get(pid);
					if(temp.containsKey("children")){
						temp.getJSONArray("children").add(obj);
					}
					else {
						tmp = new JSONArray();
						tmp.add(obj);
						temp.put("children", tmp);
					}
				}
			}
		}
		// isParent，parentId
//		System.out.print(JSON.toJSONString(array));
		return array;
	}
	
	public void doSaveUserInfo(){
		JsonData jsonData = new JsonData();
		long userId = -1;
		boolean isOK = true;
		int urSize = getIntParameter("urSize", 0);
		try{
			String userName = MyUtilities.htmlToText(getStrParameter("userName")),
			loginName = MyUtilities.htmlToText(getStrParameter("loginName")),
			passwd01 = new DES().encrypt(MyUtilities.htmlToText(getStrParameter("passwd01")), "utf-8"),
			email = MyUtilities.htmlToText(getStrParameter("email"));
	
			DataRow tsuser = new DataRow();
			tsuser.set("fname", userName);
			tsuser.set("floginName", loginName);
			tsuser.set("fpassword", passwd01);
			tsuser.set("femail", email);
			tsuser.set("froleid", urSize == 0? SrContant.ROLE_SUPER : SrContant.ROLE_USER);
			userId = service.saveUser(tsuser);
		}catch(Exception e){
			jsonData.setMsg(e.getMessage());
			jsonData.setSuccess(false);
			isOK = false;
		}
		if(isOK && userId > 0L){
			try{
				if(urSize > 0) {
					DataRow dr; //urSize
					List<DataRow> tsuserrole = new ArrayList<DataRow>(urSize);
					for(int i = 0; i < urSize; ++i){
						dr = new DataRow();
						dr.set("fuserid", userId);
						dr.set("froleid", getLongParameter("ur" + i));
						tsuserrole.add(dr);
					}
					
					service.saveUserRoles(tsuserrole);
				}
				jsonData.setMsg("成功添加新用户！");
			}catch(Exception e){
				jsonData.setMsg(e.getMessage());
				jsonData.setSuccess(false);
				service.deleteUserById(userId);
				Logger.getLogger(getClass()).error("", e);
			}
		}
		print(jsonData);
	}
	
	public void doUpdateUserInfo(){
		JsonData jsonData = new JsonData();
		long userId = getLongParameter("userId", -1L);
		int urSize = getIntParameter("urSize", 0);
		if(userId <= 0){
			jsonData.setSuccess(false);
			jsonData.setMsg("该用户不存在！");
		}
		else {
			boolean isOK = true;
			try{
				String userName = MyUtilities.htmlToText(getStrParameter("userName")),
				loginName = MyUtilities.htmlToText(getStrParameter("loginName")),
				passwd01 = new DES().encrypt(MyUtilities.htmlToText(getStrParameter("passwd01")), "utf-8"),
				email = MyUtilities.htmlToText(getStrParameter("email"));
		
				DataRow tsuser = new DataRow();
				tsuser.set("fname", userName);
				tsuser.set("floginName", loginName);
				tsuser.set("fpassword", passwd01);
				tsuser.set("femail", email);
				tsuser.set("froleid", urSize == 0? SrContant.ROLE_SUPER : SrContant.ROLE_USER);
				service.updateUser(tsuser, userId);
			}catch(Exception e){
				jsonData.setMsg(e.getMessage());
				jsonData.setSuccess(false);
				Logger.getLogger(getClass()).error("", e);
				isOK = false;
			}
			if(isOK && userId > 0L){
				try{
					service.deleteUserRole(userId);
					if(urSize > 0) {
						DataRow dr;
						List<DataRow> tsuserrole = new ArrayList<DataRow>(urSize);
						for(int i = 0; i < urSize; ++i){
							dr = new DataRow();
							dr.set("fuserid", userId);
							dr.set("froleid", getLongParameter("ur" + i));
							tsuserrole.add(dr);
						}
						service.updateUserRoles(tsuserrole, userId);
					}
					
					WebConstants.resetAuthority(getSession(), userId);
					jsonData.setMsg("成功更新用户！");
				}catch(Exception e){
					jsonData.setMsg(e.getMessage());
					jsonData.setSuccess(false);
					service.deleteUserById(userId);
					Logger.getLogger(getClass()).error("", e);
				}
			}
		}
		
		print(jsonData);
	}
	
//	public static void main(String args[]){
//		System.out.println(new AES("1").encrypt("123456", "utf-8").equals("dK6t0XKuJTo="));
//		System.out.println(new DES().encrypt("123456", "utf-8").equals("dK6t0XKuJTo="));
//	}
	
	public void doAjaxDelete(){
		long id = getLongParameter("id", -1L);
		JsonData jsonData = new JsonData();
		if(id > 0){
			Long userId = (Long)getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
			if(id == userId){
				jsonData.setSuccess(false);
				jsonData.setMsg("您已登录，不能删除自己！");
			}
			else {
				DataRow user = service.getUserById(id);
				if(user != null && SrContant.ROLE_SUPER.equalsIgnoreCase(user.getString("froleid"))){
					jsonData.setSuccess(false);
					jsonData.setMsg("您没有删除其他超级管理员的权限！");
				}
				else {
					try{
						service.deleteUserById(id);
						jsonData.setMsg("成功删除用户");
					}catch(Exception e){
						jsonData.setMsg(e.getMessage());
						jsonData.setSuccess(false);
						Logger.getLogger(getClass()).error("", e);
					}
				}
			}
		}
		else {
			jsonData.setMsg("用户的编号不正确");
			jsonData.setSuccess(false);
		}
		print(jsonData);
	}
}
