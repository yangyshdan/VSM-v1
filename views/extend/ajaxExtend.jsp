<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
	var param = $("#extendHiddenForm").serialize();
	var exurl = "${path}/servlet/extend/ExtendAction?func=exportExtendConfigData&subSystemID=${subSystemID}&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				扩展卷数
			</th>
			<th>
				扩展容量(G)
			</th>
			<th>
				总容量
			</th>
			<th>
				已用容量
			</th>
			<th>
				可用容量
			</th>
			<th>
				操作状态
			</th>
			<th>
				本地状态
			</th>
			<th>
				存储池
			</th>
			<th>
				设备ID
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty extendPage.data}">
				<c:forEach var="item" items="${extendPage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path}/servlet/extend/ExtendAction?func=extendInfo&subSystemID=${subSystemID}&extendId=${item.storage_extent_id}">${item.the_display_name}</a>
						</td>
						<td>
							${item.the_extent_volume}
						</td>									
						<td>
							<fmt:formatNumber value="${item.the_extend_space}" pattern="0.00" />
						</td>									
						<td>
							<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
						</td>
						<td>
							<cs:isProgress total="${item.the_total_space}" available="${item.the_total_space-item.the_available_space}"/>
						</td>									
						<td>
							<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
						</td>
															
						<td>
							${item.the_operational_status}
						</td>									
						<td>
							${item.the_native_status}
						</td>									
						<td>
							<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
						</td>									
						<td>
							${item.device_id }
						</td>
					</tr>
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
<div id="extendListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty extendPage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#extendListpageNub").getLinkStr({pagecount:"${extendPage.totalPages}",curpage:"${extendPage.currentPage}",numPerPage:"${extendPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/extend/ExtendAction?func=AjaxExtendPage&subSystemID=${subSystemID}&"+param,divId:'extendContent'});
	</script>
</c:if>
<c:if test="${empty extendPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="extendHiddenForm">
<input type="hidden" name="name" value="${name}" />
<input type="hidden" name="deviceId" value="${deviceId}" />
<input type="hidden" name="startCap" value="${startCap}" />
<input type="hidden" name="endCap" value="${endCap}" />
<input type="hidden" name="startAvailableCap" value="${startAvailableCap}" />
<input type="hidden" name="endAvailableCap" value="${endAvailableCap}" />
</form>