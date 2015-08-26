package zTesting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DataRow;
import com.huiming.service.topo.tree.MultiTree;
import com.huiming.service.topo.tree.Node;
import com.huiming.service.topo.tree.Point;
import com.huiming.sr.constants.SrContant;

public class MyTest {
	public static final int FRONT = 0;
	public static final int BACK = 1;
	
	static void test5(MultiTree tree, Map<String, List<Point>> portids, long arr[][], final long start, boolean isChild){
		int len = arr.length;
		Point p;
		for(int i = 0; i < len; ++i){
			if(arr[i][0] == start){
				Node node1 = tree.findNode(arr[i][0]);
				String key68 = arr[i][0] + "_" + arr[i][1];
				p = isChild? new Point(arr[i][2], arr[i][3]) : new Point(arr[i][3], arr[i][2]);
				isChild = !isChild;
				if(portids.containsKey(key68)){
					portids.get(key68).add(p);
				}
				else {
					List<Point> a = new ArrayList<Point>();
					a.add(p);
					portids.put(key68, a);
				}
				if(node1 == null){  // 如果起点节点未曾在树中出现
					// 那么生成该起点节点
					node1 = new Node(arr[i][0], tree.getRoot(), p);
					// 将起点节点变为根节点的子节点
					tree.getRoot().children.add(node1);
					// 没有起点节点肯定也没有终点节点，生成终点节点
					Node node2 = new Node(arr[i][1], node1);
					// 将终点节点变为起点节点的子节点
					node1.children.add(node2);
				}
				else{ // 起点节点存在
					node1.portIds.add(p);
					if(node1.pid.id == arr[i][1]){ continue; }
					Node node2 = tree.findNode(arr[i][1]);
					if(node2 == null){
						// 如果起点节点的子节点集合不包含终点节点，那么生成终点节点
						node2 = new Node(arr[i][1], node1);
						node1.children.add(node2);
					}
				}
			}
			else if(arr[i][1] == start){
				Node node1 = tree.findNode(arr[i][1]);
				p = new Point(arr[i][3], arr[i][2]);
				String key68 = arr[i][1] + "_" + arr[i][0];
				p = isChild? new Point(arr[i][3], arr[i][2]) : new Point(arr[i][2], arr[i][3]);
				isChild = !isChild;
				if(portids.containsKey(key68)){
					portids.get(key68).add(p);
				}
				else {
					List<Point> a = new ArrayList<Point>();
					a.add(p);
					portids.put(key68, a);
				}
				if(node1 == null){  // 未曾在树中出现
					node1 = new Node(arr[i][1], tree.getRoot(), p);
					tree.getRoot().children.add(node1);
					Node node2 = new Node(arr[i][0], node1);
					node1.children.add(node2);
				}
				else{
					node1.portIds.add(p);
					if(node1.pid.id == arr[i][0]){ continue; }
					Node node2 = tree.findNode(arr[i][0]);
					if(node2 == null){
						node2 = new Node(arr[i][0], node1);
						node1.children.add(node2);
					}
				}
			}
		}
		Node startNode = tree.findNode(start);
		if(startNode.isLeaf() == false){
			for(Node child : startNode.children){
				test5(tree, portids, arr, child.id, isChild);
			}
		}
	}
	public static void main2(String[] args) {
		/** 
		 	起点交换机(56786) -- 终点交换机(56852)
		 	56786 -- 56874
		 */
		MultiTree tree = new MultiTree();
		/*起点交换机(56764)不能连上终点交换机(56841)
		起点交换机(56786)不能连上终点交换机(56852)*/
		long start = 56764;
		long end = 56775;
		Map<String, List<Point>> portids = new HashMap<String, List<Point>>();
		test5(tree, portids, arr, start, false);
		tree.print();
		System.out.println(tree.count());
		Set<Node> nodes = null;
		if(end > 0){
			nodes = new HashSet<Node>(1);
			nodes.add(tree.findNode(end));
		}
		else {
			nodes = tree.getAllLeaves();
		}
		List<List<DataRow>> switchPath = new ArrayList<List<DataRow>>(nodes.size());
		List<List<DataRow>> portPath = new ArrayList<List<DataRow>>(nodes.size());
		for(Node endNode : nodes){
			if(endNode != null){
				List<DataRow> portIds = new ArrayList<DataRow>();
				List<DataRow> switches = new ArrayList<DataRow>();
				Node cur = endNode;
				while(cur.id != start){
					DataRow dr = new DataRow();
					dr.set("swid1", cur.id);
					dr.set("swid2", cur.pid.id);
					switches.add(dr);
					
					String key68 = cur.id + "_" + cur.pid.id;
					if(portids.containsKey(key68)){
						for(Point p : portids.get(key68)){
							dr = new DataRow();
							dr.set("swpid1", p.x);
							dr.set("swpid2", p.y);
							portIds.add(dr);
						}
					}
					cur = cur.pid;
				}
				switchPath.add(switches);
				portPath.add(portIds);
			}
		}
		System.out.println("*******************");
		System.out.println(JSON.toJSONString(switchPath));
		System.out.println(JSON.toJSONString(portPath));
		System.out.println("*******************");
//		System.out.println(JSON.toJSONString(drs));
	}
	
	
	// IP地址错误
	/**
	 	ipmiutil ver 2.96
		ihealth ver 2.96
		Connecting to node  192.168.10.121
		ipmilan_open_session error, rv = -3
		ipmilan receive from BMC failed
		ipmiutil health, receive from BMC failed
	 */
	// 用户名错误
	/**
	 	ipmiutil ver 2.96
		ihealth ver 2.96
		Connecting to node  192.168.1.121
		GetSessChallenge: Invalid user name
		ipmilan_open_session error, rv = 0x81
		ipmilan Lost Arbitration
		ipmiutil health, Lost Arbitration
	 */
	// 成功登陆
	/**
	 ipmiutil ver 2.96
		ihealth ver 2.96
		Connecting to node  192.168.1.121
		BMC manufacturer  | 000002 (IBM), product | 0011 
		BMC version       | 1.48, IPMI v2.0
		IPMI driver type  | 6        (lan)
		Power State       | 00       (S0: working)
		Selftest status   | 0055     (OK)
		Chassis Status    | 20 00 00 00 (off, see below)
			chassis_power       | off
			pwr_restore_policy  | last_state
			chassis_intrusion   | inactive
			front_panel_lockout | inactive
			drive_fault         | false
			cooling_fan_fault   | false
		Power On Hours    | 0 hours (0 days)
		BMC LAN Channels  | 1 
		Chan 1 AuthTypes  | MD2 MD5 Straight_Passwd 
		ipmiutil health, completed successfully
	 */
	// E:\program-files\apache-tomcat\webapps\dongguan_sr\WEB-INF\lib\ipmiutil-2.9.6-win64\ipmiutil.exe sel -N 192.168.1.121 -U hgc -P 123456a -c
	
	public static void main(String[] args) throws Exception {
//		Logger logger = Logger.getLogger(MyTest.class);
//		SnmpUtil util = new SnmpUtil();
//		JsonData jsonData = new JsonData();
//		
//		// 使用该命令来测试 snmpstatus -v 2c -c public 192.168.1.21
//		try {
//			String cmd;
//			String oids[] = new String[]{""};
//			cmd = util.getV1V2Command("snmpstatus", "192.168.1.211", "2c", "public", oids);
//			logger.info("*************************************************");
//			logger.info(cmd);
//			for(int i = 0, len = 2; i < len; ++i) {
//				util.testSnmp(cmd, jsonData);
//				if(jsonData.isSuccess()) { break; } // 如果成功了则跳出循环
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			jsonData.setSuccess(false);
//			jsonData.setMsg(e.getMessage());
//		}
//		logger.info(JSON.toJSONString(jsonData));
		/*
		    00.0c.29.40.44.cf.
			00.0c.29.5d.19.92.
			00.0c.29.66.19.70.
			00.12.3f.5f.62.7c.
			00.1a.64.c4.e3.2c.
			00.1b.78.96.bb.54.
			00.50.56.8f.16.ae.
			08.ed.b9.e2.85.a9.
			2c.1f.23.0e.65.42.
			48.e9.f1.c9.29.df.
			48.ea.63.00.63.b8.
			50.46.5d.a8.20.53.
			60.e7.01.8d.c5.27.
			68.94.23.06.ca.24.
			80.ea.96.4e.76.53.
			98.e0.d9.29.d6.3a.
			9c.f3.87.04.81.41.
			b8.76.3f.23.eb.b6.
			c0.a0.bb.49.bc.18.
			c4.ca.d9.35.38.2d.
			e0.06.e6.c8.3b.3c.
			e0.06.e6.c9.23.9a.
			f0.79.59.6a.46.33.
			fc.25.3f.2c.41.1e.
			fc.aa.14.d6.8b.8c.
		 */
		String strs[] = {
				"0.12.41.64.68.207",
				"0.12.41.93.25.146",
				"0.12.41.102.25.112",
				"0.18.63.95.98.124",
				"0.26.100.196.227.44",
				"0.27.120.150.187.84",
				"0.80.86.143.22.174",
				"8.237.185.226.133.169",
				"44.31.35.14.101.66",
				"72.233.241.201.41.223",
				"72.234.99.0.99.184",
				"80.70.93.168.32.83",
				"96.231.1.141.197.39",
				"104.148.35.6.202.36",
				"128.234.150.78.118.83",
				"152.224.217.41.214.58",
				"156.243.135.4.129.65",
				"184.118.63.35.235.182",
				"192.160.187.73.188.24",
				"196.202.217.53.56.45",
				"224.6.230.200.59.60",
				"224.6.230.201.35.154",
				"240.121.89.106.70.51",
				"252.37.63.44.65.30",
				"252.170.20.214.139.140"
		};
		System.out.println("252.170.20.214.139.140".lastIndexOf('.', "252.170.20.214.139.140".length() - 14));
		for(String str : strs) {
			String strs_[] = str.split("\\.");
			StringBuilder sb = new StringBuilder();
			for(String str_ : strs_){
				String s = Integer.toHexString(Integer.parseInt(str_));
				if(s.length() < 2){
					sb.append('0');
				}
				sb.append(s);
				sb.append('.');
			}
//			System.out.println(sb);
		}
	}
	
	static void throwEx(){
//		try {
			throw new IllegalArgumentException("123");
//		}
//		catch(Exception e){
//			System.out.println(e.getMessage());
//		}
	}
	
	static String getOrderId(){
		return String.valueOf(System.nanoTime() - 13938385239424L);
	}
	
	static void myTest(long id, long _id){
		System.out.print("id: " + id + ", " + (id == _id));
	}
	
	static String[] split(String str, char sep, int numOfParts){
		String parts[] = new String[numOfParts];
		char chs[] = str.toCharArray();
		int pre = 0, i = 0, end;
		char whiteSpace = ' ';
		for(int curr = 0, len = chs.length; curr < len; ++curr){
			if(chs[curr] == sep){
				end = curr - 1;
				while(end > pre && chs[end] == whiteSpace){ --end; }
				while(end > pre && chs[pre] == whiteSpace){ ++pre; }
				end = end - pre + 1;
				if(end == 1 && chs[pre] == whiteSpace){ end = 0; }
				parts[i] =  new String(chs, pre, end);
				pre = curr + 1;
				++i;
			}
		}
		end = chs.length - 1;
		while(end > pre && chs[end] == whiteSpace){ --end; }
		while(end > pre && chs[pre] == whiteSpace){ ++pre; }
		end = end - pre + 1;
		if(end == 1 && chs[pre] == whiteSpace){ end = 0; }
		parts[i] = new String(chs, pre, end);
		return parts;
	}
	
	static void test5(){
		String [] ss = {
				"200100051E413A7E","200F00051E3585AB",
				"200000051E413A7E","200E00051E3585AB",
				"200100053303D100","200600051E41719E",
				"201000051E413A7E","201000051E41719E",
				"201F00051E903C8B","202500051E895520",
				"201E00051E903C8B","202400051E895520",
				"200000053303D100","202600051E895520",
				"200700051E4170C6","20410005330CB200",
				"202700051E62E502","20400005330CB200",
				"200700051E41719E","204100053303D100",
				"202700051E895520","204000053303D100",
				"20010005330CB200","200600051E4170C6",
				"201000051E416B39","201000051E4170C6",
				"200000051E416B39","200E00051E35953E",
				"200100051E416B39","200F00051E35953E",
				"201E00051E903F1D","202400051E62E502",
				"20000005330CB200","202600051E62E502",
				"201F00051E903F1D","202500051E62E502"
		};
	}
	
	static void test4(){
		String sql = String.format("SELECT m.parent_device_id AS pid,m.device_id AS id," +
				"h.hypervisor_id AS hyp_id,h.name AS hyp_name,c.ip_address AS hyp_ip," +
				"c.DISK_SPACE/1024.0 AS total,(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used," +
				"c.DISK_AVAILABLE_SPACE/1024.0 AS available,c.computer_id AS comp_id," +
				"m.device_type as devtype FROM t_map_devices m JOIN t_res_hypervisor h " +
				"ON m.device_id=h.hypervisor_id AND m.app_id=%s AND m.parent_device_type='%s' " +
				"and m.device_type='%s' JOIN t_res_computersystem c ON h.host_computer_id=c.computer_id " +
				"ORDER BY m.device_id",
				23, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.SUBDEVTYPE_PHYSICAL);
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,v.name as vm_name,v.vm_id,"+
				"c.DISK_SPACE/1024.0 AS total,(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used," +
				"c.DISK_AVAILABLE_SPACE/1024.0 AS available,v.hypervisor_id AS hyp_id," +
				"v.computer_id AS comp_id,c.ip_address AS vm_ip,m.device_type as devtype " +
				"FROM t_map_devices m JOIN t_res_virtualmachine v ON m.device_id=v.vm_id " +
				"AND m.app_id=%s AND m.parent_device_type='%s' AND m.device_type='%s' " +
				" %s JOIN t_res_computersystem c ON c.computer_id=v.computer_id",
					23, SrContant.SUBDEVTYPE_APP, SrContant.SUBDEVTYPE_VIRTUAL,
					""
				);
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s and m.parent_device_type='%s' AND m.device_type='%s'" +
				" ORDER BY m.device_id",
				23, SrContant.SUBDEVTYPE_PHYSICALPORT, SrContant.SUBDEVTYPE_SWITCHPORT);
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type as devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s AND m.parent_device_type='%s'" +
				" AND m.device_type='%s' ORDER BY m.device_id",
				23, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_SWITCH);
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type as devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s AND m.parent_device_type='%s'" +
				" AND m.device_type='%s' ORDER BY m.device_id",
				34, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_SWITCH);
		sql = String.format(
				"select switch_id as sw_id,the_display_name as sw_name,ip_address as sw_ip," +
				"the_propagated_status as operation,switch_wwn as sw_wwn " +
				" from v_res_switch where switch_id in (%s) order by switch_id", "56786,56764");
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type as devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s AND m.parent_device_type='%s'" +
				" AND m.device_type='%s' ORDER BY m.device_id",
				34, SrContant.SUBDEVTYPE_SWITCH, SrContant.SUBDEVTYPE_SWITCH);
		sql = "select sw.switch_id as sw_id1,kk.sw_id2,po.port_id as swp_id1,kk.swp_id2," +
		"sw.the_display_name as sw_name1,kk.sw_name2,sw.ip_address as sw_ip1,kk.sw_ip2 " +
		"from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id " +
		"join v_res_port2port p2p on po.port_id=p2p.port_id1 join (select s.the_display_name " +
		"as sw_name2,s.switch_id as sw_id2,p.port_id as swp_id2,s.ip_address as sw_ip2 " +
		"from v_res_switch s join v_res_switch2port p on s.switch_id=p.switch_id) kk " +
		"on kk.swp_id2=p2p.port_id2 order by sw.switch_id,po.port_id";
		sql = String.format("SELECT p.port_id,p.port_name,h.HYPERVISOR_ID AS hyp_id," +
				"h.name AS hyp_name FROM t_res_physical_port p JOIN t_res_hypervisor h ON " +
				"p.HYPERVISOR_ID=h.HYPERVISOR_ID %s " +
				" AND p.port_id NOT IN(SELECT m.device_id AS devid FROM t_map_devices m WHERE m.device_type='PhysicalPort')" +
				"ORDER BY hyp_id", " and p.port_id IN(8) ");
		sql = String.format("SELECT p.port_id,p.port_name,p.port_number," +
				"p.port_type,p.HYPERVISOR_ID AS hyp_id,h.name AS hyp_name FROM t_res_physical_port p " +
				"JOIN t_res_hypervisor h ON p.HYPERVISOR_ID=h.HYPERVISOR_ID %s ORDER BY p.HYPERVISOR_ID",
				"AND p.HYPERVISOR_ID IN (3,4,5)");
		sql = "select sw.switch_id as sw_id1,kk.sw_id2,po.port_id as swp_id1,kk.swp_id2," +
		"sw.the_display_name as sw_name1,kk.sw_name2,sw.ip_address as sw_ip1," +
		"kk.sw_ip2 from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id " +
		" join v_res_port2port p2p on po.port_id=p2p.port_id1 " +
		" join (select s.the_display_name as sw_name2,s.switch_id as sw_id2,p.port_id as swp_id2," +
		"s.ip_address as sw_ip2 from v_res_switch s " +
		"join v_res_switch2port p on s.switch_id=p.switch_id) kk on kk.swp_id2=p2p.port_id2 ";
		
		sql = "select p.switch_id as sw_id,s.the_display_name as sw_name,p.port_id as swp_id," +
		"p.the_display_name as swp_name from v_res_switch_port p join v_res_switch s " +
		"on p.switch_id=s.switch_id and the_enabled_state<>'disabled' & order by p.switch_id,p.port_id";
		
		sql = String.format(
				"select switch_id as sw_id,the_display_name as sw_name,ip_address as sw_ip," +
				"the_propagated_status as operation,switch_wwn as sw_wwn " +
				" from v_res_switch where switch_id in (%s) order by switch_id", "123,567");
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s and m.parent_device_type='%s' AND " +
				"m.device_type='%s' ORDER BY m.device_id",
				37, SrContant.SUBDEVTYPE_PHYSICALPORT, SrContant.SUBDEVTYPE_SWITCHPORT);
		
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype " +
				"FROM t_map_devices m WHERE m.app_id=%s and m.parent_device_type='%s' AND " +
				"m.device_type='%s' ORDER BY m.device_id",
				37, SrContant.SUBDEVTYPE_PHYSICALPORT, SrContant.SUBDEVTYPE_SWITCHPORT);
		
		sql = String.format("SELECT ftopid,fresourceId AS fresid,flevel,flevelcount,FDescript FROM (" +
				"SELECT d.ftopid,d.flevel,COUNT(d.flevel) AS flevelcount,d.FDescript,d.fstate," +
				"d.fresourceid,d.ftoptype FROM tndevicelog d GROUP BY d.ftopid,d.flevel," +
				"d.FDescript HAVING d.ftopid IN(%s) AND d.fresourceid IN(%s) " +
				" AND d.ftoptype='%s' AND d.fstate=0 ORDER BY d.ftopid ASC) t1",
				5, "5,2", SrContant.SUBDEVTYPE_PHYSICAL);
		sql = String.format(
				"select subsystem_id as sto_id,the_display_name as sto_name,ip_address as sto_ip," +
				"os_type,type as sto_type,the_operational_status as operation " +
				" from v_res_storage_subsystem where subsystem_id in(%s) order by subsystem_id", "1,2,3");
		
		sql = String.format(
				"select p.pool_id,p.the_display_name as pool_name,p.the_space as total," +
				"p.the_consumed_space as used,p.the_available_space as available," +
				"p.the_operational_status as operation,v.num_lun " +
				"from v_res_storage_pool p join " +
				"(select count(v.svid) as num_lun,v.pool_id from v_res_storage_volume v " +
				"group by v.pool_id) v on p.pool_id=v.pool_id " +
				" and p.pool_id in(%s) order by p.pool_id", "1,2,3");
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype,m.db_type," +
				"v.volume_id AS vol_id,COALESCE(v.display_name,v.name) AS vol_name, " +
				"v.logical_CAPACITY AS total,v.OPERATIONAL_STATUS AS operation,v.raid_level AS redundancy " +
				" FROM t_map_devices m JOIN t_res_storagevolume v ON m.device_id=v.volume_id AND m.app_id=%s " +
				"AND m.device_type='%s' AND m.db_type='%s' ORDER BY m.device_id", 37,
				SrContant.SUBDEVTYPE_VOLUME, SrContant.DBTYPE_SR);
		
		sql = String.format(								
				"select svid as vol_id,the_display_name as vol_name,the_capacity as total," +
				"the_used_space as used,the_operational_status as operation,the_redundancy as " +
				"redundancy from v_res_storage_volume where svid in (%s) order by svid", "12,23,45");
		
		sql = "select sw.switch_id as sw_id1,kk.sw_id2,po.port_id as swp_id1,kk.swp_id2," +
		"sw.the_display_name as sw_name1,kk.sw_name2,sw.ip_address as sw_ip1," +
		"kk.sw_ip2 from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id " +
		" join v_res_port2port p2p on po.port_id=p2p.port_id1 " +
		" join (select s.the_display_name as sw_name2,s.switch_id as sw_id2,p.port_id as swp_id2," +
		"s.ip_address as sw_ip2 from v_res_switch s " +
		"join v_res_switch2port p on s.switch_id=p.switch_id) kk on kk.swp_id2=p2p.port_id2 ";
		
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,v.name as vm_name,v.vm_id,"+
				"c.DISK_SPACE/1024.0 AS total,(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used," +
				"c.DISK_AVAILABLE_SPACE/1024.0 AS available,v.hypervisor_id AS hyp_id," +
				"v.computer_id AS comp_id,c.ip_address AS vm_ip,m.device_type as devtype " +
				"FROM t_map_devices m JOIN t_res_virtualmachine v ON m.device_id=v.vm_id " +
				"AND m.app_id=%s AND m.parent_device_type='%s' AND m.device_type='%s' " +
				" %s JOIN t_res_computersystem c ON c.computer_id=v.computer_id",
					41, SrContant.SUBDEVTYPE_APP, SrContant.SUBDEVTYPE_VIRTUAL,
					""
				);
		sql = String.format("SELECT m.parent_device_id AS pid,m.device_id AS id," +
				"h.hypervisor_id AS hyp_id,h.name AS hyp_name,c.ip_address AS hyp_ip," +
				"c.DISK_SPACE/1024.0 AS total,(c.DISK_SPACE - c.DISK_AVAILABLE_SPACE)/1024.0 AS used," +
				"c.DISK_AVAILABLE_SPACE/1024.0 AS available,c.computer_id AS comp_id," +
				"m.device_type as devtype FROM t_map_devices m JOIN t_res_hypervisor h " +
				"ON m.device_id=h.hypervisor_id AND m.app_id=%s AND m.parent_device_type='%s' " +
				"and m.device_type='%s' JOIN t_res_computersystem c ON h.host_computer_id=c.computer_id " +
				"ORDER BY m.device_id",
				41, SrContant.SUBDEVTYPE_VIRTUAL, SrContant.SUBDEVTYPE_PHYSICAL);
		
		sql = String.format(
				"SELECT m.parent_device_id AS pid,m.device_id AS id,m.device_type AS devtype," +
				"p.hypervisor_id AS hyp_id,p.port_id,p.port_name,p.port_number,p.port_type " +
				"FROM t_map_devices m JOIN t_res_physical_port p ON m.device_id=p.port_id " +
				"AND m.app_id=%s AND m.parent_device_type='%s' AND m.device_type='%s' " +
				"ORDER BY m.device_id",
					41, SrContant.SUBDEVTYPE_PHYSICAL, SrContant.SUBDEVTYPE_PHYSICALPORT);
		sql="select s.*,m.model_name,v.vendor_name,f.the_display_name as fabric_name " +
		"from v_res_switch s,v_res_model m,v_res_vendor v ,v_res_fabric f " +
		"where s.model_id = m.model_id " +
		"and s.vendor_id = v.vendor_id and s.the_fabric_id = f.fabric_id ";
		
		sql = 
			"SELECT c.CPU_BUSY_PRCT,c.MEM_USED_PRCT FROM t_prf_computerper c " +
			"JOIN t_prf_timestamp t ON c.time_id=t.time_id AND " +
			"t.sample_time BETWEEN timestamp('%s') AND timestamp('%s') join "+
			( true? ("t_res_hypervisor h on h.host_computer_id=c.computer_id and h.hypervisor_id=") : ("t_res_virtualmachine v on v.computer_id=c.computer_id and v.vm_id=") ) + 123 +
			" order by t.time_id desc limit 0,1";
		DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String endDate = dateFmt.format(cal.getTime());
		
		cal.add(Calendar.SECOND, -24*60*60);
		String startDate = dateFmt.format(cal.getTime());
		System.out.println("startDate: " + startDate + ", endDate: " + endDate);
		
		sql = String.format(
				"SELECT subsystem_id as sto_id,p.pool_id,COALESCE(p.display_name,p.name) AS pool_name," +
				"p.TOTAL_USABLE_CAPACITY AS total,p.TOTAL_USABLE_CAPACITY-p.UNALLOCATED_CAPACITY AS used," +
				"p.UNALLOCATED_CAPACITY AS available,p.OPERATIONAL_STATUS AS oper_status,v.num_lun,'%s' as db_type FROM t_res_storagepool p " +
				" JOIN (SELECT COUNT(volume_id) AS num_lun,pool_id FROM t_res_storagevolume GROUP BY pool_id) v " +
				" ON v.pool_id=p.POOL_ID AND p.POOL_ID IN(%s) ORDER BY p.pool_id",
				SrContant.DBTYPE_SR, "123,12");
		sql = String.format(
				"SELECT v.volume_id AS vol_id,COALESCE(v.display_name,v.name) AS vol_name,v.logical_CAPACITY AS total," +
				"v.OPERATIONAL_STATUS AS oper_status,v.raid_level AS redundancy,'%s' as db_type " +
				"FROM t_res_storagevolume v WHERE pool_id IN(%s) ORDER BY v.volume_id",
				SrContant.DBTYPE_SR, "12,345,2");
		
		sql = String.format(
				"select svid as vol_id,the_display_name as vol_name,the_capacity as total," +
				"the_used_space as used,the_operational_status as oper_status,the_redundancy as redundancy," +
				"'%s' as db_type from v_res_storage_volume where svid in (%s) order by svid",
				SrContant.DBTYPE_SR, "12,345,2");
		sql = String.format(
				"select p.subsystem_id as sto_id,p.pool_id,p.the_display_name as pool_name," +
				"p.the_space as total,p.the_consumed_space as used,p.the_available_space as available,'%s' as db_type," +
				"p.the_operational_status as oper_status,v.num_lun from v_res_storage_pool p " +
				"join (select count(v.svid) as num_lun,v.pool_id from v_res_storage_volume v " +
				"group by v.pool_id) v on p.pool_id=v.pool_id and p.pool_id in(%s) " +
				"order by p.pool_id", SrContant.DBTYPE_TPC, "123");
		
		sql = String.format("SELECT s.os_type,v.svid AS vol_id,v.the_display_name AS vol_name,v.the_capacity AS total," +
				"v.the_used_space AS used,v.the_operational_status AS oper_status,v.the_redundancy AS " +
				"redundancy,'%s' AS db_type FROM v_res_storage_volume v JOIN v_res_storage_subsystem s " +
				"on s.subsystem_id=v.subsystem_id AND v.svid IN (%s) ORDER BY v.svid",
				SrContant.DBTYPE_SR, "123");
		
		sql = String.format("SELECT ftopid,fresourceId AS fresid,flevel,flevelcount,FDescript FROM (" +
				"SELECT d.ftopid,d.flevel,COUNT(d.flevel) AS flevelcount,d.FDescript,d.fstate,d.fresourceid,d.ftoptype " +
				" FROM tndevicelog d GROUP BY d.ftopid,d.flevel,d.FDescript HAVING " + 11 +
				" AND d.ftoptype='%s' AND d.fstate=0 " + " ORDER BY d.ftopid ASC) t1", "Phy");
		sql = "select v.vendor_name,s.type as os_type,m.model_name,s.serial_number," +
		"s.code_level as micro_code,s.cache,s.the_physical_disk_space as phy_disk," +
		"s.the_storage_pool_space as pool_space,s.the_volume_space as vol_space," +
		"s.the_assigned_volume_space as ass_vol_space,s.the_unassigned_volume_space as unass_vol_space," +
		"the_consolidated_status as con_status,t1.port_count,t2.disk_count,t3.pool_count," +
		"t4.volume_count,t5.extent_count,t6.arrsize_count,t7.rank_count,t8.node_count,t9.iog_count " +
		" from v_res_storage_subsystem s left join v_res_vendor v on v.vendor_id=s.vendor_id " +
		" join v_res_model m on s.model_id=m.model_id left join (select subsystem_id as sys_id," +
		" count(port_id) as port_count from v_res_port group by subsystem_id) t1  " +
		" on t1.sys_id=s.subsystem_id left join (select subsystem_id as sys_id,count(physical_volume_id) " +
		" as disk_count from v_res_physical_volume group by subsystem_id) t2 on t2.sys_id=s.subsystem_id  " +
		" left join (select subsystem_id as sys_id,count(pool_id) as pool_count " +
		" from v_res_storage_pool group by subsystem_id) t3 on t3.sys_id=s.subsystem_id " +
		" left join (select subsystem_id as sys_id,count(svid) as volume_count from v_res_storage_volume group by subsystem_id) t4 " +
		" on t4.sys_id=s.subsystem_id left join (select subsystem_id as sys_id,count(storage_extent_id) " +
		" as extent_count from v_res_storage_extent group by subsystem_id) t5 " +
		" on t5.sys_id=s.subsystem_id left join (select subsystem_id as sys_id,count(storage_extent_id) " +
		" as arrsize_count from v_res_arraysite group by subsystem_id) t6 on t6.sys_id=s.subsystem_id " +
		" left join (select subsystem_id as sys_id,count(storage_extent_id) as rank_count " +
		" from V_RES_STORAGE_RANK group by subsystem_id) t7 on t7.sys_id=s.subsystem_id " +
		" left join (select subsystem_id as sys_id,count(redundancy_id) as node_count " +
		" from V_RES_REDUNDANCY group by subsystem_id) t8 on t8.sys_id=s.subsystem_id " +
		" left join (select subsystem_id as sys_id,count(io_group_id) as iog_count " +
		"from V_RES_STORAGE_IOGROUP group by subsystem_id) t9 on t9.sys_id=s.subsystem_id " +
		"where s.subsystem_id=111";
		
		sql = ("select s.subsystem_id as sto_id,s.the_display_name as sto_name,p.the_space as pool_space," +
				" p.the_consumed_space as pool_con_space,p.the_available_space as pool_available_space," +
				" p.the_assigned_space as pool_assigned_space,p.the_unassigned_space as pool_unassigned_space," +
				" p.the_native_status as native_status,p.the_consolidated_status as con_status," +
				" p.the_operational_status as oper_status,p.raid_level,v.volume_count " +
				" from v_res_storage_pool p join v_res_storage_subsystem s on p.subsystem_id=s.subsystem_id and p.pool_id=&" +
				" left join (select pool_id,count(svid) as volume_count from v_res_storage_volume group by pool_id) v " +
				"on v.pool_id=p.pool_id").replace("&", String.valueOf(121));
		
		sql = "select s.subsystem_id as sto_id,s.the_display_name as sto_name,v.the_redundancy as raid_level," +
		" v.the_capacity as vol_space,v.the_used_space as vol_used_space,v.unique_id,p.pool_id," +
		" p.the_display_name as pool_name,v.update_timestamp from v_res_storage_volume v " +
		" join v_res_storage_subsystem s on v.subsystem_id=s.subsystem_id " +
		" join v_res_storage_pool p on v.pool_id=p.pool_id and v.svid=" + 121;
		
		System.out.println("25".equals(null));
	}
	
	static void test3(){
		String sql = "select sw.switch_id as sw_id1,kk.sw_id2,po. as swp_id1,kk.swp_id2,"
			+ "sw.the_display_name as sw_name1,kk.sw_name2,sw.ip_address as sw_ip1,"
			+ "kk.sw_ip2 from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id "
			+ " join v_res_port2port p2p on po.=p2p.1 "
			+ " join (select s.the_display_name as sw_name2,s.switch_id as sw_id2,p. as swp_id2,"
			+ "s.ip_address as sw_ip2 from v_res_switch s "
			+ "join v_res_switch2port p on s.switch_id=p.switch_id) kk on kk.swp_id2=p2p.2 ";
	
	 System.out.println(sql);

	long arr[][] = { 
			{ 56764, 56775, 10000, 10001, 0 }, 
			{ 56764, 56775, 10003, 10004, 0 },
			{ 56764, 56797, 10005, 10006, 0 },
			{ 56786, 56874, 10007, 10008, 0 }, 
			{ 56786, 56874, 10009, 10010, 0 },
			{ 56797, 56841, 10011, 10012, 0 },
			{ 56808, 56841, 10013, 10014, 0 }, 
			{ 56819, 56852, 10015, 10016, 0 },
			{ 56819, 56874, 10017, 10018, 0 },
			{ 56830, 56808, 10019, 10020, 0 }, 
			{ 56830, 56808, 10021, 10022, 0 },
			{ 56841, 56797, 10023, 10024, 0 },
			{ 56841, 56808, 10025, 10026, 0 }, 
			{ 56852, 56819, 10027, 10028, 0 },
			{ 56874, 56819, 10029, 10030, 0 },
			{ 56885, 56852, 10031, 10032, 0 }, 
			{ 56885, 56863, 10033, 10034, 0 },
			{ 56885, 56863, 10035, 10036, 0 } 
	};
	
//	arr = new long[][] { 
//			{ 56764, 56775, 10000, 10001, 10003, 10004, 0 }, 
//			{ 56764, 56797, 10005, 10006, 0 },
//			{ 56786, 56874, 10007, 10008, 10009, 10010, 0 }, 
//			{ 56797, 56841, 10011, 10012, 0 },
//			{ 56808, 56841, 10013, 10014, 0 }, 
//			{ 56819, 56852, 10015, 10016, 0 },
//			{ 56819, 56874, 10017, 10018, 0 },
//			{ 56830, 56808, 10019, 10020, 10021, 10022, 0 }, 
//			{ 56841, 56797, 10023, 10024, 0 },
//			{ 56841, 56808, 10025, 10026, 0 }, 
//			{ 56852, 56819, 10027, 10028, 0 },
//			{ 56874, 56819, 10029, 10030, 0 },
//			{ 56885, 56852, 10031, 10032, 0 }, 
//			{ 56885, 56863, 10033, 10034, 10035, 10036, 0 },
//	};
	String key;
//	Map<String, Bean> beans = new HashMap<String, Bean>();
//	for(int i = 0, len = arr.length; i < len; ++i){
//		key = arr[i][0] + "_" + arr[i][1];
//		Long[] ports = new Long[]{
//				arr[i][2], arr[i][3]
//		};
//		if(beans.containsKey(key)){
//			beans.get(key).ports.add(ports);
//			continue;
//		}
//		if(beans.containsKey(arr[i][1] + "_" + arr[i][0])) {
//			beans.get(arr[i][1] + "_" + arr[i][0]).ports.add(ports);
//			continue;
//		}
//		Bean bean = new Bean();
//		bean.swid1 = arr[i][0];
//		bean.swid2 = arr[i][1];
//		bean.ports.add(ports);
//		beans.put(key, bean);
//	}
//	Bean bArr[] = new Bean[beans.size()];
//	int ii = 0;
//	for(String k : beans.keySet()){
//		bArr[ii++] = beans.get(k);
//	}
//	System.out.println(JSON.toJSONString(bArr));
	
	MultiTree tree = new MultiTree();
//	Node n56797 = new Node(56797L, tree.getRoot(), 1, 0);
//	tree.getRoot().children.add(n56797);
//	Node n56764 = new Node(56764L, n56797, 1, 0);
//	n56797.children.add(n56764);
//	Node n56775 = new Node(56775L, n56764, 1, 0);
//	n56764.children.add(n56775);
//	Node n56841 = new Node(56841L, n56797, 1, 0);
//	n56797.children.add(n56841);
//	Node n56808 = new Node(56808L, n56841, 1, 0);
//	n56841.children.add(n56808);
//	Node n56830 = new Node(56830L, n56808, 1, 0);
//	n56808.children.add(n56830);
//	Node n56852 = new Node(56852L, tree.getRoot(), 1, 0);
//	tree.getRoot().children.add(n56852);
//	Node n56885 = new Node(56885L, n56852, 1, 0);
//	n56852.children.add(n56885);
//	Node n56863 = new Node(56863L, n56885, 1, 0);
//	n56885.children.add(n56863);
//	Node n56819 = new Node(56819L, n56852, 1, 0);
//	n56852.children.add(n56819);
//	Node n56874 = new Node(56874L, n56819, 1, 0);
//	n56819.children.add(n56874);
//	Node n56786 = new Node(56786L, n56874, 1, 0);
//	n56874.children.add(n56786);
	
//	tree.print();
	System.out.println(tree.count());


	long start = 56797;
	long end = 56830;
	
	
	long _start = start;
//	while(true){
//		for(int i = 0, len = arr.length; i < len; ++i){
//			if(arr[i][0] == _start){
//				add(tree, arr[i][0], arr[i][1], arr[i][2], arr[i][3]);
//				_start = arr[i][1];
//			}
//			else if(arr[i][1] == _start){
//				add(tree, arr[i][1], arr[i][0], arr[i][3], arr[i][2]);
//				_start = arr[i][0];
//			}
//		}
//		if(tree.count() == 1){ break; }
//	}
	System.out.println(tree.count());
//	Set<Node> leaves = tree.getAllLeaves();
//	List<DataRow> drs = new ArrayList<DataRow>();
//	for(Node leaf : leaves){
//		if(leaf.id == end){
//			Node cur = leaf;
//			DataRow dr = new DataRow();
//			int i = 1;
//			do{
//				if(cur.direction == FRONT){
//					for(int j = 0; j < cur.portIds.size(); ++j){
//						dr.put("swId1_" + i, cur.id);
//					}
//				}
//				else {
//					for(int j = 0; j < cur.portIds.size(); ++j){
//						dr.put("swId2_" + i, cur.id);
//					}
//				}
//				drs.add(dr);
//				cur = cur.pid;
//				++i;
//			}while(cur != null);
//		}
//	}
//	System.out.println(JSON.toJSONString(drs));
	}
	
	
	static long arr[][] = { 
			{ 56764, 56775, 10000, 10001, 0 }, 
			{ 56764, 56775, 10003, 10004, 0 },
			{ 56764, 56797, 10005, 10006, 0 },
			{ 56797, 56885, 10005, 10006, 0 },//
			{ 56786, 56874, 10007, 10008, 0 }, 
			{ 56786, 56874, 10009, 10010, 0 },
			{ 56797, 56841, 10011, 10012, 0 },
			{ 56808, 56841, 10013, 10014, 0 }, 
			{ 56819, 56852, 10015, 10016, 0 },
			{ 56819, 56874, 10017, 10018, 0 },
			{ 56830, 56808, 10019, 10020, 0 }, // 10031, 10032 | 
			{ 56830, 56808, 10021, 10022, 0 },
			{ 56841, 56797, 10023, 10024, 0 },
			{ 56841, 56808, 10025, 10026, 0 }, 
			{ 56852, 56819, 10027, 10028, 0 },
			{ 56874, 56819, 10029, 10030, 0 },
			{ 56885, 56852, 10031, 10032, 0 }, 
			{ 56885, 56863, 10033, 10034, 0 },
			{ 56885, 56863, 10035, 10036, 0 } 
	};
}
