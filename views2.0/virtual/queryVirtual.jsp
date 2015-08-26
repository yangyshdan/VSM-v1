<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/resource/js/project/util.js"></script>

<style>
	.table-condensed > tbody > tr > td {
	  padding: 2px;
	}
	.table th, .table td { 
		text-align: left;
	}
</style>
<script type="text/javascript">
	$(function(){
		$("#legend").bootstrapSwitch();
		var $graphType = $("#graphType");
		if("${level}" != "3"){
			$graphType.bootstrapSwitch();
			$graphType.bootstrapSwitch("setOnLabel", "曲线图");
			$graphType.bootstrapSwitch("setOffLabel", "TOPN图");
			$graphType.bootstrapSwitch("setOnClass", "success");
			$graphType.bootstrapSwitch("setOffClass", "warning");
			$graphType.on("switch-change", function (e, data) {
				var $t = $("#topnValueTD");
				var $t2 = $("div#legend");
			    if(data.value){ 
			    	$t.hide();
			    	$t2.parent().show();
			    }
			    else { 
			    	$t.show();
			    	$t2.parent().hide();
			    }
			});
		}
		else {
			$graphType.parent().parent().remove();
			$("#devtypeAndDevice").hide();
		}
		
		$("#queryTime button").click(function(){
			var $btn = $(this);
			$btn.addClass("btn-primary");
			$btn.siblings().removeClass("btn-primary");
			var btnVal = $btn.attr("value").toLowerCase();
			$("#queryTime input").val(btnVal);
			//oneHour,fourHours,oneDay,oneWeek,oneMonth,oneYear
			var datefmt = "yyyy-MM-dd HH:mm:ss";
			var current = new Date();
			var ed = current.Format(datefmt);
			var sd;
			switch(btnVal){
				case "onehour":
				current.setHours(current.getHours() - 1);
				sd = current.Format(datefmt);
				break;
				case "fourhours":
				current.setHours(current.getHours() - 4);
				sd = current.Format(datefmt);
				break;
				case "oneday":
				current.setDate(current.getDate() - 1);
				sd = current.Format(datefmt);
				break;
				case "oneweek":
				current.setDate(current.getDate() - 7);
				sd = current.Format(datefmt);
				break;
				case "onemonth":
				current.setMonth(current.getMonth() - 1);
				sd = current.Format(datefmt);
				break;
				case "oneyear":
				current.setFullYear(current.getFullYear() - 1);
				sd = current.Format(datefmt);
				break;
			}
			$("#startTime").val(sd);
			$("#endTime").val(ed);
		});
		$("#startTime").click(function(){
			var $btn = $("#queryTime button");
			$btn.siblings().removeClass("btn-primary");
			$btn.removeClass("btn-primary");
		});
		$("#endTime").click(function(){
			var $btn = $("#queryTime button");
			$btn.siblings().removeClass("btn-primary");
			$btn.removeClass("btn-primary");
		});
	});
</script>
<script>
$(document).ready(
	function() {
		$("#device").multiselect({
			includeSelectAllOption : true,
			maxHeight : 150
		});
		$("#prfField").multiselect({
			enableFiltering : 1,
			maxHeight : 300,
			maxWdith : 200
		});
		
		$.each($("#storageType option"),function(){
			if($(this).val() == "${historyConfig.fdevicetype}"){
				$(this).attr("selected","selected");
			}
		});
		var timeType = $("#time_type option");
		$.each(timeType,function(){
			if(this.value=="${historyConfig.time_type}"){
				$(this).attr("selected","selected");
			}
		});
		var level = parseInt("${level}");
		
		if(level == 3){
			$("#storageType").attr("disabled","disabeld");
			//$("#device").attr("disabled", "disabled");
		}
		else {
			$("#storageType").attr("disabled","disabeld");
		}
		var stype="${type}";
		$.each($("#storageType option"),function(){
			if($(this).val() == stype && stype != ''){
				$(this).attr("selected","selected");
			}
		});
		$("#threValue").keypress(function(){
			var a = this.value;
			if(isNaN(a)){
				$(this).val(a.substring(0,a.length-1));
			}
		}).keyup(function(){
			var a = this.value;
			if(isNaN(a)){
				$(this).val(a.substring(0,a.length-1));
			}
		});
		defaultStorage();
		$("#storageType").change(function(){
			$("#device option").remove();
			$("#prfField option").remove();
			defaultStorage();
		});
});

function defaultStorage(){
	var devices = "${historyConfig.fdevice}";
	var dev = devices.split(",");
	var type=$("#storageType option:selected").val();
	var jsonList = ${devList};
	
	var str = "";
	for(var i in jsonList){
		str+="<option ";
		for(var j=0, len = dev.length; j < len; ++j){
			if(jsonList[i].id == dev[j]){
				str+="selected='selected'";
			}
		}
		str+= " value='%s'>%s</option>".jFormat(jsonList[i].id, jsonList[i].name);
	}
	$("#device").append(str).multiselect("rebuild");
	var kp = "${historyConfig.fprfid}";
	
	var k = kp.split(",");
	var kpis = ${kpisList};
	var str2 = "";
	var str3 = " style = 'color: #CC0000;' onclick='color(2);' ";
	var str4 = " style = 'color: #CC9900;' onclick='color(1);' ";
	for(var i in kpis){
		if(type==kpis[i].fstoragetype){
		//重要程度的判断开始
		if(kpis[i].fimp != 0 && kpis[i].fimp != null )
		{
			str2+="<option ";
		//重要程度的判断结束
		for(var j=0;j<k.length;j++){
			if("'"+kpis[i].fid+"'"==k[j]){
				str2+="selected=selected";
			}
		}
		str2+=" value='"+kpis[i].fid+"'>"+kpis[i].ftitle+"("+kpis[i].funits+")"+"</option>";
	}
	}
	}
	$("#prfField").append(str2);
	$("#prfField").multiselect('rebuild');
}

function resetFunc(){
	var $qpage = $("#queryPage");
	var $storageType = $($qpage.find("select#storageType"));
	if($storageType.attr("disabled") == undefined){<%--如果不是固定的，那么就重置不让option被选中--%>
		$storageType.find("option:selected").removeAttr("selected");
		$storageType.multiselect("refresh");
	}
	var $device = $($qpage.find("select#device"));
	if($device.attr("disabled") == undefined && $("#devtypeAndDevice").css("display") != "none"){
		$device.find("option:selected").removeAttr("selected");
		$device.multiselect("refresh");
	}
	$qpage.find("select#prfField option").eq(0).attr({selected: true});
	$qpage.find("select#time_type option").eq(0).attr({selected: true});
	$qpage.find("#queryTime button").removeClass("btn-primary");
	$qpage.find("input#startTime").val("");
	$qpage.find("input#endTime").val("");
	$qpage.find("input#threValue").val("");
	$qpage.find("input#topnValue").val("");
}

function saveInfoHgc(){
	var storageType = $("#storageType").val();
	if(storageType==null || storageType==""){
		alert("请选择存储系统类型");
		return false;
	}
	var device = $("#device").val();
	if(device==null || device==""){
		alert("请选择设备类型");
		return false;
	}
	var prfField = $("#prfField").val();
	if(prfField==null || prfField==""){
		alert("请选择性能指标");
		return false;
	}
	var startTime = $("#startTime").val();
	if(startTime==null || startTime==""){
		alert("请选择开始时间");
		return false;
	}
	var endTime = $("#endTime").val();
	if(endTime==null || endTime==""){
		alert("请选择结束时间");
		return false;
	}
    if(!dateCompare(startTime,endTime)) {                            
        bAlert("结束日期不能小于开始日期！");
        return false;
	}
    if($("#time_type").val() == ''){
		alert("请输入时间类型");
		return false;
	}
	var level = parseInt("${level}");
	var graphT = $("#graphType div").first().hasClass("switch-on")? 
		<%=com.huiming.sr.constants.SrContant.GRAPH_TYPE_LINE%> : <%=com.huiming.sr.constants.SrContant.GRAPH_TYPE_TOPN%>;
	var topnValue = parseInt($("input#topnValue").val());
	if(level == 3){
		graphT = <%=com.huiming.sr.constants.SrContant.GRAPH_TYPE_LINE%>;
		topnValue = 5;
	}
	if(graphT != <%=com.huiming.sr.constants.SrContant.GRAPH_TYPE_LINE%>){
		/*if(isNaN(topnValue) || topnValue <= 0){
			alert("请输入TOPN数量");
			return false;
		}*/
	}
	else { topnValue = 5; }
	/*var yname = $("#yname").val();
	if(yname == null || yname == ""){
		alert("请输入Y轴名称");
		return false;
	}*/
	var threValue = parseFloat($("#threValue").val());
	threshold = isNaN(threValue)? 0 : 1;
	var jsonVal = $("#condFormHgc").serializeArray();
	jsonVal.push({name: "legend", value: $("#legend div").first().hasClass("switch-on")? 1:0});
	jsonVal.push({name: "graphType", value: graphT});
	jsonVal.push({name: "threshold", value: threshold});
	
	$.ajax({
		url: "${pageContext.request.contextPath}/servlet/virtual/VirtualAction?func=VirtualPrf",
		data: jsonVal,
		success:function(result){
			if(result == "true"){
				bAlert("操作成功！", "", [{func:"doAfterSucc();", text:"确定"}]);
			}else{
				bAlert("操作失败，请稍候再试！");
			}
		}
	});
}
</script>

<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="condFormHgc">
			<fieldset>
				<div class="control-group">
					<table class="table-condensed">
						<tbody>
							<tr id="devtypeAndDevice">
								<td width="20%">&nbsp;</td>
								<td id="storageTypeTd">
									<label class="control-label" for="storageType">设备类型</label>
									<div class="input-prepend input-append">
										<select class="form-control" name="storageType" id="storageType" style="width:180px;"> 
											<option value="HOST">虚拟机</option>
										</select>
									</div>
									<c:choose>
										<c:when test="${not empty vmId and vmId>0 }">
											<input type="hidden" name="vmId" value="${vmId}" />
											<input type="hidden" name="devId" value="${vmId}" />
											<input type="hidden" name="hypervisorId" value="${hypervisorId}">
										</c:when>
									</c:choose>
									<input type="hidden" name="storageType" value="HOST" />
								</td>
								<td id="deviceTd">
									<label class="control-label" for="device">设备</label>
									<div class="input-prepend input-append">
										<select id="device" multiple="multiple" name="device"></select>
									</div>
									<input type="hidden" name="devId" value="${historyConfig.fdevice}"/>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>					
							
							<tr>
								<td width="20%">&nbsp;</td>
								<td id="prfFieldTd">
									<label class="control-label" for="prfField">性能</label>
									<div class="input-prepend input-append">
										<select id="prfField" name="prfField"></select>
									</div>
								</td>
								<td>
									<label class="control-label" for="time_type">时间粒度</label>
									<div class="input-prepend input-append">
										<select id="time_type" name="time_type" style="width:180px;">
											<option value="minute">分钟</option>
											<option value="hour">小时</option>
											<option value="day">天</option>
										</select>
									</div>
								</td>
								<td >&nbsp;</td>
								<td >&nbsp;</td>
							</tr>
							<tr>
								<td width="20%">&nbsp;</td>
								<td width="400px;">
									<label class="control-label" for="queryTime1">时间范围</label>
									<div id="queryTime" class="controls" style="margin-left: 0px;">
					                  	<input name="queryTime" type="hidden" value="fourHours">
					                  	<button id="queryTime1" type="button" class="btn btn-default" value="oneHour">1时</button>
					                  	<button type="button" class="btn btn-default" value="fourHours">4时</button>
					                  	<button type="button" class="btn btn-default" value="oneDay">1天</button>
					                  	<button type="button" class="btn btn-default" value="oneWeek">1周</button>
					                  	<button type="button" class="btn btn-default" value="oneMonth">1月</button>
					                  	<%-- <button type="button" class="btn btn-default" value="oneYear">1年</button>--%>
					                </div>
								</td>
								<td width="100px;">
									<label class="control-label" for="startTime">&nbsp;</label>
									<div class="input-prepend input-append">
										<input value="${historyConfig.fstarttime}" name="startTime" id="startTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
									</div>
									--
									<div class="input-prepend input-append">
										<input value="${historyConfig.fendtime}" name="endTime" id="endTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
									</div>
								</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td width="20%">&nbsp;</td>
								<td>
									<label class="control-label" for="a5">画图类型</label>
									<div id="graphType" class="switch" tabindex="0">
						                <c:choose>
											<c:when test="${historyConfig.graphtype == 1}"><%--1表示画TOPN图 --%>
												<input id="a5" data-no-uniform="true" type="checkbox" >
											</c:when>
											<c:otherwise>
												<input id="a5" data-no-uniform="true" type="checkbox"  checked="checked">
											</c:otherwise>
										</c:choose>
							         </div>
								</td>
								<td colspan="3"></td>
									<%--<c:choose>
										<c:when test="${historyConfig.graphtype == 1}">1表示画TOPN图 
											<div id="topnValueTD">
										</c:when>
										<c:otherwise>
											<div id="topnValueTD" style="display:none;">
										</c:otherwise>
									</c:choose>
										<label class="control-label">TOPN数量</label>
										<div class="controls" style="margin-left:0px;">
											<input placeholder="TOPN数量" type="text" name="topnValue" id="topnValue" style="width:180px;" 
												value="${historyConfig.topnvalue}">
										</div>
									</div>
								</td>--%>
							</tr>
							<tr>
								<td width="20%">&nbsp;</td>
									<c:choose>
									<c:when test="${historyConfig.graphtype == 1}"><%--1表示画TOPN图 --%>
										<td style="display:none;">
									</c:when>
									<c:otherwise>
										<td>
									</c:otherwise>
								</c:choose>
									<label class="control-label" for="a3">显示曲线名称</label>
									<div id="legend" class="switch" tabindex="0">
						                <c:choose>
											<c:when test="${historyConfig.flegend==1}">
												<input id="a3" data-no-uniform="true" type="checkbox" checked="checked" >
											</c:when>
											<c:otherwise>
												<input id="a3" data-no-uniform="true" type="checkbox">
											</c:otherwise>
										</c:choose>
							         </div>
								</td>
								<td colspan="3">
									<label class="control-label">阀值告警线</label>
									<div class="controls" style="margin-left:0px;">
										<input placeholder="告警值" type="text" name="threValue" id="threValue" style="width:180px;" value="${historyConfig.fthrevalue}">
									</div>
								 </td>
							</tr>
							<tr>
								<td colspan="5">
									<center>
									<input type="hidden" name="level" value="${level}" />
									<a onclick="saveInfoHgc();" class="btn btn-primary">保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<a class="btn" type="reset" onclick="resetFunc()">重置</a>
									</center>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</fieldset>
		</form>
	</div>
</div>

