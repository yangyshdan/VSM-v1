<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>

<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>用户姓名</th>
			<th>登录名</th>
			<th>邮件</th>
			<th>管理级别</th>
			<%--<th>工号</th>
			<th>身份证号</th>
			<th>性别</th>
			<th>所在部门</th>
			<th>入职日期</th>--%>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty dbPage.data}">
				<c:forEach var="item" items="${dbPage.data}" varStatus="status">
					<tr>
						<td>${item.fname}</td>
						<td>${item.floginname}</td>
						<td>${item.femail}</td>
						<td>
							<c:choose>
								<c:when test="${fn:toLowerCase(item.froleid) == 'super'}">管理员</c:when>
								<c:otherwise>用户</c:otherwise>
							</c:choose>
						</td>
						<%--<td>${item.user_account}</td>
						<td>${item.id_card}</td>
						<td>${item.gender}</td>
						<td>${item.department}</td>
						<td>${item.hire_date}</td>--%>
						<td>
							<a class="btn btn-info" href="javascript:void(0)" title="查看该用户的详细信息" onclick="VSMUsers.seeUserInfo('${item.fid}')"><i class="icon-check icon-white"></i>查看</a>
							<a class="btn btn-info" href="javascript:void(0)" title="编辑该用户的详细信息" onclick="VSMUsers.editUserInfo('${item.fid}')"><i class="icon-edit icon-white"></i>编辑</a>
							<a class="btn btn-danger" href="javascript:void(0)" title="删除该用户" onclick="VSMUsers.delUserInfo('${item.fid}')"><i class="icon-trash icon-white"></i>删除</a>
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr><td colspan="8">暂无数据！</td></tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
		
<div class="pagination pagination-centered"><ul id="userListNub"></ul></div>
<c:if test="${not empty dbPage.data}">
	<form id="userHiddenForm">
		<input type="hidden" name="curPage" value="${dbPage.currentPage}">
		<input type="hidden" name="numPerPage" value="${dbPage.numPerPage}">
	</form>
	<script>
		$("#userListNub").getLinkStr({
			pagecount:"${dbPage.totalPages}",
			curpage:"${dbPage.currentPage}",
			numPerPage:"${dbPage.numPerPage}",
			isShowJump:true,
			ajaxRequestPath:"${path}/servlet/user/UserAction?func=AjaxPage",
					divId:'userContent'});
	</script>
</c:if>