<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/pagePublic.jsp"%>
<legend style="margin-bottom: 5px;"><i class="icon32 icon-color icon-newwin"></i>排版设置 <input style="float:right;margin-right:20px;" type="button" onclick="saveLayout('${modelId}');" class="btn btn-primary" value="保存 "/></legend>
		
		<c:choose>
							<c:when test="${not empty layoutChart}">
								<c:forEach var="item" items="${layoutChart}" varStatus="status">
									<c:if test="${item.fisshow == 1}">
										<div class="box span${item.fsize}" id="layout_${item.fid}">
											<div class="box-header well">
											<h2>${item.fname}</h2>
											<div class="box-icon">
												<a href="#" class="btn btn-round"  data-rel="tooltip" title="放大" onclick="Chart.enlargeDiv('${item.fid}')"><i class="icon icon-color icon-zoomin"></i> </a>
												<a href="#" class="btn btn-round"  data-rel="tooltip" title="缩小" onclick="Chart.narrowDiv('${item.fid}')"><i class="icon icon-color icon-zoomout"></i> </a>
												<a href="#" class="btn btn-round" data-rel="tooltip" title="设置"  onclick="Chart.doChangeChart('${item.fid}')"> <i class="icon-cog"></i></a>
												<a href="#" data-rel="tooltip" title="隐藏" class="btn  btn-round" onclick="Chart.removeLayout('${item.fid}')"><i class="icon-remove"></i></a>
											</div>
										</div> 
										<div class="box-content"><div class="row-fluid"></div></div>
										</div>
									</c:if>
								</c:forEach>
							</c:when>
							<c:otherwise>
										
							</c:otherwise>
						</c:choose>
