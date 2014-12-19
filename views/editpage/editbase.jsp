<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script>
</script>
<div class="box span6" id="charts_${item.fmodelid}_new">
	<div class="box-header well">
		<h2>
		<c:choose>
		<c:when test="${item.fcharttype==0}">
			<!-- 曲线 -->
			<i class="icon-random"></i>
		</c:when>
		<c:when test="${item.fcharttype==1}">
			<!-- topn类图 -->
			<i class="icon-align-left"></i>
		</c:when>
		<c:when test="${item.fcharttype==2}">
			<!-- 容量类图 -->
			<i class="icon-signal"></i>
		</c:when>
		<c:when test="${item.fcharttype==3}">
			<!-- 饼图 -->
			<i class="icon-adjust"></i>
		</c:when>
		<c:when test="${item.fcharttype==4}">
			<!-- 列表图 -->
			<i class="icon-list-alt"></i> 
		</c:when>
		</c:choose>
		
		新增</h2>
		<div class="box-icon">
			<a href="javascript:void(0)" class="btn btn-round" onclick="Index.chartsFreshen(${item.fmodelid}_new)" title="刷新" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
			<a href="javascript:void(0)" class="btn btn-round" onclick="resize('charts_${item.fmodelid}_new',this); " title="调整大小" data-rel="tooltip"><i class="icon-color icon-th-large"></i></a>
			<a href="javascript:void(0)" class="btn btn-close btn-round" title="关闭" data-rel="tooltip"><i class="icon-remove"></i></a>
		</div>
	</div>
	<div class="box-content" id="box-content_${item.fmodelid}_new">
		<div class="clearfix" id="perfContent_${item.fmodelid}_new"></div>
	</div>
</div>
