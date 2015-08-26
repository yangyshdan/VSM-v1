<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script>
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/zone/ZoneAction?func=exportZoneConfigData&"+param;
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
				类型
			</th>
			<th>
				Fabric网络
			</th>
			<th>
				WWNN
			</th>
			<th>
				是否活动
			</th>
			<th>
				描述
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty zonePage.data}">
				<c:forEach var="item" items="${zonePage.data}" varStatus="status">
					<tr ondblclick="trDbClick(${item.zone_id})" style="cursor:pointer;">
						<td>
							${item.the_display_name}
						</td>
						<td>
							<c:if test="${item.zone_type==1}">Soft</c:if>
							<c:if test="${item.zone_type==2}">Hard</c:if>
							<c:if test="${item.zone_type==3}">None</c:if>
						</td>
						<td>
							<a title="${item.fabric_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.fabric_name}</a>
						</td>
						<td>
							${item.fabric_wwn}
						</td>
						<td>
							<cs:isActive value="${item.active}" />
						</td>
						<td>
							${empty item.description ? "N/A" : item.description}
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="6">
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="zoneListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty zonePage.data}">
	<script>
		var param = $("#hiddenForm").serialize();
		$("#zoneListpageNub").getLinkStr({pagecount:"${zonePage.totalPages}",curpage:"${zonePage.currentPage}",numPerPage:"${zonePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/zone/ZoneAction?func=AjaxZonePage&"+param+"&zsetId=${zsetId}",divId:'zoneContent'});
	</script>
</c:if>
<c:if test="${empty zonePage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click", 
			function(){ bAlert("暂无可导出数据！"); }
		);
	</script>
</c:if>
<form id="hiddenForm">
	<input type="hidden" name="name" value="${name}">
	<input type="hidden" name="wwn" value="${wwn}">
	<input type="hidden" name="active" value="${active}">
	<input type="hidden" name="zoneType" value="${zoneType}">
	<input type="hidden" name="zsetId" value="${zsetId}">
</form>