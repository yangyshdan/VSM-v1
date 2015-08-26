<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<div class="row-fluid sortable">
	<div class="box-content">
		<div id="alertDlg2816" class="label" style="display:none;font-size:14px;margin-top:-10px;">
		  <strong></strong><b></b>
		</div>
		<br>
		<div style="overflow-y:auto;height:400px;">
			<table id="renameTable2816" style="width:98%;" class="table-striped table-condensed">
				<thead>
					<tr><th></th><th style="width:50%;">后端(TPC)名称</th><th>前端显示名称</th></tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty storageList}">
							<c:forEach var="item" items="${storageList}" varStatus="status">
								<tr>
									<td><span class="badge badge-success">${status.index + 1}</span></td>
									<td style="white-space:normal;word-break:break-all; word-wrap:break-word;">${item.the_display_name}</td>
									<td><input type="text" class="changeName" id="${item.subsystem_id}" dbtype="${item.dbtype}"
										style="width:99%;margin-bottom:0px;" value="${item.the_backend_name}"></td>
								</tr>
							</c:forEach>
						</c:when>
					</c:choose>
				</tbody>
			</table>
		</div>
		<div>
			<table style="width:100%;">
				<tr>
					<td colspan="3" align="center">
						<br>
						<br>
						<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<button class="btn" type="reset" onclick="renameResetFunc()">重置</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>
<script>
	function renameResetFunc(){
		$("#renameTable2816 input").val("");
	}
	function showAlert(level, msg){
		var $div = $("#alertDlg2816");
		$div.removeAttr("class");
		$div.addClass("label");
		var str;
		switch(level){
			case 1:
			$div.addClass("label-warning");
			str = "<strong>警告:&nbsp;&nbsp;</strong><b>"+msg+"</b>";
			break;
			case 2:
			$div.addClass("label-important");
			str = "<strong>关键:&nbsp;&nbsp;</strong><b>"+msg+"</b>";
			break;
			default:
			str = "<strong>信息:&nbsp;&nbsp;</strong><b>"+msg+"</b>";
			break;
		}
		$div.html(str);
		$div.show();
		return $div;
	}
	function saveInfo(){
		var data = {};
		var c1 = 0, c2 = 0;
		$("#alertDlg2816").hide();
		var $input = $("#renameTable2816 input");
		$("#renameTable2816 div.alert-block").parent().hide();
		for(var idx = 0, len = $input.length, $t, name, dbt; idx < len; ++idx){
			$t = $($input[idx]);
			name = $.trim($t.val());
			if(name.length == 0){
				showAlert(2, "存储名称不能为空, 请检查第" + (idx + 1) + "个框!");
				return;
			}
			dbt = $t.attr("dbtype");
			data["dbt" + idx] = dbt;
			data["id" + idx] = $t.attr("id");
			data["name" + idx] = name;
			if(dbt == "SR"){ ++c1; }
			else { ++c2; }
		};
		data.srSize = c1;
		data.tpcSize = c2;
		
		var $dlg = $("#myModal");
		$dlg.modal("hide");
		$dlg.removeAttr("style");
		$.ajax({
			url: "${pageContext.request.contextPath}/servlet/storage/StorageAction?func=StorageRename",
			data: data,
			success:function(result){
				if(result){
					parent.window.bAlert("操作成功！","",[{func:"parent.window.doFreshen1();",text:"确定"}]);
				}else{
					parent.window.bAlert("操作失败，请稍候再试！");
				}
			}
		}); 
	}
</script>