<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script>
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/fabric/FabricAction?func=exportFabricConfigData&"+param;
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
				WWN
			</th>
			<th>
				ZoneSet名称
			</th>
			<th>
				Zone数量
			</th>
			<th>
				SAN交换机数
			</th>
			<th>
				端口数
			</th>
			<th>
				已连接端口数
			</th>
			<!-- 
			<th>
				支持Zone
			</th>
			 -->
			<th>
				状态
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty fabricPage.data}">
				<c:forEach var="item" items="${fabricPage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.fabric_id}">${item.the_display_name}</a>
						</td>
						<td>
							${item.fabric_wwn}
						</td>
						<td>
							<a title="${item.zset_name}" href="${path}/servlet/zset/ZsetAction?func=ZsetInfo&zsetId=${item.zset_id}">${item.zset_name}</a>
						</td>
						<td>
							${item.the_zone_count}
						</td>
						<td>
							${item.the_switch_count}
						</td>
						<td>
							${item.the_port_count}
						</td>
						<td>
							${item.the_connected_port_count}
						</td>
						<!-- 
						<td>
							<cs:isActive value="${item.supports_zoning}" />
						</td>
						 -->
						<td>
							<cs:cstatus value="${item.the_propagated_status }" />
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="8">
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="fabricListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty fabricPage.data}">
	<script>
		var param = $("#hiddenForm").serialize();
		$("#fabricListpageNub").getLinkStr({pagecount:"${fabricPage.totalPages}",curpage:"${fabricPage.currentPage}",numPerPage:"${fabricPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/fabric/FabricAction?func=AjaxFabricPage&"+param,divId:'fabricContent'});
	</script>
</c:if>
<c:if test="${empty fabricPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="hiddenForm">
	<input type="hidden" name="name" value="${name}">
	<input type="hidden" name="status" value="${status}">
</form>