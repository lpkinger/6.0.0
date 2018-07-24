<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/css/main.css"
	type="text/css"></link>
<style type="text/css">
.x-grid-row .x-grid-cell {
	background-color: white !important
}

.x-livesearch-matchbase {
	font-size: 14px !important;
	font-weight: normal !important;
}

.x-tree-cls-node {
	background-image:
		url('<%=basePath%>resource/ext/resources/themes/images/back.jpg');
	background-color: #f0f0f0;
	height: 21px !important;
	background: #f0f0f0 !important;
}

.x-tree-cls-node:hover, .x-tree-cls-parent:hover {
	font-size: 14px !important;
	font-weight: normal !important;
	color: black !important;
	background-image:
		url('<%=basePath%>resource/ext/resources/themes/images/background_1.jpg');
}

.x-tree-cls-root:hover {
	font-size: 14px !important;
}

.btn-cls:hover {
	boder: 1px;
	background: #E6E6FA;
}

.btn-basecls {
	margin-left: 10px;
	background: #CFCFCF;
	border: 1px solid #8B8386;
}

.x-livesearch-match {
	font-weight: lighter;
	background-color: #EED8AE;
}

.x-livesearch-matchbase {
	font-weight: bold;
	background-color: #EE6A50;
}

.x-panel-body-default {
	background: #EE4000 border-color: #d0d0d0;
	color: black;
	border-width: 1px;
	border-style: solid;
}

#producttype .x-grid-tree-node-expanded .x-tree-icon-parent {
	background-image:
		url('<%=basePath%>resource/ext/resources/themes/images/gray/tree/folder-open.gif')
		!important;
	background:
		url('<%=basePath%>resource/ext/resources/themes/images/gray/tree/folder-open.gif')
		!important;
}

#indexTree .x-grid-cell {
	background-color: #F2F0F2;
}

#indexTree .x-grid-tree-node-expanded .x-tree-icon-parent {
	background-image:
		url('<%=basePath%>resource/ext/resources/themes/images/gray/tree/folder-open.gif')
		!important;
	background:
		url('<%=basePath%>resource/ext/resources/themes/images/gray/tree/folder-open.gif')
		!important;
}

#treegrid .x-grid-header {
	text-align: center;
}

.x-grid-tree-node-expanded .x-tree-icon-parent {
	background:
		url(../../../resource/ext/resources/themes/images/gray/tree/folder-open.gif)
		no-repeat !important;
}

.x-grid-row-selected .x-grid-cell, .x-grid-row-selected .x-grid-rowwrap-div
	{
	border-style: dotted;
	border-color: #a3bae9;
	background-color: #c0c8f0 !important;
	color: blue;
	font-weight: normal;
}
.x-panel-header-text {
	color: black;
	font-weight: bold;
}

.x-panel-with-col-lines .x-grid-row .x-grid-cell {
    border-right: 0.5px solid #d7d7d7 !important;
}

.x-form-file-wrap .x-btn button {
    background-color: #fff !important;
}

#file .x-btn-default-small-icon-text-left .x-btn-inner{
	height: 19px !important;
}

.x-quirks .x-btn-default-small-icon-text-left .x-btn-icon{
	height: 19px !important;
}
</style>

<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'plm.base.ProductType'
	    ],
	    launch: function() {
	    	Ext.create('erp.view.plm.base.ProductType');//创建视图
	    }
	});
	var caller = '';
	var height = window.innerHeight;
	if(Ext.isIE){
		height = screen.height*0.73;
	}
	var pageSize = parseInt(height*0.7/23); 
	var productTypeCode='';
	var productTypeDes='';
	var productTypeid = '';
	var productTypename='';
	var productTypeIsLeaf='';
	var em_code = '<%=session.getAttribute("em_code")%>';
</script>
</head>
<body>
</body>
</html>