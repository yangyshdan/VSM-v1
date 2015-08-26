<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/switch.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/deviceAlert.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<!-- pandect action -->
<script src="${path}/resource/js/pandect/warnings.js"></script>
<!-- pandect end -->
<script type="text/javascript">
	var switchId = "${switchId}";
	function portDbClick(id) { //端口层双击事件
		window.location = "${path}/servlet/switchport/SwitchportAction?func=PortInfo&portId="
				+ id + "&switchId=${switchId}&r=" + Math.random();
	}
	function portFreshen() { //端口层刷新
		var jsonVal = {
			switchId : "${switchId}"
		};
		loadData("${path}/servlet/switchport/SwitchportAction?func=AjaxPortPage", jsonVal, $("#portContent"));
	}
	function portList() { //端口查看所有
		window.location = "${path}/servlet/switchport/SwitchportAction?func=PortPage&switchId=${switchId}&r=" + Math.random();
	}

$(function() {
	Public.drawPie("warnings", getAttribute(${report}));
	Public.drawPrfLine("prfContent22", getAttribute(${PortRateData}));
	Public.drawTopn02("ports", {
		jsonVal: getAttribute(${PortdataTopNData}), 
		getURL: function(point){
			return "/servlet/switchport/SwitchportAction?func=PortInfo&portId=" + point.eleid + "&switchId=" + point.devid;
		}
	});
<%--增加查询条件--%>
	$.ajax({
		url : "${path}/servlet/switchs/SwitchAction?func=SwitchSettingPrf2",
		data : {
			switchId : "${switchId}",
			level : 3
		},
		type : "post",
		dataType : "html",
		success : function(result) {
			$("#queryPage").html(result);
			$("#devtypeAndDevice").hide();
		}
	});
	doListRefresh();
});

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
	else if(tab == 4){
		$("#content ul#myTab a[href='#dataTab']").parent().addClass("active").siblings().removeClass("active");
		$("#dataTab").addClass("active").siblings().removeClass("active");
	}
});
	
function doListRefresh() {
	loadData("${path}/servlet/switchs/SwitchAction?func=SwitchPrfPage", {
		switchId : switchId,
		level : 3
	}, $("#perfChart2"));
}
	
function doPerfLineFilter(timeRange) {
	loadData("${path}/servlet/switchs/SwitchAction?func=DrawPerfLine", {timeRange: timeRange, switchId:"${switchId}"}, $("#perfChart"));
}


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
		<a href="#"> <img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/switch.png" alt="StorageSystem"></a>
		<span style="font-size: 25px;">${switchInfo.the_display_name}</span>
		<br>
		<strong>IP:</strong>
		<span>
			<a title="${switchInfo.ip_address}" href="http://${switchInfo.ip_address}" target="_blank">${switchInfo.ip_address}</a>
		</span>
		<strong style="margin-left: 20px;">Status:</strong>
		<span>${switchInfo.the_operational_status}</span>
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
			<a href="#dataTab">端口</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
		<!--总览开始  -->
		<div class="tab-pane active" id="overViewTab">
			<div class="row-fluid">
				<div class="box span12" style="height: 640px;">
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
					<!-- 基本信息开始-->
					<div id="prefContent">
						<div id="charts_2_1" class="box span4"
							style="margin: 5px 15px 0px 15px; width: 48%;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									基本信息
								</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix"
									style="height: 250px;" data-highcharts-chart="2">
									<div
										style="position: relative; overflow: hidden; height: 250px; left: 0px; top: 0px;"
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
													<td><h4>厂商</h4></td>
													<td class="center">${switchInfo.vendor_name}</td>
												</tr>
												<tr>
													<td><h4>版本</h4></td>
													<td class="center">${switchInfo.version}</td>
												</tr>
												<tr>
													<td><h4>管理地址</h4></td>
													<td class="center">${switchInfo.mgmt_url_addr}</td>
												</tr>
												<tr>
													<td><h4>WWN</h4></td>
													<td class="center">${switchInfo.switch_wwn}</td>
												</tr>
												<tr>
													<td><h4>状态</h4></td>
													<td class="center">${switchInfo.the_propagated_status}</td>
												</tr>
												<tr>
													<td>
														<h4>操作状态</h4>
													</td>
													<td class="center">${switchInfo.the_operational_status}</td>
												</tr>
												<tr>
													<td><h4>IP地址</h4></td>
													<td class="center">${switchInfo.ip_address}</td>
												</tr>
												<tr>
													<td>
														<h4>型号</h4>
													</td>
													<td class="center">${switchInfo.model_name}</td>
												</tr>
												<tr>
													<td><h4>端口总数</h4></td>
													<td class="center">${portCount}</td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- 基本信息结束 -->
					<!-- 事件开始-->
					<div id="prefContent">
						<div id="charts_2_1" class="box span4" style="margin: 0px 5px 0px 0px; width: 48%;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">事件情况(%)</h2>
							</div>
							<div id="box_content_2_1" class="box-content" style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
									<div id="warnings" style="position: relative; overflow: hidden; width:90%; height: 250px; top: 0px;" class="highcharts-container"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 事件结束 -->
					<!--性能曲线开始  -->
					<div id="perfChart" style="height: 300px;">
						<div id="charts_2_1" class="box span4"
							style="margin: 5px 15px 0px 15px; width: 48%; height:293px;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									性能曲线(Total Port Date Rate)
								</h2>
							</div>
							<div class="box-content" id="loadcontent" style="float: left; width:98%; height: 250px;">
								<div id="prfContent22" style="width: 98%; height: 250px;"></div>
							</div>
						</div>
						<!--性能曲线结束  -->
						<!--Total Port Data Rate action -->
						<div id="charts_2_1" class="box span4"
							style="margin: 5px 5px 0px 0px; width: 48%; height:293px;">
							<div class="box-header well">
								<h2 style="width: 70%; height: 20px; overflow: hidden;">
									Total Port Data Rate Top5
								</h2>
							</div>
							<div id="box_content_2_1" class="box-content"
								style="height: 250px;">
								<div id="perfContent_2_1" class="clearfix"
									style="height: 250px;" data-highcharts-chart="2">
									<div id="ports"
										style="position: relative; overflow: hidden; width:98%; height: 250px; left: 0px; top: 0px; text-align: center;"
										class="highcharts-container"></div>
								</div>
							</div>
						</div>
					</div>
					<!--Total Port Date Rate end -->
				</div>
			</div>
		</div>
		<!--总览结束 -->
		<div class="tab-pane" id="detailTab">
			<!-- 详细信息开始 -->
			<!-- 存储系统详细信息表单开始  -->
			<div class="box-content" style="width: 98%; padding-top: 10px;">
				<table class="table configTable"
					style="margin-bottom: 0px; width: 49%; float: left;">
					<tbody>
						<tr>
							<th>
								<h4>厂商</h4>
							</th>
							<td class="center">
								<c:if test="${empty switchInfo.vendor_name}">"N/A"</c:if>
								<c:if test="${not empty switchInfo.vendor_name}">
									<img class="dashboard-avatar" style="border-width:0px;width:80px;height:16px;" title="${switchInfo.vendor_name}" src="${path}/resource/img/logo/${fn:toLowerCase(switchInfo.vendor_name)}.png">
								</c:if>
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									版本
								</h4>
							</th>
							<td class="center">
								${switchInfo.version}
							</td>
						</tr>
						<tr>
							<th>
								<h4>管理地址</h4>
							</th>
							<td class="center">
								${switchInfo.mgmt_url_addr}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									WWN
								</h4>
							</th>
							<td class="center">
								${switchInfo.switch_wwn}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									状态
								</h4>
							</th>
							<td class="center">
								${switchInfo.the_propagated_status}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									操作状态
								</h4>
							</th>
							<td class="center">
								${switchInfo.the_operational_status}
							</td>
						</tr>
						<%--<tr>
					<th><h4>引擎工作状态</h4></th>
					<td class="center">${resswitchInfo.engine_status}</td>
				</tr>
				<tr>
					<th><h4>电源工作状态</h4></th>
					<td class="center">${resswitchInfo.power_status}</td>
				</tr>
				--%>
						<tr>
							<th>
								<h4>
									描述
								</h4>
							</th>
							<td class="center">
								${switchInfo.description}
							</td>
						</tr>
					</tbody>
				</table>
				<table class="table configTable"
					style="margin-bottom: 0px; width: 49%; float: left;">
					<tbody>
						<tr>
							<th>
								<h4>
									IP地址
								</h4>
							</th>
							<td class="center">
								${switchInfo.ip_address}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									型号
								</h4>
							</th>
							<td class="center">
								${switchInfo.model_name}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									序列号
								</h4>
							</th>
							<td class="center">
								${switchInfo.serial_number}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									是否虚拟
								</h4>
							</th>
							<td class="center">
								${switchInfo.the_virtual==0?"否":是}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									Zone ID
								</h4>
							</th>
							<td class="center">
								${switchInfo.domain}
							</td>
						</tr>
						<tr>
							<th>
								<h4>
									硬件状态
								</h4>
							</th>
							<td class="center">
								${switchInfo.the_consolidated_status}
							</td>
						</tr>
						<%--<tr>
					<th><h4>端口工作状态</h4></th>
					<td class="center">${resswitchInfo.port_status}</td>
				</tr>
				<tr>
					<th><h4>光纤模块状态</h4></th>
					<td class="center">${resswitchInfo.fiber_status}</td>
				</tr>
				--%>
						<tr>
							<th>
								<h4>
									更新时间
								</h4>
							</th>
							<td class="center">
								${switchInfo.update_timestamp}
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- 存储系统详细信息表单结束 -->
			<div style="clear: both;"></div>
		</div>
		<!-- 详细信息结束 -->
		<!-- 性能开始 -->
		<div class="tab-pane" id="prfTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2 id="pTitle">
							交换机性能
						</h2>
						<div class="box-icon">
							<!--
						<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Switch.settingPrf('${switchId}','3')" data-rel="tooltip"><i class="icon-cog"></i></a>
					-->
							<a href="javascript:void(0)" class="btn btn-round btn-round"
								title="刷新" onclick="doListRefresh();" data-rel="tooltip">
								<i class="icon icon-color icon-refresh"></i>
							</a>
							<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel="tooltip"></i></a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i
								class="icon-chevron-up"></i>
							</a>
						</div>
					</div>
					<div id="queryPage" class="box-content"></div>
					<div class="box-content">
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
						<div id="perfChart2" class="tab-content"
							style="overflow: visible;">
							<!-- 性能曲线切换页开始 -->
							<div class="tab-pane active" id="loadcontent2">
								<div id="prfContent2" style="width: 95%; height: 350px;"></div>
							</div>
							<!-- 性能曲线切换页结束 -->
							<!-- 性能数据切换页开始 -->
							<div class="tab-pane" id="dataContent2">
								<table
									class="table table-bordered table-striped table-condensed"
									id="conTable">
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
																		<formateTime:formate
																			value="${item.prf_timestamp.time}"
																			pattern="yyyy-MM-dd HH:mm:ss" />
																	</c:when>
																	<c:otherwise>
																		<c:if test="${prfData.threshold==1}">
																			<span
																				style="${item[fn:toLowerCase(thead.key)] >=prfData.threvalue?'color:red':''}"><fmt:formatNumber
																					value="${item[fn:toLowerCase(thead.key)]}"
																					pattern="0.00" />
																			</span>
																		</c:if>
																		<c:if test="${prfData.threshold==0}">
																			<fmt:formatNumber
																				value="${item[fn:toLowerCase(thead.key)]}"
																				pattern="0.00" />
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
								<div id="SwitchInfopageNub"
									class="pagination pagination-centered"></div>
								<c:if test="${not empty prfData}">
									<script>
										$("#SwitchInfopageNub")
												.getLinkStr(
														{
															pagecount : "${prfData.tbody.totalPages}",
															curpage : "${prfData.tbody.currentPage}",
															numPerPage : "${prfData.tbody.numPerPage}",
															isShowJump : true,
															ajaxRequestPath : "${path}/servlet/switchs/SwitchAction?func=SwitchPrfPage&switchId=${switchId}&level=3&tablePage=1",
															divId : 'dataContent2'
														});
										$("#exportCSV").unbind();
										var exurl = "${path}/servlet/switchs/SwitchAction?func=exportPrefData&switchId=${switchInfo.switch_id}&level=3";
										$("#exportCSV").attr("href", exurl);
									</script>
								</c:if>
								<c:if test="${empty prfData}">
									<script>
										$("#exportCSV").unbind();
										$("#exportCSV").attr("href", "javascript:void(0);");
										$("#exportCSV").bind("click", function() {
											bAlert("暂无可导出数据！")
										});
									</script>
								</c:if>
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
							事件预警
						</h2>
						<div class="box-icon">
							<a href="javascript:void(0);" class="btn btn-round" title="确认"
								data-rel="tooltip"
								onclick="DeviceAlert.doAlertDone('${switchId}','${switchId}','Switch');"><i
								class="icon-color icon-ok"></i> </a>
							<a href="javascript:void(0);" class="btn btn-round" title="删除"
								onclick="DeviceAlert.doAlertDel('${switchId}','${switchId}','Switch');"><i
								class="icon icon-color icon-trash"></i> </a>
							<a href="javascript:void(0);" class="btn btn-round" title="刷新"
								onclick="DeviceAlert.doFreshen('${switchId}','${switchId}','Switch');"><i
								class="icon icon-color icon-refresh"></i> </a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i
								class="icon-chevron-up"></i>
							</a>
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
										<c:forEach var="item" items="${deviceLogPage.data}"
											varStatus="status">
											<tr style="cursor: pointer;"
												ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Switch')">
												<td>
													<label class="checkbox inline">
														<input type="checkbox"
															value="${item.fruleid}_${item.ftopid}_${item.flogtype}"
															name="dAlertCheck">
													</label>
												</td>
												<td>
													<fmt:formatDate value="${item.ffirsttime}" type="date"
														pattern="yyyy-MM-dd HH:mm:ss" />
												</td>
												<td>
													<fmt:formatDate value="${item.flasttime}" type="date"
														pattern="yyyy-MM-dd HH:mm:ss" />
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
														<c:when test="${item.fstate == 0}">
															<i class="icon icon-color icon-close"></i>未确认</c:when>
														<c:when test="${item.fstate == 1}">
															<i class="icon icon-green icon-bookmark"></i>已确认</c:when>
														<c:when test="${item.fstate == 2}">
															<i class="icon icon-orange icon-cancel"></i>已清除</c:when>
														<c:when test="${item.fstate == 3}">
															<i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
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
													<a
														href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Switch')"
														data-placement="left" data-rel="popover"
														data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename} <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
      <c:out value="${fn:substring(item.fdetail, 0, 200)}......" /></c:when> <c:otherwise><c:out value="${item.fdetail}" /></c:otherwise></c:choose>"
														title="详细信息"> ${item.fdescript} </a>

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
								$("#alertListNub").getLinkStr({
													pagecount : "${deviceLogPage.totalPages}",
													curpage : "${deviceLogPage.currentPage}",
													numPerPage : "${deviceLogPage.numPerPage}",
													isShowJump : true,
													ajaxRequestPath : "${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${switchId}&topId=${switchId}&resourceType=Switch&level=${level}&state=${state}",
													divId : 'dAlertContent'
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
						<h2>交换机端口(${portCount})</h2>
						<div class="box-icon">
							<a href="javascript:portFreshen()" class="btn btn-round" title="刷新" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
							<a href="javascript:void(0);" class="btn btn-round" title="导出" id="swPortExpCSV"><i class="icon-download-alt" data-rel="tooltip"></i></a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div class="box-content" style="max-height: 810px;">
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
												<c:forEach var="item" items="${portPage.data}" varStatus="status">
													<tr>
														<td>
															<a title="${item.the_display_name}" href="${path}/servlet/switchport/SwitchportAction?func=PortInfo&portId=${item.port_id}&switchId=${switchId}">${item.the_display_name}</a>
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
												<tr>
													<td colspan=6>暂无数据！</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
								<div id="portListpageNub" class="pagination pagination-centered"></div>
								<c:choose>
									<c:when test="${(not empty portPage) and (not empty portPage.data) and fn:length(portPage.data) > 0}">
										<script>
											$("#portListpageNub").getLinkStr({
												pagecount : "${portPage.totalPages}",
												curpage : "${portPage.currentPage}",
												numPerPage : "${portPage.numPerPage}",
												isShowJump : true,
												ajaxRequestPath : "${path}/servlet/switchport/SwitchportAction?func=AjaxPortPage&switchId=${switchId}",
												divId : "portContent"
											});
											var $exportCSV = $("#swPortExpCSV");
											$exportCSV.unbind();
											$exportCSV.attr("href", "${path}/servlet/switchport/SwitchportAction?func=ExportPortConfigData&switchId=${switchId}");
										</script>
									</c:when>
									<c:otherwise>
										<script>
											$(function(){
												var $exportCSV = $("#swPortExpCSV");
												$exportCSV.unbind();
												$exportCSV.attr("href", "javascript:void(0);");
												$exportCSV.bind("click", function(){ bAlert("暂无可导出数据！"); });
											});
										</script>
									</c:otherwise>
								</c:choose>
								
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