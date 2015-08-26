package com.huiming.service.topo.tree;

import java.util.Arrays;
import java.util.TreeSet;

public class Node implements Comparable<Node> {
	public long id;
	public Node pid;
	public TreeSet<Point> portIds = new TreeSet<Point>(); // 这个节点保存端口编号
	
	public TreeSet<Node> children = new TreeSet<Node>();
	
	public Node(long id){
		this(id, null, null);
	}
	
	public Node(Long id, Node pid){
		this(id, pid, null);
	}
	
	public Node(Long id, Node pid, Point p){
		this.id = id;
		this.pid = pid;
		if(p != null){ this.portIds.add(p); }
	}
	
	public Node searchChildren(long id){
		for(Node child : this.children){
			if(id == child.id){ return child; }
		}
		return null;
	}
	
	public void addChild(Node child){
		this.children.add(child);
	}
	
	public boolean isLeaf(){
		return children.isEmpty();
	}

	public int compareTo(Node node) {
		if(node == null){ return 0; }
		return  node.id == this.id? 0 : 1;
	}

	@Override
	public String toString() {
		Long ps[] = new Long[portIds.size()];
		portIds.toArray(ps);
		return "Node [id=" + id + ", pid=" + pid + ", portIds=" + Arrays.toString(ps)
				+ "]";
	}
	
}

