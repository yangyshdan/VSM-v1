package root.upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import net.sf.json.JSONObject;

import com.huiming.base.util.FileHelper;
import com.jspsmart.upload.Files;
import com.jspsmart.upload.SmartUpload;
import com.project.web.WebConstants;


@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet{
	
	public void service(ServletRequest request, ServletResponse response)throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        
        SmartUpload su = new SmartUpload();
        JspFactory factory = JspFactory.getDefaultFactory();
        PageContext pageContext = null;
        JSONObject obj = new JSONObject();
        String fileName = UUID.randomUUID().toString();
        try {
        	initFile();
			pageContext = factory.getPageContext(this, request, response, "", true, 8192, true);
			su.initialize(pageContext);
			int size = su.getSize();
			if(size>=400){
				obj.put("state", false);
				obj.put("msg", "图片不能大于400KB");
			}else{
				su.upload();
				Files file = su.getFiles();   //必须在upload后才有值
				//此为得到文件的扩展名,getFile(0)为得到唯一的一个上传文件
				String ext=file.getFile(0).getFileExt();
				String name = fileName+"."+ext;
				su.getFiles().getFile(0).saveAs("report/upload/"+name);
				WebConstants.REPORT_LOGO_URL="report/upload/"+name;
				obj.put("fileName", name);
				obj.put("state", true);
				obj.put("ext", ext);
			}
		} catch (Exception e) {
			obj.put("state", false);
			obj.put("msg",e.toString());
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.flush();
	}
	
	private void initFile(){
		String projectPath = getServletContext().getRealPath("/").replaceAll("%20", "\" \"");
		File f = new File(projectPath+"/report/upload");
		String[] childfile = f.list();
		if(childfile.length>0){
			File[] files = f.listFiles();
			for (File file2 : files) {
				FileHelper.deleteFile(file2.getAbsolutePath());
			}
		}
	}
}
