<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/pandect/platens.js"></script>
<script type="text/javascript">
//TPDR开始
	$(function() {
		var json = ${PortRateData};
		Public.drawPrfLine("prfContent22",json);
		var PortdataTopNData=${PortdataTopNData};
		Public.drawTopn("ports", PortdataTopNData);
	});
	//TPDR结束
</script>

<div id="charts_2_1" class="box span4"
	style="margin: 5px 5px 0px 10px; width: 49%; height:293px;">
	<div class="box-header well">
		<h2 style="width: 70%; height: 20px; overflow: hidden;">
			性能曲线(Total Port Date Rate)
		</h2>
	</div>
	<div class="box-content" id="loadcontent2"
		style="float: left; width:98%; height: 250px;">
		<div id="prfContent22" style="width: 98%; height: 250px;"></div>
	</div>
</div>
<!--性能曲线结束  -->
<!--Total Port Data Rate action -->
<div id="charts_2_1" class="box span4"
	style="margin: 5px 5px 0px 0px; width: 49%; height:293px;">
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