<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/switch.js"></script> 
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path }/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript"> 
var switchId = "${switchId}";
function portDbClick(id){  //端口层双击事件
	window.location = "${path}/servlet/switchport/SwitchportAction?func=PortInfo&portId="+id+"&switchId=${switchId}&r="+Math.random();
}
function portFreshen(){  //端口层刷新
	var jsonVal={switchId:"${switchId}"};
	loadData("${path}/servlet/switchport/SwitchportAction?func=AjaxPortPage",jsonVal,$("#portContent"));
}
function portList(){  //端口查看所有
	window.location="${path}/servlet/switchport/SwitchportAction?func=PortPage&switchId=${switchId}&r="+Math.random();
}

$(function(){
	doListRefresh();
});
function doListRefresh(){
	loadData("${path}/servlet/switchs/SwitchAction?func=SwitchPrfPage",{switchId:switchId,level:3},$("#perfChart2"));
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
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/switch.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${switchInfo.the_display_name}</span>
			<br>
			<strong>IP:</strong>
			<span>${switchInfo.ip_address}</span>
			<strong style="margin-left: 20px;">Status:</strong>
			<span>${switchInfo.the_operational_status}</span>
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
	<!--交换机详细信息开始 -->
	<div class="tab-pane active" id="detailTab">
		<!--交换机详细信息表单开始  -->
		<div class="box-content" style="width: 98%;  padding-top:10px;">
			<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;"> 
			  <tbody>
				<tr>
					<th><h4>厂商</h4></th>
					<td class="center">${switchInfo.vendor_name}</td>
				</tr>
				<tr>
					<th><h4>版本</h4></th>
					<td class="center">${switchInfo.version}</td>
				</tr>
				<tr>
					<th><h4>管理地址</h4></th>
					<td class="center">${switchInfo.mgmt_url_addr}</td>
				</tr>
				<tr>
					<th><h4>WWN</h4></th>
					<td class="center">${switchInfo.switch_wwn}</td>
				</tr>
				<tr>
					<th><h4>状态</h4></th>
					<td class="center">${switchInfo.the_propagated_status}</td>
				</tr>
				<tr>
					<th><h4>操作状态</h4></th>
					<td class="center">${switchInfo.the_operational_status}</td>
				</tr>
				<tr>
					<th><h4>描述</h4></th>
					<td class="center">${switchInfo.description}</td>
				</tr>
				
			  </tbody>
		 </table>  
		 <table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>IP地址</h4></th>
					<td class="center">
						${switchInfo.ip_address}
					</td>                                       
				</tr>
				<tr>
					<th><h4>型号</h4></th>
					<td class="center">
						${switchInfo.model_name}
					</td>                                       
				</tr>
				<tr>
					<th><h4>序列号</h4></th>
					<td class="center">
						${switchInfo.serial_number}
					</td>                                        
				</tr>
				<tr>
					<th><h4>是否虚拟</h4></th>
					<td class="center">
						${switchInfo.the_virtual==0?"否":是}
					</td>                                        
				</tr>
				<tr>
					<th><h4>域ID</h4></th>
					<td class="center">
						${switchInfo.domain}
					</td>                                        
				</tr>
				<tr>
					<th><h4>硬件状态</h4></th>
					<td class="center">
						${switchInfo.the_consolidated_status}
					</td>                                       
				</tr>
				<tr>
					<th><h4>更新时间</h4></th>
					<td class="center">
						${switchInfo.update_timestamp}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!--交换机详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!--交换机详细信息结束-->
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					交换机性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Switch.settingPrf('${switchId}','3')" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel="tooltip"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
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
						<div id="SwitchInfopageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty prfData}">
							<script>
								$("#SwitchInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/switchs/SwitchAction?func=SwitchPrfPage&switchId=${switchId}&level=3&tablePage=1",divId:'dataContent2'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/switchs/SwitchAction?func=exportPrefData&switchId=${switchInfo.switch_id}&level=3";
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
					事件预警
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${switchId}','${switchId}','Switch');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${switchId}','${switchId}','Switch');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${switchId}','${switchId}','Switch');"><i class="icon icon-color icon-refresh"></i> </a>
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
									<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Switch')">
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
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Switch')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename } <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
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
						$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${switchId}&topId=${switchId}&resourceType=Switch",divId:'dAlertContent'});
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
					交换机端口(${portCount})
				</h2>
				<div class="box-icon">
					<a href="javascript:portFreshen()" class="btn btn-round" title="刷新" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:portList()" class="btn btn-round" title="查看所有" data-rel="tooltip"><i class="icon icon-color icon-book"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content" style="max-height:810px;">
				<div id="perfChart" class="tab-content" style="overflow: visible;min-height:200px;">
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
												<a title="${item.the_display_name}" href="${path}/servlet/switchport/SwitchportAction?func=PortInfo&portId=${item.port_id}&switchId=${switchId}">${item.the_display_name}</a>
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
							$("#portListpageNub").getLinkStr({pagecount:"${portPage.totalPages}",curpage:"${portPage.currentPage}",numPerPage:"${portPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/switchport/SwitchportAction?func=AjaxPortPage&switchId=${switchId}",divId:'portContent'});
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
