<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/computer.js"></script> 
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path }/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript"> 
var hypervisorId = "${hypervisorId}";
$(function(){
	doListRefresh2();
//	Computer.drawTopn(${cputopJson},"cpuprfChart","cpu busy percentage","%");
//	Computer.drawTopn(${memtopJson},"memprfChart","memory used percentage","%");
});
function doListRefresh2(){
	loadData("${path}/servlet/virtual/VirtualAction?func=VirtualPrfPage",{level:3,hypervisorId:"${virtualInfo.hypervisor_id}",vmId:"${virtualInfo.vm_id}"},$("#perfChart2"));
}

function doListRefresh3(){
	loadData("${path}/servlet/virtual/VirtualAction?func=AjaxDiskPage",{vmId:"${virtualInfo.vm_id}"},$("#diskContent"));
}
</script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>
		<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/host.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${virtualInfo.display_name} </span>
			<br>
			<strong>IP:</strong>
			<span>${virtualInfo.ip_address}</span>
			<strong>系统架构:</strong>
			<span>${virtualInfo.targeted_os}</span>
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
			<a href="#diskTab">磁盘</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 虚拟机详细信息开始 -->
	<div class="tab-pane active" id="detailTab">
		<!-- 虚拟机详细信息表单开始  -->
		<div class="box-content" style="width: 98%;  padding-top:10px;">
			<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;"> 
			  <tbody>
				<tr>
					<th><h4>物理机名称</h4></th>
					<td class="center"><a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${virtualInfo.hypervisor_id}">${virtualInfo.host_name}</a></a></td>
				</tr>
				<tr>
					<th><h4>总内存(MB)</h4></th>
					<td class="center">${virtualInfo.total_memory}</td>                                       
				</tr>
				<tr>
					<th><h4>磁盘总容量(MB)</h4></th>
					<td class="center">${virtualInfo.disk_space}</td>
				</tr>
				<tr>
					<th><h4>磁盘剩余容量(MB)</h4></th>
					<td class="center">${virtualInfo.disk_available_space}</td>
				</tr>
				<tr>
					<th><h4>工作状态</h4></th>
					<td class="center">${virtualInfo.operational_status}</td>                                       
				</tr>
				<tr>
				
				
				<tr>
					<th><h4>更新时间</h4></th>
					<td class="center">${virtualInfo.update_timestamp}</td>
				</tr>
				
			  </tbody>
		 </table>  
		 <table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>CPU逻辑个数</h4></th>
					<td class="center">${virtualInfo.assigned_cpu_number}</td>
				</tr>
				<tr>
					<th><h4>CPU物理个数</h4></th>
					 	<td class="center">${virtualInfo.assigned_cpu_processunit}</td>
				</tr>
					<th><h4>最大CPU数量</h4></th>
					<td class="center">${virtualInfo.maximum_cpu_number}</td>
				</tr>
				<tr>
					<th><h4>最小CPU数量</h4></th>
					<td class="center">${virtualInfo.minimum_cpu_number}</td>
				</tr>
				<tr>
					<th><h4>最大处理单元数</h4></th>
					<td class="center">
						<fmt:formatNumber var="maxcp" value="${virtualInfo.maximum_cpu_processunit/100}" pattern="0.00"/>
						<cs:isZeroAndNull value="${maxcp}"></cs:isZeroAndNull>	
					</td>
				</tr>
				<tr>
					<th><h4>最小处理单元数</h4></th>
					<td class="center">
						<fmt:formatNumber var="mcp" value="${virtualInfo.minimum_cpu_processunit/100}" pattern="0.00"/>
						<cs:isZeroAndNull value="${mcp}"></cs:isZeroAndNull>
					</td>
				</tr>
				

				
				
			  </tbody>
		 </table>  
		</div>
		<!-- 虚拟机详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!-- 虚拟机详细信息结束-->
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<!--  
	<div class="row-fluid">
		<div class="box span6">
			<div id="cpuprfChart" style="width: 45%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
			<div style="clear: both;"></div>
		</div>
		<div class="box span6">
			<div id="memprfChart" style="width: 45%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
			<div style="clear: both;"></div>
		</div>
	</div>
	-->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					虚拟机性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf5('${virtualInfo.vm_id}','${virtualInfo.hypervisor_id}','${virtualInfo.computer_id}','3')" data-rel="tooltip"><i class="icon-cog"></i></a>
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
							$("#virtualInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=VirtualPrfPage&computerId=${virtualInfo.vm_id}&level=3&tablePage=1",divId:'dataContent2'});
  							
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
					事件预警
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${hypervisorId}','${virtualInfo.vm_id}','Virtual');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${hypervisorId}','${virtualInfo.vm_id}','Virtual');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${hypervisorId}','${virtualInfo.vm_id}','Virtual');"><i class="icon icon-color icon-refresh"></i> </a>
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
							<c:when test="${not empty logPage.data}">
								<c:forEach var="item" items="${logPage.data}" varStatus="status">
									<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Virtual')">
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
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Virtual')" data-placement="left" data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename } <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
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
				<c:if test="${not empty logPage.data}">
					<script>
						$("#alertListNub").getLinkStr({pagecount:"${logPage.totalPages}",curpage:"${logPage.currentPage}",numPerPage:"${logPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&topId=${hypervisorId}&resourceId=${virtualInfo.vm_id}&resourceType=Virtual",divId:'dAlertContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	</div>
	<!-- 事件结束 -->
	<!-- 磁盘开始 -->
	<div class="tab-pane" id="diskTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					磁盘
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${hypervisorId}','${virtualInfo.vm_id}','Virtual');"><i class="icon icon-color icon-refresh"></i> </a>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;" id="diskContent">
				<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th style="width: 20px;">
										名称
									</th>
									<th style="width: 50px;">
										物理路径
									</th>
									<th style="width: 10px;">
										状态
									</th>
									<th style="width: 20px;">
										更新时间
									</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty diskPage.data}">
								<c:forEach var="item" items="${diskPage.data}" varStatus="status">
									<tr>
												<td>
													${item.disk_name}
												</td>
												<td>
													${item.physloc}
												</td>
												<td>
													${item.status}
												</td>
												<td>
													<fmt:formatDate value="${item.update_timestamp}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
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
				
				<div class="pagination pagination-centered">
					<ul id="disktListNub"></ul>
				</div>
				<c:if test="${not empty diskPage.data}">
					<script>
						$("#disktListNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxDiskPage&vmId=${vmId}",divId:'diskContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	</div>
	<!-- 磁盘结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>
