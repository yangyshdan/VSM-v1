<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed">
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
<script>
	$(function(e){
			$('.make-switch').bootstrapSwitch();
	});
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
</script>