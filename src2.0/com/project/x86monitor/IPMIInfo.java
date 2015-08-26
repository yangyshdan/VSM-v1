package com.project.x86monitor;

/**
 * @category 保存IPMI的连接信息
 * @author 何高才
 *
 */
public class IPMIInfo {
	private Long hypervisorId;
	private String userName;
	private String password;
	private String ipAddress;
	private String port = "623";
	private Integer level;
	private Integer authType;
	private String hypervisorName;
	
	public IPMIInfo() { }

	public IPMIInfo(Long hypervisorId, String userName, String password,
			String ipAddress, String port, Integer level, Integer authType) {
		super();
		this.hypervisorId = hypervisorId;
		this.userName = userName;
		this.password = password;
		this.ipAddress = ipAddress;
		this.port = port;
		this.level = level;
		this.authType = authType;
	}

	public Long getHypervisorId() {
		return hypervisorId;
	}

	public void setHypervisorId(Long hypervisorId) {
		this.hypervisorId = hypervisorId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getAuthType() {
		return authType;
	}

	public void setAuthType(Integer authType) {
		this.authType = authType;
	}

	public String getHypervisorName() {
		return hypervisorName;
	}

	public void setHypervisorName(String hypervisorName) {
		this.hypervisorName = hypervisorName;
	}

}
