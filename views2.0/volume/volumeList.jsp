<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/volume.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
var subsystemId="${subSystemID}";
$(function () {
	var args = ${array};
	var categore = ${names};
	Volume.capecity(args,categore);
	doListRefresh();
	//var jsonVal = ${prfData};
	//Public.drawPrfLine("prfContent",jsonVal);
});

function volumeDbClick(id){
	window.location.href = "${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&subSystemID=${subSystemID}&svid="+id+"&r="+Math.random();
}
	
//数据查询
function volumeFilter(){
	var greatCapcity = $("#greatLogical_Capacity").val();
	var lessCapcity = $("#lessLogical_Capacity").val();
	var res = /^\d+(\.{1}\d+)?$/;
	var isFlag = false;
	if(greatCapcity!=""){
		if(!res.test(greatCapcity)){
			bAlert("请输入有效的容量范围!");
			return false;
		}
	}
	if(lessCapcity!=""){
		if(!res.test(lessCapcity)){
			bAlert("请输入有效的容量范围!");
			return false;
		}
	}
	if(greatCapcity!="" && lessCapcity!=""){
		if(greatCapcity<lessCapcity){
			bAlert("起始容量应小于结束容量，请重新输入!");
			return false;
		}else{
			isFlag = true;
		}
	}else{
		isFlag = true;
	}
	if(isFlag){
		var jsonVal = {name:$("#volumeName").val(),greatLogical_Capacity:greatCapcity,lessLogical_Capacity:lessCapcity,subSystemID:subsystemId};
		loadData("${path}/servlet/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
	}
}
function doFreshen(){
	var jsonVal={name:$("#hiddenName").val(),lessLogical_Capacity:$("#hiddenLessLogical_Capacity").val(),
		greatLogical_Capacity:$("#hiddenGreatLogical_Capacity").val(),subSystemID:subsystemId};
	loadData("${path}/servlet/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
}
//清除
function clearData(){
	$("#volumeName").val("");
	$("#greatLogical_Capacity").val("");
	$("#lessLogical_Capacity").val("");
}
$(clearData);

function doListRefresh(){
	loadData("${path}/servlet/volume/VolumeAction?func=VolumePrfField",
		{ isFreshen:1, subSystemID:subsystemId }, $("#prfContent"), false, false, false, true,
		function(data){
			var json = eval("("+data+")");
			Public.drawPrfLine("prfContent",json);
			$("#pTitle").html(function(){
				var str="性能  (";
				$.each(json.kpiInfo,function(i){
					str+=json.kpiInfo[i].ftitle;
					if(i<json.kpiInfo.length-1){
						str+=",";
					}
				});
				if(str.length>100){
					str = str.substring(0,100)+'...';
				}
				str+=")";
				return str;
			});
		});
}
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
		<img src="${path}/resource/img/project/volume.png"
			style="width: 10%; float: left; padding-top:20px;" />
		<div id="container"
			style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					卷
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/volume/VolumeAction?func=expertVolumeConfigData&subSystemID=${subSystemID}";
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
					                  <label class="col-lg-2 control-label" for="storageName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="volumeName" name="volumeName" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="lessLogical_Capacity" style="width:80px">逻辑容量范围 :</label>
					                  <input class="form-control" id="lessLogical_Capacity" name="lessLogical_Capacity" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="greatLogical_Capacity" name="greatLogical_Capacity" type="text" style="width:60px;">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									<button type="button" class="btn btn-primary" onclick="volumeFilter();">查询</button>
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div class="tab-pane active" id="volumeContent">
					<table class="table table-bordered table-striped table-condensed" id="conTable">
						<thead>
							<tr>
								<th>
									逻辑卷名
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
									存储池
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
												<a title="${item.the_display_name}" href="${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid=${item.svid}&subSystemID=${subSystemID}">${item.the_display_name}</a>
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
												<a title="${item.pool_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.pool_name }</a>
											</td>
											<td>
												${item.unique_id}
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=8>
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
							$("#volumeListpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/volume/VolumeAction?func=AjaxVolumePage&greatLogical_Capacity=${greatLogical_Capacity}&lessLogical_Capacity=${lessLogical_Capacity}&name=${name}&subSystemID=${subSystemID}",divId:'volumeContent'});
						</script>
					</c:if>
					<c:if test="${empty volumePage.data}">
						<script>
							$("#exportCSV").unbind();
							$("#exportCSV").attr("href","javascript:void(0);");
							$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
						</script>
					</c:if>
					<input type="hidden" id="hiddenName" value="${name}"/>
					<input type="hidden" id="hiddenLessLogical_Capacity" value="${lessLogical_Capacity}"/>
					<input type="hidden" id="hiddenGreatLogical_Capacity" value="${greatLogical_Capacity}"/>
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
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Volume.settingPrf(${subSystemID},2,'')" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh()"><i class="icon icon-color icon-refresh" data-rel="tooltip"></i></a>
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