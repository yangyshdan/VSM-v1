<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@ taglib uri="/tags/cos-cstatus" prefix="cs" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/computer.js"></script> 
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){Highcharts.setOptions({global: {useUTC: false}});});
//var arr = ${arr};
$(function(){  //加载容量柱形图
//	var storageName = new Array(arr.length);
//	$.each(arr,function(i){
//		storageName[i] = this.name;
//	})
//	var categories = ${categories};
//	Storage.capacityScript(storageName,categories);
	doListRefresh2();
//	Computer.drawTopn(${cputopJson},"cpuprfChart","cpu busy percentage","%");
//	Computer.drawTopn(${memtopJson},"memprfChart","memory used percentage","%");
//	var jsonVal = ${array};
//	for(var i=0;i<jsonVal.length;i++){
//		Topn.prffield(jsonVal[i]);
//	}
});
function doListRefresh2(){
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPrfField",{isFreshen:1},$("#prfContent"),false,false,false,true,
		function(data){
			var json = eval("("+data+")");
			Public.drawPrfLine("prfContent",json);
			$("#pTitle").html(function(){
				var str="物理机性能  (";
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
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#hiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage",jsonVal,$("#hypervisorContent"));
}

//数据查询
function hypervisorFilter(){
	var startDiskSpace = $("input[name='startDiskSpace']").val();
	var endDiskSpace = $("input[name='endDiskSpace']").val();
	var startRamSize = $("input[name='startRamSize']").val();
	var endRamSize = $("input[name='endRamSize']").val();
	var res = /^\d*$/;
	if(!res.test(startRamSize)||!res.test(endRamSize)){
		bAlert("请输入内存！");
		return false;
	}
	if(endRamSize>0 && startRamSize>=endRamSize){
		bAlert("请输入有效的内存大小范围！");
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
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage",jsonVal,$("#hypervisorContent"));
}

function clearData(){
	$("button[type='reset']").click();
}
$(clearData);

$(function(){
	$("#storageTable td").addClass("rc-td");
});
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
	<!-- 数据列表开始 -->
	<div class="tab-pane active" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					物理机列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportHypervisorConfigData";
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
					                  <label class="col-lg-2 control-label" for="displayName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="displayName" name="displayName" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="ipAddress" style="width:80px">IP地址</label>
					                  <input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 20px;">
					              </div> 
					        
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="RamSize" style="width:80px">内存大小</label>
					                  <input class="form-control" id="startRamSize" name="startRamSize" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endRamSize" name="endRamSize" type="text" style="width:60px">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startDiskSpace" style="width:80px">磁盘容量</label>
					                    <input class="form-control" id="startDiskSpace" name="startDiskSpace" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endDiskSpace" name="endDiskSpace" type="text" style="width:60px">
					              </div>					   
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button type="button" class="btn btn-primary" onclick="hypervisorFilter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				  
					<div id="hypervisorContent" style="text-align: center;overflow-y: hidden;">
					<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									IP地址
								</th>
								<th>
									虚拟机个数
								</th>
								<th>
									处理器总数
								</th>
								<th>
									未分配CPU
								</th>
								<th>
									内存(MB)
								</th>
								<th>
									未分配内存(MB)
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
								<c:when test="${not empty hypervisorPage.data}">
									<c:forEach var="item" items="${hypervisorPage.data}" varStatus="status">
												<tr>
													<td>
														<a title="${item.display_name}" href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}&computerId=${item.computer_id}">${item.display_name}</a>
													</td>
													<td>
														${item.ip_address}
													</td>

													<td>
														<cs:isZeroAndNull value="${item.vcount}"></cs:isZeroAndNull>
													</td>
													<td>
														<cs:isZeroAndNull value="${item.processor_count}"></cs:isZeroAndNull>
													</td>
													<td>
														${item.available_cpu}
													</td>
													<td>
														<cs:isZeroAndNull value="${item.ram_size}"></cs:isZeroAndNull>
													</td>
													<td>
														<cs:isProgress total="${item.ram_size}" available="${item.available_mem}"/>
													</td>
													<td>
														<cs:isZeroAndNull value="${item.disk_space}"></cs:isZeroAndNull>
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
										<td colspan=10>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
					</tbody>
					</table>
					<div id="hypervisorListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty hypervisorPage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#hypervisorListpageNub").getLinkStr({pagecount:"${hypervisorPage.totalPages}",curpage:"${hypervisorPage.currentPage}",numPerPage:"${hypervisorPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage&"+param,divId:'hypervisorContent'});
						</script>
					</c:if>
					<c:if test="${empty hypervisorPage.data}">
						<script>
							$("#exportCSV").unbind();
							$("#exportCSV").attr("href","javascript:void(0);");
							$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
						</script>
					</c:if>
				</div>
				
			</div>
		</div>
	</div>
	</div>
	<!-- 数据列表结束 -->
<!-- 性能开始 -->
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
	<!--  
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-content" style="min-height:250px;">
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
						<div class="box-content" style="background-color:#ffffff">
							<div id="${item.id}" style="width: 45%; height: 250px;"></div>
							<div style="clear: both;"></div>
						</div>
					</div>
				<c:if test="${(state.index+1)%2==0}">
				</div>
				</c:if>
			</c:forEach>
			</div>
			-->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					物理机性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf2()" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			 
				<div id="prfContent" style="width:95%;min-height:385px;"></div> 
			</div>
		</div>
	</div>
	</div>
	<!-- 性能结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>