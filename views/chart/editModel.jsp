<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<link href='${path }/resource/css/bootstrap-switch.css' rel='stylesheet'>
<div class="row-fluid sortable">
					<div class="box-content">
						<form class="form-horizontal">
							<fieldset>
							  <div class="control-group">
				                  <label class="control-label" for="name">名称</label>
				                  <div class="controls" style="margin-right: 80px;">
								  <div class="input-prepend input-append">
				                  <input type="text" class="form-control" id="name" value="${model.fname}">
				                  </div>
				                  </div>
				              </div>
				              
							  <div class="control-group">
				                  <label class="control-label" for="isshow">是否显示</label>
				                  <div class="controls" style="margin-right: 80px;">
								  <div class="input-prepend input-append">
				                  <c:choose>
									  	<c:when test="${model.fisshow == 0}">
									  		 <div class="make-switch">
								                 <input id="isshow" type="checkbox" >
								             </div>
									  	</c:when>
									  	<c:otherwise>
									  		<div   class="make-switch">
								                 <input id="isshow" type="checkbox" checked>
								             </div>
									  	</c:otherwise>
									</c:choose>	
				                  </div>
				                  </div>
				              </div>
							  <div class="form-actions">
								<input type="button" onclick="saveInfo();" class="btn btn-primary" value="保存 "/>
								<button class="btn" type="reset">重置</button>
							  </div>
							</fieldset>
						</form>
					</div>
			</div><!--/row-->

	<script>
		function saveInfo(){
			var data = {
				fid				:	'${model.fid}',
				name		:  $("#name").val(),
				isshow	:	$("#isshow").attr("checked")=="checked"?1:0
			};
			if(isEmpty(data.name)){
				alert("名称不能为空！");
				$("#name").focus();
				return false;
			}else if($("#name").val().length > 10){
				alert("名称长度不能大于十！");
				$("#name").focus();
				return false;
			}
			$.ajax({
			type: "POST",
			url : "${path}/servlet/chart/ChartAction?func=EditModel&time=" + new Date().getTime(),
			data:data,
			success:function(result){
				if(result=="true")  
				{
					parent.window.bAlert("操作成功！","",[{func:"doAfterSucc();",text:"确定"}]);
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