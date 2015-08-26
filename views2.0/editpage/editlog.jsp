<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<div class="box span4" id="charts_new">
	<input type="hidden" name="modelid" value="${item.fmodelid}"/>
	<input type="hidden" name="charttype" value="${item.fcharttype}"/>
	<input type="hidden" name="fname" value="日志告警信息模块"/>
	<div class="box-header well">
		<h2><i class="icon-plus-sign"></i> 新增日志告警信息模块</h2>
		<div class="box-icon">
			<a href="javascript:void(0)" class="btn btn-round" onclick="Widget.closeAdd()" title="关闭" data-rel="tooltip"><i class="icon-remove"></i></a>
		</div>
	</div>
	<div class="box-content" id="box-content_${item.fmodelid}_new">
		<ul class="nav nav-tabs" id="myTab">
			<li class="active">
				<a href="#wid-attr">属性</a>
			</li>
		</ul>
		<div id="myTabContent" class="tab-content" style="overflow: visible;">
			<div class="tab-pane active" id="wid-attr">
				<div id="prfContent-p" style="width: 95%; height: 100px;line-height:30px;">
					<table style="width: 100%;margin-top:10px;" class="form">
						<tr>
							<td></td>
							<td class="input-prepend input-append">
							</td>
							<td></td>
							<td>
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
			<div class="form-actions" align="center" style="margin:5px 0px 0px 5px;">
				<button class="btn btn-primary" onclick="Widget.checkLogForm()">保存</button>
				<button class="btn" onclick="Widget.reset(${item.fmodelid},${item.fcharttype});">重置</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function(){
	$("table input,table select").css({'max-width':'130px'});
});
</script>
