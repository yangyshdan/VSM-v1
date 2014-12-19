<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<div class="box span6" id="charts_${item.fmodelid}_new">
	<div class="box-header well">
		<h2><i class="icon-list-alt"></i> 新增列表信息</h2>
		<div class="box-icon">
			<a href="javascript:void(0)" class="btn btn-round" onclick="Widget.closeAdd()" title="关闭" data-rel="tooltip"><i class="icon-remove"></i></a>
		</div>
	</div>
	<div class="box-content" id="box-content_${item.fmodelid}_new">
		<ul class="nav nav-tabs" id="myTab">
			<li class="">
				<a href="#wid-attr">属性</a>
			</li>
			<li class="active">
				<a href="#wid-con">内容</a>
			</li>
		</ul>
		<div id="myTabContent" class="tab-content" style="overflow: visible;">
			<div class="tab-pane active" id="wid-attr">
				<div id="prfContent-p" style="width: 95%; height: 100px;line-height:30px;">
					<table style="width: 100%;margin-top:10px;" class="form">
						<tr>
							<td>标题</td>
							<td><input type="text" name="fname" value="" id="fname"></td>
							<td>窗口大小</td>
							<td>
								<select id="winSize">
									<option value="12">1x1</option>
									<option value="6">1x2</option>
									<option value="4">1x3</option>
								</select>
							</td>
						</tr>
						<tr>
							<td>刷新频率</td>
							<td class="input-prepend input-append">
								<input type="text" name="refresh" id="refresh" value="5" style="width:90px;">
								<span class="add-on" style="margin-left:-5px;">分钟</span>
							</td>
							<td>显示曲线名称</td>
							<td>
								<input id="flegend" value="1" checked="checked"  data-no-uniform="true" type="checkbox" class="iphone-toggle">
							</td>
						</tr>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="tab-pane" id="wid-con">
				<div id="prfContent-c" style="width: 95%; height: 100px;line-height:30px;">
					<table style="width: 100%;margin-top:10px;" class="form">
						<tr>
							<td>设备类型</td>
							<td>
								<select class="form-control" id="devType" onchange="Widget.changeDevType()">
									<option value="">-请选择-</option>
									<option value="DS">存储系统(IBM-DS8k)</option>
									<option value="BSP">存储系统(IBM-DS4k/5k)</option>
									<option value="SVC">存储系统(IBM-SVC)</option>
									<option value="SWITCH">交换机</option>
								</select>
							</td>
							<td>设备</td>
							<td>
								<select id="device" name="device">
						
								</select>
							</td>
						</tr>
						<tr>
							<td>性能指标</td>
							<td>  
								<select id="prfField" name="prfField" onchange="Widget.changeKPI()">

								</select>
							</td>
							<td>部件</td>
							<td>
								<select id="subDev" multiple="multiple">
								</select>
							</td>
						</tr>
						<tr>
							<td>时间</td>
							<td>
								<select id="daterange">
									<option value="day">最近一天</option>
									<option value="week">最近一周</option>
									<option value="month">最近一月</option>
								</select>
							</td>
							<td>时间粒度</td>
							<td>
								<select id="timesize" name="timesize">
									<option value=""> 分钟 </option>
									<option value="hourly"> 小时 </option>
									<option value="daily"> 天 </option>
								</select>
							</td>
						</tr>
					</table>
				</div>
			</div>
			<div class="form-actions" align="center" style="margin:5px 0px 0px 5px;">
				<button class="btn btn-primary" onclick="Widget.checkForm()">保存</button>
				<button class="btn" onclick="Widget.reset(${item.fmodelid},${item.fcharttype});">重置</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function(){
	$("table input,table select").css({'max-width':'130px'});
	$("#device").multiselect({
		enableFiltering : 1,
		maxHeight : 100,
		maxWidth:140,
		buttonText : function(options, select) {
			if (options.length == 0) {
				return 'None selected <b class="caret"></b>';
			}else{
				 var selected = '';
				 options.each(function() {
				 	selected += $(this).text() + ', ';
				 });
				 if(selected.length>15){
					 selected = selected.substr(0,15)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		}
	});
	$("#prfField").multiselect({
		enableFiltering : 1,
		buttonText : function(options, select) {
			if (options.length == 0) {
				return 'None selected <b class="caret"></b>';
			}else{
				 var selected = '';
				 options.each(function() {
				 	selected += $(this).text() + ', ';
				 });
				 if(selected.length>15){
					 selected = selected.substr(0,15)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxWidth:140,
		maxHeight : 120,
		onDropdownShow: function(event) {
			alert("a");
			$(this).css('max-width','100px');
		}
	});	
	$("#devType").multiselect({
		maxHeight : 100,
		buttonText : function(options, select) {
			if (options.length == 0) {
				return 'None selected <b class="caret"></b>';
			}else{
				 var selected = '';
				 options.each(function() {
				 	selected += $(this).text() + ', ';
				 });
				 if(selected.length>10){
					 selected = selected.substr(0,10)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		}
	});
	$("#subDev").multiselect({
		includeSelectAllOption : true,
		maxHeight : 100,
		buttonText : function(options, select) {
			if (options.length == 0) {
				return 'None selected <b class="caret"></b>';
			} else if(options.length > 2){
				return options.length+ ' selected <b class="caret"></b>';
			}else{
				 var selected = '';
				 options.each(function() {
				 	selected += $(this).text() + ', ';
				 });
				 if(selected.length>10){
					 selected = selected.substr(0,10)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		}
	});
	
	$("select").multiselect({
		maxHeight : 100
	});	
});
</script>
