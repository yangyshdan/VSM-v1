<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
var vmId = "${vmId}";
$(function(){
	<%--增加查询条件--%>
	$.ajax({
		url: "${path}/servlet/virtual/VirtualAction?func=VirtualSettingPrf2",
		data: {
			vmId: "${vmId}",
			hypervisorId: "${hypervisorId}",
			computerId: "${computerId}",
			level: "3"
		},
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			//$("#devtypeAndDevice").hide();
		}
	});
	doListRefresh2();
	//绘制图形
	var cpuPerfData = ${cpuPerfData};
	var memPerfData = ${memPerfData};
	var netPerfData = ${netPerfData};
	var diskPerfData = ${diskPerfData};
	
	Public.drawPrfLine("perfContent_cpu",cpuPerfData);
	Public.drawPrfLine("perfContent_mem",memPerfData);
	Public.drawPrfLine("perfContent_network",netPerfData);
	Public.drawPrfLine("perfContent_disk",diskPerfData);
	
	//判断是否需要跳转到事件选项卡
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
		} 
	}
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
	loadData("${path}/servlet/virtual/VirtualAction?func=DrawPerfLine",{hypervisorId:hypervisorId,vmId:vmId,timeRange:timeRange},$("#perfChart"));
}
function doListRefresh2(){
	loadData("${path}/servlet/virtual/VirtualAction?func=VirtualPrfPage",{level:3,hypervisorId:"${virtualInfo.hypervisor_id}",vmId:"${virtualInfo.vm_id}"},$("#perfChart2"));
}
function doAlertFilter(){
	loadData("${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",{resourceId:"${virtualInfo.vm_id}",topId:hypervisorId,resourType:"Virtual"},$("#dAlertContent"));
}

window.parent.doListRefresh = function(){
	doListRefresh2();
}

//跳转到事件选项卡
function toAlertTab(level) {
	loadData("${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",{resourceId:vmId,topId:hypervisorId,resourType:"Virtual",level:level,state:0},$("#dAlertContent"));
	$("#alertLink").click();
}
</script>
<ul class="dashboard-list" style="margin-bottom: 10px;">
	<li style="padding-top: 0px; padding-bottom: 20px;">
		<a href="#">
			<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/host.png" alt="StorageSystem">
		</a>
		<span style="font-size:25px;">${virtualInfo.display_name} </span>
		<br>
		<strong>IP:</strong>
		<span>${virtualInfo.ip_address}</span>
		<strong>操作系统:</strong>
		<span>${virtualInfo.os_version}</span>
	</li>
</ul>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li id="liOverallView" class="active">
			<a href="#overViewTab">总览</a>
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
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 虚拟机总览信息开始 -->
		<div class="tab-pane active" id="overViewTab">
			<div class="row-fluid">
				<!-- 性能统计信息部分 -->
				<div class="box span12" style="height:300px;">
					<div class="box-header well">
						<h2 id="pcTitle">
							性能统计
						</h2>
						<div class="box-icon" style="font-size:13px;">
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
								<h2 style="height:20px;overflow: hidden;">
									CPU(%Busy)
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
									虚拟机
								</td>
							</tr>
							<tr style="white-space:normal;">
								<td style="font-weight:bold;">
									操作系统
								</td>
								<td>
									${virtualInfo.os_version}
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									IP地址
								</td>
								<td>
									${virtualInfo.ip_address}
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									CPU数量
								</td>
								<td>
									${virtualInfo.processor_count}
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									CPU频率
								</td>
								<td>
									<fmt:formatNumber value="${virtualInfo.processor_speed/1000}" pattern="0.##"/>GHz
								</td>
							</tr>
							<tr>
								<td style="font-weight:bold;">
									内存
								</td>
								<td>
									<fmt:formatNumber value="${virtualInfo.total_memory/1024}" pattern="0.##"/>GB
								</td>
							</tr>
						</table>
					</div>
				</div>
				
				<!-- 事件信息部分 -->
				<div class="box span12" style="width:39%;">
					<div class="box-header well">
						<h2 id="alertTitle">事件</h2>
					</div>
					<div class="box-content">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>故障等级</th>
									<th>发生次数</th>
									<th>发生时长</th>
									<th>最近发生时间</th>
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
										<fmt:formatNumber value="${virtualInfo.disk_space/1024.0}" pattern="0.##"/>
									</td>
									<td>
										<fmt:formatNumber value="${virtualInfo.disk_available_space/1024.0}" pattern="0.##"/>
									</td>
									<td>
										<cs:isProgress total="${virtualInfo.disk_space}" available="${virtualInfo.disk_space-virtualInfo.disk_available_space}" warning="80" error="95"/>
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
			</div>
		</div>
		<!-- 虚拟机总览信息结束 -->
	
		<!-- 虚拟机详细信息开始 -->
		<div class="tab-pane" id="detailTab">
			<!-- 虚拟机详细信息表单开始  -->
			<div class="box-content" style="width: 98%;  padding-top:10px;">
				<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;"> 
				  <tbody>
					<tr>
						<th><h4>物理机名称</h4></th>
						<td class="center"><a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${virtualInfo.hypervisor_id}">${virtualInfo.host_name}</a></td>
					</tr>
					<tr>
						<th><h4>总内存(GB)</h4></th>
						<td class="center">
							<fmt:formatNumber value="${virtualInfo.total_memory/1024}" pattern="0.##"/>
						</td>                                       
					</tr>
					<tr>
						<th><h4>磁盘总容量(GB)</h4></th>
						<td class="center">
							<fmt:formatNumber value="${virtualInfo.disk_space/1024}" pattern="0.##"/>
						</td>
					</tr>
					<tr>
						<th><h4>磁盘剩余容量(GB)</h4></th>
						<td class="center">
							<fmt:formatNumber value="${virtualInfo.disk_available_space/1024}" pattern="0.##"/>
						</td>
					</tr>
					<tr>
						<th><h4>工作状态</h4></th>
						<td class="center">${empty virtualInfo.operational_status ? 'Unknown' : virtualInfo.operational_status}</td>                                       
					</tr>
					<tr>
						<th><h4>更新时间</h4></th>
						<td class="center">${virtualInfo.update_timestamp}</td>
					</tr>
					
				  </tbody>
			 </table>  
			 <table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">  
				  <tbody>
					<tr>
						<th><h4>逻辑CPU个数</h4></th>
						<td class="center">
							<fmt:formatNumber var="lCpu" value="${virtualInfo.assigned_cpu_number}" pattern="#"/>
							<cs:isZeroAndNull value="${lCpu}"></cs:isZeroAndNull>
						</td>
					</tr>
					<tr>
						<th><h4>物理CPU个数</h4></th>
						<td class="center">
							<fmt:formatNumber var="pCpu" value="${virtualInfo.assigned_cpu_processunit}" pattern="#"/>
							<cs:isZeroAndNull value="${pCpu}"></cs:isZeroAndNull>
						</td>
					</tr>
					<tr>
						<th><h4>最大CPU数量</h4></th>
						<td class="center">
							<fmt:formatNumber var="maxCpu" value="${virtualInfo.maximum_cpu_number}" pattern="#"/>
							<cs:isZeroAndNull value="${maxCpu}"></cs:isZeroAndNull>
						</td>
					</tr>
					<tr>
						<th><h4>最小CPU数量</h4></th>
						<td class="center">
							<fmt:formatNumber var="minCpu" value="${virtualInfo.minimum_cpu_number}" pattern="#"/>
							<cs:isZeroAndNull value="${minCpu}"></cs:isZeroAndNull>
						</td>
					</tr>
					<tr>
						<th><h4>最大处理单元数</h4></th>
						<td class="center">
							<fmt:formatNumber var="maxcp" value="${virtualInfo.maximum_cpu_processunit}" pattern="#"/>
							<cs:isZeroAndNull value="${maxcp}"></cs:isZeroAndNull>	
						</td>
					</tr>
					<tr>
						<th><h4>最小处理单元数</h4></th>
						<td class="center">
							<fmt:formatNumber var="mcp" value="${virtualInfo.minimum_cpu_processunit}" pattern="#"/>
							<cs:isZeroAndNull value="${mcp}"></cs:isZeroAndNull>
						</td>
					</tr>
				  </tbody>
			 </table>  
			</div>
			<!-- 虚拟机详细信息表单结束 -->
			<div style="clear: both;"></div>
		</div>
		<!-- 虚拟机详细信息结束-->
		<!-- 性能开始 -->
		<div class="tab-pane" id="prfTab">
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						虚拟机性能
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doListRefresh2()"><i class="icon icon-color icon-refresh" data-rel="tooltip"></i></a>
						<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel="tooltip"></i></a>
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
					<div id="perfChart2" class="tab-content" style="overflow: visible;">
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
							<div id="virtualInfopageNub" class="pagination pagination-centered"></div>
							<c:if test="${not empty prfData.tbody.data}">
								<script>
									$("#virtualInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=VirtualPrfPage&hypervisorId=${hypervisorId}&vmId=${vmId}&level=3&tablePage=1",divId:'dataContent2'});
	  								$("#exportCSV").unbind();
									var exurl = "${path}/servlet/virtual/VirtualAction?func=ExportPrefData&hypervisorId=${hypervisorId}&vmId=${vmId}&level=3}";
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
		</div>
		<!-- 性能结束 -->
		<!-- 事件开始 -->
		<div class="tab-pane" id="alertTab">
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						事件预警
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${hypervisorId}','${vmId}','Virtual');"><i class="icon-color icon-ok"></i> </a>
						<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${hypervisorId}','${vmId}','Virtual');"><i class="icon icon-color icon-trash"></i> </a>
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
								<c:when test="${(not empty logPage) and (not empty logPage.data)}">
									<c:forEach var="item" items="${logPage.data}" varStatus="status">
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
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','${item.fresourcetype}')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename} <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
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
					<c:if test="${(not empty logPage) and (not empty logPage.data)}">
						<script>
						$("#alertListNub").getLinkStr({pagecount:"${logPage.totalPages}",curpage:"${logPage.currentPage}",numPerPage:"${logPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${virtualInfo.vm_id}&topId=${hypervisorId}&resourceType=Virtual&level=${level}&state=${state}",divId:'dAlertContent'});
						</script>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<!-- 事件结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>
