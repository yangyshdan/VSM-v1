<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				磁盘容量(G)
			</th>
			<th>
				磁盘速度
			</th>
			<th>
				位置
			</th>
			<th>
				类型
			</th>
			<th>
				更新时间
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty subDiskPage.data}">
				<c:forEach var="item" items="${subDiskPage.data}" varStatus="status">
					<tr>
						<td>
							${item.name}
						</td>
						<td>
							<fmt:formatNumber value="${item.ddm_cap/1024}" pattern="0.00"/>
						</td>
						<td>
							${item.ddm_speed}
						</td>
						<td>
							${item.display_name}
						</td>
						<td>
							${item.ddm_type}
						</td>
						<td>
							${item.update_timestamp}
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
<div id="diskpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty subDiskPage.data}">
<script type="text/javascript">
	$("#diskpageNub").getLinkStr({pagecount:"${subDiskPage.totalPages}",curpage:"${subDiskPage.currentPage}",numPerPage:"${subDiskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/disk/DiskAction?func=AjaxStoragePage&diskgroupId=${diskgroupId}&subSystemID=${subSystemID}",divId:'subdiskContent'});
</script>
</c:if>
<c:if test="${empty subDiskPage.data}">
<script>
	$("#exportdiskCSV").unbind();
	$("#exportdiskCSV").attr("href","javascript:void(0);");
	$("#exportdiskCSV").bind("click",function(){bAlert("暂无可导出数据！")});
</script>
</c:if>