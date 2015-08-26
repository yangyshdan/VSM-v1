package root.customjstl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class CheckActive extends TagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 20131112175811L;
	
	private Integer value;

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if(value==0){
				pageContext.getOut().print("<i class=\"icon icon-color icon-close\"></i>否");
			}else{
				pageContext.getOut().print("<i class=\"icon icon-color icon-check\"></i>是");
			}
		} catch (Exception e) {
			
		}
		return EVAL_PAGE;
	}
	
	

}
