<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/Chart.js"></script>
<script>
function doListRefresh(){
		loadData("${path}/servlet/chart/ChartAction?func=AjaxModelPage",{},$("#modelContent"));
		return false;
	}
</script>
<div id="content">
<!-- 列表开始 -->
	<div class="row-fluid sortable">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					多工作台列表
				</h2>
				<div class="box-icon">
					<a href="#" class="btn btn-round" title="新增" data-rel="tooltip" onclick="Chart.doModelAdd();"><i class="icon icon-color icon-edit"></i> </a>
					<a href="#" class="btn btn-round" title="刷新" data-rel="tooltip" onclick="Chart.refreshModel();"><i class="icon icon-color icon-refresh"></i> </a>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;height:180px;" id="modelContent">
				<table class="table table-bordered table-striped bootstrap-datatable">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								是否显示
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
											${item.fname}
										</td>
										<td>
											<c:choose>
											  	<c:when test="${item.fisshow == 1}">
											  		 <div id="check_${item.fid}" name="${item.fname}" class="make-switch">
										                 <input type="checkbox" checked>
										             </div>
											  	</c:when>
											  	<c:otherwise>
											  		<div id="check_${item.fid}" name="${item.fname}" class="make-switch">
										                 <input type="checkbox">
										             </div>
											  	</c:otherwise>
											 </c:choose>	
										</td>
										<td>
												<a href="#" class="btn btn-success" style="float:right;margin-right:20px;" onclick="Chart.editModel('${item.fid}');"><i class=" icon-white  icon-edit"></i>编辑</a>
												<a href="${path}/servlet/chart/ChartAction?func=Page&modelId=${item.fid}" class="btn btn-info" style="float:right;margin-right:20px;"><i class=" icon-white  icon-edit"></i>模块设置</a>
												<c:if test="${item.fid != 1}">
													<a href="#" class="btn btn btn-da" style="float:right;margin-right:20px;" onclick="Chart.delModel('${item.fid}');"><i class=" icon-white  icon-trash"></i>删除</a>
												</c:if>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=3>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				
				<div class="pagination pagination-centered">
					<ul id="chartListNub"></ul>
				</div>
				<c:if test="${not empty dbPage.data}">
					<script>
						$("#chartListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/chart/ChartAction?func=AjaxModelPage",divId:'modelContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
	
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>