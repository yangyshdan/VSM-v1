<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="device">
						性能数据保留时间
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" name="ptime" id="ptime" value="${perfConfig.time_length}" style="width:90px;">
							<span class="add-on" style="margin-left:-5px;">天</span>
						</div>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="device">
						事件日志保留时间
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input type="text" name="etime" id="etime" value="${logConfig.time_length}" style="width:90px;">
							<span class="add-on" style="margin-left:-5px;">天</span>
						</div>
					</div>
				</div>

				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					<button type="reset" class="btn" onclick="resetFunc()">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script>

function resetFunc(){
	$("input[name='ptime']").removeAttr("value");
	$("input[name='etime']").removeAttr("value");
}

function saveInfo(){
	var ptime = $("input[name='ptime']").val();
	var etime = $("input[name='etime']").val();
	if(ptime=="" || !isNumeric(ptime)){
		alert("请输入正确的性能保留时间");
		return false;
	}
	if(ptime >365){
		alert("性能保留时间不能超过1年");
		return false;
	}
	if(etime=="" || !isNumeric(etime) || etime >365){
		alert("请输入正确的事件日志保留时间");
		return false;
	}
	if(etime >365){
		alert("事件日志保留时间不能超过1年");
		return false;
	}
	var jsonVal = $("#conditionForm").serializeArray();
	$.ajax({
		url:"${path}/servlet/dataconfig/DataconfigAction?func=UpdateSetting",
		data:jsonVal, 
		success:function(result){
			if(result=="true"){
				parent.window.bAlert("设置成功,系统会在24小时内生效!");
			}else{
				parent.window.bAlert("系统繁忙,请稍候再试！");
			}
		}
	});
}

/**
 判断是否为数字
 **/
function isNumeric(strValue) {
	if (isEmpty(strValue))
		return true;
	return executeExp(/^\d*$/g, strValue);
}

/**
 执行正则表达式
 **/
function executeExp(re, s) {
	return re.test(s);
}
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>