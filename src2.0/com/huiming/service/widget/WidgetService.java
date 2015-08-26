package com.huiming.service.widget;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.connection.Configure;
import com.huiming.base.service.BaseService;
import com.huiming.base.util.StringHelper;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class WidgetService extends BaseService {
	private boolean hasDB2 = Configure.getInstance().getDataSource(WebConstants.DB_TPC) != null;
	/**
	 * 查找模块设置信息
	 * @param fid
	 * @return
	 */
	public DataRow getWidgetInfo(String fid){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tsnschart where fid = ?",new Object[]{fid});
	}
	/**
	 * 根据类型查找设备
	 * @param type
	 * @param limitIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getStorageType(String type,String limitIds){
		List<DataRow> rows = new ArrayList<DataRow>();
		String sql = "select subsystem_id as ele_id,the_display_name as ele_name,case os_type " +
				"when 10 then 'BSP' " +
				"when 15 then 'BSP' " +
				"when 21 then 'SVC' " +
				"when 25 then 'DS' " +
				"when 37 then 'BSP' " +
				"when 38 then 'SVC' " +
				"end " +
				"as type " +
				"from v_res_storage_subsystem where 1 = 1";
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql = sql + " and subsystem_id in (" + limitIds + ")";
		}
		List<DataRow> row = getJdbcTemplate(WebConstants.DB_TPC).query(sql);
		if (type != null && type.length() > 0) {
			for (DataRow dataRow : row) {
				if(dataRow.getString("type").equalsIgnoreCase(type)){
					rows.add(dataRow);
				}
			}
		} else {
			return row;
		}
		return rows;
	}
	
	/**
	 * 根据设备类型查找相应的设备列表
	 * @param devType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDeviceListByDevType(String devType,String limitIds) {
		StringBuffer sql = new StringBuffer("select subsystem_id as ele_id,name as ele_name from t_res_storagesubsystem where storage_type = ?");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and subsystem_id in (" + limitIds + ")");
		}
		sql.append(" order by subsystem_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString(), new Object[]{devType});
	}
	
	/**
	 * 得到应用设备
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getAppList(){
		String sql="SELECT fid AS ele_id,fname AS ele_name FROM tnapps"; 
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getsubAppList(){
		String sql="SELECT fid AS id,fname AS value FROM tnapps"; 
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到交换机列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitchList(String limitIds) {
		StringBuffer sql = new StringBuffer("select switch_id as ele_id,the_display_name as ele_name from v_res_switch");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" where switch_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql.toString());
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getsubSwitchList() {
		String sql="select switch_id as id,the_display_name as value from v_res_switch";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	
	/**
	 * 获得设备列表
	 * @param storageType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getFnameList(String storageType){
		String sql="SELECT FStorageType,FDevType AS ele_id,FDevTypeName AS ele_name,FPrfView FROM tnprffields ";
		if(storageType!=null && storageType.length()>0){
			sql+="where FStorageType = '"+storageType+"' ";
		}
		sql+="GROUP BY FDevType,FStorageType order by ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	/**
	 * 获取部件列表(SR)
	 * @param systemId
	 * @param col_systemId
	 * @param col_deviceId
	 * @param col_deviceName
	 * @param tbl_device
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubDeviceList(Integer systemId, String col_systemId, String col_deviceId, String col_deviceName, String tbl_device) {
		String sql="select t." + col_deviceId + " as id,t." + col_deviceName + " as value from " + tbl_device + " t where t." + col_systemId + " = " + systemId + " order by t." + col_deviceId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到性能指标列表
	 * @param storageType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getFprffildList(String storageType){
		String sql="SELECT fid AS ele_id,ftitle AS ele_name,funits,fprfview,FStorageType,FDevType,FImp FROM tnprffields WHERE 1=1 ";
		if(storageType!=null && storageType.length()>0){
			sql+="and FStorageType = '"+storageType+"'";
		}
		sql+=" and FImp > 0  ORDER BY fdevtype,ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> getTopnKPIList(String storageType,String devType){
		String sql="SELECT fid AS ele_id,ftitle AS ele_name,funits,fprfview,FStorageType,FDevType,FImp FROM tnprffields WHERE 1=1 ";
		if(storageType!=null && storageType.length()>0){
			sql+="and FStorageType = '"+storageType+"' ";
		}
		if(devType!=null && devType.length()>0){
			sql+="and FDevType = '"+devType+"' ";
		}
		sql+=" and FImp > 0 ORDER BY fdevtype,ftitle";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubtype(String storageType){
		String sql="SELECT FDevType AS id,FDevTypeName AS VALUE FROM tnprffields where 1=1 ";
		if(storageType!=null && storageType.length()>0){
			sql+="and FStorageType = '"+storageType+"' ";
		}
		sql+="GROUP BY FDevType";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到性能指标的详细信息
	 * @param fid
	 * @return
	 */
	public DataRow getKPIinfo(String fid){
		String sql="SELECT fid AS ele_id,ftitle AS ele_name,funits,fprfview,FStorageType,FDevType,FDBType FROM tnprffields WHERE fid = ? ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{fid});
	}
	
	/**
	 * 处理性能数据
	 * @param list
	 * @param eleId
	 * @return
	 */
	public List<DataRow> getCorrespondPerfData(List<DataRow> list, Integer eleId) {
		List<DataRow> resultList = new ArrayList<DataRow>();
		for (int i = 0; i < list.size(); i++) {
			DataRow row = list.get(i);
			if (row.getInt("ele_id") == eleId) {
				resultList.add(row);
			}
		}
		return resultList;
	}
	
	/**
	 * 处理产生曲线图数据
	 * @param row
	 * @return series
	 */
	@SuppressWarnings("static-access")
	public JSONArray getHighchartLineData(DataRow row){
		JSONArray array = new JSONArray();
		JSONArray subdevs = new JSONArray().fromObject(row.getString("fsubdev"));
		
		//获取选择部件的ID
		String subIdsStr = null;
		for (Object object : subdevs) {
			JSONObject subdev = new JSONObject().fromObject(object);
			if (subIdsStr == null) {
				subIdsStr = "," + subdev.getString("id");
			} else {
				subIdsStr = subIdsStr + "," + subdev.getString("id");
			}
		}
		row.set("fsubdev", subIdsStr.replaceFirst(",", ""));
		//获取所有性能数据
		List<DataRow> allRows = getDevicePerfInfo(row);
		
		for (Object object : subdevs) {
			JSONObject subdev = new JSONObject().fromObject(object);
			Integer eleId = subdev.getInt("id");
			List<DataRow> rows = getCorrespondPerfData(allRows,eleId);
			JSONObject json = new JSONObject();
			JSONArray ary = new JSONArray();
			for (DataRow dataRow : rows) {
				try {
					JSONObject obj = new JSONObject();
					obj.put("x", Long.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataRow.getString("prf_timestamp")).getTime()));
					obj.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble(row.getString("fprfid").toLowerCase()))));
					obj.put("unit", row.getString("fyaxisname"));
					ary.add(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String lineName = "";
			if (subdev.getString("name").length() > 20) {
				lineName = subdev.getString("name").substring(0, 20) + "...:" + row.getString("ftitle");
			} else {
				lineName = subdev.getString("name") + ":" + row.getString("ftitle");
			}
			json.put("name", lineName);
			json.put("data", ary);
			array.add(json);
		}
		return array;
	}
	
	/**
	 * 处理产生饼图数据
	 * @param row
	 * @return
	 */
	public JSONArray getHighchartPieData(DataRow row){
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		DataRow dataRow = getCapacityInfo(row);
		String url = null;
		String devType = row.getString("fdevicetype");
		String device = row.getString("fdevice");
		//物理机
		if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
			DataRow phyRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select hypervisor_id,host_computer_id as computer_id from t_res_hypervisor where hypervisor_id = " + device);
			url = "/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=" + phyRow.getString("hypervisor_id") + "&computerId=" + phyRow.getString("computer_id");
		//虚拟机
		} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)) {
			DataRow vmRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select vm_id,hypervisor_id,computer_id from t_res_virtualmachine where computer_id = " + device);
			url = "/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=" + vmRow.getString("hypervisor_id") + "&vmId=" + vmRow.getString("vm_id");
		//存储系统(EMC,HDS,NETAPP)
		} else if (devType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC) 
				|| devType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)
				|| devType.equalsIgnoreCase(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
			url = "/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=" + device;
		//存储系统(SVC,BSP,DS)
		} else {
			url = "/servlet/storage/StorageAction?func=StorageInfo&subSystemID=" + device;
		}
	
		JSONObject used = new JSONObject();
		used.put("name", "已用容量(G)");
		used.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow == null ? 0 : dataRow.getString("used") == null ? 0 : dataRow.getDouble("used")/1024)));
		used.put("url", url);
		JSONObject unuse = new JSONObject();
		unuse.put("name", "空余容量(G)");
		unuse.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow == null ? 0 : dataRow.getString("unuse") == null ? 0 : dataRow.getDouble("unuse")/1024)));
		unuse.put("url", url);
		ary.add(used);
		ary.add(unuse);
		
		json.put("type", "pie");
		json.put("name", "容量");
		json.put("data", ary);
		array.add(json);
		return array;
	}
	
	/**
	 * 得到部件的性能信息
	 * @param row 
	 * @param subDev
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getDevicePerfInfo(DataRow row){
		//表名
		String viewName = row.getString("fprfview")+(row.getString("ftimesize").length()>0?"_"+row.getString("ftimesize"):"");
		//得到时间段
		String daterange = row.getString("fdaterange");
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		if(daterange.equals("day")){
			ca.add(Calendar.DAY_OF_MONTH, -1);
		}else if(daterange.equals("week")){
			ca.add(Calendar.DAY_OF_MONTH, -7);
		}else{
			ca.add(Calendar.MONTH, -1);
		}
		String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
		StringBuffer sb = new StringBuffer("select dev_id,ele_id,ele_name,prf_timestamp,");
		sb.append(row.getString("fprfid"));
		sb.append(" from " + viewName);
		sb.append(" where dev_id = ?");
		sb.append(" and ele_id in (" + row.getString("fsubdev") + ")");
		sb.append(" and prf_timestamp >= ?");
		sb.append(" and prf_timestamp <= ?");
		sb.append(" order by prf_timestamp");
		//判断到哪个数据库查询视图
		String dbType = row.getString("fdbtype");
		if (StringHelper.isNotEmpty(dbType) && StringHelper.isNotBlank(dbType)) {
			if (dbType.equals(SrContant.DBTYPE_SR)) {
				return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),new Object[]{row.getString("fdevice"),startTime,endTime});
			} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
				return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),new Object[]{row.getString("fdevice"),startTime,endTime});
			}
		}
		return null;
	}
	
	/**
	 * 得到存储系统容量信息
	 * @param row
	 * @return
	 */
	public DataRow getCapacityInfo(DataRow row){
		Integer devId = row.getInt("fdevice");
		String devType = row.getString("fdevicetype");
		String dbtype = row.getString("fdbtype");
		String sql = null;
		//For Physical
		if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
			sql = "select (cs.disk_space-cs.disk_available_space) as used,cs.disk_available_space as unuse,coalesce(cs.display_name,cs.name) as display_name from t_res_computersystem cs,t_res_hypervisor h where cs.computer_id = h.host_computer_id and h.hypervisor_id = ?";
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{devId});
		//For Virtual
		} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL)) {
			sql = "select (disk_space-disk_available_space) as used,disk_available_space as unuse,coalesce(display_name,name) as display_name from t_res_computersystem where computer_id = ?";
			return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{devId});
		//For EMC/SVC/HDS/BSP..
		} else {
			if (dbtype.equals(SrContant.DBTYPE_SR)) {
				sql = "select (total_usable_capacity-unallocated_usable_capacity) as used,unallocated_usable_capacity as unuse,name as display_name from t_res_storagesubsystem where subsystem_id = ?";
				return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{devId});
			} 
			else if (dbtype.equals(SrContant.DBTYPE_TPC)) {
				if(hasDB2){
					sql = "select the_allocated_capacity as used,the_available_capacity as unuse,the_display_name as display_name from v_res_storage_subsystem where subsystem_id = ? ";
					return getJdbcTemplate(WebConstants.DB_TPC).queryMap(sql,new Object[]{devId});
				}
			}
		}
		return null;
	}
	/**
	 * 处理产生TopN图数据
	 * @param row
	 * @return
	 */
	public JSONObject getHighchartTopnData(DataRow row) {
		JSONObject json = new JSONObject();
		JSONArray names = new JSONArray();
		JSONArray array = new JSONArray();
		//获取TopN数据
		List<DataRow> rows = getTopnData(row);
		DataRow resRow = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnprffields where fid = ?", new Object[]{row.getString("fprfid")});
		//获取设备类型
		String storageType = resRow.getString("fstoragetype");
		String devType = resRow.getString("fdevtype");
		//TopN跳转URL
		String url = null;
		
		int i = 0;
		for (DataRow dataRow : rows) {
			names.add(dataRow.getString("ele_name"));
			String devId = dataRow.getString("dev_id");
			String subDevId = dataRow.getString("ele_id");
			//判断设备类型,设置相应的URL
			if (StringHelper.isNotEmpty(devType) && StringHelper.isNotBlank(devType)) {
				//HOST
				if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HOST)) {
					//Physical
					if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PHYSICAL)) {
						url = "/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=" + devId;
					//Virtual,Hypervisor
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VIRTUAL) || devType.equalsIgnoreCase(WebConstants.DEVTYPE_HYPERVISOR)) {
						url = "/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=" + devId + "&vmId=" + subDevId;
					}
				//SWITCH
				} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_SWITCH)) {
					//Switch
					if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_SWITCH)) {
						url = "/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=" + devId;
					//Switch Port
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
						url = "/servlet/switchport/SwitchportAction?func=PortInfo&switchId=" + devId + "&portId=" + subDevId;
					}
				//EMC,HDS,NETAPP
				} else if (storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_EMC) 
						|| storageType.equalsIgnoreCase(SrContant.DEVTYPE_VAL_HDS)
						|| storageType.equalsIgnoreCase(WebConstants.STORAGE_TYPE_VAL_NETAPP)) {
					storageType = storageType.toUpperCase();
					//Storage
					if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
						url = "/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID" + devId;
					//Storage Port
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
						url = "/servlet/sr/storageport/StoragePortAction?func=LoadPortInfo&subsystemId=" + devId +"&portId=" + subDevId + "&storageType='" + storageType + "'";
					//DiskGroup
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_DISKGROUP)) {
						url = "/servlet/sr/diskgroup/DiskgroupAction?func=LoadDiskgroupInfo&subsystemId=" + devId + "&diskgroupId=" + subDevId + "&storageType='" + storageType + "'";
					//Pool
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_POOL)) {
						url = "/servlet/sr/pool/PoolAction?func=PoolInfo&subSystemID=" + devId +"&poolId=" + subDevId +"&storageType='" + storageType + "'";
					//Volume
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
						url = "/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=" + devId + "&volumeId=" + subDevId + "&storageType='" + storageType + "'";
					//HostGroup
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_HOSTGROUP)) {
						url = "/servlet/sr/storagegroup/StoragegroupAction?func=StoragegroupInfo&subsystemId=" + devId + "&hostgroupId=" + subDevId + "&storageType='" + storageType + "'";
					//Node
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
						url = "/servlet/sr/storagenode/StoragenodeAction?func=LoadStoragenodeInfo&subsystemId=" + devId + "&spId=" + subDevId + "&storageType='" + storageType + "'";
					}
				//SVC,DS,BSP
				} else {
					//Storage
					if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_STORAGE)) {
						url = "/servlet/storage/StorageAction?func=StorageInfo&subSystemID=" + devId;
					//Storage Port
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_PORT)) {
						url = "/servlet/port/PortAction?func=PortInfo&subSystemID=" + devId + "&portId=" + subDevId;
					//Pool
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_POOL)) {
						url = "/servlet/pool/PoolAction?func=PoolInfo&subSystemID=" + devId + "&poolId=" + subDevId;
					//Volume
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_VOLUME)) {
						url = "/servlet/volume/VolumeAction?func=PerVolumeInfo&subSystemID=" + devId + "&svid=" + subDevId;
					//Extent
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_EXTENT)) {
						url = "/servlet/extend/ExtendAction?func=extendInfo&subSystemID=" + devId +"&extendId=" + subDevId;
					//ArraySite
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_ARRAYSITE)) {
						url = "/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=" + devId +"&arraysiteId=" + subDevId;
					//Rank
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_RANK)) {
						url = "/servlet/rank/RankAction?func=RankInfo&subSystemID=" + devId + "&rankId=" + subDevId;
					//Node
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_NODE)) {
						url = "/servlet/node/NodeAction?func=NodeInfo&subSystemID=" + devId + "&nodeId=" + subDevId;
					//IOGroup
					} else if (devType.equalsIgnoreCase(SrContant.SUBDEVTYPE_IOGROUP)) {
						url = "/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=" + devId + "&iogroupId=" + subDevId;
					}
				}
			}
			JSONObject obj = new JSONObject();
			//为Top1添加样式
			if(i==0){  
				obj.put("dataLabels", "{style: {fontWeight:'bold',color: 'red'}}");
			}
			obj.put("time", dataRow.getString("prf_timestamp"));
			obj.put("y", Double.parseDouble(new DecimalFormat("0.00").format(dataRow.getDouble("kpi"))));
			obj.put("url", url);
			array.add(obj);
			i++;
		}
		json.put("names", names);
		json.put("data", array);
		return json;
	}
	
	
	/**
	 * 查找Topn
	 * @param row
	 * @return
	 */
	@SuppressWarnings({"unchecked", "static-access" })
	public List<DataRow> getTopnData(DataRow row){
		//表名
		String viewName = row.getString("fprfview")+(row.getString("ftimesize").length()>0?"_"+row.getString("ftimesize"):"");
		//得到时间段
		String daterange = row.getString("fdaterange");
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		if(daterange.equals("day")){
			ca.add(Calendar.DAY_OF_MONTH, -1);
		}else if(daterange.equals("week")){
			ca.add(Calendar.DAY_OF_MONTH, -7);
		}else{
			ca.add(Calendar.MONTH, -1);
		}
		String startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(ca.getTime());
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date());
		
		String devId = row.getString("fdevice");
		
		//获取选择部件的ID
		JSONArray subdevs = new JSONArray().fromObject(row.getString("fsubdev"));
		String subIdsStr = null;
		for (Object object : subdevs) {
			JSONObject subdev = new JSONObject().fromObject(object);
			if (subIdsStr == null) {
				subIdsStr = "," + subdev.getString("id");
			} else {
				subIdsStr = subIdsStr + "," + subdev.getString("id");
			}
		}
		subIdsStr = subIdsStr.replaceFirst(",", "");
		StringBuffer sb = new StringBuffer();
		sb.append("select '' as prf_timestamp,dev_id,ele_id,ele_name,");
		sb.append("avg(" + row.getString("fprfid") + ") as kpi");
		sb.append(" from " + viewName);
		sb.append(" where dev_id in (" + devId + ")");
		sb.append(" and ele_id in (" + subIdsStr + ")");
		sb.append(" and prf_timestamp >= ?");
		sb.append(" and prf_timestamp <= ?");
		sb.append(" group by ele_id,ele_name,dev_id");
		sb.append(" order by avg(" + row.getString("fprfid") + ") desc");
		//判断到哪个数据库查询视图
		String dbType = row.getString("fdbtype");
		if (StringHelper.isNotEmpty(dbType) && StringHelper.isNotBlank(dbType)) {
			if (dbType.equals(SrContant.DBTYPE_SR)) {
				return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),new Object[]{startTime,endTime}, row.getInt("ftopncount"));
			} else if (dbType.equals(SrContant.DBTYPE_TPC)) {
				return getJdbcTemplate(WebConstants.DB_TPC).query(sb.toString(),new Object[]{startTime,endTime}, row.getInt("ftopncount"));
			}
		}
		return null;
	}
	
	/**
	 * 获取物理机列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getPhysicalList(String limitIds) {
		StringBuffer sql = new StringBuffer("select h.hypervisor_id as ele_id, coalesce(cs.display_name,cs.name) as ele_name from t_res_hypervisor h,t_res_computersystem cs where h.host_computer_id = cs.computer_id");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and h.hypervisor_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}
	
	/**
	 * 获取虚拟机列表
	 * @return
	 */
	public List<DataRow> getVirtualList(String limitIds) {
		StringBuffer sql = new StringBuffer("select cs.computer_id as ele_id, coalesce(cs.display_name,cs.name) as ele_name from t_res_virtualmachine vm,t_res_computersystem cs where vm.computer_id = cs.computer_id");
		if (StringHelper.isNotEmpty(limitIds) && StringHelper.isNotBlank(limitIds)) {
			sql.append(" and vm.vm_id in (" + limitIds + ")");
		}
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql.toString());
	}
	
}
