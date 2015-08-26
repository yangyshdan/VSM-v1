<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
	delStoPoolRelation = function(stodId, poolId){
		$("#listStoPoolPanel #sto_pool_%s_%s".jFormat(stodId, poolId)).remove();
		$("#poolSelect option[value='"+poolId+"']").attr({disabled: false, selected: false});
		$("#poolSelect").multiselect("refresh");
	};
	$(function(){
		$("#poolSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择存储池",
			nSelectedText: "台存储池被选中",
			disableIfEmpty: true,
			maxHeight: 200,
			numberDisplayed: 1,
			enableClickableOptGroups: true,
			onChange: function(element, checked) {
				if(checked){
					var $tbody = $("#listStoPoolPanel tbody");
			      	var $optgroup = element.parent();
			      	stoPool5213.addMap($tbody, $optgroup.attr("value"), $optgroup.attr("label").replace("存储系统:", ""), 
			      		 element.val(), element.text(), $optgroup.attr("dbtype"));
			      	element.attr({disabled: true});
					$("#poolSelect").multiselect("refresh");
				}
		    }
		});
	});
	getOptionHtmlStr = function(list, ids){
		if(list == null || list == undefined || (!$.isArray(list))){ return ""; }
		var html = "";
		var first = true;
		var optgroupFmt = "<optgroup value='%s' dbtype='%s' label='存储系统:%s'>";
		var optionFmt = "<option value='%s' dbtype='%s' %s>%s</option>";
		function getSelectedStr(pool_id){
			return ids? (ids.indexOf(pool_id.toString()) >= 0? "disabled='disabled' selected='selected'" : "") : "";
		}
		for(var i = 0, len = list.length, obj; i < len; ++i){
			obj = list[i];
			if(first){
				html += optgroupFmt.jFormat(obj.sto_id, obj.dbtype, obj.sto_name);
				html += optionFmt.jFormat(obj.pool_id, obj.dbtype, getSelectedStr(obj.pool_id), obj.pool_name);
				first = false;
			}
			else {
				if(list[i - 1].sto_id == obj.sto_id){
					html += optionFmt.jFormat(obj.pool_id, obj.dbtype, getSelectedStr(obj.pool_id), obj.pool_name);
				}
				else {
					html += "</optgroup>";
					html += optgroupFmt.jFormat(obj.sto_id, obj.dbtype, obj.sto_name);
					html += optionFmt.jFormat(obj.pool_id, obj.dbtype, getSelectedStr(obj.pool_id), obj.pool_name);
				}
			}
		}
		html += "</optgroup>";
		return html;
	};
	doLoadPoolForm = function(){
		var db2StoIds = getIds({
			selector: "#listSwStoPanel327 tbody tr[sto_dbtype='TPC']",
			idKey: "sto_id"
		});
		var srDBStoIds = getIds({
			selector: "#listSwStoPanel327 tbody tr[sto_dbtype='SR']",
			idKey: "sto_id"
		});
		var $poolSelect = $("#poolSelect");
		var data = {};
		data.db2StoIds = db2StoIds.join(",");
		data.srDBStoIds = srDBStoIds.join(",");
		<%--节点缓存存储系统的编号--%>
		$poolSelect.data("db2StoIds", db2StoIds);
		$poolSelect.data("srDBStoIds", srDBStoIds);
		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetPoolsBystoIds", 
			data, 
			function(jsonData){
				$poolSelect.children().remove();
				var hsr = undefined, htpc = undefined;
				if(historyData){
					if(historyData.SR && historyData.SR.Pool){ hsr = historyData.SR.Pool; }
					if(historyData.TPC && historyData.TPC.Pool){ htpc = historyData.TPC.Pool; }
				}
				if(jsonData.success){
					$poolSelect.html(
						getOptionHtmlStr(jsonData.value.SR, hsr) +
						getOptionHtmlStr(jsonData.value.TPC, htpc)
					);
					var $tbody = $("#listStoPoolPanel tbody");
					if(historyData){
						if(historyData.SR && historyData.SR.Pool){
							var ids = historyData.SR.Pool.split(",");
							for(var i in ids){
								var $opt = $($poolSelect.find("option[value='" + ids[i] + "']"));
								if($opt.length == 1){
									var $optgroup = $opt.parent();
									stoPool5213.addMap($tbody, $optgroup.attr("value"), $optgroup.attr("label").replace("存储系统:", ""), 
				      		 			$opt.val(), $opt.text(), $optgroup.attr("dbtype"));
								}
							}
						}
						if(historyData.TPC && historyData.TPC.Pool){
							var ids = historyData.TPC.Pool.split(",");
							for(var i in ids){
								var $opt = $($poolSelect.find("option[value='" + ids[i] + "']"));
								if($opt.length == 1){
									var $optgroup = $opt.parent();
									stoPool5213.addMap($tbody, $optgroup.attr("value"), $optgroup.attr("label").replace("存储系统:", ""), 
				      		 			$opt.val(), $opt.text(), $optgroup.attr("dbtype"));
								}
							}
						}
					}
					$poolSelect.multiselect("rebuild");
				}
				else {
					alert(jsonData.msg);
				}
			},
		"json");
		return true;
	};
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="poolForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr>
							<td style="text-align:right;">存储池:</td>
							<td>
								<select id="poolSelect" multiple="multiple" name="poolSelect"></select>
							</td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的存储系统与存储池连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listStoPoolPanel" 
									class="table table-bordered table-striped table-condensed colToggle" 
									style="word-break:break-all;">
									<thead>
										<tr>
											<th>存储系统</th>
											<th>存储池</th>
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
