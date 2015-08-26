package com.huiming.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HtmlGenerator {

	private Configuration config = null;

	/**
	 * 创建多级目录
	 * 
	 * @param aParentDir
	 *            String
	 * @param aSubDir
	 *            以 / 开头
	 * @return boolean 是否成功
	 */
	public static boolean creatDirs(String aParentDir, String aSubDir) {
		File aFile = new File(aParentDir);
		if (aFile.exists()) {
			File aSubFile = new File(aParentDir + aSubDir);
			if (!aSubFile.exists()) {
				return aSubFile.mkdirs();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 生成静态文件
	 * 
	 * @param templateFileName
	 *            模版名称
	 * @param propMap
	 *            用于处理模板的属性Object映射
	 * @param htmlFilePath
	 *            要生成的静态文件的路径,相对设置中的根路径,例如 "/staticPage/day/"
	 * @return
	 */
	public boolean buildHtml(String templateFileName, Map variables, String htmlFilePath) {
		Template template = null;
		try {
			template = getFreemarkerCfg().getTemplate(templateFileName);
		} catch (IOException ex) {
			try {
				template = getFreemarkerCfg().getTemplate(templateFileName + ".svntmp");
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		try {
			getFreemarkerCfg().setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
			template.setEncoding("UTF-8");
			// 创建生成文件目录
			File htmlFile = new File(htmlFilePath);
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(htmlFile), "UTF-8"));
			template.process(variables, out);
			out.flush();
			out.close();
			return true;
		} catch (TemplateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 获取freemarker的配置.
	 */
	public Configuration getFreemarkerCfg() {
		if (null == config) {
			// Initialize the FreeMarker configuration;
			// - Create a configuration instance
			config = new Configuration();

			// templates是放在classpath下的一个目录
			config.setClassForTemplateLoading(this.getClass(),
			 "/templates");
			config.setDefaultEncoding("UTF-8");
		}

		return config;
	}

	
	 /**  
     * 生产html string  
     *   
     * @param template   the name of freemarker teamlate.  
     * @param variables  the data of teamlate.  
     * @return htmlStr  
     * @throws Exception  
     */  
    public String generateHtmlString(String template, Map<String,Object> variables) throws Exception{   
//        Configuration config = FreemarkerConfiguration.getConfiguation();
//        config.setServletContextForTemplateLoading(getServletContext(),"templates");
        getFreemarkerCfg().setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        Template tp = getFreemarkerCfg().getTemplate(template);   
        StringWriter stringWriter = new StringWriter();     
        BufferedWriter writer = new BufferedWriter(stringWriter);     
        tp.setEncoding("UTF-8");     
        tp.process(variables, writer);     
        String htmlStr = stringWriter.toString();   
        writer.flush();     
        writer.close();   
        return htmlStr;   
    }   
	
	 /**  
     * 生产pdf文件  
     *   
     * @param htmlStr the htmlstr  
     * @param out the specified outputstream  
     * @throws Exception  
     */  
//    public void generatePdf(String htmlStr, OutputStream out)   
//            throws Exception {   
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();   
//        Document doc = builder.parse(new ByteArrayInputStream(htmlStr.getBytes("utf-8")));   
//        ITextRenderer renderer = new ITextRenderer();   
//        renderer.setDocument(doc, null);   
//        renderer.layout();   
//        renderer.createPDF(out);   
//        out.close();   
//    }
}
