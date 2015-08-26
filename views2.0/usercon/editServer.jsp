<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/public.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path}/resource/js/project/editServer.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<!-- 物理机配置表单 -->
		<div id="physicalDiv">
			<form class="form-horizontal" id="physicalForm">
				<fieldset>
					<legend>物理机配置</legend>
					<div class="control-group">
						<label class="control-label" for="vendor">
							厂商
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<select id="vendor" name="vendor">
									<c:forEach items="${vendors}" var="item">
										<option value="${item.key}">${item.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<div class="control-group" style="display: none;">
						<label class="control-label" for="model">
							型号
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<input type="text" id="model" name="model" value=""/>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="schemaType">
							系统架构类型
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<select id="schemaType" name="schemaType">
									<c:forEach items="${schemaTypes}" var="item">
										<option value="${item.key}">${item.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="osType">
							操作系统类型
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<select id="osType" name="osType" onchange="changeOsType()">
									<c:forEach items="${osTypes}" var="item">
										<option value="${item.key}">${item.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<div id="divVirtPlatType" class="control-group">
						<label class="control-label" for="virtPlatType">
							虚拟平台类型
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<select id="virtPlatType" name="virtPlatType">
									<c:forEach items="${virtPlatTypes}" var="item">
										<option value="${item.key}">${item.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<div id="divVirtPlatType" class="control-group">
						<label class="control-label" for="switch">
							交换机
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<select id="switch" name="switch" multiple="multiple">
								</select>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ipAddress">
							IP地址
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<input type="text" id="ipAddress" name="ipAddress" value=""/>
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
					<div class="control-group">
						<label class="control-label" for="desc">
							描述
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<input type="text" id="desc" name="desc" value=""/>
							</div>
						</div>
					</div>
					<input type="hidden" id="physicalId" name="physicalId" value=""/>
					<div class="form-actions">
						<button id="btnPhyCancel" type="reset" class="btn">重置</button>
						<button id="btnPhyNext" type="button" onclick="savePhysicalInfo()" class="btn btn-primary">下一步</button>
					</div>
				</fieldset>
			</form>
		</div>
		<!-- BMC配置表单 -->
		<div id="bmcDiv" style="display: none;">
			<form class="form-horizontal" id="bmcForm">
				<fieldset>
					<legend style="margin-bottom:0px;">基板管理控制器(BMC)配置(可选)</legend>
					<div class="control-group">
						<label class="control-label" for="bmcIp">
							IP地址
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<input type="text" id="bmcIp" name="bmcIp" value=""/>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="bmcUser">
							用户名
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<input type="text" id="bmcUser" name="bmcUser" value=""/>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="bmcPassword">
							远程登录密码
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<input type="password" id="bmcPassword" name="bmcPassword" value=""/>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="bmcPort">
							监听端口
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<input type="text" id="bmcPort" name="bmcPort" value=""/>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="bmcAuthLevel">
							验证级别
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<select id="bmcAuthLevel" name="bmcAuthLevel">
									<option value="4">Administrator</option>
									<option value="1">Callback</option>
									<option value="2">User</option>
									<option value="3">Operator</option>
									<option value="5">OEM</option>
								</select>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="bmcAuthType">
							验证类型
						</label>
						<div class="controls">
							<div class="input-prepend input-append">
								<select id="bmcAuthType" name="bmcAuthType">
									<option value="0">None</option>
									<option value="1">MD2</option>
									<option value="2">MD5</option>
									<option value="4">Password</option>
									<option value="5">OEM</option>
								</select>
							</div>
						</div>
					</div>
					<div class="form-actions">
						<button id="btnBmcCancel" type="reset" class="btn">重置</button>
						<button id="btnBmcPrev" type="button" onclick="showBmcConfigForm('false')" class="btn">上一步</button>
						<button id="btnBmcNext" type="button" onclick="saveBmcInfo('true')" class="btn btn-primary">下一步</button>
						<button id="btnBmcFinish" type="button" onclick="saveBmcInfo('false')" class="btn btn-primary">完成</button>
					</div>
				</fieldset>
			</form>
		</div>
		<!-- 虚拟机配置表单 -->
		<div id="virtualDiv" style="display: none;">
			<form class="form-horizontal" id="virtualForm">
				<fieldset>
					<legend style="margin-bottom:0px;">虚拟机配置</legend>
					<div class="control-group">
						<div id="viturlConfigDiv" class="control-group" style="margin-top: 3px; margin-bottom: 2px;"></div>
					</div>
					<div class="form-actions">
						<button id="btnVirtCancel" type="reset" class="btn">重置</button>
						<button id="btnVirtPrev" type="button" onclick="showVirtConfigForm('false')" class="btn">上一步</button>
						<button id="btnVirtFinish" type="button" onclick="saveVirtualInfo()" class="btn btn-primary">完成</button>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
</div>
<script>
//IP地址验证表达式
var ipReg = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
//数字验证表达式
var numReg = /^\d+$/;
//物理机ID
var physicalId = "";
//虚拟机配置信息列表
var virtList = null;
var ostype_linux = "Linux";
var ostype_windows = "Windows";
var ostype_esxi = "ESXi";
var hypertype_kvm = "KVM";
var hypertype_vmware = "VMware";
var hypertype_xen = "XenServer";
var hypertype_no = "无";
$(function(){
	var serverInfo = ${serverInfo};
	var switchs = ${switchs}
	var mapSwitchs = ${mapSwitchs};
	$("#switch").multiselect({enableFiltering:1,maxHeight:135});
	if (serverInfo != null && serverInfo != "") {
		$("#physicalId").val(serverInfo.id);
		$("#vendor").val(serverInfo.vendor);
		$("#model").val(serverInfo.model);
		$("#schemaType").val(serverInfo.schema_type);
		$("#osType").val(serverInfo.os_type);
		$("#virtPlatType").val(serverInfo.virt_plat_type);
		$("#ipAddress").val(serverInfo.ip_address);
		$("#user").val(serverInfo.user);
		$("#password").val(serverInfo.password);
		$("#model").val(serverInfo.model);
		$("#desc").val(serverInfo.description);
		//编辑物理机,虚拟机时分情况
		if (serverInfo.toptype == "physical") {
			$("#btnVirtCancel").attr("style","display:none;");
		} else if (serverInfo.toptype == "virtual") {
			virtList = ${virtList};
			showVirtConfigForm("true");
			$("#btnVirtCancel").attr("style","display:inline;");
			$("#btnVirtPrev").attr("style","display:none;");
			EditServer.doAddVirt(serverInfo.vm_id,serverInfo.id,serverInfo.ip_address,serverInfo.user,serverInfo.password);
		}
	}
	changeOsType();
	//For Switch
	var str = "";
	for(var i in switchs){
		str += "<option value='"+switchs[i].hypswid+"'";
		if(mapSwitchs != null && mapSwitchs != "") {
			for(var j in mapSwitchs) {
				if(mapSwitchs[j].switch_id == switchs[i].hypswid) {
					str += "selected='selected'";
				}
			}
		}
		str += ">"+switchs[i].hypswname+"</option>";
	}
	$("#switch").append(str);
	$("#switch").multiselect('rebuild');
})

//操作系统类型改变时,决定虚拟化平台类型
function changeOsType() {
	var osType = $("#osType").val();
	var optStr = "";
	$("#virtPlatType").empty();
	if (osType == ostype_esxi) {
		$("#virtPlatType").append("<option value='" + hypertype_vmware +"'>" + hypertype_vmware + "</option>");
	} else {
		optStr = "<option value='" + hypertype_kvm +"'>" + hypertype_kvm + "</option>";
		optStr = optStr + "<option value='" + hypertype_xen +"'>" + hypertype_xen + "</option>";
		optStr = optStr + "<option value='" + hypertype_no +"'>" + hypertype_no + "</option>";
		$("#virtPlatType").append(optStr);
	}
}

//处理物理机配置表单
function savePhysicalInfo(){
	var vendor = $("#vendor").val();
	if(vendor == null || vendor == "") {
		alert("请选择厂商");
		return false;
	}
	var schemaType = $("#schemaType").val();
	if(schemaType == null || schemaType == "") {
		alert("请选择架构类型");
		return false;
	}
	var osType = $("#osType").val();
	if(osType == null || osType == "") {
		alert("请选择系统类型");
		return false;
	}
	var virtPlatType = $("#virtPlatType").val();
	if(virtPlatType == null || virtPlatType == "") {
		alert("请选择虚拟化平台类型");
		return false;
	}
	var switchIds = $("#switch").val();
	//if(switchIds == null || switchIds == "" || switchIds.length == 0) {
	//	alert("至少选择一台交换机");
	//	return false;
	//}
	
	var ipAddress = $("#ipAddress").val();
	if(ipAddress == null || ipAddress == ""){
		alert("请填写IP地址");
		return false;
	}
	if(ipAddress != null && ipAddress != "") {
		if(!ipReg.test(ipAddress)) {
			alert("不合法的IP地址");
			return false;
		}
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
	
	var jsonVal = $("#physicalForm").serializeArray();
	$.ajax({
		url:"${path}/servlet/usercon/UserConAction?func=TestAndSavePhysiConfig&time=" + new Date().getTime(),
		data:jsonVal,
		type:"POST",
		success:function(data){
			var data = eval("(" + data + ")");
			var result = data["result"];
			if(result=="true"){
				$("#myModal").modal("hide");
				<%-- 开始设置BMC配置表单 --%>
				var bmcInfo = data["bmcInfo"];
				initBmcConfigInfo(bmcInfo);
				showBmcConfigForm("true");
				<%-- 结束设置BMC配置表单 --%>
				<%-- 开始设置虚拟机配置表单 --%>
				$("#viturlConfigDiv").html("");
				physicalId = data["physicalId"];
				virtList = data["virtList"];
				if (virtList != null && virtList.length > 0) {
					var count = 0;
					for (var i = 0; i < virtList.length; i++) {
						if (virtList[i].hmc_id != null && virtList[i].hmc_id != "") {
							EditServer.doAddVirt(virtList[i].vm_id,virtList[i].hmc_id,virtList[i].ip_address,virtList[i].user,virtList[i].password);
							count = count + 1;
						}
					}
					//未有已配置的虚拟机时,默认显示一个
					if (count == 0) {
						EditServer.doAddVirt("","","","","");
					}
				} else {
					//找不到虚拟机,自动增加配置
					EditServer.doAddVirt("","","","","");
				}
				<%-- 结束设置虚拟机配置表单 --%>
			}else if(result=="false"){
				//parent.window.bAlert("该用户信息不可用,请检查填写信息!");
				$("#myModal").modal("hide");
				alert("该配置信息不可用,请检查填写信息!");
			}else if(result=="unknow"){
				//parent.window.bAlert("非法主机!");
				$("#myModal").modal("hide");
				alert("非法主机!");
			}else if(result=="has_user"){
				//parent.window.bAlert("已经存在该用户!");
				$("#myModal").modal("hide");
				alert("已经存在该用户!");
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
		}
	});
}

//初始化或加载BMC配置信息
function initBmcConfigInfo(bmcInfo) {
	if (bmcInfo != null && bmcInfo != "") {
		$("#bmcIp").val(bmcInfo.ip_address);
		$("#bmcUser").val(bmcInfo.user_name);
		$("#bmcPassword").val(bmcInfo.session_pwd);
		$("#bmcPort").val(bmcInfo.port);
		$("#bmcAuthLevel").val(bmcInfo.level);
		$("#bmcAuthType").val(bmcInfo.auth_type);
	}
}

//显示BMC配置表单
function showBmcConfigForm(isShow) {
	if (isShow == "true") {
		$("#physicalDiv").attr("style","display:none;");
		$("#bmcDiv").attr("style","display:block;");
		var virtPlatType = $("#virtPlatType").val();
		if (virtPlatType == "无") {
			$("#btnBmcNext").attr("style","display:none;");
			$("#btnBmcFinish").attr("style","display:inline;");
		} else {
			$("#btnBmcNext").attr("style","display:inline;");
			$("#btnBmcFinish").attr("style","display:none;");
		}
	} else {
		$("#physicalDiv").attr("style","display:block;");
		$("#bmcDiv").attr("style","display:none;");
	}
}

//保存BMC配置信息
function saveBmcInfo(showVirt) {
	var bmcIp = $("#bmcIp").val();
	if(bmcIp != null && bmcIp != "") {
		if(!ipReg.test(bmcIp)) {
			alert("不合法的IP地址");
			return false;
		}
	}
	var bmcPort = $("#bmcPort").val();
	if(bmcPort != null && bmcPort != "") {
		if(!numReg.test(bmcPort)) {
			alert("不合法的端口");
			return false;
		}
	}
	//判断是否填写BMC配置信息,如果有则验证,否则直接跳过
	if (bmcIp != null && bmcIp != "") {
		var jsonVal = $("#bmcForm").serializeArray();
		$.ajax({
			url:"${path}/servlet/usercon/UserConAction?func=TestAndSaveBmcConfig&physicalId=" + physicalId,
			data:jsonVal,
			type:"POST",
			success:function(data){
				if(data=="true"){
					//转到虚拟机配置表单
					if (showVirt == "true") {
						$("#myModal").modal("hide");
						showVirtConfigForm(showVirt);
					} else if (showVirt == "false") {
						parent.window.bAlert("操作成功!","",[{func:"doAfterSucc();",text:"确定"}]);
					}
				}else if(data=="false"){
					//parent.window.bAlert("该配置信息不可用,请检查填写信息!");
					$("#myModal").modal("hide");
					alert("该配置信息不可用,请检查填写信息!");
				}else{
					parent.window.bAlert("系统异常请稍候操作!");
				}
			},
			beforeSend:function(){
				var alertStr = "<div class='modal-header'><h3>操作提示</h3></div>";
				alertStr += "<div class='modal-body' align='center' style='height:400px;line-height:130px;'>";
				alertStr += "<img src='"+parent.window.getRootPath()+"/resource/img/loading.gif' /><span>正在测试配置信息可用性,请稍候...</span>";
				alertStr += "</div><div class='modal-footer'></div>";	
				$("#myModal").html(alertStr);
				$('#myModal').modal('show');
			}
		});
	} else {
		//转到虚拟机配置表单
		if (showVirt == "true") {
			showVirtConfigForm(showVirt);
		} else {
			parent.window.bAlert("操作成功!","",[{func:"doAfterSucc();",text:"确定"}]);
		}
	}
}

//显示虚拟机配置表单
function showVirtConfigForm(isShow) {
	if (isShow == "true") {
		$("#physicalDiv").attr("style","display:none;");
		$("#bmcDiv").attr("style","display:none;");
		$("#virtualDiv").attr("style","display:block;");
		$("#btnVirtCancel").attr("style","display:inline;");
	} else {
		$("#bmcDiv").attr("style","display:block;");
		$("#virtualDiv").attr("style","display:none;");
	}
}

//刷新虚拟机下拉表
function refreshField(id,value){
	$('option', $('#'+id)).remove();
	for(var i = 0; i < virtList.length; i++){
		var apStr = '<option value="' + virtList[i].vm_id+'"';
		if(virtList[i].vm_id == value){
			apStr += 'selected="selected"';
		}
		apStr += '>' + virtList[i].name + '</option>';
		$("#"+id).append(apStr);
	}
	$("#"+id).trigger("liszt:updated");
}

//处理虚拟机配置表单
function saveVirtualInfo() {
	var osType = $("#osType").val();
	var hyperType = $("#virtPlatType").val();
	//没有虚拟机
	if (virtList == null || virtList.length == 0) {
		if ((osType == ostype_linux && hyperType == hypertype_kvm) || (osType == ostype_esxi && hyperType == hypertype_vmware)) {
			alert("没有找到可以配置的虚拟机,关闭窗口或返回配置!");
			return;
		}
	}
	var target = "[";
	var chkWinVal = "";
	var chkLinuxVal = "";
	for(var i = 0,divs = $(".virtDiv");i < divs.length;i++){
		var vmId = $(divs.get(i)).children().children("select").val();
		var vmName = $(divs.get(i)).children().children("select").find("option:selected").text();
		var hmcId = $(divs.get(i)).children().children("#hmcId").val();
		var ip = $(divs.get(i)).children().children("#ipAddress").val();
		var user = $(divs.get(i)).children().children("#user").val();
		var password = $(divs.get(i)).children().children("#password").val();
		if(isEmpty(ip)){
			alert("请填写IP地址!");
			$(divs[i]).children().children("#ipAddress").focus();
			return false;
		}else if(!ipReg.test(ip)){
			alert("不合法的IP地址!");
			$(divs[i]).children().children("#ipAddress").focus();
			return false;
		}else if(isEmpty(user)){
			alert("请填写用户名!");
			$(divs[i]).children().children("#user").focus();
			return false;
		}else if(isEmpty(password)){
			alert("请填写密码!");
			$(divs[i]).children().children("#password").focus();
			return false;
		}
		//For Hypervisor type : KVM,VMware
		if(hyperType == hypertype_kvm || hyperType == hypertype_vmware){
			if(isEmpty(vmId)){
				alert("请选择虚拟机!");
				$(divs[i]).children().children("select").focus();
				return false;
			}else if(chkLinuxVal.indexOf(";"+vmId+";") >= 0){
				alert("存在重复虚拟机配置!");
				$(divs[i]).children().children("select").focus();
				return false;
			}else{
				chkLinuxVal += ";"+vmId+";";
			}
		//For Other
		}else{
			if(chkWinVal.indexOf(";"+ip+";") >= 0){
				alert("存在重复虚拟机配置!");
				$(divs[i]).children().children("select").focus();
				return false;
			}else{
				chkWinVal += ";"+ip+";";
			}
		}
		
		target+="{hmcId:'"+hmcId+"',vmId:'"+vmId+"',vmName:'"+vmName+"',ip:'"+ip+"',user:'"+user+"',password:'"+password+"'}";
		if(i<divs.length-1){
			target += ",";
		}
	}
	target += "]";
	target = encodeURI(target);
	$.ajax({
		url:"${path}/servlet/usercon/UserConAction?func=TestAndSaveVirtConfig&time=" + new Date().getTime(),
		data:{physicalId:physicalId,data:target,ostype:osType,hyperType:hyperType},
		type:"POST",
		success:function(data){
			if (data == "true") {
				parent.window.bAlert("操作成功!","",[{func:"doAfterSucc();",text:"确定"}]);
			} else {
				$('#myModal').modal('hide');
				alert("虚拟机:"+data+"配置有误,请重新配置!");
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

</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>