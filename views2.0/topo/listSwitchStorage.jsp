<%@ page language="java" pageEncoding="UTF-8"%>

<script type="text/javascript">
	
$(function(){
	var $swStoSelect01 = $("#swStoSelect01");
	var $stoSwSelect02 = $("#stoSwSelect02");
	$swStoSelect01.multiselect({
		enableFiltering: 1,
		nonSelectedText: "未选择交换机",
		nSelectedText: "台交换机被选中",
		disableIfEmpty: true,
		maxHeight: 300,
		enableClickableOptGroups: true,
		onChange: function(element, checked) { }
	});
	$stoSwSelect02.multiselect({
		enableFiltering: 1,
		nonSelectedText: "未选择存储系统",
		nSelectedText: "台交换机被选中",
		disableIfEmpty: true,
		maxHeight: 300,
		enableClickableOptGroups: true,
		onChange: function(element, checked) { }
	});
});
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="storageForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr>
							<td colspan="2" style="text-align:left;">选择业务系统所在的交换机及连接的存储系统</td>
						</tr>
						<tr>
							<td style="text-align:right;">交换机:</td>
							<td>
								<select id="swStoSelect01" multiple="multiple" name="swStoSelect01"></select>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;">存储系统:</td>
							<td>
								<select id="stoSwSelect02" name="stoSwSelect02"></select>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align:center;">
								<button id="addSwStoBtn" onclick="swSto.addRelation();" type="button" title="确认添加交换机与存储系统的连接关系" class="btn btn-default primary">确定</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的交换机与存储系统连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listSwStoPanel327" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th >起点交换机</th>
											<th>终点交换机</th>
											<th>存储系统</th>
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
