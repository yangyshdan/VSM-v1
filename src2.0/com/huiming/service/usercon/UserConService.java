package com.huiming.service.usercon;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.project.web.WebConstants;

public class UserConService extends BaseService {
	
	public DataRow getVirtualInfo(String id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select c.ip_address from t_res_virtualmachine t,t_res_computersystem c where t.computer_id = c.computer_id and t.vm_id = "+id);
	}
	
	/**
	 * 修改虚拟机的IP地址
	 * @param vmRow
	 */
	public void updateVirtualInfo(DataRow vmRow) {
		String sql = "update t_res_computersystem set ip_address = ? where computer_id = (select computer_id from t_res_virtualmachine where vm_id = ? and hypervisor_id = ?)";
		String vmId = vmRow.getString("vm_id");
		String hyperId = vmRow.getString("hypervisor_id");
		if (vmId != null && vmId.length() > 0) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql, new Object[]{vmRow.getString("ip_address"),vmId,hyperId});
		}
	}
	
	/**
	 * 分页查询服务器配置信息
	 * @param row
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getServerPage(String name, String serverType, String state, int curPage, int numPerPage){
		StringBuffer sb = new StringBuffer("select * from t_server where 1 = 1");
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(name) && StringHelper.isNotBlank(name)) {
			sb.append(" and name like ?");
			args.add("%" + name + "%");
		}
		if (StringHelper.isNotEmpty(serverType) && StringHelper.isNotBlank(serverType)) {
			sb.append(" and toptype = ?");
			args.add(serverType);
		}
		if (StringHelper.isNotEmpty(state) && StringHelper.isNotBlank(state)) {
			sb.append(" and state = ?");
			args.add(state);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(),curPage,numPerPage);
	}
	
	/**
	 * 获取指定的配置信息列表
	 * @param ids
	 * @return
	 */
	public List<DataRow> getServerConfigList(String ids) {
		String sql = "select id,ip_address,user,password from t_server where 1 = 1";
		if (ids != null && ids.length() > 0) {
			sql = sql + " and id in (" + ids + ")";
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 验证指定的服务器配置信息是否存在
	 * @param ipAddress
	 * @param user
	 * @param serverType
	 * @return
	 */
	public boolean hasServerConfigInfo(String ipAddress,String user,String serverType) {
		String sql = "select count(*) from t_server where ip_address = ? and user = ? and toptype = ?";
		int result = getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt(sql, new Object[] {ipAddress,user,serverType});
		return (result > 0);
	}
	
	/**
	 * 保存服务器配置信息
	 * @param row
	 * @return
	 */
	public String saveServerConfigInfo(DataRow row) {
		String serverId = row.getString("id");
		row.remove("id");
		if (serverId != null && serverId.length() > 0) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_server", row, "id", serverId);
		} else {
			serverId = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_server", row);
		}
		return serverId;
	}
	
	/**
	 * 修改指定服务器配置的密码
	 * @param id
	 * @param password
	 */
	public void updateServerConfigPassword(String id,String password) {
		String sql = "update t_server set password = ? where id = ?";
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql, new Object[]{password,id});
	}
	
	/**
	 * 保存服务器和交换机的关联
	 * @param row
	 */
	public void saveHyperSwitchMap(List<DataRow> list) {
		if (list != null && list.size() > 0) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_hypervisor_switch", "hypervisor_id", list.get(0).getString("hypervisor_id"));
			for (int i = 0; i < list.size(); i++) {
				DataRow row = list.get(i);
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_hypervisor_switch", row);
			}
		}
	}
	
	/**
	 * 获取指定服务器配置信息
	 * @param id
	 * @return
	 */
	public DataRow getServerConfigInfo(String id) {
		String sql = "select * from t_server where id = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{id});
	}
	
	/**
	 * 获取服务器关联的交换机
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getServerSwitchMap(String id) {
		List<DataRow> list = new ArrayList<DataRow>();
		String sql = "select s.id,h.hypervisor_id,hs.switch_id from t_server s,t_res_hypervisor h,t_hypervisor_switch hs where s.id = h.hmc_id and s.id = ? and h.hypervisor_id = hs.hypervisor_id";
		list = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{id});
		if (list == null || list.size() == 0) {
			sql = "select s.id,h.hypervisor_id,hs.switch_id from t_server s,t_res_hypervisor h,t_res_computersystem cs,t_hypervisor_switch hs "
				+ "where s.id = ? and h.hypervisor_id = hs.hypervisor_id and h.host_computer_id = cs.computer_id "
				+ "and s.name = cs.name and s.ip_address = cs.ip_address";
			list = getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{id});
		}
		return list;
	}
	
	/**
	 * 通过配置ID查找相应的物理机信息
	 * @param String
	 * @return
	 */
	public DataRow getPhysicalInfoByConfigId(String phyConfigId) {
		DataRow dataRow = null;
		String sql = "SELECT * FROM t_res_hypervisor WHERE hmc_id = ?";
		dataRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{phyConfigId});
		if (dataRow == null) {
			sql = "select h.hypervisor_id from t_server s,t_res_computersystem cs,t_res_hypervisor h where s.id = ? "
				+ "and h.host_computer_id = cs.computer_id and s.name = cs.name and s.ip_address = cs.ip_address";
			dataRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{phyConfigId});
		}
		return dataRow;
	}
	
	/**
	 * 通过物理机的配置ID查找其下面的已配置的虚拟机配置信息
	 * @param phyConfigId
	 * @return
	 */
	public List<DataRow> getVirtConfigListUnderPhyscial(String phyConfigId) {
		String sql = "select vm.hmc_id,vm.vm_id,s.name,s.ip_address,s.user,s.password from t_server s,t_res_virtualmachine vm "
			+ "where vm.hypervisor_id = (select hypervisor_id from t_res_hypervisor where hmc_id = ?) "
			+ "and s.id = vm.hmc_id and vm.hmc_id is not null";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{phyConfigId});
	}
	
	/**
	 * 修改物理机的HMC_ID
	 * @param hmcId
	 * @param vmId
	 */
	public void updatePhysicHmcId(String hmcId,String physicId) {
		String sql = "update t_res_hypervisor set hmc_id = ? where hypervisor_id = ?";
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql, new Object[]{hmcId,physicId});
	}
	
	/**
	 * 修改虚拟机的HMC_ID
	 * @param hmcId
	 * @param vmId
	 */
	public void updateVirtHmcId(String hmcId,String vmId,String vmName) {
		StringBuffer sql = new StringBuffer("update t_res_virtualmachine set hmc_id = ? where 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		args.add(hmcId);
		if (StringHelper.isNotEmpty(vmId) && StringHelper.isNotBlank(vmId)) {
			sql.append("and vm_id = ? ");
			args.add(vmId);
		} else {
			if (StringHelper.isNotEmpty(vmName) && StringHelper.isNotBlank(vmName)) {
				sql.append("and name = ? ");
				args.add(vmName);
			}
		}
		
		if (StringHelper.isNotEmpty(vmId) || StringHelper.isNotEmpty(vmName)) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql.toString(), args.toArray());
		}
	}
	
	/**
	 * 修改虚拟机对应的t_res_computersystem表中的信息
	 * @param vmId
	 * @param vmName
	 * @param vmIp
	 */
	public void updateComputerSytem(String vmId,String vmName,String vmIp) {
		String computerId = getJdbcTemplate(WebConstants.DB_DEFAULT).queryString("select computer_id from t_res_virtualmachine where vm_id = ?", new Object[]{vmId});
		if (StringHelper.isNotBlank(computerId) && StringHelper.isNotEmpty(computerId)) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("update t_res_computersystem set display_name = ?,ip_address = ? where computer_id = ?", new Object[]{vmName,vmIp,computerId});
		}
	}
	
	/**
	 * 删除服务器配置信息
	 * @param id
	 */
	public void delServerConfigInfo(String id) {
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_hypervisor_switch", "hypervisor_id", id);
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_server", "id", id);
	}

	public List<DataRow> getvirtualList(String id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query("select v.vm_id as ele_id,v.name as ele_name,c.ip_address from t_res_computersystem c,t_res_virtualmachine v where c.computer_id = v.computer_id and v.hypervisor_id = " + id);
	}

	public DBPage getDevicePage(int curPage, int numPerPage, String devicename, Integer devicetype){
		String sql="SELECT d.*,t.device_name dname FROM t_acct_device d,t_device_type t WHERE 1=1 AND d.type_id=t.type_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (devicename != null && devicename.length() > 0) {
			sb.append("and d.device_name like ? ");
			args.add("%" + devicename + "%");
		}
		if (devicetype != 0 && devicetype > 0) {
			sb.append("and d.type_id =? ");
			args.add(devicetype);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	public DBPage getDeviceSnmpPage(int curPage, int numPerPage, String ipAddress,
			String groupName, String snmpVersion, Boolean enabled){
		StringBuilder sql = new StringBuilder(200);
		sql.append("select a.*,b.group_name,b.polling_interval_minute,b.polling_interval_hour,b.polling_interval_day,c.device_type,c.device_model ");
		sql.append("from t_nw_res_device_snmp a join t_nw_res_group b on a.group_id=b.group_id ");
		sql.append(" join t_nw_res_device c on a.device_id=c.device_id ");
		if(ipAddress != null && ipAddress.trim().length() > 0){
			sql.append(" and a.ip_address_v4 like '%");
			sql.append(ipAddress);
			sql.append("%' ");
		}
		if(groupName != null && groupName.trim().length() > 0){
			sql.append(" and b.group_name like '%");
			sql.append(groupName);
			sql.append("%' ");
		}
		if(snmpVersion != null && snmpVersion.trim().length() > 0){
			sql.append(" and a.snmp_version like '%");
			sql.append(snmpVersion);
			sql.append("%' ");
		}
		List<Object> args = new ArrayList<Object>(1);
		if(enabled != null) {
			sql.append(" and a.enabled=? ");
			args.add(enabled);
		}
		
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sql.toString(), args.toArray(), curPage, numPerPage);
	}
	
	public List<DataRow> getAllHypervisors(){
		String sql = "SELECT id as hypSwId,name as hypSwName FROM t_server WHERE toptype='physical' order by hypSwId asc";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	public List<DataRow> getAllSwitches(String limitIds){
		StringBuffer sb = new StringBuffer("select switch_id as hypSwId,the_display_name as hypSwName,ip_address as hypSwip from v_res_switch where 1 = 1");
		if (limitIds != null && limitIds.length() > 0) {
			sb.append(" and switch_id in (" + limitIds + ") ");
		}
		sb.append(" order by switch_id asc");
		return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString());
	}
	
	public void saveDevice(List<DataRow> devices, String tableName){
		JdbcTemplate jdbc = getJdbcTemplate(WebConstants.DB_DEFAULT);
		if(devices != null && devices.size() > 0){
			for(DataRow dev : devices){
				jdbc.insert(tableName, dev);
			}
		}
	}
	
	public List<DataRow> getDeviceType(String typeId){
		String sql = "SELECT * FROM t_device_type where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (typeId != null && typeId.length() > 0) {
			sb.append("and type_id =? ");
			args.add(typeId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	public List<DataRow> getDeviceList(String id,String typeId){
		String sql = "SELECT * FROM t_acct_device d where state = 1 ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (id != null && id.length() > 0) {
			sb.append(" and d.id = ? ");
			args.add(id);
		}
		if (typeId != null && typeId.length() > 0) {
			sb.append(" and d.type_id = ? ");
			args.add(typeId);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	public DataRow getDeviceInfo(String id,String typeid){
		String sql="SELECT d.*,t.device_name dname FROM t_acct_device d,t_device_type t WHERE 1=1 AND d.type_id=t.type_id ";
		StringBuffer sb = new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if (id != null && id.length() > 0) {
			sb.append("and d.id =? ");
			args.add(id);
		}
		if (typeid != null && typeid.length() > 0) {
			sb.append("and t.type_id =? ");
			args.add(typeid);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sb.toString(),args.toArray());
	}
	public void addDeviceInfo(DataRow row){
		String id = row.getString("id");
		row.remove("id");
		if (id != null && id.length() > 0) {
			getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_acct_device", row, "id", id);
		} else {
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_acct_device", row);
		}
	}
	
	public void deviceDel(String id){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_acct_device", "id", id);
	}
	
	/**
	 * 获取存储系统配置信息
	 * @param name
	 * @param storageType
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage getStorageCfgPage(String name,String storageType,int curPage, int numPerPage) {
		StringBuffer sb = new StringBuffer("select * from t_storage_config where 1 = 1");
		List<Object> args = new ArrayList<Object>();
		if (StringHelper.isNotEmpty(name) && StringHelper.isNotBlank(name)) {
			sb.append(" and name like ? ");
			args.add("%" + name + "%");
		}
		if (StringHelper.isNotEmpty(storageType) && StringHelper.isNotBlank(storageType)) {
			sb.append(" and storage_type = ? ");
			args.add(storageType);
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(), args.toArray(), curPage, numPerPage);
	}
	
	/**
	 * 获取存储系统配置详细信息
	 * @param id
	 * @return
	 */
	public DataRow getStorageCfgInfo(String id) {
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_storage_config where id = ?", new Object[]{id});
	}
	
	/**
	 * 保存存储系统配置信息
	 * @param dataRow
	 */
	public void saveStorageCfgInfo(DataRow dataRow,long id) {
		if (dataRow != null) {
			if (id > 0) {
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_storage_config", dataRow, "id", id);
			} else {
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_storage_config", dataRow);
			}
		}
	}
	
	/**
	 * 删除存储系统配置信息
	 * @param id
	 */
	public void delStorageConfigInfo(String id) {
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("t_storage_config", "id", id);
	}
	
	/**
	 * 保存BMC配置信息
	 * @param dataRow
	 * @return
	 */
	public String saveBmcConfigInfo(DataRow dataRow) {
		String result = null;
		if (dataRow != null) {
			long hypervisorId = dataRow.getLong("hypervisor_id");
			DataRow oldRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from t_cfg_bmc where hypervisor_id = ?",new Object[]{hypervisorId});
			if (oldRow != null) {
				result = oldRow.getString("id");
				getJdbcTemplate(WebConstants.DB_DEFAULT).update("t_cfg_bmc", dataRow, "id", result);
			} else {
				result = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_cfg_bmc", dataRow);
			}
		}
		return result;
	}
	
	/**
	 * 更新BMC配置信息密码
	 * @param id
	 * @param password
	 */
	public void updateBmcConfigPassword(String id,String password) {
		if (StringHelper.isNotEmpty(id) && StringHelper.isNotBlank(id)) {
			String sql = "update t_cfg_bmc set session_pwd = ? where id = ?";
			getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql, new Object[]{password,id});
		}
	}
	
	/**
	 * 获取BMC配置信息
	 * @param hypervisorId
	 * @return
	 */
	public DataRow getBmcConfigInfo(String hypervisorId) {
		String sql = "select * from t_cfg_bmc where hypervisor_id = ?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{hypervisorId});
	}

}
