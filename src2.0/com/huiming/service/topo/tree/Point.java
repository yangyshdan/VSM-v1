package com.huiming.service.topo.tree;

public class Point implements Comparable<Point>{
	public long x;
	public long y;
	public Point(long x, long y) {
		this.x = x;
		this.y = y;
	}
	
	public int compareTo(Point o) {
		if(o == null){ return -1; }
		return ((o.x == this.x && o.y == this.y) || (o.x == this.y && o.y == this.x)? 0 : -1);
	}
}
