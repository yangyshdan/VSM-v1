package com.huiming.service.prftimestamp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.service.BaseService;
import com.huiming.sr.constants.SrContant;
import com.huiming.sr.constants.SrTblColConstant;
import com.project.web.WebConstants;

public class PrfTimestampService extends BaseService {
	//添加实时表
	public void addPrfTimestamp(List<DataRow> prftimestamps,Integer subSystemID,String storageType){
		SrContant.TIME_FKEY = SrContant.getKey();     //存当前时间点时间ID
		for (int i = 0; i <prftimestamps.size(); ++i) {
			DataRow prftimestamp = prftimestamps.get(i);
			Timestamp stime=SrContant.getTime(prftimestamp.get("sample_time").toString());
			prftimestamp.set("time_id", SrContant.TIME_FKEY);
			prftimestamp.set("sample_time", stime);
			prftimestamp.set("subsystem_id",subSystemID);
			prftimestamp.set("device_type", storageType);
			prftimestamp.set("summ_type", 1);
			getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_timestamp", prftimestamp);
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
			sb.append("and SAMPLE_TIME > '"+startTime+"' ");
		}
		if(endTime!=null && endTime.length()>0){
			sb.append("and SAMPLE_TIME < '"+endTime+"' ");
		}
		if(startTime==null || startTime.length()==0){
			if(endTime==null || endTime.length()==0){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -SrContant.DEFAULT_DAY_FOR_ACTUAL_PRF);
				String defaultTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
				sb.append("and SAMPLE_TIME < '"+defaultTime+"' ");
			}
		}
		getJdbcTemplate(WebConstants.DB_DEFAULT).update(sb.toString());
	}
	
	/**
	 * 添加实时表
	 * @param prfTimestamp
	 * @return
	 */
	public DataRow addPrfTimestamps(DataRow prfTimestamp) {
		DataRow result = new DataRow();
		//获取subsystemID
		DataRow row1 = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select subsystem_id from t_res_storagesubsystem where name = ?", new Object[]{prfTimestamp.getString(SrTblColConstant.TT_SUBSYSTEM_NAME)});
		if (row1 != null && row1.size() > 0) {
			prfTimestamp.set("subsystem_id", row1.getString("subsystem_id"));
			result.set(SrTblColConstant.TT_SUBSYSTEM_ID, row1.getString("subsystem_id"));
			//判断该时间是否存在
			DataRow row2 = getJdbcTemplate(WebConstants.DB_DEFAULT).queryMap("select time_id from t_prf_timestamp where subsystem_id = ? and summ_type = ? and sample_time = ?", new Object[]{row1.getLong(SrTblColConstant.TT_SUBSYSTEM_ID),SrContant.SUMM_TYPE_REAL,prfTimestamp.getString(SrTblColConstant.TT_SAMPLE_TIME)});
			if (row2 != null && row2.size() > 0) {
				result.set(SrTblColConstant.TT_TIME_ID,row2.getString("time_id"));
			} else {
				String timeId = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_timestamp", prfTimestamp);
				result.set(SrTblColConstant.TT_TIME_ID,timeId);
			}
		}
		return result;
	}
	
	/**
	 * 获取时间信息列表(t_prf_timestamp)
	 * @param startTime
	 * @param endTime
	 * @param systemId
	 * @param devType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataRow> getTimestampInfos(String startTime, String endTime, Integer systemId, String devType) {
		String sql = "select time_id from t_prf_timestamp where subsystem_id = ? and summ_type = ? and device_type = ? and sample_time between ? and ? order by sample_time";
		return getJdbcTemplate(WebConstants.DB_DEFAULT).query(sql, new Object[]{systemId,SrContant.SUMM_TYPE_REAL,devType,startTime,endTime});
	}
	
	/**
	 * 添加时间(小时/天)信息
	 * @param prfTimestamp
	 * @return
	 */
	public DataRow addPerHourAndDayPrfTimestamp(DataRow prfTimestamp) {
		DataRow result = new DataRow();
		String timeId = getJdbcTemplate(WebConstants.DB_DEFAULT).insert("t_prf_timestamp", prfTimestamp);
		result.set(SrTblColConstant.TT_SUBSYSTEM_ID, prfTimestamp.getString("subsystem_id"));
		result.set(SrTblColConstant.TT_TIME_ID,timeId);
		return result;
	}
	
}
