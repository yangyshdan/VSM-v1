<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>

<table class="table table-bordered table-striped table-condensed" style="table-layout:fixed;height:100%" id="conTable">
	<thead>
		<tr>
			<th width="16%">传感器数据记录类型</th>
			<th width="16%">监控项目</th>
			<th width="16%">设备名称</th>
			<th width="16%">状态</th>
			<th width="16%">读数</th>
			<th width="16%">更新时间</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${(not empty sdrPage) and (not empty sdrPage.data) and fn:length(sdrPage.data) > 0}">
				<c:forEach items="${sdrPage.data}" var="item">
				<tr>
					<td>${item.sdr_type}</td>
					<td>${item.device_type}</td>
					<td>${item.device_name}</td>
					<td>${item.status}</td>
					<td>${item.read_data}</td>
					<td>${item.update_timestamp}</td>
				</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="6">暂无数据!</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="HardwarePageNub" class="pagination pagination-centered"></div>
<c:if test="${(not empty sdrPage) and (not empty sdrPage.data) and fn:length(sdrPage.data) > 0}">
	<script>
		$("#HardwarePageNub").getLinkStr({pagecount:"${sdrPage.totalPages}",curpage:"${sdrPage.currentPage}",numPerPage:"${sdrPage.numPerPage}",isShowJump:true,
		ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=SdrPage&hypervisorId=${hypervisorId}",divId: "hardwareStatus2"});
	</script>
</c:if>
