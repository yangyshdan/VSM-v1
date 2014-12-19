package com.huiming.service.usercon;

import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class UserConService extends BaseService{
	public List<DataRow> getHMCUsList(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select * from t_acct_hmc");
	}
	
	public DBPage getHMCPage(String ipaddress,String state,int curPage,int numPerPage){
		String sql="select t.* from t_acct_hmc t where 1=1 ";
		if(ipaddress!=null && ipaddress.length()>0){
			sql+=" and t.ip_address = '"+ipaddress+"'";
		}
		if(state!=null && state.length()>0){
			sql+=" and t.state = "+state;
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql,curPage,numPerPage);
	}
	
	public List<DataRow> getVIOSUsList(){
		String sql="SELECT h.name AS c_name,v.name AS v_name,t.* FROM " +
				"t_acct_vios t,t_res_hypervisor h,t_res_virtualmachine v " +
				"WHERE h.HYPERVISOR_ID = t.HYPERVISOR_ID AND v.VM_ID = t.VM_ID";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public DBPage getVIOSUsPage(String vname,String state,int curPage,int numPerPage){
		String sql="SELECT h.name AS c_name,v.name AS v_name,t.* FROM " +
		"t_acct_vios t,t_res_hypervisor h,t_res_virtualmachine v " +
		"WHERE h.HYPERVISOR_ID = t.HYPERVISOR_ID AND v.VM_ID = t.VM_ID ";
		if(vname!=null && vname.length() >0){
			sql+="and v.vm_id like '%"+vname+"%'";
		}
		if(state!=null && state.length()>0){
			sql+=" and t.state = "+state;
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql, curPage, numPerPage);
	}
	
	public DataRow getVIOS(String id){
		String sql="SELECT h.name AS c_name,v.name AS v_name,t.* FROM " +
		"t_acct_vios t,t_res_hypervisor h,t_res_virtualmachine v " +
		"WHERE h.HYPERVISOR_ID = t.HYPERVISOR_ID AND v.VM_ID = t.VM_ID ";
		if(id!=null && id.length()>0){
			sql+="and t.id = "+id;
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql);
	}
	
	public DataRow getVirtualInfo(String id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select c.ip_address from t_res_virtualmachine t,t_res_computersystem c where t.computer_id = c.computer_id and t.vm_id = "+id);
	}
	
	public void hmcDel(String id){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_acct_hmc", "id", id);
	}
	
	public void viosDel(String id){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_acct_vios", "id", id);
	}
	
	public DataRow getHMCInfo(String id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_acct_hmc where id = "+id);
	}
	
	public DataRow getVIOSInfo(String id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_acct_vios where id = "+id);
	}
	
	public void addHMCInfo(DataRow row){
		String hmcId = row.getString("id");
		row.remove("id");
		if(hmcId!=null && hmcId.length()>0){
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_acct_hmc", row, "id", hmcId);
		}else{
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_acct_hmc", row);
		}
	}
	
	public boolean hasHMCInfo(String user,String ipaddress){
		DataRow row = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select id from t_acct_hmc where user = '"+user+"' and ip_address = '"+ipaddress+"'");
		if(row!=null && row.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public void addVIOSInfo(DataRow row){
		String id = row.getString("id");
		row.remove("id");
		if(id!=null && id.length()>0){
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_acct_vios", row, "id", id);
		}else {
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_acct_vios", row);
		}
	}
	
	public List<DataRow> gethypervisorInfo(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select hypervisor_id as ele_id ,name as ele_name from t_res_hypervisor");
	}
	
	public List<DataRow> getvirtualList(String id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select vm_id as ele_id,name as ele_name from t_res_virtualmachine where hypervisor_id = "+id);
	}
	
	public List<DataRow> getviosList(String id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select vm_id as ele_id from t_acct_vios where hypervisor_id = "+id);
	}
}
