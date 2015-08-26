<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path }/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript">
var fabricId = "${fabricId}";
function trDbClick(id){
	window.location.href = "${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId="+id+"&r="+Math.random();
}
function doFreshen(){
	var jsonVal = {fabricId:fabricId};
	loadData("${path}/servlet/fabric/FabricAction?func=AjaxSwitchPage",jsonVal,$("#switchContent"));
}
function subTabChange(id){
		$("#subFreshen").show();
		$("#subShowlist").show();
		$("#subFreshen").unbind('click');
		$("#subShowlist").unbind('click');			
		var title=$("#subTab li.active")[0].title;
		var jsonVal = {fabricId:id};
		if(title=='switch'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/fabric/FabricAction?func=AjaxSwitchPage",jsonVal,$("#switchContent"));
			});
			$("#subShowlist").bind("click",function(){
				window.location="${path}/servlet/switchs/SwitchAction?func=SwitchPage";
			});
		}else if(title=='zset'){
			$("#subFreshen").bind("click",function(){
				loadData("${path}/servlet/zset/ZsetAction?func=AjaxZsetPage",jsonVal,$("#zsetContent"));
			});
			$("#subShowlist").bind("click",function(){
				window.location = "${path}/servlet/zset/ZsetAction?func=ZsetPage";
			});
		}else{
			$("#subFreshen").hide();
			$("#subShowlist").hide();
		}
	}
$(function(){
	var fabricId = "${fabricId}";
	$("#subFreshen").bind("click",function(){
		loadData("${path}/servlet/fabric/FabricAction?func=AjaxSwitchPage",{fabricId:fabricId},$("#switchContent"));
	});
	$("#subShowlist").bind("click",function(){
		window.location="${path}/servlet/switchs/SwitchAction?func=SwitchPage";
	});
	$("#subTab li").bind('click',function(){
		subTabChange(fabricId);
	});
});
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
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/fabricbase.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${fabricInfo.the_display_name}</span>
			<br>
			<strong>端口数:</strong>
			<span>${fabricInfo.the_port_count}</span>
			<strong style="margin-left: 20px;">Status:</strong>
			<span>${fabricInfo.the_propagated_status }</span>
		</li>
</ul>
<div id="content">
<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#detailTab">配置</a>
		</li>
		<li class="">
			<a href="#alertTab">事件</a>
		</li>
		<li class="">
			<a href="#dataTab">部件</a>
		</li>
	</ul>
	
	<div id="myTabContent" class="tab-content">
	<!-- 详细信息开始 -->
	<div class="tab-pane active" id="detailTab">
		<!-- 存储系统详细信息表单开始  -->
		<div class="box-content" style="width: 98%;  padding-top:10px;">
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>交换机数</h4></th>
					<td class="center">
						${fabricInfo.the_switch_count}
					</td>
				</tr>
				<tr>
					<th><h4>端口数</h4></th>
					<td class="center">${fabricInfo.the_port_count}</td>
				</tr>
				<tr>
					<th><h4>已连接端口数</h4></th>
					<td class="center">${fabricInfo.the_connected_port_count}</td>
				</tr>
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
			  <tbody>
				<tr>
					<th><h4>WWN</h4></th>
					<td class="center">
						${fabricInfo.fabric_wwn}
					</td>                                       
				</tr>
				<tr>
					<th><h4>支持Zone</h4></th>
					<td class="center"><cs:isActive value="${fabricInfo.supports_zoning}" /></td>
				</tr>
				<tr>
					<th><h4>状态</h4></th>
					<td class="center"><cs:cstatus value="${fabricInfo.the_propagated_status }" /></td>
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 存储系统详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!-- 详细信息结束 -->
	<!-- 事件开始 -->
	<div class="tab-pane" id="alertTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					事件预警
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${fabricId}','${fabricId}','Fabric');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${fabricId}','${fabricId}','Fabric');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${fabricId}','${fabricId}','Fabric');"><i class="icon icon-color icon-refresh"></i> </a>
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
									<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Fabric')">
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
											${item.flevel}
										</td>
										<td>
											<a href="javascript:goToEventDetailPage('${item.ftopid}','${item.ftoptype}', '${item.fresourceid}')">${item.fresourcename}</a>
										</td>
										<td>
											<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','${item.fresourcetype}')"
												data-placement="left" data-rel="popover"
												data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename}<br/>
												<c:choose>
													<c:when test="${fn:length(item.fdetail) > 200}">
														<c:out value="${fn:substring(item.fdetail, 0, 200)}......" />
													</c:when> 
													<c:otherwise>
														<c:out value="${item.fdetail}" />
													</c:otherwise>
												</c:choose>" title="详细信息">
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
						$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&topId=${fabricId}&resourceId=${fabricId}&resourceType=Fabric",divId:'dAlertContent'});
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
					部件
				</h2>
				<div class="box-icon">
					<a id='subFreshen' href="javascript:void(0)" class="btn btn-round" title="刷新" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a id='subShowlist' href="javascript:void(0)" class="btn btn-round" title="查看所有" data-rel='tooltip'><i class="icon icon-color icon-book"></i></a>
	<!--				
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="ExportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
	-->
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
	<!-- 				<script>
						var exurl = "${path}/servlet/fabric/FabricAction?func=exportSwitchConfigData&fabricId=${fabricId}";
						$("#ExportCSV").attr("href",exurl);
					</script>
	 -->
				</div>
			</div>
			<div class="box-content" style="max-height:810px;" id="subTab">
				<ul class="nav nav-tabs" id="myTab">
					<li class="active" title="switch">
						<a href="#switchContent">交换机(${switchCount})</a>
					</li>
					<li title="zset">
						<a href="#zsetContent">ZoneSet(${zsetCount})</a>
					</li>
				</ul>
				<div id="perfChart" class="tab-content" style="overflow: visible;min-height:200px;">
				<!-- 交换机开始 -->
					<div class="tab-pane active" id="switchContent" style="padding-top:10px;">
						<table class="table table-bordered table-striped table-condensed" style="word-break:break-all" id="conTable">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									厂商
								</th>
								<th>
									型号
								</th>
								<th>
									状态
								</th>
								<th>
									Zone ID
								</th>
								<th>
									IP地址
								</th>
								<th>
									WWN
								</th>
								<th>
									序列号
								</th>
								<th>
									描述
								</th>
								<th>
									更新时间
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty switchPage.data}">
									<c:forEach var="item" items="${switchPage.data}" varStatus="status">
										<tr >
											<td>
												<a title="${item.the_display_name}" href="${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=${item.switch_id}">${item.the_display_name}</a>
											</td>
											<td>
												${item.vendor_name}
											</td>
											<td>
												${item.model_name}
											</td>
											<td>
												<cs:cstatus value="${item.the_propagated_status}" />
											</td>
											<td>
												${item.domain}
											</td>
											<td>
												<a title="${item.ip_address}" href="http://${item.ip_address}" target="_blank">${item.ip_address}</a>
											</td>
											<td>
												${item.switch_wwn}
											</td>
											<td>
												${item.serial_number}
											</td>
											<td>
												${item.description}
											</td>
											<td>
												${item.update_timestamp}
												</td>
											</tr>
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
						<div id="switchListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty switchPage.data}">
							<script>
								$("#switchListpageNub").getLinkStr({pagecount:"${switchPage.totalPages}",curpage:"${switchPage.currentPage}",numPerPage:"${switchPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/fabric/FabricAction?func=AjaxSwitchPage&fabricId=${fabricId}",divId:'switchContent'});
							</script>
						</c:if>
						<c:if test="${empty switchPage.data}">
							<script>
								$("#ExportCSV").unbind();
								$("#ExportCSV").attr("href","javascript:void(0);");
								$("#ExportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
							</script>
						</c:if>
					</div>
					<!-- 交换机结束 -->
					<!-- 区域集开始 -->
					<div class="tab-pane" id="zsetContent" style="padding-top:10px;">
						<table class="table table-bordered table-striped table-condensed" id="conTable">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										Zone数量
									</th>
									<th>
										Fabric网络
									</th>
									<th>
										是否活动
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty zsetPage.data}">
										<c:forEach var="item" items="${zsetPage.data}" varStatus="status">
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path }/servlet/zset/ZsetAction?func=ZsetInfo&zsetId=${item.zset_id}">${item.the_display_name}</a>
												</td>
												<td>
													${item.the_zone_count}
												</td>
												<td>
													<a title="${item.f_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.f_name}</a>
												</td>
												<td>
													<cs:isActive value="${item.active}" />
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
						<div id="zsetListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty zsetPage.data}">
							<script>
								var param = $("#conditionForm").serialize();
								$("#zsetListpageNub").getLinkStr({pagecount:"${zsetPage.totalPages}",curpage:"${zsetPage.currentPage}",numPerPage:"${zsetPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/zset/ZsetAction?func=AjaxZsetPage&fabricId="+${fabricId},divId:'zsetContent'});
							</script>
						</c:if>
					</div>
					<!-- 区域集结束 -->
				</div>
			</div>
		</div>
	</div>
	</div>
	<!-- 部件结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>