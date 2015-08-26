package root.customjstl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;



public class CheckIsNa extends TagSupport{

	private static final long serialVersionUID = 5430834843442453717L;
	
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
			if(value==null||value.equals("")||Double.parseDouble(value)==0){
				
				pageContext.getOut().print("N/A");
			}else{
				
				pageContext.getOut().print(value);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			try {
				pageContext.getOut().print("N/A");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return EVAL_PAGE;
	}
	
	

}
