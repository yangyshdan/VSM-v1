<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>VSM虚拟化资源管理系统</title>
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
<link href='${path }/resource/css/jquery.iphone.toggle.css' rel='stylesheet'>
<link href='${path }/resource/css/opa-icons.css' rel='stylesheet'>
<link href='${path }/resource/css/uploadify.css' rel='stylesheet'>
<script src="${path }/resource/js/jquery-1.7.2.min.js"></script>
<link href='${path }/resource/css/bootstrap-multiselect.css' rel='stylesheet'>
<script src="${path}/resource/js/bootstrap-multiselect.js"></script>
<link href='${path }/resource/css/bootstrap-switch.css' rel='stylesheet'>
	<!-- jQuery UI -->
<script src="${path}/resource/js/jquery-ui-1.8.21.custom.min.js"></script>
<script src="${path}/resource/js/project/window_apply.js"></script>

</head>
<body>
	<!-- topbar starts -->
	 <div class="navbar navbar-fixed-top">
      <div class="container">
        <a href="${path }/servlet/index/Index" class="navbar-brand" style="padding-top:5px;padding-bottom:5px;max-width:30%"> <img height="40px" src="${path }/resource/img/images/sr.png" /> <b>VSM虚拟化资源管理系统</b></a>
        <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <div class="nav-collapse collapse" id="navbar-main">
          <ul class="nav navbar-nav">
            
            <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="download">主机<span class="caret"></span></a>
               <ul class="dropdown-menu" aria-labelledby="download">
                <li><a tabindex="-1" href="${path }/servlet/hypervisor/HypervisorAction?func=HypervisorPage">物理机</a></li>
                <li><a tabindex="-1" href="${path }/servlet/virtual/VirtualAction?func=VirtualPage">虚拟机</a></li>
                <li><a tabindex="-1" href="${path }/servlet/apps/AppsAction">应用</a></li>
              </ul>
            </li>
            <li class="dropdown">
              <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="download">网络<span class="caret"></span></a>
              <ul class="dropdown-menu" aria-labelledby="download">
                <li><a tabindex="-1" href="${path }/servlet/switchs/SwitchAction?func=SwitchPage">交换机</a></li>
                <li><a tabindex="-1" href="${path }/servlet/fabric/FabricAction?func=FabricPage">光纤</a></li>
                <li><a tabindex="-1" href="${path }/servlet/zset/ZsetAction?func=ZsetPage">区域集</a></li>
                <li><a tabindex="-1" href="${path }/servlet/zone/ZoneAction?func=ZonePage">区域</a></li>
              </ul>
            </li>
            <li class="dropdown">
              <a  href="${path }/servlet/storage/StorageAction?func=StoragePage">存储系统</a>
            </li>
            <li class="dropdown">
              <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="alertDown">事件预警<span class="caret"></span></a>
               <ul class="dropdown-menu" aria-labelledby="alertDown">
                <li><a tabindex="-1" href="${path }/servlet/alert/DeviceAlertAction">事件告警</a></li>
                <li><a tabindex="-1" href="${path }/servlet/alert/AlertRuleAction">阀值告警规则</a></li>
              </ul>
            </li>
            <li>
              <a href="${path}/servlet/topn/TopnAction?func=TopnPage">TopN</a>
            </li>
      <!-- <li>
              <a href="${path}/servlet/index/Index?func=Layout">存储服务目录</a>
            </li>
             -->      
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
              <a class="dropdown-toggle" data-toggle="dropdown" href="#" id="themes">admin<span class="caret"></span></a>
              <ul class="dropdown-menu" aria-labelledby="themes">
                <li></li>
                <li class="divider"></li>
                <li><a tabindex="-1" href="${path }/servlet/Login?func=LogOut">登出</a></li>
              </ul>
            </li>
            <li class="dropdown">
              <a  class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);" id="alertDown">设置<span class="caret"></span></a>
              <ul class="dropdown-menu" aria-labelledby="alertDown">
		       	<!-- <li><a tabindex="-1" href="${path}/servlet/chart/ChartAction?func=ModelPage">工作台设置</a></li> -->
                <li><a tabindex="-1" href="${path}/servlet/usercon/UserConAction">Agent设置</a></li>
                <li><a tabindex="-1" href="javascript:MM_openwin3('系统设置','${path}/servlet/dataconfig/DataconfigAction',500,260,0)">系统设置</a></li>
                <li><a tabindex="-1" href="${path}/servlet/usercon/AccountMgrAction">用户设置</a></li>
              </ul>
            </li>
          </ul>

        </div>
      </div>
    </div>
	<!-- topbar ends -->
	<div class="container-fluid">
		<div class="row-fluid">