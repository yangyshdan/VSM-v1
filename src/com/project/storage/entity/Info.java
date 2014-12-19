package com.project.storage.entity;

public class Info {
	private String username;   //用户名
	private String password;   //密码
	private String ipAddress;  //ip地址
	private String nativePath; //CLI路径
	private String systemName; //存储系统名
	private Integer subSystemID;  //存储系统ID
	private Integer isUpdateConfig = 0 ;  //是否更新配置信息
	private Integer state;
	
	public Integer getIsUpdateConfig() {
		return isUpdateConfig;
	}

	public void setIsUpdateConfig(Integer isUpdateConfig) {
		this.isUpdateConfig = isUpdateConfig;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getSubSystemID() {
		return subSystemID;
	}

	public void setSubSystemID(Integer subSystemID) {
		this.subSystemID = subSystemID;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNativePath() {
		return nativePath;
	}

	public void setNativePath(String nativePath) {
		this.nativePath = nativePath;
	}

	@Override
	public String toString() {
		return "Info [username=" + username + ", password=" + password
				+ ", ipAddress=" + ipAddress + ", nativePath=" + nativePath
				+ ", systemName=" + systemName + ", subsystemId="+subSystemID
				+ ", isUpdateConfig=" + isUpdateConfig + ", state="+state+"]";
	}
}
