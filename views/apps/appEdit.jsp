<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path }/resource/js/project/apps.js"></script>
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
						<form class="form-horizontal">
							<fieldset>
							  <div class="control-group">
				                  <label class="control-label" for="name">应用名称</label>
				                  <div class="controls" style="margin-right: 80px;">
				                  	<input type="text" class="form-control"  id="name" value="${appInfo.fname}">
				                  </div>
				              </div>
				              <div class="control-group">
				                  <label class="control-label" for="hypervisor">物理机</label>
				                  <div class="controls">
								  <div class="input-prepend input-append">
								   <select  id="hypervisor" multiple="multiple">
			                        
			                      </select>
			                      </div>
								</div>
				              </div>
				              
				              <div class="control-group">
								<label class="control-label" for="virtual">虚拟机</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<select id="virtual" multiple="multiple">
							                
							      </select>
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
			$('[rel="tooltip"],[data-rel="tooltip"]').tooltip({"placement":"bottom",delay: { show: 400, hide: 200 }});
			onload();
		});
		
		function onload(){
			var hypervisors = ${editJson.hypervisors};
			var selectHV = ${editJson.selectHV};
			var selectVM = ${editJson.selectVM};
			for(var i = 0;i<hypervisors.length;i++){
				var apStr = '<option value="' +hypervisors[i].value+'"';
				for(var j = 0;j<selectHV.length;j++){
					if(selectHV[j] == hypervisors[i].value){
						apStr += ' selected="selected"';
					}
				}
				 apStr += '>' +hypervisors[i].text+ '</option>';
				 $("#hypervisor").append(apStr);
			}

			$('#hypervisor').multiselect({
				onChange: function(element, checked) {
					var vmStr = "<optgroup label='"+element[0].text+"'>";
					if(checked){
					$.ajax({
						async:false,
						type:"POST",
						data:{'hyperVisiorId':element.val()},
						url:"${path}/servlet/alert/AlertRuleAction?func=AjaxVirtual",
						success:function(data){
							if(!isEmpty(data)){
								data = eval("("+data+")");
								for(var i=0;i<data.length;i++ ){
									vmStr += '<option value="' +data[i].value+'"';
									
									vmStr += '>' +data[i].text+ '</option>';
								}
								vmStr += "</optgroup>";
								$("#virtual").append(vmStr);
								$('#virtual').multiselect('rebuild');
							}
						}
					});
					}else{
						$("optgroup[label='"+element[0].text+"']", $('#virtual')).remove();
						$('#virtual').multiselect('rebuild');
					}
				}
			});
			   $('#hypervisor').multiselect('rebuild');
			  for(var j = 0;j<selectHV.length;j++){
				  if(selectHV[j] != -1)
					$('#hypervisor').multiselect('select', selectHV[j]);
				}
			 $('#hypervisor').multiselect('rebuild');
			  for(var j = 0;j<selectVM.length;j++){
					$('#virtual').multiselect('select', selectVM[j]);
				}
			 $('#virtual').multiselect('rebuild');
		}
		
		function saveInfo(){
			var vm = $("#virtual").val()+"";
			var data = {
				id				: "${appInfo.fid}",
				name		:  $("#name").val(),
				device	: vm
			};
			if(isEmpty(data.name)){
				alert("应用名称不能为空！");
				$("#name").focus();
				return false;
			}
			if(data.name.length > 50){
				alert("应用名称不能超过50个字符！");
				$("#name").focus();
				return false;
			}
			if(vm == "null"){
				alert("请选择虚拟机！");
				$("#virtual").focus();
				return false;
			}
			$.ajax({
			type: "POST",
			url : "${path}/servlet/apps/AppsAction?func=AjaxAdd&time=" + new Date().getTime(),
			data:data,
			success:function(result){
				if(result=="true")  
				{
					parent.window.bAlert("操作成功！","",[{func:"App.doAfterSucc();",text:"确定"}]);
				}
				if(result=="false")
				{
					parent.window.bAlert("操作失败，请稍候再试！");
				}
			}
			}); 
	};

	
</script>			
			
	<%@include file="/WEB-INF/views/include/footer.jsp"%>