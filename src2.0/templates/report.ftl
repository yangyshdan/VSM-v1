<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script src="js/jquery-1.7.2.min.js"></script>
		<script src="js/highcharts.js"></script>
		<script src="js/jquery.ztree.core-3.5.js"></script>
		<script src="js/report.js"></script>
		<link rel="stylesheet" type="text/css" href="css/zTreeStyle.css">
		<link rel="stylesheet" type="text/css" href="css/reportStyle.css">
		<link rel="shortcut icon" href="css/img/favi.png">
<script type="text/javascript">
$(function(){
Highcharts.setOptions({global: {useUTC: false}});
Highcharts.setOptions({colors: ['#00827C', '#FF94CD', '#0000CC', '#995FFF', '#990066', '#FFE00B', '#71FFAB', '#99CC00', '#FF660B','#333333','#FF0B85','#0BFF0B']});
});
(function($){ 
$.fn.center = function(){ 
var top = ($(window).height() - this.height())/2; 
var left = ($(window).width() - this.width())/2; 
var scrollTop = $(document).scrollTop(); 
var scrollLeft = $(document).scrollLeft(); 
return this.css( { position : 'absolute', 'top' : top + scrollTop, 'left' : left + scrollLeft } ).show(); 
} 
})(jQuery);

var setting={
view:{
	showIcon:false
},
callback:{
	beforeClick:beforeClick,
	onClick:onClick
}
};
var zNodes=${zNodes};
function beforeClick(treeId,treeNode,clickFlag){
	return (treeNode.click!=false);
}
function onClick(event,treeId,treeNode,clickFlag){
	var divHeight = $("#"+treeNode.divId).offset().top;
	var windowHeight = $(window).height(); 
	if(divHeight>50){
		divHeight=divHeight-50;
	}
	$('body,html').animate({scrollTop:divHeight},20);
}

//下载报表
function downloadReport(downloadType){
	$("#realName").val("");
	$("#htmlDoc").val("");
	var localPath = window.location.href;
	var filePath = localPath.substring(localPath.indexOf("report"));
	var app = localPath.split("/")[3];
	var path = localPath.substring(0,localPath.indexOf(app)) + app;
	var actionUrl = localPath.substring(0,localPath.indexOf("report")) + "servlet/report/ReportAction?func=DownloadHTMLorPDF";
	var formEles = '<form id="downForm" name="downForm" action="' + actionUrl + '" method="POST">';
		formEles += '<input type="text" id="realName" name="realName" value=""></input><br/>';
		formEles += '<textarea id="htmlDoc" name="htmlDoc" value=""></textarea><br/>';
		formEles += '<input id="downSubmit" name="downSubmit" type="submit" value="submit"></input></form>';
	$("#formDiv").html(formEles);
	$("#realName").val(filePath);
	if(downloadType == "PDF") {
		$("#reportContainer").css("width",1280);
		$("#reportIndex .panel-body").animate({width:0},300);
		$("#reportIndex").animate({ width: 20}, 300);
		$("#menu").text("显示菜单");
		doDrowImg();
		setTimeout("fillFormElements()",500);		
	}else{
		$("#downSubmit").click();
	}
}

//设置参数,提交表单
function fillFormElements(){
	$("#htmlDoc").val($("#reportContainer").html());
	$("#downSubmit").click();	
}

$(document).ready(function(){
	$.fn.zTree.init($('#treeBase'),setting,zNodes);
	//$(window).scrollTop($("#storagePref").offset().top);
	$("#reportContainer").css("width",1090);
	$("#menu").toggle(function(){
		$("#reportContainer").css("width",1280);
		$("#reportIndex .panel-body").animate({width:0},300);
		$("#reportIndex").animate( { width: 20}, 300 );
		$(this).text("显示菜单");
		doDrowImg();
	},function(){
		$("#reportContainer").css("width",1090);
		$("#reportIndex .panel-body").animate({width:200},300);
		$("#reportIndex").animate( { width: 220}, 300 );
		$(this).text("隐藏菜单");
		doDrowImg();
	});
	//回到顶部,回到底部
	var overHight = $("#reportContainer").height();
	$('#up').click(function() { $('html,body').animate({ scrollTop: '0px' }, 800); });
	$('#down').click(function() { $('html,body').animate({ scrollTop: overHight + 'px' }, 800); });
});

function doDrowImg(){
<#-- TopN 信息 -->
<#list topnData as stype>
	<#list stype.configList as subType>
		<#list subType.configList as ssubType>
			<#list ssubType.configList as topn>
				Topn.prffield("T_${stype.getString("id")}_Storage${subType.getString("id")}_${ssubType.getString("id")}_${topn.getString("id")}",${topn});
			</#list>
		</#list>
	</#list>
</#list>
<#--性能信息-->
<#list perfData as stype>
	<#list stype.configList as subType>
		<#list subType.configList as perSGMap>
			<#list perSGMap.configList as performen>
				Perf.drawPrfLine("P_${stype.getString("id")}_Storage${subType.getString("id")}_${perSGMap.getString("id")}_${performen.getString("id")}",${performen.configList});
			</#list>
		</#list>
	</#list>
</#list>
}
$(doDrowImg)
</script>
	</head>
	<body>
		<#assign statusMap = { 
				"0":"Unknown",
				"1":"Not Available", 
				"2":"Servicing", 
				"3":"Starting", 
				"4":"Stopping", 
				"5":"Stopped", 
				"6":"Aborted", 
				"7":"Dormant", 
				"8":"Completed", 
				"9":"Migrating",
				"10":"Emigrating", 
				"11":"Immigrating", 
				"12":"Snapshotting", 
				"13":"Shutting Down", 
				"14":"In Test", 
				"15":"Transitioning", 
				"16":"In Service", 
				"17":"DMTF Reserved", 
				"19":"Vendor Reserved",
				"22":"Error" 
		}/>
		<div style="width:1320px;margin:0 auto; position: relative;">
			<div id="reportIndex" class="uptop">
				<div class="panel-body" style="float:left;width:200px;overflow-x:auto;overflow-y:auto;height:100%">
					<ul id="treeBase" class="ztree" style="padding:5px 0px;"></ul>
				</div>
				<div id="oprateCai" style="width:20px;height:100%;float:right;margin-top:80px;">
					<a id="menu" href="javascript:void(0)" class="cai">隐藏菜单</a>
					<a href="javascript:void(0)" onclick="downloadReport('HTML')" class="cai">下载<br/>H<br/>T<br/>M<br/>L</a>
					<a href="javascript:void(0)" onclick="downloadReport('PDF')" class="cai">下载<br/>P<br/>D<br/>F</a>
					<a id="up" href="javascript:void(0)" class="cai">回到顶部</a>
					<a id="down" href="javascript:void(0)" class="cai">回到底部</a>
				</div>
			</div>
			<div id="formDiv" style="float:right;display:none;"></div>
			<!--<div id="load" style="z-index:1;text-align:center;" display="none"><img src="css/img/loading.gif"/></div>-->
			<div id="reportContainer" class="con">
				<div id="head" align="center">
					<div style="width:100%;margin:0px auto;padding-top:50px;">
						<#if logo??>
							<img src="../upload/${logo}" style="width:100px;height:100px;" class="yuan" />
						</#if>
						<h1 style="font-size: 20px;color:blue;">
							${title}
						</h1>
						<strong style="">${startTime} ~ ${endTime}</strong>
					</div>
				</div>
				<div id="deviceDiv" style="padding-top:30px;">
					<div >
						<h2>设备信息</h2>
						<#list configData as stype>
							<#-- SVC/BSP/DS -->
							<#if (stype.getString("id")=="SVC" || stype.getString("id")=="BSP" || stype.getString("id")=="DS")>
								<div id="D_${stype.getString("id")}">
									<#list stype.configList as storage>
										<h2 id="D_${stype.getString("id")}_Storage${storage.getString('id')}">存储系统:${storage.getString("name")}</h2>
										<#list storage.configList as subType>
											<#if (subType.getString("id")=="storage" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_storage" class="style_9">存储系统</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															IP地址
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															物理磁盘容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															池容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															可用池容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															卷总容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															已分配卷总容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															未分配卷总容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															最近探查时间
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															缓存
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("ip_address")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_propagated_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_physical_disk_space")/1024; m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_storage_pool_consumed_space")/1024; m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_storage_pool_available_space")/1024; m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_volume_space")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_assigned_volume_space")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if (perSGMap.getString("the_unassigned_volume_space")=="null")>
																${perSGMap.getString("the_unassigned_volume_space")}
															<#else>
																#{perSGMap.getDouble("the_unassigned_volume_space")/1024;m1M2}
															</#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.get("the_last_probe_time")?string}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if (perSGMap.getString("cache")!="null")>
															 	#{perSGMap.getDouble("cache")/1024;m1M2}
															<#else>
															 	${perSGMap.getString("cache")!""}
															</#if>
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="node" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_node" class="style_9">冗余节点</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															组件ID
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															IP地址
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															IO Group
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															后端名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															操作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															WWN
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("component_id")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("ip_address")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("iogroup_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_backend_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("wwn")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="port" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_port" class="style_9">端口</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口类型
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															操作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															硬件状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口速率(M)
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("port_number")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_type")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_consolidated_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_port_speed")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="volume" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_volume" class="style_9">卷</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															逻辑卷名
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															逻辑卷名
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															已用容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															沉余级别
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															存储池
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															唯一编号
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_consolidated_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_capacity");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_used_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_redundancy")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("pool_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("unique_id")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="pool" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_pool" class="style_9">存储池</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															已用容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															可用容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															已分配容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															未分配容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															本地状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															操作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															硬件状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															冗余级别
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_consumed_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_available_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_assigned_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_unassigned_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_native_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_consolidated_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("raid_level")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="disk" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_disk" class="style_9">磁盘</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															容量(G)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															转速
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															运行状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															阵列
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															厂商
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															型号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															序列号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															固件版本
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															硬件状态
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_capacity");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("speed")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("diskgroup_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("vendor_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if perSGMap.getString("model_name") == "">
																N/A
															<#else>
																${perSGMap.getString("model_name")}
															</#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("serial_number")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("firmware_rev")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_consolidated_status")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="arrayset" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_arrayset" class="style_9">阵列</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															Rank
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															存储池
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															冗余级别
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															描述
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("rank_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("pool_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("raid_level")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("description")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="extent" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_extent" class="style_9">存储扩展</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															扩展卷数
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															扩展容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															总容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															可用容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															操作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															本地状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															存储池
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															设备ID
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_extent_volume")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if (perSGMap.getString("the_extent_space")!="null")>
															 #{perSGMap.getDouble("the_extent_space");m1M2}
															<#else>
															 ${perSGMap.getString("the_extent_space")}
															</#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_total_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_available_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_native_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("pool_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("device_id")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="rank" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_rank" class="style_9">Rank</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															扩展卷数
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															总容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															已用容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															空闲容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															存储池
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															状态
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_extent_volume")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_total_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_used_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("the_available_space");m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("pool_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_operational_status")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="iogroup" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_iogroup" class="style_9">IOGroup</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															镜像内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															镜像空闲内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															快照内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															快照空闲内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															阵列内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															阵列空闲内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															维护状态
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("mirroring_total_memory")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("mirroring_free_memory")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("flash_copy_total_memory")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("flash_copy_free_memory")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("raid_total_memory")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("raid_free_memory")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("maintenance")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											</#if>
										</#list>
									</#list>
								</div>
							</#if>
							<#-- For EMC/HDS/NETAPP -->
							<#if (stype.getString("id")=="EMC" || stype.getString("id")=="HDS" || stype.getString("id")=="NETAPP")>
								<div id="D_${stype.getString("id")}">
									<#list stype.configList as storage>
										<h2 id="D_${stype.getString("id")}_Storage${storage.getString('id')}">存储系统:${storage.getString("name")}</h2>
										<#list storage.configList as subType>
											<#if (subType.getString("id")=="storage" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_storage" class="style_9">存储系统</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															IP地址
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															供应商
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															产品
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															型号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															序列号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															微码版本
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															缓存信息(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															写缓存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															物理磁盘容量(TB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															逻辑容量(TB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															已用逻辑容量(TB)
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("ip_address")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("vendor_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("model")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("serial_number")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("code_level")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("cache_gb")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("nvs_gb")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("physical_disk_capacity")/1024/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("total_usable_capacity")/1024/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("total_usable_capacity")/1024/1024 - perSGMap.getDouble("unallocated_usable_capacity")/1024/1024;m1M2}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="storagegroup" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_storagegroup" class="style_9">存储关系组</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															网络地址
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															系统
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															是否可共享 
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("hostgroup_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("uid")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("model")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("shareable")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="port" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_port" class="style_9">端口</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口名
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															网络地址
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口速率
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口类型
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("network_address")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("port_speed")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("type")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="volume" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_volume" class="style_9">卷</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															逻辑卷名
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															逻辑空间(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															实占空间(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															RAID类型
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("logical_capacity")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("physical_capacity")!="null")>
																 #{perSGMap.getDouble("physical_capacity")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("physical_capacity")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("raid_level")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="pool" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_pool" class="style_9">池</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															存储池名
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															RAID类型
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															总逻辑容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															已用逻辑容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															LUN数量
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															后端磁盘数量
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("raid_level")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("total_usable_capacity")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															#{perSGMap.getDouble("total_usable_capacity")/1024 - perSGMap.getDouble("unallocated_capacity")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("num_lun")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("num_backend_disk")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="disk" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_disk" class="style_9">磁盘</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘速度
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															位置
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															类型
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 #{perSGMap.getDouble("ddm_cap")/1024;m1M2}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("ddm_speed")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("ddm_type")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											</#if>
										</#list>
									</#list>
								</div>
							</#if>
							<#-- SWITCH -->
							<#if (stype.getString("id")=="SWITCH")>
								<div id="D_SWITCH">
									<#list stype.configList as storage>
										<h2 id="D_${stype.getString("id")}_Storage${storage.getString('id')}">交换机:${storage.getString("name")}</h2>
										<#list storage.configList as subType>
											<#if (subType.getString("id")=="switch" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_switch" class="style_9">交换机</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															厂商
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															型号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															域ID
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															IP地址
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															光纤
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															WWN
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															序列号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															引擎工作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															电源工作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口工作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															光纤模块状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															描述
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")!}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("vendor_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("model_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_propagated_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("domain")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("ip_address")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("fabric_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("switch_wwn")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															${perSGMap.getString("serial_number")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if perSGMap.getString("engine_status") == "">
																N/A
															<#else>
																${perSGMap.getString("engine_status")}
															</#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if perSGMap.getString("power_status") == "">
																N/A
															<#else>
																${perSGMap.getString("power_status")}
															</#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if perSGMap.getString("port_status") == "">
																N/A
															<#else>
																${perSGMap.getString("port_status")}
															</#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if perSGMap.getString("fiber_status") == "">
																N/A
															<#else>
																${perSGMap.getString("fiber_status")}
															</#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															<#if perSGMap.getString("description") == "">
																N/A
															<#else>
																${perSGMap.getString("description")}
															</#if>
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="switchPort" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_switchPort" class="style_9">交换机端口</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口号
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口类型
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															操作状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															硬件状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															端口速率(M)
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("port_number")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_type")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_consolidated_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_port_speed")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											</#if>
										</#list>
									</#list>
								</div>
							</#if>
							<#-- HOST -->
							<#if (stype.getString("id")=="HOST")>
								<div id="D_HOST">
									<#list stype.configList as storage>
										<h2 id="D_${stype.getString("id")}_Storage${storage.getString('id')}">服务器:${storage.getString("name")}</h2>
										<#list storage.configList as subType>
											<#if (subType.getString("id")=="physical" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_physical" class="style_9">物理机</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															IP地址
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															处理器构架
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															处理器总数
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘剩余容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															更新时间
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("ip_address")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("cpu_architecture")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("processor_count")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("ram_size")!="null")>
																 #{perSGMap.getDouble("ram_size")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("ram_size")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("disk_space")!="null")>
																 #{perSGMap.getDouble("disk_space")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("disk_space")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("disk_available_space")!="null")>
																 #{perSGMap.getDouble("disk_available_space")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("disk_available_space")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("update_timestamp")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											<#elseif (subType.getString("id")=="virtual" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_virtual" class="style_9">虚拟机</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															CPU数量
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															总内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘总容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘剩余容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															更新时间
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("assigned_cpu_number")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("total_memory")!="null")>
																 #{perSGMap.getDouble("total_memory")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("total_memory")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("disk_space")!="null")>
																 #{perSGMap.getDouble("disk_space")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("disk_space")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("disk_available_space")!="null")>
																 #{perSGMap.getDouble("disk_available_space")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("disk_available_space")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															${perSGMap.getString("operational_status")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("update_timestamp")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											</#if>
										</#list>
									</#list>
								</div>
							</#if>
							<!-- APPLICATION -->
							<#if (stype.getString("id")=="APPLICATION")>
								<div id="D_APPLICATION">
									<#list stype.configList as storage>
										<h2 id="D_${stype.getString("id")}_Storage${storage.getString('id')}">应用:${storage.getString("name")}</h2>
										<#list storage.configList as subType>
											<#if (subType.getString("id")=="app" && subType.configList.size()>0)>
												<div id="D_${stype.getString("id")}_Storage${storage.getString('id')}_app" class="style_9">虚拟机</div>
												<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
												<TR vAlign=top align=left>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															前段名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															后端名称
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															CPU数量
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															总内存(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘总容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															磁盘剩余容量(GB)
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															状态
														</DIV>
													</TH>
													<TH class=style_9 align=left>
														<DIV id=AUTOGENBOOKMARK_18>
															更新时间
														</DIV>
													</TH>
												</TR>
												<#list subType.configList as perSGMap>
												<TR>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("the_display_name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("name")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("assigned_cpu_number")}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("total_memory")!="null")>
																 #{perSGMap.getDouble("total_memory")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("total_memory")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("disk_space")!="null")>
																 #{perSGMap.getDouble("disk_space")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("disk_space")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 <#if (perSGMap.getString("disk_available_space")!="null")>
																 #{perSGMap.getDouble("disk_available_space")/1024;m1M2}
															 <#else>
															 	 ${perSGMap.getString("disk_available_space")}
															 </#if>
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															${statusMap[perSGMap.getString("operational_status")]!}
														</DIV>
													</TD>
													<TD class=style_11>
														<DIV>
															 ${perSGMap.getString("update_timestamp")}
														</DIV>
													</TD>
												</TR>
												</#list>
												</TABLE>
											</#if>
										</#list>
									</#list>
								</div>
							</#if>
						</#list>
					</div>
				</div>
				<!-- performance -->
				<div id="perfDiv">
					<div><h2>性能信息</h2></div>
					<#list perfData as stype>
					<#-- 存储类型 -->
					<h2 id="P_${stype.getString("id")}">${stype.getString("name")}</h2>
					<#-- 存储系统 -->
					<#list stype.configList as subType>
					<div id="P_${stype.getString("id")}_Storage${subType.getString("id")}" class="style_9" style="font-size:18px;">${subType.getString("name")}</div>
					<#-- 组件类型 -->
					<#list subType.configList as perSGMap>
					<#-- 组件性能列表 -->
					<div id="P_${stype.getString("id")}_Storage${subType.getString("id")}_${perSGMap.getString("id")}" class="style_9" style="font-size:12px;">${perSGMap.getString("name")}</div>
					<#list perSGMap.configList as perf>
					<div style="width:100%">
						<div class="box-header well" data-original-title>
							<h2>${perf.getString("name")}</h2>
						</div>
						<div class="box-content" style="background-color:#ffffff">
							<div id="P_${stype.getString("id")}_Storage${subType.getString("id")}_${perSGMap.getString("id")}_${perf.getString("id")}" style="height: 300px;">
							</div>
						</div>
						<div class="box-header well" data-original-title>
							<h3>${perf.getString("alertInfo")}</h3>
							<div style="clear: both;"></div>
						</div>
					</div>
					</#list>
					</#list>
					</#list>
					</#list>
				</div>
				<div id="topnDiv">
					<div><h2>TopN信息</h2></div>
					<#list topnData as stype>
					<h2 id="T_${stype.getString("id")}">${stype.getString("name")}</h2>
					<#list stype.configList as subType>
					<div id="T_${stype.getString("id")}_Storage${subType.getString("id")}" class="style_9">${subType.getString("name")}</div>
					<#list subType.configList as perSGMap>
					<div id="T_${stype.getString("id")}_Storage${subType.getString("id")}_${perSGMap.getString("id")}" class="style_9">${perSGMap.getString("name")}</div>
						<#list perSGMap.configList as topn>
						<div style="width:100%">
							<div class="box-header well" data-original-title>
								<h2>
									${topn.getString("ftitle")}
								</h2>
							</div>
							<div class="box-content" style="background-color:#ffffff">
								<div id="T_${stype.getString("id")}_Storage${subType.getString("id")}_${perSGMap.getString("id")}_${topn.getString("id")}" style="height: 300px;">
									<#if (topn.data.size()==0)>无性能数据</#if>
								</div>
								<div style="clear: both;"></div>
							</div>
						</div>
						</#list>
					</#list>
					</#list>
					</#list>
				</div>
				<div id="alertDiv">
					<#list alertData as stype>
						<#if (stype.getString("id")=="alertLevel1")>
							<h2 id="A_alertLevel1">告警信息</h2>
							<div id="A_alertLevel1_StorageInfo" class="style_9"></div>
							<TABLE style="width:100%; BORDER-COLLAPSE: collapse; EMPTY-CELLS: show;" class=style_19 border=1>
							<TR vAlign=top align=left>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										首次发生时间
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										最后发生时间
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										类型
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										次数
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										状态
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										级别
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										设备
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										部件
									</DIV>
								</TH>
								<TH class=style_9 align=left>
									<DIV id=AUTOGENBOOKMARK_18>
										消息
									</DIV>
								</TH>
							</TR>
							<#list stype.configList as perSGMap>
							<TR>
								<#if (stype.data.size()==0)>
								<TD class=style_11 colspan="9">
									<DIV>
										 无告警数据
									</DIV>
								</TD>
								</#if>
								
								<#if (stype.data.size()>0)>
								<TD class=style_11>
									<DIV>
										 ${perSGMap.getString("ffirsttime")}
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										 ${perSGMap.getString("flasttime")}
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										<#if (perSGMap.getString("flogtype")=="0")>
											系统告警
										<#elseif (perSGMap.getString("flogtype")=="1")>
											TPC告警
										<#elseif (perSGMap.getString("flogtype")=="2")>
											阀值告警
										<#elseif (perSGMap.getString("flogtype")=="3")>
											硬件告警
										</#if>
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										 ${perSGMap.getString("fcount")}
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										<#if (perSGMap.getString("fstate")=="0")>
											未确认
										<#elseif (perSGMap.getString("fstate")=="1")>
											已确认
										<#elseif (perSGMap.getString("fstate")=="2")>
											已清除
										<#elseif (perSGMap.getString("fstate")=="3")>
											逻辑删除
										</#if>
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										<#if (perSGMap.getString("flevel")=="0")>
											Info
										<#elseif (perSGMap.getString("flevel")=="1")>
											Warning
										<#elseif (perSGMap.getString("flevel")=="2")>
											Critical
										</#if>
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										${perSGMap.getString("ftopname")}
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										${perSGMap.getString("fresourcename")}
									</DIV>
								</TD>
								<TD class=style_11>
									<DIV>
										${perSGMap.getString("fdescript")}
									</DIV>
								</TD>
								</#if>
							</TR>
							</#list>
							</TABLE>
						</#if>
					</#list>		
				</div>
				<br />
				<br />
				<br />
				<br />
			</div>
			
			<div>
				<#list diagnostics as stype>
					
					<#list stype.alert as alert>
						<p>在${stype.time}时间段，发生${alert}</p>
					</#list>
				</#list>		
			</div>
			<br />
				<br />
				<br />
				<br />
		</div>
	</body>
</html>