<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path}/resource/js/project/editRule.js"></script>
<script src="${path}/resource/js/project/forward.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal">
			<fieldset>
				<input type="hidden" id="forwareId" name="forwareId" value="${forward.forwareId}" />
				<legend style="margin-bottom: 5px;">
					转发级别
				</legend>
				<div class="control-group">
					<label class="control-label" for="forward_level">
						最低转发级别
					</label>
					<div class="controls" style="margin-right: 80px;">
						<div class="input-prepend input-append">
							<select class="form-control" id="forward_level">
								<option value="0">
									Info
								</option>
								<option value="1">
									Warning
								</option>
								<option value="2">
									Critical
								</option>
							</select>
						</div>
					</div>
				</div>
				<legend style="margin-bottom: 5px;">
					SNMP
				</legend>
				<div id="divSnmpDiv" class="control-group" style="margin-top: 3px; margin-bottom: 2px;"></div>

				<legend style="margin-bottom: 5px;">
					Email
				</legend>

				<div class="control-group">
					<label class="control-label" for="email_smtp">
						邮件发送服务器
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" class="form-control" id="email_smtp">
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="email_port">
						端口
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" class="form-control" id="email_port">
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="email_user">
						用户名
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" class="form-control" id="email_user">
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="email_pwd">
						密码
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="password" class="form-control" id="email_pwd">
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="email_to">
						收件人
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<textarea id="email_to"></textarea>
							<label>
								注：多个收件人用分号隔开
							</label>
						</div>
					</div>
				</div>
				<legend style="margin-bottom: 5px;">
					短信
				</legend>
				<%--
				<div class="control-group">
					<label class="control-label" for="sms_to">收件人</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<textarea id="sms_to"></textarea>
							<label>注：多个收件人用分号隔开</label>
						</div>
					</div>
				</div>
				 --%>
				<div id="divSmsDiv" class="control-group" style="margin-top: 3px; margin-bottom: 2px;">
				</div>
			</fieldset>
			<div class="form-actions">
				<input type="button" onclick="saveInfo();" class="btn btn-primary" value="保存 " />
				<button class="btn" type="reset">
					重置
				</button>
			</div>
		</form>
	</div>
</div><!--/row-->
<script>
//IP地址验证表达式
var ipReg = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
var phoneReg = /^1[3-9][0-9]{9}$/;
$(document).ready(function(){
	$("#forward_level").val("${forward.forward_level}");
	$("#email_smtp").val("${forward.email_smtp}");
	$("#email_port").val("${forward.email_port}");
	$("#email_user").val("${forward.email_user}");
	$("#email_pwd").val("${forward.email_pwd}");
	$("#email_to").val("${forward.email_to}");
	//$("#sms_to").val("${forward.sms_to}");
	if(!${empty snmps}){
		var snmps = eval("("+'${snmps}'+")");
		for(var i = 0; i < snmps.length; i++){
			EditForward.doAddSnmp(snmps[i].snmp_public,snmps[i].snmp_host,snmps[i].snmp_port);
		}
	}else{
		EditForward.doAddSnmp("public","","162");
	}
	if(!${empty smss}){
		var smss = eval("("+'${smss}'+")");
		for(var i = 0; i < smss.length; i++){
			EditForward.doAddSms(smss[i].smsuser,smss[i].smsphone);
		}
	}else{
		EditForward.doAddSms("","");
	}
});

//保存信息
function saveInfo(){
	var data = {
		forwareId		: $("#forwareId").val(),
		forward_level	: $("#forward_level").val(),
		email_smtp		: encodeURI($("#email_smtp").val()),
		email_port		: isEmpty($("#email_port").val())?25:$("#email_port").val(),
		email_user		: encodeURI($("#email_user").val()),
		email_pwd		: $("#email_pwd").val(),
		email_to		: encodeURI(isEmpty($("#email_to").val())?$("#email_user").val():$("#email_to").val()),
//		sms_to			: encodeURI($("#sms_to").val()),
		old_data		: 60
	};
	if (isEmpty(data.email_smtp) || data.email_smtp == "") {
		alert("请填写邮件服务器");
		$("#email_smtp").focus();
		return false;
	} else if (!isNumeric(data.email_port)) {
		alert("请填写合法端口");
		data.email_port = 25;
		$("#email_port").focus();
		return false;
	} else if (isEmpty(data.email_user) || data.email_user == "") {
		alert("请填写用户名");
		$("#email_user").focus();
		return false;
	} else if (isEmpty(data.email_pwd) || data.email_pwd == "") {
		alert("请填写密码");
		$("#email_pwd").focus();
		return false;
	} else if (isEmpty(data.email_to) || data.email_to == "") {
		alert("请填写收件人");
		$("#email_to").focus();
		return false;
	}
	//snmp验证
	var snmp = "[";
	var checkSnmp = "";
	for(var i = 0,divs = $(".snmpDiv");i < divs.length;i++){
		var snmp_public = $(divs.get(i)).children("#snmp_public").val();
		var snmp_host = $(divs.get(i)).children("#snmp_host").val();
		var snmp_port = $(divs.get(i)).children("#snmp_port").val();
		if(isEmpty(snmp_public) || snmp_public == ""){
			alert("请填写团体");
			$(divs.get(i)).children("#snmp_public").focus();
			return false;
		}else if(isEmpty(snmp_host) || snmp_host == ""){
			alert("请填写目标IP");
			$(divs.get(i)).children("#snmp_host").focus();
			return false;
		}else if(snmp_host != null && snmp_host != "") {
			if(!ipReg.test(snmp_host)) {
				alert("不合法的目标IP地址");
				$(divs.get(i)).children("#snmp_host").focus();
				return false;
			}
		}else if(isEmpty(snmp_port) || snmp_port == ""){
			alert("请填写端口");
			$(divs.get(i)).children("#snmp_port").focus();
			return false;
		}else if(snmp_port != null && snmp_port != "") {
			if(!isNumeric(snmp_port)) {
				alert("不合法的端口");
				$(divs.get(i)).children("#snmp_port").focus();
				return false;
			}
		}
		
		snmp+="{snmppublic:'"+(isEmpty(snmp_public)?-1:snmp_public)+"',snmphost:'"+(isEmpty(snmp_host)?-1:snmp_host)+"',snmpport:'"+(isEmpty(snmp_port)?-1:snmp_port)+"'}";
		if(i<divs.length-1){
			snmp += ",";
		}
	}
	snmp += "]";
	
	//Sms验证
	var sms = "[";
	var checkSms = "";
	for(var i = 0,divs = $(".smsDiv");i < divs.length;i++){
		var smsUser = $(divs.get(i)).children("#smsuser").val();
		var smsPhone = $(divs.get(i)).children("#smsphone").val();
		if(isEmpty(smsUser) || smsUser == ""){
			alert("请填写收件人");
			$(divs.get(i)).children("#smsuser").focus();
			return false;
		}else if(isEmpty(smsPhone) || smsPhone == ""){
			alert("请填写手机号码");
			$(divs.get(i)).children("#smsphone").focus();
			return false;
		}else if(smsPhone != null && smsPhone != "") {
			if(!phoneReg.test(smsPhone)) {
				alert("不合法的手机号码");
				$(divs.get(i)).children("#smsphone").focus();
				return false;
			}
		}
		
		sms+="{smsuser:'"+(isEmpty(smsUser)?-1:smsUser)+"',smsphone:'"+(isEmpty(smsPhone)?-1:smsPhone)+"'}";
		if(i<divs.length-1){
			sms += ",";
		}
	}
	sms += "]";
	//封装数据
	data.snmps = encodeURI(snmp);
	data.smss = encodeURI(sms);
	//提交请求
	$.ajax({
		type: "POST",
		url : "${path}/servlet/alert/AlertRuleAction?func=AjaxAddForward&time=" + new Date().getTime(),
		data:data,
		success:function(result){
			if(result=="true") {
				parent.window.bAlert("操作成功！","",[{func:"AlertRule.doAfterSucc();",text:"确定"}]);
			}
			if(result=="false"){
				parent.window.bAlert("操作失败，请稍候再试！");
			}
		}
	}); 
};

//判断是否为数字
function isNumeric(strValue)
{
	if (isEmpty(strValue)) return true;
    return executeExp(/^\d*$/g, strValue);
}
	
//执行正则表达式
function executeExp(re, s)
{
    return re.test(s);
}
</script>			
			
	<%@include file="/WEB-INF/views/include/footer.jsp"%>