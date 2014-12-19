package root.customjstl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class FormateTimestamp extends TagSupport{

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
			Long time = Long.parseLong(value);
			String sdf = new SimpleDateFormat(pattern).format(new Date(time));
			pageContext.getOut().print(sdf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
}
