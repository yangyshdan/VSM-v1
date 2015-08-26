<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/storage.js"></script>
<script src="${path}/resource/js/project/computer.js"></script>
<script src="${path}/resource/js/project/topn.js"></script>
<script src="${path}/resource/js/project/devSnmpCfg.js"></script>
<script type="text/javascript">
//刷新
function del3(id, typeId){
	var button = [{func:"deviceDel("+id+", "+typeId+")",text:'确定'}, {func:'doCancle()',text:'取消'}];
	bAlert("是否删除该记录 ?", "操作提示", button);
}

function doListRefresh(){
	$('#myModal').modal('hide');
	var type = $(".active > a").attr("href");
	if (type == "#serverTab") {
		doServerFilter();
	} else if (type == "#storageConfigTab") {
		doStorageConfigFilter();
	} else if (type == "#labraryTab") {
		doLabraryFilter();
	} else if (type == "#storageTab") {
		doStorageFilter();
	} else if (type == "#switchTab") {
		doSwitchFilter();
	} else if (type == "#nasTab") {
		doNasFilter();
	}
}

//服务器配置列表过滤
function doServerFilter() {
	var jsonVal = {};
	var args = $("#serverForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=ServerPage",jsonVal,$("#serverContent"));
}

//存储系统配置列表过滤
function doStorageConfigFilter() {
	var jsonVal = {};
	var args = $("#storageForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=StorageCfgPage",jsonVal,$("#storageConfigContent"));
}

function doLabraryFilter(){
	var jsonVal={};
	var args=$("#conditionForm4").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=AjaxDevicePage", jsonVal, $("#labrarycontent"));
}
function doStorageFilter(){
	var jsonVal={};
	var args=$("#conditionForm1").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=AjaxDevicePage", jsonVal, $("#storagecontent"));
}
function doSwitchFilter(){
	var jsonVal={};
	var args=$("#conditionForm1").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=AjaxDevicePage",jsonVal, $("#switchcontent"));
}
function doArrayFilter(){
	var jsonVal={};
	var args=$("#conditionForm1").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=AjaxDevicePage", jsonVal, $("#arraysitecontent"));
}
function doNasFilter(){
	var jsonVal={};
	var args=$("#conditionForm5").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=AjaxDevicePage", jsonVal, $("#nascontent"));
}

function deviceDel(id,typeId){
	$.ajax({
		url:'${path}/servlet/usercon/UserConAction?func=DeviceDel',
		data:{id:id,typeId:typeId},
		success:function(data){
			if (typeId == 1) {
				doStorageFilter();
			} else if (typeId == 2) {
				doSwitchFilter();
			} else if (typeId == 3) {
				doArrayFilter();
			} else if (typeId == 4) {
				doLabraryFilter();
			} else if (typeId == 5) {
				doNasFilter();
			} else if (typeId == 6) {
				doServerFilter();
			} else if (typeId == 7) {
				doStorageConfigFilter();
			}
			doCancle();
		}
	});
}

$(function(){
	$("#storageTable td").addClass("rc-td");
});
</script>
<script src="${path}/resource/js/highcharts/highcharts.js">
</script>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#serverTab">服务器配置</a>
		</li>
		<li class="">
			<a href="#storageConfigTab">存储系统配置</a>
		</li>
		<li class="">
			<a href="#endPointSnmpCfgTab">终端设备SNMP配置</a>
		</li>
		<!--  <li class="">
			<a href="#labraryTab">磁带库配置</a>
		</li>
		<li class="">
			<a href="#storageTab">存储系统配置</a>
		</li>
		<li class="">
			<a href="#switchTab">交换机配置</a>
		</li>
		<li class="">
			<a href="#arraysiteTab">磁盘阵列配置</a>
		</li>
		<li class="">
			<a href="#nasTab">NAS配置</a>
		</li>-->
	</ul>
	<div id="myTabContent" class="tab-content">
	
	<!-- 服务器配置页面开始 -->
	<div class="tab-pane active" id="serverTab">
		<div class="row-fluid">
			<div class="box span10">
				<div class="box-header well">
					<h2>
						服务器配置列表
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=editServerInfo',540,500,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="刷新" onclick="doServerFilter();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<!-- 筛选条件开始 -->
				<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
					<form class="form-horizontal" id="serverForm">
						<fieldset>
							<div class="control-group" style="margin-bottom: 10px;">
								<table class="table-condensed" width="60%" style="margin: 0px auto;">
									<tbody>
										<tr>
											<td>
												<label class="col-lg-2 control-label" for="serverName" style="width:60px">名称</label>
												<input type="text" class="form-control" id="serverName" name="serverName" style="width: 120px;margin-left: 10px;">
											</td>
											<td>
												<label class="col-lg-2 control-label" for="serverType" style="width:80px">服务器</label>
							 					<select class="form-control" name="serverType" id="serverType" style="width: 140px;margin-left: 10px;">
													<option value="" selected="selected">请选择</option>
								 					<option value="physical">物理机</option>
													<option value="virtual">虚拟机</option>
													<option value="unknown">未知</option>
												</select>
											</td>
											<td>
												<label class="col-lg-2 control-label" for="state" style="width:60px">状态</label>
												<select class="form-control" name="state" id="state" style="width: 140px;margin-left: 10px;">
													<option value='' selected="selected">请选择</option>
													<option value='1'>可用</option>
													<option value='0'>不可用</option>
												</select>
											</td>
										</tr>
										<tr>
											<td colspan="3" style="text-align:center;">
												<button type="button" class="btn btn-primary" onclick="doServerFilter();">查询</button>
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
					<div class="tab-pane active" id="serverContent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										厂商
									</th>
									<th>
										架构类型
									</th>
									<th>
										操作系统
									</th>
									<th>
										虚拟平台类型
									</th>
									<th>
										服务器类型
									</th>
									<th>
										IP地址
									</th>
									<th>
										用户名
									</th>
									<th>
										状态
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty serverPage.data}">
										<c:forEach var="item" items="${serverPage.data}" varStatus="status">
											<tr>
												<td>
													${item.name}
												</td>
												<td>
													${empty item.vendor ? 'N/A' : item.vendor}
												</td>
												<td>
													${empty item.schema_type ? 'N/A' : item.schema_type}
												</td>
												<td>
													${empty item.os_type ? 'N/A' : item.os_type}
												</td>
												<td>
													${empty item.virt_plat_type ? 'N/A' : item.virt_plat_type}
												</td>
												<td>
													<c:if test="${item.toptype == 'physical'}">物理机</c:if>
													<c:if test="${item.toptype == 'virtual'}">虚拟机</c:if>
													<c:if test="${item.toptype == 'unknown'}">未知</c:if>
												</td>
												<td>
													${item.ip_address}
												</td>
												<td>
													${item.user}
												</td>
												<td>
													<c:if test="${item.state == 1}">
														<i class="icon icon-color icon-check"></i>可用
													</c:if>
													<c:if test="${item.state == 0}">
														<i class="icon icon-color icon-close"></i>不可用
													</c:if>
												</td>
												<td>
													<a class="btn btn-info" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=editServerInfo&id=${item.id}',540,500,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},6)"><i class="icon-trash icon-white"></i>删除</a>
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
						<div id="serverPageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty serverPage.data}">
							<script>
								var param = $("#serverForm").serialize();
								$("#serverPageNub").getLinkStr({pagecount:"${serverPage.totalPages}",curpage:"${serverPage.currentPage}",numPerPage:"${serverPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=ServerPage&"+param,divId:'serverContent'});
							</script>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 服务器配置页面结束 -->
	
	<!-- 存储系统配置页面开始 -->
	<div class="tab-pane" id="storageConfigTab">
		<div class="row-fluid">
			<div class="box span10">
				<div class="box-header well">
					<h2>存储系统配置列表</h2>
					<div class="box-icon">
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=EditStorageConfig',500,400,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="刷新" onclick="doStorageConfigFilter();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<!-- 筛选条件开始 -->
				<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
					<form class="form-horizontal" id="storageForm">
						<fieldset>
							<div class="control-group" style="margin-bottom: 10px;">
								<table class="table-condensed" width="50%" style="margin: 0px auto;">
									<tbody>
										<tr>
											<td>
												<label class="col-lg-2 control-label" for="storageName" style="width:60px">名称/型号</label>
												<input type="text" class="form-control" id="storageName" name="storageName" style="width: 120px;margin-left: 10px;">
											</td>
											<td>
												<label class="col-lg-2 control-label" for="storageType" style="width:80px">设备类型</label>
							 					<select class="form-control" name="storageType" id="storageType" style="width: 180px;margin-left: 10px;">
													<option value="" selected="selected">请选择</option>
								 					<c:forEach items="${vsm_devtype}" var="dev_type">
														<c:if test="${dev_type.key == 'EMC' || dev_type.key == 'HDS' || dev_type.key == 'NETAPP'}">
															<option value="${dev_type.key}">${dev_type.value}</option>
														</c:if>
													</c:forEach>
												</select>
											</td>
										</tr>
										<tr>
											<td colspan="2" style="text-align:center;">
												<button type="button" class="btn btn-primary" onclick="doStorageConfigFilter();">查询</button>
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
					<div class="tab-pane active" id="storageConfigContent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>
										名称/型号
									</th>
									<th>
										设备类型
									</th>
									<th>
										IP地址
									</th>
									<th>
										用户名
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty storageCfgPage.data}">
										<c:forEach var="item" items="${storageCfgPage.data}" varStatus="status">
											<tr>
												<td>
													${item.name}
												</td>
												<td>
													${item.storage_type}
												</td>
												<td>
													${item.ctl01_ip}
													<c:if test="${not empty item.ctl02_ip}">
														,${item.ctl02_ip}
													</c:if>
												</td>
												<td>
													${item.user}
												</td>
												<td>
													<a class="btn btn-info" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=EditStorageConfig&id=${item.id}',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},7)"><i class="icon-trash icon-white"></i>删除</a>
												</td>
											</tr>											
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="5">
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
						</tbody>
						</table>
						<div id="storageCfgPageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty storageCfgPage.data}">
							<script>
								var param = $("#storageForm").serialize();
								$("#storageCfgPageNub").getLinkStr({pagecount:"${storageCfgPage.totalPages}",curpage:"${storageCfgPage.currentPage}",numPerPage:"${storageCfgPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=StorageCfgPage&"+param,divId:'storageConfigContent'});
							</script>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 存储系统配置页面结束 -->
	
	<%-- 终端设备SNMP配置 --%>
	<div class="tab-pane" id="endPointSnmpCfgTab">
		<div class="row-fluid">
			<div class="box span10">
				<div class="box-header well">
					<h2>终端设备SNMP配置列表</h2>
					<div class="box-icon">
						<a onclick="SnmpUtil.addSnmpDetail();" href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" data-rel="tooltip"><i class="icon icon-color icon-add"></i></a>
						<a onclick="SnmpUtil.refreshByFilter();" href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="刷新" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<%-- 筛选条件开始 --%>
				<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
					<form class="form-horizontal" id="endPointSnmpCfgForm">
						<fieldset>
							<div class="control-group" style="margin-bottom: 10px;">
								<table class="table-condensed" width="100%" style="margin: 0px auto;">
									<tbody>
										<tr>
											<td>
												<label class="col-lg-2 control-label" for="groupName" style="width:60px">分配组名称</label>
												<input class="form-control" id="groupName" name="groupName" style="width: 120px;margin-left: 10px;">
											</td>
											<td>
												<label class="col-lg-2 control-label" for="ipAddress" style="width:60px">IP地址</label>
												<input class="form-control" id="ipAddress" name="ipAddress" style="width: 120px;margin-left: 10px;">
											</td>
											<td>
												<label class="col-lg-2 control-label" for="snmpVersion" style="width:60px">SNMP版本</label>
												<select class="form-control" id="snmpVersion" name="snmpVersion" style="width:120px;margin-left:10px;">
													<option value="">不限</option>
													<option value="1">V1</option>
													<option value="2c">V2</option>
													<option value="3">V3</option>
												</select>
											</td>
											<td>
												<label class="col-lg-2 control-label" for="snmpEnabled" style="width:80px">状态</label>
							 					<select class="form-control" name="enabled" id="snmpEnabled" style="width: 180px;margin-left: 10px;">
													<option value="-1" selected="selected">不限</option>
								 					<option value="1">启动</option>
								 					<option value="0">禁用</option>
												</select>
											</td>
										</tr>
										<tr>
											<td colspan="4" style="text-align:center;">
												<button type="button" class="btn btn-primary" onclick="SnmpUtil.snmpFilter();">查询</button>
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
				<%-- 筛选条件结束 --%>
				<div class="box-content">
					<div id="snmpContent" class="box-content"  style="overflow:auto;width:98%;min-height:180px;height:600px;">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr align="center">
									<th>分配组名称</th><th width="100px">IP地址</th>
									<th width="80px">SNMP版本</th><th width="100px">SNMP社区</th>
									<th width="80px">用户名</th><th width="110px">设备</th>
									<th width="60px">重试次数</th><th width="50px">状态</th>
									<th width="130px">描述</th>
									<th width="80px">操作</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty snmpPage.data}">
										<c:forEach var="item" items="${snmpPage.data}" varStatus="status">
											<tr>
												<td>
													<%--<a href="javascript:void(0);" onclick="SnmpUtil.showGroupDetailDlg(${item.group_id})" style="cursor:pointer;"></a>--%>
													${item.group_name}
												</td>
												<td>${item.ip_address_v4}</td>
												<td>${item.snmp_version}</td>
												<td>${item.snmp_community}</td>
												<td>${item.snmp_v3_user_name}</td>
												<td>${item.device_type},${item.device_model}</td>
												<td>${item.snmp_retry}</td>
												<td>
													<c:choose>
														<c:when test="${item.enabled}">启动</c:when>
														<c:otherwise>禁用</c:otherwise>
													</c:choose>
												</td>
												<td>${item.description}</td>
												<td>
													<%--<a onclick="SnmpUtil.querySnmpDetailDlg('${item.snmp_id}')" class="btn btn-success" href="javascript:void(0)" title="查看SNMP的详细配置信息"><i class="icon-check icon-white"></i>详细</a>--%>
													<a onclick="SnmpUtil.editSnmpDetailDlg('${item.snmp_id}')" class="btn btn-info" href="javascript:void(0)" title="编辑SNMP的配置信息"><i class="icon-edit icon-white"></i>编辑</a>
													<a onclick="SnmpUtil.deleteSnmpDetail('${item.snmp_id}')" class="btn btn-danger" href="javascript:void(0)" title="删除该SNMP的配置信息"><i class="icon-trash icon-white"></i>删除</a>
												</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr><td colspan="9">暂无数据！</td></tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						
						<div class="pagination pagination-centered">
							<ul id="snmpListNub"></ul>
						</div>
						<c:if test="${not empty snmpPage.data}">
							<script>
								$("#snmpListNub").getLinkStr({
									pagecount:"${snmpPage.totalPages}",
									curpage:"${snmpPage.currentPage}",
									numPerPage:"${snmpPage.numPerPage}",
									isShowJump:true,
									ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=AjaxDeviceSnmpPage",
									divId: "snmpContent"
								});
							</script>
						</c:if>
							
					</div>
				</div>
			</div>
		</div>
	</div>
	<%-- 终端设备SNMP配置 --%>
	
	<!-- 开始 -->
	<div class="tab-pane" id="labraryTab"> 
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						磁带库用户配置信息
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlertAll('labraryIframe','labraryFrom')" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&typeId=4',500,300,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round btn-round" data-rel="tooltip" title="刷新" onclick="doLabraryFilter();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					<iframe id="labraryIframe" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
					<div id="labraryFrom" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
						<div class="arrow"></div>
						<div class="popover-inner">
							<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlertAll('labraryIframe','labraryFrom')">×</a></h3>
							<div class="popover-content" style="padding: 8px;">
						        <form class="form-horizontal" id="conditionForm4">
									<fieldset>
									<input type="hidden" id="typeId" name="typeId" value="4"/>
									  <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="labraryname" style="width:80px">设备名称</label>
						                  <input type="text" class="form-control" id="labraryname" name="devname" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="doLabraryFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane" id="labrarycontent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>磁带库名</th>
									<th>
										用户名
									</th>
									<th>
										设备类型
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty labraryPage.data}">
										<c:forEach var="item" items="${labraryPage.data}" varStatus="status">
											<tr>
												<td>
													${item.device_name}
												</td>
												<td>
													${item.users}
												</td>
												<td>
													${item.dname}
												</td>
												<td>
													<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&id=${item.id}&typeId=4',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="hostDel(${item.id},${item.state})"><i class="icon-trash icon-white"></i>删除</a>
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
						<div id="labrarypageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty labraryPage.data}">
							<script>
								var param = $("#conditionForm4").serialize();
								$("#labrarypageNub").getLinkStr({pagecount:"${labraryPage.totalPages}",curpage:"${labraryPage.currentPage}",numPerPage:"${labraryPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=AjaxDevicePage&"+param,divId:'labrarycontent'});
							</script>
						</c:if>
					</div> 
				</div>
			</div>
		</div>
	</div>
	<!-- 结束 -->
	<!-- 开始 -->
	<div class="tab-pane" id="storageTab"> 
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						存储系统用户配置信息
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlertAll('storageIframe','storageFrom')" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&typeId=1',500,400,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round btn-round" data-rel="tooltip" title="刷新" onclick="doStorageFilter();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					<iframe id="storageIframe" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
					<div id="storageFrom" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
						<div class="arrow"></div>
						<div class="popover-inner">
							<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlertAll('storageIframe','storageFrom')">×</a></h3>
							<div class="popover-content" style="padding: 8px;">
						        <form class="form-horizontal" id="conditionForm1">
									<fieldset>
									<input type="hidden" id="typeId" name="typeId" value="1"/>
									  <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="storagename" style="width:80px">设备名称</label>
						                  <input type="text" class="form-control" id="storagename" name="storagename" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="doStorageFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane" id="storagecontent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										用户名
									</th>
									<th>
										设备类型
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty storagePage.data}">
										<c:forEach var="item" items="${storagePage.data}" varStatus="status">
											<tr>
												<td>
													${item.device_name}
												</td>
												<td>
													${item.users}
												</td>
												<td>
													${item.dname}
												</td>
												<td>
													<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&id=${item.id}&typeId=1',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},1)"><i class="icon-trash icon-white"></i>删除</a>
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
						<div id="storagepageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty storagePage.data}">
							<script>
								var param = $("#conditionForm1").serialize();
								$("#storagepageNub").getLinkStr({pagecount:"${storagePage.totalPages}",curpage:"${storagePage.currentPage}",numPerPage:"${storagePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=AjaxDevicePage&"+param,divId:'storagecontent'});
							</script>
						</c:if>
					</div> 
				</div>
			</div>
		</div>
	</div>
	<!-- 结束 -->
	<!-- 开始 -->
	<div class="tab-pane" id="switchTab"> 
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">交换机用户配置信息</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlertAll('switchIframe','switchFrom')" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&typeId=2',500,400,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round btn-round" data-rel="tooltip" title="刷新" onclick="doSwitchFilter();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					<iframe id="switchIframe" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
					<div id="switchFrom" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
						<div class="arrow"></div>
						<div class="popover-inner">
							<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlertAll('switchIframe','switchFrom')">×</a></h3>
							<div class="popover-content" style="padding: 8px;">
						        <form class="form-horizontal" id="conditionForm2">
									<fieldset>
									<input type="hidden" id="typeId" name="typeId" value="2"/>
									  <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="switchname" style="width:80px">设备名称</label>
						                  <input type="text" class="form-control" id="switchname" name="switchname" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="doSwitchFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane" id="switchcontent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>名称</th>
									<th>用户名</th>
									<th>设备类型</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty switchPage.data}">
										<c:forEach var="item" items="${switchPage.data}" varStatus="status">
											<tr>
												<td>${item.device_name}</td>
												<td>${item.users}</td>
												<td>${item.dname}</td>
												<td>
													<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&id=${item.id}&typeId=2',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},2)"><i class="icon-trash icon-white"></i>删除</a>
												</td>
											</tr>											
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="4">暂无数据！</td>
										</tr>
									</c:otherwise>
								</c:choose>
						</tbody>
						</table>
						<div id="switchpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty switchPage.data}">
							<script>
								var param = $("#conditionForm2").serialize();
								$("#switchpageNub").getLinkStr({pagecount:"${switchPage.totalPages}",curpage:"${switchPage.currentPage}",numPerPage:"${switchPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=AjaxDevicePage&"+param,divId:'switchcontent'});
							</script>
						</c:if>
					</div> 
				</div>
			</div>
		</div>
	</div>
	<!-- 结束 -->
	<!-- 开始 -->
	<div class="tab-pane" id="arraysiteTab"> 
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						磁盘阵列用户配置信息
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlertAll('arraysiteIframe','arraysiteFrom')" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo1&typeId=3',500,400,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round btn-round" data-rel="tooltip" title="刷新" onclick="doArrayFilter();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					<iframe id="arraysiteIframe" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
					<div id="arraysiteFrom" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
						<div class="arrow"></div>
						<div class="popover-inner">
							<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlertAll('arraysiteIframe','arraysiteFrom')">×</a></h3>
							<div class="popover-content" style="padding: 8px;">
						        <form class="form-horizontal" id="conditionForm3">
									<fieldset>
									<input type="hidden" id="typeId" name="typeId" value="3"/>
									  <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="arrayname" style="width:80px">设备名称</label>
						                  <input type="text" class="form-control" id="arrayname" name="arrayname" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="doArrayFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane" id="arraysitecontent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										用户名
									</th>
									<th>
										设备类型
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty arraysitePage.data}">
										<c:forEach var="item" items="${arraysitePage.data}" varStatus="status">
											<tr>
												<td>
													${item.device_name}
												</td>
												<td>
													${item.users}
												</td>
												<td>
													${item.dname}
												</td>
												<td>
													<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo1&id=${item.id}&typeId=3',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},3)"><i class="icon-trash icon-white"></i>删除</a>
												</td>
											</tr>											
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr><td colspan=4>暂无数据！</td></tr>
									</c:otherwise>
								</c:choose>
						</tbody>
						</table>
						<div id="arraysitepageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty arraysitePage.data}">
							<script>
								var param = $("#conditionForm3").serialize();
								$("#arraysitepageNub").getLinkStr({pagecount:"${arraysitePage.totalPages}",curpage:"${arraysitePage.currentPage}",numPerPage:"${arraysitePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=AjaxDevicePage&"+param,divId:'arraysitecontent'});
							</script>
						</c:if>
					</div> 
				</div>
			</div>
		</div>
	</div>
	<!-- 结束 -->
	<!-- 开始 -->
	<div class="tab-pane" id="nasTab"> 
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						NAS用户配置信息
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlertAll('nasIframe','nasFrom')" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&typeId=5',500,400,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round btn-round" data-rel="tooltip" title="刷新" onclick="doNasFilter();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					<iframe id="nasIframe" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
					<div id="nasFrom" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
						<div class="arrow"></div>
						<div class="popover-inner">
							<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlertAll('nasIframe','nasFrom')">×</a></h3>
							<div class="popover-content" style="padding: 8px;">
						        <form class="form-horizontal" id="conditionForm5">
									<fieldset>
									<input type="hidden" id="typeId" name="typeId" value="5"/>
									  <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="nasname" style="width:80px">设备名称</label>
						                  <input type="text" class="form-control" id="nasname" name="nasname" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="doNasFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane" id="nascontent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>名称</th>
									<th>用户名</th>
									<th>设备类型</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty nasPage.data}">
										<c:forEach var="item" items="${nasPage.data}" varStatus="status">
											<tr>
												<td>
													${item.device_name}
												</td>
												<td>
													${item.users}
												</td>
												<td>
													${item.dname}
												</td>
												<td>
													<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('编辑','${path}/servlet/usercon/UserConAction?func=EditDeviceInfo&id=${item.id}&typeId=5',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del3(${item.id},5)"><i class="icon-trash icon-white"></i>删除</a>
												</td>
											</tr>											
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan=4>暂无数据！</td>
										</tr>
									</c:otherwise>
								</c:choose>
						</tbody>
						</table>
						<div id="naspageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty nasPage.data}">
							<script>
								var param = $("#conditionForm5").serialize();
								$("#naspageNub").getLinkStr({
									pagecount:"${nasPage.totalPages}",
									curpage:"${nasPage.currentPage}",
									numPerPage:"${nasPage.numPerPage}",
									isShowJump:true,
									ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=AjaxDevicePage&"+param,
									divId:'nascontent'
								});
							</script>
						</c:if>
					</div> 
				</div>
			</div>
		</div>
	</div>
	<!-- 结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>