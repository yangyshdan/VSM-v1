<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script>
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/storage/StorageAction?func=exportStorageConfigData&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				IP地址
			</th>
			<th>
				状态
			</th>
			<th>
				物理磁盘容量(G)
			</th>
			<th>
				池容量(G)
			</th>
			<th style="width: 170px;">
				已用池容量(G)
			</th>
			<th>
				可用池容量(G)
			</th>
			<th>
				卷总容量(G)
			</th>
			<th>
				已分配卷总容量(G)
			</th>
			<th>
				未分配卷总容量(G)
			</th>
			<th>
				最近探查时间
			</th>
			<th>
				缓存
			</th>
		</tr>
	</thead>
	<tbody>
	<c:choose>
		<c:when test="${not empty storagePage.data}">
			<c:forEach var="item" items="${storagePage.data}" varStatus="status">
				<c:choose>
					<c:when test="${item.vendor_name=='EMC'}">
						<tr>
							<td>
								<a title="${item.name}" href="${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.name}</a>
								
							</td>
							<td>
								${item.ip_address}
							</td>
							<td>
								<cs:cstatus value="${item.operattonal_status}" />
							</td>
							<td>
								<fmt:formatNumber value="${item.physical_disk_capacity/1024}" pattern="0.00" />
							</td>
							<td>
								<fmt:formatNumber value="${item.total_usable_capacity/1024}" pattern="0.00" />
							</td>
							<td >
								<cs:isProgress total="${item.total_usable_capacity}" available="${item.total_usable_capacity-item.unallocated_usable_capacity}"/>														
							</td>
							<td>
								<fmt:formatNumber value="${item.unallocated_usable_capacity/1024}" pattern="0.00" />
							</td>
							
							<td>
								<fmt:formatNumber value="${item.total_lun_capacity/1024}" pattern="0.00" />
							</td>
							<td>
								<cs:isProgress total="${item.total_lun_capacity/1024}" available="${item.the_assigned_volume_space}"/>														
							</td>												
							<td>
								<fmt:formatNumber value="${item.unmappped_lun_capacity/1024}" pattern="0.00" />
							</td>
							
							<td>
								${item.update_timestamp}
							</td>
							<td>
								${item.cache_gb/1024}
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td>
								<a title="${item.the_display_name}" href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.the_display_name}</a>
							</td>
							<td>
								${item.ip_address}
							</td>
							<td>
								<cs:cstatus value="${item.the_propagated_status}" />
							</td>
							<td>
								<fmt:formatNumber value="${item.the_physical_disk_space}" pattern="0.00" />
							</td>
							<td>
								<fmt:formatNumber value="${item.the_storage_pool_space}" pattern="0.00" />
							</td>
							<td>
								<cs:isProgress total="${item.the_storage_pool_space}" available="${item.the_storage_pool_consumed_space}"/>
														
							</td>
							<td>
								<fmt:formatNumber value="${item.the_storage_pool_available_space}" pattern="0.00" />
							</td>
							
							<td>
								<fmt:formatNumber value="${item.the_volume_space}" pattern="0.00" />
							</td>
							
							<td>
								<cs:isProgress total="${item.the_volume_space}" available="${item.the_assigned_volume_space}"/>
								
							</td>											
							<td>
								<fmt:formatNumber value="${item.the_unassigned_volume_space}" pattern="0.00" />
							</td>
							
							<td>
								${item.last_probe_time}
							</td>
							<td>
								${item.cache}
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:when>
			<c:otherwise>
				<tr>
					<td colspan=12>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="storageListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty storagePage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#storageListpageNub").getLinkStr({pagecount:"${storagePage.totalPages}",curpage:"${storagePage.currentPage}",numPerPage:"${storagePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/storage/StorageAction?func=AjaxStoragePage&"+param,divId:'loadcontent'});
	</script>
</c:if>
<c:if test="${empty storagePage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="hiddenForm">
	<input type="hidden" name="storageName" value="${storageName}">
	<input type="hidden" name="ipAddress" value="${ipAddress}">
	<input type="hidden" name="type" value="${type}">
	<input type="hidden" name="serialNumber" value="${serialNumber}">
	<input type="hidden" name="startPoolCap" value="${startPoolCap}">
	<input type="hidden" name="endPoolCap" value="${endPoolCap}">
	<input type="hidden" name="startPoolAvailableCap" value="${startPoolAvailableCap}">
	<input type="hidden" name="endPoolAvailableCap" value="${endPoolAvailableCap}">
</form>