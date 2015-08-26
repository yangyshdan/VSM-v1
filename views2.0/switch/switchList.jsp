<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/switch.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/widget.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path}/resource/js/pandect/platens.js"></script>
<script type="text/javascript">
function trDbClick(id){
	window.location.href = "${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId="+id+"&r="+Math.random();
}
$(function(){
	<%--增加查询条件--%>
	$.ajax({
		url: "${path}/servlet/switchs/SwitchAction?func=SwitchSettingPrf2&level=1",
		data: { },
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
		}
	});
	doListRefresh();
	$("#conTable").tablesorter();
	changeColumn.initCol();
});
//刷新
function doFreshen(){
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/switchs/SwitchAction?func=AjaxSwitchPage",jsonVal,$("#loadcontent"));
}

//数据查询
function switchFilter(){
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/switchs/SwitchAction?func=AjaxSwitchPage", jsonVal, $("#loadcontent"));
}

function clearData(){
	$("button[type='reset']").click();
}
$(clearData);

function doListRefresh(){
	var url = "${path}/servlet/switchs/SwitchAction?func=SwitchPrfField";
	loadData(url, {isFreshen:1, level:1},
			$("#prfContent"), false, false, false, true,
		function(data){
			var json = eval("("+data+")");
			var $tab = $("#conTable52207");
			$tab.find("thead tr").children().remove();
			$tab.find("tbody").children().remove();
			//$tab.find("thead tr").html(json.thead);
			//$tab.find("tbody").html(json.tbody);
			var $pag = $("#HypervisorInfopageNub");
			var $csv = $("#exportCSV521701");
			$csv.unbind();
			$pag.children().remove();
			if(json.totalPages > 0){
				$csv.attr({href: "${path}/servlet/switchs/SwitchAction?func=ExportPrefData&level=1" });
				$csv.attr({href: exurl});
				$pag.getLinkStr({pagecount: json.totalPages, curpage: json.currentPage, 
					numPerPage: json.numPerPage, isShowJump:true,
					exFunc: function(curpage, numPerPage){
						perfPage0237({
							url: url,
							data:{curpage: curpage, numPerPage: numPerPage, tablePage: 0, isFreshen: 1}, 
							tableSelector: "#conTable52207",
							pagSelector: "#HypervisorInfopageNub",
							csvSelector: "#exportCSV521701",
							exportCSVUrl: exurl
						});
					},
					divId: ""});
				$pag.find("ul li a.first").first().trigger("click");
			}
			else {
				$csv.attr({href: "javascript:void(0);"});
				$csv.bind("click", function(){ bAlert("暂无可导出数据！"); });
			}
			if(json.graphType == 1){
				if(json.series){ 
					Public.drawTopn02("prfContent",
						{jsonVal: json.series, 
						getURL: function(point){
							return "/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=" + point.devId;
						}});
				}
			}
			else {
				Public.drawPrfLine("prfContent", json);
				$("#pTitle").html(function(){
					var str="交换机性能  (";
					$.each(json.kpiInfo,function(i){
						str += json.kpiInfo[i].ftitle;
						if(i < json.kpiInfo.length - 1){
							str+=",";
						}
					});
					if(str.length > 100){
						str = str.substring(0,100) + "...";
					}
					str += ")";
					return str;
				});
			}
	});
}

function doLoadSwitch20101(timeRange){
	var html = "<div class='loading' style='background:none;margin:10px;'><img src='" + 
				getRootPath()+"/resource/img/loading.gif'/>正在载入……</div>";
	$.ajax({
		url: "${path}/servlet/switchs/SwitchAction?func=GetSwitchPortTOP5",
		data: { timeRange: timeRange, which: 1 },
		type: "post",
		dataType: "json",
		success:function(jsonData){
			if(jsonData.success){
				var obj = jsonData.value;
				Public.drawTopn02("pord", {
					jsonVal: obj.PordTopNData, 
					getURL: function(point){
						return "/servlet/switchport/SwitchportAction?func=PortInfo&portId=" + point.eleId + "&switchId=" + point.devId;
					}
				});
			}
			else {
				bAlert(jsonData.msg);
			}
			$("#myModal").modal("hide");
		},
		beforeSend:function(){
			$("#pord").html(html);
		}
	});
	
	$.ajax({
		url: "${path}/servlet/switchs/SwitchAction?func=GetSwitchPortTOP5",
		data: { timeRange: timeRange, which: 2},
		type: "post",
		dataType: "json",
		success:function(jsonData){
			if(jsonData.success){
				var obj = jsonData.value;
				Public.drawTopn02("pors", {
					jsonVal: obj.PorsTopNData, 
					getURL: function(point){
						return "/servlet/switchport/SwitchportAction?func=PortInfo&portId=" 
							+ point.eleId + "&switchId=" + point.devId;
					}
				});
			}
			else {
				bAlert(jsonData.msg);
			}
			$("#myModal").modal("hide");
		},
		beforeSend:function(){
			$("#pors").html(html);
		}
	});
	
	$.ajax({
		url: "${path}/servlet/switchs/SwitchAction?func=GetSwitchPortTOP5",
		data: { timeRange: timeRange, which: 3 },
		type: "post",
		dataType: "json",
		success:function(jsonData){
			if(jsonData.success){
				var obj = jsonData.value;
				Public.drawTopn02("incident", {
					jsonVal: obj.eventTOP5, 
					getURL: function(point){
						return "/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=" + point.eleId + "&tabToShow=3";
					}
				});
			}
			else {
				bAlert(jsonData.msg);
			}
			$("#myModal").modal("hide");
		},
		beforeSend:function(){
			$("#incident").html(html);
		}
	});
	
	$.ajax({
		url: "${path}/servlet/switchs/SwitchAction?func=GetSwitchPortTOP5",
		data: { timeRange: timeRange, which: 4 },
		type: "post",
		dataType: "json",
		success:function(jsonData){
			if(jsonData.success){
				var obj = jsonData.value;
				Public.drawPie("allincident", 
					obj.eventDistr, {
					getPointFormat: function(){
						return "设备:<b>{point.name}</b><br>出现次数:<b>{point.times}</b><br>所占比例:<b>{point.p}%</b>";
					},
					beforeDrawPie: function(jsonVal){
						Highcharts.setOptions({colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", 
						"#8085e8", "#8d4653", "#91e8e1", "#00827C", "#FF94CD", "#0000CC", "#995FFF", "#990066", "#FFE00B", "#71FFAB", 
						"#99CC00", "#FF660B", "#333333", "#FF0B85", "#0BFF0B"]});
					},
					getURL: function(point){
						return "/servlet/switchs/SwitchAction?func=SwitchInfo&tabToShow=3&switchId=" + point.rid;
					}
				});
			}
			else {
				bAlert(jsonData.msg);
			}
			$("#myModal").modal("hide");
		},
		beforeSend:function(){
			var html = "<div class='loading' style='background:none;margin:10px;'><img src='" + 
				getRootPath()+"/resource/img/loading.gif'/>正在载入……</div>";
			$("#allincident").html(html);
		}
	});
}

$(function(){
	doLoadSwitch20101();
	//var PordTopNData={PordTopNData};
	//var PorsTopNData={PorsTopNData};
	
	//var pie=getAttribute(${Pies});
	//Public.drawTopn("pord", PordTopNData);
	//Public.drawTopn("pors", PorsTopNData);
	/*Public.drawTopn02("pord",
		{jsonVal: getAttribute(${PordTopNData}), 
		getURL: function(point){
			return "/servlet/switchport/SwitchportAction?func=PortInfo&portId=" + point.eleId + "&switchId=" + point.devId;
		}});
	Public.drawTopn02("pors",
		{jsonVal: getAttribute(${PorsTopNData}), 
		getURL: function(point){
			return "/servlet/switchport/SwitchportAction?func=PortInfo&portId=" + point.eleId + "&switchId=" + point.devId;
		}});*/
	//Public.drawTopn("incident", ints);
	/*Widget.drawPie("allincident", pie);
	var inname1="${inname1}";
	var value1=getAttribute(${value1});
	var inname2="${inname2}";
	var value2=getAttribute(${value2});
	var inname3="${inname3}";
	var value3=getAttribute(${value3});
	var inname4="${inname4}";
	var value4=getAttribute(${value4});
	var inname5="${inname5}";
	var value5=getAttribute(${value5});
	var starTime="${startTime}";
	var endTime="${endTime}";
	//pla.platen(inname1,value1,inname2,value2,inname3,value3,inname4,value4,inname5,value5,starTime,endTime)
	pla.platen(null, null, null, null, null, null, null, null, null, null,starTime,endTime);*/
});
function doPerfLineFilter(timeRange) {
	doLoadSwitch20101(timeRange);
	//loadData("${path}/servlet/switchs/SwitchAction?func=DrawPerffile",{timeRange:timeRange},$("#prefChart"));
}


</script>

<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">设备列表</a>
		</li>
		<li class="">
			<a href="#prfTab">性能</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
<!-- 列表开始 -->
<div class="tab-pane active" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>交换机列表</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="选择列" onclick="changeColumn.showCol(this);" data-rel="tooltip"><i class="icon-eye-open"></i></a>
					<%-- 
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					--%>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/switchs/SwitchAction?func=exportSwitchConfigData";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			
			<div class="box-content">
				<!-- 筛选条件开始 -->
				<div class="box-content" style="width:90%;;height:55px;margin:0px auto;margin-bottom:10px;">
					<form class="form-horizontal" id="conditionForm">
						<fieldset>
							<div class="control-group" style="margin-bottom: 10px;">
								<table class="table-condensed" width="70%" style="margin: 0px auto;">
									<tbody>
										<tr>
											<td>
												<label class="col-lg-2 control-label" for="name" style="width:60px">名称</label>
												<input type="text" class="form-control" id="name" name="name" style="width: 140px;margin-left: 10px;">
											</td>
											<td>
												<label class="col-lg-2 control-label" for="ipAddress" style="width:70px">IP地址</label>
												<input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 10px;">
											</td>
											<td>
												<label class="col-lg-2 control-label" for="status" style="width:60px">状态</label>
												<select class="form-control" id="status" name="status" type="text" style="width: 140px; margin-left: 10px;">
													<option value="">不限</option>
													<option value="Normal">Normal</option>
													<option value="Warning">Warning</option>
													<option value="Critical">Critical</option>
												</select>
											</td>
											<td>
												<label class="col-lg-2 control-label" for="serialNumber" style="width:70px">序列号</label>
												<input class="form-control" id="serialNumber" name="serialNumber" type="text" style="width:140px;margin-left: 10px;">
											</td>
										</tr>
										<tr>
											<td colspan="4" style="text-align:center;">
												<button type="button" class="btn btn-primary" onclick="switchFilter();">查询</button>
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												<button class="btn" type="reset">重置</button>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
						</fieldset>
					</form>
				</div>
				<!-- 筛选条件结束 -->
				<%-- 
				<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
				<div id="conAlert" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
					<div class="arrow"></div>
					<div class="popover-inner">
						<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlert()">×</a></h3>
						<div class="popover-content" style="padding: 8px;">
					        <form class="form-horizontal" id="conditionForm">
								<fieldset>
								  <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="name" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="name" name="name" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="ipAddress" style="width:80px">IP地址</label>
					                  <input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 20px;">
					              </div> 
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="status" style="width:80px">状态</label>
					                   <select class="form-control" id="status" name="status" type="text" style="width:140px;margin-left: 20px;">
					                   	<option value="">不限</option>
					                   	<option value="Normal">Normal</option>
					                   	<option value="Warning">Warning</option>
					                   	<option value="Critical">Critical</option>
					                   </select>
					              </div>
					               <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="serialNumber" style="width:80px">序列号</label>
					                  <input class="form-control" id="serialNumber" name="serialNumber" type="text" style="width:140px;margin-left: 20px;">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button type="button" class="btn btn-primary" onclick="switchFilter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				--%>
				<div class="tab-pane active" id="loadcontent" style="overflow: visible;" id="loadcontent">
				<table class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all" id="conTable">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								厂商
							</th>
							<th>
								型号
							</th>
							<th>
								状态
							</th>
							<th>
								Zone ID
							</th>
							<th>
								IP地址
							</th>
							<th>
								Fabric网络
							</th>
							<th>
								WWN
							</th>
							<th>
								序列号
							</th>
							<%--
							<th>
								电源状态
							</th>
							<th>
								风扇状态
							</th>
							
							<th>
								引擎工作状态
							</th>
							<th>
								电源工作状态
							</th>
							<th>
								端口工作状态
							</th>
							<th>
								光纤模块状态
							</th>
							<th>
								描述
							</th>
							--%><th>
								更新时间
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty switchPage.data}">
								<c:forEach var="item" items="${switchPage.data}" varStatus="status">
									<tr>
										<td>
											<a title="${item.the_display_name}" href="${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=${item.switch_id}">${item.the_display_name}</a>
										</td>
										<td>
											${item.vendor_name}
										</td>
										<td>
											${item.model_name}
										</td>
										<td>
											<cs:cstatus value="${item.the_propagated_status}"/>
										</td>
										<td>
											${item.domain}
										</td>
										<td>
											<a title="${item.ip_address}" href="http://${item.ip_address}" target="_blank">${item.ip_address}</a>
										</td>
										<td>
											<a title="${item.fabric_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.fabric_name}</a>
										</td>
										<td>
											${item.switch_wwn}
										</td>
										<td>
											${item.serial_number }
										</td>
									<%--
										<td>
											${item.power_status}
										</td>
										<td>
											${item.fan_status}
										</td>
										<td>
											${item.engine_status}
										</td>
										<td>
											${item.power_status}
										</td>
										<td>
											${item.port_status}
										</td>
										<td>
											${item.fiber_status}
										</td>
										<td>
											${item.description}
										</td>--%>
										<td>
											${item.update_timestamp}
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=10>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="switchListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty switchPage.data}">
					<script>
						var param = $("#conditionForm").serialize();
						$("#switchListpageNub").getLinkStr({pagecount:"${switchPage.totalPages}",curpage:"${switchPage.currentPage}",numPerPage:"${switchPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/switchs/SwitchAction?func=AjaxSwitchPage&"+param,divId:'loadcontent'});
					</script>
				</c:if>
				<c:if test="${empty switchPage.data}">
					<script>
						$("#exportCSV").unbind();
						$("#exportCSV").attr("href","javascript:void(0);");
						$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
					</script>
				</c:if>
				</div>
			</div>
		</div>
	</div>
	<!--总览开始  -->
	<div class="box span12" style="margin: 0px 0px 0px 0px; height: 630px;">
		<div class="box-header well">
			<div class="box-icon">
			<label style="float:left;font-weight:bold;margin:-1px 2px 0px;">最近:</label>
			<a style="width: 40px; text-align: center; font-size: 13px; float: left; text-decoration: none;"
				href="javascript:void(0);" onclick="doPerfLineFilter('hour')">1小时</a>
				<label
					style="width: 8px; float: left; text-shadow: rgb(0, 0, 0, 0.2) 0px -1px -1px;">
					|
				</label>
				<a
					style="width: 30px; text-align: center; font-size: 13px; float: left; text-decoration: none;"
					href="javascript:void(0);" onclick="doPerfLineFilter('day')">1天</a>
				<label
					style="width: 8px; float: left; text-shadow: rgb(0, 0, 0, 0.2) 0px -1px -1px;">
					|
				</label>
				<a
					style="width: 30px; text-align: center; font-size: 13px; float: left; text-decoration: none;"
					href="javascript:void(0);" onclick="doPerfLineFilter('week')">1周</a>
				<label
					style="width: 8px; float: left; text-shadow: rgb(0, 0, 0, 0.2) 0px -1px -1px;">
					|
				</label>
				<a
					style="width: 30px; text-align: center; font-size: 13px; float: left; text-decoration: none;"
					href="javascript:void(0);" onclick="doPerfLineFilter('month')">1月</a>
			</div>
		</div>
	<div id="prefChart">
		<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 5px; width: 49%; float: left;">
			<div class="box-header well">
				<h2 style="width: 70%;height: 20px; overflow: hidden;">Total Port Data Rate Top5(交换机端口)</h2>
			</div>
			<div id="box_content_2_1" class="box-content" style="height: 250px;">
			<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
			<div id="pord" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px;" class="highcharts-container"></div>
			</div>
			</div>
		</div>
		<div id="charts_2_1" class="box span4" style=" margin: 0px 5px 0px 0px;width: 49%; float: right;">
			<div class="box-header well">
				<h2 style="width: 70%;height: 20px; overflow: hidden;">Total Port Packet Rate Top5(交换机端口)</h2>
			</div>
			<div id="box_content_2_1" class="box-content" style="height: 250px;">
			<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
			<div id="pors" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px;" class="highcharts-container"></div>
			</div>
			</div>
		</div>
		<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 5px;width: 49%; float: left;">
			<div class="box-header well">
				<h2 style="width: 70%;height: 20px; overflow: hidden;">事件 Top5(交换机)</h2>
			</div>
			<div id="box_content_2_1" class="box-content" style="height: 250px;">
				<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
					<div id="incident" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px;" class="highcharts-container"></div>
				</div>
			</div>
		</div>
		<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 0px;width: 49%; float: right;">
			<div class="box-header well">
				<h2 style="width: 70%;height: 20px; overflow: hidden;">所有交换机事件分布(%)</h2>
			</div>
			<div id="box_content_2_1" class="box-content" style="height: 250px;">
			<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
			<div id="allincident" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px;" class="highcharts-container"></div>
			</div>
			</div>
		</div>
		</div>
	</div>
	<!--总览结束   -->
	</div>
	<!-- 列表结束 -->
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">交换机性能</h2>
				<div class="box-icon">
					<!--
						<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Switch.settingPrf('',1)" data-rel="tooltip"><i class="icon-cog"></i></a>
					-->
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV521701" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div id="queryPage" class="box-content" style="height:220px;"></div>
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
						<div id="prfContent" style="width:94%;margin:0px;min-height:385px;height:420px;"></div>
					</div>
					<!-- 性能曲线切换页结束 -->
					<!-- 性能数据切换页开始 -->
					<div class="tab-pane" id="dataContent2" style="padding-top:10px;">
						<table class="table table-bordered table-striped table-condensed" id="conTable52207">
							<thead><tr></tr></thead>
							<tbody></tbody>
						</table>
						<div id="HypervisorInfopageNub" class="pagination pagination-centered"></div>
					</div>
					<!-- 性能数据切换页结束 -->
				</div>
			</div>
		</div>
	</div>
	</div>
	<!-- 性能结束 -->
	
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>