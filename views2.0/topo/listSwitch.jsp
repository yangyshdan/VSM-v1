<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<script type="text/javascript">
	addPhySwRelation = function(){ <%--单击按钮，获取物理机--%>
		var $swOptions = $("#switchSelect option:selected");
		if($swOptions.length == 0){
			alert("请选交换机");
			return;
		}
		var $phyOption = $("#phy2SwSelect option:selected");
		if($phyOption == null || $phyOption.length == 0){
			alert("已经没有物理机可选, 请点击下一步");
			return;
		}
		var phyId = $phyOption.val();
		var phyName = $phyOption.text();
		var $tbody = $("#listPhySwPanel tbody");
		var fmt = "<tr phy_id='%s' sw_id='%s' class='p%s'><td>%s</td><td class='switchCls2816'>%s</td><td><a class='btn btn-danger' title='delete' onclick=\"delPhySwRelation('%s','%s')\"><i class='icon-trash icon-white'></i>删除</a></td></tr>";
		var $opt = $($swOptions[0]);
		$tbody.append($(fmt.jFormat(phyId, $opt.val(), phyId, phyName, $opt.text(), phyId, phyName)));
		fmt = "<tr phy_id='%s' sw_id='%s' class='p%s'><td>%s</td><td class='switchCls2816'>%s</td><td class='del2816'>&nbsp;</td></tr>";
		for(var i = 1, len = $swOptions.length, $opt; i < len; ++i){
			$opt = $($swOptions[i]);
			$tbody.append($(fmt.jFormat(phyId, $opt.val(), phyId, phyName, $opt.text())));
		};
		$phyOption.remove();
		$swOptions.removeAttr("selected");
		$("#switchSelect").multiselect("refresh");
	};
	doLoadPhysicalToSwitchForm = function(target){
		var $phy2SwSelect = $("#phy2SwSelect");
		$phy2SwSelect.children().remove();
		var hypIds = [];
		$.each($("#physicalSelect option:selected"), function(index, opt){
			var $opt = $(opt);
			hypIds.push($opt.val());
			$phy2SwSelect.append($("<option value='%s'>%s</option>".jFormat($opt.val(), $opt.text())));
		});
		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetAllSwitchByHypIds", 
			{ hypIds: hypIds.join(",") }, 
			function(jsonData){
				var $select = $("#switchSelect");
				$select.children().remove();
				if(jsonData.success){
					$select.html(getCommonOptionHtmlStr(jsonData.value, "物理机", "hyp_id", "hyp_name", "sw_id", "sw_name"));
					$select.multiselect("rebuild");
				}
				else {
					alert(jsonData.msg);
				}
			},
		"json");
		return true;
	};

	delPhySwRelation = function(phySwId, optText){
		<%--$("#listPhySwPanel tbody tr.p" + phySwId).remove();
		$("#phy2SwSelect").append($("<option value='%s'>%s</option>".jFormat(phySwId, optText)));--%>
		$("#listPhySwPanel #phySw" + phySwId).remove();
		$("#switchSelect option[value='"+ phySwId +"']").attr({selected: false, disabled: false});
		$("#switchSelect").multiselect("refresh");
	};
	
	doValidateSwitchForm = function(target, $showTips){
		var attrs = $("#listPhySwPanel tbody tr");
		var $phy2SwSelect = $("#physicalSelect option:selected");
		var m = {}, count = 0;
		for(var  i = 0, len = attrs.length, attr; i < len; ++i){
			attr = $(attrs[i]).attr("class");
			if(m[attr] == undefined){ m[attr] = 1; ++count; }
		}
		var isOK = count == $phy2SwSelect.length;
		if(isOK){
			doLoadSw2StoForm();
		}
		else {
			$showTips.text("必须选择%s项物理机与交换机的连接!!!".jFormat($phy2SwSelect.length)).show();
		}
		return isOK;
	};
	
	$(function(){
		$("#switchSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择交换机",
			nSelectedText: "台交换机被选中",
			disableIfEmpty: true,
			maxHeight: 200,
			enableClickableOptGroups: true,
			onChange: function(element, checked) {
				if(checked){
					var $tbody = $("#listPhySwPanel tbody");
			      	var $optgroup = element.parent();
			      	var phyId = $optgroup.attr("value");
			      	var phyName = $optgroup.attr("label").replace("物理机:", "");
			      	var swId = element.val();
			      	var fmt = "<tr id='phySw%s' phy_id='%s' sw_id='%s'><td>%s</td><td>%s</td><td><a class='btn btn-danger' title='delete' onclick=\"delPhySwRelation('%s','%s')\"><i class='icon-trash icon-white'></i>删除</a></td></tr>";
					
					$tbody.append($(fmt.jFormat(swId, phyId, swId, phyName, element.text(), swId, "")));
					element.attr({disabled: true});
					$("#switchSelect").multiselect("refresh");
				}
		    }
		});
	});
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="switchForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr style="display:none;">
							<td style="text-align:right;">物理机:</td>
							<td>
								<select id="phy2SwSelect" name="phy2SwSelect"></select>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;">交换机:</td>
							<td>
								<select id="switchSelect" multiple="multiple" name="switchSelect">
									<%-- <c:choose>
										<c:when test="${not empty swData}">
											<c:forEach var="item" items="${swData}">
												<option value="${item.sw_id}">${item.sw_name}</option>
											</c:forEach>
										</c:when>
									</c:choose>
									--%>
								</select>
							</td>
						</tr>
						<tr style="display:none;">
							<td colspan="2" style="text-align:center;">
								<button id="addPhySwBtn" onclick="addPhySwRelation();" type="button" title="确认添加物理机与交换机的连接关系" class="btn btn-default primary">确定</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的物理机与交换机连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listPhySwPanel" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>物理机</th>
											<th>交换机</th>
											<th>操作</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</form>
	</div>
</div>
