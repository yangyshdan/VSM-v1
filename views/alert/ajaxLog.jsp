<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
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
											<fmt:formatDate value="${item.first_alert_time}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											<fmt:formatDate value="${item.last_alert_time}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											${item.alert_count}
										</td>
										<td>
											<c:choose>
												<c:when test="${item.state == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
												<c:when test="${item.state == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
												<c:when test="${item.state == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
												<c:when test="${item.state == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.sev == 'I'}"><span class="label">Info</span> </c:when>
												<c:when test="${item.sev == 'W'}"><span class="label label-warning">Warning</span> </c:when>
												<c:when test="${item.sev == 'E'}"><span class="label label-important">Critical</span> </c:when>
											</c:choose>
										</td>
										<td>
											${item.resource_name}
										</td>
										<td>
											${item.msg}
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
						$("#ruleListNub").getLinkStr({pagecount:"${logPage.totalPages}",curpage:"${logPage.currentPage}",numPerPage:"${logPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/AlertAction?func=AjaxPage&fstate=${fstate}&flevel=${flevel}",divId:'logContent'});
					</script>
				</c:if>