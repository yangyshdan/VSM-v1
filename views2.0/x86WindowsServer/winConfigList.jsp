<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>

<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/storage.js"></script>
<script src="${path}/resource/js/project/computer.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script>
<script src="${path}/resource/js/project/topn.js"></script>
<script type="text/javascript">
	$(function() {
		Highcharts.setOptions({ global : { useUTC : false } });
	});
	$(function() {
		$("#conTable").tablesorter();
		changeColumn.initCol();
	});
	//刷新
	function doFreshen() {
		var jsonVal = {};
		var args = $("#hiddenForm").serializeArray();
		$.each(args, function() {
			if($.trim(this.value).length > 0){
				jsonVal[this.name] = this.value;
			}
		});
		loadData("${path}/servlet/x86Windows/X86WindowsServerAction?func=AjaxComputerPage&isVM=${isVM}", jsonVal, $("#loadcontent"));
	}

	//数据查询
	function computerFilter() {
		var $loadcontent = $("#loadcontent"),
		$form = $("#conditionForm"),
		displayName = $form.find("input[name='displayName']").val(),
		startKernelMemory = $form.find("input[name='ipaddress']").val();
		loadData("${path}/servlet/x86Windows/X86WindowsServerAction?func=AjaxComputerPage&isVM=${isVM}", 
			$form.getValue(),
			$loadcontent);
	}

	function clearData() {
		$("button[type='reset']").click();
	}
	
	function settingPrf2(){
		MM_openwin3("设置参数","${path}/servlet/x86WindowsPrf/X86WindowsServerPrfAction?func=ComSettingPrf&computerId=0&level=1", 500, 400, 0);
	}
	
	$(function(){
		doListRefresh();
	});
	function doListRefresh(){
		loadData("${path}/servlet/x86WindowsPrf/X86WindowsServerPrfAction?func=ComputerPrfPage&isVM=${isVM}", {computerId: 0, level:3}, $("#perfChart2"));
	}
	
</script>
<script src="${path}/resource/js/highcharts/highcharts.js"></script>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active"><a href="#dataTab">设备列表</a></li>
		<li class=""><a href="#loadcontent2">性能曲线</a></li>
	</ul>
	<div id="myTabContent" class="tab-content">
		<!-- 数据列表开始 -->
		<div class="tab-pane active" id="dataTab">
			<div class="row-fluid">
				<div class="box span10">
					<div class="box-header well">
						<h2>${title}列表</h2>
						<div class="box-icon">
							<a href="javascript:void(0);" class="btn btn-round" title="选择列" onclick="changeColumn.showCol(this);" data-rel="tooltip"><i class="icon-eye-open"></i></a>
							<a href="javascript:void(0)" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i></a>
							<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
							<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
							<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							<script>
								var exurl = "${path}/servlet/x86Windows/X86WindowsServerAction?func=ExportHypervisorConfigData";
								$("#exportCSV").attr("href", exurl);
							</script>
						</div>
					</div>
					<div class="box-content">
						<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
						<div id="conAlert" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
							<div class="arrow"></div>
							<div class="popover-inner">
								<h3 class="popover-title"> 过滤器 <a class='btn btn-round close' title='关闭' onclick="Public.conAlert()">×</a></h3>
								<div class="popover-content" style="padding: 8px;">
									<form class="form-horizontal" id="conditionForm">
										<fieldset>
											<div class="control-group" style="margin-bottom:10px;">
												<label class="col-lg-2 control-label" for="displayName" style="width: 80px">名称</label>
												<input type="text" class="form-control" id="displayName" name="displayName" style="width:140px; margin-left: 20px;">
											</div>
											<div class="control-group" style="margin-bottom:10px;">
												<label class="col-lg-2 control-label" for="ipaddress" style="width:80px">IP地址</label>
												<input type="text" class="form-control" id="ipaddress" name="ipaddress" style="width:140px;margin-left:20px;">
											</div>
											<input type="hidden" name="isVM" value="${isVM}">
											<%--<div class="control-group" style="margin-bottom:10px;">
												<label class="col-lg-2 control-label" for="startDate" style="width:80px">修改日期</label>
												<input type="text" class="form-control" id="startDate" name="startDate" style="width:60px;margin-left:10px;">
												<input type="text" class="form-control" id="endDate" name="endDate" style="width:60px;margin-left:10px;">
											</div>--%>
											<div class="form-actions" style="padding-left:80px;margin-top:10px;margin-bottom:0px;padding-bottom:5px;padding-top:5px;">
												<button type="button" class="btn btn-primary" onclick="computerFilter();">查询</button>
												<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
											</div>
										</fieldset>
									</form>
								</div>
							</div>
						</div>
						<div class="tab-pane active" id="loadcontent" style="text-align: center; overflow: visible;">
							<table id="conTable" class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
								<thead>
									<tr>
										<th>名称</th>
										<th>IP地址</th>
										<th>操作系统版本</th>
										<th>CPU架构</th>
										<th>处理器类型</th>
										<th>厂商</th>
										<th>型号</th>
										<th>内存(MB)</th>
										<th>操作状态</th>
										<th>更新时间</th>
									</tr>
								</thead>
								<tbody>
									<c:choose>
										<c:when test="${not empty computerPage.data}">
											<c:forEach var="item" items="${computerPage.data}" varStatus="status">
												<tr>
													<td><a href="${path}/servlet/x86Windows/X86WindowsServerAction?func=X86WindowsServerInfo&computerId=${item.computer_id}&isVM=${isVM}">${item.display_name}</a></td>
													<td>${item.ip_address}</td>
													<td>${item.os_version}</td>
													<td>${item.cpu_architecture}</td>
													<td>${item.processor_type}</td>
													<td>${item.vendor}</td>
													<td>${item.model}</td>	
													<td><cs:isZeroAndNull value="${item.ram_size}"></cs:isZeroAndNull></td>
													<td>${item.operational_status}</td>
													<td><fmt:formatDate value="${item.update_timestamp}" type="date" pattern="yyyy-MM-dd HH:mm:ss" /></td>
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<tr>
												<td colspan="10">暂无数据！</td>
											</tr>
										</c:otherwise>
									</c:choose>
								</tbody>
							</table>
							<div id="computerListpageNub" class="pagination pagination-centered"></div>
							<c:if test="${not empty computerPage.data}">
								<script>
									var param = $("#conditionForm").serialize();
									$("#computerListpageNub").getLinkStr({
										pagecount : "${computerPage.totalPages}",
										curpage : "${computerPage.currentPage}",
										numPerPage : "${computerPage.numPerPage}",
										isShowJump : true,
										ajaxRequestPath : "${path}/servlet/x86Windows/X86WindowsServerAction?func=AjaxComputerPage&isVM=${isVM}&" + param,
										divId : 'loadcontent'
									});
								</script>
							</c:if>
							<c:if test="${empty computerPage.data}">
								<script>
									$("#exportCSV").unbind();
									$("#exportCSV").attr("href", "javascript:void(0);");
									$("#exportCSV").bind("click", 
										function() {
											bAlert("暂无可导出数据！");
										}
									);
								</script>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--性能开始 -->
	<div class="tab-pane" id="loadcontent2">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">${title}性能</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="settingPrf2()" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div id="prfContent2" style="width:95%;min-height:385px;"></div>
			</div>
		</div>
	</div>
	</div>
	<%--性能结束 --%>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>