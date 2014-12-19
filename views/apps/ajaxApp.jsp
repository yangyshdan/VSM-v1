<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
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