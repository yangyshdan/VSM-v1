<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/jstl-function" prefix="fn" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt" %>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<%@ taglib uri="/tags/fmtNumber" prefix="fmtNumber" %>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var subsystemId = "${subsystemId}";
var portId = "${portId}";
var storageType = "${storageType}";

$(document).ready(function(){
	<%--增加查询条件/WEB-INF/views/commonFiles/queryDeviceSettingPrf.jsp--%>
	$.ajax({
		url: "${path}/servlet/sr/storageport/StoragePortAction?func=PerfChartSetting2",
		data: {subsystemId: "${subsystemId}", level: 3, portId: "${portId}",storageType:storageType },
		type: "post",
		dataType: "html",
		success: function(result){
			$("#queryPage").html(result);
			$("#devtypeAndDevice").hide();
		}
	});
	doListRefresh();
});

//加载性能信息
function doListRefresh(){
	loadData("${path}/servlet/sr/storageport/StoragePortAction?func=LoadPerfInfo",{subsystemId:subsystemId,portId:portId,storageType:storageType,level:3},$("#perfChart"));
}

//设置性能图信息
function settingPerf() {
	Public.settingPerfChart("${path}/servlet/sr/storageport/StoragePortAction?func=PerfChartSetting&subsystemId=" + subsystemId + "&portId=" + portId + "&level=3");
}

</script>

<div id="content">
	<!-- 端口详细信息开始  -->
	<div class="well" style="padding-top:0px;padding-bottom:0px;">
		<div style="width: 10%;float: left;">
			<div style="margin-top:0px;width:100%;height:20px;text-align: center;">存储端口</div>
			<div style="width:80%"><img src="${path}/resource/img/project/storageport.png"/></div>
		</div>
		<div class="box-content" style="width: 85%; margin: 0 0 0 10px; padding-top:0px;float: left;">
		<legend style="margin-bottom:0px;">名称:${portInfo.name}</legend>
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>类型</h4></th>
					<td class="center">${portInfo.type}</td>
				</tr>
				<tr>
					<th><h4>网络地址</h4></th>
					<td class="center">${portInfo.network_address}</td>
				</tr>
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
			  <tbody>
				<tr>
					<th><h4>端口速率</h4></th>
					<td class="center">
						${portInfo.port_speed}
					</td>                                       
				</tr>
				<tr>
					<th><h4>链接状态</h4></th>
					<td class="center">
						${portInfo.link_status}
					</td>                                       
				</tr>
			  </tbody>
		 </table>  
		</div>
		<!-- 存储系统详细信息表单结束 -->
		<div style="clear: both;"></div>
	</div>
	<!-- 端口详细信息结束  -->
	
	<!-- 端口性能信息开始  -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					性能
				</h2>
				<div class="box-icon">
					<!--<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="settingPerf()" data-rel='tooltip'><i class="icon-cog"></i></a>-->
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doListRefresh()"><i class="icon icon-color icon-refresh" data-rel='tooltip'></i></a>
					<a href="javascript:void(0);" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel='tooltip'></i></a>
				</div>
			</div>
			<div id="queryPage" class="box-content" style="height:130px;"></div>
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
						<div id="prfContent" style="width: 100%; height: 300px;"></div>
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
								</c:choose>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty prfData.tbody.data}">
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
																	<span style="${item[fn:toLowerCase(thead.key)] >= prfData.threvalue?'color:red':''}">${item[fn:toLowerCase(thead.key)]}</span>
																</c:if>
																<c:if test="${prfData.threshold==0}">
																	<fmtNumber:formate value="${item[fn:toLowerCase(thead.key)]}" pattern="0.00"/>
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
						<div id="perfInfoPageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty prfData.tbody.data}">
							<script>
								$("#perfInfoPageNub").getLinkStr({pagecount:"${prfData.tbody.totalPages}",curpage:"${prfData.tbody.currentPage}",numPerPage:"${prfData.tbody.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storageport/StoragePortAction?func=LoadPerfInfo&subsystemId=${subsystemId}&portId=${portId}&storageType=${storageType}&level=3&tablePage=1",divId:'dataContent'});
								$("#exportCSV").unbind();
								var exurl = "${path}/servlet/sr/storageport/StoragePortAction?func=ExportPerfData&subsystemId=${subsystemId}&portId=${portId}&storageType=${storageType}&level=3";
  								$("#exportCSV").attr("href",exurl);
							</script>
						</c:if>
						<c:if test="${empty prfData.tbody.data}">
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
	<!-- 端口性能信息结束  -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>