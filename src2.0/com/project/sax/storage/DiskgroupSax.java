package com.project.sax.storage;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.sr.constants.SrContant;

public class DiskgroupSax extends DefaultHandler {
	private List<DataRow> DiskGroups=null;
	private DataRow DiskGroup=null;
	//创建工厂
	public List<DataRow> getDiskGroups(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		DiskgroupSax handler = new DiskgroupSax();
		parser.parse(xmlStream, handler);
		return handler.getDiskGroups();

	}
	
	public List<DataRow> getDiskGroups() {
		return DiskGroups;
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
		DiskGroups=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("RES_DISKGROUP".equals(qName)){
        	DiskGroup = new DataRow();
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		DiskGroup.set("name",name1);}
        	String disname=attrs.getValue("DISPLAY_NAME");
        	if(disname!=null){
        		DiskGroup.set("display_name",disname);}
        	String description=attrs.getValue("DESCRIPTION");
        	if(description!=null){
        		DiskGroup.set("description",description);}
        	String raid_level= attrs.getValue("RAID_LEVEL");
        	if(raid_level!=null){
        		DiskGroup.set("raid_level",raid_level);}
        	String width= attrs.getValue("WIDTH");
        	if(width!=null){
        		DiskGroup.set("width",width);}
        	String ddm_cap= attrs.getValue("DDM_CAP");
        	if(ddm_cap!=null){
        		DiskGroup.set("ddm_cap",ddm_cap);}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		DiskGroup.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        } 
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_DISKGROUP".equals(qName)){
        	DiskGroups.add(DiskGroup);
        	DiskGroup=null;
        }
	}
}
