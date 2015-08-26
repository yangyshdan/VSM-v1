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

public class PortSax extends DefaultHandler {
	
	private List<DataRow> ports=null;
	private DataRow port=null;
	//创建工厂
	public List<DataRow> getPorts(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PortSax handler = new PortSax();
		parser.parse(xmlStream, handler);
		return handler.getPorts();

	}
	
	public List<DataRow> getPorts() {
		return ports;
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
		ports=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("RES_PORT".equals(qName)){
        	port = new DataRow();
        	String linkStatus= attrs.getValue("LINK_STATUS");
        	if(linkStatus!=null){
        		port.set("link_status",linkStatus);}
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		port.set("name", name1);}
        	String netAddress=attrs.getValue("NETWORK_ADDRESS");
        	if(netAddress!=null){
        		port.set("network_address", netAddress);}
        	String portSpeed=attrs.getValue("PORT_SPEED");
        	if(portSpeed!=null){
        		port.set("port_speed", portSpeed);}
        	String subSystemName=attrs.getValue("SUBSYSTEM_NAME");
        	if(subSystemName!=null){
        		port.set("subsystem_name", subSystemName);}
        	String Type1=attrs.getValue("TYPE");
        	if(Type1!=null && Type1.length()>0){
        		port.set("type", Type1);
        	}else{
        		port.set("type", "FC");
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		port.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_PORT".equals(qName)){
        	ports.add(port);
        	port=null;
        }
	}
}
