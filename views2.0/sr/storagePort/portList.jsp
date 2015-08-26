<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js">
</script>
<script type="text/javascript">
var args = ${array};
var categore = ${categories};
var subSystemID = ${subSystemID==null?0:subSystemID};
$(function () {
        $('#container').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '端口速率'
            },
            xAxis: {
                categories: categore
            },
            yAxis: {
                min: 0,
                title: {
                    text: '速率(GBPS)'
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
            series: args
        });
    });
function portDbClick(id){
    window.location = "${path}/servlet/sr/storageport/StoragePortAction?func=LoadPortInfo&subSystemID=${subSystemID}&portId="+id;
}
//数据提交
function storageFilter(){
	var jsonVal = {portName:$("#portName").val(),networkAddress:$("#networkAddress").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/sr/storageport/StoragePortAction?func=AjaxStoragePage",jsonVal,$("#portContent"));
}
//刷新
function doFreshen(){
	var jsonVal = {portName:$("#hiddenPortName").val(),networkAddress:$("#hiddenAddress").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/sr/storageport/StoragePortAction?func=AjaxStoragePage",jsonVal,$("#portContent"));
}
//重置
function clearData(){
	$("#portName").val("");
	$("#networkAddress").val("");
}

$(clearData);
</script>
<div id="content">
	<div class="well">
		<img src="${path}/resource/img/project/storageport.png"
			style="width: 10%; float: left;margin-top:20px;" />
		<div id="container"
			style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					存储系统端口
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/sr/storageport/StoragePortAction?func=exportPortConfigData&subSystemID=${subSystemID}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
					<div class="well" style="text-align: center;">
						<table border="0" style="margin:auto;">
								<tr>
									<td>名称</td>
									<td align="right" style="width:185px;"><input id="portName" type="text" style="width:180px"/></td>
									<td style="width:30px;">&nbsp;</td>
									<td>网络地址</td>
									<td align="left" style="width:185px;"><input id="networkAddress" type="text" style="width:180px"/></td>
								</tr>
								<tr>
									<td colspan="4">&nbsp;</td>
								</tr>
								<tr>
									<td></td>
									<td align="right"><button class="btn btn-primary" onclick="clearData();">重置</button></td>
									<td style="width:20px;">&nbsp;</td>
									<td align="left"><button class="btn btn-primary" onclick="storageFilter();">查询</button></td>
									<td></td>
								</tr>
						</table>
					</div>
					<div id="portContent">
						<table class="table table-bordered table-striped table-condensed">
							<thead>
							<tr>
								<th>
									端口名
								</th>
								<th>
									网络地址
								</th>
								<th>
									端口速率
								</th>
								<th>
									端口类型
								</th>
							</tr>
						</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty portPage.data}">
										<c:forEach var="item" items="${portPage.data}" varStatus="status">
											<tr>
											<td>
												<a href="${path}/servlet/sr/storageport/StoragePortAction?func=LoadPortInfo&portId=${item.port_id}&subSystemID=${subSystemID}">${item.name}</a>
											</td>
											<td>
												${item.network_address}
											</td>
											<td>
												${item.port_speed}
											</td>
											<td>
												${item.type}
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
						<div id="portListpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty portPage.data}">
							<script>
								$("#portListpageNub").getLinkStr({pagecount:"${portPage.totalPages}",curpage:"${portPage.currentPage}",numPerPage:"${portPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/storageport/StoragePortAction?func=AjaxStoragePage&portName=${portName}&networkAddress=${networkAddress}&subSystemID=${subSystemID}",divId:'portContent'});
							</script>
						</c:if>
						<c:if test="${empty portPage.data}">
							<script>
								$("#exportCSV").unbind();
								$("#exportCSV").attr("href","javascript:void(0);");
								$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
							</script>
						</c:if>
						<input type="hidden" id = "hiddenPortName" value="${portName}"/>
						<input type="hidden" id = "hiddenAddress" value="${networkAddress}"/>
					</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>
