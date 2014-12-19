<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/iogroup.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
function iogroupDbClick(id){
    window.location = "${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId="+id;
}
//数据提交
function iogroupFilter(){
	var jsonVal = $("#conditionForm").getValue();
	jsonVal["subSystemID"] = subSystemID;
	loadData("${path}/servlet/iogroup/IogroupAction?func=AjaxIogroupPage",jsonVal,$("#iogroupContent"));
}
//刷新
function doFreshen(){
	//var jsonVal = $("#portHiddenForm").getValue();    该方法对隐藏表单域无效
	var jsonVal = {};
	jsonVal["subSystemID"] = subSystemID;
	var serVal = $("#hiddenForm").serializeArray();
	$.each(serVal,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/iogroup/IogroupAction?func=AjaxIogroupPage",jsonVal,$("#iogroupContent"));
}
//重置
function clearData(){
	$("button[type='reset']").click();
}
$(clearData);
$(doListRefresh);
function doListRefresh(){
	loadData("${path}/servlet/iogroup/IogroupAction?func=IogroupPrfField",{isFreshen:1,subSystemID:subSystemID},$("#prfContent"),false,false,false,true,
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
	<!-- 列表开始 -->
	<div class="tab-pane active" id="dataTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					IO Group
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel='tooltip' ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel='tooltip'><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/iogroup/IogroupAction?func=exportIogroupConfigData&subSystemID=${subSystemID}";
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
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									<button type="button" class="btn btn-primary" onclick="iogroupFilter();">查询</button>
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div id="iogroupContent" style="text-align: center;overflow-y: hidden;">
					<table class="table table-bordered table-striped table-condensed" id="conTable">
						<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								镜像内存(G)
							</th>
							<th>
								镜像空闲内存(G)
							</th>
							<th>
								快照内存(G)
							</th>
							<th>
								快照空闲内存(G)
							</th>
							<th>
								阵列内存(G)
							</th>
							<th>
								阵列空闲内存(G)
							</th>
							<th>
								维护状态
							</th>
						</tr>
					</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty iogroupPage.data}">
									<c:forEach var="item" items="${iogroupPage.data}" varStatus="status">
										<tr>
											<td>
												<a title="${item.the_display_name}" href="${path}/servlet/iogroup/IogroupAction?func=IogroupInfo&subSystemID=${subSystemID}&iogroupId=${item.io_group_id}">${item.the_display_name}</a>
											</td>
											<td>
												<fmt:formatNumber value="${item.mirroring_total_memory/1024}" pattern="0.00" />
											</td>
											<td>
												<cs:isProgress total="${item.mirroring_total_memory}" available="${item.mirroring_free_memory}"/>
											</td>
											<td>
												<fmt:formatNumber value="${item.flash_copy_total_memory/1024}" pattern="0.00" />
											</td>
											<td>
												<cs:isProgress total="${item.flash_copy_total_memory}" available="${item.flash_copy_free_memory}"/>
											</td>
											<td>
												<fmt:formatNumber value="${item.raid_total_memory/1024}" pattern="0.00" />
											</td>
											<td>
												<cs:isProgress total="${item.raid_total_memory}" available="${item.raid_free_memory}"/>
											</td>
											<td>
												${item.maintenance}
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
					<div id="iogroupListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty iogroupPage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#iogroupListpageNub").getLinkStr({pagecount:"${iogroupPage.totalPages}",curpage:"${iogroupPage.currentPage}",numPerPage:"${iogroupPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/iogroup/IogroupAction?func=AjaxIogroupPage&subSystemID=${subSystemID}&"+param,divId:'iogroupContent'});
						</script>
					</c:if>
					<c:if test="${empty iogroupPage.data}">
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
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="IOGroup.settingPrf(${subSystemID},2,'')" data-rel='tooltip'><i class="icon-cog"></i></a>
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
