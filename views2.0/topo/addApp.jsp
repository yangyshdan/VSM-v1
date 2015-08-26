<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>

<link href="${path}/resource/js/SmartWizard/styles/smart_wizard.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${path}/resource/js/SmartWizard/js/jquery.smartWizard-2.0.min.js"></script>

<script type="text/javascript">
	$(function() {
		var stepIndex = 1,
		$wizard = $('#wizardPN_TD4HDQ4_m'),
		onFinishCallback = function(){
			var validation = false, name = $('#_S3FaD_13As_').val(),
			description = $('#__1Vh_QLpRCp').val(),
			serverid = parseInt($('#P5Oh8q_tli3S').val());
			if(name == undefined || name == null || $.trim(name).length == 0){
				validation = true;
				$('#p6Q_nX8_T9Fi').show();
			}
			
			if(isNaN(serverid)){
				validation = true;
				$('#_g9_c47vR_Y3').show();
			}
			
			if(validation){ return; }
			else { 
				$('#p6Q_nX8_T9Fi').hide();
				$('#_g9_c47vR_Y3').hide();
			}
			
			$.ajax({
				url: '${path}/servlet/topo/TopoAction?func=SaveApp',
				type: 'post',
				dataType: 'json',
				data: {
					name: name,
					description: description,
					server_id: serverid
				},
				success: function(jsonData){
					if(jsonData.success){
						alert(jsonData.msg);
					}
					else {
						alert(jsonData.msg);
					}
				}
			});
		};
		
		$wizard.smartWizard({
			transitionEffect: 'slideleft',
			onLeaveStep: function(obj){ return true; },
			onFinish: onFinishCallback,
			enableFinishButton: false
		});
		
		$wizard.find('a.buttonPrevious').text('上一步').attr({disabled: true});
		$wizard.find('a.buttonNext').text('下一步').attr({disabled: true});
		$wizard.find('a.buttonFinish').text('提交');
		$wizard.find('#p6Q_nX8_T9Fi').hide();
		$wizard.find('#_g9_c47vR_Y3').hide();
		
		var $select = $('#P5Oh8q_tli3S');
		var ssss = ${apps};
		$.each(ssss, function(index, _switch){
			$('<option>').text(_switch.name).appendTo($select).val(_switch.id);
		});
		ssss = null;
		var $vmSelect = $("#vmSelectFv009n");
		console.log($vmSelect);
		$vmSelect.multiselect({
			enableFiltering : 1,
			maxHeight : 200,
			maxWdith : 300
		});
		$.ajax({
			url: "${path}/servlet/topo/TopoAction?func=GetAppDataFromDB2",
			type: 'post',
			dataType: 'json',
			data: { },
			success: function(data){
				if(data && data.length > 0){
					var st = "";
					$.each(data, function(index, vm){
						st += "<option value='" + vm.vm_id + "'>" + vm.vm_name + "</option>";
						//$vmSelect.append($("<option>").text(vm.vm_name).val(vm.vm_id));
					});
					$vmSelect.append(st);
					$vmSelect.multiselect("rebuild");
				}
				
			}
		});
	});
</script>
<style>
.swMain {
    float: left; 
    width: 490px;
}

.swMain .loader {
    color: #5A5655;
    background: #FFF url(../images/loader.gif) no-repeat 5px;
}
.swMain .stepContainer div.content {
    height: 340px;
    width: 100%;
    clear: both;
}
</style>

<table align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<div id="wizardPN_TD4HDQ4_m" class="swMain">
				<ul>
					<li>
						<a href="#step-1o5fiK_d0gxCG">
							<span class="stepNumber">1</span>
							<span class="stepDesc">填写应用的信息<br/>
							</span>
						</a>
					</li>
				</ul>
				<div id="step-1o5fiK_d0gxCG" style="width:480px">
					<h2 class="StepTitle">表单</h2>
					<div class="row-fluid sortable">
						<div class="box-content">
							<form class="form-horizontal" id="Form_WA9e2S__T8z">
								<fieldset>
									<div class="control-group">
										<label class="control-label" for="_S3FaD_13As_">应用名称</label>
										<div class="controls">
											<div class="input-prepend input-append">
												<input id="_S3FaD_13As_" name="name" type="text" class="form-control" placeholder="例如, 我的应用" style="width: 180px;cursor:pointer;"/>
												<small id="p6Q_nX8_T9Fi" class="help-block" style="color:red">必须填写应用名称</small>
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="__1Vh_QLpRCp">描述</label>
										<div class="controls">
											<div class="input-prepend input-append">
												<input id="__1Vh_QLpRCp" name="description" type="text" style="width: 180px;cursor:pointer;"/>
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="vmSelect">虚拟机</label>
										<div class="controls">
											<div class="input-prepend input-append">
												<select id="vmSelectFv009n" multiple="multiple" name="vmSelect"></select>
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="P5Oh8q_tli3S">主机</label>
										<div class="controls">
											<div class="input-prepend input-append">
												<select class="form-control" name="server" id="P5Oh8q_tli3S" style="width: 180px;"> 
													<option value="">--请选择--</option>
												</select>
												<small id="_g9_c47vR_Y3" class="help-block" style="color:red">必须选择一项</small>
											</div>
										</div>
									</div>
								</fieldset>
							</form>
						</div>
					</div>
				</div>
			</div>
		</td>
	</tr>
</table>
