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

public class PortAndHBASax extends DefaultHandler {
	private List<DataRow> PortAndHBAs=null;
	private DataRow PortAndHBA=null;
	//创建工厂
	public List<DataRow> getPortAndHBAs(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PortAndHBASax handler = new PortAndHBASax();
		parser.parse(xmlStream, handler);
		return handler.getPortAndHBAs();

	}
	
	public List<DataRow> getPortAndHBAs() {
		return PortAndHBAs;
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
		PortAndHBAs=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("MAP_HBA_STORAGEPORT".equals(qName)){
        	PortAndHBA = new DataRow();
        	if(attrs.getValue("HBA_DEVICENAME")!=null && attrs.getValue("HBA_DEVICENAME").length()>0){
        		PortAndHBA.set("hba_devicename", attrs.getValue("HBA_DEVICENAME"));
        	}
        	if(attrs.getValue("PORT_NAME")!=null && attrs.getValue("PORT_NAME").length()>0){
        		PortAndHBA.set("port_name", attrs.getValue("PORT_NAME"));
        	}
        	if(attrs.getValue("UID")!=null && attrs.getValue("UID").length()>0){
        		PortAndHBA.set("uid", attrs.getValue("UID"));
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("MAP_HBA_STORAGEPORT".equals(qName)){
        	PortAndHBAs.add(PortAndHBA);
        	PortAndHBA=null;
        }
	}
}
