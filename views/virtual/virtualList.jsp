<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/computer.js"></script> 
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
var hypervisorId="${hypervisorId}";
$(function () {
//	var args = ${array};
//	var categore = ${names};
//	Volume.capecity(args,categore);
	doListRefresh2();
	//var jsonVal = ${prfData};
	//Public.drawPrfLine("prfContent",jsonVal);
	
//	Computer.drawTopn(${cputopJson},"cpuprfChart","cpu busy percentage","%");
//	Computer.drawTopn(${memtopJson},"memprfChart","memory used percentage","%");
});
	
//数据查询
function virtualFilter(){
	var endNumCpu = $("input[name='endNumCpu']").val();
	var startMemory = $("input[name='startMemory']").val();
	var endMemory = $("input[name='endMemory']").val();
	var startDiskSpace = $("input[name='startDiskSpace']").val();
	var endDiskSpace = $("input[name='endDiskSpace']").val();
	var res = /^\d*$/;
	
	if(!res.test(startMemory)||!res.test(endMemory)){
		bAlert("请输入总内存容量！");
		return false;
	}
	
	if(endMemory>0 && startMemory>=endMemory){
		bAlert("请输入有效的内存容量范围！");
		return false;
	}
	if(!res.test(startDiskSpace)||!res.test(endDiskSpace)){
		bAlert("请输入磁盘容量！");
		return false;
	}
	if(endDiskSpace>0 && startDiskSpace>=endDiskSpace){
		bAlert("请输入有效的磁盘容量范围！");
		return false;
	}
	
	var jsonVal = $("#conditionForm").getValue();
	
	loadData("${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage",jsonVal,$("#virtualContent"));
}
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#virtualHiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage",jsonVal,$("#virtualContent"));
}
//清除
function clearData(){
	$("button[type='reset']").click();
}
$(clearData);

function doListRefresh2(){
	loadData("${path}/servlet/virtual/VirtualAction?func=VirtualPrfField",{isFreshen:1,hypervisorId:hypervisorId,level:2},$("#prfContent"),false,false,false,true,
		function(data){
			var json = eval("("+data+")");
			Public.drawPrfLine("prfContent",json);
			$("#pTitle").html(function(){
				var str="虚拟机性能  (";
				$.each(json.kpiInfo,function(i){
					str+=json.kpiInfo[i].ftitle;
					if(i<json.kpiInfo.length-1){
						str+=",";
					}
				});
				if(str.length>100){
					str = str.substring(0,100)+'...';
				}
				str+=")";
				return str;
			});
		});
}
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
<div id="content">
<!--  
	<div class="well">
		<img src="${path}/resource/img/project/volume.png"
			style="width: 10%; float: left; padding-top:20px;" />
		<div id="container"
			style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	-->
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">设备列表</a>
		</li>
		<li class="">
			<a href="#prfTab">性能曲线</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 虚拟机列表开始 -->
	<div class="tab-pane active" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					虚拟机列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/virtual/VirtualAction?func=exportVirtualConfigData";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
				<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
				<div id="conAlert" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
					<div class="arrow"></div>
					<div class="popover-inner">
						<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlert()">×</a></h3>
						<div class="popover-content" style="padding: 8px;">
					        <form class="form-horizontal" id="conditionForm">
								<fieldset>
								  <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="virtualName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="virtualName" name="virtualName" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startMemory" style="width:80px">总内存 </label>
					                  <input class="form-control" id="startMemory" name="startMemory" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endMemory" name="endMemory" type="text" style="width:60px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startDiskSpace" style="width:80px">磁盘容量</label>
					                    <input class="form-control" id="startDiskSpace" name="startDiskSpace" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endDiskSpace" name="endDiskSpace" type="text" style="width:60px">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button type="button" class="btn btn-primary" onclick="virtualFilter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div class="tab-pane active" id="virtualContent" style="text-align: center;overflow-y: hidden;">
					<table class="table table-bordered table-striped table-condensed" id="conTable" style="word-break:break-all">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									所属物理机
								</th>
								<th>
									IP地址
								</th>
								<th>
									CPU逻辑个数
								</th>
								<th>
									CPU物理个数
								</th>
								<th>
									总内存(MB)
								</th>
								<th>
									磁盘总容量(MB)
								</th>
								<th>
									磁盘剩余容量(MB)
								</th>
								<th>
									更新时间
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty virtualPage.data}">
									<c:forEach var="item" items="${virtualPage.data}" varStatus="status">
										<tr>
											<td>
												<a title="${item.display_name}" href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=${item.hypervisor_id}&vmId=${item.vm_id}&computerId=${item.computer_id}">${item.display_name}</a>
											</td>
											<td>
												<a title="${item.host_name}" href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.host_name}</a>
											</td>
											<td>
												${item.ip_address}
											</td>
											<td>
												${item.assigned_cpu_number}
											</td>	
											<td>
												${item.assigned_cpu_processunit}
											</td>						
											<td>
												${item.total_memory}
											</td>	
											<td>
												${item.disk_space}
											</td>									
											<td>
												<cs:isProgress total="${item.disk_space}" available="${item.disk_available_space}"/>
											</td>							
											<td>
												${item.update_timestamp}
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=9>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="virtualListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty virtualPage.data}">
						<script>
							$("#virtualListpageNub").getLinkStr({pagecount:"${virtualPage.totalPages}",curpage:"${virtualPage.currentPage}",numPerPage:"${virtualPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage",divId:'virtualContent'});
						</script>
					</c:if>
					<c:if test="${empty virtualPage}">
						<script>
							$("#exportCSV").unbind();
							$("#exportCSV").attr("href","javascript:void(0);");
							$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
						</script>
					</c:if>
					<input type="hidden" id="hiddenName" value="${name}"/>
					<input type="hidden" id="hiddenstartMemory" value="${startMemory}"/>
					<input type="hidden" id="hiddenendMemory" value="${endMemory}"/>
				</div>
			</div>
		</div>
	</div>
	</div>
	<!-- 虚拟机列表结束 -->
	<!-- 虚拟机性能开始 -->
	<div class="tab-pane" id="prfTab">
	<!--  
	<div class="row-fluid">
		<div class="box span6">
			<div id="cpuprfChart" style="width: 45%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
			<div style="clear: both;"></div>
		</div>
		<div class="box span6">
			<div id="memprfChart" style="width: 45%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
			<div style="clear: both;"></div>
		</div>
	</div>
	-->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf4()" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2()"><i class="icon icon-color icon-refresh" data-rel="tooltip"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div id="prfContent" style="width:95%;min-height:385px;"></div>
			</div>
		</div>
	</div>
	</div>
	<!-- 虚拟机性能开始 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>