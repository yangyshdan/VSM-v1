<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/project/deviceAlert.js"></script>
<style>
	.form-horizontal .control-group {
		margin-bottom: 1px;
	}
</style>
<div class="row-fluid sortable ui-sortable" style="margin-top:-25px;">
	<div class="box-content">
		<form class="form-horizontal">
			<fieldset>
				<%-- 
				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						首次发生时间
					</label>
					<div class="controls" style="line-height:25px;">
						${dAlert.ffirsttime}
					</div>
				</div> 
				--%>
				
				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						最近发生时间
					</label>
					<div class="controls" style="line-height:25px;">
						${dAlert.flasttime}
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						事件类型
					</label>
					<div class="controls" style="line-height:25px;">
						<c:choose>
							<c:when test="${dAlert.flogtype == 3}">硬件告警</c:when>
							<c:when test="${dAlert.flogtype == 2}">阀值告警</c:when>
							<c:when test="${dAlert.flogtype == 1}">TPC告警</c:when>
							<c:when test="${dAlert.flogtype == 0}">系统告警</c:when>
						</c:choose>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						告警级别
					</label>
					<div class="controls">
						<c:choose>
							<c:when test="${dAlert.flevel == 0}"><span class="label">Info</span> </c:when>
							<c:when test="${dAlert.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
							<c:when test="${dAlert.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
							<c:otherwise><span class="label">Unknow</span></c:otherwise>
						</c:choose>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						重复次数
					</label>
					<div class="controls" style="line-height:25px;">
						${dAlert.fcount}
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						事件状态
					</label>
					<div class="controls" style="line-height:25px;">
						<c:choose>
							<c:when test="${dAlert.fstate == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
							<c:when test="${dAlert.fstate == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
							<c:when test="${dAlert.fstate == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
							<c:when test="${dAlert.fstate == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
						</c:choose>
					</div>
				</div>
				<c:choose>
					<c:when test="${dAlert.fstate == 1}">
						<div class="control-group">
							<label class="control-label" style="font-weight:bold;">
								确认时间
							</label>
							<div class="controls" style="line-height:25px;">
								${dAlert.fconfirmtime}
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label" style="font-weight:bold;">
								确认人
							</label>
							<div class="controls" style="line-height:25px;">
								${dAlert.fconfirmuser}
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label" style="font-weight:bold;">
								确认备注
							</label>
							<div class="controls" style="line-height:25px;">
								${dAlert.fremark}
							</div>
						</div>
					</c:when>
				</c:choose>

				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						事件源
					</label>
					<div class="controls" style="line-height:25px;">
						${dAlert.fresourcename}
					</div>
				</div>
				
				<%-- 
				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						描述
					</label>
					<div class="controls" style="line-height:25px;">
						<span class="help-inline">${dAlert.fdescript}</span>
					</div>
				</div> 
				--%>

				<div class="control-group">
					<label class="control-label" style="font-weight:bold;">
						详细消息
					</label>
					<div class="controls" style="line-height:25px;">
						Device Type : ${dAlert.fresourcetype}<br/>
						Device Name : ${dAlert.fresourcename}<br/>
						${dAlert.fdetail}
					</div>
				</div>
				
				<div class="control-group">
					<c:if test="${dAlert.fstate != 1}">
						<label class="control-label" for="remark" style="font-weight:bold;">
							填写备注
						</label>
						<div class="controls" style="line-height:25px;">
							<input type="text" id="remark" value="" style="width:250px;">
						</div>
					</c:if>
				</div>
				
				<div class="form-actions">
					<c:if test="${dAlert.fstate != 1}">
						<button style="margin-left:20px;" type="button" onclick="haveDone('${dAlert.fruleid}_${dAlert.ftopid}_${dAlert.flogtype}')" class="btn btn-small btn-success">确认事件</button>
					</c:if>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script type="text/javascript">
function haveDone(id){
	var resourceType = "${resourceType}";
	$.ajax({
		type:'POST',
		url:getRootPath()+"/servlet/alert/DeviceAlertAction?func=DisposeAlert",
		data:{fruleid:id,remark:$("#remark").val()},
		success:function(result){
			if(result == "true"){
				if(resourceType != null && resourceType != ""){
					window.parent.doCancle();
					window.parent.DeviceAlert.doFreshen('${dAlert.ftopid}','${dAlert.fresourceid}',resourceType);
				}else{
					window.parent.doCancle();
					window.parent.DeviceAlert.dataFilter();
				}
			}else{
				window.parent.bAlert("确认事件失败，请稍候再试！");
			}
		}
	});
}
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>