<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table id="conTable" class="table table-bordered table-striped table-condensed" style="word-break:break-all">
	<thead>
		<tr>
			<th>
				名称/型号
			</th>
			<th>
				设备类型
			</th>
			<th>
				IP地址
			</th>
			<th>
				用户名
			</th>
			<th>
				操作
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty storageCfgPage.data}">
				<c:forEach var="item" items="${storageCfgPage.data}" varStatus="status">
					<tr>
						<td>
							${item.name}
						</td>
						<td>
							${item.storage_type}
						</td>
						<td>
							${item.ctl01_ip}
							<c:if test="${not empty item.ctl02_ip}">
								,${item.ctl02_ip}
							</c:if>
						</td>
						<td>
							${item.user}
						</td>
						<td>
							<a class="btn btn-info" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=EditStorageConfig&id=${item.id}',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
							<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},7)"><i class="icon-trash icon-white"></i>删除</a>
						</td>
					</tr>											
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="5">
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
</tbody>
</table>
<div id="storageCfgPageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty storageCfgPage.data}">
	<script>
		var param = $("#storageForm").serialize();
		$("#storageCfgPageNub").getLinkStr({pagecount:"${storageCfgPage.totalPages}",curpage:"${storageCfgPage.currentPage}",numPerPage:"${storageCfgPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=StorageCfgPage&"+param,divId:'storageConfigContent'});
	</script>
</c:if>