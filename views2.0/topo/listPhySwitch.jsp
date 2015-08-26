<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
	$(function(){
		$("#phy2SwitchSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择物理机",
			nSelectedText: "台物理机被选中",
			maxHeight: 250,
			onChange: function(element, checked) {
				element.attr({selected: true}).siblings().attr({selected: false});
			}
		});
		$("#switch2PhySelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择交换机",
			nSelectedText: "台交换机被选中",
			maxHeight: 250,
			onChange: function(element, checked) {
				element.attr({selected: true}).siblings().attr({selected: false});
			}
		});
	});
	
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="phyportForm2816">
			<fieldset>
				<table class="table" id="addPhyportTable" style="">
					<tbody>
						<tr class="addPhyport">
							<td style="text-align:right;">物理机:</td>
							<td>
								<select id="phy2SwitchSelect" name="phy2SwitchSelect"></select>
							</td>
						</tr>
						<tr class="addPhyport">
							<td style="text-align:right;">交换机:</td>
							<td>
								<select id="switch2PhySelect" name="switch2PhySelect"></select>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align:center;">
								<button onclick="phySw.addInfo();" type="button" class="btn btn-default primary">确定</button>
							</td>
						</tr>
					</tbody>
				</table>
				<table class="table" id="selectPhySw">
					<tbody>
						<tr><td colspan="3">在这里，查看您所添加的物理机与交换机连接</td></tr>
						<tr>
							<td colspan="3">
								<table id="listPhySwPanel" class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead><tr><th>物理机</th><th>交换机</th><th class="delHeader">操作</th></tr></thead>
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
