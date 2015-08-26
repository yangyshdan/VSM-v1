<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script>
	var exurl = "${path}/servlet/sr/storagenode/StoragenodeAction?func=expertVolumeConfigData&spId=${spId}&subsystemId=${subsystemId}";
	$("#exportVolumeCSV").attr("href",exurl);
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
				控制器类型
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty volumePage.data}">
				<c:forEach var="item" items="${volumePage.data}" varStatus="status">
					<tr>
						<td>
							<a href="${path}/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=${subsystemId}&volumeId=${item.volume_id}&storageType=${storageType}">LUN ${item.name}</a>
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
							<a href="${path}/servlet/sr/storagenode/StoragenodeAction?func=LoadStoragenodeInfo&subsystemId=${subsystemId}&spId=${item.sp_id}&storageType=${storageType}">${item.current_owner}</a>
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
	$("#volumeajaxpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagenode/StoragenodeAction?func=SPvolumePage&spId=${spId}&subsystemId=${subsystemId}&storageType=${storageType}",divId:'volumeContent'});
	</script>
</c:if>
<c:if test="${empty volumePage.data}">
	<script>
		$("#exportVolumeCSV").unbind();
		$("#exportVolumeCSV").attr("href","javascript:void(0);");
		$("#exportVolumeCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id="hiddenName" value="${name}"/>