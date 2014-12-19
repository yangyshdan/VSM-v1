package com.huiming.service.report;

import java.util.ArrayList;
import java.util.List;

import com.huiming.base.jdbc.DBPage;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.project.web.WebConstants;

public class ReportService extends BaseService{
	public int getmaxId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(id) from tnreport");
	}
	public int getReportTaskConId(){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryInt("select max(id) from tnreport_task_config");
	}
	public int addReport(DataRow row){
		int id = getmaxId()+1;
		row.set("id", id);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tnreport", row);
		return id;
	}
	public DataRow getByTimeType(String timeType){
		String sql="SELECT id,time_type FROM tnreport_task_config WHERE time_type=?";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql, new Object[]{timeType});
	}
	public void addReportTaskConfig(DataRow row){
		row.set("id", getReportTaskConId()+1);
		getJdbcTemplate(WebConstants.DB_DEFAULT).insert("tnreport_task_config", row);
	}
	public void updateReportTaskConfig(DataRow row){
		getJdbcTemplate(WebConstants.DB_DEFAULT).update("tnreport_task_config", row, "id", row.getString("id"));
	}
	public DBPage getReportPage(String name,String reportType,String startTime,String endTime,int curPage,int numPerPage){
		String sql="select r.* from tnreport r where 1=1 ";
		StringBuffer sb= new StringBuffer(sql);
		List<Object> args = new ArrayList<Object>();
		if(name!=null && name.length()>0){
			sb.append("and r.the_display_name like ? ");
			args.add("%"+name+"%");
		}
		if(reportType!=null && reportType.length()>0){
			sb.append("and r.report_type = ? ");
			args.add(reportType);
		}
		if(startTime!=null && startTime.length()>0){
			sb.append("and r.create_time >= ? ");
			args.add(startTime);
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and r.create_time <= ? ");
			args.add(endTime);
		}
		sb.append("order by r.create_time desc");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryPage(sb.toString(),args.toArray(), curPage, numPerPage);
	}
	
	public DataRow getReportInfo(int id){
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select * from tnreport where id = "+id);
	}
	
	public void delReport(int id){
		getJdbcTemplate(WebConstants.DB_DEFAULT).delete("tnreport", "id", id);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubgroupDevice(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName){
		String sql="select t."+keyName+" as id,t."+displayName+" as value from "+talbeName+" t where 1=1 ";
		if(ParentkeyName!=null && ParentkeyName.length()>0){
			sql+="and t."+ParentkeyName+" = "+parentId+" ";
		}
		sql+="group by t."+keyName+",t."+displayName;
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubDevices(Integer pId,String pName,String kName,String sName,String tname,String resource){
		String sql="select t."+kName+" as id,t."+sName+" as value from "+tname+" t where 1=1 ";
		if(pName!=null && pName.length()>0){
			sql+="and t."+pName+" = "+pId+" ";
		}
		sql+="group by t."+kName+",t."+sName;
		return getJdbcTemplate(resource).query(sql,2);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getStoragebyOStype(String osType){
		String sql="select subsystem_id as id,the_display_name as value " +
				"from v_res_storage_subsystem " +
				"where os_type in ("+WebConstants.STORAGE_OS_TYPE.getString(osType.toUpperCase())+")";
		return getJdbcTemplate(WebConstants.DB_TPC).query(sql);
	}
	
	public List<DataRow> getSubgroupDevice2(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName,String dbType){
		String sql="select t."+keyName+" as id,t."+displayName+" as value from "+talbeName+" t where 1=1 ";
		if(ParentkeyName!=null && ParentkeyName.length()>0){
			sql+="and t."+ParentkeyName+" = "+parentId+" ";
		}
		sql+="group by t."+keyName+",t."+displayName;
		return getJdbcTemplate(dbType).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubgrouphost(Integer parentId,String ParentkeyName,String keyName,String displayName,String talbeName){
		String sql="select t."+keyName+" as id,t."+displayName+" as value from "+talbeName+" t where t."+ParentkeyName+" = "+parentId+" group by t."+keyName+",t."+displayName;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getemcSys(int sysId){
		String sql="select subsystem_id as id,model as value from t_res_storagesubsystem where subsystem_id ="+sysId;
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql);
	}
	
	/**
	 * 得到任务报表配置信息
	 * @return
	 */
	public DataRow getTaskReportConfig(String exeType1) {
		String sql="select * from tnreport_task_config where exe_type1 = ? and report_type = 1";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap(sql,new Object[]{exeType1});
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSubDevicePerf(String type){
		String sql="SELECT * FROM tnprffields where FStorageType = ? GROUP BY FStorageType,FdevType ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{type});
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getKPIInfoByTitle(String fstorageType,String subType,String title){
		String sql="SELECT * FROM tnprffields where FStorageType = ? and FdevType = ? and ftitle in ("+title+") ";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql,new Object[]{fstorageType,subType});
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getInitConfig(String sql,Object[] args,String dbType){
		if(args!=null && args.length>0){
			return getJdbcTemplate(dbType).query(sql, args);
		}else{
			return getJdbcTemplate(dbType).query(sql);
		}
	}
}
