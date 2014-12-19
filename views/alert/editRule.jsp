<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path }/resource/js/project/editRule.js"></script>
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
				                  <label class="control-label" for="name">名称</label>
				                  <div class="controls" style="margin-right: 80px;">
								  <div class="input-prepend input-append">
				                  	<input type="text" class="form-control" id="name">
				                  </div>
				                  </div>
				              </div>
				              <div class="control-group">
				                  <label class="control-label" for="devicetype">级别</label>
				                  <div class="controls">
								  <div class="input-prepend input-append">
								   <select class="form-control" id="level">
			                        <option value="0">Info</option>
			                        <option value="1">Warning</option>
			                        <option value="2">Critical</option>
			                      </select>
			                      </div>
								</div>
				              </div>
				              
				              <div class="control-group">
								<label class="control-label" for="legend">是否启用</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<c:choose>
								  	<c:when test="${rule.fenabled == 0}">
								  		<input id="enabled"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
								  	</c:when>
								  	<c:otherwise>
								  		<input id="enabled" checked="checked"   data-no-uniform="true" type="checkbox" class="iphone-toggle">
								  	</c:otherwise>
								  </c:choose>
								</div>
								</div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="isshow">是否单独比较</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								  <c:choose>
								  	<c:when test="${rule.fisalone == 0}">
								  		<input id="isalone"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
								  	</c:when>
								  	<c:otherwise>
								  		<input id="isalone" checked="checked"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
								  	</c:otherwise>
								  </c:choose>
								</div>
								</div>
							  </div>
				              
							  <div class="control-group">
								<label class="control-label" for="type">	设备类型</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									 <select class="form-control" id="type" onchange="ontypechange();">
	  		                        <option value="DS">存储系统(IBM-DS8k)</option>
			                        <option value="BSP">存储系统(IBM-DS4k/5k)</option>
			                        <option value="SVC">存储系统(IBM-SVC)</option>
			                        <option value="SWITCH">交换机</option>
			                        <option value="Physical">物理主机</option>
			                        <option value="Virtual">虚拟主机</option>
			                        <option value="App">应用</option>
			                      </select>
							      </div>
							      </div>
							  </div>
							  
							  <div class="control-group" id="hy_div">
								<label class="control-label" for="hy_vm">物理主机</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<select id="hy_vm" multiple="multiple">
							                
							      	</select>
							      </div>
							      </div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="device">设备</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<select id="device" multiple="multiple">
							                
							      </select>
							      </div>
							      </div>
							  </div>
							  
							</fieldset>
							<legend style="margin-bottom: 5px;">阀值</legend>
							  <div id="divPrfDiv" class="control-group" style="margin-top: 3px; margin-bottom: 2px;">
							  
							   
							  
							  </div>
							  
							   <div class="form-actions">
								<input type="button" onclick="saveInfo();" class="btn btn-primary" value="保存 "/>
								<button class="btn" type="reset">重置</button>
							  </div>
						</form>
					</div>
			</div><!--/row-->
<script>
		$(document).ready(function(){
				$('#device').multiselect({
					  includeSelectAllOption: true,
					  maxHeight: 150,
					  maxWidth : 200
				});
			$("#name").val("${rule.fname}");	
			$('option[value="${rule.ftype}"]', $('#type')).attr('selected', 'selected');
			ontypechange();
			
			if(!${empty fields}){
				var fields = eval("("+'${fields}'+")");
				for(var i = 0; i < fields.length; ++i ){
					var v =  isEmpty(fields[i].fieldId)?"":fields[i].fieldId;
					EditRule.doAddPrf(fields[i].minvalue,fields[i].maxvalue,v);
				}
			}else{
				EditRule.doAddPrf("","","");
			}
		});
		
		function ontypechange(){
			var prfJson = ${prfJson};
			  $('option', $('#device')).remove();
			  $('optgroup', $('#device')).remove();
			  $('option', $('.prfDiv select')).remove();
			  var type = $('#type option:selected') .val();
			  var dtype = "${rule.fdeviceid}";
			  var prf = "${ruleData.fprfid}";
			  if(type=="Virtual"){
				  onloadHv();
				  $('#hy_div').show();
			  }else{
				  $('#hy_div').hide();
			   for(var i = 0;i<prfJson[type].length;i++){
					var apStr = '<option value="' +prfJson[type][i].value+'"';
					if(dtype.indexOf(prfJson[type][i].value) >= 0){
						apStr +='selected="selected"';
					}
					 apStr += '>' +prfJson[type][i].text+ '</option>';
					$("#device").append(apStr);
					$('#device').multiselect('rebuild');
				}
			   }
				for(var i = 0;i<prfJson.targets[type].length;i++){
					var apStr = '<option value="' +prfJson.targets[type][i].value+'"';
					if(prf.indexOf(prfJson.targets[type][i].value) >= 0){
						apStr +='selected="selected"';
					}
					 apStr += '>' +prfJson.targets[type][i].text+ '</option>';
					 $(".prfDiv select").append(apStr);
				}
		       $(".chzn-done").trigger("liszt:updated");    	   
		};
		
		function refreshField(id,value){
			var prfJson = ${prfJson};
			var type = $('#type option:selected') .val();
			  var prf = "${ruleData.fprfid}";
			  $('option', $('#'+id)).remove();
				for(var i = 0;i<prfJson.targets[type].length;i++){
					var apStr = '<option value="' +prfJson.targets[type][i].value+'"';
					if(prfJson.targets[type][i].value == value){
						apStr +='selected="selected"';
					}
					 apStr += '>' +prfJson.targets[type][i].text+ '</option>';
					 $("#"+id).append(apStr);
				}
				$("#"+id).trigger("liszt:updated");
		}
		
		function onloadHv(){
			var prfJson = ${prfJson};
			var selectHV = ${selectHV};
			$('option', $('#hy_vm')).remove();
			$('option', $('#device')).remove();
			$('#device').multiselect('rebuild');
			var dtype = "${rule.fdeviceid}";
			for(var i = 0;i<prfJson.Physical.length;i++){
				var apStr = '<option value="' +prfJson.Physical[i].value+'"';
				 apStr += '>' +prfJson.Physical[i].text+ '</option>';
				 $("#hy_vm").append(apStr);
			}
			  $('#hy_vm').multiselect({
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
									if(dtype.indexOf(data[i].value) >= 0){
										vmStr +='selected="selected"';
									}
									vmStr += '>' +data[i].text+ '</option>';
								}
								vmStr += "</optgroup>";
								$("#device").append(vmStr);
								$('#device').multiselect('rebuild');
							}
						}
					});
					}else{
						$("optgroup[label='"+element[0].text+"']", $('#device')).remove();
						$('#device').multiselect('rebuild');
					}
				}
			});
			    for(var j = 0;j<selectHV.length;j++){
				  if(selectHV[j] != -1)
					$('#hy_vm').multiselect('select', selectHV[j]);
				}
			 $('#hy_vm').multiselect('rebuild');
			 $('#hy_div').hide();
		}
		
		function saveInfo(){
			var data = {
				id				: "${rule.fid}",
				name		:  encodeURI($("#name").val()),
				type		: $("#type").val(),
				enabled	: $("#enabled").attr("checked")=="checked"?1:0,
				isalone	: $("#isalone").attr("checked")=="checked"?1:0,
				device	: $("#device").val(),
				targets	: ""
			};
			var target = "[";
			if(isEmpty(data.name)){
				alert("名称不能为空！");
				$("#name").focus();
				return false;
			}
			if(isEmpty(data.refresh)){
				data.refresh = 5;
			}else if(!isNumeric(data.refresh)){
				data.refresh = 5;
			}
			if(isEmpty(data.device)){
				alert("请选择设备！");
				$("#device").focus();
				return false;
			}else{
				var d = data.device+"";
				d = d.replace("multiselect-all,","");
				d = d.replace("multiselect-all","");
				if(isEmpty(d)){
					alert("请选择设备！");
					$("#device").focus();
					return false;
				}else{
					data.device = d;
				}
			}
			//阀值验证
			var checkValue = "";
			for(var i = 0,divs = $(".prfDiv");i < divs.length;i++){
				var prfId = $(divs.get(i)).children().children("select").val();
				var min = $(divs.get(i)).children().children("#min").val();
				var max = $(divs.get(i)).children().children("#max").val();
				if(isEmpty(prfId)){
					alert("请选择性能！");
					$(divs[i]).children().children("select").focus();
					return false;
				}
				if(isEmpty(min)&&isEmpty(max)){
					alert("请填写阀值！");
					$(divs[i]).children().children("#min").focus();
					return false;
				}else if(isNaN(min) || isNaN(max)){
					alert("阀值必须为数字！");
					$(divs[i]).children().children("#min").focus();
					return false;
				}else if(min==max){
					alert("阀值范围错误！");
					$(divs[i]).children().children("#min").focus();
					return false;
				}
				if(checkValue.indexOf(";"+prfId+";") >= 0){
					alert("存在重复性能指标！");
					$(divs[i]).children().children("select").focus();
					return;
				}else{
					checkValue += ";"+prfId+";";
				}
				target+="{fieldId:'"+prfId+"',minValue:"+(isEmpty(min)?-1:min)+",maxValue:"+(isEmpty(max)?-1:max)+"}";
				if(i<divs.length-1){
					target += ",";
				}
			}
			target += "]";
			data.targets = encodeURI(target);
			$.ajax({
			type: "POST",
			url : "${path}/servlet/alert/AlertRuleAction?func=AjaxAdd&time=" + new Date().getTime(),
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
    return executeExp(/^[0-9]+\.{0,1}[0-9]{0,2}$/g, strValue);
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