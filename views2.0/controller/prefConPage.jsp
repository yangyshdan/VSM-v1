<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/ftime" prefix="formateTime" %>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	$("#loadcontent").addClass("active");
	$("#dataContent").removeClass("active");
	$("a[href='#dataContent']").parent("li").removeClass("active");
	$("a[href='#loadcontent']").parent("li").addClass("active");
	var json = ${conPrfData};
	Public.drawPrfLine("prfContent",json);
});
</script>
<!-- 性能曲线切换页开始 -->
<div class="tab-pane active" id="loadcontent">
	<div id="prfContent" style="width: 100%;height: 230px;"></div>
</div>
<!-- 性能曲线切换页结束 -->
<!-- 性能数据切换页开始 -->
<div class="tab-pane" id="dataContent">
	<table class="table table-bordered table-striped table-condensed" id="conTable">
		<thead>
			<c:choose>
				<c:when test="${not empty conPrfData}">
					<tr>
						<c:forEach var="head" items="${conPrfData.thead}">
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
				<c:when test="${not empty conPrfData}">
					<c:forEach var="item" items="${conPrfData.tbody.data}" varStatus="status">
						<tr>
							<c:forEach var="thead" items="${conPrfData.thead}">
								<td>
									<c:choose>
										<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">
											${item.ele_name}
										</c:when>
										<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
											<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd hh:mm:ss" />
										</c:when>
										<c:otherwise>
											<c:if test="${conPrfData.threshold==1}">
												<span style="${item[fn:toLowerCase(thead.key)] >=conPrfData.threvalue?'color:red':''}"><fmt:formatNumber value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/></span>
											</c:if>
											<c:if test="${conPrfData.threshold==0}">
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
	<div id="conpageNub" class="pagination pagination-centered"></div>
	<c:if test="${not empty conPrfData}">
		<script>
		$("#conpageNub").getLinkStr({pagecount:"${conPrfData.tbody.totalPages}",curpage:"${conPrfData.tbody.currentPage}",numPerPage:"${conPrfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/controller/ConAction?func=ConPrfPage&tablePage=1&subSystemID=${subSystemID}",divId:'dataContent'});
		</script>
	</c:if>
	<c:if test="${empty conPrfData}">
		<script>
			$("#exportCSV").unbind();
			$("#exportCSV").attr("href","javascript:void(0);");
			$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
		</script>
	</c:if>
</div>