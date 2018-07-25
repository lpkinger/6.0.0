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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style>
.custom-label {
	color: red;
	font-size: 15px;
	font-weight: bold;
}
.loading {
	background-image: url("<%=basePath %>resource/images/loading.gif") !important;
	background-position: center;
	background-repeat: no-repeat;
}
.custom-field {
	margin-left: 10px;
	color: #666;
	font-weight: normal;
}
.checked {
	margin-left: 30% !important;
	background-image: url("<%=basePath %>resource/images/renderer/finishrecord.png") !important;
	background-repeat: no-repeat;
	background-position: center;
	color: blue;
}
.error {
	background-image: url("<%=basePath %>resource/images/renderer/important.png") !important;
	background-repeat: no-repeat;
	background-position: center;
	color: red;
	font-weight: normal;
}
.detail {
	cursor: pointer;
	color: blue;
	text-decoration: underline;
}
.custom .x-grid-row, .custom .x-grid-row .x-grid-cell,.custom .x-grid-cell-inner {
	height: auto !important;
} 
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
/**
 * 成本核算前作业
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'co.inventory.BeforeAccount'
    ],
    launch: function() {
        Ext.create('erp.view.co.inventory.BeforeAccount');
    }
});
var caller = 'Inventory!BeforeAccount';
</script>
</head>
<body >
</body>
</html>