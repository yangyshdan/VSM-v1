package com.huiming.service.topo;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class TopoService extends BaseService {
	public List<DataRow> getSwitchList(){
		String sql	= "select switch_id,logical_name from  TPC.T_RES_SWITCH";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	public List<DataRow> getStorageList(){
		String sql= "select subsystem_id,display_name from  TPC.T_RES_STORAGE_SUBSYSTEM";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	public List<DataRow> getServerList(){
		String sql="select server_id,server_name from TPC.T_RES_SERVER where SERVER_TYPE IN (0, 1)";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	//switch和storage
	public List<DataRow> getSwitchandStorageList(){
		String sql	= " select distinct S.SWITCH_ID, P.SUBSYSTEM_ID"
			+ " from TPC.T_RES_SWITCH2PORT SP"
			+ 	" inner join TPC.T_RES_PORT2PORT PP on PP.PORT_ID1 = SP.PORT_ID"
			+ 	" inner join TPC.T_RES_PORT P on P.PORT_ID = PP.PORT_ID2"
			+ 	" inner join TPC.T_RES_SWITCH S on S.SWITCH_WWN = SP.SWITCH_WWN"
			+ 	" inner join TPC.T_RES_STORAGE_SUBSYSTEM SS on SS.SUBSYSTEM_ID = P.SUBSYSTEM_ID";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		sb.append(" order by S.SWITCH_ID, P.SUBSYSTEM_ID ");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	//switch和host
	public List<DataRow> getSwitchandHostList(){
		String sql	= " select distinct SW.SWITCH_ID, SV.SERVER_ID, P.PORT_ID"
			+ " from TPC.T_RES_SWITCH2PORT SP"
			+ 	" inner join TPC.T_RES_PORT2PORT PP on PP.PORT_ID1 = SP.PORT_ID"
			+ 	" inner join TPC.T_RES_PORT P on P.PORT_ID = PP.PORT_ID2"
			+ 	" inner join TPC.T_RES_SWITCH SW on SW.SWITCH_WWN = SP.SWITCH_WWN"
			+ 	" inner join TPC.T_RES_SERVER SV on SV.SERVER_ID = P.DEVICE_ID"
			+ " where 1=1";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	//switch与switch
	public List<DataRow> getSwitchandSwitchList(){
		String sql	= " SELECT DISTINCT S1.SWITCH_ID AS SWITCH_ID1,S2.SWITCH_ID AS SWITCH_ID2"
			+ " FROM TPC.T_RES_PORT2PORT P"
			+ 	" INNER JOIN V_RES_SWITCH2PORT S1 ON S1.PORT_ID = P.PORT_ID1"
			+ 	" INNER JOIN V_RES_SWITCH2PORT S2 ON S2.PORT_ID = P.PORT_ID2";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
}
