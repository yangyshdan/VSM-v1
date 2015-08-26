<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="storageType">设备类型</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select class="form-control" id="storageType" style="width:180px;">
								<option value="">-请选择-</option>
								<c:forEach items="${vsm_devtype}" var="dev_type">
									<option value="${dev_type.key}">${dev_type.value}</option>
								</c:forEach>
							</select>
							<input type="hidden" id="hideStorageType" name="storageType" value="" />
							<input type="hidden" id="subsystemId" name="subSystemID" value="${subSystemID}" />
							<input type="hidden" id="switchId" name="switchId" value="${switchId}" />
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="device">设备</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="device" multiple="multiple" name="device">
								
							</select>
						</div>
						<input type="hidden" name="devId" value="${historyConfig.fdevice}"/>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="prfField">性能</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="prfField" multiple="multiple" name="prfField"></select>
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="daterange">开始时间</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input value="${historyConfig.fstarttime}" name="startTime" id="startTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
						</div>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="daterange">结束时间</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input value="${historyConfig.fendtime}" name="endTime" id="endTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="time_type">时间粒度</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="time_type" name="time_type" style="width:180px;">
								<option value="minute">分钟</option>
								<option value="hour">小时</option>
								<option value="day">天</option>
							</select>
						</div>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label">
						启用阀值告警线
					</label>
					<div class="controls">
						<label class="radio">
							<input type="radio" name="threshold" style="margin:0px;" id="threshold1" value="1"> 是
						</label>
					 	<label class="input-prepend input-append">
							&nbsp;&nbsp;&nbsp;告警值&nbsp;&nbsp;<input type="text" name="threValue" id="threValue" style="width:80px;" value="${historyConfig.fthrevalue}">
						</label>
						<div style="clear: both"></div>
						<label class="radio">
							<input type="radio" name="threshold" style="margin:0px;" id="threshold2" value="0"> 否
						</label>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="legend">
						是否显示曲线名称
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<c:choose>
								<c:when test="${historyConfig.flegend==1}">
									<input name="legend" checked="checked" id="legend" value="1" data-no-uniform="true" type="checkbox" class="iphone-toggle">
								</c:when>
								<c:otherwise>
									<input name="legend" id="legend" value="1" data-no-uniform="true" type="checkbox" class="iphone-toggle">
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				
				<input type="hidden" name="level" value="${level}" />
				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					<button type="reset" class="btn" onclick="resetFunc()">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script>
$(document).ready(
	function() {
		$('#device').multiselect({
			includeSelectAllOption : true,
			maxHeight : 150
		});
		$('#prfField').multiselect({
			enableFiltering : 1,
			maxHeight : 200,
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
		if(level==2){
			$("#storageType").attr("disabled","disabeld");
		}
		if(level==3){
			$("#storageType").attr("disabled","disabeld");
			$("#device").attr("disabled","disabled");
		}
		var threshold = "${historyConfig.fthreshold}";
		if(threshold==1){
			$("#threshold1").attr("checked","checked");
		}else{
			$("#threshold2").attr("checked","checked");
			$("#threValue").attr("disabled","disabled");
		}
		$("#threshold1").click(function(){
			$("#threshold2").removeAttr("checked","checked");
			$("#threshold1").attr("checked","checked");
			$("#threValue").removeAttr("disabled","disabled");
		});
		$("#threshold2").click(function(){
			$("#threshold2").attr("checked","checked");
			$("#threshold1").removeAttr("checked","checked");
			$("#threValue").attr("disabled","disabled");
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
	$("#device").append(str);
	$("#device").multiselect('rebuild');
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
		str2+=" value='"+kpis[i].fid+"'>"+kpis[i].ftitle+"</option>";
	}
	$("#prfField").append(str2);
	$("#prfField").multiselect('rebuild');
}

function resetFunc(){
	$("#device option:selected").removeAttr("selected");
	$("#device").multiselect('rebuild');
	$("#prfField option").removeAttr("selected");
	$("#prfField").multiselect('rebuild');
	$("input[type='text']").removeAttr("value");
}

function saveInfo(){
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
	if($("#time_type").val()==''){
		alert("请选择时间类型");
		return false;
	}
    if(!dateCompare(startTime,endTime)) {                            
    	alert("结束日期不能小于开始日期！");
        return false;
	}
	/**var yname = $("#yname").val();
	if(yname==null || yname==""){
		alert("请输入Y轴名称");
		return false;
	}
	**/
	var threshold=$("input[name='threshold'][checked='checked']").val();
	if(threshold==1 && $("#threValue").val()==""){
		alert("请输入阀值告警值");
		return false;
	}
	var jsonVal = $("#conditionForm").serializeArray();
	$.ajax({
		url:"${path}/${url}",
		data:jsonVal, 
		success:function(result){
			if(result=="true"){
				parent.window.bAlert("操作成功！","",[{func:"doAfterSucc();",text:"确定"}]);
			}else{
				parent.window.bAlert("操作失败，请稍候再试！");
			}
		}
	});
}

/**
 判断是否为数字
 **/
function isNumeric(strValue) {
	if (isEmpty(strValue))
		return true;
	return executeExp(/^\d*$/g, strValue);
}

/**
 执行正则表达式
 **/
function executeExp(re, s) {
	return re.test(s);
}
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>