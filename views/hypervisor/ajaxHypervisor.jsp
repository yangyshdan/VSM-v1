<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script>
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportHypervisorConfigData&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				IP地址
			</th>
			<th>
				虚拟机个数
			</th>
			<th>
				处理器总数
			</th>
			<th>
				未分配CPU
			</th>
			<th>
				内存(MB)
			</th>
			<th>
				未分配内存(MB)
			</th>
			<th>
				磁盘总容量(MB)
			</th>
			<th>
				磁盘剩余容量(MB)
			</th>
			<th>
				更新时间
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty hypervisorPage.data}">
				<c:forEach var="item" items="${hypervisorPage.data}" varStatus="status">										
					<tr>
						<td>
							<a title="${item.display_name}" href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}&computerId=${item.computer_id}">${item.display_name}</a>
						</td>
						<td>
							${item.ip_address}
						</td>

						<td>
							<cs:isZeroAndNull value="${item.vcount}"></cs:isZeroAndNull>
						</td>
						<td>
							<cs:isZeroAndNull value="${item.processor_count}"></cs:isZeroAndNull>
						</td>
						<td>
							${item.available_cpu}
						</td>
						<td>
							<cs:isZeroAndNull value="${item.ram_size}"></cs:isZeroAndNull>
						</td>
						<td>
							<cs:isProgress total="${item.ram_size}" available="${item.available_mem}"/>
						</td>
						<td>
							<cs:isZeroAndNull value="${item.disk_space}"></cs:isZeroAndNull>
						</td>
						<td>
							<cs:isProgress total="${item.disk_space}" available="${item.disk_available_space}"/>
						</td>
						<td>
							${item.update_timestamp}
						</td>
					</tr>															
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=10>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
</tbody>
</table>
<div id="hypervisorListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty hypervisorPage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#hypervisorListpageNub").getLinkStr({pagecount:"${hypervisorPage.totalPages}",curpage:"${hypervisorPage.currentPage}",numPerPage:"${hypervisorPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage&"+param,divId:'hypervisorContent'});
	</script>
</c:if>
<c:if test="${empty hypervisorPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="hiddenForm">
	<input type="hidden" name="displayName" value="${displayName}">
	<input type="hidden" name="ipAddress" value="${ipAddress}">
	<input type="hidden" name="cpuArchitecture" value="${cpuArchitecture}">
	<input type="hidden" name="startRamSize" value="${startRamSize}">
	<input type="hidden" name="endRamSize" value="${endRamSize}">
	<input type="hidden" name="startDiskSpace" value="${startDiskSpace}">
	<input type="hidden" name="endDiskSpace" value="${endDiskSpace}">
</form>
