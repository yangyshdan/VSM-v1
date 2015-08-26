<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="storageName">
						名称/型号
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="storageName" name="storageName" value=""/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="storageType">
						存储类型
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select class="form-control" name="storageType" id="storageType" style="width: 180px;margin-left: 10px;" onchange="changeStorageType()">
								<option value="" selected="selected">请选择</option>
			 					<c:forEach items="${vsm_devtype}" var="dev_type">
									<c:if test="${dev_type.key == 'EMC' || dev_type.key == 'HDS' || dev_type.key == 'NETAPP'}">
										<option value="${dev_type.key}">${dev_type.value}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="ctl01Ip">
						IP地址(1)
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="ctl01Ip" name="ctl01Ip" value=""/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="ctl02Ip">
						IP地址(2)
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="ctl02Ip" name="ctl02Ip" value=""/>
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
					<label class="control-label" for="password">
						密码
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="password" id="password" name="password" value=""/>
						</div>
					</div>
				</div>
				<div id="divNativeCliPath" class="control-group">
					<label class="control-label" for="nativeCliPath">
						代理目录
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" id="nativeCliPath" name="nativeCliPath" value=""/>
						</div>
					</div>
				</div>
				<input type="hidden" id="id" name="id" value=""/>
				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<button type="reset" class="btn">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script>
//IP地址验证表达式
var reg = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
//加载数据
$(function(){
	$("#storageType").multiselect({
		includeSelectAllOption : true,
		maxHeight : 135
	});
	
	var row = ${storageCfgInfo};
	var str = "";
	if (row!=null && row!="") {
		$("#storageName").val(row.name);
		$("#storageType").val(row.storage_type);
		$("#ctl01Ip").val(row.ctl01_ip);
		$("#ctl02Ip").val(row.ctl02_ip);
		$("#user").val(row.user);
		$("#password").val(row.password);
		$("#nativeCliPath").val(row.native_cli_path);
		$("#id").val(row.id);
	}
	$("#storageType").multiselect('rebuild');
	changeStorageType();
})

//存储设备类型改变事件
function changeStorageType() {
	var storageType = $("#storageType").val();
	if (storageType == "HDS" || storageType == "NETAPP") {
		$("#divNativeCliPath").css("display","none");
	} else if (storageType == "EMC") {
		$("#divNativeCliPath").css("display","block");
	}
}

//保存配置信息
function saveInfo() {
	var storageName = $("#storageName").val();
	if (storageName == null || storageName == "") {
		alert("请填写设备名称");
		return false;
	}
	var storageType = $("#storageType").val();
	if (storageType == null || storageType == "") {
		alert("请选择存储类型");
		return false;
	}
	var ctl01Ip = $("#ctl01Ip").val();
	var ctl02Ip = $("#ctl02Ip").val();
	if (ctl01Ip == null || ctl01Ip == "") {
		alert("请填写IP地址(1)");
		return false;
	}
	if(ctl01Ip != null && ctl01Ip != "") {
		if(!reg.test(ctl01Ip)) {
			alert("不合法的IP地址(1)");
			return false;
		}
	}
	if(ctl02Ip != null && ctl02Ip != "") {
		if(!reg.test(ctl02Ip)) {
			alert("不合法的IP地址(2)");
			return false;
		}
	}
	var user = $("#user").val();
	if (user == null || user == "") {
		alert("请填写用户名");
		return false;
	}
	var password = $("#password").val();
	if (password == null || password == "") {
		alert("请填写密码");
		return false;
	}
	var jsonVal = $("#conditionForm").serializeArray();
	$.ajax({
		url:"${path}/servlet/usercon/UserConAction?func=TestAndSaveStorageCfgInfo",
		data:jsonVal,
		dataType:'json',
		success:function(result){
			if (result == true) {
				parent.window.bAlert("操作成功!","",[{func:"doAfterSucc();",text:"确定"}]);
			} else if (result == false) {
				$("#myModal").modal("hide");
				alert("该用户信息不可用,请检查填写信息!");
			} else {
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