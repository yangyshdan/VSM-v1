<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/project/widget.js"></script>
<div class="row-fluid sortable" id="chartsContent">
<c:if test="${not empty charts}">
<c:forEach items="${charts}" var="item">
	<div class="box span${item.fsize}" id="charts_${item.fmodelid}_${item.fid}">
		<div class="box-header well">
			<h2 style="width:70%;height:20px;overflow: hidden;"><c:choose>
			<c:when test="${item.fcharttype==0}">
				<i class="icon-random"></i>
			</c:when>
			<c:when test="${item.fcharttype==1}">
				<i class="icon-align-left"></i>
			</c:when>
			<c:when test="${item.fcharttype==2}">
				<i class="icon-signal"></i>
			</c:when>
			<c:when test="${item.fcharttype==3}">
				<i class="icon-adjust"></i>
			</c:when>
			<c:when test="${item.fcharttype==4}">
				<i class="icon-list-alt"></i> 
			</c:when>
			</c:choose>${item.fname}</h2>
			<div class="box-icon">
				<a href="javascript:void(0)" class="btn btn-round" onclick="Widget.refresh('charts_${item.fmodelid}_${item.fid}')" title="刷新" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
				<a href="javascript:void(0)" class="btn btn-round" onclick="resize('charts_${item.fmodelid}_${item.fid}',this); " title="调整大小" data-rel="tooltip"><i class="icon-color icon-th-large"></i></a>
				<a href="javascript:void(0)" class="btn btn-round" onclick="Widget.editModule(${item.fmodelid},${item.fid},${item.fcharttype})" title="编辑" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
				<a href="javascript:void(0)" class="btn btn-round" onclick="Widget.closeWidget('${item.fid}')" title="关闭" data-rel="tooltip"><i class="icon-remove"></i></a>
			</div>
		</div>
		<div class="box-content" id="box-content_${item.fmodelid}_${item.fid}" style="height:200px;">
			<div class="clearfix" id="perfContent_${item.fmodelid}_${item.fid}" style="height:200px;"></div>
		</div>
		<script type="text/javascript">
		$(function(){
			Widget.refresh('perfContent_${item.fmodelid}_${item.fid}');
			Timer['${item.fmodelid}_${item.fid}'] = setInterval('Widget.refresh("perfContent_${item.fmodelid}_${item.fid}")','${item.frefresh * 60 * 1000}');
		});
		</script>
	</div>
</c:forEach>
</c:if>
	<div class="box span4" id="add-another">
		<div class="box-header well">
			<h2><i class="icon-plus-sign"></i> 添加</h2>
		</div>
		<div id="add-temp" class="box-content" style="background-color:#eee;line-height:180px;border:1px #ccc solid;" align="center" onclick="Index.doAddCharts(${fid})">
			<div class="clearfix"><span><i class="icon-plus"></i>添加模块</span></div>
		</div>
	</div>
</div>