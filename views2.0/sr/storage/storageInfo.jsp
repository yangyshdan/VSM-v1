<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/fmtNumber" prefix="fmtNumber"%>
<script src="${path}/resource/js/project/storage.js"></script> 
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var subsystemId = "${subSystemID}";
$(function(){
	var $td = $("table tbody td#ipAddr");
	var stoIpAddrs = "${storageInfo.ip_address}".split(",");
	for(var i = 0, len = stoIpAddrs.length - 1; i <= len; ++i){
		$td.append($("<a>").attr({
			title: stoIpAddrs[i],
			href: "http://" + stoIpAddrs[i],
			target: "_blank"
		}).text(stoIpAddrs[i]));
		if(i < len){
			$td.append($("<span>").text(","));
		}
	}
	$.ajax({
		url: "${path}/servlet/storage/StorageAction?func=StorageSettingPrf2",
		data: { subSystemID: "${subSystemID}", level: 3  },
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			$("#graphType").parent().parent().remove();
			$("#devtypeAndDevice2").hide();
		}
	});
	
	doListRefresh2();
	$("#subFreshen").bind("click",function(){
		loadData("${path}/servlet/sr/storageport/StoragePortAction?func=AjaxStoragePage",
			{subSystemID:subsystemId,storageType:"${storageInfo.storage_type}"},$("#portContent"));
	});
	$("#subTab li").bind('click',function(){
		subTabChange();
	});
});
function doListRefresh2(){
	loadData("${path}/servlet/storage/StorageAction?func=StoragePrfPage", {
			subSystemID: "${subSystemID}", level: 3, stotype: "${stotype}"
		}, $("#perfChart2"));
}
</script>

<div id="content">
	<div class="well" style="padding-top:0px;padding-bottom:0px;">
		<div style="width: 10%;float: left;">
			<div style="margin-top:30px;width:100%;height:30px;text-align: center;">存储系统</div>
			<div style=""><img src="${path}/resource/img/project/storage.png"/></div>
		</div>
		<!-- 存储系统详细信息开始  -->
		<div class="box-content" style="width: 85%; margin: 0 0 0 10px; padding-top:0px;float: left;">
		<legend style="margin-bottom:0px;"> 名称： ${storageInfo.name}</legend>
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>IP 地址</h4></th>
					<td class="center" id="ipAddr"></td>
				</tr>
				<tr>
					<th><h4>序列号</h4></th>
					<td class="center">${storageInfo.serial_number}</td>
				</tr>
				<tr>
					<th><h4>供应商</h4></th>
					<td class="center">${storageInfo.vendor_name}</td>
				</tr>
				
				<tr>
					<th><h4>型号</h4></th>
					<td class="center">${storageInfo.model}</td>
				</tr>
				<tr>
					<th><h4>微码版本 </h4></th>
					<td class="center">${storageInfo.code_level}</td>
				</tr>
				<!-- 
				<tr>
					<th><h4>LED灯状态</h4></th>
					<td class="center">OFF</td>
				</tr>
				 -->
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
			  <tbody>
				<tr>
					<th><h4>缓存信息</h4></th>
					<td class="center">
						<fmt:formatNumber value="${storageInfo.cache_gb/1024}" pattern="0.00" /> G
					</td>                                       
				</tr>
				<tr>
					<th><h4>写缓存</h4></th>
					<td class="center">
						<fmt:formatNumber value="${storageInfo.nvs_gb/1024}" pattern="0.00" /> G
					</td>                                       
				</tr>
				<tr>
					<th><h4>物理磁盘容量</h4></th>
					<td class="center">
						<fmt:formatNumber value="${storageInfo.physical_disk_capacity/1024/1024}" pattern="0.00" /> T
					</td>                                        
				</tr>
				<tr>
					<th><h4>逻辑容量</h4></th>
					<td class="center">
						<fmt:formatNumber value="${storageInfo.total_usable_capacity/1024/1024}" pattern="0.00" /> T
					</td>                                        
				</tr>
				<tr>
					<th><h4>已用逻辑容量</h4></th>
					<td class="center">
						<fmt:formatNumber value="${(storageInfo.total_usable_capacity - storageInfo.unallocated_usable_capacity)/1024/1024}" pattern="0.00" /> T
					</td>                                       
				</tr>
				
			  </tbody>
		 </table>  
		</div>
		<div style="clear: both;"></div>
	</div>
	<!-- 存储系统详细信息结束 -->
	
	<!-- 存储系统性能信息开始 -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					性能
				</h2>
				<div class="box-icon">
					<!--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf3('${subSystemID}','3')"><i class="icon-cog"></i></a>-->
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
				</div>
			</div>
			<div id="queryPage" class="box-content" style="height:150px;"></div>
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
																<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
															</c:when>
															<c:otherwise>
																<c:if test="${prfData.threshold==1}">
																	<span style="${item[fn:toLowerCase(thead.key)] >= prfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
																</c:if>
																<c:if test="${prfData.threshold==0}">
																	<fmtNumber:formate value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/>
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
						<c:if test="${not empty prfData.tbody.data}">
							<script>
								$(function(){
									alert("123");
								});
								$("#StorageInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/storage/StorageAction?func=StoragePrfPage&subSystemID=${subSystemID}&level=3&tablePage=1",divId:'dataContent2'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/storage/StorageAction?func=exportPrefData&subSystemID=${subSystemID}&level=3";
  								$("#exportCSV").attr("href",exurl);
							</script>
						</c:if>
						<c:if test="${empty prfData.tbody.data}">
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
	<!-- 存储系统性能信息结束-->
	
	<!-- 存储系统部件信息开始 -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					部件
				</h2>
				<div class="box-icon">
					<a id='subFreshen' href="javascript:void(0)" class="btn btn-round" title="刷新" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content" style="max-height:810px;" id="subTab">
				<ul class="nav nav-tabs" id="myTab">
					<li class="active" title="port">
						<a href="#portContent">端口(${portCount})</a>
					</li>
					<li class=""  title="disk">
						<a href="#diskContent">磁盘组(${diskCount})</a>
					</li>
					<li class="" title="pool">
						<a href="#poolContent">存储池(${poolCount})</a>
					</li>
					<li class="" title="volume">
						<a href="#volumeContent">卷(${volumeCount})</a>
					</li>
					<li class="" title="storagegroup">
						<a href="#storagegroupContent">存储关系组(${storagegroupCount})</a>
					</li>
					<li class="" title="node">
						<a href="#nodeContent">控制器(${nodeCount})</a>
					</li>
				</ul>
				
				<!-- 端口信息列表 -->
				<div id="perfChart" class="tab-content" style="overflow: visible;min-height:200px;">
					<div class="tab-pane active" id="portContent" style="padding-top:20px;">
						<table class="table table-bordered table-striped table-condensed">
						<thead>
							<tr>
								<th>
									端口名
								</th>
								<th>
									网络地址
								</th>
								<th>
									端口速率
								</th>
								<th>
									端口类型
								</th>				
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty portPage.data}">
									<c:forEach var="item" items="${portPage.data}" varStatus="status">
										<tr>
											<td>
												<a href="${path}/servlet/sr/storageport/StoragePortAction?func=LoadPortInfo&subsystemId=${subSystemID}&portId=${item.port_id}&storageType=${storageInfo.storage_type}">${item.name}</a>
											</td>
											<td>
												${item.network_address}
											</td>
											<td>
												${item.port_speed}
											</td>
											<td>
												${item.type}
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=4>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="portPageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty portPage.data}">
						<script>
							$("#portPageNub").getLinkStr({pagecount:"${portPage.totalPages}",curpage:"${portPage.currentPage}",numPerPage:"${portPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storageport/StoragePortAction?func=AjaxStoragePage&subSystemID=${subSystemID}&storageType=${storageInfo.storage_type}",divId:'portContent'});
						</script>
					</c:if>	
				</div>
				
				<!-- 磁盘组信息列表 -->
				<div class="tab-pane" id="diskContent" style="padding-top:20px;">
					<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								RAID组名
							</th>
							<th>
								RAID种类
							</th>
							<th>
								磁盘端口速度
							</th>
							<th>
								磁盘容量(G)
							</th>
							<th>
								磁盘数
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty diskPage.data}">
								<c:forEach var="item" items="${diskPage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/diskgroup/DiskgroupAction?func=LoadDiskgroupInfo&subsystemId=${subSystemID}&diskgroupId=${item.diskgroup_id}&storageType=${storageInfo.storage_type}">RAID Group ${item.name}</a>
										</td>
										<td>
											${item.raid_level}
										</td>
										<td>
											${item.ddm_speed}
										</td>
										<td>
											<fmt:formatNumber value="${item.ddm_cap/1024}" pattern="0.00"/>
										</td>
										<td>
											${item.width}
										</td>					
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=5>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="diskPageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty diskPage.data}">
					<script>
						$("#diskPageNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/diskgroup/DiskgroupAction?func=AjaxStoragePage&subSystemID=${subSystemID}&storageType=${storageInfo.storage_type}",divId:'diskContent'});
					</script>
				</c:if>
				</div>
				
				<!-- 存储池信息列表 -->
				<div class="tab-pane" id="poolContent" style="padding-top:20px;">
					<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								存储池名
							</th>
							<th>
								RAID种类
							</th>
							<th>
								总逻辑容量(G)
							</th>
							<th>
								已用逻辑容量(G)
							</th>
							<th>
								LUN数量
							</th>
							<th>
								后端磁盘数量
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty poolPage.data}">
								<c:forEach var="item" items="${poolPage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}&storageType=${storageInfo.storage_type}">POOL ${item.name}</a>
										</td>
										<td>
											${item.raid_level}
										</td>
										<td>
											<fmt:formatNumber value="${item.total_usable_capacity/1024}" pattern="0.00" />
										</td>									
										<td>	
											<fmt:formatNumber value="${(item.total_usable_capacity-item.unallocated_capacity)/1024}" pattern="0.00" />
										</td>
										<td>
											${item.num_lun}
										</td>
										<td>
											${item.num_backend_disk}
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
				
				<div id="poolPageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty poolPage.data}">
					<script>
						$("#poolPageNub").getLinkStr({pagecount:"${poolPage.totalPages}",curpage:"${poolPage.currentPage}",numPerPage:"${poolPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/pool/PoolAction?func=AjaxPoolPage&subSystemID=${subSystemID}&storageType=${storageInfo.storage_type}",divId:'poolContent'});
					</script>
				</c:if>
				</div>
				
				<!-- 卷信息列表 -->
				<div class="tab-pane" id="volumeContent" style="padding-top:20px;">
					<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								逻辑卷名
							</th>
							<th>
								逻辑空间(G)
							</th>
							<th>
								实占空间(G)
							</th>
							<th>
								RAID种类
							</th>
							<th>
								所属控制器
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty volumePage.data}">
								<c:forEach var="item" items="${volumePage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=${subSystemID}&volumeId=${item.volume_id}&storageType=${storageInfo.storage_type}">LUN ${item.name}</a>
										</td>
										<td>
											<fmt:formatNumber value="${item.logical_capacity/1024}" pattern="0.00"/>
										</td>
										<td>
											<c:if test="${not empty item.physical_capacity}"><fmt:formatNumber value="${item.physical_capacity/1024}" pattern="0.00"/></c:if>
											<c:if test="${empty item.physical_capacity}">N/A</c:if>
										</td>
										<td>
											${item.raid_level}
										</td>
										<td>
											<c:if test="${empty item.current_owner}">N/A</c:if>
											<c:if test="${not empty item.current_owner}">
												<a href="${path}/servlet/sr/storagenode/StoragenodeAction?func=LoadStoragenodeInfo&subsystemId=${subSystemID}&spId=${item.sp_id}&storageType=${storageInfo.storage_type}">${item.current_owner}</a>
											</c:if>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=4>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="volumePageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty volumePage.data}">
					<script>
						$("#volumePageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage&subSystemID=${subSystemID}&storageType=${storageInfo.storage_type}",divId:'volumeContent'});
					</script>
				</c:if>
				</div>
				
				<!-- 存储关系组信息列表 -->
				<div class="tab-pane" id="storagegroupContent" style="padding-top:20px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								网络地址
							</th>
							<th>
								系统
							</th>
							<th>
								是否可共享
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty storagegroupPage.data}">
								<c:forEach var="item" items="${storagegroupPage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/storagegroup/StoragegroupAction?func=StoragegroupInfo&subsystemId=${subSystemID}&hostgroupId=${item.hostgroup_id}&storageType=${storageInfo.storage_type}">${item.hostgroup_name}</a>
										</td>
										<td>
											${item.uid}
										</td>
										<td>
											${item.model}
										</td>
										<td>
											${item.shareable}
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=4>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="storagegroupPageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty storagegroupPage.data}">
					<script>
						$("#storagegroupPageNub").getLinkStr({pagecount:"${storagegroupPage.totalPages}",curpage:"${storagegroupPage.currentPage}",numPerPage:"${storagegroupPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagegroup/StoragegroupAction?func=AjaxStoragegroupPage&subSystemID=${subSystemID}&storageType=${storageInfo.storage_type}",divId:'storagegroupContent'});
					</script>
				</c:if>
				</div>
				
				<!-- 控制器信息列表 -->
				<div class="tab-pane" id="nodeContent" style="padding-top:20px;">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								控制器名称
							</th>
							<th>
								系统
							</th>
							<th>
								序列号
							</th>
							<th>
								编号
							</th>
							<th>
								卷数量
							</th>
							<th>
								更新时间
							</th>				
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty nodePage.data}">
								<c:forEach var="item" items="${nodePage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/storagenode/StoragenodeAction?func=LoadStoragenodeInfo&subsystemId=${subSystemID}&spId=${item.sp_id}&storageType=${storageInfo.storage_type}">${item.sp_name}</a>
										</td>
										<td>
											${item.model}
										</td>
										<td>
											${item.emc_serial_number}
										</td>
										<td>
											${item.emc_part_number}
										</td>
										<td>
											${item.lun_num}
										</td>
										<td>
											${item.update_timestamp}
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
				<div id="nodePageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty nodePage.data}">
					<script>
						$("#nodePageNub").getLinkStr({pagecount:"${nodePage.totalPages}",curpage:"${nodePage.currentPage}",numPerPage:"${nodePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagenode/StoragenodeAction?func=AjaxNodePage&subSystemID=${subSystemID}&storageType=${storageInfo.storage_type}",divId:'nodeContent'});
					</script>
				</c:if>	
				</div>
				
				</div>
			</div>
		</div>
	</div>
	<!-- 存储系统部件信息开始 -->
</div>
<script>
	function subTabChange(){
		var subSystemID="${subSystemID}";
		$("#subFreshen").show();
		$("#subFreshen").unbind('click');
		var title=$("#subTab li.active")[0].title;
		var jsonVal = {subSystemID:subSystemID,storageType:"${storageInfo.storage_type}"};
		if(title=='port'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/sr/storageport/StoragePortAction?func=AjaxStoragePage",jsonVal,$("#portContent"));
			});
		}else if(title=='disk'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/sr/diskgroup/DiskgroupAction?func=AjaxStoragePage",jsonVal,$("#diskContent"));
			});
		}else if(title=='pool'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/sr/pool/PoolAction?func=AjaxPoolPage",jsonVal,$("#poolContent"));
			});
		}else if(title=='volume'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
			});
		}else if(title=='storagegroup'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/sr/storagegroup/StoragegroupAction?func=AjaxStoragegroupPage",jsonVal,$("#storagegroupContent"));
			});
		}else if(title=='node'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/sr/storagenode/StoragenodeAction?func=AjaxNodePage",jsonVal,$("#nodeContent"));
			});
		}else{
			$("#subFreshen").hide();
		}
	}
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>