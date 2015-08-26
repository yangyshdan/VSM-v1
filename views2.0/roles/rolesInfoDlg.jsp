<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
%>
<script type="text/javascript">
	$(function(){
		var action = parseInt("${action}");
		console.log("action: " + action);
		if(isNaN(action)){ action = 0; }
		var opt = {
			data: { action: action, roleIds: "${roleId}"}, 
			id: "treeDemo"
		};
		if(action == 0 || action == 2){
			opt.settings = {
				async: {
					enable: true,
					dataType: "text",
					type: "post",
					url: "<%=basePath%>/servlet/roles/RolesAction?func=GetAllDevices",
					autoParam: ["devtype", "devid=devIds", "id=pid"],
					otherParam: ["action", action, "roleIds", "${roleId}"],
					dataFilter: function(treeId, parentNode, jsonData) {
					   	if(jsonData.success){
					   		return jsonData.value;
					   	}
					   	alert(jsonData.msg);
					    return undefined;
					}
				},
				callback: {
					beforeClick:function(treeId, treeNode){
	                    if(treeNode.isParent){
	                    	var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
	                    	if(treeObj){ treeObj.expandNode(treeNode); }
	                        return true;
	                    }
	                    return false;
	                }
				}
			};
		}
		else if(action == 1){
			opt.settings = {
				callback: {
					beforeCheck: function(treeId, treeNode){
						return false;
					}
				}
			};
			$("#saveBtn52173041").hide();
			var $form = $("#roleInfoForm");
			$form.find("input[name='roleName']").attr({disabled: true});
			$form.parent().css({height: ""});
		}
		VSMRoles.loadTree(opt);
	});
	function saveInfo(){
		var $div = $("#userInfoDlg2816");
		$div.hide();
		var roleName = $.trim($("#roleInfoForm tbody input[name='roleName']").val());
		if(roleName == null || roleName == undefined || roleName.length == 0){
			showAlert($div, 2, "请输入角色名称");
			return;
		}
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		if(treeObj == undefined || treeObj == null){
			showAlert($div, 2, "HTML出错，找不到树的对象！");
			return;
		}
		var nodes = treeObj.getCheckedNodes(true);
		if(nodes == undefined || nodes == null || nodes.length == 0){
			showAlert($div, 2, "请选择权限");
			return;
		}
		var data = {};
		data.roleName = roleName;
		data.authSize = nodes.length;
		for(var i = 0, len = nodes.length, node; i < len; ++i){
			<%--将上传devid, devtype, has, pid, id, n  设备的编号、设备类型和该设备是否有孩子--%>
			node = nodes[i];
			data["did" + i] = node.devid;
			data["dtp" + i] = node.devtype;
			data["has" + i] = node.children? (node.children.length > 0? 1 : 0) : 0;
			if(node.sto_type){ data["ot" + i] = node.sto_type; }
			data["pid" + i] = node.pid;
			data["id" + i] = node.id;
			data["n" + i] = node.name;
		}
		var action = parseInt("${action}");
		if(isNaN(action)){ action = 0; }
		var isAdd = action == 0;
		if(isAdd){ data.func = "SaveRole"; }
		else {
			data.func = "UpdateRole";
			data.roleId = "${roleId}";
		}
		console.log(data);
		//if(true){ return; }
		$.ajax({
			url: "${pageContext.request.contextPath}/servlet/roles/RolesAction",
			data: data,
			type: "post",
			dataType: "json",
			success: function(jsonData){
				var $show = $("#showTipsWhenLoadTopo");
				$show.hide();
				var $p = $show.parent();
				$p.find("#userInfoDlg2816").show();
				$p.find("#roleInfoForm").parent().show();
				$p.find("#saveBtn52173041").show();
				if(jsonData.success){
					bAlert(jsonData.msg, "提示", [{func:"VSMRoles.dataFilter();",text:"确定"}]);
				}else{
					bAlert(jsonData.msg);
				}
			},
			beforeSend: function(){
				var $show = $("#showTipsWhenLoadTopo");
				var $p = $show.parent();
				$p.find("#userInfoDlg2816").hide();
				$p.find("#roleInfoForm").parent().hide();
				$p.find("#saveBtn52173041").hide();
				$show.show();
			}
		}); 
	}
</script>
<div class="row-fluid sortable">
	<div class="box-content">
		<div id="showTipsWhenLoadTopo" style="display:none;">
			<div class='modal-header'><h3>操作提示</h3></div>
			<div class='modal-body' align='center' style='height:80px;line-height:80px;'>
				<img src='${pageContext.request.contextPath}/resource/img/loading.gif'/><span>正在保存数据, 请稍候...</span>
			</div>
			<div class='modal-footer'></div>
		</div>
		<div id="userInfoDlg2816" class="alert alert-block" style="display:none;margin-top: -15px;">
		  <strong class="label label-warning"></strong><b></b>
		</div>
		<div style="overflow-y:auto;height:400px;">
			<form id="roleInfoForm" class="form-horizontal">
				<fieldset>
					<div class="control-group" style="margin-bottom: 10px;">
						<table style="width:98%;" class="table-striped table-condensed">
							<tbody>
								
								<tr>
									<td><span class="badge badge-success"></span></td>
									<td style="text-align:right;">用户角色</td>
									<td><input class="form-control" name="roleName" value="${roleName}" style="width:280px;"></td>
								</tr>
								<tr>
									<td colspan="3">
										<label for="configTree" style="width:100%;text-align:center;">权限</label>
										<div id="configTree">
											<ul id="treeDemo" class="ztree yuan" 
											style="width:440px;margin:0px;padding:10px 0px;background-color:#D9EDF7;height:280px;overflow-y:auto;overflow-x:auto;"></ul>
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
		var $tbody = $("#roleInfoForm tbody");
		$tbody.find("input[name='roleName']").val("");
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		var nodes = treeObj.getCheckedNodes(true);
		if(nodes && nodes.length > 0){
			for(var i = 0, len = nodes.length; i < len; ++i){
				nodes[i].checked = false;
				treeObj.updateNode(nodes[i]);
			}
		}
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