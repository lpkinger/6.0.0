<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<title>消息内容</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon" />
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<%-- <link rel="stylesheet" href="<%=basePath %>resource/css/main.css"
	type="text/css"></link> --%>
<style type="text/css">
.x-tree-arrows .x-tree-elbow-plus,.x-tree-arrows .x-tree-elbow-minus,.x-tree-arrows .x-tree-elbow-end-plus,.x-tree-arrows .x-tree-elbow-end-minus
	{
	background-image:
		url('../../../resource/ext/resources/themes/images/gray/tree/arrows.gif')
		!important;
}

.x-node-expanded .x-tree-icon-parent {
	background-position: 0 0 !important;
	background: no-repeat;
	background-image: url('resources/images/folder-open.gif') !important;
}

.x-grid-tree-node-expanded .x-tree-elbow-plus,.x-grid-tree-node-expanded .x-tree-elbow-end-plus
	{
	background-image:
		url('../../resources/themes/images/neptune/tree/icons-gray.png');
	background-position: -20px 0;
}

.button1 {
	overflow: visible;
	display: inline-block;
	/* padding: 0.5em 1em; */
	border: 1px solid #d4d4d4;
	text-decoration: none;
	text-align: center;
	text-shadow: 1px 1px 0 #fff;
	font: 11px/normal sans-serif;
	color: #333;
	white-space: nowrap;
	cursor: pointer;
	outline: none;
	height: 24px !important;
	background: url(../../../resource/images/bg_back.png);
	font-weight: 400 !important;
	background-clip: padding-box;
	border-radius: 0.2em;
	zoom: 1;
}

.button1.pill {
	border-radius: 50em;
}

.x-tree-parent {
	background-color: rgb(250, 250, 250) !important;
}

.x-tab-active button .x-tab-inner {
	font-weight: bold;
	color: #fff;
	height: 50px;
}

.x-border-box .x-tab-default-top {
	height: 50px;
}

.x-tab button .x-tab-inner {
	font-weight: bold;
	width: 100px;
	text-align: center;
	margin-left: -20px;
	margin-top: 2px;
	font-size: 13px;
	height: 25px;
	padding-bottom: 3px;
	margin-bottom: 5px;
}

.x-tab-default-top {
	background: url(../../resource/images/title.png) no-repeat;
	border: none;
	height: 50px;
	width: 108px;
	line-height: 25px;
	float: left;
	color: #000;
	margin-left: -5px !important;
	text-decoration: none;
}

.x-tab-active {
	background: url(../../resource/images/title_hover.png) no-repeat;
}

.header-bottom {
	border-left: none;
	border-right: none;
	border-bottom: solid 6px #4cb0d5;
}

.x-editor {
	padding-top: 10px;
	border-bottom: solid 1px #b5b8c8;
	!
	important;
}

.x-html-editor-wrap {
	border: none;
}

.x-html-editor-wrap textarea {
	background-color: white;
}

.x-border-layout-ct {
	background-color: white;
}

.x-btn-turn {
	background: url('../../resource/images/icon/turn.png')
}

.x-btn-task {
	background: url('../../resource/images/icon/content.png')
}

.x-tab-bar {
	background-color: white;
}

.x-tab-bar-body {
	background-color: white;
	border-left: none;
	border-right: none;
	border-top: none;
	border-bottom: solid 6px #4cb0d5 !important;
}

.x-border-box .x-tab-bar-top .x-tab-bar-body {
	height: 32px;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">
  function openUrl(url){
	  window.open(basePath+url);
  }
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'oa.info.PagingGet' ],
		launch : function() {
			Ext.create('erp.view.oa.info.PagingGet');//创建视图
		}
	});
	var data = ${data};
	var context = data.PR_CONTEXT;
	var page=1;
	var pageSize=10;
</script>
</head>
<body>
</body>
</html>