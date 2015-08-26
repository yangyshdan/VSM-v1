package com.huiming.service.topo.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.huiming.base.jdbc.DataRow;
import com.huiming.base.jdbc.JdbcTemplate;
import com.huiming.sr.constants.SrContant;

public class TreeUtils {
	public static final int FIRST_SWITCH = 0;
	public static final int SECOND_SWITCH = 1;
	
	/**
	 * @see 
	 * @param tree
	 * @param arr
	 * @param start
	 */
	private void arrayToTree(MultiTree tree, Map<String, List<Point>> portids, long arr[][], 
			final long start){
		int len = arr.length;
		Point p;
		String key68;
		for(int i = 0; i < len; ++i){
			if(arr[i][0] == start){
				Node node1 = tree.findNode(arr[i][0]);
				key68 = arr[i][0] + "_" + arr[i][1]; // start_end
				p = new Point(arr[i][2], arr[i][3]);
				if(portids.containsKey(key68)){ portids.get(key68).add(p); }
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
				key68 = arr[i][1] + "_" + arr[i][0];
				p = new Point(arr[i][3], arr[i][2]);
				if(portids.containsKey(key68)){
					portids.get(key68).add(p);
				}
				else {
					List<Point> a = new ArrayList<Point>();
					a.add(p);
					portids.put(key68, a);
				}
				if(node1 == null){  // 未曾在树中出现
					node1 = new Node(arr[i][1], tree.getRoot());
					tree.getRoot().children.add(node1);
					Node node2 = new Node(arr[i][0], node1);
					node1.children.add(node2);
				}
				else{
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
		if(startNode != null && startNode.isLeaf() == false){
			for(Node child : startNode.children){
				arrayToTree(tree, portids, arr, child.id);
			}
		}
	}
	
	private void arrayToTree(MultiTree tree, long arr[][], final long start){
		if(arr == null || arr.length == 0){ return; }
		int len = arr.length;
		for(int i = 0; i < len; ++i){
			if(arr[i].length < 2){ continue; }
			if(arr[i][0] == start){
				Node node1 = tree.findNode(arr[i][0]);
				if(node1 == null){  // 如果起点节点未曾在树中出现
					// 那么生成该起点节点
					node1 = new Node(arr[i][0], tree.getRoot());
					// 将起点节点变为根节点的子节点
					tree.getRoot().children.add(node1);
					// 没有起点节点肯定也没有终点节点，生成终点节点
					Node node2 = new Node(arr[i][1], node1);
					// 将终点节点变为起点节点的子节点
					node1.children.add(node2);
				}
				else{ // 起点节点存在
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
				if(node1 == null){  // 未曾在树中出现
					node1 = new Node(arr[i][1], tree.getRoot());
					tree.getRoot().children.add(node1);
					Node node2 = new Node(arr[i][0], node1);
					node1.children.add(node2);
				}
				else{
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
		if(startNode != null && startNode.isLeaf() == false){
			for(Node child : startNode.children){
				arrayToTree(tree, arr, child.id);
			}
		}
	}
	
	public String getSwitchIds(long data[][], long start){
		MultiTree tree = new MultiTree();
		arrayToTree(tree, data, start);
		Set<Node> nodes = tree.getAllLeaves();
		StringBuilder sb_swIds = new StringBuilder(100);
		sb_swIds.append(start);
		sb_swIds.append(',');
		if(nodes != null){
			for(Node endNode : nodes){
				if(endNode != null){
					Node cur = endNode;
					while(cur != null && cur.id != start){
						if(sb_swIds.indexOf(String.valueOf(cur.id)) < 0){ // 找不到
							sb_swIds.append(cur.id);
							sb_swIds.append(',');
						}
						cur = cur.pid;
					}
				}
			}
		}
		int lastIndex = sb_swIds.length() - 1;
		if(lastIndex >= 0 && sb_swIds.charAt(lastIndex) == ','){ sb_swIds.deleteCharAt(lastIndex); }
		return sb_swIds.toString();
	}
	
	public String getSwitchIds(long data[][], long start, Set<Long> swIds){
		if(data == null || data.length == 0){
			return String.valueOf(start);
		}
		MultiTree tree = new MultiTree();
		arrayToTree(tree, data, start);
		Set<Node> nodes = tree.getAllLeaves();
		StringBuilder sb_swIds = new StringBuilder(100);
		if(swIds.contains(start)){
			sb_swIds.append(start);
			sb_swIds.append(',');
		}
		
		if(nodes != null){
			for(Node endNode : nodes){
				if(endNode != null){
					Node cur = endNode;
					while(cur != null && cur.id != start){
						if(sb_swIds.indexOf(String.valueOf(cur.id)) < 0){ // 找不到
							if(swIds.contains(cur.id)){
								sb_swIds.append(cur.id);
								sb_swIds.append(',');
							}
						}
						cur = cur.pid;
					}
				}
			}
		}
		else {
			return String.valueOf(start);
		}
		int lastIndex = sb_swIds.length() - 1;
		if(lastIndex >= 0 && sb_swIds.charAt(lastIndex) == ','){ sb_swIds.deleteCharAt(lastIndex); }
		return sb_swIds.toString();
	}
	
	public MultiTree getMultiTree(List<DataRow> data, long appId, long start, long end){
//		System.out.println("*****************************************************");
//		System.out.println(JSON.toJSONString(data));
//		System.out.println(JSON.toJSONString(this.convert(data)));
//		System.out.println("*****************************************************");
		return getMultiTree(this.convert(data), appId, start, end);
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
	public static void main(String args[]){
		TreeUtils u = new TreeUtils();
		MultiTree tree = u.getMultiTree(arr, 35, 56797, 56874);
//		MultiTree tree = u.getMultiTree(arr, 34, 56764, 56775);
		System.out.println(JSON.toJSONString(tree.getSwitchLinkSwitchs().get(0)));
		System.out.println(JSON.toJSONString(tree.getPortLinkPorts().get(0)));
		System.out.println(JSON.toJSONString(tree.getSwitchLinkPorts().get(0)));
		System.out.println(JSON.toJSONString(tree.getPortLinkSwitchs().get(0)));
	}
	
	/**
	 * @see 当连在同一台交换机上,swStart==swEnd,swStart-->swPortId-->portSwId-->swEnd
	 * @param appId
	 * @param swStart
	 * @param swEnd
	 * @param swPortId
	 * @param portSwId
	 * @return
	 */
	public MultiTree getMultiTree(long appId, long swStart, long swEnd, Long swPortId, Long portSwId){
		MultiTree tree = new MultiTree();
		List<List<DataRow>> swSwPath = new ArrayList<List<DataRow>>(1);
		List<List<DataRow>> portPortPath = new ArrayList<List<DataRow>>(1);
		List<List<DataRow>> swPortPath = new ArrayList<List<DataRow>>(1);
		List<List<DataRow>> portSwPath = new ArrayList<List<DataRow>>(1);
		
		List<DataRow> portIds = new ArrayList<DataRow>(1);
		List<DataRow> switches = new ArrayList<DataRow>(1);
		List<DataRow> swPorts = new ArrayList<DataRow>(1);
		List<DataRow> portSws = new ArrayList<DataRow>(1);
		
		DataRow dr = new DataRow();
		dr.set("app_id", appId);
		dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
		dr.set("device_type", SrContant.SUBDEVTYPE_SWITCH);
		dr.set("parent_device_id", swStart);
		dr.set("device_id", swEnd);
		dr.set("has_components", true);
		dr.set("db_type", SrContant.DBTYPE_TPC);
		switches.add(dr);
		swSwPath.add(switches);
		
		dr = new DataRow();
		dr.set("app_id", appId);
		dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
		dr.set("device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
		dr.set("parent_device_id", swPortId);
		dr.set("device_id", portSwId);
		dr.set("has_components", false);
		dr.set("db_type", SrContant.DBTYPE_TPC);
		portIds.add(dr);
		portPortPath.add(portIds);
		
		dr = new DataRow();
		dr.set("app_id", appId);
		dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
		dr.set("device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
		dr.set("parent_device_id", swStart);
		dr.set("device_id", swPortId);
		dr.set("has_components", false);
		dr.set("db_type", SrContant.DBTYPE_TPC);
		portIds.add(dr);
		swPortPath.add(swPorts);
		
		dr = new DataRow();
		dr.set("app_id", appId);
		dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
		dr.set("device_type", SrContant.SUBDEVTYPE_SWITCH);
		dr.set("parent_device_id", portSwId);
		dr.set("device_id", swEnd);
		dr.set("has_components", true);
		dr.set("db_type", SrContant.DBTYPE_TPC);
		portIds.add(dr);
		portSwPath.add(portSws);
		
		tree.setPortLinkPorts(portPortPath);
		tree.setSwitchLinkSwitchs(swSwPath);
		tree.setSwitchLinkPorts(swPortPath);
		tree.setPortLinkSwitchs(portSwPath);
		return tree;
	}
	
	public MultiTree getMultiTree(long [][]data, long appId, long start, long end){
		if(data == null){ return null; }
		MultiTree tree = new MultiTree();
		Map<String, List<Point>> portids = new HashMap<String, List<Point>>();
		arrayToTree(tree, portids, data, start);
		Set<Node> nodes = null;
		if(end > 0){
			nodes = new HashSet<Node>(1);
			nodes.add(tree.findNode(end));
		}
		else { nodes = tree.getAllLeaves(); }
		List<List<DataRow>> swSwPath = new ArrayList<List<DataRow>>(nodes.size());
		List<List<DataRow>> portPortPath = new ArrayList<List<DataRow>>(nodes.size());
		List<List<DataRow>> swPortPath = new ArrayList<List<DataRow>>(nodes.size());
		List<List<DataRow>> portSwPath = new ArrayList<List<DataRow>>(nodes.size());
		for(Node endNode : nodes){
			if(endNode != null){
				List<DataRow> portIds = new ArrayList<DataRow>();
				List<DataRow> switches = new ArrayList<DataRow>();
				List<DataRow> swPorts = new ArrayList<DataRow>();
				List<DataRow> portSws = new ArrayList<DataRow>();
				Node cur = endNode;
				String key68;
				while(cur.id != start){
					// 交换机连接交换机
					DataRow dr = new DataRow();
					dr.set("app_id", appId);
					dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.set("device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.set("parent_device_id", cur.pid.id);
					dr.set("device_id", cur.id);
					dr.set("has_components", true);
					dr.set("db_type", SrContant.DBTYPE_TPC);
					switches.add(0, dr);
					// 交换机端口连接交换机端口
					key68 = cur.pid.id + "_" + cur.id;
					if(portids.containsKey(key68)){
						for(Point p : portids.get(key68)){
							// port2port
							dr = new DataRow();
							dr.set("app_id", appId);
							dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
							dr.set("device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
							dr.set("parent_device_id", p.x);
							dr.set("device_id", p.y);
							dr.set("has_components", false);
							dr.set("db_type", SrContant.DBTYPE_TPC);
							portIds.add(dr);
							// swStart(pid)-->swPortId(x)-->portSwId(y)-->swEnd(id)
							// switch2port
							dr = new DataRow();
							dr.set("app_id", appId);
							dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
							dr.set("device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
							dr.set("parent_device_id", cur.pid.id);
							dr.set("device_id", p.x);
							dr.set("has_components", false);
							dr.set("db_type", SrContant.DBTYPE_TPC);
							swPorts.add(dr);
							// port2switch
							dr = new DataRow();
							dr.set("app_id", appId);
							dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
							dr.set("device_type", SrContant.SUBDEVTYPE_SWITCH);
							dr.set("parent_device_id", p.y);
							dr.set("device_id", cur.id);
							dr.set("has_components", false);
							dr.set("db_type", SrContant.DBTYPE_TPC);
							portSws.add(dr);
						}
					}
					cur = cur.pid;
				}
				swSwPath.add(switches);
				portPortPath.add(portIds);
				portSwPath.add(portSws);
				swPortPath.add(swPorts);
			}
		}
		tree.setPortLinkPorts(portPortPath);
		tree.setSwitchLinkSwitchs(swSwPath);
		tree.setSwitchLinkPorts(swPortPath);
		tree.setPortLinkSwitchs(portSwPath);
		return tree;
	}
	
	public DataRow getPort2PortDataRow(long appId, long start, long end){
		DataRow dr = new DataRow();
		dr.set("app_id", appId);
		dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
		dr.set("device_type", SrContant.SUBDEVTYPE_SWITCHPORT);
		dr.set("parent_device_id", start);
		dr.set("device_id", end);
		dr.set("has_components", false);
		dr.set("db_type", SrContant.DBTYPE_TPC);
		return dr;
	}
	
	public long[][] convert(List<DataRow> data){
		if(data == null || data.size() == 0){ return null; }
		// 56764, 56775, 10000, 10001, 0
		long arr[][] = new long[data.size()][4];
		int i = 0;
		for(DataRow dr : data){
			arr[i][0] = dr.getLong("sw_id1");
			arr[i][1] = dr.getLong("sw_id2");
			arr[i][2] = dr.getLong("swp_id1");
			arr[i][3] = dr.getLong("swp_id2");
			++i;
		}
		return arr;
	}
	
	/**
	 * @see 将搜索出来的交换机映射关系过滤之后再返回
	 * @param data
	 * @param contain
	 * @return
	 */
	public long[][] convert(List<DataRow> data, Set<Long> contain){
		if(data == null || data.size() == 0){ return null; }
		boolean isContainNotEmpty = contain != null && contain.size() > 0;
		// 56764, 56775, 10000, 10001, 0
		List<long[]> res = new ArrayList<long[]>(data.size());
		long temp, temp2;
		for(DataRow dr : data){
			temp = dr.getLong("sw_id1");
			temp2 = dr.getLong("sw_id2");
			if(isContainNotEmpty){
				if(!(contain.contains(temp) && contain.contains(temp2))){
					continue;
				}
			}
			res.add(new long[]{
					temp, temp2, dr.getLong("swp_id1"), dr.getLong("swp_id2")
			});
		}
		long arr[][] = new long[res.size()][];
		for(int i = 0, size = res.size(); i < size; ++i){
			arr[i] = res.get(i);
		}
		return arr;
	}
	
	public List<DataRow> getSwitch2Switch(MultiTree tree, Long appId, long start, long end){
		if(tree != null && (!tree.getRoot().isLeaf())){
			Node endNode = tree.findNode(end);
			if(endNode != null){
				Node cur = endNode;
				List<DataRow> drs = new ArrayList<DataRow>();
				while(cur.id != start){
					DataRow dr = new DataRow();
					dr.set("app_id", appId);
					dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.set("device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.set("device_id", cur.id);
					cur = cur.pid;
					dr.set("parent_device_id", cur.id);
					dr.set("has_components", true);
					dr.set("db_type", SrContant.DBTYPE_TPC);
					drs.add(dr);
				}
				return drs;
			}
		}
		return null;
	}
	
	public void saveSwitch2Switch(JdbcTemplate srDB, MultiTree tree, Long appId, long start, long end){
		if(tree != null && (!tree.getRoot().isLeaf())){
			Node endNode = tree.findNode(end);
			if(endNode != null){
				Node cur = endNode;
				while(cur.id != start){
					DataRow dr = new DataRow();
					dr.set("app_id", appId);
					dr.set("parent_device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.set("device_type", SrContant.SUBDEVTYPE_SWITCH);
					dr.set("device_id", cur.id);
					cur = cur.pid;
					dr.set("parent_device_id", cur.id);
					dr.set("has_components", true);
					dr.set("db_type", SrContant.DBTYPE_TPC);
					srDB.insert("t_map_devices", dr);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DataRow> getSwitch2Switch(JdbcTemplate tpc){
		String sql = "select sw.switch_id as sw_id1,kk.sw_id2,po.port_id as swp_id1,kk.swp_id2," +
			"sw.the_display_name as sw_name1,kk.sw_name2,sw.ip_address as sw_ip1,kk.sw_ip2 " +
			"from v_res_switch sw join v_res_switch_port po on sw.switch_id=po.switch_id " +
			"join v_res_port2port p2p on po.port_id=p2p.port_id1 join (select s.the_display_name " +
			"as sw_name2,s.switch_id as sw_id2,p.port_id as swp_id2,s.ip_address as sw_ip2 " +
			"from v_res_switch s join v_res_switch2port p on s.switch_id=p.switch_id) kk " +
			"on kk.swp_id2=p2p.port_id2 order by sw.switch_id,po.port_id";
		return tpc.query(sql);
	}
}
