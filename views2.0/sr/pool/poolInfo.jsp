<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/jstl-function" prefix="fn" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt" %>
<%@taglib uri="/tags/ftime" prefix="formateTime"%>
<script src="${path}/resource/js/project/pool.js"></script>
<script src="${path}/resource/js/ajaxPage.js"></script> 
<script type="text/javascript">
var subSystemID = "${subSystemID}";
var poolId = "${poolId}";
var storageType = "${storageType}";
//数据查询存储池
function volumeRefresh(){
	var jsonVal = {subSystemID:subSystemID,poolId:poolId,storageType:storageType};
	loadData("${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
}

$(function(){
	var jsonVal = ${jsonVal};
	CapecityScript(jsonVal);
	CapecityRateScript(jsonVal);
});

function doFreshenCap(){
	var jsonVal = {subSystemID:subSystemID,poolId:poolId,isFreshen:1};
	$.ajax({
		url:"${path}/servlet/sr/pool/PoolAction?func=CapacityInfo",
		data:jsonVal,
		success:function(data){
			CapecityScript(eval("("+data+")"));
			CapecityRateScript(eval("("+data+")"));
		}
	});
}

</script>

<div id="content">
	<!-- 存储池详细信息开始  -->
	<div class="well" style="padding-top:0px;padding-bottom:0px;">
		<div style="width: 10%;float: left;">
			<div style="margin-top:0px;width:100%;height:30px;text-align: center;">存储池</div>
			<div style="width:80%;"><img src="${path}/resource/img/project/pool.png"/></div>
		</div>
		<div class="box-content" style="width: 85%; margin: 0 0 0 10px; padding-top:0px;float: left;">
		<legend style="margin-bottom:0px;">名称: POOL ${poolInfo.name}</legend>
			<table class="table table-condensed" style="margin-bottom:0px;width:49%;float:left;">  
			  <tbody>
				<tr>
					<th><h4>后端磁盘数量</h4></th>
					<td class="center">${poolInfo.num_backend_disk}</td>
				</tr>
				<tr>
					<th><h4>LUN数量</h4></th>
					<td class="center">${poolInfo.num_lun}</td>
				</tr>
				<tr>
					<th><h4>阵列类型</h4></th>
					<td class="center">${poolInfo.raid_level}</td>
				</tr>
			  </tbody>
		 </table>  
		 <table class="table table-condensed" style="margin-bottom:0px; width:49%;float:right;">  
			  <tbody>
				<tr>
					<th><h4>总逻辑容量</h4></th>
					<td class="center">
						<fmt:formatNumber value="${poolInfo.total_usable_capacity/1024}" pattern="0.00" />G
					</td>                                       
				</tr>
				<tr>
					<th><h4>已用逻辑容量</h4></th>
					<td class="center">
						<fmt:formatNumber value="${(poolInfo.total_usable_capacity-poolInfo.unallocated_capacity)/1024}" pattern="0.00" />G
					</td>                                       
				</tr>
				<tr>
					<th><h4>更新时间 </h4></th>
					<td class="center">${poolInfo.update_timestamp}</td>
				</tr>
			  </tbody>
		 </table>  
		</div>
		<div style="clear: both;"></div>
	</div>
	<!-- 存储池详细信息结束  -->
	
	<!-- 容量图开始  -->
	<div class="row-fluid sortable">
		<div class="box span12"  align="center">
			<div class="box-header well">
				<h2>
					容量
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshenCap()"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0)" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div class="box-content" style="width: 98%;background-color:#EEE">
				<div id="CapecityContainer" style="width: 49%; height: 240px; float: left;"></div>
				<div id="CapecityRateContainer" style="width: 49%; height: 240px; float:right;"></div>
				<div style="clear:both;"></div>
			</div>
		</div>
	</div>
	<!-- 容量图结束  -->
	
	<!-- 卷信息开始 -->
	<div class="row-fluid sortable">	
		<div class="box span12">
			<div class="box-header well">
				<h2>卷</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="volumeRefresh();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/sr/volume/VolumeAction?func=expertVolumeConfigData&poolId=${poolId}&subSystemID=${subSystemID}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content" id="volumeContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								逻辑卷名
							</th>
							<th>
								逻辑空间(G)
							</th>
							<th>
								实占空间(G)
							</th>
							<th>
								RAID类型
							</th>
							<th>
								所属控制器
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=${subSystemID}&volumeId=${item.volume_id}&storageType=${storageType}">LUN ${item.name}</a>
										</td>
										<td>
											<fmt:formatNumber value="${item.logical_capacity/1024}" pattern="0.00"/>
										</td>
										<td>
											<c:if test="${not empty item.physical_capacity}">
												<fmt:formatNumber value="${item.physical_capacity/1024}" pattern="0.00"/></c:if>
											<c:if test="${empty item.physical_capacity}">N/A</c:if>
										</td>
										<td>
											${item.raid_level}
										</td>
										<td>
											<a href="${path}/servlet/sr/storagenode/StoragenodeAction?func=LoadStoragenodeInfo&subsystemId=${subSystemID}&spId=${item.sp_id}&storageType=${storageType}">${item.current_owner}</a>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=5>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="poolInfopageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty dbPage.data}">
					<script>
						$("#poolInfopageNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage&poolId=${poolId}&subSystemID=${subSystemID}&storageType=${storageType}",divId:'volumeContent'});
					</script>
				</c:if>
				<c:if test="${empty dbPage.data}">
					<script>
						$("#exportCSV").unbind();
						$("#exportCSV").attr("href","javascript:void(0);");
						$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 卷信息结束 -->	
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>