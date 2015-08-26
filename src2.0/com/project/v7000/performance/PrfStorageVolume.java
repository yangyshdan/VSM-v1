package com.project.v7000.performance;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrTblColConstant;

public class PrfStorageVolume extends DefaultHandler {
	
	private List<DataRow> storageVolumeList;
	private DataRow storageVolume;
	private DataRow timestamp;
	
	@Override
	public void startDocument() throws SAXException {
		storageVolumeList = new ArrayList<DataRow>();
		timestamp = new DataRow();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("diskStatsColl")) {
			timestamp.set(SrTblColConstant.TT_SUBSYSTEM_NAME,attributes.getValue("cluster"));
			timestamp.set(SrTblColConstant.TT_SAMPLE_TIME,attributes.getValue("timestamp"));
		}
		if (qName.equals("vdsk")) {
			storageVolume = new DataRow();
			storageVolume.set(SrTblColConstant.SV_VOLUME_NAME, attributes.getValue("id"));
			Long ro = StringHelper.isEmpty(attributes.getValue("ro")) ? 0 : Long.parseLong(attributes.getValue("ro"));
			storageVolume.set(SrTblColConstant.SV_READ_IO, ro);
			Long wo = StringHelper.isEmpty(attributes.getValue("wo")) ? 0 : Long.parseLong(attributes.getValue("wo"));
			storageVolume.set(SrTblColConstant.SV_WRITE_IO, wo);
			Long ctrh = StringHelper.isEmpty(attributes.getValue("ctrh")) ? 0 : Long.parseLong(attributes.getValue("ctrh"));
			Long ctr = StringHelper.isEmpty(attributes.getValue("ctr")) ? 0 : Long.parseLong(attributes.getValue("ctr"));
			ctr = (ctr == 0 ? 1 : ctr);
			Long rhio = ro * ctrh / ctr;
			storageVolume.set(SrTblColConstant.SV_READ_HIT_IO, rhio);
			Long ctw = StringHelper.isEmpty(attributes.getValue("ctw")) ? 0 : Long.parseLong(attributes.getValue("ctw"));
			Long ctwft = StringHelper.isEmpty(attributes.getValue("ctwft")) ? 0 : Long.parseLong(attributes.getValue("ctwft"));
			ctw = (ctw == 0 ? 1 : ctw);
			Long whio = wo * (ctw - ctwft) / ctw;
			storageVolume.set(SrTblColConstant.SV_WRITE_HIT_IO, whio);
			Long rb = StringHelper.isEmpty(attributes.getValue("rb")) ? 0 : Long.parseLong(attributes.getValue("rb"));
			storageVolume.set(SrTblColConstant.SV_READ_KB, rb);
			Long wb = StringHelper.isEmpty(attributes.getValue("wb")) ? 0 : Long.parseLong(attributes.getValue("wb"));
			storageVolume.set(SrTblColConstant.SV_WRITE_KB, wb);
			Long rl = StringHelper.isEmpty(attributes.getValue("rl")) ? 0 : Long.parseLong(attributes.getValue("rl"));
			storageVolume.set(SrTblColConstant.SV_READ_IO_TIME, rl);
			Long wl = StringHelper.isEmpty(attributes.getValue("wl")) ? 0 : Long.parseLong(attributes.getValue("wl"));
			storageVolume.set(SrTblColConstant.SV_WRITE_IO_TIME, wl);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("vdsk")) {
			storageVolumeList.add(storageVolume);
		}
	}
	
	public List<DataRow> getStorageVolumeList() {
		return storageVolumeList;
	}
	public void setStorageVolumeList(List<DataRow> storageVolumeList) {
		this.storageVolumeList = storageVolumeList;
	}
	public DataRow getStorageVolume() {
		return storageVolume;
	}
	public void setStorageVolume(DataRow storageVolume) {
		this.storageVolume = storageVolume;
	}

	public DataRow getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DataRow timestamp) {
		this.timestamp = timestamp;
	}
	
}
