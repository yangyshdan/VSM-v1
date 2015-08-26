<%@ page language="java" pageEncoding="UTF-8"%>

<script type="text/javascript">
	doLoadTable = function(){
		var data = {};
		var $app = $("#devConnSummaryApp");
		$app.siblings().remove();
		var $parent = $app.parent();
		var $appForm = $("#appForm2816");
		data.appName = $appForm.find("input[name='appName']").val();
		data.appDesc = $appForm.find("input[name='description']").val();
		$("#summAppName").text(data.appName);
		$("#summAppDesc").text(data.appDesc);
		var ids = ["#listAppPhyPanel", "#listPhyVMPanel", "#listPhyportPanel", 
			"#listSwStoPanel327", "#listSwpStopPanel201",  "#listPhySwPanel",
			"#listStoPoolPanel", "#listPoolVolumePanel"];
		for(var i = 0, len = ids.length, $panel, $td; i < len; ++i){
			$panel = $(ids[i]).clone();
			$panel.find("thead th.delHeader").remove();
			$panel.find("tbody td.del2816").remove();
			$panel.find("a.btn-danger").parent().remove();
			$parent.append($panel);
		}
	};
	doFinishing = function($showTips){
		var data = {};
		<%--应用--%>
		data.appName = $("#summAppName").text();
		data.appDesc = $("#summAppDesc").text();
		<%--应用与物理机的映射--%>
		var $trs = $("#devConnSummary #listAppPhyPanel tbody tr");
		data.appPhySize = $trs.length;
		if($trs.length > 0){
			for(var i = 0, len = $trs.length, id; i < len; ++i){
				id = "phyAppId" + i;
				data[id] = $trs[i].attributes["phy_id"].value;
			}
		}
		<%--物理机与虚拟机的映射--%>
		$trs = $("#devConnSummary #listPhyVMPanel tbody tr");
		data.phyVMSize = $trs.length;
		if($trs.length > 0){
			for(var i = 0, len = $trs.length, id; i < len; ++i){
				id = "phyVMId" + i;
				data[id] = $trs[i].attributes["phy_id"].value;
				id = "VMPhyId" + i;
				data[id] = $trs[i].attributes["vm_id"].value;
			}
		}
		
		<%--
			物理机端口与交换机端口的映射
			物理机端口--交换机端口               
			物理机------交换机            
			物理机------物理机端口               
			交换机端口--交换机
		--%>
		$trs = $("#devConnSummary #listPhyportPanel tbody tr");
		data.phypSwpSize = $trs.length;
		if($trs.length > 0){
			var mPhySw = {}, mPhyPhyp = {}, mSwpSw = {}; // 用于去重复
			var cPhySw = 0, cPhyPhyp = 0, cSwpSw = 0;
			for(var i = 0, len = $trs.length, id, phyId, swId, phypId, swpId; i < len; ++i){
				id = "phypSwpId" + i;
				phypId = $trs[i].attributes["phyp_id"].value;
				swpId = $trs[i].attributes["swp_id"].value;
				data[id] = phypId;
				id = "swpPhypId" + i;
				data[id] = swpId;
				
				phyId = $trs[i].attributes["phy_id"].value;
				swId = $trs[i].attributes["sw_id"].value;
				id = phyId + "_" + swId;
				if(mPhySw[id] == undefined){
					data["phySwId" + i] = phyId;
					data["swPhyId" + i] = swId;
					++cPhySw;
					mPhySw[id] = 1;
				}
				
				id = phyId + "_" + phypId;
				if(mPhyPhyp[id] == undefined){
					data["phyPhypId" + i] = phyId;
					data["phypPhyId" + i] = phypId;
					++cPhyPhyp;
					mPhyPhyp[id] = 1;
				}
				
				id = swpId + "_" + swId;
				if(mSwpSw[id] == undefined){
					data["swpSwId" + i] = swpId;
					data["swSwpId" + i] = swId;
					++cPhySw;
					mSwpSw[id] = 1;
				}
			}
			data["phySwSize"] = cPhySw;
			data["phyPhypSize"] = cPhyPhyp;
			data["swpSwSize"] = cSwpSw;
		}
		
		<%-- 物理机------交换机    --%>
		$trs = $("#devConnSummary #listPhySwPanel tbody tr");
		if($trs.length > 0){
			var mPhySw = {}; // 用于去重复
			var cPhySw = 0;
			for(var i = 0, len = $trs.length, id, phyId, swId; i < len; ++i){				
				phyId = $trs[i].attributes["phy_id"].value;
				swId = $trs[i].attributes["sw_id"].value;
				id = phyId + "_" + swId;
				if(mPhySw[id] == undefined){
					data["phySwId" + cPhySw] = phyId;
					data["swPhyId" + cPhySw] = swId;
					++cPhySw;
					mPhySw[id] = 1;
				}
			}
			data["phySwSize"] = cPhySw;
		}
		
		<%--交换机与存储系统的映射--%>
		$trs = $("#devConnSummary #listSwStoPanel327 tbody tr");
		data.swStoSize = $trs.length;
		if($trs.length > 0){
			var mPhySwIdStartEnd = {};
			var cPhySwIdStartEnd = 0;
			for(var i = 0, len = $trs.length, id, sid, eid; i < len; ++i){
				id = "swStoId" + i;
				eid = $trs[i].attributes["sw_id"].value;
				data[id] = eid;
				id = "stoSwId" + i;
				data[id] = $trs[i].attributes["sto_id"].value;
				id = "swStoDbtype" + i;
				data[id] = $trs[i].attributes["sto_dbtype"].value;
				sid = $trs[i].attributes["ssw_id"].value;
				id = sid + "_" + eid;
				if(mPhySwIdStartEnd[id] == undefined){
					data["phySwIdStart" + cPhySwIdStartEnd] = sid;
					data["stoSwIdEnd" + cPhySwIdStartEnd] = eid;
					++cPhySwIdStartEnd;
					mPhySwIdStartEnd[id] = 1;
				}
			}
			data.physwStoswSize = cPhySwIdStartEnd;
		}
		
		<%--起点交换机与终点交换机的映射
		$trs = $("#devConnSummary #listSw2SwPanel022 tbody tr");
		data.physwStoswSize = $trs.length;
		for(var i = 0, len = $trs.length, id; i < len; ++i){
			id = "phySwIdStart" + i;
			data[id] = $trs[i].attributes["phySw_id"].value;
			id = "stoSwIdEnd" + i;
			data[id] = $trs[i].attributes["stoSw_id"].value;
		}--%>
		<%--
			交换机端口与存储端口的映射
			交换机------交换机端口
			交换机------存储端口
			存储端口----存储系统
		--%>
		$trs = $("#devConnSummary #listSwpStopPanel201 tbody tr");
		data.swpStopSize = $trs.length;
		if($trs.length > 0){
			var mSwSwp = {}, mSwStop = {}, mStopSto = {};
			var cSwSwp = 0, cSwStop = 0, cStopSto = 0;
			for(var i = 0, len = $trs.length, id, swId, stoId, swpId, stopId, tr, dbType; i < len; ++i){
				tr = $trs[i];
				swId = tr.attributes["sw_id"].value;
				stoId = tr.attributes["sto_id"].value;
				swpId = tr.attributes["swp_id"].value;
				stopId = tr.attributes["stop_id"].value;
				dbType = tr.attributes["sto_dbtype"].value;
				id = "swpStopId" + i;
				data[id] = swpId;
				id = "stopSwpId" + i;
				data[id] = stopId;
				id = "swpStopDbtype" + i;
				data[id] = dbType;
				
				id = swId + "_" + swpId;
				if(mSwSwp[id] == undefined){
					data["stoSwpSwId" + i] = swpId;
					data["stoSwSwpId" + i] = swId;
					++cSwSwp;
					mSwSwp[id] = 1;
				}
				id = swId + "_" + stopId;
				if(mSwStop[id] == undefined){
					data["swStopId" + i] = swId;
					data["stopSwId" + i] = stopId;
					data["swStopDbType" + i] = dbType;
					++cSwStop;
					mSwStop[id] = 1;
				}
				
				id = stopId + "_" + stoId;
				if(mStopSto[id] == undefined){
					data["stopStoId" + i] = stopId;
					data["stoStopId" + i] = stoId;
					data["stopStoDbType" + i] = dbType;
					++cStopSto;
					mStopSto[id] = 1;
				}
			}
			data["stoSwSwpSize"] = cSwSwp;
			data["swStopSize"] = cSwStop;
			data["stopStoSize"] = cStopSto;
		}
		
		<%--存储系统与存储池的映射--%>
		$trs = $("#devConnSummary #listStoPoolPanel tbody tr");
		data.stoPoolSize = $trs.length;
		if($trs.length > 0){
			for(var i = 0, len = $trs.length, id; i < len; ++i){
				id = "stoPoolId" + i;
				data[id] = $trs[i].attributes["sto_id"].value;
				id = "poolStoId" + i;
				data[id] = $trs[i].attributes["pool_id"].value;
				id = "stoPoolDbtype" + i;
				data[id] = $trs[i].attributes["sto_dbtype"].value;
			}
		}
		
		<%--存储池与存储卷的映射--%>
		$trs = $("#devConnSummary #listPoolVolumePanel tbody tr");
		data.poolVolSize = $trs.length;
		if($trs.length > 0){
			for(var i = 0, len = $trs.length, id; i < len; ++i){
				id = "poolVolId" + i;
				data[id] = $trs[i].attributes["pool_id"].value;
				id = "volPoolId" + i;
				data[id] = $trs[i].attributes["vol_id"].value;
				id = "poolVolDbtype" + i;
				data[id] = $trs[i].attributes["sto_dbtype"].value;
			}
		}
		
		var isOK = {};
		if(historyData && historyData.appData){
			data.appId = historyData.appData.app_id;
		}
		
		$.ajax({
			url: "${pageContext.request.contextPath}/servlet/topo/TopoAction?func=SaveDeviceMap",
			type: "post",
			async: false,
			dataType: "json",
			data: data,
			success: function(jsonData){
		     	isOK = jsonData;
		     	$showTips.hide();
				if(jsonData.success == false){
					$showTips.html(jsonData.msg).show();
				}
				else{ $("#myModal").modal("hide"); }
		    },
			statusCode: {
			   	404: function(){
			    	alert("page not found!");
				},
				500: function(){
					alert("server internal errors!");
				}
			}
		});
		return isOK;
	};
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div id="devConnSummary" class="box-content">
		<table id="devConnSummaryApp" class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
			<thead>
				<tr>
					<th>业务系统名称</th>
					<th>描述</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td id="summAppName"></td>
					<td id="summAppDesc"></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
