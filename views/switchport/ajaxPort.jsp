<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
	var param = $("#portHiddenForm").serialize();
	var exurl = "${path}/servlet/switchport/SwitchportAction?func=exportPortConfigData&switchId=${switchId}&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
<thead>
	<tr>
		<th>
			名称
		</th>
		<th>
			端口号
		</th>
		<th>
			端口类型
		</th>
		<th>
			操作状态
		</th>
		<th>
			硬件状态
		</th>
		<th>
			端口速率(M)
		</th>
	</tr>
</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty portPage.data}">
				<c:forEach var="item" items="${portPage.data}" varStatus="status">
					<tr>
					<td>
						<a title="${item.the_display_name}" href="${path}/servlet/switchport/SwitchportAction?func=PortInfo&switchId=${switchId}&portId=${item.port_id}">${item.the_display_name}</a>
					</td>
					<td>
						${item.port_number}
					</td>
					<td>
						${item.the_type}
					</td>
					<td>
						${item.the_operational_status}
					</td>
					<td>
						<cs:cstatus value="${item.the_consolidated_status}" />
					</td>
					<td>
						${item.the_port_speed}
					</td>
				</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=6>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="portListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty portPage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#portListpageNub").getLinkStr({pagecount:"${portPage.totalPages}",curpage:"${portPage.currentPage}",numPerPage:"${portPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/switchport/SwitchportAction?func=AjaxPortPage&switchId=${switchId}&"+param,divId:'portContent'});
	</script>
</c:if>
<c:if test="${empty portPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="portHiddenForm">
<input type="hidden" id="hiddenPortName" name="portName" value="${portName}"/>
<input type="hidden" id="hiddenPortType" name="portType" value="${portType}"/>
<input type="hidden" id="hiddenStartPort" name="startPort" value="${startPort}"/>
<input type="hidden" id="hiddenEndPort" name="endPort" value="${endPort}"/>
<input type="hidden" id="hiddenStatus"  name="status" value="${status}"/>
</form>