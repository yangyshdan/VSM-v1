<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/storage.js"></script>
<script type="text/javascript">
$(function(){Highcharts.setOptions({global: {useUTC: false}});});
var arr = ${arr};
$(function(){  //加载容量柱形图
	var storageName = new Array(arr.length);
	$.each(arr,function(i){
		storageName[i] = this.name;
	})
	var categories = ${categories};
	Storage.capacityScript(storageName,categories);
	doListRefresh2();
	//var jsonVal = ${prfData};
	//Public.drawPrfLine("prfContent",jsonVal);
});
function doListRefresh2(){
	loadData("${path}/servlet/storage/StorageAction?func=StoragePrfField",{isFreshen:1},$("#prfContent"),false,false,false,true,
		function(data){
			var json = eval("("+data+")");
			Public.drawPrfLine("prfContent",json);
			$("#pTitle").html(function(){
				var str="存储系统性能  (";
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
function trDbClick(id){
	window.location.href = "${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID="+id+"&r="+Math.random();
}
function trDbClick2(id){
	window.location.href = "${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID="+id+"&r="+Math.random();
}
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#hiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/storage/StorageAction?func=AjaxStoragePage",jsonVal,$("#loadcontent"));
}

//数据查询
function storageFilter(){
	var startPoolCap = $("input[name='startPoolCap']").val();
	var endPoolCap = $("input[name='endPoolCap']").val();
	var startPoolAvailableCap = $("input[name='startPoolAvailableCap']").val();
	var endPoolAvailableCap = $("input[name='endPoolAvailableCap']").val();
	var res = /^\d*$/;
	if(!res.test(startPoolCap)||!res.test(endPoolCap)||!res.test(startPoolAvailableCap)||!res.test(endPoolAvailableCap)){
		bAlert("请输入有效容量");
		return false;
	}
	if(endPoolCap>0 && startPoolCap>=endPoolCap){
		bAlert("请输入有效的池已用空间范围");
		return false;
	}
	if(endPoolAvailableCap>0 && startPoolAvailableCap>=endPoolAvailableCap){
		bAlert("请输入有效的池可用空间范围");
		return false;
	}
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/storage/StorageAction?func=AjaxStoragePage",jsonVal,$("#loadcontent"));
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
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">设备列表</a>
		</li>
		<li class="">
			<a href="#prfTab">性能曲线</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<div class="tab-pane active" id="dataTab">
	<!-- 容量使用情况开始 -->
	<div class="box span12">
		<div style="width: 10%; float: left;height:200px; line-height:220px;" >
			<img src="${path}/resource/img/project/storage.png"/>
		</div>
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<!-- 容量使用情况结束 -->
	<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span10">
			<div class="box-header well">
				<h2>
					存储系统列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/storage/StorageAction?func=exportStorageConfigData";
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
					                  <label class="col-lg-2 control-label" for="storageName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="storageName" name="storageName" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="ipAddress" style="width:80px">IP地址</label>
					                  <input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 20px;">
					              </div> 
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startPoolCap" style="width:80px">池容量</label>
					                  <input class="form-control" id="startPoolCap" name="startPoolCap" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endPoolCap" name="endPoolCap" type="text" style="width:60px;">
					              </div>
					               <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="serialNumber" style="width:80px">序列号</label>
					                  <input class="form-control" id="serialNumber" name="serialNumber" type="text" style="width:140px;margin-left: 20px;">
					              </div>
					               <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startPoolAvailableCap" style="width:80px">池可用容量</label>
									  <input class="form-control" id="startPoolAvailableCap" name="startPoolAvailableCap" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endPoolAvailableCap" name="endPoolAvailableCap" type="text" style="width:60px">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button type="button" class="btn btn-primary" onclick="storageFilter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div class="tab-pane active" id="loadcontent" style="text-align: center;overflow-y: hidden;">
					<table id="conTable" class="table table-bordered table-striped table-condensed spetable"  style="word-break:break-all">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									IP地址
								</th>
								<th>
									状态
								</th>
								<th>
									物理磁盘容量(G)
								</th>
								<th>
									池容量(G)
								</th>
								<th>
									已用池容量(G)
								</th>
								<th>
									可用池容量(G)
								</th>
								<th>
									卷总容量(G)
								</th>
								<th>
									已分配卷总容量(G)
								</th>
								<th>
									未分配卷总容量(G)
								</th>
								<th>
									最近探查时间
								</th>
								<th>
									缓存
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty storagePage.data}">
									<c:forEach var="item" items="${storagePage.data}" varStatus="status">
										<c:choose>
											<c:when test="${item.vendor_name=='EMC'}">
												<tr>
													<td>
														<a title="${item.name}" href="${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.name}</a>
														
													</td>
													<td>
														${item.ip_address}
													</td>
													<td>
														<cs:cstatus value="${item.operattonal_status}" />
													</td>
													<td>
														<fmt:formatNumber value="${item.physical_disk_capacity/1024}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${item.total_usable_capacity/1024}" pattern="0.00" />
													</td>
													<td >
														<cs:isProgress total="${item.total_usable_capacity}" available="${item.total_usable_capacity-item.unallocated_usable_capacity}"/>														
													</td>
													<td>
														<fmt:formatNumber value="${item.unallocated_usable_capacity/1024}" pattern="0.00" />
													</td>
													
													<td>
														<fmt:formatNumber value="${item.total_lun_capacity/1024}" pattern="0.00" />
													</td>
													<td>
														<cs:isProgress total="${item.total_lun_capacity/1024}" available="${item.the_assigned_volume_space}"/>														
													</td>												
													<td>
														<fmt:formatNumber value="${item.unmappped_lun_capacity/1024}" pattern="0.00" />
													</td>
													
													<td>
														${item.update_timestamp}
													</td>
													<td>
														${item.cache_gb/1024}
													</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td>
														<a title="${item.the_display_name}" href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.the_display_name}</a>
													</td>
													<td>
														${item.ip_address}
													</td>
													<td>
														<cs:cstatus value="${item.the_propagated_status}" />
													</td>
													<td>
														<fmt:formatNumber value="${item.the_physical_disk_space}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${item.the_storage_pool_space}" pattern="0.00" />
													</td>
													<td>
														<cs:isProgress total="${item.the_storage_pool_space}" available="${item.the_storage_pool_consumed_space}"/>
																				
													</td>
													<td>
														<fmt:formatNumber value="${item.the_storage_pool_available_space}" pattern="0.00" />
													</td>
													
													<td>
														<fmt:formatNumber value="${item.the_volume_space}" pattern="0.00" />
													</td>
													
													<td>
														<cs:isProgress total="${item.the_volume_space}" available="${item.the_assigned_volume_space}"/>
														
													</td>											
													<td>
														<fmt:formatNumber value="${item.the_unassigned_volume_space}" pattern="0.00" />
													</td>
													
													<td>
														${item.last_probe_time}
													</td>
													<td>
														${item.cache}
													</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=12>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
					</tbody>
					</table>
					<div id="storageListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty storagePage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#storageListpageNub").getLinkStr({pagecount:"${storagePage.totalPages}",curpage:"${storagePage.currentPage}",numPerPage:"${storagePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/storage/StorageAction?func=AjaxStoragePage&"+param,divId:'loadcontent'});
						</script>
					</c:if>
					<c:if test="${empty storagePage.data}">
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
	<!-- 列表结束 -->
	</div>
	<!--性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					存储系统性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf2()" data-rel="tooltip"><i class="icon-cog"></i></a>
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
	<!-- 性能结束  -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>