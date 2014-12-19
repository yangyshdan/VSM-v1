package root.customjstl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class CheckStatus extends TagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 20131112110939L;
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if(value==null || value.length()==0){
				pageContext.getOut().print("");
			}else if("Normal".equalsIgnoreCase(value)){
				pageContext.getOut().print("<span class='label'>"+value+"</span>");
			}else if("Warning".equalsIgnoreCase(value)){
				pageContext.getOut().print("<span class='label label-warning'>"+value+"</span>");
			}else{
				pageContext.getOut().print("<span class='label label-important'>"+value+"</span>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

}
