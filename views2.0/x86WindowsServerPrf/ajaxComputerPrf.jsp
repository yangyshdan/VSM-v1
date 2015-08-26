<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script>
	$("#exportCSV").attr(
			"href",
			"${path}/servlet/hypervisor/HypervisorAction?func=ExportHypervisorConfigData&"
					+ $("#hiddenForm").serialize());
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>所属计算机 </th>
			<th>CPU使用率(%)</th>
			<th>内存使用率(%)</th>
			<th>磁盘读写速率(kb/s)</th>
			<th>磁盘读写IO</th>
			<th>磁盘读响应时间(s)</th>
			<th>磁盘写响应时间(s)</th>
			<th>网络流量速率(kb/s)</th>
			<th>网络数据包</th>
		</tr>
	</thead>
	<c:choose>
		<c:when test="${not empty computerPrfPage.data}">
			<c:forEach var="item" items="${computerPrfPage.data}" varStatus="status">
				<tr>
					<td>${item.computer_name}</td>
					<td>${item.cpu_busy_prct}</td>
					<td>${item.mem_used_prct}</td>
					<td>${item.disk_speed_rate}</td>
					<td>${item.disk_overall_iops}</td>
					<td>${item.disk_read_await}</td>
					<td>${item.disk_write_await}</td>
					<td>${item.net_flow_rate}</td>
					<td>${item.net_packet_rate}</td>
				</tr>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr><td colspan="9">暂无数据！</td></tr>
		</c:otherwise>
	</c:choose>
	</tbody>
</table>
<div id="computerPrfListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty computerPrfPage.data}">
	<script>
	$("#computerPrfListpageNub")
			.getLinkStr(
					{
						pagecount : "${computerPrfPage.totalPages}",
						curpage : "${computerPrfPage.currentPage}",
						numPerPage : "${computerPrfPage.numPerPage}",
						isShowJump : true,
						ajaxRequestPath : "${path}/servlet/x86Windows/X86WindowsServerAction?func=AjaxComputerPrfPage&computerId=${computerId}",
						divId : 'computerPrfContent'
					});
</script>
</c:if>
