<%@ page language="java" pageEncoding="UTF-8"%>

<script type="text/javascript">
	$(function(){
		var $swStoSelect01 = $("#swpStopSelect506");
		var $stoSwSelect02 = $("#stopSwpSelect506");
		$swStoSelect01.multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择交换机端口",
			nSelectedText: "个交换机端口被选中",
			disableIfEmpty: true,
			maxHeight: 250,
			enableClickableOptGroups: true
		});
		$stoSwSelect02.multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择存储端口",
			nSelectedText: "个存储端口被选中",
			disableIfEmpty: true,
			maxHeight: 300,
			enableClickableOptGroups: true,
			onChange: function(element, checked) {
				if(checked){
					var stoId = element.val();
				}		      	
		    }
		});
	});
	getIds = function(opt){
		var ids = [], map = {}, idKey = opt.idKey;
		$(opt.selector).each(function(index, opt){
			var id = opt.attributes[idKey].value;
			if(map[id] == undefined){
				ids.push(id);
				map[id] = 1;
			}
		});
		return ids;
	};
	doLoadSwpStop201Form = function(){
		var stoTPCIds = getIds({
			selector: "#listSwStoPanel327 tbody tr[sto_dbtype='TPC']",
			idKey: "sto_id"
		});
		var stoSRIds = getIds({
			selector: "#listSwStoPanel327 tbody tr[sto_dbtype='SR']",
			idKey: "sto_id"
		});
		var swIds = getIds({
			selector: "#listSwStoPanel327 tbody tr",
			idKey: "sw_id"
		});
		var exStartSwIds = [];
		var startSwIds = [];
		var mStartSwIds = {};
		$("#listPhyportPanel tbody tr").each(function(index, tr){
			exStartSwIds.push(tr.attributes["swp_id"].value); <%--在后台过滤掉这些SwitchID--%>
			var swId = tr.attributes["sw_id"].value;
			if(mStartSwIds[swId] == undefined){
				startSwIds.push(swId);
				mStartSwIds[swId] = 1;
			}
		});
		
		$.post("${pageContext.request.contextPath}/servlet/topo/TopoAction?func=GetAllSwpStopBySwIdsStoIds", 
			{
				stoTPCIds: stoTPCIds.join(","),
				stoSRIds: stoSRIds.join(","),
				swIds: swIds.join(","),
				exStartSwIds: exStartSwIds.join(","),
				startSwIds: startSwIds.join(",")
			}, 
			function(jsonData){
				if(jsonData.success){
					var swpData = jsonData.value.swpData;
					var stoTPCData = jsonData.value.stoData.TPC;
					var stoSRData = jsonData.value.stoData.SR;
					var $swStoSelect01 = $("#swpStopSelect506");
					var $stoSwSelect02 = $("#stopSwpSelect506");
					$swStoSelect01.children().remove();
					$stoSwSelect02.children().remove();
					if(swpData && swpData.length > 0){
						$swStoSelect01.html(getCommonOptionHtmlStr(swpData, "交换机", "sw_id", "sw_name", "swp_id", "swp_name"));
						$swStoSelect01.multiselect("rebuild");
					}
					if(stoTPCData && stoTPCData.length > 0){
						$stoSwSelect02.append($(getCommonOptionHtmlStr(stoTPCData, "存储系统", "sto_id", "sto_name", "stop_id", "stop_name")));
					}
					if(stoSRData && stoSRData.length > 0){
						$stoSwSelect02.append($(getCommonOptionHtmlStr(stoSRData, "存储系统", "sto_id", "sto_name", "stop_id", "stop_name")));
					}
					$stoSwSelect02.multiselect("rebuild");
				}
				else {
					alert(jsonData.msg);
				}
			},
		"json");
		return true;
	};
	delSwpStop201Relation = function(stoId, swId){
		$("#swpStop%s_%s".jFormat(stoId, swId)).remove();
	};
	addSwpStop201Relation = function(){
		var $swOpts = $("#swpStopSelect506 option:selected");
		var $p1 = $swOpts.parent();
		var $stoOpt = $("#stopSwpSelect506 option:selected");
		var $p2 = $stoOpt.parent();
		
		var stopId = $stoOpt.val();
		var stopName = $stoOpt.text();
		
		var stoType = $p2.attr("sto_type");
		var dbtype = $p2.attr("dbtype");
		
		var swId = $p1.attr("value");
		var swName = $p1.attr("label").replace("交换机:", "");
		var stoId = $p2.attr("value");
		var stoName = $p2.attr("label").replace("存储系统:", "");
		
		var $tbody = $("#listSwpStopPanel201 tbody");
		
		var fmt = "<tr id='swpStop%s_%s' swp_id='%s' stop_id='%s' sw_id='%s' sto_id='%s' sto_dbtype='%s' sto_type='%s'><td>%s</td><td>%s</td><td>%s</td><td class='stoCls2816'>%s</td><td><a class='btn btn-danger' title='delete' onclick=\"delSwpStop201Relation('%s','%s')\"><i class='icon-trash icon-white'></i>删除</a></td></tr>";
		$swOpts.each(function(index, opt){
			var $opt = $(opt);
			var swpId = $opt.val();
			var swpName = $opt.text();
			$tbody.append($(fmt.jFormat(stopId, swpId, swpId, stopId, swId, stoId, dbtype, stoType, swName, stoName, swpName, stopName, stopId, swpId)));
		});
	};
	doValidateStoForm = function(target, $showTips){
		return true;
	};
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="swpStopForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr>
							<td colspan="2" style="text-align:left;">选择业务系统所在的交换机端口及连接的存储端口</td>
						</tr>
						<tr style="display:none;">
							<td style="text-align:right;">起点交换机端口:</td>
							<td>
								<select id="startSwpStopSelect216" name="startSwpStopSelect216"></select>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;">终点交换机端口:</td>
							<td>
								<select id="swpStopSelect506" name="swpStopSelect506"></select>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;">存储端口:</td>
							<td>
								<select id="stopSwpSelect506" name="stopSwpSelect506"></select>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align:center;">
								<button id="addSwStoBtn" onclick="addSwpStop201Relation();" type="button" title="确认添加交换机端口与存储端口的连接关系" class="btn btn-default primary">确定</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的交换机端口与存储端口连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listSwpStopPanel201" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>终点交换机</th>
											<th>存储系统</th>
											<th>交换机端口</th>
											<th>存储端口</th>
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
