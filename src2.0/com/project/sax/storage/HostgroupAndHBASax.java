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

public class HostgroupAndHBASax extends DefaultHandler {
	private List<DataRow> hostgroupAndHBAs=null;
	private DataRow hostgroupAndHBA=null;
	//创建工厂
	public List<DataRow> getHostgroupAndHBAs(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		HostgroupAndHBASax handler = new HostgroupAndHBASax();
		parser.parse(xmlStream, handler);
		return handler.getHostgroupAndHBAs();

	}
	
	public List<DataRow> getHostgroupAndHBAs() {
		return hostgroupAndHBAs;
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
		hostgroupAndHBAs=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("MAP_HOSTGROUPHBA".equals(qName)){
        	hostgroupAndHBA = new DataRow();
        	if(attrs.getValue("HOSTGROUP_NAME")!=null && attrs.getValue("HOSTGROUP_NAME").length()>0){
        		hostgroupAndHBA.set("hostgroup_name", attrs.getValue("HOSTGROUP_NAME"));
        	}
        	if(attrs.getValue("HBA_UID")!=null && attrs.getValue("HBA_UID").length()>0){
        		hostgroupAndHBA.set("hba_uid", attrs.getValue("HBA_UID"));
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("MAP_HOSTGROUPHBA".equals(qName)){
        	hostgroupAndHBAs.add(hostgroupAndHBA);
        	hostgroupAndHBA=null;
        }
	}
}
