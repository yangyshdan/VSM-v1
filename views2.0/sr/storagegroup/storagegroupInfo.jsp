<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/jstl-function" prefix="fn" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt" %>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@ taglib uri="/tags/fmtNumber" prefix="fmtNumber" %>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var subsystemId = "${subsystemId}";
var hostgroupId = "${hostgroupId}";
var storageType = "${storageType}";

$(document).ready(function(){
	<%--增加查询条件/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp--%>
	$.ajax({
		url: "${path}/servlet/sr/storagegroup/StoragegroupAction?func=PerfChartSetting2",
		data: {subsystemId: "${subsystemId}", level: 3, hostgroupId: "${hostgroupId}" },
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			$("#devtypeAndDevice").hide();
		}
	});
	doListRefresh();
});

//加载性能信息
function doListRefresh(){
	loadData("${path}/servlet/sr/storagegroup/StoragegroupAction?func=LoadPerfInfo",{subsystemId:subsystemId,hostgroupId:hostgroupId,storageType:storageType,level:3},$("#perfChart"));
}

//设置性能图信息
function settingPerf() {
	Public.settingPerfChart("${path}/servlet/sr/storagegroup/StoragegroupAction?func=PerfChartSetting&subsystemId=" + subsystemId + "&hostgroupId=" + hostgroupId + "&level=3");
}

//卷刷新
function volumeRefresh(){
	var jsonVal = {};
	loadData("${path}/servlet/sr/storagegroup/StoragegroupAction?func=HostgroupVolumePage&hostgroupId=${hostgroupId}&subsystemId=${subsystemId}&storageType=${storageType}",jsonVal,$("#volumeContent"));
}
</script>

<div id="content">
	<!-- 存储关系组详细信息开始 -->
	<div class="well" style="padding-top:0px;padding-bottom:0px;">
		<div style="width: 10%;float: left;">
			<div style="margin-top:0px;width:100%;height:30px;text-align: center;">存储系统组</div>
			<div style="width:80%;"><img src="${path}/resource/img/project/StorageSystemBase.png"/></div>
		</div>
		<div class="box-content" style="width: 85%; margin: 0 0 0 10px; padding-top:0px;float: left;">
		<legend style="margin-bottom:0px;">名称: ${hostgroupInfo.hostgroup_name}</legend>
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>网络地址</h4></th>
					<td class="center">${hostgroupInfo.uid}</td>
				</tr>
				<tr>
					<th><h4>系统</h4></th>
					<td class="center">
						${hostgroupInfo.model}
					</td>                                       
				</tr>
				<tr>
					<th><h4>是否可共享</h4></th>
					<td class="center">
						${hostgroupInfo.shareable}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
			  <tbody>
				<tr>
					<th><h4>主机名</h4></th>
					<td class="center">${hostgroupInfo.server_name ==null?"N/A":hostgroupInfo.server_name}</td>
				</tr>
				<tr>
					<th><h4>主机IP地址</h4></th>
					<td class="center">${hostgroupInfo.server_ip_address ==null?"N/A":hostgroupInfo.server_ip_address}</td>
				</tr>
				<tr>
					<th><h4>更新时间</h4></th>
					<td class="center">
						${hostgroupInfo.update_timestamp}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		</div>
		<div style="clear: both;"></div>
	</div>
	<!-- 存储关系组详细信息结束 -->
	
	<!-- 存储关系组性能信息开始  -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					性能
				</h2>
				<div class="box-icon">
					<!--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="settingPerf()" data-rel='tooltip'><i class="icon-cog"></i></a>-->
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doListRefresh()"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel='tooltip'></i></a>
				</div>
			</div>
			<div id="queryPage" class="box-content" style="height:130px;"></div>
			<div class="box-content" style="min-height:300px;">
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
				<div id="perfChart" class="tab-content" style="overflow: visible;">
					<!-- 性能曲线切换页开始 -->
					<div class="tab-pane active" id="loadcontent">
						<div id="prfContent" style="width: 100%; height: 300px;"></div>
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
								$("#perfInfoPageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagegroup/StoragegroupAction?func=LoadPerfInfo&subsystemId=${subsystemId}&hostgroupId=${hostgroupId}&storageType=${storageType}&level=3&tablePage=1",divId:'dataContent'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/sr/storagegroup/StoragegroupAction?func=ExportPerfData&subsystemId=${subsystemId}&hostgroupId=${hostgroupId}&storageType=${storageType}&level=3";
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
					<!-- 性能数据切换页结束 -->
				</div>
			</div>
		</div>
		<!--/span-->
	</div>
	<!-- 存储关系组性能信息结束  -->
	
	<!-- 卷信息开始 -->
	<div class="row-fluid sortable">	
		<div class="box span12">
			<div class="box-header well">
				<h2>卷</h2>
				<div class="box-icon">
					<a href="#" class="btn btn-round" title="刷新" onclick="volumeRefresh();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="volumeExportCSV"><i class="icon-download-alt"></i></a>
					<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/sr/storagegroup/StoragegroupAction?func=ExportVolumeConfig&hostgroupId=${hostgroupId}";
						$("#volumeExportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content" id="volumeContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								逻辑卷名
							</th>
							<th>
								逻辑空间(G)
							</th>
							<th>
								实占空间(G)
							</th>
							<th>
								RAID类型
							</th>
							<th>
								控制器类型
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty volumePage.data}">
								<c:forEach var="item" items="${volumePage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=${subsystemId}&volumeId=${item.volume_id}&storageType=${storageType}">LUN ${item.name}</a>
										</td>
										<td>
											<fmt:formatNumber value="${item.logical_capacity/1024}" pattern="0.00"/>
										</td>
										<td>
											<c:if test="${not empty item.physical_capacity}">
												<fmt:formatNumber value="${item.physical_capacity/1024}" pattern="0.00"/></c:if>
											<c:if test="${empty item.physical_capacity}">N/A</c:if>
										</td>
										<td>
											${item.raid_level}
										</td>
										<td>
											<a href="${path}/servlet/sr/storagenode/StoragenodeAction?func=LoadStoragenodeInfo&subsystemId=${subsystemId}&spId=${item.sp_id}&storageType=${storageType}">${item.current_owner}</a>
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
				<div id="volumeajaxpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty volumePage.data}">
					<script>
						$("#volumeajaxpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagegroup/StoragegroupAction?func=HostgroupVolumePage&hostgroupId=${hostgroupId}&subsystemId=${subsystemId}&storageType=${storageType}",divId:'volumeContent'});
					</script>
				</c:if>
				<c:if test="${empty volumePage.data}">
					<script>
						$("#volumeExportCSV").unbind();
						$("#volumeExportCSV").attr("href","javascript:void(0);");
						$("#volumeExportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 卷信息结束 -->	
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>