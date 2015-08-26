<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
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
		$("#legend2").bootstrapSwitch();
		var $graphType = $("#graphType").bootstrapSwitch();
		$graphType.bootstrapSwitch("setOnLabel", "曲线图");
		$graphType.bootstrapSwitch("setOffLabel", "TOPN图");
		$graphType.bootstrapSwitch("setOnClass", "success");
		$graphType.bootstrapSwitch("setOffClass", "warning");
		$graphType.on("switch-change", function (e, data) {
			var $t = $("#topnValueTD");
			var $t2 = $("div#legend2");
		    if(data.value){ 
		    	$t.hide();
		    	$t2.parent().show();
		    }
		    else { 
		    	$t.show();
		    	$t2.parent().hide();
		    }
		});
		
		$("#queryTime2 button").click(function(){
			var $btn = $(this);
			$btn.addClass("btn-primary");
			$btn.siblings().removeClass("btn-primary");
			var btnVal = $btn.attr("value").toLowerCase();
			$("#queryTime2 input").val(btnVal);
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
			$("#startTime2").val(sd);
			$("#endTime2").val(ed);
		});
		$("#startTime2").click(function(){
			var $btn = $("#queryTime2 button");
			$btn.siblings().removeClass("btn-primary");
			$btn.removeClass("btn-primary");
		});
		$("#endTime2").click(function(){
			var $btn = $("#queryTime2 button");
			$btn.siblings().removeClass("btn-primary");
			$btn.removeClass("btn-primary");
		});
	});
</script>
<script>
$(document).ready(
	function() {
		$("#storageType2").multiselect({
			maxHeight : 300,
			maxWdith : 200
		});
		$("#device2").multiselect({
			includeSelectAllOption : true,
			maxHeight : 150
		});
		$("#prfField2").multiselect({
			enableFiltering : 1,
			maxHeight : 300,
			maxWdith : 200
		});
		$.each($("#storageType2 option"),function(){
			if($(this).val()=="${historyConfig.fdevicetype}"){
				$(this).attr("selected","selected");
			}
		});
		var timeType = $("#time_type2 option");
		$.each(timeType,function(){
			if(this.value=="${historyConfig.time_type}"){
				$(this).attr("selected","selected");
			}
		});
		var level = "${level}";
		<%--
			level为1表示跳转到示存储的非详细页面
			level为2表示跳转到示非存储的非详细页面
			level为3表示跳转到示存储或非存储的详细页面
		--%>
		if(level == 2){
			$("#storageType2").attr("disabled","disabeld");
		}
		else if(level == 3){
			$("#storageType2").attr("disabled","disabeld");
			$("#device2").attr("disabled","disabled");
		}
		
		var stype = "${type}";
		$.each($("#storageType2 option"),function(){
			if($(this).val() == stype && stype!=''){
				$(this).attr("selected","selected");
			}
		});
		$("#threValue2").keypress(function(){
			var a = this.value;
			if(isNaN(a)){ $(this).val(a.substring(0,a.length-1)); }
		}).keyup(function(){
			var a = this.value;
			if(isNaN(a)){
				$(this).val(a.substring(0,a.length-1));
			}
		});
		defaultStorage();
		$("#storageType2").change(function(){
			$("#device2 option").remove();
			$("#prfField2 option").remove();
			defaultStorage();
		});
});

function defaultStorage(){
	var devices = "${historyConfig.fdevice}";
	var dev = devices.split(",");
	var type = $("#storageType2 option:selected").val();
	$("#storageType2").multiselect('rebuild');
	var jsonList = ${devList};
	var str = "";
	for(var i in jsonList){
		if(type == jsonList[i].type){
			str+="<option ";
			for(var j=0;j<dev.length;j++){
				if(jsonList[i].id==dev[j]){
					str+="selected=selected";
				}
			}
			str+=" value='"+jsonList[i].id+"'>"+jsonList[i].name+"</option>";
		}
	}
	$("#device2").append(str);
	$("#device2").multiselect("rebuild");
	var kp = "${historyConfig.fprfid}";
	var k = kp.split(",");
	var kpis = ${kpisList};
	var str2 = "";
	for(var i in kpis){
		if(type == kpis[i].fstoragetype){
			//重要程度的判断开始
			if(kpis[i].fimp != 0 && kpis[i].fimp != null ) {
				str2 += "<option ";
				for(var j = 0;j < k.length; j++){
					if("'"+kpis[i].fid+"'" == k[j]){
						str2 += "selected=selected";
					}
				}
				str2+=" value='"+kpis[i].fid+"'>"+kpis[i].ftitle+"("+kpis[i].funits+")"+"</option>";
			}
		}
	}
	$("#prfField2").append(str2).multiselect("rebuild");
}


function resetFunc(){
	var level = parseInt("${level}");
	if(level != 3){
		var $sto = $("#storageType2");
		if($sto.attr("disabled") == undefined){
			$sto.find("option").first().attr({selected: true});
		}
		var $dev = $("#device2");
		$dev.find("option:selected").attr({selected: false});
		$dev.multiselect("refresh");
		$("#topnValue").val("");
	}
	$("#prfField2 option").first().attr({selected: true});
	$("#time_type2 option").first().attr({selected: true});
	$("input#startTime2").val("");
	$("input#endTime2").val("");
	$("input#threValue2").val("");
	$("#queryTime2 button").removeClass("btn-primary");
}

function saveInfoHgc2(){
	var storageType = $("#storageType2").val();
	if(storageType == null || storageType == ""){
		alert("请选择存储系统类型");
		return false;
	}
	var device = $("#device2").val();
	if(device==null || device==""){
		alert("请选择设备类型");
		return false;
	}
	var prfField = $("#prfField2").val();
	if(prfField==null || prfField==""){
		alert("请选择性能指标");
		return false;
	}
	var startTime = $("#endTime2").val();
	if(startTime==null || startTime==""){
		alert("请选择开始时间");
		return false;
	}
	var endTime = $("#endTime2").val();
	if(endTime==null || endTime==""){
		alert("请选择结束时间");
		return false;
	}
    if(!dateCompare(startTime,endTime)) {                            
        bAlert("结束日期不能小于开始日期！");
        return false;
	}
    if($("#time_type2").val()==''){
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
		<%--if(isNaN(topnValue) || topnValue <= 0){
			alert("请输入TOPN数量");
			return false;
		}--%>
	}
	else { topnValue = 5; }
	
	var threValue = parseFloat($("#threValue2").val());
	threshold = isNaN(threValue)? 0 : 1;
	var jsonVal = $("#condFormHgc2").serializeArray();
	jsonVal.push({name: "legend", value: $("#legend2 div").first().hasClass("switch-on")? 1:0});
	jsonVal.push({name: "threshold", value: threshold});
	jsonVal.push({name: "graphType", value: graphT});
	
	$.ajax({
		url:"${pageContext.request.contextPath}/servlet/storage/StorageAction?func=StoragePrf2",
		data: jsonVal,
		success:function(result){
			if(result == "true"){
				bAlert("操作成功！", "", [{func:"doAfterSucc2();", text:"确定"}]);
			}else{
				bAlert("操作失败，请稍候再试！");
			}
		}
	});
}
</script>

<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="condFormHgc2">
			<fieldset>
				<div class="control-group">
					<table class="table-condensed">
						<tbody>
							<tr id="devtypeAndDevice2">
								<td width="20%">&nbsp;</td>
								<td id="storageTypeTd">
									<label class="control-label" for="storageType2">设备类型</label>
									<div class="input-prepend input-append">
										<select class="form-control" name="storageType" id="storageType2" style="width:180px;"> 
											<option value="">-请选择-</option>
											<c:forEach items="${vsm_devtype}" var="dev_type">
												<c:if test="${dev_type.key != 'SWITCH' && dev_type.key != 'HOST'}">
													<option value="${dev_type.key}">${dev_type.value}</option>
												</c:if>
											</c:forEach>
										</select>
									</div>
									<input type="hidden" name="subSystemID" value="${subSystemID}" />
									<input type="hidden" name="devId" value="${subSystemID}" />
									<input type="hidden" name="storageType" value="${type}" />
								</td>
								<td id="deviceTd">
									<label class="control-label" for="device2">设备</label>
									<div class="input-prepend input-append">
										<select id="device2" multiple="multiple" name="device"></select>
									</div>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td width="20%">&nbsp;</td>
								<td id="prfFieldTd">
									<label class="control-label" for="	">性能</label>
									<div class="input-prepend input-append">
										<%-- multiple="multiple" --%>
										<select id="prfField2" name="prfField"></select>
									</div>
								</td>
								<td>
									<label class="control-label" for="time_type2">时间粒度</label>
									<div class="input-prepend input-append">
										<select id="time_type2" name="time_type" style="width:180px;">
											<option value="minute">分钟</option>
											<option value="hour">小时</option>
											<option value="day">天</option>
										</select>
									</div>
								</td>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td width="20%">&nbsp;</td>
								<td width="400px;">
									<label class="control-label" for="queryTime1">时间范围</label>
									<div id="queryTime2" class="controls" style="margin-left: 0px;">
					                  	<input name="queryTime" type="hidden" value="fourHours">
					                  	<button id="queryTime1" type="button" class="btn btn-default" value="oneHour">1时</button>
					                  	<button type="button" class="btn btn-default" value="fourHours">4时</button>
					                  	<button type="button" class="btn btn-default" value="oneDay">1天</button>
					                  	<button type="button" class="btn btn-default" value="oneWeek">1周</button>
					                  	<button type="button" class="btn btn-default" value="oneMonth">1月</button>
					                  	<%--
					                  	<button type="button" class="btn btn-default" value="oneYear">1年</button>--%>
					                </div>
								</td>
								<td width="100px;">
									<label class="control-label" for="startTime2">&nbsp;</label>
									<div class="input-prepend input-append">
										<input value="${historyConfig.fstarttime}" name="startTime" id="startTime2" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
									</div>
									--
									<div class="input-prepend input-append">
										<input value="${historyConfig.fendtime}" name="endTime" id="endTime2" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
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
								</td> --%>
							</tr>
							<td width="20%">&nbsp;</td>
									<c:choose>
									<c:when test="${historyConfig.graphtype == 1}"><%--1表示画TOPN图 --%>
										<td style="display:none;">
									</c:when>
									<c:otherwise>
										<td>
									</c:otherwise>
								</c:choose>
									<label class="control-label" for="a5">显示曲线名称</label>
									<div id="legend2" class="switch" tabindex="0">
						                <c:choose>
											<c:when test="${historyConfig.flegend==1}">
												<input id="a5" data-no-uniform="true" type="checkbox" checked="checked" >
											</c:when>
											<c:otherwise>
												<input id="a5" data-no-uniform="true" type="checkbox">
											</c:otherwise>
										</c:choose>
							         </div>
								</td>
								 <td colspan="3">
									<label class="control-label">阀值告警线</label>
									<div class="controls" style="margin-left:0px;">
										<input placeholder="告警值" type="text" name="threValue" id="threValue2" style="width:180px;" value="${historyConfig.fthrevalue}">
									</div>
								 </td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td colspan="5">
									<center>
									<input type="hidden" name="level" value="${level}" />
									<a onclick="saveInfoHgc2();" class="btn btn-primary">保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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

