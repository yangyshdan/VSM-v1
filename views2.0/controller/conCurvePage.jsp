<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="/tags/ftime" prefix="formateTime" %>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script type="text/javascript">
$(function(){
	$("#loadcontent").addClass("active");
	$("#dataContent").removeClass("active");
	$("a[href='#dataContent']").parent("li").removeClass("active");
	$("a[href='#loadcontent']").parent("li").addClass("active");
	
	var jsonVal2 = ${conPrfData};
	Public.drawPrfLine("prfContent2816", jsonVal2);
	
	
});
</script>
<!-- tab切换标签开始 -->
<ul class="nav nav-tabs" id="myTab">
	<li class="active">
		<a href="#loadcontent">性能曲线</a>
	</li>
	<li class="">
		<a href="#dataContent">性能数据</a>
	</li>
</ul>

<!-- tab切换标签结束 -->
<div id="perfChart" class="tab-content" style="min-height: 100px;">
	<!-- 性能曲线切换页开始 -->
	<div class="tab-pane active" id="loadcontent">
		<div id="prfContent2816" style="width: 100%;margin:0px;min-height: 100px;height:460px;"></div>
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
										<th>${head.value}</th>
									</c:when>
									<c:when test="${head.key=='prf_timestamp'}">
										<th>${head.value}</th>
									</c:when>
									<c:otherwise>
										<th>${head.value}</th>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td>暂无数据！</td>
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
													<span style="${item[fn:toLowerCase(thead.key)] >=conPrfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
												</c:if>
												<c:if test="${conPrfData.threshold==0}">
													${item[fn:toLowerCase(thead.key)]}
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
			$("#conpageNub").getLinkStr({
				pagecount:"${conPrfData.tbody.totalPages}",
				curpage:"${conPrfData.tbody.currentPage}",
				numPerPage:"${conPrfData.tbody.numPerPage}",
				isShowJump:true,
				ajaxRequestPath:"${path}/servlet/controller/ConAction?func=ConPrfPage&tablePage=1&subSystemID=${subSystemID}",
				divId:'dataContent'
			});
			$("#exportPrfCSV").unbind();
			var exurl = "${path}/servlet/controller/ConAction?func=exportPrefData&subSystemID=${subSystemID}";
						$("#exportPrfCSV").attr("href",exurl);
			</script>
		</c:if>
		<c:if test="${empty conPrfData}">
			<script>
				$("#exportPrfCSV").unbind();
				$("#exportPrfCSV").attr("href","javascript:void(0);");
				$("#exportPrfCSV").bind("click",function(){bAlert("暂无可导出数据！")});
			</script>
		</c:if>
	</div>
	<!-- 性能数据切换页结束 -->
	</div>