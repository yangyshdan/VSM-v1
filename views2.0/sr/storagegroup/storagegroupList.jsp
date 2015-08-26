<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
$(function () {
	var args = ${array};
	var categore = ${names};
    $('#container').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: 'StorageGroup AVG IOps TOP10 In Last 24 Hour'
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
		       			//alert(this.diskgroupId);
		       			window.location = "${path}/servlet/sr/storagegroup/StoragegroupAction?func=StoragegroupPerfInfo&subSystemID="+this.subsystemId+"&hostgroupId="+this.hostgroupId;
		      			}
		      		}
      			}
            }
        },
        series: args
    });
});
function storagegroupDbClick(id){
    window.location = "${path}/servlet/sr/storagegroup/StoragegroupAction?func=StoragegroupPerfInfo&subSystemID=${subSystemID}&hostgroupId="+id;
}
//数据提交
function storageFilter(){
	var jsonVal = {hostgroupName:$("#storagegroupName").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/sr/storagegroup/StoragegroupAction?func=AjaxStoragegroupPage",jsonVal,$("#storagegroupContent"));
}
//刷新
function doFreshen(){
	var jsonVal = {hostgroupName:$("#hiddenGroupName").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/sr/storagegroup/StoragegroupAction?func=AjaxStoragegroupPage",jsonVal,$("#storagegroupContent"));
}
//重置
function clearData(){
	$("#storagegroupName").val("");
}
$(clearData);
</script>
<div id="content">
	<div class="well">
		<img src="${path}/resource/img/project/StorageSystemBase.png"
			style="width: 10%; float: left;margin-top:20px;" />
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					 存储关系组
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var hostgroupName = $("#hiddenGroupName").val();
						var exurl = "${path}/servlet/sr/storagegroup/StoragegroupAction?func=ExportStoragegroupConfigData&subSystemID=${subSystemID}&hostgroupName=${hostgroupName}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
			<div class="well" style="text-align: center;">
				<table border="0" style="margin:auto;">
						<tr>
							<td colspan="2">存储关系组名</td>
							<td align="right""><input id="storagegroupName" type="text" style="width:180px"></td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td align="right"><button class="btn btn-primary" onclick="clearData();">重置</button></td>
							<td style="width:30px;">&nbsp;</td>
							<td align="left"><button class="btn btn-primary" onclick="storageFilter();">查询</button></td>
						</tr>
				</table>
			</div>
			<div id="storagegroupContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								网络地址
							</th>
							<th>
								系统
							</th>
							<th>
								是否可共享 
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty storagegroupPage.data}">
								<c:forEach var="item" items="${storagegroupPage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/storagegroup/StoragegroupAction?func=StoragegroupPerfInfo&hostgroupId=${item.hostgroup_id}&subSystemID=${subSystemID}">${item.hostgroup_name}</a>
										</td>
										<td>
											${item.uid}
										</td>
										<td>
											${item.model}
										</td>
										<td>
											${item.shareable}
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=4>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div id="storagegroupPageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty storagegroupPage.data}">
					<script>
						$("#storagegroupPageNub").getLinkStr({pagecount:"${storagegroupPage.totalPages}",curpage:"${storagegroupPage.currentPage}",numPerPage:"${storagegroupPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storagegroup/StoragegroupAction?func=AjaxStoragegroupPage&subSystemID=${subSystemID}",divId:'storagegroupContent'});
					</script>
				</c:if>	
				<c:if test="${empty storagegroupPage.data}">
					<script>
						$("#exportCSV").unbind();
						$("#exportCSV").attr("href","javascript:void(0);");
						$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
					</script>
				</c:if>
				<input type="hidden" id="hiddenGroupName" value="${hostgroupName}"/>
			</div>
			</div>
		</div>
	</div>
</div>	
<%@include file="/WEB-INF/views/include/footer.jsp"%>
