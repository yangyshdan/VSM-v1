<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
	<thead>
		<tr>
			<th>
				IP地址
			</th>
			<th>
				用户名
			</th>
			<th>
				状态
			</th>
			<th>
				操作
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty hmcPage.data}">
				<c:forEach var="item" items="${hmcPage.data}" varStatus="status">
					<tr>
						<td>
							${item.ip_address}
						</td>
						<td>
							${item.user}
						</td>
						<td>
							<c:if test="${item.state==1}">
								<i class="icon icon-color icon-check"></i>可用
							</c:if>
							<c:if test="${item.state==0}">
								<i class="icon icon-color icon-close"></i>不可用
							</c:if>
						</td>
						<td>
							<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=editHMCInfo&id=${item.id}',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
							<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del(${item.id})"><i class="icon-trash icon-white"></i>删除</a>
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
<div id="hmcpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty hmcPage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#hmcpageNub").getLinkStr({pagecount:"${hmcPage.totalPages}",curpage:"${hmcPage.currentPage}",numPerPage:"${hmcPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=HMCPage&"+param,divId:'loadcontent'});
	</script>
</c:if>