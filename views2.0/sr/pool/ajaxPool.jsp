<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
	var exurl = "${path}/servlet/sr/pool/PoolAction?func=exportPoolConfigData&name=${name}&greatCapacity=${greatCapacity}&lessCapacity=${lessCapacity}&poolId=${poolId}&subSystemID=${subSystemID}";
	$("#exportCSV").attr("href",exurl); 
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				存储池名
			</th>
			<th>
				RAID种类
			</th>
			<th>
				总逻辑容量(G)
			</th>
			<th>
				已用逻辑容量(G)
			</th>
			<th>
				LUN数量
			</th>
			<th>
				后端磁盘数量
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty dbPage.data}">
				<c:forEach var="item" items="${dbPage.data}" varStatus="status">
					<tr>
						<td>
							<a href="${path}/servlet/sr/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}&storageType=${storageType}">POOL ${item.name}</a>
						</td>
						<td>
							${item.raid_level}
						</td>
						<td>
							<fmt:formatNumber value="${item.total_usable_capacity/1024}" pattern="0.00" />
						</td>									
						<td>	
							<fmt:formatNumber value="${(item.total_usable_capacity-item.unallocated_capacity)/1024}" pattern="0.00" />
						</td>
						<td>
							${item.num_lun}
						</td>
						<td>
							${item.num_backend_disk}
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan=9>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>

<div id="poolajaxpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty dbPage.data}">
	<script>
		$("#poolajaxpageNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/pool/PoolAction?func=AjaxPoolPage&name=${name}&greatCapacity=${greatCapacity}&lessCapacity=${lessCapacity}&subSystemID=${subSystemID}&storageType=${storageType}",divId:'poolContent'});
	</script>
</c:if>
<c:if test="${empty dbPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id="hiddenPortName" value="${name}"/>
<input type="hidden" id="hiddenGreatCapecity" value ="${greatCapacity}"/>
<input type="hidden" id="hiddenLessCapecity" value="${lessCapacity}"/>