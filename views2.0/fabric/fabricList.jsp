<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/header.jsp"%>
<%@taglib uri="/tags/jstl-core" prefix="c" %>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-function" prefix="fn"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/ajaxPage.js">
</script>
<script type="text/javascript">
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#hiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/fabric/FabricAction?func=AjaxFabricPage",jsonVal,$("#fabricContent"));
}

//数据查询
function storageFilter(){
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/fabric/FabricAction?func=AjaxFabricPage",jsonVal,$("#fabricContent"));
}

function dbclick(id){
	location.href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId="+id;
}

function clearData(){
	$("button[type='reset']").click();
}
$(clearData);
</script>
<div id="content">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					Fabric网络
				</h2>
				<div class="box-icon">
					<%-- 
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					--%>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel="tooltip"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/fabric/FabricAction?func=exportFabricConfigData";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<!-- 筛选条件开始 -->
			<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
				<form class="form-horizontal" id="conditionForm">
					<fieldset>
						<div class="control-group" style="margin-bottom: 10px;">
							<table class="table-condensed" width="40%" style="margin: 0px auto;">
								<tbody>
									<tr>
										<td>
											<label class="col-lg-2 control-label" for="name" style="width:60px">名称</label>
											<input type="text" class="form-control" id="name" name="name" style="width: 140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="status" style="width:60px">状态</label>
											<select class="form-control" id="status" name="status" style="width: 140px; margin-left: 10px;">
												<option value="">不限</option>
												<option value="Normal">Normal</option>
												<option value="Warning">Warning</option>
												<option value="Critical">Critical</option>
											</select>
										</td>
									</tr>
									<tr>
										<td colspan="2" style="text-align:center;">
											<button type="button" class="btn btn-primary" onclick="storageFilter();">查询</button>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<button class="btn" type="reset">重置</button>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</fieldset>
				</form>
			</div>
			<!-- 筛选条件结束 -->
			
			<div class="box-content">
				<div id="myTabContent" class="tab-content" style="overflow: visible;">
					<%-- 
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
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="storageFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					--%>
					<div class="tab-pane active" id="fabricContent">
						<table class="table table-bordered table-striped table-condensed" id="conTable">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										WWN
									</th>
									<th>
										ZoneSet名称
									</th>
									<th>
										Zone数量
									</th>
									<th>
										SAN交换机数
									</th>
									<th>
										端口数
									</th>
									<th>
										已连接端口数
									</th>
									<!-- 
									<th>
										支持Zone
									</th>
									 -->
									<th>
										状态
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty fabricPage.data}">
										<c:forEach var="item" items="${fabricPage.data}" varStatus="status">
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/fabric/FabricAction?func=FabricInfo&fabricId=${item.fabric_id}">${item.the_display_name}</a>
												</td>
												<td>
													${item.fabric_wwn}
												</td>
												<td>
													<a title="${item.zset_name}" href="${path}/servlet/zset/ZsetAction?func=ZsetInfo&zsetId=${item.zset_id}">${item.zset_name}</a>
												</td>
												<td>
													${item.the_zone_count}
												</td>
												<td>
													${item.the_switch_count}
												</td>
												<td>
													${item.the_port_count}
												</td>
												<td>
													${item.the_connected_port_count}
												</td>
												<!--  
												<td>
													<cs:isActive value="${item.supports_zoning}" />
												</td>
												 -->
												<td>
													<cs:cstatus value="${item.the_propagated_status}" />
												</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="8">
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="fabricListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty fabricPage.data}">
							<script>
								var param = $("#conditionForm").serialize();
								$("#fabricListpageNub").getLinkStr({pagecount:"${fabricPage.totalPages}",curpage:"${fabricPage.currentPage}",numPerPage:"${fabricPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/fabric/FabricAction?func=AjaxFabricPage&"+param,divId:'fabricContent'});
							</script>
						</c:if>
						<c:if test="${empty fabricPage.data}">
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