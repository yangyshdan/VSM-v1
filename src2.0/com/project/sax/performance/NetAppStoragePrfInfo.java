package com.project.sax.performance;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.huiming.base.jdbc.DataRow;

public class NetAppStoragePrfInfo extends DefaultHandler {
	private List<DataRow> system;
	private List<DataRow> volume;
	private List<DataRow> port;
	private List<DataRow> time;

	private DataRow argsystem = null;
	private DataRow argvolume = null;
	private DataRow argport = null;
	private DataRow argtime = null;

	@Override
	public void startDocument() throws SAXException {
		system = new ArrayList<DataRow>();
		volume = new ArrayList<DataRow>();
		port = new ArrayList<DataRow>();
		time = new ArrayList<DataRow>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
		if ("PRF_TIMESTAMP".equals(qName)) {
			argtime = new DataRow();
			String InterlvaLen = attrs.getValue("INTERVAL_LEN");
			if (InterlvaLen != null) {
				argtime.set("interval_len", Integer.parseInt(InterlvaLen));
			}
			String time1 = attrs.getValue("SAMPLE_TIME");
			if (time1 != null) {
				argtime.set("sample_time", time1);
			}
			String SubName = attrs.getValue("SUBSYSTEM_NAME");
			if (SubName != null) {
				argtime.set("subsystem_name", SubName);
			}
			String summType = attrs.getValue("SUMM_TYPE");
			if (summType != null) {
				argtime.set("summ_type", summType);
			}
		}
		
		//For Storage system
		if (qName.equals("PRF_SYSTEM")) {
			argsystem = new DataRow();
			if (attrs.getValue("NFS_OPS") != null && attrs.getValue("NFS_OPS").length() > 0) {
				String nfs_ops = attrs.getValue("NFS_OPS");
				argsystem.set("nfs_ops", Long.parseLong(nfs_ops));
			}
			if (attrs.getValue("CIFS_OPS") != null && attrs.getValue("CIFS_OPS").length() > 0) {
				String cifs_ops = attrs.getValue("CIFS_OPS");
				argsystem.set("cifs_ops", Long.parseLong(cifs_ops));
			}
			if (attrs.getValue("HTTP_OPS") != null && attrs.getValue("HTTP_OPS").length() > 0) {
				String http_ops = attrs.getValue("HTTP_OPS");
				argsystem.set("http_ops", Long.parseLong(http_ops));
			}
			if (attrs.getValue("FCP_OPS") != null && attrs.getValue("FCP_OPS").length() > 0) {
				String fcp_ops = attrs.getValue("FCP_OPS");
				argsystem.set("fcp_ops", Long.parseLong(fcp_ops));
			}
			if (attrs.getValue("ISCSI_OPS") != null && attrs.getValue("ISCSI_OPS").length() > 0) {
				String iscsi_ops = attrs.getValue("ISCSI_OPS");
				argsystem.set("iscsi_ops", Long.parseLong(iscsi_ops));
			}
			if (attrs.getValue("READ_OPS") != null && attrs.getValue("READ_OPS").length() > 0) {
				String read_ops = attrs.getValue("READ_OPS");
				argsystem.set("read_ops", Long.parseLong(read_ops));
			}
			if (attrs.getValue("WRITE_OPS") != null && attrs.getValue("WRITE_OPS").length() > 0) {
				String write_ops = attrs.getValue("WRITE_OPS");
				argsystem.set("write_ops", Long.parseLong(write_ops));
			}
			if (attrs.getValue("TOTAL_OPS") != null && attrs.getValue("TOTAL_OPS").length() > 0) {
				String total_ops = attrs.getValue("TOTAL_OPS");
				argsystem.set("total_ops", Long.parseLong(total_ops));
			}
			if (attrs.getValue("SYS_AVG_LATENCY") != null && attrs.getValue("SYS_AVG_LATENCY").length() > 0) {
				String sys_avg_latency = attrs.getValue("SYS_AVG_LATENCY");
				argsystem.set("sys_avg_latency", Long.parseLong(sys_avg_latency));
			}
			if (attrs.getValue("NET_DATA_RECV") != null && attrs.getValue("NET_DATA_RECV").length() > 0) {
				String net_data_recv = attrs.getValue("NET_DATA_RECV");
				argsystem.set("net_data_recv", Long.parseLong(net_data_recv));
			}
			if (attrs.getValue("NET_DATA_SENT") != null && attrs.getValue("NET_DATA_SENT").length() > 0) {
				String net_data_sent = attrs.getValue("NET_DATA_SENT");
				argsystem.set("net_data_sent", Long.parseLong(net_data_sent));
			}
			if (attrs.getValue("FCP_DATA_RECV") != null && attrs.getValue("FCP_DATA_RECV").length() > 0) {
				String fcp_data_recv = attrs.getValue("FCP_DATA_RECV");
				argsystem.set("fcp_data_recv", Long.parseLong(fcp_data_recv));
			}
			if (attrs.getValue("FCP_DATA_SENT") != null && attrs.getValue("FCP_DATA_SENT").length() > 0) {
				String fcp_data_sent = attrs.getValue("FCP_DATA_SENT");
				argsystem.set("fcp_data_sent", Long.parseLong(fcp_data_sent));
			}
			if (attrs.getValue("DISK_DATA_READ") != null && attrs.getValue("DISK_DATA_READ").length() > 0) {
				String disk_data_read = attrs.getValue("DISK_DATA_READ");
				argsystem.set("disk_data_read", Long.parseLong(disk_data_read));
			}
			if (attrs.getValue("DISK_DATA_WRITTEN") != null && attrs.getValue("DISK_DATA_WRITTEN").length() > 0) {
				String disk_data_written = attrs.getValue("DISK_DATA_WRITTEN");
				argsystem.set("disk_data_written", Long.parseLong(disk_data_written));
			}
			if (attrs.getValue("HDD_DATA_READ") != null && attrs.getValue("HDD_DATA_READ").length() > 0) {
				String hdd_data_read = attrs.getValue("HDD_DATA_READ");
				argsystem.set("hdd_data_read", Long.parseLong(hdd_data_read));
			}
			if (attrs.getValue("HDD_DATA_WRITTEN") != null && attrs.getValue("HDD_DATA_WRITTEN").length() > 0) {
				String hdd_data_written = attrs.getValue("HDD_DATA_WRITTEN");
				argsystem.set("hdd_data_written", Long.parseLong(hdd_data_written));
			}
			if (attrs.getValue("SSD_DATA_READ") != null && attrs.getValue("SSD_DATA_READ").length() > 0) {
				String ssd_data_read = attrs.getValue("SSD_DATA_READ");
				argsystem.set("ssd_data_read", Long.parseLong(ssd_data_read));
			}
			if (attrs.getValue("SSD_DATA_WRITTEN") != null && attrs.getValue("SSD_DATA_WRITTEN").length() > 0) {
				String ssd_data_written = attrs.getValue("SSD_DATA_WRITTEN");
				argsystem.set("ssd_data_written", Long.parseLong(ssd_data_written));
			}
			if (attrs.getValue("CPU_BUSY") != null && attrs.getValue("CPU_BUSY").length() > 0) {
				String cpu_busy = attrs.getValue("CPU_BUSY");
				argsystem.set("cpu_busy", Long.parseLong(cpu_busy));
			}
			if (attrs.getValue("AVG_PROCESSOR_BUSY") != null && attrs.getValue("AVG_PROCESSOR_BUSY").length() > 0) {
				String avg_processor_busy = attrs.getValue("AVG_PROCESSOR_BUSY");
				argsystem.set("avg_processor_busy", Long.parseLong(avg_processor_busy));
			}
			if (attrs.getValue("TOTAL_PROCESSOR_BUSY") != null && attrs.getValue("TOTAL_PROCESSOR_BUSY").length() > 0) {
				String total_processor_busy = attrs.getValue("TOTAL_PROCESSOR_BUSY");
				argsystem.set("total_processor_busy", Long.parseLong(total_processor_busy));
			}
		}
		
		//For Port
		if ("PRF_PORT".equals(qName)) {
			Long recvIO = null;
			Long sendIO = null;
			Long recvKB = null;
			Long sendKB = null;
			Long recvTime = null;
			Long sendTime = null;
			argport = new DataRow();
			if (attrs.getValue("PORT_NAME") != null && attrs.getValue("PORT_NAME").length() > 0) {
				String PortName = attrs.getValue("PORT_NAME");
				argport.set("port_name", PortName);
			}
			if (attrs.getValue("RECV_IO") != null && attrs.getValue("RECV_IO").length() > 0) {
				recvIO = Long.parseLong(attrs.getValue("RECV_IO"));
				argport.set("recv_io", recvIO);
			}
			if (attrs.getValue("RECV_KB") != null && attrs.getValue("RECV_KB").length() > 0) {
				recvKB = Long.parseLong(attrs.getValue("RECV_KB"));
				argport.set("recv_kb", recvKB);
			}
			if (attrs.getValue("SEND_IO") != null && attrs.getValue("SEND_IO").length() > 0) {
				sendIO = Long.parseLong(attrs.getValue("SEND_IO"));
				argport.set("send_io", sendIO);
			}
			if (attrs.getValue("SEND_KB") != null && attrs.getValue("SEND_KB").length() > 0) {
				sendKB = Long.parseLong(attrs.getValue("SEND_KB"));
				argport.set("send_kb", sendKB);
			}
			if (attrs.getValue("SEND_TIME") != null && attrs.getValue("SEND_TIME").length() > 0) {
				sendTime = Long.parseLong(attrs.getValue("SEND_TIME"));
				argport.set("send_time", sendTime);
			}
			if (attrs.getValue("RECV_TIME") != null && attrs.getValue("RECV_TIME").length() > 0) {
				recvTime = Long.parseLong(attrs.getValue("RECV_TIME"));
				argport.set("recv_time", recvTime);
			}
			if (attrs.getValue("BNDW_SEND_UTIL") != null && attrs.getValue("BNDW_SEND_UTIL").length() > 0) {
				String SendUtil = attrs.getValue("BNDW_SEND_UTIL");
				argport.set("bndw_send_util", SendUtil);
			}
			if (attrs.getValue("BNDW_RECV_UTIL") != null && attrs.getValue("BNDW_RECV_UTIL").length() > 0) {
				String RecvUtil = attrs.getValue("BNDW_RECV_UTIL");
				argport.set("bndw_recv_util", RecvUtil);
			}
			Long the_io = null;
			if (recvIO != null && sendIO != null) {
				the_io = recvIO + sendIO;
			}
			Long the_kb = null;
			if (recvKB != null && sendKB != null) {
				the_kb = recvKB + sendKB;
			}
			Long the_time = null;
			if (sendTime != null && recvTime != null) {
				the_time = sendTime + recvTime;
			}
			argport.set("the_io", the_io);
			argport.set("the_kb", the_kb);
			argport.set("the_time", the_time);
		}
		
		//For Storage Volume
		if ("PRF_VOLUME".equals(qName)) {
			Long read_io_time = null;
			Long write_io_time = null;
			argvolume = new DataRow();
			if (attrs.getValue("READ_HIT_IO") != null && attrs.getValue("READ_HIT_IO").length() > 0) {
				String READ_HIT_IO = attrs.getValue("READ_HIT_IO");
				argvolume.set("read_hit_io", Long.parseLong(READ_HIT_IO));
			}
			if (attrs.getValue("WRITE_HIT_IO") != null && attrs.getValue("WRITE_HIT_IO").length() > 0) {
				argvolume.set("write_hit_io", Long.parseLong(attrs.getValue("WRITE_HIT_IO")));
			}
			if (attrs.getValue("READ_IO") != null && attrs.getValue("READ_IO").length() > 0) {
				argvolume.set("read_io", Long.parseLong(attrs.getValue("READ_IO")));
			}
			if (attrs.getValue("WRITE_IO") != null && attrs.getValue("WRITE_IO").length() > 0) {
				argvolume.set("write_io", Long.parseLong(attrs.getValue("WRITE_IO")));
			}
			if (attrs.getValue("READ_IO_TIME") != null && attrs.getValue("READ_IO_TIME").length() > 0) {
				read_io_time = Long.parseLong(attrs.getValue("READ_IO_TIME"));
				argvolume.set("read_io_time", read_io_time);
			}
			if (attrs.getValue("WRITE_IO_TIME") != null && attrs.getValue("WRITE_IO_TIME").length() > 0) {
				write_io_time = Long.parseLong(attrs.getValue("WRITE_IO_TIME"));
				argvolume.set("write_io_time", write_io_time);
			}
			if (attrs.getValue("READ_KB") != null && attrs.getValue("READ_KB").length() > 0) {
				argvolume.set("read_kb", Long.parseLong(attrs.getValue("READ_KB")));
			}
			if (attrs.getValue("WRITE_KB") != null && attrs.getValue("WRITE_KB").length() > 0) {
				argvolume.set("write_kb", Long.parseLong(attrs.getValue("WRITE_KB")));
			}
			if (attrs.getValue("VOLUME_NAME") != null && attrs.getValue("VOLUME_NAME").length() > 0) {
				argvolume.set("volume_name", attrs.getValue("VOLUME_NAME"));
			}
			if (attrs.getValue("VOL_UTIL_PERCENTAGE") != null) {
				argvolume.set("vol_util_percentage", Short.parseShort(attrs.getValue("VOL_UTIL_PERCENTAGE")));
			}
			if (attrs.getValue("READ_IO") != null && attrs.getValue("WRITE_IO") != null) {
				Long the_io = Long.parseLong(attrs.getValue("READ_IO")) + Long.parseLong(attrs.getValue("WRITE_IO"));
				argvolume.set("the_io", the_io);
			}
			if (attrs.getValue("READ_HIT_IO") != null && attrs.getValue("WRITE_HIT_IO") != null) {
				Long the_hit_io = Long.parseLong(attrs.getValue("READ_HIT_IO")) + Long.parseLong(attrs.getValue("WRITE_HIT_IO"));
				argvolume.set("the_hit_io", the_hit_io);
			}
			if (attrs.getValue("READ_KB") != null && attrs.getValue("WRITE_KB") != null) {
				Long the_kb = Long.parseLong(attrs.getValue("READ_KB")) + Long.parseLong(attrs.getValue("WRITE_KB"));
				argvolume.set("the_kb", the_kb);
			}
			if (read_io_time != null && write_io_time != null) {
				Long the_io_time = read_io_time + write_io_time;
				argvolume.set("the_io_time", the_io_time);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("PRF_TIMESTAMP".equals(qName)) {
			time.add(argtime);
		}
		if (qName.equals("PRF_SYSTEM")) {
			system.add(argsystem);
		}
		if ("PRF_PORT".equals(qName)) {
			port.add(argport);
		}
		if ("PRF_VOLUME".equals(qName)) {
			volume.add(argvolume);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
	}

	public List<DataRow> getSystem() {
		return system;
	}

	public void setSystem(List<DataRow> system) {
		this.system = system;
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
