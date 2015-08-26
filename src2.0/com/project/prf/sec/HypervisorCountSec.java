package com.project.prf.sec;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.DateHelper;
import com.huiming.service.agent.SecService;



public class HypervisorCountSec {

	public void execHourly(){
		
		DataRow data = secService.getHypervisorUpDate();
		Date date =DateHelper.parseString(data.getString("sample_time"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int hour =calendar.get(Calendar.HOUR_OF_DAY);
		String startTime=null;
		if(hour==0){
			calendar.add(Calendar.DAY_OF_MONTH, -1);//零点的时候往前推一天
			startTime = DateHelper.formatDate(calendar.getTime())+" "+"23:00:00";
		}else{
			startTime = DateHelper.formatDate(date)+" "+(calendar.get(Calendar.HOUR_OF_DAY)-1)+":00:00";
		} 
		String endTime = DateHelper.formatDate(date)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":00:00";
										
		List<DataRow> inserList = secService.getHypervisorList(startTime, endTime,"2");
		Date dateStr =DateHelper.parseString(endTime);
		java.sql.Timestamp dateDB =new java.sql.Timestamp(dateStr.getTime());
		//插入物理机hourly数据
		secService.batchInsertPrf(inserList, dateDB, "2");
	}
	
	public void execDaily(){
		
		DataRow data = secService.getHypervisorUpDate();
		Date date =DateHelper.parseString(data.getString("sample_time"));
		String time =  DateHelper.formatDate(DateHelper.getDataDiff(date, 1));
		String startTime = time+" 00:00:00";
		String endTime = time+" 23:59:59";
		
		List<DataRow> list = secService.getHypervisorList(startTime, endTime,"3");
		
		Date dateStr =DateHelper.parseString(endTime,"yyyy-MM-dd");
		java.sql.Timestamp dateDB=  new java.sql.Timestamp(dateStr.getTime());
		
		secService.batchInsertPrf(list, dateDB, "3");
		
	}
	
	
	private SecService secService =new SecService();
	
	public static void main(String[] args) {

		HypervisorCountSec hcs =new HypervisorCountSec();
		
//		hcs.execHourly1("2014-01-19 05:00:00","2014-01-19 06:00:00");
//		hcs.execHourly1("2014-01-19 06:00:00","2014-01-19 07:00:00");
//		hcs.execHourly1("2014-01-19 07:00:00","2014-01-19 08:00:00");
//		hcs.execHourly1("2014-01-19 08:00:00","2014-01-19 09:00:00");
//		hcs.execHourly1("2014-01-19 09:00:00","2014-01-19 10:00:00");
//		hcs.execHourly1("2014-01-19 10:00:00","2014-01-19 11:00:00");
//		hcs.execHourly1("2014-01-19 11:00:00","2014-01-19 12:00:00");
//		
//		hcs.execHourly1("2014-01-19 12:00:00","2014-01-19 13:00:00");
//		hcs.execHourly1("2014-01-19 13:00:00","2014-01-19 14:00:00");
//		hcs.execHourly1("2014-01-19 14:00:00","2014-01-19 15:00:00");
//		hcs.execHourly1("2014-01-19 15:00:00","2014-01-19 16:00:00");
//		hcs.execHourly1("2014-01-19 16:00:00","2014-01-19 17:00:00");
//		hcs.execHourly1("2014-01-19 17:00:00","2014-01-19 18:00:00");
//		hcs.execHourly1("2014-01-19 18:00:00","2014-01-19 19:00:00");
//		hcs.execHourly1("2014-01-19 19:00:00","2014-01-19 20:00:00");
//		hcs.execHourly1("2014-01-19 20:00:00","2014-01-19 21:00:00");
		hcs.execDaily1("2014-01-19 00:00:00", "2014-01-19 23:59:59");
	}
	
	public void execHourly1(String st,String et){
		
		DataRow data = secService.getHypervisorUpDate();
		Date date =DateHelper.parseString(data.getString("sample_time"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		String startTime =DateHelper.formatDate(date)+" "+(calendar.get(Calendar.HOUR_OF_DAY)-1)+":00:00";
		String endTime = DateHelper.formatDate(date)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":00:00";
		
		String s=st;
		String s1=et;
		
		List<DataRow> list = secService.getHypervisorList(s, s1,"2");
	
		Date dateStr =DateHelper.parseString(s1);
		java.sql.Timestamp dateDB =new java.sql.Timestamp(dateStr.getTime());
		
		secService.batchInsertPrf(list, dateDB, "2");
	}
	
	public void execDaily1(String st,String et){
		
		DataRow data = secService.getHypervisorUpDate();
		Date date =DateHelper.parseString(data.getString("sample_time"));
		
		String time =  DateHelper.formatDate(DateHelper.getDataDiff(date, 1));
		String startTime = time+" 00:00:00";
		String endTime = time+" 23:59:59";
		
		String s=st;
		String s1=et;
		
		List<DataRow> list = secService.getHypervisorList(s, s1,"3");
		
		Date dateStr =DateHelper.parseString(s1,"yyyy-MM-dd");
		java.sql.Timestamp dateDB=  new java.sql.Timestamp(dateStr.getTime());
		
		secService.batchInsertPrf(list, dateDB, "3");
		
	}
}
