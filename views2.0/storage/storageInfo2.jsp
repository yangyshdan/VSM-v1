<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/ftime" prefix="formateTime" %>
<script src="${path }/resource/js/project/storage.js"></script> 
<script src="${path }/resource/js/project/publicscript.js"></script> 
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script type="text/javascript">
var subsystemId = "${subSystemID}";
function portDbClick(id){  //端口层双击事件
	window.location = "${path}/servlet/port/PortAction?func=PortInfo&portId="+id+"&subSystemID=${subSystemID}&r="+Math.random();
}
function portFreshen(){  //端口层刷新
	var jsonVal={subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/port/PortAction?func=AjaxPortPage",jsonVal,$("#portContent"));
}
function portList(){
	window.location="${path}/servlet/port/PortAction?func=PortPage&subSystemID=${subSystemID}&r="+Math.random();
}

function diskDbClick(id){    //磁盘层双击事件
    window.location = "${path}/servlet/disk/DiskAction?func=DiskInfo&diskId="+id+"&subSystemID=${subSystemID}&r="+Math.random();
}
function diskFreshen(){ //磁盘层刷新
	var jsonVal={subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/disk/DiskAction?func=AjaxDiskPage",jsonVal,$("#diskContent"));
}
function diskList(){
	window.location = "${path}/servlet/disk/DiskAction?func=DiskPage&subSystemID=${subSystemID}&r="+Math.random();
}
function poolDbClick(id){ //存储池层双击
	window.location.href = "${path}/servlet/pool/PoolAction?func=PoolInfo&poolId="+id+"&subSystemID=${subSystemID}&r="+Math.random();
}
function poolFreshen(){//存储池层刷新
	var jsonVal={subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/pool/PoolAction?func=AjaxPoolPage",jsonVal,$("#poolContent"));
}
function poolList(){
	window.location.href = "${path}/servlet/pool/PoolAction?func=PoolPage&subSystemID=${subSystemID}&r="+Math.random();
}
function volumeDbClick(id){ //卷层双击事件
	window.location.href = "${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid="+id+"&subSystemID=${subSystemID}&r="+Math.random();
}
function volumeFreshen(){  //卷层刷新
	var jsonVal={subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
}
function volumeList(){//卷查看所有
	window.location = "${path}/servlet/volume/VolumeAction?func=volumePage&subSystemID=${subSystemID}&r="+Math.random();
}
function extendDbClick(id){  //存储扩展双击
	window.location.href = "${path}/servlet/extend/ExtendAction?func=extendInfo&subSystemID=${subSystemID}&extendId="+id+"&r="+Math.random();
}
function extendFreshen(){   //存储扩展刷新
	var jsonVal = {subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/extend/ExtendAction?func=AjaxExtendPage",jsonVal,$("#extendContent"));
}
function extendList(){//存储扩展查看全部
	window.location="${path}/servlet/extend/ExtendAction?func=extendPage&subSystemID=${subSystemID}";
}
function nodeFreshen(){   //冗余节点刷新
	var jsonVal = {subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/node/NodeAction?func=AjaxNodePage",jsonVal,$("#nodeContent"));
}
function nodeList(){  //冗余节点列表
	window.location="${path}/servlet/node/NodeAction?func=NodePage&subSystemID=${subSystemID}";
}
function iogroupFreshen(){    //IO Group刷新
	var jsonVal = {subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/iogroup/IogroupAction?func=AjaxIogroupPage",jsonVal,$("#iogroupContent"));
}
function iogroupList(){//IO Group列表
	window.location = "${path}/servlet/iogroup/IogroupAction?func=IogroupPage&subSystemID=${subSystemID}";
}

$(function(){
	doListRefresh2();
	//var jsonVal = ${prfData};
	//Public.drawPrfLine("prfContent2",jsonVal);
});

function doFreshenCap(){
	var jsonVal = {subSystemID:subsystemId,isFreshen:1};
	$.ajax({
		url:"${path}/servlet/storage/StorageAction?func=CapacityInfo",
		data:jsonVal,
		success:function(data){
			CapecityScript(eval("("+data+")"));
			CapecityRateScript(eval("("+data+")"));
		}
	});
}

function doListRefresh2(){
	loadData("${path}/servlet/storage/StorageAction?func=StoragePrfPage",{subSystemID:subsystemId,level:3},$("#perfChart2"));
}

</script>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>

<div id="content">
		<div style="width:10%;height:200px;line-height:220px;float:left;">
			<img src="${path}/resource/img/project/storage.png"/>
		</div>
		<div class="box-content" style="width: 85%;padding-top:0px;float: left;">
		<legend style="margin-bottom:0px;">名称: &nbsp;&nbsp;${storageInfo.the_display_name}</legend>
			<table class="table table-condensed" style="margin-bottom:0px;width:47%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>厂商</h4></th>
					<td class="center">${storageInfo.vendor_id}</td>
				</tr>
				<tr>
					<th><h4>型号</h4></th>
					<td class="center">${storageInfo.model_id}</td>
				</tr>
				<tr>
					<th><h4>序列号</h4></th>
					<td class="center">${storageInfo.serial_number}</td>
				</tr>
				<tr>
					<th><h4>物理磁盘容量(G)</h4></th>
					<td class="center"><fmt:formatNumber value="${storageInfo.the_physical_disk_space}" pattern="0.00" /></td>
				</tr>
				<tr>
					<th><h4>池容量(G)</h4></th>
					<td class="center"><fmt:formatNumber value="${storageInfo.the_storage_pool_consumed_space}" pattern="0.00" /></td>
				</tr>
				<tr>
					<th><h4>已分配卷容量(G)</h4></th>
					<td class="center"><fmt:formatNumber value="${storageInfo.the_assigned_volume_space}" pattern="0.00" /></td>
				</tr>
				<tr>
					<th><h4>状态</h4></th>
					<td class="center">${storageInfo.the_propagated_status}</td>
				</tr>
				<tr>
					<th><h4>硬件状态</h4></th>
					<td class="center">${storageInfo.the_consolidated_status}</td>
				</tr>
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:47%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>系统类型</h4></th>
					<td class="center">
						${storageInfo.the_os_type}
					</td>                                       
				</tr>
				<tr>
					<th><h4>IP地址</h4></th>
					<td class="center">
						${storageInfo.ip_address}
					</td>                                       
				</tr>
				<tr>
					<th><h4>微码版本</h4></th>
					<td class="center">
						${storageInfo.code_level}
					</td>                                        
				</tr>
				<tr>
					<th><h4>缓存</h4></th>
					<td class="center">
						${storageInfo.cache}
					</td>                                        
				</tr>
				<tr>
					<th><h4>卷总容量(G)</h4></th>
					<td class="center">
						<fmt:formatNumber value="${storageInfo.the_volume_space}" pattern="0.00" />
					</td>                                       
				</tr>
				<tr>
					<th><h4>可用池空间(G)</h4></th>
					<td class="center">
						<fmt:formatNumber value="${storageInfo.the_storage_pool_available_space}" pattern="0.00" />
					</td>                                       
				</tr>
				<tr>
					<th><h4>未分配卷容量(G)</h4></th>
					<td class="center">
						<fmt:formatNumber value="${storageInfo.the_unassigned_volume_space}" pattern="0.00" />
					</td>                                       
				</tr>
				<tr>
					<th><h4>运行状态</h4></th>
					<td class="center">
						${storageInfo.the_operational_status}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 存储系统详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					存储系统性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf3('${subSystemID}','3')"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
				</div>
			</div>
			<div class="box-content">
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
				<div id="perfChart2" class="tab-content" style="overflow: visible;">
					<!-- 性能曲线切换页开始 -->
					<div class="tab-pane active" id="loadcontent2">
						<div id="prfContent2" style="width: 100%; height: 300px;"></div>
					</div>
					<!-- 性能曲线切换页结束 -->
					<!-- 性能数据切换页开始 -->
					<div class="tab-pane" id="dataContent2">
						<table class="table table-bordered table-striped table-condensed" id="conTable">
							<thead>
								<c:choose>
									<c:when test="${not empty prfData}">
										<tr>
											<c:forEach var="head" items="${prfData.thead}">
											<c:choose>
												<c:when test="${head.key=='ele_name'}">
													<th>
														${head.value}
													</th>
												</c:when>
												<c:when test="${head.key=='prf_timestamp'}">
													<th>
														${head.value}
													</th>
												</c:when>
												<c:otherwise>
													<th>
														${head.value}
													</th>
												</c:otherwise>
											</c:choose>
											</c:forEach>
										</tr>
									</c:when>
									<c:otherwise>
										<tr>
											<td>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty prfData}">
										<c:forEach var="item" items="${prfData.tbody.data}" varStatus="status">
											<tr>
												<c:forEach var="thead" items="${prfData.thead}">
													<td>
														<c:choose>
															<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">
																${item.ele_name}
															</c:when>
															<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd hh:mm:ss" />
															</c:when>
															<c:otherwise>
																<c:if test="${prfData.threshold==1}">
																	<span style="${item[fn:toLowerCase(thead.key)] >=prfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
																</c:if>
																<c:if test="${prfData.threshold==0}">
																	${item[fn:toLowerCase(thead.key)]}
																</c:if>
															</c:otherwise>
														</c:choose>
													</td>
												</c:forEach>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="StorageInfopageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty prfData}">
							<script>
								$("#StorageInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/storage/StorageAction?func=StoragePrfPage&subSystemID=${subSystemID}&level=3&tablePage=1",divId:'dataContent2'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/storage/StorageAction?func=exportPrefData&subSystemID=${storageInfo.subsystem_id}&level=3";
  								$("#exportCSV").attr("href",exurl);
							</script>
						</c:if>
						<c:if test="${empty prfData}">
							<script>
								$("#exportCSV").unbind();
								$("#exportCSV").attr("href","javascript:void(0);");
								$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
							</script>
						</c:if>
					</div>
					<!-- 性能数据切换页结束 -->
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid sortable">	
		<!-- 冗余节点开始 -->
		<div class="box span6">
			<div class="box-header well">
				<h2>冗余节点</h2>
				<div class="box-icon">
					<a href="javascript:nodeFreshen()" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:nodeList()" class="btn btn-round" title="查看全部"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content" id="nodeContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed" style="word-break:break-all">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								组件ID
							</th>
							<th>
								IP地址
							</th>
							<th>
								IO Group
							</th>
							<th>
								后端名称
							</th>
							<th>
								操作状态
							</th>
							<th>
								WWN
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty nodePage.data}">
								<c:forEach var="item" items="${nodePage.data}" varStatus="status">
									<tr ondblclick="nodeDbClick(${item.redundancy_id})" style="cursor:pointer;">
										<td>
											${item.the_display_name}
										</td>
										<td>
											${item.component_id}
										</td>
										<td>
											${item.ip_address}
										</td>
										<td>
											<a href="">${item.iogroup_name}</a>
										</td>
										<td>
											${item.the_backend_name}
										</td>
										<td>
											${item.the_operational_status}
										</td>
										<td>
											${item.wwn}
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=8>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="nodeListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty nodePage.data}">
					<script>
						var param = $("#conditionForm").serialize();
						$("#nodeListpageNub").getLinkStr({pagecount:"${nodePage.totalPages}",curpage:"${nodePage.currentPage}",numPerPage:"${nodePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/node/NodeAction?func=AjaxNodePage&subSystemID=${subSystemID}&"+param,divId:'nodeContent'});
					</script>
					</c:if>
				</div>
			</div>
		</div>
		<!-- 冗余节点结束 -->
		<!-- IO Group 开始 -->
		<div class="box span6">
			<div class="box-header well" data-original-title>
				<h2>IO Group</h2>
				<div class="box-icon">
					<a href="javascript:iogroupFreshen();" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:iogroupList();" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content"  id="iogroupContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
					<tr>
						<th>
							名称
						</th>
						<th>
							镜像内存(G)
						</th>
						<th>
							镜像空闲内存(G)
						</th>
						<th>
							快照内存(G)
						</th>
						<th>
							快照空闲内存(G)
						</th>
						<th>
							阵列内存(G)
						</th>
						<th>
							阵列空闲内存(G)
						</th>
						<th>
							维护状态
						</th>
					</tr>
				</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty iogroupPage.data}">
						<c:forEach var="item" items="${iogroupPage.data}" varStatus="status">
							<tr ondblclick = "iogroupDbClick(${item.port_id})" style="cursor:pointer">
							<td>
								${item.the_display_name}
							</td>
							<td>
								<fmt:formatNumber value="${item.mirroring_total_memory/1024}" pattern="0.00" />
							</td>
							<td>
								<fmt:formatNumber value="${item.mirroring_free_memory/1024}" pattern="0.00" />
							</td>
							<td>
								<fmt:formatNumber value="${item.flash_copy_total_memory/1024}" pattern="0.00" />
							</td>
							<td>
								<fmt:formatNumber value="${item.flash_copy_free_memory/1024}" pattern="0.00" />
							</td>
							<td>
								<fmt:formatNumber value="${item.raid_total_memory/1024}" pattern="0.00" />
							</td>
							<td>
								<fmt:formatNumber value="${item.raid_free_memory/1024}" pattern="0.00" />
							</td>
							<td>
								${item.maintenance}
									</td>
								</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=8>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="iogroupListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty iogroupPage.data}">
				<script>
					var param = $("#conditionForm").serialize();
					$("#iogroupListpageNub").getLinkStr({pagecount:"${iogroupPage.totalPages}",curpage:"${iogroupPage.currentPage}",numPerPage:"${iogroupPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/iogroup/IogroupAction?func=AjaxIogroupPage&subSystemID=${subSystemID}&"+param,divId:'iogroupContent'});
				</script>
				</c:if>
				</div>
			</div>
		</div>
		<!-- IO Group结束 -->
	</div>
	<div class="row-fluid sortable">	
		<!-- 端口开始 -->
		<div class="box span6">
			<div class="box-header well">
				<h2>端口</h2>
				<div class="box-icon">
					<a href="javascript:portFreshen()" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:portList()" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content" id="portContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
					<tr>
						<th>
							名称
						</th>
						<th>
							端口号
						</th>
						<th>
							端口类型
						</th>
						<th>
							操作状态
						</th>
						<th>
							硬件状态
						</th>
						<th>
							端口速率(M)
						</th>
					</tr>
				</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty portPage.data}">
								<c:forEach var="item" items="${portPage.data}" varStatus="status">
									<tr ondblclick = "portDbClick(${item.port_id})" style="cursor:pointer">
									<td>
										${item.the_display_name}
									</td>
									<td>
										${item.port_number}
									</td>
									<td>
										${item.the_type}
									</td>
									<td>
										${item.the_operational_status}
									</td>
									<td>
										${item.the_consolidated_status}
									</td>
									<td>
										${item.the_port_speed}
									</td>
								</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=7>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="portListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty portPage.data}">
					<script>
						var param = $("#conditionForm").serialize();
						$("#portListpageNub").getLinkStr({pagecount:"${portPage.totalPages}",curpage:"${portPage.currentPage}",numPerPage:"${portPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/port/PortAction?func=AjaxPortPage&subSystemID=${subSystemID}&"+param,divId:'portContent'});
					</script>
				</c:if>	
				</div>
			</div>
		</div>
		<!-- 端口结束 -->
		<!-- 磁盘组开始 -->
		<div class="box span6">
			<div class="box-header well" data-original-title>
				<h2>磁盘</h2>
				<div class="box-icon">
					<a href="javascript:diskFreshen();" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:diskList()" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content"  id="diskContent" style="height: 260px;">
			<table class="table table-bordered table-striped table-condensed" id="conTable">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								容量(G)
							</th>
							<th>
								转速
							</th>
							<th>
								运行状态
							</th>
							<th>
								阵列
							</th>
							<th>
								厂商
							</th>
							<th>
								型号
							</th>
							<th>
								序列号
							</th>
							<th>
								固件版本
							</th>
							<th>
								硬件状态
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty diskPage.data}">
								<c:forEach var="item" items="${diskPage.data}" varStatus="status">
									<tr>
										<td>
											${item.the_display_name}
										</td>
										<td>
											<fmt:formatNumber value="${item.the_capacity}" pattern="0.00" />
										</td>
										<td>
											${item.speed}
										</td>
										<td>
											${item.the_operational_status}
										</td>
										<td>
											<a href="javascript:void(0)">${item.diskgroup_name}</a>
										</td>									
										<td>
											<a href="javascript:void(0)">${item.vendor_name}</a>
										</td>									
										<td>
											<a href="javascript:void(0)">${item.model_name}</a>
										</td>									
										<td>
											${item.serial_number}
										</td>									
										<td>
											${item.firmware_rev}
										</td>									
										<td>
											${item.the_consolidated_status}
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=11>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="diskListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty diskPage.data}">
					<script>
						$("#diskListpageNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/disk/DiskAction?func=AjaxDiskPage&subSystemID=${subSystemID}&"+param,divId:'diskContent'});
					</script>
				</c:if>
				</div>
			</div>
		</div>
		<!-- 磁盘组结束 -->
	</div>
	<!--/row-->
	<!-- row -->
	<div class="row-fluid sortable">	
		<!-- 存储池开始 -->
		<div class="box span6">
			<div class="box-header well" data-original-title>
				<h2>存储池</h2>
				<div class="box-icon">
					<a href="javascript:poolFreshen();" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:poolList()" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content"  id="poolContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								容量(G)
							</th>
							<th>
								已用容量(G)
							</th>
							<th>
								可用容量(G)
							</th>
							<th>
								已分配容量(G)
							</th>
							<th>
								未分配容量(G)
							</th>
							<th>
								本地状态
							</th>
							<th>
								操作状态
							</th>
							<th>
								硬件状态
							</th>
							<th>
								冗余级别
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty poolPage.data}">
								<c:forEach var="item" items="${poolPage.data}" varStatus="status">
									<tr ondblclick="poolDbClick(${item.pool_id})" style="cursor:pointer">
										<td>
											${item.the_display_name}
										</td>
										<td>
											<fmt:formatNumber value="${item.the_space}" pattern="0.00" />
										</td>									
										<td>	
											<fmt:formatNumber value="${item.the_consumed_space}" pattern="0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${item.the_assigned_space}" pattern="0.00" />
										</td>
										<td>
											<fmt:formatNumber value="${item.the_unassigned_space}" pattern="0.00" />
										</td>
										<td>
											${item.the_native_status}
										</td>
										<td>
											${item.the_operational_status}
										</td>
										<td>
											${item.the_consolidated_status}
										</td>
										<td>
											${item.raid_level}
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=11>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="poolListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty poolPage.data}">
					<script>
						$("#poolListpageNub").getLinkStr({pagecount:"${poolPage.totalPages}",curpage:"${poolPage.currentPage}",numPerPage:"${poolPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/pool/PoolAction?func=AjaxPoolPage&subSystemID=${subSystemID}",divId:'poolContent'});
					</script>
				</c:if>
				</div>
			</div>
		</div>
		<!-- 存储池结束 -->
		<!-- 卷开始 -->
		<div class="box span6">
			<div class="box-header well" data-original-title>
				<h2>卷</h2>
				<div class="box-icon">
					<a href="javascript:volumeFreshen();" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:volumeList()" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content" id="volumeContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								逻辑卷名
							</th>
							<th>
								状态
							</th>
							<th>
								容量(G)
							</th>
							<th>
								已用容量(G)
							</th>
							<th>
								冗余级别
							</th>
							<th>
								存储池
							</th>
							<th>
								唯一编号
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty volumePage.data}">
								<c:forEach var="item" items="${volumePage.data}" varStatus="status">
									<tr ondblclick="volumeDbClick(${item.svid})"  style="cursor:pointer">
										<td>
											${item.the_display_name}
										</td>
										<td>
											${item.the_consolidated_status}
										</td>
										<td>
											<fmt:formatNumber value="${item.the_capacity}" pattern="0.00"/>
										</td>
										<td>
											<fmt:formatNumber value="${item.the_used_space}" pattern="0.00"/>
										</td>
										<td>
											${item.the_redundancy}
										</td>
										<td>
											<a href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
										</td>
										<td>
											${item.unique_id}
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=8>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="volumeListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty volumePage.data}">
					<script>
						$("#volumeListpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/volume/VolumeAction?func=AjaxVolumePage&greatLogical_Capacity=${greatLogical_Capacity}&lessLogical_Capacity=${lessLogical_Capacity}&name=${name}&subSystemID=${subSystemID}",divId:'volumeContent'});
					</script>
				</c:if>
				</div>
			</div>
		</div>
		<!-- 卷结束 -->
	</div>
	<!-- /row -->
	<!-- row -->
	<div class="row-fluid sortable">	
	<!-- 存储扩展开始 -->
		<div class="box span6">
			<div class="box-header well">
				<h2>存储扩展</h2>
				<div class="box-icon">
					<a href="javascript:extendFreshen()" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:extendList()" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content" id="extendContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								扩展卷数
							</th>
							<th>
								扩展容量(G)
							</th>
							<th>
								总容量
							</th>
							<th>
								可用容量
							</th>
							<th>
								操作状态
							</th>
							<th>
								本地状态
							</th>
							<th>
								存储池
							</th>
							<th>
								设备ID
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty extendPage.data}">
								<c:forEach var="item" items="${extendPage.data}" varStatus="status">
									<tr ondblclick="extendDbClick(${item.storage_extent_id})" style="cursor:pointer">
										<td>
											${item.the_display_name}
										</td>
										<td>
											${item.the_extent_volume}
										</td>									
										<td>
											<fmt:formatNumber value="${item.the_extent_space}" pattern="0.00" />
										</td>									
										<td>
											<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
										</td>									
										<td>
											<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
										</td>									
										<td>
											${item.the_operational_status}
										</td>									
										<td>
											${item.the_native_status}
										</td>									
										<td>
											<a href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
										</td>									
										<td>
											${item.device_id }
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
				<div id="extendListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty extendPage.data}">
					<script>
						var param = $("#extendHiddenForm").serialize();
						$("#extendListpageNub").getLinkStr({pagecount:"${extendPage.totalPages}",curpage:"${extendPage.currentPage}",numPerPage:"${extendPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/extend/ExtendAction?func=AjaxExtendPage&subSystemID=${subSystemID}&"+param,divId:'extendContent'});
					</script>
				</c:if>
				</div>
			</div>
		</div>
		<!-- 存储扩展结束 -->
	</div>
	<!--/row-->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>