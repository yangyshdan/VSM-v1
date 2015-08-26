<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>

<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										物理路径
									</th>
									<th>
										WWPN
									</th>
									<th>
										WWNN
									</th>
									<th>
										更新时间
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty fibrePage.data}">
										<c:forEach var="item" items="${fibrePage.data}" varStatus="status">
										<tr>
										<td>
											${item.phys_loc}
										</td>
										<td>
											${item.wwpn}
										</td>	
										<td>
											${item.wwnn}
										</td>										
										<td>
											${item.update_timestamp}
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
						<div id="fibreListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty virtualPage.data}">
							<script>
								$("#fibreListpageNub").getLinkStr({pagecount:"${fibrePage.totalPages}",curpage:"${fibrePage.currentPage}",numPerPage:"${fibrePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/hypervisor/HypervisorAction?func=AjaxFibrePage&hypervisorId=${hypervisorId}",divId:'fibreContent'});
							</script>
						</c:if>	
