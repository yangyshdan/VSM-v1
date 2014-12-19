<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/header.jsp"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<script src="${path }/resource/js/ajaxPage.js"></script>
<script type="text/javascript" src="${path }/resource/js/jtopo/js/snippet/jquery.snippet.min.js"></script>
<script type="text/javascript" src="${path }/resource/js/jtopo/js/excanvas.js"></script>
<script type="text/javascript" src="${path }/resource/js/jtopo/js/jtopo-min.js"></script>
<style type="text/css">
	#contextmenu {
		border: 1px solid #aaa;
		border-bottom: 0;
		background: #eee;
		position: absolute;
		list-style: none;
		margin: 0;
		padding: 0;
		display: none;
	}
	</style>
<script type="text/javascript">
$(function(){  //加载容量柱形图
	var canvas = document.getElementById('canvas');
//var canvas=$("#canvas");
//alert(canvas);
	var stage = new JTopo.Stage(canvas);
	var scene = new JTopo.Scene(stage);	
//	scene.eagleEye.visible = true;
//	scene.setBackground('${path }/resource/js/jtopo/img/bg.jpg');
	
	function randomIP(){
		function num(){ return Math.floor(Math.random()*255);};
		return num() + '.' + num() + '.' + num()+ '.' + num();
	}
	//节点
	function node(id,x,y,text,img){
		//var text = ;/
		var node = new JTopo.Node(text.substring(6,0));		
		node.setImage('${path}/resource/img/project/'+img,true);
		//node.setSize(2,2);
		node.setLocation(x,y);
		node.scalaX =0.7; // 水平方向的缩放
		node.scalaY = 0.7; // 垂直方向的缩放
		node.label.position ='Middle_Center';
		node.label.offsetX = -5;
		node.label.offsetY = -20;
		node.style.fontColor='#0A0A0A';
		
		scene.add(node);
		node.addEventListener("mouseover", function(event){
			$("#contextmenu").text(text);
			$("#contextmenu").css({
				top: event.pageY,
				left: event.pageX
			}).show();
		})
		return node;
	}				
	//连线
	function linkNode(nodeA, nodeZ){
		var link = new JTopo.Link(nodeA, nodeZ);	
		link.fold = 'y';
		scene.add(link);
		return link;
	}
	//主机
	var hostArray = ${hostArray};
	var hostes=[];
	for(var k=0;k<hostArray.length;k++){
		var y=40;
		if(k%2==0){
			y=55;
		}
		var n1 = node(hostArray[k].hid,(1200/(hostArray.length+2))*(k+1),y,hostArray[k].hname,"host.png");
		hostes.push(n1);
	}
	//交换机
	var switchArray = ${switchArray};
	var switch1s = [];
	for(var i=0;i<switchArray.length;i++){
		var y=140;
		if(i%2==0){
			y=155;
		}
		var n2=node(switchArray[i].switchid,(1200/(switchArray.length+2))*(i+1),y,switchArray[i].logicalname,"switch.png");
		switch1s.push(n2);
	}
	//存储
	var storage1Array = ${storage1Array};
	var storage1es = [];
	for(var j=0;j<storage1Array.length;j++){
		var y=240;
		if(i%2==0){
			y=260;
		}
		var n3=node(storage1Array[j].sid,(1200/(storage1Array.length+2))*(j+1),y,storage1Array[j].sname,"StorageSystemBase.png");
		storage1es.push(n3);
	}
	//服务器和交换机
	var HostandSwitArray=${HostandSwitArray};
	for(var a2=0;a2<HostandSwitArray.length;a2++){
		for(var b2=0;b2<hostArray.length;b2++){
			if(HostandSwitArray[a2].host1==hostArray[b2].hid){
				for(var c2=0;c2<switchArray.length;c2++){
					if(HostandSwitArray[a2].switch1==switchArray[c2].switchid){
						linkNode(hostes[b2],switch1s[c2]);
					}
				}
			}	
		}
	}
	//存储和交换机
	var SwiandStorArray=${SwiandStorArray};
	for(var a=0;a<SwiandStorArray.length;a++){
		for(var b=0;b<switchArray.length;b++){
			if(SwiandStorArray[a].switid==switchArray[b].switchid){
				for(var c=0;c<storage1Array.length;c++){
					if(SwiandStorArray[a].storid==storage1Array[c].sid){
						linkNode(switch1s[b],storage1es[c]);
					}
				}
			}
		}
	}
	//交换机和交换机
	var SwitandSwitArray=${SwitandSwitArray};
	for(var a1=0;a1<SwitandSwitArray.length;a1++){
		for(var b1=0;b1<switchArray.length;b1++){
			if(SwitandSwitArray[a1].switch1==switchArray[b1].switchid){
				for(var c1=0;c1<switchArray.length;c1++){
					if(SwitandSwitArray[a1].switch2==switchArray[c1].switchid){
						linkNode(switch1s[b1],switch1s[c1]);
					}
				}
			}	
		}
	}

	stage.play(scene);
});
</script>
<script src="${path }/resource/js/highcharts/highcharts.js">
</script>
 <div id="contextmenu" style="display:none;">
  </div>
<div id="content">
	<canvas class="row-fluid" id="canvas" width="1200" height="500"></canvas>
</div>
<%@include file="/WEB-INF/views/include/footer.jsp"%>