<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path}/resource/js/project/widget.js"></script>
<script src="${path }/resource/js/project/index.js"></script>
<div class="row-fluid sortable" id="chartsContent">
	<div class="box-content" id="box-content__new">
		<div id="myTabContent" class="tab-content" style="overflow: visible;">
			<div id="prfContent-p" style="width: 95%; height: 100px;line-height:50px;">
				<table style="width: 100%;margin-top:10px;" class="form">
					<tr>
						<td>控制台名称</td>
						<td class="input-prepend input-append">
							<input type="text" name="cname" id="cname" value="">
							<span id="alertInfo" style="color:red;"></span>
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
			<div class="form-actions" align="center" style="margin:5px 0px 0px 5px;">
				<button class="btn btn-primary" onclick="Widget.checkConsoleForm()">保存</button>
				<button class="btn" onclick="parent.window.doCancle()">取消</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
$(function(){
	$("table input,table select").css({'max-width':'130px'});
});
</script>
