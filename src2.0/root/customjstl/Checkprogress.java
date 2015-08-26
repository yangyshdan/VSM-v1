package root.customjstl;

import java.text.DecimalFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class Checkprogress extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private String available;
	private String total;
	private String warning;
	private String error;
	
	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}
	
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
	@Override
	public int doEndTag() throws JspException {
		
		Double aval = 0d;
		Double percent = 0d;
		Double all = 0d;
		Double warn = 0d;
		Double err = 0d;
		
		StringBuffer sb = new StringBuffer();
		try {
			if(available.length()>0){
				aval = Double.parseDouble(available);
			}else{
				percent=100d;
			}
			if(warning.length()>0){
				warn = Double.parseDouble(warning);
			}else{
				warn=60d;
			}
			if(error.length()>0){
				err = Double.parseDouble(error);
			}else{
				err=85d;
			}
			if(total!=null && total.length()>0){
				all = Double.parseDouble(total);
				percent = Double.valueOf(new DecimalFormat("0.00").format(aval/all*100));
			}else{
				percent=0d;
			}
			if(0<=percent && percent<=warn){
				sb.append("<div style='width:100%;'><div style='float:left; width:98%;'>");
				sb.append("<div class='progress progress-striped progress-success active' style='margin-bottom:0px;'>");
				sb.append("<div class='bar' style='width: "+percent+"%'><span style='color:black;'>" +percent+"%</span></div></div></div><div style='float:right; width:30%;'></div></div>");
			}else if(warn<percent && percent<=err){
				sb.append("<div style='width:100%;'><div style='float:left; width:98%;'>");
				sb.append("<div class='progress progress-warning progress-striped active' style='margin-bottom:0px;'>");
				sb.append("<div class='bar' style='width: "+percent+"%'><span style='color:black;'>" +percent+"%</span></div></div></div><div style='float:right; width:30%;'></div></div>");
			}else if(percent > err){
				sb.append("<div style='width:100%;'><div style='float:left; width:98%;'>");
				sb.append("<div class='progress progress-danger progress-striped active' style='margin-bottom:0px;'>");
				sb.append("<div class='bar' style='width: "+percent+"%'><span style='color:black;'>" +percent+"%</span></div></div></div><div style='float:right; width:30%;'></div></div>");
			}else{
				sb.append("N/A");
			}
			pageContext.getOut().print(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

}
