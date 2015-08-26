<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
	var exurl = "${path}/servlet/pool/PoolAction?func=exportPoolConfigData&name=${name}&greatCapacity=${greatCapacity}&lessCapacity=${lessCapacity}&poolId=${pool_id}&subSystemID=${subSystemID}";
	$("#exportCSV").attr("href",exurl); 
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				名称
			</th>
			<th>
				容量(G)
			</th>
			<th>
				已用容量(G)
			</th>
			<th>
				可用容量(G)
			</th>
			<th>
				已分配容量(G)
			</th>
			<th>
				未分配容量(G)
			</th>
			<th>
				本地状态
			</th>
			<th>
				操作状态
			</th>
			<th>
				硬件状态
			</th>
			<th>
				冗余级别
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty poolPage.data}">
				<c:forEach var="item" items="${poolPage.data}" varStatus="status">
					<tr>
						<td>
							<a title="${item.the_display_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
						</td>
						<td>
							<fmt:formatNumber value="${item.the_space}" pattern="0.00" />
						</td>									
						<td>	
							<fmt:formatNumber value="${item.the_consumed_space}" pattern="0.00" />
						</td>
						<td>
							<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
						</td>
						<td>
							<fmt:formatNumber value="${item.the_assigned_space}" pattern="0.00" />
						</td>
						<td>
							<fmt:formatNumber value="${item.the_unassigned_space}" pattern="0.00" />
						</td>
						<td>
							${item.the_native_status}
						</td>
						<td>
							${item.the_operational_status}
						</td>
						<td>
							<cs:cstatus value="${item.the_consolidated_status}" />
						</td>
						<td>
							${item.raid_level}
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=11>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="poolListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty poolPage.data}">
	<script>
		$("#poolListpageNub").getLinkStr({pagecount:"${poolPage.totalPages}",curpage:"${poolPage.currentPage}",numPerPage:"${poolPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/pool/PoolAction?func=AjaxPoolPage&subSystemID=${subSystemID}",divId:'poolContent'});
	</script>
</c:if>
<c:if test="${empty poolPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id="hiddenPortName" value="${name}"/>
<input type="hidden" id="hiddenGreatCapecity" value ="${greatCapacity}"/>
<input type="hidden" id="hiddenLessCapecity" value="${lessCapacity}"/>