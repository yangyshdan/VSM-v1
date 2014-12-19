<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path }/resource/js/project/accountMgr.js"></script>
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
		<table style="width: 100%">
			<tr>
				<td colspan="2" align="center">
				<span class="center" id="pwdError" style="color: red;min-height: 18px;">${error }&nbsp;</span>
				</td>
			</tr>
		</table>
	
		<table class="exForm" style="width: 100%">
			<tr>
				<td width="15px;">用户名：</td>
				<td>
					<input type="hidden" name="fid" value="${user.fid }"/>
					<input type="text" name="userName" disabled="disabled" value="${user.floginname }" readonly="readonly"/>
				</td>
			</tr>
			<tr>
				<td>原密码：</td>
				<td >
					<input type="password" name="pwd"/>
					
				</td>
			</tr>
			<tr>
				<td>新密码：</td>
				<td>
					<input type="password" name="newPwd"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="pn-fbutton">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">修改</button>
					<button type="reset" class="btn" onclick="accountMgr.reset();">重置</button>
					
				</td>
			</tr>
		</table>
		
	</div>
</div><!--/row-->
<script>
	function saveInfo(){
		var oldPwd="${oldPwd}";
		
		var data = {
			pwd				:  $("input[name='pwd']").val(),
			newPwd			:  $("input[name='newPwd']").val(),
			fid				:  $("input[name='fid']").val()
		};
		if(isEmpty(data.pwd)){
			alert("原密码不能为空");
			$("input[name='pwd']").focus();
			return false;
		}
		if(isEmpty(data.newPwd)){
			alert("新密码不能为空");
			$("input[name='newPwd']").focus();
			return false;
			
		}else{
			if(data.newPwd.length<6||data.pwd.length>16){
				$("#pwdError").html("密码长度请在6~16位之间");
				return false;
			}

		}
		
		if(data.pwd!=oldPwd){
			
			$("input[name='pwd']").focus();
			$("#pwdError").html("原密码错误");
			return false;
		}
		
		var jsonVal = {pwd:data.pwd,newPwd:data.newPwd,fid:data.fid};
		$.ajax({
			type:"POST",
			url:"${path}/servlet/usercon/AccountMgrAction?func=UpdatePwd",
			data:jsonVal,
			contentType: "application/x-www-form-urlencoded; charset=utf-8",
			success:function(result){
				
				if(result=="true"){
					parent.window.bAlert("操作成功！","",[{func:"accountMgr.afterSucc();",text:"确定"}]);
				}else{
					bAlert("原密码不正确，请重新填写！");
				}
				
			}
		}); 
	}

	
</script>			
			
	<%@include file="/WEB-INF/views/include/footer.jsp"%>