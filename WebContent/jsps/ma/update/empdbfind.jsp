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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'ma.update.Empdbfind'
    ],
    launch: function() {
        Ext.create('erp.view.ma.update.Empdbfind');
    }
});
	var key ='empnames_';
	var caller = 'UpdateScheme';
	var keyField = '';
	var condition = " nvl(em_class,' ')<>'离职'";
	var which = 'form';
	var page = 1;
	var value = 0;
	var total = 0;
	var count = 0;
	var dataCount = 0;
	var msg = '';
	var height = window.innerHeight;
	var pageSize = parseInt(height*0.7/25);
	var dbfinds = [];
</script>
</head>
<body >
</body>
</html>