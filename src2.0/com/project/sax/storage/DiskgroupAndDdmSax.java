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

public class DiskgroupAndDdmSax extends DefaultHandler {
	private List<DataRow> DGAndDdms=null;
	private DataRow DGAndDdm=null;
	//创建工厂
	public List<DataRow> getDGAndDdms(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		DiskgroupAndDdmSax handler = new DiskgroupAndDdmSax();
		parser.parse(xmlStream, handler);
		return handler.getDGAndDdms();

	}
	
	public List<DataRow> getDGAndDdms() {
		return DGAndDdms;
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
		DGAndDdms=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("MAP_DISKGROUP2STORAGEDDM".equals(qName)){
        	DGAndDdm = new DataRow();
        	String DDMName=attrs.getValue("DDM_NAME");
        	if(DDMName!=null){
        		DGAndDdm.set("ddm_name", DDMName);}
        	String c=attrs.getValue("DISKGROUP_NAME");
        	if(attrs.getValue("DISKGROUP_NAME")!=null){
        		DGAndDdm.set("diskgroup_name", attrs.getValue("DISKGROUP_NAME"));}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("MAP_DISKGROUP2STORAGEDDM".equals(qName)){
        	DGAndDdms.add(DGAndDdm);
        	DGAndDdm=null;
        }
	}
}
