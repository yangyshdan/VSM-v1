<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/server.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script> 
<script src="${path}/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	$("#conTable").tablesorter();
	changeColumn.initCol();
	if ("${isShowCap}" == 1) {
		drawVirtualCapacity(${serverCapacity});
	}
	
	var param = $("#virtualHiddenForm").serialize();
	var exurl = "${path}/servlet/virtual/VirtualAction?func=ExportVirtualConfigData&"+param;
	$("#exportCSV").unbind();
	$("#exportCSV").attr("href",exurl);
})
</script>
<table class="table table-bordered table-striped table-condensed colToggle" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				所属物理机
			</th>
			<th>
				IP地址
			</th>
			<th>
				逻辑CPU个数
			</th>
			<th>
				物理CPU个数
			</th>
			<th>
				总内存(GB)
			</th>
			<th>
				磁盘容量使用率(%)
			</th>
			<th>
				磁盘总容量(GB)
			</th>
			<th>
				磁盘剩余容量(GB)
			</th>
			<th>
				更新时间
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty virtualPage.data}">
				<c:forEach var="item" items="${virtualPage.data}" varStatus="status">												
					<tr>
						<td>
							<a href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=${item.hypervisor_id}&vmId=${item.vm_id}">${item.display_name}</a>
						</td>
						<td>
							<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.host_name}</a>
						</td>
						<td>
							${item.ip_address}
						</td>
						<td>
							<fmt:formatNumber var="lCpu" value="${item.assigned_cpu_number}" pattern="#"/>
							<cs:isZeroAndNull value="${lCpu}"></cs:isZeroAndNull>
						</td>	
						<td>
							<fmt:formatNumber var="pCpu" value="${item.assigned_cpu_processunit}" pattern="#"/>
							<cs:isZeroAndNull value="${pCpu}"></cs:isZeroAndNull>
						</td>									
						<td>
							<fmt:formatNumber value="${item.total_memory/1024}" pattern="0.##"/>
						</td>
						<td>
							<cs:isProgress total="${item.disk_space}" available="${item.disk_space-item.disk_available_space}" warning="80" error="95"/>
						</td>
						<td>
							<fmt:formatNumber value="${item.disk_space/1024}" pattern="0.##"/>
						</td>									
						<td>
							<fmt:formatNumber value="${item.disk_available_space/1024}" pattern="0.##"/>
						</td>	
						<td>
							${item.update_timestamp}
						</td>
					</tr>											
					
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=9>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
</tbody>
</table>
<div id="virtualListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty virtualPage.data}">
	<script>
		var param = $("#virtualHiddenForm").serialize();
		$("#virtualListpageNub").getLinkStr({pagecount:"${virtualPage.totalPages}",curpage:"${virtualPage.currentPage}",numPerPage:"${virtualPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage&hypervisorId=${hypervisorId}&"+param,divId:'virtualContent'});
	</script>
</c:if>
<c:if test="${empty virtualPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){ bAlert("暂无可导出数据！"); });
	</script>
</c:if>
<form id="virtualHiddenForm">
	<input type="hidden" name="virtualName" value="${virtualName}"/>
	<input type="hidden" name="startMemory" value="${startMemory}"/>
	<input type="hidden" name="endMemory" value="${endMemory}"/>
	<input type="hidden" name="startDiskSpace" value="${startDiskSpace}"/>
	<input type="hidden" name="endDiskSpace" value="${endDiskSpace}"/>
	<input type="hidden" name="isShowCap" value="${isShowCap}"/>
</form>