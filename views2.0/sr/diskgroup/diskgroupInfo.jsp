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
var diskgroupId = "${diskgroupId}";
var storageType = "${storageType}";

$(document).ready(function(){
	<%--增加查询条件/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp--%>
	$.ajax({
		url: "${path}/servlet/sr/diskgroup/DiskgroupAction?func=PerfChartSetting2",
		data: {subsystemId: "${subsystemId}", level: 3, diskgroupId: "${diskgroupId}",storageType:storageType },
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
	loadData("${path}/servlet/sr/diskgroup/DiskgroupAction?func=LoadPerfInfo",{subsystemId:subsystemId,diskgroupId:diskgroupId,storageType:storageType,level:3},$("#perfChart"));
}

//设置性能图信息
function settingPerf() {
	Public.settingPerfChart("${path}/servlet/sr/diskgroup/DiskgroupAction?func=PerfChartSetting&subsystemId=" + subsystemId + "&diskgroupId=" + diskgroupId + "&level=3");
}

//刷新磁盘列表信息
function doDiskFreshen(){
	var jsonVal={diskgroupId:diskgroupId,subSystemID:subsystemId};
	loadData("${path}/servlet/sr/disk/DiskAction?func=AjaxStoragePage",jsonVal,$("#subdiskContent"));
}

</script>

<div id="content">
	<!-- 磁盘组详细信息开始  -->
	<div class="well" style="padding-top:0px;padding-bottom:0px;">
		<div style="width: 10%;float: left;">
			<div style="margin-top:0px;width:100%;height:20px;text-align: center;">磁盘组</div>
			<div style="width:80%;"><img src="${path}/resource/img/project/diskgroup.png"/></div>
		</div>
		<div class="box-content" style="width: 85%; margin: 0 0 0 10px; padding-top:0px;float: left;">
		<legend style="margin-bottom:0px;">名称:RAID Group ${diskgroupInfo.name}</legend>
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>阵列类型</h4></th>
					<td class="center">${diskgroupInfo.raid_level}</td>
				</tr>
				<tr>
					<th><h4>磁盘数</h4></th>
					<td class="center">${diskgroupInfo.width}</td>
				</tr>
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
			  <tbody>
				<tr>
					<th><h4>磁盘容量</h4></th>
					<td class="center">
						<fmt:formatNumber value="${diskgroupInfo.ddm_cap/1024}" pattern="0.00" />G
					</td>                                       
				</tr>
				<tr>
					<th><h4>磁盘速度</h4></th>
					<td class="center">
						${diskgroupInfo.ddm_speed}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		</div>
		<div style="clear: both;"></div>
	</div>
	<!-- 磁盘组详细信息结束  -->
	
	<!-- 磁盘组性能信息开始  -->
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
								$("#perfInfoPageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/diskgroup/DiskgroupAction?func=LoadPerfInfo&subsystemId=${subsystemId}&diskgroupId=${diskgroupId}&storageType=${storageType}&level=3&tablePage=1",divId:'dataContent'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/sr/diskgroup/DiskgroupAction?func=ExportPerfData&subsystemId=${subsystemId}&diskgroupId=${diskgroupId}&storageType=${storageType}&level=3";
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
	<!-- 磁盘组性能信息结束  -->
	
	<!-- 磁盘信息开始 -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					磁盘
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doDiskFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportdiskCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/sr/disk/DiskAction?func=ExportDiskConfigData&subSystemID=${subSystemID}&diskgroupId=${diskgroupId}";
						$("#exportdiskCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
			<div id="subdiskContent">
					<table class="table table-bordered table-striped table-condensed">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									磁盘容量(G)
								</th>
								<th>
									磁盘速度
								</th>
								<th>
									位置
								</th>
								<th>
									类型
								</th>
								<th>
									更新时间
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty subDiskPage.data}">
									<c:forEach var="item" items="${subDiskPage.data}" varStatus="status">
										<tr>
											<td>
												${item.name}
											</td>
											<td>
												<fmt:formatNumber value="${item.ddm_cap/1024}" pattern="0.00"/>
											</td>
											<td>
												${item.ddm_speed}
											</td>
											<td>
												${item.display_name}
											</td>
											<td>
												${item.ddm_type}
											</td>
											<td>
												${item.update_timestamp}
											</td>					
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=6>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
				<div id="diskpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty subDiskPage.data}">
					<script type="text/javascript">
						$("#diskpageNub").getLinkStr({pagecount:"${subDiskPage.totalPages}",curpage:"${subDiskPage.currentPage}",numPerPage:"${subDiskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/disk/DiskAction?func=AjaxStoragePage&diskgroupId=${diskgroupId}&subSystemID=${subSystemID}",divId:'subdiskContent'});
					</script>
				</c:if>
				<c:if test="${empty subDiskPage.data}">
					<script>
						$("#exportdiskCSV").unbind();
						$("#exportdiskCSV").attr("href","javascript:void(0);");
						$("#exportdiskCSV").bind("click",function(){bAlert("暂无可导出数据！")});
					</script>
				</c:if>
			</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>