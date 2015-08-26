<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<script type="text/javascript">
	$(function(){
		$("#physicalSelect").multiselect({
			enableFiltering: 1,
			nonSelectedText: "未选择物理机",
			nSelectedText: "台物理机被选中",
			disableIfEmpty: true,
			maxHeight: 250,
			onChange: function($ele, checked) {
				if(checked){
					var $tbody = $("#listAppPhyPanel tbody");
			      	phyMachine.addMap($tbody, $ele.val(), $("#appForm2816 input[name='appName']").val(), $ele.text());
					$ele.attr({disabled: true});
					$ele.parent().multiselect("refresh");
				}
		    }
		});
	});
</script>
<div class="row-fluid" style="overflow:auto;height:445px;">
	<div class="box-content">
		<form class="form-horizontal" id="physicalForm2816">
			<fieldset>
				<table class="table" style="">
					<tbody>
						<tr><td colspan="2">选择部署该业务系统的物理机</td></tr>
						<tr>
							<td style="text-align:right;">物理机:</td>
							<td>
								<select id="physicalSelect" multiple="multiple" name="physicalSelect" style="width:360px;">
									<c:choose>
										<c:when test="${not empty hypData}">
											<c:forEach var="item" items="${hypData}">
												<option value="${item.hyp_id}">${item.hyp_name}</option>
											</c:forEach>
										</c:when>
									</c:choose>
								</select>
							</td>
						</tr>
						<tr><td colspan="2">在这里，查看您所添加的业务系统与物理机</td></tr>
						<tr>
							<td colspan="2">
								<table id="listAppPhyPanel" 
									class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
									<thead>
										<tr>
											<th>业务系统</th>
											<th>物理机</th>
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
