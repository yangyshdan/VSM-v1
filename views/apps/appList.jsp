<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/apps.js"></script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>
<div id="content">
<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					应用列表
				</h2>
				<div class="box-icon">
	<!-- 				<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="过滤" onclick="AlertLog.switchFilter('logFilter')" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>    -->
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="新增" onclick="App.editApp(0);"><i class="icon icon-color icon-edit"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="刷新" onclick="App.dataFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div id="logFilter" class="" style="position:absolute;right:20px;margin-top:10px;display:none;">
				<div class="arrow"></div>
				<div class="popover-inner">
					<h3 class="popover-title">过滤器</h3>
					<div class="popover-content" style="padding: 8px;">
				        <form class="form-horizontal">
							<fieldset>
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_state" style="width:80px">名称</label>
				                  <input type="text" />
				              </div>
				              
				              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
								<button class="btn" type="reset">重置</button>
								<input type="button" onclick="App.dataFilter();" class="btn btn-primary" value="查询 "/>
							  </div>
				           	</fieldset>
				          </form>
					</div>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;" id="appContent">
				<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										编号
									</th>
									<th>
										名称
									</th>
									<th>
										虚拟机数
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty dbPage.data}">
										<c:forEach var="item" items="${dbPage.data}" varStatus="status">
											<tr style="cursor:pointer;">
												<td>
													${status.count}
												</td>
												<td>
												<a title="${item.name}" href="${path}/servlet/apps/AppsAction?func=AppsInfo&fappid=${item.fid}">${item.name}</a>
													
												</td>
												<td>
													${item.vcount}
												</td>
												<td>
													<a class="btn btn-info" href="javascript:void(0)" title="edit" onclick="App.editApp(${item.fid})"><i class="icon-edit icon-white"></i>编辑</a>
													<a class="btn btn-danger" href="javascript:void(0)" title="delete" onclick="App.doAppDel('${item.fid}')"><i class="icon-trash icon-white"></i>删除</a>
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
						
						<div class="pagination pagination-centered">
							<ul id="appListNub"></ul>
						</div>
						<c:if test="${not empty dbPage.data}">
							<script>
								$("#appListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/apps/AppsAction?func=AjaxPage",divId:'appContent'});
							</script>
						</c:if>
						
					<script>
					$(function(){
						$('[rel="popover"],[data-rel="popover"]').popover();
					});
					</script>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>