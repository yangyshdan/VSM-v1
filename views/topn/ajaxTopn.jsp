<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				TOPN数量
			</th>
			<th>
				时间范围类型
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
					<tr ondblclick="topnDbClick(${item.tid})" style="cursor:pointer">
						<td>
							${item.name}
						</td>
						<td>
							${item.top_count}
						</td>
						<td>
							${item.timescope_type==0?"固定时间段":"最近时间段"}
						</td>									
						<td>
							${item.create_time}
						</td>									
						<td align="center" >
							<a class="btn btn-success" href="javascript:topnDbClick(${item.tid})" title="view"><i class="icon-zoom-in icon-white"></i>查看</a>
							<a class="btn btn-info" href="javascript:void(0)" title="edit" onclick="Topn.settingPrf(${item.tid})"><i class="icon-edit icon-white"></i>编辑</a>
							<a class="btn btn-danger" href="javascript:void(0)" title="delete" onclick="Topn.deletePrf('${item.tid}','${item.name}')"><i class="icon-trash icon-white"></i>删除</a>
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
<div id="topnListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty dbPage.data}">
	<script>
		var param = $("#topnHiddenForm").serialize();
		$("#topnListpageNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/topn/TopnAction?func=AjaxTopnPage&"+param,divId:'myTabContent'});
	</script>
</c:if>
<form id="topnHiddenForm">
<input type="hidden" name="name" value="${name}">
<input type="hidden" name="startTime" value="${startTime}">
<input type="hidden" name="endTime" value="${endTime}">
</form>