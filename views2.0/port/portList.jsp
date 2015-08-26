<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path}/resource/js/project/port.js"></script>
<script src="${path}/resource/js/project/extent.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<script type="text/javascript">
var subSystemID = "${subSystemID}";
$(function () {
	var names = ${names};
	var args = ${array};
	Port.speed(names,args);
	doListRefresh();
});
function portDbClick(id){
    window.location = "${path}/servlet/port/PortAction?func=PortInfo&subSystemID=${subSystemID}&portId="+id;
}
//数据提交
function portFilter(){
	var startPort = $("input[name='startPort']").val();
	var endPort = $("input[name='endPort']").val();
	var res = /^\d*$/;
	if(!res.test(startPort)){
		bAlert("请输入有效端口号");
		return false;
	}
	if(!res.test(endPort)){
		bAlert("请输入有效端口号");
		return false;
	}
	if(endPort!=0 && startPort>=endPort){
		bAlert("请输入有效端口区间");
		return false;
	}
	var jsonVal = $("#conditionForm").getValue();
	jsonVal["subSystemID"] = subSystemID;
	loadData("${path}/servlet/port/PortAction?func=AjaxPortPage",jsonVal,$("#portContent"));
}
//刷新
function doFreshen(){
	var jsonVal = {};
	jsonVal["subSystemID"] = subSystemID;
	var serVal = $("#portHiddenForm").serializeArray();
	$.each(serVal,function(){
		jsonVal[this.name] = this.value;
	});
	loadData("${path}/servlet/port/PortAction?func=AjaxPortPage",jsonVal,$("#portContent"));
}
//重置
function clearData(){
	$("button[type='reset']").click();
}
$(clearData);
function doListRefresh(){
	loadData("${path}/servlet/port/PortAction?func=PortPrfField",{isFreshen:1,subSystemID:subSystemID},$("#prfContent"),false,false,false,true,
		function(data){
			var json = eval("("+data+")");
			Public.drawPrfLine("prfContent",json);
			$("#pTitle").html(function(){
				var str="端口性能  (";
				$.each(json.kpiInfo,function(i){
					str+=json.kpiInfo[i].ftitle;
					str+="("+json.kpiInfo[i].funits+")";
					if(i<json.kpiInfo.length-1){
						str+=",";
					}
				});
				if(str.length>100){
					str = str.substring(0,100)+'...';
				}
				str+=")";
				return str;
			});
		});
}
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
<div id="content">
	<ul class="nav nav-tabs" id="myTab">
		<li class="active">
			<a href="#dataTab">数据列表</a>
		</li>
		<li class="">
			<a href="#prfTab">性能曲线</a>
		</li>
	</ul>
	<div id="myTabContent" class="tab-content">
	<div class="tab-pane active" id="dataTab">
	<!-- 端口速率开始 -->

	<div class="well">
		<div style="width: 10%; float: left;height:180px;line-height:200px;" >
			<img src="${path}/resource/img/project/storageport.png"/>
		</div>
		<div id="container" style="width: 85%; height: 200px; margin: 0 0 0 30px; float: left;"></div>
		<div style="clear: both;"></div>
	</div>
	<!-- 端口速率结束 -->
	<!-- 列表开始 -->
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2>
					存储系统端口
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0);" class="btn btn-round" title="过滤" onclick="Public.conAlert()" data-rel='tooltip'><i class="" style="margin-top:3px;display:inline-block;width:16px;height:16px;background-repeat:no-repeat;background-image: url('${path}/resource/img/project/filter16.png')"></i> </a>
					<a href="javascript:void(0)" class="btn btn-round" title="刷新" onclick="doFreshen();" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-round" title="导出" id="exportCSV" data-rel='tooltip'><i class="icon-download-alt"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
					<script>
						var exurl = "${path}/servlet/port/PortAction?func=exportPortConfigData&subSystemID=${subSystemID}";
						$("#exportCSV").attr("href",exurl);
					</script>
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
					                  <label class="col-lg-2 control-label" for="portName" style="width:80px">名称</label>
					                  <input type="text" class="form-control" id="portName" name="portName" style="width: 140px;margin-left: 20px;">
					              </div>
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="portType" style="width:80px">端口类型</label>
					                  <input type="text" class="form-control" id="portType" name="portType" style="width: 140px;margin-left: 20px;">
					              </div> 
					              <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="startPort" style="width:80px">端口号</label>
					                  <input class="form-control" id="startPort" name="startPort" type="text" style="width:60px;margin-left: 20px;"> -
					                  <input class="form-control" id="endPort" name="endPort" type="text" style="width:60px;">
					              </div>
					               <div class="control-group" style="margin-bottom: 10px;">
					                  <label class="col-lg-2 control-label" for="status" style="width:80px">操作状态</label>
					                  <input class="form-control" id="status" name="status" type="text" style="width:140px;margin-left: 20px;">
					              </div>
					              <div class="form-actions" style="padding-left: 80px; margin-top: 10px; margin-bottom: 0px; padding-bottom: 5px; padding-top: 5px;">
									<button class="btn" type="reset">重置</button>&nbsp;&nbsp;
									<button type="button" class="btn btn-primary" onclick="portFilter();">查询</button>
								  </div>
					           	</fieldset>
					          </form>
						</div>
					</div>
				</div>
				<div id="portContent">
					<table class="table table-bordered table-striped table-condensed" id="conTable">
						<thead>
						<tr>
							<th>
								名称
							</th>
							<th>
								端口号
							</th>
							<th>
								端口类型
							</th>
							<th>
								操作状态
							</th>
							<th>
								硬件状态
							</th>
							<th>
								端口速率(M)
							</th>
						</tr>
					</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty portPage.data}">
									<c:forEach var="item" items="${portPage.data}" varStatus="status">
										<tr>
										<td>
											<a title="${item.the_display_name}" href="${path}/servlet/port/PortAction?func=PortInfo&portId=${item.port_id}&subSystemID=${subSystemID}">${item.the_display_name}</a>
										</td>
										<td>
											${item.port_number}
										</td>
										<td>
											${item.the_type}
										</td>
										<td>
											${item.the_operational_status}
										</td>
										<td>
											<cs:cstatus value="${item.the_consolidated_status}" />
										</td>
										<td>
											${item.the_port_speed}
										</td>
									</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan=7>
											暂无数据！
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
					<div id="portListpageNub" class="pagination pagination-centered"></div>
					<c:if test="${not empty portPage.data}">
						<script>
							var param = $("#conditionForm").serialize();
							$("#portListpageNub").getLinkStr({pagecount:"${portPage.totalPages}",curpage:"${portPage.currentPage}",numPerPage:"${portPage.numPerPage}",isShowJump:true,ajaxRequestPath:"${path}/servlet/port/PortAction?func=AjaxPortPage&subSystemID=${subSystemID}&"+param,divId:'portContent'});
						</script>
					</c:if>
					<c:if test="${empty portPage.data}">
						<script>
							$("#exportCSV").unbind();
							$("#exportCSV").attr("href","javascript:void(0);");
							$("#exportCSV").bind("click",function(){bAlert("暂无可导出数据！")});
						</script>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<!-- 列表结束 -->
	</div>
	<!-- 性能开始 -->
	<div class="tab-pane" id="prfTab">
	<div class="row-fluid">
		<div class="box span12">
			<div class="box-header well">
				<h2 id='pTitle'>
					端口性能
				</h2>
				<div class="box-icon">
					<a href="javascript:void(0)" class="btn btn-setting btn-round" title="设置" onclick="Port.settingPrf(${subSystemID},2,'')" data-rel='tooltip'><i class="icon-cog"></i></a>
					<a href="javascript:void(0)" class="btn btn-round btn-round" title="刷新" onclick="doListRefresh()" data-rel='tooltip'><i class="icon icon-color icon-refresh"></i></a>
					<a href="javascript:void(0)" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<div id="prfContent" style="width:90%;min-height:385px;"></div>
			</div>
		</div>
	</div>
	</div>
	<!-- 性能结束 -->
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>
