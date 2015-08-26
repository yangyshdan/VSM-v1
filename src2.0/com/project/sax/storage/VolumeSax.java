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


public class VolumeSax extends DefaultHandler {
	private List<DataRow> volumes=null;
	private DataRow volume;
	//创建工厂
	public List<DataRow> getVolumes(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		VolumeSax handler = new VolumeSax();
		parser.parse(xmlStream, handler);
		return handler.getVolumes();

	}

	public List<DataRow> getVolumes() {
		return volumes;
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
		volumes=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {

        if("RES_VOLUME".equals(qName)){
        	volume = new DataRow();
        	String logiCapacity=attrs.getValue("LOGICAL_CAPACITY");
        	if(logiCapacity!=null){
        		volume.set("logical_capacity", Long.parseLong(logiCapacity)/1024/1024);}
        	String phyCapacity=attrs.getValue("PHYSICAL_CAPACITY");
        	if(phyCapacity!=null){
        		volume.set("physical_capacity", Long.parseLong(phyCapacity)/1024/1024);}
        	String displayName=attrs.getValue("DISPLAY_NAME");
        	if(displayName!=null){
        		volume.set("display_name",displayName);}
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		volume.set("name",name1);}
        	String poolName=attrs.getValue("POOL_NAME");
        	if(poolName!=null){
        		volume.set("pool_name",poolName);}
        	String raidLevel=attrs.getValue("RAID_LEVEL");
        	if(raidLevel!=null){
        		volume.set("raid_level", raidLevel);}
        	String isMeta = attrs.getValue("IS_META");
        	if(isMeta!=null && isMeta.length()>0){
        		volume.set("is_meta", isMeta);
        	}
        	String isThin = attrs.getValue("IS_THIN");
        	if(isThin!=null && isThin.length()>0){
        		volume.set("is_thin", isThin);
        	}
        	if(attrs.getValue("CURRENT_OWNER")!=null && attrs.getValue("CURRENT_OWNER").length()>0){
        		volume.set("current_owner", attrs.getValue("CURRENT_OWNER"));
        	}
        	if(attrs.getValue("DEFUALT_OWNER")!=null && attrs.getValue("DEFUALT_OWNER").length()>0){
        		volume.set("default_owner", attrs.getValue("DEFUALT_OWNER"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		volume.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_VOLUME".equals(qName)){
        	volumes.add(volume);
        	volume=null;
        }
	}
}
