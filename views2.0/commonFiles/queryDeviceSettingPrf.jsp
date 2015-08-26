<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${pageContext.request.contextPath}/resource/js/My97DatePicker/WdatePicker.js"></script>
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
		$("#queryTime button").click(function(){
			var $btn = $(this);
			$btn.addClass("btn-primary");
			$btn.siblings().removeClass("btn-primary");
			var btnVal = $btn.attr("value").toLowerCase();
			$("#queryTime input").val(btnVal);
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
		var storageType = $("#storageType option");
		$.each(storageType,function(){
			if(this.value=="${storageInfo.type}"){
				$(this).attr("selected","selected");
				$("#hideStorageType").val(this.value);
			}
		});
		var timeType = $("#time_type option");
		$.each(timeType,function(){
			if(this.value=="${historyConfig.time_type}"){
				$(this).attr("selected","selected");
			}
		});
		var level = "${level}";
		if(level == 2){
			$("#storageType").attr("disabled","disabeld");
		}
		else if(level == 3){
			$("#storageType").attr("disabled","disabeld");
			$("#device").attr("disabled","disabled");
		}
		var threshold = "${historyConfig.fthreshold}";
		
		$("#threValue").val("${historyConfig.fthrevalue}").keypress(function(){
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
});

function defaultStorage(){
	var devices = "${historyConfig.fdevice}";
	var dev = devices.split(",");
	var jsonList = ${devList};
	var str="";
	for(var i in jsonList){
		str+="<option ";
		for(var j=0;j<dev.length;j++){
			if(jsonList[i].ele_id==dev[j]){
				str+="selected=selected";
			}
		}
		str+=" value='"+jsonList[i].ele_id+"'>"+jsonList[i].ele_name+"</option>";
	}
	$("#device").append(str).multiselect('rebuild');
	var kp = "${historyConfig.fprfid}";
	var k = kp.split(",");
	var kpis = ${kpisList};
	var str2 = "";
	for(var i in kpis){
		str2+="<option ";
		for(var j=0;j<k.length;j++){
			if("'"+kpis[i].fid+"'"==k[j]){
				str2+="selected=selected";
			}
		}
		str2+=" value='"+kpis[i].fid+"'>"+kpis[i].ftitle+"("+kpis[i].funits+")"+"</option>";
	}
	$("#prfField").append(str2).multiselect('rebuild');
}

function resetFunc(){
	var $qpage = $("#queryPage");
	if($qpage.find("#devtypeAndDevice2").css("display") != "none"){
		var $sto = $($qpage.find("#storageType2"));
		if($sto.attr("disabled") == undefined){
			$sto.find("option").removeAttr("selected");
		}
		var $dev = $($qpage.find("#device2"));
		$dev.find("option:selected").removeAttr("selected");
		$dev.multiselect("refresh");
	}
	
	if($qpage.find("#devtypeAndDevice").css("display") != "none"){
		var $sto = $($qpage.find("#storageType"));
		if($sto.attr("disabled") == undefined){
			$sto.find("option").removeAttr("selected");
		}
		var $dev = $($qpage.find("#device"));
		$dev.find("option:selected").removeAttr("selected");
		$dev.multiselect("refresh");
	}
	
	$qpage.find("#prfField2 option:selected").removeAttr("selected");
	$qpage.find("#time_type2 option:selected").removeAttr("selected");
	$qpage.find("#queryTime2 option").removeAttr("selected");
	$qpage.find("#startTime2").val("");
	$qpage.find("#endTime2").val("");
	$qpage.find("#threValue2").val("");
	$qpage.find("#queryTime2 button").removeClass("btn-primary");
	
	var $prfField = $("#prfField");
	$prfField.find("option:selected").removeAttr("selected");
	$prfField.multiselect("refresh");
	$qpage.find("#time_type option:selected").removeAttr("selected");
	$qpage.find("#queryTime option").removeAttr("selected");
	$qpage.find("#startTime").val("");
	$qpage.find("#endTime").val("");
	$qpage.find("#threValue").val("");
	$qpage.find("#queryTime button").removeClass("btn-primary");
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
    if($("#time_type").val()==''){
		alert("请输入时间类型");
		return false;
	}
	var threValue = parseFloat($("#threValue").val());
	threshold = isNaN(threValue)? 0 : 1;
	var jsonVal = $("#condFormHgc").serializeArray();
	jsonVal.push({name: "legend", value: $("#legend div").first().hasClass("switch-on")? 1:0});
	jsonVal.push({name: "threshold", value: threshold});
	jsonVal.push({name: "level", value: "${level}"});

	$.ajax({
		url:"${pageContext.request.contextPath}/${url}",
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
											<option value="">-请选择-</option>
											<c:forEach items="${vsm_devtype}" var="dev_type">
												<option value="${dev_type.key}">${dev_type.value}</option>
											</c:forEach>
										</select>
									</div>
									<input type="hidden" id="hideStorageType" name="storageType" value="" />
									<input type="hidden" id="subsystemId" name="subSystemID" value="${subSystemID}" />
									<input type="hidden" id="switchId" name="switchId" value="${switchId}" />
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
									<!-- multiple="multiple" -->
									<select id="prfField"  name="prfField"></select>
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
								<td colspan="2">&nbsp;</td>
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
									<label class="control-label" for="a3">显示曲线名称</label>
									<div id="legend" class="switch" tabindex="0">
						                <input id="a3" type="checkbox" />
						                <c:choose>
											<c:when test="${historyConfig.flegend==1}">
												<input checked="checked" id="a3" data-no-uniform="true" type="checkbox">
											</c:when>
											<c:otherwise>
												<input id="a3" data-no-uniform="true" type="checkbox">
											</c:otherwise>
										</c:choose>
							         </div>
							     </td>
								 <td>
									<label class="control-label">阀值告警线</label>
									<div class="controls" style="margin-left:0px;">
										<input placeholder="告警值" type="text" name="threValue" id="threValue" style="width:180px;" value="${historyConfig.fthrevalue}">
									</div>
								 </td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td colspan="5" align="center" width="100%">
									<a onclick="saveInfoHgc();" class="btn btn-primary">保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<a class="btn" type="reset" onclick="resetFunc()">重置</a>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</fieldset>
		</form>
	</div>
</div>

