<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				用户名
			</th>
			<th>
				设备类型
			</th>
			<th>
				操作
			</th>
		</tr>
	</thead>
	<tbody>
	<c:choose>
		<c:when test="${not empty nasPage.data}">
			<c:forEach var="item" items="${nasPage.data}" varStatus="status">
				<tr>
					<td>
						${item.device_name}
					</td>
					<td>
						${item.users}
					</td>
					<td>
						${item.dname}
					</td>
					<td>
						<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&id=${item.id}&typeId=5',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
						<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},5)"><i class="icon-trash icon-white"></i>删除</a>
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
<div id="naspageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty nasPage.data}">
<script>
	var param = $("#conditionForm5").serialize();
	$("#naspageNub").getLinkStr({pagecount:"${nasPage.totalPages}",curpage:"${nasPage.currentPage}",numPerPage:"${nasPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=AjaxDevicePage&"+param,divId:'nascontent'});
</script>
</c:if>