<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/project/node.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path}/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
$(function(){
	<%--增加查询条件--%>
	$.ajax({
		url: "${path}/servlet/node/NodeAction?func=NodeSettingPrf2",
		data: {subSystemID: "${subSystemID}", level: 3, nodeId: "${nodeInfo.redundancy_id}" },
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			$("#devtypeAndDevice").hide();
			if($("#prfField option").length == 0){
				$("#myTab a[href='#prfTab']").hide();
			}
			else {
				$("head").append($("<script>").attr({src: "${path }/resource/js/highcharts/highcharts.js"}))
					.append($("<script>").attr({src: "${path}/resource/js/project/publicscript.js"}));
				doListRefresh();
			}
		}
	});
});
function doListRefresh(){
	loadData("${path}/servlet/node/NodeAction?func=NodePrfPage",
		{ level: 3, subSystemID: subSystemID, nodeId:"${nodeInfo.redundancy_id}"},$("#perfChart"));
}
</script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>

<ul class="dashboard-list" style="margin-bottom: 10px;">
		<li style="padding-top: 0px; padding-bottom: 20px;">
			<a href="#">
				<img class="dashboard-avatar" style="border-width: 0px;" src="${path}/resource/img/project/node.png" alt="StorageSystem">
			</a>
			<span style="font-size:25px;">${nodeInfo.the_display_name}</span>
			<br>
			<strong>Status:</strong>
			<span>${nodeInfo.the_operational_status}</span>
		</li>
</ul>
<div id="content" class="span10">
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
	</ul>
	<div id="myTabContent" class="tab-content">
	<div class="tab-pane active" id="detailTab">
		<!-- 存储系统详细信息表单开始  -->
		<div class="box-content" style="width: 98%;  padding-top:10px;">
			 <table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">   
			  <tbody>
				<tr>
					<th><h4>IO Group</h4></th>
					<td class="center">${nodeInfo.iogroup_name}</td>
				</tr>
				<tr>
					<th><h4>组件ID</h4></th>
					<td class="center">${nodeInfo.component_id}</td>
				</tr>
				<tr>
					<th><h4>后端名称</h4></th>
					<td class="center">${nodeInfo.the_backend_name}</td>
				</tr>
				<tr>
					<th><h4>操作状态</h4></th>
					<td class="center">${nodeInfo.the_operational_status}</td>
				</tr>
			  </tbody>
		 </table>  
		  <table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">   
			  <tbody>
				<tr>
					<th><h4>存储系统</h4></th>
					<td class="center">
						${nodeInfo.sub_name}
					</td>                                       
				</tr>
				<tr>
					<th><h4>IP</h4></th>
					<td class="center">
						${nodeInfo.ip_address}
					</td>                                       
				</tr>
				<tr>
					<th><h4>WWN</h4></th>
					<td class="center">
						${nodeInfo.wwn}
					</td>                                       
				</tr>
				<tr>
					<th><h4>更新时间</h4></th>
					<td class="center">
						${nodeInfo.update_timestamp}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 存储系统详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="tTitle">性能 </h2>
				<div class="box-icon">
					<%--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Node.settingPrf('${subSystemID}',3,'${nodeInfo.redundancy_id}')" data-rel='tooltip'><i class="icon-cog"></i></a>--%>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doListRefresh()"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV" data-rel='tooltip'><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div id="queryPage" class="box-content" style="height:170px;"></div>
			<div class="box-content" style="min-height:300px;">
				<!-- tab切换标签开始 -->
				<ul class="nav nav-tabs" id="myTab">
					<li class="active">
						<a href="#loadcontent">性能曲线</a>
					</li>
					<li class="">
						<a href="#dataContent">性能数据</a>
					</li>
				</ul>
				<!-- tab切换标签结束 -->
				<div id="perfChart" class="tab-content" style="overflow: visible;">
					<!-- 性能曲线切换页开始 -->
					<div class="tab-pane active" id="loadcontent">
						<div id="prfContent" style="width: 95%; height: 350px;"></div>
					</div>
					<!-- 性能曲线切换页结束 -->
					<!-- 性能数据切换页开始 -->
					<div class="tab-pane" id="dataContent">
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
									<c:when test="${not empty prfData and not empty prfData.tbody}">
										<c:forEach var="item" items="${prfData.tbody.data}" varStatus="status">
											<tr>
												<c:forEach var="thead" items="${prfData.thead}">
													<td>
														<c:choose>
															<c:when test="${fn:toLowerCase(thead.key)=='ele_name'}">
																${item.ele_name}
															</c:when>
															<c:when test="${fn:toLowerCase(thead.key)=='prf_timestamp'}">
																<formateTime:formate value="${item.prf_timestamp.time}" pattern="yyyy-MM-dd HH:mm:ss" />
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
						<div id="nodeInfopageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty prfData}">
							<script>
								$("#nodeInfopageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/node/NodeAction?func=NodePrfPage&nodeId=${nodeInfo.redundancy_id}&level=3&tablePage=1&subSystemID=${subSystemID}",divId:'dataContent'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/node/NodeAction?func=exportPrefData&nodeId=${nodeInfo.redundancy_id}&level=3&subSystemID=${subSystemID}";
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
		<!--/span-->
	</div>
	</div>
	<!-- 性能结束 -->
	<!-- 事件开始 -->
	<div class="tab-pane" id="alertTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					事件预警
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${subSystemID}','${nodeInfo.redundancy_id}','Storage');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${subSystemID}','${nodeInfo.redundancy_id}','Storage');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${subSystemID}','${nodeInfo.redundancy_id}','Storage');"><i class="icon icon-color icon-refresh"></i> </a>
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
							<th style="width: 130px;">首次发生时间</th>
							<th style="width: 130px;">最后发生时间</th>
							<th  style="width: 55px;">类型</th>
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
					$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&topId=${subSystemID}&resourceId=${nodeInfo.redundancy_id}&resourceType=Storage",divId:'dAlertContent'});
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