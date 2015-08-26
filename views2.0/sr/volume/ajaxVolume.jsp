<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
	var exurl = "${path}/servlet/sr/volume/VolumeAction?func=expertVolumeConfigData&name=${name}&lessLogical_Capacity=${lessLogical_Capacity}&greatLogical_Capacity=${greatLogical_Capacity}&poolId=${poolId}&subSystemID=${subSystemID}";
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				逻辑卷名
			</th>
			<th>
				逻辑空间(G)
			</th>
			<th>
				实占空间(G)
			</th>
			<th>
				RAID类型
			</th>
			<th>
				所属控制器
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty volumePage.data}">
				<c:forEach var="item" items="${volumePage.data}" varStatus="status">
					<tr>
						<td>
							<a href="${path}/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=${subSystemID}&volumeId=${item.volume_id}&storageType=${storageType}">LUN ${item.name}</a>
						</td>
						<td>
							<fmt:formatNumber value="${item.logical_capacity/1024}" pattern="0.00"/>
						</td>
						<td>
							<c:if test="${not empty item.physical_capacity}"><fmt:formatNumber value="${item.physical_capacity/1024}" pattern="0.00"/></c:if>
							<c:if test="${empty item.physical_capacity}">N/A</c:if>
						</td>
						<td>
							${item.raid_level}
						</td>
						<td>
							<c:if test="${empty item.current_owner}">N/A</c:if>
							<c:if test="${not empty item.current_owner}">
								<a href="${path}/servlet/sr/storagenode/StoragenodeAction?func=StoragenodePrfInfo&spId=${item.sp_id}&subSystemID=${subSystemID}">${item.current_owner}</a>
							</c:if>
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
<div id="volumeajaxpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty volumePage.data}">
	<script>
		$("#volumeajaxpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage&name=${name}&greatLogical_Capacity=${greatLogical_Capacity}&lessLogical_Capacity=${lessLogical_Capacity}&poolId=${poolId}&subSystemID=${subSystemID}&storageType=${storageType}",divId:'volumeContent'});
	</script>
</c:if>
<input type="hidden" id="hiddenName" value="${name}"/>
<input type="hidden" id="hiddenLessLogical_Capacity" value="${lessLogical_Capacity}"/>
<input type="hidden" id="hiddenGreatLogical_Capacity" value="${greatLogical_Capacity}"/>