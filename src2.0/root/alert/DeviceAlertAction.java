package root.alert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.base.util.StringHelper;
import com.huiming.service.alert.DeviceAlertService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;

public class DeviceAlertAction extends SecurityAction{
	
	/**
	 * 加载事件列表
	 */
	public ActionResult doDefault(){
		//获取用户可见设备
		String phyLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String vmLimitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		String storageLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, null, null);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		
		DeviceAlertService service = new DeviceAlertService();
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		String resourceType = getStrParameter("resourceType", null);
		int logtype = getIntParameter("logType", -1);
		int level = getIntParameter("level", -1);
		int state = getIntParameter("state", -1);
		DBPage dbPage = service.getLogPage(curPage, numPerPage, logtype, null,
				null, null, null, resourceType, state, level, null, null,
				phyLimitIds, vmLimitIds, storageLimitIds, switchLimitIds);
		setAttribute("dbPage", dbPage);
		setAttribute("resourceType", resourceType);
		setAttribute("state", state);
		setAttribute("level", level);
		return new ActionResult("/WEB-INF/views/alert/deviceAlert.jsp");
	}
	
	/**
	 * 分页查询事件列表
	 * @return
	 */
	public ActionResult doAjaxPage() {
		//获取用户可见设备
		String phyLimitIds = (String) getSession().getAttribute(WebConstants.PHYSICAL_LIST);
		String vmLimitIds = (String) getSession().getAttribute(WebConstants.VIRTUAL_LIST);
		String storageLimitIds = getUserDefinedDeviceIds(SrContant.SUBDEVTYPE_STORAGE, null, null);
		String switchLimitIds = (String) getSession().getAttribute(WebConstants.SWITCH_LIST);
		DBPage page = null;
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		int level = getIntParameter("level",-1);
		int logtype = getIntParameter("logType", -1);
		int state = getIntParameter("state", -1);
		String resourceName = null;
		String resourceId=getStrParameter("resourceId");
		String topId=getStrParameter("topId");
		try {
			resourceName = URLDecoder.decode(getStrParameter("resourceName"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String resourType = getStrParameter("resourceType");
		String startDate = URLDecoder.decode(getStrParameter("startDate"));
		String endDate = URLDecoder.decode(getStrParameter("endDate"));
		DeviceAlertService service = new  DeviceAlertService();
		// topo根据resourceId,resourceType(ftoptype),level
		page = service.getLogPage(curPage, numPerPage, logtype, topId, null,
				resourceId, resourceName, resourType, state, level, startDate,
				endDate, phyLimitIds, vmLimitIds, storageLimitIds, switchLimitIds);
		setAttribute("dbPage", page);
		setAttribute("resourceId", resourceId);
		setAttribute("topId", topId);
		setAttribute("resourceType", resourType);
		setAttribute("state", state);
		setAttribute("level", level);
		setAttribute("logType", logtype);
		setAttribute("resourceName", resourceName);
		setAttribute("startDate", startDate);
		setAttribute("endDate", endDate);
		
		// HGC的代码
		// 这行代码是给Ajax附加一些条件
		setAttribute("attachment", String.format("&level=%s&state=%s", level, state));
		// HGC的代码
		
		return new ActionResult("/WEB-INF/views/alert/ajaxDAlert.jsp");
	}
	
	public ActionResult doPrepareEdit(){
		int id = getIntParameter("id",-1);
		DataRow row = null;
		if(id != -1){
			DeviceAlertService service = new DeviceAlertService();
			row = service.getAlertById(id);
		}
		setAttribute("dAlert", row);
		return new ActionResult("/WEB-INF/views/alert/dAlertInfo.jsp");
	}
	
	public void doGoToEventDetailPage(){
		Long ftopid = this.getLongParameter("ftopid");
		Long fresourceid = this.getLongParameter("fresourceid");
		String ftoptype = this.getStrParameter("ftoptype");
		StringBuilder errorMsg = new StringBuilder(50);
		JsonData jsonData = new JsonData();
		int i = 0;
		if(ftopid == null || ftopid <= 0){
			++i;
			errorMsg.append(i + "、设备编号为空<br>");
		}
		if(ftoptype == null || ftoptype.trim().isEmpty()){
			++i;
			errorMsg.append(i + "、设备类型为空<br>");
		}
		else {
			DataRow dr;
			DeviceAlertService service = new DeviceAlertService();
			ftoptype = ftoptype.toLowerCase();
			if(WebConstants.HYPERVISOR.equalsIgnoreCase(ftoptype)){
				dr = service.getValue("SELECT HOST_COMPUTER_ID FROM t_res_hypervisor WHERE HYPERVISOR_ID=" + ftopid);
				String hid;
				if(dr == null){ hid = "-1"; }
				else {
					hid = dr.getString("hypervisor_id");
					if(hid == null || hid.trim().isEmpty()){ hid = "-1"; }
				}
				jsonData.setValue(String.format(
					"servlet/hypervisor/HypervisorAction?func=HypervisorInfo&computerId=%s&hypervisorId=%s",
					hid, ftopid));
			}
			else if(WebConstants.VIRTUAL.equalsIgnoreCase(ftoptype)){
				dr = service.getValue("SELECT COMPUTER_ID FROM t_res_virtualmachine WHERE vm_id=" + fresourceid);
				String cid;
				if(dr == null){ cid = "-1"; }
				else {
					cid = dr.getString("computer_id");
					if(cid == null || cid.trim().isEmpty()){
						cid = "-1";
					}
				}
				jsonData.setValue(String.format(
					"servlet/virtual/VirtualAction?func=VirtualInfo&computerId=%s&hypervisorId=%s&vmId=%s",
					cid, ftopid, fresourceid));
			}
			else if(ftoptype.equals("storage")){
				jsonData.setValue("servlet/storage/StorageAction?func=StorageInfo&subSystemID=" + ftopid);
			}
			else if(ftoptype.equals("switch")){
				jsonData.setValue("servlet/switchs/SwitchAction?func=SwitchInfo&switchId=" + ftopid);
			}
			else {
				++i;
				errorMsg.append(i + "、设备类型不是physical、virtual、storage和switch<br>");
			}
		}
		if(i > 0){
			jsonData.setSuccess(false);
			jsonData.setMsg(errorMsg.toString());
		}
		print(jsonData);
	}
	
	public void doDelAlert(){
		String ids = getStrParameter("ids","");
		if(StringHelper.isNotEmpty(ids)){
			try {
				DeviceAlertService service = new DeviceAlertService();
				service.deleteAlert(ids);
				ResponseHelper.print(getResponse(), "true");
			} catch (Exception e) {
				e.printStackTrace();
				ResponseHelper.print(getResponse(), "false");
			}
		}
		ResponseHelper.print(getResponse(), "false");
	}
	
	public void doNoForward(){
		String ids = getStrParameter("ids","");
		if(StringHelper.isNotEmpty(ids)){
			try {
				DeviceAlertService service = new DeviceAlertService();
				service.noForward(ids);
				ResponseHelper.print(getResponse(), "true");
			} catch (Exception e) {
				e.printStackTrace();
				ResponseHelper.print(getResponse(), "false");
			}
		}
		ResponseHelper.print(getResponse(), "false");
	}
	
	/**
	 * 确认事件
	 */
	public void doDisposeAlert(){
		DeviceAlertService service = new DeviceAlertService();
		String ruleId = getStrParameter("fruleid");
		String remark = getStrParameter("remark");
		String confirmUser = (String) getSession().getAttribute(WebConstants.SESSION_CLIENT_LOGIN_ID);
		if(service.updateAlert(ruleId,confirmUser,remark)){
			ResponseHelper.print(getResponse(), "true");
		}else{
			ResponseHelper.print(getResponse(), "false");
		}
	}
	
	/**
	 * 告警详细信息
	 * @return
	 */
	public ActionResult doDeviceInfo() {
		DeviceAlertService service = new DeviceAlertService();
		String ruleId = getStrParameter("ruleId");
		String topId = getStrParameter("topId");
		String resourType = getStrParameter("resourceType").endsWith("undefined")?"":getStrParameter("resourceType");
		this.setAttribute("dAlert", service.getalertbyId(ruleId,topId));
		setAttribute("resourceType", resourType);
		return new ActionResult("/WEB-INF/views/alert/deviceAlertInfo.jsp");
	}
	
}
