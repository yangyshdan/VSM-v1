<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<script src="${path}/resource/js/project/publicscript.js"></script>
<table class="table table-bordered table-striped table-condensed colToggle" style="table-layout:fixed;word-break:break-all;" id="conTable">
	<thead>
		<tr>
			<th style="width: 20px;">
				<label class="checkbox inline">
					<input type="checkbox" onclick="DeviceAlert.doAlertCheck(this.checked);">
			    </label>
			</th>
			<th style="width: 130px;">
				首次发生时间
			</th>
			<th style="width: 130px;">
				最后发生时间
			</th>
			<th style="width: 60px;">
				类型
			</th>
			<th style="width: 55px;">
				重复次数
			</th>
			<th style="width: 90px;">
				状态
			</th>
			<th style="width: 90px;">
				级别
			</th>
			<th style="width: 150px;">
				事件源
			</th>
			<th>
				消息
			</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${not empty dbPage.data}">
				<c:forEach var="item" items="${dbPage.data}" varStatus="status">
					<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','${item.fresourcetype}')">
						<td>
							<label class="checkbox inline">
									<input type="checkbox" value="${item.fruleid}_${item.ftopid}_${item.flogtype}" name="dAlertCheck"/>
						    </label>
						</td>
						<td>
							<fmt:formatDate value="${item.ffirsttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
						</td>
						<td>
							<fmt:formatDate value="${item.flasttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
						</td>
						<td>
							<c:choose>
								<c:when test="${item.flogtype == 3}">硬件告警</c:when>
								<c:when test="${item.flogtype == 2}">阀值告警</c:when>
								<c:when test="${item.flogtype == 1}">TPC告警</c:when>
								<c:when test="${item.flogtype == 0}">系统告警</c:when>
							</c:choose>
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
							<a href="javascript:goToEventDetailPage('${item.ftopid}', '${item.ftoptype}','${item.fresourceid}')">${item.fresourcename}</a>
						</td>
						<td>
							<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','${resourceType}')" data-placement="left"  data-rel="popover" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename} <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
 <c:out value="${fn:substring(item.fdetail, 0, 200)}......" /></c:when> <c:otherwise><c:out value="${item.fdetail}" /></c:otherwise></c:choose>" title="详细信息">
								 ${item.fdescript}
							</a>
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
	<ul id="chartListNub"></ul>
</div>
<c:if test="${not empty dbPage.data}">
	<script>
		$('[rel="popover"],[data-rel="popover"]').popover();
		var param = $("#hiddenForm").serialize();
		$("#chartListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage&resourceId=${resourceId}&topId=${topId}&resourceType=${resourceType}&level=${level}&state=${state}${attachment}&"+param,divId:'dAlertContent'});
	</script>
</c:if>
<form id="hiddenForm"> 
	<input type="hidden" name="resourceId" value="${resourceId}" />
	<input type="hidden" name="topId" value="${topId}" />
	<input type="hidden" name="resourceType" value="${resourceType}" />
	<input type="hidden" name="state" value="${state}" />
	<input type="hidden" name="level" value="${level}" />
	<input type="hidden" name="logType" value="${logType}" />
	<input type="hidden" name="resourceName" value="${resourceName}" />
	<input type="hidden" name="startDate" value="${startDate}" />
	<input type="hidden" name="endDate" value="${endDate}" />
</form>
