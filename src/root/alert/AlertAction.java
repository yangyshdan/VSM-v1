package root.alert;

import java.net.URLDecoder;

import com.huiming.base.jdbc.DBPage;
import com.huiming.service.alert.AlertService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;
import com.project.web.WebConstants;

public class AlertAction extends SecurityAction{
	public ActionResult doDefault(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		AlertService service = new AlertService();
		DBPage dbPage = service.getPage(curPage, numPerPage,null, null, null, null, null);
		
		setAttribute("logPage", dbPage);
		return new ActionResult("/WEB-INF/views/alert/alertList.jsp");
	}
	
	public ActionResult doAjaxPage(){
		int curPage = getIntParameter("curPage",1);
		int numPerPage = getIntParameter("numPerPage",WebConstants.NumPerPage);
		String level = getStrParameter("flevel");
		String state = getStrParameter("fstate");
		String startDate = URLDecoder.decode(getStrParameter("fstartdate"));
		String	endDate = URLDecoder.decode(getStrParameter("fenddate"));
		AlertService service = new AlertService();
		DBPage dbPage = service.getPage(curPage, numPerPage,null, state, level, startDate, endDate);
		setAttribute("flevel", level);
		setAttribute("fstate", state);
		setAttribute("logPage", dbPage);
		return new ActionResult("/WEB-INF/views/alert/ajaxLog.jsp");
	}
}
