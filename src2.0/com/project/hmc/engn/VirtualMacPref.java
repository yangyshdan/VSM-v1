package com.project.hmc.engn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.DateHelper;
import com.huiming.base.util.security.AES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;
import com.project.web.WebConstants;

/**
 * 获取虚拟机性能数据
 * 
 * @author lic
 * 
 */
public class VirtualMacPref {

	private static Logger logger = Logger.getLogger(VirtualMacPref.class);
	private DecimalFormat decimalFormat = new DecimalFormat("0.0000");

	public void getResult(final String marker) throws Exception {
		List<DataRow> hypervisorList = agentService.getHpNameAndID();
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		final String regEx = "([\\w]+)=(([0-9]{1,2}/[0-9]{1,2}/[\\d]{4} [0-9]{1,2}:[0-9]{1,2}:[0-9]{2})|([\\d]+)|([\\w]+))";
		
		// 所有物理机hypervisorList.size();
		for (int i = 0, hlLeng = hypervisorList.size(); i < hlLeng; i++) {
			final DataRow hypervisor = hypervisorList.get(i);
			DataRow loginInfo = agentService.getHMCLoginInfo(hypervisor.getInt("hmc_id"));
			HmcBase base = new HmcBase(loginInfo.getString("ip_address"), 22, loginInfo.getString("user"), new AES(loginInfo.getString("id")).decrypt(loginInfo.getString("password"),"UTF-8"));
			List<DataRow> virtualMacList = agentService.getVirtualNameAndIdIsDetectable(hypervisor.getInt("hypervisor_id"));
			// 物理机下所有的虚拟机
			for (int j = 0; j < virtualMacList.size(); j++) {
				final DataRow virtualMac = virtualMacList.get(j);
				// 虚拟机性能list
				final List<DataRow> virtualMacPrefList = new ArrayList<DataRow>();
				final Connection conn = base.getConn();
				if(conn!=null){
					Runnable runnable = new Runnable() {
						public void run() {
							BufferedReader br = null;
							try {
								// 正式环境下删除
//							 br = new BufferedReader(new FileReader(
//							 "D:/yingChuang/fsm/out5.txt"));
								Session session = conn.openSession();
								session.execCommand("lslparutil -r lpar -m "+ hypervisor.getString("name")+ " --filter \"lpar_names="
										+ virtualMac.getString("name") + "\" -n 2");
								
								logger.info("开始执行命令：lslparutil -r lpar -m "+ hypervisor.getString("name")+ " --filter \"lpar_names="
										+ virtualMac.getString("name") + "\" -n "+WebConstants.PERFORMANCE_COUNT);
								session.waitForCondition(ChannelCondition.TIMEOUT,10000);
								InputStream stdout = new StreamGobbler(session.getStdout());
								//正式环境
								br = new BufferedReader(new InputStreamReader(stdout));
								String line;
								double temp_entitled_cycles = 0D;
								double temp_capped_cycles = 0D;
								double temp_uncapped_cycles = 0D;
								double temp_idle_cycles = 0D;
								double temp_time_cycles= 0D;
								Integer count = new Integer(0);
								while ((line = br.readLine()) != null&& !line.trim().equals("No results were found.")) {
									
									DataRow virtualMacPref = new DataRow();
									Matcher matcher = Pattern.compile(regEx).matcher(line);
									virtualMacPref.set("summ_type", 1);
									virtualMacPref.set("computer_id", virtualMac.getInt("computer_id"));
									virtualMacPref.set("computer_name", virtualMac.getString("name"));
									virtualMacPref.set("perf_marker", marker);
									double idle_cycles = 0D;
									double entitled_cycles = 0D;
									double capped_cycles = 0D;
									double uncapped_cycles = 0D;
									double time_cycles = 0D;
									double processor_units_utilized=0D;
									++count;
									while (matcher.find()) {
										String key = matcher.group(1);
										String value = matcher.group(2);
										if (key.equals("time")) {
											Date time = new SimpleDateFormat("MM/dd/yyyy HH:ss:mm").parse(value);
											virtualMacPref.set("sample_time",DateHelper.formatTime(time));

										}else if(key.equals("time_cycles")){
											time_cycles = Double.parseDouble(value.trim());
										}else if (key.equals("idle_cycles")) {
											idle_cycles = Double.parseDouble(value.trim());
										} else if (key.equals("entitled_cycles")) {
											entitled_cycles = Double.parseDouble(value.trim());
										} else if (key.equals("capped_cycles")) {
											capped_cycles = Double.parseDouble(value.trim());
										} else if (key.equals("uncapped_cycles")) {
											uncapped_cycles = Double.parseDouble(value.trim());
										}
									}
									if (count == 1) {
										temp_idle_cycles = idle_cycles;
										temp_entitled_cycles = entitled_cycles;
										temp_capped_cycles = capped_cycles;
										temp_uncapped_cycles = uncapped_cycles;
										temp_time_cycles= time_cycles;
										continue;
									}
									DataRow data = agentService.getVirtualProcessingModeByName(virtualMac.getString("name"));
									double sub_idle_cycles = sub(idle_cycles, temp_idle_cycles);
									double sub_capped_cycles = sub(capped_cycles, temp_capped_cycles);
									double sub_uncapped_cycles = sub(uncapped_cycles, temp_uncapped_cycles);
									double sub_entitled_cycles = sub(entitled_cycles, temp_entitled_cycles);
									double sub_time_cycles = sub(time_cycles, temp_time_cycles);
									if (entitled_cycles > 0) {
										
										double cpu_busy = 0;
										double cpu_idle =0;
										
										if(data.getString("processing_mode").equals("Shared")){
											cpu_busy = Double.parseDouble(decimalFormat.format(sub_entitled_cycles==0?0:((sub_capped_cycles+sub_uncapped_cycles)/sub_entitled_cycles)));
										}else{
											cpu_busy = Double.parseDouble(decimalFormat.format(sub_capped_cycles==0?0:((sub_capped_cycles-sub_idle_cycles)/sub_capped_cycles)));
										}
										cpu_idle = (1-cpu_busy)>0?(1-cpu_busy):0;
										virtualMacPref.set("cpu_idle_prct",cpu_idle * 10000D);
										virtualMacPref.set("cpu_busy_prct",cpu_busy * 10000D);
										
									} else {
										virtualMacPref.set("cpu_idle_prct", "10000");
										virtualMacPref.set("cpu_busy_prct", "0.00");
									}
									
									if(time_cycles>0){
										
										if(data.getString("processing_mode").equals("Shared")){
											
											processor_units_utilized=Double.parseDouble(decimalFormat.format((sub_capped_cycles+sub_uncapped_cycles)/sub_time_cycles));
										}else{
											processor_units_utilized=Double.parseDouble(decimalFormat.format((sub_capped_cycles-sub_idle_cycles)/sub_time_cycles));
										}
									}else{
										processor_units_utilized=0;
									}
									
									virtualMacPref.set("interval_len", WebConstants.interval);
									virtualMacPref.set("mem_used_prct", "100");
									virtualMacPref.set("processor_units_utilized",processor_units_utilized);
									virtualMacPrefList.add(virtualMacPref);
									
								}
								
//							if (!virtualMacPrefList.isEmpty()) {
//								
//								Date date1 = new Date(
//										DateHelper.parseString(
//														virtualMacPrefList.get(0).getString("sample_time")).getTime());
//								Date date2 = new Date(
//										DateHelper.parseString(
//												virtualMacPrefList.get(1).getString("sample_time")).getTime());
//								
//								for (int k = 0,g=virtualMacPrefList.size(); k <g ; k++) {
//									
//									virtualMacPrefList.get(k).set("interlva_len", DateHelper.getDateMiliDispersion(date1,date2)/1000L);
//									
//								}
//							}
								agentService.batchInsertPrf(virtualMacPrefList);
								logger.info("结束命令：lslparutil -r lpar -m "
										+ hypervisor.getString("name")
										+ " --filter \"lpar_names="
										+ virtualMac.getString("name") + "\" -n 2");
								
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ParseException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								try {
									if(br!=null){
										br.close();
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
								conn.close();
							}
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
					executorService.execute(runnable);
				}
			}
		}
		try {
			// 关闭线程池
			executorService.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("虚拟机性能结束++++++++++++++++++++++++++++");
	}

	/**
	 * double 相加
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public double sum(double d1, double d2) {

		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.add(bd2).doubleValue();
	}

	/**
	 * double 相减
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public double sub(double d1, double d2) {

		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.subtract(bd2).doubleValue();
	}

	/**
	 * double 乘法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public double mul(double d1, double d2) {

		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.multiply(bd2).doubleValue();
	}

	/**
	 * double 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            四舍五入 小数点位数
	 * @return
	 */
	public double div(double d1, double d2, int scale) {

		// 当然在此之前，你要判断分母是否为0，
		// 为0你可以根据实际需求做相应的处理

		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static void main(String[] args) {

		VirtualMacPref vmp = new VirtualMacPref();
		// double a= 710424087709640D;
		// double b= 109193837780587267D;
		//    	
		// System.out.println(vmp.div(a,b,2));
		// vmp.getResult();

//		double ec = 111050427383418D, cp = 94685062746097D, uncp = 836639634925029D;
//
//		double ec1 = 111047334751991D, cp1 = 94681969754019D, uncp1 = 836611808684087D;
//
//		double ec2 = 111044241660906D, cp2 = 94679978193353D, uncp2 = 836594910469198D;
//
//		double ec3 = 111041150608522D, cp3 = 94676887248873D, uncp3 = 836567102985317D;
//
//		double ec4 = 111038053679222D, cp4 = 94673790537889D, uncp4 = 836539250098915D;
//
//		System.out.println(new DecimalFormat("0.0000").format(((cp - cp1) + (uncp - uncp1)) / (ec - ec1)));
//		System.out.println(((cp1 - cp2) + (uncp1 - uncp2)) / (ec1 - ec2));
//		System.out.println(((cp2 - cp3) + (uncp2 - uncp3)) / (ec2 - ec3));
//		System.out.println(((cp3 - cp4) + (uncp3 - uncp4)) / (ec3 - ec4));
//
//		System.out.println(new DecimalFormat("0.0000").format(vmp.sum(vmp.sub(cp, cp1), vmp.sub(uncp, uncp1))
//				/ vmp.sub(ec, ec1)));
		
		try {
//			vmp.getResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// private HmcInstructions hmcInstructions = new HmcInstructions();
	private AgentService agentService = new AgentService();
}
