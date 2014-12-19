<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/jquery.ztree.core-3.5.min.js"></script>
<script src="${path }/resource/js/jquery.ztree.excheck-3.5.min.js"></script>
<script src="${path }/resource/js/jquery.ztree.exedit-3.5.min.js"></script>
<script src="${path }/resource/js/project/reporttemplate.js"></script>
<script src="${path }/resource/js/custom.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
$(function(){
	ready();
	var item = ${item};
	if(item!=null){
		BaseForm.loadConfig(item);
	}
	BaseForm.ckeckTime();
	BaseForm.checkExetype();
	$("#c_subgroup_type").multiselect({
		includeSelectAllOption : true,
		maxHeight : 150
	});
	$("#c_device_type").multiselect({
		includeSelectAllOption : true,
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
				 if(selected.length>=30){
					 selected = selected.substr(0,30)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxHeight : 150
	});
	$('#p_device_type').multiselect({
		includeSelectAllOption : true,
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
				 if(selected.length>=30){
					 selected = selected.substr(0,30)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxHeight : 150
	});
	$('#p_prffield').multiselect({
		enableFiltering : 1,
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
				 if(selected.length>=30){
					 selected = selected.substr(0,30)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxHeight : 135
	});
	$('#t_device_type').multiselect({
		includeSelectAllOption : true,
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
				 if(selected.length>=30){
					 selected = selected.substr(0,30)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxHeight : 150
	});
	$('#t_prffield').multiselect({
		enableFiltering : 1,
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
				 if(selected.length>=30){
					 selected = selected.substr(0,30)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxHeight : 135
	});
	$("#p_subgroup").multiselect({
	    includeSelectAllOption: true,
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
				 if(selected.length>=30){
					 selected = selected.substr(0,30)+"...";
				 }else{
					 selected = selected.substr(0, selected.length -2);
				 }
				 return selected + ' <b class="caret"></b>';
			}
		},
		maxHeight : 175,
		maxWidth : 200
	});
	$("#time_length").keypress(function(){
		var a = this.value;
		if(isNaN(a)){
			$(this).val("");
		}
	}).keyup(function(){
		var a = this.value;
		if(isNaN(a)){
			$(this).val("");
		}
	});
	$("#top_count").keypress(function(){
		var a = this.value;
		if(isNaN(a)){
			$(this).val("");
		}
	}).keyup(function(){
		var a = this.value;
		if(isNaN(a)){
			$(this).val("");
		}
	});
	ConfigForm.checkConfig();
	PerfForm.checkThreshold();
	PerfForm.checkPref();
	TopnForm.checkTopn();
	AlertForm.checkAlert();
});
</script>
<link rel="stylesheet" href="${path}/resource/css/custom.css" />
<link rel="stylesheet" href="${path}/resource/css/zTreeStyle/zTreeStyle.css" />
<style>
    .t{
    position:relative;
    left:6px;
    top:12px;
    padding:4px;
    width:70px;
    text-align:center;
    background-color:#ffffff;
    }
    .b{
    width:98%;
    padding:15px 5px 5px 5px;
    border:#ccc 1px solid;
    -moz-border-radius: 5px;      /* Gecko browsers */
    -webkit-border-radius: 5px;   /* Webkit browsers */
    border-radius:5px;            /* W3C syntax */
    }
    .yuan{
    	border:1px #F9F9F9 solid;
  		-moz-border-radius: 5px;      /* Gecko browsers */
   	 	-webkit-border-radius: 5px;   /* Webkit browsers */
    	border-radius:5px;            /* W3C syntax */
    }
    legend{
    	margin-bottom: 2px;
    }
    #rMenu,#rMenu2,#rMenu3 {position:absolute; z-index:1;visibility:hidden; background-color:white;}
	#rMemu ul,#rMemu2 ul,#rMemu3 ul{
		float:left;
		list-style: none;
	}
	#rMenu ul li,#rMenu2 ul li,#rMenu3 ul li{
	padding:5px;
	line-height:20px;
	width:80px;
	height:20px;
	padding:0px;
	cursor: pointer;
	list-style: none;
	margin:0px auto;
	background-color: #white;
	border-bottom:1px #eee solid;
	}
	#rMenu ul li:HOVER,#rMenu2 ul li:HOVER,#rMenu3 ul li:HOVER {
	background-color:#F7E7BE;
	border:1px #F7C372 solid;
	}
	
	.multiselect-group {
	font-weight: bold;
	text-decoration: underline;
	}
	
</style>
<div id="rMenu" align="center">
	<ul style="margin:0px;" class='yuan'>
		<li id="m_del" onclick="zTreeConfigForm.removeTreeNode()">删&nbsp;&nbsp;&nbsp;除</li>
		<li id="m_reset" onclick="ConfigForm.clearConfigCon()">清&nbsp;&nbsp;&nbsp;空</li>
	</ul>
</div>
<div id="rMenu2" align="center">
	<ul style="margin:0px;" class='yuan'>
		<li id="m_del2" onclick="zTreePerfForm.removeTreeNode()">删&nbsp;&nbsp;&nbsp;除</li>
		<li id="m_reset2" onclick="PerfForm.clearPerfCon()">清&nbsp;&nbsp;&nbsp;空</li>
	</ul>
</div>
<div id="rMenu3" align="center">
	<ul style="margin:0px;" class='yuan'>
		<li id="m_del3" onclick="zTreeTopnForm.removeTreeNode()">删&nbsp;&nbsp;&nbsp;除</li>
		<li id="m_reset3" onclick="TopnForm.clearTopnCon()">清&nbsp;&nbsp;&nbsp;空</li>
	</ul>
</div>
<div id="content">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					<c:if test="${item!='null'}">
						编辑报表模板
					<input type="hidden" name="templateId" value="${item.id}">
					</c:if>
					<c:if test="${item=='null'}">
					<input type="hidden" name="templateId" value="-1">
						添加报表模板
					</c:if>
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div class="general feature_tour">
					<div class="middle">
						<div class="wrapper">
							<div class="tab">
								<a class="current"><i class="blue" style="">1. </i>基本信息</a>
								<a><i class="blue">2. </i>设备信息</a>
								<a><i class="blue">3. </i>性能信息</a>
								<a><i class="blue">4. </i>TopN指标</a>
								<a><i class="blue">5. </i>告警信息</a>
							</div>
							<div class="mask">
								<div class="maskCon">
									<div id="con1" class="innerCon" align="left">
										<div class="row-fluid sortable" style="width:100%;">
											<div class="box-content" style="margin:0px 10%">
												<form class="form-horizontal" style="margin:0px;" id="cForm">
												  <fieldset>
													<legend>配置</legend>
														<div style="width:80%;z-index:-1;position: absolute;">
															<div style="width:100px;height:100px;background-color:#eee;margin-right:35px;float:right" class="yuan" id="imgDiv" align="center">
																<img alt="表头Logo图标" id="reportLogo" align="middle" src="" style="width:100px;height:100px;line-height:100px;border:none" class="yuan">
															</div>
														</div>
														<div class="control-group" id="reportNameDiv" style="margin-top:20px;">
															<label class="control-label" for="report_name">
																模板名称
															</label>
															<div class="controls">
																<input class="hide" type="radio" name="report_type" value="0" checked="checked">
																<input type="text" id="report_name" name="report_name" value="">
															</div>
														</div>
														<div id="imgFile" class="form-horizontal" style="float:left;margin-top:0px;padding-top:0px;">
															<div class="control-group">
																<label class="control-label" for="fileInput">
																	报表图标
																</label>
																<div class="controls">
																	<input class="input-file uniform_on" id="fileInput" name="fileInput" type="file" onchange="BaseForm.checkFile()">
																	<a href="javascript:void(0)" class="btn btn-success" onclick="BaseForm.fileUpload()">上传</a>
																</div>
															</div>
														</div>
													</fieldset>
												</form>   
												<legend>时间</legend>
												<div id="timelyTab">
												<div class="t">即时报表</div>
						                  		<form class="b" id="timelyReport">
						                  			<table class="table-condensed">
						                  				<tr>
						                  					<td style="padding-bottom:10px;width:140px;margin-right:5px;" align="right">
						                  						时间范围&nbsp;&nbsp;
						                  					</td>
						                  					<td>
						                  						<select id="timescope_type" name="timescope_type">
																	<option value="0">
																		最近时间段
																	</option>
																</select>
															</td>
						                  					<td style="padding-bottom:10px;">
						                  					</td>
						                  					<td>
						                  					</td>
						                  				</tr>
						                  				<tr>
						                  					<td style="padding-bottom:10px;" align="right">
						                  						时间长度&nbsp;&nbsp;
						                  					</td>
						                  					<td>
						                  						<input type="text" name="time_length" id="time_length" />
						                  					</td>
						                  					<td style="padding-bottom:10px;width:140px;" align="right">
						                  						时间长度类型&nbsp;&nbsp;
						                  					</td>
						                  					<td>
						                  						<select id="time_type" name="time_type">
																	<option value="day">
																		天
																	</option>
																	<option value="week">
																		周
																	</option>
																	<option value="month">
																		月
																	</option>
																	<option value="year">
																		年
																	</option>
																</select>
															</td>
						                  				</tr>
						                  			</table>
												</form>
												</div>
											</div>
										</div>
									</div>
									<div id="con2" class="innerCon">
										<div class="row-fluid sortable" style="width:100%;">
											<div class="box-content" style="margin:0px 10%">
												<form class="form-horizontal" style="margin:0px;">
												  <fieldset>
													<legend>设备</legend>
													<div style="float:left;width:45%;">
								                  		<div class="t">选择配置</div>
								                        <div class="b" style="width:100%;background-color:#ffffff;height:325px;">
								                      		<table class="table-condensed" style="margin-top:40px;">
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						快捷选择&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<label for='checkAll'><input type="checkbox" name="checkAll" id="checkAll" /> &nbsp;所有设备及组件信息</label>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						设备类型&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="c_storage_type" name="c_storage_type">
																			<option value="SVC" selected="selected">
																				存储系统(SVC)
																			</option>
																			<option value="DS">
																				存储系统(IBM-DS)
																			</option>
																			<option value="BSP">
																				存储系统(IBM-BSP)
																			</option>
																			<option value="SWITCH">
																				交换机
																			</option>
																			<option value="APPLICATION">
																				应用
																			</option>
																			<option value="HOST">
																				主机
																			</option>
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						设备&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="c_device_type" name="c_device_type" multiple="multiple">
																				
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						组件&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="c_subgroup_type" name="c_subgroup_type" multiple="multiple">
							
																		</select>
																	</td>
								                  				</tr>
								                  			</table>
								                        </div>
							                  		</div>
							                        <div style="width:50px;float:left;height:325px;line-height:160px;" align="right">
							                        	<a href="javascript:void(0)" onclick="ConfigForm.addConfigCon()"><i title='添加' data-rel='tooltip' class="icon32 icon-color icon-square-plus" style="margin-right:3px;"></i></a>
							                        	<a href="javascript:void(0)" onclick="ConfigForm.clearConfigCon()"><i title='清除' data-rel='tooltip' class="icon32 icon-color icon-square-minus" style="margin-right:3px;"></i></a>
							                        </div>
							                        <div style="float:left;width:45%;margin:0px;padding:0px;">
								                        <div class="t">内容</div>
								                        <div class="b" style="width:100%;height:325px;">
								                        	<div id="configTree">
																<ul id="treeDemo" class="ztree yuan"  style="width:100%;margin:0px;padding:10px 0px;background-color:#D9EDF7;height:300px;overflow-y:scroll;overflow-x:auto;"></ul>
								                        	</div>
								                        </div>
							                        </div>
													</fieldset>
												</form>   
											</div>
										</div>
									</div>
									<div id="con3" class="innerCon">
										<div class="row-fluid sortable" style="width:100%;">
											<div class="box-content" style="margin:0px 10%">
												<form class="form-horizontal" style="margin:0px;">
												  <fieldset>
													<legend>性能</legend>
														<div style="float:left;width:45%;">
								                  		<div class="t">选择配置</div>
								                        <div class="b" style="width:100%;background-color:#ffffff;height:325px;">
								                        	<table class="table-condensed" style="margin-top:40px;">
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						设备类型&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="p_storage_type" name="p_storage_type">
																			<option value="SVC" selected="selected">
																				存储系统(SVC)
																			</option>
																			<option value="DS">
																				存储系统(IBM-DS)
																			</option>
																			<option value="BSP">
																				存储系统(IBM-BSP)
																			</option>
																			<option value="SWITCH">
																				交换机
																			</option>
																			<option value="APPLICATION">
																				应用
																			</option>
																			<option value="HOST">
																				主机
																			</option>
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						设备&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="p_device_type" name="p_device_type" multiple="multiple">
																				
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						组件类型&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="p_subgroup_type" name="p_subgroup_type">
							
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						组件&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="p_subgroup" name="p_subgroup" multiple="multiple">
																			
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						性能指标&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="p_prffield" name="p_prffield" multiple="multiple">
													
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						显示告警线&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<input type="radio" name="threshold" style="margin:0px;" id="threshold1" value="1"> 是
																		&nbsp;告警值&nbsp;<input type="text" name="threValue" id="threValue" style="width:70px;" value="${historyConfig.fthrevalue}"> <br/>
																		<input type="radio" name="threshold" style="margin:0px;" id="threshold2" value="0" checked="checked"> 否
																	</td>
								                  				</tr>
								                  			</table>
								                        </div>
							                  		</div>
							                        <div style="width:50px;float:left;height:325px;line-height:160px;" align="right">
							                        	<a href="javascript:void(0)" onclick="PerfForm.addPerfCon()"><i data-rel='tooltip' title='添加' class="icon32 icon-color icon-square-plus" style="margin-right:3px;"></i></a>
							                        	<a href="javascript:void(0)" onclick="PerfForm.clearPerfCon()"><i data-rel='tooltip' title='清除' class="icon32 icon-color icon-square-minus" style="margin-right:3px;"></i></a>
							                        </div>
							                        <div style="float:left;width:45%;">
								                        <div class="t">内容</div>
								                        <div class="b" style="width:100%;height:325px;">
								                        	<div id="perfTree">
																<ul id="treeDemo2" class="ztree yuan"  style="width:100%;margin:0px;padding:10px 0px;background-color:#D9EDF7;height:300px;overflow-y:scroll;overflow-x:auto;"></ul>
								                        	</div>
								                        </div>
							                        </div>
													</fieldset>
												</form>   
											</div>
										</div>
									</div>
									<div id="con4" class="innerCon">
										<div class="row-fluid sortable" style="width:100%;">
											<div class="box-content" style="margin:0px 10%">
												<form class="form-horizontal" style="margin:0px;">
												  <fieldset>
													<legend>TopN</legend>
														<div style="float:left;width:45%;">
								                  		<div class="t">选择配置</div>
								                        <div class="b" style="width:100%;background-color:#ffffff;height:325px;">
								                        	<table class="table-condensed" style="margin-top:40px;">
								                        		<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						TOP数量&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<input type="text" name="top_count" id="top_count" />
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						设备类型&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="t_storage_type" name="t_storage_type">
																			<option value="SVC" selected="selected">
																				存储系统(SVC)
																			</option>
																			<option value="DS">
																				存储系统(IBM-DS)
																			</option>
																			<option value="BSP">
																				存储系统(IBM-BSP)
																			</option>
																			<option value="SWITCH">
																				交换机
																			</option>
																			<option value="APPLICATION">
																				应用
																			</option>
																			<option value="HOST">
																				主机
																			</option>
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						设备&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="t_device_type" name="t_device_type" multiple="multiple">
																				
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						组件&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="t_subgroup_type" name="t_subgroup_type">
							
																		</select>
																	</td>
								                  				</tr>
								                  				<tr>
								                  					<td style="padding-bottom:10px;width:140px;" align="right">
								                  						性能指标&nbsp;&nbsp;
								                  					</td>
								                  					<td>
								                  						<select id="t_prffield" name="t_prffield" multiple="multiple">
													
																		</select>
																	</td>
								                  				</tr>
								                  			</table>
								                        </div>
							                  		</div>
							                        <div style="width:50px;float:left;height:325px;line-height:160px;" align="right">
							                        	<a href="javascript:void(0)" onclick="TopnForm.addTopnCon()"><i data-rel='tooltip' title='添加' class="icon32 icon-color icon-square-plus" style="margin-right:3px;"></i></a>
							                        	<a href="javascript:void(0)" onclick="TopnForm.clearTopnCon()"><i data-rel='tooltip' title='清除' class="icon32 icon-color icon-square-minus" style="margin-right:3px;"></i></a>
							                        </div>
							                        <div style="float:left;width:45%;">
								                        <div class="t">内容</div>
								                        <div class="b" style="width:100%;height:325px;">
								                        	<div id="topnTree">
																<ul id="treeDemo3" class="ztree yuan"  style="width:100%;margin:0px;padding:10px 0px;background-color:#D9EDF7;height:300px;overflow-y:scroll;overflow-x:auto;"></ul>
								                        	</div>
								                        </div>
							                        </div>
													</fieldset>
												</form>   
											</div>
										</div>
									</div>
									<div id="con5" class="innerCon">
										<div class="row-fluid sortable" style="width:100%;">
											<div class="box-content" style="margin:0px 10%">
												<form class="form-horizontal" style="margin:0px;" id="aForm">
												  <fieldset>
													<legend>事件告警</legend>
														<div class="control-group">
															<label class="control-label" for="alertLevel">
																告警信息级别:
															</label>
															<div class="controls">
																<label class="checkbox" style="padding-top:5px;float:left;">
																	<input type="checkbox" name="alertLevel" value="Info">
																	<span class='label'>Info</span>
																</label>
																<label class="checkbox" style="padding-top:5px;float:left;padding-left:40px;">
																	<input type="checkbox" name="alertLevel" value="Warning">
																	<span class='label label-warning'>Warning</span>
																</label>
																<label  class="checkbox" style="padding-top:5px;float:left;padding-left:40px;">
																	<input type="checkbox" name="alertLevel" value="Critical">
																	<span class='label label-important'>Critical</span>
																</label>
																<label style="padding-top:5px;float:left;padding-left:40px;" class="checkbox">
																	<input type="checkbox" name="alertLevelAll" value="all">
																	全选
																</label>
															</div>
														</div>
													</fieldset>
													<div class='form-actions' style="width:100%;margin-top:100px;padding-left:0px;" align="center">
														<a href="javascript:Report.createTemplate()" type="button" class="btn btn-success" >保存模板</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														<a href="javascript:Report.execReport()" type="button" class="btn btn-primary">生成报表</a>
													</div>
												</form>  
												<form id="successForm" method="post" action="" target="_blank">
													<input name='jsonStr' type="hidden" value=""/>
												</form> 
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="nav">
								<p>
									<a title="上一步" href="javascript:void(0)" class="prev" data-rel='tooltip'></a>
									<a title="下一步" href="javascript:void(0)" class="next" data-rel='tooltip'></a>
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>