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
<script src="${path}/resource/js/project/deviceAlert.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>

<script type="text/javascript">
var hypervisorId = "${hypervisorId}";
var computerId = "${computerId}";

$(function(){
	Highcharts.setOptions({global: {useUTC: false}});
	<%--增加查询条件--%>
	$.ajax({
		url: "${path}/servlet/hypervisor/HypervisorAction?func=HypervisorSettingPrf2",
		data: {
			hypervisorId: "${hypervisorInfo.hypervisor_id}",
			computerId: "${computerId}",
			level: 3,
			type: "Hypervisor"
		},
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			$("#devtypeAndDevice").hide();
		}
	});
	doListRefresh2();
	
	//绘制图形
	var cpuPerfData = ${cpuPerfData};
	var memPerfData = ${memPerfData};
	var netPerfData = ${netPerfData};
	var diskPerfData = ${diskPerfData};
	
	Public.drawPrfLine("perfContent_cpu", cpuPerfData);
	Public.drawPrfLine("perfContent_mem", memPerfData);
	Public.drawPrfLine("perfContent_network", netPerfData);
	Public.drawPrfLine("perfContent_disk", diskPerfData);
	//跳转到相应的选项卡
	var tab = "${tabToShow}";
	//默认展示总览页面(0),不为0时展示其他选项卡
	if (tab > 0) {
		$("#liOverallView").removeClass("active");
		$("#overViewTab").removeClass("active");
		//详细信息选项卡
		if (tab == 1) {
			$("#liDetail").addClass("active");
			$("#detailTab").addClass("active");
		//性能信息选项卡
		} else if (tab == 2) {
			$("#liPerf").addClass("active");
			$("#prfTab").addClass("active");
		//事件选项卡
		} else if (tab == 3) {
			$("#liAlert").addClass("active");
			$("#alertTab").addClass("active");
		//虚拟机选项卡
		} else if (tab == 4) {
			$("#liData").addClass("active");
			$("#dataTab").addClass("active");
		}
	}
	
	$("#subFreshen").bind("click",function(){
		loadData(getRootPath()+"/servlet/virtual/VirtualAction?func=AjaxVirtualPage",{computerId:computerId,hypervisorId:hypervisorId},$("#virtualContent"));
	});
	$("#subShowlist").bind("click",function(){
		window.location=getRootPath()+"/servlet/virtual/VirtualAction?func=VirtualPage";
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
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=DrawPerfLine",{hypervisorId:hypervisorId,computerId:computerId,timeRange:timeRange},$("#perfChart"));
}

function doListRefresh2(){
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPrfPage",
		{hypervisorId:hypervisorId,level:3}, $("#perfChart2"));
}

window.parent.doListRefresh = function(){
	doListRefresh2();
}
function doAlertFilter(){
	loadData("${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",{resourceId:hypervisorId,topId:hypervisorId,resourType:"Physical"},$("#dAlertContent"));
}

//跳转到事件选项卡
function toAlertTab(level) {
	loadData("${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",{resourceId:hypervisorId,topId:hypervisorId,resourType:"Physical",level:level,state:0},$("#dAlertContent"));
	$("#alertLink").click();
}

</script>

<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/hv.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${hypervisorInfo.display_name}</span>
			<br>
			<strong>IP:</strong>
			<span>${hypervisorInfo.ip_address}</span>
			<strong style="margin-left: 20px;">Status:</strong>
			<span>
			<c:choose>
				<c:when test="${hypervisorInfo.operational_status=='0'}">Unknown</c:when>
				<c:when test="${hypervisorInfo.operational_status=='1'}">Not Available</c:when>
				<c:when test="${hypervisorInfo.operational_status=='2'}">Servicing</c:when>
				<c:when test="${hypervisorInfo.operational_status=='3'}">Starting</c:when>
				<c:when test="${hypervisorInfo.operational_status=='4'}">Stopping</c:when>
				<c:when test="${hypervisorInfo.operational_status=='5'}">Stopped</c:when>
				<c:when test="${hypervisorInfo.operational_status=='6'}">Aborted</c:when>
				<c:when test="${hypervisorInfo.operational_status=='7'}">Dormant</c:when>
				<c:when test="${hypervisorInfo.operational_status=='8'}">Completed</c:when>
				<c:when test="${hypervisorInfo.operational_status=='9'}">Migrating</c:when>
				<c:when test="${hypervisorInfo.operational_status=='10'}">Migrating</c:when>
				<c:when test="${hypervisorInfo.operational_status=='11'}">Emigrating</c:when>
				<c:when test="${hypervisorInfo.operational_status=='12'}">Immigrating</c:when>
				<c:otherwise>${hypervisorInfo.operational_status}</c:otherwise>
			</c:choose>
			</span>
		</li>
</ul>

<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li id="liOverallView" class="active">
			<a href="#overViewTab">总览</a>
		</li>
		<li class="">
			<a href="#hardwareStatus">硬件状态</a>
		</li>
		<li id="liDetail" class="">
			<a href="#detailTab">配置</a>
		</li>
		<li id="liPerf" class="">
			<a href="#prfTab">性能</a>
		</li>
		<li id="liAlert" class="">
			<a id="alertLink" href="#alertTab">事件</a>
		</li>
		<li id="liData" class="">
			<a href="#dataTab">虚拟机</a>
		</li>
	</ul>
	
	<div id="myTabContent" class="tab-content">
		<!-- 物理机总览信息开始 -->
		<div class="tab-pane active" id="overViewTab" style="height:700px;">
			<div class="row-fluid">
				<!-- 性能统计信息部分 -->
				<div class="box span12" style="height:300px;">
					<div class="box-header well">
						<h2 id="pcTitle">性能统计</h2>
						<div id="timeRangeDiv" class="box-icon" style="font-size:13px;">
							<label style="float:left;font-weight:bold;margin:-1px 2px 0px;">最近:</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('hour')" style="width:40px;text-align:center;">1小时</a>
							<label style="width:8px;float:left;">|</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('day')" style="width:30px;text-align:center;">1天</a>
							<label style="width:8px;float:left;">|</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('week')" style="width:30px;text-align:center;">1周</a>
							<label style="width:8px;float:left;">|</label>
							<a href="javascript:void(0);" onclick="doPerfLineFilter('month')" style="width:30px;text-align:center;">1月</a>
						</div>
					</div>
					<!-- Performance Charts -->
					<div id="perfChart" class="box-content">
						<!-- CPU Busy Performance Chart -->
						<div class="box span4" id="cpuChart" style="width:24%;margin-left:10px;">
							<div class="box-header well">
								<h2 style="height:20px;overflow: hidden;">CPU(%Busy)</h2>
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
				<div class="box span12" style="width:20%;height:auto;float:left;margin-left:0px;">
					<div class="box-header well">
						<h2 id="assetsTitle">
							基本信息
						</h2>
					</div>
					<div class="box-content">
						<table class="table table-bordered table-striped table-condensed">
							<tr>
								<td style="font-weight:bold;">
									设备类型
								</td>
								<td>
									物理机
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									厂商
								</td>
								<td>
									${empty hypervisorInfo.vendor ? 'N/A' : hypervisorInfo.vendor}
								</td>
							</tr>
							<tr style="white-space:normal;">
								<td style="font-weight:bold;">
									操作系统
								</td>
								<td>
									${hypervisorInfo.os_version}
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									IP地址
								</td>
								<td>
									${hypervisorInfo.ip_address}
								</td>
							</tr>
							<%-- 
							<tr>
								<td style="font-weight:bold;">
									Hypervisor
								</td>
								<td>
									<c:choose>
										<c:when test="${empty hypvInfo.name}">N/A</c:when>
										<c:when test="${not empty hypvInfo.name}">
											<a href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatInfo&virtualPlatId=${hypvInfo.id}&physicalId=${hypvInfo.hypervisor_id}">${hypvInfo.name}</a>
										</c:when>
									</c:choose>
								</td>
							</tr>
							 --%>
							<tr>
								<td style="font-weight:bold;">
									CPU数量
								</td>
								<td>
									${hypervisorInfo.processor_count}
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									CPU频率
								</td>
								<td>
									<fmt:formatNumber value="${hypervisorInfo.processor_speed/1000}" pattern="0.##"/>GHz
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									内存
								</td>
								<td>
									<fmt:formatNumber value="${hypervisorInfo.ram_size/1024}" pattern="0.##"/>GB
								</td>
							</tr>
						</table>
					</div>
				</div>
				
				<!-- 事件信息部分 -->
				<div class="box span12" style="width:39%;">
					<div class="box-header well">
						<h2 id="alertTitle">
							事件
						</h2>
					</div>
					<div class="box-content">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										故障等级
									</th>
									<th>
										发生次数
									</th>
									<th>
										发生时长
									</th>
									<th>
										最近发生时间
									</th>
								</tr>
							</thead>
							<tbody>
								<tr onclick="toAlertTab('2');" style="cursor:pointer;">
									<td>
										<span class='label label-important'>Critical</span>
									</td>
									<td>
										${errorData.count}
									</td>
									<td>
										${errorData.timelen}
									</td>
									<td>
										${errorData.lateTime}
									</td>
								</tr>
								<tr onclick="toAlertTab('1');" style="cursor:pointer;">
									<td>
										<span class='label label-warning'>Warning</span>
									</td>
									<td>
										${warnData.count}
									</td>
									<td>
										${warnData.timelen}
									</td>
									<td>
										${warnData.lateTime}
									</td>
								</tr>
								<tr onclick="toAlertTab('0');" style="cursor:pointer;">
									<td>
										<span class='label'>Info</span>
									</td>
									<td>
										${infoData.count}
									</td>
									<td>
										${infoData.timelen}
									</td>
									<td>
										${infoData.lateTime}
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				
				<!-- 磁盘信息部分 -->
				<div class="box span12" style="width:39%;float:right;">
					<div class="box-header well">
						<h2 id="diskTitle">
							磁盘
						</h2>
					</div>
					<div class="box-content">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										总容量(GB)
									</th>
									<th>
										剩余容量(GB)
									</th>
									<th>
										使用率(%)
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>
										<fmt:formatNumber value="${hypervisorInfo.disk_space/1024}" pattern="0.##"/>
									</td>
									<td>
										<fmt:formatNumber value="${hypervisorInfo.disk_available_space/1024}" pattern="0.##"/>
									</td>
									<td>
										<cs:isProgress total="${hypervisorInfo.disk_space}" available="${hypervisorInfo.disk_space-hypervisorInfo.disk_available_space}" warning="80" error="95"/>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				
				<%-- CPU TopN信息部分 --%>
				<%-- 
				<div class="box span12" style="width:39%;height:200px;margin-left:0px;">
					<div class="box-header well">
						<h2 id="cpuTopNTitle">
							虚拟机 CPU Busy Top5
						</h2>
					</div>
					<div class="box-content" id="box-content_topN_cpu" style="height:150px;">
						<div class="clearfix" id="perfContent_topN_cpu" style="height:150px;">
						</div>
					</div>
				</div>
				 --%>
				
				<%-- Memory TopN信息部分 --%>
				<%-- 
				<div class="box span12" style="width:39%;height:200px;">
					<div class="box-header well">
						<h2 id="memTopNTitle">
							虚拟机 Mem Used Top5
						</h2>
					</div>
					<div class="box-content" id="box-content_topN_mem" style="height:150px;">
						<div class="clearfix" id="perfContent_topN_mem" style="height:150px;"></div>
					</div>
				</div>
				 --%>
			</div>
		</div>
		<!-- 物理机总览信息结束 -->
		<div class="tab-pane" id="hardwareStatus" style="overflow: hidden !important;">
			<div class="box span12" style="width:99%; min-height: 650px;">
					<div class="box-header well">
						<h2 id="hardwareStatusTitle">服务器硬件状态(${sdrCount})</h2>
					</div>
					<div class="box-content" id="hardwareStatus2">
						<table class="table table-bordered table-striped table-condensed"  style="table-layout:fixed;" id="conTable">
							<thead>
								<tr>
									<th width="16%">传感器数据记录类型</th>
									<th width="16%">监控项目</th>
									<th width="16%">设备名称</th>
									<th width="16%">状态</th>
									<th width="16%">读数</th>
									<th width="16%">更新时间</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${(not empty sdrPage) and (not empty sdrPage.data) and fn:length(sdrPage.data) > 0}">
										<c:forEach items="${sdrPage.data}" var="item">
										<tr>
											<td>${item.sdr_type}</td>
											<td>${item.device_type}</td>
											<td>${item.device_name}</td>
											<td>${item.status}</td>
											<td>${item.read_data}</td>
											<td>${item.update_timestamp}</td>
										</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="6">暂无数据!</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="HardwarePageNub" class="pagination pagination-centered"></div>
							<c:if test="${(not empty sdrPage) and (not empty sdrPage.data) and fn:length(sdrPage.data) > 0}">
								<script>
									$("#HardwarePageNub").getLinkStr({pagecount:"${sdrPage.totalPages}",curpage:"${sdrPage.currentPage}",numPerPage:"${sdrPage.numPerPage}",isShowJump:true,
									ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=SdrPage&hypervisorId=${hypervisorId}",divId: "hardwareStatus2"});
								</script>
							</c:if>
					</div>
				</div>
		</div>
		<%-- 硬件状态 开始#hardwareStatus--%>
		
		<%-- 硬件状态 结束--%>
	
		<!-- 物理机详细信息开始 -->
		<div class="tab-pane" id="detailTab">
			<div class="box-content" style="width: 98%; padding-top:10px;">
			<!-- 物理机详细信息开始 -->
				<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;"> 
				 	<tbody>
						<tr>
							<th><h4>厂商</h4></th>
							<td class="center">
								<c:if test="${empty hypervisorInfo.vendor}">
									N/A
								</c:if>
								<c:if test="${not empty hypervisorInfo.vendor}">
									<img class="dashboard-avatar" style="border-width:0px;width:80px;height:16px;" title="${hypervisorInfo.vendor}" src="${path}/resource/img/logo/${fn:toLowerCase(hypervisorInfo.vendor)}.png">
								</c:if>
							</td>                                       
						</tr>
					  	<tr>
							<th><h4>CPU数量</h4></th>
							<td class="center">
								${hypervisorInfo.processor_count}
							</td>                                       
						</tr>	
						<tr>
							<th><h4>磁盘总容量(GB)</h4></th>
							<td class="center">
								<fmt:formatNumber value="${hypervisorInfo.disk_space/1024}" pattern="0.##" />
							</td>                                       
						</tr>		
						<tr>
							<th><h4>内存(GB)</h4></th>
							<td class="center">
								<fmt:formatNumber value="${hypervisorInfo.ram_size/1024}" pattern="0.##" />
							</td>                                        
						</tr>
						<tr>
							<th><h4>未分配CPU</h4></th>
							<td class="center">
								<fmt:formatNumber value="${hypervisorInfo.available_cpu}" pattern="0.##" />
							</td>                                       
						</tr>
						<tr>
							<th><h4>主板厂商</h4></th>
							<td class="center">${hypervisorInfo.board_vendor}</td>                                       
						</tr>
						<tr>
							<th><h4>主板序列号</h4></th>
							<td class="center">${hypervisorInfo.board_serial_num}</td>                                       
						</tr>
						<tr>
							<th><h4>主板出厂日期</h4></th>
							<td class="center">${hypervisorInfo.board_mfg_datetime}</td>                                       
						</tr>
						<tr>
							<th><h4>产品名称</h4></th>
							<td class="center">${hypervisorInfo.prod_name}</td>                                       
						</tr>
						<tr>
							<th><h4>产品型号</h4></th>
							<td class="center">${hypervisorInfo.prod_model}</td>                                       
						</tr>
						<tr>
							<th><h4>底盘类型</h4></th>
							<td class="center">${hypervisorInfo.chassis_type}</td>                                       
						</tr>
						<tr>
							<th><h4>底盘序列号</h4></th>
							<td class="center">${hypervisorInfo.chassis_serial_num}</td>                                       
						</tr>
					</tbody>
				</table>  
				<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;"> 
					<tbody>
						<tr>
							<th><h4>操作系统</h4></th>
							<td class="center">
								${hypervisorInfo.os_version}
							</td>                                       
						</tr>
						<tr>
							<th><h4>CPU频率</h4></th>
							<td class="center">
								<fmt:formatNumber value="${hypervisorInfo.processor_speed/1000}" pattern="0.##"/>GHz
							</td>                                       
						</tr>
						<tr>
							<th><h4>磁盘剩余容量(GB)</h4></th>
							<td class="center">
								<fmt:formatNumber value="${hypervisorInfo.disk_available_space/1024}" pattern="0.##" />
							</td>                                       
						</tr>
						<tr>
							<th><h4>未分配内存(GB)</h4></th>
							<td class="center">
								<fmt:formatNumber value="${hypervisorInfo.available_mem/1024}" pattern="0.##" />
							</td>                                       
						</tr>
						<tr>
							<th><h4>更新时间</h4></th>
							<td class="center">
								${hypervisorInfo.update_timestamp}
							</td>                                       
						</tr>
						<tr>
							<th><h4>主板制造商</h4></th>
							<td class="center">${hypervisorInfo.board_factory}</td>                                       
						</tr>
						<tr>
							<th><h4>主板型号</h4></th>
							<td class="center">${hypervisorInfo.board_model}</td>                                       
						</tr>
						<tr>
							<th><h4>产品厂商</h4></th>
							<td class="center">${hypervisorInfo.prod_factory}</td>                                       
						</tr>
						<tr>
							<th><h4>产品版本</h4></th>
							<td class="center">${hypervisorInfo.prod_version}</td>                                       
						</tr>
						<tr>
							<th><h4>产品序列号</h4></th>
							<td class="center">${hypervisorInfo.prod_serial_num}</td>                                       
						</tr>
						<tr>
							<th><h4>底盘型号</h4></th>
							<td class="center">${hypervisorInfo.chassis_model}</td>                                       
						</tr>
						
						
					</tbody>
				</table>
			</div>
			
			<!-- 物理机详细信息表单结束 -->
			<div style="clear: both;"></div>
		</div>
		<!-- 物理机详细信息结束-->
		<!-- 物理机性能开始 -->
		<div class="tab-pane" id="prfTab">
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						物理机性能
					</h2>
					<div class="box-icon">
						<!--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf3('${hypervisorInfo.hypervisor_id}','${computerId}','3')" data-rel='tooltip'><i class="icon-cog"></i></a>-->
						<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
						<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
						<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div id="queryPage" class="box-content" ></div>
				<div class="box-content" >
				
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
					<div id="perfChart2" class="tab-content" style="overflow: visible;min-height:200px;">
						<!-- 性能曲线切换页开始 -->
						<div class="tab-pane active" id="loadcontent2">
							<div id="prfContent2" style="width: 97%; max-height: 350px;"></div>
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
																	<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
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
							<div id="HypervisorInfopageNub" class="pagination pagination-centered"></div>
							<c:if test="${not empty prfData.tbody.data}">
								<script>
									$("#HypervisorInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,
									ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPrfPage&hypervisorId=${hypervisorId}&level=3&tablePage=1",divId:'dataContent2'});							
									
									$("#exportCSV").unbind();
									var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportPrefData&hypervisorId=${hypervisorId}&computerId=${computerId}&level=3&type=Hypervisor";
									$("#exportCSV").attr("href",exurl);
								</script>
							</c:if>
							<c:if test="${empty prfData.tbody.data}">
								<script>
									$("#exportCSV").attr("href","javascript:void(0);");
									$("#exportCSV").bind("click", function(){bAlert("暂无可导出数据！")});
								</script>
							</c:if>
						</div>
						<!-- 性能数据切换页结束 -->
					</div>
				</div>
			</div>
		</div>
		</div>
		<!-- 物理机性能结束 -->
		<!-- 物理机事件开始 -->
		<div class="tab-pane" id="alertTab">
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">事件</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${hypervisorId}','${hypervisorId}','Physical');"><i class="icon-color icon-ok"></i> </a>
						<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${hypervisorId}','${hypervisorId}','Physical');"><i class="icon icon-color icon-trash"></i> </a>
						<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doAlertFilter();"><i class="icon icon-color icon-refresh"></i> </a>
						<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content" id="dAlertContent">
					<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
						<thead>
							<tr>
								<th style="width: 20px;">
									<label class="checkbox inline">
										<input type="checkbox" onclick="DeviceAlert.doAlertCheck(this.checked);">
								    </label>
								</th>
								<th style="width: 130px;">
									首次发生时间
								</th>
								<th style="width: 130px;">
									最后发生时间
								</th>
								<th  style="width: 55px;">
									类型
								</th>
								<th  style="width: 55px;">
									重复次数
								</th>
								<th style="width: 90px;">
									状态
								</th>
								<th style="width: 90px;">
									级别
								</th>
								<th style="width: 150px;">
									事件源
								</th>
								<th>
									消息
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty deviceLogPage.data}">
									<c:forEach var="item" items="${deviceLogPage.data}" varStatus="status">
										<tr>
											<td>
												<label class="checkbox inline">
													<input type="checkbox" value="${item.fruleid}_${item.ftopid}_${item.flogtype}" name="dAlertCheck">
											    </label>
											</td>
											<td>
												<fmt:formatDate value="${item.ffirsttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
											</td>
											<td>
												<fmt:formatDate value="${item.flasttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
											</td>
											<td>
												<c:choose>
													<c:when test="${item.flogtype == 3}">硬件告警</c:when>
													<c:when test="${item.flogtype == 2}">阀值告警</c:when>
													<c:when test="${item.flogtype == 1}">TPC告警</c:when>
													<c:when test="${item.flogtype == 0}">系统告警</c:when>
												</c:choose>
											</td>
											<td>
												${item.fcount}
											</td>
											<td>
												<c:choose>
													<c:when test="${item.fstate == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
													<c:when test="${item.fstate == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
													<c:when test="${item.fstate == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
													<c:when test="${item.fstate == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${item.flevel == 0}"><span class="label">Info</span> </c:when>
													<c:when test="${item.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
													<c:when test="${item.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
												</c:choose>
											</td>
											<td>
												<a href="javascript:goToEventDetailPage('${item.ftopid}', '${item.ftoptype}','${item.fresourceid}')">${item.fresourcename}</a>
											</td>
											<td>
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','${item.fresourcetype}')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename } <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
												<c:out value="${fn:substring(item.fdetail, 0, 200)}......" /></c:when> <c:otherwise><c:out value="${item.fdetail}" /></c:otherwise></c:choose>" title="详细信息">
												 ${item.fdescript}
												</a>
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
					
					<div class="pagination pagination-centered">
						<ul id="alertListNub"></ul>
					</div>
					<c:if test="${not empty deviceLogPage.data}">
						<script>	
							$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",
							curpage:"${deviceLogPage.currentPage}",
							numPerPage:"${deviceLogPage.numPerPage}",
							isShowJump:true,
							ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${hypervisorId}&topId=${hypervisorId}&resourceType=Physical&level=${level}&state=${state}",
							divId:'dAlertContent'});
						</script>
					</c:if>
				</div>
			</div>
		</div>
		</div>
		<!-- 物理机事件结束 -->
		
		<!--物理机下部件虚拟机信息开始 -->
		<div class="tab-pane" id="dataTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2>
							虚拟机(${virtualCount})
						</h2>
						<div class="box-icon">
							<a id='subFreshen' href="javascript:void(0)" class="btn btn-round" title="刷新" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div class="box-content" style="max-height:810px;" id="subTab">
						<div id="perfChart" class="tab-content" style="overflow: visible;min-height:200px;">
						<!-- 虚拟机开始 -->
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
													<cs:isProgress total="${item.disk_space}" available="${item.disk_space-item.disk_available_space}" warning="80" error="95"/>
												</td>
												<td>
													<fmt:formatNumber value="${item.disk_space/1024}" pattern="0.##"/>
												</td>									
												<td>
													<fmt:formatNumber value="${item.disk_available_space/1024}" pattern="0.##"/>
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
										$("#virtualListpageNub").getLinkStr({pagecount:"${virtualPage.totalPages}",curpage:"${virtualPage.currentPage}",numPerPage:"${virtualPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage&computerId=${computerId}&hypervisorId=${hypervisorId}",divId:'virtualContent'});
									</script>
								</c:if>	
							</div>
							<!-- 虚拟机结束 -->
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--物理机下部件虚拟机信息结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>