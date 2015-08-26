package com.huiming.service.x86monitor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import system.DateTime;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.base.jdbc.session.Session;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;
import com.project.x86monitor.MyUtilities;
import com.project.x86monitor.TPrfTimestamp;

public class DataCollectService<T> extends BaseService {
	Logger logger = Logger.getLogger(DataCollectService.class);
	private String dbType = WebConstants.DB_DEFAULT;
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void deleteOldLog(String table, String timeColumn, Integer number, String unit){
		Session session = null;
		try {
			session = getSession(dbType);
			session.beginTrans();
			session.update(
					String.format("delete from %s where %s<DATE_SUB(CURDATE(),INTERVAL ? %s)",
						table, timeColumn, unit),
					new Object[]{number});
			session.commitTrans();
		} catch (Exception e) {
			if(session != null){
				session.rollbackTrans();
			}
			logger.error("", e);
		} finally{
			if(session != null){
				session.close();
				session = null;
			}
		}
	}
	
	public DataCollectService(String dbType){
		if(dbType != null){
			this.dbType = dbType;
		}
	}
	
	public Long getComputerIdByComputerName(String sql, Object ... args){
		return getJdbcTemplate(dbType).queryLong(sql, args);
	}
	
	/**
	 * @see 通过computerId从t_res_hypervisor获得HypervisorId
	 * @param computerId
	 * @return
	 */
	public Long getHypervisorIdByComputerId(Long computerId){
		if(computerId == null || computerId <= 0L){ return -1L; }
		String sql = "SELECT hpy.hypervisor_id AS hypid FROM t_res_hypervisor hpy " +
				"JOIN t_res_computersystem com ON hpy.host_computer_id=com.computer_id " +
				"AND com.computer_id=" + computerId;
		DataRow dr = getJdbcTemplate(dbType).queryMap(sql);
		if(dr == null){ return -1L; }
		return dr.getLong("hypid");
	}
	
	/**
	 * @see 通过computerId从t_res_virtualmachine获得HypervisorId和vm_id
	 * @param computerId
	 * @return [HypervisorId, vm_id]
	 */
	public Long[] getHypIdAndVMIdByComputerId(Long computerId){
		Long val[] = new Long[]{-1L, -1L};
		if(computerId != null && computerId > 0){
			String sql = "SELECT hypervisor_id as hypid,vm_id as vmid FROM t_res_virtualmachine WHERE computer_id=" + computerId;
			DataRow dr = getJdbcTemplate(dbType).queryMap(sql);
			if(dr != null){
				val[0] = dr.getLong("hypid");
				val[1] = dr.getLong("vmid");
			}
		}
		return val;
	}
	
	/**
	 * @see 通过IP获得服务器的ID
	 * @param ip
	 * @return
	 */
	public Long getComputerIdByIP(String ip){
		String sql = "SELECT computer_id FROM t_res_computersystem WHERE IP_ADDRESS=?";
		return getJdbcTemplate(dbType).queryLong(sql, new Object[]{ip});
	}
	
	/**
	 * @see 统计近一天的性能数据
	 */
	@SuppressWarnings("unchecked")
	public void generatePrfHourly(){
		String sql = "SELECT t1.COMPUTER_ID AS COMPUTER_ID,t1.computer_name as cprn,AVG(t1.CPU_IDLE_PRCT) AS CPU_IDLE_PRCT,AVG(t1.CPU_BUSY_PRCT) AS CPU_BUSY_PRCT," +
				"AVG(MEM_FREE_PRCT) AS MEM_FREE_PRCT,AVG(MEM_USED_PRCT) AS MEM_USED_PRCT,SUM(t1.DISK_READDATARATE_KB) AS DISK_READDATARATE_KB," +
				"SUM(t1.DISK_WRITEDATARATE_KB) AS DISK_WRITEDATARATE_KB,SUM(t1.DISK_OVERALL_IOPS) AS DISK_OVERALL_IOPS,SUM(t1.DISK_READ_AWAIT) AS DISK_READ_AWAIT," +
				"SUM(t1.DISK_WRITE_AWAIT) AS DISK_WRITE_AWAIT,SUM(t1.NET_SEND_KB) AS NET_SEND_KB,SUM(t1.NET_RECV_KB) AS NET_RECV_KB," +
				"SUM(t1.NET_SEND_PACKET) AS NET_SEND_PACKET,SUM(t1.NET_RECV_PACKET) AS NET_RECV_PACKET,t1.device_type as dty " +
				"FROM (SELECT t1.COMPUTER_ID,t1.computer_name,t1.CPU_IDLE_PRCT,t1.CPU_BUSY_PRCT,t1.MEM_FREE_PRCT,t1.MEM_USED_PRCT,t1.DISK_READDATARATE_KB,t1.DISK_WRITEDATARATE_KB," +
				"t1.DISK_OVERALL_IOPS,t1.DISK_READ_AWAIT,t1.DISK_WRITE_AWAIT,t1.NET_SEND_KB,t1.NET_RECV_KB,t1.NET_SEND_PACKET,t1.NET_RECV_PACKET,t2.device_type  " +
				"FROM t_prf_computerper t1 JOIN t_prf_timestamp t2 ON t1.time_id=t2.time_id AND t2.SUMM_TYPE=1 AND "+
				"t2.device_type in ('X86','" + SrContant.SUBDEVTYPE_PHYSICAL + "','" + SrContant.SUBDEVTYPE_VIRTUAL + "','" + WebConstants.DEVTYPE_HYPERVISOR + "') " + // 更改
				"AND (t2.sample_time BETWEEN DATE_SUB(SYSDATE(), INTERVAL 1 HOUR) AND SYSDATE())) t1 GROUP BY COMPUTER_ID,DEVICE_TYPE";
		JdbcTemplate jdbc = getJdbcTemplate(dbType);
		List<DataRow> drs = jdbc.query(sql);
		if(drs != null && drs.size() > 0){
			DataCollectService<TPrfTimestamp> ser = new DataCollectService<TPrfTimestamp>(WebConstants.DB_DEFAULT);
			Long timeId;
			short sty = (short)SrContant.SUMM_TYPE_HOUR;
			for(DataRow dr : drs){
				TPrfTimestamp t = new TPrfTimestamp();
				//设置设备ID
				t.setSubsystemId(dr.getLong("computer_id"));
				t.setSubsystemName(dr.getString("cprn"));
				t.setSampleTime(new Date());
				t.setIntervalLen(3600);
				t.setSummType(sty);
				t.setDeviceType(dr.getString("dty"));
				timeId = Long.parseLong(ser.insert(t, null, false));
				
				dr.set("time_id", timeId);
				//设置设备名称
				dr.set("computer_name", dr.getString("cprn"));
				//移除"cprn"和"dty",否则插入数据时报错
				dr.remove("dty");
				dr.remove("cprn");
				jdbc.insert("t_prf_computerper", dr);
			}
		}
	}
	
	/**
	 * @see 统计近一个小时的性能数据
	 */
	@SuppressWarnings("unchecked")
	public void generatePrfDaily(){
		String sql = "SELECT t1.COMPUTER_ID AS COMPUTER_ID,t1.computer_name as cprn,AVG(t1.CPU_IDLE_PRCT) AS CPU_IDLE_PRCT,AVG(t1.CPU_BUSY_PRCT) AS CPU_BUSY_PRCT," +
			"AVG(MEM_FREE_PRCT) AS MEM_FREE_PRCT,AVG(MEM_USED_PRCT) AS MEM_USED_PRCT,SUM(t1.DISK_READDATARATE_KB) AS DISK_READDATARATE_KB," +
			"SUM(t1.DISK_WRITEDATARATE_KB) AS DISK_WRITEDATARATE_KB,SUM(t1.DISK_OVERALL_IOPS) AS DISK_OVERALL_IOPS,SUM(t1.DISK_READ_AWAIT) AS DISK_READ_AWAIT," +
			"SUM(t1.DISK_WRITE_AWAIT) AS DISK_WRITE_AWAIT,SUM(t1.NET_SEND_KB) AS NET_SEND_KB,SUM(t1.NET_RECV_KB) AS NET_RECV_KB," +
			"SUM(t1.NET_SEND_PACKET) AS NET_SEND_PACKET,SUM(t1.NET_RECV_PACKET) AS NET_RECV_PACKET,t1.device_type as dty " +
			"FROM (SELECT t1.COMPUTER_ID,t1.computer_name,t1.CPU_IDLE_PRCT,t1.CPU_BUSY_PRCT,t1.MEM_FREE_PRCT,t1.MEM_USED_PRCT,t1.DISK_READDATARATE_KB,t1.DISK_WRITEDATARATE_KB," +
			"t1.DISK_OVERALL_IOPS,t1.DISK_READ_AWAIT,t1.DISK_WRITE_AWAIT,t1.NET_SEND_KB,t1.NET_RECV_KB,t1.NET_SEND_PACKET,t1.NET_RECV_PACKET,t2.device_type " +
			"FROM t_prf_computerper t1 JOIN t_prf_timestamp t2 ON t1.time_id=t2.time_id AND t2.SUMM_TYPE=1 AND "+
			"t2.device_type in ('X86','" + SrContant.SUBDEVTYPE_PHYSICAL + "','" + SrContant.SUBDEVTYPE_VIRTUAL + "','" + WebConstants.DEVTYPE_HYPERVISOR + "') " + // 更改
			"AND (t2.sample_time BETWEEN DATE_SUB(SYSDATE(), INTERVAL 1 DAY) AND SYSDATE())) t1 GROUP BY COMPUTER_ID,DEVICE_TYPE";
		JdbcTemplate jdbc = getJdbcTemplate(dbType);
		List<DataRow> drs = jdbc.query(sql);
		if(drs != null && drs.size() > 0){
			DataCollectService<TPrfTimestamp> ser = new DataCollectService<TPrfTimestamp>(WebConstants.DB_DEFAULT);
			Long timeId;
			short sty = (short)SrContant.SUMM_TYPE_DAY;
			for(DataRow dr : drs){
				TPrfTimestamp t = new TPrfTimestamp();
				//设置设备ID
				t.setSubsystemId(dr.getLong("computer_id"));
				t.setSubsystemName(dr.getString("cprn"));
				t.setSampleTime(new Date());
				t.setIntervalLen(86400);
				t.setSummType(sty);
				t.setDeviceType(dr.getString("dty"));
				timeId = Long.parseLong(ser.insert(t, null, false));
				
				dr.set("time_id", timeId);
				//设置设备名称
				dr.set("computer_name", dr.getString("cprn"));
				//移除"cprn"和"dty",否则插入数据时报错
				dr.remove("dty");
				dr.remove("cprn");
				jdbc.insert("t_prf_computerper", dr);
			}
		}
	}
	
	public DateTime getLogLatestDate(){
		String sql = "SELECT MAX(flasttime) as t FROM tndevicelog";
		DataRow dr = getJdbcTemplate(dbType).queryMap(sql);
		return dr == null? null : MyUtilities.convertString2DateTime(dr.getString("t"));
	}
	
	/**
	 * @see 如果为空则不插入
	 * @param sys
	 * @param exclusions 排除的字段或属性
	 * @param isColumn 如果是字段则填写true，如果是属性则填写false，建议使用false性能更好
	 * @return
	 */
	public String insert(T sys, String [] exclusions, boolean isColumn){
		DataRow dr = new DataRow();
		String tableName = property2Column(sys.getClass().getSimpleName());
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(sys.getClass());
			PropertyDescriptor props[] = beanInfo.getPropertyDescriptors();
			Method method = null;
			Object value;
			Object noParams[] = new Object[0];
			boolean first = true;
			boolean isExclusionsNull = exclusions != null;
			boolean ignore;
			
			String prop;
			DateTime dt;
			for(int i = 0; i < props.length; ++i){
				prop = props[i].getName();
				if(first){
					if(prop.equalsIgnoreCase("class")){
						first = false;
						continue;
					}
				}
				if(isExclusionsNull){
					if(isColumn){ prop = column2Property(prop); }
					ignore = false;
					for(int j = 0; j < exclusions.length; ++j){
						if(prop.equalsIgnoreCase(exclusions[j])){
							ignore = true;
							break;
						}
					}
					if(ignore){ continue; }
				}
				method = props[i].getReadMethod();
				if(method == null){
					throw new IllegalArgumentException("没有名字为" + props[i].getName() + "的Getter方法");
				}
				else {
					value = method.invoke(sys, noParams);
					if(value != null){
						if(value instanceof DateTime){
							dt = (DateTime)value;
							if(dt.getTicks() > 0L){
								try {
									dr.set(property2Column(props[i].getName()), df.parse(dt.ToString().replace("/", "-")));
								} catch (ParseException e) {
									logger.error("", e);
								}
							}
						}
						else if(value instanceof Integer){
							if((Integer)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
						}
						else if(value instanceof Long){
							if((Long)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
						}
						else if(value instanceof Short){
							if((Short)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
						}
						else {
							dr.set(property2Column(props[i].getName()), value);
						}
					}
				}
			}
		} catch (IntrospectionException e) {
			logger.error("IntrospectionException", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException", e);
		} catch (IllegalArgumentException e) {
			logger.error("IllegalArgumentException", e);
		} catch (InvocationTargetException e) {
			logger.error("InvocationTargetException", e);
		}
		return getJdbcTemplate(dbType).insert(tableName, dr);
	}
	
	public void insert(T syses[], String [] exclusions, boolean isColumn){
		if(syses == null || syses.length <= 0){ return; }
		String tableName = property2Column(syses[0].getClass().getSimpleName());
		try {
			BeanInfo beanInfo;
			PropertyDescriptor props[];
			Method method = null;
			Object value;
			Object noParams[] = new Object[0];
			boolean first;
			boolean isExclusionsNull = exclusions != null;
			boolean ignore;
			JdbcTemplate jdbc = getJdbcTemplate(dbType);
			String prop;
			DateTime dt;
			DataRow dr = new DataRow();
			for(T sys : syses){
				if(sys == null){ continue; }
				beanInfo = Introspector.getBeanInfo(sys.getClass());
				props = beanInfo.getPropertyDescriptors();
				first = true;
				for(int i = 0; i < props.length; ++i){
					prop = props[i].getName();
					if(first){
						if(prop.equalsIgnoreCase("class")){
							first = false;
							continue;
						}
					}
					if(isExclusionsNull){
						if(isColumn){ prop = column2Property(prop); }
						ignore = false;
						for(int j = 0; j < exclusions.length; ++j){
							if(prop.equalsIgnoreCase(exclusions[j])){
								ignore = true;
								break;
							}
						}
						if(ignore){ continue; }
					}
					method = props[i].getReadMethod();
					if(method == null){
						throw new IllegalArgumentException("没有名字为" + props[i].getName() + "的Getter方法");
					}
					else {
						value = method.invoke(sys, noParams);
						if(value != null){
							if(value instanceof DateTime){
								dt = (DateTime)value;
								if(dt.getTicks() > 0L){
									try {
										dr.set(property2Column(props[i].getName()), df.parse(dt.ToString().replace("/", "-")));
									} catch (ParseException e) {
										logger.error("", e);
									}
								}
							}
							else if(value instanceof Integer){
								if((Integer)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
							}
							else if(value instanceof Long){
								if((Long)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
							}
							else if(value instanceof Short){
								if((Short)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
							}
							else {
								dr.set(property2Column(props[i].getName()), value);
							}
						}
					}
				}
				jdbc.insert(tableName, dr);
			}
		} catch (IntrospectionException e) {
			logger.error("IntrospectionException", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException", e);
		} catch (IllegalArgumentException e) {
			logger.error("IllegalArgumentException", e);
		} catch (InvocationTargetException e) {
			logger.error("InvocationTargetException", e);
		}
	}
	
	public void update(T sys, String [] exclusions, boolean isColumn, String identify,
			Object identifyValue){
		DataRow dr = new DataRow();
		String tableName = property2Column(sys.getClass().getSimpleName());
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(sys.getClass());
			PropertyDescriptor props[] = beanInfo.getPropertyDescriptors();
			Method method = null;
			Object value;
			Object noParams[] = new Object[0];
			boolean first = true;
			boolean isExclusionsNull = exclusions != null;
			boolean ignore;
			
			String prop;
			DateTime dt;
			for(int i = 0; i < props.length; ++i){
				prop = props[i].getName();
				if(first){
					if(prop.equalsIgnoreCase("class")){
						first = false;
						continue;
					}
				}
				if(isExclusionsNull){
					if(isColumn){ prop = column2Property(prop); }
					ignore = false;
					for(int j = 0; j < exclusions.length; ++j){
						if(prop.equalsIgnoreCase(exclusions[j])){
							ignore = true;
							break;
						}
					}
					if(ignore){ continue; }
				}
				method = props[i].getReadMethod();
				if(method == null){
					throw new IllegalArgumentException("没有名字为" + props[i].getName() + "的Getter方法");
				}
				else {
					value = method.invoke(sys, noParams);
					if(value != null){
						if(value instanceof DateTime){
							dt = (DateTime)value;
							if(dt.getMillisecond() > 0){
								try {
									dr.set(property2Column(props[i].getName()), df.parse(dt.ToString().replace("/", "-")));
								} catch (ParseException e) {
									logger.error("", e);
								}
							}
						}
						else if(value instanceof Integer){
							if((Integer)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
						}
						else if(value instanceof Long){
							if((Long)value >= 0L){ dr.set(property2Column(props[i].getName()), value); }
						}
						else if(value instanceof Short){
							if((Short)value >= 0){ dr.set(property2Column(props[i].getName()), value); }
						}
						else {
							dr.set(property2Column(props[i].getName()), value);
						}
					}
				}
			}
		} catch (IntrospectionException e) {
			logger.error("IntrospectionException", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException", e);
		} catch (IllegalArgumentException e) {
			logger.error("IllegalArgumentException", e);
		} catch (InvocationTargetException e) {
			logger.error("InvocationTargetException", e);
		}
		// update T_RES_COMPUTERSYSTEM set DISPLAY_NAME=?,CPU_ARCHITECTURE=?,OWNED_DISK_AVAILABLE_SPACK=?,
		// DETECTABLE=?,RAM_SIZE=?,SWAP_SIZE=?,TIME_ZONE=?,HOST_URL=?,OPERATIONAL_STATUS=?,IS_VIRTUAL=?,
		// OS_VERSION=?,OWNED_DISK_SPACE=?,COMPUTER_ID=?,DISK_AVAILABLE_SPACE=?,IP_ADDRESS=?,PROCESSOR_COUNT=?,
		// DOMAIN_NAME=?,CLUSTER_ID=?,NAME=?,FILESYSTEM_AVAILABLE_SPACE=?,WINDOWS_DOMAIN_ID=?,PROCESSOR_SPEED=?,
		// PROCESSOR_TYPE=?,DISK_SPACE=?,UPDATE_TIMESTAMP=?,NODE_HOSTED_ID=? where computer_id=?
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(tableName, dr, identify, identifyValue);
	}
	
	public Integer disableTask(Long computerId, Long serverId){
		String sql = null;
		if(serverId != null && serverId > 0){
			sql = "UPDATE t_server SET state=0 WHERE id=" + serverId;
		}
		else if(computerId != null && computerId > 0){
			sql = "UPDATE t_server SET state=0 WHERE id=(SELECT w.id FROM "+
				"(SELECT * FROM t_server) w JOIN t_res_computersystem r ON "+
				"w.IP_ADDRESS=r.IP_ADDRESS AND r.computer_id=" +computerId + ")";
		}
		if(sql != null){
			return getJdbcTemplate(WebConstants.DB_DEFAULT).update(sql);
		}
		return -1;
	}
	
	/**
	 * @see 将属性转换成字段
	 * @param property
	 * @return
	 */
	public String property2Column(String property){
		StringBuilder sb = new StringBuilder(property.length() + 10);
		char ch[] = property.toCharArray();
		sb.append(Character.toUpperCase(ch[0]));
		for(int i = 1; i < ch.length; ++i){
			if(Character.isUpperCase(ch[i])){
				sb.append('_');
				sb.append(ch[i]);
			}
			else {
				sb.append(Character.toUpperCase(ch[i]));
			}
		}
		return sb.toString();
	}
	
	/**
	 * @see 将字段转换成属性
	 * @param property
	 * @return
	 */
	public String column2Property(String column){
		StringBuilder sb = new StringBuilder(column.length());
		char ch[] = column.toCharArray();
		sb.append(Character.toLowerCase(ch[0]));
		int l = ch.length - 1;
		for(int i = 1; i < l; ++i){
			sb.append(ch[i] == '_'? Character.toUpperCase(ch[++i]): Character.toLowerCase(ch[i]));
		}
		sb.append(Character.toLowerCase(ch[l]));
		return sb.toString();
	}
	
	public Long getComputerId(String ip){
		String sql = "SELECT computer_id FROM t_res_computersystem WHERE IP_ADDRESS=?";
		return getJdbcTemplate(dbType).queryLong(sql, new Object[]{ip});
	}
	
	/**
	 * @see 查询是否存在BMC
	 * @param id
	 * @return
	 */
	public boolean isBMCResourceExists(long id){
		return getJdbcTemplate(dbType).queryLong("SELECT COUNT(HYPERVISOR_ID) AS c FROM t_res_bmc WHERE HYPERVISOR_ID=" + id) > 0;
	}
	
	/**
	 * @see 保存日志事件
	 * @param datas
	 */
	public void insertDeviceLogs(List<DataRow> datas){
		if(datas != null && datas.size() > 0){
			JdbcTemplate srDB = getJdbcTemplate(dbType);
			for(DataRow data : datas){
				srDB.insert("tndevicelog", data);
			}
		}
	}
	
	/**
	 * @see 保存设备状态
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> getAllStatusId(long hypervisorId) {
		String sql = "SELECT bmc_index as idx FROM t_status_sensors where hypervisor_id=" + hypervisorId;
		List<DataRow> drs = getJdbcTemplate(dbType).query(sql);
		if(drs != null && drs.size() > 0){
			Set<Long> ids = new HashSet<Long>(drs.size());
			for(DataRow dr : drs){
				ids.add(dr.getLong("idx"));
			}
			return ids;
		}
		return new HashSet<Long>(0);
	}
	
	/**
	 * @see 批量更新
	 * @param drs
	 * @param tableName
	 * @param identify
	 */
	public void update(List<DataRow> drs, String tableName, String identify){
		if(drs != null && drs.size() > 0){
			JdbcTemplate srDB = getJdbcTemplate(dbType);
			long id;
			for(DataRow data : drs){
				id = data.getLong(identify);
				data.remove(identify);
				srDB.update(tableName, data, identify, id);
			}
		}
	}
	
	/**
	 * @see 批量插入
	 * @param drs
	 * @param table
	 */
	public void insert(List<DataRow> drs, String table){
		if(drs != null && drs.size() > 0){
			JdbcTemplate srDB = getJdbcTemplate(dbType);
			for(DataRow data : drs){
				srDB.insert(table, data);
			}
		}
	}
}
