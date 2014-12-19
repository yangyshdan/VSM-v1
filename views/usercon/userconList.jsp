<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script src="${path }/resource/js/project/storage.js"></script>
<script src="${path }/resource/js/project/computer.js"></script>
<script src="${path }/resource/js/project/topn.js"></script>
<script type="text/javascript">
//刷新
function doFreshen(){
	var jsonVal={};
	var args=$("#conditionForm").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=HMCPage",jsonVal,$("#loadcontent"));
}
function doFreshen2(){
	var jsonVal={};
	var args=$("#conditionForm2").serializeArray();
	$.each(args,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/usercon/UserConAction?func=VIOSPage",jsonVal,$("#vioscontent"));
}

function del(id){
	var button = [{func:"hmcDel("+id+")",text:'确定'},{func:'doCancle()',text:'取消'}];
	bAlert("是否删除该记录 ?","操作提示",button);
}
function del2(id){
	var button = [{func:"viosDel("+id+")",text:'确定'},{func:'doCancle()',text:'取消'}];
	bAlert("是否删除该记录 ?","操作提示",button);
}

function hmcDel(id){
	$.ajax({
		url:'${path}/servlet/usercon/UserConAction?func=hmcDel',
		data:{id:id},
		success:function(data){
			doCancle();
			doFreshen();
		}
	});
}

function doListRefresh(){
	doFreshen2();
	//location.href = "${path}/servlet/usercon/UserConAction";
}

function viosDel(id){
	$.ajax({
		url:'${path}/servlet/usercon/UserConAction?func=viosDel',
		data:{id:id},
		success:function(data){
			doCancle();
			doFreshen2();
		}
	});
}

function saveHMC(jsonVal){
	$.ajax({
		url:"${path}/servlet/usercon/UserConAction?func=saveInfo",
		data:jsonVal,
		success:function(data){
			location.href="${path}/servlet/usercon/UserConAction";
		}
	});
}

$(function(){
	$("#storageTable td").addClass("rc-td");
});
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#hmcTab">HMC配置</a>
		</li>
		<li class="">
			<a href="#viosTab">虚机配置</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<!-- 开始 -->
	<div class="tab-pane active" id="hmcTab">
		<div class="row-fluid">
			<div class="box span10">
				<div class="box-header well">
					<h2>
						HMC用户配置列表
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=editHMCInfo',500,400,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="刷新" onclick="doFreshen();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
					<div id="conAlert" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
						<div class="arrow"></div>
						<div class="popover-inner">
							<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlert()">×</a></h3>
							<div class="popover-content" style="padding: 8px;">
						        <form class="form-horizontal" id="conditionForm">
									<fieldset>
									  <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="ipaddress" style="width:80px">IP地址</label>
						                  <input type="text" class="form-control" id="ipaddress" name="ipaddress" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="state" style="width:80px">状态</label>
						                  <select class="form-control" name="hmc_state" id="hmc_state" style="width: 140px;margin-left: 20px;">
							                  	<option value='' selected="selected">请选择</option>
							                  	<option value='1'>可用</option>
							                  	<option value='0'>不可用</option>
							              </select>
						              </div> 
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="hmcFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane active" id="loadcontent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>
										IP地址
									</th>
									<th>
										用户名
									</th>
									<th>
										状态
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty hmcPage.data}">
										<c:forEach var="item" items="${hmcPage.data}" varStatus="status">
											<tr>
												<td>
													${item.ip_address}
												</td>
												<td>
													${item.user}
												</td>
												<td>
													<c:if test="${item.state==1}">
														<i class="icon icon-color icon-check"></i>可用
													</c:if>
													<c:if test="${item.state==0}">
														<i class="icon icon-color icon-close"></i>不可用
													</c:if>
												</td>
												<td>
													<a class="btn btn-info" data-rel='tooltip' href="javascript:MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=editHMCInfo&id=${item.id}',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del(${item.id})"><i class="icon-trash icon-white"></i>删除</a>
												</td>
											</tr>											
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan=4>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
						</tbody>
						</table>
						<div id="hmcpageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty hmcPage.data}">
							<script>
								var param = $("#conditionForm").serialize();
								$("#hmcpageNub").getLinkStr({pagecount:"${hmcPage.totalPages}",curpage:"${hmcPage.currentPage}",numPerPage:"${hmcPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=HMCPage&"+param,divId:'loadcontent'});
							</script>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 结束 -->
	<!-- 开始 -->
	<div class="tab-pane" id="viosTab"> 
		<div class="row-fluid">
			<div class="box span12">
				<div class="box-header well">
					<h2 id="pTitle">
						虚机用户配置信息
					</h2>
					<div class="box-icon">
						<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlertAll('viosIframe','viosFrom')" data-rel="tooltip" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
						<a href="javascript:void(0)" class="btn btn-round" data-rel="tooltip" title="添加" onclick="MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=editVIOSInfo',500,400,0);" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
						<a href="javascript:void(0)" class="btn btn-round btn-round" data-rel="tooltip" title="刷新" onclick="doFreshen2();" data-rel="tooltip"><i class="icon icon-color icon-refresh"></i></a>
						<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					</div>
				</div>
				<div class="box-content">
					<iframe id="viosIframe" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
					<div id="viosFrom" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
						<div class="arrow"></div>
						<div class="popover-inner">
							<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="Public.conAlert()">×</a></h3>
							<div class="popover-content" style="padding: 8px;">
						        <form class="form-horizontal" id="conditionForm2">
									<fieldset>
									  <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="vname" style="width:80px">虚拟机名称</label>
						                  <input type="text" class="form-control" id="vname" name="vname" style="width: 140px;margin-left: 20px;">
						              </div>
						              <div class="control-group" style="margin-bottom: 10px;">
						                  <label class="col-lg-2 control-label" for="state" style="width:80px">状态</label>
						                  <select  class="form-control" name="vios_state" id="vios_state" style="width: 140px;margin-left: 20px;">
						                  	<option value='' selected="selected">请选择</option>
						                  	<option value='1'>可用</option>
						                  	<option value='0'>不可用</option>
						                  </select>
						              </div> 
						              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
										<button type="button" class="btn btn-primary" onclick="hypervisorFilter();">查询</button>
										<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									  </div>
						           	</fieldset>
						          </form>
							</div>
						</div>
					</div>
					<div class="tab-pane" id="vioscontent" style="text-align: center;overflow: visible;">
						<table id="conTable" class="table table-bordered table-striped table-condensed"  style="word-break:break-all">
							<thead>
								<tr>
									<th>
										物理机
									</th>
									<th>
										虚拟机
									</th>
									<th>
										用户名
									</th>
									<th>
										状态
									</th>
									<th>
										操作
									</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty viosPage.data}">
										<c:forEach var="item" items="${viosPage.data}" varStatus="status">
											<tr>
												<td>
													${item.c_name}
												</td>
												<td>
													${item.v_name}
												</td>
												<td>
													${item.user}
												</td>
												<td>
													<c:if test="${item.state==1}">
														<i class="icon icon-color icon-check"></i>可用
													</c:if>
													<c:if test="${item.state==0}">
														<i class="icon icon-color icon-close"></i>不可用
													</c:if>
												</td>
												<td>
													<a class="btn btn-success" data-rel='tooltip' href="javascript:MM_openwin3('添加','${path}/servlet/usercon/UserConAction?func=editVIOSInfo&id=${item.id}',500,400,0)" title="edit"><i class="icon-zoom-in icon-white"></i>编辑</a>
													<a class="btn btn-danger" data-rel='tooltip' href="javascript:void(0)" title="delete" onclick="del2(${item.id})"><i class="icon-trash icon-white"></i>删除</a>
												</td>
											</tr>											
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan=5>
												暂无数据！
											</td>
										</tr>
									</c:otherwise>
								</c:choose>
						</tbody>
						</table>
						<div id="viospageNub" class="pagination pagination-centered"></div>
						<c:if test="${not empty viosPage.data}">
							<script>
								var param = $("#conditionForm2").serialize();
								$("#viospageNub").getLinkStr({pagecount:"${viosPage.totalPages}",curpage:"${viosPage.currentPage}",numPerPage:"${viosPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/usercon/UserConAction?func=VIOSPage&"+param,divId:'vioscontent'});
							</script>
						</c:if>
					</div> 
				</div>
			</div>
		</div>
	</div>
	<!-- 结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>