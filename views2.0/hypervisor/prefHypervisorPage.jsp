<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/ftime" prefix="formateTime" %>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	$("#loadcontent2").addClass("active");
	$("#dataContent2").removeClass("active");
	$("a[href='#dataContent2']").parent("li").removeClass("active");
	$("a[href='#loadcontent2']").parent("li").addClass("active");
	
	var json = ${prfData};
	if(json.graphType == 1){
		if(json.series){ Public.drawTopn02("prfContent2", {jsonVal: json.series}); }
	}
	else {
		Public.drawPrfLine("prfContent2",json);
		$("#pTitle").html(function(){
			var str="物理机性能  (";
			$.each(json.kpiInfo,function(i){
				str+=json.kpiInfo[i].ftitle;
				if(i<json.kpiInfo.length-1){
					str+=",";
				}
			});
			if(str.length>100){
				str = str.substring(0,100)+'...';
			}
			str+=")";
			return str;
		});
	}
});
</script>
<!-- 性能曲线切换页开始 -->
<div class="tab-pane active" id="loadcontent2">
	<div id="prfContent2" style="width: 97%; height: 350px;"></div>
</div>
<!-- 性能曲线切换页结束 -->
<!-- 性能数据切换页开始 -->
<div class="tab-pane" id="dataContent2" style="padding-top:10px;">
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
	<div id="HypervisorInfopageNub" class="pagination pagination-centered"></div>
	<c:if test="${not empty prfData.tbody.data}">
		<script>
			$("#HypervisorInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPrfPage&hypervisorId=${hypervisorId}&level=3&tablePage=1",divId:'dataContent2'});							
			$("#exportCSV").unbind();
			var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportPrefData&hypervisorId=${hypervisorId}&computerId=${computerId}&level=3&type=Hypervisor";
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
</div>
