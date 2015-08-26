<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
var exurl = "${path}/servlet/sr/storagegroup/StoragegroupAction?func=ExportVolumeConfig&hostgroupId=${hostgroupId}";
$("#volumeExportCSV").attr("href",exurl);
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
		$("#volumeajaxpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagegroup/StoragegroupAction?func=HostgroupVolumePage&hostgroupId=${hostgroupId}&subsystemId=${subsystemId}&storageType=${storageType}",divId:'volumeContent'});
	</script>
</c:if>
<c:if test="${empty volumePage.data}">
	<script>
		$("#volumeExportCSV").unbind();
		$("#volumeExportCSV").attr("href","javascript:void(0);");
		$("#volumeExportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id="hiddenName" value="${name}"/>
<input type="hidden" id="hiddenLessLogical_Capacity" value="${lessLogical_Capacity}"/>
<input type="hidden" id="hiddenGreatLogical_Capacity" value="${greatLogical_Capacity}"/>