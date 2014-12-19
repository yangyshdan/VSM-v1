<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
	var exurl = "${path}/servlet/pool/PoolAction?func=expertVolumeConfigData&poolId=${poolId}&subSystemID=${subSystemID}";
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				逻辑卷名
			</th>
			<th>
				存储系统
			</th>
			<th>
				状态
			</th>
			<th>
				容量(G)
			</th>
			<th>
				已用容量(G)
			</th>
			<th>
				冗余级别
			</th>
			<th>
				唯一编号
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty volumePage.data}">
				<c:forEach var="item" items="${volumePage.data}" varStatus="status">
					<tr>
						<td>
							${item.the_display_name}
						</td>
						<td>
							<a title="${item.sub_name}" href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.sub_name }</a>
						</td>
						<td>
							${item.the_consolidated_status}
						</td>
						<td>
							<fmt:formatNumber value="${item.the_capacity}" pattern="0.00"/>
						</td>
						<td>
							<fmt:formatNumber value="${item.the_used_space}" pattern="0.00"/>
						</td>
						<td>
							${item.the_redundancy}
						</td>
						<td>
							${item.unique_id}
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
<div id="volumeListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty volumePage.data}">
	<script>
		$("#volumeListpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/pool/PoolAction?func=VolumeInfo&poolId=${poolId}&isFreshen=1",divId:'volumeContent'});
	</script>
</c:if>
<c:if test="${empty volumePage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>