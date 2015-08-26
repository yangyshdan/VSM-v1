<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script type="text/javascript">
	$(function(){
		$("#serverSelect").multiselect({
			enableFiltering : 1,
			nonSelectedText: "未选择的服务器",
			nSelectedText: "台服务器被选中",
			maxHeight : 200,
			maxWdith : 300,
			minWdith : 200
		});
		$.ajax({
			url: "${path}/servlet/topo/TopoAction?func=GetAppDataFromMySQL",
			type: "post",
			dataType: "json",
			data: { },
			success: function(jsonData){
				if(jsonData.success){
					var data = jsonData.value;
					if(data && data.length > 0){
						var $serverSelect = $("#serverSelect");
						$.each(data, function(index, dev){
							$serverSelect.append($("<option>").text(dev.dev_name).val(dev.seid)
								.attr({ip: dev.ip_address, computerId: dev.dev_id}));
						});
						$serverSelect.multiselect("rebuild");
					}
				}
				else {
					parent.window.bAlert(jsonData.msg);
				}
			}
		});
		var appInfo = ${appInfo};
		if(appInfo){
			var $form = $("#conditionForm");
			$form.find("input[name='appName']").val(appInfo.name);
			var vms = appInfo.vms;
			$.each($("#serverSelect option"), function(){
				var $option = $(this);
				var optVal = $option.val();
				for(var i = 0, len = vms.length; i < len; ++i){
					if(optVal == vms[i].vm_id){
						$option.attr("selected", "selected");
						break;
					}
				}
			});
			$vmSelect.multiselect("rebuild");
			$form.find("input[name='description']").val(appInfo.description);
		};
		
		saveAppInfo = function(){
			var $form = $("#conditionForm"),
			countError = 0;
			var appName = $form.find("input[name='appName']").val();
			$("#appName_S3FaD_13As").siblings("small.help-block").remove();
			if(appName == null || appName == undefined || $.trim(appName).length == 0){
				++countError;
				$("#appName_S3FaD_13As").parent().append($("<small>").addClass("help-block").css({color: "red"}).text("应用名称不能为空"));
			}
			var description = $form.find("input[name='description']").val();
			var options = $("#serverSelect option:selected");
			var data = {};
			$("#serverSelect").siblings("small.help-block").remove();
			if(options.length == 0){
				++countError;
				$("#serverSelect").parent().append($("<small>").addClass("help-block").css({color: "red"}).text("请选择连接的服务器, 至少选择一项"));
			}
			else {
				$.each(options, function(index, option){
					var $opt = $(option),
					idKey = "devid" + index,
					nameKey = "devname" + index,
					ipKey = "devip" + index;
					cidKey = "computerId" + index;
					data[idKey] = $opt.val();
					data[nameKey] = $opt.text();
					data[ipKey] = $opt.attr("ip");
					data[cidKey] = $opt.attr("computerId");
				});
				data.appName = appName;
				data.description = description;
				data.devCount = options.length;
			}
			if(countError > 0){ return false; }
			$.ajax({
				url: "${path}/servlet/topo/TopoAction?func=SaveApp",
				type: "post",
				dataType: "json",
				data: data,
				success: function(data){
					if(data.success){
						parent.window.bAlert(data.msg, "", [{func:"doAfterSucc22222();", text:"确定"}]);
					}
					else {
						parent.window.bAlert(data.msg);
					}
				}
			});
		}
	});
</script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="storageType">应用名称</label>
					<div class="controls">
						<input id="appName_S3FaD_13As" name="appName" type="text" class="form-control" placeholder="例如, 我的应用" style="width:180px;cursor:pointer;"/>
					</div>
				</div>
				<div class="control-group"><%-- 应用可以连接虚拟机，也可以连接物理机  --%>
					<label class="control-label" for="serverSelect">Windows X86 服务器</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="serverSelect" multiple="multiple" name="serverSelect"></select>
						</div>
					</div>
				</div>
				<%--
				<div class="control-group">
					<label class="control-label" for="P5Oh8q_tli3S">主机</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select class="form-control" name="server" id="P5Oh8q_tli3S" style="width:180px;"> 
								<option value="">暂时不可用</option>
							</select>
						</div>
					</div>
				</div>--%>
				<div class="control-group">
					<label class="control-label" for="desc_QLpRCp">描述</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input id="desc_QLpRCp" name="description" type="text" style="width:180px;cursor:pointer;"/>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<button type="button" onclick="saveAppInfo()" class="btn btn-primary">保存</button>
					<button class="btn" type="reset" onclick="resetFunc()">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>