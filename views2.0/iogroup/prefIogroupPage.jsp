<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/ftime" prefix="formateTime" %>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	$("#loadcontent2").addClass("active");
	$("#dataContent").removeClass("active");
	$("a[href='#dataContent']").parent("li").removeClass("active");
	$("a[href='#loadcontent2']").parent("li").addClass("active");
	var json = ${prfData};
	Public.drawPrfLine("prfContent",json);
	$("#pTitle").html(function(){
		var str="性能  (";
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
});
</script>
<!-- 性能曲线切换页开始 -->
<div class="tab-pane active" id="loadcontent2">
	<div id="prfContent" style="width: 95%; height: 350px;"></div>
</div>
<!-- 性能曲线切换页结束 -->
<!-- 性能数据切换页开始 -->
<div class="tab-pane" id="dataContent">
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
<div id="iogroupInfopageNub" class="pagination pagination-centered"></div>
<c:choose>
	<c:when test="${(not empty prfData) and (not empty prfData.tbody) and (not empty prfData.tbody.data) and fn:length(prfData.tbody.data) > 0}">
		<script>
			$(function(){
				$("#iogroupInfopageNub").getLinkStr({
					pagecount:"${prfData.tbody.totalPages}",
					curpage:"${prfData.tbody.currentPage}",
					numPerPage:"${prfData.tbody.numPerPage}",
					isShowJump:true,
					ajaxRequestPath: "${path}/servlet/iogroup/IogroupAction?func=IogroupPrfPage&iogroupId=${iogroupId}&level=3&tablePage=1&subSystemID=${subSystemID}",
					divId: "dataContent"});
				var $exportCSV = $("#exportCSV");
				$exportCSV.unbind();
				$exportCSV.attr("href", "${path}/servlet/iogroup/IogroupAction?func=ExportPrefData&iogroupId=${iogroupId}&level=3&tablePage=1&subSystemID=${subSystemID}");
			});
		</script>
	</c:when>
	<c:otherwise>
		<script>
			$(function(){
				var $exportCSV = $("#exportCSV");
				$exportCSV.unbind();
				$exportCSV.attr("href", "javascript:void(0);");
				$exportCSV.bind("click",function(){ bAlert("暂无可导出数据！"); });
			});
			
		</script>
	</c:otherwise>
</c:choose>
</div>