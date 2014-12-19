<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include  file="/WEB-INF/views/include/pagePublic.jsp"%>


<c:if test="${not empty actionErrors}">

<div class="modal hide fade" id="myModal">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>提示信息</h3>
	</div>
	<div class="modal-body">
	<c:forEach var="error" items="${actionErrors}">
		<p>${error}</p>
	</c:forEach>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn btn-primary">确定</a>
	</div>
</div>
<c:remove var="actionErrors" scope="request" />
</c:if>
