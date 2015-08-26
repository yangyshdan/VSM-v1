<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script>
	var hostgroupName = $("#hiddenGroupName").val();
	var exurl = "${path}/servlet/sr/storagegroup/StoragegroupAction?func=ExportStoragegroupConfigData&subSystemID=${subSystemID}&hostgroupName=${hostgroupName}";
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				网络地址
			</th>
			<th>
				系统
			</th>
			<th>
				是否可共享 
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty storagegroupPage.data}">
				<c:forEach var="item" items="${storagegroupPage.data}" varStatus="status">
					<tr>
						<td>
							<a href="${path}/servlet/sr/storagegroup/StoragegroupAction?func=StoragegroupInfo&subsystemId=${subSystemID}&hostgroupId=${item.hostgroup_id}&storageType=${storageType}">${item.hostgroup_name}</a>
						</td>
						<td>
							${item.uid}
						</td>
						<td>
							${item.model}
						</td>
						<td>
							${item.shareable}
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=4>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="storagegroupPageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty storagegroupPage.data}">
	<script>
		$("#storagegroupPageNub").getLinkStr({pagecount:"${storagegroupPage.totalPages}",curpage:"${storagegroupPage.currentPage}",numPerPage:"${storagegroupPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagegroup/StoragegroupAction?func=AjaxStoragegroupPage&subSystemID=${subSystemID}&hostgroupName=${hostgroupName}&storageType=${storageType}",divId:'storagegroupContent'});
	</script>
</c:if>	
<c:if test="${empty storagegroupPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id="hiddenGroupName" value="${hostgroupName}"/>