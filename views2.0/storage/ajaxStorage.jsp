<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	//加载容量柱形图
	drawStorageCapacity(${arr},${categories});
	$("#conTable").tablesorter();
	changeColumn.initCol();
});

var param = $("#hiddenForm").serialize();
var exurl = "${path}/servlet/storage/StorageAction?func=exportStorageConfigData&"+param;
$("#exportCSV").unbind();
$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all" id="conTable">
	<thead>
		<tr>
			<th>名称</th>
			<th>厂商</th>
			<th>IP地址</th>
			<th>状态</th>
			<th>物理磁盘容量(G)</th>
			<th>池容量(G)</th>
			<th>可用池容量(G)</th>
			<th>容量使用(%)</th>
			<th>卷总容量(G)</th>
			<th>已分配卷总容量(G)</th>
			<th>未分配卷总容量(G)</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty storagePage.data}">
				<c:forEach var="item" items="${storagePage.data}" varStatus="status">
					<c:choose>
						<c:when test="${item.storage_type=='EMC' || item.storage_type=='HDS' || item.storage_type=='NETAPP'}">
							<tr>
								<td>
									<a href="${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.name}</a>
								</td>
								<td>${item.vendor_name}</td>
								<td>
									<c:if test="${not empty item.ip_address}">
										<c:if test="${fn:contains(item.ip_address,',')}">
											<a title="${fn:split(item.ip_address,',')[0]}" href="http://${fn:split(item.ip_address,',')[0]}" target="_blank">${fn:split(item.ip_address,',')[0]}</a>,<a title="${fn:split(item.ip_address,',')[1]}" href="http://${fn:split(item.ip_address,',')[1]}" target="_blank">${fn:split(item.ip_address,',')[1]}</a>
										</c:if>
										<c:if test="${not fn:contains(item.ip_address,',')}">
											<a title="${item.ip_address}" href="http://${item.ip_address}" target="_blank">${item.ip_address}</a>
										</c:if>
									</c:if>
								</td>
								<td>
									<c:if test="${empty item.operattonal_status}">
														<cs:cstatus value="Normal" />
														</c:if>
														<c:if test="${not empty item.operattonal_status}">
														<cs:cstatus value="${item.operattonal_status}" />
														</c:if>
								</td>
								<td>
									<fmt:formatNumber value="${item.physical_disk_capacity/1024}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${item.total_usable_capacity/1024}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${item.unallocated_usable_capacity/1024}" pattern="0.00" />
								</td>
								<td >
									<cs:isProgress total="${item.total_usable_capacity}" available="${item.total_usable_capacity-item.unallocated_usable_capacity}"  warning="60" error="85"/>
								</td>
								<td>
									<fmt:formatNumber value="${item.total_lun_capacity/1024}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${(item.total_lun_capacity - item.unmapped_lun_capacity)/1024}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${item.unmappped_lun_capacity/1024}" pattern="0.00" />
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td>
									<a title="${item.the_display_name}" href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.the_display_name}</a>
								</td>
								<td>${item.vendor_name}</td>
								<td>
									<c:if test="${not empty item.ip_address}">
										<c:if test="${fn:contains(item.ip_address,',')}">
											<a title="${fn:split(item.ip_address,',')[0]}" href="http://${fn:split(item.ip_address,',')[0]}" target="_blank">${fn:split(item.ip_address,',')[0]}</a>,<a title="${fn:split(item.ip_address,',')[1]}" href="http://${fn:split(item.ip_address,',')[1]}" target="_blank">${fn:split(item.ip_address,',')[1]}</a>
										</c:if>
										<c:if test="${not fn:contains(item.ip_address,',')}">
											<a title="${item.ip_address}" href="http://${item.ip_address}" target="_blank">${item.ip_address}</a>
										</c:if>
									</c:if>
								</td>
								<td>
									<cs:cstatus value="${item.the_propagated_status}" />
								</td>
								<td>
									<fmt:formatNumber value="${item.the_physical_disk_space}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${item.the_storage_pool_consumed_space}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${item.the_storage_pool_available_space}" pattern="0.00" />
								</td>
								<td >
									<cs:isProgress total="${item.the_allocated_capacity+item.the_available_capacity}" available="${item.the_allocated_capacity}" warning="" error=""/>														
								</td>
								<td>
									<fmt:formatNumber value="${item.the_volume_space}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${item.the_assigned_volume_space}" pattern="0.00" />
								</td>
								<td>
									<fmt:formatNumber value="${item.the_unassigned_volume_space}" pattern="0.00" />
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=10>
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
		var param = $("#hiddenForm").serialize();
		$("#storageListpageNub").getLinkStr({
			pagecount:"${storagePage.totalPages}",
			curpage:"${storagePage.currentPage}",
			numPerPage:"${storagePage.numPerPage}",
			isShowJump:true,
			ajaxRequestPath:"${path}/servlet/storage/StorageAction?func=AjaxStoragePage&"+param,
			divId:'loadcontent'
		});
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