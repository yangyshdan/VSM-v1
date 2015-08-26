package com.project.x86monitor.beans;

import java.util.List;
import java.util.Set;

import com.huiming.base.jdbc.DataRow;
import com.project.x86monitor.IPMIInfo;

public class SysEventLogBean {
	private Long fromNowOn;
	private List<DataRow> sysEventLogs;
	private List<DataRow> updateData;
	private List<DataRow> insertData;
	private Set<Long> statusIds;
	private IPMIInfo ipmi;
	private DataRow config;
	
	public Long getFromNowOn() {
		return fromNowOn;
	}
	public void setFromNowOn(Long fromNowOn) {
		this.fromNowOn = fromNowOn;
	}
	
	public List<DataRow> getSysEventLogs() {
		return sysEventLogs;
	}
	
	public void setSysEventLogs(List<DataRow> sysEventLogs) {
		this.sysEventLogs = sysEventLogs;
	}
	
	public Set<Long> getStatusIds() {
		return statusIds;
	}
	
	public void setStatusIds(Set<Long> statusIds) {
		this.statusIds = statusIds;
	}
	
	public IPMIInfo getIpmi() {
		return ipmi;
	}
	public void setIpmi(IPMIInfo ipmi) {
		this.ipmi = ipmi;
	}
	public List<DataRow> getUpdateData() {
		return updateData;
	}
	public void setUpdateData(List<DataRow> updateData) {
		this.updateData = updateData;
	}
	public List<DataRow> getInsertData() {
		return insertData;
	}
	public void setInsertData(List<DataRow> insertData) {
		this.insertData = insertData;
	}
	public DataRow getConfig() {
		return config;
	}
	public void setConfig(DataRow config) {
		this.config = config;
	}
	
}
