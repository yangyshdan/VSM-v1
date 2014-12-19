<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/Chart.js"></script>
<script src="${path }/resource/js/project/homepage.js"></script>
<script type="text/javascript">
$(function(){
	Highcharts.setOptions({global: {useUTC: false}});
});
$(document).ready(function(){
	Chart.capacityChart(${capacityJson});
	Chart.assetsChart(${assetsJson});
	setInterval(Chart.doChartRef,1200000);
	setInterval(logRefresh,180000);
});

</script>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>
<div id="content">

	<div class="well" style="height:200px;">
		<div id="assetsCon" style="width: 30.3%;height:98%;float:left;"></div>
		<div id="capacityCon" style="width: 68.2%;float:left;height:98%;margin-right:0.5%;"></div>
	</div>
	<!-- 事件开始 -->
	<div class="row-fluid sortable">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					事件
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="logRefresh();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;max-height:180px;" id="logContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th style="width: 85px;">
								首次发生时间
							</th>
							<th style="width: 85px;">
								最后发生时间
							</th>
							<th  style="width: 55px;">
								重复次数
							</th>
							<th style="width: 30px;">
								状态
							</th>
							<th style="width: 30px;">
								级别
							</th>
							<th style="width: 50px;">
								事件源
							</th>
							<th>
								消息
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty logPage}">
								<c:forEach var="item" items="${logPage}" varStatus="status">
									<tr style="cursor:pointer;">
										<td>
											<fmt:formatDate value="${item.first_alert_time}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											<fmt:formatDate value="${item.last_alert_time}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											${item.alert_count}
										</td>
										<td>
											<c:choose>
												<c:when test="${item.state == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
												<c:when test="${item.state == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
												<c:when test="${item.state == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
												<c:when test="${item.state == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.the_severity == 0}"><span class="label">Info</span> </c:when>
												<c:when test="${item.the_severity == 1}"><span class="label">Notice</span> </c:when>
												<c:when test="${item.the_severity == 2}"><span class="label label-info">Warning</span> </c:when>
												<c:when test="${item.the_severity == 3}"><span class="label label-warning">Error</span> </c:when>
												<c:when test="${item.the_severity == 4}"><span class="label important">Critical</span> </c:when>
												<c:when test="${item.the_severity == 5}"><span class="label important">Alert</span> </c:when>
												<c:when test="${item.the_severity == 6}"><span class="label important">Emergency</span> </c:when>
											</c:choose>
										</td>
										<td>
											${item.resource_name}
										</td>
										<td>
											${item.msg}
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=7>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<!-- 事件结束 -->
</div>
<script type="text/javascript">
$(document).ready(function(){
	HomePage.drawDiv(${chartSort},${chartList});
});
function logRefresh(){
		loadData("${path}/servlet/index/HomePage?func=AjaxAlertLog",{},$("#logContent"));
		return false;
}
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>