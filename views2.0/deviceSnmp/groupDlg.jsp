<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>

<div id="addSnmpGroupPage" class="row-fluid sortable" style="display:none;">
	<div class="box-content">
		<div id="groupDlgTip2816" class="alert alert-block" style="display:none;margin-top:-15px;">
		  <strong class="label label-warning"></strong><b></b>
		</div>
		<div style="overflow-y:auto;height:400px;">
			<form id="snmpGroupForm" class="form-horizontal">
				<fieldset>
					<div class="control-group" style="margin-bottom: 10px;">
						<div id="snmpGroupFormTabCap" style="text-align:center;"></div>
						<table id="snmpGroupFormTab" style="width:98%;" class="table-striped table-condensed">
							<tbody>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">组名称</td>
									<td><input placeholder="例如, 交换机组" class="form-control" name="groupName" style="width:200px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">描述</td>
									<td><input class="form-control" name="groupDesc" style="width:200px;"></td>
								</tr>
								<%--<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">每隔多少分钟收集一次</td>
									<td><input value="5" class="form-control" name="pollingMinute"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">每隔多少小时收集一次</td>
									<td><input value="1" class="form-control" name="pollingHour"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">每隔多少天收集一次</td>
									<td><input value="1" class="form-control" name="pollingDay"></td>
								</tr>--%>
							</tbody>
						</table>
					</div>
				</fieldset>
			</form>
		</div>
		<div>
			<table style="width:100%;">
				<tr>
					<td colspan="3" align="center">
						<button type="button" onclick="SnmpUtil.saveGroupInfo();" class="btn btn-primary">保存</button>
						&nbsp;&nbsp;&nbsp;
						<button class="btn" type="reset" onclick="SnmpUtil.resetGroupFunc()">重置</button>
						&nbsp;&nbsp;&nbsp;
						<button type="button" onclick="SnmpUtil.cancelGroupInfo();" class="btn btn-info">返回</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>