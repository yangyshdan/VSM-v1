<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	$("#conTable").tablesorter();
	changeColumn.initCol();
	drawPhysicalCapacity(${serverCapacity});
	
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportHypervisorConfigData&"+param;
	$("#exportCSV").unbind();
	$("#exportCSV").attr("href",exurl);
});
</script>
<table id="conTable" class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
	<thead>
		<tr>
			<th>名称</th>
			<th>IP地址</th>
			<th>操作系统</th>
			<th>
				Hypervisor
			</th>
			<th>
				CPU数量
			</th>
			<th>
				内存(GB)
			</th>
			<th>磁盘容量使用(%)</th>
			<th>
				磁盘总容量(GB)
			</th>
			<th>
				磁盘剩余容量(GB)
			</th>
			<!-- 
			<th>
				虚拟机数量
			</th>
			 -->
			<th>
				更新时间
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty hypervisorPage.data}">
				<c:forEach var="item" items="${hypervisorPage.data}" varStatus="status">										
					<tr>
						<td>
							<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}&computerId=${item.computer_id}">${item.display_name}</a>
						</td>
						<td>
							${item.ip_address}
						</td>
						<td>
							${item.os_version}
						</td>
						<td>
							<c:choose>
								<c:when test="${empty item.vp_id}">N/A</c:when>
								<c:when test="${not empty item.vp_id}">
									<a href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatInfo&virtualPlatId=${item.vp_id}&physicalId=${item.hypervisor_id}">${item.vp_name}</a>
								</c:when>
							</c:choose>
						</td>
						<td>
							<cs:isZeroAndNull value="${item.processor_count}"></cs:isZeroAndNull>
						</td>
						<td>
							<fmt:formatNumber value="${item.ram_size/1024}" pattern="0.##"/>
						</td>
						<td>
							<div style="width:100%;">
								<div style="float:left; width:98%;">
										<c:choose>
											<c:when test="${item.percent <= 0.6}">
												<div style="margin-bottom:0px;" class="progress progress-success progress-striped active">
											</c:when>
											<c:when test="${item.percent > 0.6 and item.percent <= 0.85}">
												<div style="margin-bottom:0px;" class="progress progress-warning progress-striped active">
											</c:when>
											<c:otherwise>
												<div style="margin-bottom:0px;" class="progress progress-danger progress-striped active">
											</c:otherwise>
										</c:choose>
										<div style="width:${item.percent * 100}%" class="bar">
											<span style="color:black;"><fmt:formatNumber value="${item.percent * 100}" pattern="0.#"/>%</span>
										</div>
									</div>
								</div>
								<div style="float:right; width:30%;"></div>
							</div>
						</td>
						<td>
							<fmt:formatNumber value="${item.disk_space/1024}" pattern="0.##"/>
						</td>
						<td>
							<fmt:formatNumber value="${item.disk_available_space/1024}" pattern="0.##"/>
						</td>
						<!-- 
						<td>
							<cs:isZeroAndNull value="${item.vcount}"></cs:isZeroAndNull>
						</td>
						 -->
						<td>
							${item.update_timestamp}
						</td>
					</tr>														
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="10">
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
</tbody>
</table>
<div id="hypervisorListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty hypervisorPage.data}">
	<script>
		var param = $("#hiddenForm").serialize();
		$("#hypervisorListpageNub").getLinkStr({
			pagecount:"${hypervisorPage.totalPages}",
			curpage:"${hypervisorPage.currentPage}",
			numPerPage:"${hypervisorPage.numPerPage}",
			isShowJump:true,
			ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage&"+param,
			divId:'loadcontent'
		});
	</script>
</c:if>
<c:if test="${empty hypervisorPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="hiddenForm">
	<input type="hidden" name="displayName" value="${displayName}">
	<input type="hidden" name="ipAddress" value="${ipAddress}">
	<input type="hidden" name="cpuArchitecture" value="${cpuArchitecture}">
	<input type="hidden" name="startRamSize" value="${startRamSize}">
	<input type="hidden" name="endRamSize" value="${endRamSize}">
	<input type="hidden" name="startDiskSpace" value="${startDiskSpace}">
	<input type="hidden" name="endDiskSpace" value="${endDiskSpace}">
</form>
