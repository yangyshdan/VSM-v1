<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				扩展卷数
			</th>
			<th>
				总容量(G)
			</th>
			<th>
				已用容量(G)
			</th>
			<th>
				空闲容量(G)
			</th>
			<th>
				存储池
			</th>
			<th>
				状态
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty rankPage.data}">
				<c:forEach var="item" items="${rankPage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path}/servlet/rank/RankAction?func=RankInfo&subSystemID=${subSystemID}&rankId=${item.storage_extent_id}">${item.the_display_name}</a>
						</td>
						<td>
							${item.the_extent_volume}
						</td>									
						<td>
							<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
						</td>									
						<td>
							<fmt:formatNumber value="${item.the_used_space}" pattern="0.00" />
						</td>									
						<td>
							<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
						</td>									
						<td>
							<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
						</td>									
						<td>
							${item.the_operational_status}
						</td>									
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=8>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="rankListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty rankPage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#rankListpageNub").getLinkStr({
			pagecount:"${rankPage.totalPages}",
			curpage:"${rankPage.currentPage}",
			numPerPage:"${rankPage.numPerPage}",
			isShowJump:true,
			ajaxRequestPath:"${path}/servlet/rank/RankAction?func=AjaxRankPage&subSystemID=${subSystemID}&"+param,
			divId:"rankContent"
		});
		var $csv = $("#exportCSV");
		$csv.unbind();
		var exurl = "${path}/servlet/rank/RankAction?func=exportRankConfigData&name=${name}&type=${type}&rankId=${rankId}&subSystemID=${subSystemID}";
		$csv.attr("href", exurl); 
	</script>
</c:if>
<c:if test="${empty rankPage.data}">
	<script>
		var $csv = $("#exportCSV");
		$csv.unbind();
		$csv.attr("href", "javascript:void(0);");
		$csv.bind("click", function(){ bAlert("暂无可导出数据！"); });
	</script>
</c:if>
<form id="hiddenForm">
<input type="hidden" name="name" value="${name}"/>
<input type="hidden" name="type" value="${type}"/>
</form>