<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script type="text/javascript">
//highcharts X轴为时间轴时，不使用默认时间
$(function(){Highcharts.setOptions({global: {useUTC: false}});});
var subSystemID = "${subSystemID}";
$(function () {
		var args = ${array};
		var categore = ${categories};
        $('#container').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: 'RAID Group AVG IOps TOP10 In Last 24 Hour'
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
			        			window.location = "${path}/servlet/sr/diskgroup/DiskgroupAction?func=LoadDiskgroupInfo&subSystemID="+this.subsystemId+"&diskgroupID="+this.diskgroupId;
		        			}
		        		}
		        	}
                }
            },
            series: args
        });
    });
function diskDbClick(id){
    window.location = "${path}/servlet/sr/diskgroup/DiskgroupAction?func=LoadDiskgroupInfo&subSystemID=${subSystemID}&diskgroupID="+id;
}
//数据提交
function storageFilter(){
	var jsonVal = {diskgroupName:$("#diskgroupName").val(),raidLevel:$("#raidLevel").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/sr/diskgroup/DiskgroupAction?func=AjaxStoragePage",jsonVal,$("#diskContent"));
}
//刷新
function doFreshen(){
	var jsonVal = {diskgroupName:$("#hiddenDiskName").val(),raidLevel:$("#hiddenLevel").val(),subSystemID:subSystemID};
	loadData("${path}/servlet/sr/diskgroup/DiskgroupAction?func=AjaxStoragePage",jsonVal,$("#diskContent"));
}
//重置
function clearData(){
	$("#diskgroupName").val("");
	$("#raidLevel").val("");
}
$(clearData);
</script>
<div id="content">
	<div class="well">
		<img src="${path}/resource/img/project/diskgroup.png"
			style="width: 10%; float: left;margin-top:20px;" />
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					RAID组
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV"><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/sr/diskgroup/DiskgroupAction?func=exportConfigData&subSystemID=${subSystemID}";
						$("#exportCSV").attr("href",exurl);
					</script>
				</div>
			</div>
			<div class="box-content">
			<div class="well" style="text-align: center;">
				<table border="0" style="margin:auto;">
						<tr>
							<td>名称</td>
							<td align="right" style="width:185px;"><input id="diskgroupName" type="text" style="width:180px"></td>
							<td style="width:30px;">&nbsp;</td>
							<td>阵列</td>
							<td align="left" style="width:185px;"><input id="raidLevel" type="text" style="width:180px"></td>
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
			<div id="diskContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>
								RAID组名
							</th>
							<th>
								RAID种类
							</th>
							<th>
								磁盘端口速度
							</th>
							<th>
								磁盘容量(G)
							</th>
							<th>
								磁盘数
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty diskPage.data}">
								<c:forEach var="item" items="${diskPage.data}" varStatus="status">
									<tr>
										<td>
											<a href="${path}/servlet/sr/diskgroup/DiskgroupAction?func=LoadDiskgroupInfo&diskgroupID=${item.diskgroup_id}&subSystemID=${subSystemID}">RAID Group ${item.name}</a>
										</td>
										<td>
											${item.raid_level}
										</td>
										<td>
											${item.ddm_speed}
										</td>
										<td>
											<fmt:formatNumber value="${item.ddm_cap/1024}" pattern="0.00"/>
										</td>
										<td>
											${item.width}
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
				<div id="diskListpageNub" class="pagination pagination-centered"></div>
				<c:if test="${not empty diskPage.data}">
					<script type="text/javascript">
						$("#diskListpageNub").getLinkStr({pagecount:"${diskPage.totalPages}",curpage:"${diskPage.currentPage}",numPerPage:"${diskPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/sr/diskgroup/DiskgroupAction?func=AjaxStoragePage&diskgroupName=${diskgroupName}&raidLevel=${raidLevel}&subSystemID=${subSystemID}",divId:'diskContent'});
					</script>
				</c:if>
				<c:if test="${empty diskPage.data}">
					<script>
						$("#exportCSV").unbind();
						$("#exportCSV").attr("href","javascript:void(0);");
						$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
					</script>
				</c:if>
			</div>
		</div>
		</div>
	</div>
</div>	
<%@include file="/WEB-INF/views/include/footer.jsp"%>
