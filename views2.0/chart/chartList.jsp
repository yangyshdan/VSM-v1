<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/Chart.js"></script>
<script type="text/javascript">
	function doListRefresh(){
		var id = "${modelId}";
		loadData("${path}/servlet/chart/ChartAction?func=AjaxPage",{modelId:id},$("#chartContent"));
		loadData("${path}/servlet/chart/ChartAction?func=AjaxLayout",{modelId:id},$("#layoutDiv"));
		return false;
	}
</script>
<style>
.layoutChart .row-fluid {
	height:30px;
}

.layoutChart .span6 {
    width: 49.8%;
}

.layoutChart .span8 {
    width: 66.4%;
}

.layoutChart .span4 {
    width: 33.2%;
}
.layoutChart [class*="span"] {
    float: left;
    margin-left: 1px;
}

</style>
<div id="content">
<!-- 列表开始 -->
	<div class="row-fluid sortable">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					图表
				</h2>
				<div class="box-icon">
					<!--  <a href="#" class="btn btn-round" title="新增" data-rel="tooltip" onclick="Chart.doChartAdd('${modelId}');"><i class="icon icon-color icon-edit"></i> </a>-->
					<a href="#" class="btn btn-round" title="编辑" data-rel="tooltip" onclick="Chart.doChartEdit('${modelId}');"><i class="icon icon-color icon-compose"></i> </a>
					<a href="#" class="btn btn-round" title="删除" data-rel="tooltip" onclick="Chart.doChartDel();"><i class="icon icon-color icon-trash"></i> </a>
	<!-- 				<a href="#" class="btn btn-round" title="隐藏" data-rel="tooltip" onclick="Chart.doChartShow(0);"><i class="icon icon-red icon-locked"></i> </a>
					<a href="#" class="btn btn-round" title="显示" data-rel="tooltip" onclick="Chart.doChartShow(1);"><i class="icon icon-green icon-unlocked"></i> </a>
					 -->
					<a href="#" class="btn btn-round" title="刷新" data-rel="tooltip" onclick="doListRefresh();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;" id="chartContent">
				<table class="table table-bordered table-striped bootstrap-datatable">
					<thead>
						<tr>
							<th  style="width: 20px;">
								<label class="checkbox inline">
									<input type="checkbox"   onclick="Chart.doChartCheck(this.checked);">
							    </label>
							</th>
							<th>
								名称
							</th>
							<th>
								设备类型
							</th>
							<th>
								y轴名称
							</th>
							<th>
								刷新时间(分钟)
							</th>
							<th>
								是否显示设备名称
							</th>
							<th>
								是否显示
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr style="cursor:pointer;">
										<td>
											<label class="checkbox inline">
												<input type="checkbox" value="${item.fid}"  name="chartCheck">
										    </label>
										</td>
										<td>
											${item.fname}
										</td>
										<td>
											${item.fdevicetype}
										</td>
										<td>
											${item.fyaxisname}
										</td>
										<td>
											${item.frefresh}
										</td>
										<td>
											<c:choose>
												<c:when test="${item.flegend == 0}"><i class="icon icon-color icon-close"></i></c:when>
												<c:otherwise><i class="icon icon-color icon-check"></i></c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
											  	<c:when test="${item.fisshow == 1}">
											  		 <div id="check_${item.fid}" name="${item.fname}" class="make-switch">
										                 <input type="checkbox" checked>
										             </div>
											  	</c:when>
											  	<c:otherwise>
											  		<div id="check_${item.fid}" name="${item.fname}" class="make-switch">
										                 <input type="checkbox">
										             </div>
											  	</c:otherwise>
											 </c:choose>	
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=7>
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
						$("#chartListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/chart/ChartAction?func=AjaxPage",divId:'chartContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<script>
		$('.make-switch').on('switch-change', function (e, data) {
		    var $el = $(data.el) , value = data.value;
		      console.log(e, $el, value);
		      var id = $(this).attr("id").replace("check_","");
		      if(data.value)
		    	 Chart.drawDiv($(this).attr("name"),id) 
		      else
		    	$("#layout_"+id).remove();  
		      
		     $(".tooltip").hide();
		 });
		function saveLayout(id){
			Chart.saveLayout(id);
		}
	</script>
	<!-- 列表结束 --><%--
	
	<div id="layoutDiv" class="well sortable layoutChart" style="overflow:hidden;margin-top:10px;">
		<legend style="margin-bottom: 5px;"><i class="icon32 icon-color icon-newwin"></i>排版设置 <input style="float:right;margin-right:20px;" type="button" onclick="saveLayout('${modelId}');" class="btn btn-primary" value="保存 "/></legend>
		
			<c:choose>
				<c:when test="${not empty layoutChart}">
					<c:forEach var="item" items="${layoutChart}" varStatus="status">
						<c:if test="${item.fisshow == 1}">
						<!-- 性能开始 -->
							<div class="box span${item.fsize}" id="layout_${item.fid}">
								<div class="box-header well">
								<h2>${item.fname}</h2>
								<div class="box-icon">
									<a href="#" class="btn btn-round"  data-rel="tooltip" title="放大" onclick="Chart.enlargeDiv('${item.fid}')"><i class="icon icon-color icon-zoomin"></i> </a>
									<a href="#" class="btn btn-round"  data-rel="tooltip" title="缩小" onclick="Chart.narrowDiv('${item.fid}')"><i class="icon icon-color icon-zoomout"></i> </a>
									<a href="#" class="btn btn-round" data-rel="tooltip" title="设置"  onclick="Chart.doChangeChart('${item.fid}')"> <i class="icon-cog"></i></a>
									<a href="#" data-rel="tooltip" title="隐藏" class="btn  btn-round" onclick="Chart.removeLayout('${item.fid}')"><i class="icon-remove"></i></a>
								</div>
							</div> 
							<div class="box-content"><div class="row-fluid"></div></div>
							</div>
							<!-- 性能结束 -->
						</c:if>
					</c:forEach>
				</c:when>
				<c:otherwise>
							
				</c:otherwise>
			</c:choose>
	</div>
</div>
--%><%@include file="/WEB-INF/views/include/footer.jsp"%>