<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
	var exurl = "${path}/servlet/sr/storageport/StoragePortAction?func=exportPortConfigData&portName=${portName}&networkAddress=${networkAddress}&subSystemID=${subSystemID}";
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				端口名
			</th>
			<th>
				网络地址
			</th>
			<th>
				端口速率
			</th>
			<th>
				端口类型
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty portPage.data}">
				<c:forEach var="item" items="${portPage.data}" varStatus="status">
					<tr>
						<td>
							<a href="${path}/servlet/sr/storageport/StoragePortAction?func=LoadPortInfo&subsystemId=${subSystemID}&portId=${item.port_id}&storageType=${storageType}">${item.name}</a>
						</td>
						<td>
							${item.network_address}
						</td>
						<td>
							${item.port_speed}
						</td>
						<td>
							${item.type}
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
<div id="portPagepageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty portPage.data}">
	<script>
		$("#portPagepageNub").getLinkStr({pagecount:"${portPage.totalPages}",curpage:"${portPage.currentPage}",numPerPage:"${portPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storageport/StoragePortAction?func=AjaxStoragePage&portName=${portName}&networkAddress=${networkAddress}&subSystemID=${subSystemID}&storageType=${storageType}",divId:'portContent'});
	</script>
</c:if>
<c:if test="${empty portPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id = "hiddenPortName" value="${portName}"/>
<input type="hidden" id = "hiddenAddress" value="${networkAddress}"/>