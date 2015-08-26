<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path}/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/apps.js"></script>
<script src="${path}/resource/js/project/util.js"></script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>

<div id="content">
<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					应用列表
				</h2>
				<div class="box-icon">
					<%--<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="过滤" onclick="Public.conAlert();" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>--%>
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="新增" onclick="App.addApp21722();"><i class="icon icon-color icon-add"></i> </a>
					<a href="javascript:void(0);" class="btn btn-round"  data-rel="tooltip" data-original-title="刷新" onclick="App.dataFilter();"><i class="icon icon-color icon-refresh"></i> </a>
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<div id="conAlert1" class="" style="position:absolute;right:20px;margin-top:10px;display:none;">
				<div class="arrow"></div>
				<div class="popover-inner">
					<h3 class="popover-title">过滤器</h3>
					<div class="popover-content" style="padding: 8px;">
				        <form class="form-horizontal">
							<fieldset>
				               <div class="control-group" style="margin-bottom: 10px;">
				                  <label class="col-lg-2 control-label" for="f_state" style="width:80px">名称</label>
				                  <input type="text" />
				              </div>
				              
				              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
								<button class="btn" type="reset">重置</button>
								<input type="button" onclick="App.dataFilter();" class="btn btn-primary" value="查询 "/>
							  </div>
				           	</fieldset>
				          </form>
					</div>
				</div>
			</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;height:600px;" id="appContent">
				<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>名称</th><th>应用描述</th>
							<th>服务器</th><th>交换机</th>
							<th>储存系统</th><th>储存池</th>
							<th>储存卷</th><th>操作</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr>
									<%--<a href="${path}/servlet/apps/AppsAction?func=AppsInfo&fappid=${item.fid}"> </a>--%>
										<td><a href="${path}/servlet/topo/TopoAction?func=HuimingTopo&appId=${item.id}" style="cursor:pointer;">${item.name}</a></td>
										<td>${item.description}</td>
										<c:choose>
											<c:when test="${not empty item.asso}">
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													  	<c:set value="${(not empty item.count.Physical)? item.count.Physical : 0}" var="phyCount"></c:set>
													  	<c:set value="${(not empty item.count.Virtual)? item.count.Virtual : 0}" var="virCount"></c:set>
													   <span class="badge ">${phyCount + virCount}</span> 服务器<span class="caret"></span>
													  </button>
													  <c:if test="${phyCount + virCount > 0}">
													  	<ul class="dropdown-menu" role="menu">
														  	<c:choose>
														  		<c:when test="${virCount > 0}">
																	<c:forEach var="v" items="${item.asso.Virtual}" varStatus="status">
																		<li><a href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&computerId=${v.value.comp_id}&hypervisorId=${v.value.hyp_id}&vmId=${v.value.vm_id}">${v.value.vm_name}</a></li>
																	</c:forEach>
																</c:when>
															</c:choose>
															<c:choose>
																<c:when test="${phyCount > 0}">
																	<c:forEach var="h" items="${item.asso.Physical}" varStatus="status">
																		<li><a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&computerId=${h.value.comp_id}&hypervisorId=${h.value.hyp_id}">${h.value.hyp_name}</a></li>
																	</c:forEach>
																</c:when>
															</c:choose>
														  </ul>
													  </c:if>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													    <span class="badge">${(not empty item.count.Switch)? item.count.Switch : 0}</span>交换机<span class="caret"></span>
													  </button>
													  <c:choose>
													  		<c:when test="${not empty item.asso.Switch}">
													  			<ul class="dropdown-menu" role="menu">
																	<c:forEach var="s" items="${item.asso.Switch}" varStatus="status">
																		<li><a href="${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=${s.value.sw_id}">${s.value.sw_name}</a></li>
																	</c:forEach>
																</ul>
															</c:when>
													  </c:choose>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													    <span class="badge">${empty item.count.Storage? 0 : item.count.Storage}</span>存储系统<span class="caret"></span>
													  </button>
													    <c:choose>
													  		<c:when test="${not empty item.asso.Storage}">
													  			<ul class="dropdown-menu" role="menu">
																	<c:forEach var="s" items="${item.asso.Storage}" varStatus="status">
																		<c:choose>
														  					<c:when test="${s.value.db_type=='TPC'}">
																				<li><a href="${path}/servlet/storage/StorageAction?func=StorageInfo&subSystemID=${s.value.sto_id}">${s.value.sto_name}</a></li>
																			</c:when>
																			<c:otherwise>
																				<li><a href="${path}/servlet/sr/storagesystem/StorageAction?func=StorageInfo&subSystemID=${s.value.sto_id}">${s.value.sto_name}</a></li>
																			</c:otherwise>
																		</c:choose>
																	</c:forEach>
																</ul>
															</c:when>
														</c:choose>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													    <span class="badge">${empty item.count.Pool? 0 : item.count.Pool}</span>存储池<span class="caret"></span>
													  </button>
													    <c:choose>
													  		<c:when test="${not empty item.asso.Pool}">
													  			<ul class="dropdown-menu" role="menu">
																<c:forEach var="s" items="${item.asso.Pool}" varStatus="status">
																	<c:choose>
													  					<c:when test="${s.value.db_type=='TPC'}">
																			<li><a href="${path}/servlet/pool/PoolAction?func=PoolInfo&poolId=${s.value.pool_id}&subSystemID=${s.value.sto_id}">${s.value.pool_name}</a></li>
																		</c:when>
																		<c:otherwise>
																			<li><a href="${path}/servlet/sr/pool/PoolAction?func=PoolInfo&poolId=${s.value.pool_id}&subSystemID=${s.value.sto_id}&storageType=${s.value.os_type}">${s.value.pool_name}</a></li>
																		</c:otherwise>
																	</c:choose>
																</c:forEach>
																</ul>
															</c:when>
														</c:choose>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													   <span class="badge">${empty item.count.Volume? 0 : item.count.Volume}</span> 存储卷<span class="caret"></span>
													  </button>
													    <c:choose>
													  		<c:when test="${not empty item.asso.Volume}">
													  			<ul class="dropdown-menu" role="menu">
																	<c:forEach var="s" items="${item.asso.Volume}" varStatus="status">
																		<c:choose>
														  					<c:when test="${s.value.db_type=='TPC'}">
																				<li><a href="${path}/servlet/volume/VolumeAction?func=PerVolumeInfo&svid=${s.value.vol_id}&subSystemID=${s.value.sto_id}">${s.value.vol_name}</a></li>
																			</c:when>
																			<c:otherwise>
																				<li><a href="${path}/servlet/sr/volume/VolumeAction?func=LoadVolumeInfo&subsystemId=${s.value.sto_id}&volumeId=${s.value.vol_id}&storageType=${s.value.os_type}">${s.value.vol_name}</a></li>
																			</c:otherwise>
																		</c:choose>
																	</c:forEach>
																</ul>
															</c:when>
														</c:choose>
													</div>
												</td>
											</c:when>
											<c:otherwise>
												<td>无</td>
												<td>无</td>
												<td>无</td>
												<td>无</td>
												<td>无</td>
											</c:otherwise>
										</c:choose>
										<td>
											<a class="btn btn-info" href="javascript:void(0)" title="edit" onclick="App.editApp('${item.id}')"><i class="icon-edit icon-white"></i>编辑</a>
											<a class="btn btn-danger" href="javascript:void(0)" title="delete" onclick="App.doAppDel('${item.id}')"><i class="icon-trash icon-white"></i>删除</a>
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
						
						<div class="pagination pagination-centered">
							<ul id="appListNub"></ul>
						</div>
						<c:if test="${not empty dbPage.data}">
							<script>
								$("#appListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/apps/AppsAction?func=AjaxPage",divId:'appContent'});
							</script>
						</c:if>
						
					<script type="text/javascript">
						$(function(){
							$('[rel="popover"],[data-rel="popover"]').popover();
						});
					</script>
					
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>