<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/pool.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
$(function () {
var name=${names};
var map=${array};
Pool.capacity(name,map);
//var jsonVal = ${prfData};
//Public.drawPrfLine("prfContent",jsonVal);
doListRefresh();
});
	
//数据查询
function poolFilter(){
	var greatCapcity = $("#greatCapacity").val();
	var lessCapcity = $("#lessCapacity").val();
	var res = /^\d*$/;
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
		var jsonVal = {name:$("#poolName").val(),greatCapacity:greatCapcity,lessCapacity:lessCapcity,subSystemID:subSystemID};
		loadData("${path}/servlet/pool/PoolAction?func=AjaxPoolPage",jsonVal,$("#poolContent"));
	}
}
function doFreshen(){
	var jsonVal={name:$("#hiddenPortName").val(),greatCapacity:$("#hiddenGreatCapecity").val(),lessCapacity:$("#hiddenLessCapecity").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/pool/PoolAction?func=AjaxPoolPage",jsonVal,$("#poolContent"));	
}
//清除
function clearData(){
	$("#poolName").val("");
	$("#greatCapacity").val("");
	$("#lessCapacity").val("");
}
$(clearData);

function doListRefresh(){
	loadData("${path}/servlet/pool/PoolAction?func=PoolPrfField",{isFreshen:1,subSystemID:subSystemID},$("#prfContent"),false,false,false,true,
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
		<div style="width: 10%; float: left; height:200px;line-height:200px;" >
			<img src="${path}/resource/img/project/pool.png"/>
		</div>
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					存储池
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel='tooltip'><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel='tooltip'><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/pool/PoolAction?func=exportPoolConfigData&subSystemID=${subSystemID}";
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
					                  <label class="col-lg-2 control-label" for="poolName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="poolName" name="poolName" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="lessCapacity" style="width:80px">逻辑容量范围</label>
					                  <input class="form-control" id="lessCapacity" name="lessCapacity" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="greatCapacity" name="greatCapacity" type="text" style="width:60px;">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									<button type="button" class="btn btn-primary" onclick="poolFilter();">查询</button>
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div class="tab-pane active" id="poolContent">
					<table class="table table-bordered table-striped table-condensed" id="conTable">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									容量(G)
								</th>
								<th>
									已用容量(G)
								</th>
								<th>
									可用容量(G)
								</th>
								<th>
									已分配容量(G)
								</th>
								<th>
									未分配容量(G)
								</th>
								<th>
									本地状态
								</th>
								<th>
									操作状态
								</th>
								<th>
									硬件状态
								</th>
								<th>
									冗余级别
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty poolPage.data}">
									<c:forEach var="item" items="${poolPage.data}" varStatus="status">
										<tr>
											<td>
												<a title="${item.the_display_name}" href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
											</td>
											<td>
												<fmt:formatNumber value="${item.the_space}" pattern="0.00" />
											</td>									
											<td>	
												<fmt:formatNumber value="${item.the_consumed_space}" pattern="0.00" />
											</td>
											<td>
												<fmt:formatNumber value="${item.the_available_space}" pattern="0.00" />
											</td>
											<td>
												<fmt:formatNumber value="${item.the_assigned_space}" pattern="0.00" />
											</td>
											<td>
												<fmt:formatNumber value="${item.the_unassigned_space}" pattern="0.00" />
											</td>
											<td>
												${item.the_native_status}
											</td>
											<td>
												${item.the_operational_status}
											</td>
											<td>
												<cs:cstatus value="${item.the_consolidated_status}" />
											</td>
											<td>
												${item.raid_level}
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=11>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="poolListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty poolPage}">
						<script>
							$("#poolListpageNub").getLinkStr({pagecount:"${poolPage.totalPages}",curpage:"${poolPage.currentPage}",numPerPage:"${poolPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/pool/PoolAction?func=AjaxPoolPage&subSystemID=${subSystemID}",divId:'poolContent'});
						</script>
					</c:if>
					<c:if test="${empty poolPage}">
						<script>
							$("#exportCSV").unbind();
							$("#exportCSV").attr("href","javascript:void(0);");
							$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
						</script>
					</c:if>
					<input type="hidden" id="hiddenPortName" value="${name}"/>
					<input type="hidden" id="hiddenGreatCapecity" value ="${greatCapacity}"/>
					<input type="hidden" id="hiddenLessCapecity" value="${lessCapacity}"/>
				</div>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
	</div>
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id='pTitle'>
					性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Pool.settingPrf(${subSystemID},2,'')" data-rel='tooltip'><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh()" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
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