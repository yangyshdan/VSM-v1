<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/alertRule.js"></script>
<script src="${path }/resource/js/project/alertLog.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>
<div id="content">
<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					硬件告警
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="AlertLog.switchFilter('logFilter')" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="转发设置" onclick="AlertRule.doForward();"><i class="icon icon-color icon-compose"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="AlertLog.dataFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div id="logFilter" class="" style="position:absolute;right:20px;margin-top:10px;display:none;">
				<div class="arrow"></div>
				<div class="popover-inner">
					<h3 class="popover-title">过滤器</h3>
					<div class="popover-content" style="padding: 8px;">
				        <form class="form-horizontal">
							<fieldset>
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_level" style="width:80px">级别</label>
				                  <select class="form-control" id="f_level" style="width: 150px;margin-left: 20px;">
			                        <option value="">all</option>
			                        <option value="I">Info</option>
			                        <option value="W">Warning</option>
			                        <option value="E">Critical</option>
			                      </select>
				              </div>
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_state" style="width:80px">状态</label>
				                  <select class="form-control" id="f_state" style="width: 150px;margin-left: 20px;">
			                        <option value="">all</option>
			                        <option value="0">未确认</option>
			                        <option value="1">已确认</option>
			                        <option value="2">已清除</option>
			                        <option value="3">逻辑删除</option>
			                      </select>
				              </div>
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_startDate" style="width:80px">开始时间</label>
				                  <input value="" name="startDate" id="f_startDate" type="text" style="width: 140px;cursor:pointer;margin-left: 20px;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
				              </div>
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_endDate" style="width:80px">结束时间</label>
								  <input value="" name="endDate" id="f_endDate" type="text" style="width: 140px;cursor:pointer;margin-left: 20px;" onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" readonly="readonly"/>
				              </div>
				              
				              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
								<button class="btn" type="reset">重置</button>
								<input type="button" onclick="AlertLog.dataFilter();" class="btn btn-primary" value="查询 "/>
							  </div>
				           	</fieldset>
				          </form>
					</div>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;" id="logContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th style="width: 85px;">
								首次发生时间
							</th>
							<th style="width: 85px;">
								最后发生时间
							</th>
							<th  style="width: 55px;">
								重复次数
							</th>
							<th style="width: 30px;">
								状态
							</th>
							<th style="width: 30px;">
								级别
							</th>
							<th style="width: 50px;">
								事件源
							</th>
							<th>
								消息
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty logPage.data}">
								<c:forEach var="item" items="${logPage.data}" varStatus="status">
									<tr style="cursor:pointer;">
										<td>
											<fmt:formatDate value="${item.first_alert_time}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											<fmt:formatDate value="${item.last_alert_time}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											${item.alert_count}
										</td>
										<td>
											<c:choose>
												<c:when test="${item.state == 0}"><i class="icon icon-color icon-close"></i>未确认</c:when>
												<c:when test="${item.state == 1}"><i class="icon icon-green icon-bookmark"></i>已确认</c:when>
												<c:when test="${item.state == 2}"><i class="icon icon-orange icon-cancel"></i>已清除</c:when>
												<c:when test="${item.state == 3}"><i class="icon icon-black icon-trash"></i>逻辑删除</c:when>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.sev == 'I'}"><span class="label">Info</span> </c:when>
												<c:when test="${item.sev == 'W'}"><span class="label label-warning">Warning</span> </c:when>
												<c:when test="${item.sev == 'E'}"><span class="label label-important">Critical</span> </c:when>
											</c:choose>
										</td>
										<td>
											${item.resource_name}
										</td>
										<td>
											${item.msg}
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
				
				<div class="pagination pagination-centered">
					<ul id="ruleListNub"></ul>
				</div>
				<c:if test="${not empty logPage.data}">
					<script>
						$("#ruleListNub").getLinkStr({pagecount:"${logPage.totalPages}",curpage:"${logPage.currentPage}",numPerPage:"${logPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/AlertAction?func=AjaxPage",divId:'logContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>