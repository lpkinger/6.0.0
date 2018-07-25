<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
	.x-html-editor-wrap textarea {
		background-color: #EEE8CD;
	}
	body{
		background: #f1f1f1;
	}
	.x-panel-body-default {
		background: #f9f9f9;
		border-color: #d0d0d0;
		color: black;
		border-width: 1px;
		border-style: solid
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">

//通用界面

Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.CustomForm'
    ],
    launch: function() {
    	Ext.create('erp.view.common.CustomForm');//创建视图
    }
});
var caller = getUrlParam('whoami');//caller决定了该页面的一切信息和逻辑
var kind=getUrlParam('pagekind');
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body >
</body>
</html>