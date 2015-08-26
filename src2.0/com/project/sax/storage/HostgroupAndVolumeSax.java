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

public class HostgroupAndVolumeSax extends DefaultHandler {
	private List<DataRow> hostgroupAndVolumes=null;
	private DataRow hostgroupAndVolume=null;
	//创建工厂
	public List<DataRow> getHostgroupAndVolumes(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		HostgroupAndVolumeSax handler = new HostgroupAndVolumeSax();
		parser.parse(xmlStream, handler);
		return handler.getHostgroupAndVolumes();

	}
	
	public List<DataRow> getHostgroupAndVolumes() {
		return hostgroupAndVolumes;
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
		hostgroupAndVolumes=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {
        if("MAP_HOSTGROUPVOLUME".equals(qName)){
        	hostgroupAndVolume = new DataRow();
        	if(attrs.getValue("HOSTGROUP_NAME")!=null && attrs.getValue("HOSTGROUP_NAME").length()>0){
        		hostgroupAndVolume.set("hostgroup_name", attrs.getValue("HOSTGROUP_NAME"));
        	}
        	if(attrs.getValue("VOLUME_NAME")!=null && attrs.getValue("VOLUME_NAME").length()>0){
        		hostgroupAndVolume.set("volume_name", attrs.getValue("VOLUME_NAME"));
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("MAP_HOSTGROUPVOLUME".equals(qName)){
        	hostgroupAndVolumes.add(hostgroupAndVolume);
        	hostgroupAndVolume=null;
        }
	}
}
