<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				创建时间
			</th>
			<th>
				操作
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty dbPage.data}">
				<c:forEach var="item" items="${dbPage.data}" varStatus="status">
					<tr style="cursor:pointer">
						<td>
							<a href="javascript:DbClick('${item.id}')">${item.the_display_name}</a>
						</td>
						<td>
							${item.create_time}
						</td>									
						<td align="center" >
							<c:choose>
								<c:when test="${item.report_type == 0}">
									<a class="btn btn-success" data-rel='tooltip' href="javascript:DbClick('${item.id}')" title="view"><i class="icon-zoom-in icon-white"></i>编辑</a>
									<a class="btn btn-warning" data-rel='tooltip' href="javascript:void(0)" title="create" onclick="createReport(${item.id})"><i class="icon-print icon-white"></i>生成报表</a>
									<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del('${item.id}','${item.the_display_name }')"><i class="icon-trash icon-white"></i>删除</a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-success" data-rel='tooltip' href="javascript:editTaskReport('${item.id}')" title="view"><i class="icon-zoom-in icon-white"></i>编辑</a>
									<a class="btn btn-warning" data-rel='tooltip' href="javascript:void(0)" title="create" onclick="createReport(${item.id})"><i class="icon-print icon-white"></i>生成报表</a>
								</c:otherwise>
							</c:choose>
						</td>										
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=3>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="reportListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty dbPage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#reportListpageNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/report/ReportTemplate?func=AjaxPage&"+param,divId:'myTabContent'});
	</script>
</c:if>
<form>
<input type="hidden" name="name" value="${name }"/>
</form>