<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<style type="text/css">
ul{
	list-style:none;
	margin:0px;
}
ul li{
	line-height:40px;
	cursor:pointer;
}
ul li a:HOVER {
	background-color:#F7E7BE;
	border:1px #F7C372 solid;
}
</style>
<script type="text/javascript">
$(function(){
	$("ul li").hover(function(){
		$.each($("ul li"),function(){
			$($(this).children("a")[0].target).hide();
		});
		$($(this).children("a")[0].target).show();
	});
});

function toParent(type){
	var modelid = ${fmodelid};
	window.parent.doCancle();
	window.parent.Widget.addPattern(modelid,type);
}
</script>
<div class="row-fluid sortable ui-sortable">
	<div class="box-content">
		<div style="float:left;width:20%">
			<ul>
				<li><a target="#line" onclick="toParent(0)">1.性能曲线</a></li>
				<li><a target="#topn" onclick="toParent(1)">2.TopN</a></li>
				<li><a target="#capacity" onclick="toParent(3)">3.容量</a></li>
				<%--<li><a target="#log" onclick="toParent(4)">4.日志信息</a></li>--%>
				<%--<li><a target="#list" onclick="toParent(5)">5.列表信息</a></li>--%>
			</ul>
		</div>
		<div style="float:right;width:78%">
			<div id="line" style="display: block;">
				<img alt="性能曲线" src="${path}/resource/img/images/line.png" width="320" height="120">
				<div style="width:80%;margin:0px auto;line-height:20px;">
					<span>曲线信息</span>
				</div>
			</div>
			<div id="topn" style="display: none;">
				<img alt="topn" src="${path}/resource/img/images/topn.png" width="320" height="120">
				<div style="width:80%;margin:0px auto;line-height:20px;">
					<span>TopN信息</span>
				</div>
			</div>
			<div id="capacity" style="display: none;">
				<img alt="容量信息" src="${path}/resource/img/images/capacity.png" width="320" height="120">
				<div style="width:80%;margin:0px auto;line-height:20px;">
					<span>可定制展示存储设备容量信息</span>
				</div>
			</div>
			<div id="log" style="display: none;">
				<img alt="日志信息" src="${path}/resource/img/images/log.png" width="320" height="120">
				<div style="width:80%;margin:0px auto;line-height:20px;">
					<span>交换机、光纤、存储系统的各类型日志信息数量</span>
				</div>
			</div>
			<div id="list" style="display: none;">
				<img alt="列表信息" src="${path}/resource/img/images/list.png" width="320" height="120">
				<div style="width:80%;margin:0px auto;line-height:20px;">
					<span>可定制各设备列表信息</span>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>