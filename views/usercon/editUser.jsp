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
		<table class="exForm" style="width: 100%">
			<tr>
				<td width="15px;">用户名：</td>
				<td>
					<input type="hidden" name="fid" value="${user.fid }"/>
					<input type="text" name="userName"  value="${user.floginname }"/>
				</td>
				
			</tr>
			<tr>
				<td>邮箱：</td>
					<td>
						<input type="text" name="email" value="${user.femail }"/>
					</td>
				</tr>
			<tr>
				<td>角色：</td>
				<td colspan="3">
					<c:forEach var="role" items="${roleList}">
						<label style="float: left;" class="froleContent">
							<input type="radio" name="roleIds" value="${role.fid }"/>${role.fname }
						</label>
					</c:forEach>
				</td>
			</tr>
			<tr>
				<td colspan="4" class="pn-fbutton" style="padding-top:10px;">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">修改</button>
					<button type="reset" class="btn" onclick="accountMgr.reset();">重置</button>
					
				</td>
			</tr>
		</table>
		
	</div>
</div><!--/row-->
<script>
	$(document).ready(function(){
		var userRoleStr="${userRoleStr}";
		
		 //var roleIds = $("input[name='roleIds']");
		$("input[name='roleIds']").each(function(){
			if($(this).val()==userRoleStr){
				$(this).attr("checked",true);
			}
		});
	});

	function saveInfo(){
		var data = {
			userName		:  $("input[name='userName']").val(),
			email			:  $("input[name='email']").val(),
			roleIds			:  $("input[name='roleIds']:checked"),
			fid				:  $("input[name='fid']").val()
		};
		if(isEmpty(data.userName)){
			alert("用户名不能为空！");
			$("input[name='userName']").focus();
			return false;
		}
		if(data.roleIds.length==0){
			alert("角色必选选择");
			return false;
		}
		if(!isEmpty(data.email)){
			if(!checkEmail(data.email)){
				alert("请填写正确的邮箱地址");
				return false;
			}
		}
		/**
		var roleIds="";
		for(var i=0,rl=data.roleIds.length; i< rl;i++ ){
			roleIds+=data.roleIds[i].value;
			if(i != rl-1){
				roleIds+=',';
			}
		}
		*/
		var jsonVal = {userName:data.userName,roleIds:data.roleIds.val(),email:data.email,fid:data.fid};
		$.ajax({
			type:"POST",
			url:"${path}/servlet/usercon/AccountMgrAction?func=Update",
			data:jsonVal,
			contentType: "application/x-www-form-urlencoded; charset=utf-8",
			success:function(result){
				
				if(result=="true"){
					parent.window.bAlert("操作成功！","",[{func:"accountMgr.afterSucc();",text:"确定"}]);
				}else{
					parent.window.bAlert("操作失败，请稍候再试！");
				}
				
			}
		}); 
	}

	
</script>			
			
	<%@include file="/WEB-INF/views/include/footer.jsp"%>