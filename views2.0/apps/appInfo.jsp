<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/computer.js"></script> 
<script src="${path}/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
$(function(){
	Highcharts.setOptions({global: {useUTC: false}});
});
var fappId="${fappId}";
$(function () {
	doListRefresh2();

});
//刷新
function doFreshen(){
	loadData("${path}/servlet/apps/AppsAction?func=AjaxVirtual",jsonVal,$("#virtualContent"));
}
//清除
function clearData(){
	$("button[type='reset']").click();
}
$(clearData);

function doListRefresh2(){
	loadData("${path}/servlet/apps/AppsAction?func=AppsPrfPage",{fappId:fappId,level:3},$("#perfChart2"));
}
function doAlertFilter(){
	loadData("${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",{resourceId:fappId,topId:fappId,resourType:"App"},$("#dAlertContent"));
}
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
		<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/app.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${appInfo.fname} </span>
		</li>
</ul>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">虚拟机</a>
		</li>
		<li class="">
			<a href="#prfTab">性能曲线</a>
		</li>
		<li class="">
			<a href="#alertTab">事件</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 虚拟机列表开始 -->
	<div class="tab-pane active" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					虚拟机列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/virtual/VirtualAction?func=exportVirtualConfigData";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
				<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
				<div class="tab-pane active" id="virtualContent">
					<table class="table table-bordered table-striped table-condensed" id="conTable">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									所属物理机
								</th>
								<th>
									IP地址
								</th>
								<th>
									CPU逻辑个数
								</th>
								<th>
									CPU物理个数
								</th>
								<th>
									总内存(MB)
								</th>
								<th>
									磁盘总容量(MB)
								</th>
								<th>
									磁盘剩余容量(MB)
								</th>
								<th>
									更新时间
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty dbPage.data}">
									<c:forEach var="item" items="${dbPage.data}" varStatus="status">
										<tr>
											<td>
												<a href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=${item.hypervisor_id}&vmId=${item.vm_id}&computerId=${item.computer_id}">${item.display_name}</a>
											</td>
											<td>
												<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.host_name}</a>
											</td>
											<td>
												${item.ip_address}
											</td>
											<td>
												${item.assigned_cpu_number}
											</td>	
											<td>
												${item.assigned_cpu_processunit/100}
											</td>								
											<td>
												${item.total_memory}
											</td>	
											<td>
												${item.disk_space}
											</td>									
											<td>
												${item.disk_available_space}
											</td>									
											<td>
												${item.update_timestamp}
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
					<div id="virtualListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty dbPage.data}">
						<script>
							$("#virtualListpageNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/apps/AppsAction?func=AjaxVirtual&fappId=${fappId}",divId:'virtualContent'});
						</script>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	</div>
	<!-- 虚拟机列表结束 -->
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					应用性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf6('${fappId}','3')" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doListRefresh2()"><i class="icon icon-color icon-refresh" data-rel="tooltip"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel="tooltip"></i></a>
					<script>
						var exurl = "${path}/servlet/virtual/VirtualAction?func=ExportPrefData&&computerId=${virtualInfo.computer_id}&level=3&hypervisorId=${virtualInfo.hypervisor_id}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content" style="min-height:300px;">
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
						<div id="prfContent2" style="width: 95%; height: 350px;"></div>
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
						<div id="virtualInfopageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty prfData}">
							<script>
							$("#virtualInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/apps/AppsAction?func=AppsPrfPage&fappId=${fappId}&level=3&tablePage=1",divId:'dataContent2'});
  							
							</script>
						</c:if>
						<c:if test="${empty prfData}">
							<script>
								$("#exportCSV").unbind();
								$("#exportCSV").attr("href","javascript:void(0);");
								$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！++++++")});
							</script>
						</c:if>
					</div>
					<!-- 性能数据切换页结束 -->
				</div>
			</div>
		</div>
		<!--/span-->
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
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doAlertFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content" id="dAlertContent">
				<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th style="width: 20px;">
								<label class="checkbox inline">
									<input type="checkbox" onclick="DeviceAlert.doAlertCheck(this.checked);">
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
							<th style="width: 150px;">
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
									<tr>
										<td>
											<label class="checkbox inline">
												<input type="checkbox" value="${item.fid}"  name="dAlertCheck">
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
												<c:when test="${item.flogtype == 3}">硬件告警</c:when>
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
											<a href="javascript:goToEventDetailPage('${item.ftopid}','${item.ftoptype}', '${item.fresourceid}')">${item.fresourcename}</a>
										</td>
										<td>
											<a href="#"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename} <br/>${item.fdetail}" title="详细信息">
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
						$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${fappId}&topId=${fappId}&resourceType=App",divId:'dAlertContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	</div>
	<!-- 事件结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>