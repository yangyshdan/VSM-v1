<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>

<script src="${path}/resource/js/ajaxPage.js"></script>
<script type="text/javascript" src="${path}/resource/js/project/publicscript.js"></script>
<script type="text/javascript" src="${path}/resource/js/contextMenu/jquery.contextmenu.js"></script>
<script type="text/javascript" src="${path}/resource/js/jtopo/js/excanvas.js"></script>
<script type="text/javascript" src="${path}/resource/js/jtopo/jtopo-0.4.8-min.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script>
<script type="text/javascript" src="${path}/resource/js/project/util.js"></script>
<script type="text/javascript" src="${path}/resource/js/project/window_apply.js"></script>

<style type="text/css">
	#deviceAlertLog {
		width: 350px;
		border-radius: 6px;
		position: absolute;
		border: 2px solid #000000;
		background: #FFFFFF;
		list-style: none;
		margin: 0;
		padding: 0;
		display: none;
		z-index: 1;
	}
	.popover-title {
		padding: 5px 10px;
		line-height: 1;
		background-color: #F5F5F5;
		border-bottom: 1px solid #EEE;
		border-radius: 3px 3px 0px 0px;
	}
	.popover-text {
		padding: 5px 10px;
		line-height: 1;
		color: #000000;
		background-color: #F5F5F5;
		border-bottom: 1px solid #EEE;
		border-radius: 3px 3px 0px 0px;
	}
	.hmFill { cursor: text; }
	.contextMenu { display: none }
	a#goToDetailPage:hover{
		cursor:pointer;
	}
	
	#vmCfg5127 tbody>tr>td:FIRST-CHILD, #phyCfg5217 tbody>tr>td:FIRST-CHILD, 
	#swCfg5217 tbody>tr>td:FIRST-CHILD, #stoCfg5217 tbody>tr>td:FIRST-CHILD, 
	#poolCfg5217 tbody>tr>td:FIRST-CHILD, #volCfg5217 tbody>tr>td:FIRST-CHILD, 
	#stoSRCfg5217 tbody>tr>td:FIRST-CHILD, #poolSRCfg5217 tbody>tr>td:FIRST-CHILD, 
	#volSRCfg5217 tbody>tr>td:FIRST-CHILD{
		text-align:right;
		width:120px;
	}
	div#timeRangeNav a { width:35px;text-align:center; }
	div#timeRangeNav a:hover{ cursor:pointer; }
	div#timeRangeNav label{ width:8px;float:left; }
	table.table tbody tr td{ font-size:12px; }
</style>

<script type="text/javascript">
	$(function(){
		<%--
			由于新旧版的Highchart存在兼容性问题，而且有些功能旧版Highchart实现不了，所以使用下面的方法把旧版本Highchart干掉
		--%>
		var $head = $("head");
		$head.find("script#highchartsJS").remove();
		Highcharts = null;
		HighchartsAdapter = null;
		$head.append(
			$("<script>").attr({
				type: "text/javascript",
				src: "${path}/resource/js/Highcharts-4.0.3/js/highcharts.js"
			})
		).append(
			$("<script>").attr({
				type: "text/javascript",
				src: "${path}/resource/js/Highcharts-4.0.3/js/highcharts-more.js"
			})
		);
	});
</script>
<script type="text/javascript" src="${path}/resource/js/jtopo/js/huiming.topo.utils.js"></script>
<script type="text/javascript">
$(function(){
	window.onload = function(){
		var $div = $("#serverPerf2816");
		var width = $div.parent().parent().parent().width() - 4;
		$div.find("#ctr2816").css({width: width});
		$div.find("#ctr281601").css({width: width});
		$div.find("#ctr281602").css({width: width});
	};
	$("#timeRangeNav").find("a").click(function(){
		var $a = $(this);
		var _time = $a.attr("time");
		var time = _time.toLowerCase();
		second = 0;
		if(time.indexOf("hour") >= 0){ second = 60 * 60; }
		else if(time.indexOf("day") >= 0){ second = 24 * 60 * 60; }
		else if(time.indexOf("week") >= 0){ second = 7 * 24 * 60 * 60; }
		else {
			var c = new Date();
			var s1 = c.getTime();
			c.setMonth(c.getMonth() - 1);
			second = (s1 - c.getTime()) / 1000.0;
		}
		loadDevicePerf(second, _time);
	});
	changeColumn.initPopupMenuByArray({
		 selector: "#content",
		 menuId: "topoRefreshFreqId",
		 width: 200,
		 height: 100,
		 inputtype: "radio",
		 jsonData:[
		 	{id: 0, name:"无刷新"},
		 	{id: 5, name:"5秒钟"},
		 	{id: 300, name:"5分钟"},
		 	{id: 600, name:"10分钟"},
		 	{id: 1200, name:"20分钟"},
		 	{id: 1800, name:"30分钟"}
		 ],
		 converter: function(vo){
		 	return {value: vo.id, name: vo.name};
		 },
		 clickHandler: function($input){
		 	setAutoFunc(parseInt($input.val()));
		 	$input.attr("checked", true);
		 }
	});
	
	var $devEvent5217 = $("#devEvent5217");
	devEvent5217Func = function($this, id, level){
		var num = parseInt($this.find("td.times").text());
		if(num == 0){
			var str;
			switch(level){
				case 1: str = "Warning"; break;
				case 2: str = "Critical"; break;
				default: str = "Info"; break;
			}
			bAlert("目前没有侦测到设备的&级别的事件".replace("&", str), "提示");
			return;
		}
		var url = $(id).attr("url");
		if(url != undefined && url != null && (!isNaN(num))){
			window.location.href = url + level;
		}
		else {
			bAlert("请双击设备图标", "提示");
		}
	};
	$devEvent5217.find("#devEvent5217tr01").click(function(){
		devEvent5217Func($(this), "#devEvent5217", 2);
	});
	$devEvent5217.find("#devEvent5217tr02").click(function(){
		devEvent5217Func($(this), "#devEvent5217", 1);
	});
	$devEvent5217.find("#devEvent5217tr03").click(function(){
		devEvent5217Func($(this), "#devEvent5217", 0);
	});
	var myAppId = parseInt("${appId}");<%--跳转页面--%>
	var $menu = changeColumn.initPopupMenuByAjax({
		selector: "#content",
	   	menuId: "chooseAppMenuId",
	   	ifnodata: {id: "modelContent", content: "请添加应用，并刷新页面"},
	  	url: "${path}/servlet/topo/TopoAction?func=GetAllAppData1",
	   	params: {},
	   	width: 200,
	   	height: 200,
	   	selectedIndex: 0,
	   	selectedAppId: isNaN(myAppId)? undefined : myAppId,
	   	inputtype: "radio",
	   	converter: function(vo){
	   		return {value: vo.appid, name: vo.appname, nodecount: vo.nodecount, checked: vo.is_new};
	    },
	    clickHandler: function($input){
	   		$input.attr("checked", true);
	   		var appid = $input.val();
	   		$.ajax({
				url: "${path}/servlet/topo/TopoAction?func=GetTopoData",
				data: {appid: appid, devtype: 4},
				type: "post",
				dataType: "json",
				success: function(jsonData){
					if(jsonData.success){
						drawDevicesTopo(jsonData.value, {
							canvasPid: "modelContent",
							canvasId: "canvas",
							imgFolder: "${path}/resource/js/jtopo/img/",
							beforeDrawTopo: function(){ },
							afterDrawTopo: function(canvas, stage, scene){ }
						});
					}
					else { alert(jsonData.msg); }
					$("#showTipsWhenLoadTopo").hide();
				},
				beforeSend:function(){
					$("#showTipsWhenLoadTopo").show();
					$("#canvas").hide();
				},
				error: function (XMLHttpRequest, textStatus, errorThrown) { },
				complete: function(XHR, TS){ }
			});
	   }
	});
});
</script>
<script type="text/javascript">

var userAction = parseInt("${action}");
autoFunc = function(){
  	var appid = $("#chooseAppMenuId input[name='chooseAppMenuId']:checked").val();
  	if(isNaN(appid)){ return; }
  	var $cavans = $("#canvas");
	var $tip = $("#showTipsWhenLoadTopo");
  	$.ajax({
		url: "${path}/servlet/topo/TopoAction?func=GetTopoData",
		data: {appid: appid, devtype: 4, noRepaint: "yes"},
		type: "post",
		dataType: "json",
		success: function(jsonData){
			if(jsonData.success){
				refreshTopoWithoutRepaint($cavans, jsonData.value);
			}
			else { alert(jsonData.msg); }
			$cavans.show();
			$tip.hide();
		},
		beforeSend:function(){
			$tip.show();
			$cavans.hide();
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) { },
		complete: function(XHR, TS){ }
	});
};
<%--2015-03-03版拓扑
		jsonData = {
			appData: {},
			appPhyData: [{}, {}],
			vmData: [{}, {}],
		};
		opts = {
			canvasPid: "modelContent",
			canvasId: "canvas",
			beforeDrawTopo: function(){ },
			afterDrawTopo: function(canvas, stage, scene){ }
		}
--%>
	function drawDevicesTopo(jsonData, opts){
		if($.isFunction(opts.beforeDrawTopo)){ opts.beforeDrawTopo(); }
		var $canvas = $("#" +opts.canvasId);
		if($canvas.length > 0){ $canvas.remove(); }<%--解决canvas在不断刷新过程中内存不能及时释放，销毁一个canvas的方法--%>
		var $modelContent = $("#" + opts.canvasPid);
		var canvasHeight = $modelContent.parent().height() - 40;
		var canvasWidth = $modelContent.width();
		var canvasFmt = "<canvas id='canvas' height='%s' width='%s'>浏览器不支持HTML5技术，推荐火狐、Chrome和搜狗</canvas>";
		$canvas = $(canvasFmt.jFormat(canvasHeight, canvasWidth));
		$modelContent.append($canvas);
		var canvas = $canvas[0];
		
	    var stage = new JTopo.Stage(canvas);
	    var scene = new JTopo.Scene(stage);
	    scene.alpha = 1;
	    stage.wheelZoom = null;
	    JTopo.util.nodeAlarmColor = 35;
	    
	   	var containerCfg = {"业务系统": 80, "服务器": 200, "SAN网络": 320, "存储系统": 300};
	   	var ctrKeys = {"业务系统": "app", "服务器": "ser", "SAN网络": "san", "存储系统": "sto"};
	   	var containers = {};
	    var y = 5;
	    for(var key in containerCfg){
	    	var ctr = addContainer({
		     	title: key,
		     	x: 5,
		     	y: y,
		     	width: canvasWidth - 10,
		     	height: containerCfg[key]
		    });
	     	scene.add(ctr);
	     	containers[ctrKeys[key]] = ctr;
		    y += containerCfg[key] + 5;
	    }
	    if(jsonData.isTopoDataValid == false){ return; }
	    var imgFolder = opts.imgFolder;
	    var blankImagePath = imgFolder + "blank.png";
	    var appImagePath = imgFolder + "app.png";
		var vmImagePath = imgFolder + "vm.png";
		var hypImagePath = imgFolder + "hypervisor.png";
		var stoImagePath = imgFolder + "storage.png";
		var swImagePath = imgFolder + "switch.png";
		var swPortImagePath = imgFolder + "switchport.png";
		var poolImagePath = imgFolder + "pool.png";
		var volumeImagePath = imgFolder + "lun.png";
		var vmURL = "${path}/servlet/virtual/VirtualAction?func=VirtualInfo&computerId=%s&hypervisorId=%s&vmId=%s";
		var phyURL = "${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&computerId=%s&hypervisorId=%s";
		var swURL = "${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=";
		var stoURL = "${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=";
		var poolURL = "${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=%s&subSystemID=%s";
		var volURL = "${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid=%s&subSystemID=%s";
		var stoSRURL = "${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=%s&storageType=%s";
		var poolSRURL = "${path}/servlet/sr/pool/PoolAction?func=PoolInfo&subSystemID=%s&poolId=%s&storageType=%s";
		var volSRURL = "${path}/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=%s&volumeId=%s&storageType=%s";
		var devNameHref = "<a href='%s'>%s</a>";
		var tipDlgSelector = "#deviceAlertLog";
		var cacheKey = "pos2816";
		var isNodePos = false;
		var nodePos = jsonData.nodePos;
		if(nodePos){ isNodePos = true; }
		
		var MAX_TEXT_WIDTH = Number.MIN_VALUE;
		var appData = jsonData.appData;
		var nodeDragable = true;
		var isNodeXYKept = nodeDragable;
		var dispDevType = getDisplayDevType(appData.devtype);
		var appNode = getTopoNode({
	 		imgPath: appImagePath,
	 		devName: appData.appname,
	 		devNameLength: 28,
	 		dragable: nodeDragable,
	 		deviceInfo: {
	 			"名称": appData.appname,
	 			"类型": dispDevType
	 		},
	 		layout: null,
	 		devType: appData.devtype,
	 		displayDevType: dispDevType,
	 		stoType: null,
	 		tipDlgSelector: tipDlgSelector,
	 		ipAddress: null,
	 		devId: appData.app_id,
	 		alertInfo: null,
	 		otherInfo: [appData.appdesc],
	 		isNodeXYKept: isNodeXYKept,
	 		cacheKey: cacheKey
	 	}, $canvas, scene);
	 	if(isNodePos){
	 		var np = nodePos["App" + appNode.devId];
	 		if(np){ appNode.setLocation(np.x, np.y); }
	 	}
	 	if(appNode.textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = appNode.textWidth; }
	    
     	<%--应用连接若干个虚拟机--%>
     	var virtual = {};
	    var map = jsonData.appVMMap;
	    if(isDataValid(map)){
	    	var data = jsonData.vmDataSR;
	     	var logs = jsonData.vmLogs;
	     	for(var i = 0, len = map.length, m, d, id, layout, dispDevType, np, url; i < len; ++i){
				m = map[i];
				id = m.id;
				d = data[m.id];
				url = vmURL.jFormat(d.comp_id, d.hyp_id, id);
				if(virtual[id] == undefined){
					layout = null;
					dispDevType = getDisplayDevType(m.devtype);
					virtual[id] = getTopoNode({
				 		imgPath: vmImagePath,
				 		devName: d.vm_name,
				 		devNameLength: 28,
				 		dragable: nodeDragable,
				 		deviceInfo: {
				 			"名称": devNameHref.jFormat(url, d.vm_name),
				 			"类型": dispDevType,
				 			"IP地址": d.vm_ip,
				 			"磁盘容量(G)": toFixed2(d.total),
				 			"磁盘可用容量(G)": toFixed2(d.available),
				 			"磁盘已用容量(G)": toFixed2(d.used),
				 			"磁盘使用情况(%)": getPercentageLabel(d.total, d.used, 0.6, 0.85)
				 		},
				 		layout: layout,
				 		devType: m.devtype,
				 		displayDevType: dispDevType,
				 		stoType: m.db_type,
				 		tipDlgSelector: tipDlgSelector,
				 		devId: id,
				 		detailPageURL: url,
				 		alertInfo: getDeviceLogs2(logs, id, 5),
				 		isNodeXYKept: isNodeXYKept,
				 		cacheKey: cacheKey,
				 		dbclickEventFunc: function(event){
				 			loadDeviceInfo(event.target, 2);
				 			$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
				 		}
				 	}, $canvas, scene);
				 	virtual[id].comp_id = d.comp_id;
				 	virtual[id].hyp_id = d.hyp_id;
				 	virtual[id].osType = d.os_type;
				 	if(isNodePos){
				 		np = nodePos["Virtual" + id];
				 		if(np){ virtual[id].setLocation(np.x, np.y); }
				 	}
				}
				if(virtual[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = virtual[id].textWidth; }
				scene.add(addLink(appNode, virtual[id], null));
			}
	    }
	    
	    <%--若干个虚拟机连接若干个物理机--%>
	    var physical = {};
	    function getPhysicalNode(map, dev, id, layout, alertInfo){
			var dispDevType = getDisplayDevType(m.devtype);
			var url = phyURL.jFormat(dev.comp_id, id);
			var node = getTopoNode({
		 		imgPath: hypImagePath,
		 		devName: dev.hyp_name,
		 		devNameLength: 28,
		 		dragable: nodeDragable,
		 		deviceInfo: {
		 			"名称": devNameHref.jFormat(url, dev.hyp_name),
		 			"类型": dispDevType,
		 			"IP地址": dev.hyp_ip,
		 			"磁盘容量(G)": toFixed2(dev.total),
		 			"磁盘可用容量(G)": toFixed2(dev.available),
		 			"磁盘已用容量(G)": toFixed2(dev.used),
		 			"磁盘使用情况(%)": getPercentageLabel(dev.total, dev.used, 0.6, 0.85)
		 		},
		 		layout: layout,
		 		devType: m.devtype,
		 		displayDevType: dispDevType,
		 		stoType: dev.db_type,
		 		tipDlgSelector: tipDlgSelector,
		 		devId: dev.hyp_id,
		 		alertInfo: alertInfo,
		 		isNodeXYKept: isNodeXYKept,
		 		cacheKey: cacheKey,
		 		detailPageURL: url,
		 		dbclickEventFunc: function(event){
		 			loadDeviceInfo(event.target, 3);
		 			<%--显示事件的tab，并且确定level--%>
		 			$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
		 		}
		 	}, $canvas, scene);
		 	node.comp_id = dev.comp_id;
		 	node.osType = dev.os_type;
		 	if(isNodePos){
		 		var np = nodePos["Physical" + id];
		 		if(np){ node.setLocation(np.x, np.y); }
		 	}
			return node;
		};
	    map = jsonData.vmPhyMap;
	    if(isDataValid(map)){
	    	var data = jsonData.phyDataSR;
	     	var logs = jsonData.phyLogs;
	     	for(var i = 0, len = map.length, m, id, layout, dispDevType; i < len; ++i){
				m = map[i];
				id = m.id;
				if(physical[id] == undefined){
					layout = null;
					physical[id] = getPhysicalNode(m, data[m.id], id, layout, getDeviceLogs2(logs, id, 5));
				}
				if(physical[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = physical[id].textWidth; }
				scene.add(addLink(virtual[m.pid], physical[id], null));
			}
	    }
	    
	    <%--一个应用连接若干个物理机--%>
	    map = jsonData.appPhyMap;
	    if(isDataValid(map)){
	     	var logs = jsonData.phyLogs;
	     	var data = jsonData.phyDataSR;
	     	for(var i = 0, len = map.length, m, id, layout; i < len; ++i){
				m = map[i];
				id = m.id;
				if(physical[id] == undefined){
					layout = null;
					physical[id] = getPhysicalNode(m, data[id], id, layout, getDeviceLogs2(logs, id, 5));
				}
				if(physical[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = physical[id].textWidth; }
				scene.add(addLink(appNode, physical[id], null));
			}
	    }
	    
	    <%--若干个物理机连接若干个物理机端口--%>
	    map = jsonData.phyPhypMap;
	    var physicalPort = {};
	    if(isDataValid(map)){
	    	data = jsonData.phypDataSR;
	     	for(var i = 0, len = map.length, m, dev, id, layout, dispDevType; i < len; ++i){
				m = map[i];
				id = m.id;
				dev = data[m.id];
				if(physicalPort[id] == undefined){
					layout = null;
					dispDevType = getDisplayDevType(m.devtype);
					physicalPort[id] = getTopoNode({
				 		imgPath: swPortImagePath,
				 		devName: dev.port_name,
				 		devNameLength: 28,
				 		dragable: nodeDragable,
				 		deviceInfo: {
				 			"名称": dev.port_name,
				 			"类型": dispDevType,
				 			"端口号": dev.port_number,
				 			"端口类型": dev.port_type
						},
				 		layout: layout,
				 		devType: m.devtype,
				 		displayDevType: dispDevType,
				 		stoType: dev.db_type,
				 		tipDlgSelector: tipDlgSelector,
				 		devId: dev.port_id,
				 		alertInfo: getDeviceLogs2(logs, id, 5),
				 		isNodeXYKept: isNodeXYKept,
				 		cacheKey: cacheKey,
				 		dbclickEventFunc: function(event){
				 			loadDeviceInfo(event.target, 2);
				 			$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
				 		}
				 	}, $canvas, scene);
				}
				physicalPort[id].osType = dev.os_type;
				if(physicalPort[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = physicalPort[id].textWidth; }
				scene.add(addLink(physical[m.pid], physicalPort[id], null));
			}
	    }
	    <%--交换机端口--%>
	    var switchport = {};
	    var operStatusFmt = "<span class='label %s'>%s</span>";
	    function getSwitchportNode(m, dev, id, layout, alertInfo){
			var dispDevType = getDisplayDevType(m.devtype);
			var node = getTopoNode({
		 		imgPath: swPortImagePath,
		 		devName: dev.port_name,
		 		devNameLength: 28,
		 		dragable: nodeDragable,
		 		deviceInfo: {
		 			"名称": dev.port_name,
		 			"类型": dispDevType,
		 			"端口号": dev.port_number,
		 			"端口类型": dev.port_type,
		 			"端口速率(MB)": dev.port_speed,
		 			"操作状态": operStatusFmt.jFormat(getOperationalStatusCSS(dev.oper_status), dev.oper_status),
		 			"硬件状态": operStatusFmt.jFormat(getConsolidateStatusCSS(dev.consolidated), dev.consolidated)
		 		},
		 		layout: layout,
		 		devType: m.devtype,
		 		displayDevType: dispDevType,
		 		stoType: dev.db_type,
		 		tipDlgSelector: tipDlgSelector,
		 		devId: id,
		 		alertInfo: alertInfo,
		 		isNodeXYKept: isNodeXYKept,
		 		cacheKey: cacheKey,
		 		dbclickEventFunc: function(event){
		 			loadDeviceInfo(event.target, 2);
		 			$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
		 		}
		 	}, $canvas, scene);
		 	node.sw_id = dev.sw_id;
		 	node.swp_id = dev.port_id;
		 	node.osType = dev.os_type;
		 	
		 	return node;
	    }
	    <%--交换机--%>
	    var switches = {};
	    function getSwitchNode(m, dev, id, layout, alertInfo){
			var dispDevType = getDisplayDevType(m.devtype);
			var url = swURL + dev.sw_id;
			var node = getTopoNode({
		 		imgPath: swImagePath,
		 		devName: dev.sw_name,
		 		devNameLength: 28,
		 		dragable: nodeDragable,
		 		deviceInfo: {
		 			"名称": devNameHref.jFormat(url, dev.sw_name),
		 			"类型": dispDevType,
		 			"IP地址": dev.sw_ip,
		 			"WWN": dev.sw_wwn,
		 			"状态": dev.prop_status,
		 			"操作状态": dev.oper_status,
		 			"硬件状态": dev.cons_status
		 		},
		 		layout: layout,
		 		devType: m.devtype,
		 		displayDevType: dispDevType,
		 		stoType: dev.db_type,
		 		tipDlgSelector: tipDlgSelector,
		 		devId: id,
		 		alertInfo: alertInfo,
		 		isNodeXYKept: isNodeXYKept,
		 		cacheKey: cacheKey,
		 		detailPageURL: url,
		 		dbclickEventFunc: function(event){
		 			loadDeviceInfo(event.target, 4);
		 			$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
		 		}
		 	}, $canvas, scene);
		 	node.sw_id = dev.sw_id;
		 	if(isNodePos){
		 		var np = nodePos["Switch" + id];
		 		if(np){ node.setLocation(np.x, np.y); }
		 	}
		 	return node;
	    }
	    
	    <%--SR和TPC存储端口--%>
	    var storageportTPC = {};
	    var storageportSR = {};
	    function getStorageportNode(m, dev, id, layout, alertInfo){
			var dispDevType = getDisplayDevType(m.devtype);
			var node = getTopoNode({
		 		imgPath: swPortImagePath,
		 		devName: dev.stop_name,
		 		devNameLength: 28,
		 		dragable: nodeDragable,
		 		deviceInfo: {
		 			"名称": dev.stop_name,
		 			"类型": dispDevType,
		 			"端口速率(MB)": dev.port_speed,
		 			"操作状态": operStatusFmt.jFormat(getOperationalStatusCSS(dev.oper_status), dev.oper_status)
		 		},
		 		layout: layout,
		 		devType: m.devtype,
		 		displayDevType: dispDevType,
		 		stoType: dev.db_type,
		 		tipDlgSelector: tipDlgSelector,
		 		devId: id,
		 		alertInfo: alertInfo,
		 		isNodeXYKept: isNodeXYKept,
		 		cacheKey: cacheKey,
		 		dbclickEventFunc: function(event){
		 			$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
		 		}
		 	}, $canvas, scene);
		 	node.sto_id = dev.sto_id;
		 	node.stop_id = dev.stop_id;
		 	node.osType = dev.os_type;
		 	return node;
	    }
	    
	    <%--SR和TPC存储系统--%>
	    var storageTPC = {};
	    var storageSR = {};
	    function getStorageNode(m, dev, id, layout, alertInfo){
			var dispDevType = getDisplayDevType(m.devtype);
			var url = dev.db_type == "SR"? (stoSRURL.jFormat(dev.sto_id, dev.os_type)) : (stoURL + dev.sto_id);
			var node = getTopoNode({
		 		imgPath: stoImagePath,
		 		devName: dev.sto_name,
		 		devNameLength: 28,
		 		dragable: nodeDragable,
		 		deviceInfo: {
		 			"名称": devNameHref.jFormat(url, dev.sto_name),
		 			"类型": dispDevType,		 			
		 			"IP地址": dev.sto_ip,
		 			"磁盘容量(G)": toFixed2(dev.total),
		 			"磁盘可用容量(G)": toFixed2(dev.available),
		 			"磁盘已用容量(G)": toFixed2(dev.used),
		 			"磁盘使用情况(%)": getPercentageLabel(dev.total, dev.used, 0.6, 0.85),
		 			"操作状态": operStatusFmt.jFormat(getOperationalStatusCSS(dev.oper_status), dev.oper_status? dev.oper_status : "Unknown")
		 		},
		 		layout: layout,
		 		devType: m.devtype,
		 		displayDevType: dispDevType,
		 		stoType: dev.db_type,
		 		tipDlgSelector: tipDlgSelector,
		 		devId: id,
		 		alertInfo: alertInfo,
		 		isNodeXYKept: isNodeXYKept,
		 		cacheKey: cacheKey,
		 		detailPageURL: url,
		 		dbclickEventFunc: function(event){
		 			loadDeviceInfo(event.target, 5);
		 			$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
		 		}
		 	}, $canvas, scene);
		 	node.sto_id = dev.sto_id;
		 	node.osType = dev.os_type;
		 	if(isNodePos){
		 		var np = nodePos["Storage" + id];
		 		if(np){ node.setLocation(np.x, np.y); }
		 	}
		 	return node;
	    }
	    
	    <%--SR和TPC存储池--%>
	    var stopoolSR = {}, stopoolTPC = {};
	    function getStoragePoolNode(map, dev, id, layout, alertInfo){
	    	if(dev == undefined){ return dev; }
			var dispDevType = getDisplayDevType(map.devtype);
			var url = (dev.db_type == "SR"? poolSRURL.jFormat(dev.sto_id, dev.pool_id, dev.os_type) : poolURL.jFormat(dev.pool_id, dev.sto_id));
			var node = getTopoNode({
				imgPath: poolImagePath,
				devName: dev.pool_name? dev.pool_name : "Unknown",
				devNameLength: 28,
				dragable: nodeDragable,
				deviceInfo: {
					"名称": dev.pool_name? devNameHref.jFormat(url, dev.pool_name) : "Unknown",
					"类型": dispDevType,
					"存储池容量(G)": toFixed2(dev.total),
					"存储池可用容量(G)": toFixed2(dev.available),
					"存储池已用容量(G)": toFixed2(dev.used),
					"存储池使用情况(%)": getPercentageLabel(dev.total, dev.used, 0.6, 0.85),
					"存储卷数量": dev.num_lun,
					"操作状态": operStatusFmt.jFormat(getOperationalStatusCSS(dev.oper_status), dev.oper_status? dev.oper_status : "Unknown")
				},
				layout: layout,
				devType: m.devtype,
				displayDevType: dispDevType,
				stoType: dev.db_type,
				tipDlgSelector: tipDlgSelector,
				devId: id,
				alertInfo: alertInfo,
				isNodeXYKept: isNodeXYKept,
				cacheKey: cacheKey,
				detailPageURL: url,
				dbclickEventFunc: function(event){
					loadDeviceInfo(event.target, 6);
					$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
				}
			}, $canvas, scene);
			node.sto_id = dev.sto_id;
			node.pool_id = dev.pool_id;
			node.osType = dev.os_type;
			if(isNodePos){
		 		var np = nodePos["Pool" + id];
		 		if(np){ node.setLocation(np.x, np.y); }
		 	}
			return node;
		}
		
		<%--SR和TPC存储卷--%>
		var stoVolSR = {}, stoVolTPC = {};
	    function getStorageVolumeNode(map, dev, id, layout, alertInfo){
			var dispDevType = getDisplayDevType(map.devtype);
			if(dev == undefined){ dev = {vol_name: "", redundancy: "", total: 0, used: 0 }; }
			var url = dev.db_type == "SR"? volSRURL.jFormat(dev.sto_id, dev.vol_id, dev.os_type) : volURL.jFormat(dev.vol_id, dev.sto_id);
			var node = getTopoNode({
				imgPath: volumeImagePath,
				devName: dev.vol_name? dev.vol_name : "Unknown",
				devNameLength: 28,
				dragable: nodeDragable,
				deviceInfo: {
					"名称": dev.vol_name? devNameHref.jFormat(url, dev.vol_name) : "Unknown",
					"类型": dispDevType,
					"冗余级别": dev.redundancy,
					"存储卷容量(G)": toFixed2(dev.total),
					"存储卷可用容量(G)": toFixed2(dev.total - dev.used),
					"存储卷已用容量(G)": toFixed2(dev.used),
					"存储卷使用情况(%)": getPercentageLabel(dev.total, dev.used, 0.6, 0.85),
					"操作状态": operStatusFmt.jFormat(getOperationalStatusCSS(dev.oper_status), dev.oper_status? dev.oper_status : "Unknown")
				},
				layout: layout,
				devType: map.devtype,
				displayDevType: dispDevType,
				stoType: dev.db_type,
				tipDlgSelector: tipDlgSelector,
				devId: id,
				alertInfo: alertInfo,
				isNodeXYKept: isNodeXYKept,
				cacheKey: cacheKey,
				detailPageURL: url,
				dbclickEventFunc: function(event){
					loadDeviceInfo(event.target, 7);
					$("#devEvent5217").attr("url", event.target.detailPageURL + "&tabToShow=3&state=0&level=");
				}
			}, $canvas, scene);
			node.sto_id = dev.sto_id;
			node.vol_id = dev.vol_id;
			node.osType = dev.os_type;
			if(isNodePos){
		 		var np = nodePos["Volume" + id];
		 		if(np){ node.setLocation(np.x, np.y); }
		 	}
			return node;
		}
	    <%--若干个物理机端口连接若干个交换机端口--%>
	    map = jsonData.phypSwpMap;
	    if(isDataValid(map)){
	    	var data = jsonData.swpDataTPC;
	     	var logs = jsonData.swpLogs;
	     	for(var i = 0, len = map.length, m, id, layout, dispDevType; i < len; ++i){
				m = map[i];
				id = m.id;
				if(switchport[id] == undefined){
					layout = null;
					switchport[id] = getSwitchportNode(m, data[m.id], id, layout, getDeviceLogs2(logs, id, 5));
				}
				if(switchport[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = switchport[id].textWidth; }
				scene.add(addLink(physicalPort[m.pid], switchport[id], null));
			}
	    }
	    
	    <%--若干个交换机端口连接若干个交换机--%>
	    var swpSwMap = jsonData.swpSwMap;
	    var switches = {};
	    if(isDataValid(swpSwMap)){
	    	var swpSwpMap = jsonData.swpSwpMap;
	    	var swSwpMap = jsonData.swSwpMap;
	    	var swDataTPC = jsonData.swDataTPC;
	    	var swpDataTPC = jsonData.swpDataTPC;
	    	var swpLogs = jsonData.swpLogs;
	     	var swLogs = jsonData.swLogs;
	    	for(var i = 0, len = swpSwMap.length, m, id, layout; i < len; ++i){
	    		m = swpSwMap[i];
	    		id = m.id;
	    		if(switches[id] == undefined){
	    			layout = null; <%--所有交换机的日志--%>
	    			switches[id] = getSwitchNode(m, swDataTPC[m.id], id, layout, getDeviceLogs2(swLogs, id, 5));
	    		}
	    	}
	     	
	     	for(var i = 0, len = swpSwpMap.length, m, id, layout; i < len; ++i){
	    		m = swpSwpMap[i];
				id = m.id;
				if(switchport[id] == undefined){
					layout = null;
					switchport[id] = getSwitchportNode(m, swpDataTPC[m.id], id, layout, getDeviceLogs2(swpLogs, id, 5));
				}
	    	}
	    	
	    	for(var i = 0, len = swSwpMap.length, m, id, layout; i < len; ++i){
	    		m = swSwpMap[i];
				id = m.id;
				if(switchport[id] == undefined){
					layout = null;
					switchport[id] = getSwitchportNode(m, swpDataTPC[m.id], id, layout, getDeviceLogs2(swpLogs, id, 5));
				}
	    	}
	     	
	     	for(var i = 0, len = swpSwpMap.length, m, id, layout; i < len; ++i){
	    		m = swpSwpMap[i];
				id = m.id;
				if(switchport[id] == undefined){
					layout = null;
					switchport[id] = getSwitchportNode(m, swpDataTPC[m.id], id, layout, getDeviceLogs2(swpLogs, id, 5));
				}
	    	}
	    	<%--添加link--%>
	    	var link = {};
	    	for(var i = 0, len = swpSwMap.length, m, id; i < len; ++i){
	    		m = swpSwMap[i];
	    		id = "swp%ssw%s".jFormat(m.pid, m.id);
	    		if(link[id] == undefined){
	    			if(switches[m.id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = switches[m.id].textWidth; }
	    			scene.add(addLink(switchport[m.pid], switches[m.id], null));
	    			link[id] = 1;
	    		}
	    	}
	    	for(var i = 0, len = swpSwpMap.length, m, id; i < len; ++i){
	    		m = swpSwpMap[i];
	    		id = "swp%sswp%s".jFormat(m.pid, m.id);
	    		if(link[id] == undefined){
	    			scene.add(addLink(switchport[m.pid], switchport[m.id], null));
	    			link[id] = 1;
	    		}
	    	}
	    	for(var i = 0, len = swSwpMap.length, m, id; i < len; ++i){
	    		m = swSwpMap[i];
	    		id = "sw%sswp%s".jFormat(m.pid, m.id);
	    		if(link[id] == undefined){
	    			scene.add(addLink(switches[m.pid], switchport[m.id], null));
	    			link[id] = 1;
	    		}
	    	}
		    var stopDataTPC = jsonData.stopDataTPC;
		    var stopStoTPCMap = jsonData.stopStoTPCMap;
		    var swpStopTPCMap = jsonData.swpStopTPCMap;
		    var swpStopSRMap = jsonData.swpStopSRMap;
		    
		    if(isDataValid(swpStopSRMap)){
		    	var logs = jsonData.stopSRLogs;
		    	for(var i = 0, len = swpStopSRMap.length, m, id, layout; i < len; ++i){
			    	m = swpStopSRMap[i];
			    	if(storageportSR[m.id] == undefined){
			    		layout = null;
			    		storageportSR[m.id] = getStorageportNode(m, stopDataTPC[m.id], m.id, layout, getDeviceLogs2(logs, m.id, 5));
			    	}
			    	scene.add(addLink(switchport[m.pid], storageportTPC[m.id], null));
			    }
		    }
		    
		    if(isDataValid(swpStopTPCMap)){
		    	var logs = jsonData.stopTPCLogs;
		    	for(var i = 0, len = swpStopTPCMap.length, m, id, layout; i < len; ++i){
			    	m = swpStopTPCMap[i];
			    	if(storageportTPC[m.id] == undefined){
			    		layout = null;
			    		storageportTPC[m.id] = getStorageportNode(m, stopDataTPC[m.id], m.id, layout, getDeviceLogs2(logs, m.id, 5));
			    	}
			    	scene.add(addLink(switchport[m.pid], storageportTPC[m.id], null));
			    }
		    }
		    
		    if(isDataValid(jsonData.stopStoTPCMap)){
		    	var map = jsonData.stopStoTPCMap;
		    	var logs = jsonData.stoTPCLogs;
		    	var data = jsonData.stoDataTPC;
		     	for(var i = 0, len = map.length, m, id, layout; i < len; ++i){
					m = map[i];
					id = m.id;
					if(storageTPC[id] == undefined){
						layout = null;
						storageTPC[id] = getStorageNode(m, data[m.id], id, layout, getDeviceLogs2(logs, id, 5));
					}
					scene.add(addLink(storageportTPC[m.pid], storageTPC[id], null));
				}
		    }
		    
		    if(isDataValid(jsonData.stopStoSRMap)){
				var map = jsonData.stopStoSRMap;
				var logs = jsonData.stoSRLogs;
				var data = jsonData.stoDataSR;
				for(var i = 0, len = map.length, m, id, layout; i < len; ++i){
					m = map[i];
					id = m.id;
					if(storageSR[id] == undefined){
						layout = null;
						storageSR[id] = getStorageNode(m, data[m.id], id, layout, getDeviceLogs2(logs, id, 5));
					}
					scene.add(addLink(storageportSR[m.pid], storageSR[id], null));
				}
			}
	    }
	    map = jsonData.phySwMap;
	    if(isDataValid(map)){
	    	var data = jsonData.swDataTPC;
	    	var swLogs = jsonData.swLogs;
	    	for(var i = 0, len = map.length, m, id, layout; i < len; ++i){
	    		m = map[i];
	    		id = m.id;
	    		if(switches[id] == undefined){
	    			layout = null;
	    			switches[id] = getSwitchNode(m, data[m.id], id, layout, getDeviceLogs2(swLogs, id, 5));
	    		}
	    		if(switches[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = switches[id].textWidth; }
	    		scene.add(addLink(physical[m.pid], switches[id], null));
	    	}
	    }
	    map = jsonData.swSwMap;
	    if(isDataValid(map)){
	    	var data = jsonData.swDataTPC;
	    	var swLogs = jsonData.swLogs;
	    	var SwSwLink = {};
	    	for(var i = 0, len = map.length, m, id, layout, swid_01, swid_02; i < len; ++i){
	    		m = map[i];
	    		id = m.id;
	    		if(m.pid == m.id){ continue; }
	    		if(switches[id] == undefined){
	    			layout = null;
	    			switches[id] = getSwitchNode(m, data[m.id], id, layout, getDeviceLogs2(swLogs, id, 5));
	    		}
	    		if(switches[m.pid] == undefined){
	    			layout = null;
	    			switches[m.pid] = getSwitchNode(m, data[m.pid], m.pid, layout, getDeviceLogs2(swLogs, m.pid, 5));
	    		}
	    		swid_01 = m.pid + "," + id;
	    		swid_02 = id + "," + m.pid;
	    		if(SwSwLink[swid_01] == undefined && SwSwLink[swid_02] == undefined){
	    			if(switches[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = switches[id].textWidth; }
	    			scene.add(addLink(switches[m.pid], switches[id], null));
	    			SwSwLink[swid_01] = 1;
	    			SwSwLink[swid_02] = 1;
	    		}
	    	}
	    	SwSwLink = null;
	    }
	    
	    map = jsonData.swStoTPCMap;
	    var swStoTPCLink = {};
	    if(isDataValid(map)){
	    	var data = jsonData.stoDataTPC;
	    	var logs = jsonData.stoTPCLogs;
	    	for(var i = 0, len = map.length, m, id, layout, linkKey; i < len; ++i){
	    		m = map[i];
	    		id = m.id;
	    		if(storageTPC[id] == undefined){
	    			layout = null;
	    			storageTPC[id] = getStorageNode(m, data[id], id, layout, getDeviceLogs2(logs, id, 5));
	    		}
	    		if(switches[m.pid] == undefined){
	    			layout = null;
	    			switches[m.pid] = getSwitchNode(m, jsonData.swDataTPC[m.pid], m.pid, layout, getDeviceLogs2(jsonData.swLogs, m.pid, 5));
	    		}
	    		linkKey = m.pid + "_" + id;
	    		if(swStoTPCLink[linkKey] == undefined){
	    			if(storageTPC[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = storageTPC[id].textWidth; }
	    			scene.add(addLink(switches[m.pid], storageTPC[id], null));
	    			swStoTPCLink[linkKey] = 1;
	    		}
	    	}
	    }
	    
	    map = jsonData.swStoSRMap;
	    var swStoSRLink = {};
	    if(isDataValid(map)){
	    	var data = jsonData.stoDataSR;
	    	var logs = jsonData.stoSRLogs;
	    	for(var i = 0, len = map.length, m, id, layout, linkKey; i < len; ++i){
	    		m = map[i];
	    		id = m.id;
	    		if(storageSR[id] == undefined){
	    			layout = null;
	    			storageSR[id] = getStorageNode(m, data[m.id], id, layout, getDeviceLogs2(logs, id, 5));
	    		}
	    		linkKey = m.pid + "_" + id;
	    		if(swStoSRLink[linkKey] == undefined){
	    			if(storageSR[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = storageSR[id].textWidth; }
	    			scene.add(addLink(switches[m.pid], storageSR[id], null));
	    			swStoSRLink[linkKey] = 1;
	    		}
	    	}
	    }
	    
	    var stoPoolTPCLink = {};
	    map = jsonData.stoPoolTPCMap;
	    if(isDataValid(map)){
	    	var data = jsonData.stoPoolDataTPC;
	    	var logs = jsonData.stoPoolTPCLogs;
	    	for(var i = 0, len = map.length, m, id, layout, n1, n2; i < len; ++i){
				m = map[i];
				id = m.id;
				n1 = stopoolTPC[id];
				if(n1 == undefined){
					layout = null;
					n1 = getStoragePoolNode(m, data[id], id, layout, getDeviceLogs2(logs, id, 5));
				}
				n2 = storageTPC[m.pid];
				if(n1 && n2){
					if(n1.textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = n1.textWidth; }
					if(stoPoolTPCLink[m.pid] != id){
						scene.add(addLink(n2, n1, null));
						stoPoolTPCLink[m.pid] = id;
					}
					stopoolTPC[id] = n1;
				}
			}
	    }
	    var stoPoolSRLink = {};
	    map = jsonData.stoPoolSRMap;
	    if(isDataValid(map)){
	    	var data = jsonData.stoPoolDataSR;
	    	var logs = jsonData.stoPoolSRLogs;
	    	for(var i = 0, len = map.length, m, dev, id, layout, n1, n2; i < len; ++i){
				m = map[i];
				id = m.id;
				if(stopoolSR[id] == undefined){
					layout = null;
					stopoolSR[id] = getStoragePoolNode(m, data[id], id, layout, getDeviceLogs2(logs, id, 5));
				}
				n1 = storageSR[m.pid];
				n2 = stopoolSR[id];
				if(n1 && n2){
					if(stopoolSR[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = stopoolSR[id].textWidth; }
					if(stoPoolSRLink[m.pid] != id){
						scene.add(addLink(n1, n2, null));
						stoPoolSRLink[m.pid] = id;
					}
				
				}
			}
	    }
	    
		map = jsonData.stoVolTPCMap;
	    if(isDataValid(map)){
	    	var data = jsonData.stoVolDataTPC;
	    	var logs = jsonData.stoVolTPCLogs;
	    	for(var i = 0, len = map.length, m, id, layout; i < len; ++i){
				m = map[i];
				id = m.id;
				if(stoVolTPC[id] == undefined){
					layout = null;
					stoVolTPC[id] = getStorageVolumeNode(m, data[id], id, layout, getDeviceLogs2(logs, id, 5));
				}
				if(stoVolTPC[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = stoVolTPC[id].textWidth; }
				scene.add(addLink(stopoolTPC[m.pid], stoVolTPC[id], null));
			}
	    }
	    map = jsonData.stoVolSRMap;
	    if(isDataValid(map)){
	    	var data = jsonData.stoVolDataSR;
	    	var logs = jsonData.stoVolSRLogs;
	    	for(var i = 0, len = map.length, m, dev, id, layout; i < len; ++i){
				m = map[i];
				id = m.id;
				if(stoVolSR[id] == undefined){
					layout = null;
					stoVolSR[id] = getStorageVolumeNode(m, data[id], id, layout, getDeviceLogs2(logs, id, 5));
				}
				if(stoVolSR[id].textWidth > MAX_TEXT_WIDTH){ MAX_TEXT_WIDTH = stoVolSR[id].textWidth; }
				scene.add(addLink(stopoolSR[m.pid], stoVolSR[id], null));
			}
	    }
		if(jsonData.nodePos){
	    	scene.autoLayout = false;
	    	var pos2816 = {};
	    	var node = appNode;
	    	pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	for(var key in virtual){
	    		var node = virtual[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in physical){
	    		var node = physical[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in switches){
	    		var node = switches[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in storageTPC){
	    		var node = storageTPC[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in storageSR){
	    		var node = storageSR[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in stopoolTPC){
	    		var node = stopoolTPC[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in stopoolSR){
	    		var node = stopoolSR[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in stoVolTPC){
	    		var node = stoVolTPC[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	for(var key in stoVolSR){
	    		var node = stoVolSR[key];
	    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
	    	}
	    	$canvas.data("pos2816", pos2816);
	    	JTopo.layout.layoutNode(scene, appNode, true);
	    }
	    else {
	    	scene.autoLayout = true;
	    	var pos2816 = {};
	    	scene.doLayout(JTopo.layout.TreeLayout("down", 28 + MAX_TEXT_WIDTH, 100));
	    	function getWidth(x){
	    		if(x > 0){
		    		width = x + 150;
		    		while(width > canvasWidth){ width -= canvasWidth; }
		    	}
		    	else {
		    		while(width < 0){ width += canvasWidth; }
		    	}
	    		return width;
	    	};
	    	var nodeHGap = 50;
	    	var _nodeHGap = 20;
	    	appNode.x = getWidth(appNode.x);
	    	appNode.y = 15;
	    	var ctr = containers["app"];
	    	ctr.height = appNode.y + appNode.height + _nodeHGap;
	    	pos2816[appNode.devType + "," + appNode.devId] = { x: appNode.x, y: appNode.y};
	    	
	    	var minY = Number.MAX_VALUE, 
	    	maxY = Number.MIN_VALUE, nodeHeight;
	    	var map = jsonData.appVMMap;
	    	var data = virtual;
	    	if(isDataValid(map)){
		    	for(var i = 0, len = map.length, m, node, h; i < len; ++i){
		    		m = map[i];
		    		node = data[m.id];
		    		node.x = getWidth(node.x);
		    		h = appNode.y + appNode.height;
		    		if(node.y < h){ node.y = h + nodeHGap; }
		    		if(minY > node.y){ minY = node.y; }
		    		if(maxY < node.y){ maxY = node.y; }
		    		nodeHeight = node.height;
		    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    	}
		    }
	    	
	    	map = jsonData.vmPhyMap;
	    	data = physical;
	    	var pdata = virtual;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		node.x = getWidth(node.x);
		    		h = pNode.y + pNode.height;
		    		if(node.y < h){ node.y = h + nodeHGap; }
		    		if(minY > node.y){ minY = node.y; }
		    		if(maxY < node.y){ maxY = node.y; }
		    		nodeHeight = node.height;
		    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    	}
	    	}
	    	
	    	map = jsonData.appPhyMap;
	    	data = physical;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, node, h; i < len; ++i){
		    		m = map[i];
		    		node = data[m.id];
		    		node.x = getWidth(node.x);
		    		h = appNode.y + appNode.height;
		    		if(node.y < h){ node.y = h + nodeHGap; }
		    		if(minY > node.y){ minY = node.y; }
		    		if(maxY < node.y){ maxY = node.y; }
		    		nodeHeight = node.height;
		    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    	}
	    	}
	    	ctr = containers["ser"];
	    	ctr.y = minY - 5;
	    	ctr.height = maxY - minY + nodeHeight + nodeHGap;
	    	
	    	minY = Number.MAX_VALUE;
	    	maxY = Number.MIN_VALUE;
	    	map = jsonData.phySwMap;
	    	data = switches;
	    	pdata = physical;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		node.x = getWidth(node.x);
		    		h = pNode.y + pNode.height;
		    		if(node.y < h){ node.y = h + nodeHGap; }
		    		if(minY > node.y){ minY = node.y; }
		    		if(maxY < node.y){ maxY = node.y; }
		    		nodeHeight = node.height;
		    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    	}
	    	}
	    	
	    	map = jsonData.swSwMap;
	    	data = switches;
	    	pdata = switches;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		node.x = getWidth(node.x);
		    		h = pNode.y + pNode.height;
		    		if(node.y < h){ node.y = h + nodeHGap; }
		    		if(minY > node.y){ minY = node.y; }
		    		if(maxY < node.y){ maxY = node.y; }
		    		nodeHeight = node.height;
		    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    	}
	    	}
	    	
	    	ctr = containers["san"];
	    	ctr.y = minY - 5;
	    	ctr.height = maxY - minY + nodeHeight + nodeHGap;
	    	
	    	minY = Number.MAX_VALUE;
	    	maxY = Number.MIN_VALUE;
	    	var map = jsonData.swStoTPCMap;
	    	var data = storageTPC;
	    	var pdata = switches;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		if(pNode == undefined || pNode == null){ continue; }
		    		node = data[m.id];
		    		node.x = getWidth(node.x);
		    		h = pNode.y + pNode.height;
		    		if(node.y < h){ node.y = h + nodeHGap; }
		    		if(minY > node.y){ minY = node.y; }
		    		if(maxY < node.y){ maxY = node.y; }
		    		nodeHeight = node.height;
		    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    	}
	    	}
	    	
	    	map = jsonData.swStoSRMap;
	    	data = storageSR;
	    	pdata = switches;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		node.x = getWidth(node.x);
		    		h = pNode.y + pNode.height;
		    		if(node.y < h){ node.y = h + nodeHGap; }
		    		if(minY > node.y){ minY = node.y; }
		    		if(maxY < node.y){ maxY = node.y; }
		    		nodeHeight = node.height;
		    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    	}
	    	}
	    	
	    	map = jsonData.stoPoolTPCMap;
	    	data = stopoolTPC;
	    	pdata = storageTPC;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		if(pNode && node){
		    			node.x = getWidth(node.x);
			    		h = pNode.y + pNode.height;
			    		if(node.y < h){ node.y = h + nodeHGap; }
			    		if(minY > node.y){ minY = node.y; }
			    		if(maxY < node.y){ maxY = node.y; }
			    		nodeHeight = node.height;
			    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    		}
		    	}
	    	}
	    	
	    	map = jsonData.stoPoolSRMap;
	    	data = stopoolSR;
	    	pdata = storageSR;
	    	if(isDataValid(map)){
	    		for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		if(pNode && node){
		    			node.x = getWidth(node.x);
			    		h = pNode.y + pNode.height + nodeHGap;
			    		if(node.y < h){ node.y = h + nodeHGap; }
			    		if(minY > node.y){ minY = node.y; }
			    		if(maxY < node.y){ maxY = node.y; }
			    		nodeHeight = node.height;
			    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    		}
		    	}
	    	}
	    	
	    	map = jsonData.stoVolTPCMap;
	    	data = stoVolTPC;
	    	pdata = stopoolTPC;
	    	if(isDataValid(map)){
		    	for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		if(pNode && node){
		    			node.x = getWidth(node.x);
			    		h = pNode.y + pNode.height + nodeHGap;
			    		if(node.y < h){ node.y = h + nodeHGap; }
			    		if(minY > node.y){ minY = node.y; }
			    		if(maxY < node.y){ maxY = node.y; }
			    		nodeHeight = node.height;
			    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    		}
		    	}
	    	}
	    	
	    	map = jsonData.stoVolSRMap;
	    	data = stoVolSR;
	    	pdata = stopoolSR;
	    	if(isDataValid(map)){
		    	for(var i = 0, len = map.length, m, pNode, node, h; i < len; ++i){
		    		m = map[i];
		    		pNode = pdata[m.pid];
		    		node = data[m.id];
		    		if(pNode && node){
		    			node.x = getWidth(node.x);
			    		h = pNode.y + pNode.height + nodeHGap;
			    		if(node.y < h){ node.y = h + nodeHGap; }
			    		if(minY > node.y){ minY = node.y; }
			    		if(maxY < node.y){ maxY = node.y; }
			    		nodeHeight = node.height;
			    		pos2816[node.devType + "," + node.devId] = { x: node.x, y: node.y};
		    		}
		    	}
	    	}
	    	ctr = containers["sto"];
	    	ctr.y = minY - 5;
	    	ctr.height = maxY - minY + nodeHeight + nodeHGap;
	    	JTopo.layout.layoutNode(scene, appNode, true);
	    	$canvas.data("pos2816", pos2816);
	    }
	    var ALL_TOPO_NODES = {};
    	ALL_TOPO_NODES.app = appNode;
    	ALL_TOPO_NODES.vir = virtual;
    	ALL_TOPO_NODES.phy = physical;
    	ALL_TOPO_NODES.swi = switches;
    	ALL_TOPO_NODES.stoSR = storageSR;
    	ALL_TOPO_NODES.pooSR = stopoolSR;
    	ALL_TOPO_NODES.volSR = stoVolSR;
    	ALL_TOPO_NODES.stoTPC = storageTPC;
    	ALL_TOPO_NODES.pooTPC = stopoolTPC;
    	ALL_TOPO_NODES.volTPC = stoVolTPC;
    	$canvas.data("ALL_TOPO_NODES", ALL_TOPO_NODES);
	    stage.centerAndZoom();
		if($.isFunction(opts.afterDrawTopo)){
			opts.afterDrawTopo(canvas, stage, scene);
		}
		bindPopupMenu($canvas, stage, scene, {
	   		heightGap: 28,
		   	widthGap: 120
	   	});
}
function doCancle(){
	$("#myModal").modal("hide");
}
function delApp(){
	var id = $("#chooseAppMenuId input[name='chooseAppMenuId']:checked").val();
	bAlert("该应用关联关系与性能信息也将删除，确认删除吗？","警告",[{func:"doAjaxDel532("+id+");", text:"确定"},{func:"doCancle();",text:"取消"}]);
}
</script>
<div id="deviceAlertLog" style="display:none;"></div>
<div id="content">
	<div class="row-fluid">
		<div class="box span9" style="height:975px;">
			<div class="box-header well">
				<h2>应用拓扑 </h2>
				<div class="box-icon">
					<a id="chooseApp" href="javascript:void(0);" class="btn btn-round" title="选择应用" 
						onclick="changeColumn.showMenu(this,'chooseAppMenuId')" data-rel="tooltip"><i class="icon-eye-open"></i></a>
					<a id="addAppBtn" href="#" class="btn btn-round" title="新增应用" data-rel="tooltip">
						<i class="icon icon-color icon-add"></i></a>
					<a id="editAppBtn" href="javascript:void(0);" class="btn btn-round" title="编辑当前应用" 
						data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除当前应用" 
						onclick="delApp()" data-rel="tooltip"><i class="icon icon-color icon-cross"></i></a>
					<a id="topoRefreshFreq" href="javascript:void(0);" class="btn btn-round" title="选择拓扑图刷新频率" 
						onclick="changeColumn.showMenu(this,'topoRefreshFreqId')" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
				</div>
			</div>
			<div  class="box-content"  id="modelContent">
				<div id="showTipsWhenLoadTopo">
					<div class='modal-header'><h3>操作提示</h3></div>
					<div class='modal-body' align='center' style='height:80px;line-height:80px;'>
						<img src='${path}/resource/img/loading.gif'/><span>正在获取拓扑图的数据, 请稍候...</span>
					</div>
					<div class='modal-footer'></div>
				</div>
				<div class="contextMenu" id="sysMenu">
					<ul>
						<li id="li1_tc_G_lH92cU"><img src="${path}/resource/js/contextMenu/images/book_open.png"/>居中显示</li>
						<li id="li2l___S_12__zH"><img src="${path}/resource/js/contextMenu/images/arrow_out.png"/>全屏显示</li>
						<%--
						<li id="li3___NLP5s_j_4"><img src="${path}/resource/js/contextMenu/images/zoom_in.png"/>放大</li>
						<li id="li4V__FNjO__Kll"><img src="${path}/resource/js/contextMenu/images/zoom_out.png"/>缩小</li>
						<li id="li5o29g0_8C__R7"><input type="checkbox">鼠标缩放</li>
						--%>
						<li id="keepIconsPosition"><img src="${path}/resource/js/contextMenu/images/ddr_memory.png"/>记住图标位置</li>
						<%--
						<li id="autoLayout"><img src="${path}/resource/js/contextMenu/images/layout_content.png"/>自动布局</li>
						<li id="li6Yu_tR6265_YK"><img src="${path}/resource/js/contextMenu/images/accept.png"/>打开鹰眼</li>
						<li id="setupTopoWHGap"><img src="${path}/resource/js/contextMenu/images/setting_tools.png"/>图标间隔</li>
						--%>
						<hr style="margin-top:2px;margin-bottom:2px;">
						<li id="li6_9__6_Ra__XH"><img src="${path}/resource/js/contextMenu/images/table_export.png" />导出PNG图</li>
					</ul>
				</div>
			</div>
		</div>
		
		<div class="box span3" style="height:975px;">
			<div class="box-header well">
				<h2 id="summaryPage">总览</h2>
				<div class="box-content" style="margin-top:20px;">
					<!-- tab切换标签开始 -->
					<ul class="nav nav-tabs" id="myTab">
						<li class="active"><a href="#devConfig1501Tab">配置</a></li>
						<li class=""> <a href="#devPerf1501Tab">性能</a></li>
						<li class=""> <a href="#devEvent1501Tab">事件</a></li>
						<%--<li class=""> <a href="#devComp501Tab">部件</a></li>--%>
					</ul>
				</div>
				<!-- tab切换标签结束 -->
				<div id="perfChart2" class="tab-content" style="overflow:visible;min-height:200px;">
					<div id="showTipsDlg" style="width:100%;height:100%;overflow:hidden;overflow-y:auto;display:none;">
						<div>
							<div class="modal-footer"></div>
							<div class='modal-header'><h3>操作提示</h3></div>
							<div class='modal-body' align='center' style='height:100%;width:100%;line-height:80px;'>
								<img src='${path}/resource/img/loading.gif'/><span>正在获取数据, 请稍候...</span>
							</div>
							<div class='modal-footer'></div>
						</div>
					</div>
					<hr style="margin-top: 5px;margin-bottom: 5px">
					<%--配置--%>
					<div class="tab-pane active" id="devConfig1501Tab">
						<div id="devCfgTab" style="height:190px;display:none;">
							<table class="table table-bordered" id="vmCfg5127">
								<tbody>
									<tr>
										<td width="100px">物理机名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>虚拟机名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>总内存(GB)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>磁盘总容量(GB)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>磁盘剩余容量(GB)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>更新时间</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							<table class="table table-bordered" id="phyCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td width="100px">物理机名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>厂商</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>操作系统</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>CPU数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>CPU频率(GHz)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>磁盘总容量(GB)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>磁盘剩余容量(GB)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>内存(GB)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>虚拟机数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>更新时间</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							
							<table class="table table-bordered" id="swCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td width="100px">交换机名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>厂商 </td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>版本</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>型号</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>管理地址 </td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>序列号</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>Zone ID</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>交换机端口数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>更新时间</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							
							<table class="table table-bordered" id="stoCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td width="100px">存储系统名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>厂商</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>系统类型</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>型号</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>序列号</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>微码版本</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>缓存</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>物理磁盘容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>池容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>卷总容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>已分配卷容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>未分配卷容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>硬件状态</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>端口数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>磁盘数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>存储池数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>卷数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>存储扩展数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>阵列数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>Rank数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>控制器数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>IOGroup数量</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							<table class="table table-bordered" id="stoSRCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td width="100px">存储系统名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>厂商</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>IP地址</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>序列号</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>微码版本</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>缓存信息(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>写缓存(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>物理磁盘容量(T)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>逻辑容量(T)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>已用逻辑容量(T)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>容量使用情况(%)</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							<table class="table table-bordered" id="poolSRCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td>存储系统名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>存储池名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>后端磁盘数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>卷数量</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>阵列类型</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>总逻辑容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>已用逻辑容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>容量使用情况(%)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>更新时间</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							<table class="table table-bordered" id="volSRCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td width="100px">存储系统名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>存储池名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>存储卷名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>阵列类型</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>逻辑空间(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>实占空间(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>默认控制器</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>当前控制器</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							<table class="table table-bordered" id="poolCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td>存储系统名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>存储池名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>已用容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>可用容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>已分配容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>未分配容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>本地状态</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>硬件状态</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>操作状态</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>冗余级别</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>卷数量</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
							<table class="table table-bordered" id="volCfg5217" style="display:none;">
								<tbody>
									<tr>
										<td>存储系统名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>存储池名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td width="100px">存储卷名称</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>冗余级别</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>已用容量(G)</td>
										<td class="hmFill"></td>
									</tr>
									<tr>
										<td>唯一编号</td>
										<td class="hmFill" style="white-space:normal;"></td>
									</tr>
									<tr>
										<td>更新时间</td>
										<td class="hmFill"></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<%--性能--%>
					<div class="tab-pane" id="devPerf1501Tab" style="padding-top:10px;">
						<div id="devPerfTab">
							<div class="box-header well" style="margin-top:0px;">
								<div id="timeRangeNav" style="width:180px;font-size:13px;" class="box-icon">
									<a time="Last 1 hour">1时</a>
									<label>|</label>
									<a time="Last 1 day">1天</a>
									<label>|</label>
									<a time="Last 1 week">1周</a>
									<label>|</label>
									<a time="Last 1 month">1月</a>
								</div>
							</div>
							<div id="serverPerf2816" style="cursor:default;">
								<div class="box span12" style="width:100%;margin-top:-4px;">
									<div class="box-header well">
										<h2 id="perfTitle01" style="height:20px;overflow: hidden;"></h2>
									</div>
									<div style="height:225px;">
										<div id="ctr2816" class="clearfix" style="height:225px;"></div>
									</div>
								</div>
								<div class="box span12" style="width:100%;">
									<div class="box-header well">
										<h2 id="perfTitle02" style="height:20px;overflow: hidden;"></h2>
									</div>
									<div class="box-content" style="height:225px;">
										<div id="ctr281601" class="clearfix" style="height:225px;"></div>
									</div>
								</div>
								<div class="box span12" style="width:100%;">
									<div class="box-header well">
										<h2 id="perfTitle03" style="height:20px;overflow: hidden;"></h2>
									</div>
									<div class="box-content" style="height:225px;">
										<div id="ctr281602" class="clearfix" style="height:225px;"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<%--事件--%>
					<div class="tab-pane" id="devEvent1501Tab" style="padding-top:10px;">
						<div id="devEventTab" class="box-content" style="overflow:auto;width:94%;min-height:148px;">
							<table class="table table-bordered" id="devEvent5217" style="table-layout:fixed;">
								<thead>
									<tr>
										<th style="text-align:center;width:70px;">故障等级</th>
										<th style="text-align:center;width:60px;">发生次数 </th>
										<th style="text-align:center;width:160px;">最早时间</th>
										<th style="text-align:center;width:160px;">最迟时间</th>
									</tr>
								</thead>
								<tbody>
									<tr id="devEvent5217tr01" style="cursor:pointer;">
										<td><span class="label label-important">Critical</span></td>
										<td class="times"></td>
										<td class="early"></td>
										<td class="lately"></td>
									</tr>
									<tr id="devEvent5217tr02" style="cursor:pointer;">
										<td><span class="label label-warning">Warning</span></td>
										<td class="times"></td>
										<td class="early"></td>
										<td class="lately"></td>
									</tr>
									<tr id="devEvent5217tr03" style="cursor:pointer;">
										<td><span class="label">Info</span></td>
										<td class="times"></td>
										<td class="early"></td>
										<td class="lately"></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<%--部件
					<div class="tab-pane" id="devComp501Tab" style="padding-top:10px;">
						<div id="devCompTab"></div>
					</div>--%>
				</div>
			</div>
		</div>
		<div class="box span12 box-header well" style="margin-left:0;"></div>
	</div>
</div>

<%@include file="/WEB-INF/views/include/footer.jsp"%>