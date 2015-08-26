<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<script type="text/javascript">
	$(function(){
		$("#volumeSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择存储卷",
			nSelectedText: "台存储卷被选中",
			disableIfEmpty: true,
			maxHeight: 200,
			numberDisplayed: 1,
			enableClickableOptGroups: true,
			onChange: function(element, checked) {
		      	var $tbody = $("#listPoolVolumePanel tbody");
		      	var $optgroup = element.parent();
		      	var pool_id = $optgroup.attr("value");
		      	var pool_name = $optgroup.attr("label").replace("存储池:", "");
		      	var dbtype = element.attr("dbtype");
		      	var vol_id = element.val();
		      	poolVol5123.addMap($tbody, pool_id, pool_name, vol_id, element.text(), dbtype);
				element.attr({disabled: true});
				$("#volumeSelect").multiselect("refresh");
		    }
		});
	});
	getVolumeOptionHtmlStr = function(list, ids){
		if(list == null || list == undefined || (!$.isArray(list))){ return ""; }
		var html = "";
		var first = true;
		var optgroupFmt = "<optgroup value='%s' dbtype='%s' label='存储池:%s'>";
		var optionFmt = "<option value='%s' dbtype='%s' %s>%s</option>";
		function getSelectedStr(id){
			return ids? (ids.indexOf(id.toString()) >= 0? "disabled='disabled' selected='selected'" : "") : "";
		}
		for(var i = 0, len = list.length, obj; i < len; ++i){
			obj = list[i];
			if(first){
				html += optgroupFmt.jFormat(obj.pool_id, obj.dbtype, obj.pool_name);
				html += optionFmt.jFormat(obj.vol_id, obj.dbtype, getSelectedStr(obj.vol_id), obj.vol_name);
				first = false;
			}
			else {
				if(list[i - 1].pool_id == obj.pool_id){
					html += optionFmt.jFormat(obj.vol_id, obj.dbtype, getSelectedStr(obj.vol_id), obj.vol_name);
				}
				else {
					html += "</optgroup>";
					html += optgroupFmt.jFormat(obj.pool_id, obj.dbtype, obj.pool_name);
					html += optionFmt.jFormat(obj.vol_id, obj.dbtype, getSelectedStr(obj.vol_id), obj.vol_name);
				}
			}
		}
		html += "</optgroup>";
		return html;
	};
	doLoadVolumeForm = function(){
		var map_ibm = {}, map_other = {};
		var tpcPoolIds = [], srDBPoolIds = [], tpcStoIds = [], srDBStoIds = [];
		$("#listStoPoolPanel tbody tr[sto_dbtype='TPC']").each(function(index, td){
			var stoId = td.attributes["sto_id"].value;
			var poolId = td.attributes["pool_id"].value;
			tpcPoolIds.push(poolId);
			if(map_ibm[stoId] == undefined){
				tpcStoIds.push(stoId);
				map_ibm[stoId] = 1;
			}
		});
		
		$("#listStoPoolPanel tbody tr[sto_dbtype='SR']").each(function(index, td){
			var stoId = td.attributes["sto_id"].value;
			var poolId = td.attributes["pool_id"].value;
			srDBPoolIds.push(poolId);
			if(map_other[stoId] == undefined){
				srDBStoIds.push(stoId);
				map_other[stoId] = 1;
			}
		});

		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetVolumesBystoIdsPoolIds", 
			{ 	db2StoIds: tpcStoIds.join(","), 
				srDBStoIds: srDBStoIds.join(","),
				db2PoolIds: tpcPoolIds.join(","), 
				srDBPoolIds: srDBPoolIds.join(",")
			}, 
			function(jsonData){
				var $volumeSelect = $("#volumeSelect");
				$volumeSelect.children().remove();
				if(jsonData.success){
					var hsr = undefined, htpc = undefined;
					if(historyData){
						if(historyData.SR && historyData.SR.Volume){ hsr = historyData.SR.Volume; }
						if(historyData.TPC && historyData.TPC.Volume){ htpc = historyData.TPC.Volume; }
					}
					$volumeSelect.html(
						getVolumeOptionHtmlStr(jsonData.value.SR, hsr) +
						getVolumeOptionHtmlStr(jsonData.value.TPC, htpc)
					);
					var $tbody = $("#listPoolVolumePanel tbody");
					if(historyData){
						if(historyData.SR && historyData.SR.Volume){
							var ids = historyData.SR.Volume.split(",");
							for(var i in ids){
								var $opt = $($volumeSelect.find("option[value='" + ids[i] + "']"));
								if($opt.length == 1){
									var $optgroup = $opt.parent();
									poolVol5123.addMap($tbody, $optgroup.attr("value"), $optgroup.attr("label").replace("存储池:", ""), 
				      		 			$opt.val(), $opt.text(), $optgroup.attr("dbtype"));
								}
							}
						}
						if(historyData.TPC && historyData.TPC.Volume){
							var ids = historyData.TPC.Volume.split(",");
							for(var i in ids){
								var $opt = $($volumeSelect.find("option[value='" + ids[i] + "']"));
								if($opt.length == 1){
									var $optgroup = $opt.parent();
									poolVol5123.addMap($tbody, $optgroup.attr("value"), $optgroup.attr("label").replace("存储池:", ""), 
				      		 			$opt.val(), $opt.text(), $optgroup.attr("dbtype"));
								}
							}
						}
					}
					
					$volumeSelect.multiselect("rebuild");
				}
				else { alert(jsonData.msg); }
			},
		"json");
	};
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="poolForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr>
							<td style="text-align:right;">存储卷:</td>
							<td><select id="volumeSelect" multiple="multiple" name="volumeSelect"></select></td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的存储池与存储卷连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listPoolVolumePanel" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>存储池</th>
											<th>存储卷</th>
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
