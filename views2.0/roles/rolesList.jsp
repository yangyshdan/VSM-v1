
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/util.js"></script>
<script src="${path}/resource/js/project/roles.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path}/resource/js/jquery.ztree.core-3.5.min.js"></script>
<script src="${path}/resource/js/jquery.ztree.excheck-3.5.min.js"></script>
<script src="${path}/resource/js/jquery.ztree.exedit-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/resource/css/custom.css" />
<link rel="stylesheet" href="${path}/resource/css/zTreeStyle/zTreeStyle.css" />
<style> .spetable td { text-overflow:ellipsis;overflow:hidden;white-space: nowrap; } </style>
<div id="content">
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>角色列表</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="新增" 
						onclick="VSMRoles.addRoleInfo();"><i class="icon icon-color icon-add"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="刷新" onclick="VSMRoles.dataFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;height:600px;" id="rolesContent">
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
										<td>${item.miCount}
											<%-- 
											<div class="btn-group">
											  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
											   <span class="badge ">${item.miCount}</span>权限<span class="caret"></span>
											  </button>
											  <ul class="dropdown-menu" role="menu" style="max-height: 220px;overflow-y: auto;" aria-labelledby="dLabel">
											  	<c:choose>
											  		<c:when test="${not empty item.menus}">
														<c:forEach var="v" items="${item.menus}" varStatus="status">
															<li><a tabindex="-1" href="#">${v.menu_name}</a></li>
														</c:forEach>
													</c:when>
												</c:choose>
											  </ul>
											</div>--%>
										</td>
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
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>