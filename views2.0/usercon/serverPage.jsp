<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				厂商
			</th>
			<th>
				架构类型
			</th>
			<th>
				操作系统
			</th>
			<th>
				虚拟平台类型
			</th>
			<th>
				服务器类型
			</th>
			<th>
				IP地址
			</th>
			<th>
				用户名
			</th>
			<th>
				状态
			</th>
			<th>
				操作
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty serverPage.data}">
				<c:forEach var="item" items="${serverPage.data}" varStatus="status">
					<tr>
						<td>
							${item.name}
						</td>
						<td>
							${empty item.vendor ? 'N/A' : item.vendor}
						</td>
						<td>
							${empty item.schema_type ? 'N/A' : item.schema_type}
						</td>
						<td>
							${empty item.os_type ? 'N/A' : item.os_type}
						</td>
						<td>
							${empty item.virt_plat_type ? 'N/A' : item.virt_plat_type}
						</td>
						<td>
							<c:if test="${item.toptype == 'physical'}">物理机</c:if>
							<c:if test="${item.toptype == 'virtual'}">虚拟机</c:if>
							<c:if test="${item.toptype == 'unknown'}">未知</c:if>
						</td>
						<td>
							${item.ip_address}
						</td>
						<td>
							${item.user}
						</td>
						<td>
							<c:if test="${item.state == 1}">
								<i class="icon icon-color icon-check"></i>可用
							</c:if>
							<c:if test="${item.state == 0}">
								<i class="icon icon-color icon-close"></i>不可用
							</c:if>
						</td>
						<td>
							<a class="btn btn-info" data-rel='tooltip' href="javascript:MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=editServerInfo&id=${item.id}',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
							<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},6)"><i class="icon-trash icon-white"></i>删除</a>
						</td>
					</tr>											
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="10">
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
</tbody>
</table>
<div id="serverPageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty serverPage.data}">
	<script>
		var param = $("#serverForm").serialize();
		$("#serverPageNub").getLinkStr({pagecount:"${serverPage.totalPages}",curpage:"${serverPage.currentPage}",numPerPage:"${serverPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=ServerPage&"+param,divId:'serverContent'});
	</script>
</c:if>