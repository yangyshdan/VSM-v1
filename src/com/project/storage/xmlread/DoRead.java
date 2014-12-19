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
		if(preTag!=null){
			String content = new String(ch, start, length);
			if ("username".equals(preTag)) {
				info.setUsername(content);
			} else if ("password".equals(preTag)) {
				info.setPassword(content);
			} else if ("ip".equals(preTag)) {
				info.setIpAddress(content);
			} else if ("native_cli_path".equals(preTag)) {
				info.setNativePath(content);
			}
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
