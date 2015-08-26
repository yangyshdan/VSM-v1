package com.project.sax.storage;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.sr.constants.SrContant;

public class StorageConfigInfo extends DefaultHandler{
	private List<DataRow> subsystem = null;
	private List<DataRow> diskgroupAndDDM=null;
	private List<DataRow> portAndHba=null;
	private List<DataRow> hostgroupAndHba=null;
	private List<DataRow> hostgroupAndVolume=null;
	private List<DataRow> poolAndDiskgroup=null;
	private List<DataRow> poolAndVolume=null;
	private List<DataRow> diskgroups=null;
	private List<DataRow> hbas=null;
	private List<DataRow> hostgroups=null;
	private List<DataRow> pool=null;
	private List<DataRow> ports=null;
	private List<DataRow> storageNodes=null;
	private List<DataRow> ddms=null;
	private List<DataRow> volumes=null;
	
	private DataRow argsub = null;
	private DataRow argdiskAndDDM = null;
	private DataRow argportAndHba = null;
	private DataRow arghostAndHba = null;
	private DataRow arghostAndVolume = null;
	private DataRow argpoolAndDisks = null;
	private DataRow argpoolAndVolume = null;
	private DataRow argdiskgroup = null;
	private DataRow arghba = null;
	private DataRow arghostgroup = null;
	private DataRow argpool = null;
	private DataRow argport = null;
	private DataRow argnode = null;
	private DataRow argddm = null;
	private DataRow argvolume = null;
	
	@Override
	public void startDocument() throws SAXException {
		subsystem=new ArrayList<DataRow>();
		diskgroupAndDDM=new ArrayList<DataRow>();
		portAndHba=new ArrayList<DataRow>();
		hostgroupAndHba=new ArrayList<DataRow>();
		hostgroupAndVolume=new ArrayList<DataRow>();
		poolAndDiskgroup=new ArrayList<DataRow>();
		poolAndVolume=new ArrayList<DataRow>();
		diskgroups=new ArrayList<DataRow>();
		hbas=new ArrayList<DataRow>();
		hostgroups=new ArrayList<DataRow>();
		pool=new ArrayList<DataRow>();
		ports=new ArrayList<DataRow>();
		storageNodes=new ArrayList<DataRow>();
		ddms=new ArrayList<DataRow>();
		volumes=new ArrayList<DataRow>();
		
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
        if("RES_STORAGESUBSYSTEM".equals(qName)){  
        	argsub = new DataRow();
        	String allocated_capacity=attrs.getValue("ALLOCATED_CAPACITY");
        	if(allocated_capacity!=null){
        		argsub.set("cache_gb", Long.parseLong(allocated_capacity)/1024/1024);}
        	String available_capacity =attrs.getValue("AVAILABLE_CAPACITY");
        	if(available_capacity !=null){
        		argsub.set("available_capacity", Long.parseLong(available_capacity)/1024/1024);}
        	String backend_storage_capacity =attrs.getValue("BACKEND_STORAGE_CAPACITY");
        	if(backend_storage_capacity !=null){
        		argsub.set("backend_storage_capacity", Long.parseLong(backend_storage_capacity)/1024/1024);}
        	String display_name =attrs.getValue("DISPLAY_NAME");
        	if(display_name!=null){
        		argsub.set("display_name", display_name);}
        	String cache_gb=attrs.getValue("CACHE_GB");
        	if(cache_gb!=null){
        		argsub.set("cache_gb", Integer.parseInt(cache_gb));}
        	String nvs_gb=attrs.getValue("NVS_GB");
        	if(nvs_gb!=null){
        		argsub.set("nvs_gb", Integer.parseInt(nvs_gb));}
        	String num_disk=attrs.getValue("NUM_DISK");
        	if(num_disk!=null){
        		argsub.set("num_disk", Integer.parseInt(num_disk));}
        	String num_lun=attrs.getValue("NUM_LUN");
        	if(num_lun!=null){
        		argsub.set("num_lun", Integer.parseInt(num_lun));}
        	String ip_address=attrs.getValue("IP_ADDRESS");
        	if(ip_address!=null){
        		argsub.set("ip_address",ip_address);}
        	String model=attrs.getValue("MODEL");
        	if(model!=null){
        		argsub.set("model", model);
        	}
        	String code_level=attrs.getValue("CODE_LEVEL");
        	if(model!=null){
        		argsub.set("code_level", code_level);}
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		argsub.set("name", name1);}
        	String ser_number=attrs.getValue("SERIAL_NUMBER");
        	if(ser_number!=null){
        		argsub.set("serial_number", ser_number);}
        	String led_status =attrs.getValue("LED_STATUS");
        	if(led_status!=null){
        		argsub.set("led_status", led_status);}
        	String vend_name=attrs.getValue("VENDOR_NAME");
        	if(vend_name!=null){
        		argsub.set("vendor_name", vend_name);}
        	String phy_capacity=attrs.getValue("PHYSICAL_DISK_CAPACITY");
        	if(phy_capacity!=null){
        		argsub.set("physical_disk_capacity", Long.parseLong(phy_capacity)/1024/1024);}
        	String un_capacity=attrs.getValue("UNFORMATTED_PHYSICAL_DISK_CAPACITY");
        	if(un_capacity!=null){
        		argsub.set("unformatted_physical_disk_capacity", Long.parseLong(un_capacity)/1024/1024);}
        	String tollun_capacity =attrs.getValue("TOTAL_BACKEND_LUN_CAPACITY");
        	if(tollun_capacity!=null){
        		argsub.set("total_backend_lun_capacity", Integer.parseInt(tollun_capacity)/1024/1024);}
        	String unused_capacity =attrs.getValue("UNUSED_BACKEND_LUN_CAPACITY");
        	if(unused_capacity!=null){
        		argsub.set("unused_backend_lun_capacity", Long.parseLong(unused_capacity)/1024/1024);}
        	String usable_capacity =attrs.getValue("TOTAL_USABLE_CAPACITY");
        	if(usable_capacity!=null){
        		argsub.set("total_usable_capacity", Long.parseLong(usable_capacity)/1024/1024);}
        	String unallocat_capacity =attrs.getValue("UNALLOCATED_USABLE_CAPACITY");
        	if(unallocat_capacity!=null){
        		argsub.set("unallocated_usable_capacity", Long.parseLong(unallocat_capacity)/1024/1024);}
        	String tolun_capacity =attrs.getValue("TOTAL_LUN_CAPACITY");
        	if(tolun_capacity!=null){
        		argsub.set("total_lun_capacity", Long.parseLong(tolun_capacity)/1024/1024);}
        	String unmapp_cap =attrs.getValue("UNMAPPED_LUN_CAPACITY");
        	if(unmapp_cap!=null){
        		argsub.set("unmapped_lun_capacity", Long.parseLong(unmapp_cap)/1024/1024);}
        	String operate_status =attrs.getValue("OPERATIONAL_STATUS");
        	if(operate_status!=null){
        		argsub.set("operational_status", operate_status);}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		argsub.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
        if("MAP_DISKGROUP2STORAGEDDM".equals(qName)){
        	argdiskAndDDM = new DataRow();
        	if(attrs.getValue("DDM_NAME")!=null && attrs.getValue("DDM_NAME").length() > 0){
        		argdiskAndDDM.set("ddm_name", attrs.getValue("DDM_NAME"));
        	}
        	if(attrs.getValue("DISKGROUP_NAME")!=null && attrs.getValue("DISKGROUP_NAME").length()>0){
        		argdiskAndDDM.set("diskgroup_name", attrs.getValue("DISKGROUP_NAME"));
        	}
        }
        if("MAP_HBA_STORAGEPORT".equals(qName)){
        	argportAndHba = new DataRow();
        	if(attrs.getValue("HBA_DEVICENAME")!=null && attrs.getValue("HBA_DEVICENAME").length()>0){
        		argportAndHba.set("hba_devicename", attrs.getValue("HBA_DEVICENAME"));
        	}
        	if(attrs.getValue("PORT_NAME")!=null && attrs.getValue("PORT_NAME").length()>0){
        		argportAndHba.set("port_name", attrs.getValue("PORT_NAME"));
        	}
        	if(attrs.getValue("UID")!=null && attrs.getValue("UID").length()>0){
        		argportAndHba.set("uid", attrs.getValue("UID"));
        	}
        }
        if("MAP_HOSTGROUPHBA".equals(qName)){
        	arghostAndHba = new DataRow();
        	if(attrs.getValue("HOSTGROUP_NAME")!=null && attrs.getValue("HOSTGROUP_NAME").length()>0){
        		arghostAndHba.set("hostgroup_name", attrs.getValue("HOSTGROUP_NAME"));
        	}
        	if(attrs.getValue("HBA_UID")!=null && attrs.getValue("HBA_UID").length()>0){
        		arghostAndHba.set("hba_uid", attrs.getValue("HBA_UID"));
        	}
        }
        if("MAP_HOSTGROUPVOLUME".equals(qName)){
        	arghostAndVolume = new DataRow();
        	if(attrs.getValue("HOSTGROUP_NAME")!=null && attrs.getValue("HOSTGROUP_NAME").length()>0){
        		arghostAndVolume.set("hostgroup_name", attrs.getValue("HOSTGROUP_NAME"));
        	}
        	if(attrs.getValue("VOLUME_NAME")!=null && attrs.getValue("VOLUME_NAME").length()>0){
        		arghostAndVolume.set("volume_name", attrs.getValue("VOLUME_NAME"));
        	}
        }
        if("MAP_STORAGEPOOL2DISKGROUP".equals(qName)){
        	argpoolAndDisks = new DataRow();
        	String poolName=attrs.getValue("STORAGEPOOL_NAME");
        	if(poolName!=null){
        		argpoolAndDisks.set("pool_name",poolName);
        	}
        	String DiskgroupName=attrs.getValue("DISKGROUP_NAME");
        	if(DiskgroupName!=null){
        		argpoolAndDisks.set("diskgroup_name", DiskgroupName);
        	}
        }
        if("MAP_STORAGEPOOL2STORAGEVOLUME".equals(qName)){
        	argpoolAndVolume = new DataRow();
        	String PoolName=attrs.getValue("POOL_NAME");
        	if(PoolName!=null){
        		argpoolAndVolume.set("pool_name", PoolName);
        	}
        	String volumeName=attrs.getValue("VOLUME_NAME");
        	if(volumeName!=null){
        		argpoolAndVolume.set("volume_name", volumeName);
        	}
        }
        if("RES_DISKGROUP".equals(qName)){
        	argdiskgroup = new DataRow();
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		argdiskgroup.set("name",name1);
        	}
        	String disname=attrs.getValue("DISPLAY_NAME");
        	if(disname!=null){
        		argdiskgroup.set("display_name",disname);
        	}
        	String description=attrs.getValue("DESCRIPTION");
        	if(description!=null){
        		argdiskgroup.set("description",description);
        	}
        	String raid_level= attrs.getValue("RAID_LEVEL");
        	if(raid_level!=null){
        		argdiskgroup.set("raid_level",raid_level);
        	}
        	String width= attrs.getValue("WIDTH");
        	if(width!=null){
        		argdiskgroup.set("width",width);
        	}
        	String ddm_cap= attrs.getValue("DDM_CAP");
        	if(ddm_cap!=null){
        		argdiskgroup.set("ddm_cap",ddm_cap);
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		argdiskgroup.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
        if("RES_HBA".equals(qName)){
        	arghba = new DataRow();
        	if(attrs.getValue("SERVER_IP_ADDRESS")!=null && attrs.getValue("SERVER_IP_ADDRESS").length()>0){
        		arghba.set("server_ip_address", attrs.getValue("SERVER_IP_ADDRESS"));
        	}
        	if(attrs.getValue("SERVER_NAME")!=null && attrs.getValue("SERVER_NAME").length()>0){
        		arghba.set("server_name", attrs.getValue("SERVER_NAME"));
        	}
        	if(attrs.getValue("UID")!=null && attrs.getValue("UID").length()>0){
        		arghba.set("hba_uid", attrs.getValue("UID"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		arghba.set("update_timestamp",SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        } 
        if("RES_HOSTGROUP".equals(qName)){
        	arghostgroup = new DataRow();
        	if(attrs.getValue("HOSTGROUP_NAME")!=null && attrs.getValue("HOSTGROUP_NAME").length()>0){
        		arghostgroup.set("hostgroup_name", attrs.getValue("HOSTGROUP_NAME"));
        	}
        	if(attrs.getValue("SHAREABLE")!=null && attrs.getValue("SHAREABLE").length()>0){
        		arghostgroup.set("shareable", attrs.getValue("SHAREABLE"));
        	}
        	if(attrs.getValue("UID")!=null && attrs.getValue("UID").length()>0){
        		arghostgroup.set("UID", attrs.getValue("UID"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		arghostgroup.set("update_timestamp",SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        } 
        if("RES_POOL".equals(qName)){
        	argpool = new DataRow();
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		argpool.set("name",name1);}
        	String nubdisk=attrs.getValue("NUM_BACKEND_DISK");
        	if(nubdisk!=null){
        		argpool.set("num_backend_disk", Integer.parseInt(nubdisk));}
        	String numlum=attrs.getValue("NUM_LUN");
        	if(numlum!=null){
        		argpool.set("num_lun", Integer.parseInt(numlum));}
        	String readlevel=attrs.getValue("RAID_LEVEL");
        	if(readlevel!=null){
        		argpool.set("raid_level",readlevel);}
        	String tot_capacity=attrs.getValue("TOTAL_USABLE_CAPACITY");
        	if(tot_capacity!=null){
        		argpool.set("total_usable_capacity", Long.parseLong(tot_capacity)/1024/1024);}
        	String display_name=attrs.getValue("DISPLAY_NAME");
        	if(display_name!=null){
        		argpool.set("display_name", display_name);}
        	String unallocat_cap=attrs.getValue("UNALLOCATED_CAPACITY");
        	if(unallocat_cap!=null){
        		argpool.set("unallocated_capacity", Long.parseLong(unallocat_cap)/1024/1024);}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		argpool.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
        if("RES_PORT".equals(qName)){
        	argport = new DataRow();
        	String linkStatus= attrs.getValue("LINK_STATUS");
        	if(linkStatus!=null){
        		argport.set("link_status",linkStatus);}
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		argport.set("name", name1);}
        	String netAddress=attrs.getValue("NETWORK_ADDRESS");
        	if(netAddress!=null){
        		argport.set("network_address", netAddress);}
        	String argportSpeed=attrs.getValue("PORT_SPEED");
        	if(argportSpeed!=null){
        		argport.set("port_speed", argportSpeed);}
        	String subSystemName=attrs.getValue("SUBSYSTEM_NAME");
        	if(subSystemName!=null){
        		argport.set("subsystem_name", subSystemName);}
        	String Type1=attrs.getValue("TYPE");
        	if(Type1!=null && Type1.length()>0){
        		argport.set("type", Type1);
        	}else{
        		argport.set("type", "FC");
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		argport.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
        if("RES_SP".equals(qName)){
        	argnode = new DataRow();
        	if(attrs.getValue("SP_NAME")!=null && attrs.getValue("SP_NAME").length()>0){
        		argnode.set("sp_name", attrs.getValue("SP_NAME"));
        	}
        	if(attrs.getValue("EMC_ARTWORK_REVISION")!=null && attrs.getValue("EMC_ARTWORK_REVISION").length()>0){
        		argnode.set("emc_artwork_revision", attrs.getValue("EMC_ARTWORK_REVISION"));
        	}
        	if(attrs.getValue("EMC_ASSEMBLY_REVISION")!=null && attrs.getValue("EMC_ASSEMBLY_REVISION").length()>0){
        		argnode.set("emc_assembly_revision", attrs.getValue("EMC_ASSEMBLY_REVISION"));
        	}
        	if(attrs.getValue("EMC_PART_NUMBER")!=null && attrs.getValue("EMC_PART_NUMBER").length()>0){
        		argnode.set("emc_part_number", attrs.getValue("EMC_PART_NUMBER"));
        	}
        	if(attrs.getValue("EMC_SERIAL_NUMBER")!=null && attrs.getValue("EMC_SERIAL_NUMBER").length()>0){
        		argnode.set("emc_serial_number", attrs.getValue("EMC_SERIAL_NUMBER"));
        	}
        	if(attrs.getValue("PROGRAMMABLE_NAME")!=null && attrs.getValue("PROGRAMMABLE_NAME").length()>0){
        		argnode.set("programmable_name", attrs.getValue("PROGRAMMABLE_NAME"));
        	}
        	if(attrs.getValue("PROGRAMMABLE_REVISION")!=null && attrs.getValue("PROGRAMMABLE_REVISION").length()>0){
        		argnode.set("programmable_revision", attrs.getValue("PROGRAMMABLE_REVISION"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		argnode.set("update_timestamp",SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
        if("RES_STORAGE_DDM".equals(qName)){
        	argddm = new DataRow();
        	argddm.set("ddm_cap", Integer.parseInt(attrs.getValue("DDM_CAP")));
        	argddm.set("ddm_speed", attrs.getValue("DDM_SPEED"));
        	argddm.set("display_name", attrs.getValue("DISPLAY_NAME"));
        	argddm.set("name", attrs.getValue("NAME"));
        	argddm.set("subsystem_name", attrs.getValue("SUBSYSTEM_NAME"));
        	argddm.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	argddm.set("ddm_type", attrs.getValue("DDM_TYPE"));
        	argddm.set("ddm_rpm", attrs.getValue("DDM_RPM"));
        }
        if("RES_VOLUME".equals(qName)){
        	argvolume = new DataRow();
        	String logiCapacity=attrs.getValue("LOGICAL_CAPACITY");
        	if(logiCapacity!=null){
        		argvolume.set("logical_capacity", Long.parseLong(logiCapacity)/1024/1024);}
        	String phyCapacity=attrs.getValue("PHYSICAL_CAPACITY");
        	if(phyCapacity!=null){
        		argvolume.set("physical_capacity", Long.parseLong(phyCapacity)/1024/1024);}
        	String displayName=attrs.getValue("DISPLAY_NAME");
        	if(displayName!=null){
        		argvolume.set("display_name",displayName);}
        	String name1=attrs.getValue("NAME");
        	if(name1!=null){
        		argvolume.set("name",name1);}
        	String poolName=attrs.getValue("POOL_NAME");
        	if(poolName!=null){
        		argvolume.set("pool_name",poolName);}
        	String raidLevel=attrs.getValue("RAID_LEVEL");
        	if(raidLevel!=null){
        		argvolume.set("raid_level", raidLevel);}
        	String isMeta = attrs.getValue("IS_META");
        	if(isMeta!=null && isMeta.length()>0){
        		argvolume.set("is_meta", isMeta);
        	}
        	String isThin = attrs.getValue("IS_THIN");
        	if(isThin!=null && isThin.length()>0){
        		argvolume.set("is_thin", isThin);
        	}
        	if(attrs.getValue("CURRENT_OWNER")!=null && attrs.getValue("CURRENT_OWNER").length()>0){
        		argvolume.set("current_owner", attrs.getValue("CURRENT_OWNER"));
        	}
        	if(attrs.getValue("DEFUALT_OWNER")!=null && attrs.getValue("DEFUALT_OWNER").length()>0){
        		argvolume.set("default_owner", attrs.getValue("DEFUALT_OWNER"));
        	}
        	if(attrs.getValue("UPDATE_TIMESTAMP")!=null && attrs.getValue("UPDATE_TIMESTAMP").length()>0){
        		argvolume.set("update_timestamp", SrContant.getTime(attrs.getValue("UPDATE_TIMESTAMP")));
        	}
        }
        
	}
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		if ("RES_STORAGESUBSYSTEM".equals(qName)) {
			subsystem.add(argsub);
		}
        if("MAP_DISKGROUP2STORAGEDDM".equals(qName)){
        	diskgroupAndDDM.add(argdiskAndDDM);
        }
        if("MAP_HBA_STORAGEPORT".equals(qName)){
        	portAndHba.add(argportAndHba);
        }
        if("MAP_HOSTGROUPHBA".equals(qName)){
        	hostgroupAndHba.add(arghostAndHba);
        }
        if("MAP_HOSTGROUPVOLUME".equals(qName)){
        	hostgroupAndVolume.add(arghostAndVolume);
        }
        if("MAP_STORAGEPOOL2DISKGROUP".equals(qName)){
        	poolAndDiskgroup.add(argpoolAndDisks);
        }
        if("MAP_STORAGEPOOL2STORAGEVOLUME".equals(qName)){
        	poolAndVolume.add(argpoolAndVolume);
        }
        if("RES_DISKGROUP".equals(qName)){
        	diskgroups.add(argdiskgroup);
        }
        if("RES_HBA".equals(qName)){
        	hbas.add(arghba);
        }
        if("RES_HOSTGROUP".equals(qName)){
        	hostgroups.add(arghostgroup);
        }
        if("RES_POOL".equals(qName)){
        	pool.add(argpool);
        }
        if("RES_PORT".equals(qName)){
        	ports.add(argport);
        }
        if("RES_SP".equals(qName)){
        	storageNodes.add(argnode);
        }
        if("RES_STORAGE_DDM".equals(qName)){
        	ddms.add(argddm);
        }
        if("RES_VOLUME".equals(qName)){
        	volumes.add(argvolume);
        }
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}
	
	public List<DataRow> getSubsystem() {
		return subsystem;
	}
	public void setSubsystem(List<DataRow> subsystem) {
		this.subsystem = subsystem;
	}
	public List<DataRow> getDiskgroupAndDDM() {
		return diskgroupAndDDM;
	}
	public void setDiskgroupAndDDM(List<DataRow> diskgroupAndDDM) {
		this.diskgroupAndDDM = diskgroupAndDDM;
	}
	public List<DataRow> getPortAndHba() {
		return portAndHba;
	}
	public void setPortAndHba(List<DataRow> portAndHba) {
		this.portAndHba = portAndHba;
	}
	public List<DataRow> getHostgroupAndHba() {
		return hostgroupAndHba;
	}
	public void setHostgroupAndHba(List<DataRow> hostgroupAndHba) {
		this.hostgroupAndHba = hostgroupAndHba;
	}
	public List<DataRow> getHostgroupAndVolume() {
		return hostgroupAndVolume;
	}
	public void setHostgroupAndVolume(List<DataRow> hostgroupAndVolume) {
		this.hostgroupAndVolume = hostgroupAndVolume;
	}
	public List<DataRow> getPoolAndDiskgroup() {
		return poolAndDiskgroup;
	}
	public void setPoolAndDiskgroup(List<DataRow> poolAndDiskgroup) {
		this.poolAndDiskgroup = poolAndDiskgroup;
	}
	public List<DataRow> getPoolAndVolume() {
		return poolAndVolume;
	}
	public void setPoolAndVolume(List<DataRow> poolAndVolume) {
		this.poolAndVolume = poolAndVolume;
	}
	public List<DataRow> getDiskgroups() {
		return diskgroups;
	}
	public void setDiskgroups(List<DataRow> diskgroups) {
		this.diskgroups = diskgroups;
	}
	public List<DataRow> getHbas() {
		return hbas;
	}
	public void setHbas(List<DataRow> hbas) {
		this.hbas = hbas;
	}
	public List<DataRow> getHostgroups() {
		return hostgroups;
	}
	public void setHostgroups(List<DataRow> hostgroups) {
		this.hostgroups = hostgroups;
	}
	public List<DataRow> getPool() {
		return pool;
	}
	public void setPool(List<DataRow> pool) {
		this.pool = pool;
	}
	public List<DataRow> getPorts() {
		return ports;
	}
	public void setPorts(List<DataRow> ports) {
		this.ports = ports;
	}
	public List<DataRow> getStorageNodes() {
		return storageNodes;
	}
	public void setStorageNodes(List<DataRow> storageNodes) {
		this.storageNodes = storageNodes;
	}
	public List<DataRow> getDdms() {
		return ddms;
	}
	public void setDdms(List<DataRow> ddms) {
		this.ddms = ddms;
	}
	public List<DataRow> getVolumes() {
		return volumes;
	}
	public void setVolumes(List<DataRow> volumes) {
		this.volumes = volumes;
	}
}
