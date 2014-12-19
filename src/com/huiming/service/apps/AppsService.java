package com.huiming.service.apps;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class AppsService extends BaseService{
	public DBPage getPage(int curPage,int numPerPage,String name){
		StringBuffer sql = new StringBuffer("select a.fid,a.fname as name ,count(b.fappid) as vcount from tnapps a inner join t_app_mapping b on a.fid = b.fappid where 1=1 ");
		List<Object> args = new ArrayList<Object>();
		if(StringHelper.isNotEmpty(name)){
			sql.append(" and name like ? ");
			args.add("%"+name+"%");
		}
		sql.append(" group by fid  order by fid ");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(), curPage, numPerPage);
	}
	public DBPage getAppsVirtual(int curPage,int numPerPage,Integer fappid){
		StringBuffer sql = new StringBuffer("SELECT v.vm_id,v.hypervisor_id,v.targeted_os,v.assigned_cpu_number,v.assigned_cpu_processunit,v.maximum_cpu_number,v.maximum_cpu_processunit," +
				"v.minimum_cpu_number,v.minimum_cpu_processunit,v.total_memory,v.update_timestamp,v.host_name,c.computer_id," +
				"COALESCE(c.display_name,v.name) AS display_name,c.disk_space,c.disk_available_space,c.ip_address FROM t_res_virtualmachine v,t_res_computersystem c,t_app_mapping m " +
				"WHERE c.computer_id=v.computer_id AND v.vm_id=m.fvirtualid");
		List<Object> args = new ArrayList<Object>();
		if(fappid!=null&&fappid>0){
			sql.append(" and m.fappid = ? ");
			args.add(fappid);
		}
		sql.append(" order by v.update_timestamp desc ");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(),args.toArray(), curPage, numPerPage);
	}
	
	public DataRow getAppInfo(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnapps where fid = ?" ,new Object[]{id});
	}
	
	public List<DataRow> getMappingHV(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(" select distinct(a.hypervisor_id) as hv_id from t_res_virtualmachine a where vm_id in (select fvirtualid from t_app_mapping where fappid = ?)",new Object[]{id});
	}
	
	public List<DataRow> getMappingVM(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select * from t_app_mapping where fappid = ?",new Object[]{id});
	}
	public List<DataRow> getAppList(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select * from tnapps");
	}
	
	public int aotuMapping(int vmId,String vmName){
		String regEx = "^([a-z0-9A-Z]+)_([a-z0-9A-Z]+)_([\\w]*)$";
		Matcher m = Pattern.compile(regEx).matcher(vmName);
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			if(m.find()){
				String appName = m.group(2);
				//1 ，插入app名称
				int id = session.queryInt("select fid from tnapps where fname = ?", new Object[]{appName});
				if(id == 0){
					DataRow app = new DataRow();
					app.set("fname", appName);
					id = Integer.parseInt(session.insert("tnapps", app));
				}
				//2 ，更新mapping
				int mid = session.queryInt("select fid from t_app_mapping where fappid = ? and fvirtualid = ?", new Object[]{id,vmId});
				if(mid == 0){
					DataRow mapping = new DataRow();
					mapping.set("fappid", id);
					mapping.set("fvirtualid", vmId);
					session.insert("t_app_mapping", mapping);
				}
			}
			session.commitTrans();
		}  catch (Exception e) {
			
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}
		finally
		{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		
		return flag;
	}
	
	public int updateMapping(int id,DataRow app,String vm){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			//1 更新app名称
			if(id > 0){
				session.update("tnapps", app, "fid", id);
			}else{
				id = Integer.parseInt(session.insert("tnapps", app));
			}
			//2 更新mapping
			String[] virtual = vm.split(",");
			session.delete("t_app_mapping", "fappid", id);
			session.update(" update t_res_computersystem set display_name = null where computer_id in ( select a.computer_id from  t_app_mapping b  inner join t_res_virtualmachine a on a.vm_id = b.fvirtualid where b.fappid = ? )" ,new Object[]{id});
			for (int i = 0; i < virtual.length; i++) {
				DataRow tar = new DataRow();
				tar.set("fappid", id);
				tar.set("fvirtualid", Integer.parseInt(virtual[i]));
				session.insert("t_app_mapping", tar);
			}
			session.commitTrans();
		}  catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}
		finally
		{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		
		return flag;
	}
	
	public int deleteApp(int id){
		Session session = null;
		int flag = 0 ;
		try {
			session = getSession(WebConstants.DB_DEFAULT);
			session.beginTrans();
			//删除mapping
			session.delete("t_app_mapping", "fappid", id);
			//删除性能
			
			//删除app
			session.delete("tnapps", "fid", id);
			session.commitTrans();
		}  catch (Exception e) {
			e.printStackTrace();
			flag = -1;
			if (session != null)
			{
				session.rollbackTrans();
			}
		}
		finally
		{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
		
		return flag;
	}
	
	/**
	 * 得到APP下的虚拟机信息
	 * @param appId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getMappingVirtual(Integer appId){
		String sql="SELECT v.vm_id,v.name,v.hypervisor_id,v.targeted_os,v.assigned_cpu_number,v.assigned_cpu_processunit,v.maximum_cpu_number,v.maximum_cpu_processunit," +
				"v.minimum_cpu_number,v.minimum_cpu_processunit,v.total_memory,DATE_FORMAT(c.update_timestamp,'%Y-%c-%d %H:%m:%s') AS update_timestamp,v.host_name," +
				"c.computer_id,COALESCE(c.display_name,v.name) AS the_display_name,c.disk_space,c.disk_available_space,c.operational_status,c.ip_address " +
				"FROM t_res_virtualmachine v,t_res_computersystem c,t_app_mapping t WHERE c.computer_id=v.computer_id AND t.fvirtualid = v.VM_ID and t.fappid = ? ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{appId});
	}
	
	public static void main(String[] args) {
		String str = "^([a-z0-9A-Z]+)_([a-z0-9A-Z]+)_([\\w]*)$";
		String test = "s_W_";
		Matcher m = Pattern.compile(str).matcher(test);
		if(m.find()){
			System.out.println(m.group(2));
		}
	}
	
	public List<DataRow> getAsset(){
		String sql = "select 'hv' as SystemType, count(hypervisor_id) as counts from t_res_hypervisor " +
							" union select 'vm' as SystemType, count(vm_id) as counts from t_res_virtualmachine " +
							 " union select 'app' as SystemType, count(fid) as counts from tnapps";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
}
