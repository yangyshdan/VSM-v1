<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
	doValidateAppForm = function(target){
		var appName = $.trim($(target).find("input[name='appName']").val());
		var $showTips = $("#showTips");
		$showTips.hide();
		var isOK = appName && appName.length > 0;
		if(!isOK){ 
			$showTips.text("名称不能为空!!!").show();
			return isOK;
		}
		var userAction = parseInt("${action}");
		if(isNaN(userAction)){
			userAction = 0;
		}
		if(userAction == 0){
			$.ajax({
				url: "${pageContext.request.contextPath}/servlet/topo/TopoAction?func=AppNameExists",
				type: "post",
				async: false,
				dataType: "json",
				data: {appName: appName},
				success: function(jsonData){
					isOK = true;
			     	if(jsonData.success){
			     		isOK = jsonData.value;
			     		if(jsonData.value){
			     			$showTips.text("业务系统名称已存在!!!").show();
			     			isOK = false;
			     		}
			     		else { isOK = true; }
			     	}
			     	else {
			     		$showTips.text(jsonData.msg).show();
			     		isOK = false;
			     	}
			    },
				statusCode: {
				   	404: function(){
				    	alert("page not found!");
					},
					500: function(){
						alert("server internal errors!");
					}
				}
			});
		}
		return isOK;
	};
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="appForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr>
							<td style="text-align:right;">名称:</td>
							<td><input name="appName" type="text" class="form-control" placeholder="例如, 我的业务系统" style="width:260px;cursor:pointer;"/></td>
						</tr>
						<tr>
							<td style="text-align:right;">描述:</td>
							<td><input name="description" type="text" style="width:260px;cursor:pointer;"/></td>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</form>
	</div>
</div>
