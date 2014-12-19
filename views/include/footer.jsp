<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

</div>
<footer>
	
</footer>
	<link href="${path }/resource/css/top.css" rel="stylesheet">
	<div id="returnTop_layout" style="bottom: 0px; right: 0px; z-index: 501; width: 100%; position: fixed;display:none;">
		<div id="returnTop_layout_inner" class="gb_poptips gb_poptips_fold" style="bottom: 0px;">
			<div id="returnTop_layout_btn" class="gb_poptips_btn transition">
				<a id="returnTop" class="poptips_btn" title="" href="javascript:void(0);">
				<i class="ui_icon icon_ticker_top" title="回到顶部"></i>
				<span>回到顶部</span>
				</a>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$(function(){
			$(window).scroll(function(){
				var currentScroll = $(window).scrollTop();
				if(currentScroll>0){
					$("#returnTop_layout").fadeIn('slow');
				}else{
					$("#returnTop_layout").fadeOut('fast');
				}
			});
			$("#returnTop").hover(function(){
				$(this).addClass("poptips_btn_hover");
			},function(){
				$(this).removeClass("poptips_btn_hover");
			}).click(function(){
				$('html,body').animate({scrollTop:0},100);
			});
		});
	</script>
	<div class="modal hide fade" id="myModal"></div>
</div>

<!-- jQuery -->
	<!-- transition / effect library -->
	<script src="${path}/resource/js/bootstrap-transition.js"></script>
	<!-- alert enhancer library -->
	<script src="${path}/resource/js/bootstrap-alert.js"></script>
	<!-- modal / dialog library -->
	<script src="${path}/resource/js/bootstrap-modal.js"></script>
	<!-- custom dropdown library -->
	<script src="${path }/resource/js/bootstrap-dropdown.js"></script>
	<!-- scrolspy library -->
	<script src="${path}/resource/js/bootstrap-scrollspy.js"></script>
	<!-- library for creating tabs -->
	<script src="${path}/resource/js/bootstrap-tab.js"></script>
	<!-- library for advanced tooltip -->
	<script src="${path}/resource/js/bootstrap-tooltip.js"></script>
	<!-- popover effect library -->
	<script src="${path}/resource/js/bootstrap-popover.js"></script>
	<!-- button enhancer library -->
	<script src="${path}/resource/js/bootstrap-button.js"></script>
	<!-- accordion library (optional, not used in demo) -->
	<script src="${path}/resource/js/bootstrap-collapse.js"></script>
	<!-- carousel slideshow library (optional, not used in demo) -->
	<script src="${path}/resource/js/bootstrap-carousel.js"></script>
	<!-- autocomplete library -->
	<script src="${path}/resource/js/bootstrap-typeahead.js"></script>
	<!-- tour library -->
	<script src="${path}/resource/js/bootstrap-tour.js"></script>
	<!-- library for cookie management -->
	<script src="${path}/resource/js/jquery.cookie.js"></script>
	<!-- calander plugin -->
	<script src='${path}/resource/js/fullcalendar.min.js'></script>
	<!-- data table plugin -->
	<script src='${path}/resource/js/jquery.dataTables.min.js'></script>

	<!-- chart libraries start --><%--
	<script src="${path}/resource/js/excanvas.js"></script>
	<script src="${path}/resource/js/jquery.flot.min.js"></script>
	<script src="${path}/resource/js/jquery.flot.pie.min.js"></script>
	<script src="${path}/resource/js/jquery.flot.stack.js"></script>
	<script src="${path}/resource/js/jquery.flot.resize.min.js"></script>
	--%><!-- chart libraries end -->

	<!-- select or dropdown enhancer -->
	<script src="${path}/resource/js/jquery.chosen.min.js"></script>
	<!-- checkbox, radio, and file input styler -->
	<script src="${path}/resource/js/jquery.uniform.min.js"></script>
	<!-- plugin for gallery image view -->
	<script src="${path}/resource/js/jquery.colorbox.min.js"></script>
	<!-- rich text editor library -->
	<script src="${path}/resource/js/jquery.cleditor.min.js"></script>
	<!-- notification plugin -->
	<script src="${path}/resource/js/jquery.noty.js"></script>
	<!-- file manager library -->
	<script src="${path}/resource/js/jquery.elfinder.min.js"></script>
	<!-- star rating plugin -->
	<script src="${path}/resource/js/jquery.raty.min.js"></script>
	<!-- for iOS style toggle switch -->
	<script src="${path}/resource/js/jquery.iphone.toggle.js"></script>
	<!-- autogrowing textarea plugin -->
	<script src="${path}/resource/js/jquery.autogrow-textarea.js"></script>
	<!-- multiple file upload plugin -->
	<script src="${path}/resource/js/jquery.uploadify-3.1.min.js"></script>
	<!-- history.js for cross-browser state change on ajax -->
	<script src="${path}/resource/js/jquery.history.js"></script>
	<!-- application script for Charisma demo -->
	<script src="${path}/resource/js/charisma.js"></script>
	<script src="${path}/resource/js/jquery.resizableColumns.min.js"></script>
	<script src="${path}/resource/js/jquery.form.js"></script>
	<script src="${path}/resource/js/ajaxfileupload.js"></script>
	<script src="${path}/resource/js/bootstrap-switch.js"></script>
</body>
</html>