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

public class PrfVolumeSax extends DefaultHandler {
	private List<DataRow> prfvolumes=null;
	private DataRow prfvolume=null;
	//创建工厂
	public List<DataRow> getPrfVolumes(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PrfVolumeSax handler = new PrfVolumeSax();
		parser.parse(xmlStream, handler);
		return handler.getVolumes();

	}
	
	public List<DataRow> getVolumes() {
		return prfvolumes;
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
		prfvolumes = new ArrayList<DataRow>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
        if("PRF_VOLUME".equals(qName)){  
        	Long read_io_time = null;
        	Long write_io_time = null;
        	prfvolume=new DataRow();
        	if(attrs.getValue("READ_HIT_IO")!=null && attrs.getValue("READ_HIT_IO").length()>0){
        		String READ_HIT_IO=attrs.getValue("READ_HIT_IO");
        		prfvolume.set("read_hit_io", Long.parseLong(READ_HIT_IO));}
        	if(attrs.getValue("WRITE_HIT_IO")!=null && attrs.getValue("WRITE_HIT_IO").length()>0){
        		prfvolume.set("write_hit_io",Long.parseLong(attrs.getValue("WRITE_HIT_IO")));
        	}
        	if(attrs.getValue("READ_IO")!=null && attrs.getValue("READ_IO").length()>0){
        		prfvolume.set("read_io", Long.parseLong(attrs.getValue("READ_IO")));} 
        	if(attrs.getValue("WRITE_IO")!=null && attrs.getValue("WRITE_IO").length()>0){
        		prfvolume.set("write_io",Long.parseLong(attrs.getValue("WRITE_IO")));}
        	if(attrs.getValue("READ_IO_TIME")!=null && attrs.getValue("READ_IO_TIME").length()>0){
        		read_io_time = Long.parseLong(attrs.getValue("READ_IO_TIME"));
        		prfvolume.set("read_io_time",read_io_time);
        	}
        	if(attrs.getValue("WRITE_IO_TIME")!=null && attrs.getValue("WRITE_IO_TIME").length()>0){
        		write_io_time = Long.parseLong(attrs.getValue("WRITE_IO_TIME"));
        		prfvolume.set("write_io_time",write_io_time);
        	}
        	if(attrs.getValue("READ_KB")!=null && attrs.getValue("READ_KB").length()>0){
        		prfvolume.set("read_kb",Long.parseLong(attrs.getValue("READ_KB")));}
        	if(attrs.getValue("WRITE_KB")!=null && attrs.getValue("WRITE_KB").length()>0){
        		prfvolume.set("write_kb",Long.parseLong(attrs.getValue("WRITE_KB")));}
        	if(attrs.getValue("VOLUME_NAME")!=null && attrs.getValue("VOLUME_NAME").length()>0){
        		prfvolume.set("volume_name",attrs.getValue("VOLUME_NAME"));}
        	if(attrs.getValue("VOL_UTIL_PERCENTAGE")!=null){
        		prfvolume.set("vol_util_percentage",Short.parseShort(attrs.getValue("VOL_UTIL_PERCENTAGE")));
        	}      	
        	if(attrs.getValue("READ_IO")!=null&&attrs.getValue("WRITE_IO")!=null){
	        	Long the_io=Long.parseLong(attrs.getValue("READ_IO"))+Long.parseLong(attrs.getValue("WRITE_IO"));
	        	prfvolume.set("the_io", the_io);
        	}
        	if(attrs.getValue("READ_HIT_IO")!=null&&attrs.getValue("WRITE_HIT_IO")!=null){
	        	Long the_hit_io=Long.parseLong(attrs.getValue("READ_HIT_IO"))+Long.parseLong(attrs.getValue("WRITE_HIT_IO"));
	        	prfvolume.set("the_hit_io", the_hit_io);
        	}
        	if(attrs.getValue("READ_KB")!=null && attrs.getValue("WRITE_KB")!=null){
	        	Long the_kb=Long.parseLong(attrs.getValue("READ_KB"))+Long.parseLong(attrs.getValue("WRITE_KB"));
	        	prfvolume.set("the_kb", the_kb);
        	}
        	if(read_io_time!=null&&write_io_time!=null){
        		Long the_io_time=read_io_time+write_io_time;
        		prfvolume.set("the_io_time", the_io_time);
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("PRF_VOLUME".equals(qName)){
        	prfvolumes.add(prfvolume);
        }
	}
}
