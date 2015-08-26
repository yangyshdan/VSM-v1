package com.project.hmc.engn;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.libvirt.Connect;
import org.libvirt.ConnectAuth;
import org.libvirt.Domain;
import org.libvirt.DomainBlockInfo;
import org.libvirt.DomainBlockStats;
import org.libvirt.DomainInterfaceStats;
import org.libvirt.Network;
import org.libvirt.NodeInfo;
import org.libvirt.StoragePoolInfo;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.util.security.AES;
import com.huiming.service.agent.AgentService;
import com.huiming.service.prftimestamp.PrfTimestampService;
import com.huiming.sr.constants.SrContant;
import com.project.web.WebConstants;

public class LibvirtEngine {

	private Logger logger = Logger.getLogger(this.getClass());
	private static final String CONST_QEMU = "qemu";      
	private static final String CONST_ESX = "esx";
	private DecimalFormat deciFmt = new DecimalFormat("0.00");
	private DecimalFormat intFmt = new DecimalFormat("0");
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	PrfTimestampService timestampService = new PrfTimestampService();
	AgentService agentService = new AgentService();
	private String hypvType;
	private String ipAddress;
	private String user;
	private String password;
	private Connect connect;

	/**
	 * Constructor
	 */
	public LibvirtEngine() {
		super();
	}
	
	/**
	 * Constructor
	 * @param hypvType
	 * @param ipAddress
	 * @param user
	 * @param password
	 */
	public LibvirtEngine(String hypvType, String ipAddress, String user, String password) {
		super();
		this.hypvType = hypvType;
		this.ipAddress = ipAddress;
		this.user = user;
		this.password = password;
		//初始化连接
		initConnect();
	}

	/**
	 * 获取HYPERVISOR信息
	 * @return
	 */
	public DataRow getHypervisorInfo() {
		DataRow hyperRow = null;
		try {
			if (connect != null) {
				hyperRow = new DataRow();
				//Host Name
				hyperRow.set("hypervisor_name", connect.getHostName());
				//Gets the name of the HYPERVISOR software used.
				hyperRow.set("name", connect.getType());
				hyperRow.set("type", hypvType);
				//Gets the version level of the HYPERVISOR running.
				hyperRow.set("version", connect.getVersion());
				//MaxVcpus
				if (hypvType.equals(WebConstants.VIRT_PLAT_TYPE_KVM)) {
					hyperRow.set("allow_maxcpu", connect.getMaxVcpus(connect.getType()));
				}
				//Free Memory for the connection
//				System.out.println(Math.round(new Double(connect.getFreeMemory())/1024/1024));
				//Number of Domains
				hyperRow.set("vms_num", (connect.numOfDomains() + connect.numOfDefinedDomains()));
				//Number of Interfaces
				hyperRow.set("interfaces_num", (connect.numOfInterfaces() + connect.numOfDefinedInterfaces()));
				//Number of Networks
				hyperRow.set("networks_num", (connect.numOfNetworks() + connect.numOfDefinedNetworks()));
				//Number of StoragePools
				int numStorage = connect.numOfStoragePools();
				System.out.println(numStorage);
				//Storage Capacity
				long stoCap = 0;
				long stoAll = 0;
				long stoAva = 0;
				//List Storage Pool
				for (String poolName : connect.listStoragePools()) {
					StoragePoolInfo spInfo = connect.storagePoolLookupByName(poolName).getInfo();
					stoCap = stoCap + Long.parseLong(intFmt.format(new Double(spInfo.capacity)/1024/1024));
					stoAll = stoAll + Long.parseLong(intFmt.format(new Double(spInfo.allocation)/1024/1024));
					stoAva = stoAva + Long.parseLong(intFmt.format(new Double(spInfo.available)/1024/1024));
				}
				hyperRow.set("storage_capacity", stoCap);
				hyperRow.set("storage_allocation", stoAll);
				hyperRow.set("storage_available", stoAva);

				//统计分配的存储、CPU和内存
				long assignStorage = 0;
				int assignCpu = 0;
				long assignMem = 0;
				//List active domains
				for (int activeDomId : connect.listDomains()) {
					Domain domain = connect.domainLookupByID(activeDomId);
					//For KVM环境
					if (connect.getURI().startsWith(CONST_QEMU)) {
						//获取磁盘信息
						DomainBlockInfo blockInfo = domain.blockInfo("/var/lib/libvirt/images/" + domain.getName() + ".img");
						assignStorage = assignStorage + blockInfo.getCapacity();
					}
					assignCpu = assignCpu + domain.getInfo().nrVirtCpu;
					assignMem = assignMem + domain.getInfo().memory;
				}
				//List inactive domains
				for (String name : connect.listDefinedDomains()) {
					Domain domain = connect.domainLookupByName(name);
					//For KVM环境
					if (connect.getURI().startsWith(CONST_QEMU)) {
						//获取磁盘信息
						DomainBlockInfo blockInfo = domain.blockInfo("/var/lib/libvirt/images/" + domain.getName() + ".img");
						assignStorage = assignStorage + blockInfo.getCapacity();
					}
					assignCpu = assignCpu + domain.getInfo().nrVirtCpu;
//					assignMem = assignMem + domain.getInfo().memory;
				}
				hyperRow.set("storage_assigned", intFmt.format(assignStorage/1024/1024));
				hyperRow.set("assign_cpu", assignCpu);
				hyperRow.set("assign_memory", assignMem/1024);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return hyperRow;
	}

	/**
	 * 获取主机(物理机)信息
	 * @return
	 */
	public DataRow getHostInfo() {
		DataRow physicalRow = null;
		try {
			if (connect != null) {
				physicalRow = new DataRow();
				physicalRow.set("name", connect.getHostName());
				//Node Info
				NodeInfo nodeInfo = connect.nodeInfo();
				//Number of active CPUs
				physicalRow.set("available_cpu", nodeInfo.cpus);
				//Free Memory
				physicalRow.set("available_mem", intFmt.format(new Double(connect.getFreeMemory())/1024/1024));
				//OS Version
				physicalRow.set("os_version", connect.getType() + " " + connect.getVersion());
				//CPU model
//				physicalRow.set("cpu_architecture", nodeInfo.model);
				//Number of active CPUs
				physicalRow.set("processor_count", nodeInfo.cpus);
				//Expected CPU frequency
				physicalRow.set("processor_speed", nodeInfo.mhz);
				//Memory size in kilobytes
				physicalRow.set("ram_size", intFmt.format(new Double(nodeInfo.memory)/1024));
				//Storage Capacity
				long stoCap = 0;
				long stoAll = 0;
				long stoAva = 0;
				//List Storage Pool
				for (String poolName : connect.listStoragePools()) {
					StoragePoolInfo spInfo = connect.storagePoolLookupByName(poolName).getInfo();
					stoCap = stoCap + Long.parseLong(intFmt.format(new Double(spInfo.capacity)/1024/1024));
					stoAll = stoAll + Long.parseLong(intFmt.format(new Double(spInfo.allocation)/1024/1024));
					stoAva = stoAva + Long.parseLong(intFmt.format(new Double(spInfo.available)/1024/1024));
				}
				physicalRow.set("disk_space", stoCap);
				physicalRow.set("disk_available_space", stoAva);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return physicalRow;
	}

	/**
	 * 获取该物理机下所有虚拟机信息
	 * @return
	 */
	public List<DataRow> getDomainInfoList() {
		List<DataRow> domainList = new ArrayList<DataRow>();
		try {
			//列出所有处于启动(激活)状态的虚拟机)
			for (int activeDomId : connect.listDomains()) {
				DataRow vmRow = new DataRow();
				//根据Id,获取各个虚拟机的详细信息
				Domain domain = connect.domainLookupByID(activeDomId);
				vmRow.set("name", domain.getName());
				vmRow.set("uid", domain.getUUIDString());
				vmRow.set("os_version", domain.getOSType());
				vmRow.set("ram_size", (domain.getInfo().memory/1024));
//				vmRow.set("maximum_cpu_number", domain.getMaxVcpus());
				vmRow.set("maximum_cpu_processunit", 0);
				vmRow.set("total_memory", (domain.getInfo().memory/1024));
				vmRow.set("host_name", connect.getHostName());
//				vmRow.set("operational_status", domain.getInfo().state);
				vmRow.set("operational_status", "RUNNING");
				vmRow.set("processor_count", domain.getInfo().nrVirtCpu);
				vmRow.set("assigned_cpu_number", domain.getInfo().nrVirtCpu);

				//For KVM环境
				if (connect.getURI().startsWith(CONST_QEMU)) {
					//获取连接到该虚拟机的磁盘信息
					DomainBlockInfo blockInfo = domain.blockInfo("/var/lib/libvirt/images/" + domain.getName() + ".img");
					vmRow.set("disk_space", (blockInfo.getCapacity()/1024/1024));
//					vmRow.set("disk_available_space",(blockInfo.getAllocation()/1024/1024));
				}
				domainList.add(vmRow);
			}

			//所有处于停止状态的虚拟机
			for (String name : connect.listDefinedDomains()) {
				DataRow vmRow = new DataRow();
				//根据名称,获取各个虚拟机的详细信息
				Domain domain = connect.domainLookupByName(name);
				vmRow.set("name", domain.getName());
				vmRow.set("uid", domain.getUUIDString());
				vmRow.set("os_version", domain.getOSType());
				vmRow.set("ram_size", (domain.getInfo().memory/1024));
//				vmRow.set("maximum_cpu_number", domain.getMaxVcpus());
				vmRow.set("maximum_cpu_processunit", 0);
				vmRow.set("total_memory", (domain.getInfo().memory/1024));
				vmRow.set("host_name", connect.getHostName());
//				vmRow.set("operational_status", domain.getInfo().state);
				vmRow.set("operational_status", "STOP");
				//For KVM环境
				if (connect.getURI().startsWith(CONST_QEMU)) {
					//获取连接到该虚拟机的磁盘信息
					DomainBlockInfo blockInfo = domain.blockInfo("/var/lib/libvirt/images/" + domain.getName() + ".img");
					vmRow.set("disk_space", (blockInfo.getCapacity()/1024/1024));
//					vmRow.set("disk_available_space",(blockInfo.getAllocation()/1024/1024));
				}
				domainList.add(vmRow);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return domainList;
	}

	/**
	 * 获取虚拟机性能信息
	 * @param domain
	 * @return
	 */
	public void doCollectVirtMachinePerf(List<DataRow> virtMachineList) {
		try {
			List<DataRow> insertVmList = new ArrayList<DataRow>();
			for (int i = 0; i < virtMachineList.size(); i++) {
				DataRow vmRow = virtMachineList.get(i);
				String vmName = vmRow.getString("computer_name");
				String refComputerId = vmRow.getString("ref_computer_id");
				String hypervisorId = vmRow.getString("hypervisor_id");
//				String deviceType = vmRow.getString("device_type");
				DataRow hyperRow = agentService.getHypervisorInfo(hypervisorId);
				String hmcId = hyperRow.getString("hmc_id");
				String hyperIp = hyperRow.getString("ip_address");
				String hyperUser = hyperRow.getString("user");
				String hyperPwd = new AES(hmcId).decrypt(hyperRow.getString("password"),"UTF-8");
				String hyperVpt = hyperRow.getString("virt_plat_type");
				
				//暂时不支持VMWare
				if (hyperVpt.equals(WebConstants.VIRT_PLAT_TYPE_VMWARE)) {
					continue;
				}
				
				//建立连接
				LibvirtEngine engine = new LibvirtEngine(hyperVpt, hyperIp, hyperUser, hyperPwd);
				connect = engine.getConnect();
				//获取虚拟机
				Domain domain = connect.domainLookupByName(vmName);
				//获取Interface Name
				String domainXml = domain.getXMLDesc(0);
				String infaceXml = StringUtils.substringBetween(domainXml,"<interface", "</interface>");
				String targetXml = StringUtils.substringBetween(infaceXml,"<target", "/>");
				String infaceName = StringUtils.substringBetween(targetXml,"'", "'");
				//时间间隔
				final int interval = 3000;
				//定义变量
				/***** CPU *****/
				double startCpuTime;
				double endCpuTime;
				double cpuTime;
				double realTime;
				double cpuUsage;
				/***** Disk *****/
				double start_rd_bytes;
				double start_wr_bytes;
				double end_rd_bytes;
				double end_wr_bytes;
				double rd_usage;
				double wr_usage;
				/***** Network *****/
				double start_rx_bytes;
				double start_rx_packets;
				double start_tx_bytes;
				double start_tx_packets;
				double end_rx_bytes;
				double end_rx_packets;
				double end_tx_bytes;
				double end_tx_packets;
				double rx_usage;
				double tx_usage;
				double rx_packets_usage;
				double tx_packets_usage;

				//For CPU
				startCpuTime = domain.getInfo().cpuTime;

				//For Disk
				DomainBlockStats startStats = domain.blockStats("hda");
				start_rd_bytes = startStats.rd_bytes;
				start_wr_bytes = startStats.wr_bytes;

				//For Network
				DomainInterfaceStats startDis = domain.interfaceStats(infaceName);
				start_rx_bytes = startDis.rx_bytes;
				start_rx_packets = startDis.rx_packets;
				start_tx_bytes = startDis.tx_bytes;
				start_tx_packets = startDis.tx_packets;

				//隔指定时间段后再取一次
				Thread.sleep(interval);

				//For CPU
				endCpuTime = domain.getInfo().cpuTime;
				cpuTime = (int) (endCpuTime - startCpuTime)/1000000;
				realTime = (int) interval;
				cpuUsage = cpuTime/realTime;
//				System.out.println("CPU Usage : " + intFmt.format(cpuUsage*100));

				//For Disk
				DomainBlockStats endStats = domain.blockStats("hda");
				end_rd_bytes = endStats.rd_bytes;
				end_wr_bytes = endStats.wr_bytes;
				rd_usage = (end_rd_bytes - start_rd_bytes)/interval*1000/1024;
				wr_usage = (end_wr_bytes - start_wr_bytes)/interval*1000/1024;
//				System.out.println("Disk Read KB : " + deciFmt.format(rd_usage));
//				System.out.println("Disk Write KB : " + deciFmt.format(wr_usage));

				//For Network
				DomainInterfaceStats endDis = domain.interfaceStats(infaceName);
				end_rx_bytes = endDis.rx_bytes;
				end_rx_packets = endDis.rx_packets;
				end_tx_bytes = endDis.tx_bytes;
				end_tx_packets = endDis.tx_packets;
				rx_usage = (end_rx_bytes - start_rx_bytes)/interval*1000/1024;
				rx_packets_usage = (end_rx_packets - start_rx_packets)/interval*1000;
				tx_usage = (end_tx_bytes - start_tx_bytes)/interval*1000/1024;
				tx_packets_usage = (end_tx_packets - start_tx_packets)/interval*1000;
//				System.out.println("Network Recv KB : " + deciFmt.format(rx_usage));
//				System.out.println("Network Recv Packets : " + deciFmt.format(rx_packets_usage));
//				System.out.println("Network Send KB : " + deciFmt.format(tx_usage));
//				System.out.println("Network Send Packets : " + deciFmt.format(tx_packets_usage));

				//设置数据
				DataRow dataRow = new DataRow();
				dataRow.set("sample_time", sdf.format(new Date()));
				dataRow.set("interval_len", WebConstants.interval);
				//类型(1.实时数据;2.小时数据;3.天数据)
				dataRow.set("summ_type", SrContant.SUMM_TYPE_REAL);
				dataRow.set("computer_id", refComputerId);
				dataRow.set("computer_name", vmName);
				dataRow.set("device_type", WebConstants.DEVTYPE_HYPERVISOR);
				dataRow.set("cpu_usr_prct", intFmt.format(cpuUsage * 100));
				dataRow.set("disk_readdatarate_kb", deciFmt.format(rd_usage));
				dataRow.set("disk_writedatarate_kb", deciFmt.format(wr_usage));
				dataRow.set("net_recv_kb", deciFmt.format(rx_usage));
				dataRow.set("net_send_kb", deciFmt.format(tx_usage));
				dataRow.set("net_recv_packet", deciFmt.format(rx_packets_usage));
				dataRow.set("net_send_packet", deciFmt.format(tx_packets_usage));
				insertVmList.add(dataRow);
			}
			//关闭连接
			closeConnect();

			//批量插入性能数据
			agentService.batchInsertServerPerf(insertVmList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * main()
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));
//		LibvirtEngine libvirtEngine = new LibvirtEngine(WebConstants.VIRT_PLAT_TYPE_KVM, "192.168.1.80", null, null);
		LibvirtEngine libvirtEngine = new LibvirtEngine(WebConstants.VIRT_PLAT_TYPE_VMWARE, "192.168.100.115", "root", "root@1234");
		libvirtEngine.getHypervisorInfo();
	}
//                         
	/**
	 * 初始化信息,建立连接
	 */
	public void initConnect() {
		try {
			//建立连接
			//For KVM
			if (hypvType.equals(WebConstants.VIRT_PLAT_TYPE_KVM)) {
				connect = new Connect("qemu+tcp://" + ipAddress + "/system");
			//For VMWare
			} else if (hypvType.equals(WebConstants.VIRT_PLAT_TYPE_VMWARE)) {
				ConnectAuth auth = new CustomConnectAuth();
				connect = new Connect("esx://" + user + "@" + ipAddress + "/?no_verify=1", auth, 0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 连接用户验证
	 * @author Administrator
	 *
	 */
	class CustomConnectAuth extends ConnectAuth {
		public CustomConnectAuth() {
			credType = new CredentialType[1];
			credType[0] = CredentialType.VIR_CRED_NOECHOPROMPT;
		}
		@Override
		public int callback(Credential[] credentials) {
			for (int i = 0; i < credentials.length; i++) {
				Credential credential = credentials[i];
				if (credential.type == CredentialType.VIR_CRED_AUTHNAME) {
					//set login user
					credential.result = user;
					//Use the default result 
					if (credential.result.length() == 0) {
						credential.result = credential.defresult;
					}
				} else if (credential.type == CredentialType.VIR_CRED_NOECHOPROMPT) {
					//set login password
					credential.result = password;
				} else {
					return -1;
				}
			}
			return 0;
		}
	}

	/**
	 * 获取连接
	 * 
	 * @param connUrl
	 * @return
	 */
	public Connect getConnect() {
		try {
			if (connect != null) {
				return connect;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return connect;
	}

	/**
	 * 关闭连接,释放资源
	 */
	public void closeConnect() {
		try {
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHypvType() {
		return hypvType;
	}

	public void setHypvType(String hypvType) {
		this.hypvType = hypvType;
	}

}
