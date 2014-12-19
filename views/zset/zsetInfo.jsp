<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var zsetId = "${zsetId}";
function refreshZone(){
	loadData("${path}/servlet/zone/ZoneAction?func=AjaxZonePage&zsetId="+${zsetId},{},$("#zoneContent"));
		return false;
}
</script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>
<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/zset.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${zsetInfo.the_display_name}</span>
			<br>
			<strong>区域数:</strong>
			<span>${zsetInfo.the_zone_count}</span>
			<strong style="margin-left: 20px;">是否活动:</strong>
			<span>${zsetInfo.active==0?"否":"是"}</span>
		</li>
</ul>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#detailTab">配置</a>
		</li>
		<li class="">
			<a href="#dataTab">区域</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
<!-- 详细信息开始 -->
	<div class="tab-pane active" id="detailTab">
		<div class="box-content" style="width: 98%; padding-top:10px;">
		<!-- 详细信息开始 -->
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>区域数</h4></th>
					<td class="center">
						${zsetInfo.the_zone_count}
					</td>
				</tr>
				<tr>
					<th><h4>光纤</h4></th>
					<td class="center">${zsetInfo.f_name}</td>
				</tr>
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
			  <tbody>
				<tr>
					<th><h4>是否活动</h4></th>
					<td class="center">
						${zsetInfo.active==0?"否":"是"}
					</td>                                       
				</tr>
				<tr>
					<th><h4>描述</h4></th>
					<td class="center">${zsetInfo.description}</td>
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!-- 详细信息结束-->
	
	<!--部件的开始 -->
	<div class="tab-pane" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					区域列表(${zoneCount})
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="refreshZone();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="zoneExportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/zset/ZsetAction?func=exportZsetConfigData";
						$("#zoneExportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content" style="max-height:810px;" id="subTab">
				<div id="perfChart" class="tab-content" style="overflow: visible;min-height:200px;">
				<!-- 区域开始 -->
					<div class="tab-pane active" id="zoneContent" style="padding-top:10px;">
						<table class="table table-bordered table-striped table-condensed" id="conTable">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										类型
									</th>
									<th>
										是否活动
									</th>
									<th>
										WWN
									</th>
									<th>
										描述
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty zonePage.data}">
										<c:forEach var="item" items="${zonePage.data}" varStatus="status">
											<tr>
												<td>
													${item.the_display_name}
												</td>
												<td>
													<c:if test="${item.zone_type==1}">Soft</c:if>
													<c:if test="${item.zone_type==2}">Hard</c:if>
													<c:if test="${item.zone_type==3}">None</c:if>
												</td>
												<td>
													<cs:isActive value="${item.active}" />
												</td>
												<td>
													${item.fabric_wwn}
												</td>
												<td>
													${item.description}
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
						<div id="zoneListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty zonePage.data}">
							<script>
								$("#zoneListpageNub").getLinkStr({pagecount:"${zonePage.totalPages}",curPage:"${zonePage.currentPage}",numPerPage:"${zonePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/zone/ZoneAction?func=AjaxZonePage&zsetId="+${zsetId},divId:'zoneContent'});
							</script>
						</c:if>
						<c:if test="${empty zonePage.data}">
							<script>
								$("#zoneExportCSV").unbind();
								$("#zoneExportCSV").attr("href","javascript:void(0);");
								$("#zoneExportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
							</script>
						</c:if>
					</div>
					<!-- 虚拟机结束 -->
				</div>
			</div>
		</div>
	</div>
	</div>
	<!--部件的结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>