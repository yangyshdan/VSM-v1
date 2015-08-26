package com.huiming.sr.constants;

/**
 * 该类主要用于声明与数据表列名对应的常量
 * @author Administrator
 *
 */
public class SrTblColConstant {
	//For view suffix
	public static final String VIEW_SUFFIX_DAILY = "_DAILY";
	public static final String VIEW_SUFFIX_HOURLY = "_HOURLY";
	
	//For t_res_storagesubsystem
	public static final String TBL_RES_STORAGESUBSYSTEM = "t_res_storagesubsystem";
	public static final String REF_SUBSYSTEM_ID = "subsystem_id";
	public static final String RSS_SUBSYSTEM_ID = "subsystem_id";
	public static final String RSS_SUBSYSTEM_NAME = "name";
	public static final String RSS_DISPLAY_NAME = "display_name";
	public static final String RSS_STORAGE_TYPE = "storage_type";
	
	//For t_res_diskgroup
	public static final String TBL_RES_DISKGROUP = "t_res_diskgroup";
	public static final String RDG_DISKGROUP_ID = "diskgroup_id";
	public static final String RDG_DISKGROUP_NAME = "name";
	public static final String RDG_DISPLAY_NAME = "display_name";
	
	//For t_res_hostgroup
	public static final String TBL_RES_HOSTGROUP = "t_res_hostgroup";
	public static final String RHG_HOSTGROUP_ID = "hostgroup_id";
	public static final String RHG_HOSTGROUP_NAME = "hostgroup_name";
	
	//For t_res_port
	public static final String TBL_RES_PORT = "t_res_port";
	public static final String RP_PORT_ID = "port_id";
	public static final String RP_NAME = "name";
	
	//For t_res_storagenode
	public static final String TBL_RES_STORAGENODE = "t_res_storagenode";
	public static final String RSN_SP_ID = "sp_id";
	public static final String RSN_SP_NAME = "sp_name";
	
	//For t_res_storagevolume
	public static final String TBL_RES_STORAGEVOLUME = "t_res_storagevolume";
	public static final String RSV_VOLUME_ID = "volume_id";
	public static final String RSV_NAME = "name";
	
	//For t_prf_timestamp
	public static final String TT_SUBSYSTEM_ID = "subsystem_id";
	public static final String TT_TIME_ID = "time_id";
	public static final String TT_SUBSYSTEM_NAME = "subsystem_name";
	public static final String TT_SAMPLE_TIME = "sample_time";
	public static final String TT_INTERVAL_LEN = "interval_len";
	public static final String TT_SUMM_TYPE = "summ_type";
	public static final String TT_DEVICE_TYPE = "device_type";
	
	//For t_prf_diskgroup
	public static final String DG_DISKGROUP_NAME = "diskgroup_name";
	public static final String DG_BCK_READ_IO = "bck_read_io";
	public static final String DG_BCK_WRITE_IO = "bck_write_io";
	public static final String DG_BCK_READ_KB = "bck_read_kb";
	public static final String DG_BCK_WRITE_KB = "bck_write_kb";
	public static final String DG_BCK_READ_TIME = "bck_read_time";
	public static final String DG_BCK_WRITE_TIME = "bck_write_time";
	public static final String DG_IO_TYPE = "io_type";
	
	//For t_prf_storagenode
	public static final String SN_SP_NAME = "sp_name";
	public static final String SN_READ_IO = "read_io";
	public static final String SN_WRITE_IO = "write_io";
	public static final String SN_READ_KB = "read_kb";
	public static final String SN_WRITE_KB = "write_kb";
	public static final String SN_READ_HIT_IO = "read_hit_io";
	public static final String SN_WRITE_HIT_IO = "write_hit_io";
	public static final String SN_READ_IO_TIME = "read_io_time";
	public static final String SN_WIRTE_IO_TIME = "wirte_io_time";
	
	//For t_prf_port
	public static final String P_PORT_NAME = "port_name";
	public static final String P_SEND_IO = "send_io";
	public static final String P_RECV_IO = "recv_io";
	public static final String P_SEND_KB = "send_kb";
	public static final String P_RECV_KB = "recv_kb";
	
	//For t_prf_storagevolume
	public static final String SV_VOLUME_NAME = "volume_name";
	public static final String SV_READ_IO = "read_io";
	public static final String SV_WRITE_IO = "write_io";
	public static final String SV_READ_HIT_IO = "read_hit_io";
	public static final String SV_WRITE_HIT_IO = "write_hit_io";
	public static final String SV_READ_KB = "read_kb";
	public static final String SV_WRITE_KB = "write_kb";
	public static final String SV_READ_IO_TIME = "read_io_time";
	public static final String SV_WRITE_IO_TIME = "write_io_time";
	
	//For tnprffields
	public static final String PF_STORAGE_TYPE = "fstoragetype";
	public static final String PF_DBTYPE = "fdbtype";
	public static final String PF_VIEW = "fprfview";

}
