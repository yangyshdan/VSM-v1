<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/disk.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
$(function () {
	var name=${names};
	var map=${array};
	Disk.capecity(name,map);
});
function diskDbClick(id){
	window.location.href = "${path}/servlet/sr/disk/DiskAction?func=diskInfo&subSystemID=${subSystemID}&diskId="+id+"&r="+Math.random();
}
	
//数据查询
function diskFilter(){
	var startCap = $("#startCap").val();
	var endCap = $("#endCap").val();
	var res = /^\d*$/;
	if(!res.test(startCap) || !res.test(endCap)){
		bAlert("请输入有效容量");
		return false;
	}
	if(endCap>0 && startCap>=endCap){
		bAlert("请输入有效容量范围");
	}
	var jsonVal = $("#conditionForm").getValue();
	jsonVal["subSystemID"]=subSystemID;
	loadData("${path}/servlet/sr/disk/DiskAction?func=AjaxDiskPage",jsonVal,$("#diskContent"));
}
function doFreshen(){
	var jsonArray = $("#diskHiddenForm").serializeArray();
	var jsonVal={};
	$.each(jsonArray,function(){
		jsonVal[this.name] = this.value;
	});
	jsonVal["subSystemID"]=subSystemID;
	loadData("${path}/servlet/sr/disk/DiskAction?func=AjaxDiskPage",jsonVal,$("#diskContent"));	
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
	<div class="well">
		<img src="${path}/resource/img/project/pool.png" style="width: 10%; float: left; padding-top:20px;" />
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					磁盘
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()"  data-rel='tooltip'><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel='tooltip'><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/sr/disk/DiskAction?func=exportDiskConfigData&subSystemID=${subSystemID}";
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
					                  <label class="col-lg-2 control-label" for="startCap" style="width:80px">总容量</label>
					                  <input class="form-control" id="startCap" name="startCap" type="text" style="width:60px;margin-left: 20px;"> -
									  <input class="form-control" id="endCap" name="endCap" type="text" style="width:60px;">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									<button type="button" class="btn btn-primary" onclick="diskFilter();">查询</button>
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div class="tab-pane active" id="diskContent" style="text-align: center;overflow-y: hidden;">
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
									转速
								</th>
								<th>
									运行状态
								</th>
								<th>
									阵列
								</th>
								<th>
									厂商
								</th>
								<th>
									型号
								</th>
								<th>
									序列号
								</th>
								<th>
									固件版本
								</th>
								<th>
									硬件状态
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty diskPage.data}">
									<c:forEach var="item" items="${diskPage.data}" varStatus="status">
										<tr>
											<td>
												${item.the_display_name}
											</td>
											<td>
												<fmt:formatNumber value="${item.the_capacity}" pattern="0.00" />
											</td>
											<td>
												${item.speed}
											</td>
											<td>
												${item.the_operational_status}
											</td>
											<td>
												<a title="${item.diskgroup_name}" href="${path}/servlet/arraysite/ArraysiteAction?func=ArraysiteInfo&subSystemID=${subSystemID}&arraysiteId=${item.the_arraysite_id}">${item.diskgroup_name}</a>
											</td>									
											<td>
												${item.vendor_name}
											</td>									
											<td>
												${item.model_name}
											</td>									
											<td>
												${item.serial_number}
											</td>									
											<td>
												${item.firmware_rev}
											</td>									
											<td>
												<cs:cstatus value="${item.the_consolidated_status}" />
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
					<div id="diskListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty diskPage.data}">
						<script>
							var param = $("#diskHiddenForm").serialize();
							$("#diskListpageNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/disk/DiskAction?func=AjaxDiskPage&subSystemID=${subSystemID}&"+param,divId:'diskContent'});
						</script>
					</c:if>
					<c:if test="${empty diskPage.data}">
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
<%@include file="/WEB-INF/views/include/footer.jsp"%>