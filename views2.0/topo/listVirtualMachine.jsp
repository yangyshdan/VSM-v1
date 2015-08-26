<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<script type="text/javascript">
	$(function(){
		$("#virtualSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择虚拟机",
			nSelectedText: "台虚拟机被选中",
			disableIfEmpty: true,
			maxHeight: 200,
			numberDisplayed: 1,
			enableClickableOptGroups: true,
			onChange: function(element, checked) {
				if(checked){
					var $tbody = $("#listPhyVMPanel tbody");
			      	var $optgroup = element.parent();
			      	var phyId = $optgroup.attr("value");
			      	var phyName = $optgroup.attr("label").replace("物理机:", "");
			      	var vmId = element.val();
			      	virMachine.addMap($tbody, phyId, vmId, phyName, element.text());
					element.attr({disabled: true});
					$("#virtualSelect").multiselect("refresh");
				}
		    }
		});
	});
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="virtualForm2816">
			<fieldset>
				<table class="table">
					<tbody>
						<tr><td colspan="2">选择部署该业务系统的虚拟机(可选)</td></tr>
						<tr>
							<td style="text-align:right;">虚拟机:</td>
							<td>
								<select id="virtualSelect" multiple="multiple" name="virtualSelect"></select>
							</td>
						</tr>
						<tr>
							<td colspan="2">在这里，查看您所添加的物理机与虚拟机连接</td>
						</tr>
						<tr>
							<td colspan="2">
								<table id="listPhyVMPanel" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>物理机</th>
											<th>虚拟机</th>
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
