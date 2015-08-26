<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>

<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>角色名称</th>
			<th>权限数量</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty dbPage.data}">
				<c:forEach var="item" items="${dbPage.data}" varStatus="status">
					<tr>
						<td>${item.fname}</td>
						<td>${item.miCount}</td>
						<td>
							<a class="btn btn-info" href="javascript:void(0)" title="查看角色" onclick="VSMRoles.showRoleInfo('${item.fid}')"><i class="icon-check icon-white"></i>查看</a>
							<a class="btn btn-info" href="javascript:void(0)" title="编辑角色" onclick="VSMRoles.editRoleInfo('${item.fid}')"><i class="icon-edit icon-white"></i>编辑</a>
							<a class="btn btn-danger" href="javascript:void(0)" title="删除角色" onclick="VSMRoles.delRoleInfo('${item.fid}')"><i class="icon-trash icon-white"></i>删除</a>
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr><td colspan="3">暂无数据！</td></tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
		
<div class="pagination pagination-centered"><ul id="rolesListNub"></ul></div>
<c:if test="${not empty dbPage.data}">
	<form id="roleHiddenForm">
		<input type="hidden" name="curPage" value="${dbPage.currentPage}">
		<input type="hidden" name="numPerPage" value="${dbPage.numPerPage}">
	</form>
	<script>
		$("#rolesListNub").getLinkStr({
			pagecount:"${dbPage.totalPages}",
			curpage:"${dbPage.currentPage}",
			numPerPage:"${dbPage.numPerPage}",
			isShowJump:true,
			ajaxRequestPath:"${path}/servlet/roles/RolesAction?func=AjaxPage",
			divId: "rolesContent"
		});
	</script>
</c:if>