<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
	var diskgroupName = $("#hiddenDiskName").val();
	var raidLevel = $("#hiddenLevel").val();
	var exurl = "${path}/servlet/sr/diskgroup/DiskgroupAction?func=exportConfigData&diskgroupName=${diskgroupName}&raidLevel=${raidLevel}&subSystemID=${subSystemID}";
	$("#exportCSV").attr("href",exurl);
</script>
<table class="table table-bordered table-striped table-condensed">
	<thead>
		<tr>
			<th>
				RAID组名称
			</th>
			<th>
				RAID种类
			</th>
			<th>
				磁盘端口速度
			</th>
			<th>
				磁盘容量(G)
			</th>
			<th>
				磁盘数
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty diskPage.data}">
				<c:forEach var="item" items="${diskPage.data}" varStatus="status">
					<tr>
						<td>
							<a href="${path}/servlet/sr/diskgroup/DiskgroupAction?func=LoadDiskgroupInfo&subsystemId=${subSystemID}&diskgroupId=${item.diskgroup_id}&storageType=${storageType}">RAID Group ${item.name}</a>
						</td>
						<td>
							${item.raid_level}
						</td>
						<td>
							${item.ddm_speed}
						</td>
						<td>
							<fmt:formatNumber value="${item.ddm_cap/1024}" pattern="0.00"/>
						</td>
						<td>
							${item.width}
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
<div id="diskPagepageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty diskPage.data}">
	<script>
		$("#diskPagepageNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/diskgroup/DiskgroupAction?func=AjaxStoragePage&diskgroupName=${diskgroupName}&raidLevel=${raidLevel}&subSystemID=${subSystemID}&storageType=${storageType}",divId:'diskContent'});
	</script>
</c:if>
<c:if test="${empty diskPage.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>
<input type="hidden" id="hiddenDiskName" value="${diskgroupName}"/>
<input type="hidden" id="hiddenLevel" value="${raidLevel}"/>