<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/computer.js"></script> 
<script src="${path}/resource/js/project/changeColumn.js"></script> 
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${pageContext.request.contextPath}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path}/resource/js/project/util.js"></script>
<script src="${path}/resource/js/project/server.js"></script>

<script type="text/javascript">
var hypervisorId="${hypervisorId}";
function drawVirtualCapacity(serverCapacity){
	Server.capacityScript1({
		id: "container",
   		value: serverCapacity,
   		title: "虚拟机硬盘容量使用情况",
  		getURL: function(ids){
  			return "/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=%s&vmId=%s&computerId=%s"
  			.jFormat(ids.hyp_id, ids.vm_id, ids.comp_id);
  		}
    });
}
$(function () {
	doListRefresh2();
	$("#conTable").tablesorter();
	changeColumn.initCol();
    drawVirtualCapacity(${serverCapacity});
});
	
//数据查询
function virtualFilter(){
	var startMemory = $("input[name='startMemory']").val();
	var endMemory = $("input[name='endMemory']").val();
	var startDiskSpace = $("input[name='startDiskSpace']").val();
	var endDiskSpace = $("input[name='endDiskSpace']").val();
	var res = /^\d*$/;
	
	if(!res.test(startMemory)||!res.test(endMemory)){
		bAlert("内存总容量只能是整数数值！");
		return false;
	}
	
	if(parseInt(endMemory)>0 && parseInt(startMemory)>=parseInt(endMemory)){
		bAlert("请输入有效的内存容量范围！");
		return false;
	}
	if(!res.test(startDiskSpace)||!res.test(endDiskSpace)){
		bAlert("磁盘容量只能是整数数值！");
		return false;
	}
	if(parseInt(endDiskSpace)>0 && parseInt(startDiskSpace)>=parseInt(endDiskSpace)){
		bAlert("请输入有效的磁盘容量范围！");
		return false;
	}
	var jsonVal={};
	var args=$("#conditionForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage", jsonVal, $("#virtualContent"));
}
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#conditionForm").serializeArray();
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
	var url = "${path}/servlet/virtual/VirtualAction?func=VirtualPrfField";
	loadData(url, {isFreshen:1, hypervisorId:hypervisorId, level:2},
			$("#prfContent"), false, false, false, true,
		function(data){
			var json = eval("("+data+")");
			var $tab = $("#conTable52207");
			$tab.find("thead tr").children().remove();
			$tab.find("tbody").children().remove();
			//$tab.find("thead tr").html(json.thead);
			//$tab.find("tbody").html(json.tbody);
			var $pag = $("#HypervisorInfopageNub");
			var $csv = $("#exportCSV521701");
			$csv.unbind();
			$pag.children().remove();
			$csv.attr("href", "${path}/servlet/virtual/VirtualAction?func=ExportPrefData");
			if(json.totalPages > 0){
				$csv.attr({href: "${path}/servlet/virtual/VirtualAction?func=ExportPrefData&level=2&hypervisorId=" + hypervisorId });
				$pag.getLinkStr({pagecount: json.totalPages, curpage: json.currentPage, 
					numPerPage: json.numPerPage, isShowJump: true,
					exFunc: function(curpage, numPerPage){
						perfPage0237({
							url: url,
							data:{curpage: curpage, numPerPage: numPerPage, tablePage: 0, isFreshen: 1, level:2}, 
							tableSelector: "#conTable52207",
							pagSelector: "#HypervisorInfopageNub",
							csvSelector: "#exportCSV521701",
							exportCSVUrl: exurl
						});
					},
					divId: ""
				});
				$pag.find("ul li a.first").first().trigger("click");
			}
			else {
				$csv.attr({ href: "javascript:void(0);" });
				$csv.bind("click", function(){ bAlert("暂无可导出数据！"); });
			}
			if(json.graphType == 1){
				if(json.series){ 
					Public.drawTopn02("prfContent",
						{
						jsonVal: json.series, 
						getURL: function(point){
									return "/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=" + point.devId + "&vmId=" 
										+ point.eleId + "&computerId=" + point.compId;
								}
						}
					);
				}
			}
			else {
				Public.drawPrfLine("prfContent", json);
				$("#pTitle").html(function(){
					var str="虚拟机性能  (";
					var lastLen = json.kpiInfo.length - 1;
					$.each(json.kpiInfo,function(i){
						str += json.kpiInfo[i].ftitle;
						if(i < lastLen){
							str+=",";
						}
					});
					if(str.length > 100){
						str = str.substring(0,100) + "...";
					}
					str += ")";
					return str;
				});
			}
	});
}

function doListRefresh23(){
	loadData("${path}/servlet/virtual/VirtualAction?func=VirtualPrfField",{isFreshen:1,hypervisorId:hypervisorId,level:2},
		$("#prfContent"),false,false,false,true,
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
				if(str.length > 100){
					str = str.substring(0,100)+ "...";
				}
				str+=")";
				return str;
			});
		});
}

var mouseEvent = function(e){
	this.x = e.pageX;
	this.y = e.pageY;
}
function mouseOffset(e){
	var mouse= new mouseEvent(e);
	var leftPos= mouse.x;
	var topPos =mouse.y;
	return {left:leftPos, top:topPos};
}

window.parent.doListRefresh = function(){
	doListRefresh2();
}

$(function(){ <%--增加查询条件--%>
	$.ajax({
		url: "${path}/servlet/virtual/VirtualAction?func=VirtualSettingPrf2",
		data: { level: 2 },
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			$("#isShowDevName").bootstrapSwitch("setActive", false);
		}
	});
});

</script>

<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">设备列表</a>
		</li>
		<li class="">
			<a href="#prfTab">性能</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 虚拟机列表开始 -->
	<div class="tab-pane active" id="dataTab">
	<div class="box span12">
		<div style="width: 10%; float: left;height:200px; line-height:220px;" >
			<img src="${path}/resource/img/project/host.png"/>
		</div>
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					虚拟机列表
				</h2>
				<div class="box-icon">																		
					<a href="javascript:void(0);" class="btn btn-round" title="选择列" onclick="changeColumn.showCol(this);" data-rel="tooltip"><i class="icon-eye-open"></i></a>
					<%-- 
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					--%>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/virtual/VirtualAction?func=ExportVirtualConfigData";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<!-- 筛选条件开始 -->
			<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
				<form class="form-horizontal" id="conditionForm">
					<fieldset>
						<div class="control-group" style="margin-bottom: 10px;">
							<table class="table-condensed" width="70%" style="margin: 0px auto;">
								<tbody>
									<tr>
										<td>
											<label class="col-lg-2 control-label" for="virtualName" style="width:60px">名称</label>
											<input type="text" class="form-control" id="virtualName" name="virtualName" style="width: 140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="startMemory" style="width:100px">内存大小(GB)</label>
											<input class="form-control" id="startMemory" name="startMemory" type="text" style="width:60px;margin-left: 10px;"> -
											<input class="form-control" id="endMemory" name="endMemory" type="text" style="width:60px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="startDiskSpace" style="width:100px">磁盘容量(GB)</label>
											<input class="form-control" id="startDiskSpace" name="startDiskSpace" type="text" style="width:60px;margin-left: 10px;"> -
											<input class="form-control" id="endDiskSpace" name="endDiskSpace" type="text" style="width:60px">
										</td>
									</tr>
									<tr>
										<td colspan="3" style="text-align:center;">
											<input type="hidden" id="isShowCap" name="isShowCap" value="1"/>
											<button type="button" class="btn btn-primary" onclick="virtualFilter();">查询</button>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<button class="btn" type="reset">重置</button>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</fieldset>
				</form>
			</div>
			<!-- 筛选条件结束 -->
			
			<div class="box-content">
				<%-- 
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
									<input type="hidden" id="isShowCap" name="isShowCap" value="1"/>
									<button type="button" class="btn btn-primary" onclick="virtualFilter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				--%>
				
				<div class="tab-pane active" id="virtualContent">
				
					<table class="table table-bordered table-striped table-condensed colToggle" id="conTable">
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
									逻辑CPU个数
								</th>
								<th>
									物理CPU个数
								</th>
								<th>
									总内存(GB)
								</th>
								<th>
									磁盘容量使用率(%)
								</th>
								<th>
									磁盘总容量(GB)
								</th>
								<th>
									磁盘剩余容量(GB)
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
												<a href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=${item.hypervisor_id}&vmId=${item.vm_id}&computerId=${item.computer_id}">${item.display_name}</a>
											</td>
											<td>
												<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.host_name}</a>
											</td>
											<td>
												${item.ip_address}
											</td>
											<td>
												<fmt:formatNumber var="lCpu" value="${item.assigned_cpu_number}" pattern="#"/>
												<cs:isZeroAndNull value="${lCpu}"></cs:isZeroAndNull>
											</td>	
											<td>
												<fmt:formatNumber var="pCpu" value="${item.assigned_cpu_processunit}" pattern="#"/>
												<cs:isZeroAndNull value="${pCpu}"></cs:isZeroAndNull>
											</td>						
											<td>
												<fmt:formatNumber value="${item.total_memory/1024}" pattern="0.##"/>
											</td>
											<td>
												<cs:isProgress total="${item.disk_space}" available="${item.disk_space-item.disk_available_space}" warning="80" error="95"/>
											</td>
											<td>
												<fmt:formatNumber value="${item.disk_space/1024}" pattern="0.##"/>
											</td>									
											<td>
												<fmt:formatNumber value="${item.disk_available_space/1024}" pattern="0.##"/>
											</td>									
											<td>
												${item.update_timestamp}
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=9>暂无数据！</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="virtualListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty virtualPage.data}">
						<script>
							$("#virtualListpageNub").getLinkStr({pagecount:"${virtualPage.totalPages}",curpage:"${virtualPage.currentPage}",numPerPage:"${virtualPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage&isShowCap=${isShowCap}",divId:'virtualContent'});
						</script>
					</c:if>
					<c:if test="${empty virtualPage}">
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
				<h2 id="pTitle">性能</h2>
				<div class="box-icon">
					<!--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf4()" data-rel="tooltip"><i class="icon-cog"></i></a>-->
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2()"><i class="icon icon-color icon-refresh" data-rel="tooltip"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV521701" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div id="queryPage" class="box-content" style="height:220px;"></div>
			<div class="box-content" >
				<!-- tab切换标签开始 -->
				<ul class="nav nav-tabs" id="myTab">
					<li class="active">
						<a href="#loadcontent2">性能曲线</a>
					</li>
					<li class="">
						<a href="#dataContent2">性能数据</a>
					</li>
				</ul>
				<!-- tab切换标签结束 -->
				<div id="perfChart2" class="tab-content" style="overflow: visible;min-height:200px;">
					<!-- 性能曲线切换页开始 -->
					<div class="tab-pane active" id="loadcontent2">
						<div id="prfContent" style="width:94%;margin:0px;min-height:385px;height:420px;"></div>
					</div>
					<!-- 性能曲线切换页结束 -->
					<!-- 性能数据切换页开始 -->
					<div class="tab-pane" id="dataContent2" style="padding-top:10px;">
						<table class="table table-bordered table-striped table-condensed" id="conTable52207">
							<thead><tr></tr></thead>
							<tbody></tbody>
						</table>
						<div id="HypervisorInfopageNub" class="pagination pagination-centered"></div>
					</div>
					<!-- 性能数据切换页结束 -->
				</div>
			</div>
		</div>
	</div>
	</div>
	<!-- 虚拟机性能开始 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>