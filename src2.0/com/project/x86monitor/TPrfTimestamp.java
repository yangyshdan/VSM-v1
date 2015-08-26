package com.project.x86monitor;

import java.util.Date;

public class TPrfTimestamp {
    private Long timeId;

    private Date sampleTime;

    private Integer intervalLen;

    private Short summType;

    private String subsystemName;

    private Long subsystemId;

    private String perfMarker;
    
    private String deviceType;

    public Long getTimeId() {
        return timeId;
    }

    public void setTimeId(Long timeId) {
        this.timeId = timeId;
    }

    public Date getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(Date sampleTime) {
        this.sampleTime = sampleTime;
    }

    public Integer getIntervalLen() {
        return intervalLen;
    }

    public void setIntervalLen(Integer intervalLen) {
        this.intervalLen = intervalLen;
    }

    public Short getSummType() {
        return summType;
    }

    public void setSummType(Short summType) {
        this.summType = summType;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName == null ? null : subsystemName.trim();
    }

    public Long getSubsystemId() {
        return subsystemId;
    }

    public void setSubsystemId(Long subsystemId) {
        this.subsystemId = subsystemId;
    }

    public String getPerfMarker() {
        return perfMarker;
    }

    public void setPerfMarker(String perfMarker) {
        this.perfMarker = perfMarker == null ? null : perfMarker.trim();
    }

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}