package com.project.ipnetwork;

import java.util.Map;

public class TCfgDeviceSnmp {
    private Integer snmpId;

    private Integer groupId;
    
    private Integer deviceId;

    private String ipAddressV4;

    private String ipAddressV6;

    private String snmpVersion;

    private Integer snmpPort;

    private String snmpCommunity;

    private String snmpV3UserName;

    private String snmpV3OptionalName;

    private String snmpV3AuthProtocal;

    private String snmpV3AuthPasswd;

    private String snmpV3EncryptProtocal;

    private String snmpV3EncryptPasswd;

    private Integer snmpTimeout;

    private Integer snmpRetry;

    private String description;
    
    private String groupName;

    private Integer pollingIntervalMinute;

    private Integer pollingIntervalHour;

    private Integer pollingIntervalDay;
    
    private String deviceType;
    
    private String deviceModel;
    
    public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	// entityClass.entity  oid
    private Map<String, String> entityOid;
    
    public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getPollingIntervalMinute() {
		return pollingIntervalMinute;
	}

	public void setPollingIntervalMinute(Integer pollingIntervalMinute) {
		this.pollingIntervalMinute = pollingIntervalMinute;
	}

	public Integer getPollingIntervalHour() {
		return pollingIntervalHour;
	}

	public void setPollingIntervalHour(Integer pollingIntervalHour) {
		this.pollingIntervalHour = pollingIntervalHour;
	}

	public Integer getPollingIntervalDay() {
		return pollingIntervalDay;
	}

	public void setPollingIntervalDay(Integer pollingIntervalDay) {
		this.pollingIntervalDay = pollingIntervalDay;
	}

    public Map<String, String> getEntityOid() {
		return entityOid;
	}

	public void setEntityOid(Map<String, String> entityOid) {
		this.entityOid = entityOid;
	}

	public Integer getSnmpId() {
        return snmpId;
    }

    public void setSnmpId(Integer snmpId) {
        this.snmpId = snmpId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getIpAddressV4() {
        return ipAddressV4;
    }

    public void setIpAddressV4(String ipAddressV4) {
        this.ipAddressV4 = ipAddressV4 == null ? null : ipAddressV4.trim();
    }

    public String getIpAddressV6() {
        return ipAddressV6;
    }

    public void setIpAddressV6(String ipAddressV6) {
        this.ipAddressV6 = ipAddressV6 == null ? null : ipAddressV6.trim();
    }

    public String getSnmpVersion() {
        return snmpVersion;
    }

    public void setSnmpVersion(String snmpVersion) {
        this.snmpVersion = snmpVersion == null ? null : snmpVersion.trim();
    }

    public Integer getSnmpPort() {
        return snmpPort;
    }

    public void setSnmpPort(Integer snmpPort) {
        this.snmpPort = snmpPort;
    }

    public String getSnmpCommunity() {
        return snmpCommunity;
    }

    public void setSnmpCommunity(String snmpCommunity) {
        this.snmpCommunity = snmpCommunity == null ? null : snmpCommunity.trim();
    }

    public String getSnmpV3UserName() {
        return snmpV3UserName;
    }

    public void setSnmpV3UserName(String snmpV3UserName) {
        this.snmpV3UserName = snmpV3UserName == null ? null : snmpV3UserName.trim();
    }

    public String getSnmpV3OptionalName() {
        return snmpV3OptionalName;
    }

    public void setSnmpV3OptionalName(String snmpV3OptionalName) {
        this.snmpV3OptionalName = snmpV3OptionalName == null ? null : snmpV3OptionalName.trim();
    }

    public String getSnmpV3AuthProtocal() {
        return snmpV3AuthProtocal;
    }

    public void setSnmpV3AuthProtocal(String snmpV3AuthProtocal) {
        this.snmpV3AuthProtocal = snmpV3AuthProtocal == null ? null : snmpV3AuthProtocal.trim();
    }

    public String getSnmpV3AuthPasswd() {
        return snmpV3AuthPasswd;
    }

    public void setSnmpV3AuthPasswd(String snmpV3AuthPasswd) {
        this.snmpV3AuthPasswd = snmpV3AuthPasswd == null ? null : snmpV3AuthPasswd.trim();
    }

    public String getSnmpV3EncryptProtocal() {
        return snmpV3EncryptProtocal;
    }

    public void setSnmpV3EncryptProtocal(String snmpV3EncryptProtocal) {
        this.snmpV3EncryptProtocal = snmpV3EncryptProtocal == null ? null : snmpV3EncryptProtocal.trim();
    }

    public String getSnmpV3EncryptPasswd() {
        return snmpV3EncryptPasswd;
    }

    public void setSnmpV3EncryptPasswd(String snmpV3EncryptPasswd) {
        this.snmpV3EncryptPasswd = snmpV3EncryptPasswd == null ? null : snmpV3EncryptPasswd.trim();
    }

    public Integer getSnmpTimeout() {
        return snmpTimeout;
    }

    public void setSnmpTimeout(Integer snmpTimeout) {
        this.snmpTimeout = snmpTimeout;
    }

    public Integer getSnmpRetry() {
        return snmpRetry;
    }

    public void setSnmpRetry(Integer snmpRetry) {
        this.snmpRetry = snmpRetry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}