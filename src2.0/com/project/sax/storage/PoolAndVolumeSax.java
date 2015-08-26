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

public class PoolAndVolumeSax extends DefaultHandler {
	private List<DataRow> VolumeAndPools=null;
	private DataRow VolumeAndPool=null;
	//创建工厂
	public List<DataRow> getPoolAndVolumes(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PoolAndVolumeSax handler = new PoolAndVolumeSax();
		parser.parse(xmlStream, handler);
		return handler.getPoolAndVolumes();

	}
	
	public List<DataRow> getPoolAndVolumes() {
		return VolumeAndPools;
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
		VolumeAndPools=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("MAP_STORAGEPOOL2STORAGEVOLUME".equals(qName)){
        	VolumeAndPool = new DataRow();
        	String PoolName=attrs.getValue("POOL_NAME");
        	if(PoolName!=null){
        		VolumeAndPool.set("pool_name", PoolName);}
        	String volumeName=attrs.getValue("VOLUME_NAME");
        	if(volumeName!=null){
        		VolumeAndPool.set("volume_name", volumeName);}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("MAP_STORAGEPOOL2STORAGEVOLUME".equals(qName)){
        	VolumeAndPools.add(VolumeAndPool);
        	VolumeAndPool=null;
        }
	}
}
