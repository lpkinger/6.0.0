<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
	<link rel="stylesheet" href="<%=basePath %>jsps/ma/jprocess/auto.css"
	type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
    	'hr.emplmana.Account'
    ],
    launch: function() {
         Ext.create('erp.view.hr.emplmana.Account');
    }
});
var caller = "Employee!account";
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body>

</body>
</html>