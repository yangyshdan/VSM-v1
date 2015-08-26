<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="device">
						设备名
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="device" name="device"  multiple="multiple">
								
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="user">
						用户名
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="user" name="user" value=""/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="daterange">
						密码
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="password" id="password" name="password" value=""/>
						</div>
					</div>
				</div>
				<input type="hidden" id="Id" name="Id" value="${Id}"/>
				<input type="hidden" id="typeId" name="typeId" value="${typeId}"/>
				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					<button type="reset" class="btn">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script>
$(function(){
	$("#device").multiselect({
		includeSelectAllOption : true,
		
		maxHeight : 135
	});
	
	var row = ${deviceInfo};
	//var row1 = ${deviceInfo1};
	var str = "";
	if(row!=null && row!=""){
		$("#device option:[value='"+row.ele_id+"']").attr("selected","selected");
		str+="<option value='"+row.ele_id+"' selected='selected'>"+row.device_name+"</option>";
		$("#device").append(str);
		$("#device").multiselect('rebuild');
		$("#user").attr("value",row.users);
		$("#password").attr("value",row.pwd);
		$("#Id").attr("value",row.id);
		$("#typeId").attr("value",row.type_id);	
	}else{
		var jsonList = ${deviceList};
		for(var i in jsonList){
			str+="<option value='"+jsonList[i].id+"'>"+jsonList[i].name+"</option>";
		}
		$("#device").append(str);
		$("#device").multiselect('rebuild');
	}
})
function saveInfo(){
	
	var device = $("#device option:selected");
	if(device==null || device.length ==0){
		alert("请选择设备");
		return false;
	}
	var user = $("#user").val();
	if(user==null || user==""){
		alert("用户名不能为空");
		return false;
	}
	var password = $("#password").val();
	if(password==null || password==""){
		alert("密码不能为空");
		return false;
	}
	var jsonVal = $("#conditionForm").serializeArray();
	$.ajax({
		url:"${path}/servlet/usercon/UserConAction?func=TestAcctDevice",
		data:jsonVal,
		dataType:'json',
		success:function(result){
			if(result.state=="true"){
				parent.window.bAlert("操作成功！","",[{func:"doAfterSucc();",text:"确定"}]);
			}else if(result.state=="false" && result.fcount==1){
				parent.window.bAlert("该用户不可用!");
			}else if(result.state=="false" && result.fcount>1){
				parent.window.bAlert("部分用户信息不可用!");
			}
		},
		beforeSend:function(){
			var alertStr = "<div class='modal-header'><h3>操作提示</h3></div>";
			alertStr += "<div class='modal-body' align='center' style='height:400px;line-height:130px;'>";
			alertStr += "<img src='"+parent.window.getRootPath()+"/resource/img/loading.gif' /><span>正在测试账户可用性,请稍候...</span>";
			alertStr += "</div><div class='modal-footer'></div>";	
			$("#myModal").html(alertStr);
			$('#myModal').modal('show');
			//parent.window.bAlert("正在测试账户可用性,请稍候...");
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