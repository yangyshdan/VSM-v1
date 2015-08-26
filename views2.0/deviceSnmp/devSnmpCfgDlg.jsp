
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@ taglib uri="/tags/jstl-function" prefix="fn" %>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>

<script type="text/javascript" src="${path}/resource/js/project/util.js"></script>
<script type="text/javascript">
	showWhileSnmpV3Func = function(id) {
		var $dlg = $("#showWhileSnmpV3");
		var $tr = $("#snmpCommunityTr");
		if(id == 3) { $tr.hide(); $dlg.show(); }
		else { $tr.show(); $dlg.hide(); }
	};
	authFunc = function(_this) {
		var val = $(_this).find("option:selected").val();
		var $sel = $("select[name='snmpV3EncryptProtocal']");
		if(val == "None"){
			$("#snmpV3EncryptProtocalTable").hide();
			$("#snmpV3AuthPasswdTr").hide();
		}
		else {
			$("#snmpV3AuthPasswdTr").show();
			$("#snmpV3EncryptProtocalTable").show();
		}
	};
	$(function(){
		$("#snmpV3EncryptProtocalTable").hide();
		$("#snmpV3AuthPasswdTr").hide();
	});
	<c:if test="${not empty editSnmpInfo}">
		var editSnmpInfo = ${editSnmpInfo};
	</c:if>
</script>
<div id="showTipsWhenNotTestSnmp">
<jsp:include page="/WEB-INF/views/deviceSnmp/groupDlg.jsp"></jsp:include>
<div id="addSnmpCfgPage" class="row-fluid sortable">
	<div class="box-content">
		<div id="devSnmpCfgTip" class="alert alert-block" style="display:none;margin-top:-15px;">
		  <strong class="label label-warning"></strong><b></b>
		</div>
		<div style="overflow-y:auto;height:400px;">
			<form id="devSnmpCfgForm" class="form-horizontal">
				<fieldset>
					<div class="control-group" style="margin-bottom: 10px;">
						<table style="width:98%;" class="table-striped table-condensed">
							<tbody>
								<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;" width="80px;">组名称</td>
									<td>
										<select class="form-control" name="groupId" style="width:180px;">
										</select>
										<i onclick="SnmpUtil.addGroup();" title="添加组" class="icon icon-color icon-add"></i>
										<i onclick="SnmpUtil.editGroup();" title="编辑组" class="icon icon-color icon-edit"></i>
										<i onclick="SnmpUtil.deleteGroup();" title="删除组" class="icon icon-color icon-cross"></i>
									</td>
								</tr>
								<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">IP地址</td>
									<td><input class="form-control" name="ipAddress" style="width:180px;"></td>
								</tr>
								<%--<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">SNMP端口</td>
									<td><input placeholder="161" value="161" class="form-control" name="snmpPort" style="width:180px;"></td>
								</tr>--%>
								<%--<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">SNMP超时时间(分钟)</td>
									<td>
										<select class="form-control" name="snmpTimeout" style="width:180px;">
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
										</select>
									</td>
								</tr>--%>
								<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">重试次数</td>
									<td>
										<select class="form-control" name="snmpRetry" style="width:180px;">
											<option value="1">1</option>
											<option value="2" selected="selected">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
										</select>
									</td>
								</tr>
								<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">选择设备</td>
									<td>
										<select class="form-control" name="deviceId" style="width:180px;">
											<c:choose>
												<c:when test="${not empty devices and fn:length(devices) > 0}">
													<c:forEach items="${devices}" var="item">
														<option value="${item.device_id}">设备类型:${item.device_type},型号:${item.device_model}</option>
													</c:forEach>
												</c:when>
												<c:otherwise>
													<option value="">没有该设备可选</option>
												</c:otherwise>
											</c:choose>
										</select>
									</td>
								</tr>
								<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">是否启用</td>
									<td>
										<input type="radio" class="form-control" value="1" name="snmpEnabled" checked="checked">启动&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="radio" class="form-control" value="0" name="snmpEnabled">禁用
									</td>
								</tr>
								<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">SNMP描述</td>
									<td><input class="form-control" name="snmpDesc" style="width:180px;"></td>
								</tr>
								<tr>
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">SNMP版本</td>
									<td width="180px">
										<input onclick="showWhileSnmpV3Func(1)" type="radio" value="1" class="form-control" name="snmpVersion">V1&nbsp;&nbsp;&nbsp;&nbsp;
										<input onclick="showWhileSnmpV3Func(2)" type="radio" value="2c" class="form-control" name="snmpVersion" checked="checked">V2&nbsp;&nbsp;&nbsp;&nbsp;
										<input onclick="showWhileSnmpV3Func(3)" type="radio" value="3" class="form-control" name="snmpVersion">V3
									</td>
								</tr>
								<tr id="snmpCommunityTr">
									<td width="25px;" style="text-align:center"><span class="badge badge-success"></span></td>
									<td style="text-align:right;">读社区字符串</td>
									<td><input class="form-control" name="snmpCommunity" style="width:180px;"></td>
								</tr>
								<tr id="showWhileSnmpV3" style="display:none;">
									<td colspan="3">
										<table>
											<tbody>
												<tr>
													<td style="text-align:right;" width="105px">用户名</td>
													<td><input placeholder="例如, admin" class="form-control" name="snmpV3UserName" style="width:180px;"></td>
												</tr>
											</tbody>
										</table>
										<table>
											<caption>身份验证</caption>
											<tbody>
												<tr>
													<td style="text-align:right;" width="105px">协议</td>
													<td>
														<select onchange="authFunc(this);" class="form-control" name="snmpV3AuthProtocal" style="width:180px;">
															<option value="None">None</option>
															<option value="MD5">MD5</option>
															<option value="SHA">SHA</option>
														</select>
													</td>
												</tr>
												<tr id="snmpV3AuthPasswdTr">
													<td style="text-align:right;">密码</td>
													<td><input type="password" class="form-control" name="snmpV3AuthPasswd" style="width:180px;"></td>
												</tr>
											</tbody>
										</table>
										<table id="snmpV3EncryptProtocalTable">
											<caption>加密</caption>
											<tbody>
												<tr>
													<td style="text-align:right;" width="105px">协议</td>
													<td>
														<select class="form-control" onchange="SnmpUtil.encryptFunc(this);" name="snmpV3EncryptProtocal" style="width:180px;">
															<option value="None">None</option>
															<option value="DES">DES</option>
															<option value="AES">AES</option>
														</select>
													</td>
												</tr>
												<tr>
													<td style="text-align:right;">密码</td>
													<td><input type="password" class="form-control" disabled="disabled" name="snmpV3EncryptPasswd" style="width:180px;"></td>
												</tr>
											</tbody>
										</table>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</fieldset>
			</form>
		</div>
		<div id="saveBtn52173041">
			<table style="width:100%;">
				<tr>
					<td colspan="3" align="center">
						<button type="button" onclick="SnmpUtil.testSnmpFunc('SaveSnmpInfo');" class="btn btn-primary">保存</button>
						&nbsp;&nbsp;&nbsp;
						<button class="btn" type="reset" onclick="SnmpUtil.resetDeviceFunc();">重置</button>
						&nbsp;&nbsp;&nbsp;
						<button class="btn btn-info" type="button" onclick="SnmpUtil.testSnmpFunc('TestSnmp');">测试SNMP</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>
</div>
<div id="showTipsWhenTestSnmp" style="display:none;">
	<div class='modal-header'><h3>操作提示</h3></div>
	<div class='modal-body' align='center' style='height:80px;line-height:80px;'>
		<img src='${path}/resource/img/loading.gif'/><span>正在测试连接SNMP终端设备的数据, 请稍候...</span>
	</div>
	<div class='modal-footer'></div>
</div>

