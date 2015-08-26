<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js">
</script>
<script type="text/javascript">
$(function () {
	var args = ${array};
	var categore = ${names};
    $('#container').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: 'Volume AVG IOps TOP10 In Last 24 Hour'
        },
        xAxis: {
            categories: categore,
            labels:{
            	rotation:-15,
            	align:'right'
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'AVG IOps'
            },
            stackLabels: {
                enabled: true,
                style: {
                    fontWeight: 'bold',
                    color: (Highcharts.theme && Highcharts.theme.textColor) || 'black'
                }
            }
        },
        legend: {
        	enabled:false
        },
        tooltip: {
            formatter: function() {
                return '<b>'+ this.x +'</b><br/>'+ this.y+' '+this.series.name;
            }
        },
        plotOptions: {
            column: {
            	cursor:'pointer',
                dataLabels: {
                    enabled: true,
                    color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'black'
                },
                point:{
		      		events:{
		      			click:function(){
		       				window.location = "${path}/servlet/sr/volume/VolumeAction?func=AjaxPrfVolumePage&subSystemID="+this.subsystemId+"&id="+this.volumeId;
		      			}
		      		}
		      	}
            }
        },
        series: args
    });
});

function volumeDbClick(id){
	window.location.href = "${path}/servlet/sr/volume/VolumeAction?func=AjaxPrfVolumePage&subSystemID=${subSystemID}&id="+id+"&r="+Math.random();
}
	
//数据查询
function volumeFilter(){
	var greatCapcity = $("#greatLogical_Capacity").val();
	var lessCapcity = $("#lessLogical_Capacity").val();
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
		var jsonVal = {name:$("#volumeName").val(),greatLogical_Capacity:greatCapcity,lessLogical_Capacity:lessCapcity};
		loadData("${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
	}
}
function doFreshen(){
	var jsonVal={name:$("#hiddenName").val(),lessLogical_Capacity:$("#hiddenLessLogical_Capacity").val(),greatLogical_Capacity:$("#hiddenGreatLogical_Capacity").val()};
	loadData("${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage",jsonVal,$("#volumeContent"));
}
//清除
function clearData(){
	$("#volumeName").val("");
	$("#greatLogical_Capacity").val("");
	$("#lessLogical_Capacity").val("");
}
$(clearData);
</script>
<div id="content">
	<div class="well">
		<img src="${path}/resource/img/project/volume.png"
			style="width: 10%; float: left; padding-top:20px;" />
		<div id="container"
			style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					卷
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/sr/volume/VolumeAction?func=expertVolumeConfigData&subSystemID=${subSystemID}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
				<div id="myTabContent">
					<div class="well" style="text-align: center;">
						<table border="0" style="margin:auto;">
							<tr>
								<td>名称:</td>
								<td align="right" style="width:185px;"><input id="volumeName" type="text" style="width:180px"></td>
								<td style="width:30px;">&nbsp;</td>
								<td>逻辑容量范围 :</td>
								<td align="left" style="width:185px;"><input id="lessLogical_Capacity" type="text" style="width:180px"> - </td>
								<td align="left" style="width:185px;"><input id="greatLogical_Capacity" type="text" style="width:180px"></td>
							</tr>
							<tr>
								<td colspan="6">&nbsp;</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td align="right"><button class="btn btn-primary" onclick="clearData();">重置</button></td>
								<td align="left">  <button class="btn btn-primary" onclick="volumeFilter();">查询</button></td>
								<td></td>
							</tr>
						</table>
					</div>
					<div class="tab-pane active" id="volumeContent">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
								<tr>
									<th>
										逻辑卷名
									</th>
									<th>
										逻辑空间(G)
									</th>
									<th>
										实占空间(G)
									</th>
									<th>
										RAID类型
									</th>
									<th>
										所属控制器
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty volumePage.data}">
										<c:forEach var="item" items="${volumePage.data}" varStatus="status">
											<tr>
												<td>
													<a href="${path}/servlet/sr/volume/VolumeAction?func=AjaxPrfVolumePage&id=${item.volume_id}&subSystemID=${subSystemID}">LUN ${item.name}</a>
												</td>
												<td>
													<fmt:formatNumber value="${item.logical_capacity/1024}" pattern="0.00"/>
												</td>
												<td>
													<c:if test="${not empty item.physical_capacity}"><fmt:formatNumber value="${item.physical_capacity/1024}" pattern="0.00"/></c:if>
													<c:if test="${empty item.physical_capacity}">N/A</c:if>
												</td>
												<td>
													${item.raid_level}
												</td>
												<td>
													<c:if test="${empty item.current_owner}">N/A</c:if>
													<c:if test="${not empty item.current_owner}">
														<a href="${path}/servlet/storagenode/StoragenodeAction?func=StoragenodePrfInfo&spId=${item.sp_id}&subSystemID=${subSystemID}">${item.current_owner}</a>
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan=5>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
						<div id="volumeListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty volumePage.data}">
							<script>
								$("#volumeListpageNub").getLinkStr({pagecount:"${volumePage.totalPages}",curpage:"${volumePage.currentPage}",numPerPage:"${volumePage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/volume/VolumeAction?func=AjaxVolumePage&poolId=${poolId}&subSystemID=${subSystemID}",divId:'volumeContent'});
							</script>
						</c:if>
						<c:if test="${empty volumePage.data}">
							<script>
								$("#exportCSV").unbind();
								$("#exportCSV").attr("href","javascript:void(0);");
								$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
							</script>
						</c:if>
						<input type="hidden" id="hiddenName" value="${name}"/>
						<input type="hidden" id="hiddenLessLogical_Capacity" value="${lessLogical_Capacity}"/>
						<input type="hidden" id="hiddenGreatLogical_Capacity" value="${greatLogical_Capacity}"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>