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

public class HostGroupSax extends DefaultHandler {
	private List<DataRow> hostgroups=null;
	private DataRow hostgroup=null;
	//创建工厂
	public List<DataRow> getHostgroups(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		HostGroupSax handler = new HostGroupSax();
		parser.parse(xmlStream, handler);
		return handler.getHostgroups();

	}
	
	public List<DataRow> getHostgroups() {
		return hostgroups;
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
		hostgroups=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("RES_HOSTGROUP".equals(qName)){
        	hostgroup = new DataRow();
        	if(attrs.getValue("HOSTGROUP_NAME")!=null && attrs.getValue("HOSTGROUP_NAME").length()>0){
        		hostgroup.set("hostgroup_name", attrs.getValue("HOSTGROUP_NAME"));
        	}
        	if(attrs.getValue("SHAREABLE")!=null && attrs.getValue("SHAREABLE").length()>0){
        		hostgroup.set("shareable", attrs.getValue("SHAREABLE"));
        	}
        	if(attrs.getValue("UID")!=null && attrs.getValue("UID").length()>0){
        		hostgroup.set("UID", attrs.getValue("UID"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		hostgroup.set("update_timestamp",SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        } 
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_HOSTGROUP".equals(qName)){
        	hostgroups.add(hostgroup);
        	hostgroup=null;
        }
	}
}
