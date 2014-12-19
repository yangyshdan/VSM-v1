package com.project.sax.performance;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.huiming.base.jdbc.DataRow;

public class StoragePrfInfo extends DefaultHandler{
	private List<DataRow> diskgroup;
	private List<DataRow> volume;
	private List<DataRow> port;
	private List<DataRow> time;
	
	private DataRow argdiskgroup = null;
	private DataRow argvolume = null;
	private DataRow argport = null;
	private DataRow argtime = null;
	
	@Override
	public void startDocument() throws SAXException {
		diskgroup = new ArrayList<DataRow>();
		volume = new ArrayList<DataRow>();
		port = new ArrayList<DataRow>();
		time = new ArrayList<DataRow>();
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
        if("PRF_TIMESTAMP".equals(qName)){  
        	argtime=new DataRow();
        	String InterlvaLen=attrs.getValue("INTERVAL_LEN");
        	if(InterlvaLen!=null){
        		argtime.set("interlva_len", Integer.parseInt(InterlvaLen));
        	}
        	String time1=attrs.getValue("SAMPLE_TIME");
        	if(time1!=null){
        		argtime.set("sampl_time", time1);
        	}
        	String SubName=attrs.getValue("SUBSYSTEM_NAME");
        	if(SubName!=null){
        		argtime.set("subsystem_name", SubName); 
        	}    
        	String summType=attrs.getValue("SUMM_TYPE");
        	if(summType!=null){
        		argtime.set("summ_type", summType); 
        	}    
        }
        if("PRF_DISKGROUP".equals(qName)){  
        	argdiskgroup=new DataRow();
        	Long bckReadIO = null;
        	Long bckWriteIO = null;
        	Long bckReadKB = null;
        	Long bckWriteKB = null;
        	Long bckReadTime = null;
        	Long bckWriteTime = null;
        	if(attrs.getValue("SAMPLE_TIME_END")!=null && attrs.getValue("SAMPLE_TIME_END").length()>0){
        		String TimeEnd=attrs.getValue("SAMPLE_TIME_END");
        		argdiskgroup.set("sample_time_end", TimeEnd);
        	}
        	if(attrs.getValue("BCK_READ_IO")!=null && attrs.getValue("BCK_READ_IO").length()>0){
        		bckReadIO=Long.parseLong(attrs.getValue("BCK_READ_IO"));
        		argdiskgroup.set("bck_read_io", bckReadIO);
        	}
        	if(attrs.getValue("BCK_WRITE_IO")!=null &&attrs.getValue("BCK_WRITE_IO").length()>0){
        		bckWriteIO=Long.parseLong(attrs.getValue("BCK_WRITE_IO"));
        		argdiskgroup.set("bck_write_io", bckWriteIO);
        	}
        	if(attrs.getValue("BCK_READ_KB")!=null && attrs.getValue("BCK_READ_KB").length()>0){
        		bckReadKB=Long.parseLong(attrs.getValue("BCK_READ_KB"));
        		argdiskgroup.set("bck_read_kb",bckReadKB);
        	}
        	if(attrs.getValue("BCK_WRITE_KB")!=null &&attrs.getValue("BCK_WRITE_KB").length()>0){
        		bckWriteKB=Long.parseLong(attrs.getValue("BCK_WRITE_KB"));
        		argdiskgroup.set("bck_write_kb",bckWriteKB);
        	}
        	if(attrs.getValue("BCK_READ_TIME")!=null && attrs.getValue("BCK_READ_TIME").length()>0){
        		bckReadTime = Long.parseLong(attrs.getValue("BCK_READ_TIME"));
        		argdiskgroup.set("bck_read_time",bckReadTime);
        	}
        	if(attrs.getValue("BCK_WRITE_TIME")!=null && attrs.getValue("BCK_WRITE_TIME").length()>0){
        		bckWriteTime = Long.parseLong(attrs.getValue("BCK_WRITE_TIME"));
        		argdiskgroup.set("bck_write_time",bckWriteTime);
        	}
        	if(attrs.getValue("DISKGROUP_NAME")!=null && attrs.getValue("DISKGROUP_NAME").length()>0){
        		String DiskgroupName=attrs.getValue("DISKGROUP_NAME");
        		argdiskgroup.set("diskgroup_name",DiskgroupName);
        	}
        	if(attrs.getValue("DISK_UTIL_PERCENTAGE")!=null && attrs.getValue("DISK_UTIL_PERCENTAGE").length()>0){
        		String DiskUtil=attrs.getValue("DISK_UTIL_PERCENTAGE");
        		argdiskgroup.set("disk_util_percentage",Integer.parseInt(DiskUtil));
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
        	argdiskgroup.set("the_bck_io", the_bck_io);
        	argdiskgroup.set("the_bck_kb", the_bck_kb);
        	argdiskgroup.set("the_time", the_time);
	
        }
        if("PRF_PORT".equals(qName)){  
        	Long recvIO = null;
        	Long sendIO = null;
        	Long recvKB = null;
        	Long sendKB = null;
        	Long recvTime = null;
        	Long sendTime = null;
        	argport=new DataRow();
        	if(attrs.getValue("PORT_NAME")!=null && attrs.getValue("PORT_NAME").length()>0){
        		String PortName=attrs.getValue("PORT_NAME");
        		argport.set("port_name",PortName);
        	}
        	if(attrs.getValue("RECV_IO")!=null && attrs.getValue("RECV_IO").length()>0){
        		recvIO=Long.parseLong(attrs.getValue("RECV_IO"));
        		argport.set("recv_io", recvIO);
        	}
        	if(attrs.getValue("RECV_KB")!=null && attrs.getValue("RECV_KB").length()>0){
        		recvKB=Long.parseLong(attrs.getValue("RECV_KB"));
        		argport.set("recv_kb",recvKB);
        	}
        	if(attrs.getValue("SEND_IO")!=null && attrs.getValue("SEND_IO").length()>0){
        		sendIO=Long.parseLong(attrs.getValue("SEND_IO"));
        		argport.set("send_io",sendIO);
        	}
        	if(attrs.getValue("SEND_KB")!=null && attrs.getValue("SEND_KB").length()>0){
        		sendKB=Long.parseLong(attrs.getValue("SEND_KB"));
        		argport.set("send_kb",sendKB);
        	}
        	if(attrs.getValue("SEND_TIME")!=null && attrs.getValue("SEND_TIME").length()>0){
        		sendTime = Long.parseLong(attrs.getValue("SEND_TIME"));
        		argport.set("send_time",sendTime);
        	}
        	if(attrs.getValue("RECV_TIME")!=null && attrs.getValue("RECV_TIME").length()>0){
        		recvTime = Long.parseLong(attrs.getValue("RECV_TIME"));
        		argport.set("recv_time",recvTime);
        	}
        	if(attrs.getValue("BNDW_SEND_UTIL")!=null && attrs.getValue("BNDW_SEND_UTIL").length()>0){
        		String SendUtil=attrs.getValue("BNDW_SEND_UTIL");
        		argport.set("bndw_send_util",SendUtil);
        	}
        	if(attrs.getValue("BNDW_RECV_UTIL")!=null && attrs.getValue("BNDW_RECV_UTIL").length()>0){
        		String RecvUtil=attrs.getValue("BNDW_RECV_UTIL");
        		argport.set("bndw_recv_util",RecvUtil);
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
			argport.set("the_io", the_io);
			argport.set("the_kb", the_kb);
			argport.set("the_time", the_time);
        }
        if("PRF_VOLUME".equals(qName)){  
        	Long read_io_time = null;
        	Long write_io_time = null;
        	argvolume=new DataRow();
        	if(attrs.getValue("READ_HIT_IO")!=null && attrs.getValue("READ_HIT_IO").length()>0){
        		String READ_HIT_IO=attrs.getValue("READ_HIT_IO");
        		argvolume.set("read_hit_io", Long.parseLong(READ_HIT_IO));}
        	if(attrs.getValue("WRITE_HIT_IO")!=null && attrs.getValue("WRITE_HIT_IO").length()>0){
        		argvolume.set("write_hit_io",Long.parseLong(attrs.getValue("WRITE_HIT_IO")));
        	}
        	if(attrs.getValue("READ_IO")!=null && attrs.getValue("READ_IO").length()>0){
        		argvolume.set("read_io", Long.parseLong(attrs.getValue("READ_IO")));} 
        	if(attrs.getValue("WRITE_IO")!=null && attrs.getValue("WRITE_IO").length()>0){
        		argvolume.set("write_io",Long.parseLong(attrs.getValue("WRITE_IO")));}
        	if(attrs.getValue("READ_IO_TIME")!=null && attrs.getValue("READ_IO_TIME").length()>0){
        		read_io_time = Long.parseLong(attrs.getValue("READ_IO_TIME"));
        		argvolume.set("read_io_time",read_io_time);
        	}
        	if(attrs.getValue("WRITE_IO_TIME")!=null && attrs.getValue("WRITE_IO_TIME").length()>0){
        		write_io_time = Long.parseLong(attrs.getValue("WRITE_IO_TIME"));
        		argvolume.set("write_io_time",write_io_time);
        	}
        	if(attrs.getValue("READ_KB")!=null && attrs.getValue("READ_KB").length()>0){
        		argvolume.set("read_kb",Long.parseLong(attrs.getValue("READ_KB")));}
        	if(attrs.getValue("WRITE_KB")!=null && attrs.getValue("WRITE_KB").length()>0){
        		argvolume.set("write_kb",Long.parseLong(attrs.getValue("WRITE_KB")));}
        	if(attrs.getValue("VOLUME_NAME")!=null && attrs.getValue("VOLUME_NAME").length()>0){
        		argvolume.set("volume_name",attrs.getValue("VOLUME_NAME"));}
        	if(attrs.getValue("VOL_UTIL_PERCENTAGE")!=null){
        		argvolume.set("vol_util_percentage",Short.parseShort(attrs.getValue("VOL_UTIL_PERCENTAGE")));
        	}      	
        	if(attrs.getValue("READ_IO")!=null&&attrs.getValue("WRITE_IO")!=null){
	        	Long the_io=Long.parseLong(attrs.getValue("READ_IO"))+Long.parseLong(attrs.getValue("WRITE_IO"));
	        	argvolume.set("the_io", the_io);
        	}
        	if(attrs.getValue("READ_HIT_IO")!=null&&attrs.getValue("WRITE_HIT_IO")!=null){
	        	Long the_hit_io=Long.parseLong(attrs.getValue("READ_HIT_IO"))+Long.parseLong(attrs.getValue("WRITE_HIT_IO"));
	        	argvolume.set("the_hit_io", the_hit_io);
        	}
        	if(attrs.getValue("READ_KB")!=null && attrs.getValue("WRITE_KB")!=null){
	        	Long the_kb=Long.parseLong(attrs.getValue("READ_KB"))+Long.parseLong(attrs.getValue("WRITE_KB"));
	        	argvolume.set("the_kb", the_kb);
        	}
        	if(read_io_time!=null&&write_io_time!=null){
        		Long the_io_time=read_io_time+write_io_time;
        		argvolume.set("the_io_time", the_io_time);
        	}
        }
	}
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if("PRF_TIMESTAMP".equals(qName)){
        	time.add(argtime);
        }
        if("PRF_DISKGROUP".equals(qName)){
        	diskgroup.add(argdiskgroup);
        }
        if("PRF_PORT".equals(qName)){
        	port.add(argport);
        }
        if("PRF_VOLUME".equals(qName)){
        	volume.add(argvolume);
        }
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}
	public List<DataRow> getDiskgroup() {
		return diskgroup;
	}
	public void setDiskgroup(List<DataRow> diskgroup) {
		this.diskgroup = diskgroup;
	}
	public List<DataRow> getVolume() {
		return volume;
	}
	public void setVolume(List<DataRow> volume) {
		this.volume = volume;
	}
	public List<DataRow> getPort() {
		return port;
	}
	public void setPort(List<DataRow> port) {
		this.port = port;
	}
	public List<DataRow> getTime() {
		return time;
	}
	public void setTime(List<DataRow> time) {
		this.time = time;
	}
}
