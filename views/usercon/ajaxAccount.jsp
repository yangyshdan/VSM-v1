<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
		<thead>
			<tr>
			<th  style="width: 20px;">
			</th>
			<th style="width: 130px;">
					用户名
				</th>
				<th style="width: 130px;">
					所属角色
				</th>
				<th  style="width: 55px;">
					操作
				</th>
						
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${not empty dbPage.data}">
					<c:forEach var="item" items="${dbPage.data}" varStatus="status">
						<tr>
							<td>
								<label class="checkbox inline">
									<input type="checkbox" value="${item.fid}"  name="userChoose">
							    </label>
							</td>
							<td>
								${item.floginname }
							</td>
							<td>
								${item.fname }
							</td>
							<td>
								<a class="btn btn-info" href="javascript:void(0)" title="edit" onclick="accountMgr.edit(${item.fid})"><i class="icon-edit icon-white"></i>编辑</a>
								<a class="btn btn-danger" href="javascript:void(0)" title="delete" onclick="accountMgr.del('${item.fid}','${item.name}')"><i class="icon-trash icon-white"></i>删除</a>
							</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan=9>
							暂无数据！
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	
	<div class="pagination pagination-centered">
		<ul id="alertListNub"></ul>
	</div>
	<c:if test="${not empty dbPage.data}">
		<script>
			$("#alertListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",divId:'dAlertContent'});
		</script>
	</c:if>