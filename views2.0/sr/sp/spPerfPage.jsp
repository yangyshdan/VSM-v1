<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/jstl-function" prefix="fn" %>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@ taglib uri="/tags/fmtNumber" prefix="fmtNumber" %>
<script src="${path}/resource/js/project/publicscript.js"></script>

<table class="table table-bordered table-striped table-condensed" id="conTable">
	<thead>
		<c:choose>
			<c:when test="${not empty prfData}">
				<tr>
					<c:forEach var="head" items="${prfData.thead}">
					<c:choose>
						<c:when test="${head.key=='ele_name'}">
							<th>
								${head.value}
							</th>
						</c:when>
						<c:when test="${head.key=='prf_timestamp'}">
							<th>
								${head.value}
							</th>
						</c:when>
						<c:otherwise>
							<th>
								${head.value}
							</th>
						</c:otherwise>
					</c:choose>
					</c:forEach>
				</tr>
			</c:when>
		</c:choose>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty prfData.tbody.data}">
				<c:forEach var="item" items="${prfData.tbody.data}" varStatus="status">
					<tr>
						<c:forEach var="thead" items="${prfData.thead}">
							<td>
								<c:choose>
									<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">
										${item.ele_name}
									</c:when>
									<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
										<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
									</c:when>
									<c:otherwise>
										<c:if test="${prfData.threshold==1}">
											<span style="${item[fn:toLowerCase(thead.key)] >= prfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
										</c:if>
										<c:if test="${prfData.threshold==0}">
											<fmtNumber:formate value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/>
										</c:if>
									</c:otherwise>
								</c:choose>
							</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<div id="perfInfoPageNub" class="pagination pagination-centered"></div>
<c:if test="${not empty prfData.tbody.data}">
	<script>
		$("#perfInfoPageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagenode/StoragenodeAction?func=LoadPerfInfo&subsystemId=${subsystemId}&spId=${spId}&storageType=${storageType}&level=3&tablePage=1",divId:'dataContent'});
		$("#exportCSV").unbind();
		var exurl = "${path}/servlet/sr/storagenode/StoragenodeAction?func=ExportPerfData&subsystemId=${subsystemId}&spId=${spId}&storageType=${storageType}&level=3";
				$("#exportCSV").attr("href",exurl);
	</script>
</c:if>
<c:if test="${empty prfData.tbody.data}">
	<script>
		$("#exportCSV").unbind();
		$("#exportCSV").attr("href","javascript:void(0);");
		$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
	</script>
</c:if>