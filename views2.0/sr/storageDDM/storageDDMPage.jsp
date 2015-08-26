<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>

<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				系统
			</th>
			<th>
				DDM_CAP
			</th>
			<th>
				DDM_SPEED
			</th>
			<th>
				更新时间
			</th>
			<th>
				使用状态
			</th>
			<th>
				陈列名称
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty dbPage.data}">
				<c:forEach var="item" items="${dbPage.data}" varStatus="status">
					<tr>
						<td>
							${item.name}
						</td>
						<td>
							${item.sname}
						</td>
						<td>
							${item.ddm_cap}
						</td>
						<td>
							${item.ddm_speed}
						</td>
						<td>
							${item.update_timestamp}
						</td>
						<td>
							${item.operational_status}
						</td>
						<td>
							${item.display_name}
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=7>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="pageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty dbPage.data}">
	<script>
		$("#pageNub").getLinkStr({pagecount:${dbPage.totalPages},curpage:${dbPage.currentPage},numPerPage:${dbPage.numPerPage},isShowJump:true,ajaxRequestPath:"${path}/servlet/config/ConfigAction?func=AjaxStoragePage",divId:'loadcontent'});
	</script>
</c:if>