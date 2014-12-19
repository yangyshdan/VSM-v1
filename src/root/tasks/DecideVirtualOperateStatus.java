package root.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.huiming.base.jdbc.DataRow;
import com.huiming.base.timerengine.Task;
import com.huiming.base.util.security.DES;
import com.huiming.service.agent.AgentService;
import com.project.hmc.core.HmcBase;
import com.project.nmon.engn.Scp_Sftp;


public class DecideVirtualOperateStatus implements Task{

	public void execute() {

		BufferedReader br =null;
		String line=null;
		List<String> vmList = new ArrayList<String>();
		List<DataRow> hypervisorList =agentService.getHpNameAndID();
		for (int i = 0,len=hypervisorList.size(); i < len; i++) {
			DataRow hypervisor = hypervisorList.get(i);
			//获取连接hmc的登录配置信息
			DataRow loginInfo = agentService.getHMCLoginInfo(hypervisor.getInt("hmc_id"));
			HmcBase hmcBase =new HmcBase(loginInfo.getString("ip_address"),22,loginInfo.getString("user"),
					new DES().decrypt(loginInfo.getString("password")));
			try {
				Session session = hmcBase.openConn();
				//发送命令查询虚拟机的运行状态
				session.execCommand("lssyscfg -r lpar -m "+hypervisor.getString("name")+" -F name,state");
				session.waitForCondition(ChannelCondition.TIMEOUT, 10000);
				InputStream stdout = new StreamGobbler(session.getStdout());
				br=new BufferedReader(new InputStreamReader(stdout));
				while((line=br.readLine())!=null){
					if(line.indexOf("Running") == -1){
						String[] virtual = line.split(",");
						vmList.add(virtual[0]);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		StringBuffer sb = new StringBuffer();
		if(vmList!=null && vmList.size()>0){
			for (String vmName : vmList) {
				sb.append("'"+vmName+"',");
			}
			List<DataRow> vmOnciList = agentService.getVirtualComputerId(sb.substring(0, sb.length()-1));
			agentService.batchUpateVmState(vmOnciList);
		}
	}
	
	private AgentService agentService =new AgentService();
}
