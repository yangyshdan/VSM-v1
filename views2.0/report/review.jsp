<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/jquery.ztree.core-3.5.min.js"></script>
<script src="${path }/resource/js/jquery.ztree.excheck-3.5.min.js"></script>
<script src="${path }/resource/js/jquery.ztree.exedit-3.5.min.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" href="${path}/resource/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript">
var setting = {view:{showIcon:false}};
$(document).ready(function(){
	var zNodes = ${cZnode};
	var zNodes2 = ${pZnode};
	var zNodes3 = ${tZnode};
	var zNodes4 = ${aZnode};
	$.fn.zTree.init($("#configZtree"), setting, zNodes);
	$.fn.zTree.init($("#perfZtree"), setting, zNodes2);
	$.fn.zTree.init($("#topnZtree"), setting, zNodes3);
	$.fn.zTree.init($("#alertZtree"), setting, zNodes4);
});
</script>
<style>
    .yuan{
    	border:1px #F9F9F9 solid;
  		-moz-border-radius: 5px;      /* Gecko browsers */
   	 	-webkit-border-radius: 5px;   /* Webkit browsers */
    	border-radius:5px;            /* W3C syntax */
    }
</style>
<link rel="shortcut icon" href="${path }/resource/img/images/favi.png">
<div id="content">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-content">
				<div style="width:80%;margin:0px auto;background-color:#f7f7f7;padding:3px;">
					<div style="width:100%;;margin:0px auto;" align="center">
						<img src="${path}/${logoImg}" style="width:100px;height:100px;" class="yuan" />
						<h2 style="font-size: 20px;">
							${reportName}
						</h2>
						<strong style="">${startTime} ~ ${endTime}</strong>
					</div>
					<div style="width:100%">
						<fieldset>
							<legend>配置信息</legend>
							<ul id="configZtree" class="ztree yuan" style="width:95%;height:300px;background-color:#D9EDF7;overflow-y:scroll;overflow-x:auto;"></ul>
						</fieldset>
						<fieldset>
							<legend>性能信息</legend>
							<ul id="perfZtree" class="ztree yuan" style="width:95%;height:300px;background-color:#D9EDF7;overflow-y:scroll;overflow-x:auto;"></ul>
						</fieldset>
						<fieldset>
							<legend>TopN指标</legend>
							<ul id="topnZtree" class="ztree yuan" style="width:95%;height:300px;background-color:#D9EDF7;overflow-y:scroll;overflow-x:auto;"></ul>
						</fieldset>
						<fieldset>
							<legend>告警信息</legend>
							<ul id="alertZtree" class="ztree yuan" style="width:95%;height:300px;background-color:#D9EDF7;overflow-y:scroll;overflow-x:auto;"></ul>
						</fieldset>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>