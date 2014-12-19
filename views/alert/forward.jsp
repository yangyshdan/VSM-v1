<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path }/resource/js/project/editRule.js"></script>
<script src="${path }/resource/js/project/forward.js"></script>
<div class="row-fluid sortable">
					<div class="box-content">
						<form class="form-horizontal">
							<fieldset>
							<input type="hidden" id="forwareId" name="forwareId" value="${forward.forwareId}" />
							<legend style="margin-bottom: 5px;">转发级别</legend>
							  <div class="control-group">
				                  <label class="control-label" for="forward_level">最低转发级别</label>
				                  <div class="controls" style="margin-right: 80px;">
								  <div class="input-prepend input-append">
				                  	<select class="form-control" id="forward_level" >
			                        <option value="0">Info</option>
			                        <option value="1">Warning</option>
			                        <option value="2">Critical</option>
			                      </select>
				                  </div>
				                  </div>
				              </div>
							<legend style="margin-bottom: 5px;">SNMP</legend>
							<div id="divSnmpDiv" class="control-group" style="margin-top: 3px; margin-bottom: 2px;">

							  </div>
							  					  
							  <legend style="margin-bottom: 5px;">Email</legend>
							  
							  <div class="control-group">
								<label class="control-label" for="email_smtp">邮件发送服务器</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<input type="text" class="form-control" id="email_smtp">
								</div>
								</div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="email_port">端口</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<input type="text" class="form-control" id="email_port">
								</div>
								</div>
							  </div>
							  
							   <div class="control-group">
								<label class="control-label" for="email_user">用户名</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<input type="text" class="form-control" id="email_user">
								</div>
								</div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="email_pwd">密码</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<input type="password" class="form-control" id="email_pwd">
								</div>
								</div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="email_to">收件人</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<textarea id="email_to"></textarea>
									<label>注：多个收件人用分号隔开</label>
								</div>
								
								</div>
								
							  </div>
							 
							  
							</fieldset>
							
							  
							  
							   <div class="form-actions">
								<input type="button" onclick="saveInfo();" class="btn btn-primary" value="保存 "/>
								<button class="btn" type="reset">重置</button>
							  </div>
						</form>
					</div>
			</div><!--/row-->
<script>
		$(document).ready(function(){
				$("#forward_level").val("${forward.forward_level}");
				$("#email_smtp").val("${forward.email_smtp}");
				$("#email_port").val("${forward.email_port}");
				$("#email_user").val("${forward.email_user}");
				$("#email_pwd").val("${forward.email_pwd}");
				$("#email_to").val("${forward.email_to}");
				if(!${empty fields}){
					var fields = eval("("+'${fields}'+")");
					for(var i = 0; i < fields.length; ++i ){
						EditForward.doAddSnmp(fields[i].snmp_public,fields[i].snmp_host,fields[i].snmp_port);
					}
				}else{
					EditForward.doAddSnmp("public","","162");
				}
		});
		function saveInfo(){
			var data = {
				forwareId				: $("#forwareId").val(),
				forward_level				: $("#forward_level").val(),
				email_smtp					: encodeURI($("#email_smtp").val()),
				email_port						: isEmpty($("#email_port").val())?25:$("#email_port").val(),
				email_user						: encodeURI($("#email_user").val()),
				email_pwd					: $("#email_pwd").val(),
				email_to							: encodeURI(isEmpty($("#email_to").val())?$("#email_user").val():$("#email_to").val()),
				old_data						: 60
			};
			if(isNumeric(data.email_port)){
				data.email_port = 25;
			}
			
			//snmp验证
			var target = "[";
			var checkValue = "";
			for(var i = 0,divs = $(".snmpDiv");i < divs.length;i++){
				var snmp_public = $(divs.get(i)).children("#snmp_public").val();
				var snmp_host = $(divs.get(i)).children("#snmp_host").val();
				var snmp_port = $(divs.get(i)).children("#snmp_port").val();
				
				if(isEmpty(snmp_public)){
					alert("请填写团体！");
					return false;
				}else if(isEmpty(snmp_host)){
					alert("请填写目标IP");
					return false;
				}else if(isEmpty(snmp_host)){
					alert("请填写端口");
					return false;
				}
				target+="{snmppublic:'"+(isEmpty(snmp_public)?-1:snmp_public)+"',snmphost:'"+(isEmpty(snmp_host)?-1:snmp_host)+"',snmpport:'"+(isEmpty(snmp_port)?-1:snmp_port)+"'}";
				if(i<divs.length-1){
					target += ",";
				}
			}
			
			target += "]";
			
			data.targets = encodeURI(target);
			$.ajax({
			type: "POST",
			url : "${path}/servlet/alert/AlertRuleAction?func=AjaxAddForward&time=" + new Date().getTime(),
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
    return executeExp(/^\d*$/g, strValue);
}
	
/**
   执行正则表达式
**/
function executeExp(re, s)
{
    return re.test(s);
}
</script>			
			
	<%@include file="/WEB-INF/views/include/footer.jsp"%>