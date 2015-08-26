<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@include file="/WEB-INF/views/include/leftMenu.jsp"%>
<script src="${path }/resource/js/ajaxPage.js">
</script>
<script type="text/javascript">
$(function () {
        $('#container').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '容量使用情况'
            },
            xAxis: {
                categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
            },
            yAxis: {
                min: 0,
                title: {
                    text: '容量(T)'
                },
                stackLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                    }
                }
            },
            legend: {
                align: 'right',
                x: -100,
                verticalAlign: 'top',
                y: 20,
                floating: true,
                backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColorSolid) || 'white',
                borderColor: '#CCC',
                borderWidth: 1,
                shadow: false
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.x +'</b><br/>'+
                        this.series.name +': '+ this.y +'<br/>'+
                        'Total: '+ this.point.stackTotal;
                }
            },
            plotOptions: {
                column: {
                    stacking: 'normal',
                    dataLabels: {
                        enabled: true,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    }
                }
            },
            series: [{
                name: '空闲空间',
                data: [5, 3, 4, 7, 2]
            }, {
                name: '已用空间',
                data: [2, 2, 3, 2, 1]
            }]
        });
    });
</script>
</script>
<div id="content" class="span10">
	<div class="well">
		<img src="${path}/resource/img/StorageSystemBase.png"
			style="width: 10%; float: left;margin-top:40px;" />
		<div id="container"
			style="width: 85%; height: 200px; margin: 30px 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid sortable">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					存储系统
				</h2>
			</div>
			<div class='box-content'>
					<div id="loadcontent">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										名称
									</th>
									<th>
										系统
									</th>
									<th>
										DDM_CAP
									</th>
									<th>
										DDM_SPEED
									</th>
									<th>
										更新时间
									</th>
									<th>
										使用状态
									</th>
									<th>
										陈列名称
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty dbPage.data}">
										<c:forEach var="item" items="${dbPage.data}" varStatus="status">
											<tr>
												<td>
													${item.name}
												</td>
												<td>
													${item.sname}
												</td>
												<td>
													${item.ddm_cap}
												</td>
												<td>
													${item.ddm_speed}
												</td>
												<td>
													${item.update_timestamp}
												</td>
												<td>
													${item.operational_status}
												</td>
												<td>
													${item.display_name}
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
						<div id="pageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty dbPage.data}">
							<script>
								$("#pageNub").getLinkStr({pagecount:${dbPage.totalPages},curpage:${dbPage.currentPage},numPerPage:${dbPage.numPerPage},isShowJump:true,ajaxRequestPath:"${path}/servlet/config/ConfigAction?func=AjaxStoragePage",divId:'loadcontent'});
							</script>
						</c:if>
					</div>
			</div>
		</div>
		<!--/span-->
	</div>
	<!--/row-->
</div>

<%@include file="/WEB-INF/views/include/footer.jsp"%>
