<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/accountMgr.js"></script>
<script type="text/javascript">
function userFilter(){
	
	var userName=$("input[name='userName']").val();
	if(isEmpty(userName)){

		alert('请输入用户名');
		
		return false;
	}

	loadData("${path}/servlet/usercon/AccountMgrAction?func=AjaxFilter",{userName:userName},$("#accountContent"));
}
function chooseUser(){
	var userChooses = $("input[name='userChoose']:checked");
	
	if(userChooses.length < 1){
		alert('请选择用户');
		return false;
	}
	if(userChooses.length>1){
		alert('请选择一位用户');
		return false;
	}
	
	accountMgr.editPwd(userChooses.val());
}
</script>
<style>
.spetable td{
	 text-overflow:ellipsis;overflow:hidden;white-space: nowrap;
}
</style>
<div id="content">
	<!-- 
	<div class="well" style="height:260px;">
		<div id="shareChart" style="width: 30.3%;height:98%;float:left;"></div>
		<div id="allShareCon" style="width: 68.2%;float:left;height:98%;margin-right:0.5%;"></div>
	</div>
 -->
<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12" >
			<div class="box-header well">
				<h2>
					帐号列表
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="accountMgr.conAlertAll('conAlert1','conAlert')" ><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="添加" onclick="accountMgr.add();" data-rel="tooltip"><i class="icon icon-color icon-edit"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="修改密码" onclick="chooseUser();" data-rel="tooltip"><i class="icon icon-color icon-key"></i></a>
					<!-- 
					<a href="javascript:void(0);" class="btn btn-round" title="删除" onclick=""><i class="icon icon-color icon-trash"></i> </a>
					 -->
					<a href="javascript:void(0);" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i> </a>
				</div>
			</div>
			<iframe id="conAlert1" style="z-index:1;right:20px;margin-top:10px;display:none;position:absolute;" src="javascript:false" frameborder="0"></iframe>
				<div id="conAlert" class="" style="right:20px;margin-top:10px;display:none;position:absolute;z-index:2">
					<div class="arrow"></div>
					<div class="popover-inner">
						<h3 class="popover-title">过滤器<a class='btn btn-round close' title='关闭' onclick="accountMgr.conAlertAll('conAlert1','conAlert')">×</a></h3>
						<div class="popover-content" style="padding: 8px;">
					        <form class="form-horizontal" id="conditionForm">
								<fieldset>
								 <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="userName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="userName" name="userName" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button type="button" class="btn btn-primary" onclick="userFilter();">查询</button>
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
			<div class="box-content"  style="overflow:auto;width:98%;min-height:180px;" id="accountContent">
				<table class="table table-bordered table-striped table-condensed spetable" style="table-layout:fixed;">
					<thead>
						<tr>
							<th  style="width: 10px;">
							</th>
							<th style="width: 130px;">
								用户名
							</th>
							<th style="width: 130px;">
								所属角色
							</th>
							<th  style="width: 55px;">
								操作
							</th>
							
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty dbPage.data}">
								<c:forEach var="item" items="${dbPage.data}" varStatus="status">
									<tr>
									<!-- -->
										<td>
											<label class="checkbox inline">
												<input type="checkbox" value="${item.fid}"  name="userChoose">
										    </label>
										</td>
										 
										<td>
											${item.floginname }
										</td>
										<td>
											${item.fname }
										</td>
										<td>
											<a class="btn btn-info" href="javascript:void(0)" title="edit" onclick="accountMgr.edit(${item.fid})"><i class="icon-edit icon-white"></i>编辑</a>
											<a class="btn btn-danger" href="javascript:void(0)" title="delete" onclick="accountMgr.delPrepare1('${item.fid}')"><i class="icon-trash icon-white"></i>删除</a>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan=9>
										暂无数据！
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				
				<div class="pagination pagination-centered">
					<ul id="alertListNub"></ul>
				</div>
				<c:if test="${not empty dbPage.data}">
					<script>
						$("#alertListNub").getLinkStr({pagecount:"${dbPage.totalPages}",curpage:"${dbPage.currentPage}",numPerPage:"${dbPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/alert/DeviceAlertAction?func=AjaxPage",divId:'dAlertContent'});
					</script>
				</c:if>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>