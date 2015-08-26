package root.report;

import net.sf.json.JSONObject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.report.ReportService;

public class ReportJob implements Job{

	public void execute(JobExecutionContext context) throws JobExecutionException {

		DataRow data = (DataRow) context.getJobDetail().getJobDataMap().get("row");
		JSONObject res= (JSONObject) context.getJobDetail().getJobDataMap().get("res");
		try {
			
			rm.doReportFtl(data, res);
			ts.addReport(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private ReportMaker rm = new ReportMaker();
	private ReportService ts = new ReportService();

}
