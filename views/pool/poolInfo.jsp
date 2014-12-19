<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@ taglib uri="/tags/cos-cstatus" prefix="cs" %>
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/project/pool.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
var poolId = "${poolInfo.pool_id}";

//数据查询存储池
function volumeFilter(){
	var jsonVal={poolId:poolId,isFreshen:1};
	loadData("${path}/servlet/pool/PoolAction?func=VolumeInfo",jsonVal,$("#volumeContent"));
}
//function volumeDbClick(id){
//	window.location.href = "${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&subSystemID=${subSystemID}&svid="+id+"&r="+Math.random();
//}

//$(function(){
//	var obj = ${jsonVal};
//	CapecityScript(obj);
//	CapecityRateScript(obj);
//});
//function doListRefresh(){
//	loadData("${path}/servlet/pool/PoolAction?func=PoolPrfPage",{level:3,subSystemID:subSystemID,poolId:poolId},$("#perfChart"));
//}

//function doFreshenCap(){
//	var jsonVal = {subSystemID:subSystemID,poolId:poolId,isFreshen:1};
//	$.ajax({
//		url:"${path}/servlet/pool/PoolAction?func=PoolCapacityInfo",
//		data:jsonVal,
//		success:function(data){
//			CapecityScript(eval("("+data+")"));
//			CapecityRateScript(eval("("+data+")"));
//		}
//	});
//}

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
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/pool.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${poolInfo.the_display_name}</span>
			<br>
			<strong>Status:</strong>
			<span>${poolInfo.the_operational_status}</span>
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
	<div id="myTabContent" class="tab-content" >
<!-- 详细信息开始 -->
<div class="tab-pane active" id="detailTab">
		<!-- 存储池详细信息表单开始  -->
		<div class="box-content" style="width: 98%;  padding-top:10px;">
			<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">   
			  <tbody>
				<tr>
					<th><h4>存储系统</h4></th>
					<td class="center">${poolInfo.sub_name}</td>
				</tr>
				<tr>
					<th><h4>容量(G)</h4></th>
					<td class="center"><fmt:formatNumber value="${poolInfo.the_space}" pattern="0.00" /></td>
				</tr>
				<tr>
					<th><h4>可用容量(G)</h4></th>
					<td class="center"><fmt:formatNumber value="${poolInfo.the_available_space}" pattern="0.00" /></td>
				</tr>
				<tr>
					<th><h4>未分配容量(G)</h4></th>
					<td class="center"><fmt:formatNumber value="${poolInfo.the_unassigned_space}" pattern="0.00" /></td>
				</tr>
				<tr>
					<th><h4>操作状态</h4></th>
					<td class="center">${poolInfo.the_operational_status}</td>
				</tr>
			  </tbody>
		 </table>  
		<table class="table  configTable" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>已用容量(G)</h4></th>
					<td class="center">
						<fmt:formatNumber value="${poolInfo.the_consumed_space}" pattern="0.00" />
					</td>                                       
				</tr>
				<tr>
					<th><h4>已分配容量(G)</h4></th>
					<td class="center">
						<fmt:formatNumber value="${poolInfo.the_assigned_space}" pattern="0.00" />
					</td>                                       
				</tr>
				<tr>
					<th><h4>本地状态</h4></th>
					<td class="center">${poolInfo.the_native_status}</td>
				</tr>
				<tr>
					<th><h4>硬件状态</h4></th>
					<td class="center">${poolInfo.the_consolidated_status}</td>
				</tr>
				<tr>
					<th><h4>沉余级别</h4></th>
					<td class="center">${poolInfo.raid_level}</td>
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 存储池详细信息表单结束 -->
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
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${subSystemID}','${poolInfo.pool_id}','Storage');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${subSystemID}','${poolInfo.pool_id}','Storage');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${subSystemID}','${poolInfo.pool_id}','Storage');"><i class="icon icon-color icon-refresh"></i> </a>
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
									<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Storage')">
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
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Storage')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename } <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
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
						$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${poolInfo.pool_id}&topId=${subSystemID}&resourceType=Storage",divId:'dAlertContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	</div>
	<!-- 事件结束 -->
	<!-- 卷开始 -->
	<div class="tab-pane" id="dataTab">
	<div class="row-fluid">	
		<div class="box span12">
			<div class="box-header well" data-original-title>
				<h2>卷</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="volumeFilter();" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up" data-rel='tooltip'></i></a>
					<script>
						var exurl = "${path}/servlet/pool/PoolAction?func=expertVolumeConfigData&poolId=${poolId}&subSystemID=${subSystemID}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
				<div id="perfChart" class="tab-content" style="overflow: visible;">
					<div class="tab-pane active" id="volumeContent" style="padding-top:10px;">
						<div id="volumeContent" style="min-height: 300px;overflow-y: hidden;">
							<table class="table table-bordered table-striped table-condensed" id="conTable">
								<thead>
									<tr>
										<th>
											逻辑卷名
										</th>
										<th>
											存储系统
										</th>
										<th>
											状态
										</th>
										<th>
											容量(G)
										</th>
										<th>
											已用容量(G)
										</th>
										<th>
											沉余级别
										</th>
										<th>
											唯一编号
										</th>
									</tr>
								</thead>
								<tbody>
									<c:choose>
										<c:when test="${not empty volumePage.data}">
											<c:forEach var="item" items="${volumePage.data}" varStatus="status">
												<tr>
													<td>
														${item.the_display_name}
													</td>
													<td>
														<a title="${item.sub_name}" href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${item.subsystem_id}">${item.sub_name }</a>
													</td>
													<td>
														${item.the_consolidated_status}
													</td>
													<td>
														<fmt:formatNumber value="${item.the_capacity}" pattern="0.00"/>
													</td>
													<td>
														<fmt:formatNumber value="${item.the_used_space}" pattern="0.00"/>
													</td>
													<td>
														${item.the_redundancy}
													</td>
													<td>
														${item.unique_id}
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
							<div id="volumeListpageNub" class="pagination pagination-centered"></div>
							<c:if test="${not empty volumePage.data}">
								<script>
									$("#volumeListpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/pool/PoolAction?func=VolumeInfo&poolId=${poolId}&isFreshen=1",divId:'volumeContent'});
								</script>
							</c:if>
							<c:if test="${empty volumePage.data}">
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
		</div>
	</div>
	</div>
	<!-- 卷结束 -->	
	<!--/row-->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>