<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/alertRule.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
	function doListRefresh(){
		loadData("${path}/servlet/alert/AlertRuleAction?func=AjaxPage",{},$("#ruleContent"));
		return false;
	}
</script>
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
					阀值告警规则
				</h2>
				<div class="box-icon">
					<%-- 
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="AlertRule.switchFilter('ruleFilter')" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					--%>
					<a href="javascript:void(0);" class="btn btn-round" title="新增" onclick="AlertRule.doDbClick(-1);"><i class="icon icon-color icon-edit"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="转发设置" onclick="AlertRule.doForward();"><i class="icon icon-color icon-compose"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick="AlertRule.doAlertDel();"><i class="icon icon-color icon-trash"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="AlertRule.dataFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<!-- 筛选条件开始 -->
			<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
				<form class="form-horizontal" id="conditionForm">
					<fieldset>
						<div class="control-group" style="margin-bottom: 10px;">
							<table class="table-condensed" width="40%" style="margin: 0px auto;">
								<tbody>
									<tr>
										<td>
											<label class="col-lg-2 control-label" for="f_name" style="width:60px">名称</label>
											<input type="text" class="form-control" id="f_name" style="width: 140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="f_enabled" style="width:100px">是否启用</label>
											<select class="form-control" id="f_enabled" style="width: 150px;margin-left: 10px;">
												<option value="">all</option>
												<option value="1">启用</option>
												<option value="0">未启用</option>
											</select>
										</td>
									</tr>
									<tr>
										<td colspan="2" style="text-align:center;">
											<input type="button" onclick="AlertRule.dataFilter();" class="btn btn-primary" value="查询 "/>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<button class="btn" type="reset">重置</button>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</fieldset>
				</form>
			</div>
			<!-- 筛选条件结束 -->
			
			<%-- 
			<div id="ruleFilter" class="" style="position:absolute;right:20px;margin-top:10px;display:none;">
				<div class="arrow"></div>
				<div class="popover-inner">
					<h3 class="popover-title">过滤器</h3>
					<div class="popover-content" style="padding: 8px;">
				        <form class="form-horizontal">
							<fieldset>
				              <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_name" style="width:80px">名称</label>
				                  <input type="text" class="form-control" id="f_name" style="width: 140px;margin-left: 20px;">
				              </div> 
				              <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_enabled" style="width:80px">是否启用</label>
				                  <select class="form-control" id="f_enabled"  style="width: 150px;margin-left: 20px;">
			                        <option value="">all</option>
			                        <option value="1">启用</option>
			                        <option value="0">未启用</option>
			                      </select>
				              </div>
				              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
								<button class="btn" type="reset">重置</button>
								<input type="button" onclick="AlertRule.dataFilter();" class="btn btn-primary" value="查询 "/>
							  </div>
				           	</fieldset>
				          </form>
					</div>
				</div>
			</div>
			--%>
			<div class="box-content" style="overflow:auto;width:98%;min-height:180px;" id="dAlertContent">
				<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th style="width: 20px;">
								<label class="checkbox inline">
									<input type="checkbox" onclick="AlertRule.doAlertCheck(this.checked);">
								</label>
							</th>
							<th>
								名称
							</th>
							<!-- 
							<th>
								级别
							</th>
							 -->
							<th>
								是否可用
							</th>
							<th>
								类型
							</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr style="cursor:pointer;" ondblclick="AlertRule.doDbClick('${item.fid}')">
										<td>
											<label class="checkbox inline">
												<input type="checkbox" value="${item.fid}" name="ruleCheck">
										    </label>
										</td>
										<td>
											<a href="javascript:void(0);" onclick="AlertRule.doDbClick('${item.fid}')">${item.fname}</a>
										</td>
										<!-- 
										<td>
											<c:choose>
												<c:when test="${item.flevel == 0}"><span class="label">Info</span> </c:when>
												<c:when test="${item.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
												<c:when test="${item.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
											</c:choose>
										</td>
										 -->
										<td>
											<c:choose>
												<c:when test="${item.fenabled == 0}">
													<i class="icon icon-color icon-close"></i>
												</c:when>
												<c:otherwise>
													<i class="icon icon-color icon-check"></i>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.ftype == 'DS'}">存储系统(IBM-DS8k)</c:when>
												<c:when test="${item.ftype == 'BSP'}">存储系统(IBM-DS4k/5k)</c:when>
												<c:when test="${item.ftype == 'SVC'}">存储系统(IBM-SVC)</c:when>
												<c:when test="${item.ftype == 'NAS'}">存储系统(NAS)</c:when>
												<c:when test="${item.ftype == 'EMC'}">存储系统(EMC-CX/VNX)</c:when>
												<c:when test="${item.ftype == 'HDS'}">存储系统(HDS-AMS)</c:when>
												<c:when test="${item.ftype == 'NETAPP'}">存储系统(NETAPP)</c:when>
												<c:when test="${item.ftype == 'PHYSICAL'}">物理机 </c:when>
												<c:when test="${item.ftype == 'VIRTUAL'}">虚拟机 </c:when>
												<c:when test="${item.ftype == 'SWITCH'}">交换机 </c:when>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan="4">
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
				<c:if test="${not empty dbPage.data}">
					<script>
						$("#ruleListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/AlertRuleAction?func=AjaxPage&name=${name}&enabled=${enabled}",divId:'ruleContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>