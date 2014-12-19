<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
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
												<a href="${path}/servlet/chart/ChartAction?function=Page&modelId=${item.fid}" class="btn btn-info" style="float:right;margin-right:20px;"><i class=" icon-white  icon-edit"></i>模块设置</a>
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
						$("#chartListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/chart/ChartAction?func=AjaxPage",divId:'modelContent'});
					</script>
				</c:if>
