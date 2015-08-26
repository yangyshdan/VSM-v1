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

public class PoolAndDiskgroupSax extends DefaultHandler {
	private List<DataRow> DGAndPools=null;
	private DataRow DGAndPool=null;
	//创建工厂
	public List<DataRow> getDGAndPools(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PoolAndDiskgroupSax handler = new PoolAndDiskgroupSax();
		parser.parse(xmlStream, handler);
		return handler.getDGAndPools();

	}
	
	public List<DataRow> getDGAndPools() {
		return DGAndPools;
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
		DGAndPools=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("MAP_STORAGEPOOL2DISKGROUP".equals(qName)){
        	DGAndPool = new DataRow();
        	String poolName=attrs.getValue("STORAGEPOOL_NAME");
        	if(poolName!=null){
        		DGAndPool.set("pool_name",poolName);}
        	String DiskgroupName=attrs.getValue("DISKGROUP_NAME");
        	if(DiskgroupName!=null){
        	DGAndPool.set("diskgroup_name", DiskgroupName);}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("MAP_STORAGEPOOL2DISKGROUP".equals(qName)){
        	DGAndPools.add(DGAndPool);
        	DGAndPool=null;
        }
	}
}
