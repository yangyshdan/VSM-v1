<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
//热量
//端口
$(function(){
 	var PortTopNData=${PortTopNData};
 	var HeatTopNData=${HeatTopNData};
 	////if(PortTopNData==0){
 	//}else{
	Public.drawTopn("ports", PortTopNData);
	Public.drawTopn("heats", HeatTopNData);
	//}
});

</script>

<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 0px; width: 49%; float: right;">
	<div class="box-header well">
		<h2 style="width: 70%;height: 20px; overflow: hidden;">Total I/O (overall) Top5</h2>
	</div>
	<div id="box_content_2_1" class="box-content" style="height: 250px;">
	<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
	<div id="heats" style="position: relative; overflow: hidden; width:90%;height: 250px;left: 0px; top: 0px;" class="highcharts-container"></div>
	</div>
	</div>
</div>
<!-- 热量结束 -->
<!-- 端口开始 -->
<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 0px;width: 49%;">
	<div class="box-header well">
		<h2 style="width: 70%;height: 20px; overflow: hidden;">Total Port I/O Rate Top5</h2>
	</div>
	<div id="box_content_2_1" class="box-content" style="height: 250px;">
	<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
	<div id="ports" style="position: relative; overflow: hidden; width:90%;height: 250px;left: 0px; top: 0px; text-align: center;" class="highcharts-container"></div>
	</div>
	</div>
</div>