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

public class SubSystemSax extends DefaultHandler {
	private List<DataRow> subSystems=null;
	private DataRow subSystem=null;
	//创建工厂
	public List<DataRow> getSubSystems(InputStream xmlStream) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		SubSystemSax handler = new SubSystemSax();
		parser.parse(xmlStream, handler);
		return handler.getSubSystems();

	}
	public List<DataRow> getSubSystems() {
		return subSystems;
	}
	/**
	 * 读取内容
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}
	/**
	 * 开始文档
	 */
	@Override
	public void startDocument() throws SAXException {
		subSystems = new ArrayList<DataRow>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
        if("RES_STORAGESUBSYSTEM".equals(qName)){  
        	subSystem=new DataRow();
        	String allocated_capacity=attrs.getValue("ALLOCATED_CAPACITY");
        	if(allocated_capacity!=null){
        		subSystem.set("cache_gb", Long.parseLong(allocated_capacity)/1024/1024);}
        	String available_capacity =attrs.getValue("AVAILABLE_CAPACITY");
        	if(available_capacity !=null){
        		subSystem.set("available_capacity", Long.parseLong(available_capacity)/1024/1024);}
        	String backend_storage_capacity =attrs.getValue("BACKEND_STORAGE_CAPACITY");
        	if(backend_storage_capacity !=null){
        		subSystem.set("backend_storage_capacity", Long.parseLong(backend_storage_capacity)/1024/1024);}
        	String display_name =attrs.getValue("DISPLAY_NAME");
        	if(display_name!=null){
        		subSystem.set("display_name", display_name);}
        	String cache_gb=attrs.getValue("CACHE_GB");
        	if(cache_gb!=null){
        		subSystem.set("cache_gb", Integer.parseInt(cache_gb));}
        	String nvs_gb=attrs.getValue("NVS_GB");
        	if(nvs_gb!=null){
        		subSystem.set("nvs_gb", Integer.parseInt(nvs_gb));}
        	String num_disk=attrs.getValue("NUM_DISK");
        	if(num_disk!=null){
        		subSystem.set("num_disk", Integer.parseInt(num_disk));}
        	String num_lun=attrs.getValue("NUM_LUN");
        	if(num_lun!=null){
        		subSystem.set("num_lun", Integer.parseInt(num_lun));}
        	String ip_address=attrs.getValue("IP_ADDRESS");
        	if(ip_address!=null){
        		subSystem.set("ip_address",ip_address);}
        	String model=attrs.getValue("MODEL");
        	if(model!=null){
        		subSystem.set("model", model);}
        	String code_level=attrs.getValue("CODE_LEVEL");
        	if(model!=null){
        		subSystem.set("code_level", code_level);}
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		subSystem.set("name", name1);}
        	String ser_number=attrs.getValue("SERIAL_NUMBER");
        	if(ser_number!=null){
        		subSystem.set("serial_number", ser_number);}
        	String vend_name=attrs.getValue("VENDOR_NAME");
        	if(vend_name!=null){
        		subSystem.set("vendor_name", vend_name);}
        	String phy_capacity=attrs.getValue("PHYSICAL_DISK_CAPACITY");
        	if(phy_capacity!=null){
        		subSystem.set("physical_disk_capacity", Long.parseLong(phy_capacity)/1024/1024);}
        	String un_capacity=attrs.getValue("UNFORMATTED_PHYSICAL_DISK_CAPACITY");
        	if(un_capacity!=null){
        		subSystem.set("unformatted_physical_disk_capacity", Long.parseLong(un_capacity)/1024/1024);}
        	String tollun_capacity =attrs.getValue("TOTAL_BACKEND_LUN_CAPACITY");
        	if(tollun_capacity!=null){
        		subSystem.set("total_backend_lun_capacity", Integer.parseInt(tollun_capacity)/1024/1024);}
        	String unused_capacity =attrs.getValue("UNUSED_BACKEND_LUN_CAPACITY");
        	if(unused_capacity!=null){
        		subSystem.set("unused_backend_lun_capacity", Long.parseLong(unused_capacity)/1024/1024);}
        	String usable_capacity =attrs.getValue("TOTAL_USABLE_CAPACITY");
        	if(usable_capacity!=null){
        		subSystem.set("total_usable_capacity", Long.parseLong(usable_capacity)/1024/1024);}
        	String unallocat_capacity =attrs.getValue("UNALLOCATED_USABLE_CAPACITY");
        	if(unallocat_capacity!=null){
        		subSystem.set("unallocated_usable_capacity", Long.parseLong(unallocat_capacity)/1024/1024);}
        	String tolun_capacity =attrs.getValue("TOTAL_LUN_CAPACITY");
        	if(tolun_capacity!=null){
        		subSystem.set("total_lun_capacity", Long.parseLong(tolun_capacity)/1024/1024);}
        	String unmapp_cap =attrs.getValue("UNMAPPED_LUN_CAPACITY");
        	if(unmapp_cap!=null){
        		subSystem.set("unmapped_lun_capacity", Long.parseLong(unmapp_cap)/1024/1024);}
        	String operate_status =attrs.getValue("OPERATIONAL_STATUS");
        	if(operate_status!=null){
        		subSystem.set("operational_status", operate_status);}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		subSystem.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
	}
	
	/**
	 * 当元素结束
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
        if("RES_STORAGESUBSYSTEM".equals(qName)){
        	subSystems.add(subSystem);
        	subSystem = null;
        }
	}
}
