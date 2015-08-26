<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	$("#conTable").tablesorter();
	changeColumn.initCol();
});

var param = $("#switchHiddenForm").serialize();
var exurl = "${path}/servlet/switchs/SwitchAction?func=exportSwitchConfigData&"+param;
$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				厂商
			</th>
			<th>
				型号
			</th>
			<th>
				状态
			</th>
			<th>
				Zone ID
			</th>
			<th>
				IP地址
			</th>
			<th>
				Fabric网络
			</th>
			<th>
				WWN
			</th>
			<th>
				序列号
			</th>
			<%--<th>
				引擎工作状态
			</th>
			<th>
				电源工作状态
			</th>
			<th>
				端口工作状态
			</th>
			<th>
				光纤模块状态
			</th>
			<th>
				描述
			</th>
			--%><th>
				更新时间
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty switchPage.data}">
				<c:forEach var="item" items="${switchPage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=${item.switch_id}">${item.the_display_name}</a>
						</td>
						<td>
							${item.vendor_name}
						</td>
						<td>
							${item.model_name}
						</td>
						<td>
							<cs:cstatus value="${item.the_propagated_status}" />
						</td>
						<td>
							${item.domain}
						</td>
						<td>
							<a title="${item.ip_address}" href="http://${item.ip_address}" target="_blank">${item.ip_address}</a>
						</td>
						<td>
							<a title="${item.fabric_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.fabric_name}</a>
						</td>
						<td>
							${item.switch_wwn}
						</td>
						<td>
							${item.serial_number}
						</td><%--
						<td>
							${item.engine_status}
						</td>
						<td>
							${item.power_status}
						</td>
						<td>
							${item.port_status}
						</td>
						<td>
							${item.fiber_status}
						</td>
						<td>
							${item.description}
						</td>
						--%><td>
							${item.update_timestamp}
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
<div id="switchListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty switchPage.data}">
	<script>
		var param = $("#switchHiddenForm").serialize();
		$("#switchListpageNub").getLinkStr({pagecount:"${switchPage.totalPages}",curpage:"${switchPage.currentPage}",numPerPage:"${switchPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/switchs/SwitchAction?func=AjaxSwitchPage&"+param,divId:'loadcontent'});
	</script>
</c:if>
<c:if test="${empty switchPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="switchHiddenForm"> 
<input type="hidden" name="name" value="${name}" />
<input type="hidden" name="ipAddress" value="${ipAddress}" />
<input type="hidden" name="status" value="${status}" />
<input type="hidden" name="serialNumber" value="${serialNumber}" />
<input type="hidden" name="fabricId" value="${fabricId}" />
</form>