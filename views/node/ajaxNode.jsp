<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
	var param = $("#nodeHiddenForm").serialize();
	var exurl = "${path}/servlet/node/NodeAction?func=exportNodeConfigData&subSystemID=${subSystemID}&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" style="word-break:break-all" id="conTable">
<thead>
	<tr>
		<th>
			名称
		</th>
		<th>
			组件ID
		</th>
		<th>
			IP地址
		</th>
		<th>
			IO Group
		</th>
		<th>
			后端名称
		</th>
		<th>
			操作状态
		</th>
		<th>
			WWN
		</th>
	</tr>
</thead>
<tbody>
	<c:choose>
		<c:when test="${not empty nodePage.data}">
			<c:forEach var="item" items="${nodePage.data}" varStatus="status">
				<tr>
					<td>
						<a title="${item.the_display_name}" href="${path}/servlet/node/NodeAction?func=NodeInfo&nodeId=${item.redundancy_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
					</td>
					<td>
						${item.component_id}
					</td>
					<td>
						${item.ip_address}
					</td>
					<td>
						<a title="${item.iogroup_name}" href="${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId=${item.io_group_id}">${item.iogroup_name}</a>
					</td>
					<td>
						${item.the_backend_name}
					</td>
					<td>
						${item.the_operational_status}
					</td>
					<td>
						${item.wwn}
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
<div id="nodeListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty nodePage.data}">
<script>
	var param = $("#conditionForm").serialize();
	$("#nodeListpageNub").getLinkStr({pagecount:"${nodePage.totalPages}",curpage:"${nodePage.currentPage}",numPerPage:"${nodePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/node/NodeAction?func=AjaxNodePage&subSystemID=${subSystemID}&"+param,divId:'nodeContent'});
</script>
</c:if>
<c:if test="${empty nodePage.data}">
<script>
	$("#exportCSV").unbind();
	$("#exportCSV").attr("href","javascript:void(0);");
	$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
</script>
</c:if>
<form id="nodeHiddenForm">
<input type="hidden" id="hiddenPortName" name="name" value="${name}"/>
<input type="hidden" id="hiddenPortType" name="ipAddress" value="${ipAddress}"/>
<input type="hidden" id="hiddenStartPort" name="componentId" value="${componentId}"/>
</form>