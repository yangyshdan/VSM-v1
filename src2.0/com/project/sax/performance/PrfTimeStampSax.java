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
import com.huiming.sr.constants.SrContant;

public class PrfTimeStampSax extends DefaultHandler {
	private List<DataRow> prftimestamps=null;
	private DataRow prftimestamp=null;
	//创建工厂
	public List<DataRow> getPrfTimestamps(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PrfTimeStampSax handler = new PrfTimeStampSax();
		parser.parse(xmlStream, handler);
		return handler.getPrfTimestamps();

	}
	
	public List<DataRow> getPrfTimestamps() {
		return prftimestamps;
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
		prftimestamps = new ArrayList<DataRow>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
        if("PRF_TIMESTAMP".equals(qName)){  
        	prftimestamp=new DataRow();
        	String InterlvaLen=attrs.getValue("INTERVAL_LEN");
        	if(InterlvaLen!=null){
        		prftimestamp.set("interval_len", Integer.parseInt(InterlvaLen));
        	}
        	String time1=attrs.getValue("SAMPLE_TIME");
        	if(time1!=null){
        		prftimestamp.set("sample_time", time1);}
        	String SubName=attrs.getValue("SUBSYSTEM_NAME");
        	if(SubName!=null){
        		prftimestamp.set("subsystem_name", SubName); }    
        	String summType=attrs.getValue("SUMM_TYPE");
        	if(summType!=null){
        		prftimestamp.set("summ_type", summType); }    
        }
        
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("PRF_TIMESTAMP".equals(qName)){
        	prftimestamps.add(prftimestamp);
        }
	}
}
