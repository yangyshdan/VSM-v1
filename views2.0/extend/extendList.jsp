<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script> 
<script src="${path }/resource/js/project/extent.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
$(function(){
	var name=${names};
	var map=${array};
	Extent.speed(name,map);
});

function extendDbClick(id){
	window.location.href = "${path}/servlet/extend/ExtendAction?func=extendInfo&subSystemID=${subSystemID}&extendId="+id+"&r="+Math.random();
}
	
//数据查询
function extendFilter(){
	var startCap = $("#startCap").val();
	var endCap = $("#endCap").val();
	var startAvailableCap = $("#startAvailableCap").val();
	var endAvailableCap = $("#endAvailableCap").val();
	var res = /^\d*$/;
	if(!res.test(startCap) || !res.test(endCap) || !res.test(startAvailableCap) || !res.test(endAvailableCap)){
		bAlert("请输入有效容量");
		return false;
	}
	if(endCap>0 && startCap>=endCap){
		bAlert("请输入有效总容量范围");
	}
	if(endAvailableCap>0 && startAvailableCap>=endAvailableCap){
		bAlert("请输入有效可用容量范围");
	}
	var jsonVal = $("#conditionForm").getValue();
	jsonVal["subSystemID"]=subSystemID;
	loadData("${path}/servlet/extend/ExtendAction?func=AjaxExtendPage",jsonVal,$("#extendContent"));
}
function doFreshen(){
	var jsonArray = $("#extendHiddenForm").serializeArray();
	var jsonVal={};
	$.each(jsonArray,function(){
		jsonVal[this.name] = this.value;
	});
	jsonVal["subSystemID"]=subSystemID;
	loadData("${path}/servlet/extend/ExtendAction?func=AjaxExtendPage",jsonVal,$("#extendContent"));	
}
//清除
function clearData(){
	$("button[type='reset']").click();
}
$(clearData);
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">数据列表</a>
		</li>
		<li class="">
			<a href="#prfTab">性能曲线</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<div class="tab-pane active" id="dataTab">
	<div class="well">
		<img src="${path}/resource/img/project/pool.png" style="width: 10%; float: left; padding-top:20px;" />
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					存储扩展列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel='tooltip'><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel='tooltip'><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/extend/ExtendAction?func=exportExtendConfigData&subSystemID=${subSystemID}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
				<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
				<div id="conAlert" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
					<div class="arrow"></div>
					<div class="popover-inner">
						<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlert()">×</a></h3>
						<div class="popover-content" style="padding: 8px;">
					        <form class="form-horizontal" id="conditionForm">
								<fieldset>
								  <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="name" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="name" name="name" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="deviceId" style="width:80px">设备ID</label>
					                  <input type="text" class="form-control" id="deviceId" name="deviceId" style="width: 140px;margin-left: 20px;">
					              </div> 
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startCap" style="width:80px">总容量</label>
					                  <input class="form-control" id="startCap" name="startCap" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endCap" name="endCap" type="text" style="width:60px;">
					              </div>
					               <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startAvailableCap" style="width:80px">可用容量</label>
									  <input class="form-control" id="startAvailableCap" name="startAvailableCap" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endAvailableCap" name="endAvailableCap" type="text" style="width:60px">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									<button type="button" class="btn btn-primary" onclick="extendFilter();">查询</button>
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div class="tab-pane active" id="extendContent" style="overflow:visible;">
					<table class="table table-bordered table-striped table-condensed" id="conTable">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									扩展卷数
								</th>
								<th>
									扩展容量(G)
								</th>
								<th>
									总容量
								</th>
								<th>
									可用容量
								</th>
								<th>
									操作状态
								</th>
								<th>
									本地状态
								</th>
								<th>
									存储池
								</th>
								<th>
									设备ID
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty extendPage.data}">
									<c:forEach var="item" items="${extendPage.data}" varStatus="status">
										<tr>
											<td>
												<a title="${item.the_display_name}" href="${path}/servlet/extend/ExtendAction?func=extendInfo&subSystemID=${subSystemID}&extendId=${item.storage_extent_id}">${item.the_display_name}</a>
											</td>
											<td>
												${item.the_extent_volume}
											</td>									
											<td>
												<fmt:formatNumber value="${item.the_extent_space}" pattern="0.00" />
											</td>									
											<td>
												<fmt:formatNumber value="${item.the_total_space}" pattern="0.00" />
											</td>									
											<td>
												<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
											</td>									
											<td>
												${item.the_operational_status}
											</td>									
											<td>
												${item.the_native_status}
											</td>									
											<td>
												<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
											</td>									
											<td>
												${item.device_id }
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
					<div id="extendListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty extendPage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#extendListpageNub").getLinkStr({pagecount:"${extendPage.totalPages}",curpage:"${extendPage.currentPage}",numPerPage:"${extendPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/extend/ExtendAction?func=AjaxExtendPage&subSystemID=${subSystemID}&"+param,divId:'extendContent'});
						</script>
					</c:if>
					<c:if test="${empty extendPage.data}">
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
	<!-- 列表结束 -->
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id="pTitle">
					性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Extent.settingPrf(${subSystemID},2,'')"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div id="prfContent" style="width:95%;min-height:385px;"></div>
			</div>
		</div>
	</div>
	</div>
	<!-- 性能结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>