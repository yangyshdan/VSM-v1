package root.devicescript;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.service.alert.NasAlertService;
import com.huiming.service.usercon.UserConService;
import com.project.hmc.core.HmcBase;


public class NasScript {

	//{svc:'21,38',bsp:'15,37',ds:'25'}
	public void getResult(){
		
		String line ="";
		String regex= "([\\w.]+[:\\w]*):([\\w]+)\\s+(\\d{2}\\w+\\d [0-9]{1,2}:[0-9]{1,2}:[0-9]{2})";
		Pattern pattern = Pattern.compile(regex);
		List<DataRow> list = new ArrayList<DataRow>();
		HmcBase hmcBase = new HmcBase("hostName", 22, "userName", "password");
//		Connection conn = null;
//		Session session = null;
		try {
//			conn = hmcBase.getConn();
//			session = conn.openSession();
//			session.execCommand("ems event status");
//			session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
//			InputStream stdout=new StreamGobbler(session.getStdout());
//			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader br = new BufferedReader(new FileReader("d://testing_environment//ceshi.txt"));
			while ((line=br.readLine())!=null) {
				DataRow data= new DataRow();
				
				Matcher matcher = pattern.matcher(line);
				while(matcher.find()){
					System.out.println("事件"+matcher.group(1)+"  状态"+matcher.group(2)+"  时间"+matcher.group(3));//事件
//					System.out.println(matcher.group(2));//状态
//					data.set("controller_status", "");
//					data.set("device_id", "");
//					data.set("battery_status", "");
//					data.set("power_status", "");
//					data.set("disk_status", "");
//					data.set("hea_status", "");
//					data.set("network_status", "");
				}
//				list.add(data);
			}
			
//			session.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
//			conn.close();
		}
	}
	
	public void getAllNasDevice(){
		
		HmcBase hmcBase = new HmcBase("hostName", 22, "userName", "password");
		String line=null;
		Connection conn=null;
		Session session=null;
		try {
			conn =hmcBase.getConn();
			session = conn.openSession();
			session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
			session.execCommand("nas_storage -list");
			InputStream stdout= new StreamGobbler(session.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			
			while((line=br.readLine())!=null){
				
			}
			session.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		
	}
}
