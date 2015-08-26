package com.huiming.service.topo.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.huiming.base.jdbc.DataRow;


public class MultiTree {
	private Node root;
	private List<List<DataRow>> portLinkPorts = null;
	private List<List<DataRow>> switchLinkSwitchs = null;
	
	private List<List<DataRow>> switchLinkPorts = null;
	private List<List<DataRow>> portLinkSwitchs = null;
	
	public MultiTree(){ root = new Node(Integer.MIN_VALUE); }
	
	public void addToRootNode(Node node){
		root.children.add(node);
	}
	
	/**
	 * @see 插入一个节点
	 * @param root
	 * @param node
	 * @return 把父节点返回
	 */
	public Node insert(Node node){
		Node parent = findParent(node);
		if(parent != null){
			parent.children.add(node);
			return parent;
		}
		else {
			this.root.children.add(node);
			return this.root;
		}
	}
	
	public void print(){
		print(this.root);
	}
	
	private void print(Node node){
		if(node != null){
			if(node.isLeaf()){
				System.out.println(node.id);
			}
			else {
				if(node.pid != null){ // 不显示根节点
					System.out.print(node.id + ", ");
				}
				for(Node child : node.children){ print(child); }
			}
		}
	}
	
	public Node findParent(Node node){
		if(node.pid == null){ return null; } // 根节点没有父节点
		return findNodeById(null, node.pid.id);
	}
	
	public Set<Node> findChild(long id){
		Node node = findNodeById(null, id);
		if(node == null){ return null; }
		return node.children;
	}
	
	public Node findNode(long id){
		return findNodeById(null, id);
	}
	
	/**
	 * @see 根据ID找出节点
	 * @param root 如果是要从根节点开始搜索则为null
	 * @param id
	 * @return
	 */
	private Node findNodeById(Node root, long id){
		Node cur;
		if(root == null){ cur = this.root; } // 如果是根节点则直接去访问根节点的子节点
		else {
			if(root.pid != null && root.id == id){ // 说明root不是根节点且id一致
				return root;
			}
			cur = root;
		}
		if(cur.children.isEmpty()){ return null; }
		Node temp;
		for(Node child : cur.children){
			if(child.id == id){ return child; } // 添加成功
			temp = findNodeById(child, id);
			if(temp != null){ return temp; }
		}
		return null;
	}
	
	public int count(){
		return count(this.root) - 1;
	}
	private int count(Node node){
		int count = 0;
		if(node != null){
			++count;
			for(Node child : node.children){
				count += count(child);
			}
		}
		return count;
	}
	
	/**
	 * @see 最后一个节点是否等于id，用于判断终点节点
	 * @param id
	 * @return
	 */
	public boolean isLastEqual(Node root, long id){
		Node cur = root == null? this.root : root;
		if(cur.isLeaf()){ // 这个节点是叶子节点
			return cur.id == id;
		}
		for(Node child : cur.children){
			if(isLastEqual(child, id)){ return true; }
		}
		return false;
	}
	
	public Set<Node> getAllLeaves(){
		return getLeaves(this.root);
	}
	
	private Set<Node> getLeaves(Node node){
		if(node == null){ return null; }
		Set<Node> leaves = new HashSet<Node>();
		if(node.isLeaf()){
			leaves.add(node);
		}
		else {
			for(Node child : node.children){
				if(child.isLeaf()){
					leaves.add(child);
				}
				else {
					leaves.addAll(getLeaves(child));
				}
			}
		}
		return leaves;
	}

	public Node getRoot() {
		return root;
	}

	public List<List<DataRow>> getPortLinkPorts() {
		return portLinkPorts;
	}

	public void setPortLinkPorts(List<List<DataRow>> portLinkPorts) {
		this.portLinkPorts = portLinkPorts;
	}

	public List<List<DataRow>> getSwitchLinkSwitchs() {
		return switchLinkSwitchs;
	}

	public void setSwitchLinkSwitchs(List<List<DataRow>> switchLinkSwitchs) {
		this.switchLinkSwitchs = switchLinkSwitchs;
	}

	public List<List<DataRow>> getSwitchLinkPorts() {
		return switchLinkPorts;
	}

	public void setSwitchLinkPorts(List<List<DataRow>> switchLinkPorts) {
		this.switchLinkPorts = switchLinkPorts;
	}

	public List<List<DataRow>> getPortLinkSwitchs() {
		return portLinkSwitchs;
	}

	public void setPortLinkSwitchs(List<List<DataRow>> portLinkSwitchs) {
		this.portLinkSwitchs = portLinkSwitchs;
	}
}
