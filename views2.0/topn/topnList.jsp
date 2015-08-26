<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/topn.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
function topnDbClick(id){
	window.location.href = "${path}/servlet/topn/TopnAction?func=TopnInfo&tid="+id+"&r="+Math.random();
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
	loadData("${path}/servlet/topn/TopnAction?func=AjaxTopnPage",jsonVal,$("#myTabContent"));
}
function doFreshen(){
	var jsonArray = $("#topnHiddenForm").serializeArray();
	var jsonVal={};
	$.each(jsonArray,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/topn/TopnAction?func=AjaxTopnPage",jsonVal,$("#myTabContent"));	
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
					TOPN
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="添加" onclick="Topn.settingPrf('')" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
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
									TOPN数量
								</th>
								<th>
									时间范围类型
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
										<tr ondblclick="topnDbClick(${item.tid})" style="cursor:pointer">
											<td>
												${item.name}
											</td>
											<td>
												${item.top_count}
											</td>
											<td>
												${item.timescope_type==0?"固定时间段":"最近时间段"}
											</td>									
											<td>
												${item.create_time}
											</td>									
											<td align="center" >
												<a class="btn btn-success" href="javascript:topnDbClick(${item.tid})" title="view"><i class="icon-zoom-in icon-white"></i>查看</a>
												<a class="btn btn-info" href="javascript:void(0)" title="edit" onclick="Topn.settingPrf(${item.tid})"><i class="icon-edit icon-white"></i>编辑</a>
												<a class="btn btn-danger" href="javascript:void(0)" title="delete" onclick="Topn.deletePrf('${item.tid}','${item.name}')"><i class="icon-trash icon-white"></i>删除</a>
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
					<div id="topnListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty dbPage.data}">
						<script>
							var param = $("#topnHiddenForm").serialize();
							$("#topnListpageNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/topn/TopnAction?func=AjaxTopnPage&"+param,divId:'myTabContent'});
						</script>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>