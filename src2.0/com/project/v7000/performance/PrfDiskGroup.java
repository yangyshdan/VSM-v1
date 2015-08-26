package com.project.v7000.performance;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrTblColConstant;

public class PrfDiskGroup extends DefaultHandler {
	
	private List<DataRow> diskGroupList;
	private DataRow diskGroup;
	private DataRow timestamp;
	
	@Override
	public void startDocument() throws SAXException {
		diskGroupList = new ArrayList<DataRow>();
		timestamp = new DataRow();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("diskStatsColl")) {
			timestamp.set(SrTblColConstant.TT_SUBSYSTEM_NAME,attributes.getValue("cluster"));
			timestamp.set(SrTblColConstant.TT_SAMPLE_TIME,attributes.getValue("timestamp"));
		}
		if (qName.equals("mdsk")) {
			diskGroup = new DataRow();
			diskGroup.set(SrTblColConstant.DG_DISKGROUP_NAME, attributes.getValue("id"));
			diskGroup.set(SrTblColConstant.DG_BCK_READ_IO, Long.parseLong(attributes.getValue("ro")));
			diskGroup.set(SrTblColConstant.DG_BCK_WRITE_IO, Long.parseLong(attributes.getValue("wo")));
			Long rb = StringHelper.isEmpty(attributes.getValue("rb")) ? 0 : Long.parseLong(attributes.getValue("rb"));
			diskGroup.set(SrTblColConstant.DG_BCK_READ_KB, rb);
			Long wb = StringHelper.isEmpty(attributes.getValue("wb")) ? 0 : Long.parseLong(attributes.getValue("wb"));
			diskGroup.set(SrTblColConstant.DG_BCK_WRITE_KB, wb);
			Long re = StringHelper.isEmpty(attributes.getValue("re")) ? 0 : Long.parseLong(attributes.getValue("re"));
			Long rq = StringHelper.isEmpty(attributes.getValue("rq")) ? 0 : Long.parseLong(attributes.getValue("rq"));
			diskGroup.set(SrTblColConstant.DG_BCK_READ_TIME, (re + rq));
			Long we = StringHelper.isEmpty(attributes.getValue("we")) ? 0 : Long.parseLong(attributes.getValue("we"));
			Long wq = StringHelper.isEmpty(attributes.getValue("wq")) ? 0 : Long.parseLong(attributes.getValue("wq"));
			diskGroup.set(SrTblColConstant.DG_BCK_WRITE_TIME, (we + wq));
			diskGroup.set(SrTblColConstant.DG_IO_TYPE, "BEFORE_RAID");
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("mdsk")) {
			diskGroupList.add(diskGroup);
		}
	}
	
	public List<DataRow> getDiskGroupList() {
		return diskGroupList;
	}
	public void setDiskGroupList(List<DataRow> diskGroupList) {
		this.diskGroupList = diskGroupList;
	}
	public DataRow getDiskGroup() {
		return diskGroup;
	}
	public void setDiskGroup(DataRow diskGroup) {
		this.diskGroup = diskGroup;
	}

	public DataRow getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DataRow timestamp) {
		this.timestamp = timestamp;
	}
	
}
