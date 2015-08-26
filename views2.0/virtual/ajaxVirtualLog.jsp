<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th style="width: 85px;">
										首次发生时间
									</th>
									<th style="width: 85px;">
										最后发生时间
									</th>
									<th  style="width: 55px;">
										重复次数
									</th>
									<th style="width: 30px;">
										状态
									</th>
									<th style="width: 30px;">
										级别
									</th>
									<th style="width: 50px;">
										事件源
									</th>
									<th>
										消息
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty logPage.data}">
										<c:forEach var="item" items="${logPage.data}" varStatus="status">
											<tr style="cursor:pointer;">
												<td>
													<fmt:formatDate value="${item.ffirsttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
												</td>
												<td>
													<fmt:formatDate value="${item.flasttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
												</td>
												<td>
													${item.fcount}
												</td>
												<td>
													<c:choose>
														<c:when test="${item.fstate == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
														<c:when test="${item.fstate == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
														<c:when test="${item.fstate == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
														<c:when test="${item.fstate == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
													</c:choose>
												</td>
												<td>
													<c:choose>
														<c:when test="${item.flevel == 0}"><span class="label">Info</span> </c:when>
														<c:when test="${item.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
														<c:when test="${item.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
													</c:choose>
												</td>
												<td>
													${item.fresourcename}
												</td>
												<td>
												<a href="#"  data-rel="popover" data-content="${item.fdetail}" title="详细信息">${item.fdescript}</a>
													
												</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan=7>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						
						<div class="pagination pagination-centered">
							<ul id="ruleListNub"></ul>
						</div>
						<c:if test="${not empty logPage.data}">
							<script>
								$("#ruleListNub").getLinkStr({pagecount:"${logPage.totalPages}",curpage:"${logPage.currentPage}",numPerPage:"${logPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxLogPage&vmId=${vmId}",divId:'logContent'});
							</script>
						</c:if>
						
					<script>
					$(function(){
						$('[rel="popover"],[data-rel="popover"]').popover();
					});
					</script>