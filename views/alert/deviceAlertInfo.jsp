<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path}/resource/js/project/deviceAlert.js"></script>
<div class="row-fluid sortable ui-sortable">
	<div class="box-content">
		<form class="form-horizontal">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="ffirsttime">
						首次发生时间
					</label>
					<div class="controls" style="margin-right: 80px;line-height:27px;">
						${dAlert.ffirsttime}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="flasttime">
						最后发生时间
					</label>
					<div class="controls" style="margin-right: 80px;line-height:27px;">
						${dAlert.flasttime}
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="flogtype">
						类型
					</label>
					<div class="controls" style="line-height:27px;">
						<c:choose>
							<c:when test="${dAlert.flogtype == 3}">HMC告警</c:when>
							<c:when test="${dAlert.flogtype == 2}">阀值告警</c:when>
							<c:when test="${dAlert.flogtype == 1}">TPC告警</c:when>
							<c:when test="${dAlert.flogtype == 0}">系统告警</c:when>
						</c:choose>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="fcount">
						重复次数
					</label>
					<div class="controls" style="line-height:27px;">
						${dAlert.fcount}
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="fstate">
						状态
					</label>
					<div class="controls" style="line-height:27px;">
						<c:choose>
							<c:when test="${dAlert.fstate == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
							<c:when test="${dAlert.fstate == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
							<c:when test="${dAlert.fstate == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
							<c:when test="${dAlert.fstate == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
						</c:choose>
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="flevel">
						级别
					</label>
					<div class="controls">
						<c:choose>
							<c:when test="${dAlert.flevel == 0}"><span class="label">Info</span> </c:when>
							<c:when test="${dAlert.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
							<c:when test="${dAlert.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
						</c:choose>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="ftopname">
						事件源
					</label>
					<div class="controls" style="line-height:27px;">
						${dAlert.ftopname}
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label" for="fdescript">
						描述
					</label>
					<div class="controls" style="line-height:27px;">
						<span class="help-inline">${dAlert.fdescript}</span>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="fdetail">
						消息
					</label>
					<div class="controls" style="line-height:27px;">
						Device Type:${dAlert.fresourcetype}<br/>
						Device Name:${dAlert.fresourcename } <br/>
						${dAlert.fdetail}
					</div>
				</div>
				
				<div class="form-actions">
					<c:if test="${dAlert.fstate!=1}">
						<button type="button" onclick="haveDone('${dAlert.fruleid}_${dAlert.ftopid}_${dAlert.flogtype}','${resourceType}')" class="btn btn-small btn-success">确认事件</button>
					</c:if>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<script type="text/javascript">
function haveDone(id,resourcetype){
	$.ajax({
		type:'POST',
		url:"/vsm/servlet/alert/DeviceAlertAction?func=DisposeAlert",
		data:{fruleid:id,resourcetype:resourcetype},
		success:function(result){
			if(result=="true"){
				if(resourcetype!=""){
					window.parent.doCancle();
					window.parent.DeviceAlert.doFreshen('${dAlert.ftopid}','${dAlert.fresourceid}',resourcetype);
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