<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>

<table class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				类型
			</th>
			<th>
				版本
			</th>
			<th>
				允许最大CPU数量
			</th>
			<th>
				所在物理机
			</th>
			<th>
				虚拟机数量
			</th>
			<th>
				虚拟网络数量
			</th>
			<th>
				网络接口数量
			</th>
		</tr>
	</thead>
	<tbody>		
		<c:choose>
			<c:when test="${not empty virtPlatPage.data}">
				<c:forEach var="item" items="${virtPlatPage.data}" varStatus="status">		
					<tr>
						<td>
							<a href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatInfo&virtualPlatId=${item.id}&physicalId=${item.hypervisor_id}">${item.name}</a>
						</td>
						<td>
							${item.type}
						</td>
						<td>
							${item.version}
						</td>
						<td>
							${empty item.allow_maxcpu ? 'N/A' : item.allow_maxcpu}
						</td>
						<td>
							<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.hypervisor_name}</a>
						</td>
						<td>
							<a href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatInfo&virtualPlatId=${item.id}&physicalId=${item.hypervisor_id}&showVmTab=1">${item.vms_num}</a>
						</td>
						<td>
							${item.interfaces_num}
						</td>
						<td>
							${item.networks_num}
						</td>
					</tr>	
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="8">暂无数据！</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="virtualPlatPageNub" class="pagination pagination-centered"></div>
<script>
	var param = $("#hiddenForm").serialize();
	$("#virtualPlatPageNub").getLinkStr({pagecount:"${virtPlatPage.totalPages}",curpage:"${virtPlatPage.currentPage}",numPerPage:"${virtPlatPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtualPlat/VirtualPlatAction?func=AjaxVirtPlatPage&"+param,divId:'loadcontent'});
</script>
<form id="hiddenForm">
	<input type="hidden" name="name" value="${name}"/>
	<input type="hidden" name="type" value="${type}"/>
	<input type="hidden" name="physicalName" value="${physicalName}"/>
</form>