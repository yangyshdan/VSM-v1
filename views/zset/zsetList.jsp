<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/cos-cstatus" prefix="cs"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/ajaxPage.js">
</script>
<script type="text/javascript">
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#hiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/zset/ZsetAction?func=AjaxZsetPage",jsonVal,$("#zsetContent"));
}

//数据查询
function storageFilter(){
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/zset/ZsetAction?func=AjaxZsetPage",jsonVal,$("#zsetContent"));
}

function dbclick(id){
	location.href="${path}/servlet/zset/ZsetAction?func=ZsetInfo&zsetId="+id;
}

function clearData(){
	$("button[type='reset']").click();
}
$(clearData);
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
<div id="content">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					区域集
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/zset/ZsetAction?func=exportZsetConfigData";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
				<div id="myTabContent" class="tab-content" style="overflow: visible;">
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
						                  <input type="text" class="form-control" id="name" name="name" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="active" style="width:80px">是否活动</label>
						                  <select name="active" style="width:150px;margin-left: 20px;">
												<option value="" selected="selected">-请选择-</option>
												<option value="1">是</option>
												<option value="0">否</option>
										  </select>
						              </div> 
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="storageFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane active" id="zsetContent" style="text-align: center;overflow-y: hidden;">
						<table class="table table-bordered table-striped table-condensed" id="conTable">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										区域数
									</th>
									<th>
										光纤
									</th>
									<th>
										是否活动
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty zsetPage.data}">
										<c:forEach var="item" items="${zsetPage.data}" varStatus="status">
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path }/servlet/zset/ZsetAction?func=ZsetInfo&zsetId=${item.zset_id}">${item.the_display_name}</a>
												</td>
												<td>
													${item.the_zone_count}
												</td>
												<td>
													<a title="${item.f_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.the_fabric_id}">${item.f_name}</a>
												</td>
												<td>
													<cs:isActive value="${item.active}" />
												</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan=4>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="zsetListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty zsetPage.data}">
							<script>
								var param = $("#conditionForm").serialize();
								$("#zsetListpageNub").getLinkStr({pagecount:"${zsetPage.totalPages}",curpage:"${zsetPage.currentPage}",numPerPage:"${zsetPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/zset/ZsetAction?func=AjaxZsetPage&"+param,divId:'zsetContent'});
							</script>
						</c:if>
						<c:if test="${empty zsetPage.data}">
							<script>
								$("#exportCSV").unbind();
								$("#exportCSV").attr("href","javascript:void(0);");
								$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
							</script>
						</c:if>
					</div>
					<!-- 存储切换页结束 -->
				</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>