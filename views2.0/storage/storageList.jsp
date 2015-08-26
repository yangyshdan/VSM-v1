<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-function" prefix="fn" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/storage.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
$(function(){Highcharts.setOptions({global: {useUTC: false}});});

var arr = ${arr};

function drawStorageCapacity(arr,categories) {
	var storageName = new Array(arr.length);
	$.each(arr,function(i){
		storageName[i] = this.name;
	})
	Storage.capacityScript02({
 		id: "container",
 		title: "存储系统容量使用情况",
 		names: storageName, 
 		series: categories,
 		getURL: function(obj){
 			var type = obj.type.toUpperCase();
 			return ((type == "EMC" || type == "HDS" || type == "NETAPP")? "/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=" : 
 			"/servlet/storage/StorageAction?func=StorageInfo&subSystemID=") + obj.id;
 		}
	});
}

$(function(){  
	//加载容量柱形图
	drawStorageCapacity(arr, ${categories});
	doListRefresh2();
	$("#conTable").tablesorter();
	changeColumn.initCol();
});

function doListRefresh2(){
	var url = "${path}/servlet/storage/StorageAction?func=StoragePrfField";
	loadData(url, {isFreshen: 1}, 
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
		if(json.totalPages > 0){
			var exurl = "${path}/servlet/storage/StorageAction?func=ExportPerfData&level=1";
			$csv.attr({href: exurl});
			$pag.getLinkStr({pagecount: json.totalPages, curpage: json.currentPage, 
				numPerPage: json.numPerPage, isShowJump: true,
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
				divId: ""});
			$pag.find("ul li a.first").first().trigger("click");
		}
		else {
			$csv.attr({ href: "javascript:void(0);" });
			$csv.bind("click", function(){ bAlert("暂无可导出数据！"); });
		}
		if(json.graphType == 1){
			if(json.series){ 
				Public.drawTopn02("prfContent",
					{jsonVal: json.series, 
					getURL: function(point){
						return (point.dbtype == "SR"?
							"/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=" :
							"/servlet/storage/StorageAction?func=StorageInfo&subSystemID=") + point.devId;
					}});
			}
		}
		else {
			Public.drawPrfLine("prfContent", json);
			$("#pTitle").html(function(){
				var str="存储系统性能   (";
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
function trDbClick(id){
	window.location.href = "${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID="+id+"&r="+Math.random();
}
function trDbClick2(id){
	window.location.href = "${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID="+id+"&r="+Math.random();
}

function doRename(){
	MM_openwin4({
		title: "存储设备重命名",
	 	url: window.getRootPath() + "/servlet/storage/StorageAction?func=PreRename",
		width: 700,
		data: {},
		callback: function($myModal){
			$myModal.modal("show");
		}
	});
}

function resetFunc(){
	var $storageType = $("select#storageType2");
	if($storageType.attr("disabled") == undefined){<%--如果不是固定的，那么就重置不让option被选中--%>
		$storageType.find("option:selected").removeAttr("selected");
		$storageType.multiselect("refresh");
	}
	var $device = $("select#device2");
	if($device.attr("disabled") == undefined){
		$device.find("option:selected").removeAttr("selected");
		$device.multiselect("refresh");
	}
	$("select#prfField2 option").eq(0).attr({selected: true});
	$("select#time_type2 option").eq(0).attr({selected: true});
	$("#queryTime button").removeClass("btn-primary");
	$("input#startTime2").val("");
	$("input#endTime2").val("");
	$("input#threValue2").val("");
	$("input#topnValue").val("");
}

//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#hiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/storage/StorageAction?func=AjaxStoragePage", jsonVal, $("#loadcontent"));
}

function doFreshen1(){
	$("#myModal").modal("hide");
	var jsonVal = {};
	var args=$("#hiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/storage/StorageAction?func=AjaxStoragePage", jsonVal, $("#loadcontent"));
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
	
	<%--增加查询条件--%>
	$.ajax({
		url: "${path}/servlet/storage/StorageAction?func=StorageSettingPrf2",
		data: { },
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
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
					<a href="javascript:void(0);" class="btn btn-round" title="选择列" onclick="changeColumn.showCol(this)" data-rel="tooltip"><i class="icon-eye-open"></i> </a>
					<%-- 
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					--%>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="重命名" onclick="doRename();" data-rel="tooltip"><i class="icon icon-color icon-bookmark"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/storage/StorageAction?func=exportStorageConfigData";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<!-- 筛选条件开始 -->
			<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
				<form class="form-horizontal" id="conditionForm">
					<fieldset>
						<div class="control-group" style="margin-bottom: 10px;">
							<table class="table-condensed" width="90%" style="margin: 0px auto;">
								<tbody>
									<tr>
										<td>
											<label class="col-lg-2 control-label" for="storageName" style="width:50px">名称</label>
											<input type="text" class="form-control" id="storageName" name="storageName" style="width: 140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="ipAddress" style="width:70px">IP地址</label>
											<input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="startPoolCap" style="width:70px">池容量</label>
											<input class="form-control" id="startPoolCap" name="startPoolCap" type="text" style="width:60px;margin-left: 10px;"> -
											<input class="form-control" id="endPoolCap" name="endPoolCap" type="text" style="width:60px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="serialNumber" style="width:70px">序列号</label>
					                  		<input class="form-control" id="serialNumber" name="serialNumber" type="text" style="width:140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="startPoolAvailableCap" style="width:100px">池可用容量</label>
											<input class="form-control" id="startPoolAvailableCap" name="startPoolAvailableCap" type="text" style="width:60px;margin-left: 10px;"> -
											<input class="form-control" id="endPoolAvailableCap" name="endPoolAvailableCap" type="text" style="width:60px">
										</td>
									</tr>
									<tr>
										<td colspan="5" style="text-align:center;">
											<button type="button" class="btn btn-primary" onclick="storageFilter();">查询</button>
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
				--%>
				<div class="tab-pane active" id="loadcontent" style="overflow: visible;" id="loadcontent">
					<table class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all" id="conTable">
						<thead>
							<tr>
								<th>名称</th>
								<th>厂商</th>
								<th>IP地址</th>
								<th>状态</th>
								<th>物理磁盘容量(G)</th>
								<th>池容量(G)</th>
								<th>可用池容量(G)</th>
								<th>容量使用(%)</th>
								<th>卷总容量(G)</th>
								<th>已分配卷总容量(G)</th>
								<th>未分配卷总容量(G)</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty storagePage.data}">
									<c:forEach var="item" items="${storagePage.data}" varStatus="status">
										<c:choose>
											<c:when test="${item.storage_type=='EMC' || item.storage_type=='HDS' || item.storage_type=='NETAPP'}">
												<tr>
													<td>
														<a href="${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.name}</a>
													</td>
													<td>${item.vendor_name}</td>
													<td>
														<c:if test="${not empty item.ip_address}">
															<c:if test="${fn:contains(item.ip_address,',')}">
																<a title="${fn:split(item.ip_address,',')[0]}" href="http://${fn:split(item.ip_address,',')[0]}" target="_blank">${fn:split(item.ip_address,',')[0]}</a>,<a title="${fn:split(item.ip_address,',')[1]}" href="http://${fn:split(item.ip_address,',')[1]}" target="_blank">${fn:split(item.ip_address,',')[1]}</a>
															</c:if>
															<c:if test="${not fn:contains(item.ip_address,',')}">
																<a title="${item.ip_address}" href="http://${item.ip_address}" target="_blank">${item.ip_address}</a>
															</c:if>
														</c:if>
													</td>
													<td>
														<c:if test="${empty item.operattonal_status}">
														<cs:cstatus value="Normal" />
														</c:if>
														<c:if test="${not empty item.operattonal_status}">
														<cs:cstatus value="${item.operattonal_status}" />
														</c:if>
													</td>
													<td>
														<fmt:formatNumber value="${item.physical_disk_capacity/1024}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${item.total_usable_capacity/1024}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${item.unallocated_usable_capacity/1024}" pattern="0.00" />
													</td>
													<td >
														<cs:isProgress total="${item.total_usable_capacity}" available="${item.total_usable_capacity-item.unallocated_usable_capacity}"  warning="60" error="85"/>
													</td>
													<td>
														<fmt:formatNumber value="${item.total_lun_capacity/1024}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${(item.total_lun_capacity - item.unmapped_lun_capacity)/1024}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${item.unmappped_lun_capacity/1024}" pattern="0.00" />
													</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td>
														<a title="${item.the_display_name}" href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.the_display_name}</a>
													</td>
													<td>${item.vendor_name}</td>
													<td>
														<c:if test="${not empty item.ip_address}">
															<c:if test="${fn:contains(item.ip_address,',')}">
																<a title="${fn:split(item.ip_address,',')[0]}" href="http://${fn:split(item.ip_address,',')[0]}" target="_blank">${fn:split(item.ip_address,',')[0]}</a>,<a title="${fn:split(item.ip_address,',')[1]}" href="http://${fn:split(item.ip_address,',')[1]}" target="_blank">${fn:split(item.ip_address,',')[1]}</a>
															</c:if>
															<c:if test="${not fn:contains(item.ip_address,',')}">
																<a title="${item.ip_address}" href="http://${item.ip_address}" target="_blank">${item.ip_address}</a>
															</c:if>
														</c:if>
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
														<fmt:formatNumber value="${item.the_storage_pool_available_space}" pattern="0.00" />
													</td>
													<td >
														<cs:isProgress total="${item.the_allocated_capacity+item.the_available_capacity}" 
														available="${item.the_allocated_capacity}" warning="60" error="85"/>														
													</td>
													<td>
														<fmt:formatNumber value="${item.the_volume_space}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${item.the_assigned_volume_space}" pattern="0.00" />
													</td>
													<td>
														<fmt:formatNumber value="${item.the_unassigned_volume_space}" pattern="0.00" />
													</td>
												</tr>
											</c:otherwise>
										</c:choose>
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
					<div id="storageListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty storagePage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#storageListpageNub").getLinkStr({
								pagecount:"${storagePage.totalPages}",
								curpage:"${storagePage.currentPage}",
								numPerPage:"${storagePage.numPerPage}",
								isShowJump:true,
								ajaxRequestPath:"${path}/servlet/storage/StorageAction?func=AjaxStoragePage&"+param,
								divId:'loadcontent'
							});
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
				<h2 id="pTitle">存储系统性能</h2>
				<div class="box-icon">
					<!--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Storage.settingPrf2()" data-rel="tooltip"><i class="icon-cog"></i></a>-->
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
					<div class="tab-pane" id="dataContent2" style="padding-top:10px;">
						<table class="table table-bordered table-striped table-condensed" id="conTable52207">
							<thead><tr></tr></thead>
							<tbody></tbody>
						</table>
						<div id="HypervisorInfopageNub" class="pagination pagination-centered"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
	<!-- 性能结束  -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>