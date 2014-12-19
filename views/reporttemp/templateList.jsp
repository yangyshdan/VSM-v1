<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core"  prefix="c"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
function DbClick(id){
	window.location="${path}/servlet/report/ReportTemplate?func=EditTemplate&id="+id;
}
	
//数据查询
function Filter(){
	var jsonVal = $("#conditionForm").getValue();
	loadData("${path}/servlet/report/ReportTemplate?func=AjaxPage",jsonVal,$("#myTabContent"));
}
function doFreshen(){
	var jsonArray = $("#hiddenForm").serializeArray();
	var jsonVal={};
	$.each(jsonArray,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/report/ReportTemplate?func=AjaxPage",jsonVal,$("#myTabContent"));	
}
function addReport(){
	window.location="${path}/servlet/report/ReportTemplate?func=EditTemplate";
}
function editTaskReport(id){
	window.location="${path}/servlet/report/ReportTemplate?func=EditTaskTemplate&id="+id;
}
function reportList(){
	window.location=getRootPath()+"/servlet/report/ReportAction?func=ReportPage";
}
function createReport(id){
	$.ajax({
		url:getRootPath()+'/servlet/report/ReportTemplate?func=CreateReport&r='+Math.random(),
		data:{id:id},
		dataType:'json',
		success:function(json){
			if(json.res=="true"){
				var button = [{func:'reportList()',text:"确定"}];
				bAlert("生成报表成功","操作提示",button);
			}else{
				bAlert("系统繁忙,请稍候操作!");
			}
		},
		beforeSend:function(){
			var alertStr = "<div class='modal-header'><h3>操作提示</h3></div>";
			alertStr += "<div class='modal-body' align='center' style='height:130px;line-height:130px;'>";
			alertStr += "<img src='"+getRootPath()+"/resource/img/loading.gif' /><span>正在生成报表,请稍后...</span>";
			alertStr += "</div><div class='modal-footer'></div>";	
			$("#myModal").html(alertStr);
			$('#myModal').modal('show');
		}
	});	
}
function delReport(id){
	$.ajax({
		url:"${path}/servlet/report/ReportTemplate?func=DelReport",
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
					报表模板
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="添加模板" onclick="addReport()" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
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
									创建时间
								</th>
								<th>
									操作
								</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty dbPage.data}">
									<c:forEach var="item" items="${dbPage.data}" varStatus="status">
										<tr style="cursor:pointer">
											<td>
												<a href="javascript:DbClick('${item.id}')">${item.the_display_name}</a>
											</td>
											<td>
												${item.create_time}
											</td>									
											<td align="center" >
												<c:choose>
													<c:when test="${item.report_type == 0}">
														<a class="btn btn-success" data-rel='tooltip' href="javascript:DbClick('${item.id}')" title="view"><i class="icon-zoom-in icon-white"></i>编辑</a>
														<a class="btn btn-warning" data-rel='tooltip' href="javascript:void(0)" title="create" onclick="createReport(${item.id})"><i class="icon-print icon-white"></i>生成报表</a>
														<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del('${item.id}','${item.the_display_name }')"><i class="icon-trash icon-white"></i>删除</a>
													</c:when>
													<c:otherwise>
														<a class="btn btn-success" data-rel='tooltip' href="javascript:editTaskReport('${item.id}')" title="view"><i class="icon-zoom-in icon-white"></i>编辑</a>
														<a class="btn btn-warning" data-rel='tooltip' href="javascript:void(0)" title="create" onclick="createReport(${item.id})"><i class="icon-print icon-white"></i>生成报表</a>
													</c:otherwise>
												</c:choose>
											</td>									
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=3>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="reportListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty dbPage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#reportListpageNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/report/ReportTemplate?func=AjaxPage&"+param,divId:'myTabContent'});
						</script>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>