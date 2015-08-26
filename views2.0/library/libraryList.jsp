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
	var args=$("#libraryHiddenForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/library/LibraryAction?func=AjaxLibraryPage",jsonVal,$("#librarycontent"));
}

//数据查询
function libraryFilter(){
	
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/library/LibraryAction?func=AjaxLibraryPage",jsonVal,$("#librarycontent"));
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
					磁带库
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()"  data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh" data-rel="tooltip"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt" data-rel="tooltip"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/library/LibraryAction?func=ExportLibraryConfigData";
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
					                  <label class="col-lg-2 control-label" for="displayName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="displayName" name="displayName" style="width: 140px;margin-left: 20px;">
						              </div>					   
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="libraryFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane active" id="librarycontent">
						<table class="table table-bordered table-striped table-condensed" id="conTable">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										工作状态
									</th>
									<th>
										电源状态
									</th>
									<th>
										磁带状态
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
									<c:when test="${not empty libraryPage.data}">
										<c:forEach var="item" items="${libraryPage.data}" varStatus="status">																					
											<tr>
												<td>
													<a title="${item.the_display_name}" href="${path}/servlet/library/LibraryAction?func=LibraryInfo&libraryId=${item.tape_library_id}">${item.the_display_name}</a>
												</td>
												<td>
													${item.the_operational_status}
												</td>
												<td>
													${item.power_status}
												</td>
												<td>
													${item.tape_status}
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
											<td colspan=6>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="libraryListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty libraryPage.data}">
							<script>
								var param = $("#conditionForm").serialize();
								$("#libraryListpageNub").getLinkStr({pagecount:"${libraryPage.totalPages}",curpage:"${libraryPage.currentPage}",numPerPage:"${libraryPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/library/LibraryAction?func=AjaxLibraryPage&"+param,divId:'librarycontent'});
							</script>
						</c:if>
						<c:if test="${empty libraryPage.data}">
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