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
<link rel="stylesheet" href="<%=basePath %>resource/ux/css/ClearButton.css"></link>
<style>
.msg-body {
	background-color: #fff;
	border: 1px solid #e3e3e3;
	padding: 10px;
	min-height: 10px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.05);
	font-size: 80%;
}
.text-info {
	color: #31708f;
}
.text-warning {
	color: #f37;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/form/ClearButton.js"></script>  
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'scm.sale.MachineNoScan'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.sale.MachineNoScan');//创建视图
    }
});
var caller = 'MachineNoScan';
var condition = '';
var page = 1;
var value = 0;
var total = 0;
var dataCount = 0;//结果总数
var msg = '';
var height = window.innerHeight;
var pageSize = parseInt(height*0.7/20);
var keyField = "";
var pfField = "";
var url = "";
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body >
</body>
</html>