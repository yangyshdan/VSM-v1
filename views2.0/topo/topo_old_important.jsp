<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script type="text/javascript" src="${path}/resource/js/contextMenu/jquery.contextmenu.js"></script>
<script type="text/javascript" src="${path}/resource/js/jtopo/js/excanvas.js"></script>
<script type="text/javascript" src="${path}/resource/js/jtopo/jtopo-0.4.8-min.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script>
<script type="text/javascript" src="${path}/resource/js/project/util.js"></script>

<style type="text/css">
	#deviceAlertLog {
		width: 250px;
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
	.contextMenu { display: none }
</style>
<script type="text/javascript" src="${path}/resource/js/Highcharts-4.0.3/js/highcharts.js"></script>
<script type="text/javascript" src="${path}/resource/js/Highcharts-4.0.3/js/highcharts-more.js"></script>
<script type="text/javascript" src="${path}/resource/js/jtopo/js/huiming.topo.utils.js"></script>
<script type="text/javascript">

$(function(){	
	$("#AddAppdb3p1rguUkTI").click(function(){
		MM_openwin3("新增应用", "${path}/servlet/topo/TopoAction?func=TopoAddApp", 500, 290, 0);
	});
	
});

/**
options = {
	swid: 56671203,
	swname: "switch",
	imgPath: ""
}
*/
drawSwitchComp = function(options){
	$.ajax({
		url: "${path}/servlet/topo/TopoAction?func=GetSwitchPortData",
		data: {swid: options.swid},
		type: "post",
		dataType: "json",
		success:function(jsonData){
			if(jsonData.success){
				var value = jsonData.value;
				if($.isArray(value) && value.length > 0){
					var cols = 3;
					var rows = getNumber(value.length, cols);
					var rowsHeight = 80 * rows;  // 所有行总共的高度
					$("#topoCompCanvas").remove();
					$("#topoCompContent").append($("<canvas style='margin-top:0px;' id='topoCompCanvas' height='"+(rowsHeight + 10)+"' width='300'>浏览器不支持HTML5技术，推荐下载火狐、Chrome和搜狗浏览器</canvas>"));
									
					var canvas = document.getElementById("topoCompCanvas");            
		            stage = new JTopo.Stage(canvas);
		            var scene = new JTopo.Scene();
		            //scene.background = './img/bg.jpg';
		            stage.add(scene);
		            
					var gridLayout = JTopo.layout.GridLayout(rows, cols);
				    var container2 = new JTopo.Container();
				    container2.layout = gridLayout;
				    container2.fillColor = "255,255,255";
				    container2.setBound(5, 5, 300, rowsHeight);
				    scene.add(container2);
				    <%--不要用for循环--%>
				    //for(var i = 0, len = value.length, port; i < len; ++i){ 
				    $.each(value, function(index, port){
				        var node = new JTopo.Node();
				        node.setImage(options.imgPath, true);
						node.position = "Middle_Center";
						node.text = "" + port.port_number;
						node.fontColor = "0,0,0";
						var content = "<h3 class='popover-title'>交换机端口详细信息</h3>";
						var msgs = {
							"名称": port.name, "所属交换机": options.swname, "端口号": port.port_number,
							"端口类型": port.the_type, "端口速率(M)": port.speed
						};
				        content += "<table>";
				        var statusfmt = "<tr><td align='right' class='popover-text'>%s:</td><td><span class='label %s'>%s</span></td></tr>";
				        for(var key in msgs){
				        	content += "<tr><td align='right' class='popover-text'>%s:</td><td>%s</td></tr>".jFormat(key, msgs[key]);
				        }
			        	content += statusfmt.jFormat("操作状态", getOperationalStatusCSS(port.operation), port.operation);
			        	content += statusfmt.jFormat("硬件状态", getConsolidateStatusCSS(port.consolidate), port.consolidate);
				        content += "</table>";
				        var alertInfo = getDeviceLogs(port.logs, port.port_id, 5);
						if(alertInfo){
							var alertInfoFmt = "<legend style='margin-bottom:0px;'><i class='icon %s' style='float:left;margin-top:10px;'></i>%s(%s)</legend>%s";
							content += "<h3 class='popover-title'>报警详细</h3>";
							content += "<p style='margin: 0px 10px 0px 10px;'>";
							var hasCritical = false;
							if(alertInfo.criticalCount && alertInfo.criticalCount > 0){ <%--说明有--%>
								content += alertInfoFmt.jFormat("icon-color icon-cross", "Critical Alert", alertInfo.criticalCount, alertInfo.criticalNames.join("<br>"));
								node.alarmText = "Critical(%s)".jFormat(alertInfo.criticalCount);
								hasCritical = true;
							}
							if(alertInfo.warningCount && alertInfo.warningCount > 0){ <%--说明有警告--%>
								content += alertInfoFmt.jFormat("icon-color icon-alert", "Warning Alert", alertInfo.warningCount, alertInfo.warningNames.join("<br>"));
								if(hasCritical == false){ <%--在没有Critical的情况下--%>
									node.alarmText = "Warning(" + alertInfo.warningCount + ")";
									node.alarmColor = "255,244,0";
								}
							}
							
							if(alertInfo.infoCount && alertInfo.infoCount > 0){ <%--说明有--%>
								content += alertInfoFmt.jFormat("icon-darkgray icon-info", "Info", alertInfo.infoCount, alertInfo.infoNames.join("<br>"));
							}
							content += "</p>";
						}
						node.content = content;
						var winHeight = $(window).height();
						var winWidth = $(window).width();
						node.mouseover(function(event){
							var $dlg = $("#deviceAlertLog");
							<%--
								screenX描述鼠标此时所在位置对应在屏幕的x坐标
							--%>
							var _l = event.pageX,
							_t = event.pageY;
							if(_l + $dlg.width() > winHeight){
								_l -= $dlg.width() + event.target.width;
							}
							if(_t + $dlg.height() > winHeight){
								_t -= ($dlg.height() + event.target.height);
							}
							
							$dlg.css({ left: _l, top: _t });
							$dlg.html(event.target.content).show();
						});
						node.mouseout(function(){
							$("#deviceAlertLog").hide();
						});
						if(node.alarmText){
							setInterval(function(){
				                if(node.alarm != null){
				                	console.log("hide");
				                	console.log(node.alarm);
				                    node.alarm = null;
				                }
				                else{
				                	console.log("show");
				                	console.log(node.alarm);
				                    node.alarm = node.alarmText;
				                }
				            }, 600);
						}
				        scene.add(node);
				        container2.add(node);
				    });
				    $("#showTipsWhenLoadComp").hide();
				}
			}
			else {
				$("#myModal").html("<div style='height:100px;width:300px;margin-top:10px;'><h4>" + jsonData.msg + "</h4><center>3秒钟后消失...</center></div>");
				setTimeout("$('#myModal').modal('hide')", 3000);
			}
		},
		beforeSend:function(){
			$("#showTipsWhenLoadComp").show();
			$("#topoCompCanvas").hide();
		}
	});
};

<%--
options = {
	imgFolder: "${path}/resource/img/topo/",
	widthGap: 100,
	heightGap: 100,
	nodeAlarmColor: 55,
}
--%>
drawWindowsX86ServerTopo = function(jsonData, options){
	// context.clearRect(0, 0, canvas.width, canvas.height)
	var $perfChart2 = $("#perfChart2");
	$perfChart2.find("#serverPropertyTable").hide();
	$perfChart2.find("#storagePropertyTable").hide();
	$perfChart2.find("#switchPropertyTable").hide();
	var $canvas = $("#" +options.canvasId);
	if($canvas.length > 0){ $canvas.remove(); }
	var $modelContent = $("#modelContent");
	$modelContent.css({height: "100%"});
	var canvasHeight = $modelContent.parent().height() - 40;
	var canvasWidth = $modelContent.width();
	$canvas = $("<canvas id='canvas' height='%s' width='%s'>浏览器不支持HTML5技术，推荐下载火狐、Chrome和搜狗浏览器</canvas>"
			.jFormat(canvasHeight, canvasWidth));
	$modelContent.append($canvas);
	var canvas = $canvas[0];//document.getElementById(options.canvasId);
	
    var stage = new JTopo.Stage(canvas);
    var scene = new JTopo.Scene(stage);
    var zoom = 0.85;
    scene.alpha = 1;
    stage.wheelZoom = null;
    var imgFolder = options.imgFolder;
	function addNoNode(imgPath){
        var node = new JTopo.Node();
        node.setImage(imgPath, false);
		node.visible = false;
        scene.add(node);
        return node;
    }

	function addNoLink(nodeA, nodeZ){
         var link = new JTopo.FoldLink(nodeA, nodeZ, ""); // CurveLink
         link.lineWidth = 0; // 线宽
         link.strokeColor = "255,255,255";
		 link.alpha = 0.0;
         scene.add(link);
         return link;
     }
     
     var ctx78HggaC90 = canvas.getContext("2d");
     function measureText(str){
     	return ctx78HggaC90.measureText(str).width;
     }
         
    /*
       opt = {
       	text: "这是节点标题",
       	deviceid: "设备编号, 例如 2315",
       	devicetype: "交换机",
       	ipaddress: '192.168.1.3',
       	imgPath: imgFolder + "switch.png"
       	tipDialogSelector: "#deviceAlertLog",
       	otherInfo: [""],
       	redirectURL: "",
       	deviceIdKey: "computerId",
       	deviceNameLimit: 5,
       	alertInfo: { criticalCount: 100, warningCount: 100, infoCount: 100, criticalNames:[], warningNames: [], infoNames:[] }
       }
     */
    JTopo.util.nodeAlarmColor = options.nodeAlarmColor? options.nodeAlarmColor : 35;  <%--默认节点的报警颜色不显示， 1~255--%>
    var preTextWidth = 0, nextTextWidth = 0, maxWidth = 0, first = true;
    var nodeSet = {};
    var nodePoses = jsonData.nodePos;
    var isNodePosesInvalid = nodePoses == undefined && nodePoses == null;
	function addNode(opt){
        var node = new JTopo.Node();
        node.setImage(opt.imgPath, true);
		node.scalaX =0.7;
		node.scalaY = 0.7;
		node.position = "Middle_Center";
		node.fontColor = "0,0,0";
		node.dragable = false;
		if(opt.text && opt.text.length > 28){
			opt.text = opt.text.substring(0, 28) + "...";
		}
		node.name = opt.text;
		if(opt.layout){
			node.layout = opt.layout;
		}
		if(first){
			preTextWidth = measureText(opt.text);
			maxWidth = preTextWidth + 0;
			first = false;
		}
		else {
			nextTextWidth = measureText(opt.text);
			if(maxWidth < (nextTextWidth + preTextWidth)){
				maxWidth = nextTextWidth + preTextWidth;
			}
			preTextWidth = nextTextWidth;
		}
		node.text = "%s: %s".jFormat(opt.devicetype, opt.text);
		node.devicetype = opt.devicetype;
		if(opt.storagetype){
			node.storagetype = opt.storagetype;
		}
        scene.add(node);
        var tipDialogSelector = opt.tipDialogSelector;
        node.deviceid = opt.deviceid;
        node.ipaddress = opt.ipaddress? opt.ipaddress : "unknown";
        var content = "<h3 class='popover-title'>设备详细信息</h3>";
        content += "<table><tr><td align='right' class='popover-text'>%s名称:</td><td>%s</td></tr><tr><td align='right' class='popover-text'>IP地址:</td><td>%s</td></tr></table>"
        .jFormat(opt.devicetype, opt.text, node.ipaddress);
        
        var alertInfo = opt.alertInfo;
		if(alertInfo){
			var alertInfoFmt = "<legend style='margin-bottom:0px;'><i class='icon %s' style='float:left;margin-top:10px;'></i>%s(%s)</legend>%s";
			content += "<h3 class='popover-title'>报警详细</h3>";
			content += "<p style='margin: 0px 10px 0px 10px;'>";
			var hasCritical = false;
			if(alertInfo.criticalCount && alertInfo.criticalCount > 0){ <%--说明有--%>
				content += alertInfoFmt.jFormat("icon-color icon-cross", "Critical Alert", alertInfo.criticalCount, alertInfo.criticalNames.join("<br>"));
				node.alarmText = "Critical(%s)".jFormat(alertInfo.criticalCount);
				hasCritical = true;
			}
			if(alertInfo.warningCount && alertInfo.warningCount > 0){ <%--说明有警告--%>
				content += alertInfoFmt.jFormat("icon-color icon-alert", "Warning Alert", alertInfo.warningCount, alertInfo.warningNames.join("<br>"));
				if(hasCritical == false){ <%--在没有Critical的情况下--%>
					node.alarmText = "Warning(" + alertInfo.warningCount + ")";
					node.alarmColor = "255,244,0";
				}
			}
			
			if(alertInfo.infoCount && alertInfo.infoCount > 0){ <%--说明有--%>
				content += alertInfoFmt.jFormat("icon-darkgray icon-info", "Info", alertInfo.infoCount, alertInfo.infoNames.join("<br>"));
			}
			content += "</p>";
		}
		if(opt.otherInfo){
			content += "<h3 class='popover-title'>其他信息</h3>";
			content += "<p style='margin: 0px 10px 0px 10px;'>";
			var otherInfo = opt.otherInfo;
			if($.isArray(otherInfo)){
				for(var i = 0, len = otherInfo.length; i < len; ++i){
					content += "<legend style='margin-bottom:0px;'><i class='icon icon-darkgray icon-info' style='float:left;margin-top:10px;'></i>" 
							+ otherInfo[i] + "</legend>";
				}
			}
			content += "</p>";
        }
		node.content = content;
		node.addEventListener("click", function(event){
			<%--,是分隔符，设备加类型才能保证唯一性--%>
       		var key = opt.devicetype + "," + node.deviceid;
       		if($canvas.data("posTreh89YYtzX")){
       			$canvas.data("posTreh89YYtzX")[key] = {
	       			x: (isNaN(this.x)? 0 : this.x) + (isNaN(scene.translateX)? 0 : scene.translateX),
	       			y: (isNaN(this.y)? 0 : this.y) + (isNaN(scene.translateY)? 0 : scene.translateY)
	       		};
       		}
		});
		node.addEventListener("dbclick", function(event){
       		var thisNode = event.target;
       		if(opt.redirectURL && node.deviceid){
       			var url = opt.deviceIdKey? 
       			opt.redirectURL + (opt.redirectURL.indexOf("?") >= 0? "&" : "?") + opt.deviceIdKey + "=" + node.deviceid : opt.redirectURL;
       			var $a = $("<a>").text("查看事件详细").css({cursor: "pointer"}).click(function(){
       				window.location.href = url;
       			});
       			$("#goToEventDetailPageId").html($a);
       		}
       		else { $("#goToEventDetailPageId").html("<h2>无事件详细页面</h2>"); }
       		
       		$("#deviceName2816").text(thisNode.name);
			$("#deviceType2816").text(thisNode.devicetype);
			$("#deviceIPAddress2816").text(thisNode.ipaddress);
       		<%--
       			如果是Server，则显示CPU和MEM的仪表
       			如果是Switch，则显示IOPS曲线
       			如果是Storage，则显示capacity饼形图和IOPS曲线
       		--%>
			showSummary({
				devicetype: thisNode.devicetype,
				deviceid: thisNode.deviceid,
				storagetype: thisNode.storagetype,
				name: thisNode.name
			});
			if(thisNode.devicetype == "Switch"){
				drawSwitchComp({
					swid: thisNode.deviceid,
					swname: thisNode.name,
					imgPath: "${path}/resource/js/jtopo/img/switchport.png"
				});
			}
       	});
		
		//node.mouseup(function(event){ });
		node.mouseover(function(event){
			$dlg = $(tipDialogSelector);
			var ww = $(window).width(),
			_l = (this.x + scene.translateX) + 75,// * (1 + wheelZoom),
			_t = (this.y + scene.translateY) + 30;// * (1 + wheelZoom);
			if(_l + $dlg.width() > ww){
				_l -= $dlg.width() + 65;
			}
			$dlg.css({ left: _l, top: _t });
			$dlg.html(event.target.content).show();
		});
		node.mouseout(function(){
			$(tipDialogSelector).hide();
		});
		if(node.alarmText){
			setInterval(function(){
                if(node.alarm == node.alarmText){
                    node.alarm = null;
                }else{
                    node.alarm = node.alarmText;
                }
            }, 600);
		}
		var key = opt.devicetype + "," + node.deviceid;
		nodeSet[key] = node;
		<%--添加位置坐标--%>
		if(nodePoses){
			var xy = nodePoses[opt.devicetype + node.deviceid];
			if(xy){ node.setLocation(xy.x, xy.y); }
		}
        return node;
     };
    
     function addLink(nodeA, nodeZ, opt){ <%--FoldLink 1 CurveLink 2 FlexionalLink 3--%>
         var link;
         if(opt && opt.linkType){
         	switch(opt.linkType){
				case 1: link = new JTopo. FoldLink(nodeA, nodeZ, ""); break;
         		case 2:  link = new JTopo. CurveLink(nodeA, nodeZ, ""); break;
         		case 3:  link = new JTopo. FlexionalLink(nodeA, nodeZ, ""); break;
         		default: link = new JTopo. Link(nodeA, nodeZ, ""); break;
         	}
         }
         else {
         	link = new JTopo.Link(nodeA, nodeZ, "");
         }
         link.bundleOffset = 60;
         link.bundleGap = 20;
         link.arrowsRadius = 10;
         link.textOffsetY = 3;
         link.strokeColor = "0,200,255";  //    JTopo.util.randomColor() "255,255,0";//
         link.lineWidth = 2;
         link.direction = opt? (opt.direction? opt.direction : "horizontal") : "horizontal"; // vertical  horizontal
         <%--link.content = ("Recv:%s mb/s, Send:%s mb/s").jFormat(92.56, 28.12);
         link.fontColor = "0,0,0";
         link.mouseover(function(event){
         	link.text = link.content;
         });
         link.mouseout(function(event){
         	link.text = null;
         });--%>
         scene.add(link);
         return link;
     }

	<%--一台设备必须查出它的fresid, ftopid, devid(可选)--%>
    <%--根节点(第一层)，隐藏根节点--%>
	//var root = addNoNode(imgFolder + "blank.png");
	<%--添加应用, 根节点连接一个应用--%>
	var appData = jsonData.appData;
	var blankImagePath = imgFolder + "blank.png";
	var vmImagePath = imgFolder + "vm.png";
	var hypImagePath = imgFolder + "hypervisor.png";
	var stoImagePath = imgFolder + "storage.png";
	var swImagePath = imgFolder + "switch.png";
	var vmType = "VirtualMachine";
	var hypType = "Hypervisor";
	var stoType = "Storage";
	var swType = "Switch";
	var vmURL = "${path}/servlet/virtual/VirtualAction?func=VirtualInfo&computerId=%s&hypervisorId=%s&vmId=%s";
	var hypURL = "${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&computerId=%s&hypervisorId=%s";
	var stoURL = "${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=";
	var swURL = "${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=";
	var appNode = addNoNode(blankImagePath);
	var tipDialogSelector = "#deviceAlertLog";
	<%--应用必须有app_id --%>
	/*
	addNode({
       	text: appData.appname,
       	deviceid: appData.appid,
       	devicetype: "应用",
       	ipaddress: "无IP地址",
       	imgPath: imgFolder + "app.png",
       	tipDialogSelector: "#deviceAlertLog",
       	otherInfo: [appData.appdesc]
     });
     */
     //addNoLink(root, appNode);
     <%-- 
     	options = {
     		title: "虚拟机",
     	}
     --%>
     function addContainer(options){
     	var container = new JTopo.Container(options.title);
     	container.textPosition = "Middle_Left";
	    container.fontColor = "0,0,0";
	    container.borderColor = "120,236,243";
	    container.borderWidth = 0.8;
	    container.dashedPattern = 5;
	    container.shadowBlur = 0;
	    container.fillColor = "216,225,233"; //"229,236,243";
	    container.font = "14pt 微软雅黑";
	    container.borderRadius = 10;
	    container.setBound(options.x, options.y, options.width, options.height);
	    container.dragable = false;
	    container.shadow = null;
	    //var flowLayout = JTopo.layout.FlowLayout(10, 10);
	    //container.layout = flowLayout;
	    scene.add(container);
	    return container;
     }
     var containerCfg = {"虚拟机": 100, "物理机": 100, "交换机": 200, "存储系统": 100};
     var y = 5;
     for(var key in containerCfg){
     	addContainer({
	     	title: key,
	     	x: 5,
	     	y: y,
	     	width: canvasWidth - 10,
	     	height: containerCfg[key]
	     });
	     y += containerCfg[key] + 5;
     }
     
     <%--添加虚拟机, 一个应用连接若干个虚拟机--%>
     var data = jsonData.vmData, vms = {}, appLinkVM = {};
     if(isDataValid(data)){
     	var logs = jsonData.vmLogs;
     	for(var i = 0, len = data.length, dev, vm, alv, dev_id; i < len; ++i){
			dev = data[i];
			dev_id = dev.vm_id;
			<%--虚拟机必须有vm_id(t_res_virtualmachine), vm_name, ip_address, hyp_id, comp_id --%>
			vm = vms[dev_id];
			if(vm == undefined){
				vm = addNode({
			       	text: dev.vm_name,
			       	deviceid: dev_id,
			       	devicetype: vmType,
			       	ipaddress: dev.ip_address,
			       	imgPath: vmImagePath,
			       	tipDialogSelector: tipDialogSelector,
			       	redirectURL: vmURL.jFormat(dev.comp_id, dev.hyp_id, dev_id),
			       	alertInfo: getDeviceLogs(logs, dev_id, 5)
		       });
		       //container.add(vm);
		       vms[dev_id] = vm;
			}
			<%--如果appid的键值存在，并且所对应的vmid相等，那么就说明还没有画线--%>
			alv = appLinkVM[appData.app_id];
			if(!(alv && alv == dev_id)){
				addNoLink(appNode, vm);
				appLinkVM[appData.appid] = dev_id;
			}
		}
     }
     
     var hypervisors = {};
     <%--添加物理机, 一个虚拟机连接若干个物理机--%>
     data = jsonData.vmLinkHypData;  <%--本质上是一个Hypervisor--%>
     var vmLinkHpy = {};
     if(isDataValid(data)){
     	var logs = jsonData.hypervisorLogs;
     	for(var i = 0, len = data.length, dev, hid, vlh; i < len; ++i){
     		<%--虚拟机连接物理机必须有vm_id, hyp_id(t_res_hypervisor), hyp_name, ip_address, comp_id --%>
			dev = data[i];
			hid = dev.hyp_id;
			if(hypervisors[hid] == undefined){
				hypervisors[hid] = addNode({
			       	text: dev.hyp_name,
			       	deviceid: hid,
			       	devicetype: hypType,
			       	imgPath: hypImagePath,
			       	ipaddress: dev.ip_address,
			       	tipDialogSelector: tipDialogSelector,
			       	redirectURL: hypURL.jFormat(dev.comp_id, hid),
			       	alertInfo: getDeviceLogs(logs, hid, 5)
		       });
			}
			vlh = vmLinkHpy[dev.vm_id];
			if(!(vlh && vlh == hid)){
				addLink(vms[dev.vm_id], hypervisors[hid]);
				vmLinkHpy[dev.vm_id] = hid;
			}
		}
     }
     
     <%--添加物理机, 一个应用连接若干个物理机--%>
     data = jsonData.hypervisorData;
     var appLinkHpy = {};
     var virtualVMNode = addNoNode(blankImagePath);<%--虚构一台虚拟机，使连接应用的不连接虚拟机的物理机退到第三层--%>
     addNoLink(appNode, virtualVMNode);
     if(isDataValid(data)){
     	var logs = jsonData.hypervisorLogs;
     	<%--物理机必须有hyp_id(t_res_hypervisor), hyp_name, ip_address, comp_id --%>
     	for(var i = 0, len = data.length, dev, hypervisor, alh, hyp_id; i < len; ++i){
			dev = data[i];
			hyp_id = dev.hyp_id;
			hypervisor = hypervisors[hyp_id];
			if(hypervisor == undefined){<%--如果不存在物理机，说明虚拟机不在这台物理机运行，让应用直接连接物理机--%>
				hypervisor = addNode({
			       	text: dev.hyp_name,
			       	deviceid: hyp_id,
			       	devicetype: hypType,
			       	ipaddress: dev.ip_address,
			       	imgPath: hypImagePath,
			       	tipDialogSelector: tipDialogSelector,
			       	redirectURL: hypURL.jFormat(dev.comp_id, hyp_id),
			       	alertInfo: getDeviceLogs(logs, hyp_id, 5)
		       });
		       hypervisors[hyp_id] = hypervisor;
		       alh = appLinkHpy[appData.app_id];
		       if(!(alh && alh == hyp_id)){
					addNoLink(virtualVMNode, hypervisor);
					appLinkHpy[appData.appid] = hyp_id;
			   }
			}
		}
     }
     
     <%--添加交换机, 一个物理机连接若干个交换机--%>
     var storageData = jsonData.storageData;
     var switchLogs = jsonData.switchLogs;
     var storageLogs = jsonData.storageLogs;
     var storages = {};
     data = jsonData.switchData;
     var hpySwitches = {};
     var hpyLinkSw = {};
     var swLinkSto = {};
     var sw2Switches = {};
     var isStorageDataValid = isDataValid(storageData);
     function linkSw2Sto(swID){
    	for(var j = 0, l = storageData.length, slst, sw, sto, sto_id; j < l; ++j){
    		<%--存储系统必须有sto_id(v_res_storagesubsystem), sw_id(v_res_switch), sto_name, sto_ip, os_type, comp_id --%>
			sto = storageData[j];
			sto_id = sto.sto_id;
			if(sto.sw_id == swID){<%--如果该交换机在存储数据当中--%>
				if(storages[sto_id] == undefined){
					storages[sto_id] = addNode({
				       	text: sto.sto_name,
				       	deviceid: sto_id,
				       	devicetype: stoType,
				       	storagetype: sto.os_type,
				       	ipaddress: sto.sto_ip,
				       	imgPath: stoImagePath,
				       	tipDialogSelector: tipDialogSelector,
				       	redirectURL: stoURL + sto_id,
				       	alertInfo: getDeviceLogs(storageLogs, sto_id, 5)
			       });
				}
				slst = swLinkSto[sto.sw_id];
				if(!(slst && slst == sto_id)){
					sw = hpySwitches[sto.sw_id];
					if(sw){ addLink(sw, storages[sto_id], {direction: "horizontal"}); }
					else {
						sw = sw2Switches[sto.switch_id];
						if(sw){ addLink(sw, storages[sto_id], {direction: "vertical"}); }
					}
					swLinkSto[sto.sw_id] == sto_id;
			    }
				break;
			}
		}
     }
     if(isDataValid(data)){ <%--如果有交换机的数据--%>
     	for(var i = 0, len = data.length, dev, swID, hid, hypSwi, hlsw; i < len; ++i){ <%--遍历交换机的数据--%>
     		<%--物理机连交换机必须有sw_id(v_res_switch), hyp_id(t_res_hypervisor), sw_name, hyp_name, sw_ip--%>
			dev = data[i];
			swID = dev.sw_id;
			hid = dev.hyp_id;
			hypSwi = hpySwitches[swID];
			if(hypSwi == undefined){
				hypSwi = addNode({
			       	text: dev.sw_name,
			       	deviceid: swID,
			       	devicetype: swType,
			       	ipaddress: dev.sw_ip,
			       	imgPath: swImagePath,
			       	tipDialogSelector: tipDialogSelector,
			       	redirectURL: swURL + swID,
			       	alertInfo: getDeviceLogs(switchLogs, swID, 5)
		       });
		       hpySwitches[swID] = hypSwi;
			}
			hlsw = hpyLinkSw[hid];
			if(!(hlsw && hlsw == swID)){
				addLink(hypervisors[hid], hpySwitches[swID]);
				hpyLinkSw[hid] = swID;
		    }
		    linkSw2Sto(swID);
		}
     }
     
     
     
     <%--交换机连接交换机--%>
   	 var sw_id1 = "sw_id" + (jsonData.switchStart? "1" : "2");
   	 var sw_id2 = "sw_id" + (jsonData.switchStart? "2" : "1");
   	 var sw_name1 = "sw_name" + (jsonData.switchStart? "1" : "2");
   	 var sw_name2 = "sw_name" + (jsonData.switchStart? "2" : "1");
   	 var sw_ip1 = "sw_ip" + (jsonData.switchStart? "1" : "2");
   	 var sw_ip2 = "sw_ip" + (jsonData.switchStart? "2" : "1");
   	 var sw2swData = jsonData.sw2swData;
   	 var swLinkSw = {};
   	 var usedInHyp = {};
   	 if(isDataValid(sw2swData)){
   	 	var switchLogs = jsonData.switchLogs;
   		for(var i = 0, len = sw2swData.length, sw2sw, isLinkHpy1, isLinkHpy2, isLinkSwitch1, isLinkSwitch2; i < len; ++i){
   			<%--交换机必须有sw_id1(v_res_switch), sw_id2(v_res_switch), sw_name1, sw_name2, sw_ip1, sw_ip2--%>
   			sw2sw = sw2swData[i];
   			isLinkHpy1 = hpySwitches[sw2sw.sw_id1] == undefined;<%--false表示该交换机与物理机连接--%>
   			isLinkHpy2 = hpySwitches[sw2sw.sw_id2] == undefined;<%--false表示该交换机与物理机连接--%>
   			
   			isLinkSwitch1 = sw2Switches[sw2sw.sw_id1] == undefined;<%--false表示该交换机与交换机连接--%>
   			isLinkSwitch2 = sw2Switches[sw2sw.sw_id2] == undefined;<%--false表示该交换机与交换机连接--%>
   			
   			if(isLinkHpy1){<%--如果s1没有和物理机连接--%>
   				if(isLinkSwitch1){<%--如果s1还没有被访问过--%>
   					<%--生成s1并保存到sw2Switches--%>
   					sw2Switches[sw2sw.sw_id1] = addNode({
				       	text: sw2sw.sw_name1,
				       	deviceid: sw2sw.sw_id1,
				       	devicetype: swType,
				       	ipaddress: sw2sw.sw_ip1,
				       	imgPath: swImagePath,
				       	tipDialogSelector: tipDialogSelector,
				       	redirectURL: swURL + sw2sw.sw_id1,
				       	alertInfo: getDeviceLogs(switchLogs, sw2sw.sw_id1, 5)
			       });
   				}
   				if(isLinkHpy2){<%--如果s2没有和物理机连接--%>
   					if(isLinkSwitch2){<%--如果s2还没有被访问过--%>
	   					<%--生成s2并保存到sw2Switches--%>
	   					sw2Switches[sw2sw.sw_id2] = addNode({
					       	text: sw2sw.sw_name2,
					       	deviceid: sw2sw.sw_id2,
					       	devicetype: swType,
					       	ipaddress: sw2sw.sw_ip2,
					       	imgPath: swImagePath,
					       	tipDialogSelector: tipDialogSelector,
					       	redirectURL: swURL + sw2sw.sw_id2,
					       	alertInfo: getDeviceLogs(switchLogs, sw2sw.sw_id2, 5)
				       });
	   				}
	   				var sw2swSid1 = sw2sw[sw_id1], sw2swSid2 = sw2sw[sw_id2];
	   				if(swLinkSw[sw2swSid1]){<%--说明--%>
	   					if(swLinkSw[sw2swSid1][sw2swSid2] == undefined){
	   						addLink(sw2Switches[sw2swSid1], sw2Switches[sw2swSid2]);  // , {direction: "vertical"}
							swLinkSw[sw2swSid1][sw2swSid2] = 1;
	   					}
	   				}
	   				else {
	   					addLink(sw2Switches[sw2swSid1], sw2Switches[sw2swSid2]);  // , {direction: "vertical"}
						swLinkSw[sw2swSid1] = {sw2swSid2: 1};
	   				}
   				}
   				else {<%--如果s2和物理机连接，优先s2连s1--%>
	   				if(swLinkSw[sw2sw.sw_id2]){<%--说明--%>
	   					if(swLinkSw[sw2sw.sw_id2][sw2sw.sw_id1] == undefined){
	   						addLink(hpySwitches[sw2sw.sw_id2], sw2Switches[sw2sw.sw_id1]);
							swLinkSw[sw2sw.sw_id2][sw2sw.sw_id1] = 1;
	   					}
	   				}
	   				else {
	   					addLink(hpySwitches[sw2sw.sw_id2], sw2Switches[sw2sw.sw_id1]);
						swLinkSw[sw2sw.sw_id2] = {};
						swLinkSw[sw2sw.sw_id2][sw2sw.sw_id1] = 1;
	   				}
   				}
   			}
   			else {<%--如果s1和物理机连接--%>
   				if(isLinkHpy2){<%--如果s2没有和物理机连接--%>
   					if(isLinkSwitch2){<%--如果s2还没有被访问过--%>
	   					<%--生成s2并保存到sw2Switches--%>
	   					sw2Switches[sw2sw.sw_id2] = addNode({
					       	text: sw2sw.sw_name2,
					       	deviceid: sw2sw.sw_id2,
					       	devicetype: swType,
					       	ipaddress: sw2sw.sw_ip2,
					       	imgPath: swImagePath,
					       	tipDialogSelector: tipDialogSelector,
					       	redirectURL: swURL + sw2sw.sw_id2,
					       	alertInfo: getDeviceLogs(switchLogs, sw2sw.sw_id2, 5)
				       });
	   				}
				    if(swLinkSw[sw2sw.sw_id1]){<%--说明--%>
	   					if(swLinkSw[sw2sw.sw_id1][sw2sw.sw_id2] == undefined){
	   						addLink(hpySwitches[sw2sw.sw_id1], sw2Switches[sw2sw.sw_id2]);
							swLinkSw[sw2sw.sw_id1][sw2sw.sw_id2] = 1;
	   					}
	   				}
	   				else {
	   					addLink(hpySwitches[sw2sw.sw_id1], sw2Switches[sw2sw.sw_id2]);
						swLinkSw[sw2sw.sw_id1] = {};
						swLinkSw[sw2sw.sw_id1][sw2sw.sw_id2] = 1;
	   				}
   				}
   				else {
   					<%--
   						如果s2和物理机连接
   						那么s1和s2都和物理机连接 , 咨询一下古劲
   						FlexionalLink
   					--%>
				    var sw2swSid1 = sw2sw[sw_id1], sw2swSid2 = sw2sw[sw_id2];
	   				if(swLinkSw[sw2swSid1]){<%--说明--%>
	   					if(swLinkSw[sw2swSid1][sw2swSid2] == undefined){
	   						addLink(hpySwitches[sw2swSid1], hpySwitches[sw2swSid2], {direction: "vertical"}); //horizontal
							swLinkSw[sw2swSid1][sw2swSid2] = 1;
	   					}
	   				}
	   				else {
	   					addLink(hpySwitches[sw2swSid1], hpySwitches[sw2swSid2], {direction: "vertical"}); //horizontal
						swLinkSw[sw2swSid1] = {sw2swSid2: 1};
	   				}
   				}
   			}
		}
   	}
     
     <%--添加存储系统, 一个交换机连接若干个存储系统，switches必然存在因为后台根据switchid获取storagesubsystem--%>
     data = jsonData.storageData;
     if(isDataValid(data)){
     	var logs = jsonData.storageLogs;
     	for(var i = 0, len = data.length, dev, stsw; i < len; ++i){
     		<%--存储系统必须有sto_id(v_res_storagesubsystem), sw_id(v_res_switch), sto_name, sw_name, sto_ip, sw_ip--%>
			dev = data[i];
			if(storages[dev.sto_id] == undefined){
				storages[dev.sto_id] = addNode({
			       	text: dev.sto_name,
			       	deviceid: dev.sto_id,
			       	devicetype: stoType,
			       	storagetype: dev.os_type,
			       	ipaddress: dev.sto_ip,
			       	imgPath: stoImagePath,
			       	tipDialogSelector: tipDialogSelector,
			       	redirectURL: stoURL + dev.sto_id,
			       	alertInfo: getDeviceLogs(logs, dev.sto_id, 5)
		       });
			}
			if(sw2Switches[dev.sw_id]){
				stsw = swLinkSto[dev.sw_id];
				if(!(stsw && stsw == dev.sto_id)){
					addLink(sw2Switches[dev.sw_id], storages[dev.sto_id]);
					swLinkSto[dev.sw_id] == dev.sto_id;
			    }
			}
		}
     }
    // widthGap: 28, heightGap: 87,maxWidth + 10
    //stage.setCenter(10, 20);
    //appNode.setLocation(0, 0);
    //virtualVMNode.setLocation(0, 0);
    
    if(jsonData.nodePos){
    	scene.autoLayout = false;
    	JTopo.layout.layoutNode(scene, appNode, true);
    }
    else {
    	scene.autoLayout = true;
    	scene.doLayout(JTopo.layout.TreeLayout("down", 
	   		options.heightGap? options.heightGap : 28,
	   		options.widthGap? options.widthGap: maxWidth + 20
	   	));
    }
   	
   	bindPopupMenu($canvas, stage, scene, {
   		heightGap: options.heightGap? options.heightGap : 28,
	   	widthGap: options.widthGap? options.widthGap: maxWidth + 20
   	});
   	
    var offsetLeft, offsetTop;
    var isFullScreenState = true;
    $(document).bind(
	    "fullscreenchange webkitfullscreenchange mozfullscreenchange",
	    function(){
	        isFullScreenState = document.fullscreen || document.webkitIsFullScreen || document.mozFullScreen || false;
           var $li = $("#li2l___S_12__zH");
		  	if(isFullScreenState){
		  		$li.html("<img src='${path}/resource/js/contextMenu/images/arrow_in.png'/>退出全屏");
		  	}
		  	else { $li.html("<img src='${path}/resource/js/contextMenu/images/arrow_out.png'/>全屏显示"); }
	    }
	);

	<%--假如一台交换机只连接一台设备，那么它将被移除。这一步是移除这样的交换机--%>
	while(true){
		var eles = scene.findElements(function(e){
	    	return e.devicetype == "Switch" && (e.inLinks.length + e.outLinks.length) < 2;
	    });
	    if(eles && $.isArray(eles) && eles.length > 0){
	    	for(var i in eles){
	    		var key = eles[i].devicetype + "," + eles[i].deviceid;
	    		scene.remove(nodeSet[key]);
	    		nodeSet[key] = null; <%--将node也从nodeSet清除--%>
	    	}
	    }
	    else { break; }
	}
	//stage.centerAndZoom();
    var data = $canvas.data("posTreh89YYtzX");
    if(data == undefined){ data = {}; }
    for(var key in nodeSet){
    	if(nodeSet[key]){
    		data[key] = {
		     	x: (isNaN(nodeSet[key].x)? 0 : nodeSet[key].x) + (isNaN(scene.translateX)? 0 : scene.translateX) ,
		     	y: (isNaN(nodeSet[key].y)? 0 : nodeSet[key].y) + (isNaN(scene.translateY)? 0 : scene.translateY)
		    };
    	}
    }
    $canvas.data("posTreh89YYtzX", data);
};


var monitorInterval = null;
autoFunc = function(){
	$("#chooseAppMenuId input[name='chooseAppMenuId']:checked").trigger("click");
};
function setAuto(intervalTime){
	if(isNaN(intervalTime) == false && intervalTime > 0){ 
		monitorInterval = setInterval("autoFunc()", intervalTime * 1000); 
	}
	else { 
		if(monitorInterval){    
			clearInterval(monitorInterval); 
			monitorInterval = null; 
		} 
	} 
}

$(function(){
	var $timeRangeNav = $("#timeRangeNav");
	$timeRangeNav.hide();
	$timeRangeNav.find("a").click(function(){
		var $a = $(this);
		var time = $a.attr("time").toLowerCase();
		var startdate = new Date();
		if(time.indexOf("hour") >= 0){
			startdate.setHours(startdate.getHours() - 1);
		}
		else if(time.indexOf("day") >= 0){
			startdate.setDate(startdate.getDate() - 1);
		}
		else if(time.indexOf("week") >= 0){
			startdate.setDate(startdate.getDate() - 7);
		}
		else {
			startdate.setMonth(startdate.getMonth() - 1);
		}
		var devtype = $a.attr("devtype");
		var params = $timeRangeNav.data("params");
		params.startdate = startdate.Format("yyyy-MM-dd HH:mm:ss");
		$.ajax({
				url: "${path}/servlet/topo/TopoAction?func=ShowSummaryCurve",
				data: params,
				type: "post",
				dataType: "json",
				success:function(jsonData){
					if(jsonData.success){
						if(params.devtype == "Switch"){
							var value = jsonData.value;
	     					$("#container2816").html("").hide().children().remove();
	     					if(value && $.isArray(value) && value.length > 0){
	     						var series = {name: params.name};
	     						var data = [];
	     						$.each(value, function(index, vo){
	     							data.push(toFixed2(vo.data));
	     						});
	     						series.data = data;
	     						drawCurve({
	     							id: "container28161",
									title: time + " -- Total Port Data Rate",
									categories: undefined,
									yAxisTitle: "MB/Sec",
									unit: "MB/Sec",
									warningLineOption: { threshold: 0, threvalue: 0 },
								    series: [series]
	     						});
	     					}
	     					else {
	     						$("#timeRangeNav").hide();
	     						$("#container28161").html("<h2>没有数据</h2>");
	     					}
		       			}
		       			else {
		       				var value = jsonData.value;
			       			if(value && $.isArray(value) && value.length > 0){
			       				var series = {name: params.name, unit: "MB/Sec"};
	      						var data = [];
	      						$.each(value, function(index, vo){
	      							data.push(toFixed2(vo.a415));
	      						});
	      						series.data = data;
	      						drawCurve({
	      							id: "container28161",
									title: time + " -- Total IO Rate",
									categories: undefined,
									yAxisTitle: "Ops/Sec",
									unit: "Ops/Sec",
									warningLineOption: { threshold: 0, threvalue: 0 },
								    series: [series]
	      						});
			       			}
			       			else {
			       				$("#timeRangeNav").hide();
	      						$("#container28161").html("<h2>没有数据</h2>");
	      					}
			       		}
					}
					else {
						$("#myModal").html("<div style='height:100px;width:300px;margin-top:10px;'><h4>" + jsonData.msg + "</h4><center>3秒钟后消失...</center></div>");
						setTimeout("$('#myModal').modal('hide')", 3000);
					}
				},
				beforeSend:function(){ }
			});
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
		 	setAuto(parseInt($input.val()));
		 	$input.attr("checked", true);
		 }
	});
	
	var widthGap = undefined, heightGap = undefined;
	var $menu = changeColumn.initPopupMenuByAjax({
		selector: "#content",
	   	menuId: "chooseAppMenuId",
	   	ifnodata: {id: "modelContent", content: "请添加应用，并刷新页面"},
	  	url: "${path}/servlet/topo/TopoAction?func=GetAllAppData",
	   	params: {},
	   	width: 200,
	   	height: 200,
	   	inputtype: "radio",
	   	converter: function(vo){
	   		return {value: vo.appid, name: vo.appname, nodecount: vo.nodecount};
	   },
	   clickHandler: function($input){
	   		$input.attr("checked", true);
	   		var appid = $input.val();
	   		$.ajax({
				url: "${path}/servlet/topo/TopoAction?func=GetWindowsX86ServerTopoData",
				data: {appid: appid},
				type: "post",
				dataType: "json",
				success:function(jsonData){
					if(jsonData.success){
						drawWindowsX86ServerTopo(jsonData.value, {
							imgFolder: "${path}/resource/js/jtopo/img/",
							canvasId: "canvas",
							widthGap: widthGap,
							heightGap: heightGap
						});
						$("#showTipsWhenLoadTopo").hide();
					}
					else {
						$("#myModal").html("<div style='height:100px;width:300px;margin-top:10px;'><h4>" + jsonData.msg + "</h4><center>3秒钟后消失...</center></div>");
						setTimeout("$('#myModal').modal('hide')", 3000);
					}
				},
				beforeSend:function(){
					$("#showTipsWhenLoadTopo").show();
					$("#canvas").hide();
				}
			});
	   }
	});
	
	loadTopo = function(wGap, hGap){
		widthGap = wGap;
		heightGap = hGap;
		$("#chooseAppMenuId input[name='chooseAppMenuId']:checked").trigger("click");
		
	};
});
</script>

<div id="deviceAlertLog" style="display:none;"></div>
<div id="content">
	<div class="row-fluid">
		<div class="box span9" style="height:775px;">
			<div class="box-header well">
				<h2>应用拓扑 </h2>
				<div class="box-icon">
					<a id="chooseApp" href="javascript:void(0);" class="btn btn-round" title="选择应用" 
						onclick="changeColumn.showMenu(this,'chooseAppMenuId')" data-rel="tooltip"><i class="icon-eye-open"></i></a>
					<a id="topoRefreshFreq" href="javascript:void(0);" class="btn btn-round" title="选择拓扑图刷新频率" 
						onclick="changeColumn.showMenu(this,'topoRefreshFreqId')" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a id="AddAppdb3p1rguUkTI" href="#" class="btn btn-round" title="新增应用" data-rel="tooltip">
						<i class="icon icon-color icon-edit"></i></a>
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
						<!--<li id="li3___NLP5s_j_4"><img src="${path}/resource/js/contextMenu/images/zoom_in.png"/>放大</li>
						<li id="li4V__FNjO__Kll"><img src="${path}/resource/js/contextMenu/images/zoom_out.png"/>缩小</li>
						<li id="li5o29g0_8C__R7"><input type="checkbox">鼠标缩放</li>
						--><li id="keepIconsPosition"><img src="${path}/resource/js/contextMenu/images/ddr_memory.png"/>记住图标位置</li>
						<li id="autoLayout"><img src="${path}/resource/js/contextMenu/images/layout_content.png"/>自动布局</li>
						<li id="li6Yu_tR6265_YK"><img src="${path}/resource/js/contextMenu/images/accept.png"/>打开鹰眼</li>
						<!--<li id="setupTopoWHGap"><img src="${path}/resource/js/contextMenu/images/setting_tools.png"/>图标间隔</li>
						--><hr style="margin-top:2px;margin-bottom:2px;">
						<li id="li6_9__6_Ra__XH"><img src="${path}/resource/js/contextMenu/images/table_export.png" />导出PNG图</li>
					</ul>
				</div>
			</div>
		</div>
		
		<div class="box span3" style="height:775px;">
			<div class="box-header well">
				<h2 id="summaryPage">总览</h2>
				<!--<div class="box-icon">
					<a id="summary" class="btn btn-round" title="隐藏总览页面" data-rel="tooltip" style="">
						<i class="icon icon-color icon-arrow-e"></i>
					</a>
				</div>-->
				
				<div class="box-content" style="margin-top:20px;">
					<!-- tab切换标签开始 -->
					<ul class="nav nav-tabs" id="myTab">
						<li class="active">
							<a href="#deviceDetailTab">设备详细信息</a>
						</li>
						<!--<li class=""> <a href="#topnTab">TOPN</a> </li> -->
						<li class=""> <a href="#propertyTab">资产</a> </li>
						<li class=""> <a href="#topoCompTab">部件</a> </li>
					</ul>
				</div>
				<!-- tab切换标签结束 -->
				<div id="perfChart2" class="tab-content" style="overflow: visible;min-height:200px;">
					<!-- 性能曲线切换页开始 -->
					<div class="tab-pane active" id="deviceDetailTab">
						<div style="height:190px;">
							<table class="table table-bordered">
								<tbody>
									<tr>
										<td width="100px" style="text-align:right;">名称:</td>
										<td id="deviceName2816"></td>
									</tr>
									<tr>
										<td style="text-align:right;">设备类型:</td>
										<td id="deviceType2816"></td>
									</tr>
									<tr>
										<td style="text-align:right;">IP地址:</td>
										<td id="deviceIPAddress2816"></td>
									</tr>
									<tr>
										<td style="text-align:right;">新增事件数量:</td>
										<td id="eventCount2816"></td>
									</tr>
									<tr>
										<td colspan="2" id="goToEventDetailPageId" style="text-align:center">&nbsp;</td>
									</tr>
								</tbody>
							</table>
						</div>
						<div id="container2816" style="margin-top:0px;height:225px;width:308px;max-height:225px;"></div>
						<hr style="height:5px;width:100%;color:red;">
						<div>
							<div class="box-header well" style="margin-top: -20px;">
								<div id="timeRangeNav" style="width:180px;font-size:13px;" class="box-icon">
									<a style="width:46px;text-align:center;"  href="javascript:void(0);" time="last hour">1小时</a>
									<label style="width:8px;float:left;">|</label>
									<a style="width:30px;text-align:center;"  href="javascript:void(0);" time="last day">1天</a>
									<label style="width:8px;float:left;">|</label>
									<a style="width:30px;text-align:center;" href="javascript:void(0);" time="last week">1周</a>
									<label style="width:8px;float:left;">|</label>
									<a style="width:30px;text-align:center;" href="javascript:void(0);" time="last month">1月</a>
								</div>
							</div>
							<div id="container28161" style="margin-top:0px;height:225px;width:308px;max-height:225px;"></div>
						</div>
					</div>
					
					<div class="tab-pane" id="propertyTab" style="padding-top:10px;">
						<table class="table table-bordered" id="serverPropertyTable">
							<tbody>
								<tr>
									<td width="100px" style="text-align:right;">cpu数量:</td>
									<td id="serverCpuCount"></td>
								</tr>
								<tr>
									<td style="text-align:right;">内存大小(MB):</td>
									<td id="serverMemSize"></td>
								</tr>
								<tr>
									<td style="text-align:right;">硬盘总容量(GB):</td>
									<td id="serverHDSize"></td>
								</tr>
								<tr>
									<td style="text-align:right;">可用容量(GB):</td>
									<td id="serverAvailableCapacity"></td>
								</tr>
							</tbody>
						</table>
						
						<table class="table table-bordered" id="switchPropertyTable">
							<tbody>
								<tr>
									<td width="100px" style="text-align:right;">端口数量:</td>
									<td id="switchPortCount"></td>
								</tr>
							</tbody>
						</table>
						
						<table class="table table-bordered" id="storagePropertyTable">
							<tbody>
								<tr>
									<td width="100px" style="text-align:right;">端口数量:</td>
									<td id="stoPortCount"></td>
								</tr>
								<tr>
									<td style="text-align:right;">磁盘数量:</td>
									<td id="stoDiskCount"></td>
								</tr>
								<tr>
									<td style="text-align:right;">卷数量:</td>
									<td id="stoVolumeCount"></td>
								</tr>
								<tr>
									<td style="text-align:right;">池容量:</td>
									<td id="stoPoolCount"></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="tab-pane" id="topoCompTab" style="padding-top:10px;">
						<div id="topoCompContent" style="width:100%;height:680px;overflow:hidden;overflow-y:auto;">
							<div id="showTipsWhenLoadComp" style="display:none;">
								<div class='modal-header'><h3>操作提示</h3></div>
								<div class='modal-body' align='center' style='height:160px;line-height:80px;'>
									<img src='${path}/resource/img/loading.gif'/><span>正在获取部件的数据, 请稍候...</span>
								</div>
								<div class='modal-footer'></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="box span12 box-header well" style="margin-left:0;"></div>
	</div>
</div>

<%@include file="/WEB-INF/views/include/footer.jsp"%>