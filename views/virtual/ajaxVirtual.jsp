<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script>
var param = $("#virtualHiddenForm").serialize();
var exurl = "${path}/servlet/virtual/VirtualAction?func=exportVirtualConfigData&hypervisorId=${hypervisorId}&"+param;
$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
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
				CPU逻辑个数
			</th>
			<th>
				CPU物理个数
			</th>
			<th>
				总内存(MB)
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
			<c:when test="${not empty virtualPage.data}">
				<c:forEach var="item" items="${virtualPage.data}" varStatus="status">												
					<tr>
						<td>
							<a title="${item.display_name}" href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=${item.hypervisor_id}&vmId=${item.vm_id}">${item.display_name}</a>
						</td>
						<td>
							<a title="${item.host_name}" href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.host_name}</a>
						</td>
						<td>
							${item.ip_address}
						</td>
						<td>
							${item.assigned_cpu_number}
						</td>	
						<td>
							${item.assigned_cpu_processunit}
						</td>									
						<td>
							${item.total_memory}
						</td>	
						<td>
							${item.disk_space}
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
		var param = $("#conditionForm").serialize();
		$("#virtualListpageNub").getLinkStr({pagecount:"${virtualPage.totalPages}",curpage:"${virtualPage.currentPage}",numPerPage:"${virtualPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage&hypervisorId=${hypervisorId}&"+param,divId:'virtualContent'});
	</script>
</c:if>
<c:if test="${empty virtualPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="virtualHiddenForm">
<input type="hidden" id="hiddenvirtualName" name="virtualName" value="${virtualName}"/>
<input type="hidden" id="hiddenstartMemory" name="startMemory" value="${startMemory}"/>
<input type="hidden" id="hiddenendMemory" name="endMemory" value="${endMemory}"/>
<input type="hidden" id="hiddenstartDiskSpace" name="startDiskSpace" value="${startDiskSpace}"/>
<input type="hidden" id="hiddenendDiskSpace" name="endDiskSpace" value="${endDiskSpace}"/>
</form>