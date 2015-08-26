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

public class PrfPortSax extends DefaultHandler {
	private List<DataRow> prfports=null;
	private DataRow prfport=null;
	//创建工厂
	public List<DataRow> getPrfPorts(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PrfPortSax handler = new PrfPortSax();
		parser.parse(xmlStream, handler);
		return handler.getPorts();

	}
	
	public List<DataRow> getPorts() {
		return prfports;
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
		prfports = new ArrayList<DataRow>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
        if("PRF_PORT".equals(qName)){  
        	Long recvIO = null;
        	Long sendIO = null;
        	Long recvKB = null;
        	Long sendKB = null;
        	Long recvTime = null;
        	Long sendTime = null;
        	prfport=new DataRow();
        	if(attrs.getValue("PORT_NAME")!=null && attrs.getValue("PORT_NAME").length()>0){
        		String PortName=attrs.getValue("PORT_NAME");
        		prfport.set("port_name",PortName);
        	}
        	if(attrs.getValue("RECV_IO")!=null && attrs.getValue("RECV_IO").length()>0){
        		recvIO=Long.parseLong(attrs.getValue("RECV_IO"));
        		prfport.set("recv_io", recvIO);
        	}
        	if(attrs.getValue("RECV_KB")!=null && attrs.getValue("RECV_KB").length()>0){
        		recvKB=Long.parseLong(attrs.getValue("RECV_KB"));
        		prfport.set("recv_kb",recvKB);
        	}
        	if(attrs.getValue("SEND_IO")!=null && attrs.getValue("SEND_IO").length()>0){
        		sendIO=Long.parseLong(attrs.getValue("SEND_IO"));
        		prfport.set("send_io",sendIO);
        	}
        	if(attrs.getValue("SEND_KB")!=null && attrs.getValue("SEND_KB").length()>0){
        		sendKB=Long.parseLong(attrs.getValue("SEND_KB"));
        		prfport.set("send_kb",sendKB);
        	}
        	if(attrs.getValue("SEND_TIME")!=null && attrs.getValue("SEND_TIME").length()>0){
        		sendTime = Long.parseLong(attrs.getValue("SEND_TIME"));
        		prfport.set("send_time",sendTime);
        	}
        	if(attrs.getValue("RECV_TIME")!=null && attrs.getValue("RECV_TIME").length()>0){
        		recvTime = Long.parseLong(attrs.getValue("RECV_TIME"));
        		prfport.set("recv_time",recvTime);
        	}
        	if(attrs.getValue("BNDW_SEND_UTIL")!=null && attrs.getValue("BNDW_SEND_UTIL").length()>0){
        		String SendUtil=attrs.getValue("BNDW_SEND_UTIL");
        		prfport.set("bndw_send_util",SendUtil);
        	}
        	if(attrs.getValue("BNDW_RECV_UTIL")!=null && attrs.getValue("BNDW_RECV_UTIL").length()>0){
        		String RecvUtil=attrs.getValue("BNDW_RECV_UTIL");
        		prfport.set("bndw_recv_util",RecvUtil);
        	}
        	Long the_io=null;
        	if(recvIO!=null && sendIO!=null){
        		the_io = recvIO+sendIO;
        	}
        	Long the_kb=null;
        	if(recvKB!=null && sendKB!=null){
        		the_kb = recvKB+sendKB;
        	}
        	Long the_time=null;
        	if(sendTime!=null && recvTime!=null){
        		the_time=sendTime+recvTime;
        	}
			prfport.set("the_io", the_io);
			prfport.set("the_kb", the_kb);
			prfport.set("the_time", the_time);
        	
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("PRF_PORT".equals(qName)){
        	prfports.add(prfport);
        }
	}
}
