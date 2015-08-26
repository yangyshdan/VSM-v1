<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<link href="${path}/resource/css/bootstrap-switch.css" rel="stylesheet">
<script src="${path}/resource/js/project/editRule.js"></script>
<style>
	.multiselect-group {
		font-weight: bold;
		text-decoration: underline;
	}
	.multiselect-all label {
		font-weight: bold;
	}
</style>

<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal">
			<fieldset style="margin-left: 80px;">
				<div class="control-group">
					<label class="control-label" for="name">
						名称
					</label>
					<div class="controls" style="margin-right: 80px;">
						<div class="input-prepend input-append">
							<input type="text" class="form-control" id="name">
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="enabled">是否启用</label>
					<div class="controls">
						<div id="enabled" class="switch">
							<input data-no-uniform="true" type="checkbox" checked="checked">
						</div>
					</div>
				</div>
			  
				<div class="control-group">
					<label class="control-label" for="type">设备类型</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select class="form-control" id="type" onchange="ontypechange();">
							<option value="PHYSICAL">物理机</option>
							<option value="VIRTUAL">虚拟机</option>
							<c:forEach items="${vsm_devtype}" var="dev_type">
								<c:if test="${dev_type.key != 'HOST'}">
									<option value="${dev_type.key}">${dev_type.value}</option>
								</c:if>
							</c:forEach>
                     		</select>
			      		</div>
			      	</div>
			  	</div>
			  
				<div class="control-group" id="hy_div">
					<label class="control-label" for="hy_vm">物理机</label>
					<div class="controls">
				  		<div class="input-prepend input-append">
							<select id="hy_vm" multiple="multiple"></select>
						</div>
					</div>
				</div>
			  
				<div class="control-group">
					<label class="control-label" for="device">设备</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="device" multiple="multiple"></select>
						</div>
					</div>
				</div>
			</fieldset>
			<legend style="margin-bottom: 5px;">阀值</legend>
			<div class="control-group" style="margin-top: 3px; margin-bottom: 2px;">
				<table id="divPrfDiv">
					<tr>
						<td>性能指标</td>
						<td>峰值警告值</td>
						<td>峰值错误值</td>
						<td>平均值警告值</td>
						<td>平均值错误值</td>
					</tr>
				</table> 
			</div>
			<div class="form-actions">
				<input style="margin-left: 80px;" type="button" onclick="saveInfo();" class="btn btn-primary" value="保存 "/>
				<button class="btn" type="reset">重置</button>
			</div>
		</form>
	</div>
</div>
<script>
$(document).ready(function(){
	$("#type").multiselect({
		maxHeight: 150,
		maxWidth : 200
	});
	$('#device').multiselect({
		includeSelectAllOption: true,
		maxHeight: 150,
		maxWidth : 200
	});
	$("#enabled").bootstrapSwitch();
	if ("${rule.fid}" > 0) {
		$("#name").val("${rule.fname}");	
		$("#enabled").val("${rule.fenabled}");
		if ("${rule.fenabled}" == 0) {
			$("#enabled div").first().removeClass("switch-on");
			$("#enabled div").first().addClass("switch-off");
		}
		$('option[value="${rule.ftype}"]', $('#type')).attr('selected', 'selected');
	}
	$("#type").multiselect('rebuild');
	ontypechange();
	
	if(!${empty fields}){
		var fields = eval("("+'${fields}'+")");
		for(var i = 0; i < fields.length; ++i ){
			var v =  isEmpty(fields[i].fieldId)?"":fields[i].fieldId;
			EditRule.doAddPrf(fields[i].warnValue,fields[i].errorValue,fields[i].aveminvalue,fields[i].avemaxvalue,v);
		}
	}else{
		EditRule.doAddPrf("","","","","");
	}
});

function ontypechange(){
	var prfJson = ${prfJson};
	$('#device option').remove();
	$("#device optgroup").remove();
	$('option', $('.prfDiv select')).remove();
	var type = $('#type option:selected').val();
	var dtype = "${rule.fdeviceid}";
	var prf = "${ruleData.fprfid}";
	if(type == "Virtual"){
		onloadHv();
		$('#hy_div').show();
	}else{
		$('#hy_div').hide();
	 	for(var i = 0;i<prfJson[type].length;i++){
			var apStr = '<option value="' +prfJson[type][i].value+'"';
			if(dtype.indexOf(prfJson[type][i].value) >= 0){
				apStr +=' selected="selected"';
			}
			apStr += '>' +prfJson[type][i].text+ '</option>';
			$("#device").append(apStr);
		} 
	 	$('#device').multiselect('rebuild');
	}
	for(var i = 0;i<prfJson.targets[type].length;i++){
		var apStr = '<option value="' +prfJson.targets[type][i].value+'"';
		if(prf.indexOf(prfJson.targets[type][i].value) >= 0){
			apStr +=' selected="selected"';
		}
		apStr += '>' +prfJson.targets[type][i].text+ '</option>';
		$(".prfDiv select").append(apStr);
	}
	$(".chzn-done").trigger("liszt:updated");    	   
};
		
function refreshField(id,value){
	var prfJson = ${prfJson};
	var type = $('#type option:selected') .val();
	var prf = "${ruleData.fprfid}";
	$('option', $('#'+id)).remove();
	for(var i = 0;i<prfJson.targets[type].length;i++){
		var apStr = '<option value="' +prfJson.targets[type][i].value+'"';
		if(prfJson.targets[type][i].value == value){
			apStr +='selected="selected"';
		}
		 apStr += '>' +prfJson.targets[type][i].text+ '</option>';
		 $("#"+id).append(apStr);
	}
	$("#"+id).trigger("liszt:updated");
}

function onloadHv(){
	var prfJson = ${prfJson};
	var selectHV = ${selectHV};
	$('option', $('#hy_vm')).remove();
	$('option', $('#device')).remove();
	$('#device').multiselect('rebuild');
	var dtype = "${rule.fdeviceid}";
	for(var i = 0;i<prfJson.Physical.length;i++){
		var apStr = '<option value="' +prfJson.Physical[i].value+'"';
		 apStr += '>' +prfJson.Physical[i].text+ '</option>';
		 $("#hy_vm").append(apStr);
	}
	$('#hy_vm').multiselect({
		onChange: function(element, checked) {
			var vmStr = "<optgroup label='"+element[0].text+"'>";
			if(checked){
				$.ajax({
					async:false,
					type:"POST",
					data:{'hyperVisiorId':element.val()},
					url:"${path}/servlet/alert/AlertRuleAction?func=AjaxVirtual",
					success:function(data){
						if(!isEmpty(data)){
							data = eval("("+data+")");
							for(var i=0;i<data.length;i++ ){
								vmStr += '<option value="' +data[i].value+'"';
								if(dtype.indexOf(data[i].value) >= 0){
									vmStr +='selected="selected"';
								}
								vmStr += '>' +data[i].text+ '</option>';
							}
							vmStr += "</optgroup>";
							$("#device").append(vmStr);
							$('#device').multiselect('rebuild');
						}
					}
				});
			}else{
				$("optgroup[label='"+element[0].text+"']", $('#device')).remove();
				$('#device').multiselect('rebuild');
			}
		}
	});
	
	for(var j = 0;j<selectHV.length;j++){
		if(selectHV[j] != -1)
			$('#hy_vm').multiselect('select', selectHV[j]);
	}
	$('#hy_vm').multiselect('rebuild');
	$('#hy_div').hide();
};
		
//保存阀值设置信息
function saveInfo(){
	var data = {
		id		: "${rule.fid}",
		name	: encodeURI($("#name").val()),
		type	: $("#type").val(),
		level	: $("#level").val(),
		enabled	: $("#enabled div").first().hasClass("switch-on") ? 1 : 0,
		isalone	: 1,
		device	: $("#device").val(),
		targets	: ""
	};
	
	var target = "[";
	if(isEmpty(data.name)){
		alert("名称不能为空！");
		$("#name").focus();
		return false;
	}
	if(isEmpty(data.refresh)){
		data.refresh = 5;
	}else if(!isNumeric(data.refresh)){
		data.refresh = 5;
	}
	if(isEmpty(data.device)){
		alert("请选择设备！");
		$("#device").focus();
		return false;
	}else{
		var d = data.device+"";
		d = d.replace("multiselect-all,","");
		d = d.replace("multiselect-all","");
		if(isEmpty(d)){
			alert("请选择设备！");
			$("#device").focus();
			return false;
		}else{
			data.device = d;
		}
	}
	//阀值验证
	var checkValue = "";
	for(var i = 0,divs = $(".prfDiv");i < divs.length;i++){
		var prfId = $(divs.get(i)).children().children().children("select").val();
		var warn = $(divs.get(i)).children().children("#warn").val();
		var error = $(divs.get(i)).children().children("#error").val();
		var avemin = $(divs.get(i)).children().children("#avemin").val();
		var avemax = $(divs.get(i)).children().children("#avemax").val();
		if(isEmpty(prfId)){
			alert("请选择性能！");
			$(divs[i]).children().children("select").focus();
			return false;
		}
		if(isEmpty(warn)){
			alert("请填写阀值！");
			$(divs[i]).children().children("#warn").focus();
			return false;
		}else if(isEmpty(error)){
			alert("请填写阀值！");
			$(divs[i]).children().children("#error").focus();
			return false;
		}else if(isNaN(warn)){
			alert("阀值必须为数字！");
			$(divs[i]).children().children("#warn").focus();
			return false;
		}else if(isNaN(error)){
			alert("阀值必须为数字！");
			$(divs[i]).children().children("#error").focus();
			return false;
		}else if(warn >= error){
			alert("阀值范围错误！");
			$(divs[i]).children().children("#error").focus();
			return false;
		}
		if(checkValue.indexOf(";"+prfId+";") >= 0){
			alert("存在重复性能指标！");
			$(divs[i]).children().children("select").focus();
			return;
		}else{
			checkValue += ";"+prfId+";";
		}
		target+="{fieldId:'"+prfId+"',warnValue:"+(isEmpty(warn)?-1:warn)+",errorValue:"+(isEmpty(error)?-1:error)+",aveminValue:"+(isEmpty(avemin)?-1:avemin)+",avemaxValue:"+(isEmpty(avemax)?-1:avemax)+"}";
		if(i<divs.length-1){
			target += ",";
		}
	}
	
	target += "]";
	console.log(target);
	data.targets = encodeURI(target);
	
	$.ajax({
		type: "POST",
		url : "${path}/servlet/alert/AlertRuleAction?func=AjaxAdd&time=" + new Date().getTime(),
		data:data,
		success:function(result){
			if(result=="true")  
			{
				parent.window.bAlert("操作成功！","",[{func:"AlertRule.doAfterSucc();",text:"确定"}]);
			}
			if(result=="false")
			{
				parent.window.bAlert("操作失败，请稍候再试！");
			}
		}
	}); 
};
	/**
  判断是否为数字
**/
function isNumeric(strValue)
{
	if (isEmpty(strValue)) return true;
    return executeExp(/^[0-9]+\.{0,1}[0-9]{0,2}$/g, strValue);
};
	
/**
   执行正则表达式
**/
function executeExp(re, s)
{
    return re.test(s);
}
</script>			
			
	<%@include file="/WEB-INF/views/include/footer.jsp"%>