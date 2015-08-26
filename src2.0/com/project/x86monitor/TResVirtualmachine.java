package com.project.x86monitor;

import java.util.Date;

public class TResVirtualmachine {
    private Integer vmId;

    private Long computerId;

    private Long hypervisorId;

    private Long previousHypervisiorId;

    private String name;

    private String config;

    private String uid;

    private String targetedOs;

    private Short assignedCpuNumber;

    private Integer assignedCpuProcessunit;

    private Short maximumCpuNumber;

    private Integer maximumCpuProcessunit;

    private Short minimumCpuNumber;

    private Integer minimumCpuProcessunit;

    private Long totalMemory;

    private String operationalStatus;

    private Date updateTimestamp;

    private String hostName;

    private String processingMode;

    public Integer getVmId() {
        return vmId;
    }

    public void setVmId(Integer vmId) {
        this.vmId = vmId;
    }

    public Long getComputerId() {
        return computerId;
    }

    public void setComputerId(Long computerId) {
        this.computerId = computerId;
    }

    public Long getHypervisorId() {
        return hypervisorId;
    }

    public void setHypervisorId(Long hypervisorId) {
        this.hypervisorId = hypervisorId;
    }

    public Long getPreviousHypervisiorId() {
        return previousHypervisiorId;
    }

    public void setPreviousHypervisiorId(Long previousHypervisiorId) {
        this.previousHypervisiorId = previousHypervisiorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config == null ? null : config.trim();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getTargetedOs() {
        return targetedOs;
    }

    public void setTargetedOs(String targetedOs) {
        this.targetedOs = targetedOs == null ? null : targetedOs.trim();
    }

    public Short getAssignedCpuNumber() {
        return assignedCpuNumber;
    }

    public void setAssignedCpuNumber(Short assignedCpuNumber) {
        this.assignedCpuNumber = assignedCpuNumber;
    }

    public Integer getAssignedCpuProcessunit() {
        return assignedCpuProcessunit;
    }

    public void setAssignedCpuProcessunit(Integer assignedCpuProcessunit) {
        this.assignedCpuProcessunit = assignedCpuProcessunit;
    }

    public Short getMaximumCpuNumber() {
        return maximumCpuNumber;
    }

    public void setMaximumCpuNumber(Short maximumCpuNumber) {
        this.maximumCpuNumber = maximumCpuNumber;
    }

    public Integer getMaximumCpuProcessunit() {
        return maximumCpuProcessunit;
    }

    public void setMaximumCpuProcessunit(Integer maximumCpuProcessunit) {
        this.maximumCpuProcessunit = maximumCpuProcessunit;
    }

    public Short getMinimumCpuNumber() {
        return minimumCpuNumber;
    }

    public void setMinimumCpuNumber(Short minimumCpuNumber) {
        this.minimumCpuNumber = minimumCpuNumber;
    }

    public Integer getMinimumCpuProcessunit() {
        return minimumCpuProcessunit;
    }

    public void setMinimumCpuProcessunit(Integer minimumCpuProcessunit) {
        this.minimumCpuProcessunit = minimumCpuProcessunit;
    }

    public Long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus == null ? null : operationalStatus.trim();
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName == null ? null : hostName.trim();
    }

    public String getProcessingMode() {
        return processingMode;
    }

    public void setProcessingMode(String processingMode) {
        this.processingMode = processingMode == null ? null : processingMode.trim();
    }
}