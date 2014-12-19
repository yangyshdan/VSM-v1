<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<%@ taglib uri="/tags/jstl-function" prefix="fn" %>
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path }/resource/js/project/topn.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
$(function(){
	var jsonVal = ${array};
	for(var i=0;i<jsonVal.length;i++){
		Topn.prffield(jsonVal[i]);
	}
});
</script>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>
<div id="content">
	<div>
		<div class="box-content" style="width: 100%; margin:0px; padding:0px;float: left;">
			<ul class="breadcrumb">
				<li>
					<span class="divider">设备:</span>
					<c:choose>
						<c:when test="${type=='emc'}">
							<c:forEach items="${devList}" var="item" varStatus="state">
								<a href="${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=${item.ele_id}">${item.ele_name}</a>&nbsp;,
							</c:forEach>
						</c:when>
						<c:when test="${type=='switch'}">
							<c:forEach items="${devList}" var="item" varStatus="state">
								<a href="${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=${item.ele_id}">${item.ele_name}</a>&nbsp;,
							</c:forEach>
						</c:when>
						<c:when test="${type=='svc' || type=='ds' || type=='bsp'}">
							<c:forEach items="${devList}" var="item" varStatus="state">
								<a href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${item.ele_id}">${item.ele_name}</a>&nbsp;,
							</c:forEach>
						</c:when>
						<c:when test="${type=='host'}">
							<c:forEach items="${devList}" var="item" varStatus="state">
								<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId==${item.ele_id}">${item.ele_name}</a>&nbsp;,
							</c:forEach>
						</c:when>
						<c:when test="${type=='application'}">
							<c:forEach items="${devList}" var="item" varStatus="state">
								${item.ele_name }&nbsp;,
							</c:forEach>
						</c:when>
					</c:choose>
					<span class="divider"> &gt;</span>
				</li>
				<li>
					<span class="divider">组件:</span>
					<span class="divider">${topnInfo.fname}</span>
				</li>
			</ul>
		</div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Topn.settingPrf('${topnInfo.tid}')" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="Topn.doListRefresh(${topnInfo.tid})" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
				</div>
			</div>
			<div class="box-content" style="min-height:300px;">
			<c:forEach items="${array}" var="item" varStatus="state">
				<c:if test="${state.index%2==0}">
				<div class="row-fluid">
				</c:if>
					<c:if test="${fn:length(array)==1}">
					<div class="box span12">
					</c:if>
					<c:if test="${fn:length(array) > 1}">
					<div class="box span6">
					</c:if>
						<div class="box-header well" data-original-title>
							<h2>
								${item.ftitle}
							</h2>
							<div class="box-icon">
								<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
							</div>
						</div>
						<div class="box-content" style="background-color:#ffffff">
							<div id="${item.id}" style="width: 100%; height: 300px;"></div>
							<div style="clear: both;"></div>
						</div>
					</div>
				<c:if test="${(state.index+1)%2==0}">
				</div>
				</c:if>
			</c:forEach>
			</div>
		</div>
		<!--/span-->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>