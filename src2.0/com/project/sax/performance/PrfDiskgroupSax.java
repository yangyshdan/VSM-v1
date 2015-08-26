package com.project.sax.performance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.huiming.base.jdbc.DataRow;

public class PrfDiskgroupSax extends DefaultHandler {
	private List<DataRow> prfdisgroups=null;
	private DataRow prfdisgroup=null;
	//创建工厂
	public List<DataRow> getPrfDiskgroups(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PrfDiskgroupSax handler = new PrfDiskgroupSax();
		parser.parse(xmlStream, handler);
		return handler.getDiskgroups();

	}
	
	public List<DataRow> getDiskgroups() {
		return prfdisgroups;
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
		prfdisgroups = new ArrayList<DataRow>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
        if("PRF_DISKGROUP".equals(qName)){  
        	prfdisgroup=new DataRow();
        	Long bckReadIO = null;
        	Long bckWriteIO = null;
        	Long bckReadKB = null;
        	Long bckWriteKB = null;
        	Long bckReadTime = null;
        	Long bckWriteTime = null;
        	if(attrs.getValue("SAMPLE_TIME_END")!=null && attrs.getValue("SAMPLE_TIME_END").length()>0){
        		String TimeEnd=attrs.getValue("SAMPLE_TIME_END");
        		prfdisgroup.set("sample_time_end", TimeEnd);
        	}
        	if(attrs.getValue("BCK_READ_IO")!=null && attrs.getValue("BCK_READ_IO").length()>0){
        		bckReadIO=Long.parseLong(attrs.getValue("BCK_READ_IO"));
        		prfdisgroup.set("bck_read_io", bckReadIO);
        	}
        	if(attrs.getValue("BCK_WRITE_IO")!=null &&attrs.getValue("BCK_WRITE_IO").length()>0){
        		bckWriteIO=Long.parseLong(attrs.getValue("BCK_WRITE_IO"));
        		prfdisgroup.set("bck_write_io", bckWriteIO);
        	}
        	if(attrs.getValue("BCK_READ_KB")!=null && attrs.getValue("BCK_READ_KB").length()>0){
        		bckReadKB=Long.parseLong(attrs.getValue("BCK_READ_KB"));
        		prfdisgroup.set("bck_read_kb",bckReadKB);
        	}
        	if(attrs.getValue("BCK_WRITE_KB")!=null &&attrs.getValue("BCK_WRITE_KB").length()>0){
        		bckWriteKB=Long.parseLong(attrs.getValue("BCK_WRITE_KB"));
        		prfdisgroup.set("bck_write_kb",bckWriteKB);
        	}
        	if(attrs.getValue("BCK_READ_TIME")!=null && attrs.getValue("BCK_READ_TIME").length()>0){
        		bckReadTime = Long.parseLong(attrs.getValue("BCK_READ_TIME"));
        		prfdisgroup.set("bck_read_time",bckReadTime);
        	}
        	if(attrs.getValue("BCK_WRITE_TIME")!=null && attrs.getValue("BCK_WRITE_TIME").length()>0){
        		bckWriteTime = Long.parseLong(attrs.getValue("BCK_WRITE_TIME"));
        		prfdisgroup.set("bck_write_time",bckWriteTime);
        	}
        	if(attrs.getValue("DISKGROUP_NAME")!=null && attrs.getValue("DISKGROUP_NAME").length()>0){
        		String DiskgroupName=attrs.getValue("DISKGROUP_NAME");
        		prfdisgroup.set("diskgroup_name",DiskgroupName);
        	}
        	if(attrs.getValue("DISK_UTIL_PERCENTAGE")!=null && attrs.getValue("DISK_UTIL_PERCENTAGE").length()>0){
        		String DiskUtil=attrs.getValue("DISK_UTIL_PERCENTAGE");
        		prfdisgroup.set("disk_util_percentage",Integer.parseInt(DiskUtil));
        	}
        	Long the_bck_io=null;
        	if(bckReadIO!=null && bckWriteIO!=null){
        		the_bck_io=bckReadIO+bckWriteIO;
        	}
        	Long the_bck_kb=null;
        	if(bckReadKB!=null && bckWriteKB!=null){
        		the_bck_kb = bckReadKB+bckWriteKB;
        	}
        	Long the_time=null;
        	if(bckReadTime!=null && bckWriteTime!=null){
        		the_time = bckReadTime+bckWriteTime;
        	}
        	prfdisgroup.set("the_bck_io", the_bck_io);
        	prfdisgroup.set("the_bck_kb", the_bck_kb);
        	prfdisgroup.set("the_time", the_time);
	
        }
//        preTag = qName;//将正在解析的节点名称赋给preTag  
	}
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("PRF_DISKGROUP".equals(qName)){
        	prfdisgroups.add(prfdisgroup);
        }
	}
}
