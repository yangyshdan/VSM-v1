package com.project.x86monitor;

import java.util.List;




public class DeviceInfo {
//	private final Logger logger = Logger.getLogger(DeviceInfo.class);
	private Long computerId;  // 就是指t_server的ID
	private String hostName;
	private String ipAddress;
//	private String anotherIpAddress;
	private String model;
	private String username;
	private String password;
	private String vendor;
	private String currentIP; // 当前连接上的IP是哪个IP
	private Integer authentication;
	private Integer impersonate;
	private Boolean state;
	private List<Integer> switchIds;
	private List<String> switchNames;
	private String toptype;
	
	public String getToptype() {
		return toptype;
	}

	public void setToptype(String toptype) {
		this.toptype = toptype;
	}

	public DeviceInfo(){

	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

//	public String getAnotherIpAddress() {
//		return anotherIpAddress;
//	}
//
//	public void setAnotherIpAddress(String anotherIpAddress) {
//		this.anotherIpAddress = anotherIpAddress;
//	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
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

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public Integer getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Integer authentication) {
		this.authentication = authentication;
	}

	public Integer getImpersonate() {
		return impersonate;
	}

	public void setImpersonate(Integer impersonate) {
		this.impersonate = impersonate;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public List<Integer> getSwitchIds() {
		return switchIds;
	}

	public void setSwitchIds(List<Integer> switchIds) {
		this.switchIds = switchIds;
	}

	public List<String> getSwitchNames() {
		return switchNames;
	}

	public void setSwitchNames(List<String> switchNames) {
		this.switchNames = switchNames;
	}

	public Long getComputerId() {
		return computerId;
	}

	public void setComputerId(Long computerId) {
		this.computerId = computerId;
	}

	@Override
	public String toString() {
		return "DeviceInfo [computerId=" + computerId + ", hostName=" + hostName + ", ipAddress=" + ipAddress
				+ ", model=" + model
				+ ", username=" + username + ", password=" + password
				+ ", vendor=" + vendor + ", currentIP=" + currentIP
				+ ", authentication=" + authentication + ", impersonate="
				+ impersonate + ", state=" + state
				+ ", toptype="+ toptype +"]";
	}
	
//	private long fid;
//	private String fno;
//	private String fruleid;
//	private int flogtype;
//	private String ftopid;
//	private String ftoptype;
//	private String ftopname;
//	private String fresourceid;
//	private String fresourcename;
//	private String fresourcetype;
//	private int fcount;
//	private Date ffirsttime;
//	private Date flasttime;
//	private String flevel;
//	private String fdescript;
//	private String fdetail;
//	private int fstate;
//	private String fsourcetype;
//	private int fisforward;
//	private String fname;
//	private String fremark;
//	private Date fconfirmtime;
//	private int fisdelete;
//
//	public long getFid() {
//		return fid;
//	}
//
//	public void setFid(long fid) {
//		this.fid = fid;
//	}
//
//	public String getFno() {
//		return fno;
//	}
//
//	public void setFno(String fno) {
//		this.fno = fno;
//	}
//
//	public String getFruleid() {
//		return fruleid;
//	}
//
//	public void setFruleid(String fruleid) {
//		this.fruleid = fruleid;
//	}
//
//	public int getFlogtype() {
//		return flogtype;
//	}
//
//	public void setFlogtype(int flogtype) {
//		this.flogtype = flogtype;
//	}
//
//	public String getFtopid() {
//		return ftopid;
//	}
//
//	public void setFtopid(String ftopid) {
//		this.ftopid = ftopid;
//	}
//
//	public String getFtoptype() {
//		return ftoptype;
//	}
//
//	public void setFtoptype(String ftoptype) {
//		this.ftoptype = ftoptype;
//	}
//
//	public String getFtopname() {
//		return ftopname;
//	}
//
//	public void setFtopname(String ftopname) {
//		this.ftopname = ftopname;
//	}
//
//	public String getFresourceid() {
//		return fresourceid;
//	}
//
//	public void setFresourceid(String fresourceid) {
//		this.fresourceid = fresourceid;
//	}
//
//	public String getFresourcename() {
//		return fresourcename;
//	}
//
//	public void setFresourcename(String fresourcename) {
//		this.fresourcename = fresourcename;
//	}
//
//	public String getFresourcetype() {
//		return fresourcetype;
//	}
//
//	public void setFresourcetype(String fresourcetype) {
//		this.fresourcetype = fresourcetype;
//	}
//
//	public int getFcount() {
//		return fcount;
//	}
//
//	public void setFcount(int fcount) {
//		this.fcount = fcount;
//	}
//
//	public Date getFfirsttime() {
//		return ffirsttime;
//	}
//
//	public void setFfirsttime(Date ffirsttime) {
//		this.ffirsttime = ffirsttime;
//	}
//
//	public Date getFlasttime() {
//		return flasttime;
//	}
//
//	public void setFlasttime(Date flasttime) {
//		this.flasttime = flasttime;
//	}
//
//	public String getFlevel() {
//		return flevel;
//	}
//
//	public void setFlevel(String flevel) {
//		this.flevel = flevel;
//	}
//
//	public String getFdescript() {
//		return fdescript;
//	}
//
//	public void setFdescript(String fdescript) {
//		this.fdescript = fdescript;
//	}
//
//	public String getFdetail() {
//		return fdetail;
//	}
//
//	public void setFdetail(String fdetail) {
//		this.fdetail = fdetail;
//	}
//
//	public int getFstate() {
//		return fstate;
//	}
//
//	public void setFstate(int fstate) {
//		this.fstate = fstate;
//	}
//
//	public String getFsourcetype() {
//		return fsourcetype;
//	}
//
//	public void setFsourcetype(String fsourcetype) {
//		this.fsourcetype = fsourcetype;
//	}
//
//	public int getFisforward() {
//		return fisforward;
//	}
//
//	public void setFisforward(int fisforward) {
//		this.fisforward = fisforward;
//	}
//
//	public String getFname() {
//		return fname;
//	}
//
//	public void setFname(String fname) {
//		this.fname = fname;
//	}
//
//	public String getFremark() {
//		return fremark;
//	}
//
//	public void setFremark(String fremark) {
//		this.fremark = fremark;
//	}
//
//	public Date getFconfirmtime() {
//		return fconfirmtime;
//	}
//
//	public void setFconfirmtime(Date fconfirmtime) {
//		this.fconfirmtime = fconfirmtime;
//	}
//
//	public int getFisdelete() {
//		return fisdelete;
//	}
//
//	public void setFisdelete(int fisdelete) {
//		this.fisdelete = fisdelete;
//	}
	
	
}
