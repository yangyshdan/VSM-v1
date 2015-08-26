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

public class PoolSax extends DefaultHandler {
	private List<DataRow> pools=null;
	private DataRow pool=null;
	//创建工厂
	public List<DataRow> getPools(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PoolSax handler = new PoolSax();
		parser.parse(xmlStream, handler);
		return handler.getPools();

	}
	
	public List<DataRow> getPools() {
		return pools;
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
		pools=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("RES_POOL".equals(qName)){
        	pool = new DataRow();
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		pool.set("name",name1);}
        	String nubdisk=attrs.getValue("NUM_BACKEND_DISK");
        	if(nubdisk!=null){
        		pool.set("num_backend_disk", Integer.parseInt(nubdisk));}
        	String numlum=attrs.getValue("NUM_LUN");
        	if(numlum!=null){
        		pool.set("num_lun", Integer.parseInt(numlum));}
        	String readlevel=attrs.getValue("RAID_LEVEL");
        	if(readlevel!=null){
        		pool.set("raid_level",readlevel);}
        	String tot_capacity=attrs.getValue("TOTAL_USABLE_CAPACITY");
        	if(tot_capacity!=null){
        		pool.set("total_usable_capacity", Long.parseLong(tot_capacity)/1024/1024);}
        	String display_name=attrs.getValue("DISPLAY_NAME");
        	if(display_name!=null){
        		pool.set("display_name", display_name);}
        	String unallocat_cap=attrs.getValue("UNALLOCATED_CAPACITY");
        	if(unallocat_cap!=null){
        		pool.set("unallocated_capacity", Long.parseLong(unallocat_cap)/1024/1024);}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		pool.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_POOL".equals(qName)){
        	pools.add(pool);
        	pool=null;
        }

	}
}
