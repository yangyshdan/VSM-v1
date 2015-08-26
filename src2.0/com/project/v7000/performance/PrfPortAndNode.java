package com.project.v7000.performance;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrTblColConstant;

public class PrfPortAndNode extends DefaultHandler {
	
	private List<DataRow> storageNodeList;
	private List<DataRow> portList;
	private DataRow storageNode;
	private DataRow port;
	private DataRow timestamp;
	
	@Override
	public void startDocument() throws SAXException {
		storageNodeList = new ArrayList<DataRow>();
		portList = new ArrayList<DataRow>();
		timestamp = new DataRow();
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("diskStatsColl")) {
			timestamp.set(SrTblColConstant.TT_SUBSYSTEM_NAME,attributes.getValue("cluster"));
			timestamp.set(SrTblColConstant.TT_SAMPLE_TIME,attributes.getValue("timestamp"));
		}
		if (qName.equals("node")) {
			storageNode = new DataRow();
			storageNode.set(SrTblColConstant.SN_SP_NAME, attributes.getValue("id"));
			Long ro = StringHelper.isEmpty(attributes.getValue("ro")) ? 0 : Long.parseLong(attributes.getValue("ro"));
			storageNode.set(SrTblColConstant.SN_READ_IO, ro);
			Long wo = StringHelper.isEmpty(attributes.getValue("wo")) ? 0 : Long.parseLong(attributes.getValue("wo"));
			storageNode.set(SrTblColConstant.SN_WRITE_IO, wo);
			Long rb = StringHelper.isEmpty(attributes.getValue("rb")) ? 0 : Long.parseLong(attributes.getValue("rb"));
			storageNode.set(SrTblColConstant.SN_READ_KB, rb);
			Long wb = StringHelper.isEmpty(attributes.getValue("wb")) ? 0 : Long.parseLong(attributes.getValue("wb"));
			storageNode.set(SrTblColConstant.SN_WRITE_KB, wb);
			
//			storageNode.set("read_hit_io", 0);
//			storageNode.set("write_hit_io", 0);
			Long re = StringHelper.isEmpty(attributes.getValue("re")) ? 0 : Long.parseLong(attributes.getValue("re"));
			Long rq = StringHelper.isEmpty(attributes.getValue("rq")) ? 0 : Long.parseLong(attributes.getValue("rq"));
			storageNode.set(SrTblColConstant.SN_READ_IO_TIME, (re + rq));
			Long we = StringHelper.isEmpty(attributes.getValue("we")) ? 0 : Long.parseLong(attributes.getValue("we"));
			Long wq = StringHelper.isEmpty(attributes.getValue("wq")) ? 0 : Long.parseLong(attributes.getValue("wq"));
			storageNode.set(SrTblColConstant.SN_WIRTE_IO_TIME, (we + wq));
		} else if (qName.equals("port")) {
			port = new DataRow();
			port.set(SrTblColConstant.P_PORT_NAME, attributes.getValue("wwpn"));
			Long her = StringHelper.isEmpty(attributes.getValue("her")) ? 0 : Long.parseLong(attributes.getValue("her"));
			port.set(SrTblColConstant.P_SEND_IO, her);
			Long cet = StringHelper.isEmpty(attributes.getValue("cet")) ? 0 : Long.parseLong(attributes.getValue("cet"));
			port.set(SrTblColConstant.P_RECV_IO, cet);
			Long hbt = StringHelper.isEmpty(attributes.getValue("hbt")) ? 0 : Long.parseLong(attributes.getValue("hbt"));
			port.set(SrTblColConstant.P_SEND_KB, hbt);
			Long cbt = StringHelper.isEmpty(attributes.getValue("cbt")) ? 0 : Long.parseLong(attributes.getValue("cbt"));
			port.set(SrTblColConstant.P_RECV_KB, cbt);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("node")) {
			storageNodeList.add(storageNode);
		} else if (qName.equals("port")) {
			portList.add(port);
		}
	}
	public List<DataRow> getPortList() {
		return portList;
	}
	public void setPortList(List<DataRow> portList) {
		this.portList = portList;
	}
	public DataRow getPort() {
		return port;
	}
	public void setPort(DataRow port) {
		this.port = port;
	}
	public List<DataRow> getStorageNodeList() {
		return storageNodeList;
	}
	public void setStorageNodeList(List<DataRow> storageNodeList) {
		this.storageNodeList = storageNodeList;
	}
	public DataRow getStorageNode() {
		return storageNode;
	}
	public void setStorageNode(DataRow storageNode) {
		this.storageNode = storageNode;
	}
	public DataRow getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(DataRow timestamp) {
		this.timestamp = timestamp;
	}
	
}
