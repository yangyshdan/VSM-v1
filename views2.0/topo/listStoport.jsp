<%@ page language="java" pageEncoding="UTF-8"%>

<script type="text/javascript">
	$(function(){
		<%--初始化存储端口，把所有存储端口拿出来--%>
		/*$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetPhyportsByPhyids", 
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
						$sel01.html(getCommonOptionHtmlStr(jsonData.value.FrontSwitchPort, "交换机端口", "sw_id", "sw_name", "swp_id", "swp_name"));
						$sel01.multiselect("rebuild");
					}
				}
				else {
					alert(jsonData.msg);
				}
			},
		"json");*/
		$("#stoportSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择存储端口",
			nSelectedText: "个存储端口被选中",
			disableIfEmpty: true,
			maxHeight: 250,
			enableClickableOptGroups: true,
			onChange: function(element, checked) {
				if(checked){
					var stoId = element.val();
					/*$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetPhyportsByPhyids", 
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
									$sel01.html(getCommonOptionHtmlStr(jsonData.value.FrontSwitchPort, "交换机端口", "sw_id", "sw_name", "swp_id", "swp_name"));
									$sel01.multiselect("rebuild");
								}
							}
							else {
								alert(jsonData.msg);
							}
						},
					"json");*/
				}		      	
		    }
		});
	});
	
	delStoportSwportRelation = function(stoId, stoName, stoType, dbtype){
		$("#listSwStoPanel tbody tr.sw_sto_" + stoId).remove();
		$("#storageSelect").append($("<option value='%s' sto_dbtype='%s' sto_type='%s'>%s</option>".jFormat(stoId, dbtype, stoType, stoName)));
		$("#storageSelect").attr({disabled: false});
	};
	addStoportSwportRelation = function(){
		
	};
	doValidateStoportForm = function(target, $showTips){
		return true;
	};
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="stoportForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr>
							<td colspan="2" style="text-align:left;">选择业务系统所在的存储系统的端口</td>
						</tr>
						<tr>
							<td style="text-align:right;">存储端口:</td>
							<td>
								<select id="stoportSelect" name="stoportSelect"></select>
							</td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的存储端口与交换机端口连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listStoportSwportPanel" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>存储端口</th>
											<th>交换机端口</th>
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
