<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<table class="table table-bordered table-striped table-condensed">
					<thead>
						<tr>
							<th>名称</th>
							<th>应用描述</th>
							<th>服务器</th>
							<th>交换机</th>
							<th>储存系统</th>
							<th>储存池</th>
							<th>储存卷</th>
							<th>操作</th>
						</tr>
					</thead>
					<%-- --%>
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
													   <span class="badge ">${item.count.Physical + item.count.Virtual}</span>服务器<span class="caret"></span>
													  </button>
													  <ul class="dropdown-menu" role="menu">
													  	<c:choose>
													  		<c:when test="${not empty item.asso.Virtual}">
																<c:forEach var="v" items="${item.asso.Virtual}" varStatus="status">
																	<li><a href="${path}/servlet/virtual/VirtualAction?func=VirtualInfo&computerId=${v.value.comp_id}&hypervisorId=${v.value.hyp_id}&vmId=${v.value.vm_id}">${v.value.vm_name}</a></li>
																</c:forEach>
															</c:when>
														</c:choose>
														<c:choose>
															<c:when test="${not empty item.asso.Physical}">
																<c:forEach var="h" items="${item.asso.Physical}" varStatus="status">
																	<li><a href="${path}/servlet/hypervisor/HypervisorAction?func=HypervisorInfo&computerId=${h.value.comp_id}&hypervisorId=${h.value.hyp_id}">${h.value.hyp_name}</a></li>
																</c:forEach>
															</c:when>
														</c:choose>
													  </ul>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													   <span class="badge ">${item.count.Switch}</span>交换机<span class="caret"></span>
													  </button>
													  <ul class="dropdown-menu" role="menu">
													  	<c:choose>
													  		<c:when test="${not empty item.asso.Switch}">
																<c:forEach var="s" items="${item.asso.Switch}" varStatus="status">
																	<li><a href="${path}/servlet/switchs/SwitchAction?func=SwitchInfo&switchId=${s.value.sw_id}">${s.value.sw_name}</a></li>
																</c:forEach>
															</c:when>
														</c:choose>
													  </ul>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													    <span class="badge">${item.count.Storage}</span>存储系统<span class="caret"></span>
													  </button>
													  <ul class="dropdown-menu" role="menu">
													    <c:choose>
													  		<c:when test="${not empty item.asso.Storage}">
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
															</c:when>
														</c:choose>
													  </ul>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													    <span class="badge">${item.count.Pool}</span>存储池<span class="caret"></span>
													  </button>
													  <ul class="dropdown-menu" role="menu">
													    <c:choose>
													  		<c:when test="${not empty item.asso.Pool}">
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
															</c:when>
														</c:choose>
													  </ul>
													</div>
												</td>
												<td>
													<div class="btn-group">
													  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
													   <span class="badge">${item.count.Volume}</span> 存储卷<span class="caret"></span>
													  </button>
													  <ul class="dropdown-menu" role="menu">
													    <c:choose>
													  		<c:when test="${not empty item.asso.Volume}">
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
															</c:when>
														</c:choose>
													  </ul>
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