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
	var json = ${prfData};
	Public.drawPrfLine("prfContent",json);
	$("#pTitle").html(function(){
		var str="性能  (";
		$.each(json.kpiInfo,function(i){
			str+=json.kpiInfo[i].ftitle;
			str+="("+json.kpiInfo[i].funits+")";
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
});
</script>
<!-- 性能曲线切换页开始 -->
<div class="tab-pane active" id="loadcontent">
	<div id="prfContent" style="width: 95%; height: 350px;"></div>
</div>
<!-- 性能曲线切换页结束 -->
<!-- 性能数据切换页开始 -->
<div class="tab-pane" id="dataContent">
	<table class="table table-bordered table-striped table-condensed" id="conTable">
		<thead>
			<c:set var="isTbodyNotEmpty" value="${(not empty prfData) and (not empty prfData.tbody) and (not empty prfData.tbody.data) and fn:length(prfData.tbody.data) > 0}"></c:set>
			<c:choose>
				<c:when test="${isTbodyNotEmpty and (not empty prfData.thead)}">
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
					<tr><td>暂无数据！</td></tr>
				</c:otherwise>
			</c:choose>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${isTbodyNotEmpty}">
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
	<div id="portInfopageNub" class="pagination pagination-centered"></div>
	<c:if test="${isTbodyNotEmpty}">
		<script>
		$("#portInfopageNub").getLinkStr({
			pagecount:"${prfData.tbody.totalPages}",
			curpage:"${prfData.tbody.currentPage}",
			numPerPage:"${prfData.tbody.numPerPage}",
			isShowJump:true,
			ajaxRequestPath:"${path}/servlet/port/PortAction?func=PortPrfPage&portId=${portId}&level=3&tablePage=1&subSystemID=${subSystemID}",divId:'dataContent'});
			var $csv = $("#exportCSV");
			$csv.unbind();
			var exurl = "${path}/servlet/port/PortAction?func=exportPrefData&portId=${portid}&level=3&subSystemID=${subSystemID}";
			$csv.attr("href",exurl);
		</script>
	</c:if>
	<c:if test="${not isTbodyNotEmpty}">
		<script>
			var $csv = $("#exportCSV");
			$csv.unbind();
			$csv.attr("href","javascript:void(0);");
			$csv.bind("click",function(){ bAlert("暂无可导出数据！"); });
		</script>
	</c:if>
</div>