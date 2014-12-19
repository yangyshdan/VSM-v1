<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script src="${path }/resource/js/project/Chart.js"></script>

<style>
.row-fluid {
	height:30px;
}

.row-fluid .span6 {
    width: 49.8%;
}

.row-fluid .span8 {
    width: 66.4%;
}

.row-fluid .span4 {
    width: 33.2%;
}
.layoutChart [class*="span"] {
    float: left;
    margin-left: 1px;
}

</style>
<div id="content">
<!-- 列表开始 -->
	<div class="well sortable layoutChart" style="overflow:hidden">
		<div class="box span12">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 12</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-setting btn-round" data-rel="tooltip" title="设置"><i class="icon-cog"></i></a>
							<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">
                      
                    </div>                   
                  </div>
				</div><!--/span-->

				<div class="box span4">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 3</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">
                        
                    </div>                   
                  </div>
				</div><!--/span-->
				<div class="box span4">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 3</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">
                       
                    </div>                   
                  </div>
				</div><!--/span-->
				<div class="box span4">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 3</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">

                    </div>                   
                  </div>
				</div><!--/span-->
				
				<div class="box span8">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 8</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">

                    </div>                   
                  </div>
				</div><!--/span-->
				<div class="box span4">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 3</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">

                    </div>                   
                  </div>
				</div><!--/span-->

				<div class="box span6">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 6</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a>
							<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">
                       
                    </div>                   
                  </div>
				</div><!--/span-->
				
				<div class="box span6">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-th"></i> Grid 6</h2>
						<div class="box-icon">
							<a href="#" class="btn btn-setting btn-round"><i class="icon-cog"></i></a>
							<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							<a href="#" class="btn btn-close btn-round"><i class="icon-remove"></i></a>
						</div>
					</div>
					<div class="box-content">
                  	<div class="row-fluid">
                       
                    </div>                   
                  </div>
				</div>
	</div>
	<!-- 列表结束 -->
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>