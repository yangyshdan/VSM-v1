<%@ page language="java" pageEncoding="UTF-8"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/js/jquery.steps/css/jquery.steps.css">
<script src="${pageContext.request.contextPath}/resource/js/jquery.steps/jquery.steps.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/js/jtopo/js/editTopo.js"></script>
<style>
	.multiselect-container span.add-on { float: left; }
</style>
<script type="text/javascript">
	var historyData = getHistoryData(${history});
	$(function() {
		$("#wizard").steps({
			headerTag : "h2",
			bodyTag : "section",
			transitionEffect : "slideLeft",
			stepsOrientation : "vertical",
			labels: {
		        cancel: "取  消",
		        current: "当前页面:",
		        pagination: "分页",
		        finish: "提交",
		        next: "下一步",
		        previous: "上一步",
		        loading: "正在加载页面 ..."
		    },
			enableCancelButton: true,
			onCanceled: function (event) {
				var $dlg = $("#myModal");
				$dlg.modal("hide");
				$dlg.removeAttr("style");
			},
			onStepChanging: function(event, currentIndex, newIndex){
				if(currentIndex > newIndex){ return true; }<%--说明返回上一步--%>
				var $showTips = $("#showTips").hide();
				//console.log("currentIndex: " + currentIndex);
				switch(currentIndex){
					case 0: 
						var isOk = doValidateAppForm(event.target, $showTips);
						if(isOk){
							phyMachine.doLoad(historyData);
						}
						return isOk;
					case 1:
						var is = phyMachine.doValidate(event.target, $showTips);
						return is;
					case 2: return phySw.doLoadPhySwitch();
					case 3: 
						if(!phySw.doValidate($showTips)){ return false; }
						swSto.doLoadSwSto11207Form();
						break;
					case 4: 
						if(!swSto.doValidate($showTips)){ return false; }
						return doLoadPoolForm(event.target, $showTips);
					case 5:
						if(!stoPool5213.doValidate($showTips)){ return false; }
						doLoadVolumeForm();
						break;
					case 6:
						if(!poolVol5123.doValidate($showTips)){ return false; }
						doLoadTable(); break;
				}
				return true;
			},
			onFinishing: function(event, currentIndex){
				var jsonData = doFinishing($("#showTips"));
				var $dlg = $("#myModal");
				$dlg.modal("hide");
				$dlg.removeAttr("style");
				return jsonData.success;
			},
			onFinished:function (event, currentIndex) { 
				var $dlg = $("#myModal");
				$dlg.modal("hide");
				$dlg.removeAttr("style");
				var App = App || {};
				if($.isFunction(App.dataFilter)){ App.dataFilter(); }<%-- 刷新页面 --%>
			}
		});
		<%-- 如果有历史记录，那么就从一开始就初始化app --%>
		if(historyData){
			if(historyData.appData){
				var $appForm2816 = $("#appForm2816");
				$appForm2816.find("input[name='appName']").val(historyData.appData.appname);
				$appForm2816.find("input[name='description']").val(historyData.appData.appdesc);
			}
		}
	});
</script>

<div class="content">
	<small id="showTips" style="color:red;display:none;"></small>
	<div id="wizard">
		<h2>业务系统</h2>
		<section><jsp:include page="listApp.jsp"></jsp:include></section>

		<h2>物理机</h2>
		<section><jsp:include page="listPhysicalMachine.jsp"></jsp:include></section>
		
		<h2>虚拟机</h2>
		<section><jsp:include page="listVirtualMachine.jsp"></jsp:include></section>
		
		<%-- 暂时不要端口，但是保留该段代码，由物理机与交换机替换 --%>
		<h2>物理机与交换机</h2>
		<section><jsp:include page="listPhySwitch.jsp"></jsp:include></section>
		
		<%-- 以下的交换机由选择的物理机推导出来 --%>
		<h2>交换机与存储系统</h2>
		<section><jsp:include page="listSwitchStorage.jsp"></jsp:include></section>
		
		<h2>阵列/池</h2>
		<section><jsp:include page="listPool.jsp"></jsp:include></section>
		
		<h2>卷</h2>
		<section><jsp:include page="listVolume.jsp"></jsp:include></section>
		
		<h2>信息汇总</h2>
		<section><jsp:include page="listSummary.jsp"></jsp:include></section>
	</div>
</div>
