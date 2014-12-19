<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/storage.js"></script> 
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path }/resource/js/project/topn.js"></script>
<script src="${path }/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript">
var subsystemId = "${subSystemID}";
$(function(){Highcharts.setOptions({global: {useUTC: false}});});
$(function(){
	var jsonVal2 = ${conPrfData};
	$("#loadcontent").addClass("active");
	$("#dataContent").removeClass("active");
	$("a[href='#dataContent']").parent("li").removeClass("active");
	$("a[href='#loadcontent']").parent("li").addClass("active");
	Public.drawPrfLine("prfContent",jsonVal2);
	doListRefresh2();
});

$(function(){
	var subsystemId = "${subSystemID}";
	$("#subFreshen").bind("click",function(){
		loadData(getRootPath()+"/servlet/port/PortAction?func=AjaxPortPage",{subSystemID:subsystemId},$("#portContent"));
	});
	$("#subShowlist").bind("click",function(){
		window.location=getRootPath()+"/servlet/port/PortAction?func=PortPage&subSystemID="+subsystemId;
	});
	$("#subTab li").bind('click',function(){
		StroageInfo.subTabChange(subsystemId);
	});
});
function doListRefresh2(){
	loadData("${path}/servlet/storage/StorageAction?func=StoragePrfPage",{subSystemID:subsystemId,level:3},$("#perfChart2"));
}
function doListRefresh(){
	loadData("${path}/servlet/controller/ConAction?func=ConPrfPage",{subSystemID:subsystemId},$("#perfChartcon"));
}
</script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>
<!--  
<div style="width:100%;margin-bottom: 10px;">
			<div style="width: 5%;float: left;"><img src="${path}/resource/img/project/storage.png"/></div>
			<legend style="margin-bottom:0px;"> &nbsp;&nbsp;${storageInfo.the_display_name}</legend>
</div>
		-->
		<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/storage.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${storageInfo.the_display_name} </span>
			<br>
			<strong>IP:</strong>
			<span>${storageInfo.ip_address}</span>
			<strong style="margin-left: 20px;">Status:</strong>
			<span>${storageInfo.the_operational_status}</span>
		</li>
</ul>
		
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#detailTab">配置</a>
		</li>
		<li class="">
			<a href="#prfTab">性能</a>
		</li>
		<li class="">
			<a href="#alertTab">事件</a>
		</li>
		<li class="">
			<a href="#dataTab">部件</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 存储系统详细信息表单开始 -->
	<div class="tab-pane active" id="detailTab">
	<!--<div class="well" style="padding-top:0px;padding-bottom:0px;">
 		<div style="width: 10%;float: left;">
			<div style="margin-top:50px;width:100%;height:30px;text-align: center;">存储系统</div>
			<div style=""><img src="${path}/resource/img/project/storage.png"/></div>
		</div>
 -->
		<div class="box-content" style="width: 98%;  padding-top:10px;">
<!-- 		<legend style="margin-bottom:0px;">名称: &nbsp;&nbsp;${storageInfo.the_display_name}</legend> -->
			<table class="table  configTable" style="margin-bottom:0px;width:49%;float:left;">  
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
		 <table class="table configTable" style="margin-bottom:0px; width:49%;float:right;">  
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
		<div style="clear: both;"></div>
	</div>
<!--	</div>
	 存储系统详细信息表单结束 -->
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf3('${subSystemID}','3')" data-rel='tooltip'><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
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
						<div id="prfContent2" style="width: 95%; max-height: 350px;"></div>
					</div>
					<!-- 性能曲线切换页结束 -->
					<!-- 性能数据切换页开始 -->
					<div class="tab-pane" id="dataContent2" style="padding-top:10px;">
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
																	<span style="${item[fn:toLowerCase(thead.key)] >=prfData.threvalue?'color:red':''}"><fmt:formatNumber value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/></span>
																</c:if>
																<c:if test="${prfData.threshold==0}">	
																	<fmt:formatNumber value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/>
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
	</div>
	<!-- 性能结束 -->
	<!-- 事件开始 -->
	<div class="tab-pane" id="alertTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					事件
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${subSystemID}','${subSystemID}','Storage');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${subSystemID}','${subSystemID}','Storage');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${subSystemID}','${subSystemID}','Storage');"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;" id="dAlertContent">
				<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th  style="width: 20px;">
								<label class="checkbox inline">
									<input type="checkbox"   onclick="DeviceAlert.doAlertCheck(this.checked);">
							    </label>
							</th>
							<th style="width: 130px;">
										首次发生时间
									</th>
									<th style="width: 130px;">
										最后发生时间
									</th>
									<th  style="width: 55px;">
										类型
									</th>
									<th  style="width: 55px;">
										重复次数
									</th>
									<th style="width: 90px;">
										状态
									</th>
									<th style="width: 90px;">
										级别
									</th>
									<th style="width: 170px;">
										事件源
									</th>
									<th>
										消息
									</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty deviceLogPage.data}">
								<c:forEach var="item" items="${deviceLogPage.data}" varStatus="status">
									<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Storage')">
										<td>
											<label class="checkbox inline">
												<input type="checkbox" value="${item.fruleid}_${item.ftopid}_${item.flogtype}"  name="dAlertCheck">
										    </label>
										</td>
												<td>
													<fmt:formatDate value="${item.ffirsttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
												</td>
												<td>
													<fmt:formatDate value="${item.flasttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
												</td>
												<td>
													<c:choose>
													
														<c:when test="${item.flogtype == 3}">HMC告警</c:when>
														<c:when test="${item.flogtype == 2}">阀值告警</c:when>
														<c:when test="${item.flogtype == 1}">TPC告警</c:when>
														<c:when test="${item.flogtype == 0}">系统告警</c:when>
													</c:choose>
												</td>
												<td>
													${item.fcount}
												</td>
												<td>
													<c:choose>
														<c:when test="${item.fstate == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
														<c:when test="${item.fstate == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
														<c:when test="${item.fstate == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
														<c:when test="${item.fstate == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
													</c:choose>
												</td>
												<td>
													<c:choose>
														<c:when test="${item.flevel == 0}"><span class="label">Info</span> </c:when>
														<c:when test="${item.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
														<c:when test="${item.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
													</c:choose>
												</td>
												<td>
													${item.ftopname}
												</td>
												<td>
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Virtual')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename } <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
      <c:out value="${fn:substring(item.fdetail, 0, 200)}......" /></c:when> <c:otherwise><c:out value="${item.fdetail}" /></c:otherwise></c:choose>" title="详细信息">
													 ${item.fdescript}
												</a>
													
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
				
				<div class="pagination pagination-centered">
					<ul id="alertListNub"></ul>
				</div>
				<c:if test="${not empty deviceLogPage.data}">
					<script>
						$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${subSystemID}&topId=${subSystemID}&resourceType=Storage",divId:'dAlertContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	</div>
	<!-- 事件结束 -->
	<!-- 部件开始 -->
	<div class="tab-pane" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					部件
				</h2>
				<div class="box-icon">
					<a id='subFreshen' href="javascript:void(0)" class="btn btn-round" title="刷新" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a id='subShowlist' href="javascript:void(0)" class="btn btn-round" title="查看所有" data-rel='tooltip'><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content" style="max-height:810px;" id="subTab">
				<ul class="nav nav-tabs" id="myTab">
					<li class="active" title="port">
						<a href="#portContent">端口(${portCount})</a>
					</li>
					<li class=""  title="disk">
						<a href="#diskContent">磁盘(${diskCount})</a>
					</li>
					<li class="" title="pool">
						<a href="#poolContent">存储池(${poolCount})</a>
					</li>
					<li class="" title="volume">
						<a href="#volumeContent">卷(${volumeCount})</a>
					</li>
					<li class="" title="extent">
						<a href="#extendContent">存储扩展(${extendCount})</a>
					</li>
					<li class="" title="arraysite">
						<a href="#arraysiteContent">阵列(${arrayCount})</a>
					</li>
					<li class="" title="rank">
						<a href="#rankContent">Rank(${rankCount})</a>
					</li>
					<li class="" title="node">
						<a href="#nodeContent">冗余节点(${nodeCount})</a>
					</li>
					<li class="" title="iogroup">
						<a href="#iogroupContent">IOGroup(${iogroupCount})</a>
					</li>
					<li class="" title="controller">
						<a href="#controllerContent">控制器</a>
					</li>
				</ul>
				<div id="perfChart" class="tab-content" style="overflow-y: hidden;min-height:200px;">
					<div class="tab-pane active" id="portContent" style="padding-top:10px;">
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
											<tr>
											<td>
												<a title="${item.the_display_name}" href="${path}/servlet/port/PortAction?func=PortInfo&portId=${item.port_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
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
												<cs:cstatus value="${item.the_consolidated_status}" />
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
					<div class="tab-pane" id="diskContent" style="padding-top:10px;overflow-y: hidden;">
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
													<a title="${item.diskgroup_name}" href="${path}/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=${subSystemID}&arraysiteId=${item.the_arraysite_id}">${item.diskgroup_name}</a>
												</td>									
												<td>
													${item.vendor_name}
												</td>									
												<td>
													${item.model_name}
												</td>									
												<td>
													${item.serial_number}
												</td>									
												<td>
													${item.firmware_rev}
												</td>									
												<td>
													<cs:cstatus value="${item.the_consolidated_status}" />
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
					<div class="tab-pane" id="poolContent" style="padding-top:10px;overflow-y: hidden;">
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
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
												</td>
												<td>
													<fmt:formatNumber value="${item.the_space}" pattern="0.00" />
												</td>									
												<td>
													<cs:isProgress total="${item.the_space}" available="${item.the_consumed_space}"/>
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
													<cs:cstatus value="${item.the_consolidated_status}" />
												</td>
												<td>
													${item.raid_level}
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
						<div id="poolListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty poolPage.data}">
							<script>
								$("#poolListpageNub").getLinkStr({pagecount:"${poolPage.totalPages}",curpage:"${poolPage.currentPage}",numPerPage:"${poolPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/pool/PoolAction?func=AjaxPoolPage&subSystemID=${subSystemID}",divId:'poolContent'});
							</script>
						</c:if>	
					</div>
					<div class="tab-pane" id="volumeContent" style="padding-top:10px;overflow-y: hidden;">
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
										沉余级别
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
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid=${item.svid}&subSystemID=${subSystemID}">${item.the_display_name}</a>
												</td>
												<td>
													<cs:cstatus value="${item.the_consolidated_status}" />
												</td>
												<td>
													<fmt:formatNumber value="${item.the_capacity}" pattern="0.00"/>
												</td>
												<td>
													<cs:isProgress total="${item.the_capacity}" available="${item.the_used_space}"/>
												</td>
												<td>
													${item.the_redundancy}
												</td>
												<td>
													<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
												</td>
												<td>
													${item.unique_id}
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
						<div id="volumeListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty volumePage.data}">
							<script>
								$("#volumeListpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/volume/VolumeAction?func=AjaxVolumePage&greatLogical_Capacity=${greatLogical_Capacity}&lessLogical_Capacity=${lessLogical_Capacity}&name=${name}&subSystemID=${subSystemID}",divId:'volumeContent'});
							</script>
						</c:if>
					</div>
					<div class="tab-pane" id="extendContent" style="padding-top:10px;overflow-y: hidden;">
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
										已用容量
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
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/extend/ExtendAction?func=extendInfo&subSystemID=${subSystemID}&extendId=${item.storage_extent_id}">${item.the_display_name}</a>
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
													<cs:isProgress total="${item.the_total_space}" available="${item.the_total_space-item.the_available_space}"/>
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
													<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
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
					<div class="tab-pane" id="arraysiteContent" style="padding-top:10px;overflow-y: hidden;">
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
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=${subSystemID}&arraysiteId=${item.disk_group_id}">${item.the_display_name}</a>
												</td>
												<td>
													<a href="${path}/servlet/rank/RankAction?func=RankInfo&rankId=${item.storage_extent_id}&subSystemID=${subSystemID}">${item.rank_name}</a>
												</td>									
												<td>
													<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
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
						<c:if test="${not empty arraysitePage.data}">
							<script>
								$("#arraysiteListpageNub").getLinkStr({pagecount:"${arraysitePage.totalPages}",curpage:"${arraysitePage.currentPage}",numPerPage:"${arraysitePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/arraysite/ArraysiteAction?func=AjaxArraysitePage&subSystemID=${subSystemID}",divId:'arraysiteContent'});
							</script>
						</c:if>
					</div>
					<div class="tab-pane" id="rankContent" style="padding-top:10px;overflow-y: hidden;">
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
											<tr>
												<td>
													<a title="${item.the_display_name.trim()}" href="${path}/servlet/rank/RankAction?func=RankInfo&subSystemID=${subSystemID}&rankId=${item.storage_extent_id}">${item.the_display_name}</a>
												</td>
												<td>
													${item.the_extent_volume}
												</td>									
												<td>
													<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
												</td>									
												<td>
													<cs:isProgress total="${item.the_total_space}" available="${item.the_used_space}"/>
												</td>									
												<td>
													<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
												</td>								
												<td>
													<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
												</td>									
												<td>
													${item.the_operational_status}
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
						<div id="rankListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty rankPage.data}">
							<script>
							$("#rankListpageNub").getLinkStr({pagecount:"${rankPage.totalPages}",curpage:"${rankPage.currentPage}",numPerPage:"${rankPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/rank/RankAction?func=AjaxRankPage&subSystemID=${subSystemID}",divId:'rankContent'});
							</script>
						</c:if>
					</div>
					<div class="tab-pane" id="controllerContent" style="padding-top:0px;overflow-y: hidden;">
						<div class="box span12">
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
								<div id="perfChartcon" class="tab-content" style="min-height: 100px;">
									<!-- 性能曲线切换页开始 -->
									<div class="tab-pane active" id="loadcontent">
										<div id="prfContent" style="width: 94%;margin:0px;min-height: 300px;"></div>
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
					</div>
					<div class="tab-pane" id="nodeContent" style="padding-top:10px;overflow-y: hidden;">
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
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/node/NodeAction?func=NodeInfo&nodeId=${item.redundancy_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
												</td>
												<td>
													${item.component_id}
												</td>
												<td>
													${item.ip_address}
												</td>
												<td>
													<a title="${item.iogroup_name}" href="${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId=${item.io_group_id}">${item.iogroup_name}</a>
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
					<div class="tab-pane" id="iogroupContent" style="padding-top:10px;overflow-y: hidden;">
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
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId=${item.io_group_id}">${item.the_display_name}</a>
												</td>
												<td>
													<fmt:formatNumber value="${item.mirroring_total_memory/1024}" pattern="0.00" />
												</td>
												<td>
													<cs:isProgress total="${item.mirroring_total_memory}" available="${item.mirroring_free_memory}"/>
												</td>
												<td>
													<fmt:formatNumber value="${item.flash_copy_total_memory/1024}" pattern="0.00" />
												</td>
												<td>
													<cs:isProgress total="${item.flash_copy_total_memory}" available="${item.flash_copy_free_memory}"/>
												</td>
												<td>
													<fmt:formatNumber value="${item.raid_total_memory/1024}" pattern="0.00" />
												</td>
												<td>
													<cs:isProgress total="${item.raid_total_memory}" available="${item.raid_free_memory}"/>
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
		</div>
	</div>
	</div>
	<!-- 部件结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>