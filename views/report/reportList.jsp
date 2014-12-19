<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
function DbClick(path){
	window.open("${path}/"+path,"_blank");
}
	
//数据查询
function Filter(){
	var startTime = $("input[type='text'][name='startTime']").val();
	var endTime = $("input[type='text'][name='endTime']").val();
    if(!dateCompare(startTime,endTime)) {                            
    	alert("结束日期不能小于开始日期！");
        return false;
	}
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/report/ReportAction?func=AjaxReportPage",jsonVal,$("#myTabContent"));
}
function doFreshen(){
	var jsonArray = $("#hiddenForm").serializeArray();
	var jsonVal={};
	$.each(jsonArray,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/report/ReportAction?func=AjaxReportPage",jsonVal,$("#myTabContent"));	
}
function addReport(){
	window.location="${path}/servlet/report/ReportAction?func=CustomReport";
}
function templateList(){
	window.location="${path}/servlet/report/ReportTemplate";
}
function delReport(id){
	$.ajax({
		url:"${path}/servlet/report/ReportAction?func=DelReport",
		data:{id:id},
		success:function(data){
			if(data=='true'){
				doFreshen();
			}else{
				bAlert("系统繁忙,请稍后操作");
			}
		}
	});
}
function del(id,displayName){
	var alertStr = "<div class='modal-header'><button type='button' class='close' data-dismiss='modal'>×</button>";
	alertStr += "<h3><button style='width:32px;height:32px;background-image:url(${path}/resource/img/dialogs.png);background-repeat: no-repeat;background-position:0px -31px;border:none;background-color:#ffffff;'></button> 提示信息</h3>";
	alertStr += "</div><div class='modal-body' id='alertdiv'>";
	alertStr += "<span>确认删除: "+displayName+"?</span>";
	alertStr += "</div><div class='modal-footer'>";	
	alertStr += "<a href='javascript:void(0)' class='btn btn-primary' data-dismiss='modal' onclick='delReport("+id+")'>确认</a>";
	alertStr += "<a href='javascript:void(0)' class='btn' data-dismiss='modal'>关闭</a></div>";
	$("#myModal").html(alertStr);
	$('#myModal').modal('show');
}

function downloadReport(id){
	window.location="${path}/servlet/report/ReportAction?func=Download&id="+id;
}
//清除
function clearData(){
	$("button[type='reset']").click();
}
$(clearData);
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
<div id="content">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					报表列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="添加" onclick="addReport()" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="报表模板" onclick="templateList()" data-rel="tooltip"><i class="icon icon-color icon-clipboard"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
				<div id="conAlert" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
					<div class="arrow"></div>
					<div class="popover-inner">
						<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlert()">×</a></h3>
						<div class="popover-content" style="padding: 8px;">
					        <form class="form-horizontal" id="conditionForm">
								<fieldset>
								  <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="name" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="name" name="name" style="width: 140px;margin-left: 20px;">
					              </div>
								  <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="name" style="width:80px">类型</label>
					                  <select name="reportType" style="width: 140px;margin-left: 20px;">
					                  	<option value='0'>即时报表</option>
					                  	<option value='1'>任务报表</option>
					                  </select>
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startTime" style="width:80px">开始时间</label>
					                  <input name="startTime" type="text" style="width: 140px;cursor:pointer;margin-left: 20px;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/> 
					              </div> 
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="endTime" style="width:80px">结束时间</label>
									  <input name="endTime" type="text" style="width: 140px;cursor:pointer;margin-left: 20px;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button type="button" class="btn btn-primary" onclick="Filter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div id="myTabContent">
					<table class="table table-bordered table-striped table-condensed" id="conTable">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									报表类型
								</th>
								<th>
									时间段
								</th>
								<th>
									创建时间
								</th>
								<th>
									操作
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty reportPage.data}">
									<c:forEach var="item" items="${reportPage.data}" varStatus="status">
										<tr ondblclick="DbClick('${item.real_name}')" style="cursor:pointer">
											<td>
												<a href="javascript:DbClick('${item.real_name}')">${item.the_display_name}</a>
											</td>
											<td>
												${item.report_type==0?'即时报表':'任务报表'}
											</td>
											<td>
												${item.starttime}&nbsp;&nbsp;至&nbsp;&nbsp;${item.endtime}
											</td>									
											<td>
												${item.create_time}
											</td>									
											<td align="center" >
												<a class="btn btn-success" data-rel='tooltip' href="javascript:DbClick('${item.real_name}')" title="view"><i class="icon-zoom-in icon-white"></i>查看</a>
												<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del('${item.id}','${item.the_display_name }')"><i class="icon-trash icon-white"></i>删除</a>
												<a class="btn btn-warning" data-rel='tooltip' href="javascript:void(0)" title="download" onclick="downloadReport(${item.id})"><i class="icon-download-alt icon-white"></i>下载</a>
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
					<div id="reportListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty reportPage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#reportListpageNub").getLinkStr({pagecount:"${reportPage.totalPages}",curpage:"${reportPage.currentPage}",numPerPage:"${reportPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/report/ReportAction?func=AjaxReportPage&"+param,divId:'myTabContent'});
						</script>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>