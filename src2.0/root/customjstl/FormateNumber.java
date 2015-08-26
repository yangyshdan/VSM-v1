package root.customjstl;

import java.text.DecimalFormat;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class FormateNumber extends TagSupport {

	private static final long serialVersionUID = -2257758871420307972L;
	
	private String value;
	private String pattern;
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			if (value == null || value.equals("") || value.equals("null")) {
				pageContext.getOut().print("null");
			} else {
				String numVal = new DecimalFormat(pattern).format(Double.parseDouble(value));
				pageContext.getOut().print(numVal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
	
}
