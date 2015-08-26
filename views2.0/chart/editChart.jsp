<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>

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
				              <!-- 设置类型 -->					
							  <div class="control-group">
				                  <label class="control-label" for="charttype">显示类型</label>
				                  <div class="controls">
								  <div class="input-prepend input-append">
			                      <select class="form-control" id="charttype" onchange="chartTypeFunc();">
			                        <option value="0">性能曲线</option>
			                        <option value="1">TOPN图表</option>
			                      </select>
			                      </div>
								</div>
				              </div>
				              <div class="control-group">
				                  <label class="control-label" for="devicetype">设备类型</label>
				                  <div class="controls">
								  <div class="input-prepend input-append">
			                      <select class="form-control" id="devicetype" onchange="ontypechange();">
			                        <option value="Physical">物理机</option>
			                         <option value="Virtual">虚拟机</option>
			                         <option value="App">应用</option>
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
							  
							  <div class="control-group">
								<label class="control-label" for="prfField">性能参数</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<select id="prfField" multiple="multiple">
							             
							     </select>
							     </div>
							     </div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="daterange">时间范围</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<select id="daterange">
							        <option value="day">最近一天</option>
			                        <option value="week">最近一周</option>
			                        <option value="month">最近一月</option>
							     </select>
							     </div>
							     </div>
							  </div>
							<div class="control-group">
								<label class="control-label" for="timesize">
									时间粒度
								</label>
								<div class="controls">
									<div class="input-prepend input-append">
										<select id="timesize" name="timesize">
											<option value="minute">
												分钟
											</option>
											<option value="hour">
												小时
											</option>
											<option value="day">
												天
											</option>
										</select>
									</div>
								</div>
							</div>
							  <div class="control-group">
								<label class="control-label" for="refresh">刷新频率</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<input id="refresh" style='width:174px;' type="text"><span class="add-on">分钟</span>
								  </div>
								</div>
							  </div>
							  
							  
							  <div class="control-group" id="yaxisQuery">
								<label class="control-label" for="yaxisname">y轴名称</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<input id="yaxisname" size="16" type="text">
								</div>
								</div>
							  </div>
							  <div class="control-group" style="display: none;" id="topnQuery">
								<label class="control-label" for="topcount">TOPN数量</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<input id="topcount" size="16" type="text">
								</div>
								</div>
							  </div>
							  <div class="control-group" id="controlQuery">
								<label class="control-label" for="legend">是否显示设备名</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								<c:choose>
								  	<c:when test="${chartData.flegend == 1}">
								  		<input id="legend" checked="checked"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
								  	</c:when>
								  	<c:otherwise>
								  		<input id="legend"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
								  	</c:otherwise>
								  </c:choose>
								</div>
								</div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="isshow">是否显示</label>
								<div class="controls">
								  <div class="input-prepend input-append">
								  <c:choose>
								  	<c:when test="${chartData.fisshow != 1}">
								  		<input id="isshow"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
								  	</c:when>
								  	<c:otherwise>
								  		<input id="isshow" checked="checked"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
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
		$(document).ready(function(){
				$('#device').multiselect({
					  includeSelectAllOption: true,
					  maxHeight: 150
				});
				$('#prfField').multiselect({
					  enableFiltering: 1,
					  maxHeight: 200,
					  maxWdith: 200
				});

				$('option[value="${chartData.fdevicetype}"]', $('#devicetype')).attr('selected', 'selected');
				ontypechange();
				$("#name").val("${chartData.fname}");
				$("#refresh").val("${chartData.frefresh}");
				$('option[value="${chartData.fdaterange}"]', $('#daterange')).attr('selected', 'selected');
				$('option[value="${chartData.ftimesize}"]', $('#timesize')).attr('selected', 'selected');
				$("#yaxisname").val("${chartData.fyaxisname}");
				$("#topcount").val("${chartData.ftopncount}");
				$('option[value="${chartData.fcharttype}"]', $('#charttype')).attr('selected', 'selected');
				chartTypeFunc();
		});
		function ontypechange(){
			var chartJson = ${chartJson};
			  $('option', $('#device')).remove();
			  $('option', $('#prfField')).remove();
			  var type = $('#devicetype option:selected') .val();
			  var dtype = "${chartData.fdevice}";
			  var prf = "${chartData.fprfid}";
				for(var i = 0;i<chartJson[type].length;i++){
					var apStr = '<option value="' +chartJson[type][i].value+'"';
					if(dtype.indexOf(chartJson[type][i].value) >= 0){
						apStr +='selected="selected"';
					}
					 apStr += '>' +chartJson[type][i].text+ '</option>';
					$("#device").append(apStr);
				}
				for(var i = 0;i<chartJson.targets[type].length;i++){
					var apStr = '<option value="' +chartJson.targets[type][i].value+'"';
					if(prf.indexOf(chartJson.targets[type][i].value) >= 0){
						apStr +='selected="selected"';
					}
					 apStr += '>' +chartJson.targets[type][i].text+ '</option>';
					$("#prfField").append(apStr);
				}
	           $('#device').multiselect('rebuild');
	           $('#prfField').multiselect('rebuild');
		};
		function chartTypeFunc(){
			var chartType = $("#charttype option:selected").val();
			if(chartType=='0'){
				$('#yaxisQuery').css("display","block");
				$('#controlQuery').css("display","block");
				$('#topnQuery').hide();
				$("#topcount").attr("disabled","disabled");
				$("#yaxisname").removeAttr("disabled");
				$("#yaxisname").css("background-color","#fff");
			}else{
				$('#topnQuery').css("display","block");
				$('#yaxisQuery').hide();
				$('#controlQuery').hide();
				$("#yaxisname").attr("disabled","disabled");
				$("#topcount").removeAttr("disabled");
				$("#yaxisname").css("background-color","");
			}
		}
		function saveInfo(){
			var data = {
				id				: "${chartData.fid}",
				modelId : "${modelId}",
				name		:  encodeURI($("#name").val()),
				type		: $("#devicetype").val(),
				show		: $("#isshow").attr("checked")=="checked"?1:0,
				refresh	: $("#refresh").val(),
				charttype:$("#charttype").val(),
				yaxisname: encodeURI($("#yaxisname").val()),
				topcount:$("#topcount").val(),
				dataRange	: $("#daterange").val(),
				timeSize	: $("#timesize").val(),
				device	: $("#device").val(),
				prfField	: $("#prfField").val()+"",
				legend	: $("#legend").attr("checked")=="checked"?1:0
			};
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
			if(isEmpty(data.prfField)){
				alert("请选择性能！");
				$("#prfId").focus();
				return false;
			}
			$.ajax({
			type: "POST",
			url : "${path}/servlet/chart/ChartAction?func=AddChart&time=" + new Date().getTime(),
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
	/**
  判断是否为数字
**/
function isNumeric(strValue)
{
	if (isEmpty(strValue)) return true;
    return executeExp(/^\d*$/g, strValue);
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