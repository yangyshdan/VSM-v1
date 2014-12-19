<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/switch.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
function trDbClick(id){
	window.location.href = "${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId="+id+"&r="+Math.random();
}
$(function(){
	doListRefresh();
});
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#switchHiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/switchs/SwitchAction?func=AjaxSwitchPage",jsonVal,$("#loadcontent"));
}

//数据查询
function switchFilter(){
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/switchs/SwitchAction?func=AjaxSwitchPage",jsonVal,$("#loadcontent"));
}

function clearData(){
	$("button[type='reset']").click();
}
$(clearData);
function doListRefresh(){
	loadData("${path}/servlet/switchs/SwitchAction?func=SwitchPrfField",{isFreshen:1},$("#prfContent"),false,false,false,true,
		function(data){
			var json = eval("("+data+")");
			Public.drawPrfLine("prfContent",json);
			$("#pTitle").html(function(){
				var str="交换机性能  (";
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
<!-- 列表开始 -->
<div class="tab-pane active" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					交换机列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/switchs/SwitchAction?func=exportSwitchConfigData";
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
					                  <label class="col-lg-2 control-label" for="ipAddress" style="width:80px">IP地址</label>
					                  <input type="text" class="form-control" id="ipAddress" name="ipAddress" style="width: 140px;margin-left: 20px;">
					              </div> 
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="status" style="width:80px">状态</label>
					                  <input class="form-control" id="status" name="status" type="text" style="width:140px;margin-left: 20px;">
					              </div>
					               <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="serialNumber" style="width:80px">序列号</label>
					                  <input class="form-control" id="serialNumber" name="serialNumber" type="text" style="width:140px;margin-left: 20px;">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button type="button" class="btn btn-primary" onclick="switchFilter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div class="tab-pane active" id="loadcontent" style="overflow: visible;" id="loadcontent">
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
								域ID
							</th>
							<th>
								IP地址
							</th>
							<th>
								光纤
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
									<tr>
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
											${item.ip_address}
										</td>
										<td>
											<a title="${item.fabric_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.fabric_name}</a>
										</td>
										<td>
											${item.switch_wwn}
										</td>
										<td>
											${item.serial_number }
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
									<td colspan=11>
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
						var param = $("#conditionForm").serialize();
						$("#switchListpageNub").getLinkStr({pagecount:"${switchPage.totalPages}",curpage:"${switchPage.currentPage}",numPerPage:"${switchPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/switchs/SwitchAction?func=AjaxSwitchPage&"+param,divId:'loadcontent'});
					</script>
				</c:if>
				<c:if test="${empty switchPage.data}">
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
					交换机性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Switch.settingPrf('',1)" data-rel="tooltip"><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
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