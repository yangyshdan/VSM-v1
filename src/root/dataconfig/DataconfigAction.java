package root.dataconfig;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.ResponseHelper;
import com.huiming.service.dataconfig.DataconfigService;
import com.huiming.web.base.ActionResult;
import com.project.web.SecurityAction;

public class DataconfigAction extends SecurityAction{
	DataconfigService service = new DataconfigService();
	public ActionResult doDefault(){
		DataRow perfConfig = service.getDataConfigInfo("perfromance");
		DataRow logConfig = service.getDataConfigInfo("eventlog");
		this.setAttribute("perfConfig", perfConfig);
		this.setAttribute("logConfig", logConfig);
		return new ActionResult("/WEB-INF/views/alert/dataconfig.jsp");
	}
	
	public void doUpdateSetting(){
		int ptime = getIntParameter("ptime");
		int etime = getIntParameter("etime");
		DataRow prow = new DataRow();
		prow.set("time_length", ptime);
		DataRow erow = new DataRow();
		erow.set("time_length", etime);
		try {
			service.updateDataconfig("perfromance", prow);
			service.updateDataconfig("eventlog", erow);
			ResponseHelper.print(getResponse(), "true");
		} catch (Exception e) {
			ResponseHelper.print(getResponse(), "false");
			e.printStackTrace();
		}
	}
}
