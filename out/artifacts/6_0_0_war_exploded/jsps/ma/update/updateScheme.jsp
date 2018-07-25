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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<style>
.custom .x-panel-body-default {
	background: #f1f2f5;
}
.custom .x-form-item-label {
	margin-left: 3px;
	font-family: "ChantelliAntiquaRegular";
}
 .x-tree-parent:hover {
 	background-color:#FAFAFA !important;
    font-size: 14px;
    font-weight: bold;
    color: green;
}
 .x-tree-parent:hover {
    background-color:#f1f1f1 !important;
    font-size: 15;
    margin-top: 5px;
    height: 23!important;
}
.x-grid-row-selected .x-grid-cell,.x-grid-row-selected .x-grid-rowwrap-div {
    border-style: dotted;
    border-color: #a3bae9;
    background-color: #f1f1f1!important;
    color: blue;
    font-weight: normal;
}
.x-tree-parent {
    background-color:#f1f1f1 !important;
    font-size: 15;
    margin-top: 5px;
    height: 23!important;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript">
	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'ma.update.UpdateScheme'
	    ],
	    launch: function() {
	    	Ext.create('erp.view.ma.update.UpdateScheme');//创建视图
	    }
	});
	var caller = "UpdateScheme";
	var formCondition = '';
	var gridCondition = '';
	var dataCount=0;
	var pageSize = parseInt(window.innerHeight*0.6/25);
	var msg = '';
	var page = 1; 
	var condition='';
	var which = 'form';
	var key = 'empnames_';
</script>
</head>
<body >
</body>
</html>