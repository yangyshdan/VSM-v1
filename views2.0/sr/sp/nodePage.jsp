<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed">
<thead>
	<tr>
		<th>
			控制器名称
		</th>
		<th>
			系统
		</th>
		<th>
			序列号
		</th>
		<th>
			编号
		</th>
		<th>
			卷数量
		</th>
		<th>
			更新时间
		</th>				
	</tr>
</thead>
<tbody>
	<c:choose>
		<c:when test="${not empty nodePage.data}">
			<c:forEach var="item" items="${nodePage.data}" varStatus="status">
				<tr>
					<td>
						<a href="${path}/servlet/sr/storagenode/StoragenodeAction?func=LoadStoragenodeInfo&subsystemId=${subSystemID}&spId=${item.sp_id}&storageType=${storageType}">${item.sp_name}</a>
					</td>
					<td>
						${item.model}
					</td>
					<td>
						${item.emc_serial_number}
					</td>
					<td>
						${item.emc_part_number}
					</td>
					<td>
						${item.lun_num}
					</td>
					<td>
						${item.update_timestamp}
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=5>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="nodePageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty nodePage.data}">
<script>
	$("#nodePageNub").getLinkStr({pagecount:"${nodePage.totalPages}",curpage:"${nodePage.currentPage}",numPerPage:"${nodePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagenode/StoragenodeAction?func=AjaxNodePage&subSystemID=${subSystemID}&storageType=${storageType}",divId:'nodeContent'});
</script>
</c:if>	