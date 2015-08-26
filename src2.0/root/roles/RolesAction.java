package root.roles;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.service.roles.RolesService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;
import com.project.x86monitor.MyUtilities;

public class RolesAction extends SecurityAction{
	private RolesService service = new RolesService();
	
	public ActionResult doDefault(){
		setAttribute("dbPage", service.getRolesPage(1, WebConstants.NumPerPage));
		return new ActionResult("/WEB-INF/views/roles/rolesList.jsp");
	}
	
	private long getUserIdFromSession(){
		Object obj = getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
		return obj == null? -1L : (Long)obj;
	}
	
	public void doGetAllDevices(){
		String devtype = getStrParameter("devtype");
		String pid = getStrParameter("pid");
		String devIds = getStrParameter("devIds");
		String roleIds = getStrParameter("roleIds", "0");
		int action = getIntParameter("action", SrContant.STATE_ADD);
//		Logger.getLogger(getClass()).info("GetAllDevices: " + action);
		JsonData jsonData = new JsonData();
		try {
			List<DataRow> array = service.getAllDevices(devtype, pid, devIds, roleIds, action);
			jsonData.setValue(array);
		} catch(Exception e){
			Logger.getLogger(getClass()).error("", e);
			jsonData.setMsg("获取权限数据失败！");
			jsonData.setSuccess(false);
		}
		ResponseHelper.print(getResponse(), JSON.toJSONString(jsonData));
	}
	
	public ActionResult doAjaxPage(){		
		int curPage = getIntParameter("curPage", 1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		DBPage dbPage = service.getRolesPage(curPage, numPerPage);
		setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/roles/ajaxRoles.jsp");
	}
	
	public ActionResult doShowRolesInfoDlg(){
		Long roleId = getLongParameter("roleId", -1L);
		int def = SrContant.STATE_ADD; // 给定默认值
		if(roleId != null && roleId > 0){ // 说明处于编辑或者查看状态
			this.setAttribute("roleId", roleId);
			DataRow dr = service.getRoleNameById(roleId);
			this.setAttribute("roleName", dr == null? "" : dr.getString("fname"));
			def = SrContant.STATE_CHECK;
		}
		this.setAttribute("action", getIntParameter("action", def));
		
		return new ActionResult("/WEB-INF/views/roles/rolesInfoDlg.jsp");
	}
	
	private void loadDR(List<DataRow> data, long devid, String ostype, String devtype, 
			String pid, String name, String id){
		DataRow d = new DataRow();
		d.set("fmenuId", devid);
		d.set("fdevtype", devtype);
		d.set("parentId", pid);
		d.set("os_type", ostype);
		d.set("menu_name", name);
		d.set("menu_id", id);
		data.add(d);
	}
	
	private void loadDR(List<DataRow> data, long devid, String devtype, String pid, String name, String id){
		DataRow d = new DataRow();
		d.set("fmenuId", devid);
		d.set("fdevtype", devtype);
		d.set("parentId", pid);
		d.set("menu_name", name);
		d.set("menu_id", id);
		data.add(d);
	}
	// 这个函数肯定是用于新增的
	/**
	 * @see 递归查询
	 */
	private void load(List<DataRow> data, String devtype, String pid, String devIds){
		List<DataRow> array = service.getAllDevices(devtype, pid, devIds, "0", SrContant.STATE_ADD);
		if(array != null && array.size() > 0){
			for(DataRow dr : array){
				loadDR(data, dr.getLong("devid"), dr.getString("devtype"),
						dr.getString("pid"), dr.getString("name"), dr.getString("id"));
				load(data, dr.getString("devtype"), dr.getString("id"), dr.getString("devid"));
			}
		}
	}
	
	private void load(List<DataRow> data, String devtype, String osType, String pid, String devIds){
		List<DataRow> array = service.getAllDevices(devtype, pid, devIds, "0", SrContant.STATE_ADD);
		if(array != null && array.size() > 0){
			for(DataRow dr : array){
				loadDR(data, dr.getLong("devid"), dr.getString("sto_type"), dr.getString("devtype"),
						dr.getString("pid"), dr.getString("name"), dr.getString("id"));
				load(data, dr.getString("devtype"), dr.getString("sto_type"), dr.getString("id"), dr.getString("devid"));
			}
		}
	}
	
	private List<DataRow> getRoleMenuData(){
		// 描述某个节点属于什么类型
		String m_phy = "m_phy", vir_01 = "vir_01", hyp_01 = "hyp_01",
			m_fab = "m_fab", sw_01 = "sw_01", zset_01 = "zset_01",
			m_storage = "m_storage", m_application = "m_application";
		
		int authSize = this.getIntParameter("authSize", 0);
		List<DataRow> data = new ArrayList<DataRow>(authSize * 5);
		long devId;
		String devType;
		String pid, id, name;
		boolean hasChildren;
		for(int i = 0; i < authSize; ++i){
			//devid, devtype, has, pid, id, n
			devType = getStrParameter("dtp" + i);
			hasChildren = getIntParameter("has" + i, 0) == 1;
			devId = getLongParameter("did" + i, -1L);
			id = getStrParameter("id" + i);
			pid = getStrParameter("pid" + i);
			name = getStrParameter("n" + i);
			if(devType == null || devType.isEmpty()){ continue; }
			if(SrContant.SUBDEVTYPE_STORAGE.equals(devType)){
				loadDR(data, devId, getStrParameter("ot" + i), devType, pid, name, id);
			}
			else {
				loadDR(data, devId, devType, pid, name, id);
			}
			if(hasChildren){ continue; }
			if(m_phy.equals(devType)){ // 勾选”物理机“
				load(data, devType, id, String.valueOf(devId));
			}
			else if(SrContant.SUBDEVTYPE_PHYSICAL.equals(devType)){
//				if(hasChildren){ continue; } // 该物理机所对应的所有设备
				load(data, devType, id, String.valueOf(devId));
			}
			else if(vir_01.equals(devType)){
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
			else if(hyp_01.equals(devType)){
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
			else if(m_fab.equals(devType)){ // 勾选“Fabric网络”
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
			else if(SrContant.DEVTYPE_VAL_FABRIC.equals(devType)){ // 勾选其中一项“Fabric网络”
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
			else if(sw_01.equals(devType)){ // 勾选其中一项“Fabric网络”
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
			else if(zset_01.equals(devType)){ // 勾选其中一项“Fabric网络”
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
			else if(SrContant.DEVTYPE_VAL_ZONESET.equals(devType)){ // 勾选其中一项“ZoneSet”
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
			else if(m_storage.equals(devType)){
//				if(hasChildren){ continue; }
//				load(data, devType, id, String.valueOf(devId));
				load(data, devType, m_storage, id, String.valueOf(devId));
			}
			else if(m_application.equals(devType)){
//				if(hasChildren){ continue; }
				load(data, devType, id, String.valueOf(devId));
			}
		}
		return data;
	}
	public void doSaveRole(){
		JsonData jsonData = new JsonData();
		try{
			service.saveRoleMenus(MyUtilities.htmlToText(getStrParameter("roleName")), getRoleMenuData());
			jsonData.setMsg("保存角色成功!");
		}catch(Exception e){
			Logger.getLogger(getClass()).error("", e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		print(jsonData);
	}
	
	public void doUpdateRole(){
		JsonData jsonData = new JsonData();
		try{
			long roleId = getLongParameter("roleId", -1L);
			String roleName = MyUtilities.htmlToText(getStrParameter("roleName"));
			if(roleId > 0){
				service.updateRoleMenus(roleId, roleName, getRoleMenuData());
				jsonData.setMsg("编辑角色成功!");
				
				// 因为修改角色，所以要更新一下权限
				WebConstants.resetAuthority(getSession(), getUserIdFromSession());
			}
			else {
				jsonData.setSuccess(false);
				jsonData.setMsg("角色编号不正确!");
			}
		}catch(Exception e){
			Logger.getLogger(getClass()).error("", e);
			jsonData.setSuccess(false);
			jsonData.setMsg(e.getMessage());
		}
		print(jsonData);
	}
	
	public void doAjaxDelete(){
		long roleId = getLongParameter("id", -1L);
		JsonData jsonData = new JsonData();
		if(roleId > 0){
			try{
				// 	在删除角色之前，确定其他用户是否拥有这个角色
				List<DataRow> data = service.getUsersWithThisRole(roleId);
				if(data == null){  // 没有受角色影响的用户
					service.deleteRoleById(roleId);
					jsonData.setMsg("成功删除角色");
				}
				else {
					jsonData.setSuccess(false);
					jsonData.setMsg("请到\"用户管理\"编辑拥有该角色的用户, 把该角色从用户的权限选项移除!");
				}
			}catch(Exception e){
				jsonData.setMsg(e.getMessage());
				jsonData.setSuccess(false);
				Logger.getLogger(getClass()).error("", e);
			}
		}
		else {
			jsonData.setMsg("编号"+roleId+"不正确");
			jsonData.setSuccess(false);
		}
		print(jsonData);
	}
}
