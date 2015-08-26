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

public class StorageNodeSax extends DefaultHandler {
	private List<DataRow> storagenodes=null;
	private DataRow storagenode=null;
	//创建工厂
	public List<DataRow> getStoragenodes(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		StorageNodeSax handler = new StorageNodeSax();
		parser.parse(xmlStream, handler);
		return handler.getStoragenodes();

	}
	
	public List<DataRow> getStoragenodes() {
		return storagenodes;
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
		storagenodes=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("RES_SP".equals(qName)){
        	storagenode = new DataRow();
        	if(attrs.getValue("SP_NAME")!=null && attrs.getValue("SP_NAME").length()>0){
        		storagenode.set("sp_name", attrs.getValue("SP_NAME"));
        	}
        	if(attrs.getValue("EMC_ARTWORK_REVISION")!=null && attrs.getValue("EMC_ARTWORK_REVISION").length()>0){
        		storagenode.set("emc_artwork_revision", attrs.getValue("EMC_ARTWORK_REVISION"));
        	}
        	if(attrs.getValue("EMC_ASSEMBLY_REVISION")!=null && attrs.getValue("EMC_ASSEMBLY_REVISION").length()>0){
        		storagenode.set("emc_assembly_revision", attrs.getValue("EMC_ASSEMBLY_REVISION"));
        	}
        	if(attrs.getValue("EMC_PART_NUMBER")!=null && attrs.getValue("EMC_PART_NUMBER").length()>0){
        		storagenode.set("emc_part_number", attrs.getValue("EMC_PART_NUMBER"));
        	}
        	if(attrs.getValue("EMC_SERIAL_NUMBER")!=null && attrs.getValue("EMC_SERIAL_NUMBER").length()>0){
        		storagenode.set("emc_serial_number", attrs.getValue("EMC_SERIAL_NUMBER"));
        	}
        	if(attrs.getValue("PROGRAMMABLE_NAME")!=null && attrs.getValue("PROGRAMMABLE_NAME").length()>0){
        		storagenode.set("programmable_name", attrs.getValue("PROGRAMMABLE_NAME"));
        	}
        	if(attrs.getValue("PROGRAMMABLE_REVISION")!=null && attrs.getValue("PROGRAMMABLE_REVISION").length()>0){
        		storagenode.set("programmable_revision", attrs.getValue("PROGRAMMABLE_REVISION"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		storagenode.set("update_timestamp",SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        } 
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_SP".equals(qName)){
        	storagenodes.add(storagenode);
        	storagenode=null;
        }
	}
}
