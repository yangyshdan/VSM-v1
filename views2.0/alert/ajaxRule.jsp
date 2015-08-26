<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
	<thead>
		<tr>
			<th style="width: 20px;">
				<label class="checkbox inline">
					<input type="checkbox" onclick="AlertRule.doAlertCheck(this.checked);">
			    </label>
			</th>
			<th>
				名称
			</th>
			<!-- 
			<th>
				级别
			</th>
			 -->
			<th>
				是否可用
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
							<a href="javascript:void(0);" onclick="AlertRule.doDbClick('${item.fid}')">${item.fname}</a>
						</td>
						<!-- 
						<td>
							<c:choose>
								<c:when test="${item.flevel == 0}"><span class="label">Info</span> </c:when>
								<c:when test="${item.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
								<c:when test="${item.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
							</c:choose>
						</td>
						 -->
						<td>
							<c:choose>
								<c:when test="${item.fenabled == 0}">
									<i class="icon icon-color icon-close"></i>
								</c:when>
								<c:otherwise>
									<i class="icon icon-color icon-check"></i>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${item.ftype == 'DS'}">存储系统(IBM-DS8k)</c:when>
								<c:when test="${item.ftype == 'BSP'}">存储系统(IBM-DS4k/5k)</c:when>
								<c:when test="${item.ftype == 'SVC'}">存储系统(IBM-SVC)</c:when>
								<c:when test="${item.ftype == 'NAS'}">存储系统(NAS)</c:when>
								<c:when test="${item.ftype == 'EMC'}">存储系统(EMC-CX/VNX)</c:when>
								<c:when test="${item.ftype == 'HDS'}">存储系统(HDS-AMS)</c:when>
								<c:when test="${item.ftype == 'NETAPP'}">存储系统(NETAPP)</c:when>
								<c:when test="${item.ftype == 'PHYSICAL'}">物理机 </c:when>
								<c:when test="${item.ftype == 'VIRTUAL'}">虚拟机 </c:when>
								<c:when test="${item.ftype == 'SWITCH'}">交换机 </c:when>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="4">
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
		$("#ruleListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/AlertRuleAction?func=AjaxPage&name=${name}&enabled=${enabled}",divId:'ruleContent'});
	</script>
</c:if>