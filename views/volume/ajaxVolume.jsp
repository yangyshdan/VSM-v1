<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
	var exurl = "${path}/servlet/volume/VolumeAction?func=expertVolumeConfigData&name=${name}&lessLogical_Capacity=${lessLogical_Capacity}&greatLogical_Capacity=${greatLogical_Capacity}&subSystemID=${subSystemID}";
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				逻辑卷名
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
				沉余级别
			</th>
			<th>
				存储池
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
							<a title="${item.the_display_name}" href="${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid=${item.svid}&subSystemID=${subSystemID}">${item.the_display_name}</a>
						</td>
						<td>
							${item.the_consolidated_status}
						</td>
						<td>
							<fmt:formatNumber value="${item.the_capacity}" pattern="0.00"/>
						</td>
						<td>
							<cs:isProgress total="${item.the_capacity}" available="${item.the_used_space}"/>
						</td>
						<td>
							${item.the_redundancy}
						</td>
						<td>
							<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
						</td>
						<td>
							${item.unique_id}
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
<div id="volumeListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty volumePage.data}">
	<script>
		$("#volumeListpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/volume/VolumeAction?func=AjaxVolumePage&greatLogical_Capacity=${greatLogical_Capacity}&lessLogical_Capacity=${lessLogical_Capacity}&name=${name}&subSystemID=${subSystemID}",divId:'volumeContent'});
	</script>
</c:if>
<c:if test="${empty volumePage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id="hiddenName" value="${name}"/>
<input type="hidden" id="hiddenLessLogical_Capacity" value="${lessLogical_Capacity}"/>
<input type="hidden" id="hiddenGreatLogical_Capacity" value="${greatLogical_Capacity}"/>