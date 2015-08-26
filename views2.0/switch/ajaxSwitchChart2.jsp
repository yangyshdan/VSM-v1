<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/widget.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/pandect/platens.js"></script>
<script type="text/javascript">
$(function(){
	var PordTopNData=${PordTopNData};
	var PorsTopNData=${PorsTopNData};
	var pie=${Pies};
	Public.drawTopn("pord", PordTopNData);
	Public.drawTopn("pors", PorsTopNData);
	Widget.drawPie("allincident", pie);
});
$(function(){
	var PordTopNData=${PordTopNData};
	var PorsTopNData=${PorsTopNData};
	var pie=${Pies};
	Public.drawTopn("pord", PordTopNData);
	Public.drawTopn("pors", PorsTopNData);
	//Public.drawTopn("incident", ints);
	Widget.drawPie("allincident", pie);
	var inname1="${inname1}";
	var value1=${value1};
	var inname2="${inname2}";
	var value2=${value2};
	var inname3="${inname3}";
	var value3=${value3};
	var inname4="${inname4}";
	var value4=${value4};
	var inname5="${inname5}";
	var value5=${value5};
	var starTime="${startTime}";
	var endTime="${endTime}";
	pla.platen(inname1,value1,inname2,value2,inname3,value3,inname4,value4,inname5,value5,starTime,endTime)
});
</script>

<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 5px; width: 49%; float: left;">
	<div class="box-header well">
		<h2 style="width: 70%;height: 20px; overflow: hidden;">Total Port Data Rate Top5</h2>
	</div>
	<div id="box_content_2_1" class="box-content" style="height: 250px;">
	<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
	<div id="pord" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px;" class="highcharts-container"></div>
	</div>
	</div>
</div>
<div id="charts_2_1" class="box span4" style=" margin: 0px 5px 0px 0px;width: 49%; float: right;">
	<div class="box-header well">
		<h2 style="width: 70%;height: 20px; overflow: hidden;">Total Port Packet Rate Top5</h2>
	</div>
	<div id="box_content_2_1" class="box-content" style="height: 250px;">
	<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
	<div id="pors" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px; text-align: center;" class="highcharts-container"></div>
	</div>
	</div>
</div>
<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 5px;width: 49%; float: left;">
	<div class="box-header well">
		<h2 style="width: 70%;height: 20px; overflow: hidden;">事件 Top5</h2>
	</div>
	<div id="box_content_2_1" class="box-content" style="height: 250px;">
	<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
	<div id="incident" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px; text-align: center;" class="highcharts-container"></div>
	</div>
	</div>
</div>
<div id="charts_2_1" class="box span4" style=" margin: 5px 5px 0px 5px;width: 49%; float: right;">
	<div class="box-header well">
		<h2 style="width: 70%;height: 20px; overflow: hidden;">所有交换机事件分布(%)</h2>
	</div>
	<div id="box_content_2_1" class="box-content" style="height: 250px;">
	<div id="perfContent_2_1" class="clearfix" style="height: 250px;" data-highcharts-chart="2">
	<div id="allincident" style="position: relative; overflow: hidden; width:98%;height: 250px;left: 0px; top: 0px;" class="highcharts-container"></div>
	</div>
	</div>
</div>