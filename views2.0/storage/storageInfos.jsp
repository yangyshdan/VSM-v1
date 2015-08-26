<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<%@taglib uri="/tags/fmtNumber" prefix="fmtNumber"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/widget.js"></script>
<script src="${path}/resource/js/project/storage.js"></script>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/topn.js"></script>
<script src="${path}/resource/js/project/deviceAlert.js"></script>
<script src="${path}/resource/js/pandect/warnings.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
$(function(){
	Public.drawTopn02("ports", {
		jsonVal: getAttribute(${PortTopNData}), 
		getURL: function(point){ // 默认都是TPC的
					return "/servlet/port/PortAction?func=PortInfo&subSystemID=" + point.devid + "&portId=" + point.eleid;
				}
		}
	);
	
	Public.drawTopn02("heats", {
		jsonVal: getAttribute(${HeatTopNData}), 
		getURL: function(point){ // 默认都是TPC的
					return "/servlet/volume/VolumeAction?func=PerVolumeInfo&subSystemID=" + point.devid + "&svid=" + point.eleid;
				}
		}
	);
	
	var vloume=${vloume};
	Widget.drawPie("volumes", vloume);
	
	var report = ${report};
	Public.drawPie("warnings", report);
});


window.parent.doListRefresh = function(){
	$.ajax({
		url: "${path}/servlet/controller/ConAction?func=ConPrfCurvePage",
		data: {
			subSystemID: "${subSystemID}",
			level: 2,
			isFreshen: 0
		},
		dataType: "html",
		success: function(result){
			var $queryTab = $("#queryTab");
			$queryTab.children().remove();
			$queryTab.html(result);
			$queryTab.find("ul#myTab a[href='#dataContent']").click(function(){
				$(this).parent().addClass("active").siblings().first().removeClass("active");
				$queryTab.find("#dataContent").addClass("active");
				$queryTab.find("#loadcontent").removeClass("active");
			});
			$queryTab.find("ul#myTab a[href='#loadcontent']").click(function(){
				$(this).parent().addClass("active").siblings().first().removeClass("active");
				$queryTab.find("#loadcontent").addClass("active");
				$queryTab.find("#dataContent").removeClass("active");
			});
		}
	});
};

var subsystemId = "${subSystemID}";
$(function(){Highcharts.setOptions({global: {useUTC: false}});});
$(function(){
	$.ajax({
		url: "${path}/servlet/storage/StorageAction?func=StorageSettingPrf2",
		data: { subSystemID: "${subSystemID}", level: 3 },
		type: "post",
		dataType: "html",
		success: function(result){
			var $qPage = $("#queryPage");
			$qPage.html(result);
			$("#devtypeAndDevice2").hide();
			$("#graphType").parent().parent().remove();
			if($qPage.find("#prfField2 option").length == 0){
				$("ul#myTab a[href='#prfTab']").hide();
			}
			else {
				doListRefresh2();
			}
		}
	});
	<%--$.ajax({
		url: "${path}/servlet/controller/ConAction?func=ConSettingPrf2",
		data: { subSystemID: "${subSystemID}", level: 2 },
		dataType: "html",
		success: function(result){
			$("#queryPage2").html(result);
		}
	});--%>

	$("#ctrlerContentTab").click(function(){
		$("#controllerContent #loadcontent").addClass("active");
		$("#controllerContent ul#myTab a[href='#loadcontent']").trigger("click");
	});

	//doListRefresh2();
	var jsonVal2 = ${conPrfData};
	Public.drawPrfLine("prfContent2816", jsonVal2);
	
	$("#perfContent_2_1 a").click(function(){
		var $a = $(this);
		var link = $a.attr("link");
		if(link){
			$("div#content ul#myTab a[href='#dataTab']").trigger("click");
			$("div#dataTab ul#myTab a[href='#"+link+"']").trigger("click");
		}
	});
	
});

$(function(){
	var subsystemId = "${subSystemID}";
	$("#subFreshen").bind("click",function(){
		loadData(getRootPath()+"/servlet/port/PortAction?func=AjaxPortPage",{subSystemID:subsystemId},$("#portContent"));
	});
	$("#subShowlist").bind("click",function(){
		window.location=getRootPath()+"/servlet/port/PortAction?func=PortPage&subSystemID="+subsystemId;
	});
	$("#subTab li").bind('click',function(){
		StroageInfo.subTabChange(subsystemId);
	});
});
function doListRefresh2(){
	loadData("${path}/servlet/storage/StorageAction?func=StoragePrfPage",{subSystemID:subsystemId, level:3},$("#perfChart2"));
}
function doPerfLineFilter(timeRange) {
	loadData("${path}/servlet/storage/StorageAction?func=DrawPerfLine",{timeRange:timeRange, subSystemID: "${subSystemID}"},$("#perfChart"));
}

$(function(){
	//判断是否需要跳转到事件选项卡
	var tab = "${tabToShow}";
	//切换到事件选项卡
	if (tab == 3) {
		$("#liOverallView").removeClass("active");
		$("#overViewTab").removeClass("active");
		$("#liAlert").addClass("active");
		$("#alertTab").addClass("active");
	}
	
	window.onload = function(){<%-- 当所有页面加载完毕之后，就触发click事件--%>
		var tab = "${tabToShow}";
		var subTabToShow = "${subTabToShow}";
		if(tab == 4){
			if(subTabToShow.length > 0){
				$("div#content ul#myTab a[href='#dataTab']").trigger("click");
				$("div#dataTab ul#myTab a[href='#"+subTabToShow+"']").trigger("click");
			}
		}
	};
});

</script>
<style>
.spetable td {
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}

#tablestyle h4 {
	color: black;
}
</style>

<ul class="dashboard-list" style="margin-bottom: 10px;">
	<li style="padding-top: 0px; padding-bottom: 20px;">
		<a href="#"> <img class="dashboard-avatar"
				style="border-width: 0px;"
				src="${path}/resource/img/project/storage.png" alt="StorageSystem">
		</a>
		<span style="font-size: 25px;">${storageInfo.the_display_name}
		</span>
		<br>
		<strong>IP:</strong>
		<span> <c:if test="${not empty storageInfo.ip_address}">
				<c:if test="${fn:contains(storageInfo.ip_address,',')}">
					<a title="${fn:split(storageInfo.ip_address,',')[0]}"
						href="http://${fn:split(storageInfo.ip_address,',')[0]}"
						target="_blank">${fn:split(storageInfo.ip_address,',')[0]}</a>,<a
						title="${fn:split(storageInfo.ip_address,',')[1]}"
						href="http://${fn:split(storageInfo.ip_address,',')[1]}"
						target="_blank">${fn:split(storageInfo.ip_address,',')[1]}</a>
				</c:if>
				<c:if test="${not fn:contains(storageInfo.ip_address,',')}">
					<a title="${storageInfo.ip_address}"
						href="http://${storageInfo.ip_address}" target="_blank">${storageInfo.ip_address}</a>
				</c:if>
			</c:if> </span>
		<strong style="margin-left: 20px;">Status:</strong>
		<span>${storageInfo.the_operational_status}</span>
	</li>
</ul>

<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li id="liOverallView" class="active">
			<a href="#overViewTab">总览</a>
		</li>
		<li class="">
			<a href="#detailTab">配置</a>
		</li>
		<li class="">
			<a href="#prfTab">性能</a>
		</li>
		<li id="liAlert" class="">
			<a id="alertLink" href="#alertTab">事件</a>
		</li>
		<li class="">
			<a href="#dataTab">部件</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
		<!-- 总览开始 -->
		<div class="tab-pane active" id="overViewTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<div class="box-icon">
							<label style="float: left; font-weight: bold; margin: -1px 2px 0px;">最近:</label>
							<a style="width: 40px; text-align: center; font-size: 13px; float: left; text-decoration: none;" href="javascript:void(0);" onclick="doPerfLineFilter('hour')">1小时</a>
							<label style="width: 8px; float: left; text-shadow: rgb(0, 0, 0, 0.2) 0px -1px -1px;">|</label>
							<a style="width: 30px; text-align: center; font-size: 13px; float: left; text-decoration: none;" href="javascript:void(0);" onclick="doPerfLineFilter('day')">1天</a>
							<label style="width: 8px; float: left; text-shadow: rgb(0, 0, 0, 0.2) 0px -1px -1px;">|</label>
							<a style="width: 30px; text-align: center; font-size: 13px; float: left; text-decoration: none;" href="javascript:void(0);" onclick="doPerfLineFilter('week')">1周</a>
							<label style="width: 8px; float: left; text-shadow: rgb(0, 0, 0, 0.2) 0px -1px -1px;">|</label>
							<a style="width: 30px; text-align: center; font-size: 13px; float: left; text-decoration: none;" href="javascript:void(0);" onclick="doPerfLineFilter('month')">1月</a>
						</div>
					</div>
					<!-- 基本信息 开始-->
					<div id="prefContent">
						<div id="charts_2_1" class="box span4"
							style="margin: 5px 4px 0px 5px; width: 32%;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">基本信息</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix"
									style="height: 250px;" data-highcharts-chart="2">
									<div
										style="position: relative; overflow: hidden; width: 100%; height: 250px; left: 0px; top: 0px;"
										class="highcharts-container">
										<table
											class="table table-bordered table-striped table-condensed spetable"
											style="margin-bottom: 0px;" id="tablestyle">
											<tbody>
												<tr>
													<td><h4>名称</h4></td>
													<td><h4>描述</h4></td>
												</tr>
												<tr>
													<td>
														<h4>厂商</h4>
													</td>
													<td class="center">
														${storageInfo.vendor_name}
													</td>
												</tr>
												<tr>
													<td>
														<h4>
															型号
														</h4>
													</td>
													<td class="center">
														${storageInfo.model_name}
													</td>
												</tr>
												<tr>
													<td>
														<h4>
															序列号
														</h4>
													</td>
													<td class="center"> ${storageInfo.serial_number}</td>
												</tr>
												<tr>
													<td>
														<h4>
															物理磁盘容量(G)
														</h4>
													</td>
													<td class="center">
														<fmt:formatNumber value="${storageInfo.the_physical_disk_space}" pattern="0.00" />
													</td>
												</tr>
												<tr>
													<td>
														<h4>
															池容量(G)
														</h4>
													</td>
													<td class="center">
														<fmt:formatNumber
															value="${storageInfo.the_storage_pool_consumed_space}"
															pattern="0.00" />
													</td>
												</tr>
												<tr>
													<td>
														<h4>
															已分配卷容量(G)
														</h4>
													</td>
													<td class="center">
														<fmt:formatNumber
															value="${storageInfo.the_assigned_volume_space}"
															pattern="0.00" />
													</td>
												</tr>
												<tr>
													<td>
														<h4>
															运行状态
														</h4>
													</td>
													<td class="center">
														${storageInfo.the_operational_status}
													</td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
					<%-- 基本信息结束 --%>
					<%-- 容量开始 --%>
					<div id="prefContent">
						<div id="charts_2_1" class="box span4"
							style="margin: 5px; width: 33%;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									容量使用(%)
								</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix"
									style="height: 250px;" data-highcharts-chart="2">
									<div id="volumes"
										style="position: relative; overflow: hidden; width: 100%; height: 250px; left: 0px; top: 0px;"
										class="highcharts-container"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 容量结束 -->
					<!-- 告警开始-->
					<div id="prefContent">
						<div id="charts_2_1" class="box span4"
							style="margin: 0px 5px 0px 0px; width: 33%;float: right;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									告警情况(%)
								</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix"
									style="height: 250px;" data-highcharts-chart="2">
									<div id="warnings"
										style="position: relative; overflow: hidden; width: 100%; height: 250px; left: 0px; top: 0px;"
										class="highcharts-container"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 告警结束 -->
					<!-- 热量开始 -->
					<div id=prefChart>
						<div id="charts_2_1" class="box span4"
							style="margin: 5px 5px 0px 0px; width: 49%; float: right;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									Total I/O Rate (overall) Top5
								</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix"
									style="height: 250px;" data-highcharts-chart="2">
									<div id="heats"
										style="position: relative; overflow: hidden; width: 90%; height: 250px; left: 0px; top: 0px;"
										class="highcharts-container"></div>
								</div>
							</div>
						</div>
						<!-- 热量结束 -->
						<!-- 端口开始 -->
						<div id="charts_2_1" class="box span4"
							style="margin: 5px 5px 0px 5px; width: 49%; float: left;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									Total Port I/O Rate Top5
								</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix"
									style="height: 250px;" data-highcharts-chart="2">
									<div id="ports" style="position: relative; overflow: hidden; width: 90%; height: 250px; left: 0px; top: 0px; text-align: center;"
										class="highcharts-container"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 端口结束-->

					<!-- 部件统计开始-->
					<div id="prefContent">
						<div id="charts_2_1" class="box span4"
							style="margin-left: 5px;; width: 99.2%;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									部件统计
								</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 70px;">
								<div id="perfContent_2_1" class="clearfix" style="height: 70px;" data-highcharts-chart="2">
									<div style="position: relative; overflow: hidden; width: 100%; height: 60px; left: 0px; top: 10px;"
										class="highcharts-container">
										<table
											class="table table-bordered table-striped table-condensed spetable"
											style="margin-bottom: 10px;" id="tablestyle">
											<tbody>
												<tr>
													<td><h4>名称</h4></td>
													<td><h4>端口</h4></td>
													<td><h4>磁盘</h4></td>
													<td><h4>存储池</h4></td>
													<td><h4>卷</h4></td>
													<td>
														<h4>存储扩展
														</h4>
													</td>
													<td>
														<h4>阵列
														</h4>
													</td>
													<td>
														<h4>Rank
														</h4>
													</td>
													<td>
														<h4>控制器</h4>
													</td>
													<td>
														<h4>IOGroup</h4>
													</td>
												</tr>
												<tr>
													<td><h4>数量</h4></td>
													<td><a href="javascript:void(0);" link="portContent">${portCount}</a></td>
													<td><a href="javascript:void(0);" link="diskContent">${diskCount}</a></td>
													<td><a href="javascript:void(0);" link="poolContent">${poolCount}</a></td>
													<td><a href="javascript:void(0);" link="volumeContent">${volumeCount}</a></td>
													<td><a href="javascript:void(0);" link="extendContent">${extendCount}</a></td>
													<td><a href="javascript:void(0);" link="arraysiteContent">${arrayCount}</a></td>
													<td><a href="javascript:void(0);" link="rankContent">${rankCount}</a></td>
													<td><a href="javascript:void(0);" link="nodeContent">${nodeCount}</a></td>
													<td><a href="javascript:void(0);" link="iogroupContent">${iogroupCount}</a></td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 总览 结束-->
		<!-- 存储系统详细信息表单开始 -->
		<div class="tab-pane" id="detailTab">
			<%--<div class="well" style="padding-top:0px;padding-bottom:0px;">
 		<div style="width: 10%;float: left;">
			<div style="margin-top:50px;width:100%;height:30px;text-align: center;">存储系统</div>
			<div style=""><img src="${path}/resource/img/project/storage.png"/></div>
		</div>
 --%>
			<div class="box-content" style="width: 98%; padding-top: 10px;">
				<!-- 		<legend style="margin-bottom:0px;">名称: &nbsp;&nbsp;${storageInfo.the_display_name}</legend> -->
				<table class="table configTable"
					style="margin-bottom: 0px; width: 49%; float: left;">
					<tbody>
						<tr>
							<th>
								<h4>
									厂商
								</h4>
							</th>
							<td class="center">
								<c:if test="${empty storageInfo.vendor_name}">"N/A"</c:if>
								<c:if test="${not empty storageInfo.vendor_name}">
									<img class="dashboard-avatar"
										style="border-width: 0px; width: 80px; height: 16px;"
										alt="${storageInfo.vendor_name}"
										title="${storageInfo.vendor_name}"
										src="${path}/resource/img/logo/${fn:toLowerCase(storageInfo.vendor_name)}.png">
								</c:if>
							</td>
						</tr>
						<tr>
							<th><h4>型号</h4></th>
							<td class="center">${storageInfo.model_name}</td>
						</tr>
						<tr>
							<th><h4>序列号</h4></th>
							<td class="center">${storageInfo.serial_number}</td>
						</tr>
						<tr>
							<th><h4>物理磁盘容量(G)</h4></th>
							<td class="center">
								<fmt:formatNumber value="${storageInfo.the_physical_disk_space}" pattern="0.00" />
							</td>
						</tr>
						<tr>
							<th><h4>池容量(G)</h4></th>
							<td class="center"><fmt:formatNumber value="${storageInfo.the_storage_pool_consumed_space}" pattern="0.00" /></td>
						</tr>
						<tr>
							<th>
								<h4>
									已分配卷容量(G)
								</h4>
							</th>
							<td class="center">
								<fmt:formatNumber value="${storageInfo.the_assigned_volume_space}" pattern="0.00" />
							</td>
						</tr>
						<tr>
							<th><h4>状态</h4></th>
							<td class="center">${storageInfo.the_propagated_status}</td>
						</tr>
						<tr>
							<th>
								<h4>
									硬件状态
								</h4>
							</th>
							<td class="center">
								${storageInfo.the_consolidated_status}
							</td>
						</tr>
						<%--<c:if test="${nasInfo!=null}">
				<tr>
					<th><h4>控制器工作状态</h4></th>
					<td class="center">${nasInfo.controller_status}</td>
				</tr>
				<tr>
					<th><h4>电池工作状态</h4></th>
					<td class="center">${nasInfo.battery_status}</td>
				</tr>
				<tr>
					<th><h4>电源工作状态</h4></th>
					<td class="center">${nasInfo.power_status}</td>
				</tr>
				<tr>
					<th><h4>磁盘工作状态</h4></th>
					<td class="center">${nasInfo.disk_status}</td>
				</tr>
				</c:if>
			  --%>
					</tbody>
				</table>
				<table class="table configTable"
					style="margin-bottom: 0px; width: 49%; float: right;">
					<tbody>
						<tr>
							<th>
								<h4>
									系统类型
								</h4>
							</th>
							<td class="center">
								${storageInfo.the_os_type}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									IP地址
								</h4>
							</th>
							<td class="center">
								${storageInfo.ip_address}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									微码版本
								</h4>
							</th>
							<td class="center">
								${storageInfo.code_level}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									缓存
								</h4>
							</th>
							<td class="center">
								${storageInfo.cache}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									卷总容量(G)
								</h4>
							</th>
							<td class="center">
								<fmt:formatNumber value="${storageInfo.the_volume_space}"
									pattern="0.00" />
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									可用池空间(G)
								</h4>
							</th>
							<td class="center">
								<fmt:formatNumber
									value="${storageInfo.the_storage_pool_available_space}"
									pattern="0.00" />
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									未分配卷容量(G)
								</h4>
							</th>
							<td class="center">
								<fmt:formatNumber
									value="${storageInfo.the_unassigned_volume_space}"
									pattern="0.00" />
							</td>
						</tr>

						<tr>
							<th>
								<h4>
									运行状态
								</h4>
							</th>
							<td class="center">
								${storageInfo.the_operational_status}
							</td>
						</tr>
						<%--<c:if test="${nasInfo!=null}">
						<tr>
							<th><h4>接口卡工作状态</h4></th>
							<td class="center">${nasInfo.hea_status}</td>
						</tr>
						<tr>
							<th><h4>磁盘空间使用率</h4></th>
							<td class="center">${nasInfo.network_status}</td>
						</tr>
						<tr>
							<th><h4>网络连通性</h4></th>
							<td class="center">${nasInfo.disk_group_space_prct}</td>
						</tr>
						</c:if>
					  --%>
					</tbody>
				</table>
			</div>
			<div style="clear: both;"></div>
		</div>
		<!--	</div>
	 存储系统详细信息表单结束 -->
		<!-- 性能开始 -->
		<div class="tab-pane" id="prfTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2 id="pTitle">
							性能
						</h2>
						<div class="box-icon">
		<%--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf3('${subSystemID}','3')" data-rel='tooltip'><i class="icon-cog"></i></a>--%>
							<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
							<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div id="queryPage" class="box-content" style="height: 220px;"></div>
					<div class="box-content" style="margin-top: -60px;">
						<!-- tab切换标签开始 -->
						<ul class="nav nav-tabs" id="myTab">
							<li class="active"><a href="#loadcontent2">性能曲线</a></li>
							<li class=""><a href="#dataContent2">性能数据</a></li>
						</ul>
						<!-- tab切换标签结束 -->
						<div id="perfChart2" class="tab-content"
							style="overflow: visible; min-height: 200px;">
							<!-- 性能曲线切换页开始 -->
							<div class="tab-pane active" id="loadcontent2">
								<div id="prfContent2" style="width: 100%; max-height: 350px;"></div>
							</div>
							<!-- 性能曲线切换页结束 -->
							<!-- 性能数据切换页开始 -->
							<div class="tab-pane" id="dataContent2"
								style="padding-top: 10px;">
								<table class="table table-bordered table-striped table-condensed" id="conTable">
									<thead>
										<c:choose>
											<c:when test="${not empty prfData}">
												<tr>
													<c:forEach var="head" items="${prfData.thead}">
														<c:choose>
															<c:when test="${head.key=='ele_name'}">
																<th>${head.value}</th>
															</c:when>
															<c:when test="${head.key=='prf_timestamp'}">
																<th>${head.value}</th>
															</c:when>
															<c:otherwise>
																<th>${head.value}</th>
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
												<c:forEach var="item" items="${prfData.tbody.data}"
													varStatus="status">
													<tr>
														<c:forEach var="thead" items="${prfData.thead}">
															<td>
																<c:choose>
																	<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">
																		${item.ele_name}
																	</c:when>
																	<c:when
																		test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																		<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
																	</c:when>
																	<c:otherwise>
																		<c:if test="${prfData.threshold==1}">
																			<span style="${item[fn:toLowerCase(thead.key)] >=prfData.threvalue?'color:red':''}"><fmt:formatNumber
																					value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00" />
																			</span>
																		</c:if>
																		<c:if test="${prfData.threshold==0}">
																			<fmtNumber:formate value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00" />
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
													<td>暂无数据！</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="StorageInfopageNub" class="pagination pagination-centered"></div>
								<c:choose>
									<c:when test="${(not empty prfData) and (not empty prfData.tbody) and (not empty prfData.tbody.data) and fn:length(prfData.tbody.data) > 0}">
										<script>
											$(function(){
												$("#StorageInfopageNub").getLinkStr({
													pagecount:"${prfData.tbody.totalPages}",
													curpage:"${prfData.tbody.currentPage}",
													numPerPage:"${prfData.tbody.numPerPage}",
													isShowJump:true,
													ajaxRequestPath:"${path}/servlet/storage/StorageAction?func=StoragePrfPage&subSystemID=${subSystemID}&level=3&tablePage=1",
													divId:'dataContent2'});
												var $exportCSV = $("#exportCSV");
												$exportCSV.unbind();
				  								$exportCSV.attr("href", "${path}/servlet/storage/StorageAction?func=exportPrefData&subSystemID=${subSystemID}&level=3");
											
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
							<!-- 性能数据切换页结束 -->
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 性能结束 -->
		<!-- 事件开始 -->
		<div class="tab-pane" id="alertTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2 id="pTitle">
							事件
						</h2>
						<div class="box-icon">
							<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${subSystemID}','${subSystemID}','Storage');"><i class="icon-color icon-ok"></i> </a>
							<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${subSystemID}','${subSystemID}','Storage');"><i class="icon icon-color icon-trash"></i> </a>
							<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${subSystemID}','${subSystemID}','Storage');"><i class="icon icon-color icon-refresh"></i> </a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
						</div>
					</div>
					<div class="box-content" id="dAlertContent">
						<table
							class="table table-bordered table-striped table-condensed spetable"
							style="table-layout: fixed;">
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
									<th style="width: 55px;">
										类型
									</th>
									<th style="width: 55px;">
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
											<tr style="cursor: pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Storage')">

												<td>
													<label class="checkbox inline">
														<input type="checkbox" value="${item.fruleid}_${item.ftopid}_${item.flogtype}" name="dAlertCheck">
													</label>
												</td>
												<td>
													<fmt:formatDate value="${item.ffirsttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss" />
												</td>
												<td>
													<fmt:formatDate value="${item.flasttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss" />
												</td>
												<td>
													<c:choose>
														<c:when test="${item.flogtype == 3}">硬件告警</c:when>
														<c:when test="${item.flogtype == 2}">阀值告警</c:when>
														<c:when test="${item.flogtype == 1}">TPC告警</c:when>
														<c:when test="${item.flogtype == 0}">系统告警</c:when>
													</c:choose>
												</td>
												<td>${item.fcount}</td>
												<td>
													<c:choose>
														<c:when test="${item.fstate == 0}"> <i class="icon icon-color icon-close"></i>未确认</c:when>
														<c:when test="${item.fstate == 1}"> <i class="icon icon-green icon-bookmark"></i>已确认</c:when>
														<c:when test="${item.fstate == 2}"> <i class="icon icon-orange icon-cancel"></i>已清除</c:when>
														<c:when test="${item.fstate == 3}"> <i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
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
													<a href="javascript:goToEventDetailPage('${item.ftopid}','${item.ftoptype}','${item.fresourceid}')">${item.fresourcename}</a>
												</td>
												<td>
													<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','${item.fresourcetype}')" data-placement="left" data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename}<br/>
														<c:choose>
															<c:when test="${fn:length(item.fdetail) > 200}">
																<c:out value="${fn:substring(item.fdetail, 0, 200)}......" />
															</c:when> 
															<c:otherwise>
																<c:out value="${item.fdetail}" />
															</c:otherwise>
														</c:choose>"
														title="详细信息"> ${item.fdescript}</a>
												</td>
											</tr>
										</c:forEach>
									</c:when>
								<c:otherwise><tr><td colspan=9>暂无数据！</td></tr></c:otherwise>
							</c:choose>
						</tbody>
					</table>
						<div class="pagination pagination-centered"><ul id="alertListNub"></ul></div>
						<c:if test="${not empty deviceLogPage.data}">
							<script type="text/javascript">
								$("#alertListNub").getLinkStr({
									pagecount:"${deviceLogPage.totalPages}",
									curpage:"${deviceLogPage.currentPage}",
									numPerPage:"${deviceLogPage.numPerPage}",
									isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${subSystemID}&topId=${subSystemID}&resourceType=Storage&level=${level}&state=${state}",
									divId: "dAlertContent"
								});
							</script>
						</c:if>
					</div>
				</div>
			</div>
		</div>
		<!-- 事件结束 -->
		<!-- 部件开始 -->
		<div class="tab-pane" id="dataTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2>
							部件
						</h2>
						<div class="box-icon">
							<a id='subFreshen' href="javascript:void(0)"
								class="btn btn-round" title="刷新" data-rel='tooltip'><i
								class="icon icon-color icon-refresh"></i>
							</a>
							<%--  <a id='subShowlist' href="javascript:void(0)" class="btn btn-round" title="查看所有" data-rel='tooltip'><i class="icon icon-color icon-book"></i></a>--%>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i
								class="icon-chevron-up"></i>
							</a>
						</div>
					</div>
					<div class="box-content" style="max-height: 810px;" id="subTab">
						<ul class="nav nav-tabs" id="myTab">
							<li class="active" title="port">
								<a href="#portContent">端口(${portCount})</a>
							</li>
							<li class="" title="disk">
								<a href="#diskContent">磁盘(${diskCount})</a>
							</li>
							<li class="" title="pool">
								<a href="#poolContent">存储池(${poolCount})</a>
							</li>
							<li class="" title="volume">
								<a href="#volumeContent">卷(${volumeCount})</a>
							</li>
							<li class="" title="extent">
								<a href="#extendContent">存储扩展(${extendCount})</a>
							</li>
							<li class="" title="arraysite">
								<a href="#arraysiteContent">阵列(${arrayCount})</a>
							</li>
							<li class="" title="rank">
								<a href="#rankContent">Rank(${rankCount})</a>
							</li>
							<li class="" title="node">
								<!-- 冗余节点 -->
								<a href="#nodeContent">控制器(${nodeCount})</a>
							</li>
							<li class="" title="iogroup">
								<a href="#iogroupContent">IOGroup(${iogroupCount})</a>
							</li>
							<%--<li id="ctrlerContentTab" class="" title="controller">
									<a href="#controllerContent">控制器</a>
								</li>
							--%>
						</ul>
						<div id="perfChart" class="tab-content"
							style="overflow: visible; min-height: 200px;">
							<div class="tab-pane active" id="portContent"
								style="padding-top: 10px;">
								<table
									class="table table-bordered table-striped table-condensed">
									<thead>
										<tr>
											<th>名称</th>
											<th>端口号</th>
											<th>端口类型</th>
											<th>操作状态</th>
											<th>硬件状态</th>
											<th>端口速率(M)</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty portPage.data}">
												<c:forEach var="item" items="${portPage.data}"
													varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/port/PortAction?func=PortInfo&portId=${item.port_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
														</td>
														<td>${item.port_number}</td>
														<td>${item.the_type}</td>
														<td>${item.the_operational_status}</td>
														<td><cs:cstatus value="${item.the_consolidated_status}" /></td>
														<td>${item.the_port_speed}</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr><td colspan=7>暂无数据！</td></tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="portListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty portPage.data}">
									<script type="text/javascript">
										var param = $("#conditionForm").serialize();
										$("#portListpageNub").getLinkStr({
											pagecount:"${portPage.totalPages}",
											curpage:"${portPage.currentPage}",
											numPerPage:"${portPage.numPerPage}",
											isShowJump:true,
											ajaxRequestPath:"${path}/servlet/port/PortAction?func=AjaxPortPage&subSystemID=${subSystemID}&"+param,
											divId: "portContent"
										});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="diskContent" style="padding-top: 10px;">
								<table
									class="table table-bordered table-striped table-condensed">
									<thead>
										<tr>
											<th>
												名称
											</th>
											<th>
												容量(G)
											</th>
											<th>
												转速
											</th>
											<th>
												运行状态
											</th>
											<th>
												阵列
											</th>
											<th>
												厂商
											</th>
											<th>
												型号
											</th>
											<th>
												序列号
											</th>
											<th>
												固件版本
											</th>
											<th>
												硬件状态
											</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty diskPage.data}">
												<c:forEach var="item" items="${diskPage.data}"
													varStatus="status">
													<tr>
														<td>${item.the_display_name}</td>
														<td><fmt:formatNumber value="${item.the_capacity}" pattern="0.00" /></td>
														<td>${item.speed}
														</td>
														<td>${item.the_operational_status}</td>
														<td><a title="${item.diskgroup_name}" href="${path}/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=${subSystemID}&arraysiteId=${item.the_arraysite_id}">${item.diskgroup_name}</a></td>
														<td>${item.vendor_name}</td>
														<td>
															${item.model_name}
														</td>
														<td>
															${item.serial_number}
														</td>
														<td>
															${item.firmware_rev}
														</td>
														<td>
															<cs:cstatus value="${item.the_consolidated_status}" />
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=11>暂无数据！</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="diskListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty diskPage.data}">
									<script>
										$("#diskListpageNub").getLinkStr({
											pagecount:"${diskPage.totalPages}",
											curpage:"${diskPage.currentPage}",
											numPerPage:"${diskPage.numPerPage}",
											isShowJump:true,
											ajaxRequestPath:"${path}/servlet/disk/DiskAction?func=AjaxDiskPage&subSystemID=${subSystemID}",
											divId: "diskContent"
										});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="poolContent" style="padding-top: 10px;">
								<table
									class="table table-bordered table-striped table-condensed">
									<thead>
										<tr>
											<th>
												名称
											</th>
											<th>
												容量(G)
											</th>
											<th>
												已用容量(G)
											</th>
											<th>
												可用容量(G)
											</th>
											<th>
												已分配容量(G)
											</th>
											<th>
												未分配容量(G)
											</th>
											<th>
												本地状态
											</th>
											<th>
												操作状态
											</th>
											<th>
												硬件状态
											</th>
											<th>
												冗余级别
											</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${(not empty poolPage) and (not empty poolPage.data)}">
												<c:forEach var="item" items="${poolPage.data}"
													varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
														</td>
														<td>
															<fmt:formatNumber value="${item.the_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_consumed_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_assigned_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_unassigned_space}" pattern="0.00" />
														</td>
														<td>
															${item.the_native_status}
														</td>
														<td>
															${item.the_operational_status}
														</td>
														<td>
															<cs:cstatus value="${item.the_consolidated_status}" />
														</td>
														<td>
															${item.raid_level}
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=11> 暂无数据！</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="poolListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty poolPage.data}">
									<script>
										$("#poolListpageNub").getLinkStr({
											pagecount:"${poolPage.totalPages}",
											curpage:"${poolPage.currentPage}",
											numPerPage:"${poolPage.numPerPage}",
											isShowJump:true,
											ajaxRequestPath:"${path}/servlet/pool/PoolAction?func=AjaxPoolPage&subSystemID=${subSystemID}",
											divId: "poolContent"
										});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="volumeContent" style="padding-top: 10px;">
								<table class="table table-bordered table-striped table-condensed" id="">
									<thead>
										<tr>
											<th>逻辑卷名</th>
											<th>状态</th>
											<th>容量(G)</th>
											<th>已用容量(G)</th>
											<th>冗余级别</th>
											<th>存储池</th>
											<th>唯一编号</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty volumePage.data}">
												<c:forEach var="item" items="${volumePage.data}" varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid=${item.svid}&subSystemID=${subSystemID}">${item.the_display_name}</a>
														</td>
														<td>
															<cs:cstatus value="${item.the_consolidated_status}" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_capacity}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_used_space}" pattern="0.00" />
														</td>
														<td>
															${item.the_redundancy}
														</td>
														<td>
															<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name}</a>
														</td>
														<td>
															${item.unique_id}
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=8>暂无数据！</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="volumeListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty volumePage.data}">
									<script>
										$("#volumeListpageNub").getLinkStr({
											pagecount: "${volumePage.totalPages}",
											curpage: "${volumePage.currentPage}",
											numPerPage: "${volumePage.numPerPage}",
											isShowJump: true,
											ajaxRequestPath: "${path}/servlet/volume/VolumeAction?func=AjaxVolumePage&greatLogical_Capacity=${greatLogical_Capacity}&lessLogical_Capacity=${lessLogical_Capacity}&name=${name}&subSystemID=${subSystemID}",
											divId: "volumeContent" 
										});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="extendContent" style="padding-top: 10px;">
								<table class="table table-bordered table-striped table-condensed">
									<thead>
										<tr>
											<th>名称</th>
											<th>扩展卷数</th>
											<th>扩展容量(G)</th>
											<th>总容量</th>
											<th>可用容量</th>
											<th>操作状态</th>
											<th>本地状态</th>
											<th>存储池</th>
											<th>设备ID</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty extendPage.data}">
												<c:forEach var="item" items="${extendPage.data}" varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/extend/ExtendAction?func=extendInfo&subSystemID=${subSystemID}&extendId=${item.storage_extent_id}">${item.the_display_name}</a>
														</td>
														<td>
															${item.the_extent_volume}
														</td>
														<td>
															<fmt:formatNumber value="${item.the_extent_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
														</td>
														<td>
															${item.the_operational_status}
														</td>
														<td>
															${item.the_native_status}
														</td>
														<td>
															<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name}</a>
														</td>
														<td>
															${item.device_id }
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=10> 暂无数据！ </td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="extendListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty extendPage.data}">
									<script>
										var param = $("#extendHiddenForm").serialize();
										$("#extendListpageNub").getLinkStr({
											pagecount:"${extendPage.totalPages}",
											curpage:"${extendPage.currentPage}",
											numPerPage:"${extendPage.numPerPage}",
											isShowJump:true, 
											ajaxRequestPath:"${path}/servlet/extend/ExtendAction?func=AjaxExtendPage&subSystemID=${subSystemID}&"+param,
											divId: "extendContent"
										});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="arraysiteContent"
								style="padding-top: 10px;">
								<table
									class="table table-bordered table-striped table-condensed">
									<thead>
										<tr>
											<th>
												名称
											</th>
											<th>
												Rank
											</th>
											<th>
												存储池
											</th>
											<th>
												冗余级别
											</th>
											<%--<th>
										控制器工作状态
									</th>
									<th>
										电池工作状态
									</th>
									<th>
										电源工作状态
									</th>
									<th>
										磁盘工作状态
									</th>
									<th>
										接口卡工作状态
									</th>
									<th>
										盘柜状态
									</th>
									<th>
										光纤模块状态
									</th>
									--%>
											<th>
												描述
											</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty arraysitePage.data}">
												<c:forEach var="item" items="${arraysitePage.data}"
													varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=${subSystemID}&arraysiteId=${item.disk_group_id}">${item.the_display_name}</a>
														</td>
														<td>
															<a title="${item.rank_name}" href="${path}/servlet/rank/RankAction?func=RankInfo&rankId=${item.storage_extent_id}&subSystemID=${subSystemID}">${item.rank_name}</a>
														</td>
														<td>
															<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name}</a>
														</td>
														<td>
															${item.raid_level}
														</td>
														<%--<td>
													${item.controller_status}
												</td>
												<td>
													${item.battery_status}
												</td>
												<td>
													${item.power_status}
												</td>
												<td>
													${item.disk_status}
												</td>
												<td>
													${item.hea_status}
												</td>
												<td>
													${item.disk_enclosure_status}
												</td>
												<td>
													${item.fiber_status}
												</td>									
												--%>
														<td>
															${item.description}
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=6>暂无数据！</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="arraysiteListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty arraysitePage.data}">
									<script type="text/javascript">
										$("#arraysiteListpageNub").getLinkStr({
											pagecount:"${arraysitePage.totalPages}",
											curpage:"${arraysitePage.currentPage}",
											numPerPage:"${arraysitePage.numPerPage}",
											isShowJump:true,
											ajaxRequestPath:"${path}/servlet/arraysite/ArraysiteAction?func=AjaxArraysitePage&subSystemID=${subSystemID}",
											divId: "arraysiteContent"});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="rankContent" style="padding-top: 10px;">
								<table
									class="table table-bordered table-striped table-condensed"
									id="conTable">
									<thead>
										<tr>
											<th>
												名称
											</th>
											<th>
												扩展卷数
											</th>
											<th>
												总容量(G)
											</th>
											<th>
												已用容量(G)
											</th>
											<th>
												空闲容量(G)
											</th>
											<th>
												存储池
											</th>
											<th>
												状态
											</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty rankPage.data}">
												<c:forEach var="item" items="${rankPage.data}" varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/rank/RankAction?func=RankInfo&subSystemID=${subSystemID}&rankId=${item.storage_extent_id}">${item.the_display_name}</a>
														</td>
														<td>
															${item.the_extent_volume}
														</td>
														<td>
															<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_used_space}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
														</td>
														<td>
															<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name}</a>
														</td>
														<td>
															${item.the_operational_status}
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=8>暂无数据！</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="rankListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty rankPage.data}">
									<script type="text/javascript">
										$("#rankListpageNub").getLinkStr({
											pagecount:"${rankPage.totalPages}",
											curpage:"${rankPage.currentPage}",
											numPerPage:"${rankPage.numPerPage}",
											isShowJump:true,
											ajaxRequestPath:"${path}/servlet/rank/RankAction?func=AjaxRankPage&subSystemID=${subSystemID}",
											divId: "rankContent"
										});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="controllerContent" style="padding-top: 0px;">
								<div class="box span12" style="height: 820px;">
									<div class="box-header well">
										<h2>
											控制器
										</h2>
										<div class="box-icon">
											<a href="javascript:void(0);" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf('${subSystemID}',2,'')"><i class="icon-cog"></i></a>
											<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doListRefresh()"><i class="icon icon-color icon-refresh"></i></a>
											<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportPrfCSV"><i class="icon-download-alt"></i></a>
										</div>
									</div>
									<div class="box-content" style="height: 220px;">
										<div id="queryPage2" class="box-content"></div>
									</div>
									<div id="queryTab" class="box-content"
										style="max-height: 460px;">
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
										<div id="perfChart" class="tab-content"
											style="min-height: 100px;">
											<!-- 性能曲线切换页开始 -->
											<div class="tab-pane active" id="loadcontent">
												<div id="prfContent2816"
													style="width: 94%; margin: 0px; min-height: 100px; height: 460px;"></div>
											</div>
											<!-- 性能曲线切换页结束 -->
											<!-- 性能数据切换页开始 -->
											<div class="tab-pane" id="dataContent">
												<table
													class="table table-bordered table-striped table-condensed"
													id="conTable">
													<thead>
														<c:choose>
															<c:when test="${not empty conPrfData}">
																<tr>
																	<c:forEach var="head" items="${conPrfData.thead}">
																		<c:choose>
																			<c:when test="${head.key=='ele_name'}">
																				<th>
																					${head.value}
																				</th>
																			</c:when>
																			<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																				<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
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
															<c:when test="${not empty conPrfData}">
																<c:forEach var="item" items="${conPrfData.tbody.data}"
																	varStatus="status">
																	<tr>
																		<c:forEach var="thead" items="${conPrfData.thead}">
																			<td>
																				<c:choose>
																					<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">${item.ele_name}</c:when>
																					<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																						<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
																					</c:when>
																					<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																						<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
																					</c:when>
																					<c:otherwise>
																						<c:if test="${conPrfData.threshold == 1}">
																							<span style="${item[fn:toLowerCase(thead.key)] >=conPrfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
																						</c:if>
																						<c:if test="${conPrfData.threshold==0}">
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
																<tr><td colspan="3">暂无数据！</td></tr>
															</c:otherwise>
														</c:choose>
													</tbody>
												</table>
												<div id="conpageNub" class="pagination pagination-centered"></div>
												<c:if test="${not empty conPrfData}">
													<script type="text/javascript">
														$("#conpageNub").getLinkStr({
															pagecount:"${conPrfData.tbody.totalPages}",
															curpage:"${conPrfData.tbody.currentPage}",
															numPerPage:"${conPrfData.tbody.numPerPage}",
															isShowJump:true,
															ajaxRequestPath:"${path}/servlet/controller/ConAction?func=ConPrfPage&tablePage=1&subSystemID=${subSystemID}",
															divId: "dataContent"
														});
														$("#exportPrfCSV").unbind();
														var exurl = "${path}/servlet/controller/ConAction?func=exportPrefData&subSystemID=${subSystemID}";
						  								$("#exportPrfCSV").attr("href", exurl);
													</script>
												</c:if>
												<c:if test="${empty conPrfData}">
													<script type="text/javascript">
														$("#exportPrfCSV").unbind();
														$("#exportPrfCSV").attr("href", "javascript:void(0);");
														$("#exportPrfCSV").bind("click", function(){ bAlert("暂无可导出数据！"); });
													</script>
												</c:if>
											</div>
											<!-- 性能数据切换页结束 -->
										</div>
									</div>
								</div>
							</div>

							<div class="tab-pane" id="nodeContent" style="padding-top: 10px;">
								<table
									class="table table-bordered table-striped table-condensed"
									style="word-break: break-all">
									<thead>
										<tr>
											<th>
												名称
											</th>
											<th>
												组件ID
											</th>
											<th>
												IP地址
											</th>
											<th>
												IO Group
											</th>
											<th>
												后端名称
											</th>
											<th>
												操作状态
											</th>
											<th>
												WWN
											</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty nodePage.data}">
												<c:forEach var="item" items="${nodePage.data}" varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/node/NodeAction?func=NodeInfo&nodeId=${item.redundancy_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
														</td>
														<td>
															${item.component_id}
														</td>
														<td>
															${item.ip_address}
														</td>
														<td>
															<a title="${item.iogroup_name}" href="${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId=${item.io_group_id}">${item.iogroup_name}</a>
														</td>
														<td>
															${item.the_backend_name}
														</td>
														<td>
															${item.the_operational_status}
														</td>
														<td>
															${item.wwn}
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan=8> 暂无数据！ </td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="nodeListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty nodePage.data}">
									<script>
										var param = $("#conditionForm").serialize();
										$("#nodeListpageNub").getLinkStr({
											pagecount:"${nodePage.totalPages}",
											curpage:"${nodePage.currentPage}",
											numPerPage:"${nodePage.numPerPage}",
											isShowJump:true,
											ajaxRequestPath:"${path}/servlet/node/NodeAction?func=AjaxNodePage&subSystemID=${subSystemID}&"+param,
											divId: "nodeContent"
										});
									</script>
								</c:if>
							</div>
							<div class="tab-pane" id="iogroupContent" style="padding-top: 10px;">
								<table class="table table-bordered table-striped table-condensed">
									<thead>
										<tr>
											<th>
												名称
											</th>
											<th>
												镜像内存(G)
											</th>
											<th>
												镜像空闲内存(G)
											</th>
											<th>
												快照内存(G)
											</th>
											<th>
												快照空闲内存(G)
											</th>
											<th>
												阵列内存(G)
											</th>
											<th>
												阵列空闲内存(G)
											</th>
											<th>
												维护状态
											</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${not empty iogroupPage.data}">
												<c:forEach var="item" items="${iogroupPage.data}" varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId=${item.io_group_id}">${item.the_display_name}</a>
														</td>
														<td>
															<fmt:formatNumber value="${item.mirroring_total_memory/1024.0}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.mirroring_free_memory/1024.0}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.flash_copy_total_memory/1024.0}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.flash_copy_free_memory/1024.0}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.raid_total_memory/1024.0}" pattern="0.00" />
														</td>
														<td>
															<fmt:formatNumber value="${item.raid_free_memory/1024.0}" pattern="0.00" />
														</td>
														<td>
															${item.maintenance}
														</td>
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr><td colspan=8>暂无数据！</td></tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="iogroupListpageNub" class="pagination pagination-centered"></div>
								<c:if test="${not empty iogroupPage.data}">
									<script>
										var param = $("#conditionForm").serialize();
										$("#iogroupListpageNub").getLinkStr({
											pagecount:"${iogroupPage.totalPages}",
											curpage:"${iogroupPage.currentPage}",
											numPerPage:"${iogroupPage.numPerPage}",
											isShowJump:true,
											ajaxRequestPath:"${path}/servlet/iogroup/IogroupAction?func=AjaxIogroupPage&subSystemID=${subSystemID}&"+param,
											divId: "iogroupContent"
										});
									</script>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 部件结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>