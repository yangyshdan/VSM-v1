<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr align="center">
			<th>分配组名称</th><th width="100px">IP地址</th>
			<th width="80px">SNMP版本</th><th width="100px">SNMP社区</th>
			<th width="80px">用户名</th><th width="110px">设备</th>
			<th width="60px">重试次数</th><th width="50px">状态</th>
			<th width="130px">描述</th>
			<th width="80px">操作</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty snmpPage.data}">
				<c:forEach var="item" items="${snmpPage.data}" varStatus="status">
					<tr>
						<td>
							<%--<a href="javascript:void(0);" onclick="SnmpUtil.showGroupDetailDlg(${item.group_id})" style="cursor:pointer;"></a>--%>
							${item.group_name}
						</td>
						<td>${item.ip_address_v4}</td>
						<td>${item.snmp_version}</td>
						<td>${item.snmp_community}</td>
						<td>${item.snmp_v3_user_name}</td>
						<td>${item.device_type},${item.device_model}</td>
						<td>${item.snmp_retry}</td>
						<td>
							<c:choose>
								<c:when test="${item.enabled}">启动</c:when>
								<c:otherwise>禁用</c:otherwise>
							</c:choose>
						</td>
						<td>${item.description}</td>
						<td>
							<%--<a onclick="SnmpUtil.querySnmpDetailDlg('${item.snmp_id}')" class="btn btn-success" href="javascript:void(0)" title="查看SNMP的详细配置信息"><i class="icon-check icon-white"></i>详细</a>--%>
							<a onclick="SnmpUtil.editSnmpDetailDlg('${item.snmp_id}')" class="btn btn-info" href="javascript:void(0)" title="编辑SNMP的配置信息"><i class="icon-edit icon-white"></i>编辑</a>
							<a onclick="SnmpUtil.deleteSnmpDetail('${item.snmp_id}')" class="btn btn-danger" href="javascript:void(0)" title="删除该SNMP的配置信息"><i class="icon-trash icon-white"></i>删除</a>
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr><td colspan="9">暂无数据！</td></tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>

<div class="pagination pagination-centered">
	<ul id="snmpListNub"></ul>
</div>
<c:if test="${not empty snmpPage.data}">
	<script>
		$("#snmpListNub").getLinkStr({
			pagecount:"${snmpPage.totalPages}",
			curpage:"${snmpPage.currentPage}",
			numPerPage:"${snmpPage.numPerPage}",
			isShowJump:true,
			ajaxRequestPath:"${snmpAjaxPath}",
			divId: "snmpContent"
		});
	</script>
</c:if>