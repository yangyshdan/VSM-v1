<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<div class="box span6" id="charts_new">
	<input type="hidden" name="modelid" value="${item.fmodelid}"/>
	<input type="hidden" name="fid" value="${item.fid}"/>
	<input type="hidden" name="charttype" value="${item.fcharttype}"/>
	<div class="box-header well">
		<h2><i class="icon-adjust"></i> ${empty item.fid ? '新增' : '编辑'}容量图模块</h2>
		<div class="box-icon">
			<a href="javascript:void(0)" class="btn btn-round" onclick="Widget.closeAdd(${item.fmodelid},'${item.fid}')" title="关闭" data-rel="tooltip"><i class="icon-remove"></i></a>
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
									<option value="4">1x1</option>
									<option value="6">1x2</option>
									<option value="12">1x3</option>
								</select>
							</td>
						</tr>
						<tr>
							<td>刷新频率</td>
							<td class="input-prepend input-append">
								<input type="text" name="refresh" id="refresh" value="5" style="width:90px;">
								<span class="add-on" style="margin-left:-5px;">分钟</span>
							</td>
							<td></td>
							<td>
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
							<td> </td>
							<td>  
							</td>
							<td> </td>
							<td>
							</td>
						</tr>
						<tr>
							<td>设备类型</td>
							<td>
								<select class="form-control" id="devType" onchange="Widget.changeDevType('','','')">
									<option value="">-请选择-</option>
									<option value="PHYSICAL">服务器(物理机)</option>
									<option value="VIRTUAL">服务器(虚拟机)</option>
									<c:forEach items="${vsm_devtype}" var="dev_type">
										<c:if test="${dev_type.key != 'SWITCH' && dev_type.key != 'HOST'}">
											<option value="${dev_type.key}">${dev_type.value}</option>
										</c:if>
									</c:forEach>
								</select>
							</td>
							<td>设备</td>
							<td>
								<select id="device" name="device">
						
								</select>
							</td>
						</tr>
						<tr>
							<td></td>
							<td style="display: none;">
								<select id="prfField" name="prfField">

								</select>
							</td>
							<td></td>
							<td>
							</td>
						</tr>
					</table>
				</div>
			</div>
			<div class="form-actions" align="center" style="margin:5px 0px 0px 5px;">
				<button class="btn btn-primary" onclick="Widget.checkPieForm()">保存</button>
				<button class="btn" onclick="Widget.reset(${item.fmodelid},'${item.fid}',${item.fcharttype});">重置</button>
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
	$("select").multiselect({
		maxHeight : 100
	});	
	//如果选择的是编辑模板
	var moduleInfo = ${moduleInfo};
	if (moduleInfo != null) {
		//$("#fname").val(moduleInfo.fname);
		$("#winSize").val(moduleInfo.fsize);
		$("#refresh").val(moduleInfo.frefresh);
		$("#devType option").each(function() {
			var val = $(this).val();
			if (moduleInfo.fdevicetype == val) {
				$(this).attr("selected", true);
			}
		}); 
		$("#winSize").multiselect('rebuild');
		$("#devType").multiselect('rebuild');
		Widget.changeDevType(moduleInfo.fdevice,moduleInfo.fsubdev,moduleInfo.fprfid);
	}
});
</script>
