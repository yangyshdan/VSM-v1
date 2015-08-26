<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script>
	$("#exportCSV").attr("href", "${path}/servlet/hypervisor/HypervisorAction?func=ExportHypervisorConfigData&"
					+ $("#hiddenForm").serialize());
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>名称</th>
			<th>IP地址</th>
			<th>操作系统版本</th>
			<th>CPU架构</th>
			<th>处理器类型</th>
			<th>厂商</th>
			<th>型号</th>
			<th>内存(MB)</th>
			<th>操作状态</th>
			<th>更新时间</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty computerPage.data}">
				<c:forEach var="item" items="${computerPage.data}" varStatus="status">
					<tr>
						<td><a href="${path}/servlet/x86Windows/X86WindowsServerAction?func=X86WindowsServerInfo&computerId=${item.computer_id}">${item.display_name}</a></td>
						<td>${item.ip_address}</td>
						<td>${item.os_version}</td>
						<td>${item.cpu_architecture}</td>
						<td>${item.processor_type}</td>
						<td>${item.vendor}</td>
						<td>${item.model}</td>	
						<td><cs:isZeroAndNull value="${item.ram_size}"></cs:isZeroAndNull></td>
						<td>${item.operational_status}</td>
						<td><fmt:formatDate value="${item.update_timestamp}" type="date" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="10">暂无数据！</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="computerListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty computerPage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#computerListpageNub").getLinkStr({
			pagecount : "${computerPage.totalPages}",
			curpage : "${computerPage.currentPage}",
			numPerPage : "${computerPage.numPerPage}",
			isShowJump : true,
			ajaxRequestPath : "${path}/servlet/x86Windows/X86WindowsServerAction?func=AjaxComputerPage&" + param,
			divId : 'loadcontent'
		});
	</script>
</c:if>
<c:if test="${empty computerPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href", "javascript:void(0);");
		$("#exportCSV").bind("click", 
			function() {
				bAlert("暂无可导出数据！");
			}
		);
	</script>
</c:if>
<form id="hiddenForm">
	<input type="hidden" name="displayName" value="${displayName}">
	<input type="hidden" name="ipaddress" value="${ipaddress}">
</form>
