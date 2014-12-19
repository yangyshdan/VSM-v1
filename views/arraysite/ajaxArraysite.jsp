<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
	var param = $("#hiddenForm").serialize();
	var exurl = "${path}/servlet/arraysite/ArraysiteAction?func=exportArraysiteConfigData&subSystemID=${subSystemID}&"+param;
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				Rank
			</th>
			<th>
				存储池
			</th>
			<th>
				冗余级别
			</th>
			<th>
				描述
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty arraysitePage.data}">
				<c:forEach var="item" items="${arraysitePage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path}/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=${subSystemID}&arraysiteId=${item.disk_group_id}">${item.the_display_name}</a>
						</td>
						<td>
							<a title="${item.rank_name.trim()}" href="${path}/servlet/rank/RankAction?func=RankInfo&rankId=${item.storage_extent_id}&subSystemID=${subSystemID}">${item.rank_name}</a>
						</td>									
						<td>
							<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
						</td>									
						<td>
							${item.raid_level}
						</td>									
						<td>
							${item.description}
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
<div id="arraysiteListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty arraysitePage.data}">
	<script>
		var param = $("#conditionForm").serialize();
		$("#arraysiteListpageNub").getLinkStr({pagecount:"${arraysitePage.totalPages}",curpage:"${arraysitePage.currentPage}",numPerPage:"${arraysitePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/arraysite/ArraysiteAction?func=AjaxArraysitePage&subSystemID=${subSystemID}&"+param,divId:'arraysiteContent'});
	</script>
</c:if>
<c:if test="${empty arraysitePage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<form id="hiddenForm">
<input type="hidden" id="name" name="name" value="${name}"/>
<input type="hidden" id="raidLevel" name="raidLevel" value="${raidLevel}"/>
</form>