<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<a  href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPage"  class="well span3 top-block" style="color: black;">
	<img class="assetsImg"  alt="host" src="${path}/resource/img/project/host.png">
	<table class="assetsTable">
		<tr align="left"><td><i class="icon icon-darkgray icon-info"></i><strong>  Info:</strong> <span id="hostLog-info">${logCount.hostLog.info}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-alert"></i><strong>  Warning:</strong> <span id="hostLog-warning">${logCount.hostLog.warning}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-cross"></i><strong>  Critical:</strong> <span id="hostLog-error">${logCount.hostLog.error}</span></td></tr>
	</table>
	<span id="hostAssets" class="notification">服务器</span>
</a>
<a  href="${path}/servlet/switchs/SwitchAction?func=SwitchPage"  class="well span3 top-block" style="color: black;">
	<img class="assetsImg"  alt="switch" src="${path}/resource/img/project/switch.png">
	<table class="assetsTable">
		<tr align="left"><td><i class="icon icon-darkgray icon-info"></i><strong>  Info:</strong> <span id="switchLog-info">${logCount.switchLog.info}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-alert"></i><strong>  Warning:</strong> <span id="switchLog-warning">${logCount.switchLog.warning}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-cross"></i><strong>  Critical:</strong> <span id="switchLog-error">${logCount.switchLog.error}</span></td></tr>
	</table>
	<span id="switchAssets" class="notification">交换机</span>
</a>
<a  href="#"  class="well span3 top-block" style="color: black;">
	<img class="assetsImg"  alt="fabric" src="${path}/resource/img/project/fabricbase.png">
	<table class="assetsTable">
		<tr align="left"><td><i class="icon icon-darkgray icon-info"></i><strong>  Info:</strong> <span id="fabricLog-info">${logCount.fabricLog.info}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-alert"></i><strong>  Warning:</strong> <span id="fabricLog-warning">${logCount.fabricLog.warning}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-cross"></i><strong>  Critical:</strong> <span id="fabricLog-error">${logCount.fabricLog.error}</span></td></tr>
	</table>
	<span id="fabricAssets" class="notification">光纤</span>
</a>
<a  href="${path}/servlet/storage/StorageAction?func=StoragePage"  class="well span3 top-block" style="color: black;">
	<img class="assetsImg" alt="storage" src="${path}/resource/img/project/StorageSystemBase.png">
	<table class="assetsTable">
		<tr align="left"><td><i class="icon icon-darkgray icon-info"></i><strong>  Info:</strong> <span id="storageLog-info">${logCount.storageLog.info}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-alert"></i><strong>  Warning:</strong> <span id="storageLog-warning">${logCount.storageLog.warning}</span></td></tr>
		<tr align="left"><td><i class="icon icon-color icon-cross"></i><strong>  Critical:</strong> <span id="storageLog-error">${logCount.storageLog.error}</span></td></tr>
	</table>
	<span id="storageAssets" class="notification">存储系统</span>
</a>
<script type="text/javascript">
$(function(){
	Timer['${item.fmodelid}_${item.fid}'] = setInterval('Widget.refresh("logContent_${item.fmodelid}_${item.fid}")','${item.frefresh * 60 * 1000}');
});
</script>
