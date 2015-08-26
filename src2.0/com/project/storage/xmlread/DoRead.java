package com.project.storage.xmlread;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.project.storage.entity.Info;

public class DoRead extends DefaultHandler{
	private List<Info> infos=null;
	private Info info;
	private String preTag = null; // 记录解析上个节点的值
	//创建工厂
	public List<Info> getStorageInfo(){
		DoRead read = new DoRead();
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("service.xml");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(input,read);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return read.getInfos();
	}

	public List<Info> getInfos() {
		return infos;
	}
	/**
	 * 读取内容
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		 if (this.preTag != null) {
		      String content = new String(ch, start, length);
		      if ("username".equals(this.preTag))
		        this.info.setUsername(content);
		      else if ("password".equals(this.preTag))
		        this.info.setPassword(content);
		      else if ("ctl0_ip".equals(this.preTag))
		        this.info.setIpAddress(content);
		      else if ("ctl1_ip".equals(this.preTag))
		        this.info.setIp1Address(content);
		      else if ("native_cli_path".equals(this.preTag))
		        this.info.setNativePath(content);
		      else if ("type".equals(this.preTag))
		        this.info.setType(content);
		      else if ("name".equals(this.preTag))
		        this.info.setSystemName(content);
		}
	}

	/**
	 * 开始文档
	 */
	public void startDocument() throws SAXException {
		infos=new ArrayList<Info>();
	}

	/**
	 * 开始读取节点元素
	 */
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("storage".equals(qName)){
        	info = new Info();
        	info.setSystemName(attrs.getValue("name"));
        	info.setSubSystemID(Integer.parseInt(attrs.getValue("id")));
        }
		preTag = qName;
	}
	
	/**
	 * 当元素结束
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("storage".equals(qName)){
        	infos.add(info);
        	info=null;
        }
        preTag = null;
	}
}
