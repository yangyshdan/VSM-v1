<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="ipaddress">
						IP地址
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="ipaddress" name="ipaddress" value=""/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="user">
						用户名
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id = "user" name="user" value=""/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="password">
						密码
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="password" id="password" name="password" value=""/>
						</div>
					</div>
				</div>
				<input type="hidden" id="hmcId" name="hmcId" value=""/>
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
	var hmcInfo = ${hmcInfo};
	if(hmcInfo!=null && hmcInfo!=""){
		$("#ipaddress").attr("value",hmcInfo.ip_address);
		$("#user").attr("value",hmcInfo.user);
		$("#password").attr("value",hmcInfo.password);
		$("#hmcId").attr("value",hmcInfo.id);
	}
})
function saveInfo(){
	var ipaddress = $("#ipaddress").val();
	if(ipaddress==null || ipaddress ==""){
		alert("请输入IP地址");
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
		url:"${path}/servlet/usercon/UserConAction?func=TestAcct",
		data:jsonVal,
		success:function(result){
			if(result=="true"){
				parent.window.bAlert("操作成功！","",[{func:"doAfterSucc();",text:"确定"}]);
			}else if(result=="false"){
				parent.window.bAlert("该用户信息不可用,是否保存?","",[{func:"saveHMC("+JSON.stringify(jsonVal)+")",text:"是"},{func:"doCancle()",text:"否"}]);
			}else if(result=="unknow"){
				parent.window.bAlert("非法主机!");
			}else if(result=="has_user"){
				parent.window.bAlert("已经存在该用户!");
			}else{
				parent.window.bAlert("系统异常请稍候操作!");
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