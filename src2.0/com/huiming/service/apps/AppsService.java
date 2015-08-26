package com.huiming.service.apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.service.topo.SemiautoTopoService;
import com.huiming.service.topo.SemiautoTopoService.IDeviceIdHandler;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class AppsService extends BaseService{
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryDevicesByAppid1(Long appId, JdbcTemplate tpc, JdbcTemplate srDB){
		Map<String, Object> json = new HashMap<String, Object> (10);
		List<DataRow> appData = srDB.query("SELECT devtype,devid FROM t_nodes_info WHERE appid=" + appId);
		if(appData != null && appData.size() > 0){
			long devid;
			String devtype;
			Map<String, StringBuilder> ids = new HashMap<String, StringBuilder> (10);
			for(DataRow dr : appData){
				devid = dr.getLong("devid");
				devtype = dr.getString("devtype");
				if(!ids.containsKey(devtype)){
					ids.put(devtype, new StringBuilder(30));
				}
				ids.get(devtype).append(devid + ",");
			}
			StringBuilder devids;
			List<DataRow> data;
			for(String dtype : ids.keySet()){
				devids = ids.get(dtype);
				if(devids.length() - 1 > -1){
					devids.deleteCharAt(devids.length() - 1);
				}
				else { continue; }
				if(SrContant.SUBDEVTYPE_SWITCH.equalsIgnoreCase(dtype)){
					data = tpc.query("select switch_id as sw_id,the_display_name as sw_name from v_res_switch where switch_id in(&) order by sw_id asc".replace("&", devids));
					if(data != null && data.size() > 0){
						json.put("sw", data);
						json.put("swSize", data.size());
					}
					else {
						json.put("swSize", 0);
					}
				}
				else if(SrContant.SUBDEVTYPE_HYPERVISOR.equalsIgnoreCase(dtype)){
					data = srDB.query("SELECT HYPERVISOR_ID AS hyp_id,HOST_COMPUTER_ID AS comp_id,NAME as hyp_name FROM t_res_hypervisor WHERE HYPERVISOR_ID IN(&) ORDER BY hyp_id".replace("&", devids));
					if(data != null && data.size() > 0){
						json.put("hyp", data);
						json.put("hypSize", data.size());
					}
					else {
						json.put("hypSize", 0);
					}
				}
				else if(SrContant.SUBDEVTYPE_VM.equalsIgnoreCase(dtype)){
					data = srDB.query("SELECT vm_id,COMPUTER_ID AS comp_id,HYPERVISOR_ID AS hyp_id,NAME as vm_name FROM t_res_virtualmachine WHERE vm_id IN(&) ORDER BY vm_id".replace("&", devids));
					if(data != null && data.size() > 0){
						json.put("vm", data);
						json.put("vmSize", data.size());
					}
					else {
						json.put("vmSize", 0);
					}
				}
				else if(SrContant.SUBDEVTYPE_STORAGE.equalsIgnoreCase(dtype)){
					data = tpc.query("select subsystem_id as sto_id,the_display_name as sto_name from v_res_storage_subsystem where subsystem_id in(&) order by subsystem_id".replace("&", devids));
					if(data != null && data.size() > 0){
						json.put("sto", data);
						json.put("stoSize", data.size());
					}
					else {
						json.put("stoSize", 0);
					}
				}
			}
		}
		return json;
	}
	
	/**
	 * @see 
	 * @param curPage
	 * @param numPerPage
	 * @param userId 根据userId可以获取用户创建的
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DBPage getAppPage(int curPage, int numPerPage, String name, long userId, String role){
		if(Configure.getInstance().getDataSource(WebConstants.DB_TPC) == null){
			return null;
		}
		JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
		DBPage page = null;
		String sql = "SELECT a.* FROM t_app a where 1=1 ";
		List<Object> args = new ArrayList<Object>();
		if(StringHelper.isNotEmpty(name)){
			sql += " and a.name like ? ";
			args.add("%"+name+"%");
		}
		if(!SrContant.ROLE_SUPER.equalsIgnoreCase(role)){
			// 如果不是超级管理员，那么就过滤一下
			sql += " and a.user_id=" + userId;
		}
		
		sql += " ORDER BY a.id";
		// 把应用查出来
		page = srDB.queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
		if(page != null){
			List<DataRow> data = page.getData();
			if(data != null && data.size() > 0){
				SemiautoTopoService sa = new SemiautoTopoService();
//				ptype,type,pid,id,dbt
				String mapDevSQL = "SELECT m.app_id,m.parent_device_type as ptype,m.parent_device_id as pid," +
					"m.device_type as type,m.device_id as id,m.db_type as dbt FROM t_map_devices m WHERE m.app_id IN(";
				
				long app_id;
				JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
				// asso = {41: { vm: {vm_id:12, name: 'vm12'}, pool: {} }};
				IDeviceIdHandler h = new IDeviceIdHandler(){
					public DataRow handler(JdbcTemplate srDB, JdbcTemplate tpc, String devType, long devId, 
							String dbType) {
						DataRow data = null;
						if(SrContant.SUBDEVTYPE_VIRTUAL.equalsIgnoreCase(devType)){
							data = srDB.queryMap("SELECT vm_id,COMPUTER_ID AS comp_id,HYPERVISOR_ID AS hyp_id,NAME AS vm_name FROM t_res_virtualmachine WHERE vm_id=" + devId);
						}
						else if(SrContant.SUBDEVTYPE_PHYSICAL.equalsIgnoreCase(devType)){
							data = srDB.queryMap("SELECT HOST_COMPUTER_ID AS comp_id,HYPERVISOR_ID AS hyp_id,NAME AS hyp_name FROM t_res_hypervisor WHERE HYPERVISOR_ID=" + devId);
						}
						else if(SrContant.SUBDEVTYPE_SWITCH.equalsIgnoreCase(devType)){
							data = tpc.queryMap("select the_display_name as sw_name,switch_id as sw_id from v_res_switch where switch_id=" + devId);
						}
						else if(SrContant.SUBDEVTYPE_STORAGE.equalsIgnoreCase(devType)){
							if(SrContant.DBTYPE_SR.equalsIgnoreCase(dbType)){
								data = srDB.queryMap("SELECT COALESCE(DISPLAY_NAME,NAME) AS sto_name,subsystem_id AS sto_id,storage_type AS os_type FROM t_res_storagesubsystem WHERE subsystem_id=" + devId);
							}
							else {
								data = tpc.queryMap("select the_display_name as sto_name,subsystem_id as sto_id,os_type from v_res_storage_subsystem where subsystem_id=" + devId);
							}
						}
						else if(SrContant.SUBDEVTYPE_POOL.equalsIgnoreCase(devType)){
							if(SrContant.DBTYPE_SR.equalsIgnoreCase(dbType)){
								data = srDB.queryMap("SELECT p.pool_id,p.subsystem_id AS sto_id,COALESCE(p.DISPLAY_NAME,p.NAME) AS pool_name,s.STORAGE_TYPE AS os_type " +
										" FROM t_res_storagepool p JOIN t_res_storagesubsystem s ON s.subsystem_id=p.subsystem_id AND pool_id=" + devId);
							}
							else {
								data = tpc.queryMap("select p.the_display_name as pool_name,p.pool_id,p.subsystem_id as sto_id from v_res_storage_pool p where p.pool_id=" + devId);
							}
						}
						else if(SrContant.SUBDEVTYPE_VOLUME.equalsIgnoreCase(devType)){
							if(SrContant.DBTYPE_SR.equalsIgnoreCase(dbType)){
								data = srDB.queryMap("SELECT v.VOLUME_ID AS vol_id,v.subsystem_id AS sto_id,COALESCE(v.DISPLAY_NAME,v.NAME) AS vol_name,s.STORAGE_TYPE AS os_type " +
										" FROM t_res_storagevolume v JOIN t_res_storagesubsystem s ON s.subsystem_id=v.subsystem_id AND v.volume_id=" + devId);
							}
							else {
								data = tpc.queryMap("select v.the_display_name as vol_name,v.svid as vol_id,v.subsystem_id as sto_id from v_res_storage_volume v where v.svid=" + devId);
							}
						}
						if(data != null){ data.put("db_type", dbType); }
						return data;
					}
				};
				List<DataRow> appData = null;
				for(int i = 0, size = data.size(); i < size; ++i){
					app_id = data.get(i).getLong("id");
					appData = srDB.query(mapDevSQL + app_id + ")");
					Map<String, Map<String, DataRow>> devtype_devId_devData = sa.getDeviceIds2(srDB, tpc, appData, h);
					if(devtype_devId_devData != null && devtype_devId_devData.size() > 0){
						Map<String, Integer> count = new HashMap<String, Integer>(devtype_devId_devData.size());
						for(String devType : devtype_devId_devData.keySet()){
							count.put(devType, devtype_devId_devData.get(devType).size());
						}
						if(!count.containsKey(SrContant.SUBDEVTYPE_VIRTUAL)){
							count.put(SrContant.SUBDEVTYPE_VIRTUAL, 0);
						}
						data.get(i).put("asso", devtype_devId_devData);
						data.get(i).put("count", count);
					}
				}
			}
		}
//		Logger.getLogger(getClass()).info(JSON.toJSONStringWithDateFormat(page, "yyyy-MM-dd HH:mm:ss"));
		return page;
	}
	
	@SuppressWarnings("unchecked")
	public DBPage getPage(int curPage, int numPerPage, String name){
		JdbcTemplate jdbc = getJdbcTemplate(WebConstants.DB_DEFAULT);
//		StringBuilder sql = new StringBuilder("select a.*,count(b.app_id) as vcount from t_app a inner join t_app_server b on a.id = b.app_id where 1=1 ");
		String sql = "SELECT * FROM t_app ";
		List<Object> args = new ArrayList<Object>();
		if(StringHelper.isNotEmpty(name)){
			sql += " and name like ? ";
			args.add("%"+name+"%");
		}
		sql += " ORDER BY id";
		// 把应用、应用的所在服务器、关联服务器的数量查出来
		DBPage page = jdbc.queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
		if(page != null){
			List<DataRow> data = page.getData();
			if(data != null && data.size() > 0){
				long app_id;
				JdbcTemplate tpc = getJdbcTemplate(WebConstants.DB_TPC);
				JdbcTemplate srDB = getJdbcTemplate(WebConstants.DB_DEFAULT);
				for(int i = 0, size = data.size(); i < size; ++i){
					app_id = data.get(i).getLong("id");
					data.get(i).put("associations", queryDevicesByAppid1(app_id, tpc, srDB));
				}
			}
		}
//		Logger.getLogger(this.getClass()).info(JSON.toJSONStringWithDateFormat(page, "yyyy-MM-dd HH:mm:ss"));
		return page;
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
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_app where id = ?" ,new Object[]{id});
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getMappingComputer(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(" select distinct(server_id) as vm_id from t_app_server where app_id = ?",new Object[]{id});
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getMappingVM(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select * from t_app_mapping where fappid = ?",new Object[]{id});
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getAppList(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select * from t_app");
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
	
	public void deleteApp(long id){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_app", "id", id);
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

}
