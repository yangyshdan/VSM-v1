<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="physical">
						物理机
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="physical" name="physical">
								<c:choose>
									<c:when test="${not empty hypervisor}">
									<c:forEach items="${hypervisor}" var="item">		
										<option value="${item.ele_id}">${item.ele_name}</option>
									</c:forEach>
									</c:when>
								</c:choose>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group" id="virtualDIV">
					<label class="control-label" for="virtual">
						虚拟机
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="virtual" name="virtual"  multiple="multiple">
								
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="prfField">
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
				<input type="hidden" id="viosId" name="viosId" value=""/>
				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					<button type="reset" onclick="resetBtn()" class="btn">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script>
$(function(){
	$('#virtual').multiselect({
		includeSelectAllOption : true,
		maxHeight:200
	});
	var row = ${viosInfo};
	if(row!=null && row.length>0){
		$("#physical option[value='"+row.hypervisor_id+"']").attr("selected","selected");
		$("#physical").multiselect('rebuild');
	}
	$("#physical").bind('change',loadVirtualList);
	loadVirtualList();
});

function resetBtn(){
	$("#user").html("");
	$("#password").html("");
	$("#virtual").multiselect('rebuild');
}

function loadVirtualList(){
	$("#virtual").html('');
	var row = ${viosInfo};
	var hypervisorId = $("#physical option:selected").eq(0).val();
	$.ajax({
		url:"${path}/servlet/usercon/UserConAction?func=LoadVirtual",
		data:{hypervisorId:hypervisorId},
		async:false,
		dataType:'json',
		success:function(data){
			var str = "";
			for(var i =0;i<data.length;i++){
				str+="<option value='"+data[i].ele_id+"'>"+data[i].ele_name+"</option>";
			}
			if(row!=null && row!="" && hypervisorId==row.hypervisor_id){
				str+="<option value='"+row.vm_id+"' selected='selected'>"+row.v_name+"</option>";
				$("#user").attr("value",row.user);
				$("#password").attr("value",row.password);
				$("#viosId").attr("value",row.id);
			}
			$("#virtual").append(str);
			$("#virtual").multiselect('rebuild');
		}
	});
}

function saveInfo(){
	var physical = $("#physical option:selected");
	if(physical==null || physical.length ==0){
		alert("请选择物理机");
		return false;
	}
	var virtual = $("#virtual option:selected");
	if(virtual==null || virtual.length ==0){
		alert("请选择虚拟机");
		return false;
	}
	if(virtual.length==1 && virtual[0].value =="multiselect-all"){
		alert("请选择虚拟机");
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
		url:"${path}/servlet/usercon/UserConAction?func=TestAcctVios",
		data:jsonVal,
		success:function(result){
			if(result=="true"){
				parent.window.bAlert("操作成功!","",[{func:"doAfterSucc();",text:"确定"}]);
			}else if(result=="false"){
				parent.window.bAlert("用户名不可用!","",[{func:"doAfterSucc();",text:"确定"}]);
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