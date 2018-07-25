<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray-sysnavigation.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main-sysnavigation.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/tree.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<style>
.custom .x-grid-row .x-grid-cell {
	color: null;
	font: normal 11px tahoma, arial, verdana, sans-serif;
	background-color: #f1f1f1;
	height: 26px;
	line-height: 26px;
	border: 1px solid white !important;
}
.x-tree-arrows .x-tree-elbow-plus,.x-tree-arrows .x-tree-elbow-minus,.x-tree-arrows .x-tree-elbow-end-plus,.x-tree-arrows .x-tree-elbow-end-minus
	{
	background-image: url('../../resource2/resources/ext/resources/themes/images/gray/tree/arrows.gif')
}
.tree-nav-close {
	background-image: url('../../resource/images/icon/trash.png');
	float:left;
	margin-top:-17px;
	width:16px;
}
.x-tree-panel .x-grid-row .x-grid-cell-inner {
	
	cursor: default;
	
}
.x-btn-default-toolbar-small-icon-text-left .x-btn-inner {
    line-height: 20px !important;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript">
var caller = "SysNavigation";
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'ma.SysNavigation'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.SysNavigation');//创建视图
    }
});
</script>
</head>
<body >
</body>
</html>