<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/ftime" prefix="formateTime" %>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
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
			<c:otherwise>
				<tr>
					<td>
						暂无数据！
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty prfData}">
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
											<span style="${item[fn:toLowerCase(thead.key)] >=prfData.threvalue?'color:red':''}"><fmt:formatNumber value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/></span>
										</c:if>
										<c:if test="${prfData.threshold==0}">
											<fmt:formatNumber value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/>
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
<div id="switchportInfopageNub" class="pagination pagination-centered"></div>
<c:choose>
	<c:when test="${not empty prfData.tbody.data and prfData.tbody.totalPages > 0}">
		<script type="text/javascript">
			$("#switchportInfopageNub").getLinkStr({
				pagecount:"${prfData.tbody.totalPages}",
			 	curpage:"${prfData.tbody.currentPage}", 
			 	numPerPage:"${prfData.tbody.numPerPage}",
			 	isShowJump:true, 
			 	ajaxRequestPath:"${path}/servlet/switchport/SwitchportAction?func=PortPrfPage&portId=${portInfo.port_id}&level=3&tablePage=1&switchId=${switchId}",
			 	divId: "dataContent"});
			 var $csv = $("#exportCSV");
			$csv.unbind();
			var exurl = "${path}/servlet/switchport/SwitchportAction?func=exportPrefData&portId=${portInfo.port_id}&level=3&switchId=${switchId}";
					$csv.attr("href", exurl);
		</script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript">
			var $csv = $("#exportCSV");
			$csv.unbind();
			$csv.attr("href","javascript:void(0);");
			$csv.bind("click",function(){bAlert("暂无可导出数据！")});
		</script>
	</c:otherwise>
</c:choose>
<!-- 性能数据切换页结束 -->