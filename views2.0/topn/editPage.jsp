<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path }/resource/js/project/topn.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<input type="hidden" name="tid" value="${historyConfig.tid}"/>
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="name">
						名称
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="name" name="name" value="${historyConfig.name}"/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="top_count">
						TOPN数量
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input id="top_count" type="text" name="top_count" value="${historyConfig.top_count}"/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="timescope_type">
						时间范围类型
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="timescope_type" name="timescope_type">
								<option value="0">
									固定时间段
								</option>
								<option value="1">
									最近时间段
								</option>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="time_length">
						时间长度
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="time_length" name="time_length" value="${historyConfig.time_length}"/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="time_type">
						时间类型
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="time_type" name="time_type">
								<option value="minute">
									分钟
								</option>
								<option value="hour">
									小时
								</option>
								<option value="day">
									天
								</option>
								<option value="month">
									月
								</option>
								<option value="year">
									年
								</option>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="startTime">
						开始时间
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input value="${historyConfig.starttime}" name="startTime" id="startTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="endTime">
						结束时间
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input value="${historyConfig.endtime}" name="endTime" id="endTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="timeSize">
						时间粒度
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="timeSize" name="timeSize">
								<option value="minute">
									分钟
								</option>
								<option value="hour">
									小时
								</option>
								<option value="day">
									天
								</option>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="fdevicetype">
						设备类型
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select class="form-control" id="fdevicetype" name="fdevicetype" style="width: 180px;">
								<c:forEach items="${vsm_devtype}" var="dev_type">
									<option value="${dev_type.key }">${dev_type.value }</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="fdevice">
						设备
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="fdevice" multiple="multiple" name="fdevice">
								
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="fname">
						组件类型
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="fname" name="fname">
								
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="fprfid">
						性能指标
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="fprfid" multiple="multiple" name="fprfid">

							</select>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					<button type="reset" class="btn" onclick="resetInfo()">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script>
$(document).ready(
	function() {
		$('#fdevice').multiselect({
			includeSelectAllOption : true,
			maxHeight : 150
		});
		$('#fprfid').multiselect({
			enableFiltering : 1,
			maxHeight : 200,
			maxWdith : 200
		});
		$('#fname').multiselect();
		var timescopeType = $("#timescope_type option");
		$.each(timescopeType,function(){
			if(this.value=="${historyConfig.timescope_type}"){
				$(this).attr("selected","selected");
			}
		});
		var timeType = $("#time_type option");
		$.each(timeType,function(){
			if(this.value=="${historyConfig.time_type}"){
				$(this).attr("selected","selected");
			}
		});
		var timeSize = $("#timeSize option");
		$.each(timeSize,function(){
			if(this.value=="${historyConfig.time_size}"){
				$(this).attr("selected","selected");
			}
		});
		var fdeviceType = $("#fdevicetype option");
		$.each(fdeviceType,function(){
			if(this.value=="${historyConfig.fdevicetype}"){
				$(this).attr("selected","selected");
			}
		});
		timescopeTypeFunc();
		$("#timescope_type").bind("change",timescopeTypeFunc);
		
		fdevicetypeFunc();
		$("#fdevicetype").bind("change",fdevicetypeFunc);
			
		$("#fname").bind("change",fnameFunc);
		
		$("#top_count").keypress(function(){
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
		
		$("#time_length").keypress(function(){
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
		
		
});

function timescopeTypeFunc(){
	var timescopeType = $("#timescope_type option:selected").val();
	if(timescopeType=='0'){
		$("input[name='time_length']").attr("disabled","disabled");
		$("#time_type").attr("disabled","disabled");
		$("input[name='startTime']").removeAttr("disabled");
		$("input[name='endTime']").removeAttr("disabled");
		$("input[name='startTime']").css("background-color","#fff");
		$("input[name='endTime']").css("background-color","#fff");
	}else{
		$("input[name='startTime']").attr("disabled","disabled");
		$("input[name='endTime']").attr("disabled","disabled");
		$("input[name='time_length']").removeAttr("disabled");
		$("input[name='startTime']").css("background-color","");
		$("input[name='endTime']").css("background-color","");
		$("#time_type").removeAttr("disabled");
	}
}

function fdevicetypeFunc(){
	var type=$("#fdevicetype").val();
	if(type==''){
		resetInfo();
		return;
	}
	$("#fdevice option").remove();
	$("#fname option").remove();
	$("#fprfid option").remove();
	var deviceList = ${deviceList};
	var jsonList = "${historyConfig.fdevice}";
	var subdev = jsonList.split(",");
	var devStr = "";
	for(var i =0;i<deviceList[type].length;i++){
		devStr+="<option ";
		for(var j=0;j<subdev.length;j++){
			if(deviceList[type][i].ele_id == subdev[j]){
				devStr+="selected='selected' ";
			}
		}
		devStr+="value='"+deviceList[type][i].ele_id+"'>"+deviceList[type][i].ele_name+"</option>";
	}
	$("#fdevice").append(devStr);
	$("#fdevice").multiselect('rebuild');
	
	var fnameList = ${fnameList};
	var fname = "${historyConfig.fname}";
	var fnameStr = "";
	for(var i=0;i<fnameList[type].length;i++){
		fnameStr+="<option ";
		if(fnameList[type][i].ele_id==fname){
			fnameStr+="selected='selected' ";
		}
		fnameStr+="value='"+fnameList[type][i].ele_id+"' id='"+fnameList[type][i].fprfview+"'>"+fnameList[type][i].ele_name+"</option>";
	}
	$("#fname").append(fnameStr);
	$("#fname").multiselect('rebuild');
	
	fnameFunc();
}

function fnameFunc(){
	$("#fprfid option").remove();
	var fnameVal = $("#fname").val();
	var type=$("#fdevicetype").val();
	if(fnameVal==''){
		return;
	}
	var fprfidList = ${fprfidList};
	var fprfid = "${historyConfig.fprfid}";
	var subid = fprfid.split(",");
	var fprfStr="";
	for(var i =0;i<fprfidList[type].length;i++){
		if(fprfidList[type][i].fdevtype == fnameVal){
			fprfStr+="<option ";
			for(var j=0;j<subid.length;j++){
				if(fprfidList[type][i].ele_id == subid[j]){
					fprfStr+="selected='selected' ";
				}
			}
			fprfStr+="value='"+fprfidList[type][i].ele_id+"'>"+fprfidList[type][i].ele_name+"</option>";
		}
	}
	$("#fprfid").append(fprfStr);
	$("#fprfid").multiselect('rebuild');
}

function resetInfo(){
	$("#fdevice option").remove();
	$("#fdevice").multiselect('rebuild');
	$("#fname option").remove();
	$("#fname").multiselect('rebuild');
	$("#fprfid option").remove();
	$("#fprfid").multiselect('rebuild');
	$("#fdevicetype option:selected").removeAttr("selected");
	$("#name").attr("value","");
	$("#top_count").attr("value","");
	$("#startTime").attr("value","");
	$("#endTime").attr("value","");
	$("#time_length").attr("value","");
}

function saveInfo(){
	var name = $("#name").val();
	if(name==null || name==""){
		alert("名称不能为空");
		return false;
	}
	var top_count = $("#top_count").val();
	if(top_count==null || top_count==""){
		alert("请输入TOP数值");
		return false;
	}
	if(isNaN(top_count)){
		alert("请输入正确TOP数");
		$("#top_count").val("");
		return false;
	}
	var timeScopeType = $("#timescope_type").val();
	if(timeScopeType=='0'){
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
	    	alert("结束日期不能小于开始日期！");
	        return false;
		}
	}else{
		if($("#time_length").val()==''){
			alert("请输入时间长度");
			return false;
		}
		if(isNaN($("#time_length").val())){
			$("#time_length").val("");
			alert("请输入正确时间长度");
			return false;
		}
		if($("#time_type").val()==''){
			alert("请输入时间类型");
			return false;
		}
	}
	if($("#fdevicetype").val()==''){
		alert("请选择设备类型");
		return false;
	}
	if($("#fdevice option:selected").length==0){
		alert("请选择设备");
		return false;
	}
	if($("#fname").val()==''){
		alert("请选择组件类型");
		return false;
	}
	if($("#fprfid option:selected").length==0){
		alert("请选择性能指标");
		return false;
	}
	var viewname = $("#fname option:selected")[0].id;
	var jsonVal = $("#conditionForm").serializeArray();
	$.ajax({
		url:"${path}/servlet/topn/TopnAction?func=TopnPrf&viewname="+viewname,
		data:jsonVal,
		success:function(obj){
			var jsonVal = $.parseJSON(obj);
			if(jsonVal.state=="true"){
				parent.window.bAlert("操作成功！","",[{func:"Topn.doListRefresh("+jsonVal.tid+");",text:"确定"}]);
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