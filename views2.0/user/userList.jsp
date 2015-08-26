
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/util.js"></script>
<script src="${path}/resource/js/project/users.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>

<style> .spetable td { text-overflow:ellipsis;overflow:hidden;white-space: nowrap; } </style>
<div id="content">
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>用户列表</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="新增" 
						onclick="VSMUsers.addUserInfo(-1);"><i class="icon icon-color icon-add"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="刷新" onclick="VSMUsers.dataFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div style="width:90%;;height:100px;margin:0px auto;display:none;" class="box-content" id="userFilter">
				<form class="form-horizontal">
					<fieldset>
						<div style="margin-bottom: 10px;" class="control-group">
							<table class="table-condensed">
								<tbody>
									<tr>
										<td width="8%;">&nbsp;</td>
										<td>
											<label style="width: 80px" for="userName" class="col-lg-2 control-label">用户姓名</label>
											<input type="text" style="width: 140px; margin-left: 20px;" id="userName" name="userName" class="form-control">
										</td>
										<td>
											<label style="width: 80px" for="loginName" class="col-lg-2 control-label">登录名</label>
											<input type="text" style="width: 140px; margin-left: 20px;" id="loginName" name="loginName" class="form-control">
										</td>
										<td>
											<label style="width: 80px" for="account" class="col-lg-2 control-label">工号</label>
											<input type="text" style="width: 140px; margin-left: 20px;" id="account" name="account" class="form-control">
										</td>
										<td>
											<label style="width: 80px" for="idCard" class="col-lg-2 control-label">身份证</label>
											<input type="text" style="width: 140px; margin-left: 20px;" id="idCard" name="idCard" class="form-control">
										</td>
										<td width="8%;">&nbsp;</td>
									</tr>
									<tr>
										<td width="8%;">&nbsp;</td>
										<td>
											<label style="width: 80px" for="gender" class="col-lg-2 control-label">性别</label>
											<select class="form-control" id="gender" style="width: 150px; margin-left: 20px;">
												<option value="-1">不限</option>
												<option value="男">男</option>
												<option value="女">女</option>
											</select>
										</td>
										<td colspan="4">
											<label style="width: 80px" for="startHireDate" class="col-lg-2 control-label">
												入职时间范围
											</label>
											<input type="text" readonly="readonly" onclick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" 
												style="width: 140px; cursor: pointer; margin-left: 20px;" id="startHireDate" name="startHireDate" value="">
											--<input type="text" readonly="readonly" onclick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})" 
												style="width: 140px; cursor: pointer; margin-left: 20px;" id="endHireDate" name="endHireDate" value="">
										</td>
									</tr>
									<tr>
										<td style="text-align:center;" colspan="6">
											<input type="button" value="查询 " class="btn btn-primary" onclick="DeviceAlert.dataFilter();">
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<button type="reset" class="btn">重置</button>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</fieldset>
				</form>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;height:600px;" id="userContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>用户姓名</th>
							<th>登录名</th>
							<th>邮件</th>
							<th>管理级别</th>
							<%--<th>工号</th>
							<th>身份证号</th>
							<th>性别</th>
							<th>所在部门</th>
							<th>入职日期</th>--%>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr>
										<td>${item.fname}</td>
										<td>${item.floginname}</td>
										<td>${item.femail}</td>
										<td>
											<c:choose>
												<c:when test="${fn:toLowerCase(item.froleid) == 'super'}">管理员</c:when>
												<c:otherwise>用户</c:otherwise>
											</c:choose>
										</td>
										<%--<td>${item.user_account}</td>
										<td>${item.id_card}</td>
										<td>${item.gender}</td>
										<td>${item.department}</td>
										<td>${item.hire_date}</td>--%>
										<td>
											<a class="btn btn-info" href="javascript:void(0)" title="查看该用户的详细信息" onclick="VSMUsers.seeUserInfo('${item.fid}')"><i class="icon-check icon-white"></i>查看</a>
											<a class="btn btn-info" href="javascript:void(0)" title="编辑该用户的详细信息" onclick="VSMUsers.editUserInfo('${item.fid}')"><i class="icon-edit icon-white"></i>编辑</a>
											<a class="btn btn-danger" href="javascript:void(0)" title="删除该用户" onclick="VSMUsers.delUserInfo('${item.fid}')"><i class="icon-trash icon-white"></i>删除</a>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr><td colspan="8">暂无数据！</td></tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
						
				<div class="pagination pagination-centered"><ul id="userListNub"></ul></div>
				<c:if test="${not empty dbPage.data}">
					<form id="userHiddenForm">
						<input type="hidden" name="curPage" value="${dbPage.currentPage}">
						<input type="hidden" name="numPerPage" value="${dbPage.numPerPage}">
					</form>
					<script>
						$("#userListNub").getLinkStr({
							pagecount:"${dbPage.totalPages}",
							curpage:"${dbPage.currentPage}",
							numPerPage:"${dbPage.numPerPage}",
							isShowJump:true,
							ajaxRequestPath:"${path}/servlet/user/UserAction?func=AjaxPage",
							divId: "userContent"
						});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>