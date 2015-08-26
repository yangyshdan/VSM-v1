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

public class DdmSax extends DefaultHandler {
	private List<DataRow> ddms=null;
	private DataRow ddm;
	//创建工厂
	public List<DataRow> getDdms(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		DdmSax handler = new DdmSax();
		parser.parse(xmlStream, handler);
		return handler.getDdms();

	}

	public List<DataRow> getDdms() {
		return ddms;
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
		ddms=new ArrayList<DataRow>();
	}

	/**
	 * 开始读取节点元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attrs) throws SAXException {

        if("RES_STORAGE_DDM".equals(qName)){
        	ddm = new DataRow();
        	ddm.set("ddm_cap", Integer.parseInt(attrs.getValue("DDM_CAP")));
        	ddm.set("ddm_speed", attrs.getValue("DDM_SPEED"));
        	ddm.set("display_name", attrs.getValue("DISPLAY_NAME"));
        	ddm.set("name", attrs.getValue("NAME"));
        	ddm.set("subsystem_name", attrs.getValue("SUBSYSTEM_NAME"));
        	ddm.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	ddm.set("ddm_type", attrs.getValue("DDM_TYPE"));
        	ddm.set("ddm_rpm", attrs.getValue("DDM_RPM"));
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("RES_STORAGE_DDM".equals(qName)){
        	ddms.add(ddm);
        	ddm=null;
        }
	}
}
