package com.project.sax.storage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.sr.constants.SrContant;

public class StoragehbaSax extends DefaultHandler {
	private List<DataRow> storagehbas=null;
	private DataRow storagehba=null;
	//创建工厂
	public List<DataRow> getStoragehbas(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		StoragehbaSax handler = new StoragehbaSax();
		parser.parse(xmlStream, handler);
		return handler.getStoragehbas();

	}
	
	public List<DataRow> getStoragehbas() {
		return storagehbas;
	}
	/**
	 * 读取内容
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}

	/**
	 * 开始文档
	 */
	@Override
	public void startDocument() throws SAXException {
		storagehbas=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("RES_HBA".equals(qName)){
        	storagehba = new DataRow();
        	if(attrs.getValue("SERVER_IP_ADDRESS")!=null && attrs.getValue("SERVER_IP_ADDRESS").length()>0){
        		storagehba.set("server_ip_address", attrs.getValue("SERVER_IP_ADDRESS"));
        	}
        	if(attrs.getValue("SERVER_NAME")!=null && attrs.getValue("SERVER_NAME").length()>0){
        		storagehba.set("server_name", attrs.getValue("SERVER_NAME"));
        	}
        	if(attrs.getValue("UID")!=null && attrs.getValue("UID").length()>0){
        		storagehba.set("hba_uid", attrs.getValue("UID"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		storagehba.set("update_timestamp",SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        } 
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_HBA".equals(qName)){
        	storagehbas.add(storagehba);
        	storagehba=null;
        }
	}
}
