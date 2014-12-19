<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th  style="width: 20px;">
								<label class="checkbox inline">
									<input type="checkbox"   onclick="AlertRule.doAlertCheck(this.checked);">
							    </label>
							</th>
							<th>
								名称
							</th>
							<th>
								级别
							</th>
							<th>
								是否启用
							</th>
							<th>
								类型
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr style="cursor:pointer;" ondblclick="AlertRule.doDbClick('${item.fid}')">
										<td>
											<label class="checkbox inline">
												<input type="checkbox" value="${item.fid}"  name="ruleCheck">
										    </label>
										</td>
										<td>
										${item.fname}
										</td>
										<td>
											<c:choose>
												<c:when test="${item.flevel == 0}"><span class="label">Info</span> </c:when>
												<c:when test="${item.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
												<c:when test="${item.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.fenabled == 0}"><i class="icon icon-color icon-close"></i></c:when>
												<c:otherwise><i class="icon icon-color icon-check"></i></c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.ftype == 'DS'}">存储系统(DS8K) </c:when>
												<c:when test="${item.ftype == 'BSP'}">存储系统(DS5K) </c:when>
												<c:when test="${item.ftype == 'SVC'}">存储系统(SVC) </c:when>
												<c:when test="${item.ftype == 'SWITCH'}">交换机 </c:when>
												<c:when test="${item.ftype == 'Physical'}">物理主机 </c:when>
												<c:when test="${item.ftype == 'Virtual'}">虚拟主机 </c:when>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=5>
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
				<c:if test="${not empty dbPage.data}">
					<script>
						$("#ruleListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/AlertRuleAction?func=AjaxPage",divId:'ruleContent'});
					</script>
				</c:if>