<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/ftime" prefix="formateTime" %>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path }/resource/js/project/publicscript.js"></script>
<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th style="width: 20px;">
										名称
									</th>
									<th style="width: 50px;">
										物理路径
									</th>
									<th style="width: 10px;">
										状态
									</th>
									<th style="width: 20px;">
										更新时间
									</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty diskPage.data}">
								<c:forEach var="item" items="${diskPage.data}" varStatus="status">
									<tr>
												<td>
													${item.disk_name}
												</td>
												<td>
													${item.physloc}
												</td>
												<td>
													${item.status}
												</td>
												<td>
													<fmt:formatDate value="${item.update_timestamp}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
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
					<ul id="disktListNub"></ul>
				</div>
				<c:if test="${not empty diskPage.data}">
					<script>
						$("#disktListNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtual/VirtualAction?func=AjaxDiskPage&vmId=${vmId}",divId:'diskContent'});
					</script>
				</c:if>