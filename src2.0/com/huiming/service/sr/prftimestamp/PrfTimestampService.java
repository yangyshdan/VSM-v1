package com.huiming.service.sr.prftimestamp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class PrfTimestampService extends BaseService {
	//添加实时表
	public void addPrfTimestamp(List<DataRow> prftimestamps,Integer subSystemID){
		SrContant.TIME_FKEY = SrContant.getKey();     //存当前时间点时间ID
		for (int i = 0; i <prftimestamps.size(); ++i) {
			DataRow prftimestamp = prftimestamps.get(i);
			Timestamp stime=SrContant.getTime(prftimestamp.get("sampl_time").toString());
			prftimestamp.set("time_id", SrContant.TIME_FKEY);
			prftimestamp.set("sampl_time", stime);
			prftimestamp.set("subsystem_id",subSystemID);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_timestamp", prftimestamp);
		}
	}
	
	/**
	 * 得到实时表时间数据
	 * @param startTime
	 * @param endTime
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getTimestampInfo(String startTime,String endTime,Integer subsystemId){
		StringBuffer sb = new StringBuffer("SELECT AVG(interlva_len) AS INTERLVA_LEN,SUMM_TYPE,subsystem_name,subsystem_id ");
		List<Object> args = new ArrayList<Object>();
		sb.append("FROM t_prf_timestamp where 1=1 ");
		if(startTime!=null && startTime.length()>0){
			sb.append("and SAMPL_TIME > ? ");
			args.add(startTime);
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and SAMPL_TIME < ? ");
			args.add(endTime);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY subsystem_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}	
	
	/**
	 * 得到小时表时间数据
	 * @param startTime
	 * @param endTime
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getTimestamp2Info(String startTime,String endTime,Integer subsystemId){
		StringBuffer sb = new StringBuffer("SELECT AVG(interlva_len) AS INTERLVA_LEN,SUMM_TYPE,subsystem_name,subsystem_id ");
		List<Object> args = new ArrayList<Object>();
		sb.append("FROM t_prf_timestamp2 where 1=1 ");
		if(startTime!=null && startTime.length()>0){
			sb.append("and SAMPL_TIME > ? ");
			args.add(startTime);
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and SAMPL_TIME < ? ");
			args.add(endTime);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY subsystem_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	
	/**
	 * 得到天表时间数据
	 * @param startTime
	 * @param endTime
	 * @param subsystemId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getTimestamp3Info(String startTime,String endTime,Integer subsystemId){
		StringBuffer sb = new StringBuffer("SELECT AVG(interlva_len) AS INTERLVA_LEN,SUMM_TYPE,subsystem_name,subsystem_id ");
		List<Object> args = new ArrayList<Object>();
		sb.append("FROM t_prf_timestamp3 where 1=1 ");
		if(startTime!=null && startTime.length()>0){
			sb.append("and SAMPL_TIME > ? ");
			args.add(startTime);
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and SAMPL_TIME < ? ");
			args.add(endTime);
		}
		if(subsystemId!=null && subsystemId!=0){
			sb.append("and subsystem_id = ? ");
			args.add(subsystemId);
		}
		sb.append("GROUP BY subsystem_id");
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sb.toString(),args.toArray());
	}
	//插入小时表
	public void addPerhourInfo(Long timeId,List<DataRow> times){
		if(times!=null && times.size()>0){
			for (DataRow dataRow : times) {
				dataRow.set("time_id", timeId);
				dataRow.set("sampl_time", new Date());
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_timestamp2", dataRow);
			}
		}
	}
	//插入天表
	public void addPerdayInfo(Long timeId,List<DataRow> times){
		if(times!=null && times.size()>0){
			for (DataRow dataRow : times) {
				dataRow.set("time_id", timeId);
				dataRow.set("sampl_time", new Date());
				getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_timestamp3", dataRow);
			}
		}
	}
	
	/**
	 * 删除时间表数据，级联删性能表数据
	 * @param startTime
	 * @param endTime
	 */
	public void deleteTimeInfo(String startTime,String endTime){
		String sql ="DELETE from t_prf_timestamp where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if(startTime!=null && startTime.length()>0){
			sb.append("and SAMPL_TIME > '"+startTime+"' ");
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and SAMPL_TIME < '"+endTime+"' ");
		}
		if(startTime==null || startTime.length()==0){
			if(endTime==null || endTime.length()==0){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -SrContant.DEFAULT_DAY_FOR_ACTUAL_PRF);
				String defaultTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
				sb.append("and SAMPL_TIME < '"+defaultTime+"' ");
			}
		}
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sb.toString());
	}
	
	public void deleteTime2Info(String startTime,String endTime){
		String sql="delete from t_prf_timestamp2 where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if(startTime!=null && startTime.length()>0){
			sb.append("and SAMPL_TIME > '"+startTime+"' ");
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and SAMPL_TIME < '"+endTime+"' ");
		}
		if(startTime==null || startTime.length()==0){
			if(endTime==null || endTime.length()==0){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -SrContant.DEFAULT_DAY_FOR_ACTUAL_PRFHOUER);
				String defaultTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
				sb.append("and SAMPL_TIME < '"+defaultTime+"' ");
			}
		}
	}
	
	public void deleteTime3Info(String startTime,String endTime){
		String sql="delete from t_prf_timestamp3 where 1=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if(startTime!=null && startTime.length()>0){
			sb.append("and SAMPL_TIME > '"+startTime+"' ");
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and SAMPL_TIME < '"+endTime+"' ");
		}
		if(startTime==null || startTime.length()==0){
			if(endTime==null || endTime.length()==0){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -SrContant.DEFAULT_DAY_FOR_ACTUAL_PRFDAY);
				String defaultTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
				sb.append("and SAMPL_TIME < '"+defaultTime+"' ");
			}
		}
	}
}
