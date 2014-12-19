<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>

<div class="row-fluid sortable">
					<div class="box-content">
						<form class="form-horizontal">
							<fieldset>
							  <div class="control-group">
				                  <label class="control-label" for="name">发生时间</label>
				                  <div class="controls" style="margin-right: 80px;">
								  <div class="input-prepend input-append">
				                  ${dAlert.ftime}
				                  </div>
				                  </div>
				              </div>
				              <div class="control-group">
				                  <label class="control-label" for="devicetype">级别</label>
				                  <div class="controls">
								  <div class="input-prepend input-append">
			                      <c:choose>
									<c:when test="${dAlert.flevel == 0}"><span class="label">Info</span> </c:when>
									<c:when test="${dAlert.flevel == 1}"><span class="label label-warning">Warning</span> </c:when>
									<c:when test="${dAlert.flevel == 2}"><span class="label label-important">Critical</span> </c:when>
								</c:choose>
			                      </div>
								</div>
				              </div>
				              
							  <div class="control-group">
								<label class="control-label" for="device">事件源类型</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									${dAlert.fresourcetype}
							      </div>
							      </div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="prfField">事件源</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									${dAlert.fresourcename}
							     </div>
							     </div>
							  </div>
							  
							  <div class="control-group">
								<label class="control-label" for="daterange">消息</label>
								<div class="controls">
								  <div class="input-prepend input-append">
									<textarea class="form-control" id="textArea"  rows="6">${dAlert.fmessage}</textarea>
							     </div>
							     </div>
							  </div>
							</fieldset>
						</form>
					</div>
			</div><!--/row-->
	<%@include file="/WEB-INF/views/include/footer.jsp"%>