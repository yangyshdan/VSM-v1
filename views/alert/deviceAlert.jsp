<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/deviceAlert.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path }/resource/js/highcharts/highcharts.js"></script>
<script type="text/javascript">
	function doListRefresh(){
		loadData("${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",{},$("#alertContent"));
		return false;
	}
</script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>
<div id="content">
	<!-- 
	<div class="well" style="height:260px;">
		<div id="shareChart" style="width: 30.3%;height:98%;float:left;"></div>
		<div id="allShareCon" style="width: 68.2%;float:left;height:98%;margin-right:0.5%;"></div>
	</div>
 -->
<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					告警列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" data-rel="tooltip" onclick="DeviceAlert.switchFilter('alertFilter')" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="确认" data-rel="tooltip" onclick="DeviceAlert.doAlertDone('','','');"><i class="icon-color icon-ok"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="DeviceAlert.doAlertDel('','','');"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" data-rel="tooltip" onclick="DeviceAlert.dataFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div id="alertFilter" class="" style="position:absolute;right:20px;margin-top:10px;display:none;">
				<div class="arrow"></div>
				<div class="popover-inner">
					<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="DeviceAlert.switchFilter('alertFilter')">×</a></h3>
					<div class="popover-content" style="padding: 8px;">
				        <form class="form-horizontal">
							<fieldset>
							  <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="logtype" style="width:80px">事件类型</label>
				                  <select class="form-control" id="logtype"  style="width: 150px;margin-left: 20px;">
			                        <option value="-1">all</option>
			                        <option value="0">系统告警</option>
			                        <option value="1">TPC告警</option>
			                        <option value="2">阀值告警</option>
			                        <option value="3">HMC告警</option>
			                      </select>
				              </div>
				              <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="level" style="width:80px">级别</label>
				                  <select class="form-control" id="level" style="width: 150px;margin-left: 20px;">
			                        <option value="-1">all</option>
			                        <option value="0">Info</option>
			                        <option value="1">Warning</option>
			                        <option value="2">Critical</option>
			                      </select>
				              </div>
							  <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="logstate" style="width:80px">状态</label>
				                  <select class="form-control" id="logstate"  style="width: 150px;margin-left: 20px;">
			                        <option value="-1">all</option>
			                        <option value="0">未确认</option>
			                        <option value="1">已确认</option>
			                      </select>
				              </div>
							  <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="resourcetype" style="width:80px">事件源类型</label>
				                  <select class="form-control" id="resourcetype"  style="width: 150px;margin-left: 20px;">
			                        <option value="">all</option>
			                        <option value="Storage">存储系统</option>
			                        <option value="Switch">交换机</option>
			                        <option value="Physical">物理机</option>
			                        <option value="Virtual">虚拟机</option>
			                        <option value="App">应用</option>
			                      </select>
				              </div>
				              <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="resourcename" style="width:80px">事件源名称</label>
				                  <input type="text" class="form-control" id="resourcename" style="width: 140px;margin-left: 20px;">
				              </div> 
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="startDate" style="width:80px">开始时间</label>
				                  <input value="" name="startDate" id="startDate" type="text" style="width: 140px;cursor:pointer;margin-left: 20px;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
				              </div>
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="endDate" style="width:80px">结束时间</label>
								  <input value="" name="endDate" id="endDate" type="text" style="width: 140px;cursor:pointer;margin-left: 20px;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
				              </div>
				              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
								<button class="btn" type="reset">重置</button>
								<input type="button" onclick="DeviceAlert.dataFilter();" class="btn btn-primary" value="查询 "/>
							  </div>
				           	</fieldset>
				          </form>
					</div>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;" id="dAlertContent">
				<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th  style="width: 20px;">
								<label class="checkbox inline">
									<input type="checkbox"   onclick="DeviceAlert.doAlertCheck(this.checked);">
							    </label>
							</th>
							<th style="width: 130px;">
										首次发生时间
									</th>
									<th style="width: 130px;">
										最后发生时间
									</th>
									<th  style="width: 55px;">
										类型
									</th>
									<th  style="width: 55px;">
										重复次数
									</th>
									<th style="width: 90px;">
										状态
									</th>
									<th style="width: 90px;">
										级别
									</th>
									<th style="width: 170px;">
										事件源
									</th>
									<th>
										消息
									</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr style="cursor:pointer;" ondblclick="DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','')">
										<td>
											<label class="checkbox inline">
												<input type="checkbox" value="${item.fruleid}_${item.ftopid}_${item.flogtype}"  name="dAlertCheck">
										    </label>
										</td>
										<td>
													<fmt:formatDate value="${item.ffirsttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
												</td>
												<td>
													<fmt:formatDate value="${item.flasttime}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
												</td>
												<td>
													<c:choose>
														<c:when test="${item.flogtype == 3}">HMC告警</c:when>
														<c:when test="${item.flogtype == 2}">阀值告警</c:when>
														<c:when test="${item.flogtype == 1}">TPC告警</c:when>
														<c:when test="${item.flogtype == 0}">系统告警</c:when>
													</c:choose>
												</td>
												<td>
													${item.fcount}
												</td>
												<td>
													<c:choose>
														<c:when test="${item.fstate == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
														<c:when test="${item.fstate == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
														<c:when test="${item.fstate == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
														<c:when test="${item.fstate == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
													</c:choose>
												</td>
												<td>
													<c:choose>
														<c:when test="${item.flevel == 0}"><span class="label">Info</span> </c:when>
														<c:when test="${item.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
														<c:when test="${item.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
													</c:choose>
												</td>
												<td>
													${item.ftopname}
												</td>
												<td>
												<a href="javascript:DeviceAlert.doDetailInfo('${item.fruleid}','${item.ftopid}','')"  data-rel="popover" data-placement="left" data-content="Device Type:${item.fresourcetype}<br/>Device Name:${item.fresourcename } <br/><c:choose><c:when test="${fn:length(item.fdetail) > 200}">
      <c:out value="${fn:substring(item.fdetail, 0, 200)}......" /></c:when> <c:otherwise><c:out value="${item.fdetail}" /></c:otherwise></c:choose>" title="详细信息">
													 ${item.fdescript}
												</a>
													
												</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=9>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				
				<div class="pagination pagination-centered">
					<ul id="alertListNub"></ul>
				</div>
				<c:if test="${not empty dbPage.data}">
					<script>
						$("#alertListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",divId:'dAlertContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<script>
	$(document).ready(function(){
		$("#resourcetype").val("${resourceType}");
	});
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>