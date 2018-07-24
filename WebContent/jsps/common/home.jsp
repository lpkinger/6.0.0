<%@ page language="java" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
  <head>
  	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
	<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link> 
	<style type="text/css">
		font{
			font-size: 14px;
		}
		ul li a{
			font-size: 13.5px;
			text-decoration: none;
		}
		.x-panel-body-default {
			background: #f1f2f5;
			border-left: none !important;
			border-top: none !important;
			border-bottom: none !important;
		}
		.bench{
			background: url('<%=basePath %>resource/images/background_1.jpg');
		}
		.mydiv{
			height: 100%;
			background: #f1f1f1;
		}
		.home-div{
			height: 100%;
			background: url('<%=basePath %>resource/images/background_2.jpg');
		}
		.home-img{
			cursor: pointer;
		}
		.home-img:hover{
			background: url('<%=basePath %>resource/images/background_2.jpg');
		}
		.home-img span{
			font-size: 1;
			color: red;			
		}
		.news,.notify{
			height: 100%;
			background: url('<%=basePath %>resource/images/background_1.jpg');
		}
		#bench_news,#bench_notify {
			width: 100%;			
		}
		.schedule{
			background: url('<%=basePath %>resource/images/background_1.jpg');
		}
		.news ul{
			list-style: none;
		}
		.div-more a{
			color: #CD9B9B;
		}
		.subscription{
			height: 100%;
			background: url('<%=basePath %>resource/images/background_1.jpg');
		}
		.div-link{
			padding-left:10px;
			margin-top:3px;
		}
		a:hover{
			color: #8B8B00;
		}
		.btn-title{
			font-size: 13px;
			color: blue;
			margin-bottom: 0px;
		}
		.btn-title:hover{
			color: red;
		}
		.news-img{
			cursor: pointer;
			margin-left: 5px;
		}
		.news-img:hover{
			background: transparent;
			background-color: #D6BEF9;
			height: 38px;
			width: 38px;
		}
		.div-left {
			display: inline;
			float: left;
			color: green
		}
		.div-right{
			display: inline;
			float: right;
			font-family: sans-serif;
		}
		.div-right a{
			color: blue;
			text-decoration: none;
			font-size: 13px;
		}
		.div-right a:hover{
			color: red;
			cursor: pointer;
			font-weight: bold;
		}
		.div-left font{
			float: left;
			color:green;
			font-size: 13px;
			display: inline;
		}
		.x-panel-header-text {
			font-size: 13px;
			color: #09a471;
			font-weight: normal;
		}
		.x-panel-header-text-default-framed {
			font-size: 13px;
			color: #09a471;
			font-weight: normal;
		}
		#bench .x-panel-header .x-box-inner .x-box-item {
			display: inline;
			height: 18px;
		}
		.number {
			height: 24px;
			width: 24px;
			background:  url('<%=basePath %>resource/images/number/number.png');
			margin-top: -30px;
		}
		.custom-grid .x-grid-row .x-grid-row-alt {
			height : 20px;
		}
		.custom-grid .x-grid-row .x-grid-cell{
			height : 20px;
			line-height	: 20px;
			vertical-align : top;
		}
		.custom-grid .x-grid-row .x-grid-cell-inner{
			height : 20px;
			line-height	: 20px;
			vertical-align : top;
		}
		.custom-grid .x-grid-row .x-grid-cell:hover{
			height : 26px;
			line-height	: 26px;
		}
		.custom-grid a {
			text-decoration: none;
		}
		.custom-grid a:hover {
			font-weight: 800;
		}
		.custom-grid .x-action-col-icon {
			margin: 0px 5px 0px 5px;
			cursor: pointer;
		}
		.task-grid .x-grid-row .x-grid-row-alt {
				background-color: red !important;
		}
		 .x-grid-cell-topic b {
            display: block;
        }
        .x-grid-cell-topic .x-grid-cell-inner {
            white-space: normal;
            height : auto !important ;
            background-color: #f1f1f1;
	        border-color: #ededed;
	        border-style: solid;
	        border-width: 1px 0;
	        border-top-color: #FAFAFA;
            border-bottom:none;
            line-height	: 26 px;
	        white-space: nowrap 
        }
        .x-grid-cell-topic a {
            text-decoration: none;
        }
        .x-grid-cell-topic a:hover {
            text-decoration:underline;
        }
		.x-grid-cell-topic .x-grid-cell-innerf {
			padding: 5px;
		}
		.x-grid-rowbody {
	        padding: 0 5px 5px 5px; 
		}
		.list-default>li {
			padding: 2px 5px;
			overflow: hidden;
			text-overflow: ellipsis;
    		white-space: nowrap;
		}
	</style>
	<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
	<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
	<script type="text/javascript">
		window.em_code = '<%=session.getAttribute("em_code")%>';
		window.em_uu = '<%=session.getAttribute("em_uu")%>';
		window.em_id = '<%=session.getAttribute("em_id")%>';
		window.em_name = '<%=session.getAttribute("em_name")%>';
		window.em_defaulthsid = '<%=session.getAttribute("em_defaulthsid")%>';
		window.em_defaultorid = '<%=session.getAttribute("em_defaultorid")%>';
	</script>
	<script type="text/javascript" src="<%=basePath %>resource/other/workBench.js?_em=<%=session.getAttribute("em_uu") %>"></script>
	<script type="text/javascript">
 	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'common.Home'
	    ],
	    launch: function() {
	    	Ext.create('erp.view.common.home.Viewport');
	    	getMyBench();//调我的工作台设置
	    }
	});
	var height = window.innerHeight;
	var width = window.innerWidth;
	if(Ext.isIE){
		height = screen.height*0.75;
		width = screen.width*0.8;
	}
	</script>
  </head>
  <body>
  	<div id='myflow' class='mydiv'></div>
  	<div id='myoverflow' class='mydiv'></div>
  	<div id='notify' class='notify'></div>
  	<div id='news' class='news'></div>
  	<div id='subscription' class='subscription'></div>
  	<div id='check' class='check'></div>
  	<div id='note2' class='mydiv'></div>
  	<div id='plan' class='mydiv'></div>
  	<div id='meeting' class='mydiv'></div>
  </body>
</html>