<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
%>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
<script src="${path}/resource/js/jquery.ztree.core-3.5.min.js"></script>
<script src="${path}/resource/js/jquery.ztree.excheck-3.5.min.js"></script>
<script src="${path}/resource/js/jquery.ztree.exedit-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/resource/css/custom.css" />
<link rel="stylesheet" href="${path}/resource/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript">
	<%--
		1、查看。把密码框移除，把所有控件disabled
		2、编辑。显示密码框，往所有控件填写信息
		3、新增。显示密码框，不往所有控件填写信息
		如果是超级管理员，就告知拥有所有权限
	--%>
	$(function(){
		var level = parseInt("${level}");
		var $adminLevel = $("#adminLevel");
		var $userRole = $("#userRole");
		$adminLevel.data("adLevel", "user");
		var isSuper = false;
		if(level == 1 || level == 2) {
			isSuper = "${userInfo.froleid}".toLowerCase() == "super";
			if(isSuper) {
				$adminLevel.find("input#a5").attr({checked: true});
				$adminLevel.data("adLevel", "super");
			}
		}
		if(level == 1) {
			$adminLevel.find("input#a5").attr({disabled: true});
		}
		$adminLevel.bootstrapSwitch();
		$adminLevel.bootstrapSwitch("setOnLabel", "管理员");
		$adminLevel.bootstrapSwitch("setOffLabel", "用户");
		$adminLevel.bootstrapSwitch("setOnClass", "success");
		$adminLevel.bootstrapSwitch("setOffClass", "warning");
		
		$adminLevel.on("switch-change", function (e, data) {
			var $adLevel = $(this);
			var isSuper = $adLevel.data("adLevel") == "super";
			$adLevel.data("adLevel", isSuper? "user" : "super");
			if(isSuper) {
				$userRole.parent().parent().show();
				$("#configTree").parent().show();
			}
			else {
				$userRole.parent().parent().hide();
				$("#configTree").parent().hide();
			}
		});
		if(isSuper) {
			$userRole.parent().parent().hide();
			$("#configTree").parent().hide();
		}
		$userRole.multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择用户角色",
			nSelectedText: "台用户角色被选中",
			disableIfEmpty: true,
			maxHeight: 250,
			onChange: function(element, checked) {
				if(level != 1){
					if(!checked && element.siblings(":selected").length == 0){
						$("#treeDemo").children().remove();
						return;
					}
					var roleIds = [];
					element.parent().find("option:selected").each(function(i, opt){ roleIds.push($(opt).val()); });
					var action = "${action}";
					var myRoleIds = roleIds.join(",");
					var opts = {data:{roleIds: myRoleIds, action: action } };
					opts.settings = {
						async: {
							enable: true,
							dataType: "text",
							type: "post",
							url: "<%=basePath%>/servlet/roles/RolesAction?func=GetAllDevices",
							autoParam: ["devtype", "devid=devIds", "id=pid"],
							otherParam: ["action", action, "roleIds", myRoleIds],
							dataFilter: function(treeId, parentNode, jsonData) {
							   	if(jsonData.success){
							   		return jsonData.value;
							   	}
							   	alert(jsonData.msg);
							    return undefined;
							}
						},
						callback: { beforeCheck: function(treeId, treeNode){ return false; } }
					};
					VSMUsers.loadTree(opts);
				}
		    }
		});
		if(level == 1){
			$("#saveBtn52173041").hide();
			var $form = $("#userInfoForm");
			$form.parent().css({height: "480px"});
			var $tbody = $($form.find("tbody"));
			$tbody.find("input[name='passwd01']").parent().parent().remove();
			$tbody.find("input[name='passwd02']").parent().parent().remove();
			var state = {disabled: true};
			$tbody.find("input[name='userName']").val("${userInfo.fname}").attr(state);
			$tbody.find("input[name='loginName']").val("${userInfo.floginname}").attr(state);
			$tbody.find("input[name='account']").val("${userInfo.user_account}").attr(state);
			$tbody.find("input[name='idCard']").val("${userInfo.id_card}").attr(state);
			$tbody.find("input[name='department']").val("${userInfo.department}").attr(state);
			$tbody.find("input[name='phone']").val("${userInfo.user_phone}").attr(state);
			$tbody.find("input[name='email']").val("${userInfo.femail}").attr(state);
			$tbody.find("input[name='gender']").attr(state);
			$tbody.find("input[name='gender'][value='${userInfo.gender}']").attr({checked: true});
			$tbody.find("input[name='nation']").val("${userInfo.nation}").attr(state);
			$tbody.find("input[name='nativePlace']").val("${userInfo.native_place}").attr(state);
			$tbody.find("select[name='education']").val("${userInfo.education}").attr(state);
			$tbody.find("input[name='address']").val("${userInfo.address}").attr(state);
			$tbody.find("input[name='birthday']").val("${userInfo.birthday}").attr(state);
			$tbody.find("input[name='hireDate']").val("${userInfo.hire_date}").attr(state);
			$tbody.find("input[name='comments']").val("${userInfo.comments}").attr(state);
		}
		else if(level == 2){
			var $tbody = $("#userInfoForm tbody");
			$tbody.find("input[name='passwd01']").val("");
			$tbody.find("input[name='passwd02']").val("");
			$tbody.find("input[name='userName']").val("${userInfo.fname}");
			$tbody.find("input[name='loginName']").val("${userInfo.floginname}");
			$tbody.find("input[name='account']").val("${userInfo.user_account}");
			$tbody.find("input[name='idCard']").val("${userInfo.id_card}");
			$tbody.find("input[name='department']").val("${userInfo.department}");
			$tbody.find("input[name='phone']").val("${userInfo.user_phone}");
			$tbody.find("input[name='email']").val("${userInfo.femail}");
			$tbody.find("input[name='gender'][value='${userInfo.gender}']").attr({checked: true});
			$tbody.find("input[name='nation']").val("${userInfo.nation}");
			$tbody.find("input[name='nativePlace']").val("${userInfo.native_place}");
			$tbody.find("select[name='education']").val("${userInfo.education}");
			$tbody.find("input[name='address']").val("${userInfo.address}");
			$tbody.find("input[name='birthday']").val("${userInfo.birthday}");
			$tbody.find("input[name='hireDate']").val("${userInfo.hire_date}");
			$tbody.find("input[name='comments']").val("${userInfo.comments}");
		}
		else {
			$userRole.find("option").removeAttr("selected");
			$userRole.multiselect("refresh");
		}
		if((level == 1 || level == 2)){
			var roleIds = [];
			var $sel = $("#userRole");
			$sel.find("option:selected").each(function(i, opt){ roleIds.push($(opt).val()); });
			var opts = { data: {roleIds: roleIds.join(","), action: 1} };
			
			if(level == 1){
				$sel.find("option").attr({disabled: true});
				$sel.multiselect("refresh");
			}
			opts.settings = { callback: { beforeCheck: function(treeId, treeNode){ return false; } } };
			VSMUsers.loadTree(opts);
		}
	});
	function saveInfo(){
		var $tbody = $("#userInfoForm tbody");
		var data = {};
		var $div = $("#userInfoDlg2816");
		$div.hide();
		var temp = $tbody.find("input[name='userName']").val();
		if(temp && $.trim(temp).length > 0){
			data.userName = temp;
		}else { showAlert($div, 2, "请输入用户名称！"); return; }
		
		var temp = $tbody.find("input[name='loginName']").val();
		if(temp && $.trim(temp).length > 0){
			data.loginName = temp;
		}else { showAlert($div, 2, "请输入登录名！"); return; }
		console.log($tbody.find("input[name='passwd01']"));
		temp = $tbody.find("input[name='passwd01']").val();
		if(!temp){
			showAlert($div, 1, "必须填写密码！"); return;
		}
		if(temp && temp.length < 6){
			showAlert($div, 1, "密码的字符至少是6个！"); return;
		}
		if(temp != $tbody.find("input[name='passwd02']").val()){
			showAlert($div, 1, "密码前后不一致！"); return;
		}
		data.passwd01 = temp;
		
		temp = $tbody.find("input[name='email']").val();
		if(!/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/.test(temp)){
			showAlert($div, 1, "邮件地址格式不正确！"); return;
		}
		data.email = temp;
		
		var adl = $("#adminLevel").data("adLevel");
		if(adl == "user") {
			var urSize = 0;
			$tbody.find("#userRole option:selected").each(
					function(idx, opt){ 
						data["ur" + idx] = $(opt).val();
						++urSize;
					}
			);
			if(urSize > 0){ data.urSize = urSize; }
			else {
				showAlert($div, 2, "请选择角色！");
				return;
			}
		}
		if(parseInt("${level}") == 2){
			data.func = "UpdateUserInfo";
			data.userId = "${userId}";
		}
		else {
			data.func = "SaveUserInfo";
		}
		$.ajax({
			url: "${pageContext.request.contextPath}/servlet/user/UserAction",
			data: data,
			type: "post",
			dataType: "json",
			success: function(jsonData){
				if(jsonData.success){
					bAlert(jsonData.msg, "提示", [{func: "VSMUsers.dataFilter();", text: "确定" }]);
				}
				else{
					bAlert(jsonData.msg);
				}
			}
		}); 
	}
</script>
<div class="row-fluid sortable">
	<div class="box-content">
		<div id="userInfoDlg2816" class="alert alert-block" style="display:none;margin-top:-15px;">
		  <strong class="label label-warning"></strong><b></b>
		</div>
		<div style="overflow-y:auto;height:400px;">
			<form id="userInfoForm" class="form-horizontal">
				<fieldset>
					<div class="control-group" style="margin-bottom: 10px;">
						<table style="width:98%;" class="table-striped table-condensed">
							<tbody>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">用户姓名</td>
									<td><input type="text" class="form-control" name="userName" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">登录名</td>
									<td><input type="text"  class="form-control" name="loginName" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">密码</td>
									<td><input type="password" class="form-control" name="passwd01"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">确认密码</td>
									<td><input type="password" class="form-control" name="passwd02"></td>
								</tr>
								<%--<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">工号</td>
									<td><input class="form-control" name="account" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;" style="width:280px;">身份证</td>
									<td><input class="form-control" name="idCard"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">所在部门</td>
									<td><input class="form-control" name="department" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">手机号码</td>
									<td><input class="form-control" name="phone" style="width:280px;"></td>
								</tr>--%>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">邮件地址</td>
									<td><input type="text" class="form-control" name="email" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">管理级别</td>
									<td >
										<div id="adminLevel" class="switch" tabindex="0">
							                <input id="a5" data-no-uniform="true" type="checkbox">
								         </div>
									</td>
								</tr>
								<%--<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">性别</td>
									<td>
										<input type="radio" class="form-control" checked="checked" name="gender" value="男">男 
										<input type="radio" class="form-control" name="gender" value="女">女
									</td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">民族</td>
									<td><input class="form-control" name="nation" value="汉族" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">籍贯</td>
									<td><input class="form-control" name="nativePlace" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">学历</td>
									<td>
										<select class="form-control" name="education">
											<optgroup label="大学">
												<option value="博士">博士</option>
												<option value="硕士">硕士</option>
												<option value="本科">本科</option>
												<option value="大专" selected="selected">大专</option>
												<option value="中专">中专</option>
											</optgroup>
											<optgroup label="中学">
												<option value="高中">高中</option>
												<option value="初中">初中</option>
											</optgroup>
											<optgroup label="小学">
												<option value="小学">小学</option>
											</optgroup>
										</select>
									</td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">居住地址</td>
									<td><input class="form-control" name="address" style="width:280px;"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">出生日期</td>
									<td><input class="form-control" name="birthday" style="width: 140px; cursor: pointer; margin-left: 20px;"
													onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})"
													readonly="readonly"></td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">入职日期</td>
									<td>
										<input class="form-control" name="hireDate"
											style="width: 140px; cursor: pointer; margin-left: 20px;"
													onClick="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:true})"
													readonly="readonly">
									</td>
								</tr>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">用户备注</td>
									<td><input class="form-control" name="comments" style="width:280px;"></td>
								</tr>--%>
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">用户角色</td>
									<td>
										<select id="userRole" class="form-control" name="userRole" multiple="multiple" style="width:140px;">
											<c:choose>
												<c:when test="${not empty rolesData}">
													<c:forEach items="${rolesData}" var="item">
														<c:choose>
															<c:when test="${not empty item.checked and item.checked==1}">
																<option value="${item.fid}" selected="selected">${item.fname}</option>
															</c:when>
															<c:otherwise>
																<option value="${item.fid}">${item.fname}</option>
															</c:otherwise>
														</c:choose>
													</c:forEach>
												</c:when>
											</c:choose>
										</select>
										<%--
											菜单是固定的不能被任何用户管理，否则极有可能破坏软件的健壮性
											删除角色之后顺便把角色与菜单的映射关系也删除
											删除某位管理员之后顺便把管理员与角色的映射关系也删除
											用户角色是可以多选的，因为一个用户可以担任多种角色
										
										<i onclick="alert('刷新角色选择框');" title="刷新角色选择框" class="icon icon-color icon-refresh"></i>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<i onclick="alert('新增角色');" title="新增角色" class="icon icon-color icon-add"></i>
										&nbsp;&nbsp;
										<i onclick="alert('修改角色');" title="修改角色" class="icon icon-color icon-edit"></i>
										&nbsp;&nbsp;
										<i onclick="alert('删除角色');" title="删除角色" class="icon icon-color icon-cross"></i>--%>
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<label for="configTree" style="width:100%;text-align:center;">权限</label>
										<div id="configTree">
											<ul id="treeDemo" class="ztree yuan" style="width:100%;margin:0px;padding:10px 0px;background-color:#D9EDF7;height:300px;overflow-y:scroll;overflow-x:auto;"></ul>
			                        	</div>
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
						<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
						<button class="btn" type="reset" onclick="resetFunc()">重置</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

<script>
	function resetFunc(){
		var $tbody = $("#userInfoForm tbody");
		$tbody.find("input[type='text']").val("");
		$tbody.find("input[name='gender']").first().attr({checked: true});
		var $sel = $($tbody.find("select#userRole"));
		$sel.find("option").removeAttr("selected");
		$sel.multiselect("refresh");
		$tbody.find("ul#treeDemo").children().remove();
	}
	function showAlert($div, level, msg){
		var str;
		switch(level){
			case 1:
			str = "<strong class='label label-warning'>警告</strong><b>"+msg+"</b>";
			break;
			case 2:
			str = "<strong class='label label-important'>关键</strong><b>"+msg+"</b>";
			break;
			default:
			str = "<strong class='label'>信息</strong><b>"+msg+"</b>";
			break;
		}
		$div.html(str);
		$div.show();
		return $div;
	}
</script>