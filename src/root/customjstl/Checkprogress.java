package root.customjstl;

import java.text.DecimalFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class Checkprogress extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private String available;
	private String total;
	
	
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
		
		StringBuffer sb = new StringBuffer();
		try {
			if(available.length()>0){
				aval = Double.parseDouble(available);
			}else{
				percent=100d;
			}
			if(total!=null && total.length()>0){
				all = Double.parseDouble(total);
				percent = Double.valueOf(new DecimalFormat("0.00").format(aval/all*100));
			}else{
				percent=0d;
			}
			if(0<percent && percent<=60){
				sb.append("<div style='width:100%;'><div style='float:left; width:70%;'>");
				sb.append("<div class='progress progress-striped progress-success active' style='margin-bottom:0px;'>");
				sb.append("<div class='bar' style='width: "+percent+"%'>" +percent+"%</div></div></div><div style='float:right; width:30%;'>"+new DecimalFormat("0.00").format(aval)+"</div></div>");
			}else if(60<percent && percent<=90){
				sb.append("<div style='width:100%;'><div style='float:left; width:70%;'>");
				sb.append("<div class='progress progress-warning progress-striped active' style='margin-bottom:0px;'>");
				sb.append("<div class='bar' style='width: "+percent+"%'>" +percent+"%</div></div></div><div style='float:right; width:30%;'>"+new DecimalFormat("0.00").format(aval)+"</div></div>");
			}else if(percent > 90){
				sb.append("<div style='width:100%;'><div style='float:left; width:70%;'>");
				sb.append("<div class='progress progress-danger progress-striped active' style='margin-bottom:0px;'>");
				sb.append("<div class='bar' style='width: "+percent+"%'>" +percent+"%</div></div></div><div style='float:right; width:30%;'>"+new DecimalFormat("0.00").format(aval)+"</div></div>");
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
