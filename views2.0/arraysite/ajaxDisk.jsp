<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
$(function(){
	$("#diskTalbe td").addClass("rc-td");
	$("#diskTalbe").resizableColumns();
});
</script>
<table class="table table-bordered table-striped table-condensed" id="diskTable">
<thead>
	<tr>
		<th>
			名称
		</th>
		<th>
			容量(G)
		</th>
		<th>
			转速
		</th>
		<th>
			运行状态
		</th>
		<th>
			阵列
		</th>
		<th>
			厂商
		</th>
		<th>
			型号
		</th>
		<th>
			序列号
		</th>
		<th>
			固件版本
		</th>
		<th>
			硬件状态
		</th>
	</tr>
</thead>
<tbody>
	<c:choose>
		<c:when test="${not empty diskPage.data}">
			<c:forEach var="item" items="${diskPage.data}" varStatus="status">
				<tr>
					<td>
						${item.the_display_name}
					</td>
					<td>
						<fmt:formatNumber value="${item.the_capacity}" pattern="0.00" />
					</td>
					<td>
						${item.speed}
					</td>
					<td>
						${item.the_operational_status}
					</td>
					<td>
						${item.diskgroup_name}
					</td>									
					<td>
						${item.vendor_name}
					</td>									
					<td>
						${item.model_name}
					</td>									
					<td>
						${item.serial_number}
					</td>									
					<td>
						${item.firmware_rev}
					</td>									
					<td>
						<cs:cstatus value="${item.the_consolidated_status}" />
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
<div id="diskListpageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty diskPage.data}">
	<script>
		var param = $("#diskHiddenForm").serialize();
		$("#diskListpageNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/arraysite/ArraysiteAction?func=AjaxDiskPage&subSystemID=${subSystemID}&arraysiteId=${arraysiteId}",divId:'diskContent'});
	</script>
</c:if>
<c:if test="${empty diskPage.data}">
	<script>
		$("#exportDiskCSV").unbind();
		$("#exportDiskCSV").attr("href","javascript:void(0);");
		$("#exportDiskCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>