<%@ page language="java" pageEncoding="UTF-8"%>

<script type="text/javascript">
	doLoadSw2SwForm022 = function(target){
		var phySwIds = [], stoSwIds = [];
		var $tr1 = $("#listPhyportPanel tbody tr");
		$tr1.each(function(index, tr){
			phySwIds.push(tr.attributes["sw_id"].value);
		});
		
		var $tr2 = $("#listSwStoPanel327 tbody tr");
		$tr2.each(function(index, tr){
			stoSwIds.push(tr.attributes["sw_id"].value);
		});
		
		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetAllPhySwStoSw", 
			{ phySwIds: phySwIds.join(","), stoSwIds: stoSwIds.join(",") }, 
			function(jsonData){
				if(jsonData.success){
					var data = jsonData.value.phySw;
					if(data){
						var $sel1 = $("#phy2SwSelect022");
						$sel1.children().remove();
						for(var i = 0, len = data.length, d; i < len; ++i){
							d = data[i];
							$sel1.append($("<option value='%s'>%s</option>".jFormat(d.sw_id, d.sw_name)));
						}
						$sel1.multiselect("rebuild");
					}
					data = jsonData.value.stoSw;
					if(data){
						var $sel2 = $("#sw2StoSelect022");
						$sel2.children().remove();
						for(var i = 0, len = data.length, d; i < len; ++i){
							d = data[i];
							$sel2.append($("<option value='%s'>%s</option>".jFormat(d.sw_id, d.sw_name)));
						}
						$sel2.multiselect("rebuild");
					}
				}
				else {
					alert(jsonData.msg);
				}
			},
		"json");
		return true;
	};
	addSw2Sw022Relation = function(){ <%--单击按钮，获取物理机--%>
		var $opts1 = $("#phy2SwSelect022 option:selected");
		var $opts2 = $("#sw2StoSelect022 option:selected");
		
		var phySwId = $opts1.val();
		var phySwName = $opts1.text();
		var stoSwId = $opts2.val();
		var stoSwName = $opts2.text();
		
		var $tbody = $("#listSw2SwPanel022 tbody");
		var fmt = "<tr id='psw_ssw%s_%s' phySw_id='%s' stoSw_id='%s'><td>%s</td><td class='switchCls2816'>%s</td><td>"+
				"<a class='btn btn-danger' title='delete' onclick=\"delSw2Sw022Relation('%s','%s')\">"+
				"<i class='icon-trash icon-white'></i>删除</a></td></tr>";
		$tbody.append($(fmt.jFormat(phySwId, stoSwId, phySwId, stoSwId, phySwName, stoSwName, phySwId, stoSwId)));
	};

	delSw2Sw022Relation = function(phySwId, stoSwId){
		$("#listSw2SwPanel022 #psw_ssw%s_%s".jFormat(phySwId, stoSwId)).remove();
	};
	
	doValidateSwitchForm = function(target, $showTips){
		return true;
	};
	
	$(function(){
		$("#phy2SwSelect022").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择交换机",
			nSelectedText: "台交换机被选中",
			disableIfEmpty: true,
			maxHeight: 200,
			enableClickableOptGroups: true
		});
		
		$("#sw2StoSelect022").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择交换机",
			nSelectedText: "台交换机被选中",
			disableIfEmpty: true,
			maxHeight: 200,
			enableClickableOptGroups: true
		});
	});
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="sw2SwForm2816022">
			<fieldset>
				<table class="table">
					<tbody>
						<tr>
							<td style="text-align:left;" colspan="2">
								<ol type="decimal">
									<li>与物理机连接的交换机作为起点交换机</li>
									<li>与存储系统连接的交换机作为终点交换机</li>
								</ol>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;">连接物理机的交换机:</td>
							<td>
								<select id="phy2SwSelect022" name="phy2SwSelect022"></select>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;">连接存储系统的交换机:</td>
							<td>
								<select id="sw2StoSelect022" name="sw2StoSelect022"></select>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align:center;">
								<button id="addSw2Sw022Btn" onclick="addSw2Sw022Relation();" type="button" title="确认添加起点交换机与终点交换机的连接关系" class="btn btn-default primary">确定</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的起点交换机与终点交换机连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listSw2SwPanel022" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>起点交换机</th>
											<th>终点交换机</th>
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
