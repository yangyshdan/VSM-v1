<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/computer.js"></script> 
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path }/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript">
$(function(){
	Highcharts.setOptions({global: {useUTC: false}});
});

var hypervisorId = "${hypervisorId}";
var computerId = "${computerId}";
$(function(){
	//var jsonVal2 = ${conPrfData};
	//Public.drawPrfLine("prfContent",jsonVal2);
	doListRefresh2();
//	Computer.drawTopn(${cputopJson},"cpuprfChart","cpu busy percentage","%");
//	Computer.drawTopn(${memtopJson},"memprfChart","memory used percentage","%");
});
$(function(){
	$("#subFreshen").bind("click",function(){
		loadData(getRootPath()+"/servlet/virtual/VirtualAction?func=AjaxVirtualPage",{computerId:computerId,hypervisorId:hypervisorId},$("#virtualContent"));
	});
	$("#fibreFreshen").bind("click",function(){
		loadData(getRootPath()+"/servlet/hypervisor/HypervisorAction?func=AjaxFibrePage",{computerId:computerId,hypervisorId:hypervisorId},$("#fibreContent"));
	});
	$("#subShowlist").bind("click",function(){
		window.location=getRootPath()+"/servlet/virtual/VirtualAction?func=VirtualPage";
	});
	$("#subTab li").bind('click',function(){
		StroageInfo.subTabChange(computerId);
	});
});
function doListRefresh2(){
	loadData("${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPrfPage",{hypervisorId:hypervisorId,level:3},$("#perfChart2"));
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
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/hv.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${hypervisorInfo.display_name}</span>
			<br>
			<strong>IP:</strong>
			<span>${hypervisorInfo.ip_address}</span>
			<strong style="margin-left: 20px;">Status:</strong>
			<span>${hypervisorInfo.operational_status }</span>
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
			<a href="#dataTab">虚拟机</a>
		</li>
		<li class="">
			<a href="#fibreTab">光纤卡</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
<!-- 物理机详细信息开始 -->
	<div class="tab-pane active" id="detailTab">
	
	<!--  
		<div style="width: 10%;float: left;">
			<div style="margin-top:50px;width:100%;height:30px;text-align: center;">物理机</div>
			<div style=""><img src="${path}/resource/img/project/storage.png"/></div>
		</div>
		-->
		<div class="box-content" style="width: 98%; padding-top:10px;">
		<!-- 物理机详细信息开始 -->
			<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;"> 
			  <tbody>
				<tr>
					<th><h4>厂商</h4></th>
					<td class="center">${hypervisorInfo.vendor}</td>
				</tr>
				<tr>
					<th><h4>内存(MB)</h4></th>
					<td class="center">
						${hypervisorInfo.ram_size}
					</td>                                        
				</tr>
				<tr>
					<th><h4>未分配CPU</h4></th>
					<td class="center">
						${hypervisorInfo.available_cpu}
					</td>                                       
				</tr>			
				<tr>
					<th><h4>磁盘总容量(MB)</h4></th>
					<td class="center">
						<fmt:formatNumber value="${hypervisorInfo.disk_space}" pattern="0.00" />
					</td>                                       
				</tr>				
				<tr>
					<th><h4>更新时间</h4></th>
					<td class="center">
						${hypervisorInfo.update_timestamp}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		 <table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">   
			  <tbody>

				<tr>
					<th><h4>处理器数量</h4></th>
					<td class="center">
						${hypervisorInfo.processor_count}
					</td>                                       
				</tr>	
				<tr>
					<th><h4>未分配内存</h4></th>
					<td class="center">
						<fmt:formatNumber value="${hypervisorInfo.available_mem}" pattern="0.00" />
					</td>                                       
				</tr>
				<tr>
					<th><h4>磁盘剩余容量(MB)</h4></th>
					<td class="center">
						<fmt:formatNumber value="${hypervisorInfo.disk_available_space}" pattern="0.00" />
					</td>                                       
				</tr>
				<tr>
					<th><h4>Status</h4></th>
					<td class="center">
						${hypervisorInfo.operational_status}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 物理机详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!-- 物理机详细信息结束-->
	<!-- 物理机性能开始 -->
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
					物理机性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Computer.settingPrf3('${hypervisorInfo.hypervisor_id}','${computerId}','3')" data-rel='tooltip'><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh2();"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/hypervisor/HypervisorAction?func=ExportPrefData&&computerId=${hypervisorInfo.host_computer_id}&level=3&type=Hypervisor";
						$("#exportCSV").attr("href",exurl);
					</script>
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
						<div id="HypervisorInfopageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty prfData}">
							<script>
								$("#HypervisorInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=HypervisorPrfPage&hypervisorId=${hypervisorId}&level=3&tablePage=1",divId:'dataContent2'});							
							</script>
						</c:if>
						<c:if test="${not empty prfData}">
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
	<!-- 物理机性能结束 -->
	<!-- 物理机事件开始 -->
	<div class="tab-pane" id="alertTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					事件
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${hypervisorId}','${hypervisorId}','Physical');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${hypervisorId}','${hypervisorId}','Physical');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${hypervisorId}','${hypervisorId}','Physical');"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content" id="dAlertContent">
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
									<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Physical')">
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
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Physical')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename } <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
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
						$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${hypervisorId}&topId=${hypervisorId}&resourceType=Physical",divId:'dAlertContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	</div>
	<!-- 物理机事件结束 -->
	<!--物理机下部件虚拟的开始 -->
	<div class="tab-pane" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					虚拟机(${virtualCount})
				</h2>
				<div class="box-icon">
					<a id='subFreshen' href="javascript:void(0)" class="btn btn-round" title="刷新" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a id='subShowlist' href="javascript:void(0)" class="btn btn-round" title="查看所有" data-rel='tooltip'><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content" style="max-height:810px;" id="subTab">
				<div id="perfChart" class="tab-content">
				<!-- 虚拟机开始 -->
					<div class="tab-pane active" id="virtualContent" style="text-align: center;overflow-y: hidden;">
						<table class="table table-bordered table-striped table-condensed">
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
									<c:when test="${not empty virtualPage.data}">
										<c:forEach var="item" items="${virtualPage.data}" varStatus="status">
										<tr>
										<td>
											<a title="${item.display_name}" href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&hypervisorId=${item.hypervisor_id}&vmId=${item.vm_id}">${item.display_name}</a>
										</td>
										<td>
											<a title="${item.host_name}" href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.host_name}</a>
										</td>
										<td>
											${item.ip_address}
										</td>
										<td>
											${item.assigned_cpu_number}
										</td>	
										<td>
											${item.assigned_cpu_processunit}
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
											<td colspan=9>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="virtualListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty virtualPage.data}">
							<script>
								$("#virtualListpageNub").getLinkStr({pagecount:"${virtualPage.totalPages}",curpage:"${virtualPage.currentPage}",numPerPage:"${virtualPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxVirtualPage&computerId=${computerId}&hypervisorId=${hypervisorId}",divId:'virtualContent'});
							</script>
						</c:if>	
					</div>
					<!-- 虚拟机结束 -->
				</div>
			</div>
		</div>
	</div>
	</div>
	<!--物理机下部件虚拟的结束 -->
	<!--物理机下部件光纤卡的开始 -->
	<div class="tab-pane" id="fibreTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					光纤卡(${fibreCount})
				</h2>
				<div class="box-icon">
					<a id='fibreFreshen' href="javascript:void(0)" class="btn btn-round" title="刷新" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content" style="max-height:810px;" id="fibreTab">
				<div id="perfChart" class="tab-content">
				<!-- 光纤卡开始 -->
					<div class="tab-pane active" id="fibrieContent" style="text-align: center;overflow-y: hidden;">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										物理路径
									</th>
									<th>
										WWPN
									</th>
									<th>
										WWNN
									</th>
									<th>
										更新时间
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty fibrePage.data}">
										<c:forEach var="item" items="${fibrePage.data}" varStatus="status">
										<tr>
										<td>
											${item.phys_loc}
										</td>
										<td>
											${item.wwpn}
										</td>	
										<td>
											${item.wwnn}
										</td>										
										<td>
											${item.update_timestamp}
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
						<div id="fibreListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty virtualPage.data}">
							<script>
								$("#fibreListpageNub").getLinkStr({pagecount:"${fibrePage.totalPages}",curpage:"${fibrePage.currentPage}",numPerPage:"${fibrePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=AjaxFibrePage&computerId=${computerId}&hypervisorId=${hypervisorId}",divId:'fibreContent'});
							</script>
						</c:if>	
					</div>
					<!-- 光纤卡结束 -->
				</div>
			</div>
		</div>
	</div>
	</div>
	<!--物理机下部件光纤卡的结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>