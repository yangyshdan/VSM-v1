<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>

<div node-type="outer" id="outer" class="layer_form_tips" style="width: 215px; left: 670px;visibility:visible;">
	<div class="bg">
		<div node-type="inner" class="content">
			<a onclick="closeOuter('outer')" class="W_ico12 icon_close"></a>
			<c:forEach var="error" items="${actionErrors}">
				<p node-type="msg" class="tips"><span class="icon_delS"></span>${error }</p>
			</c:forEach>
		</div>
		<div class="arrow arrow_tips" style="left: 86px;"></div>
	</div>
</div>
<c:remove var="actionErrors" scope="request" />
<div class="clearfix"></div>

