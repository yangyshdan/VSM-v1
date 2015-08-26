<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/public.jsp"%>
<script type="text/javascript">
	$(function(){
		
	});
	saveInfo = function(){
		var $widthGap = $("#widthGap"),
		countError = 0,
		$heightGap = $("#heightGap"),
		widthGap = $.trim($widthGap.val()),
		heightGap = $.trim($heightGap.val());
		$widthGap.siblings("small.help-block").remove();
		if(widthGap == null || widthGap == undefined || widthGap.length == 0){
			++countError;
			$widthGap.parent().append($("<small>").addClass("help-block").css({color: "red"}).text("宽度差不能为空"));
		}
		var numWidthGap = parseInt(widthGap);
		if(isNaN(numWidthGap)){ // 如果不是数字
			++countError;
			$widthGap.parent().append($("<small>").addClass("help-block").css({color: "red"}).text("请正确填写宽度差"));
		}
		
		$heightGap.siblings("small.help-block").remove();
		if(heightGap == null || heightGap == undefined || heightGap.length == 0){
			++countError;
			$heightGap.parent().append($("<small>").addClass("help-block").css({color: "red"}).text("高度差不能为空"));
		}
		var numHeightGap = parseInt(heightGap);
		if(isNaN(numHeightGap)){ // 如果不是数字
			++countError;
			$heightGap.parent().append($("<small>").addClass("help-block").css({color: "red"}).text("请正确填写高度差"));
		}
		if(countError > 0){
			return false;
		}
		window.parent.loadTopo(numWidthGap, numHeightGap);
	}
</script>
<div class="row-fluid sortable">
	<div class="box-content">
		<form class="form-horizontal" id="conditionForm">
			<fieldset>
				<div class="control-group">
					<label class="control-label" for="widthGap">图标之间的宽度差</label>
					<div class="controls">
						<input id="widthGap" name="widthGap" type="text" class="form-control" placeholder="请填写正整数, 例如, 80" style="width:180px;cursor:pointer;"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="heightGap">图标之间的高度差</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input id="heightGap" name="heightGap" type="text" class="form-control" placeholder="请填写正整数, 例如, 80" style="width:180px;cursor:pointer;"/>
						</div>
					</div>
				</div>
				<div class="form-actions">
					<button type="button" onclick="saveInfo()" class="btn btn-primary">保存</button>
					<button class="btn" type="reset" onclick="resetFunc()">重置</button>
				</div>
			</fieldset>
		</form>
	</div>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>