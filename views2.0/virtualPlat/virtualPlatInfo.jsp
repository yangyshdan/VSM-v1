<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/computer.js"></script> 
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var physicalId = "${virtPlatInfo.hypervisor_id}";
$(function(){
	Highcharts.setOptions({global: {useUTC: false}});
	<%--增加查询条件--%>
	$.ajax({
		url: "${path}/servlet/virtualPlat/VirtualPlatAction?func=HypervisorVmSettingPrf",
		data: {
			physicalId: physicalId,
			level: 3
		},
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			$("#isShowDevName").bootstrapSwitch("setActive", false);
		}
	});
	
	//绘制图形
	var cpuPerfData = ${cpuPerfData};
	var memPerfData = ${memPerfData};
	var netPerfData = ${netPerfData};
	var diskPerfData = ${diskPerfData};
	var cpuTopNData = ${cpuTopNData};
	var memTopNData = ${memTopNData};
	Public.drawPrfLine("perfContent_cpu",cpuPerfData);
	Public.drawPrfLine("perfContent_mem",memPerfData);
	Public.drawPrfLine("perfContent_network",netPerfData);
	Public.drawPrfLine("perfContent_disk",diskPerfData);
	Public.drawTopn("perfContent_topN_cpu",cpuTopNData);
	Public.drawTopn("perfContent_topN_mem",memTopNData);
	//加载性能图
	doListRefresh();
	
	//判断是否需要跳转到虚拟机列表选项卡
	var tab = "${showVmTab}";
	//切换到虚拟机选项卡
	if (tab == 1) {
		$("#liOverallView").removeClass("active");
		$("#overViewTab").removeClass("active");
		$("#liVms").addClass("active");
		$("#vmsTab").addClass("active");
	}
	
	//虚拟机列表
	var vmList = ${vmList};
	$("#selVmList").multiselect({enableFiltering:1,maxHeight:120});
	var str = "";
	for(var i in vmList){
		str += "<option value='"+vmList[i].vm_id+"'>"+vmList[i].name+"</option>";
	}
	$("#selVmList").append(str);
	$("#selVmList").multiselect('rebuild');
	
	$("#subFreshen").bind("click",function(){
		loadData(getRootPath()+"/servlet/virtual/VirtualAction?func=AjaxVirtualPage",{hypervisorId:physicalId},$("#virtualContent"));
	});
	$("#subTab li").bind('click',function(){
		StroageInfo.subTabChange(computerId);
	});
});

//性能统计信息筛选
function doPerfLineFilter(timeRange) {
	var pcTitle = "性能统计";
	if (timeRange == "hour") {
		pcTitle = pcTitle + "(1小时)";
	} else if (timeRange == "day") {
		pcTitle = pcTitle + "(1天)";
	} else if (timeRange == "week") {
		pcTitle = pcTitle + "(1周)";
	} else if (timeRange == "month") {
		pcTitle = pcTitle + "(1月)";
	}
	$("#pcTitle").html(pcTitle);
	var seleVmIds = $("#selVmList").val();
	var vmIdStr = "";
	if (seleVmIds != null && seleVmIds != "") {
		for (i = 0; i < seleVmIds.length; i++) {
			vmIdStr = vmIdStr + "," + seleVmIds[i];
		}
	}
	loadData("${path}/servlet/virtualPlat/VirtualPlatAction?func=DrawPerfLine",{physicalId:physicalId,vmIds:vmIdStr,timeRange:timeRange},$("#perfChart"));
}

//加载性能数据
function doListRefresh(){
	loadData("${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPrfPage",{physicalId:physicalId,level:3},$("#perfChart2"));
}
//跳转到虚拟机列表选项卡
function toVmsTab() {
	$("#vmsLink").click();
}

</script>
<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width:0px;width:100px;" src="${path}/resource/img/logo/${fn:toLowerCase(virtPlatInfo.type)}.png">
			</a>
			<span style="font-size:25px;">${virtPlatInfo.name}</span>
			<br>
			<strong>类型:</strong>
			<span>${virtPlatInfo.type}</span>
			<strong style="margin-left: 20px;">版本:</strong>
			<span>${virtPlatInfo.version}</span>
		</li>
</ul>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li id="liOverallView" class="active">
			<a href="#overViewTab">总览</a>
		</li>
		<li id="liPerf" class="">
			<a href="#prfTab">性能</a>
		</li>
		<li id="liVms" class="">
			<a id="vmsLink" href="#vmsTab">虚拟机</a>
		</li>
	</ul>
	
	<div id="myTabContent" class="tab-content">
	
		<!-- HYPERVISOR总览信息开始 -->
		<div class="tab-pane active" id="overViewTab" style="height:700px;">
			<div class="row-fluid">
				<!-- 性能统计信息部分 -->
				<div class="box span12" style="height:300px;">
					<div class="box-header well">
						<h2 id="pcTitle">
							性能统计
						</h2>
						<div id="timeRangeDiv" class="box-icon" style="font-size:13px;float:right;margin-right:5px;">
							<label style="float:left;font-weight:bold;margin:-1px 2px 0px;">最近:</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('hour')" style="width:40px;text-align:center;">1小时</a>
							<label style="width:8px;float:left;">|</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('day')" style="width:30px;text-align:center;">1天</a>
							<label style="width:8px;float:left;">|</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('week')" style="width:30px;text-align:center;">1周</a>
							<label style="width:8px;float:left;">|</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('month')" style="width:30px;text-align:center;">1月</a>
						</div>
						<div id="selVmListDiv" class="control-group" style="float:right;height:20px;">
							<label style="font-weight:bold;margin:-1px 5px 0px;display:inline;">选择虚拟机:</label>
							<div class="controls" style="float:right;margin-top:-7px;margin-right:5px;">
								<div class="input-prepend input-append" style="display:inline;">
									<select id="selVmList" name="selVmList" multiple="multiple" style="width:100px;margin-right:5px;"></select>
								</div>
							</div>
						</div>
					</div>
					<!-- Performance Charts -->
					<div id="perfChart" class="box-content">
						<!-- CPU Busy Performance Chart -->
						<div class="box span4" id="cpuChart" style="width:24%;margin-left:10px;">
							<div class="box-header well">
								<h2 style="height:20px;overflow: hidden;">
									CPU(%User)
								</h2>
							</div>
							<div class="box-content" id="box-content_cpu" style="height:200px;">
								<div class="clearfix" id="perfContent_cpu" style="height:200px;"></div>
							</div>
						</div>
						<!-- Memory Used Performance Chart -->
						<div class="box span4" id="memChart" style="width:24%;">
							<div class="box-header well">
								<h2 style="height:20px;overflow: hidden;">
									Mem(%Used)
								</h2>
							</div>
							<div class="box-content" id="box-content_mem" style="height:200px;">
								<div class="clearfix" id="perfContent_mem" style="height:200px;"></div>
							</div>
						</div>
						<!-- Network Performance Chart -->
						<div class="box span4" id="networkChart" style="width:24%;">
							<div class="box-header well">
								<h2 style="height:20px;overflow: hidden;">
									Network(Total Packets/sec)
								</h2>
							</div>
							<div class="box-content" id="box-content_network" style="height:200px;">
								<div class="clearfix" id="perfContent_network" style="height:200px;"></div>
							</div>
						</div>
						<!-- Disk Performance Chart -->
						<div class="box span4" id="diskChart" style="width:24%;">
							<div class="box-header well">
								<h2 style="height:20px;overflow: hidden;">
									Disk(Total Bytes/sec)
								</h2>
							</div>
							<div class="box-content" id="box-content_disk" style="height:200px;">
								<div class="clearfix" id="perfContent_disk" style="height:200px;"></div>
							</div>
						</div>
					</div>
				</div>
				
				<!-- 基本信息部分 -->
				<div class="box span12" style="width:20%;height:280px;float:left;margin-left:0px;">
					<div class="box-header well">
						<h2 id="assetsTitle">
							基本信息
						</h2>
					</div>
					<div class="box-content">
						<table class="table table-bordered table-striped table-condensed">
							<tr style="white-space:normal;">
								<td style="font-weight:bold;">
									物理机CPU
								</td>
								<td>
									${virtPlatInfo.physcpu}
								</td>
							</tr>
							<tr style="white-space:normal;">
								<td style="font-weight:bold;">
									已分配CPU
								</td>
								<td>
									${virtPlatInfo.assign_cpu}
								</td>
							</tr>
							<tr style="white-space:normal;">
								<td style="font-weight:bold;">
									物理机内存
								</td>
								<td>
									<fmt:formatNumber value="${virtPlatInfo.physmem/1024}" pattern="0.##"/>GB
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									已分配内存
								</td>
								<td>
									<fmt:formatNumber value="${virtPlatInfo.assign_memory/1024}" pattern="0.##"/>GB
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									存储实际可用容量
								</td>
								<td>
									<fmt:formatNumber value="${virtPlatInfo.storage_capacity/1024}" pattern="0.##"/>GB
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									已分配容量
								</td>
								<td>
									<fmt:formatNumber value="${virtPlatInfo.storage_assigned/1024}" pattern="0.##"/>GB
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									存储剩余容量
								</td>
								<td>
									<fmt:formatNumber value="${virtPlatInfo.storage_available/1024}" pattern="0.##"/>GB
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									虚拟机数量
								</td>
								<td>
									<a href="javascript:void(0);" onclick="toVmsTab()">${virtPlatInfo.vms_num}</a>
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									虚拟网络数量
								</td>
								<td>
									${virtPlatInfo.interfaces_num}
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									网络接口数量
								</td>
								<td>
									${virtPlatInfo.networks_num}
								</td>
							</tr>
						</table>
					</div>
				</div>
				
				<!-- CPU TopN信息部分 -->
				<div class="box span12" style="width:39%;height:280px;">
					<div class="box-header well">
						<h2 id="cpuTopNTitle">
							虚拟机 CPU User Top5
						</h2>
					</div>
					<div class="box-content" id="box-content_topN_cpu" style="height:230px;">
						<div class="clearfix" id="perfContent_topN_cpu" style="height:230px;">
						</div>
					</div>
				</div>
				
				<!-- Memory TopN信息部分 -->
				<div class="box span12" style="width:39%;height:280px;float:right;">
					<div class="box-header well">
						<h2 id="memTopNTitle">
							虚拟机 Mem Used Top5
						</h2>
					</div>
					<div class="box-content" id="box-content_topN_mem" style="height:230px;">
						<div class="clearfix" id="perfContent_topN_mem" style="height:230px;"></div>
					</div>
				</div>
			</div>
		</div>
		<!-- HYPERVISOR总览信息结束  -->
	
		<!-- HYPERVISOR中虚拟机性能开始 -->
		<div class="tab-pane" id="prfTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2 id="pTitle">虚拟机性能</h2>
						<div class="box-icon">
							<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh();"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
							<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div id="queryPage" class="box-content" ></div>
					<div class="box-content" style="min-height:300px;">
						<!-- tab切换标签开始 -->
						<ul class="nav nav-tabs" id="myTab">
							<li class="active">
								<a href="#loadcontent2">性能曲线</a>
							</li>
							<li class="">
								<a href="#dataContent2">性能数据</a>
							</li>
						</ul>
						<!-- tab切换标签结束 -->
						<div id="perfChart2" class="tab-content" style="overflow:visible;">
							<!-- 性能曲线切换页开始 -->
							<div class="tab-pane active" id="loadcontent2">
								<div id="prfContent2" style="width:97%; max-height:350px;"></div>
							</div>
							<%-- 性能曲线切换页结束 --%>
							<%-- 性能数据切换页开始 --%>
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
											<c:when test="${not empty prfData}">
												<c:forEach var="item" items="${prfData.tbody.data}" varStatus="status">
													<tr>
														<c:forEach var="thead" items="${prfData.thead}">
															<td>
																<c:choose>
																	<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">
																		${item.ele_name}
																	</c:when>
																	<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																		<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd hh:mm:ss" />
																	</c:when>
																	<c:otherwise>
																		<c:if test="${prfData.threshold==1}">
																			<span style="${item[fn:toLowerCase(thead.key)] >=prfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
																		</c:if>
																		<c:if test="${prfData.threshold==0}">
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
								<div id="hyperVmPageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty prfData.tbody.data}">
									<script>
										$("#hyperVmPageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPrfPage&physicalId=${physicalId}&level=3&tablePage=1",divId:'dataContent2'});							
										$("#exportCSV").unbind();
										var exurl = "${path}/servlet/virtualPlat/VirtualPlatAction?func=ExportPrefData&physicalId=${physicalId}&level=3";
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
			</div>
		</div>
		<!-- HYPERVISOR中虚拟机性能结束 -->
		
		<!-- HYPERVISOR中虚拟机信息开始 -->
		<div class="tab-pane" id="vmsTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2>
							虚拟机(${virtualPage.totalRows})
						</h2>
						<div class="box-icon">
							<a id='subFreshen' href="javascript:void(0)" class="btn btn-round" title="刷新" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div class="box-content" style="max-height:810px;" id="subTab">
						<div id="perfChart" class="tab-content" style="overflow: visible;min-height:200px;">
							<div class="tab-pane active" id="virtualContent">
								<table class="table table-bordered table-striped table-condensed">
									<thead>
										<tr>
											<th>
												名称
											</th>
											<th>
												所属物理机
											</th>
											<th>
												IP地址
											</th>
											<th>
												逻辑CPU个数
											</th>
											<th>
												物理CPU个数
											</th>
											<th>
												总内存(GB)
											</th>
											<th>
												磁盘容量使用率(%)
											</th>
											<th>
												磁盘总容量(GB)
											</th>
											<th>
												磁盘剩余容量(GB)
											</th>
											<th>
												更新时间
											</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty virtualPage.data}">
												<c:forEach var="item" items="${virtualPage.data}" varStatus="status">
												<tr>
												<td>
													<a href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=${item.hypervisor_id}&vmId=${item.vm_id}">${item.display_name}</a>
												</td>
												<td>
													<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.host_name}</a>
												</td>
												<td>
													${item.ip_address}
												</td>
												<td>
													<fmt:formatNumber var="lCpu" value="${item.assigned_cpu_number}" pattern="#"/>
													<cs:isZeroAndNull value="${lCpu}"></cs:isZeroAndNull>
												</td>	
												<td>
													<fmt:formatNumber var="pCpu" value="${item.assigned_cpu_processunit}" pattern="#"/>
													<cs:isZeroAndNull value="${pCpu}"></cs:isZeroAndNull>
												</td>										
												<td>
													<fmt:formatNumber value="${item.total_memory/1024}" pattern="0.##"/>
												</td>	
												<td>
													<fmt:formatNumber value="${item.disk_space/1024}" pattern="0.##"/>
												</td>									
												<td>
													<fmt:formatNumber value="${item.disk_available_space/1024}" pattern="0.##"/>
												</td>	
												<td>
													<cs:isProgress total="${item.disk_space}" available="${item.disk_space-item.disk_available_space}" warning="80" error="95"/>
												</td>								
												<td>
													${item.update_timestamp}
												</td>	
												</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=9>
														暂无数据！
													</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="virtualListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty virtualPage.data}">
									<script>
										$("#virtualListpageNub").getLinkStr({pagecount:"${virtualPage.totalPages}",curpage:"${virtualPage.currentPage}",numPerPage:"${virtualPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage&hypervisorId="+physicalId,divId:'virtualContent'});
									</script>
								</c:if>	
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- HYPERVISOR中虚拟机信息结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>