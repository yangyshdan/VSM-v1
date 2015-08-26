<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/storage.js"></script>
<script src="${path}/resource/js/project/computer.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script>
<script src="${path}/resource/js/project/topn.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path}/resource/js/project/util.js"></script>
<script src="${path}/resource/js/project/server.js"></script>

<script type="text/javascript">
$(function(){Highcharts.setOptions({global: {useUTC: false}});});

function drawPhysicalCapacity(serverCapacity){
	if(serverCapacity == undefined || serverCapacity == null){  }
	Server.capacityScript1({
		id: "container",
   		value: serverCapacity,
   		title: "物理机硬盘容量使用情况",
  		getURL: function(ids){
  			return "/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&computerId=%s&hypervisorId=%s".jFormat(ids.comp_id, ids.hyp_id);
  		}
    });
};

$(function(){  <%--增加查询条件--%>  //加载容量柱形图
	$.ajax({
		url: "${path}/servlet/hypervisor/HypervisorAction?func=HypervisorSettingPrf2",
		data: { },
		type: "post",
		dataType: "html",
		success: function(result){ 
			$("#queryPage").html(result);
			<%--加载完查询条件页面时才刷新页面--%>
			doListRefresh2();
		}
	});
    drawPhysicalCapacity(${serverCapacity});<%--画柱状图的--%>
    //drawVMCpuMemTop5(${vmCPUBusyTop5}, ${vmMemUsedTop5});
    
	$("#conTable").tablesorter();
	changeColumn.initCol();
});

/**
	opts = {
		url: "",
		data:{curPage: 1, numPerPage: 12, isGraph: 0}, 
		tableSelector: "#conTable52207",
		pagSelector: "#HypervisorInfopageNub",
		csvSelector: "#exportCSV521701",
		exportCSVUrl: ""
	}
 */

function doListRefresh2(){
	var url = "${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPrfField";
	loadData(url, { isFreshen:1, level:1 },
			$("#prfContent"), false, false, false, true,
		function(data){
			var json = eval("("+data+")");
			var $tab = $("#conTable52207");
			$tab.find("thead tr").children().remove();
			$tab.find("tbody").children().remove();
			$tab.find("thead tr").html(json.thead);
			$tab.find("tbody").html(json.tbody);
			var $pag = $("#HypervisorInfopageNub");
			var $csv = $("#exportCSV521701");
			$csv.unbind();
			$pag.children().remove();
			$csv.attr("href","${path}/servlet/hypervisor/HypervisorAction?func=ExportPrefData");
			if(json.totalPages > 0){
				var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportPrefData&level=1";
				$csv.attr({href: exurl});
				$pag.getLinkStr({
					pagecount: json.totalPages, 
					curpage: json.currentPage, 
					numPerPage: json.numPerPage, 
					isShowJump: true,
					exFunc: function(curpage, numPerPage){
						perfPage0237({
							url: url,
							data:{curpage: curpage, numPerPage: numPerPage, tablePage: 0, isFreshen: 1}, 
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
				$csv.attr({href: "javascript:void(0);"});
				$csv.bind("click", function(){bAlert("暂无可导出数据！")});
			}
			if(json.graphType == 1){
				if(json.series){ 
					Public.drawTopn02("prfContent", {jsonVal: json.series, getURL: function(point){
						return "/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId="+ point.devId +"&computerId=" + point.compId
					}}); 
				}
			}
			else {
				Public.drawPrfLine("prfContent", json);
				$("#pTitle").html(function(){
					var str="物理机性能  (";
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

window.parent.doListRefresh = function(){ doListRefresh2(); };

//刷新
function doFreshen(){
	var jsonVal = {};
	var args=$("#hiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage", jsonVal, $("#loadcontent"));
}

//数据查询
function hypervisorFilter(){
	var startDiskSpace = $("input[name='startDiskSpace']").val();
	var endDiskSpace = $("input[name='endDiskSpace']").val();
	var startRamSize = $("input[name='startRamSize']").val();
	var endRamSize = $("input[name='endRamSize']").val();
	var res = /^\d*$/;
	if(!res.test(startRamSize)||!res.test(endRamSize)){
		bAlert("内存大小只能是整数数值！");
		return false;
	}
	if(parseInt(endRamSize)>0 && parseInt(startRamSize)>=parseInt(endRamSize)){
		bAlert("请输入有效的内存大小范围！");
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
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage",jsonVal,$("#loadcontent"));
}

function clearData(){
	$("button[type='reset']").click();
}
$(clearData);

$(function(){
	$("#storageTable td").addClass("rc-td");
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
		<!-- 数据列表开始 -->
		<div class="tab-pane active" id="dataTab">
			<div class="box span12">
				<div style="width: 10%; float: left;height:200px; line-height:220px;" >
					<img src="${path}/resource/img/project/hv.png"/>
				</div>
				<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
				<div style="clear: both;"></div>
			</div>
			<div class="row-fluid">
				<div class="box span10">
					<div class="box-header well">
						<h2>物理机列表</h2>
						<div class="box-icon">
							<a href="javascript:void(0);" class="btn btn-round" title="选择列" onclick="changeColumn.showCol(this);" data-rel="tooltip"><i class="icon-eye-open"></i></a>
							<%-- 
							<a href="javascript:void(0)" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
							--%>
							<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
							<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
							<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							<script>
								var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportHypervisorConfigData";
								$("#exportCSV").attr("href",exurl);
							</script>
						</div>
					</div>
					<!-- 筛选条件开始 -->
					<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
						<form class="form-horizontal" id="conditionForm">
							<fieldset>
								<div class="control-group" style="margin-bottom: 10px;">
									<table class="table-condensed" width="80%" style="margin: 0px auto;">
										<tbody>
											<tr>
												<td>
													<label class="col-lg-2 control-label" for="displayName" style="width:60px">名称</label>
													<input type="text" class="form-control" id="displayName" name="displayName" style="width: 140px;margin-left: 10px;">
												</td>
												<td>
													<label class="col-lg-2 control-label" for="ipAddress" style="width:60px">IP地址</label>
													<input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 10px;">
												</td>
												<td>
													<label class="col-lg-2 control-label" for="RamSize" style="width:100px">内存大小(GB)</label>
													<input class="form-control" id="startRamSize" name="startRamSize" type="text" style="width:60px;margin-left: 10px;"> -
													<input class="form-control" id="endRamSize" name="endRamSize" type="text" style="width:60px">
												</td>
												<td>
													<label class="col-lg-2 control-label" for="startDiskSpace" style="width:100px">磁盘容量(GB)</label>
													<input class="form-control" id="startDiskSpace" name="startDiskSpace" type="text" style="width:60px;margin-left: 10px;"> -
													<input class="form-control" id="endDiskSpace" name="endDiskSpace" type="text" style="width:60px">
												</td>
											</tr>
											<tr>
												<td colspan="4" style="text-align:center;">
													<button type="button" class="btn btn-primary" onclick="hypervisorFilter();">查询</button>
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
							                  <label class="col-lg-2 control-label" for="displayName" style="width:80px">名称</label>
							                  <input type="text" class="form-control" id="displayName" name="displayName" style="width: 140px;margin-left: 20px;">
							              </div>
							              <div class="control-group" style="margin-bottom: 10px;">
							                  <label class="col-lg-2 control-label" for="ipAddress" style="width:80px">IP地址</label>
							                  <input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 20px;">
							              </div> 
							              <!-- 
							              <div class="control-group" style="margin-bottom: 10px;">
							                  <label class="col-lg-2 control-label" for="osVersion" style="width:80px">操作系统</label>
							                  <input type="text" class="form-control" id="osVersion" name="osVersion" style="width: 140px;margin-left: 20px;">
							              </div>
							               -->
							              <div class="control-group" style="margin-bottom: 10px;">
							                  <label class="col-lg-2 control-label" for="RamSize" style="width:80px">内存大小(GB)</label>
							                  <input class="form-control" id="startRamSize" name="startRamSize" type="text" style="width:60px;margin-left: 20px;"> -
											  <input class="form-control" id="endRamSize" name="endRamSize" type="text" style="width:60px">
							              </div>
							              <div class="control-group" style="margin-bottom: 10px;">
							                  <label class="col-lg-2 control-label" for="startDiskSpace" style="width:80px">磁盘容量(GB)</label>
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
						--%>
						<div class="tab-pane active" id="loadcontent" style="text-align: center;overflow: visible;">
							<table id="conTable" class="table table-bordered table-striped table-condensed colToggle"  style="word-break:break-all">
								<thead>
									<tr>
										<th>名称</th>
										<th>IP地址</th>
										<th>操作系统</th>
										<th>Hypervisor</th>
										<th>CPU数量</th>
										<th>内存(GB)</th>
										<th>磁盘容量使用(%)</th>
										<th>磁盘总容量(GB)</th>
										<th>磁盘剩余容量(GB)</th>
										<!-- 
										<th>
											虚拟机数量
										</th>
										 -->
										<th>更新时间</th>
									</tr>
								</thead>
								<tbody>
									<c:choose>
										<c:when test="${not empty hypervisorPage.data}">
											<c:forEach var="item" items="${hypervisorPage.data}" varStatus="status">				
														<tr>
															<td>
																<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}&computerId=${item.computer_id}">${item.display_name}</a>
															</td>
															<td>
																${item.ip_address}
															</td>
															<td>
																${item.os_version}
															</td>
															<td>
																<c:choose>
																	<c:when test="${empty item.vp_id}">N/A</c:when>
																	<c:when test="${not empty item.vp_id}">
																		<a href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatInfo&virtualPlatId=${item.vp_id}&physicalId=${item.hypervisor_id}">${item.vp_name}</a>
																	</c:when>
																</c:choose>
															</td>
															<td>
																<cs:isZeroAndNull value="${item.processor_count}"></cs:isZeroAndNull>
															</td>
															
															<td>
																<fmt:formatNumber value="${item.ram_size/1024}" pattern="0.##"/>
															</td>
															<td>
																<div style="width:100%;">
																	<div style="float:left; width:98%;">
																		<c:choose>
																			<c:when test="${item.percent <= 0.6}">
																				<div style="margin-bottom:0px;" class="progress progress-success progress-striped active">
																			</c:when>
																			<c:when test="${item.percent > 0.6 and item.percent <= 0.85}">
																				<div style="margin-bottom:0px;" class="progress progress-warning progress-striped active">
																			</c:when>
																			<c:otherwise>
																				<div style="margin-bottom:0px;" class="progress progress-danger progress-striped active">
																			</c:otherwise>
																		</c:choose>
																		<div style="width:${item.percent * 100}%" class="bar">
																			<span style="color:black;"><fmt:formatNumber value="${item.percent * 100}" pattern="0.#"/>%</span>
																		</div>
																	</div>
																	<div style="float:right; width:30%;"></div>
																</div>
															</td>
															<td>
																<fmt:formatNumber value="${item.disk_space/1024}" pattern="0.##"/>
															</td>
															<td>
																<fmt:formatNumber value="${item.disk_available_space/1024}" pattern="0.##"/>
															</td>
															<!-- 
															<td>
																<cs:isZeroAndNull value="${item.vcount}"></cs:isZeroAndNull>
															</td>
															 -->
															<td>
																${item.update_timestamp}
															</td>
														</tr>											
												
											</c:forEach>
										</c:when>
										<c:otherwise>
											<tr>
												<td colspan="10">
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
									$("#hypervisorListpageNub").getLinkStr({pagecount:"${hypervisorPage.totalPages}",curpage:"${hypervisorPage.currentPage}",numPerPage:"${hypervisorPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=AjaxHypervisorPage&"+param,divId:'loadcontent'});
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
			<!--  
			<div class="row-fluid" style="clear:both;">
				<div class="box-header well" >
					<h2>性能统计(最近1小时)</h2>
				</div>
				<div style="clear:both;margin-top:-20px;">
					<div class="box span12" style="width:50%;">
						<div class="box-header well">
							<h2>虚拟机 CPU Busy Top5</h2>
						</div>
						<div id="vmCPUBusyTop5" style="height:300px;"></div>
					</div>
					<div class="box span12" style="width:49%;">
						<div class="box-header well">
							<h2>虚拟机 Memory Used Top5</h2>
						</div>
						<div id="vmMemUsedTop5" style="height:300px;"></div>
					</div>
				</div>
			</div>
			-->
			</div>
			
			<!-- 数据列表结束 -->
			<!-- 性能开始 -->
			<div class="tab-pane" id="prfTab"> 
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2 id="pTitle">物理机性能</h2>
						<div class="box-icon">
							<!--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf2()" data-rel="tooltip"><i class="icon-cog"></i></a>-->
							<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
							<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV521701" data-rel="tooltip"><i class="icon-download-alt"></i></a>
							<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div id="queryPage" class="box-content" style="height: 220px;"></div>
					
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
			<!-- 性能结束 -->
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>