<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@ taglib uri="/tags/cos-cstatus" prefix="cs" %>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/deviceAlert.js"></script>
<script type="text/javascript">
var libraryId = "${libraryId}";

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
			<span style="font-size:25px;">${libraryInfo.the_display_name}</span>
			<br>
			<strong>Status:</strong>
			<span>${libraryInfo.the_operational_status}</span>
		</li>
</ul>
<div id="content" class="span10">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#detailTab">配置</a>
		</li>
		<li class="">
			<a href="#alertTab">事件</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
<!-- 详细信息开始 -->
<div class="tab-pane active" id="detailTab">
		<!-- 存储池详细信息表单开始  -->
		<div class="box-content" style="width: 98%;  padding-top:10px;">
			<table class="table configTable" style="margin-bottom:0px;width:49%;float:left;">   
			  <tbody>
				<tr>
					<th><h4>工作状态</h4></th>
					<td class="center">${libraryInfo.the_operational_status}</td>
				</tr>
				<tr>
					<th><h4>电源状态</h4></th>
					<td class="center">${reslibraryInfo.power_status}</td>
				</tr>
			  </tbody>
		 </table>  
		<table class="table  configTable" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>磁带状态</h4></th>
					<td class="center">${reslibraryInfo.tape_status}</td>
				</tr>
				<tr>
					<th><h4>更新时间</h4></th>
					<td class="center">${libraryInfo.update_timestamp}</td>
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 存储池详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!--/row-->
	<!-- 事件开始 -->
	<div class="tab-pane" id="alertTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					事件预警
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('${libraryId}','${libraryId}','Library');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('${libraryId}','${libraryId}','Library');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="DeviceAlert.doFreshen('${libraryId}','${libraryId}','Library');"><i class="icon icon-color icon-refresh"></i> </a>
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
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','Storage')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename} <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
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
					$("#alertListNub").getLinkStr({pagecount:"${deviceLogPage.totalPages}",curpage:"${deviceLogPage.currentPage}",numPerPage:"${deviceLogPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&topId=${libraryId}&resourceId=${libraryId}&resourceType=Library",divId:'dAlertContent'});
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