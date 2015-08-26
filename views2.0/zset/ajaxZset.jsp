<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script>
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/zset/ZsetAction?func=exportZsetConfigData&"+param;
	$("#exportCSV").unbind();
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				Zone数量
			</th>
			<th>
				Fabric网络	
			</th>
			<th>
				是否活动
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty zsetPage.data}">
				<c:forEach var="item" items="${zsetPage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path }/servlet/zset/ZsetAction?func=ZsetInfo&zsetId=${item.zset_id}">${item.the_display_name}</a>
						</td>
						<td>
							${item.the_zone_count}
						</td>
						<td>
							<a title="${item.f_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.f_name}</a>
						</td>
						<td>
							<cs:isActive value="${item.active}" />
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=4>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="zsetListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty zsetPage.data}">
	<script>
		var param = $("#hiddenForm").serialize();
		$("#zsetListpageNub").getLinkStr({pagecount:"${zsetPage.totalPages}",curpage:"${zsetPage.currentPage}",numPerPage:"${zsetPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/zset/ZsetAction?func=AjaxZsetPage&"+param+"&fabricId=${fabricId}",divId:'zsetContent'});
	</script>
</c:if>
<c:if test="${empty zsetPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="hiddenForm">
	<input type="hidden" name="name" value="${name}">
	<input type="hidden" name="active" value="${active}">
</form>