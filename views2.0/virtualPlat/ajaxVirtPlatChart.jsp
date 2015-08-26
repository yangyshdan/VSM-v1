<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	//获取数据,绘制性能图
	var cpuPerfData = ${cpuPerfData};
	var memPerfData = ${memPerfData};
	var netPerfData = ${netPerfData};
	var diskPerfData = ${diskPerfData};
	var cpuTopNData = ${cpuTopNData};
	var memTopNData = ${memTopNData};
	Public.drawPrfLine("perfContent_cpu",cpuPerfData);
	Public.drawPrfLine("perfContent_mem",memPerfData);
	Public.drawPrfLine("perfContent_network",netPerfData);
	Public.drawPrfLine("perfContent_disk",diskPerfData);
	Public.drawTopn("perfContent_topN_cpu",cpuTopNData);
	Public.drawTopn("perfContent_topN_mem",memTopNData);
});
</script>

<!-- CPU Busy Performance Chart -->
<div class="box span4" id="cpuChart" style="width:24%;margin-left:10px;">
	<div class="box-header well">
		<h2 style="height:20px;overflow: hidden;">
			CPU(%User)
		</h2>
	</div>
	<div class="box-content" id="box-content_cpu" style="height:200px;">
		<div class="clearfix" id="perfContent_cpu" style="height:200px;"></div>
	</div>
</div>
<!-- Memory Used Performance Chart -->
<div class="box span4" id="memChart" style="width:24%;">
	<div class="box-header well">
		<h2 style="height:20px;overflow: hidden;">
			Mem(%Used)
		</h2>
	</div>
	<div class="box-content" id="box-content_mem" style="height:200px;">
		<div class="clearfix" id="perfContent_mem" style="height:200px;"></div>
	</div>
</div>
<!-- Network Performance Chart -->
<div class="box span4" id="networkChart" style="width:24%;">
	<div class="box-header well">
		<h2 style="height:20px;overflow: hidden;">
			Network(Total Packets/sec)
		</h2>
	</div>
	<div class="box-content" id="box-content_network" style="height:200px;">
		<div class="clearfix" id="perfContent_network" style="height:200px;"></div>
	</div>
</div>
<!-- Disk Performance Chart -->
<div class="box span4" id="diskChart" style="width:24%;">
	<div class="box-header well">
		<h2 style="height:20px;overflow: hidden;">
			Disk(Total Bytes/sec)
		</h2>
	</div>
	<div class="box-content" id="box-content_disk" style="height:200px;">
		<div class="clearfix" id="perfContent_disk" style="height:200px;"></div>
	</div>
</div>