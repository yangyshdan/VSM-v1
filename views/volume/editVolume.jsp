<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="storageType">
						设备类型
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select class="form-control" id="storageType" style="width: 180px;" disabled="disabled">
								<option value="">
									-请选择-
								</option>
								<option value="DS">
									存储系统(DS8k)
								</option>
								<option value="BSP">
									存储系统(DS4k/5k)
								</option>
								<option value="SVC">
									存储系统(SVC)
								</option>
							</select>
							<input type="hidden" id="hideStorageType" name="storageType" value="" />
							<input type="hidden" id="subsystemId" name="subSystemID" value="${subSystemID}" />
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="device">
						设备
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="device" multiple="multiple" name="device">
								
							</select>
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="prfField">
						性能
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<select id="prfField" multiple="multiple" name="prfField">

							</select>
						</div>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="daterange">
						开始时间
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input value="${historyConfig.fstarttime}" name="startTime" id="startTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
						</div>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="daterange">
						结束时间
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input value="${historyConfig.fendtime}" name="endTime" id="endTime" type="text" style="width: 180px;cursor:pointer;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
						</div>
					</div>
				</div>


				<div class="control-group">
					<label class="control-label" for="legend">
						是否显示曲线名称
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<c:choose>
								<c:when test="${historyConfig.flegend==1}">
									<input name="legend" checked="checked" id="legend" value="1" data-no-uniform="true" type="checkbox" class="iphone-toggle">
								</c:when>
								<c:otherwise>
									<input name="legend" id="legend" value="1" data-no-uniform="true" type="checkbox" class="iphone-toggle">
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="isshow">
						是否显示设备名
					</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<c:choose>
								<c:when test="${historyConfig.fisshow==1}">
									<input name="isshow" checked="checked" id="isshow" value="1" data-no-uniform="true" type="checkbox" class="iphone-toggle">
								</c:when>
								<c:otherwise>
									<input name="isshow" id="isshow" value="1" data-no-uniform="true" type="checkbox" class="iphone-toggle">
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					<button class="btn" type="reset">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script>
$(document).ready(
	function() {
		$('#device').multiselect({
			includeSelectAllOption : true,
			maxHeight : 150
		});
		$('#prfField').multiselect({
			enableFiltering : 1,
			maxHeight : 200,
			maxWdith : 200
		});
		var storageType = $("#storageType option");
		$.each(storageType,function(){
			if(this.value=="${storageInfo.type}"){
				$(this).attr("selected","selected");
				$("#hideStorageType").val(this.value);
			}
		});
		defaultStorage();
});

function defaultStorage(){
	var devices = "${historyConfig.fdevice}";
	var dev = devices.split(",");
	var jsonList = ${volumeList};
	var str="";
	for(var i in jsonList){
		str+="<option ";
		for(var j=0;j<dev.length;j++){
			if(jsonList[i].svid==dev[j]){
				str+="selected=selected";
			}
		}
		str+=" value='"+jsonList[i].svid+"'>"+jsonList[i].the_display_name+"</option>";
	}
	$("#device").append(str);
	$("#device").multiselect('rebuild');
	var kp = "${historyConfig.fprfid}";
	var k = kp.split(",");
	var kpis = ${kpisList};
	var str2 = "";
	for(var i in kpis){
		str2+="<option ";
		for(var j=0;j<k.length;j++){
			if("'"+kpis[i].fid+"'"==k[j]){
				str2+="selected=selected";
			}
		}
		str2+=" value='"+kpis[i].fid+"'>"+kpis[i].ftitle+"</option>";
	}
	$("#prfField").append(str2);
	$("#prfField").multiselect('rebuild');
}

function saveInfo(){
	var storageType = $("#storageType").val();
	if(storageType==null || storageType==""){
		alert("请选择存储系统类型");
		return false;
	}
	var device = $("#device").val();
	if(device==null || device==""){
		alert("请选择设备类型");
		return false;
	}
	var prfField = $("#prfField").val();
	if(prfField==null || prfField==""){
		alert("请选择性能指标");
		return false;
	}
	var startTime = $("#startTime").val();
	if(startTime==null || startTime==""){
		alert("请选择开始时间");
		return false;
	}
	var endTime = $("#endTime").val();
	if(endTime==null || endTime==""){
		alert("请选择结束时间");
		return false;
	}
    if(!dateCompare(startTime,endTime)) {                            
        bAlert("结束日期不能小于开始日期！");
        return false;
	}
	var jsonVal = $("#conditionForm").serializeArray();
	$.ajax({
		url:"${path}/servlet/volume/VolumeAction?func=VolumePrf",
		data:jsonVal,
		success:function(result){
			if(result=="true"){
				parent.window.bAlert("操作成功！","",[{func:"doAfterSucc();",text:"确定"}]);
			}else{
				parent.window.bAlert("操作失败，请稍候再试！");
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