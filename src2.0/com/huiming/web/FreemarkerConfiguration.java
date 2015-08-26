package com.huiming.web;   
  
import freemarker.template.Configuration;   
  
public class FreemarkerConfiguration {   
       
    private static Configuration config = null;   
       
    /**  
     * Static initialization.  
     *   
     * Initialize the configuration of Freemarker.  
     */  
    static{   
        config = new Configuration();   
        config.setClassForTemplateLoading(FreemarkerConfiguration.class, "template");   
      //设置FreeMarker的模版文件位置
//      config.setServletContextForTemplateLoading(getServletContext(),"templates");
    }   
       
    public static Configuration getConfiguation(){   
        return config;   
    }   
  
}  
