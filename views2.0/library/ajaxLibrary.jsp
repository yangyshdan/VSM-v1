<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
	var param = $("#libraryHiddenForm").serialize();
	var exurl = "${path}/servlet/library/LibraryAction?func=ExportLibraryConfigData&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" style="word-break:break-all" id="conTable">
<thead>
	<tr>
		<th>
			名称
		</th>
		<th>
			工作状态
		</th>
		<th>
			电源状态
		</th>
		<th>
			磁带状态
		</th>
		<th>
			描述
		</th>
		<th>
			更新时间
		</th>
	</tr>
</thead>
<tbody>
	<c:choose>
		<c:when test="${not empty libraryPage.data}">
			<c:forEach var="item" items="${libraryPage.data}" varStatus="status">
				<tr>
					<td>
						<a title="${item.the_display_name}" href="${path}/servlet/library/LibraryAction?func=LibraryInfo&libraryId=${item.tape_library_id}">${item.the_display_name}</a>
					</td>
					<td>
						${item.the_operational_status}
					</td>
					<td>
						${item.power_status}
					</td>
					<td>
						${item.tape_status}
					</td>
					<td>
						${item.description}
					</td>
					<td>
						${item.update_timestamp}
					</td>
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
<div id="libraryListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty libraryPage.data}">
<script>
	var param = $("#conditionForm").serialize();
	$("#libraryListpageNub").getLinkStr({pagecount:"${libraryPage.totalPages}",curpage:"${libraryPage.currentPage}",numPerPage:"${libraryPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/library/LibraryAction?func=AjaxLibraryPage&"+param,divId:'librarycontent'});
</script>
</c:if>
<c:if test="${empty libraryPage.data}">
<script>
	$("#exportCSV").unbind();
	$("#exportCSV").attr("href","javascript:void(0);");
	$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
</script>
</c:if>
<form id="libraryHiddenForm">
<input type="hidden" id="hiddendisplayName" name="displayName" value="${displayName}"/>
</form>