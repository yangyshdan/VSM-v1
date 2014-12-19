<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/index.js"></script>
<script src="${path }/resource/js/project/homepage.js"></script>
<script src="${path}/resource/js/project/widget.js"></script>
<style>
	#size-setting ul li a:HOVER {
	background-color:#F7E7BE;
	border:1px #F7C372 solid;
	}
	#size-setting ul li a {
	  	-moz-border-radius: 0px;      /* Gecko browsers */
   	 	-webkit-border-radius: 0px;   /* Webkit browsers */
    	border-radius:0px;            /* W3C syntax */
	}
	.assetsImg {
		border: 0px;
		width:40%;
	}
	.assetsTable {
		margin-right: 16%;
		float:right;
		margin-top: 7%;
	}
	.multiselect-group{
		font-weight: bold;
		text-decoration: underline;
	}
	.popover{
		width:19%;	
	}
</style>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>
<div id="content">
	<div class="span12" style="margin-top:-10px;margin-bottom: 10px;">
		<div style="float:left;">
			<select id="selectError" style="">
				<c:forEach items="${dashList}" var="item">
					<option value="${item.fid}">${item.fname}</option>
				</c:forEach>
			</select>
		</div>
		<div style="float:left;margin-left:-2px;border:none">
			<a class="btn btn-success" onclick="Widget.addConsole()" data-rel="tooltip" title="添加模板" style="border-left:none;border-radius: 0 4px 4px 0;margin-left:-1px;"><i class="icon-plus icon-white"></i></a>
		</div>
		<div style="float:right;margin-top:-8px;cursor: pointer;" data-rel="tooltip" title="删除控制台" id="delConsole">
		</div>
	</div>
	<div id="homeContent" class="tab-content" style="overflow: visible;min-height:200px;">
		<div id="size-setting">
			<iframe id="resize1" style="z-index:1;margin-top:10px;display:none;position:absolute;" src="javascript:void(0)" frameborder="0"></iframe>
			<div id="resize2" class="" style="margin-top:10px;display:none;position:absolute;z-index:2">
				<ul style="margin:0px;list-style: none;border:2px #000 solid;">
					<li><a class="btn"><i class="icon-align-justify"></i>1x1</a></li>
					<li><a class="btn"><i class="icon-th-large"></i>1x2</a></li>
					<li><a class="btn"><i class="icon-th"></i>1x3</a></li>
				</ul>
			</div>
		</div>
		<div class="row-fluid sortable" style="margin-top: 10px;" id="logContent" >
			<a  href="${path}/servlet/alert/DeviceAlertAction?type=App"  id="App"  class="well span3 top-block" style="color: black;" >
				<img class="assetsImg"  alt="fabric" src="${path}/resource/img/project/app.png">
				<span id="appAssets" class="notification">应用</span>
			</a>
			<a id="Physical"  href="${path}/servlet/alert/DeviceAlertAction?type=Physical"  class=" well span3 top-block" style="color: black;">
				<img class="assetsImg"  alt="host" src="${path}/resource/img/project/hv.png">
				<span id="hvAssets" class="notification">物理机</span>
			</a>
			<a id="Virtual"  href="${path}/servlet/alert/DeviceAlertAction?type=Virtual"  class="well span3 top-block" style="color: black;">
				<img class="assetsImg"  alt="host" src="${path}/resource/img/project/host.png">
				<span id="vmAssets" class="notification">虚拟机</span>
			</a>
			<a id="Switch"  href="${path}/servlet/alert/DeviceAlertAction?type=Switch"  class="well span3 top-block" style="color: black;">
				<img class="assetsImg"  alt="switch" src="${path}/resource/img/project/switch.png">
				<span id="switchAssets" class="notification">交换机</span>
			</a>
			<a id="Storage"  href="${path}/servlet/alert/DeviceAlertAction?type=Storage"  class="well span3 top-block" style="color: black;">
				<img class="assetsImg" alt="storage" src="${path}/resource/img/project/StorageSystemBase.png">
				<span id="storageAssets" class="notification">存储系统</span>
			</a>
		</div>
		<div id="prefContent">

		</div>
	</div>
</div>

<script type="text/javascript">
function resize(rootDiv,obj){
	$("#resize1").attr("height",$("#resize2").height());
	$("#resize1").attr("width",$("#resize2").width());
	$("#resize1").slideToggle(200);
	$("#resize2").slideToggle(200);
	$("#resize1").css("left",$(obj).offset().left);
	$("#resize1").css("top",$(obj).offset().top+$(obj).innerHeight());
	$("#resize2").css("left",$(obj).offset().left);
	$("#resize2").css("top",$(obj).offset().top+$(obj).innerHeight());

	var link = $("#size-setting ul li a");
	$.each(link,function(i){
		if(this.text=='1x1'){
			$(this).attr('href',"javascript:Widget.sizesetting('"+rootDiv+"',4)");
		}else if(this.text=='1x2'){
			$(this).attr('href',"javascript:Widget.sizesetting('"+rootDiv+"',6)");
		}else if(this.text=='1x3'){
			$(this).attr('href',"javascript:Widget.sizesetting('"+rootDiv+"',12)");
		}
	});
}

$(function(){
	//添加告警内容
	Index.appendAssets(${assetsJson});
	//日志处理
	$(".btn-group .dropdown-toggle").css({"border-bottom-right-radius":"0px","border-top-right-radius":"0px"});
	Index.loadAlert(${logCount});
	$(".info-block,.warning-block,.error-block").popover({
		trigger:'manual',
		placement:'bottom',
		title:'报警详细',
		html:true,
		content:$(this).attr("data-content"),
		animation:false
	}).on("mouseenter",function(){
		var _this = this;
		$('.popover').remove();
		$(this).siblings(".popover").on("mouseleave",function(){
			$(_this).popover("hide");
		});
		$(this).popover("show");
	}).on("mouseleave",function(){
		var _this = this;
		setTimeout(function(){
			if(!$(".popover:hover").length){
				$(_this).popover("hide");
			}
		},5000);
	});
	 $(document.body).click(function(){
        $('.popover').remove();
    });
	 $(".info-block").prepend('<i class="icon icon-darkgray icon-info" style="float: left;margin-top: 2px;"></i>');
	 $(".warning-block").prepend('<i class="icon icon-color icon-alert"  style="float: left;margin-top: 2px;"></i>');
	 $(".error-block").prepend('<i class="icon icon-color icon-cross" style="float: left;margin-top: 2px;"></i>');

	 //加载图表
	 Index.changeCharts();
});
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>