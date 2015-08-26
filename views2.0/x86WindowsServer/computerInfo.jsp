<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/computer.js"></script>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
	$(function() {
		Highcharts.setOptions({ global : { useUTC : false } });
	});
	
	

	Date.prototype.Format = function(fmt) {
		  var _h = this.getHours();
		  var o = { 
			"M+" : this.getMonth() + 1,                 //月份 
			"d+" : this.getDate(),                    //日 
			"H+" : _h,
			"h+" : _h - 12 > 0? _h - 12 : _h,                   //小时 
			"m+" : this.getMinutes(),                 //分 
			"s+" : this.getSeconds(),                 //秒 
			"q+" : Math.floor((this.getMonth()+3)/3), //季度 
			"S"  : this.getMilliseconds()             //毫秒 
		  }; 
		  if(/(y+)/.test(fmt)) 
			fmt = fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		  for(var k in o){
			if(new RegExp("("+ k +")").test(fmt)){
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
			}
		  }
		  return fmt; 
	};
	
	function parseToDate(strTime){
		var arr=strTime.split(" ");
		if(arr.length>=2)
		{
			var arr1=arr[0].split("-");
			var arr2=arr[1].split(":");
		}
		else
			return null;
		if(arr1.length >= 3 && arr2.length >= 3){
			var b=new Date(arr1[0],arr1[1],arr1[2],arr2[0],arr2[1],arr2[2]);
			return b;
		}
		else { return null; }
	};
	
	/*
		options = {
			url: '',
			data: {},
	  		colsToUnits: { X86_1: "KB/sec", X86_90: "ss"},
			columns: ["X86_1", "X86_90"],
			colsToLegendNames: { X86_1: "磁盘速率", X86_90: "网络流量"},
		}
	 */
	loadDatagrid = function(options){
		$.ajax({
			url: options.url,
			data: options.data,
			type: "post",
			dataType: "json",
			success: function(jsonData){
				if(jsonData.success){
					var colsToUnits = options.colsToUnits,
						columns = options.columns,
						colsToLegendNames = options.colsToLegendNames;
					var $div = $("#computerPrfContent");
					<%-------加表格标题-------%>
					var $tr = $("#computerPrfContent>table>thead>tr").first();
					$tr.find("th").remove(); <%--移除，重新加入--%>
					$tr.append($("<th>").text("设备名称"));
					for(var i = 0, len = columns.length; i < len; ++i){
						$tr.append($("<th>").text(colsToLegendNames[columns[i]] + "(" + colsToUnits[columns[i]] + ")"));
					}
					$tr.append($("<th>").text("更新时间"));
					<%-------加表格内容-------%>
					var tbodyPage = jsonData.value;
					var tbodyData = tbodyPage.data;
					var $tbody = $("#computerPrfContent tbody");
					if(tbodyData != null && tbodyData != undefined && tbodyData.length > 0){
						$tbody.find("tr").remove();
						$.each(tbodyData, function(index, rowData){
							var $tr = $("<tr>").appendTo($tbody);
							$tr.append($("<td>").text(rowData.ele_name));
							for(var i = 0, len = columns.length; i < len; ++i){
								$tr.append($("<td>").text(rowData[columns[i]]));
							}
							$tr.append($("<td>").text(rowData.prf_timestamp));
						});
						
						$("#computerPrfListpageNub").getLinkStr({
							pagecount : tbodyPage.totalPages,
							curpage : tbodyPage.currentPage,
							numPerPage : tbodyPage.numPerPage,
							isShowJump : true,
							ajaxRequestPath : "${path}/servlet/x86Windows/X86WindowsServerAction?func=AjaxComputerPrfPage&computerId=" + tbodyPage.computerId,
							divId : 'computerPrfContent'
						 });
					}
					else {
						$("<tr>").append($("<td>").attr("colspan", columns.length + 2).text("暂无数据！")).appendTo($tbody);
					}
				}
				else {
					
				}
			}
		});
	}
	
	var computerId = "${computerId}";
	
	$(function() {
		$("#subFreshen").bind("click",
			function() {
				loadData(
					getRootPath() + "/servlet/x86Windows/X86WindowsServerAction?func=AjaxComputerPrfPage&computerId=${computerId}",
					{ computerId : computerId }, $("#computerPrfContent"));
			});
		$("#subTab li").bind('click', function() {
			//StroageInfo.subTabChange(computerId);
		});
	});
	/*
	function doListRefresh(){
		loadData("${path}/servlet/x86WindowsPrf/X86WindowsServerPrfAction?func=AjaxComputerPrfPage", {computerId: "${computerId}", level:3}, $("#perfChart2"));
	}*/
	function doAlertFilter() {
		loadData("${path}/servlet/alert/DeviceAlertAction?func=AjaxPage", {
			resourceId : computerId,
			topId : computerId,
			resourType : "Computer"
		}, $("#dAlertContent"));
	}
	
	function showOptionDialog(computerId, level){ // ShowCurveAndContentDialog
		MM_openwin3("设置参数","${path}/servlet/x86WindowsPrf/X86WindowsServerPrfAction?func=ComSettingPrf&computerId=" 
			+ computerId + "&level=" + level, 500, 400, 0);
	}
	
	$(function(){
		doListRefresh();
	});
	function doListRefresh(){
		loadData("${path}/servlet/x86WindowsPrf/X86WindowsServerPrfAction?func=ComputerPrfPage", {computerId: computerId, level:3}, $("#perfChart2"));
	}
</script>
<script src="${path}/resource/js/highcharts/highcharts.js"></script>
<ul class="dashboard-list" style="margin-bottom: 10px;">
	<li style="padding-top: 0px; padding-bottom: 20px;">
		<a href="#"> <img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/hv.png" alt="ComputerSystem"> </a>
		<span style="font-size: 25px;">${computerInfo.display_name}</span>
		<br>
		<strong>IP:</strong>
		<span>
			<c:choose>
				<c:when test="${empty computerInfo.ip_address}">Unknown</c:when>
				<c:otherwise>${computerInfo.ip_address}</c:otherwise>
			</c:choose>
		</span>
		<strong style="margin-left: 20px;">Status:</strong>
		<span>${computerInfo.operational_status}</span>
	</li>
</ul>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active"><a href="#detailTab">配置</a></li>
		<li class=""><a href="#alertTab">事件</a></li>
		<li class=""><a href="#computerPrfTab">性能</a></li>
	</ul>
	<div id="myTabContent" class="tab-content">
		<!-- 计算机详细信息开始 -->
		<div id="detailTab" class="tab-pane active">
			<div class="box-content" style="width: 98%; padding-top: 10px;">
				<!-- 计算机详细信息开始 -->
				<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">
					<tbody>
						<tr>
							<th><h4>操作系统名称</h4></th>
							<td class="center">${computerInfo.name}</td>
						</tr>
						<tr>
							<th><h4>IP地址</h4></th>
							<td class="center">${computerInfo.ip_address}</td>
						</tr>
						<tr>
							<th><h4>内存(MB)</h4></th>
							<td class="center"><fmt:formatNumber value="${computerInfo.ram_size}" pattern="0.00"/></td>
						</tr>
						<tr>
							<th><h4>操作系统版本</h4></th>
							 <td class="center">${computerInfo.os_version}</td>
						</tr>
						<tr>
							<th><h4>CPU架构</h4></th>
							 <td class="center">${computerInfo.cpu_architecture}</td>
						</tr>
						<tr>
							<th><h4>型号</h4></th>
							 <td class="center">${computerInfo.model}</td>
						</tr>
						<tr>
							<th><h4>磁盘大小(GB)</h4></th>
							 <td class="center"><fmt:formatNumber value="${computerInfo.disk_space / 1024.0}" pattern="0.00"/></td>
						</tr>
						<tr>
							<th><h4>磁盘可用空间(GB)</h4></th>
							 <td class="center"><fmt:formatNumber value="${computerInfo.disk_available_space / 1024.0}" pattern="0.00"/></td>
						</tr>
						<tr>
							<th><h4>上一次启动时间</h4></th>
							 <td class="center"><fmt:formatDate value="${computerInfo.last_boot_time}" type="date" pattern="yyyy-MM-dd HH:mm:ss" /></td>
						</tr>
					</tbody>
				</table>
				<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">
					<tbody>
						<tr>
							<th><h4>显示名称</h4></th>
							<td class="center">${computerInfo.display_name}</td>
						</tr>
						<tr>
							<th><h4>域名</h4></th>
							<td class="center">${computerInfo.domain_name}</td>
						</tr>
						<tr>
							<th><h4>设备类型</h4></th>
							<td class="center"><c:choose><c:when test="${empty computerInfo.is_virtual}">未知</c:when><c:when test="${computerInfo.is_virtual}">虚拟机</c:when><c:otherwise>物理机</c:otherwise></c:choose></td>
						</tr>
						<tr>
							<th><h4>厂商</h4></th>
							 <td class="center">${computerInfo.vendor}</td>
						</tr>
						<tr>
							<th><h4>处理器类型</h4></th>
							 <td class="center">${computerInfo.processor_type}</td>
						</tr>
						<tr>
							<th><h4>处理器数量</h4></th>
							 <td class="center">${computerInfo.processor_count}</td>
						</tr>
						<tr>
							<th><h4>处理器频率(GHz)</h4></th>
							 <td class="center"><fmt:formatNumber value="${computerInfo.processor_speed / 1000.0}" pattern="0.00"/></td>
						</tr>
						<tr>
							<th><h4>操作状态</h4></th>
							 <td class="center">${computerInfo.operational_status}</td>
						</tr>
						<tr>
							<th><h4>更新时间</h4></th>
							<td class="center"><fmt:formatDate value="${computerInfo.update_timestamp}" type="date" pattern="yyyy-MM-dd HH:mm:ss" /></td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- 计算机详细信息表单结束 -->
			<div style="clear: both;"></div>
		</div>
		<!-- 计算机详细信息结束-->
		<!-- 计算机事件开始 -->
		<div class="tab-pane" id="alertTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2 id="pTitle1">事件(${deviceLogCount})</h2>
						<div class="box-icon">
							<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doAlertFilter();"><i class="icon icon-color icon-refresh"></i> </a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div class="box-content" id="dAlertContent">
						<table class="table table-bordered table-striped table-condensed spetable" style="table-layout: fixed;">
							<thead>
								<tr>
									<th style="width: 20px;">
										<label class="checkbox inline">
											<input type="checkbox" onclick="DeviceAlert.doAlertCheck(this.checked);">
										</label>
									</th>
									<th style="width:130px;">首次发生时间</th>
									<th style="width:130px;">最后发生时间</th>
									<th style="width:55px;">类型</th>
									<th style="width:55px;">重复次数</th>
									<th style="width:90px;">状态</th>
									<th style="width:90px;">级别</th>
									<th style="width:170px;">事件源</th>
									<th>消息</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty dbPage.data}">
										<c:forEach var="item" items="${dbPage.data}" varStatus="status">
											<tr>
												<td>
													<label class="checkbox inline">
														<input type="checkbox" value="${item.fid}" name="dAlertCheck">
													</label>
												</td>
												<td>
													<fmt:formatDate value="${item.ffirsttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss" />
												</td>
												<td>
													<fmt:formatDate value="${item.flasttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss" />
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
														<c:when test="${item.flevel == 0}">
															<span class="label">Info</span>
														</c:when>
														<c:when test="${item.flevel == 1}">
															<span class="label label-warning">Warning</span>
														</c:when>
														<c:when test="${item.flevel == 2}">
															<span class="label label-important">Critical</span>
														</c:when>
													</c:choose>
												</td>
												<td>
													${item.ftopname}
												</td>
												<td>
													<a href="#" data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename} <br/>${item.fdetail}"title="详细信息"> ${item.fdescript} </a>

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
						<c:if test="${not empty dbPage.data}">
							<script>
								$("#alertListNub").getLinkStr(
									{
										pagecount : "${dbPage.totalPages}",
										curpage : "${dbPage.currentPage}",
										numPerPage : "${dbPage.numPerPage}",
										isShowJump : true,
										ajaxRequestPath : "${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${computerId}&topId=${computerId}&resourceType=Physical",
										divId : 'dAlertContent'
									});
							</script>
						</c:if>
					</div>
				</div>
			</div>
		</div>
		<!-- 计算机事件结束 -->
		<!--计算机性能的开始 -->
		<div class="tab-pane" id="computerPrfTab">
			<div class="row-fluid">
				<div class="box span12">
					<div class="box-header well">
						<h2 id="pTitle">${title}性能</h2>
						<div class="box-icon">
							<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="showOptionDialog('${computerId}','3')" data-rel="tooltip"><i class="icon-cog"></i></a>
							<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
							<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel="tooltip"></i></a>
							<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
						</div>
					</div>
					<div class="box-content" style="max-height: 810px;">
						<!-- tab切换标签开始 -->
						<ul class="nav nav-tabs" id="myTab">
							<li class="active"><a href="#loadcontent2">性能曲线</a></li>
							<li class=""><a href="#dataContent2">性能数据</a></li>
						</ul>
						<!-- tab切换标签结束 -->
						<div id="perfChart2" class="tab-content" style="overflow: visible;">
							<!-- 性能曲线切换页开始 -->
							<div class="tab-pane active" id="loadcontent2">
								<div id="prfContent2" style="width: 95%; height: 350px;"></div>
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
						<div id="ComputerInfopageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty prfData}">
							<script>
								$("#ComputerInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,
								ajaxRequestPath:"${path}/servlet/x86WindowsPrf/X86WindowsServerPrfAction?func=ComputerPrfPage&computerId=${computerId}&level=3&tablePage=1",divId:'dataContent2'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/x86WindowsPrf/X86WindowsServerPrfAction?func=exportPrefData&computerId=${computerInfo.computer_id}&level=3";
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
		<!--计算机的结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>