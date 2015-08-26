<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/apps.js"></script>
<script src="${path}/resource/js/project/util.js"></script>

<style>
.spetable td { text-overflow:ellipsis;overflow:hidden;white-space: nowrap; }
</style>

<div id="content">
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>SNMP配置列表</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="新增" onclick="SnmpUtil.addSnmpDetailDlg();"><i class="icon icon-color icon-add"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="刷新" onclick="SnmpUtil.refreshPageWithFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div id="snmpContent" class="box-content"  style="overflow:auto;width:98%;min-height:180px;height:600px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>分配组名称</th><th>IP地址</th>
							<th>SNMP版本</th><th>SNMP端口</th>
							<th>SNMP社区</th><th>超时时间</th>
							<th>重试次数</th><th>启用状态</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr>
										<td><a href="SnmpUtil.showGroupDetailDlg(${item.group_id)" style="cursor:pointer;">${item.group_name}</a></td>
										<td>${item.ip_address_v4}</td>
										<td>${item.snmp_version}</td>
										<td>${item.snmp_port}</td>
										<td>${item.snmp_community}</td>
										<td>${item.snmp_timeout}</td>
										<td>${item.snmp_retry}</td>
										<td>${item.enabled}</td>
										<td>
											<a class="btn btn-info" href="javascript:void(0)" title="edit" onclick="SnmpUtil.editSnmpDetailDlg('${item.snmp_id}')"><i class="icon-edit icon-white"></i>编辑</a>
											<a class="btn btn-danger" href="javascript:void(0)" title="delete" onclick="SnmpUtil.deleteSnmpDetail('${item.snmp_id}')"><i class="icon-trash icon-white"></i>删除</a>
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
				<c:if test="${not empty dbPage.data}">
					<script>
						$("#snmpListNub").getLinkStr({
							pagecount:"${dbPage.totalPages}",
							curpage:"${dbPage.currentPage}",
							numPerPage:"${dbPage.numPerPage}",
							isShowJump:true,
							ajaxRequestPath:"${path}/servlet/deviceSnmp/DeviceSnmpAction?func=AjaxPage",
							divId: "snmpContent"
						});
					</script>
				</c:if>
					
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>