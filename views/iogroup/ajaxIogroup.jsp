<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/iogroup/IogroupAction?func=exportIogroupConfigData&subSystemID=${subSystemID}&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
	<tr>
		<th>
			名称
		</th>
		<th>
			镜像内存(G)
		</th>
		<th>
			镜像空闲内存(G)
		</th>
		<th>
			快照内存(G)
		</th>
		<th>
			快照空闲内存(G)
		</th>
		<th>
			阵列内存(G)
		</th>
		<th>
			阵列空闲内存(G)
		</th>
		<th>
			维护状态
		</th>
	</tr>
</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty iogroupPage.data}">
				<c:forEach var="item" items="${iogroupPage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId=${item.io_group_id}">${item.the_display_name}</a>
						</td>
						<td>
							<fmt:formatNumber value="${item.mirroring_total_memory/1024}" pattern="0.00" />
						</td>
						<td>
							<cs:isProgress total="${item.mirroring_total_memory}" available="${item.mirroring_free_memory}"/>
						</td>
						<td>
							<fmt:formatNumber value="${item.flash_copy_total_memory/1024}" pattern="0.00" />
						</td>
						<td>
							<cs:isProgress total="${item.flash_copy_total_memory}" available="${item.flash_copy_free_memory}"/>
						</td>
						<td>
							<fmt:formatNumber value="${item.raid_total_memory/1024}" pattern="0.00" />
						</td>
						<td>
							<cs:isProgress total="${item.raid_total_memory}" available="${item.raid_free_memory}"/>
						</td>
						<td>
							${item.maintenance}
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
<div id="iogroupListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty iogroupPage.data}">
<script>
	var param = $("#conditionForm").serialize();
	$("#iogroupListpageNub").getLinkStr({pagecount:"${iogroupPage.totalPages}",curpage:"${iogroupPage.currentPage}",numPerPage:"${iogroupPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/iogroup/IogroupAction?func=AjaxIogroupPage&subSystemID=${subSystemID}&"+param,divId:'iogroupContent'});
</script>
</c:if>
<c:if test="${empty iogroupPage.data}">
<script>
	$("#exportCSV").unbind();
	$("#exportCSV").attr("href","javascript:void(0);");
	$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
</script>
</c:if>
<form id="hiddenForm">
<input type="hidden" id="name" name="name" value="${name}"/>
</form>