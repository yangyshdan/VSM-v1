<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script src="${path}/resource/js/My97DatePicker/WdatePicker.js"></script>
<script src="${path }/resource/js/project/storage.js"></script>
<script src="${path }/resource/js/project/publicscript.js"></script>
<div class="row-fluid sortable">
	<div class="box-content">
		<table style="width: 100%;  ">
			<thead>
				<tr><th style="width:50%;">后端(TPC)名称</th><th>前端显示名称</th></tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${not empty storageList}">
						<c:forEach var="item" items="${storageList}" varStatus="status">
							<tr>
								<td style="white-space:normal;word-break:break-all; word-wrap:break-word;">${item.the_display_name}</td>
								<td><input type="text" class="changeName" id="${item.subsystem_id }" style="width:99%;margin-bottom:0px;" value="${item.the_backend_name }"></td>
							</tr>
						</c:forEach>
							<tr>
								<td colspan="2" align="center">
									<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
									<button class="btn" type="reset" onclick="resetFunc()">重置</button>
								</td>
							</tr>
					</c:when>
				</c:choose>
			</tbody>
		</table>
	</div>
</div>
<script>




function saveInfo(){
var jsonVal = "[";
	
	for(var i = 0,divs = $(".changeName");i < divs.length;i++){
		var dname = $(divs.get(i)).val();
		jsonVal += "{name:'"+dname+"',id:"+$(divs.get(i)).attr("id")+"}";
		if(i<divs.length-1){
			jsonVal += ",";
		}
	}
	jsonVal += "]";
	
	$.ajax({
		url:"${path}/servlet/storage/StorageAction?func=StorageRename",
		data:{"targets":encodeURI(jsonVal)},
		success:function(result){
			if(result=="true"){
				parent.window.bAlert("操作成功！","",[{func:"parent.window.doFreshen1();",text:"确定"}]);
			}else{
				parent.window.bAlert("操作失败，请稍候再试！");
			}
		}
	}); 
}

/**
 判断是否为数字
 **/
function isNumeric(strValue) {
	if (isEmpty(strValue))
		return true;
	return executeExp(/^\d*$/g, strValue);
}

/**
 执行正则表达式
 **/
function executeExp(re, s) {
	return re.test(s);
}
</script>
<%@include file="/WEB-INF/views/include/footer.jsp"%>