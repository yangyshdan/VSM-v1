<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var zsetId = "${zsetId}";
function doFreshen(){
	var jsonVal={};
	loadData("${path}/servlet/zone/ZoneAction?func=AjaxZonePage&zsetId="+zsetId, jsonVal,$("#zoneContent"));
}
</script>
<script src="${path}/resource/js/highcharts/highcharts.js"></script>
<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/zset.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${zsetInfo.the_display_name}</span>
			<br>
			<strong>Zone数量:</strong>
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
			<a href="#dataTab">Zone</a>
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
					<th><h4>Zone数量</h4></th>
					<td class="center">
						${zsetInfo.the_zone_count}
					</td>
				</tr>
				<tr>
					<th><h4>Fabric网络</h4></th>
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
					Zone(${zoneCount})
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="zoneExportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/zset/ZsetAction?func=ExportZoneConfigData&zsetId="+zsetId;
						$("#zoneExportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content" style="max-height:810px;" id="subTab">
				<div id="perfChart" class="tab-content" style="overflow: visible;min-height:200px;">
				<!-- Zone开始 -->
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
										Fabric网络
									</th>
									<th>
										WWNN
									</th>
									<th>
										是否活动
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
											<tr ondblclick="trDbClick(${item.zone_id})" style="cursor:pointer;">
												<td>
													${item.the_display_name}
												</td>
												<td>
													<c:if test="${item.zone_type==1}">Soft</c:if>
													<c:if test="${item.zone_type==2}">Hard</c:if>
													<c:if test="${item.zone_type==3}">None</c:if>
												</td>
												<td>
													<a title="${item.fabric_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.fabric_name}</a>
												</td>
												<td>
													${item.fabric_wwn}
												</td>
												<td>
													<cs:isActive value="${item.active}" />
												</td>
												<td>
													${empty item.description ? "N/A" : item.description}
												</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="6">
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
								$("#zoneListpageNub").getLinkStr({pagecount:"${zonePage.totalPages}",curpage:"${zonePage.currentPage}",numPerPage:"${zonePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/zone/ZoneAction?func=AjaxZonePage&zsetId=${zsetId}",divId:'zoneContent'});
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
					<!-- Zone结束 -->
				</div>
			</div>
		</div>
	</div>
	</div>
	<!--部件的结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>