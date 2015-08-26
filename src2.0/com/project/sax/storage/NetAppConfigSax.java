package com.project.sax.storage;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.sr.constants.SrContant;

public class NetAppConfigSax extends DefaultHandler {
	
	private List<DataRow> systemList = null;
	private List<DataRow> poolList = null;
	private List<DataRow> portList = null;
	private List<DataRow> nodeList = null;
	private List<DataRow> ddmList = null;
	private List<DataRow> volumeList = null;
	private List<DataRow> poolAndVolumeList = null;
	
	private DataRow system = null;
	private DataRow pool = null;
	private DataRow port = null;
	private DataRow node = null;
	private DataRow ddm = null;
	private DataRow volume = null;
	private DataRow poolAndVolume = null;
	
	@Override
	public void startDocument() throws SAXException {
		systemList = new ArrayList<DataRow>();
		poolList = new ArrayList<DataRow>();
		portList = new ArrayList<DataRow>();
		nodeList = new ArrayList<DataRow>();
		ddmList = new ArrayList<DataRow>();
		volumeList = new ArrayList<DataRow>();
		poolAndVolumeList = new ArrayList<DataRow>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
		//For Storage subSystem
		if (qName.equals("RES_STORAGESUBSYSTEM")) {
			system = new DataRow();
			String name = attrs.getValue("NAME");
			if (name != null) {
				system.set("name", name);
			}
			String display_name = attrs.getValue("DISPLAY_NAME");
			if (display_name != null) {
				system.set("display_name", display_name);
			}
			String allocated_capacity = attrs.getValue("ALLOCATED_CAPACITY");
			if (allocated_capacity != null) {
				system.set("cache_gb", Long.parseLong(allocated_capacity) / 1024);
				system.set("allocated_capacity", Long.parseLong(allocated_capacity));
			}
			String available_capacity = attrs.getValue("AVAILABLE_CAPACITY");
			if (available_capacity != null) {
				system.set("available_capacity", Long.parseLong(available_capacity));
			}
			String num_disk = attrs.getValue("NUM_DISK");
			if (num_disk != null) {
				system.set("num_disk", Integer.parseInt(num_disk));
			}
			String num_lun = attrs.getValue("NUM_LUN");
			if (num_lun != null) {
				system.set("num_lun", Integer.parseInt(num_lun));
			}
			String ip_address = attrs.getValue("IP_ADDRESS");
			if (ip_address != null) {
				system.set("ip_address", ip_address);
			}
			String model = attrs.getValue("MODEL");
			if (model != null) {
				system.set("model", model);
			}
			String code_level = attrs.getValue("CODE_LEVEL");
			if (model != null) {
				system.set("code_level", code_level);
			}
			String ser_number = attrs.getValue("SERIAL_NUMBER");
			if (ser_number != null) {
				system.set("serial_number", ser_number);
			}
			String vend_name = attrs.getValue("VENDOR_NAME");
			if (vend_name != null) {
				system.set("vendor_name", vend_name);
			}
			String phy_capacity = attrs.getValue("PHYSICAL_DISK_CAPACITY");
			if (phy_capacity != null) {
				system.set("physical_disk_capacity", Long.parseLong(phy_capacity));
			}
			String usable_capacity = attrs.getValue("TOTAL_USABLE_CAPACITY");
			if (usable_capacity != null) {
				system.set("total_usable_capacity",Long.parseLong(usable_capacity));
			}
			String unallocat_capacity = attrs.getValue("UNALLOCATED_USABLE_CAPACITY");
			if (unallocat_capacity != null) {
				system.set("unallocated_usable_capacity", Long.parseLong(unallocat_capacity));
			}
			String tolun_capacity = attrs.getValue("TOTAL_LUN_CAPACITY");
			if (tolun_capacity != null) {
				system.set("total_lun_capacity", Long.parseLong(tolun_capacity));
			}
			String operate_status = attrs.getValue("OPERATIONAL_STATUS");
			if (operate_status != null) {
				system.set("operational_status", operate_status);
			}
			system.set("update_timestamp", SrContant.getTimestamp());
		}
		
		//For Storage Node
		if (qName.equals("RES_NODE")) {
			node = new DataRow();
			String sp_name = attrs.getValue("SP_NAME");
			if (sp_name != null) {
				node.set("sp_name", sp_name);
			}
			node.set("update_timestamp", SrContant.getTimestamp());
		}
		
		//For Port
		if (qName.equals("RES_PORT")) {
			port = new DataRow();
			String name = attrs.getValue("NAME");
			if (name != null) {
				port.set("name", name);
			}
			port.set("type", "Nx_PORT");
			String subsystem_name = attrs.getValue("SUBSYSTEM_NAME");
			if (subsystem_name != null) {
				port.set("subsystem_name", subsystem_name);
			}
			String sp_name = attrs.getValue("SP_NAME");
			if (sp_name != null) {
				port.set("node_name", sp_name);
			}
			port.set("update_timestamp", SrContant.getTimestamp());
		}
		
		//For Storage Pool
		if (qName.equals("RES_POOL")) {
			pool = new DataRow();
			String name = attrs.getValue("NAME");
			if (name != null) {
				pool.set("name", name);
			}
			String display_name = attrs.getValue("DISPLAY_NAME");
			if (display_name != null) {
				pool.set("display_name", display_name);
			}
			String num_backend_disk = attrs.getValue("NUM_BACKEND_DISK");
			if (num_backend_disk != null) {
				pool.set("num_backend_disk", Integer.parseInt(num_backend_disk));
			}
			String num_lun = attrs.getValue("NUM_LUN");
			if (num_lun != null) {
				pool.set("num_lun", Integer.parseInt(num_lun));
			}
			String total_usable_capacity = attrs.getValue("TOTAL_USABLE_CAPACITY");
			if (total_usable_capacity != null) {
				pool.set("total_usable_capacity", Long.parseLong(total_usable_capacity));
			}
			String unallocated_capacity = attrs.getValue("UNALLOCATED_CAPACITY");
			if (unallocated_capacity != null) {
				pool.set("unallocated_capacity", Long.parseLong(unallocated_capacity));
			}
			String operational_status = attrs.getValue("OPERATIONAL_STATUS");
			if (operational_status != null) {
				pool.set("operational_status", operational_status);
			}
			pool.set("update_timestamp", SrContant.getTimestamp());
		}
		
		//For Storage Volume
		if (qName.equals("RES_VOLUME")) {
			volume = new DataRow();
			String name = attrs.getValue("NAME");
			if (name != null) {
				volume.set("name", name);
			}
			String display_name = attrs.getValue("DISPLAY_NAME");
			if (display_name != null) {
				volume.set("display_name", display_name);
			}
			String sp_name = attrs.getValue("SP_NAME");
			if (sp_name != null) {
				volume.set("current_owner", sp_name);
			}
			String logical_capacity = attrs.getValue("LOGICAL_CAPACITY");
			if (logical_capacity != null) {
				volume.set("logical_capacity", logical_capacity);
			}
			String operational_status = attrs.getValue("OPERATIONAL_STATUS");
			if (operational_status != null) {
				volume.set("operational_status", operational_status);
			}
			volume.set("update_timestamp", SrContant.getTimestamp());
		}
		
		//For Disk
		if (qName.equals("RES_DISK")) {
			ddm = new DataRow();
			String name = attrs.getValue("NAME");
			if (name != null) {
				ddm.set("name", name);
			}
			String display_name = attrs.getValue("DISPLAY_NAME");
			if (display_name != null) {
				ddm.set("display_name", display_name);
			}
			String subsystem_name = attrs.getValue("SUBSYSTEM_NAME");
			if (subsystem_name != null) {
				ddm.set("subsystem_name", subsystem_name);
			}
			String ddm_cap = attrs.getValue("DDM_CAP");
			if (ddm_cap != null) {
				ddm.set("ddm_cap", Long.parseLong(ddm_cap));
			}
			String ddm_type = attrs.getValue("DDM_TYPE");
			if (ddm_type != null) {
				ddm.set("ddm_type", ddm_type);
			}
			String ddm_rpm = attrs.getValue("DDM_RPM");
			if (ddm_rpm != null) {
				ddm.set("ddm_rpm", ddm_rpm);
			}
			ddm.set("update_timestamp", SrContant.getTimestamp());
		}
		
		//For Storage Pool And Volume
		if (qName.equals("MAP_STORAGEPOOL2STORAGEVOLUME")) {
			poolAndVolume = new DataRow();
			String pool_name = attrs.getValue("POOL_NAME");
			if (pool_name != null) {
				poolAndVolume.set("pool_name", pool_name);
			}
			String volume_name = attrs.getValue("VOLUME_NAME");
			if (volume_name != null) {
				poolAndVolume.set("volume_name", volume_name);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("RES_STORAGESUBSYSTEM")) {
			systemList.add(system);
		}
		if (qName.equals("RES_NODE")) {
			nodeList.add(node);
		}
		if (qName.equals("RES_PORT")) {
			portList.add(port);
		}
		if (qName.equals("RES_POOL")) {
			poolList.add(pool);
		}
		if (qName.equals("RES_VOLUME")) {
			volumeList.add(volume);
		}
		if (qName.equals("RES_DISK")) {
			ddmList.add(ddm);
		}
		if (qName.equals("MAP_STORAGEPOOL2STORAGEVOLUME")) {
			poolAndVolumeList.add(poolAndVolume);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
	}

	public List<DataRow> getSystemList() {
		return systemList;
	}

	public void setSystemList(List<DataRow> systemList) {
		this.systemList = systemList;
	}

	public List<DataRow> getPoolList() {
		return poolList;
	}

	public void setPoolList(List<DataRow> poolList) {
		this.poolList = poolList;
	}

	public List<DataRow> getPortList() {
		return portList;
	}

	public void setPortList(List<DataRow> portList) {
		this.portList = portList;
	}

	public List<DataRow> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<DataRow> nodeList) {
		this.nodeList = nodeList;
	}

	public List<DataRow> getDdmList() {
		return ddmList;
	}

	public void setDdmList(List<DataRow> ddmList) {
		this.ddmList = ddmList;
	}

	public List<DataRow> getVolumeList() {
		return volumeList;
	}

	public void setVolumeList(List<DataRow> volumeList) {
		this.volumeList = volumeList;
	}

	public List<DataRow> getPoolAndVolumeList() {
		return poolAndVolumeList;
	}

	public void setPoolAndVolumeList(List<DataRow> poolAndVolumeList) {
		this.poolAndVolumeList = poolAndVolumeList;
	}
	
}
