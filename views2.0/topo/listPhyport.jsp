<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
	doLoadFrontSwitchPorts1 = function($showTips, isSwitch){		
		var phyportIds = [];
		$.each($("#listPhyportPanel tbody tr"), function(index, tr){
			phyportIds.push($(tr).attr("port_id"));
		});
		
		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetPhyportSwportsByIds", 
			{ phyportIds: phyportIds.join(","), isSwitch: isSwitch }, 
			function(jsonData){
				var $virtualSelect = $("#frontSwitchPortSelect");
				$virtualSelect.children().remove();
				var $select1 = $("#fPhyPortSelect");
				$select1.children().remove();
				if(jsonData.success){
					if(jsonData.value.SwitchPort){
						var $virtualSelect = $("#frontSwitchPortSelect");
						$virtualSelect.children().remove();
						$virtualSelect.html(getCommonOptionHtmlStr(jsonData.value.SwitchPort, "交换机", "sw_id", "sw_name", "swp_id", "swp_name"));
						$virtualSelect.multiselect("rebuild");
					}
					if(jsonData.value.PhysicalPort){
						var $select1 = $("#fPhyPortSelect");
						$select1.children().remove();
						$select1.html(getCommonOptionHtmlStr(jsonData.value.PhysicalPort, "物理机", "hyp_id", "hyp_name", "port_id", "port_name"));
						$select1.multiselect("rebuild");
					}
				}
				else {
					alert(jsonData.msg);
				}
			},
		"json");
		return true;
	};
	doFreshen2816 = function(){
		doLoadPhysicalToPhyportForm(null, false);
	};
	doLoadPhysicalToPhyportForm = function(target, isSwitch){
		<%--加载物理机选项--%>
		var $phy2PortSelect = $("#phy2PortSelect");
		$phy2PortSelect.children().remove();
		var phyIds = [];
		$.each($("#physicalSelect option:selected"), function(index, opt){
			var $opt = $(opt);
			var phyId = $opt.val();
			$phy2PortSelect.append($("<option value='%s'>%s</option>".jFormat(phyId, $opt.text())));
			phyIds.push(phyId);
		});
		<%--根据物理机ID推导出物理机端口和交换机端口--%>
		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetPhyportsByPhyids", 
			{ phyids: phyIds.join(","), isSwitch: isSwitch }, 
			function(jsonData){
				if(jsonData.success && jsonData.value){
					if(jsonData.value.PhysicalPort){
						var $sel = $("#phyportSelect");
						$sel.children().remove();
						$sel.html(getCommonOptionHtmlStr(jsonData.value.PhysicalPort, "物理机", "hyp_id", "hyp_name", "port_id", "port_name"));
						$sel.multiselect("rebuild");
					}
					if(jsonData.value.FrontSwitchPort){
						var $sel01 = $("#frontSwportSelect");
						$sel01.children().remove();
						$sel01.html(getCommonOptionHtmlStr(jsonData.value.FrontSwitchPort, "交换机", "sw_id", "sw_name", "swp_id", "swp_name"));
						$sel01.multiselect("rebuild");
					}
				}
				else {
					alert(jsonData.msg);
				}
			},
		"json");
		return true;
	};
	$(function(){
		$("#phyportSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择物理机端口",
			nSelectedText: "个物理机端口被选中",
			disableIfEmpty: true,
			maxHeight: 250,
			enableClickableOptGroups: true
		});
		
		$("#frontSwportSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择交换机端口",
			nSelectedText: "个交换机端口被选中",
			disableIfEmpty: true,
			maxHeight: 250,
			enableClickableOptGroups: true
		});
		$("#addSelectPhyportBtn a").click(function(){
			var $btn = $(this);
			if(parseInt($btn.attr("action")) == 1){
				$("#selectPhyportSwport").show();
				$("#addPhyportTable").hide();
			}
			else {
				$("#addPhyportTable").show();
				$("#selectPhyportSwport").hide();
			}
			$btn.attr("disabled", true);
			$btn.siblings().attr("disabled", false);
		});
	});
	delPhyportRelation = function(phypId, swpId){
		$("#listPhyportPanel tbody tr.phypswp%s_%s".jFormat(phypId, swpId)).remove();
		$("#phyportSelect option[value='%s']".jFormat(phypId)).attr({selected: false, disabled: false});
		$("#phyportSelect").multiselect("refresh");
		
		$("#frontSwportSelect option[value='%s']".jFormat(swpId)).attr({selected: false, disabled: false});
		$("#frontSwportSelect").multiselect("refresh");
	};
	addPhyportInfo = function(){
		var $phySelect = $("#phy2PortSelect");
		var phyId = $phySelect.val();
		var phyName = $phySelect.find("option:selected").text();
		var $table = $("#addPhyportInfo");
		var phyportName = $table.find("input[name='phyportName']").val();
		var phyportNumber = $table.find("input[name='phyportNumber']").val();
		var phyportType = $table.find("input[name='phyportType']").val();
		
		var phyIds = [];
		$("#physicalSelect option:selected").each(function(index, opt){
			phyIds.push($(opt).val());
		});
		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=SavePhyPort", 
		{ 
			phyids: phyIds.join(","),
			hypId: phyId,
			portName: phyportName,
			portNum: phyportNumber,
			portType: phyportType
		}, 
		function(json){ <%--返回的是t_res_physical_port--%>
			if(json.success){						
				var $phyportSelect = $("#phyportSelect optgroup[value='%s']".jFormat(phyId));
				$phyportSelect.append($("<option value='%s'>%s</option>".jFormat(json.value, phyportName)));
				$phyportSelect.multiselect("refresh");
				$("#showTips").text("您添加物理机端口%s成功保存!".jFormat(phyportName)).show();
			}
			else { alert(json.msg); }
		},
		"json");
	};
	addPhyportRelation = function(){ <%--单击按钮，获取物理机--%>
		var $opt1 = $("#phyportSelect option:selected:not(disabled='disabled')");
		if($opt1.length != 1){ alert("请选择一个物理机端口"); return; }
		var $opt2 = $("#frontSwportSelect option:selected:not(disabled='disabled')");
		if($opt2.length != 1){ alert("请选择一个交换机端口"); return; }
		
		var $p1 = $opt1.parent();
		var $p2 = $opt2.parent();
		
		var phypId = $opt1.val();
		var phypName = $opt1.text();
		var swpId = $opt2.val();
		var swpName = $opt2.text();
		
      	var $tbody = $("#listPhyportPanel tbody");
      	var fmt = "<tr phyp_id='%s' swp_id='%s' phy_id='%s' sw_id='%s' class='phypswp%s_%s'><td>%s</td><td>%s</td><td>%s</td><td class='phypswpCls'>%s</td><td>"+
		"<a class='btn btn-danger' title='delete' onclick=\"delPhyportRelation('%s','%s')\"><i class='icon-trash icon-white'></i>删除</a></td></tr>";
      	
      	$tbody.append($(fmt.jFormat(phypId, swpId, $p1.attr("value"), $p2.attr("value"), phypId, swpId,
      		$p1.attr("label").replace("物理机:", ""), $p2.attr("label").replace("交换机:", ""),
      	 phypName, swpName, phypId, swpId)));
      	
      	$opt1.attr({disabled: true});
      	$("#phyportSelect").multiselect("refresh");
      	
      	$opt2.attr({disabled: true});
      	$("#frontSwportSelect").multiselect("refresh");
	};
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="phyportForm2816">
			<fieldset>
				 <div style="margin:0;" class="btn-toolbar">
	              <div id="addSelectPhyportBtn" class="btn-group">
	                <a action="1" class="btn" disabled="disabled">选择物理机端口</a>
	                <a action="2" class="btn">新增物理机端口</a>
	              </div>
	            </div>
				<table class="table" id="addPhyportTable" style="display:none;">
					<tbody>
						<tr class="addPhyport">
							<td style="text-align:right;">物理机:</td>
							<td><select id="phy2PortSelect" name="phy2PortSelect"></select></td>
						</tr>
						<tr class="addPhyport">
							<td id="addPhyportInfo" colspan="2">
								<table class="table">
									<tbody>
										<tr><td colspan="2" style="text-align:center;">物理机端口信息</td></tr>
										<tr>
											<td style="text-align:right;">物理机端口名称:</td>
											<td><input name="phyportName" type="text" class="form-control" placeholder="例如, physical port 1" style="width:150px;cursor:pointer;"/></td>
										</tr>
										<tr>
											<td style="text-align:right;">物理机端口号:</td>
											<td><input name="phyportNumber" type="text" class="form-control" placeholder="例如, 1" style="width:150px;cursor:pointer;"/></td>
										</tr>
										<tr>
											<td style="text-align:right;">物理机端口类型 :</td>
											<td><input name="phyportType" type="text" class="form-control" placeholder="例如, G_PORT" style="width:150px;cursor:pointer;"/></td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align:center;">
								<button onclick="addPhyportInfo();" type="button" class="btn btn-default primary">确定</button>
							</td>
						</tr>
					</tbody>
				</table>
				<table class="table" id="selectPhyportSwport">
					<tbody>
						<tr><td colspan="3">选择物理机端口与所对应的交换机端口</td></tr>
						<tr>
							<td style="text-align:right;">物理机端口:</td>
							<td>
								<select id="phyportSelect" name="phyportSelect"></select>
							</td>
							<td>
								<div class="box-icon" style="">																		
									<a href="javascript:void(0);" class="btn btn-round" title="刷新" onclick="doFreshen2816();" 
										data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
								</div>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;">交换机端口:</td>
							<td><select id="frontSwportSelect" name="frontSwportSelect"></select></td>
							<td></td>
						</tr>
						<tr>
							<td colspan="3" style="text-align:center;">
								<button id="addPhyportRelationBtn" onclick="addPhyportRelation();" type="button" class="btn btn-default primary">确定</button>
						    </td>
						</tr>
						<tr><td colspan="3">在这里，查看您所添加的物理机与交换机连接</td></tr>
						<tr>
							<td colspan="3">
								<table id="listPhyportPanel" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>物理机</th>
											<th>交换机</th>
											<th>物理机端口</th>
											<th>交换机端口</th>
											<th class="delHeader">操作</th>
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
