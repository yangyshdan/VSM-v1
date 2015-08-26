<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>SR虚拟化资源管理系统</title>
<link rel="shortcut icon" href="${path }/resource/img/images/favi.png">
<link id="bs-css" href="${path }/resource/css/bootstrap-cerulean.css" rel="stylesheet">
	<style type="text/css">
	  body {
		padding-bottom: 40px;
	  }
	  .sidebar-nav {
		padding: 9px 0;
	  }
	</style>

<link href="${path }/resource/css/jquery.resizableColumns.css" rel="stylesheet">
<link href="${path }/resource/css/charisma-app.css" rel="stylesheet">
<link href="${path }/resource/css/jquery-ui-1.8.21.custom.css" rel="stylesheet">
<link href='${path }/resource/css/fullcalendar.css' rel='stylesheet'>
<link href='${path }/resource/css/fullcalendar.print.css' rel='stylesheet'  media='print'>
<link href='${path }/resource/css/chosen.css' rel='stylesheet'>
<link href='${path }/resource/css/uniform.default.css' rel='stylesheet'>
<link href='${path }/resource/css/colorbox.css' rel='stylesheet'>
<link href='${path }/resource/css/jquery.cleditor.css' rel='stylesheet'>
<link href='${path }/resource/css/jquery.noty.css' rel='stylesheet'>
<link href='${path }/resource/css/noty_theme_default.css' rel='stylesheet'>
<link href='${path }/resource/css/elfinder.min.css' rel='stylesheet'>
<link href='${path }/resource/css/elfinder.theme.css' rel='stylesheet'>
<%--<link href='${path }/resource/css/jquery.iphone.toggle.css' rel='stylesheet'>--%>
<link href='${path }/resource/css/opa-icons.css' rel='stylesheet'>
<link href='${path }/resource/css/uploadify.css' rel='stylesheet'>
<script src="${path }/resource/js/jquery-1.7.2.min.js"></script>
<link href='${path }/resource/css/bootstrap-multiselect.css' rel='stylesheet'>
<script src="${path}/resource/js/bootstrap-multiselect.js"></script>
<link href='${path }/resource/css/bootstrap-switch.css' rel='stylesheet'>
	<%-- jQuery UI --%>
<script src="${path}/resource/js/jquery-ui-1.8.21.custom.min.js"></script>
<script src="${path}/resource/js/project/window_apply.js"></script>
<script id="highchartsJS" src="${path}/resource/js/highcharts/highcharts.js"></script>
<script type="text/javascript" src="${path}/resource/js/project/users.js"></script>
<script type="text/javascript">
Date.prototype.Format = function(fmt) {
	  var _h = this.getHours();
	  var o = { 
		"M+" : this.getMonth() + 1,                 //月份 
		"d+" : this.getDate(),                    //日 
		"H+" : _h,
		"h+" : _h - 12 > 0? _h - 12 : _h,                   //小时 
		"m+" : this.getMinutes(),                 //分 
		"s+" : this.getSeconds(),                 //秒 
		"q+" : Math.floor((this.getMonth()+3)/3), //季度 
		"S"  : this.getMilliseconds()             //毫秒 
	  }; 
	  if(/(y+)/.test(fmt)) 
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	  for(var k in o){
		if(new RegExp("("+ k +")").test(fmt)){
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
		}
	  }
	  return fmt; 
};

function goToEventDetailPage(ftopid, ftoptype, fresourceid){
	var ftt = $.trim(ftoptype).toLowerCase();
	if(ftt != undefined && ftt != null){
		if(ftt == "tpc"){
			bAlert("选择的是TPC，没有事件。");
			return false;
		}
	}
	$.ajax({
		url: "${path}/servlet/alert/DeviceAlertAction?func=GoToEventDetailPage",
		data: {ftopid: ftopid, ftoptype: ftt, fresourceid: fresourceid},
		type: "post",
		dataType: "json",
		success:function(jsonData){
			if(jsonData.success){
				window.location.href = "${path}/" + jsonData.value;
			}
			else {
				bAlert(jsonData.msg);
			}
		}
	});
}

</script>

</head>
<body>
	<!-- topbar starts -->
	 <div class="navbar navbar-fixed-top">
      <div class="container">
        <a href="${path }/servlet/index/Index" class="navbar-brand" style="padding-top:5px;padding-bottom:5px;max-width:30%"> <img height="40px" src="${path }/resource/img/images/sr.png" /> <b>虚拟化资源管理系统</b></a>
        <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <div class="nav-collapse collapse" id="navbar-main">
          <ul class="nav navbar-nav">
          	  	<li class="dropdown">
		            <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="download">服务器<span class="caret"></span></a>
		            <ul class="dropdown-menu" aria-labelledby="download">
	            		<li><a tabindex="-1" href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPage">物理机</a></li>
	            		<li><a tabindex="-1" href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatPage">Hypervisor</a></li>
	            		<li><a tabindex="-1" href="${path}/servlet/virtual/VirtualAction?func=VirtualPage">虚拟机</a></li>
		           </ul>
		           </li>
		          	<li class="dropdown">
		             <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="download">SAN网络<span class="caret"></span></a>
		             <ul class="dropdown-menu" aria-labelledby="download">
	             		<li><a tabindex="-1" href="${path}/servlet/switchs/SwitchAction?func=SwitchPage">SAN交换机</a></li>
	             		<li><a tabindex="-1" href="${path}/servlet/fabric/FabricAction?func=FabricPage">Fabric网络</a></li>
	             		<li><a tabindex="-1" href="${path}/servlet/zset/ZsetAction?func=ZsetPage">ZoneSet</a></li>
	             		<li><a tabindex="-1" href="${path}/servlet/zone/ZoneAction?func=ZonePage">Zone</a></li>
		             </ul>
		           </li>	           
       
       		<!-- <li class="dropdown">
              <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="storage" >存储<span class="caret"></span></a>
              <ul class="dropdown-menu" aria-labelledby="download">
                <li><a tabindex="-1" href="${path }/servlet/storage/StorageAction?func=StoragePage">存储系统</a></li>
                <li><a tabindex="-1" href="${path }/servlet/library/LibraryAction?func=LibraryPage">磁带库</a></li>
              </ul>
            </li>
             -->
             <li><a href="${path }/servlet/storage/StorageAction?func=StoragePage">存储系统</a></li>
          	 	<li class="dropdown">
	              <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="alertDown">事件预警<span class="caret"></span></a>
	               <ul class="dropdown-menu" aria-labelledby="alertDown">
	               		<li><a tabindex="-1" href="${path }/servlet/alert/DeviceAlertAction">事件告警</a></li>
	          	  		<li><a tabindex="-1" href="${path }/servlet/alert/AlertRuleAction">阀值告警规则</a></li>
	              </ul>
	            </li>
            <!--<li><a href="${path}/servlet/topn/TopnAction?func=TopnPage">TopN</a></li> -->
            	<li class="dropdown">
	            	<a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="download">应用拓扑<span class="caret"></span></a>
	               <ul class="dropdown-menu" aria-labelledby="download">
		            		<li><a href="${path}/servlet/topo/TopoAction?func=HuimingTopo">拓扑图</a></li>
		          	  		<li><a tabindex="-1" href="${path }/servlet/apps/AppsAction">应用列表</a></li>
		           </ul>
		         </li>
            
      		<!-- <li>HuimingTopo<a href="${path}/servlet/index/Index?func=Layout">存储服务目录</a> </li> -->
      			<li class="dropdown">
	              <a  class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="alertDown">报表<span class="caret"></span></a>
	              <ul class="dropdown-menu" aria-labelledby="alertDown">
	              		<li><a tabindex="-1" href="${path}/servlet/report/ReportAction?func=ReportPage">报表列表</a></li>
	              		<li><a tabindex="-1" href="${path}/servlet/report/ReportTemplate">报表模板</a></li>
	              </ul>
	            </li>
          </ul>

          <ul class="nav navbar-nav pull-right">
            <li class="dropdown">
              <a class="dropdown-toggle" data-toggle="dropdown" href="#" id="themes">${clientName}<span class="caret"></span></a>
              <ul class="dropdown-menu" aria-labelledby="themes"><%--
              	<li><a tabindex="-1" href="javascript:void(0);" onclick="Header.showUserInfoDlg('${clientId}');">登录用户信息</a></li> --%>
                <li><a tabindex="-1" href="${path}/servlet/Login?func=LogOut">登出</a></li>
              </ul>
            </li>
            <c:if test="${not empty auth52['']}"></c:if>
            <c:if test="${not empty auth52['m_settings_30'] or (not empty clientType and clientType=='super')}">
            	<li class="dropdown">
	              <a  class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="alertDown">设置<span class="caret"></span></a>
	              <ul class="dropdown-menu" aria-labelledby="alertDown">
			        <!-- <li><a tabindex="-1" href="${path}/servlet/chart/ChartAction?func=ModelPage">工作台设置</a></li> -->
			        	 <c:if test="${not empty auth52['m_sys_setting_36'] or (not empty clientType and clientType=='super')}">
			        	<li><a tabindex="-1" href="${path}/servlet/usercon/UserConAction">系统设置</a></li>
			        	</c:if>
			        	 <c:if test="${not empty auth52['m_user_mgnt_37'] or (not empty clientType and clientType=='super')}">
            			<li><a tabindex="-1" href="${path}/servlet/user/UserAction">用户管理</a></li>
            			</c:if>
            			 <c:if test="${not empty auth52['m_role_mgnt_38'] or (not empty clientType and clientType=='super')}">
            			<li><a tabindex="-1" href="${path}/servlet/roles/RolesAction">角色管理</a></li>
            			</c:if>
	              </ul>
	            </li>
            </c:if>
          </ul>
        </div>
      </div>
    </div>
	<!-- topbar ends -->
	<div class="container-fluid">
		<div class="row-fluid">