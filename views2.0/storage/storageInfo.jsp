<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
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
function portList(){  //端口查看所有
	window.location="${path}/servlet/port/PortAction?func=PortPage&subSystemID=${subSystemID}&r="+Math.random();
}

function diskDbClick(id){    //磁盘层双击事件
    window.location = "${path}/servlet/disk/DiskAction?func=DiskInfo&diskId="+id+"&subSystemID=${subSystemID}&r="+Math.random();
}
function diskFreshen(){ //磁盘层刷新
	var jsonVal={subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/disk/DiskAction?func=AjaxDiskPage",jsonVal,$("#diskContent"));
}
function diskList(){ //磁盘查看所有
	window.location = "${path}/servlet/disk/DiskAction?func=DiskPage&subSystemID=${subSystemID}&r="+Math.random();
}
function poolDbClick(id){ //存储池层双击
	window.location.href = "${path}/servlet/pool/PoolAction?func=PoolInfo&poolId="+id+"&subSystemID=${subSystemID}&r="+Math.random();
}
function poolFreshen(){//存储池层刷新
	var jsonVal={subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/pool/PoolAction?func=AjaxPoolPage",jsonVal,$("#poolContent"));
}
function poolList(){  //池查看所有
	window.location.href = "${path}/servlet/pool/PoolAction?func=PoolPage&subSystemID=${subSystemID}&r="+Math.random();
}
function volumeDbClick(id){ //卷层双击事件
	window.location.href = "${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid="+id+"&subSystemID=${subSystemID}&r="+Math.random();
}
function volumeFreshen(){  //卷层刷新
	var jsonVal={subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
}
function volumeList(){  //卷查看所有
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

function arraysiteFreshen(){
	var jsonVal = {subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/arraysite/ArraysiteAction?func=AjaxArraysitePage",jsonVal,$("#arraysiteContent"));
}
function arraysiteList(){
	window.location="${path}/servlet/arraysite/ArraysiteAction?func=ArraysitePage&subSystemID=${subSystemID}";
}
function arraysiteDbClick(id){
    window.location = "${path}/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=${subSystemID}&arraysiteId="+id;
}

function rankFreshen(){
	var jsonVal = {subSystemID:"${subSystemID}"};
	loadData("${path}/servlet/rank/RankAction?func=AjaxRankPage",jsonVal,$("#rankContent"));
}
function rankList(){
	window.location="${path}/servlet/rank/RankAction?func=RankPage&subSystemID=${subSystemID}";
}
function rankDbClick(id){
    window.location = "${path}/servlet/rank/RankAction?func=RankInfo&subSystemID=${subSystemID}&rankId="+id;
}


$(function(){
	var jsonVal2 = ${conPrfData};
	Public.drawPrfLine("prfContent",jsonVal2);
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

function doListRefresh(){
	loadData("${path}/servlet/controller/ConAction?func=ConPrfPage",{subSystemID:subsystemId},$("#perfChart"));
}

function doListRefresh2(){
	loadData("${path}/servlet/storage/StorageAction?func=StoragePrfPage",{subSystemID:subsystemId,level:3},$("#perfChart2"));
}
</script>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>

<div id="content">
	<div class="well" style="padding-top:0px;padding-bottom:0px;">
		<img src="${path}/resource/img/project/storage.png" style="width:12%;margin:0px atuo;float:left;padding-top:70px;" align="bottom"/>
		<!-- 存储系统详细信息表单开始  -->
		<div class="box-content" style="width: 85%; margin: 0 0 0 10px; padding-top:0px;float: left;">
		<legend style="margin-bottom:0px;">名称: &nbsp;&nbsp;${storageInfo.the_display_name}</legend>
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>厂商</h4></th>
					<td class="center">${storageInfo.vendor_name}</td>
				</tr>
				<tr>
					<th><h4>型号</h4></th>
					<td class="center">${storageInfo.model_name}</td>
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
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
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
	</div>
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
		<!-- 磁盘阵列开始 -->
		<div class="box span6">
			<div class="box-header well">
				<h2>阵列</h2>
				<div class="box-icon">
					<a href="javascript:arraysiteFreshen()" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:arraysiteList()" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content" id="arraysiteContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								Rank
							</th>
							<th>
								存储池
							</th>
							<th>
								冗余级别
							</th>
							<th>
								描述
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty arraysitePage.data}">
								<c:forEach var="item" items="${arraysitePage.data}" varStatus="status">
									<tr ondblclick="arraysiteDbClick(${item.disk_group_id})" style="cursor:pointer">
										<td>
											${item.the_display_name}
										</td>
										<td>
											<a href="${path}/servlet/rank/RankAction?function=RankInfo&rankId=${item.storage_extent_id}&subSystemID=${subSystemID}">${item.rank_name}</a>
										</td>									
										<td>
											<a href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
										</td>									
										<td>
											${item.raid_level}
										</td>									
										<td>
											${item.description}
										</td>									
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=6>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="arraysiteListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty extendPage.data}">
					<script>
						$("#arraysiteListpageNub").getLinkStr({pagecount:"${arraysitePage.totalPages}",curpage:"${arraysitePage.currentPage}",numPerPage:"${arraysitePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/arraysite/ArraysiteAction?func=AjaxArraysitePage&subSystemID=${subSystemID}",divId:'arraysiteContent'});
					</script>
				</c:if>
				</div>
			</div>
		</div>
		<!-- 磁盘阵列结束 -->
	</div>
	<div class="row-fluid sortable">	
		<!-- Rank开始 -->
		<div class="box span6">
			<div class="box-header well">
				<h2>Rank</h2>
				<div class="box-icon">
					<a href="javascript:rankFreshen()" class="btn btn-round" title="刷新"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:rankList()" class="btn btn-round" title="查看所有"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
			<div class="tab-content" id="rankContent" style="height: 260px;">
				<table class="table table-bordered table-striped table-condensed" id="conTable">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								扩展卷数
							</th>
							<th>
								总容量(G)
							</th>
							<th>
								已用容量(G)
							</th>
							<th>
								空闲容量(G)
							</th>
							<th>
								存储池
							</th>
							<th>
								状态
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty rankPage.data}">
								<c:forEach var="item" items="${rankPage.data}" varStatus="status">
									<tr ondblclick="rankDbClick(${item.storage_extent_id})" style="cursor:pointer">
										<td>
											${item.the_display_name}
										</td>
										<td>
											${item.the_extent_volume}
										</td>									
										<td>
											<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
										</td>									
										<td>
											<fmt:formatNumber value="${item.the_used_space}" pattern="0.00" />
										</td>									
										<td>
											<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
										</td>									
										<td>
											<a href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
										</td>									
										<td>
											${item.the_operational_status}
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
				<div id="rankListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty rankPage.data}">
					<script>
						var param = $("#conditionForm").serialize();
						$("#rankListpageNub").getLinkStr({pagecount:"${rankPage.totalPages}",curpage:"${rankPage.currentPage}",numPerPage:"${rankPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/rank/RankAction?func=AjaxRankPage&subSystemID=${subSystemID}&"+param,divId:'rankContent'});
					</script>
				</c:if>
				</div>
			</div>
		</div>
		<!-- Rank结束 -->
		<div class="box span6">
			<div class="box-header well">
				<h2>
					控制器
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf('${subSystemID}',2,'')"><i class="icon-cog"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doListRefresh()"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportPrfCSV"><i class="icon-download-alt"></i></a>
				</div>
			</div>
			<div class="box-content" style="max-height:260px;">
				<!-- tab切换标签开始 -->
				<ul class="nav nav-tabs" id="myTab">
					<li class="active">
						<a href="#loadcontent">性能曲线</a>
					</li>
					<li class="">
						<a href="#dataContent">性能数据</a>
					</li>
				</ul>
				<!-- tab切换标签结束 -->
				<div id="perfChart" class="tab-content" style="height: 230px;">
					<!-- 性能曲线切换页开始 -->
					<div class="tab-pane active" id="loadcontent">
						<div id="prfContent" style="width: 94%;margin:0px;height: 230px;"></div>
					</div>
					<!-- 性能曲线切换页结束 -->
					<!-- 性能数据切换页开始 -->
					<div class="tab-pane" id="dataContent">
						<table class="table table-bordered table-striped table-condensed" id="conTable">
							<thead>
								<c:choose>
									<c:when test="${not empty conPrfData}">
										<tr>
											<c:forEach var="head" items="${conPrfData.thead}">
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
									<c:when test="${not empty conPrfData}">
										<c:forEach var="item" items="${conPrfData.tbody.data}" varStatus="status">
											<tr>
												<c:forEach var="thead" items="${conPrfData.thead}">
													<td>
														<c:choose>
															<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">
																${item.ele_name}
															</c:when>
															<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd hh:mm:ss" />
															</c:when>
															<c:otherwise>
																<c:if test="${conPrfData.threshold==1}">
																	<span style="${item[fn:toLowerCase(thead.key)] >=conPrfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
																</c:if>
																<c:if test="${conPrfData.threshold==0}">
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
						<div id="conpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty conPrfData}">
							<script>
							$("#conpageNub").getLinkStr({pagecount:"${conPrfData.tbody.totalPages}",curpage:"${conPrfData.tbody.currentPage}",numPerPage:"${conPrfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/controller/ConAction?func=ConPrfPage&tablePage=1&subSystemID=${subSystemID}",divId:'dataContent'});
								$("#exportPrfCSV").unbind();
								var exurl = "${path}/servlet/controller/ConAction?func=exportPrefData&subSystemID=${subSystemID}";
  								$("#exportPrfCSV").attr("href",exurl);
							</script>
						</c:if>
						<c:if test="${empty conPrfData}">
							<script>
								$("#exportPrfCSV").unbind();
								$("#exportPrfCSV").attr("href","javascript:void(0);");
								$("#exportPrfCSV").bind("click",function(){bAlert("暂无可导出数据！")});
							</script>
						</c:if>
					</div>
					<!-- 性能数据切换页结束 -->
				</div>
			</div>
		</div>
		<!--/span-->
	</div>
	<!--/row-->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>