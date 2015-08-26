<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="devices">
						存储系统
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="devices" name="devices">
								<c:choose>
									<c:when test="${not empty device}">
									<c:forEach items="${device}" var="item">
										<option value="${item.id}">${item.name}</option>
									</c:forEach>
									</c:when>
								</c:choose>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="ele">
						<c:if test="${typeId==3}">磁盘阵列</c:if>
						<c:if test="${typeId==4}">磁带库</c:if>
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="ele" name="ele"  multiple="multiple">
								
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
	$("#devices").multiselect({
		includeSelectAllOption : true,
		buttonText : function(options, select) {
			if (options.length == 0) {
				return 'None selected <b class="caret"></b>';
			} else if(options.length > 2){
				return options.length+ ' selected <b class="caret"></b>';
			}else{
				 var selected = '';
				 options.each(function() {
				 selected += $(this).text() + ', ';
				 });
				 if(selected.length>=30){
					 selected = selected.substr(0,30)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxHeight : 135
	});
	
	$('#ele').multiselect({
		includeSelectAllOption : true,
		maxHeight:200
	});
	$("#devices").bind('change',loadeleList);
	loadeleList();
});

function loadeleList(){
	$("#ele").html('');
	$("#ele").multiselect('rebuild');
	var row = ${deviceInfo};
	var row1 = ${deviceInfo1};
	var deviceId = $("#devices option:selected").eq(0).val();
	var typeId=${typeId};
	$.ajax({
		url:"${path}/servlet/usercon/UserConAction?func=LoadEle",
		data:{deviceId:deviceId,typeId:typeId},
		async:false,
		dataType:'json',
		success:function(data){
			var str = "";
			for(var i =0;i<data.length;i++){
				str+="<option value='"+data[i].ele_id+"'>"+data[i].ele_name+"</option>";
			}
			if(row!=null && row!=""){
				$("#devices option:[value='"+row.dev_id+"']").attr("selected","selected");
				str+="<option value='"+row.ele_id+"' selected='selected'>"+row1.the_display_name+"</option>";
				$("#user").attr("value",row.users);
				$("#password").attr("value",row.password);
				$("#viosId").attr("value",row.id);
			}
			$("#ele").append(str);
			$("#ele").multiselect('rebuild');
		}
	});
}

function saveInfo(){
	var devices = $("#devices option:selected");
	if(devices==null || devices.length ==0){
		alert("请选择设备");
		return false;
	}
	var ele = $("#ele option:selected");
	if(ele==null || ele.length ==0){
		alert("请选择虚拟机");
		return false;
	}
	if(ele.length==1 && ele[0].value =="multiselect-all"){
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
		url:"${path}/servlet/usercon/UserConAction?func=TestAcctDevice1",
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