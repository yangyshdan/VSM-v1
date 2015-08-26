<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
$(function () {
var name=${name1};
var map=${map1};
    $('#container').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: '容量TOP20'
        },
        xAxis: {
            categories:name,
            labels:{
           		rotation:-15,
           		align:'right'
          		}
        },
        yAxis: {
            min: 0,
            title: {
                text: '容量(G)'
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
            enabled:false
        },
        tooltip: {
            formatter: function() {
                return '<b>'+ this.x +'</b><br/>'+
                    this.series.name +': '+ this.y +'<br/>';

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
        series:map
    });
});

function poolDbClick(id){
	window.location.href = "${path}/servlet/sr/pool/PoolAction?func=PoolInfo&subSystemID=${subSystemID}&poolId="+id+"&r="+Math.random();
}
	
//数据查询
function poolFilter(){
	var greatCapcity = $("#greatCapacity").val();
	var lessCapcity = $("#lessCapacity").val();
	var res = /^\d+(\.{1}\d+)?$/;
	var isFlag = false;
	if(greatCapcity!=""){
		if(!res.test(greatCapcity)){
			bAlert("请输入有效的容量范围!");
			return false;
		}
	}
	if(lessCapcity!=""){
		if(!res.test(lessCapcity)){
			bAlert("请输入有效的容量范围!");
			return false;
		}
	}
	if(greatCapcity!="" && lessCapcity!=""){
		if(greatCapcity<lessCapcity){
			bAlert("起始容量应小于结束容量，请重新输入!");
			return false;
		}else{
			isFlag = true;
		}
	}else{
		isFlag = true;
	}
	if(isFlag){
		var jsonVal = {name:$("#poolName").val(),greatCapacity:(greatCapcity*1024),lessCapacity:(lessCapcity*1024),subSystemID:subSystemID};
		loadData("${path}/servlet/sr/pool/PoolAction?func=AjaxPoolPage",jsonVal,$("#poolContent"));
	}
}
function doFreshen(){
	var jsonVal={name:$("#hiddenPortName").val(),greatCapacity:$("#hiddenGreatCapecity").val(),lessCapacity:$("#hiddenLessCapecity").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/sr/pool/PoolAction?func=AjaxPoolPage",jsonVal,$("#poolContent"));	
}
//清除
function clearData(){
	$("#poolName").val("");
	$("#greatCapacity").val("");
	$("#lessCapacity").val("");
}
$(clearData);
</script>
<div id="content">
	<div class="well">
		<img src="${path}/resource/img/project/pool.png" style="width: 10%; float: left; padding-top:20px;" />
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>存储池</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div id="myTabContent">
					<!-- 存储切换页开始 -->
					<div class="well" style="text-align: center;">
							<table border="0" style="margin:auto;">
								<tr>
									<td>名称</td>
									<td align="right" style="width:185px;"><input id="poolName" type="text" style="width:180px"></td>
									<td style="width:30px;">&nbsp;</td>
									<td>容量</td>
									<td align="left" style="width:185px;"><input id="lessCapacity" type="text" style="width:180px"></td>
									<td style="width:10px;">-</td>
									<td align="left" style="width:185px;"><input id="greatCapacity" type="text" style="width:180px"></td>
								</tr>
								<tr>
									<td colspan="4">&nbsp;</td>
								</tr>
								<tr>
									<td></td>
									<td align="right"><button class="btn btn-primary" onclick="clearData();">重置</button></td>
									<td style="width:20px;">&nbsp;</td>
									<td align="left"><button class="btn btn-primary" onclick="poolFilter();">查询</button></td>
									<td></td>
								</tr>
							</table>
						</div>
					<div class="tab-pane active" id="poolContent">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										存储池名
									</th>
									<th>
										RAID种类
									</th>
									<th>
										总逻辑容量(G)
									</th>
									<th>
										已用逻辑容量(G)
									</th>
									<th>
										LUN数量
									</th>
									<th>
										后端磁盘数量
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty dbPage.data}">
										<c:forEach var="item" items="${dbPage.data}" varStatus="status">
											<tr>
												<td>
													<a href="${path}/servlet/sr/pool/PoolAction?func=PoolInfo&poolId=${item.pool_id}&subSystemID=${subSystemID}">POOL ${item.name}</a>
												</td>
												<td>
													${item.raid_level}
												</td>
												<td>
													<fmt:formatNumber value="${item.total_usable_capacity/1024}" pattern="0.00" />
												</td>									
												<td>	
													<fmt:formatNumber value="${(item.total_usable_capacity-item.unallocated_capacity)/1024}" pattern="0.00" />
												</td>
												<td>
													${item.num_lun}
												</td>
												<td>
													${item.num_backend_disk}
												</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan=4>暂无数据！</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="poolListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty dbPage.data}">
							<script>
								$("#poolListpageNub").getLinkStr({
									pagecount:"${dbPage.totalPages}",
									curpage:"${dbPage.currentPage}",
									numPerPage:"${dbPage.numPerPage}",
									isShowJump:true,
									ajaxRequestPath:"${path}/servlet/sr/pool/PoolAction?func=AjaxPoolPage&subSystemID=${subSystemID}",
									divId:'poolContent'
								});
								var $csv = $("#exportCSV");
								var exurl = "${path}/servlet/sr/pool/PoolAction?func=exportPoolConfigData&subSystemID=${subSystemID}";
								$csv.unbind();
								$csv .attr("href",exurl);
							</script>
						</c:if>
						<c:if test="${empty dbPage.data}">
							<script>
								var $csv = $("#exportCSV");
								$csv.unbind();
								$csv.attr("href", "javascript:void(0);");
								$csv.bind("click", function(){ bAlert("暂无可导出数据！"); });
							</script>
						</c:if>
						<input type="hidden" id="hiddenPortName" value="${name}"/>
						<input type="hidden" id="hiddenGreatCapecity" value ="${greatCapacity}"/>
						<input type="hidden" id="hiddenLessCapecity" value="${lessCapacity}"/>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>

<%@include file="/WEB-INF/views/include/footer.jsp"%>