<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c"%>
<%@taglib uri="/tags/cos-cstatus" prefix="cs"%>
<%@taglib uri="/tags/jstl-format" prefix="fmt"%>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/publicscript.js"></script>
<script src="${path}/resource/js/project/storage.js"></script>
<script src="${path}/resource/js/project/computer.js"></script>
<script src="${path}/resource/js/project/changeColumn.js"></script>
<script src="${path}/resource/js/project/topn.js"></script>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
//刷新
function doVirtPlatFilter(){
	var jsonVal={};
	var args=$("#conditionForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/virtualPlat/VirtualPlatAction?func=AjaxVirtPlatPage",jsonVal,$("#loadcontent"));
}
</script>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">设备列表</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 数据列表开始 -->
	<div class="tab-pane active" id="dataTab">
	<div class="row-fluid">
		<div class="box span10">
			<div class="box-header well">
				<h2>
					Hypervisor列表
				</h2>
				<div class="box-icon">
					<%--
					<a href="javascript:void(0)" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip"><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					 --%>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doVirtPlatFilter()" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<!-- 筛选条件开始 -->
			<div class="box-content" style="width:90%;;height:55px;margin:0px auto;">
				<form class="form-horizontal" id="conditionForm">
					<fieldset>
						<div class="control-group" style="margin-bottom: 10px;">
							<table class="table-condensed" width="60%" style="margin: 0px auto;">
								<tbody>
									<tr>
										<td>
											<label class="col-lg-2 control-label" for="name" style="width:60px">名称</label>
											<input type="text" class="form-control" id="name" name="name" style="width: 140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="type" style="width:60px">类型</label>
											<input type="text" class="form-control" id="type" name="type" style="width: 140px;margin-left: 10px;">
										</td>
										<td>
											<label class="col-lg-2 control-label" for="physicalName" style="width:100px">物理机名称</label>
											<input type="text" class="form-control" id="physicalName" name="physicalName" style="width: 140px;margin-left: 10px;">
										</td>
									</tr>
									<tr>
										<td colspan="3" style="text-align:center;">
											<button type="button" class="btn btn-primary" onclick="doVirtPlatFilter();">查询</button>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<button class="btn" type="reset">重置</button>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</fieldset>
				</form>
			</div>
			<!-- 筛选条件结束 -->
			
			<div class="box-content">
				<div class="tab-pane active" id="loadcontent" style="text-align:center;overflow:visible;">
					<table class="table table-bordered table-striped table-condensed colToggle" style="word-break:break-all">
						<thead>
							<tr>
								<th>
									名称
								</th>
								<th>
									类型
								</th>
								<th>
									版本
								</th>
								<th>
									允许最大CPU数量
								</th>
								<th>
									所在物理机
								</th>
								<th>
									虚拟机数量
								</th>
								<th>
									虚拟网络数量
								</th>
								<th>
									网络接口数量
								</th>
							</tr>
						</thead>
						<tbody>		
							<c:choose>
								<c:when test="${not empty virtPlatPage.data}">
									<c:forEach var="item" items="${virtPlatPage.data}" varStatus="status">		
										<tr>
											<td>
												<a href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatInfo&virtualPlatId=${item.id}&physicalId=${item.hypervisor_id}">${item.name}</a>
											</td>
											<td>
												${item.type}
											</td>
											<td>
												${item.version}
											</td>
											<td>
												${empty item.allow_maxcpu ? 'N/A' : item.allow_maxcpu}
											</td>
											<td>
												<a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&hypervisorId=${item.hypervisor_id}">${item.hypervisor_name}</a>
											</td>
											<td>
												<a href="${path}/servlet/virtualPlat/VirtualPlatAction?func=VirtualPlatInfo&virtualPlatId=${item.id}&physicalId=${item.hypervisor_id}&showVmTab=1">${item.vms_num}</a>
											</td>
											<td>
												${item.interfaces_num}
											</td>
											<td>
												${item.networks_num}
											</td>
										</tr>	
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan="8">暂无数据！</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="virtualPlatPageNub" class="pagination pagination-centered"></div>
					<script>
						var param = $("#conditionForm").serialize();
						$("#virtualPlatPageNub").getLinkStr({pagecount:"${virtPlatPage.totalPages}",curpage:"${virtPlatPage.currentPage}",numPerPage:"${virtPlatPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/virtualPlat/VirtualPlatAction?func=AjaxVirtPlatPage&"+param,divId:'loadcontent'});
					</script>
				</div>
			</div>
		</div>
	</div>
	</div>
	<!-- 数据列表结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>