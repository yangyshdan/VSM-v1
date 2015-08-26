package root.apps;

import org.apache.log4j.Logger;

import com.huiming.base.jdbc.DBPage;
import com.huiming.service.apps.AppsService;
import com.huiming.service.baseprf.BaseprfService;
import com.huiming.sr.constants.SrContant;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;
import com.project.x86monitor.JsonData;

public class AppsAction extends SecurityAction{
	BaseprfService baseService = new BaseprfService();
	
	private long getUserIdFromSession(){
		Object obj = getSession().getAttribute(WebConstants.SESSION_CLIENT_ID);
		return obj == null? -1L : (Long)obj;
	}
	
	private String getUserTypeFromSession(){
		Object obj = getSession().getAttribute(WebConstants.SESSION_CLIENT_TYPE);
		return obj == null? SrContant.ROLE_USER : (String)obj;
	}
	
	
	private AppsService service = new AppsService();
	
	public ActionResult doDefault(){
		DBPage dbPage = service.getAppPage(1, WebConstants.NumPerPage, null, getUserIdFromSession(), 
				getUserTypeFromSession());
		setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/apps/appList.jsp");
	}
	
	public ActionResult doAjaxPage(){
		String name = getStrParameter("name");
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage", WebConstants.NumPerPage);
		DBPage dbPage = service.getAppPage(curPage, numPerPage, name, getUserIdFromSession(), 
				getUserTypeFromSession());
		setAttribute("dbPage", dbPage);
		return new ActionResult("/WEB-INF/views/apps/ajaxApp.jsp");
	}
	
	public void doAjaxDelete(){
		long id = getLongParameter("id", -1L);
		JsonData jsonData = new JsonData();
		if(id > 0){
			try{
				service.deleteApp(id);
				jsonData.setMsg("成功删除应用");
			}catch(Exception e){
				jsonData.setMsg(e.getMessage());
				jsonData.setSuccess(false);
				Logger.getLogger(getClass()).info("", e);
			}
		}
		else {
			jsonData.setMsg("应用的编号不正确");
			jsonData.setSuccess(false);
		}
		print(jsonData);
	}
//	void out(String msg, Object obj){
//		Logger.getLogger(getClass()).info("****************************************************");
//		Logger.getLogger(getClass()).info(msg);
//		Logger.getLogger(getClass()).info(JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss"));
//		Logger.getLogger(getClass()).info("****************************************************");
//	}

}
