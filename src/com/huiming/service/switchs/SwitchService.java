package com.huiming.service.switchs;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class SwitchService extends BaseService{
	public DBPage getSwitchPage(int curPage,int numPerPage,String name,String ipAddress,String status,String serialNumber){
		String sql="select s.*,m.model_name,v.vendor_name,f.the_display_name as fabric_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v ,v_res_fabric f " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id and s.the_fabric_id = f.fabric_id";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and s.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if(status!=null && status.length()>0){
			sb.append("and s.the_propagated_status = ? ");
			args.add(status);
		}
		if(serialNumber!=null && serialNumber.length()>0){
			sb.append("and s.serial_number = ? ");
			args.add(serialNumber);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList(String name,String ipAddress,String status,String serialNumber){
		String sql="select s.*,m.model_name,v.vendor_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and s.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(ipAddress!=null && ipAddress.length()>0){
			sb.append("and s.ip_address = ? ");
			args.add(ipAddress);
		}
		if(status!=null && status.length()>0){
			sb.append("and s.the_propagated_status = ? ");
			args.add(status);
		}
		if(serialNumber!=null && serialNumber.length()>0){
			sb.append("and s.serial_number = ? ");
			args.add(serialNumber);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),args.toArray());
	}
	
	public DataRow getSwitchInfo(Integer switchId){
		String sql="select s.*,m.model_name,v.vendor_name,f.the_display_name as fabric_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v ,v_res_fabric f " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id and s.the_fabric_id = f.fabric_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(switchId!=0 && switchId>0){
			sb.append("and s.switch_id = ? ");
			args.add(switchId);
		}
		return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sb.toString(),args.toArray());
	}
	
	public DataRow getSwitchStatus(Integer switchId){
		String sql="SELECT MAX(id),engine_status,power_status,port_status,fiber_status," +
				" switch_id FROM t_res_switch WHERE switch_id=?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{switchId});
	}

	public List<DataRow> getdevInfo() {
		String sql="select s.switch_id as id,s.the_display_name as name from v_res_switch s,v_res_model m,v_res_vendor v ,v_res_fabric f " +
		"where s.model_id = m.model_id and s.vendor_id = v.vendor_id and s.the_fabric_id = f.fabric_id";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	public  List<DataRow> getResSwitchList(){
		String sql="SELECT * FROM t_res_switch where 1=1";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	public DataRow getResSwitchInfo(Integer switchId){
		String sql="SELECT * FROM t_res_switch where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(switchId!=0 && switchId>0){
			sb.append("and switch_id = ? ");
			args.add(switchId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
}
