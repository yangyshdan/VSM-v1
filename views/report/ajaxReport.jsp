<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				报表类型
			</th>
			<th>
				时间段
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
			<c:when test="${not empty reportPage.data}">
				<c:forEach var="item" items="${reportPage.data}" varStatus="status">
					<tr ondblclick="DbClick('${item.real_name}')" style="cursor:pointer">
						<td>
							<a href="javascript:DbClick('${item.real_name}')">${item.the_display_name}</a>
						</td>
						<td>
							${item.report_type==0?'即时报表':'任务报表'}
						</td>
						<td>
							${item.starttime}&nbsp;&nbsp;至&nbsp;&nbsp;${item.endtime}
						</td>									
						<td>
							${item.create_time}
						</td>									
						<td align="center" >
							<a class="btn btn-success" data-rel='tooltip' href="javascript:DbClick('${item.real_name}')" title="view"><i class="icon-zoom-in icon-white"></i>查看</a>
							<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del('${item.id}','${item.the_display_name }')"><i class="icon-trash icon-white"></i>删除</a>
							<a class="btn btn-warning" data-rel='tooltip' href="javascript:void(0)" title="download" onclick="downloadReport(${item.id})"><i class="icon-download-alt icon-white"></i>下载</a>
						</td>									
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=5>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="reportListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty reportPage.data}">
	<script>
		var param = $("#hiddenForm").serialize();
		$("#reportListpageNub").getLinkStr({pagecount:"${reportPage.totalPages}",curpage:"${reportPage.currentPage}",numPerPage:"${reportPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/report/ReportAction?func=AjaxReportPage&"+param,divId:'myTabContent'});
	</script>
</c:if>
<form>
<input type="hidden" name="name" value="${name }"/>
<input type="hidden" name="reportType" value="${reportType }"/>
<input type="hidden" name="startTime" value="${startTime }"/>
<input type="hidden" name="endTime" value="${endTime }"/>
</form>